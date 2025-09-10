package co.com.pragma.r2dbc.adapter;

import co.com.pragma.model.solicitud.enums.EstadoSolicitud;
import co.com.pragma.model.solicitud.solicitudprestamos.Solicitud;
import co.com.pragma.model.solicitud.solicitudprestamos.gateways.SolicitudRepository;
import co.com.pragma.r2dbc.entity.SolicitudEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import co.com.pragma.r2dbc.mapper.SolicitudEntityMapper;
import co.com.pragma.r2dbc.repositories.ReactiveSolicitudRepository;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static co.com.pragma.r2dbc.common.Constantes.SOLICITUD_PRESTAMO_NOT_SAVED_ERROR_MSG;

@Slf4j
@Repository
public class SolicitudPrestamoRepositoryAdapter extends ReactiveAdapterOperations<
        Solicitud,
        SolicitudEntity,
        UUID,
        ReactiveSolicitudRepository
        > implements SolicitudRepository {

    private final TransactionalOperator txOperator;
    private final R2dbcEntityTemplate entityTemplate;
    private final SolicitudEntityMapper solicitudMapper;

    public SolicitudPrestamoRepositoryAdapter(ReactiveSolicitudRepository repository,
                                              ObjectMapper mapper,
                                              TransactionalOperator txOperator,
                                              R2dbcEntityTemplate entityTemplate,
                                              SolicitudEntityMapper solicitudMapper) {
        super(repository, mapper, solicitudMapper::toDomain);
        this.txOperator = txOperator;
        this.entityTemplate = entityTemplate;
        this.solicitudMapper = solicitudMapper;
    }

    /**
     * Persiste una nueva solicitud en la base de datos.
     *
     * @param solicitud modelo de dominio
     * @return solicitud persistida
     */
    @Override
    public Mono<Solicitud> guardar(Solicitud solicitud) {
        log.info("📥 Guardando solicitud con ID: {}", solicitud.getId());
        log.info("📩 Correo en modelo Solicitud: {}", solicitud.getCorreo());

        SolicitudEntity entity = solicitudMapper.toEntity(solicitud);
        logEntityTrace(entity);

        return Mono.defer(() ->
                txOperator.transactional(
                        entityTemplate.insert(SolicitudEntity.class)
                                .using(entity)
                                .map(solicitudMapper::toDomain)
                )
        ).doOnNext(saved -> {
            log.info("✅ Solicitud guardada con ID: {}", saved.getId());
            log.info("📩 Correo persistido en modelo: {}", saved.getCorreo());
        }).onErrorResume(e -> {
            log.error("❌ Error al guardar solicitud con ID: {}", solicitud.getId(), e);
            return Mono.error(new RuntimeException(SOLICITUD_PRESTAMO_NOT_SAVED_ERROR_MSG));
        });
    }

    /**
     * Lista todas las solicitudes que están en estados revisables.
     *
     * @return flujo de solicitudes en estado pendiente, rechazada o revisión manual
     */
    @Override
    public Flux<Solicitud> listarSolicitudesParaRevision() {
        List<EstadoSolicitud> estadosPermitidos = List.of(
                EstadoSolicitud.PENDIENTE_REVISION,
                EstadoSolicitud.RECHAZADA,
                EstadoSolicitud.REVISION_MANUAL
        );

        log.info("🔍 Listando solicitudes en estados permitidos: {}", estadosPermitidos);

        return repository.findByEstadoIn(estadosPermitidos)
                .map(solicitudMapper::toDomain)
                .doOnNext(s -> {
                    log.debug("➡️ Solicitud encontrada con ID: {} - Estado: {}", s.getId(), s.getEstado());
                    log.debug("📩 Correo en solicitud listada: {}", s.getCorreo());
                });
    }

    @Override
    public Mono<Solicitud> buscarPorId(UUID id) {
        log.info("🔍 Buscando solicitud por ID: {}", id);
        return repository.findById(id)
                .map(solicitudMapper::toDomain)
                .doOnNext(s -> log.debug("✅ Solicitud encontrada: {} - Estado: {}", s.getId(), s.getEstado()))
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("⚠️ Solicitud no encontrada con ID: {}", id);
                    return Mono.empty();
                }));
    }


    private void logEntityTrace(SolicitudEntity entity) {
        log.info("📦 Entidad mapeada para persistencia:");
        log.info("   ➤ ID: {}", entity.getId());
        log.info("   ➤ Documento: {}", entity.getDocumentoIdentidad());
        log.info("   ➤ Correo: {}", entity.getCorreo());
        log.info("   ➤ Nombre: {}", entity.getNombre());
        log.info("   ➤ Canal: {}", entity.getCanal());
        log.info("   ➤ Estado: {}", entity.getEstado());
    }
}
