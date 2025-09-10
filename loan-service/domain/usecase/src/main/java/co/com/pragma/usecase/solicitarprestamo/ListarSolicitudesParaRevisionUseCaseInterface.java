package co.com.pragma.usecase.solicitarprestamo;

import co.com.pragma.model.solicitud.solicitudprestamos.Solicitud;
import reactor.core.publisher.Flux;

public interface ListarSolicitudesParaRevisionUseCaseInterface {
    public Flux<Solicitud> ejecutar();
}
