package co.com.pragma.usecase.common.validaciones.fields;

import co.com.pragma.model.solicitud.solicitudprestamos.Solicitud;
import co.com.pragma.usecase.exceptions.ValidacionCampoException;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.UUID;

class TipoTest {
    private final Tipo validator = new Tipo();

    @Test
    void validate_shouldComplete_whenDocumentNumberPresent() {
        Solicitud solicitud = new Solicitud();
        solicitud.setIdTipoPrestamo(UUID.fromString("b3f1c2e4-9a2d-4d7e-8c3a-2a1f5e6d9f1a"));
        StepVerifier.create(validator.validar(solicitud)).verifyComplete();
    }

    @Test
    void validate_shouldError_whenDocumentNumberNull() {
        Solicitud solicitud = new Solicitud();
        solicitud.setIdTipoPrestamo(null);
        StepVerifier.create(validator.validar(solicitud))
                .expectError(ValidacionCampoException.class)
                .verify();
    }

}