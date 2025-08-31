package co.com.pragma.api;

import co.com.pragma.api.dto.SolicitudRequestDTO;
import co.com.pragma.api.dto.SolicitudResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;

@Configuration
@Tag(name = "Solicitud", description = "Operaciones para registrar y consultar solicitudes de préstamo")
public class RouterRest {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/solicitud",
                    method = RequestMethod.POST,
                    beanClass = SolicitudHandler.class,
                    beanMethod = "save",
                    operation = @Operation(
                            operationId = "registrarSolicitud",
                            summary = "Registrar solicitud de préstamo",
                            description = "Registra una nueva solicitud con estado PENDIENTE_REVISION",
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = SolicitudRequestDTO.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Solicitud registrada exitosamente"),
                                    @ApiResponse(responseCode = "400", description = "Datos inválidos")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/solicitud/documento/{documento}",
                    method = RequestMethod.GET,
                    beanClass = SolicitudHandler.class,
                    beanMethod = "getByDocumento",
                    operation = @Operation(
                            summary = "Buscar solicitud por documento",
                            description = "Obtiene una solicitud específica por documento de identidad",
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Solicitud encontrada"),
                                    @ApiResponse(responseCode = "404", description = "No encontrada")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/solicitud",
                    method = RequestMethod.GET,
                    beanClass = SolicitudHandler.class,
                    beanMethod = "getByTipo",
                    operation = @Operation(
                            summary = "Listar solicitudes por tipo",
                            description = "Obtiene todas las solicitudes filtradas por tipo de préstamo",
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Lista de solicitudes"),
                                    @ApiResponse(responseCode = "400", description = "Tipo inválido")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> solicitudRoutes(SolicitudHandler handler) {
        return RouterFunctions.route(POST("/api/v1/solicitud"), handler::save)
                .andRoute(GET("/api/v1/solicitud/documento/{documento}"), handler::getByDocumento)
                .andRoute(GET("/api/v1/solicitud"), handler::getByTipo); // usa query param ?tipo=CONSUMO
    }
}
