package co.com.pragma.usecase.exceptions;

public class NegocioException extends RuntimeException{
    private final int code;

    public NegocioException(String message, int code) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
