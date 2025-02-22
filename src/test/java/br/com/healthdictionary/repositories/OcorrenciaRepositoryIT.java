package br.com.healthdictionary.repositories;

import br.com.healthdictionary.models.OcorrenciaModel;
import br.com.healthdictionary.models.PacienteModel;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase
public class OcorrenciaRepositoryIT {

    @Autowired
    private OcorrenciaRepository ocorrenciaRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    private PacienteModel pacienteModel;

    @BeforeEach
    public void setup() {
        pacienteModel = pacienteRepository.save(new PacienteModel("1", "test", LocalDate.parse("2000-01-01"), "1", null));
    }

    @AfterEach
    public void tearDown() {
        ocorrenciaRepository.deleteAll();
        pacienteRepository.deleteAll();
    }

    @Test
    @DisplayName("deve registrar ocorrencia com sucesso")
    public void deveRegistrarOcorrenciaComSucesso() {
        OcorrenciaModel ocorrenciaModel = new OcorrenciaModel(null, "test", "test",
                LocalDateTime.now(), pacienteModel);

        OcorrenciaModel ocorrenciaModelSaved = ocorrenciaRepository.save(ocorrenciaModel);
        assertThat(ocorrenciaModelSaved).isInstanceOf(OcorrenciaModel.class).isNotNull();
        assertThat(ocorrenciaModelSaved.getId()).isNotNegative();
        assertThat(ocorrenciaModelSaved.getSintomas()).isEqualTo(ocorrenciaModel.getDiagnostico());
        assertThat(ocorrenciaModelSaved.getRegistro()).isEqualTo(ocorrenciaModel.getRegistro());
    }

    @Test
    @DisplayName("deve consulta ocorrencia com sucesso")
    public void deveConsultarOcorrenciaComSucesso() {
        OcorrenciaModel ocorrenciaModel = new OcorrenciaModel(null, "test", "test",
                LocalDateTime.now(), pacienteModel);

        OcorrenciaModel ocorrenciaModelSaved = ocorrenciaRepository.save(ocorrenciaModel);
        Optional<OcorrenciaModel> optionalOcorrencia = ocorrenciaRepository.findById(ocorrenciaModelSaved.getId());
        assertThat(optionalOcorrencia).isInstanceOf(Optional.class).isNotNull();
        if (optionalOcorrencia.isEmpty())
            throw new NoSuchElementException("ocorrência não encontrada");

        assertThat(optionalOcorrencia.get()).isInstanceOf(OcorrenciaModel.class).isNotNull();
        assertThat(optionalOcorrencia.get().getId()).isEqualTo(ocorrenciaModelSaved.getId());
        assertThat(optionalOcorrencia.get().getSintomas()).isEqualTo(ocorrenciaModelSaved.getDiagnostico());
        assertThat(optionalOcorrencia.get().getRegistro()).isEqualTo(ocorrenciaModelSaved.getRegistro());
    }

    @Test
    @DisplayName("deve apagar ocorrencia com sucesso")
    public void deveApagarOcorrenciaComSucesso() {
        OcorrenciaModel ocorrenciaModel = new OcorrenciaModel(null, "test", "test",
                LocalDateTime.now(), pacienteModel);

        OcorrenciaModel ocorrenciaModelSaved = ocorrenciaRepository.save(ocorrenciaModel);
        ocorrenciaRepository.deleteById(ocorrenciaModelSaved.getId());
        Optional<OcorrenciaModel> optionalOcorrencia = ocorrenciaRepository.findById(ocorrenciaModelSaved.getId());
        assertThat(optionalOcorrencia).isInstanceOf(Optional.class).isEmpty();
    }

    @Test
    @DisplayName("deve listar ocorrencias com sucesso")
    public void deveListarCidsOcorrenciaSucesso() {
        OcorrenciaModel ocorrenciaModel1 = new OcorrenciaModel(null, "test", "test",
                LocalDateTime.now(), pacienteModel);

        OcorrenciaModel ocorrenciaModel2 = new OcorrenciaModel(null, "test", "test",
                LocalDateTime.now(), pacienteModel);

        ocorrenciaRepository.saveAll(List.of(ocorrenciaModel1, ocorrenciaModel2));
        List<OcorrenciaModel> ocorrenciasReturned = ocorrenciaRepository.findAll();
        assertThat(ocorrenciasReturned).isNotNull().hasSize(2);
    }

    @Test
    @DisplayName("deve listar ocorrencias por cpf com sucesso")
    public void deveListarCidsOcorrenciasPorCpfComSucesso() {
        OcorrenciaModel ocorrenciaModel1 = new OcorrenciaModel(null, "test", "test",
                LocalDateTime.now(), pacienteModel);

        OcorrenciaModel ocorrenciaModel2 = new OcorrenciaModel(null, "test", "test",
                LocalDateTime.now(), pacienteModel);

        ocorrenciaRepository.saveAll(List.of(ocorrenciaModel1, ocorrenciaModel2));
        List<OcorrenciaModel> ocorrenciasReturned = ocorrenciaRepository.findAllByPacienteCpf(pacienteModel.getCpf());
        assertThat(ocorrenciasReturned).isNotNull().hasSize(2);
    }


}
