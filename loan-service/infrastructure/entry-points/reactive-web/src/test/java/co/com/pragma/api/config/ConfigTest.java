package co.com.pragma.api.config;

import co.com.pragma.api.RouterRest;
import co.com.pragma.api.SolicitudHandler;
import co.com.pragma.api.dto.SolicitudRequestDTO;
import co.com.pragma.api.dto.SolicitudResponseDTO;
import co.com.pragma.api.mapper.SolicitudApiMapper;
import co.com.pragma.model.solicitud.solicitudprestamos.Solicitud;
import co.com.pragma.usecase.solicitarprestamo.SolicitarPrestamoUseCase;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {RouterRest.class, SolicitudHandler.class})
@WebFluxTest
@Import({CorsConfig.class, SecurityHeadersConfig.class})
class ConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private SolicitarPrestamoUseCase useCase;

    @MockBean
    private SolicitudApiMapper mapper;

    @MockBean
    private Validator validator;

    @BeforeEach
    void setup() {
        SolicitudRequestDTO dto = new SolicitudRequestDTO("123456789", new BigDecimal("5000000"), 24, "uuid", null);
        Solicitud domain = Solicitud.builder().id("sol123").build();

        when(validator.validate(any(SolicitudRequestDTO.class))).thenReturn(Collections.emptySet());
        when(mapper.toDomain(dto)).thenReturn(domain);
        when(useCase.crearSolicitud(domain)).thenReturn(Mono.just(domain));
        when(mapper.toResponseDTO(domain)).thenReturn(new SolicitudResponseDTO("sol123", "123456789", new BigDecimal("5000000"), 24, "uuid", null));
    }

    @Test
    void corsConfigurationShouldAllowOrigins() {
        webTestClient.post()
                .uri("/api/v1/solicitudes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new SolicitudRequestDTO("123456789", new BigDecimal("5000000"), 24, "uuid", null))
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().valueEquals("Content-Security-Policy", "default-src 'self'; frame-ancestors 'self'; form-action 'self'")
                .expectHeader().valueEquals("Strict-Transport-Security", "max-age=31536000;")
                .expectHeader().valueEquals("X-Content-Type-Options", "nosniff")
                .expectHeader().valueEquals("Server", "")
                .expectHeader().valueEquals("Cache-Control", "no-store")
                .expectHeader().valueEquals("Pragma", "no-cache")
                .expectHeader().valueEquals("Referrer-Policy", "strict-origin-when-cross-origin");
    }

}
