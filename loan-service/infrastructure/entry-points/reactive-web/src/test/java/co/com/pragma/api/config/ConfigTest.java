package co.com.pragma.api.config;

import co.com.pragma.api.RouterRest;
import co.com.pragma.api.SolicitudHandler;
import co.com.pragma.api.dto.solicitud.SolicitudRequestDTO;
import co.com.pragma.api.dto.solicitud.SolicitudResponseDTO;
import co.com.pragma.api.dto.usuario.UsuarioAutenticadoDTO;
import co.com.pragma.api.helper.TokenExtractor;
import co.com.pragma.api.mapper.SolicitudApiMapper;
import co.com.pragma.model.solicitud.enums.EstadoSolicitud;
import co.com.pragma.model.solicitud.solicitudprestamos.Solicitud;
import co.com.pragma.usecase.solicitarprestamo.ActualizarEstadoSolicitudUseCaseInterface;
import co.com.pragma.usecase.solicitarprestamo.SolicitarPrestamoUseCase;
import co.com.pragma.usecase.solicitarprestamo.ListarSolicitudesParaRevisionUseCaseInterface;
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
import java.time.LocalDate;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest
@ContextConfiguration(classes = {RouterRest.class, SolicitudHandler.class})
@Import({CorsConfig.class, SecurityHeadersConfig.class})
class ConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private SolicitarPrestamoUseCase useCase;

    @MockBean
    private ListarSolicitudesParaRevisionUseCaseInterface listarUseCase;

    @MockBean
    private SolicitudApiMapper mapper;

    @MockBean
    private Validator validator;

    @MockBean
    private TokenExtractor tokenExtractor;

    @MockBean
    private ActualizarEstadoSolicitudUseCaseInterface actualizarEstadoSolicitudUseCase;


    private SolicitudRequestDTO requestDTO;
    private Solicitud domain;
    private SolicitudResponseDTO responseDTO;

    @BeforeEach
    void setup() {
        UUID solicitudId = UUID.randomUUID();
        UUID tipoPrestamoId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();
        String documentoIdentidad = "123456789";
        String canal = "APP_WEB";
        BigDecimal montoSolicitado = new BigDecimal("5000000.00");
        Integer plazoMeses = 24;
        BigDecimal salarioBase = new BigDecimal("3500000.00");
        Double tasaInteres = 1.5;

        requestDTO = new SolicitudRequestDTO(
                canal,
                montoSolicitado,
                plazoMeses,
                tipoPrestamoId.toString(),
                salarioBase,
                tasaInteres
        );

        domain = Solicitud.builder()
                .id(solicitudId)
                .documentoIdentidad(documentoIdentidad)
                .canal(canal)
                .montoSolicitado(montoSolicitado)
                .plazoMeses(plazoMeses)
                .idTipoPrestamo(tipoPrestamoId)
                .salarioBase(salarioBase)
                .montoMensualSolicitud(new BigDecimal("208333.33"))
                .tasaInteres(tasaInteres)
                .correo("arcila@example.com")
                .nombre("Ana Arcila")
                .estado(EstadoSolicitud.PENDIENTE_REVISION)
                .build();

        responseDTO = new SolicitudResponseDTO(
                solicitudId.toString(),
                documentoIdentidad,
                "arcila@example.com",
                "Ana Arcila",
                canal,
                montoSolicitado,
                plazoMeses,
                tipoPrestamoId.toString(),
                "Crédito Educativo",
                salarioBase,
                new BigDecimal("208333.33"),
                tasaInteres,
                EstadoSolicitud.PENDIENTE_REVISION
        );

        when(validator.validate(any(SolicitudRequestDTO.class))).thenReturn(Collections.emptySet());

        when(tokenExtractor.extractUsuario(any(String.class)))
                .thenReturn(Mono.just(
                        UsuarioAutenticadoDTO.builder()
                                .id(usuarioId.toString())
                                .documentoIdentidad(documentoIdentidad)
                                .correo("arcila@example.com")
                                .nombres("Ana")
                                .apellidos("Arcila")
                                .rol("ROL_CLIENTE")
                                .estado("ACTIVO")
                                .sesionActiva(true)
                                .salarioBase(salarioBase)
                                .build()
                ));

        when(mapper.toDomain(requestDTO)).thenReturn(domain);
        when(useCase.crearSolicitud(any(Solicitud.class), any(String.class))).thenReturn(Mono.just(domain));
        when(mapper.toResponseDTO(domain)).thenReturn(responseDTO);
    }

    @Test
    void debePermitirCORSYRetornarHeadersDeSeguridad() {
        webTestClient.post()
                .uri("/api/v1/solicitudes")
                .header(HttpHeaders.AUTHORIZATION, "Bearer fake-token")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDTO)
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
