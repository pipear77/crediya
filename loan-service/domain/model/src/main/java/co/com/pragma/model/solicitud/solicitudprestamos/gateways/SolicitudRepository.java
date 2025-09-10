package co.com.pragma.model.solicitud.solicitudprestamos.gateways;

import co.com.pragma.model.solicitud.solicitudprestamos.Solicitud;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SolicitudRepository {
    Mono<Solicitud> guardar(Solicitud solicitud);
    Flux<Solicitud> listarSolicitudesParaRevision();
}
