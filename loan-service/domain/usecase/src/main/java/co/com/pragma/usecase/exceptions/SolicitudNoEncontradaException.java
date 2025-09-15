package co.com.pragma.usecase.exceptions;

import static co.com.pragma.usecase.common.constantes.Constantes.SOLICITUD_NO_ENCONTRADA;

public class SolicitudNoEncontradaException extends RuntimeException {
        public SolicitudNoEncontradaException() {
            super(SOLICITUD_NO_ENCONTRADA);
        }

        public SolicitudNoEncontradaException(String mensaje) {
            super(mensaje);
        }
    }
