package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.TipoPrestamoRequestDTO;
import co.com.pragma.api.dto.TipoPrestamoResponseDTO;
import co.com.pragma.model.solicitud.tipoprestamos.TipoPrestamo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TipoPrestamoMapper {

    TipoPrestamo toDomain(TipoPrestamoRequestDTO dto);

    TipoPrestamoResponseDTO toResponseDTO(TipoPrestamo domain);
}
