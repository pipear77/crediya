package co.com.pragma.usecase.solicitarprestamo;

import co.com.pragma.model.solicitud.enums.EstadoSolicitud;
import co.com.pragma.model.solicitud.solicitudprestamos.Solicitud;
import co.com.pragma.model.solicitud.solicitudprestamos.gateways.SolicitudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public class ActualizarEstadoSolicitudUseCase implements ActualizarEstadoSolicitudUseCaseInterface{
    private final SolicitudRepository solicitudRepository;

    public ActualizarEstadoSolicitudUseCase(SolicitudRepository solicitudRepository) {
        this.solicitudRepository = solicitudRepository;
    }
    @Override
    public Mono<Solicitud> ejecutar(UUID id, EstadoSolicitud nuevoEstado) {
        return solicitudRepository.buscarPorId(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Solicitud no encontrada")))
                .flatMap(solicitud -> {
                    solicitud.setEstado(nuevoEstado);
                    return solicitudRepository.guardar(solicitud);
                });
    }
}
