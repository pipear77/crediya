package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.tipoprestamo.TipoPrestamoRequestDTO;
import co.com.pragma.api.dto.tipoprestamo.TipoPrestamoResponseDTO;
import co.com.pragma.model.solicitud.tipoprestamos.TipoPrestamo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface TipoPrestamoMapper {

    @Mapping(source = "id", target = "id", qualifiedByName = "stringToUuid")
    TipoPrestamo toDomain(TipoPrestamoRequestDTO dto);

    @Mapping(source = "id", target = "id", qualifiedByName = "uuidToString")
    TipoPrestamoResponseDTO toResponseDTO(TipoPrestamo domain);

    @Named("stringToUuid")
    default UUID stringToUuid(String id) {
        return id != null && !id.isBlank() ? UUID.fromString(id) : null;
    }

    @Named("uuidToString")
    default String uuidToString(UUID id) {
        return id != null ? id.toString() : null;
    }
}
