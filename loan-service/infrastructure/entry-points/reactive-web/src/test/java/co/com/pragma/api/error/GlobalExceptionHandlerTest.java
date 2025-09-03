package co.com.pragma.api.error;

import co.com.pragma.usecase.exceptions.ValidacionCampoException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldHandleValidacionCampoException() {
        ValidacionCampoException ex = new ValidacionCampoException("Campo inválido", 400);
        ProblemDetail detail = handler.handleCampoInvalido(ex);

        assertThat(detail.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(detail.getTitle()).isEqualTo("Error de campo");
        assertThat(detail.getDetail()).isEqualTo("Campo inválido");
        assertThat(detail.getProperties()).containsEntry("codigo", 400);
    }

    @Test
    void shouldHandleGeneralException() {
        Exception ex = new RuntimeException("Error inesperado");
        ProblemDetail detail = handler.handleGeneral(ex);

        assertThat(detail.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(detail.getTitle()).isEqualTo("Error inesperado");
        assertThat(detail.getDetail()).isEqualTo("Error inesperado");
    }
}
