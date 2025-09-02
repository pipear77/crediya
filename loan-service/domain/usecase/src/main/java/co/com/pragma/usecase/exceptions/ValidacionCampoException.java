package co.com.pragma.usecase.exceptions;

public class ValidacionCampoException extends NegocioException{
    public ValidacionCampoException(String message, int code) {
        super(message, code);

    }

}
