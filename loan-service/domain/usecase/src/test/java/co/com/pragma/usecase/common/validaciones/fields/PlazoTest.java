package co.com.pragma.usecase.common.validaciones.fields;

import co.com.pragma.model.solicitud.solicitudprestamos.Solicitud;
import co.com.pragma.usecase.exceptions.ValidacionCampoException;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class PlazoTest {
    private final Plazo validator = new Plazo();

    @Test
    void validate_shouldComplete_whenDocumentNumberPresent() {
        Solicitud solicitud = new Solicitud();
        solicitud.setPlazoMeses(1111111);
        StepVerifier.create(validator.validar(solicitud)).verifyComplete();
    }

    @Test
    void validate_shouldError_whenDocumentNumberNull() {
        Solicitud solicitud = new Solicitud();
        solicitud.setDocumentoIdentidad(null);
        StepVerifier.create(validator.validar(solicitud))
                .expectError(ValidacionCampoException.class)
                .verify();
    }
}