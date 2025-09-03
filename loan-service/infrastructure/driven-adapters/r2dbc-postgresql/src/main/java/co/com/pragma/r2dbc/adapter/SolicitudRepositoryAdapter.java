package co.com.pragma.r2dbc.adapter;

import co.com.pragma.model.solicitud.solicitudprestamos.Solicitud;
import co.com.pragma.model.solicitud.solicitudprestamos.gateways.SolicitudRepository;
import co.com.pragma.r2dbc.mapper.SolicitudMapper;
import co.com.pragma.r2dbc.repositories.ReactiveSolicitudRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class SolicitudRepositoryAdapter implements SolicitudRepository {

    private final ReactiveSolicitudRepository dataRepository;
    private final SolicitudMapper mapper;

    @Override
    public Mono<Solicitud> guardar(Solicitud solicitud) {
        return dataRepository.save(mapper.toEntity(solicitud))
                .map(mapper::toDomain);
    }
}