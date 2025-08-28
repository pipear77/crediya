package co.com.pragma.r2dbc.adapter;

import co.com.pragma.model.solicitud.Solicitud;
import co.com.pragma.model.solicitud.enums.TipoPrestamo;
import co.com.pragma.model.solicitud.gateways.SolicitudRepository;
import co.com.pragma.r2dbc.mapper.SolicitudMapper;
import co.com.pragma.r2dbc.service.ReactiveSolicitudDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class SolicitudRepositoryAdapter implements SolicitudRepository {

    private final ReactiveSolicitudDataRepository dataRepository;
    private final SolicitudMapper mapper;

    @Override
    public Mono<Solicitud> guardar(Solicitud solicitud) {
        return dataRepository.save(mapper.toEntity(solicitud))
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Solicitud> buscarPorDocumento(String documentoIdentidad) {
        return dataRepository.findByDocumentoIdentidad(documentoIdentidad)
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Solicitud> listarPorTipo(TipoPrestamo tipo) {
        return dataRepository.findByTipoPrestamo(tipo)
                .map(mapper::toDomain);
    }
}