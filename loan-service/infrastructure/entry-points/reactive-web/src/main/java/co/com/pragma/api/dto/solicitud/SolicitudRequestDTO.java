package co.com.pragma.api.dto.solicitud;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Schema(name = "SolicitudRequestDTO", description = "Datos para registrar una solicitud de préstamo")
public record SolicitudRequestDTO(

        @NotBlank
        @Schema(example = "APP_WEB", description = "Canal por el cual se realizó la solicitud")
        String canal,

        @NotNull
        @DecimalMin(value = "0.0", inclusive = false)
        @Schema(example = "5000000.00", description = "Monto solicitado en pesos colombianos")
        BigDecimal montoSolicitado,

        @NotNull
        @Min(1)
        @Schema(example = "24", description = "Plazo del préstamo en meses")
        Integer plazoMeses,

        @NotBlank
        @Schema(example = "b3f1c2e4-9a2d-4d7e-8c3a-2a1f5e6d9f1a", description = "UUID del tipo de préstamo")
        String idTipoPrestamo,

        @NotNull
        @DecimalMin(value = "0.0", inclusive = true)
        @Schema(example = "3500000.00", description = "Salario base del solicitante")
        BigDecimal salarioBase,

        @NotNull
        @DecimalMin(value = "0.0", inclusive = true)
        @Schema(example = "1.5", description = "Tasa de interés aplicada al préstamo")
        Double tasaInteres
) {}
