package br.com.healthdictionary.repositories;

import br.com.healthdictionary.models.CidModel;
import br.com.healthdictionary.models.TratamentoModel;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase
public class TratamentoRepositoryIT {

    @Autowired
    private TratamentoRepository tratamentoRepository;

    @Autowired
    private CidRepository cidRepository;

    private CidModel cidModel;

    @BeforeEach
    public void setup() {
        cidModel = cidRepository.save(new CidModel("1", "test", null));
    }

    @AfterEach
    public void tearDown() {
        tratamentoRepository.deleteAll();
        cidRepository.deleteAll();
    }

    @Test
    @DisplayName("deve registrar tratamento com sucesso")
    public void deveRegistrarTratamentoComSucesso() {
        TratamentoModel tratamentoModel = new TratamentoModel(null, "test",
                "test", cidModel, null);

        TratamentoModel tratamentoModelSaved = tratamentoRepository.save(tratamentoModel);
        assertThat(tratamentoModelSaved).isInstanceOf(TratamentoModel.class).isNotNull();
        assertThat(tratamentoModelSaved.getId()).isEqualTo(tratamentoModel.getId());
        assertThat(tratamentoModelSaved.getTratamento()).isEqualTo(tratamentoModel.getTratamento());
        assertThat(tratamentoModelSaved.getReferenciaBibliografica()).isEqualTo(tratamentoModel.getReferenciaBibliografica());
        assertThat(tratamentoModelSaved.getCid()).isEqualTo(tratamentoModel.getCid());
        assertThat(tratamentoModelSaved.getMedicacoes()).isEqualTo(tratamentoModel.getMedicacoes());
    }

    @Test
    @DisplayName("deve consultar tratamento com sucesso")
    public void deveConsultarTratamentoComSucesso() {
        TratamentoModel tratamentoModel = new TratamentoModel(null, "test",
                "test", cidModel, null);

        TratamentoModel tratamentoModelSaved = tratamentoRepository.save(tratamentoModel);
        Optional<TratamentoModel> optionalTratamento = tratamentoRepository.findById(tratamentoModelSaved.getId());
        assertThat(optionalTratamento).isInstanceOf(Optional.class).isNotNull();

        if (optionalTratamento.isEmpty())
            throw new NoSuchElementException("tratamento não encontrado");

        assertThat(optionalTratamento.get()).isInstanceOf(TratamentoModel.class).isNotNull();
        assertThat(optionalTratamento.get().getId()).isEqualTo(tratamentoModel.getId());
        assertThat(optionalTratamento.get().getTratamento()).isEqualTo(tratamentoModel.getTratamento());
        assertThat(optionalTratamento.get().getReferenciaBibliografica()).isEqualTo(tratamentoModel.getReferenciaBibliografica());
        assertThat(optionalTratamento.get().getCid()).isEqualTo(tratamentoModel.getCid());
        assertThat(optionalTratamento.get().getMedicacoes()).isEqualTo(tratamentoModel.getMedicacoes());
    }

    @Test
    @DisplayName("deve apagar tratamento com sucesso")
    public void deveApagarTratamentoComSucesso() {
        TratamentoModel tratamentoModel = new TratamentoModel(null, "test",
                "test", cidModel, null);

        TratamentoModel tratamentoModelSaved = tratamentoRepository.save(tratamentoModel);
        tratamentoRepository.deleteById(tratamentoModelSaved.getId());
        Optional<TratamentoModel> optionalTratamento = tratamentoRepository.findById(tratamentoModelSaved.getId());
        assertThat(optionalTratamento).isInstanceOf(Optional.class).isEmpty();
    }

    @Test
    @DisplayName("deve listar tratamentos com sucesso")
    public void deveListarTratamentosComSucesso() {
        TratamentoModel tratamentoModel1 = new TratamentoModel(null, "test",
                "test", cidModel, null);

        TratamentoModel tratamentoModel2 = new TratamentoModel(null, "test",
                "test", cidModel, null);

        tratamentoRepository.saveAll(List.of(tratamentoModel1, tratamentoModel2));
        List<TratamentoModel> tratamentosReturned = tratamentoRepository.findAll();
        assertThat(tratamentosReturned).isNotNull().hasSize(2);
    }

    @Test
    @DisplayName("deve listar tratamentos por codigo de cid com sucesso")
    public void deveListarTratamentosPorCodigoDeCidComSucesso() {
        TratamentoModel tratamentoModel1 = new TratamentoModel(null, "test",
                "test", cidModel, null);

        TratamentoModel tratamentoModel2 = new TratamentoModel(null, "test",
                "test", cidModel, null);

        tratamentoRepository.saveAll(List.of(tratamentoModel1, tratamentoModel2));
        List<TratamentoModel> tratamentosReturned = tratamentoRepository.findAllByCidCodigo(cidModel.getCodigo());
        assertThat(tratamentosReturned).isNotNull().hasSize(2);
    }


}
