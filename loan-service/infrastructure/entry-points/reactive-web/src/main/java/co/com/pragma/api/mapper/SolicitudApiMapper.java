package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.solicitud.SolicitudRequestDTO;
import co.com.pragma.api.dto.solicitud.SolicitudResponseDTO;
import co.com.pragma.model.solicitud.solicitudprestamos.Solicitud;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface SolicitudApiMapper {

    @Mapping(target = "id", ignore = true) // se genera en el use case
    @Mapping(target = "documentoIdentidad", source = "documentoIdentidad")
    @Mapping(target = "montoSolicitado", source = "dto.montoSolicitado")
    @Mapping(target = "plazoMeses", source = "dto.plazoMeses")
    @Mapping(target = "idTipoPrestamo", source = "dto.idTipoPrestamo")
    @Mapping(target = "estado", source = "dto.estado")
    Solicitud toDomain(SolicitudRequestDTO dto, String documentoIdentidad);

    @Mapping(source = "id", target = "id", qualifiedByName = "uuidToString")
    @Mapping(target = "documentoIdentidad", source = "documentoIdentidad")
    @Mapping(target = "montoSolicitado", source = "montoSolicitado")
    @Mapping(target = "plazoMeses", source = "plazoMeses")
    @Mapping(target = "idTipoPrestamo", source = "idTipoPrestamo")
    @Mapping(target = "estado", source = "estado")
    SolicitudResponseDTO toResponseDTO(Solicitud domain);

    @Named("uuidToString")
    default String uuidToString(UUID id) {
        return id != null ? id.toString() : null;
    }
}
