package br.com.healthdictionary.services;

import br.com.healthdictionary.dto.CidDTO;
import br.com.healthdictionary.dto.MedicacaoDTO;
import br.com.healthdictionary.dto.TratamentoDTO;
import br.com.healthdictionary.models.TratamentoModel;
import br.com.healthdictionary.repositories.TratamentoRepository;
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

public class TratamentoServiceTest {

    @InjectMocks
    private TratamentoService tratamentoService;

    @Mock
    private TratamentoRepository tratamentoRepository;

    @Mock
    private CidService cidService;

    @Mock
    private MedicacaoService medicacaoService;

    @Mock
    private ModelMapper mapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("deve recuperar tratamento corretamente")
    public void deveRecuperarTratamentoCorretamente() {
        TratamentoModel tratamentoModel = new TratamentoModel(1L, "test", "test", null, null);
        MedicacaoDTO medicacaoDTO = new MedicacaoDTO(1L, "test", 1.1, 1, "test");
        TratamentoDTO tratamentoDto = new TratamentoDTO(1L, "test", "test", List.of(medicacaoDTO));
        when(tratamentoRepository.findById(1L)).thenReturn(Optional.of(tratamentoModel));
        when(medicacaoService.recuperaMedicacoesPeloTratamentoId(1L)).thenReturn(List.of(medicacaoDTO));
        when(mapper.map(tratamentoModel, TratamentoDTO.class)).thenReturn(tratamentoDto);
        TratamentoDTO tratamentoDtoReturned = tratamentoService.recuperaTratamentoPeloId(1L);
        assertThat(tratamentoDtoReturned).isInstanceOf(TratamentoDTO.class).isNotNull();
        assertThat(tratamentoDtoReturned.getTratamento()).isEqualTo(tratamentoModel.getTratamento());
        assertThat(tratamentoDtoReturned.getReferenciaBibliografica()).isEqualTo(tratamentoModel.getReferenciaBibliografica());
    }

