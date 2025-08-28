package co.com.pragma.usecase.exceptions;

public class CampoObligatorioException extends RuntimeException {
    private final String campo;

    public CampoObligatorioException(String campo) {
        super("El campo '" + campo + "' es obligatorio");
        this.campo = campo;
    }

    public String getCampo() {
        return campo;
    }
}
