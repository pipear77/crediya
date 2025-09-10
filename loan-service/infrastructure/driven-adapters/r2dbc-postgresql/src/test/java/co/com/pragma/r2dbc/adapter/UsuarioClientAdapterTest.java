/*
package co.com.pragma.r2dbc.adapter;

import co.com.pragma.api.dto.usuario.UsuarioAutenticadoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioClientAdapterTest {

    private WebClient webClient;
    private UsuarioClientAdapter adapter;

    private final String baseUrl = "http://localhost:8080";
    private final String token = "Bearer fake-token";
    private final String documento = "123456789";

    private RequestHeadersUriSpec<?> uriSpec;
    private RequestHeadersSpec<?> headersSpec;
    private ResponseSpec responseSpec;


    @BeforeEach
    void setUp() {
        webClient = mock(WebClient.class);
        uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        headersSpec = mock(WebClient.RequestHeadersSpec.class);
        responseSpec = mock(WebClient.ResponseSpec.class);

        adapter = new UsuarioClientAdapter(webClient);
        adapter.usuarioServiceUrl = baseUrl;
    }

    @Test
    void buscarPorDocumento_deberiaRetornarUsuario_siRespuestaEsExitosa() {
        UsuarioAutenticadoDTO dto = UsuarioAutenticadoDTO.builder()
                .id("user-id")
                .correo("leidy@example.com")
                .documentoIdentidad(documento)
                .nombres("Leidy")
                .apellidos("Prueba")
                .rol("ROL_CLIENTE")
                .estado("ACTIVO")
                .sesionActiva(true)
                .salarioBase(new BigDecimal("2500000"))
                .build();

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(baseUrl + "/api/v1/usuarios/{documento}", documento)).thenReturn(uriSpec);
        when(uriSpec.header("Authorization", "Bearer fake-token")).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UsuarioAutenticadoDTO.class)).thenReturn(Mono.just(dto));

        StepVerifier.create(adapter.buscarPorDocumento(documento, token))
                .assertNext(usuario -> {
                    assertEquals("user-id", usuario.getId());
                    assertEquals("leidy@example.com", usuario.getCorreo());
                    assertEquals("Leidy Prueba", usuario.getNombres() + " " + usuario.getApellidos());
                    assertEquals(new BigDecimal("2500000"), usuario.getSalarioBase());
                })
                .verifyComplete();

        verify(webClient).get();
        verify(uriSpec).uri(baseUrl + "/api/v1/usuarios/{documento}", documento);
        verify(uriSpec).header("Authorization", "Bearer fake-token");
        verify(headersSpec).retrieve();
        verify(responseSpec).onStatus(any(), any());
        verify(responseSpec).bodyToMono(UsuarioAutenticadoDTO.class);
    }

    @Test
    void buscarPorDocumento_deberiaRetornarError_siRespuestaEsFallida() {
        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(baseUrl + "/api/v1/usuarios/{documento}", documento)).thenReturn(uriSpec);
        when(uriSpec.header("Authorization", "Bearer fake-token")).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenAnswer(invocation -> {
            var predicate = invocation.getArgument(0);
            var errorHandler = invocation.getArgument(1);
            if (predicate.test(HttpStatus.INTERNAL_SERVER_ERROR)) {
                return responseSpec;
            }
            return responseSpec;
        });
        when(responseSpec.bodyToMono(UsuarioAutenticadoDTO.class)).thenReturn(Mono.error(new RuntimeException("Error al validar usuario: 500")));

        StepVerifier.create(adapter.buscarPorDocumento(documento, token))
                .expectErrorMatches(error -> error instanceof RuntimeException &&
                        error.getMessage().contains("Error al validar usuario"))
                .verify();

        verify(webClient).get();
        verify(uriSpec).uri(baseUrl + "/api/v1/usuarios/{documento}", documento);
        verify(uriSpec).header("Authorization", "Bearer fake-token");
        verify(headersSpec).retrieve();
        verify(responseSpec).onStatus(any(), any());
        verify(responseSpec).bodyToMono(UsuarioAutenticadoDTO.class);
    }
}
*/
