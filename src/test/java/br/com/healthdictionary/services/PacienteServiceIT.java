package br.com.healthdictionary.services;

import br.com.healthdictionary.dto.PacienteDTO;
import br.com.healthdictionary.models.PacienteModel;
import br.com.healthdictionary.repositories.PacienteRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;

import java.time.LocalDate;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase
public class PacienteServiceIT {

    @Autowired
    private PacienteService pacienteService;

    @Autowired
    private PacienteRepository pacienteRepository;

    private PacienteModel pacienteModel;

    @BeforeEach
    public void setup() {
        pacienteModel = pacienteRepository.save(new PacienteModel("1", "test", LocalDate.parse("1990-01-01"), "1", null));
    }

    @AfterEach
    public void tearDown() {
        pacienteRepository.deleteAll();
    }

    @Test
    @DisplayName("deve recuperar paciente corretamente")
    public void deveRecuperarPacienteCorretamente() {
        PacienteDTO pacienteDto = pacienteService.recuperaPacientePeloCpf(pacienteModel.getCpf());
        assertThat(pacienteDto).isInstanceOf(PacienteDTO.class).isNotNull();
        assertThat(pacienteDto.getNome()).isEqualTo(pacienteModel.getNome());
        assertThat(pacienteDto.getDataNascimento()).isEqualTo(pacienteModel.getDataNascimento());
        assertThat(pacienteDto.getCodigoSus()).isEqualTo(pacienteModel.getCodigoSus());
    }

    @Test
    @DisplayName("deve lancar exception para paciente nao encontrado na consulta")
    public void deveLancarExceptionParaPacienteNaoEncontradoNaConsulta() {
        assertThatThrownBy(() -> pacienteService.recuperaPacientePeloCpf("0"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("paciente não encontrado");
    }

    @Test
    @DisplayName("deve cadastrar paciente corretamente")
    public void deveCadastrarPacienteCorretamente() {
        PacienteDTO pacienteDto = new PacienteDTO("2", "test", LocalDate.parse("1990-01-01"), "1");
        PacienteDTO pacienteDtoSaved = pacienteService.cadastraPaciente(pacienteDto);
        assertThat(pacienteDtoSaved).isInstanceOf(PacienteDTO.class).isNotNull();
        assertThat(pacienteDtoSaved.getNome()).isEqualTo(pacienteDto.getNome());
        assertThat(pacienteDtoSaved.getDataNascimento()).isEqualTo(pacienteDto.getDataNascimento());
        assertThat(pacienteDtoSaved.getCodigoSus()).isEqualTo(pacienteDto.getCodigoSus());
    }

    @Test
    @DisplayName("deve lancar exception para paciente ja cadastrado")
    public void deveLancarExceptionParaPacienteJaCadastrado() {
        PacienteDTO pacienteDto = new PacienteDTO("1", "test", LocalDate.parse("1990-01-01"), "1");
        assertThatThrownBy(() -> pacienteService.cadastraPaciente(pacienteDto))
                .isInstanceOf(DuplicateKeyException.class)
                .hasMessage("paciente já existe");
    }

    @Test
    @DisplayName("deve atualizar ocorrencia corretamente")
    public void deveAtualizarOcorrenciaCorretamente() {
        PacienteDTO pacienteDto = new PacienteDTO("1", "test updated", LocalDate.parse("1990-01-01"), "1");
        PacienteDTO pacienteDtoUpdated = pacienteService.atualizaPaciente(pacienteDto.getCpf(), pacienteDto);
        assertThat(pacienteDtoUpdated).isInstanceOf(PacienteDTO.class).isNotNull();
        assertThat(pacienteDtoUpdated.getNome()).isEqualTo(pacienteDto.getNome());
    }

    @Test
    @DisplayName("deve lancar exception para paciente nao encontrado")
    public void deveLancarExceptionParaPacienteNaoEncontrado() {
        PacienteDTO pacienteDto = new PacienteDTO("2", "test updated", LocalDate.parse("1990-01-01"), "1");
        assertThatThrownBy(() -> pacienteService.atualizaPaciente("2", pacienteDto))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("paciente não encontrado");
    }

}
