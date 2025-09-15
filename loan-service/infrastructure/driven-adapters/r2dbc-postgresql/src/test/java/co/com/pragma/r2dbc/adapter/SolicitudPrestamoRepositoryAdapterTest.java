package co.com.pragma.r2dbc.adapter;

import co.com.pragma.model.solicitud.solicitudprestamos.Solicitud;
import co.com.pragma.r2dbc.entity.SolicitudEntity;
import co.com.pragma.r2dbc.exceptions.SolicitudPrestamoNoGuardadoException;
import co.com.pragma.r2dbc.mapper.SolicitudEntityMapper;
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

import static co.com.pragma.r2dbc.common.Constantes.SOLICITUD_PRESTAMO_NOT_SAVED_ERROR_MSG;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SolicitudPrestamoRepositoryAdapterTest {

    private ReactiveSolicitudRepository repository;
    private ObjectMapper mapper;
    private TransactionalOperator txOperator;
    private R2dbcEntityTemplate entityTemplate;
    private SolicitudEntityMapper solicitudMapper;
    private SolicitudPrestamoRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        repository = mock(ReactiveSolicitudRepository.class);
        mapper = mock(ObjectMapper.class); // no usado directamente en guardar()
        txOperator = mock(TransactionalOperator.class);
        entityTemplate = mock(R2dbcEntityTemplate.class);
        solicitudMapper = mock(SolicitudEntityMapper.class);

        adapter = new SolicitudPrestamoRepositoryAdapter(repository, mapper, txOperator, entityTemplate, solicitudMapper);
    }

    @Test
    void guardarSolicitud_exito() {
        UUID id = UUID.randomUUID();
        Solicitud solicitud = Solicitud.builder().id(id).documentoIdentidad("123456789").build();
        SolicitudEntity entity = SolicitudEntity.builder().id(id).documentoIdentidad("123456789").build();
        ReactiveInsertOperation.ReactiveInsert<SolicitudEntity> insertSpec = mock(ReactiveInsertOperation.ReactiveInsert.class);

        // ✅ Mock correcto del mapper usado en guardar()
        when(solicitudMapper.toEntity(solicitud)).thenReturn(entity);
        when(solicitudMapper.toDomain(entity)).thenReturn(solicitud);

        when(entityTemplate.insert(SolicitudEntity.class)).thenReturn(insertSpec);
        when(insertSpec.using(entity)).thenReturn(Mono.just(entity));
        when(txOperator.transactional(any(Mono.class))).thenAnswer(inv -> inv.getArgument(0));

        StepVerifier.create(adapter.guardar(solicitud))
                .expectNextMatches(saved -> saved.getId().equals(id))
                .verifyComplete();

        verify(solicitudMapper).toEntity(solicitud);
        verify(entityTemplate).insert(SolicitudEntity.class);
        verify(insertSpec).using(entity);
        verify(txOperator).transactional(any(Mono.class));
        verify(solicitudMapper).toDomain(entity);
    }

    @Test
    void guardarSolicitud_error() {
        UUID id = UUID.randomUUID();
        Solicitud solicitud = Solicitud.builder().id(id).build();
        SolicitudEntity entity = SolicitudEntity.builder().id(id).build();
        ReactiveInsertOperation.ReactiveInsert<SolicitudEntity> insertSpec = mock(ReactiveInsertOperation.ReactiveInsert.class);

        when(solicitudMapper.toEntity(solicitud)).thenReturn(entity);
        when(entityTemplate.insert(SolicitudEntity.class)).thenReturn(insertSpec);
        when(insertSpec.using(entity)).thenReturn(Mono.error(new RuntimeException("DB error")));
        when(txOperator.transactional(any(Mono.class))).thenAnswer(inv -> inv.getArgument(0));

        StepVerifier.create(adapter.guardar(solicitud))
                .expectErrorMatches(e -> e instanceof SolicitudPrestamoNoGuardadoException &&
                        SOLICITUD_PRESTAMO_NOT_SAVED_ERROR_MSG.equals(e.getMessage()))
                .verify();

        verify(solicitudMapper).toEntity(solicitud);
        verify(entityTemplate).insert(SolicitudEntity.class);
        verify(insertSpec).using(entity);
        verify(txOperator).transactional(any(Mono.class));
    }


}
