package br.com.healthdictionary.repositories;

import br.com.healthdictionary.models.CidModel;
import br.com.healthdictionary.models.TratamentoModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class TratamentoRepositoryTest {

    @Mock
    private TratamentoRepository tratamentoRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("deve registrar tratamento com sucesso")
    public void deveRegistrarTratamentoComSucesso() {
        TratamentoModel tratamentoModel = new TratamentoModel(null, "test",
                "test", null, null);
        when(tratamentoRepository.save(tratamentoModel)).thenReturn(tratamentoModel);
        TratamentoModel tratamentoModelSaved = tratamentoRepository.save(tratamentoModel);
        assertThat(tratamentoModelSaved).isInstanceOf(TratamentoModel.class).isNotNull();
        assertThat(tratamentoModelSaved.getId()).isEqualTo(tratamentoModel.getId());
        assertThat(tratamentoModelSaved.getTratamento()).isEqualTo(tratamentoModel.getTratamento());
        assertThat(tratamentoModelSaved.getReferenciaBibliografica()).isEqualTo(tratamentoModel.getReferenciaBibliografica());
        assertThat(tratamentoModelSaved.getCid()).isEqualTo(tratamentoModel.getCid());
        assertThat(tratamentoModelSaved.getMedicacoes()).isEqualTo(tratamentoModel.getMedicacoes());
    }

    @Test
    @DisplayName("deve consultar tratamento com sucesso")
    public void deveConsultarTratamentoComSucesso() {
        TratamentoModel tratamentoModel = new TratamentoModel(1L, "test",
                "test", null, null);

        when(tratamentoRepository.findById(1L)).thenReturn(Optional.of(tratamentoModel));
        Optional<TratamentoModel> optionalTratamento = tratamentoRepository.findById(1L);
        assertThat(optionalTratamento).isInstanceOf(Optional.class).isNotNull();

        if (optionalTratamento.isEmpty())
            throw new NoSuchElementException("tratamento não encontrado");

        assertThat(optionalTratamento.get()).isInstanceOf(TratamentoModel.class).isNotNull();
        assertThat(optionalTratamento.get().getId()).isEqualTo(tratamentoModel.getId());
        assertThat(optionalTratamento.get().getTratamento()).isEqualTo(tratamentoModel.getTratamento());
        assertThat(optionalTratamento.get().getReferenciaBibliografica()).isEqualTo(tratamentoModel.getReferenciaBibliografica());
        assertThat(optionalTratamento.get().getCid()).isEqualTo(tratamentoModel.getCid());
        assertThat(optionalTratamento.get().getMedicacoes()).isEqualTo(tratamentoModel.getMedicacoes());
    }

    @Test
    @DisplayName("deve apagar tratamento com sucesso")
    public void deveApagarTratamentoComSucesso() {
        Long id = 1L;
        doNothing().when(tratamentoRepository).deleteById(id);
        tratamentoRepository.deleteById(id);
        verify(tratamentoRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("deve listar tratamentos com sucesso")
    public void deveListarTratamentosComSucesso() {
        TratamentoModel tratamentoModel1 = new TratamentoModel(1L, "test",
                "test", null, null);

        TratamentoModel tratamentoModel2 = new TratamentoModel(2L, "test",
                "test", null, null);


        List<TratamentoModel> tratamentos = List.of(tratamentoModel1, tratamentoModel2);
        when(tratamentoRepository.findAll()).thenReturn(tratamentos);
        List<TratamentoModel> tratamentosReturned = tratamentoRepository.findAll();

        assertThat(tratamentosReturned).isNotNull().hasSize(2)
                .containsExactlyInAnyOrder(tratamentoModel1, tratamentoModel2);
    }

    @Test
    @DisplayName("deve listar tratamentos por codigo de cid com sucesso")
    public void deveListarTratamentosPorCodigoDeCidComSucesso() {
        TratamentoModel tratamentoModel1 = new TratamentoModel(1L, "test",
                "test", null, null);

        TratamentoModel tratamentoModel2 = new TratamentoModel(2L, "test",
                "test", null, null);

        List<TratamentoModel> tratamentos = List.of(tratamentoModel1, tratamentoModel2);
        when(tratamentoRepository.findAllByCidCodigo("1")).thenReturn(tratamentos);
        List<TratamentoModel> tratamentosReturned = tratamentoRepository.findAllByCidCodigo("1");

        assertThat(tratamentosReturned).isNotNull().hasSize(2)
                .containsExactlyInAnyOrder(tratamentoModel1, tratamentoModel2);
    }


}
