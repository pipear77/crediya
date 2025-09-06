package co.com.pragma.api.dto.tipoprestamo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(name = "TipoPrestamoResponseDTO", description = "Datos del tipo de préstamo disponible")
public record TipoPrestamoResponseDTO(

        @Schema(example = "uuid-tipo-prestamo-001", description = "Identificador único del tipo de préstamo")
        String id,

        @Schema(example = "CONSUMO", description = "Nombre del tipo de préstamo")
        String nombre,

        @Schema(example = "1000000.00", description = "Monto mínimo permitido")
        BigDecimal minMonto,

        @Schema(example = "10000000.00", description = "Monto máximo permitido")
        BigDecimal maxMonto,

        @Schema(example = "0.015", description = "Ratio de interés aplicado")
        Double ratioInteres,

        @Schema(example = "true", description = "Indica si requiere aprobación manual")
        Boolean aprobacion
) {}
