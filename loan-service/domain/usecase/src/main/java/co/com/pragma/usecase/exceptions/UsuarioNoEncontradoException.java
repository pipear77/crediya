package co.com.pragma.usecase.exceptions;

public class UsuarioNoEncontradoException extends NegocioException {
    public UsuarioNoEncontradoException(String message, int code) {
        super(message, code);
    }
}
