package co.com.pragma.usecase.solicitarprestamo;

import co.com.pragma.model.solicitud.enums.EstadoSolicitud;
import co.com.pragma.model.solicitud.gateways.UsuarioClientRepository;
import co.com.pragma.model.solicitud.solicitudprestamos.Solicitud;
import co.com.pragma.model.solicitud.solicitudprestamos.gateways.SolicitudRepository;
import co.com.pragma.model.solicitud.tipoprestamos.TipoPrestamo;
import co.com.pragma.model.solicitud.tipoprestamos.gateways.TipoPrestamoRepository;
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
                .id("123")
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

        when(usuarioClientRepository.buscarPorDocumento(solicitud.getDocumentoIdentidad()))
                .thenReturn(Mono.just(solicitud));
        when(solicitudRepository.guardar(any(Solicitud.class)))
                .thenReturn(Mono.just(solicitud));

        StepVerifier.create(useCase.crearSolicitud(solicitud))
                .expectNextMatches(s -> s.getEstado() == EstadoSolicitud.PENDIENTE_REVISION)
                .verifyComplete();

        verify(usuarioClientRepository).buscarPorDocumento("111111");
        verify(solicitudRepository).guardar(solicitud);
        verifyNoMoreInteractions(usuarioClientRepository, solicitudRepository);
    }

    @Test
    void crearSolicitud_deberiaFallar_siUsuarioNoExiste() {
        Solicitud solicitud = solicitudValida();

        when(usuarioClientRepository.buscarPorDocumento(solicitud.getDocumentoIdentidad()))
                .thenReturn(Mono.empty());

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
    void crearSolicitud_deberiaFallar_siFaltaDocumentoIdentidad() {
        Solicitud solicitud = solicitudValida().toBuilder()
                .documentoIdentidad(null)
                .build();

        when(usuarioClientRepository.buscarPorDocumento(any()))
                .thenReturn(Mono.just(solicitud)); // Mock necesario para evitar NPE

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

        when(usuarioClientRepository.buscarPorDocumento(anyString()))
                .thenReturn(Mono.just(solicitud));

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

        when(usuarioClientRepository.buscarPorDocumento(anyString()))
                .thenReturn(Mono.just(solicitud));

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

        when(usuarioClientRepository.buscarPorDocumento(anyString()))
                .thenReturn(Mono.just(solicitud));

        StepVerifier.create(useCase.crearSolicitud(solicitud))
                .expectError(ValidacionCampoException.class)
                .verify();

        verifyNoInteractions(solicitudRepository);
    }
}
