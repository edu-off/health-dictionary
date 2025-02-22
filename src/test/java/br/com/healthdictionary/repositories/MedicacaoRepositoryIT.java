package br.com.healthdictionary.repositories;

import br.com.healthdictionary.models.CidModel;
import br.com.healthdictionary.models.MedicacaoModel;
import br.com.healthdictionary.models.TratamentoModel;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase
public class MedicacaoRepositoryIT {

    @Autowired
    private MedicacaoRepository medicacaoRepository;

    @Autowired
    private TratamentoRepository tratamentoRepository;

    @Autowired
    private CidRepository cidRepository;

    @AfterEach
    public void tearDown() {
        medicacaoRepository.deleteAll();
        tratamentoRepository.deleteAll();
        cidRepository.deleteAll();
    }

    @Test
    @DisplayName("deve registrar medicacao com sucesso")
    public void deveRegistrarMedicacaoComSucesso() {
        CidModel cidModel = cidRepository.save(new CidModel("X00", "test", null));
        TratamentoModel tratamentoModel = tratamentoRepository.save(new TratamentoModel(null, "test", "test", cidModel, null));
        MedicacaoModel medicacaoModel = new MedicacaoModel(null, "test", 1.1, 1, "test", tratamentoModel);

        MedicacaoModel medicacaoModelSaved = medicacaoRepository.save(medicacaoModel);
        assertThat(medicacaoModelSaved).isInstanceOf(MedicacaoModel.class).isNotNull();
        assertThat(medicacaoModelSaved.getMedicacao()).isEqualTo(medicacaoModel.getMedicacao());
        assertThat(medicacaoModelSaved.getDosagem()).isEqualTo(medicacaoModel.getDosagem());
        assertThat(medicacaoModelSaved.getQuantidadeDias()).isEqualTo(medicacaoModel.getQuantidadeDias());
        assertThat(medicacaoModelSaved.getObservacao()).isEqualTo(medicacaoModel.getObservacao());
        assertThat(medicacaoModelSaved.getTratamento()).isEqualTo(medicacaoModel.getTratamento());
    }

    @Test
    @DisplayName("deve consultar cid com sucesso")
    public void deveConsultarCidComSucesso() {
        CidModel cidModel = cidRepository.save(new CidModel("X00", "test", null));
        TratamentoModel tratamentoModel = tratamentoRepository.save(new TratamentoModel(null, "test", "test", cidModel, null));
        MedicacaoModel medicacaoModel = medicacaoRepository.save(new MedicacaoModel(null, "test", 1.1, 1, "test", tratamentoModel));

        Optional<MedicacaoModel> optionalMedicacao = medicacaoRepository.findById(medicacaoModel.getId());
        assertThat(optionalMedicacao).isInstanceOf(Optional.class).isNotNull();
        if (optionalMedicacao.isEmpty())
            throw new NoSuchElementException("medicação não encontrada");

        assertThat(optionalMedicacao.get()).isInstanceOf(MedicacaoModel.class).isNotNull();
        assertThat(optionalMedicacao.get().getMedicacao()).isEqualTo(medicacaoModel.getMedicacao());
        assertThat(optionalMedicacao.get().getDosagem()).isEqualTo(medicacaoModel.getDosagem());
        assertThat(optionalMedicacao.get().getQuantidadeDias()).isEqualTo(medicacaoModel.getQuantidadeDias());
        assertThat(optionalMedicacao.get().getObservacao()).isEqualTo(medicacaoModel.getObservacao());
        assertThat(optionalMedicacao.get().getTratamento()).isEqualTo(medicacaoModel.getTratamento());
    }

    @Test
    @DisplayName("deve apagar cid com sucesso")
    public void deveApagarCidComSucesso() {
        CidModel cidModel = cidRepository.save(new CidModel("X00", "test", null));
        TratamentoModel tratamentoModel = tratamentoRepository.save(new TratamentoModel(null, "test", "test", cidModel, null));
        MedicacaoModel medicacaoModel = medicacaoRepository.save(new MedicacaoModel(null, "test", 1.1, 1, "test", tratamentoModel));
        medicacaoRepository.deleteById(medicacaoModel.getId());
        Optional<MedicacaoModel> optionalMedicacao = medicacaoRepository.findById(medicacaoModel.getId());
        assertThat(optionalMedicacao).isInstanceOf(Optional.class).isEmpty();
    }

    @Test
    @DisplayName("deve listar cids com sucesso")
    public void deveListarCidsComSucesso() {
        CidModel cidModel = cidRepository.save(new CidModel("X00", "test", null));
        TratamentoModel tratamentoModel = tratamentoRepository.save(new TratamentoModel(null, "test", "test", cidModel, null));
        MedicacaoModel medicacaoModel1 = new MedicacaoModel(null, "test", 1.1, 1, "test", tratamentoModel);
        MedicacaoModel medicacaoModel2 = new MedicacaoModel(null, "test", 1.1, 1, "test", tratamentoModel);
        medicacaoRepository.saveAll(List.of(medicacaoModel1, medicacaoModel2));
        List<MedicacaoModel> medicacoesReturned = medicacaoRepository.findAll();
        assertThat(medicacoesReturned).isNotNull().hasSize(2);
    }

}
