package co.com.pragma.usecase.solicitarprestamo;

import co.com.pragma.model.solicitud.enums.EstadoSolicitud;
import co.com.pragma.model.solicitud.solicitudprestamos.Solicitud;
import co.com.pragma.model.solicitud.solicitudprestamos.gateways.SolicitudRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarSolicitudesParaRevisionUseCaseTest {

    @Mock
    SolicitudRepository solicitudRepository;

    @InjectMocks
    ListarSolicitudesParaRevisionUseCase useCase;

    @Test
    void debeListarSoloSolicitudesRevisables() {
        Solicitud s1 = new Solicitud(); s1.setEstado(EstadoSolicitud.PENDIENTE_REVISION);
        Solicitud s2 = new Solicitud(); s2.setEstado(EstadoSolicitud.RECHAZADA);
        Solicitud s3 = new Solicitud(); s3.setEstado(EstadoSolicitud.REVISION_MANUAL);
        Solicitud s4 = new Solicitud(); s4.setEstado(EstadoSolicitud.APROBADA); // no debe pasar

        when(solicitudRepository.listarSolicitudesParaRevision())
                .thenReturn(Flux.just(s1, s2, s3, s4));

        useCase.ejecutar()
                .as(StepVerifier::create)
                .expectNext(s1)
                .expectNext(s2)
                .expectNext(s3)
                .verifyComplete();

        verify(solicitudRepository).listarSolicitudesParaRevision();
    }
}