package co.com.pragma.r2dbc.entity;


import co.com.pragma.model.solicitud.enums.EstadoSolicitud;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table(name = "solicitud_prestamo", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SolicitudEntity {
    @Id
    private String id;
    @Column("documento_identidad")
    private String documentoIdentidad;
    @Column("monto_solicitado")
    private BigDecimal montoSolicitado;
    @Column("plazo_meses")
    private Integer plazoMeses;
    @Column("id_tipo_prestamo")
    private String idTipoPrestamo;
    private EstadoSolicitud estado;

}
