package co.com.pragma.api;

import co.com.pragma.api.dto.SolicitudRequestDTO;
import co.com.pragma.api.dto.SolicitudResponseDTO;
import co.com.pragma.api.mapper.SolicitudApiMapper;
import co.com.pragma.model.solicitud.enums.EstadoSolicitud;
import co.com.pragma.model.solicitud.solicitudprestamos.Solicitud;
import co.com.pragma.usecase.solicitarprestamo.SolicitarPrestamoUseCase;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
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

    @Test
    @DisplayName("POST /api/v1/solicitudes should return 201 Created with response body")
    void shouldCreateSolicitudSuccessfully() {
        SolicitudRequestDTO requestDTO = new SolicitudRequestDTO(
                "123456789",
                new BigDecimal("5000000"),
                24,
                "uuid-tipo-prestamo-001",
                EstadoSolicitud.PENDIENTE_REVISION
        );

        Solicitud solicitud = Solicitud.builder()
                .id("sol123")
                .documentoIdentidad("123456789")
                .montoSolicitado(new BigDecimal("5000000"))
                .plazoMeses(24)
                .idTipoPrestamo("uuid-tipo-prestamo-001")
                .estado(EstadoSolicitud.PENDIENTE_REVISION)
                .build();

        SolicitudResponseDTO responseDTO = new SolicitudResponseDTO(
                "sol123",
                "123456789",
                new BigDecimal("5000000"),
                24,
                "uuid-tipo-prestamo-001",
                EstadoSolicitud.PENDIENTE_REVISION
        );

        when(validator.validate(any(SolicitudRequestDTO.class))).thenReturn(Collections.emptySet());
        when(mapper.toDomain(requestDTO)).thenReturn(solicitud);
        when(useCase.crearSolicitud(solicitud)).thenReturn(Mono.just(solicitud));
        when(mapper.toResponseDTO(solicitud)).thenReturn(responseDTO);

        webTestClient.post()
                .uri("/api/v1/solicitudes")
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
