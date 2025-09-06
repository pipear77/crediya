package co.com.pragma.api.helper;

import co.com.pragma.api.dto.usuario.UsuarioAutenticadoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class TokenExtractor {

    private final WebClient authWebClient;

    public Mono<UsuarioAutenticadoDTO> extractUsuario(String token) {
        return authWebClient.get()
                .uri("/auth/validate")
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(UsuarioAutenticadoDTO.class);
    }
}

