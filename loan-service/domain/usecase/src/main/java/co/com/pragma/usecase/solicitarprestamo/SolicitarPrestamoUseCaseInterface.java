package co.com.pragma.usecase.solicitarprestamo;

import co.com.pragma.model.solicitud.solicitudprestamos.Solicitud;
import reactor.core.publisher.Mono;

public interface SolicitarPrestamoUseCaseInterface {
    public Mono<Solicitud> crearSolicitud(Solicitud solicitud, String token);
}
