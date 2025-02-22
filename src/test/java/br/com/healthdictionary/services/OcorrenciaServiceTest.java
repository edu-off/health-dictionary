package br.com.healthdictionary.services;

import br.com.healthdictionary.dto.OcorrenciaDTO;
import br.com.healthdictionary.dto.PacienteDTO;
import br.com.healthdictionary.models.OcorrenciaModel;
import br.com.healthdictionary.repositories.OcorrenciaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

public class OcorrenciaServiceTest {

    @InjectMocks
    private OcorrenciaService ocorrenciaService;

    @Mock
    private OcorrenciaRepository ocorrenciaRepository;

    @Mock
    private PacienteService pacienteService;

    @Mock
    private ModelMapper mapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("deve recuperar ocorrencia corretamente")
    public void deveRecuperarOcorrenciaCorretamente() {
        OcorrenciaModel ocorrenciaModel = new OcorrenciaModel(1L, "test", "test", LocalDateTime.now(), null);
        OcorrenciaDTO ocorrenciaDto = new OcorrenciaDTO(1L, "test", "test", LocalDateTime.now());
        when(ocorrenciaRepository.findById(1L)).thenReturn(Optional.of(ocorrenciaModel));
        when(mapper.map(ocorrenciaModel, OcorrenciaDTO.class)).thenReturn(ocorrenciaDto);
        OcorrenciaDTO ocorrenciaDTO = ocorrenciaService.recuperaOcorrenciaPeloId(1L);
        assertThat(ocorrenciaDTO).isInstanceOf(OcorrenciaDTO.class).isNotNull();
        assertThat(ocorrenciaDTO.getId()).isEqualTo(ocorrenciaModel.getId());
        assertThat(ocorrenciaDTO.getSintomas()).isEqualTo(ocorrenciaModel.getSintomas());
        assertThat(ocorrenciaDTO.getDiagnostico()).isEqualTo(ocorrenciaModel.getDiagnostico());
        assertThat(ocorrenciaDTO.getRegistro()).isInstanceOf(LocalDateTime.class).isNotNull();
    }

