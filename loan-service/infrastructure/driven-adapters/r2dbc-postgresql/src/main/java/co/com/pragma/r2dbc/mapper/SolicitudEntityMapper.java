package co.com.pragma.r2dbc.mapper;

import co.com.pragma.model.solicitud.solicitudprestamos.Solicitud;
import co.com.pragma.r2dbc.entity.SolicitudEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SolicitudEntityMapper {

    @Mapping(source = "id", target = "id")
    SolicitudEntity toEntity(Solicitud solicitud);

    @Mapping(source = "id", target = "id")
    Solicitud toDomain(SolicitudEntity entity);
}
