package co.com.pragma.usecase.solicitarprestamo;

import co.com.pragma.model.solicitud.solicitudprestamos.Solicitud;
import co.com.pragma.model.solicitud.enums.TipoPrestamo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SolicitarPrestamoUseCaseInterface {
    public Mono<Solicitud> ejecutar(Solicitud solicitud);
    public Mono<Solicitud> buscarPorDocumento(String documentoIdentidad);
    public Flux<Solicitud> listarPorTipo(TipoPrestamo tipo);
}
