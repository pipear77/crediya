package co.com.pragma.r2dbc.repositories;

import co.com.pragma.model.solicitud.enums.EstadoSolicitud;
import co.com.pragma.r2dbc.entity.SolicitudEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio reactivo para operaciones sobre la entidad SolicitudEntity.
 */
public interface ReactiveSolicitudRepository extends ReactiveCrudRepository<SolicitudEntity, UUID>,
        ReactiveQueryByExampleExecutor<SolicitudEntity> {

    /**
     * Consulta todas las solicitudes cuyo estado esté dentro del conjunto permitido.
     *
     * @param estados lista de estados válidos para revisión
     * @return flujo reactivo de entidades encontradas
     */
    Flux<SolicitudEntity> findByEstadoIn(List<EstadoSolicitud> estados);

    /**
     * Busca una solicitud por su ID.
     *
     * @param id identificador único
     * @return Mono con la entidad encontrada o vacío
     */
    Mono<SolicitudEntity> findById(UUID id); // ya heredado de ReactiveCrudRepository

}
