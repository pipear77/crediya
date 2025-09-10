package co.com.pragma.api.dto.solicitud;

import co.com.pragma.model.solicitud.enums.EstadoSolicitud;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(name = "ActualizarEstadoSolicitudDTO", description = "Datos para actualizar el estado de una solicitud de préstamo")
public record ActualizarEstadoSolicitudDTO(

        @NotNull
        @Schema(example = "APROBADA", description = "Nuevo estado que se desea asignar a la solicitud")
        EstadoSolicitud nuevoEstado

) {}