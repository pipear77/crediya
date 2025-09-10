package co.com.pragma.usecase.solicitarprestamo;

import co.com.pragma.model.solicitud.enums.EstadoSolicitud;
import co.com.pragma.model.solicitud.solicitudprestamos.Solicitud;
import co.com.pragma.model.solicitud.solicitudprestamos.gateways.SolicitudRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActualizarEstadoSolicitudUseCaseTest {

    @Mock
    SolicitudRepository solicitudRepository;

    @InjectMocks
    ActualizarEstadoSolicitudUseCase useCase;

    @Test
    void debeActualizarEstadoCorrectamente() {
        UUID id = UUID.randomUUID();
        Solicitud solicitud = new Solicitud();
        solicitud.setId(id);
        solicitud.setEstado(EstadoSolicitud.PENDIENTE_REVISION);

        when(solicitudRepository.buscarPorId(id)).thenReturn(Mono.just(solicitud));
        when(solicitudRepository.guardar(any(Solicitud.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        useCase.ejecutar(id, EstadoSolicitud.APROBADA)
                .as(StepVerifier::create)
                .expectNextMatches(s -> s.getEstado() == EstadoSolicitud.APROBADA)
                .verifyComplete();

        verify(solicitudRepository).buscarPorId(id);
        verify(solicitudRepository).guardar(any(Solicitud.class));
    }

    @Test
    void debeFallarSiSolicitudNoExiste() {
        UUID id = UUID.randomUUID();

        when(solicitudRepository.buscarPorId(id)).thenReturn(Mono.empty());

        useCase.ejecutar(id, EstadoSolicitud.APROBADA)
                .as(StepVerifier::create)
                .expectErrorMatches(e -> e.getMessage().equals("Solicitud no encontrada"))
                .verify();

        verify(solicitudRepository).buscarPorId(id);
        verify(solicitudRepository, never()).guardar(any());
    }
}
