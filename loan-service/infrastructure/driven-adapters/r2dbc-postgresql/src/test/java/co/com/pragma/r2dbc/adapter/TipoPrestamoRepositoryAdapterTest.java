package co.com.pragma.r2dbc.adapter;

import co.com.pragma.model.solicitud.tipoprestamos.TipoPrestamo;
import co.com.pragma.r2dbc.entity.TipoPrestamoEntity;
import co.com.pragma.r2dbc.repositories.ReactiveTipoPrestamoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.Mockito.*;

class TipoPrestamoRepositoryAdapterTest {

    private ReactiveTipoPrestamoRepository repository;
    private ObjectMapper mapper;
    private TipoPrestamoRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        repository = mock(ReactiveTipoPrestamoRepository.class);
        mapper = mock(ObjectMapper.class);
        adapter = new TipoPrestamoRepositoryAdapter(repository, mapper);
    }

    @Test
    void findById_deberiaRetornarTipoPrestamo_siExiste() {
        UUID id = UUID.randomUUID();

        TipoPrestamoEntity entity = TipoPrestamoEntity.builder()
                .id(id)
                .nombre("PERSONAL")
                .build();

        TipoPrestamo domain = TipoPrestamo.builder()
                .id(id)
                .nombre("PERSONAL")
                .build();

        when(repository.findById(id)).thenReturn(Mono.just(entity));
        when(mapper.map(entity, TipoPrestamo.class)).thenReturn(domain);

        StepVerifier.create(adapter.findById(id))
                .expectNextMatches(tp -> tp.getId().equals(id) && tp.getNombre().equals("PERSONAL"))
                .verifyComplete();

        verify(repository).findById(id);
        verify(mapper).map(entity, TipoPrestamo.class);
    }

    @Test
    void findById_deberiaRetornarMonoVacio_siNoExiste() {
        UUID id = UUID.randomUUID();

        when(repository.findById(id)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findById(id))
                .expectComplete()
                .verify();

        verify(repository).findById(id);
        verifyNoInteractions(mapper);
    }
}
