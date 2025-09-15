package co.com.pragma.api;

import co.com.pragma.api.dto.solicitud.ActualizarEstadoSolicitudDTO;
import co.com.pragma.api.dto.solicitud.SolicitudRequestDTO;
import co.com.pragma.api.dto.solicitud.SolicitudResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.PATCH;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    private static final String BASE_PATH = "/api/v1/solicitudes";
    private static final String MEDIA_JSON = MediaType.APPLICATION_JSON_VALUE;

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = BASE_PATH,
                    method = RequestMethod.POST,
                    produces = MEDIA_JSON,
                    consumes = MEDIA_JSON,
                    beanClass = SolicitudHandler.class,
                    beanMethod = "save",
                    operation = @Operation(
                            operationId = "crearSolicitud",
                            summary = "Registrar una solicitud de préstamo",
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(mediaType = MEDIA_JSON,
                                            schema = @Schema(implementation = SolicitudRequestDTO.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Solicitud creada exitosamente",
                                            content = @Content(mediaType = MEDIA_JSON,
                                                    schema = @Schema(implementation = SolicitudResponseDTO.class))),
                                    @ApiResponse(responseCode = "400", description = "Solicitud inválida")
                            }
                    )
            ),
            @RouterOperation(
                    path = BASE_PATH,
                    method = RequestMethod.GET,
                    produces = MEDIA_JSON,
                    beanClass = SolicitudHandler.class,
                    beanMethod = "listar",
                    operation = @Operation(
                            operationId = "listarSolicitudes",
                            summary = "Listar solicitudes para revisión manual",
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Listado exitoso",
                                            content = @Content(mediaType = MEDIA_JSON,
                                                    schema = @Schema(implementation = SolicitudResponseDTO.class))),
                                    @ApiResponse(responseCode = "403", description = "Acceso denegado")
                            }
                    )
            ),
            @RouterOperation(
                    path = BASE_PATH + "/{id}/estado",
                    method = RequestMethod.PATCH,
                    beanClass = SolicitudHandler.class,
                    beanMethod = "actualizarEstado",
                    operation = @Operation(
                            operationId = "actualizarEstadoSolicitud",
                            summary = "Actualizar el estado de una solicitud de préstamo",
                            parameters = {
                                    @Parameter(
                                            name = "id",
                                            description = "ID de la solicitud de préstamo",
                                            required = true,
                                            in = ParameterIn.PATH,
                                            schema = @Schema(type = "string", format = "uuid")
                                    )
                            },
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(mediaType = MEDIA_JSON,
                                            schema = @Schema(implementation = ActualizarEstadoSolicitudDTO.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente"),
                                    @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
                                    @ApiResponse(responseCode = "403", description = "Acceso denegado"),
                                    @ApiResponse(responseCode = "404", description = "Solicitud no encontrada")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(SolicitudHandler handler) {
        return route()
                .POST(BASE_PATH, accept(MediaType.APPLICATION_JSON), handler::save)
                .GET(BASE_PATH, accept(MediaType.APPLICATION_JSON), handler::listar)
                .PATCH(BASE_PATH + "/{id}/estado", accept(MediaType.APPLICATION_JSON), handler::actualizarEstado)
                .build();
    }
}
