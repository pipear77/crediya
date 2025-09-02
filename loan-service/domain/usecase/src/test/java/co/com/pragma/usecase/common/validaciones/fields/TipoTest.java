package co.com.pragma.usecase.common.validaciones.fields;

import co.com.pragma.model.solicitud.enums.TipoPrestamo;
import co.com.pragma.model.solicitud.solicitudprestamos.Solicitud;
import co.com.pragma.usecase.exceptions.ValidacionCampoException;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class TipoTest {
    private final Tipo validator = new Tipo();

    @Test
    void validate_shouldComplete_whenDocumentNumberPresent() {
        Solicitud solicitud = new Solicitud();
        solicitud.setTipoPrestamo(TipoPrestamo.VEHICULO);
        StepVerifier.create(validator.validar(solicitud)).verifyComplete();
    }

    @Test
    void validate_shouldError_whenDocumentNumberNull() {
        Solicitud solicitud = new Solicitud();
        solicitud.setTipoPrestamo(null);
        StepVerifier.create(validator.validar(solicitud))
                .expectError(ValidacionCampoException.class)
                .verify();
    }
}