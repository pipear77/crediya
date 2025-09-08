package co.com.pragma.r2dbc.adapter;

import co.com.pragma.model.solicitud.gateways.UsuarioClientRepository;
import co.com.pragma.model.solicitud.solicitudprestamos.Solicitud;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class UsuarioClientAdapter implements UsuarioClientRepository {

    private final WebClient webClient;

    @Value("${external.user-service.url}")
    private String usuarioServiceUrl;

    @Override
    public Mono<Solicitud> buscarPorDocumento(String documentoIdentidad, String token) {
        String cleanedToken = token.startsWith("Bearer ") ? token.substring(7).trim() : token;
        log.info("🔐 Validando usuario con documento: {}", documentoIdentidad);

        return webClient.get()
                .uri(usuarioServiceUrl + "/api/v1/usuarios/{documento}", documentoIdentidad)
                .header("Authorization", "Bearer " + cleanedToken)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> {
                    log.warn("❌ user-service respondió con error {} al consultar documento {}", response.statusCode(), documentoIdentidad);
                    return Mono.error(new RuntimeException("Error al validar usuario: " + response.statusCode()));
                })
                .bodyToMono(Solicitud.class)
                .doOnNext(usuario -> log.info("✅ Usuario encontrado: {}", usuario.getDocumentoIdentidad()))
                .doOnError(e -> log.error("❌ Fallo al consultar usuario: {}", documentoIdentidad, e));
    }
}
