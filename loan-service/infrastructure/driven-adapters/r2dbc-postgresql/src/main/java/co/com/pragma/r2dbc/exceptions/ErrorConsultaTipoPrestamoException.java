package co.com.pragma.r2dbc.exceptions;


import static co.com.pragma.r2dbc.common.Constantes.SOLICITUD_PRESTAMO_NOT_SAVED_ERROR_MSG;

public class ErrorConsultaTipoPrestamoException extends RuntimeException {
        public ErrorConsultaTipoPrestamoException() {
            super(SOLICITUD_PRESTAMO_NOT_SAVED_ERROR_MSG);
        }

        public ErrorConsultaTipoPrestamoException(String mensaje) {
            super(mensaje);
        }
    }
