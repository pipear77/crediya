package co.com.pragma.api;

import co.com.pragma.api.dto.solicitud.SolicitudRequestDTO;
import co.com.pragma.api.dto.solicitud.SolicitudResponseDTO;
import co.com.pragma.api.helper.TokenExtractor;
import co.com.pragma.api.mapper.SolicitudApiMapper;
import co.com.pragma.model.solicitud.solicitudprestamos.Solicitud;
import co.com.pragma.usecase.solicitarprestamo.SolicitarPrestamoUseCase;
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

import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SolicitudHandler {

    private final SolicitarPrestamoUseCase useCase;
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

        return tokenExtractor.extractUsuario(rawToken)
                .flatMap(usuario -> {
                    if (!"ROL_CLIENTE".equals(usuario.getRol())) {
                        log.warn("Rol no autorizado: {}", usuario.getRol());
                        return ServerResponse.status(403).bodyValue("Solo los clientes pueden crear solicitudes");
                    }

                    return request.bodyToMono(SolicitudRequestDTO.class)
                            .doOnNext(dto -> log.info("Payload recibido: {}", dto))
                            .flatMap(this::validate)
                            .map(dto -> {
                                Solicitud solicitud = mapper.toDomain(dto, usuario.getDocumentoIdentidad());
                                solicitud.setId(UUID.fromString(usuario.getId()));
                                return solicitud;
                            })
                            .flatMap(solicitud -> useCase.crearSolicitud(solicitud, cleanedToken))
                            .map(mapper::toResponseDTO)
                            .flatMap(responseDTO -> ServerResponse
                                    .created(request.uri())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(responseDTO));
                })
                .onErrorResume(e -> {
                    log.error("Error al registrar solicitud", e);
                    return ServerResponse.status(500).bodyValue("Error interno al procesar la solicitud");
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