    @Test
    @DisplayName("deve lancar exception para ocorrencia nao encontrada na consulta")
    public void deveLancarExceptionParaOcorrenciaNaoEncontradaNaConsulta() {
        when(ocorrenciaRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> ocorrenciaService.recuperaOcorrenciaPeloId(1L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("ocorrência não encontrada");
    }

    @Test
    @DisplayName("deve cadastrar ocorrencia corretamente")
    public void deveCadastrarOcorrenciaCorretamente() {
        OcorrenciaModel ocorrenciaModel = new OcorrenciaModel(1L, "test", "test", LocalDateTime.now(), null);
        OcorrenciaDTO ocorrenciaDto = new OcorrenciaDTO(1L, "test", "test", LocalDateTime.now());
        PacienteDTO pacienteDto = new PacienteDTO("1", "test", LocalDate.parse("1990-01-01"), "1");
        when(pacienteService.recuperaPacientePeloCpf("1")).thenReturn(pacienteDto);
        when(ocorrenciaRepository.save(ocorrenciaModel)).thenReturn(ocorrenciaModel);
        when(mapper.map(ocorrenciaModel, OcorrenciaDTO.class)).thenReturn(ocorrenciaDto);
        when(mapper.map(ocorrenciaDto, OcorrenciaModel.class)).thenReturn(ocorrenciaModel);
        OcorrenciaDTO ocorrenciaDtoSaved = ocorrenciaService.cadastraOcorrencia("1", ocorrenciaDto);
        assertThat(ocorrenciaDtoSaved).isInstanceOf(OcorrenciaDTO.class).isNotNull();
        assertThat(ocorrenciaDtoSaved.getId()).isEqualTo(ocorrenciaDto.getId());
        assertThat(ocorrenciaDtoSaved.getSintomas()).isEqualTo(ocorrenciaDto.getSintomas());
        assertThat(ocorrenciaDtoSaved.getDiagnostico()).isEqualTo(ocorrenciaDto.getDiagnostico());
        assertThat(ocorrenciaDtoSaved.getRegistro()).isInstanceOf(LocalDateTime.class).isNotNull();
    }

    @Test
    @DisplayName("deve lancar exception para paciente nao encontrado")
    public void deveLancarExceptionParaPacienteNaoEncontrado() {
        OcorrenciaDTO ocorrenciaDto = new OcorrenciaDTO(1L, "test", "test", LocalDateTime.now());
        when(pacienteService.recuperaPacientePeloCpf("1")).thenThrow(new NoSuchElementException("paciente não encontrado"));
        assertThatThrownBy(() -> ocorrenciaService.cadastraOcorrencia("1", ocorrenciaDto))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("paciente não encontrado");
    }

    @Test
    @DisplayName("deve atualizar ocorrencia corretamente")
    public void deveAtualizarOcorrenciaCorretamente() {
        OcorrenciaModel ocorrenciaModel = new OcorrenciaModel(1L, "test", "test", LocalDateTime.now(), null);
        OcorrenciaDTO ocorrenciaDto = new OcorrenciaDTO(1L, "test", "test", LocalDateTime.now());
        PacienteDTO pacienteDto = new PacienteDTO("1", "test", LocalDate.parse("1990-01-01"), "1");
        when(pacienteService.recuperaPacientePeloCpf("1")).thenReturn(pacienteDto);
        when(ocorrenciaRepository.findById(1L)).thenReturn(Optional.of(ocorrenciaModel));
        when(ocorrenciaRepository.save(ocorrenciaModel)).thenReturn(ocorrenciaModel);
        when(mapper.map(ocorrenciaModel, OcorrenciaDTO.class)).thenReturn(ocorrenciaDto);
        OcorrenciaDTO ocorrenciaDtoUpdated = ocorrenciaService.atualizaOcorrencia(1L, ocorrenciaDto);
        assertThat(ocorrenciaDtoUpdated).isInstanceOf(OcorrenciaDTO.class).isNotNull();
        assertThat(ocorrenciaDtoUpdated.getId()).isEqualTo(ocorrenciaDto.getId());
        assertThat(ocorrenciaDtoUpdated.getSintomas()).isEqualTo(ocorrenciaDto.getSintomas());
        assertThat(ocorrenciaDtoUpdated.getDiagnostico()).isEqualTo(ocorrenciaDto.getDiagnostico());
        assertThat(ocorrenciaDtoUpdated.getRegistro()).isInstanceOf(LocalDateTime.class).isNotNull();
    }

    @Test
    @DisplayName("deve lancar exception para ocorrencia nao encontrada")
    public void deveLancarExceptionParaOcorrenciaNaoEncontradaNaAtualizacao() {
        OcorrenciaDTO ocorrenciaDto = new OcorrenciaDTO(1L, "test", "test", LocalDateTime.now());
        when(ocorrenciaRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> ocorrenciaService.atualizaOcorrencia(1L, ocorrenciaDto))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("ocorrência não encontrada");
    }

    @Test
    @DisplayName("deve recuperar ocorrencias por cpf corretamente")
    public void deveRecuperarOcorrenciasPorCpfCorretamente() {
        OcorrenciaModel ocorrenciaModel = new OcorrenciaModel(1L, "test", "test", LocalDateTime.now(), null);
        OcorrenciaDTO ocorrenciaDto = new OcorrenciaDTO(1L, "test", "test", LocalDateTime.now());
        when(ocorrenciaRepository.findAllByPacienteCpf("1")).thenReturn(List.of(ocorrenciaModel));
        when(mapper.map(ocorrenciaModel, OcorrenciaDTO.class)).thenReturn(ocorrenciaDto);
        List<OcorrenciaDTO> ocorrenciasDto = ocorrenciaService.recuperaOcorrenciasPeloCpf("1");
        assertThat(ocorrenciasDto).isInstanceOf(List.class).isNotNull().hasSize(1);
    }


}
