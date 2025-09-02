package co.com.pragma.usecase.exceptions;

public class TipoPrestamoInvalidoException extends NegocioException {

    public TipoPrestamoInvalidoException(String message, int code) {
        super(message, code);
    }
}
