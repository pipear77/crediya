package co.com.pragma.r2dbc.exceptions;


import java.util.UUID;

import static co.com.pragma.r2dbc.common.Constantes.SOLICITUD_PRESTAMO_NOT_SAVED_ERROR_MSG;

public class SolicitudPrestamoNoGuardadoException extends RuntimeException {
    public SolicitudPrestamoNoGuardadoException() {
        super();
    }

    public SolicitudPrestamoNoGuardadoException(String mensaje) {
        super(mensaje);
    }

    public SolicitudPrestamoNoGuardadoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
