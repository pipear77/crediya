package co.com.pragma.api;

import co.com.pragma.api.dto.solicitud.SolicitudRequestDTO;
import co.com.pragma.api.dto.solicitud.SolicitudResponseDTO;
import co.com.pragma.api.dto.usuario.UsuarioAutenticadoDTO;
import co.com.pragma.api.helper.TokenExtractor;
import co.com.pragma.api.mapper.SolicitudApiMapper;
import co.com.pragma.model.solicitud.enums.EstadoSolicitud;
import co.com.pragma.model.solicitud.solicitudprestamos.Solicitud;
import co.com.pragma.usecase.solicitarprestamo.ActualizarEstadoSolicitudUseCaseInterface;
import co.com.pragma.usecase.solicitarprestamo.ListarSolicitudesParaRevisionUseCaseInterface;
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
import java.time.LocalDate;
import java.util.Collections;
import java.util.UUID;

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

    @MockBean
    private ListarSolicitudesParaRevisionUseCaseInterface listarUseCase;

    @MockBean
    private ActualizarEstadoSolicitudUseCaseInterface actualizarEstadoSolicitudUseCase;



    private SolicitudRequestDTO requestDTO;
    private Solicitud solicitud;
    private SolicitudResponseDTO responseDTO;

    @BeforeEach
    void setup() {
        UUID solicitudId = UUID.randomUUID();
        UUID tipoPrestamoId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();
        String documentoIdentidad = "123456789";
        String correo = "cristiano@gmail.com";
        String nombre = "Cristiano Ronaldo";
        String canal = "APP_WEB";
        BigDecimal montoSolicitado = new BigDecimal("5000000.00");
        Integer plazoMeses = 24;
        BigDecimal salarioBase = new BigDecimal("1000000.00");
        BigDecimal montoMensual = new BigDecimal("250000.00");
        Double tasaInteres = 0.15;

        requestDTO = new SolicitudRequestDTO(
                canal,
                montoSolicitado,
                plazoMeses,
                tipoPrestamoId.toString(),
                salarioBase,
                tasaInteres
        );

        solicitud = Solicitud.builder()
                .id(solicitudId)
                .documentoIdentidad(documentoIdentidad)
                .correo(correo)
                .nombre(nombre)
                .canal(canal)
                .montoSolicitado(montoSolicitado)
                .plazoMeses(plazoMeses)
                .idTipoPrestamo(tipoPrestamoId)
                .salarioBase(salarioBase)
                .montoMensualSolicitud(montoMensual)
                .tasaInteres(tasaInteres)
                .estado(EstadoSolicitud.PENDIENTE_REVISION)
                .build();

        responseDTO = new SolicitudResponseDTO(
                solicitudId.toString(),
                documentoIdentidad,
                correo,
                nombre,
                canal,
                montoSolicitado,
                plazoMeses,
                tipoPrestamoId.toString(),
                "Crédito Educativo",
                salarioBase,
                montoMensual,
                tasaInteres,
                EstadoSolicitud.PENDIENTE_REVISION
        );

        when(validator.validate(any(SolicitudRequestDTO.class))).thenReturn(Collections.emptySet());

        when(tokenExtractor.extractUsuario(any(String.class)))
                .thenReturn(Mono.just(
                        UsuarioAutenticadoDTO.builder()
                                .id(usuarioId.toString())
                                .documentoIdentidad(documentoIdentidad)
                                .correo(correo)
                                .nombres(nombre)
                                .rol("ROL_CLIENTE")
                                .estado("ACTIVO")
                                .sesionActiva(true)
                                .salarioBase(salarioBase)
                                .build()
                ));

        when(mapper.toDomain(requestDTO)).thenReturn(solicitud);
        when(useCase.crearSolicitud(any(Solicitud.class), any(String.class))).thenReturn(Mono.just(solicitud));
        when(mapper.toResponseDTO(solicitud)).thenReturn(responseDTO);
    }

    @Test
    @DisplayName("✅ POST /api/v1/solicitudes debe retornar 201 con cuerpo de respuesta")
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
                    // Validaciones explícitas con mensajes claros
                    org.junit.jupiter.api.Assertions.assertEquals(responseDTO.id(), resp.id(), "ID no coincide");
                    org.junit.jupiter.api.Assertions.assertEquals(responseDTO.documentoIdentidad(), resp.documentoIdentidad(), "Documento no coincide");
                    org.junit.jupiter.api.Assertions.assertEquals(responseDTO.correo(), resp.correo(), "Correo no coincide");
                    org.junit.jupiter.api.Assertions.assertEquals(responseDTO.nombre(), resp.nombre(), "Nombre no coincide");
                    org.junit.jupiter.api.Assertions.assertEquals(responseDTO.canal(), resp.canal(), "Canal no coincide");
                    org.junit.jupiter.api.Assertions.assertEquals(responseDTO.montoSolicitado(), resp.montoSolicitado(), "Monto solicitado no coincide");
                    org.junit.jupiter.api.Assertions.assertEquals(responseDTO.plazoMeses(), resp.plazoMeses(), "Plazo no coincide");
                    org.junit.jupiter.api.Assertions.assertEquals(responseDTO.idTipoPrestamo(), resp.idTipoPrestamo(), "ID tipo préstamo no coincide");
                    org.junit.jupiter.api.Assertions.assertEquals(responseDTO.tipoTramite(), resp.tipoTramite(), "Tipo trámite no coincide");
                    org.junit.jupiter.api.Assertions.assertEquals(responseDTO.salarioBase(), resp.salarioBase(), "Salario base no coincide");
                    org.junit.jupiter.api.Assertions.assertEquals(responseDTO.montoMensualSolicitud(), resp.montoMensualSolicitud(), "Monto mensual no coincide");
                    org.junit.jupiter.api.Assertions.assertEquals(responseDTO.tasaInteres(), resp.tasaInteres(), "Tasa de interés no coincide");
                    org.junit.jupiter.api.Assertions.assertEquals(responseDTO.estado(), resp.estado(), "Estado no coincide");
                });
    }

}
