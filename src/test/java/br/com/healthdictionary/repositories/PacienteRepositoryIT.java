package br.com.healthdictionary.repositories;

import br.com.healthdictionary.models.PacienteModel;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase
public class PacienteRepositoryIT {

    @Autowired
    private PacienteRepository pacienteRepository;

    @AfterEach
    public void tearDown() {
        pacienteRepository.deleteAll();
    }

    @Test
    @DisplayName("deve registrar paciente com sucesso")
    public void deveRegistrarPacienteComSucesso() {
        PacienteModel pacienteModel = new PacienteModel("1", "nome",
                LocalDate.parse("1990-01-01"), "1", null);

        PacienteModel pacienteModelSaved = pacienteRepository.save(pacienteModel);
        assertThat(pacienteModelSaved).isInstanceOf(PacienteModel.class).isNotNull();
        assertThat(pacienteModelSaved.getCpf()).isEqualTo(pacienteModel.getCpf());
        assertThat(pacienteModelSaved.getNome()).isEqualTo(pacienteModel.getNome());
        assertThat(pacienteModelSaved.getDataNascimento()).isEqualTo(pacienteModel.getDataNascimento());
        assertThat(pacienteModelSaved.getCodigoSus()).isEqualTo(pacienteModel.getCodigoSus());
        assertThat(pacienteModelSaved.getOcorrencias()).isEqualTo(pacienteModel.getOcorrencias());
    }

    @Test
    @DisplayName("deve consultar psciente com sucesso")
    public void deveConsultarPacienteComSucesso() {
        PacienteModel pacienteModel = new PacienteModel("1", "nome",
                LocalDate.parse("1990-01-01"), "1", null);

        PacienteModel pacienteModelSaved = pacienteRepository.save(pacienteModel);
        Optional<PacienteModel> optionalPaciente = pacienteRepository.findById(pacienteModelSaved.getCpf());

        assertThat(optionalPaciente).isInstanceOf(Optional.class).isNotNull();
        if (optionalPaciente.isEmpty())
            throw new NoSuchElementException("paciente não encontrado");

        assertThat(optionalPaciente.get()).isInstanceOf(PacienteModel.class).isNotNull();
        assertThat(optionalPaciente.get().getCpf()).isEqualTo(pacienteModel.getCpf());
        assertThat(optionalPaciente.get().getNome()).isEqualTo(pacienteModel.getNome());
        assertThat(optionalPaciente.get().getDataNascimento()).isEqualTo(pacienteModel.getDataNascimento());
        assertThat(optionalPaciente.get().getCodigoSus()).isEqualTo(pacienteModel.getCodigoSus());
        assertThat(optionalPaciente.get().getOcorrencias()).isEqualTo(pacienteModel.getOcorrencias());
    }

    @Test
    @DisplayName("deve apagar paciente com sucesso")
    public void deveApagarPacienteComSucesso() {
        PacienteModel pacienteModel = new PacienteModel("1", "nome",
                LocalDate.parse("1990-01-01"), "1", null);

        PacienteModel pacienteModelSaved = pacienteRepository.save(pacienteModel);
        pacienteRepository.deleteById(pacienteModelSaved.getCpf());
        Optional<PacienteModel> optionalPaciente = pacienteRepository.findById(pacienteModelSaved.getCpf());
        assertThat(optionalPaciente).isInstanceOf(Optional.class).isEmpty();
    }

    @Test
    @DisplayName("deve listar pacientes com sucesso")
    public void deveListarPacientesSucesso() {
        PacienteModel pacienteModel1 = new PacienteModel("1", "nome",
                LocalDate.parse("1990-01-01"), "1", null);

        PacienteModel pacienteModel2 = new PacienteModel("2", "nome",
                LocalDate.parse("1990-01-01"), "1", null);

        pacienteRepository.saveAll(List.of(pacienteModel1, pacienteModel2));
        List<PacienteModel> pacientesReturned = pacienteRepository.findAll();
        assertThat(pacientesReturned).isNotNull().hasSize(2);
    }

}
