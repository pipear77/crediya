package co.com.pragma.r2dbc.adapter;

import co.com.pragma.model.solicitud.tipoprestamos.TipoPrestamo;
import co.com.pragma.model.solicitud.tipoprestamos.gateways.TipoPrestamoRepository;
import co.com.pragma.r2dbc.entity.TipoPrestamoEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import co.com.pragma.r2dbc.repositories.ReactiveTipoPrestamoRepository;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Repository
public class TipoPrestamoRepositoryAdapter extends ReactiveAdapterOperations<
        TipoPrestamo,
        TipoPrestamoEntity,
        UUID,
        ReactiveTipoPrestamoRepository
        > implements TipoPrestamoRepository {

    public TipoPrestamoRepositoryAdapter(ReactiveTipoPrestamoRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, TipoPrestamo.class));
    }

    @Override
    public Mono<TipoPrestamo> findById(UUID id) {
        log.info("🔍 Buscando tipo de préstamo con ID: {}", id);

        return super.findById(id)
                .doOnNext(tipo -> log.info("✅ Tipo de préstamo encontrado: {}", tipo))
                .onErrorResume(e -> {
                    log.error("❌ Error al consultar tipo de préstamo con ID: {}", id, e);
                    return Mono.error(new RuntimeException("Error al consultar tipo de préstamo con ID: " + id));
                });
    }
}
