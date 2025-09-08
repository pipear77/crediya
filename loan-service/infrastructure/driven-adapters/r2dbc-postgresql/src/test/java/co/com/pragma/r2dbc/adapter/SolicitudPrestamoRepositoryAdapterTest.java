package co.com.pragma.r2dbc.adapter;

import co.com.pragma.model.solicitud.solicitudprestamos.Solicitud;
import co.com.pragma.r2dbc.entity.SolicitudEntity;
import co.com.pragma.r2dbc.repositories.ReactiveSolicitudRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.core.ReactiveInsertOperation;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SolicitudPrestamoRepositoryAdapterTest {

    private ReactiveSolicitudRepository repository;
    private ObjectMapper mapper;
    private TransactionalOperator txOperator;
    private SolicitudPrestamoRepositoryAdapter adapter;
    private R2dbcEntityTemplate entityTemplate;

    @BeforeEach
    void setUp() {
        repository = mock(ReactiveSolicitudRepository.class);
        mapper = mock(ObjectMapper.class);
        txOperator = mock(TransactionalOperator.class);
        entityTemplate = mock(R2dbcEntityTemplate.class); // ✅ mock explícito

        adapter = new SolicitudPrestamoRepositoryAdapter(repository, mapper, txOperator, entityTemplate);
    }

    @Test
    void guardarSolicitud_exito() {
        UUID id = UUID.randomUUID();

        Solicitud solicitud = Solicitud.builder()
                .id(id)
                .documentoIdentidad("123456789")
                .build();

        SolicitudEntity entity = SolicitudEntity.builder()
                .id(id)
                .documentoIdentidad("123456789")
                .build();

        ReactiveInsertOperation.ReactiveInsert<SolicitudEntity> insertSpec = mock(ReactiveInsertOperation.ReactiveInsert.class);

        when(mapper.map(solicitud, SolicitudEntity.class)).thenReturn(entity);
        when(entityTemplate.insert(SolicitudEntity.class)).thenReturn(insertSpec);
        when(insertSpec.using(entity)).thenReturn(Mono.just(entity));
        when(mapper.map(entity, Solicitud.class)).thenReturn(solicitud);
        when(txOperator.transactional(any(Mono.class))).thenAnswer(inv -> inv.getArgument(0));

        StepVerifier.create(adapter.guardar(solicitud))
                .expectNextMatches(saved -> saved.getId().equals(id))
                .verifyComplete();

        verify(entityTemplate).insert(SolicitudEntity.class);
        verify(insertSpec).using(entity);
        verify(txOperator).transactional(any(Mono.class));
    }


    @Test
    void guardarSolicitud_error() {
        UUID id = UUID.randomUUID();

        Solicitud solicitud = Solicitud.builder().id(id).build();
        SolicitudEntity entity = SolicitudEntity.builder().id(id).build();

        ReactiveInsertOperation.ReactiveInsert<SolicitudEntity> insertSpec = mock(ReactiveInsertOperation.ReactiveInsert.class);

        when(mapper.map(solicitud, SolicitudEntity.class)).thenReturn(entity);
        when(entityTemplate.insert(SolicitudEntity.class)).thenReturn(insertSpec);
        when(insertSpec.using(entity)).thenReturn(Mono.error(new RuntimeException("DB error")));
        when(txOperator.transactional(any(Mono.class))).thenAnswer(inv -> inv.getArgument(0));

        StepVerifier.create(adapter.guardar(solicitud))
                .expectErrorMatches(e -> e instanceof RuntimeException &&
                        e.getMessage().equals("Error creando la solicitud de prestamo"))
                .verify();

        verify(entityTemplate).insert(SolicitudEntity.class);
        verify(insertSpec).using(entity);
        verify(txOperator).transactional(any(Mono.class));
    }

}
