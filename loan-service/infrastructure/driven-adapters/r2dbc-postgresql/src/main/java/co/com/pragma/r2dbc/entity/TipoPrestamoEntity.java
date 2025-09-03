package co.com.pragma.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table(name = "loan_type", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder

public class TipoPrestamoEntity {
    @Id
    private Long id;
    private String name;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private Double interestRate;
    private Boolean automaticApproval;

}
