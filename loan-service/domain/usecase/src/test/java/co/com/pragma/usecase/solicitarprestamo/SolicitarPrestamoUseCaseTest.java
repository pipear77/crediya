package co.com.pragma.usecase.solicitarprestamo;

import co.com.pragma.model.solicitud.enums.EstadoSolicitud;
import co.com.pragma.model.solicitud.gateways.UsuarioClientRepository;
import co.com.pragma.model.solicitud.solicitudprestamos.Solicitud;
import co.com.pragma.model.solicitud.solicitudprestamos.gateways.SolicitudRepository;
import co.com.pragma.model.solicitud.tipoprestamos.TipoPrestamo;
import co.com.pragma.model.solicitud.tipoprestamos.gateways.TipoPrestamoRepository;
import co.com.pragma.model.solicitud.usuario.Usuario;
import co.com.pragma.usecase.exceptions.TipoPrestamoNotFoundException;
import co.com.pragma.usecase.exceptions.ValidacionCampoException;
import co.com.pragma.usecase.exceptions.error.CodigosEstadoHttp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.UUID;

import static co.com.pragma.usecase.common.constantes.Constantes.USUARIO_NO_ENCONTRADO;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SolicitarPrestamoUseCaseTest {

    private SolicitarPrestamoUseCase useCase;

    @Mock
    private SolicitudRepository solicitudRepository;

    @Mock
    private UsuarioClientRepository usuarioClientRepository;

    @Mock
    private TipoPrestamoRepository tipoPrestamoRepository;

    private final String token = "fake-token";
    private final UUID tipoId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        useCase = new SolicitarPrestamoUseCase(solicitudRepository, usuarioClientRepository, tipoPrestamoRepository);
    }

    private Solicitud solicitudValida() {
        return Solicitud.builder()
                .documentoIdentidad("111111")
                .montoSolicitado(new BigDecimal("5000000"))
                .plazoMeses(12)
                .idTipoPrestamo(tipoId)
                .build();
    }

    private Usuario usuarioValido() {
        return Usuario.builder()
                .id("user-id")
                .correo("cliente@example.com")
                .documentoIdentidad("111111")
                .nombres("Leidy")
                .apellidos("Prueba")
                .rol("ROL_CLIENTE")
                .estado("ACTIVO")
                .sesionActiva(true)
                .salarioBase(new BigDecimal("2500000"))
                .build();
    }

    private TipoPrestamo tipoPrestamoValido(UUID id) {
        return TipoPrestamo.builder()
                .id(id)
                .nombre("Crédito Educativo")
                .montoMinimo(new BigDecimal("1000000"))
                .montoMaximo(new BigDecimal("10000000"))
                .tasaInteres(0.015)
                .aprobacionAutomatica(true)
                .build();
    }

    @Test
    void crearSolicitud_deberiaGuardarSolicitud_siTodoEsValido() {
        Solicitud solicitud = solicitudValida();
        Usuario usuario = usuarioValido();
        TipoPrestamo tipo = tipoPrestamoValido(tipoId);

        when(usuarioClientRepository.buscarPorDocumento("111111", token)).thenReturn(Mono.just(usuario));
        when(tipoPrestamoRepository.findById(tipoId)).thenReturn(Mono.just(tipo));
        when(solicitudRepository.guardar(any(Solicitud.class))).thenAnswer(invocation -> {
            Solicitud s = invocation.getArgument(0);
            return Mono.just(s.toBuilder()
                    .id(UUID.randomUUID())
                    .estado(EstadoSolicitud.PENDIENTE_REVISION)
                    .build());
        });

        StepVerifier.create(useCase.crearSolicitud(solicitud, token))
                .assertNext(saved -> {
                    assertNotNull(saved.getId());
                    assertEquals(EstadoSolicitud.PENDIENTE_REVISION, saved.getEstado());
                    assertEquals("111111", saved.getDocumentoIdentidad());
                    assertEquals("cliente@example.com", saved.getCorreo());
                    assertEquals("Leidy Prueba", saved.getNombre());
                    assertEquals("Crédito Educativo", saved.getTipoPrestamo());
                    assertEquals(0.015, saved.getTasaInteres());
                    assertEquals(new BigDecimal("2500000"), saved.getSalarioBase());
                })
                .verifyComplete();

        verify(usuarioClientRepository).buscarPorDocumento("111111", token);
        verify(tipoPrestamoRepository).findById(tipoId);
        verify(solicitudRepository).guardar(any(Solicitud.class));
        verifyNoMoreInteractions(usuarioClientRepository, tipoPrestamoRepository, solicitudRepository);
    }

    @Test
    void crearSolicitud_deberiaFallar_siMontoFueraDeRango() {
        Solicitud solicitud = solicitudValida().toBuilder()
                .montoSolicitado(new BigDecimal("20000000")) // fuera del rango
                .build();

        when(usuarioClientRepository.buscarPorDocumento("111111", token)).thenReturn(Mono.just(usuarioValido()));
        when(tipoPrestamoRepository.findById(tipoId)).thenReturn(Mono.just(tipoPrestamoValido(tipoId)));

        StepVerifier.create(useCase.crearSolicitud(solicitud, token))
                .expectErrorSatisfies(error -> {
                    assertInstanceOf(ValidacionCampoException.class, error);
                    assertTrue(error.getMessage().contains("El monto solicitado debe estar entre"));
                })
                .verify();

        verify(usuarioClientRepository).buscarPorDocumento("111111", token);
        verify(tipoPrestamoRepository).findById(tipoId);
        verifyNoInteractions(solicitudRepository);
    }

    @Test
    void crearSolicitud_deberiaFallar_siUsuarioNoExiste() {
        Solicitud solicitud = solicitudValida();

        when(usuarioClientRepository.buscarPorDocumento("111111", token)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.crearSolicitud(solicitud, token))
                .expectErrorSatisfies(error -> {
                    assertInstanceOf(ValidacionCampoException.class, error);
                    assertEquals(USUARIO_NO_ENCONTRADO, ((ValidacionCampoException) error).getMessage());
                    assertEquals(CodigosEstadoHttp.NOT_FOUND.getCode(), ((ValidacionCampoException) error).getCode());
                })
                .verify();

        verify(usuarioClientRepository).buscarPorDocumento("111111", token);
        verifyNoInteractions(solicitudRepository, tipoPrestamoRepository);
    }

    @Test
    void crearSolicitud_deberiaFallar_siTipoPrestamoNoExiste() {
        Solicitud solicitud = solicitudValida();

        when(usuarioClientRepository.buscarPorDocumento("111111", token)).thenReturn(Mono.just(usuarioValido()));
        when(tipoPrestamoRepository.findById(tipoId)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.crearSolicitud(solicitud, token))
                .expectError(TipoPrestamoNotFoundException.class)
                .verify();

        verify(usuarioClientRepository).buscarPorDocumento("111111", token);
        verify(tipoPrestamoRepository).findById(tipoId);
        verifyNoInteractions(solicitudRepository);
    }

    @Test
    void crearSolicitud_deberiaFallar_siMontoEsNulo() {
        Solicitud solicitud = solicitudValida().toBuilder().montoSolicitado(null).build();

        when(usuarioClientRepository.buscarPorDocumento(anyString(), anyString())).thenReturn(Mono.never());

        StepVerifier.create(useCase.crearSolicitud(solicitud, token))
                .expectError(ValidacionCampoException.class)
                .verify();

        verifyNoInteractions(solicitudRepository, tipoPrestamoRepository);
    }

    @Test
    void crearSolicitud_deberiaFallar_siPlazoEsNulo() {
        Solicitud solicitud = solicitudValida().toBuilder().plazoMeses(null).build();

        when(usuarioClientRepository.buscarPorDocumento(anyString(), anyString())).thenReturn(Mono.never());

        StepVerifier.create(useCase.crearSolicitud(solicitud, token))
                .expectError(ValidacionCampoException.class)
                .verify();

        verifyNoInteractions(solicitudRepository, tipoPrestamoRepository);
    }

    @Test
    void crearSolicitud_deberiaFallar_siTipoPrestamoEsNulo() {
        Solicitud solicitud = solicitudValida().toBuilder().idTipoPrestamo(null).build();

        when(usuarioClientRepository.buscarPorDocumento(anyString(), anyString())).thenReturn(Mono.never());

        StepVerifier.create(useCase.crearSolicitud(solicitud, token))
                .expectErrorSatisfies(error -> {
                    assertInstanceOf(ValidacionCampoException.class, error);
                    assertEquals("El tipo es obligatorio", error.getMessage());
                })
                .verify();

        verifyNoInteractions(solicitudRepository, tipoPrestamoRepository);
    }
}
