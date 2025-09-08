package co.com.pragma.r2dbc.repositories;

import co.com.pragma.model.solicitud.tipoprestamos.TipoPrestamo;
import co.com.pragma.r2dbc.entity.SolicitudEntity;
import co.com.pragma.r2dbc.entity.TipoPrestamoEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface ReactiveTipoPrestamoRepository extends ReactiveCrudRepository<TipoPrestamoEntity, UUID>, ReactiveQueryByExampleExecutor<TipoPrestamoEntity> {
}

