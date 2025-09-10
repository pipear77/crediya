package co.com.pragma.api.dto.solicitud;

import co.com.pragma.model.solicitud.enums.EstadoSolicitud;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;


@Schema(name = "SolicitudResponseDTO", description = "Datos completos de la solicitud registrada")
public record SolicitudResponseDTO(

        @Schema(example = "sol123", description = "Identificador único de la solicitud")
        String id,

        @Schema(example = "123456789", description = "Documento de identidad del solicitante")
        String documentoIdentidad,

        @Schema(example = "cristiano@gmail.com", description = "Correo electrónico del solicitante")
        String correo,

        @Schema(example = "Cristiano Ronaldo", description = "Nombre completo del solicitante")
        String nombre,

        @Schema(example = "APP_WEB", description = "Canal por el cual se realizó la solicitud")
        String canal,

        @Schema(example = "5000000.00", description = "Monto solicitado")
        BigDecimal montoSolicitado,

        @Schema(example = "24", description = "Plazo en meses")
        Integer plazoMeses,

        @Schema(example = "uuid-tipo-prestamo-001", description = "ID del tipo de préstamo")
        String idTipoPrestamo,

        @Schema(example = "Crédito educativo", description = "Tipo de trámite o préstamo")
        String tipoTramite,

        @Schema(example = "1000000.00", description = "Salario base del solicitante")
        BigDecimal salarioBase,

        @Schema(example = "250000.00", description = "Monto mensual estimado de la solicitud")
        BigDecimal montoMensualSolicitud,

        @Schema(example = "0.19", description = "Tasa de interés aplicada")
        Double tasaInteres,

        @Schema(example = "APROBADA", description = "Estado de la solicitud de préstamo")
        EstadoSolicitud estado
) {
}
