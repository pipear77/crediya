package co.com.pragma.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table(name = "tipo_prestamo", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder

public class TipoPrestamoEntity {
    @Id
    private String id;
    private String nombre;
    private BigDecimal minMonto;
    private BigDecimal maxMonto;
    private Double ratioInteres;
    private Boolean aprobacion;

}
