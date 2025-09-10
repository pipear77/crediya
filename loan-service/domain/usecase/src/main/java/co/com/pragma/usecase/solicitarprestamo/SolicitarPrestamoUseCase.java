package co.com.pragma.usecase.solicitarprestamo;

import co.com.pragma.model.solicitud.enums.EstadoSolicitud;
import co.com.pragma.model.solicitud.gateways.UsuarioClientRepository;
import co.com.pragma.model.solicitud.solicitudprestamos.Solicitud;
import co.com.pragma.model.solicitud.solicitudprestamos.gateways.SolicitudRepository;
import co.com.pragma.model.solicitud.tipoprestamos.TipoPrestamo;
import co.com.pragma.model.solicitud.tipoprestamos.gateways.TipoPrestamoRepository;
import co.com.pragma.usecase.common.SolicitudPrestamoValidationPipeline;
import co.com.pragma.usecase.common.validaciones.fields.Monto;
import co.com.pragma.usecase.common.validaciones.fields.NumeroDocumento;
import co.com.pragma.usecase.common.validaciones.fields.Plazo;
import co.com.pragma.usecase.common.validaciones.fields.Tipo;
import co.com.pragma.usecase.exceptions.TipoPrestamoNotFoundException;
import co.com.pragma.usecase.exceptions.ValidacionCampoException;
import co.com.pragma.usecase.exceptions.error.CodigosEstadoHttp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

import static co.com.pragma.usecase.common.constantes.Constantes.*;

@RequiredArgsConstructor
public class SolicitarPrestamoUseCase implements SolicitarPrestamoUseCaseInterface {

    private final SolicitudRepository solicitudRepository;
    private final UsuarioClientRepository usuarioClientRepository;
    private final TipoPrestamoRepository tipoPrestamoRepository;

    @Override
    public Mono<Solicitud> crearSolicitud(Solicitud solicitud, String token) {
        SolicitudPrestamoValidationPipeline pipeline = new SolicitudPrestamoValidationPipeline()
                .agregarValidacion(new Tipo())
                .agregarValidacion(new Plazo())
                .agregarValidacion(new Monto());

        return pipeline.validar(solicitud)
                .then(usuarioClientRepository.buscarPorDocumento(solicitud.getDocumentoIdentidad(), token)
                        .switchIfEmpty(Mono.error(new ValidacionCampoException(
                                USUARIO_NO_ENCONTRADO,
                                CodigosEstadoHttp.NOT_FOUND.getCode()
                        )))
                )
                .flatMap(usuario -> findTipoPrestamoById(solicitud.getIdTipoPrestamo())
                        .flatMap(tipo -> {
                            if (solicitud.getMontoSolicitado() == null ||
                                    tipo.getMontoMinimo() == null ||
                                    tipo.getMontoMaximo() == null) {
                                return Mono.error(new ValidacionCampoException(
                                        "No se puede validar el monto solicitado porque falta información financiera del tipo de préstamo",
                                        CodigosEstadoHttp.BAD_REQUEST.getCode()
                                ));
                            }

                            if (solicitud.getMontoSolicitado().compareTo(tipo.getMontoMinimo()) < 0 ||
                                    solicitud.getMontoSolicitado().compareTo(tipo.getMontoMaximo()) > 0) {
                                return Mono.error(new ValidacionCampoException(
                                        "El monto solicitado debe estar entre " + tipo.getMontoMinimo() + " y " + tipo.getMontoMaximo(),
                                        CodigosEstadoHttp.BAD_REQUEST.getCode()
                                ));
                            }

                            // Asignación interna de campos
                            solicitud.setId(UUID.randomUUID());
                            solicitud.setEstado(EstadoSolicitud.PENDIENTE_REVISION);
                            solicitud.setCorreo(usuario.getCorreo());
                            solicitud.setNombre(usuario.getNombres() + " " + usuario.getApellidos());
                            solicitud.setSalarioBase(usuario.getSalarioBase());
                            solicitud.setCanal("APP_WEB");
                            solicitud.setTipoPrestamo(tipo.getNombre());
                            solicitud.setTasaInteres(tipo.getTasaInteres());
                            solicitud.setMontoMensualSolicitud(
                                    solicitud.getMontoSolicitado().divide(BigDecimal.valueOf(solicitud.getPlazoMeses()), 2, BigDecimal.ROUND_HALF_UP)
                            );

                            return solicitudRepository.guardar(solicitud);
                        }));
    }

    private Mono<TipoPrestamo> findTipoPrestamoById(UUID tipoId) {
        return tipoPrestamoRepository.findById(tipoId)
                .switchIfEmpty(Mono.error(new TipoPrestamoNotFoundException(
                        TIPO_PRESTAMO_NO_ENCONTRADO,
                        CODIGO_NO_ENCONTRADO
                )));
    }

    private Mono<Void> verificarUsuario(String documentoIdentidad, String token) {
        return usuarioClientRepository.buscarPorDocumento(documentoIdentidad, token)
                .switchIfEmpty(Mono.error(new ValidacionCampoException(
                        USUARIO_NO_ENCONTRADO,
                        CodigosEstadoHttp.NOT_FOUND.getCode()
                )))
                .then();
    }
}
