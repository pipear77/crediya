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

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "idTipoPrestamo", target = "idTipoPrestamo", qualifiedByName = "stringToUUID")
    @Mapping(target = "tipoPrestamo", ignore = true) // se asigna en el UseCase
    Solicitud toDomain(SolicitudRequestDTO dto);

    @Mapping(source = "id", target = "id", qualifiedByName = "uuidToString")
    @Mapping(source = "documentoIdentidad", target = "documentoIdentidad")
    @Mapping(source = "correo", target = "correo")
    @Mapping(source = "nombre", target = "nombre")
    @Mapping(source = "canal", target = "canal")
    @Mapping(source = "plazoMeses", target = "plazoMeses")
    @Mapping(source = "idTipoPrestamo", target = "idTipoPrestamo", qualifiedByName = "uuidToString")
    @Mapping(source = "salarioBase", target = "salarioBase")
    @Mapping(source = "montoSolicitado", target = "montoSolicitado")
    @Mapping(source = "montoMensualSolicitud", target = "montoMensualSolicitud")
    @Mapping(source = "tasaInteres", target = "tasaInteres")
    @Mapping(source = "estado", target = "estado")
    @Mapping(source = "tipoPrestamo", target = "tipoTramite")
    SolicitudResponseDTO toResponseDTO(Solicitud solicitud);

    @Named("uuidToString")
    default String uuidToString(UUID id) {
        return id != null ? id.toString() : null;
    }

    @Named("stringToUUID")
    default UUID stringToUUID(String id) {
        return id != null && !id.isBlank() ? UUID.fromString(id) : null;
    }
}
