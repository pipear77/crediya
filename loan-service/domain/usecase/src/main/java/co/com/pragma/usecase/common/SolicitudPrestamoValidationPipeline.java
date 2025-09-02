package co.com.pragma.usecase.common;

import co.com.pragma.model.solicitud.solicitudprestamos.Solicitud;
import co.com.pragma.usecase.common.validaciones.SolicitarPrestamoValidacion;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public class SolicitudPrestamoValidationPipeline {
    private final List<SolicitarPrestamoValidacion> validations = new ArrayList<>();

    public SolicitudPrestamoValidationPipeline agregarValidacion(SolicitarPrestamoValidacion validation) {
        validations.add(validation);

        return this;
    }

    public Mono<Void> validate(Solicitud solicitud) {
        Mono<Void> result = Mono.empty();

        for (SolicitarPrestamoValidacion validation : validations) {
            result = result.then(validation.validar(solicitud));
        }

        return result;
    }

}
