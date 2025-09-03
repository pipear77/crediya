package co.com.pragma.r2dbc.adapter;

import co.com.pragma.model.solicitud.solicitudprestamos.Solicitud;
import co.com.pragma.r2dbc.entity.SolicitudEntity;
import co.com.pragma.r2dbc.repositories.ReactiveSolicitudRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SolicitudPrestamoRepositoryAdapterTest {
    private ReactiveSolicitudRepository repository;
    private ObjectMapper mapper;
    private TransactionalOperator txOperator;
    private SolicitudPrestamoRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        repository = mock(ReactiveSolicitudRepository.class);
        mapper = mock(ObjectMapper.class);
        txOperator = mock(TransactionalOperator.class);

        adapter = new SolicitudPrestamoRepositoryAdapter(repository, mapper, txOperator);
    }

    @Test
    void guardarSolicitud_exito() {
        Solicitud solicitud = Solicitud.builder()
                .id("uuid-123")
                .documentoIdentidad("123456789")
                .build();

        SolicitudEntity entity = SolicitudEntity.builder()
                .id("uuid-123")
                .documentoIdentidad("123456789")
                .build();

        when(mapper.map(any(Solicitud.class), eq(SolicitudEntity.class))).thenReturn(entity);
        when(repository.save(entity)).thenReturn(Mono.just(entity));
        when(mapper.map(entity, Solicitud.class)).thenReturn(solicitud);
        when(txOperator.transactional(any(Mono.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StepVerifier.create(adapter.guardar(solicitud))
                .expectNextMatches(saved -> saved.getId().equals("uuid-123"))
                .verifyComplete();

        verify(repository).save(entity);
        verify(txOperator).transactional(any(Mono.class));
    }

    @Test
    void guardarSolicitud_error() {
        Solicitud solicitud = Solicitud.builder().id("uuid-123").build();
        SolicitudEntity entity = SolicitudEntity.builder().id("uuid-123").build();

        when(mapper.map(any(Solicitud.class), eq(SolicitudEntity.class))).thenReturn(entity);
        when(repository.save(entity)).thenReturn(Mono.error(new RuntimeException("DB error")));
        when(txOperator.transactional(any(Mono.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StepVerifier.create(adapter.guardar(solicitud))
                .expectErrorMatches(e -> e instanceof RuntimeException &&
                        e.getMessage().equals("Error creando la solicitud de prestamo"))
                .verify();

        verify(repository).save(entity);
        verify(txOperator).transactional(any(Mono.class));
    }
}