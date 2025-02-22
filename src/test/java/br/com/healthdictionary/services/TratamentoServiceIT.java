package br.com.healthdictionary.services;

import br.com.healthdictionary.dto.MedicacaoDTO;
import br.com.healthdictionary.dto.TratamentoDTO;
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
public class TratamentoServiceIT {

    @Autowired
    private TratamentoService tratamentoService;

    @Autowired
    private TratamentoRepository tratamentoRepository;

    @Autowired
    private CidRepository cidRepository;

    @Autowired
    private MedicacaoRepository medicacaoRepository;

    private TratamentoModel tratamentoModel;
    private MedicacaoModel medicacaoModel1;
    private MedicacaoModel medicacaoModel2;

    @BeforeEach
    public void setup() {
        CidModel cidModel = cidRepository.save(new CidModel("1", "test", null));
        tratamentoModel = tratamentoRepository.save(new TratamentoModel(null, "test", "test", cidModel, null));
        medicacaoModel1 = medicacaoRepository.save(new MedicacaoModel(null, "test", 1.1, 1, "test", tratamentoModel));
        medicacaoModel2 = medicacaoRepository.save(new MedicacaoModel(null, "test", 1.1, 1, "test", tratamentoModel));
    }

    @AfterEach
    public void tearDown() {
        medicacaoRepository.deleteAll();
        tratamentoRepository.deleteAll();
        cidRepository.deleteAll();
    }

    @Test
    @DisplayName("deve recuperar tratamento corretamente")
    public void deveRecuperarTratamentoCorretamente() {
        TratamentoDTO tratamentoDTO = tratamentoService.recuperaTratamentoPeloId(tratamentoModel.getId());
        assertThat(tratamentoDTO).isInstanceOf(TratamentoDTO.class).isNotNull();
        assertThat(tratamentoDTO.getTratamento()).isEqualTo(tratamentoModel.getTratamento());
        assertThat(tratamentoDTO.getReferenciaBibliografica()).isEqualTo(tratamentoModel.getReferenciaBibliografica());
    }

    @Test
    @DisplayName("deve lancar exception para tratamento que nao existe na consulta")
    public void deveLancarExceptionParaTratamentoQueNaoExisteNaConsulta() {
        assertThatThrownBy(() -> tratamentoService.recuperaTratamentoPeloId(0L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("tratamento não encontrado");
    }

    @Test
    @DisplayName("deve cadastrar tratamento corretamente")
    public void deveCadastrarTratamentoCorretamente() {
        MedicacaoDTO medicacaoDto1 = new MedicacaoDTO(null, "test", 1.1, 1, "test");
        MedicacaoDTO medicacaoDto2 = new MedicacaoDTO(null, "test", 1.1, 1, "test");
        TratamentoDTO tratamentoDto = new TratamentoDTO(null, "test", "test", List.of(medicacaoDto1, medicacaoDto2));
        TratamentoDTO tratamentoDtoSaved = tratamentoService.cadastraTratamento("1", tratamentoDto);
        assertThat(tratamentoDtoSaved).isInstanceOf(TratamentoDTO.class).isNotNull();
        assertThat(tratamentoDtoSaved.getTratamento()).isEqualTo(tratamentoModel.getTratamento());
        assertThat(tratamentoDtoSaved.getReferenciaBibliografica()).isEqualTo(tratamentoModel.getReferenciaBibliografica());
    }

    @Test
    @DisplayName("deve lancar exception para codigo de cid que nao existe")
    public void deveLancarExceptionParaCodigoDeCidQueNaoExiste() {
        TratamentoDTO tratamentoDto = new TratamentoDTO(null, "test", "test", List.of());
        assertThatThrownBy(() -> tratamentoService.cadastraTratamento("0", tratamentoDto))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("cid não encontrado");
    }

    @Test
    @DisplayName("deve atualizar tratamento corretamente")
    public void deveAtualizarTratamentoCorretamente() {
        MedicacaoDTO medicacaoDto1 = new MedicacaoDTO(medicacaoModel1.getId(), "test updated 1", 1.1, 1, "test");
        MedicacaoDTO medicacaoDto2 = new MedicacaoDTO(medicacaoModel2.getId(), "test updated 2", 1.1, 1, "test");
        TratamentoDTO tratamentoDto = new TratamentoDTO(null, "test updated", "test", List.of(medicacaoDto1, medicacaoDto2));
        TratamentoDTO tratamentoDtoUpdated = tratamentoService.atualizaTratamento(tratamentoModel.getId(), tratamentoDto);
        assertThat(tratamentoDtoUpdated).isInstanceOf(TratamentoDTO.class).isNotNull();
        assertThat(tratamentoDtoUpdated.getTratamento()).isEqualTo(tratamentoModel.getTratamento());
        assertThat(tratamentoDtoUpdated.getReferenciaBibliografica()).isEqualTo(tratamentoModel.getReferenciaBibliografica());
    }

    @Test
    @DisplayName("deve lancar exception para tratamento que nao existe na atualizacao")
    public void deveLancarExceptionParaTratamentoQueNaoExisteNaAtualizacao() {
        TratamentoDTO tratamentoDto = new TratamentoDTO(null, "test", "test", List.of());
        assertThatThrownBy(() -> tratamentoService.atualizaTratamento(0L, tratamentoDto))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("tratamento não encontrado");
    }

    @Test
    @DisplayName("deve adicionar medicacao em tratamento corretamente")
    public void deveAdicionarMedicacaoEmTratamentoCorretamente() {
        MedicacaoDTO medicacaoDto = new MedicacaoDTO(null, "test", 1.1, 1, "test");
        tratamentoService.adicionaMedicacaoNoTratamento(tratamentoModel.getId(), medicacaoDto);
    }

    @Test
    @DisplayName("deve lancar exception para tratamento que nao existe ao adicionar medicacao")
    public void deveLancarExceptionParaTratamentoQueNaoExisteAoAdicionarMedicacao() {
        MedicacaoDTO medicacaoDto = new MedicacaoDTO(null, "test", 1.1, 1, "test");
        assertThatThrownBy(() -> tratamentoService.adicionaMedicacaoNoTratamento(0L, medicacaoDto))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("tratamento não encontrado");
    }

    @Test
    @DisplayName("deve excluir medicacao corretamente")
    public void deveExcluirMedicacaoCorretamente() {
        tratamentoService.excluiMedicacaoNoTratamento(medicacaoModel1.getId());
    }

    @Test
    @DisplayName("deve lancar exception para medicacao que nao existe")
    public void deveLancarExceptionParaMedicacaoQueNaoExiste() {
        assertThatThrownBy(() -> tratamentoService.excluiMedicacaoNoTratamento(0L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("medicação não encontrada");
    }

    @Test
    @DisplayName("deve recuperar tratamentos por codigo do cid corretamente")
    public void deveRecuperarTratamentosPorCodigoDoCidCorretamente() {
        List<TratamentoDTO> tratamentosDTO = tratamentoService.recuperaTratamentosPeloCodigoCid("1");
        assertThat(tratamentosDTO).isInstanceOf(List.class).isNotNull().hasSize(1);
    }

}
