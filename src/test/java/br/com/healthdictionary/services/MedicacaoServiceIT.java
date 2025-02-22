package br.com.healthdictionary.services;

import br.com.healthdictionary.dto.MedicacaoDTO;
import br.com.healthdictionary.models.CidModel;
import br.com.healthdictionary.models.MedicacaoModel;
import br.com.healthdictionary.models.TratamentoModel;
import br.com.healthdictionary.repositories.CidRepository;
import br.com.healthdictionary.repositories.MedicacaoRepository;
import br.com.healthdictionary.repositories.TratamentoRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase
public class MedicacaoServiceIT {

    @Autowired
    private MedicacaoService medicacaoService;

    @Autowired
    private MedicacaoRepository medicacaoRepository;

    @Autowired
    private TratamentoRepository tratamentoRepository;

    @Autowired
    private CidRepository cidRepository;

    private MedicacaoModel medicacaoModel;
    private TratamentoModel tratamentoModel;

    @BeforeEach
    public void setup() {
        CidModel cidModel = cidRepository.save(new CidModel("1", "test", null));
        tratamentoModel = tratamentoRepository.save(new TratamentoModel(null, "test", "test", cidModel, null));
        medicacaoModel = medicacaoRepository.save(new MedicacaoModel(null, "test", 1.1, 1, "test", tratamentoModel));
    }

    @AfterEach
    public void tearDown() {
        medicacaoRepository.deleteAll();
        tratamentoRepository.deleteAll();
        cidRepository.deleteAll();
    }

    @Test
    @DisplayName("deve recuperar medicacao corretamente")
    public void deveRecuperarMedicacaoCorretamente() {
        List<MedicacaoDTO> medicacoesDto = medicacaoService.recuperaMedicacoesPeloTratamentoId(tratamentoModel.getId());
        assertThat(medicacoesDto).isInstanceOf(List.class).hasSize(1);
    }

    @Test
    @DisplayName("deve cadastrar medicacao corretamente")
    public void deveCadastrarCidCorretamente() {
        MedicacaoDTO medicacaoDto = new MedicacaoDTO(null, "test", 1.1, 1, "test");
        MedicacaoDTO medicacaoDtoSaved = medicacaoService.cadastraMedicacao(tratamentoModel.getId(), medicacaoDto);
        assertThat(medicacaoDtoSaved).isInstanceOf(MedicacaoDTO.class).isNotNull();
        assertThat(medicacaoDtoSaved.getId()).isNotNegative();
        assertThat(medicacaoDtoSaved.getMedicacao()).isEqualTo(medicacaoDto.getMedicacao());
        assertThat(medicacaoDtoSaved.getDosagem()).isEqualTo(medicacaoDto.getDosagem());
        assertThat(medicacaoDtoSaved.getQuantidadeDias()).isEqualTo(medicacaoDto.getQuantidadeDias());
        assertThat(medicacaoDtoSaved.getObservacao()).isEqualTo(medicacaoDto.getObservacao());
    }

    @Test
    @DisplayName("deve lancar exception para tratamento nao encontrado")
    public void deveLancarExceptionParaTratamentoNaoEncontrado() {
        MedicacaoDTO medicacaoDto = new MedicacaoDTO(null, "test", 1.1, 1, "test");
        assertThatThrownBy(() -> medicacaoService.cadastraMedicacao(0L, medicacaoDto))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("tratamento não encontrado");
    }

    @Test
    @DisplayName("deve atualizar medicacao corretamente")
    public void deveAtualizarMedicacaoCorretamente() {
        MedicacaoDTO medicacaoDto = new MedicacaoDTO(null, "test updated", 1.1, 1, "test");
        MedicacaoDTO medicacaoDtoSaved = medicacaoService.atualizaMedicacao(medicacaoModel.getId(), medicacaoDto);
        assertThat(medicacaoDtoSaved).isInstanceOf(MedicacaoDTO.class).isNotNull();
        assertThat(medicacaoDtoSaved.getId()).isEqualTo(medicacaoModel.getId());
        assertThat(medicacaoDtoSaved.getMedicacao()).isEqualTo(medicacaoDto.getMedicacao());
        assertThat(medicacaoDtoSaved.getDosagem()).isEqualTo(medicacaoDto.getDosagem());
        assertThat(medicacaoDtoSaved.getQuantidadeDias()).isEqualTo(medicacaoDto.getQuantidadeDias());
        assertThat(medicacaoDtoSaved.getObservacao()).isEqualTo(medicacaoDto.getObservacao());
    }

    @Test
    @DisplayName("deve lancar exception para medicacao nao encontrada na atualizacao")
    public void deveLancarExceptionParaMedicacaoNaoEncontradaNaAtualizacao() {
        MedicacaoDTO medicacaoDto = new MedicacaoDTO(null, "test updated", 1.1, 1, "test");
        assertThatThrownBy(() -> medicacaoService.atualizaMedicacao(0L, medicacaoDto))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("medicação não encontrada");
    }

    @Test
    @DisplayName("deve excluir medicacao corretamente")
    public void deveExcluirMedicacaoCorretamente() {
        medicacaoService.excluiMedicacao(medicacaoModel.getId());
    }

    @Test
    @DisplayName("deve lancar exception para medicacao nao encontrada na exclusao")
    public void deveLancarExceptionParaMedicacaoNaoEncontradaNaExclusao() {
        assertThatThrownBy(() -> medicacaoService.excluiMedicacao(0L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("medicação não encontrada");
    }

}
