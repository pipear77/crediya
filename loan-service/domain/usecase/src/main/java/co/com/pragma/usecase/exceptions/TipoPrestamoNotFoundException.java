package co.com.pragma.usecase.exceptions;

public class TipoPrestamoNotFoundException extends NegocioException {

    public TipoPrestamoNotFoundException(String message, int code) {
        super(message, code);
    }
}
