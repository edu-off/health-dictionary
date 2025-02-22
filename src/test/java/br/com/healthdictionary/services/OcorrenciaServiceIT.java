package br.com.healthdictionary.services;

import br.com.healthdictionary.dto.OcorrenciaDTO;
import br.com.healthdictionary.models.OcorrenciaModel;
import br.com.healthdictionary.models.PacienteModel;
import br.com.healthdictionary.repositories.OcorrenciaRepository;
import br.com.healthdictionary.repositories.PacienteRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.InstanceOfAssertFactories.LIST;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase
public class OcorrenciaServiceIT {

    @Autowired
    private OcorrenciaService ocorrenciaService;

    @Autowired
    private OcorrenciaRepository ocorrenciaRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    private OcorrenciaModel ocorrenciaModel;

    @BeforeEach
    public void setup() {
        PacienteModel pacienteModel = pacienteRepository.save(new PacienteModel("1", "test", LocalDate.parse("1990-01-01"), "1", null));
        ocorrenciaModel = ocorrenciaRepository.save(new OcorrenciaModel(null, "test", "test", LocalDateTime.now(), pacienteModel));
    }

    @AfterEach
    public void tearDown() {
        ocorrenciaRepository.deleteAll();
        pacienteRepository.deleteAll();
    }

    @Test
    @DisplayName("deve recuperar ocorrencia corretamente")
    public void deveRecuperarOcorrenciaCorretamente() {
        OcorrenciaDTO ocorrenciaDtoReturned = ocorrenciaService.recuperaOcorrenciaPeloId(ocorrenciaModel.getId());
        assertThat(ocorrenciaDtoReturned).isInstanceOf(OcorrenciaDTO.class).isNotNull();
        assertThat(ocorrenciaDtoReturned.getId()).isEqualTo(ocorrenciaModel.getId());
        assertThat(ocorrenciaDtoReturned.getSintomas()).isEqualTo(ocorrenciaModel.getSintomas());
        assertThat(ocorrenciaDtoReturned.getDiagnostico()).isEqualTo(ocorrenciaModel.getDiagnostico());
        assertThat(ocorrenciaDtoReturned.getRegistro()).isInstanceOf(LocalDateTime.class).isNotNull();
    }

    @Test
    @DisplayName("deve lancar exception para ocorrencia nao encontrada na consulta")
    public void deveLancarExceptionParaOcorrenciaNaoEncontradaNaConsulta() {
        assertThatThrownBy(() -> ocorrenciaService.recuperaOcorrenciaPeloId(0L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("ocorrência não encontrada");
    }

    @Test
    @DisplayName("deve cadastrar ocorrencia corretamente")
    public void deveCadastrarOcorrenciaCorretamente() {
        OcorrenciaDTO ocorrenciaDto = new OcorrenciaDTO(null, "test", "test", null);
        OcorrenciaDTO ocorrenciaDtoSaved = ocorrenciaService.cadastraOcorrencia("1", ocorrenciaDto);
        OcorrenciaDTO ocorrenciaDtoReturned = ocorrenciaService.recuperaOcorrenciaPeloId(ocorrenciaDtoSaved.getId());
        assertThat(ocorrenciaDtoReturned).isInstanceOf(OcorrenciaDTO.class).isNotNull();
        assertThat(ocorrenciaDtoReturned.getId()).isEqualTo(ocorrenciaDtoSaved.getId());
        assertThat(ocorrenciaDtoReturned.getSintomas()).isEqualTo(ocorrenciaDto.getSintomas());
        assertThat(ocorrenciaDtoReturned.getDiagnostico()).isEqualTo(ocorrenciaDto.getDiagnostico());
        assertThat(ocorrenciaDtoReturned.getRegistro()).isInstanceOf(LocalDateTime.class).isNotNull();
    }

    @Test
    @DisplayName("deve lancar exception para paciente nao encontrado")
    public void deveLancarExceptionParaPacienteNaoEncontrado() {
        OcorrenciaDTO ocorrenciaDto = new OcorrenciaDTO(null, "test", "test", null);
        assertThatThrownBy(() -> ocorrenciaService.cadastraOcorrencia("0", ocorrenciaDto))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("paciente não encontrado");
    }

    @Test
    @DisplayName("deve atualizar ocorrencia corretamente")
    public void deveAtualizarOcorrenciaCorretamente() {
        OcorrenciaDTO ocorrenciaDto = new OcorrenciaDTO(null, "test updated", "test", null);
        OcorrenciaDTO ocorrenciaDtoSaved = ocorrenciaService.atualizaOcorrencia(ocorrenciaModel.getId(), ocorrenciaDto);
        OcorrenciaDTO ocorrenciaDtoReturned = ocorrenciaService.recuperaOcorrenciaPeloId(ocorrenciaDtoSaved.getId());
        assertThat(ocorrenciaDtoReturned).isInstanceOf(OcorrenciaDTO.class).isNotNull();
        assertThat(ocorrenciaDtoReturned.getId()).isEqualTo(ocorrenciaDtoSaved.getId());
        assertThat(ocorrenciaDtoReturned.getSintomas()).isEqualTo(ocorrenciaDto.getSintomas());
        assertThat(ocorrenciaDtoReturned.getDiagnostico()).isEqualTo(ocorrenciaDto.getDiagnostico());
        assertThat(ocorrenciaDtoReturned.getRegistro()).isInstanceOf(LocalDateTime.class).isNotNull();
    }

    @Test
    @DisplayName("deve lancar exception para ocorrencia nao encontrada")
    public void deveLancarExceptionParaOcorrenciaNaoEncontradaNaAtualizacao() {
        OcorrenciaDTO ocorrenciaDto = new OcorrenciaDTO(null, "test", "test", null);
        assertThatThrownBy(() -> ocorrenciaService.atualizaOcorrencia(0L, ocorrenciaDto))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("ocorrência não encontrada");
    }

    @Test
    @DisplayName("deve recuperar ocorrencias por cpf corretamente")
    public void deveRecuperarOcorrenciasPorCpfCorretamente() {
        List<OcorrenciaDTO> ocorrenciasDto = ocorrenciaService.recuperaOcorrenciasPeloCpf("1");
        assertThat(ocorrenciasDto).isInstanceOf(List.class).isNotNull().hasSize(1);
    }

}
