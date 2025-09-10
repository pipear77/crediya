package co.com.pragma.api;

import co.com.pragma.api.dto.solicitud.SolicitudRequestDTO;
import co.com.pragma.api.dto.solicitud.SolicitudResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/solicitudes",
                    method = RequestMethod.POST,
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    consumes = MediaType.APPLICATION_JSON_VALUE,
                    beanClass = SolicitudHandler.class,
                    beanMethod = "save",
                    operation = @Operation(
                            operationId = "crearSolicitud",
                            summary = "Registrar una solicitud de préstamo",
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = SolicitudRequestDTO.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "Solicitud creada exitosamente",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = SolicitudResponseDTO.class)
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Solicitud inválida",
                                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/solicitudes",
                    method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    beanClass = SolicitudHandler.class,
                    beanMethod = "listar",
                    operation = @Operation(
                            operationId = "listarSolicitudes",
                            summary = "Listar solicitudes para revisión manual",
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Listado exitoso",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = SolicitudResponseDTO.class)
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "403",
                                            description = "Acceso denegado",
                                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
                                    )
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(SolicitudHandler handler) {
        return route(POST("/api/v1/solicitudes"), handler::save)
                .andRoute(GET("/api/v1/solicitudes"), handler::listar);
    }
}
