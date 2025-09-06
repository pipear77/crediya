package co.com.pragma.api;

import co.com.pragma.api.dto.solicitud.SolicitudRequestDTO;
import co.com.pragma.api.dto.solicitud.SolicitudResponseDTO;
import co.com.pragma.api.dto.usuario.UsuarioAutenticadoDTO;
import co.com.pragma.api.helper.TokenExtractor;
import co.com.pragma.api.mapper.SolicitudApiMapper;
import co.com.pragma.model.solicitud.enums.EstadoSolicitud;
import co.com.pragma.model.solicitud.solicitudprestamos.Solicitud;
import co.com.pragma.usecase.solicitarprestamo.SolicitarPrestamoUseCase;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest
@ContextConfiguration(classes = {RouterRest.class, SolicitudHandler.class})
@Import({SolicitudHandler.class})
class RouterRestTest {

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

    private SolicitudRequestDTO requestDTO;
    private Solicitud solicitud;
    private SolicitudResponseDTO responseDTO;

    @BeforeEach
    void setup() {
        requestDTO = new SolicitudRequestDTO(
                new BigDecimal("5000000"),
                24,
                "uuid-tipo-prestamo-001",
                EstadoSolicitud.PENDIENTE_REVISION
        );

        solicitud = Solicitud.builder()
                .id("sol123")
                .documentoIdentidad("123456789")
                .montoSolicitado(requestDTO.montoSolicitado())
                .plazoMeses(requestDTO.plazoMeses())
                .idTipoPrestamo(requestDTO.idTipoPrestamo())
                .estado(requestDTO.estado())
                .build();

        responseDTO = new SolicitudResponseDTO(
                "sol123",
                "123456789",
                requestDTO.montoSolicitado(),
                requestDTO.plazoMeses(),
                requestDTO.idTipoPrestamo(),
                requestDTO.estado()
        );

        when(validator.validate(any(SolicitudRequestDTO.class))).thenReturn(Collections.emptySet());

        when(tokenExtractor.extractUsuario(any(String.class)))
                .thenReturn(Mono.just(
                        UsuarioAutenticadoDTO.builder()
                                .documentoIdentidad("123456789")
                                .rol("ROL_CLIENTE")
                                .estado("ACTIVO")
                                .sesionActiva(true)
                                .build()
                ));

        when(mapper.toDomain(requestDTO, "123456789")).thenReturn(solicitud);
        when(useCase.crearSolicitud(solicitud)).thenReturn(Mono.just(solicitud));
        when(mapper.toResponseDTO(solicitud)).thenReturn(responseDTO);
    }

    @Test
    @DisplayName("POST /api/v1/solicitudes should return 201 Created with response body")
    void shouldCreateSolicitudSuccessfully() {
        webTestClient.post()
                .uri("/api/v1/solicitudes")
                .header(HttpHeaders.AUTHORIZATION, "Bearer fake-token")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(requestDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody(SolicitudResponseDTO.class)
                .value(resp -> {
                    assert resp.id().equals("sol123");
                    assert resp.documentoIdentidad().equals("123456789");
                });
    }
}
