package co.com.pragma.api.dto.solicitud;

import co.com.pragma.model.solicitud.enums.EstadoSolicitud;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(name = "SolicitudResponseDTO", description = "Datos completos de la solicitud registrada")
public record SolicitudResponseDTO(

        @Schema(example = "a1a1a1a1-a1a1-a1a1-a1a1-a1a1a1a1a101", description = "Identificador único de la solicitud")
        String id,

        @Schema(example = "123456789", description = "Documento de identidad del solicitante")
        String documentoIdentidad,

        @Schema(example = "arcila@example.com", description = "Correo electrónico del solicitante")
        String correo,

        @Schema(example = "Ana Arcila", description = "Nombre completo del solicitante")
        String nombre,

        @Schema(example = "APP_WEB", description = "Canal por el cual se realizó la solicitud")
        String canal,

        @Schema(example = "5000000.00", description = "Monto solicitado en pesos colombianos")
        BigDecimal montoSolicitado,

        @Schema(example = "24", description = "Plazo del préstamo en meses")
        Integer plazoMeses,

        @Schema(example = "b3f1c2e4-9a2d-4d7e-8c3a-2a1f5e6d9f1a", description = "UUID del tipo de préstamo")
        String idTipoPrestamo,

        @Schema(example = "Crédito Personal", description = "Nombre legible del tipo de préstamo")
        String tipoTramite,

        @Schema(example = "3500000.00", description = "Salario base del solicitante")
        BigDecimal salarioBase,

        @Schema(example = "208333.33", description = "Monto mensual estimado de la solicitud")
        BigDecimal montoMensualSolicitud,

        @Schema(example = "1.5", description = "Tasa de interés aplicada al préstamo")
        Double tasaInteres,

        @Schema(example = "PENDIENTE_REVISION", description = "Estado actual de la solicitud")
        EstadoSolicitud estado
) {}
