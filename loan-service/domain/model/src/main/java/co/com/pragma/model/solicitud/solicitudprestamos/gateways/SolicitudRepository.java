package co.com.pragma.model.solicitud.solicitudprestamos.gateways;

import co.com.pragma.model.solicitud.solicitudprestamos.Solicitud;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface SolicitudRepository {
    Mono<Solicitud> guardar(Solicitud solicitud);
    Flux<Solicitud> listarSolicitudesParaRevision();
    Mono<Solicitud> buscarPorId(UUID id);
    Mono<Solicitud> actualizar(Solicitud solicitud);
}
