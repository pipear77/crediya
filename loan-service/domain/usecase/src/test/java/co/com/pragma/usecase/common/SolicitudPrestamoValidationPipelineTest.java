package co.com.pragma.usecase.common;

import co.com.pragma.model.solicitud.solicitudprestamos.Solicitud;
import co.com.pragma.usecase.common.validaciones.SolicitarPrestamoValidacion;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


class SolicitudPrestamoValidationPipelineTest {
    @Test
    void validate_shouldComplete_whenAllValidationsPass() {
        SolicitarPrestamoValidacion solicitud1 = solicitud -> Mono.empty();
        SolicitarPrestamoValidacion solicitud2 = solicitud -> Mono.empty();

        SolicitudPrestamoValidationPipeline pipeline = new SolicitudPrestamoValidationPipeline()
                .agregarValidacion(solicitud1)
                .agregarValidacion(solicitud2);

        StepVerifier.create(pipeline.validate(new Solicitud()))
                .verifyComplete();
    }

}