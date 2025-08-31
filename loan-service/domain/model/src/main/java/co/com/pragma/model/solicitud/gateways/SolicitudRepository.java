package co.com.pragma.model.solicitud.gateways;

import co.com.pragma.model.solicitud.Solicitud;
import co.com.pragma.model.solicitud.enums.TipoPrestamo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SolicitudRepository {
    Mono<Solicitud> guardar(Solicitud solicitud);
    Mono<Solicitud> buscarPorDocumento(String documentoIdentidad);
    Flux<Solicitud> listarPorTipo(TipoPrestamo tipo);
}
