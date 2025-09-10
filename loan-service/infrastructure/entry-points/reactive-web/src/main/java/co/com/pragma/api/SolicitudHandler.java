package co.com.pragma.api;

import co.com.pragma.api.dto.Page;
import co.com.pragma.api.dto.solicitud.SolicitudRequestDTO;
import co.com.pragma.api.dto.solicitud.SolicitudResponseDTO;
import co.com.pragma.api.helper.TokenExtractor;
import co.com.pragma.api.mapper.SolicitudApiMapper;
import co.com.pragma.model.solicitud.enums.EstadoSolicitud;
import co.com.pragma.model.solicitud.solicitudprestamos.Solicitud;
import co.com.pragma.usecase.solicitarprestamo.ListarSolicitudesParaRevisionUseCaseInterface;
import co.com.pragma.usecase.solicitarprestamo.SolicitarPrestamoUseCaseInterface;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SolicitudHandler {

    private final SolicitarPrestamoUseCaseInterface useCase;
    private final ListarSolicitudesParaRevisionUseCaseInterface listarSolicitudesParaRevisionUseCase;
    private final SolicitudApiMapper mapper;
    private final Validator validator;
    private final TokenExtractor tokenExtractor;

    public Mono<ServerResponse> save(ServerRequest request) {
        log.info("Solicitud de préstamo recibida");

        String rawToken = request.headers().firstHeader("Authorization");
        if (rawToken == null || !rawToken.startsWith("Bearer ")) {
            log.warn("Token no presente o mal formado");
            return ServerResponse.status(401).bodyValue("Token de autorización requerido");
        }

        String cleanedToken = rawToken.substring(7).trim();

        return tokenExtractor.extractUsuario(rawToken).flatMap(usuario -> {
            if (!"ROL_CLIENTE".equals(usuario.getRol())) {
                log.warn("Rol no autorizado: {}", usuario.getRol());
                return ServerResponse.status(403).bodyValue("Solo los clientes pueden crear solicitudes");
            }

            return request.bodyToMono(SolicitudRequestDTO.class).doOnNext(dto -> log.info("Payload recibido: {}", dto)).flatMap(this::validate).map(dto -> {
                Solicitud solicitud = mapper.toDomain(dto);
                solicitud.setId(UUID.fromString(usuario.getId()));
                solicitud.setEstado(EstadoSolicitud.PENDIENTE_REVISION);
                solicitud.setDocumentoIdentidad(usuario.getDocumentoIdentidad());
                solicitud.setCorreo(usuario.getCorreo());
                log.info("el correo esta mapeado desde el token", usuario.getCorreo());
                solicitud.setNombre(usuario.getNombres());
                return solicitud;
            }).flatMap(solicitud -> useCase.crearSolicitud(solicitud, cleanedToken)).map(mapper::toResponseDTO).flatMap(responseDTO -> ServerResponse.created(request.uri()).contentType(MediaType.APPLICATION_JSON).bodyValue(responseDTO));
        }).onErrorResume(e -> {
            log.error("Error al registrar solicitud", e);
            return ServerResponse.status(500).bodyValue("Error interno al procesar la solicitud");
        });
    }

    public Mono<ServerResponse> listar(ServerRequest request) {
        log.info("Listando solicitudes para revisión manual con paginación");

        String rawToken = request.headers().firstHeader("Authorization");
        if (rawToken == null || !rawToken.startsWith("Bearer ")) {
            log.warn("Token no presente o mal formado");
            return ServerResponse.status(401).bodyValue("Token de autorización requerido");
        }

        int page = Integer.parseInt(request.queryParam("page").orElse("0"));
        int size = Integer.parseInt(request.queryParam("size").orElse("10"));

        return tokenExtractor.extractUsuario(rawToken).flatMap(usuario -> {
            if (!List.of("ROL_ASESOR", "ROL_ADMINISTRADOR").contains(usuario.getRol())) {
                log.warn("Rol no autorizado: {}", usuario.getRol());
                return ServerResponse.status(403).bodyValue("Solo asesores o administradores pueden listar solicitudes");
            }

            return listarSolicitudesParaRevisionUseCase.ejecutar().map(mapper::toResponseDTO).filter(s -> s.estado() == EstadoSolicitud.PENDIENTE_REVISION) // ✅ filtro HU4
                    .collectList().map(mapped -> {
                        List<SolicitudResponseDTO> paged = mapped.stream().skip((long) page * size).limit(size).toList();

                        BigDecimal deudaTotalMensual = mapped.stream().filter(s -> s.estado() == EstadoSolicitud.APROBADA).map(SolicitudResponseDTO::montoMensualSolicitud).filter(monto -> monto != null).reduce(BigDecimal.ZERO, BigDecimal::add);

                        int aprobadas = (int) mapped.stream().filter(s -> s.estado() == EstadoSolicitud.APROBADA).count();

                        return Page.<SolicitudResponseDTO>builder().content(paged).page(page).size(size).totalElements(mapped.size()).sumApprovedAmount(deudaTotalMensual).approvedCount(aprobadas).build();
                    }).flatMap(p -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(p));
        }).onErrorResume(e -> {
            log.error("Error al listar solicitudes", e);
            return ServerResponse.status(500).bodyValue("Error interno al listar solicitudes");
        });

    }


    private <T> Mono<T> validate(T bean) {
        Set<ConstraintViolation<T>> violations = validator.validate(bean);
        if (!violations.isEmpty()) {
            log.warn("Validación fallida: {}", violations);
            return Mono.error(new ConstraintViolationException(violations));
        }
        return Mono.just(bean);
    }
}
