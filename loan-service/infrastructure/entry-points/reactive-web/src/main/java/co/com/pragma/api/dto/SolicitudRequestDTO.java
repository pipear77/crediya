package co.com.pragma.api.dto;

import co.com.pragma.model.solicitud.enums.EstadoSolicitud;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Schema(name = "SolicitudRequestDTO", description = "Datos para registrar una solicitud de préstamo")
public record SolicitudRequestDTO(

        @NotBlank
        @Schema(example = "123456789", description = "Documento de identidad del solicitante")
        String documentoIdentidad,

        @NotNull
        @DecimalMin(value = "0.0", inclusive = false)
        @Schema(example = "5000000.00", description = "Monto solicitado")
        BigDecimal montoSolicitado,

        @NotNull
        @Min(1)
        @Schema(example = "24", description = "Plazo en meses")
        Integer plazoMeses,

        @NotBlank
        @Schema(example = "uuid-tipo-prestamo-001", description = "ID del tipo de préstamo")
        String idTipoPrestamo,

        @NotNull
        @Schema(example = "PENDIENTE", description = "Estado de la solicitud")
        EstadoSolicitud estado
) {}
