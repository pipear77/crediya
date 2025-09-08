package co.com.pragma.r2dbc.adapter;

import co.com.pragma.model.solicitud.solicitudprestamos.Solicitud;
import co.com.pragma.model.solicitud.solicitudprestamos.gateways.SolicitudRepository;
import co.com.pragma.r2dbc.entity.SolicitudEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import co.com.pragma.r2dbc.repositories.ReactiveSolicitudRepository;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

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

    public SolicitudPrestamoRepositoryAdapter(ReactiveSolicitudRepository repository, ObjectMapper mapper, TransactionalOperator txOperator, R2dbcEntityTemplate entityTemplate) {
        super(repository, mapper, d -> mapper.map(d, Solicitud.class));
        this.txOperator = txOperator;
        this.entityTemplate = entityTemplate;
    }

    @Override
    public Mono<Solicitud> guardar(Solicitud solicitud) {
        log.info("Guardando solicitud: {}", solicitud);

        SolicitudEntity entity = mapper.map(solicitud, SolicitudEntity.class);

        return txOperator.transactional(
                        entityTemplate.insert(SolicitudEntity.class)
                                .using(entity)
                                .map(saved -> mapper.map(saved, Solicitud.class))
                ).doOnNext(saved -> log.info("Solicitud guardada: {}", saved))
                .onErrorResume(e -> {
                    log.error("Error al guardar solicitud: {}", solicitud, e);
                    return Mono.error(new RuntimeException(SOLICITUD_PRESTAMO_NOT_SAVED_ERROR_MSG));
                });
    }

}
