package co.com.pragma.api.dto;

import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(name = "TipoPrestamoRequestDTO", description = "Datos para crear o actualizar un tipo de préstamo")
public record TipoPrestamoRequestDTO(

        @NotBlank
        @Schema(example = "CONSUMO", description = "Nombre del tipo de préstamo")
        String nombre,

        @NotNull
        @DecimalMin(value = "0.0", inclusive = false)
        @Schema(example = "1000000.00", description = "Monto mínimo permitido")
        BigDecimal minMonto,

        @NotNull
        @DecimalMin(value = "0.0", inclusive = false)
        @Schema(example = "10000000.00", description = "Monto máximo permitido")
        BigDecimal maxMonto,

        @NotNull
        @DecimalMin(value = "0.0", inclusive = false)
        @Schema(example = "0.015", description = "Ratio de interés aplicado")
        Double ratioInteres,

        @NotNull
        @Schema(example = "true", description = "Indica si requiere aprobación manual")
        Boolean aprobacion
) {}
