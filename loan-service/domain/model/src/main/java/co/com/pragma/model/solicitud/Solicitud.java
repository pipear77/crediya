package co.com.pragma.model.solicitud;
import co.com.pragma.model.solicitud.enums.EstadoSolicitud;
import co.com.pragma.model.solicitud.enums.TipoPrestamo;
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
    TipoPrestamo tipoPrestamo;
    EstadoSolicitud estado;
}
