package br.com.healthdictionary.repositories;

import br.com.healthdictionary.models.MedicacaoModel;
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

public class MedicacaoRepositoryTest {

    @Mock
    private MedicacaoRepository medicacaoRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("deve registrar medicacao com sucesso")
    public void deveRegistrarMedicacaoComSucesso() {
        MedicacaoModel medicacaoModel = new MedicacaoModel(null, "medicacao teste",
                1.1, 1, "obs", null);

        when(medicacaoRepository.save(medicacaoModel)).thenReturn(medicacaoModel);
        MedicacaoModel medicacaoModelSaved = medicacaoRepository.save(medicacaoModel);
        assertThat(medicacaoModelSaved).isInstanceOf(MedicacaoModel.class).isNotNull();
        assertThat(medicacaoModelSaved.getId()).isEqualTo(medicacaoModel.getId());
        assertThat(medicacaoModelSaved.getMedicacao()).isEqualTo(medicacaoModel.getMedicacao());
        assertThat(medicacaoModelSaved.getDosagem()).isEqualTo(medicacaoModel.getDosagem());
        assertThat(medicacaoModelSaved.getQuantidadeDias()).isEqualTo(medicacaoModel.getQuantidadeDias());
        assertThat(medicacaoModelSaved.getObservacao()).isEqualTo(medicacaoModel.getObservacao());
        assertThat(medicacaoModelSaved.getTratamento()).isEqualTo(medicacaoModel.getTratamento());
    }

    @Test
    @DisplayName("deve consultar medicacao com sucesso")
    public void deveConsultarMedicacaoComSucesso() {
        MedicacaoModel medicacaoModel = new MedicacaoModel(1L, "medicacao teste",
                1.1, 1, "obs", null);

        when(medicacaoRepository.findById(1L)).thenReturn(Optional.of(medicacaoModel));
        Optional<MedicacaoModel> optionalMedicacao = medicacaoRepository.findById(1L);

        assertThat(optionalMedicacao).isInstanceOf(Optional.class).isNotNull();
        if (optionalMedicacao.isEmpty())
            throw new NoSuchElementException("medicação não encontrada");

        assertThat(optionalMedicacao.get()).isInstanceOf(MedicacaoModel.class).isNotNull();
        assertThat(optionalMedicacao.get().getId()).isEqualTo(medicacaoModel.getId());
        assertThat(optionalMedicacao.get().getMedicacao()).isEqualTo(medicacaoModel.getMedicacao());
        assertThat(optionalMedicacao.get().getDosagem()).isEqualTo(medicacaoModel.getDosagem());
        assertThat(optionalMedicacao.get().getQuantidadeDias()).isEqualTo(medicacaoModel.getQuantidadeDias());
        assertThat(optionalMedicacao.get().getObservacao()).isEqualTo(medicacaoModel.getObservacao());
        assertThat(optionalMedicacao.get().getTratamento()).isEqualTo(medicacaoModel.getTratamento());
    }

    @Test
    @DisplayName("deve apagar medicacao com sucesso")
    public void deveApagarMedicacaoComSucesso() {
        Long id = 1L;
        doNothing().when(medicacaoRepository).deleteById(id);
        medicacaoRepository.deleteById(id);
        verify(medicacaoRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("deve listar medicacoes com sucesso")
    public void deveListarMedicacoesComSucesso() {
        MedicacaoModel medicacaoModel1 = new MedicacaoModel(1L, "medicacao teste",
                1.1, 1, "obs", null);

        MedicacaoModel medicacaoModel2 = new MedicacaoModel(2L, "medicacao teste",
                1.1, 1, "obs", null);


        List<MedicacaoModel> medicacoes = List.of(medicacaoModel1, medicacaoModel2);
        when(medicacaoRepository.findAll()).thenReturn(medicacoes);
        List<MedicacaoModel> medicacoesReturned = medicacaoRepository.findAll();

        assertThat(medicacoesReturned).isNotNull().hasSize(2)
                .containsExactlyInAnyOrder(medicacaoModel1, medicacaoModel2);
    }

}