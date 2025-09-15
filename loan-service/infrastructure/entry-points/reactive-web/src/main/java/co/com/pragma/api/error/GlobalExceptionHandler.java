package co.com.pragma.api.error;

import co.com.pragma.usecase.exceptions.ValidacionCampoException;
import co.com.pragma.usecase.exceptions.TipoPrestamoNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.resource.NoResourceFoundException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleValidation(ConstraintViolationException ex) {
        log.warn("Error de validación: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Validación fallida");
        problem.setDetail("Datos de entrada inválidos");
        problem.setProperty("violaciones",
                ex.getConstraintViolations().stream()
                        .map(v -> v.getPropertyPath() + " " + v.getMessage())
                        .toList()
        );
        return problem;
    }

    @ExceptionHandler(ValidacionCampoException.class)
    public ProblemDetail handleCampoInvalido(ValidacionCampoException ex) {
        log.warn("Campo inválido: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Error de campo");
        problem.setDetail(ex.getMessage());
        problem.setProperty("codigo", ex.getCode());
        return problem;
    }

    @ExceptionHandler(TipoPrestamoNotFoundException.class)
    public ProblemDetail handleTipoPrestamoNoEncontrado(TipoPrestamoNotFoundException ex) {
        log.warn("Tipo de préstamo no encontrado: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setTitle("Tipo de préstamo no disponible");
        problem.setDetail(ex.getMessage());
        problem.setProperty("codigo", ex.getCode());
        return problem;
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ProblemDetail handleStaticResourceNotFound(NoResourceFoundException ex) {
        log.warn("Recurso estático no encontrado: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setTitle("Recurso no encontrado");
        problem.setDetail("El recurso solicitado no existe.");
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneral(Exception ex) {
        // Evita capturar errores de recursos estáticos como 500
        if (ex instanceof NoResourceFoundException) {
            return null; // deja que WebFlux lo maneje como 404
        }
        log.error("Error inesperado: {}", ex.getMessage(), ex);
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problem.setTitle("Error inesperado");
        problem.setDetail(ex.getMessage());
        return problem;
    }
}
