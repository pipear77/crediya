package co.com.pragma.api.config;

import co.com.pragma.api.RouterRest;
import co.com.pragma.api.SolicitudHandler;
import co.com.pragma.api.helper.TokenExtractor;
import co.com.pragma.api.mapper.SolicitudApiMapper;
import co.com.pragma.usecase.solicitarprestamo.ListarSolicitudesParaRevisionUseCase;
import co.com.pragma.usecase.solicitarprestamo.SolicitarPrestamoUseCase;
import jakarta.validation.Validator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import jakarta.validation.Validator;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@Import({RouterRest.class})
public class TestApplicationConfig {

    @Bean
    public SolicitudHandler solicitudHandler(SolicitarPrestamoUseCase solicitarUseCase,
                                             ListarSolicitudesParaRevisionUseCase listarUseCase,
                                             SolicitudApiMapper mapper,
                                             Validator validator,
                                             TokenExtractor tokenExtractor) {
        return new SolicitudHandler(solicitarUseCase, listarUseCase, mapper, validator, tokenExtractor);
    }


    @Bean
    public Validator validator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        return factory.getValidator();
    }
}
