package co.com.pragma.model.solicitud.tipoprestamos.gateways;

import co.com.pragma.model.solicitud.tipoprestamos.TipoPrestamo;
import reactor.core.publisher.Mono;

public interface TipoPrestamoRepository {
    Mono<TipoPrestamo> findById(Long id);
}
