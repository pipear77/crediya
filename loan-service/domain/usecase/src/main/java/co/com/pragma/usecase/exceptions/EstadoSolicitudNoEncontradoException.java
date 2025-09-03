package co.com.pragma.usecase.exceptions;

public class EstadoSolicitudNoEncontradoException extends NegocioException {
    public EstadoSolicitudNoEncontradoException(String message, int code) {
        super(message, code);
    }
}
