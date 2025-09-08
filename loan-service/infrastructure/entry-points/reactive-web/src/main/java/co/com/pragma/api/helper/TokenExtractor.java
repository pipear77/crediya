package co.com.pragma.api.helper;

import co.com.pragma.api.dto.usuario.UsuarioAutenticadoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.springframework.http.HttpStatusCode;


@Slf4j
@Component
@RequiredArgsConstructor
public class TokenExtractor {

    private final WebClient authWebClient;

    public Mono<UsuarioAutenticadoDTO> extractUsuario(String token) {
        String cleanedToken = token.startsWith("Bearer ") ? token.substring(7).trim() : token;
        log.info("🔗 Token limpio enviado al user-service: '{}'", cleanedToken);

        return authWebClient.get()
                .uri("/api/v1/validate")
                .header("Authorization", "Bearer " + cleanedToken)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> {
                    log.warn("❌ user-service respondió con error: {}", response.statusCode());
                    return Mono.error(new RuntimeException("Error al validar token: " + response.statusCode()));
                })
                .bodyToMono(UsuarioAutenticadoDTO.class)
                .doOnNext(dto -> log.info("✅ Usuario autenticado: id={}, rol={}, documento={}",
                        dto.getId(), dto.getRol(), dto.getDocumentoIdentidad()))
                .doOnError(e -> log.error("❌ Fallo al extraer usuario desde token", e));
    }

}
