package co.com.pragma.usecase.solicitarprestamo;

import co.com.pragma.model.solicitud.enums.EstadoSolicitud;
import co.com.pragma.model.solicitud.gateways.UsuarioClientRepository;
import co.com.pragma.model.solicitud.solicitudprestamos.Solicitud;
import co.com.pragma.model.solicitud.solicitudprestamos.gateways.SolicitudRepository;
import co.com.pragma.model.solicitud.tipoprestamos.TipoPrestamo;
import co.com.pragma.model.solicitud.tipoprestamos.gateways.TipoPrestamoRepository;
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

import static co.com.pragma.usecase.common.constantes.Constantes.USUARIO_NO_ENCONTRADO;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

    @BeforeEach
    void setUp() {
        useCase = new SolicitarPrestamoUseCase(solicitudRepository, usuarioClientRepository, tipoPrestamoRepository);
    }

    private static Solicitud solicitudValida() {
        return Solicitud.builder()
                .documentoIdentidad("111111")
                .montoSolicitado(new BigDecimal("10000"))
                .plazoMeses(12)
                .idTipoPrestamo("uuid-tipo-prestamo-001")
                .build();
    }

    private static TipoPrestamo loanType(String id) {
        TipoPrestamo t = new TipoPrestamo();
        t.setId(id);
        t.setNombre("PERSONAL");
        return t;
    }

    @Test
    void crearSolicitud_deberiaGuardarSolicitud_siTodoEsValido() {
        Solicitud solicitud = solicitudValida();

        when(usuarioClientRepository.buscarPorDocumento("111111"))
                .thenReturn(Mono.just(solicitud));
        when(tipoPrestamoRepository.findById("uuid-tipo-prestamo-001"))
                .thenReturn(Mono.just(loanType("uuid-tipo-prestamo-001")));
        when(solicitudRepository.guardar(any(Solicitud.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(useCase.crearSolicitud(solicitud))
                .assertNext(saved -> {
                    assertNotNull(saved.getId());
                    assertEquals(EstadoSolicitud.PENDIENTE_REVISION, saved.getEstado());
                    assertEquals("111111", saved.getDocumentoIdentidad());
                })
                .verifyComplete();

        verify(usuarioClientRepository).buscarPorDocumento("111111");
        verify(tipoPrestamoRepository).findById("uuid-tipo-prestamo-001");
        verify(solicitudRepository).guardar(any(Solicitud.class));
        verifyNoMoreInteractions(usuarioClientRepository, tipoPrestamoRepository, solicitudRepository);
    }

    @Test
    void crearSolicitud_deberiaFallar_siUsuarioNoExiste() {
        Solicitud solicitud = solicitudValida();

        when(usuarioClientRepository.buscarPorDocumento("111111"))
                .thenReturn(Mono.empty());
        when(tipoPrestamoRepository.findById("uuid-tipo-prestamo-001"))
                .thenReturn(Mono.just(new TipoPrestamo()));

        StepVerifier.create(useCase.crearSolicitud(solicitud))
                .expectErrorSatisfies(error -> {
                    assertInstanceOf(ValidacionCampoException.class, error);
                    assertEquals(USUARIO_NO_ENCONTRADO, ((ValidacionCampoException) error).getMessage());
                    assertEquals(CodigosEstadoHttp.NOT_FOUND.getCode(), ((ValidacionCampoException) error).getCode());
                })
                .verify();

        verify(usuarioClientRepository).buscarPorDocumento("111111");
        verifyNoInteractions(solicitudRepository);
    }

    @Test
    void crearSolicitud_deberiaFallar_siTipoPrestamoNoExiste() {
        Solicitud solicitud = solicitudValida();

        when(usuarioClientRepository.buscarPorDocumento("111111"))
                .thenReturn(Mono.just(solicitud));
        when(tipoPrestamoRepository.findById("uuid-tipo-prestamo-001"))
                .thenReturn(Mono.empty());

        StepVerifier.create(useCase.crearSolicitud(solicitud))
                .expectError(TipoPrestamoNotFoundException.class)
                .verify();

        verify(usuarioClientRepository).buscarPorDocumento("111111");
        verify(tipoPrestamoRepository).findById("uuid-tipo-prestamo-001");
        verifyNoInteractions(solicitudRepository);
    }

    @Test
    void crearSolicitud_deberiaFallar_siFaltaDocumentoIdentidad() {
        Solicitud solicitud = solicitudValida().toBuilder()
                .documentoIdentidad(null)
                .build();

        // Mock explícito para evitar NPE por llamada con null
        when(usuarioClientRepository.buscarPorDocumento(isNull()))
                .thenReturn(Mono.just(new Solicitud()));

        when(tipoPrestamoRepository.findById(any()))
                .thenReturn(Mono.just(new TipoPrestamo())); // o isNull() si quieres ser más preciso

        StepVerifier.create(useCase.crearSolicitud(solicitud))
                .expectError(ValidacionCampoException.class)
                .verify();

        verifyNoInteractions(solicitudRepository);
    }


    @Test
    void crearSolicitud_deberiaFallar_siMontoEsNulo() {
        Solicitud solicitud = solicitudValida().toBuilder()
                .montoSolicitado(null)
                .build();

        when(usuarioClientRepository.buscarPorDocumento("111111"))
                .thenReturn(Mono.just(solicitud));
        when(tipoPrestamoRepository.findById("uuid-tipo-prestamo-001"))
                .thenReturn(Mono.just(new TipoPrestamo()));

        StepVerifier.create(useCase.crearSolicitud(solicitud))
                .expectError(ValidacionCampoException.class)
                .verify();

        verifyNoInteractions(solicitudRepository);
    }

    @Test
    void crearSolicitud_deberiaFallar_siPlazoEsNulo() {
        Solicitud solicitud = solicitudValida().toBuilder()
                .plazoMeses(null)
                .build();

        when(usuarioClientRepository.buscarPorDocumento("111111"))
                .thenReturn(Mono.just(solicitud));
        when(tipoPrestamoRepository.findById("uuid-tipo-prestamo-001"))
                .thenReturn(Mono.just(new TipoPrestamo()));

        StepVerifier.create(useCase.crearSolicitud(solicitud))
                .expectError(ValidacionCampoException.class)
                .verify();

        verifyNoInteractions(solicitudRepository);
    }

    @Test
    void crearSolicitud_deberiaFallar_siTipoPrestamoEsNulo() {
        Solicitud solicitud = solicitudValida().toBuilder()
                .idTipoPrestamo(null)
                .build();

        when(usuarioClientRepository.buscarPorDocumento("111111"))
                .thenReturn(Mono.just(solicitud));
        when(tipoPrestamoRepository.findById(isNull()))
                .thenReturn(Mono.just(new TipoPrestamo()));

        StepVerifier.create(useCase.crearSolicitud(solicitud))
                .expectError(ValidacionCampoException.class)
                .verify();

        verifyNoInteractions(solicitudRepository);
    }
}
