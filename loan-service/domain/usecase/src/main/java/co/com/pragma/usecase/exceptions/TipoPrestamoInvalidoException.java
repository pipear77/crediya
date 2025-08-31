package co.com.pragma.usecase.exceptions;

public class TipoPrestamoInvalidoException extends RuntimeException {

    public TipoPrestamoInvalidoException() {
        super("El tipo de préstamo seleccionado no es válido.");
    }
}
