package co.com.pragma.usecase.common.validaciones.fields;

import co.com.pragma.model.solicitud.solicitudprestamos.Solicitud;
import co.com.pragma.usecase.exceptions.ValidacionCampoException;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class NumeroDocumentoTest {
    private final NumeroDocumento validator = new NumeroDocumento();

    @Test
    void validate_shouldComplete_whenDocumentNumberPresent() {
        Solicitud solicitud = new Solicitud();
        solicitud.setDocumentoIdentidad(String.valueOf(1111111L));
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