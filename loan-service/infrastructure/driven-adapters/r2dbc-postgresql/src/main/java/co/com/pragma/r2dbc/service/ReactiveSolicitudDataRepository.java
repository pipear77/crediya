package co.com.pragma.r2dbc.service;

import co.com.pragma.r2dbc.entity.SolicitudEntity;
import co.com.pragma.model.solicitud.enums.TipoPrestamo;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


// TODO: This file is just an example, you should delete or modify it
public interface ReactiveSolicitudDataRepository extends ReactiveCrudRepository<SolicitudEntity, String>, ReactiveQueryByExampleExecutor<SolicitudEntity> {
    Mono<SolicitudEntity> findByDocumentoIdentidad(String documentoIdentidad);
    Flux<SolicitudEntity> findByTipoPrestamo(TipoPrestamo tipoPrestamo);
}
