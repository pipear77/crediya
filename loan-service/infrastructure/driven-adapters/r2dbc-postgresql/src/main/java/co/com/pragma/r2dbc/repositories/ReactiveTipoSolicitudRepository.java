package co.com.pragma.r2dbc.repositories;

import co.com.pragma.model.solicitud.tipoprestamos.TipoPrestamo;
import co.com.pragma.r2dbc.entity.SolicitudEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ReactiveTipoSolicitudRepository extends ReactiveCrudRepository<TipoPrestamo, String>, ReactiveQueryByExampleExecutor<SolicitudEntity> {
}

