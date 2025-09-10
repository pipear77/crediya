package co.com.pragma.r2dbc.entity;

import co.com.pragma.model.solicitud.enums.EstadoSolicitud;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Table(name = "solicitud_prestamo", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SolicitudEntity {

    @Id
    private UUID id;

    @Column("documento_identidad")
    private String documentoIdentidad;

    @Column("correo")
    private String correo;

    @Column("nombre")
    private String nombre;

    @Column("monto_solicitado")
    private BigDecimal montoSolicitado;

    @Column("plazo_meses")
    private Integer plazoMeses;

    @Column("id_tipo_prestamo")
    private UUID idTipoPrestamo;

    @Column("tipo_prestamo")
    private String tipoPrestamo;

    @Column("tasa_interes")
    private BigDecimal tasaInteres;

    @Column("salario_base")
    private BigDecimal salarioBase;

    @Column("monto_mensual_solicitud")
    private BigDecimal montoMensualSolicitud;

    @Column("estado")
    private EstadoSolicitud estado;

    @Column("canal")
    private String canal;
}
