package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.SolicitudRequestDTO;
import co.com.pragma.api.dto.SolicitudResponseDTO;
import co.com.pragma.model.solicitud.solicitudprestamos.Solicitud;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SolicitudApiMapper {

    @Mapping(target = "idTipoPrestamo", source = "idTipoPrestamo")
    Solicitud toDomain(SolicitudRequestDTO dto);

    @Mapping(target = "idTipoPrestamo", source = "idTipoPrestamo")
    SolicitudResponseDTO toResponseDTO(Solicitud domain);
}
