package co.com.pragma.r2dbc.mapper;

import co.com.pragma.model.solicitud.solicitudprestamos.Solicitud;
import co.com.pragma.r2dbc.entity.SolicitudEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface SolicitudEntityMapper {

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "correo", target = "correo"),
            @Mapping(source = "documentoIdentidad", target = "documentoIdentidad"),
            @Mapping(source = "nombre", target = "nombre"),
            @Mapping(source = "canal", target = "canal"),
            @Mapping(source = "montoSolicitado", target = "montoSolicitado"),
            @Mapping(source = "plazoMeses", target = "plazoMeses"),
            @Mapping(source = "idTipoPrestamo", target = "idTipoPrestamo"),
            @Mapping(source = "salarioBase", target = "salarioBase"),
            @Mapping(source = "montoMensualSolicitud", target = "montoMensualSolicitud"),
            @Mapping(source = "tasaInteres", target = "tasaInteres"),
            @Mapping(source = "estado", target = "estado")
    })
    SolicitudEntity toEntity(Solicitud solicitud);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "correo", target = "correo"),
            @Mapping(source = "documentoIdentidad", target = "documentoIdentidad"),
            @Mapping(source = "nombre", target = "nombre"),
            @Mapping(source = "canal", target = "canal"),
            @Mapping(source = "montoSolicitado", target = "montoSolicitado"),
            @Mapping(source = "plazoMeses", target = "plazoMeses"),
            @Mapping(source = "idTipoPrestamo", target = "idTipoPrestamo"),
            @Mapping(source = "salarioBase", target = "salarioBase"),
            @Mapping(source = "montoMensualSolicitud", target = "montoMensualSolicitud"),
            @Mapping(source = "tasaInteres", target = "tasaInteres"),
            @Mapping(source = "estado", target = "estado")
    })
    Solicitud toDomain(SolicitudEntity entity);
}
