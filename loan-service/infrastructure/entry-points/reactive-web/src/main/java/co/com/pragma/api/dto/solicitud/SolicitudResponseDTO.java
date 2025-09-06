package co.com.pragma.api.dto.solicitud;

import co.com.pragma.model.solicitud.enums.EstadoSolicitud;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(name = "SolicitudResponseDTO", description = "Datos de la solicitud registrada")
public record SolicitudResponseDTO(

        @Schema(example = "sol123", description = "Identificador único de la solicitud")
        String id,

        @Schema(example = "123456789", description = "Documento de identidad del solicitante")
        String documentoIdentidad,

        @Schema(example = "5000000.00", description = "Monto solicitado")
        BigDecimal montoSolicitado,

        @Schema(example = "24", description = "Plazo en meses")
        Integer plazoMeses,

        @Schema(example = "uuid-tipo-prestamo-001", description = "ID del tipo de préstamo")
        String idTipoPrestamo,

        @Schema(example = "PENDIENTE", description = "Estado de la solicitud")
        EstadoSolicitud estado
) {}