    @Test
    @DisplayName("deve lancar exception para tratamento que nao existe na consulta")
    public void deveLancarExceptionParaTratamentoQueNaoExisteNaConsulta() {
        when(tratamentoRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> tratamentoService.recuperaTratamentoPeloId(1L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("tratamento não encontrado");
    }

    @Test
    @DisplayName("deve cadastrar tratamento corretamente")
    public void deveCadastrarTratamentoCorretamente() {
        CidDTO cidDto = new CidDTO("1", "test");
        TratamentoDTO tratamentoDto = new TratamentoDTO(1L, "test", "test", List.of());
        TratamentoModel tratamentoModel = new TratamentoModel(1L, "test", "test", null, null);
        when(cidService.recuperaCidPeloCodigo("1")).thenReturn(cidDto);
        when(mapper.map(tratamentoDto, TratamentoModel.class)).thenReturn(tratamentoModel);
        when(tratamentoRepository.save(tratamentoModel)).thenReturn(tratamentoModel);
        when(mapper.map(tratamentoModel, TratamentoDTO.class)).thenReturn(tratamentoDto);
        TratamentoDTO tratamentoDtoSaved = tratamentoService.cadastraTratamento("1", tratamentoDto);
        assertThat(tratamentoDtoSaved).isInstanceOf(TratamentoDTO.class).isNotNull();
        assertThat(tratamentoDtoSaved.getTratamento()).isEqualTo(tratamentoModel.getTratamento());
        assertThat(tratamentoDtoSaved.getReferenciaBibliografica()).isEqualTo(tratamentoModel.getReferenciaBibliografica());
    }

    @Test
    @DisplayName("deve lancar exception para codigo de cid que nao existe")
    public void deveLancarExceptionParaCodigoDeCidQueNaoExiste() {
        TratamentoDTO tratamentoDto = new TratamentoDTO(1L, "test", "test", List.of());
        when(cidService.recuperaCidPeloCodigo("1")).thenThrow(new NoSuchElementException("cid não encontrado"));
        assertThatThrownBy(() -> tratamentoService.cadastraTratamento("1", tratamentoDto))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("cid não encontrado");
    }

    @Test
    @DisplayName("deve atualizar tratamento corretamente")
    public void deveAtualizarTratamentoCorretamente() {
        TratamentoDTO tratamentoDto = new TratamentoDTO(1L, "test", "test", List.of());
        TratamentoModel tratamentoModel = new TratamentoModel(1L, "test", "test", null, null);
        when(tratamentoRepository.findById(1L)).thenReturn(Optional.of(tratamentoModel));
        when(tratamentoRepository.save(tratamentoModel)).thenReturn(tratamentoModel);
        when(mapper.map(tratamentoModel, TratamentoDTO.class)).thenReturn(tratamentoDto);
        TratamentoDTO tratamentoDtoSaved = tratamentoService.atualizaTratamento(1L, tratamentoDto);
        assertThat(tratamentoDtoSaved).isInstanceOf(TratamentoDTO.class).isNotNull();
        assertThat(tratamentoDtoSaved.getTratamento()).isEqualTo(tratamentoModel.getTratamento());
        assertThat(tratamentoDtoSaved.getReferenciaBibliografica()).isEqualTo(tratamentoModel.getReferenciaBibliografica());
    }

    @Test
    @DisplayName("deve lancar exception para tratamento que nao existe na atualizacao")
    public void deveLancarExceptionParaTratamentoQueNaoExisteNaAtualizacao() {
        TratamentoDTO tratamentoDto = new TratamentoDTO(1L, "test", "test", List.of());
        when(tratamentoRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> tratamentoService.atualizaTratamento(1L, tratamentoDto))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("tratamento não encontrado");
    }

    @Test
    @DisplayName("deve adicionar medicacao em tratamento corretamente")
    public void deveAdicionarMedicacaoEmTratamentoCorretamente() {
        TratamentoModel tratamentoModel = new TratamentoModel(1L, "test", "test", null, null);
        MedicacaoDTO medicacaoDto = new MedicacaoDTO(null, "test", 1.1, 1, "test");
        when(tratamentoRepository.findById(1L)).thenReturn(Optional.of(tratamentoModel));
        when(medicacaoService.cadastraMedicacao(1L, medicacaoDto)).thenReturn(medicacaoDto);
        MedicacaoDTO medicacaoDtoSaved = tratamentoService.adicionaMedicacaoNoTratamento(1L, medicacaoDto);
        assertThat(medicacaoDtoSaved).isInstanceOf(MedicacaoDTO.class).isNotNull();
        assertThat(medicacaoDtoSaved.getMedicacao()).isEqualTo(medicacaoDto.getMedicacao());
        assertThat(medicacaoDtoSaved.getDosagem()).isEqualTo(medicacaoDto.getDosagem());
        assertThat(medicacaoDtoSaved.getQuantidadeDias()).isEqualTo(medicacaoDto.getQuantidadeDias());
        assertThat(medicacaoDtoSaved.getObservacao()).isEqualTo(medicacaoDto.getObservacao());
    }

    @Test
    @DisplayName("deve lancar exception para tratamento que nao existe ao adicionar medicacao")
    public void deveLancarExceptionParaTratamentoQueNaoExisteAoAdicionarMedicacao() {
        MedicacaoDTO medicacaoDto = new MedicacaoDTO(null, "test", 1.1, 1, "test");
        when(tratamentoRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> tratamentoService.adicionaMedicacaoNoTratamento(1L, medicacaoDto))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("tratamento não encontrado");
    }

    @Test
    @DisplayName("deve excluir medicacao corretamente")
    public void deveExcluirMedicacaoCorretamente() {
        doNothing().when(medicacaoService).excluiMedicacao(1L);
        tratamentoService.excluiMedicacaoNoTratamento(1L);
        verify(medicacaoService, times(1)).excluiMedicacao(1L);
    }

    @Test
    @DisplayName("deve lancar exception para medicacao que nao existe")
    public void deveLancarExceptionParaMedicacaoQueNaoExiste() {
        doThrow(new NoSuchElementException("medicação não encontrada")).when(medicacaoService).excluiMedicacao(1L);
        assertThatThrownBy(() -> tratamentoService.excluiMedicacaoNoTratamento(1L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("medicação não encontrada");
    }

    @Test
    @DisplayName("deve recuperar tratamentos por codigo do cid corretamente")
    public void deveRecuperarTratamentosPorCodigoDoCidCorretamente() {
        TratamentoModel tratamentoModel = new TratamentoModel(1L, "test", "test", null, null);
        TratamentoDTO tratamentoDto = new TratamentoDTO(1L, "test", "test", null);
        when(tratamentoRepository.findAllByCidCodigo("1")).thenReturn(List.of(tratamentoModel));
        when(mapper.map(tratamentoModel, TratamentoDTO.class)).thenReturn(tratamentoDto);
        List<TratamentoDTO> tratamentosDto = tratamentoService.recuperaTratamentosPeloCodigoCid("1");
        assertThat(tratamentosDto).isInstanceOf(List.class).isNotNull().hasSize(1);
    }

}
