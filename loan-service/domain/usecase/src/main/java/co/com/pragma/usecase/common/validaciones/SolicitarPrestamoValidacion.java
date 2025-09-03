package co.com.pragma.usecase.common.validaciones;

import co.com.pragma.model.solicitud.solicitudprestamos.Solicitud;
import reactor.core.publisher.Mono;

public interface SolicitarPrestamoValidacion {
    Mono<Void> validar(Solicitud solicitud);
}
