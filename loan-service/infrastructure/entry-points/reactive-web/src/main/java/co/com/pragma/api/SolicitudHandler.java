package co.com.pragma.api;

import co.com.pragma.api.dto.ErrorResponseDTO;
import co.com.pragma.api.dto.Page;
import co.com.pragma.api.dto.solicitud.ActualizarEstadoSolicitudDTO;
import co.com.pragma.api.dto.solicitud.SolicitudRequestDTO;
import co.com.pragma.api.dto.solicitud.SolicitudResponseDTO;
import co.com.pragma.api.dto.usuario.UsuarioAutenticadoDTO;
import co.com.pragma.api.helper.TokenExtractor;
import co.com.pragma.api.mapper.SolicitudApiMapper;
import co.com.pragma.model.solicitud.enums.EstadoSolicitud;
import co.com.pragma.model.solicitud.solicitudprestamos.Solicitud;
import co.com.pragma.usecase.solicitarprestamo.ActualizarEstadoSolicitudUseCaseInterface;
import co.com.pragma.usecase.solicitarprestamo.ListarSolicitudesParaRevisionUseCaseInterface;
import co.com.pragma.usecase.solicitarprestamo.SolicitarPrestamoUseCaseInterface;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class SolicitudHandler {

    private final SolicitarPrestamoUseCaseInterface solicitarPrestamoUseCase;
    private final ListarSolicitudesParaRevisionUseCaseInterface listarSolicitudesUseCase;
    private final ActualizarEstadoSolicitudUseCaseInterface actualizarEstadoUseCase;
    private final SolicitudApiMapper mapper;
    private final Validator validator;
    private final TokenExtractor tokenExtractor;

    public Mono<ServerResponse> save(ServerRequest request) {
        log.info("Solicitud de préstamo recibida");

        return extractToken(request)
                .flatMap(token -> validarRol(token, List.of("ROL_CLIENTE"))
                        .flatMap(usuario -> request.bodyToMono(SolicitudRequestDTO.class)
                                .doOnNext(dto -> log.info("Payload recibido: {}", dto))
                                .flatMap(this::validate)
                                .map(dto -> mapSolicitud(dto, usuario))
                                .flatMap(solicitud -> solicitarPrestamoUseCase.crearSolicitud(solicitud, token))
                                .map(mapper::toResponseDTO)
                                .flatMap(dto -> ServerResponse.created(request.uri())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(dto))))
                .onErrorResume(e -> buildErrorResponse(e, request));
    }

    public Mono<ServerResponse> listar(ServerRequest request) {
        log.info("Listando solicitudes para revisión manual con paginación");

        int page = parseIntParam(request, "page", 0);
        int size = parseIntParam(request, "size", 10);

        return extractToken(request)
                .flatMap(token -> validarRol(token, List.of("ROL_ASESOR", "ROL_ADMIN"))
                        .flatMap(usuario -> listarSolicitudesUseCase.ejecutar()
                                .map(mapper::toResponseDTO)
                                .filter(this::esEstadoRevisable)
                                .collectList()
                                .map(lista -> buildPage(lista, page, size))
                                .flatMap(pageResult -> ServerResponse.ok()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(pageResult))))
                .onErrorResume(e -> buildErrorResponse(e, request));
    }

    // 🔐 Token y rol
    private Mono<String> extractToken(ServerRequest request) {
        String rawToken = request.headers().firstHeader("Authorization");
        if (rawToken == null || !rawToken.startsWith("Bearer ")) {
            log.warn("Token no presente o mal formado");
            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token de autorización requerido"));
        }
        return Mono.just(rawToken.substring(7).trim());
    }

    private Mono<UsuarioAutenticadoDTO> validarRol(String token, List<String> rolesPermitidos) {
        return tokenExtractor.extractUsuario(token)
                .flatMap(usuario -> {
                    if (!rolesPermitidos.contains(usuario.getRol())) {
                        log.warn("Rol no autorizado: {}", usuario.getRol());
                        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "Rol no autorizado"));
                    }
                    return Mono.just(usuario);
                });
    }

    // ✅ Validación
    private <T> Mono<T> validate(T bean) {
        Set<ConstraintViolation<T>> violations = validator.validate(bean);
        if (!violations.isEmpty()) {
            log.warn("Validación fallida: {}", violations);
            return Mono.error(new ConstraintViolationException(violations));
        }
        return Mono.just(bean);
    }

    // 📦 Mapeo
    private Solicitud mapSolicitud(SolicitudRequestDTO dto, UsuarioAutenticadoDTO usuario) {
        Solicitud solicitud = mapper.toDomain(dto);
        solicitud.setId(UUID.fromString(usuario.getId()));
        solicitud.setEstado(EstadoSolicitud.PENDIENTE_REVISION);
        solicitud.setDocumentoIdentidad(usuario.getDocumentoIdentidad());
        solicitud.setCorreo(usuario.getCorreo());
        solicitud.setNombre(usuario.getNombres());
        return solicitud;
    }

    // 📊 Paginación y métricas
    private Page<SolicitudResponseDTO> buildPage(List<SolicitudResponseDTO> solicitudes, int page, int size) {
        List<SolicitudResponseDTO> paged = solicitudes.stream()
                .skip((long) page * size)
                .limit(size)
                .toList();

        BigDecimal deudaTotalMensual = solicitudes.stream()
                .filter(s -> s.estado() == EstadoSolicitud.APROBADA)
                .map(SolicitudResponseDTO::montoMensualSolicitud)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int aprobadas = (int) solicitudes.stream()
                .filter(s -> s.estado() == EstadoSolicitud.APROBADA)
                .count();

        return Page.<SolicitudResponseDTO>builder()
                .content(paged)
                .page(page)
                .size(size)
                .totalElements(solicitudes.size())
                .sumApprovedAmount(deudaTotalMensual)
                .approvedCount(aprobadas)
                .build();
    }

    private boolean esEstadoRevisable(SolicitudResponseDTO s) {
        return List.of(
                EstadoSolicitud.PENDIENTE_REVISION,
                EstadoSolicitud.RECHAZADA,
                EstadoSolicitud.REVISION_MANUAL
        ).contains(s.estado());
    }

    // 🧯 Manejo de errores con DTO
    private Mono<ServerResponse> buildErrorResponse(Throwable e, ServerRequest request) {
        int status = e instanceof ResponseStatusException rse ? rse.getStatusCode().value() : 500;
        String error = e instanceof ResponseStatusException rse ? rse.getReason() : "Error interno";
        String message = e.getMessage();

        if (e instanceof ConstraintViolationException cve) {
            message = cve.getConstraintViolations().stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .collect(Collectors.joining(", "));
            status = 400;
            error = "Validación fallida";
        }

        ErrorResponseDTO dto = new ErrorResponseDTO(status, error, message, request.path());

        log.error("Error procesando solicitud [{}]: {}", request.path(), message, e);
        return ServerResponse.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto);
    }

    // 🔢 Utilidad para parámetros
    private int parseIntParam(ServerRequest request, String key, int defaultValue) {
        try {
            return Integer.parseInt(request.queryParam(key).orElse(String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            log.warn("Parámetro inválido '{}': {}", key, e.getMessage());
            return defaultValue;
        }
    }

    public Mono<ServerResponse> actualizarEstado(ServerRequest request) {
        UUID idSolicitud;
        try {
            idSolicitud = UUID.fromString(request.pathVariable("id"));
        } catch (IllegalArgumentException e) {
            log.warn("ID de solicitud inválido: {}", request.pathVariable("id"));
            return ServerResponse.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(new ErrorResponseDTO(
                            400,
                            "Solicitud inválida",
                            "ID de solicitud mal formado",
                            request.path()
                    ));
        }

        return extractToken(request)
                .flatMap(token -> validarRol(token, List.of("ROL_ASESOR", "ROL_ADMIN"))
                        .flatMap(usuario -> request.bodyToMono(ActualizarEstadoSolicitudDTO.class)
                                .flatMap(this::validate)
                                // 👇 aquí pasas los dos parámetros
                                .flatMap(dto -> actualizarEstadoUseCase.ejecutar(idSolicitud, dto.nuevoEstado()))
                                .map(mapper::toResponseDTO)
                                .flatMap(dto -> ServerResponse.ok()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(dto))))
                .onErrorResume(e -> buildErrorResponse(e, request));
    }


}

