package br.com.healthdictionary.services;

import br.com.healthdictionary.dto.PacienteDTO;
import br.com.healthdictionary.models.PacienteModel;
import br.com.healthdictionary.repositories.PacienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DuplicateKeyException;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

public class PacienteServiceTest {

    @InjectMocks
    private PacienteService pacienteService;

    @Mock
    private PacienteRepository pacienteRepository;

    @Mock
    private ModelMapper mapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("deve recuperar paciente corretamente")
    public void deveRecuperarPacienteCorretamente() {
        PacienteModel pacienteModel = new PacienteModel("1", "test", LocalDate.parse("1990-01-01"), "1", null);
        PacienteDTO pacienteDto = new PacienteDTO("1", "test", LocalDate.parse("1990-01-01"), "1");
        when(pacienteRepository.findById("1")).thenReturn(Optional.of(pacienteModel));
        when(mapper.map(pacienteModel, PacienteDTO.class)).thenReturn(pacienteDto);
        PacienteDTO pacienteDtoReturned = pacienteService.recuperaPacientePeloCpf("1");
        assertThat(pacienteDtoReturned).isInstanceOf(PacienteDTO.class).isNotNull();
        assertThat(pacienteDtoReturned.getNome()).isEqualTo(pacienteModel.getNome());
        assertThat(pacienteDtoReturned.getDataNascimento()).isEqualTo(pacienteModel.getDataNascimento());
        assertThat(pacienteDtoReturned.getCodigoSus()).isEqualTo(pacienteModel.getCodigoSus());
    }

    @Test
    @DisplayName("deve lancar exception para paciente nao encontrado na consulta")
    public void deveLancarExceptionParaPacienteNaoEncontradoNaConsulta() {
        when(pacienteRepository.findById("0")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> pacienteService.recuperaPacientePeloCpf("0"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("paciente não encontrado");
    }

    @Test
    @DisplayName("deve cadastrar paciente corretamente")
    public void deveCadastrarPacienteCorretamente() {
        PacienteModel pacienteModel = new PacienteModel("1", "test", LocalDate.parse("1990-01-01"), "1", null);
        PacienteDTO pacienteDto = new PacienteDTO("1", "test", LocalDate.parse("1990-01-01"), "1");
        when(pacienteRepository.findById("1")).thenReturn(Optional.empty());
        when(pacienteRepository.save(pacienteModel)).thenReturn(pacienteModel);
        when(mapper.map(pacienteDto, PacienteModel.class)).thenReturn(pacienteModel);
        when(mapper.map(pacienteModel, PacienteDTO.class)).thenReturn(pacienteDto);
        PacienteDTO pacienteDtoSaved = pacienteService.cadastraPaciente(pacienteDto);
        assertThat(pacienteDtoSaved).isInstanceOf(PacienteDTO.class).isNotNull();
        assertThat(pacienteDtoSaved.getNome()).isEqualTo(pacienteDto.getNome());
        assertThat(pacienteDtoSaved.getDataNascimento()).isEqualTo(pacienteDto.getDataNascimento());
        assertThat(pacienteDtoSaved.getCodigoSus()).isEqualTo(pacienteDto.getCodigoSus());
    }

    @Test
    @DisplayName("deve lancar exception para paciente ja cadastrado")
    public void deveLancarExceptionParaPacienteJaCadastrado() {
        PacienteModel pacienteModel = new PacienteModel("1", "test", LocalDate.parse("1990-01-01"), "1", null);
        PacienteDTO pacienteDto = new PacienteDTO("1", "test", LocalDate.parse("1990-01-01"), "1");
        when(pacienteRepository.findById("1")).thenReturn(Optional.of(pacienteModel));
        assertThatThrownBy(() -> pacienteService.cadastraPaciente(pacienteDto))
                .isInstanceOf(DuplicateKeyException.class)
                .hasMessage("paciente já existe");
    }

    @Test
    @DisplayName("deve atualizar ocorrencia corretamente")
    public void deveAtualizarOcorrenciaCorretamente() {
        PacienteModel pacienteModel = new PacienteModel("1", "test", LocalDate.parse("1990-01-01"), "1", null);
        PacienteDTO pacienteDto = new PacienteDTO("1", "test", LocalDate.parse("1990-01-01"), "1");
        when(pacienteRepository.findById("1")).thenReturn(Optional.of(pacienteModel));
        when(pacienteRepository.save(pacienteModel)).thenReturn(pacienteModel);
        when(mapper.map(pacienteModel, PacienteDTO.class)).thenReturn(pacienteDto);
        PacienteDTO pacienteDtoUpdated = pacienteService.atualizaPaciente(pacienteDto.getCpf(), pacienteDto);
        assertThat(pacienteDtoUpdated).isInstanceOf(PacienteDTO.class).isNotNull();
        assertThat(pacienteDtoUpdated.getNome()).isEqualTo(pacienteDto.getNome());
    }

    @Test
    @DisplayName("deve lancar exception para paciente nao encontrado")
    public void deveLancarExceptionParaPacienteNaoEncontrado() {
        when(pacienteRepository.findById("1")).thenReturn(Optional.empty());
        PacienteDTO pacienteDto = new PacienteDTO("1", "test updated", LocalDate.parse("1990-01-01"), "1");
        assertThatThrownBy(() -> pacienteService.atualizaPaciente("1", pacienteDto))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("paciente não encontrado");
    }

}
