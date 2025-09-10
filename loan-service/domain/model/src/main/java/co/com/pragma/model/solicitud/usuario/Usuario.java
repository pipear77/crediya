package co.com.pragma.model.solicitud.usuario;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

import java.math.BigDecimal;
@Value
@Builder
@Getter
@Setter
public class Usuario {
    String id;
    String correo;
    String documentoIdentidad;
    String nombres;
    String apellidos;
    String rol;
    String estado;
    boolean sesionActiva;
    BigDecimal salarioBase;
}
