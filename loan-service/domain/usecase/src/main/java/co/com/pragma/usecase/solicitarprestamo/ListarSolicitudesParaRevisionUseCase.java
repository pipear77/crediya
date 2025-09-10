package co.com.pragma.usecase.solicitarprestamo;

import co.com.pragma.model.solicitud.enums.EstadoSolicitud;
import co.com.pragma.model.solicitud.solicitudprestamos.Solicitud;
import co.com.pragma.model.solicitud.solicitudprestamos.gateways.SolicitudRepository;

import java.util.List;
import reactor.core.publisher.Flux;

public class ListarSolicitudesParaRevisionUseCase implements ListarSolicitudesParaRevisionUseCaseInterface {

    private final SolicitudRepository solicitudRepository;

    public ListarSolicitudesParaRevisionUseCase(SolicitudRepository solicitudRepository) {
        this.solicitudRepository = solicitudRepository;
    }

    @Override
    public Flux<Solicitud> ejecutar() {
        List<EstadoSolicitud> estadosRevisables = List.of(
                EstadoSolicitud.PENDIENTE_REVISION,
                EstadoSolicitud.RECHAZADA,
                EstadoSolicitud.REVISION_MANUAL
        );

        return solicitudRepository.listarSolicitudesParaRevision()
                .filter(solicitud -> estadosRevisables.contains(solicitud.getEstado()));
    }
}
