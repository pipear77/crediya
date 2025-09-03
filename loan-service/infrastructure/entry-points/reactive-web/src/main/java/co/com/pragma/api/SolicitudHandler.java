package co.com.pragma.api;

import co.com.pragma.api.dto.SolicitudRequestDTO;
import co.com.pragma.api.mapper.SolicitudApiMapper;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class SolicitudHandler {

    private final SolicitarPrestamoUseCase useCase;
    private final SolicitudApiMapper mapper;
    private final Validator validator;

    public Mono<ServerResponse> save(ServerRequest request) {
        log.info("Solicitud de préstamo recibida");

        return request
                .bodyToMono(SolicitudRequestDTO.class)
                .doOnNext(dto -> log.info("Payload recibido: {}", dto))
                .flatMap(this::validate)
                .map(mapper::toDomain)
                .flatMap(useCase::crearSolicitud)
                .map(mapper::toResponseDTO)
                .doOnNext(res -> log.info("Solicitud registrada: {}", res))
                .doOnError(e -> log.error("Error al registrar solicitud", e))
                .flatMap(response -> ServerResponse
                        .created(request.uri())
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response));
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
