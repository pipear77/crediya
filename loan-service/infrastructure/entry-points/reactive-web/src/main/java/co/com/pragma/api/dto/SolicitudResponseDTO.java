package co.com.pragma.api.dto;

import co.com.pragma.model.solicitud.enums.EstadoSolicitud;
import co.com.pragma.model.solicitud.enums.TipoPrestamo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudResponseDTO {

    @Schema(example = "sol123", description = "Identificador único de la solicitud")
    private String id;

    @Schema(example = "123456789", description = "Documento de identidad del solicitante")
    private String documentoIdentidad;

    @Schema(example = "5000000.00", description = "Monto solicitado")
    private BigDecimal montoSolicitado;

    @Schema(example = "24", description = "Plazo en meses")
    private Integer plazoMeses;

    @Schema(example = "CONSUMO", description = "Tipo de préstamo")
    private TipoPrestamo tipoPrestamo;

    @Schema(example = "PENDIENTE", description = "Estado de la solicitud")
    private EstadoSolicitud estado;
}