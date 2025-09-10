package co.com.pragma.usecase.solicitarprestamo;

import co.com.pragma.model.solicitud.enums.EstadoSolicitud;
import co.com.pragma.model.solicitud.solicitudprestamos.Solicitud;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ActualizarEstadoSolicitudUseCaseInterface {
    Mono<Solicitud> ejecutar(UUID id, EstadoSolicitud nuevoEstado);
}
