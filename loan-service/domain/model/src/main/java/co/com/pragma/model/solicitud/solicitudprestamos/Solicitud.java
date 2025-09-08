package co.com.pragma.model.solicitud.solicitudprestamos;
import co.com.pragma.model.solicitud.enums.EstadoSolicitud;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Solicitud {
    UUID id;
    String documentoIdentidad;
    BigDecimal montoSolicitado;
    Integer plazoMeses;
    UUID idTipoPrestamo;
    EstadoSolicitud estado;
}
