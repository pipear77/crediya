package co.com.pragma.usecase.solicitarprestamo;

import co.com.pragma.model.solicitud.solicitudprestamos.Solicitud;
import co.com.pragma.model.solicitud.enums.EstadoSolicitud;
import co.com.pragma.model.solicitud.enums.TipoPrestamo;
import co.com.pragma.model.solicitud.solicitudprestamos.gateways.SolicitudRepository;
import co.com.pragma.usecase.exceptions.TipoPrestamoInvalidoException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.EnumSet;

@RequiredArgsConstructor
public class SolicitarPrestamoUseCase {

    private final SolicitudRepository solicitudRepository;

    /**
     * Registra una nueva solicitud de préstamo con estado inicial "PENDIENTE_REVISION"
     * y valida los campos obligatorios según las reglas de negocio actuales.
     */
    public Mono<Solicitud> ejecutar(Solicitud solicitud) {
        return validarSolicitud(solicitud)
                .then(Mono.defer(() -> {
                    solicitud.setEstado(EstadoSolicitud.PENDIENTE_REVISION);
                    return solicitudRepository.guardar(solicitud);
                }));
    }

    /**
     * Valida los campos obligatorios de la solicitud.
     * Solo se valida lo que está explícitamente definido en la historia de usuario.
     */
    private Mono<Void> validarSolicitud(Solicitud solicitud) {
        if (isNullOrEmpty(solicitud.getDocumentoIdentidad())) {
            return Mono.error(new CampoObligatorioException("documentoIdentidad"));
        }
        if (solicitud.getMontoSolicitado() == null) {
            return Mono.error(new CampoObligatorioException("montoSolicitado"));
        }
        if (solicitud.getPlazoMeses() == null) {
            return Mono.error(new CampoObligatorioException("plazoMeses"));
        }
        if (solicitud.getTipoPrestamo() == null) {
            return Mono.error(new CampoObligatorioException("tipoPrestamo"));
        }
        if (!EnumSet.allOf(TipoPrestamo.class).contains(solicitud.getTipoPrestamo())) {
            return Mono.error(new TipoPrestamoInvalidoException());
        }

        return Mono.empty();
    }

    private boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * Busca una solicitud por documento de identidad.
     */
    public Mono<Solicitud> buscarPorDocumento(String documentoIdentidad) {
        return solicitudRepository.buscarPorDocumento(documentoIdentidad);
    }

    /**
     * Lista todas las solicitudes filtradas por tipo de préstamo.
     */
    public Flux<Solicitud> listarPorTipo(TipoPrestamo tipo) {
        return solicitudRepository.listarPorTipo(tipo);
    }
}