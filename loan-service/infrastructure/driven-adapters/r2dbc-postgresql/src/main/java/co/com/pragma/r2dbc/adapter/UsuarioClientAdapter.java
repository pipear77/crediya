package co.com.pragma.r2dbc.adapter;

import co.com.pragma.model.solicitud.gateways.UsuarioClientRepository;
import co.com.pragma.model.solicitud.solicitudprestamos.Solicitud;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UsuarioClientAdapter implements UsuarioClientRepository {

    private final WebClient webClient;

    @Value("${external.user-service.url}")
    private String usuarioServiceUrl;

    @Override
    public Mono<Solicitud> buscarPorDocumento(String documentoIdentidad) {
        return webClient.get()
                .uri(usuarioServiceUrl + "/usuarios/{documento}", documentoIdentidad)
                .retrieve()
                .bodyToMono(Solicitud.class);
    }
}