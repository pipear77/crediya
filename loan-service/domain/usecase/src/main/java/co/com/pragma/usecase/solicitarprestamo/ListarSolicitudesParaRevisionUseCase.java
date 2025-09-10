package co.com.pragma.usecase.solicitarprestamo;

import co.com.pragma.model.solicitud.solicitudprestamos.Solicitud;
import co.com.pragma.model.solicitud.solicitudprestamos.gateways.SolicitudRepository;
import reactor.core.publisher.Flux;

public class ListarSolicitudesParaRevisionUseCase implements ListarSolicitudesParaRevisionUseCaseInterface{
    private final SolicitudRepository solicitudRepository;

    public ListarSolicitudesParaRevisionUseCase(SolicitudRepository solicitudRepository) {
        this.solicitudRepository = solicitudRepository;
    }

    @Override
    public Flux<Solicitud> ejecutar() {
        return solicitudRepository.listarSolicitudesParaRevision();
    }
}
