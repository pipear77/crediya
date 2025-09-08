package co.com.pragma.model.solicitud.gateways;

import co.com.pragma.model.solicitud.solicitudprestamos.Solicitud;
import reactor.core.publisher.Mono;

public interface UsuarioClientRepository {
    public Mono<Solicitud> buscarPorDocumento(String documentoIdentidad, String token);

}
