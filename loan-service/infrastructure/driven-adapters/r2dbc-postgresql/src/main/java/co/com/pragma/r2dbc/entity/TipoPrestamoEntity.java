package co.com.pragma.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Table(name = "tipo_prestamo", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder

public class TipoPrestamoEntity {
    @Id
    private UUID id;
    private String nombre;
    private BigDecimal montoMinimo;
    private BigDecimal montoMaximo;
    private Double tasaInteres;
    private Boolean aprobacionAutomatica;

}
