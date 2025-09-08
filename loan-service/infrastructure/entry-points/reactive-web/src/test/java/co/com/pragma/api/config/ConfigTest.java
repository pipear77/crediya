package co.com.pragma.api.config;

import co.com.pragma.api.RouterRest;
import co.com.pragma.api.SolicitudHandler;
import co.com.pragma.api.dto.solicitud.SolicitudRequestDTO;
import co.com.pragma.api.dto.solicitud.SolicitudResponseDTO;
import co.com.pragma.api.helper.TokenExtractor;
import co.com.pragma.api.mapper.SolicitudApiMapper;
import co.com.pragma.model.solicitud.enums.EstadoSolicitud;
import co.com.pragma.model.solicitud.solicitudprestamos.Solicitud;
import co.com.pragma.usecase.solicitarprestamo.SolicitarPrestamoUseCase;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;

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

    @MockBean
    private TokenExtractor tokenExtractor;

    private SolicitudRequestDTO dto;
    private Solicitud domain;
    private SolicitudResponseDTO responseDTO;

    @BeforeEach
    void setup() {
        UUID solicitudId = UUID.randomUUID();
        String documentoIdentidad = "123456789";
        UUID tipoPrestamoId = UUID.randomUUID();

        dto = new SolicitudRequestDTO(
                new BigDecimal("5000000"),
                24,
                "uuid-tipo-prestamo-001",
                EstadoSolicitud.PENDIENTE_REVISION
        );

        domain = Solicitud.builder()
                .id(solicitudId)
                .documentoIdentidad(documentoIdentidad)
                .montoSolicitado(dto.montoSolicitado())
                .plazoMeses(dto.plazoMeses())
                .idTipoPrestamo(tipoPrestamoId)
                .estado(dto.estado())
                .build();

        responseDTO = new SolicitudResponseDTO(
                solicitudId.toString(),
                documentoIdentidad,
                dto.montoSolicitado(),
                dto.plazoMeses(),
                dto.idTipoPrestamo(),
                dto.estado()
        );

        when(validator.validate(any(SolicitudRequestDTO.class))).thenReturn(Collections.emptySet());

        when(tokenExtractor.extractUsuario(any(String.class)))
                .thenReturn(Mono.just(
                        co.com.pragma.api.dto.usuario.UsuarioAutenticadoDTO.builder()
                                .documentoIdentidad(documentoIdentidad)
                                .rol("ROL_CLIENTE")
                                .estado("ACTIVO")
                                .sesionActiva(true)
                                .build()
                ));

        when(mapper.toDomain(dto, documentoIdentidad)).thenReturn(domain);
        when(useCase.crearSolicitud(any(Solicitud.class), any(String.class))).thenReturn(Mono.just(domain));
        when(mapper.toResponseDTO(domain)).thenReturn(responseDTO);
    }

    @Test
    void corsConfigurationShouldAllowOrigins() {
        UUID usuarioId = UUID.randomUUID(); // ✅ ID necesario para evitar NullPointerException

        when(tokenExtractor.extractUsuario(any(String.class)))
                .thenReturn(Mono.just(
                        co.com.pragma.api.dto.usuario.UsuarioAutenticadoDTO.builder()
                                .id(usuarioId.toString()) // ✅ ahora sí tiene ID
                                .documentoIdentidad("123456789")
                                .rol("ROL_CLIENTE")
                                .estado("ACTIVO")
                                .sesionActiva(true)
                                .build()
                ));

        webTestClient.post()
                .uri("/api/v1/solicitudes")
                .header(HttpHeaders.AUTHORIZATION, "Bearer fake-token")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
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
