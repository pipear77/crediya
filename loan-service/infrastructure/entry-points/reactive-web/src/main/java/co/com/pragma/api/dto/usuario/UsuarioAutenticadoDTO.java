package co.com.pragma.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioAutenticadoDTO {
    private String id;                    // UUID del usuario
    private String correo;               // Correo electrónico
    private String documentoIdentidad;   // Número de documento
    private String rol;                  // ROL_CLIENTE, ROL_ADMIN, etc.
    private String estado;               // ACTIVO, INACTIVO, etc.
    private boolean sesionActiva;        // true si tiene sesión iniciada
}