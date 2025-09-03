package co.com.pragma.r2dbc.mapper;

import co.com.pragma.model.solicitud.solicitudprestamos.Solicitud;
import co.com.pragma.r2dbc.entity.SolicitudEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SolicitudEntityMapper {
    SolicitudEntity toEntity(Solicitud solicitud);
    Solicitud toDomain(SolicitudEntity entity);
}
