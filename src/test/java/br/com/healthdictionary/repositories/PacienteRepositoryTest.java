package br.com.healthdictionary.repositories;

import br.com.healthdictionary.models.PacienteModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class PacienteRepositoryTest {

    @Mock
    private PacienteRepository pacienteRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("deve registrar paciente com sucesso")
    public void deveRegistrarPacienteComSucesso() {
        PacienteModel pacienteModel = new PacienteModel("1", "nome",
                LocalDate.parse("1990-01-01"), "1", null);

        when(pacienteRepository.save(pacienteModel)).thenReturn(pacienteModel);
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

        when(pacienteRepository.findById("1")).thenReturn(Optional.of(pacienteModel));
        Optional<PacienteModel> optionalPaciente = pacienteRepository.findById("1");

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
        String cpf = "1";
        doNothing().when(pacienteRepository).deleteById(cpf);
        pacienteRepository.deleteById(cpf);
        verify(pacienteRepository, times(1)).deleteById(cpf);
    }

    @Test
    @DisplayName("deve listar pacientes com sucesso")
    public void deveListarPacientesSucesso() {
        PacienteModel pacienteModel1 = new PacienteModel("1", "nome",
                LocalDate.parse("1990-01-01"), "1", null);

        PacienteModel pacienteModel2 = new PacienteModel("2", "nome",
                LocalDate.parse("1990-01-01"), "1", null);


        List<PacienteModel> pacientes = List.of(pacienteModel1, pacienteModel2);
        when(pacienteRepository.findAll()).thenReturn(pacientes);
        List<PacienteModel> paicentesReturned = pacienteRepository.findAll();

        assertThat(paicentesReturned).isNotNull().hasSize(2)
                .containsExactlyInAnyOrder(pacienteModel1, pacienteModel2);
    }


}
