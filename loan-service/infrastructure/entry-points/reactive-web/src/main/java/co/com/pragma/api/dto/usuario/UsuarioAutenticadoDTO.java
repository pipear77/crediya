package co.com.pragma.api.dto.usuario;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioAutenticadoDTO {
    private String id;
    private String correo;
    private String documentoIdentidad;
    private String nombres;
    private String apellidos;
    private String rol;
    private String estado;
    private boolean sesionActiva;
    private BigDecimal salarioBase;
}