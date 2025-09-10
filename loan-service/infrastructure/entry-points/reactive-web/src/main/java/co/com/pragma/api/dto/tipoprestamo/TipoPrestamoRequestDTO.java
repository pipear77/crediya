package co.com.pragma.api.dto.tipoprestamo;

import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(name = "TipoPrestamoRequestDTO", description = "Datos para crear o actualizar un tipo de préstamo")
public record TipoPrestamoRequestDTO(

        @Schema(example = "b3f1c2e4-9a2d-4d7e-8c3a-2a1f5e6d9f1a", description = "Identificador único del tipo de préstamo")
        String id,

        @NotBlank
        @Schema(example = "CONSUMO", description = "Nombre del tipo de préstamo")
        String nombre,

        @NotNull
        @DecimalMin(value = "0.0", inclusive = false)
        @Schema(example = "1000000.00", description = "Monto mínimo permitido")
        BigDecimal montoMinimo,

        @NotNull
        @DecimalMin(value = "0.0", inclusive = false)
        @Schema(example = "10000000.00", description = "Monto máximo permitido")
        BigDecimal montoMaximo,

        @NotNull
        @DecimalMin(value = "0.0", inclusive = false)
        @Schema(example = "0.015", description = "Ratio de interés aplicado")
        Double tasaInteres,

        @NotNull
        @Schema(example = "true", description = "Indica si requiere aprobación manual")
        Boolean aprobacionAutomatica
) {}
