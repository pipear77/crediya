package co.com.pragma.usecase.exceptions.error;

public enum CodigosEstadoHttp {
    CONFLICT(409),
    BAD_REQUEST(400),
    NOT_FOUND(404),
    INTERNAL_SERVER_ERROR(500);


    private final int code;

    CodigosEstadoHttp(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
