package br.com.healthdictionary.services;

import br.com.healthdictionary.dto.MedicacaoDTO;
import br.com.healthdictionary.dto.TratamentoDTO;
import br.com.healthdictionary.models.MedicacaoModel;
import br.com.healthdictionary.repositories.MedicacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class MedicacaoServiceTest {

    @InjectMocks
    private MedicacaoService medicacaoService;

    @Mock
    private MedicacaoRepository medicacaoRepository;

    @Mock
    private TratamentoService tratamentoService;

    @Mock
    private ModelMapper mapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("deve recuperar medicacao corretamente")
    public void deveRecuperarMedicacaoCorretamente() {
        MedicacaoModel medicacaoModel1 = new MedicacaoModel(1L, "test", 1.1, 1, "test", null);
        MedicacaoModel medicacaoModel2 = new MedicacaoModel(2L, "test", 1.1, 1, "test", null);
        MedicacaoDTO medicacaoDto1 = new MedicacaoDTO(null, "test", 1.1, 1, "test");
        MedicacaoDTO medicacaoDto2 = new MedicacaoDTO(null, "test", 1.1, 1, "test");
        when(medicacaoRepository.findAllByTratamentoId(1L)).thenReturn(List.of(medicacaoModel1, medicacaoModel2));
        when(mapper.map(medicacaoModel1, MedicacaoDTO.class)).thenReturn(medicacaoDto1);
        when(mapper.map(medicacaoModel2, MedicacaoDTO.class)).thenReturn(medicacaoDto2);
        List<MedicacaoDTO> medicacoesDto = medicacaoService.recuperaMedicacoesPeloTratamentoId(1L);
        assertThat(medicacoesDto).isInstanceOf(List.class).hasSize(2);
    }

    @Test
    @DisplayName("deve cadastrar medicacao corretamente")
    public void deveCadastrarCidCorretamente() {
        MedicacaoModel medicacaoModel = new MedicacaoModel(1L, "test", 1.1, 1, "test", null);
        MedicacaoDTO medicacaoDto = new MedicacaoDTO(null, "test", 1.1, 1, "test");
        TratamentoDTO tratamentoDto = new TratamentoDTO(1L, "test", "test", List.of(medicacaoDto));
        when(tratamentoService.recuperaTratamentoPeloId(1L)).thenReturn(tratamentoDto);
        when(medicacaoRepository.save(medicacaoModel)).thenReturn(medicacaoModel);
        when(mapper.map(medicacaoDto, MedicacaoModel.class)).thenReturn(medicacaoModel);
        when(mapper.map(medicacaoModel, MedicacaoDTO.class)).thenReturn(medicacaoDto);
        MedicacaoDTO medicacaoDtoSaved = medicacaoService.cadastraMedicacao(1L, medicacaoDto);
        assertThat(medicacaoDtoSaved).isInstanceOf(MedicacaoDTO.class).isNotNull();
        assertThat(medicacaoDtoSaved.getMedicacao()).isEqualTo(medicacaoDto.getMedicacao());
        assertThat(medicacaoDtoSaved.getDosagem()).isEqualTo(medicacaoDto.getDosagem());
        assertThat(medicacaoDtoSaved.getQuantidadeDias()).isEqualTo(medicacaoDto.getQuantidadeDias());
        assertThat(medicacaoDtoSaved.getObservacao()).isEqualTo(medicacaoDto.getObservacao());
    }

    @Test
    @DisplayName("deve lancar exception para tratamento nao encontrado")
    public void deveLancarExceptionParaTratamentoNaoEncontrado() {
        MedicacaoDTO medicacaoDto = new MedicacaoDTO(null, "test", 1.1, 1, "test");
        when(tratamentoService.recuperaTratamentoPeloId(1L)).thenThrow(new NoSuchElementException("tratamento não encontrado"));
        assertThatThrownBy(() -> medicacaoService.cadastraMedicacao(1L, medicacaoDto))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("tratamento não encontrado");
    }

    @Test
    @DisplayName("deve atualizar medicacao corretamente")
    public void deveAtualizarMedicacaoCorretamente() {
        MedicacaoModel medicacaoModel = new MedicacaoModel(1L, "test", 1.1, 1, "test", null);
        MedicacaoDTO medicacaoDto = new MedicacaoDTO(null, "test", 1.1, 1, "test");
        when(medicacaoRepository.findById(1L)).thenReturn(Optional.of(medicacaoModel));
        when(medicacaoRepository.save(medicacaoModel)).thenReturn(medicacaoModel);
        when(mapper.map(medicacaoModel, MedicacaoDTO.class)).thenReturn(medicacaoDto);
        MedicacaoDTO medicacaoDtoUpdated = medicacaoService.atualizaMedicacao(1L, medicacaoDto);
        assertThat(medicacaoDtoUpdated).isInstanceOf(MedicacaoDTO.class).isNotNull();
        assertThat(medicacaoDtoUpdated.getMedicacao()).isEqualTo(medicacaoDto.getMedicacao());
        assertThat(medicacaoDtoUpdated.getDosagem()).isEqualTo(medicacaoDto.getDosagem());
        assertThat(medicacaoDtoUpdated.getQuantidadeDias()).isEqualTo(medicacaoDto.getQuantidadeDias());
        assertThat(medicacaoDtoUpdated.getObservacao()).isEqualTo(medicacaoDto.getObservacao());
    }

    @Test
    @DisplayName("deve lancar exception para medicacao nao encontrada na atualizacao")
    public void deveLancarExceptionParaMedicacaoNaoEncontradaNaAtualizacao() {
        MedicacaoDTO medicacaoDto = new MedicacaoDTO(null, "test", 1.1, 1, "test");
        when(medicacaoRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> medicacaoService.atualizaMedicacao(1L, medicacaoDto))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("medicação não encontrada");
    }

    @Test
    @DisplayName("deve excluir medicacao corretamente")
    public void deveExcluirMedicacaoCorretamente() {
        MedicacaoModel medicacaoModel = new MedicacaoModel(1L, "test", 1.1, 1, "test", null);
        when(medicacaoRepository.findById(1L)).thenReturn(Optional.of(medicacaoModel));
        doNothing().when(medicacaoRepository).deleteById(1L);
        medicacaoService.excluiMedicacao(1L);
        verify(medicacaoRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("deve lancar exception para medicacao nao encontrada na exclusao")
    public void deveLancarExceptionParaMedicacaoNaoEncontradaNaExclusao() {
        when(medicacaoRepository.findById(1L)).thenReturn(Optional.empty());
        doThrow(new NoSuchElementException("medicação não encontrada"))
                .when(medicacaoRepository)
                .deleteById(1L);
    }


}
