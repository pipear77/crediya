package co.com.pragma.usecase.common.validaciones.fields;

import co.com.pragma.model.solicitud.solicitudprestamos.Solicitud;
import co.com.pragma.usecase.common.validaciones.SolicitarPrestamoValidacion;
import co.com.pragma.usecase.exceptions.ValidacionCampoException;
import co.com.pragma.usecase.exceptions.error.CodigosEstadoHttp;
import reactor.core.publisher.Mono;

import static co.com.pragma.usecase.common.constantes.Constantes.ERROR_MONTO_REQUERIDO;

public class Monto implements SolicitarPrestamoValidacion {
    @Override
    public Mono<Void> validar(Solicitud solicitud) {
        if (solicitud.getMontoSolicitado() == null)
            return Mono.error(new ValidacionCampoException(ERROR_MONTO_REQUERIDO, CodigosEstadoHttp.BAD_REQUEST.getCode()));

        return Mono.empty();
    }

}
