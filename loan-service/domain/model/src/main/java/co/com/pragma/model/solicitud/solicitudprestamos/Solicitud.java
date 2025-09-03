package co.com.pragma.model.solicitud.solicitudprestamos;
import co.com.pragma.model.solicitud.enums.EstadoSolicitud;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Solicitud {
    String id;
    String documentoIdentidad;
    BigDecimal montoSolicitado;
    Integer plazoMeses;
    Long idTipoPrestamo;
    EstadoSolicitud estado;
}
