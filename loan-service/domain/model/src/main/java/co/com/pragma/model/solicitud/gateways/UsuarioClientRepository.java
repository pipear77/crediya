package co.com.pragma.model.solicitud.gateways;

import co.com.pragma.model.solicitud.usuario.Usuario;
import reactor.core.publisher.Mono;

public interface UsuarioClientRepository {
    Mono<Usuario> buscarPorDocumento(String documentoIdentidad, String token);
}