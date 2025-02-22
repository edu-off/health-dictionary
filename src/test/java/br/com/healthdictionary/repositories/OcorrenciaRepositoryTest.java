package br.com.healthdictionary.repositories;

import br.com.healthdictionary.models.OcorrenciaModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class OcorrenciaRepositoryTest {

    @Mock
    private OcorrenciaRepository ocorrenciaRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("deve registrar ocorrencia com sucesso")
    public void deveRegistrarOcorrenciaComSucesso() {
        OcorrenciaModel ocorrenciaModel = new OcorrenciaModel(null, "test", "test",
                LocalDateTime.now(), null);

        when(ocorrenciaRepository.save(ocorrenciaModel)).thenReturn(ocorrenciaModel);
        OcorrenciaModel ocorrenciaModelSaved = ocorrenciaRepository.save(ocorrenciaModel);
        assertThat(ocorrenciaModelSaved).isInstanceOf(OcorrenciaModel.class).isNotNull();
        assertThat(ocorrenciaModelSaved.getId()).isEqualTo(ocorrenciaModel.getId());
        assertThat(ocorrenciaModelSaved.getSintomas()).isEqualTo(ocorrenciaModel.getDiagnostico());
        assertThat(ocorrenciaModelSaved.getRegistro()).isEqualTo(ocorrenciaModel.getRegistro());
    }

    @Test
    @DisplayName("deve consulta ocorrencia com sucesso")
    public void deveConsultarOcorrenciaComSucesso() {
        OcorrenciaModel ocorrenciaModel = new OcorrenciaModel(1L, "test", "test",
                LocalDateTime.now(), null);

        when(ocorrenciaRepository.findById(1L)).thenReturn(Optional.of(ocorrenciaModel));
        Optional<OcorrenciaModel> optionalOcorrencia = ocorrenciaRepository.findById(1L);

        assertThat(optionalOcorrencia).isInstanceOf(Optional.class).isNotNull();
        if (optionalOcorrencia.isEmpty())
            throw new NoSuchElementException("ocorrência não encontrada");

        assertThat(optionalOcorrencia.get()).isInstanceOf(OcorrenciaModel.class).isNotNull();
        assertThat(optionalOcorrencia.get().getId()).isEqualTo(ocorrenciaModel.getId());
        assertThat(optionalOcorrencia.get().getSintomas()).isEqualTo(ocorrenciaModel.getDiagnostico());
        assertThat(optionalOcorrencia.get().getRegistro()).isEqualTo(ocorrenciaModel.getRegistro());
    }

    @Test
    @DisplayName("deve apagar ocorrencia com sucesso")
    public void deveApagarOcorrenciaComSucesso() {
        Long id = 1L;
        doNothing().when(ocorrenciaRepository).deleteById(id);
        ocorrenciaRepository.deleteById(id);
        verify(ocorrenciaRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("deve listar ocorrencias com sucesso")
    public void deveListarCidsOcorrenciaSucesso() {
        OcorrenciaModel ocorrenciaModel1 = new OcorrenciaModel(1L, "test", "test",
                LocalDateTime.now(), null);

        OcorrenciaModel ocorrenciaModel2 = new OcorrenciaModel(2L, "test", "test",
                LocalDateTime.now(), null);

        List<OcorrenciaModel> ocorrencias = List.of(ocorrenciaModel1, ocorrenciaModel2);
        when(ocorrenciaRepository.findAll()).thenReturn(ocorrencias);
        List<OcorrenciaModel> ocorrenciasReturned = ocorrenciaRepository.findAll();

        assertThat(ocorrenciasReturned).isNotNull().hasSize(2)
                .containsExactlyInAnyOrder(ocorrenciaModel1, ocorrenciaModel2);
    }

    @Test
    @DisplayName("deve listar ocorrencias por cpf com sucesso")
    public void deveListarCidsOcorrenciasPorCpfComSucesso() {
        OcorrenciaModel ocorrenciaModel1 = new OcorrenciaModel(1L, "test", "test",
                LocalDateTime.now(), null);

        OcorrenciaModel ocorrenciaModel2 = new OcorrenciaModel(2L, "test", "test",
                LocalDateTime.now(), null);

        List<OcorrenciaModel> ocorrencias = List.of(ocorrenciaModel1, ocorrenciaModel2);
        when(ocorrenciaRepository.findAllByPacienteCpf("1")).thenReturn(ocorrencias);
        List<OcorrenciaModel> ocorrenciasReturned = ocorrenciaRepository.findAllByPacienteCpf("1");

        assertThat(ocorrenciasReturned).isNotNull().hasSize(2)
                .containsExactlyInAnyOrder(ocorrenciaModel1, ocorrenciaModel2);
    }

}
