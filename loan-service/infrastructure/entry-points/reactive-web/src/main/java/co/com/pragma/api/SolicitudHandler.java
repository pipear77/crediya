package co.com.pragma.api;

import co.com.pragma.api.dto.SolicitudRequestDTO;
import co.com.pragma.api.dto.SolicitudResponseDTO;
import co.com.pragma.model.solicitud.solicitudprestamos.Solicitud;
import co.com.pragma.model.solicitud.enums.TipoPrestamo;
import co.com.pragma.usecase.solicitarprestamo.SolicitarPrestamoUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class SolicitudHandler {

    private final SolicitarPrestamoUseCase useCase;

    public Mono<ServerResponse> save(ServerRequest request) {
        return request.bodyToMono(SolicitudRequestDTO.class)
                .doOnSubscribe(sub -> log.info("Iniciando registro de solicitud"))
                .map(this::toDomain)
                .flatMap(useCase::ejecutar)
                .map(this::toResponseDTO)
                .flatMap(dto -> ServerResponse.status(201).bodyValue(dto))
                .onErrorResume(e -> {
                    log.error("Error al registrar solicitud: {}", e.getMessage(), e);
                    return ServerResponse.badRequest().bodyValue("Error: " + e.getMessage());
                });
    }

    public Mono<ServerResponse> getByDocumento(ServerRequest request) {
        String documento = request.pathVariable("documento");
        return useCase.buscarPorDocumento(documento)
                .map(this::toResponseDTO)
                .flatMap(dto -> ServerResponse.ok().bodyValue(dto))
                .switchIfEmpty(ServerResponse.notFound().build())
                .doOnError(e -> log.error("Error al buscar solicitud por documento: {}", e.getMessage(), e));
    }

    public Mono<ServerResponse> getByTipo(ServerRequest request) {
        String tipoParam = request.queryParam("tipo").orElse("");
        try {
            TipoPrestamo tipo = TipoPrestamo.valueOf(tipoParam.toUpperCase());
            return useCase.listarPorTipo(tipo)
                    .map(this::toResponseDTO)
                    .collectList()
                    .flatMap(lista -> ServerResponse.ok().bodyValue(lista));
        } catch (IllegalArgumentException ex) {
            log.warn("Tipo de préstamo inválido: {}", tipoParam);
            return ServerResponse.badRequest().bodyValue("Tipo de préstamo inválido: " + tipoParam);
        }
    }

    private Solicitud toDomain(SolicitudRequestDTO dto) {
        return Solicitud.builder()
                .documentoIdentidad(dto.getDocumentoIdentidad())
                .montoSolicitado(dto.getMontoSolicitado())
                .plazoMeses(dto.getPlazoMeses())
                .tipoPrestamo(dto.getTipoPrestamo())
                .estado(dto.getEstado()) // opcional, se sobreescribe en el use case
                .build();
    }

    private SolicitudResponseDTO toResponseDTO(Solicitud domain) {
        return SolicitudResponseDTO.builder()
                .id(domain.getId())
                .documentoIdentidad(domain.getDocumentoIdentidad())
                .montoSolicitado(domain.getMontoSolicitado())
                .plazoMeses(domain.getPlazoMeses())
                .tipoPrestamo(domain.getTipoPrestamo())
                .estado(domain.getEstado())
                .build();
    }
}