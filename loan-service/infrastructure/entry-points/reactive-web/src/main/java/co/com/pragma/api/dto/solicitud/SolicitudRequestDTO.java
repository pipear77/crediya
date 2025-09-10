package co.com.pragma.api.dto.solicitud;

import co.com.pragma.model.solicitud.enums.EstadoSolicitud;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(name = "SolicitudRequestDTO", description = "Datos para registrar una solicitud de préstamo")
public record SolicitudRequestDTO(

        @NotBlank
        @Schema(example = "APP_WEB", description = "Canal por el cual se realizó la solicitud")
        String canal,

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
        @DecimalMin(value = "0.0", inclusive = true)
        @Schema(example = "1000000.00", description = "Salario base del solicitante")
        BigDecimal salarioBase,

        @NotNull
        @DecimalMin(value = "0.0", inclusive = true)
        @Schema(example = "0.19", description = "Tasa de interés aplicada")
        Double tasaInteres
) {}
