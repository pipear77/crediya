package co.com.pragma.api.config;

import co.com.pragma.api.RouterRest;
import co.com.pragma.api.SolicitudHandler;
import co.com.pragma.api.mapper.SolicitudApiMapper;
import co.com.pragma.usecase.solicitarprestamo.SolicitarPrestamoUseCase;
import jakarta.validation.Validator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import jakarta.validation.Validator;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;

@Configuration
@Import({RouterRest.class})
public class TestApplicationConfig {

    @Bean
    public SolicitudHandler solicitudHandler(SolicitarPrestamoUseCase useCase,
                                             SolicitudApiMapper mapper,
                                             Validator validator) {
        return new SolicitudHandler(useCase, mapper, validator);
    }

    @Bean
    public Validator validator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        return factory.getValidator();
    }
}
