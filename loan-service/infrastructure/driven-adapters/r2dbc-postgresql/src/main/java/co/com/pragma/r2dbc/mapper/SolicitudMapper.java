package co.com.pragma.r2dbc.mapper;

import co.com.pragma.model.solicitud.Solicitud;
import co.com.pragma.r2dbc.entity.SolicitudEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SolicitudMapper {
    SolicitudEntity toEntity(Solicitud solicitud);
    Solicitud toDomain(SolicitudEntity entity);
}
