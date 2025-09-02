package co.com.pragma.usecase.common.validaciones.fields;

import co.com.pragma.model.solicitud.solicitudprestamos.Solicitud;
import co.com.pragma.usecase.exceptions.ValidacionCampoException;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

class CantidadTest {
    private final Cantidad validator = new Cantidad();

    @Test
    void validate_shouldComplete_whenAmountPresent() {
        Solicitud solicitud = new Solicitud();
        solicitud.setMontoSolicitado(new BigDecimal("4000000"));
        StepVerifier.create(validator.validar(solicitud)).verifyComplete();
    }

    @Test
    void validate_shouldError_whenAmountNull() {
        Solicitud solicitud = new Solicitud();
        solicitud.setMontoSolicitado(null);
        StepVerifier.create(validator.validar(solicitud))
                .expectError(ValidacionCampoException.class)
                .verify();
    }

}