package br.com.healthdictionary.repositories;

import br.com.healthdictionary.models.CidModel;
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
public class CidRepositoryIT {

    @Autowired
    private CidRepository cidRepository;

    @AfterEach
    public void tearDown() {
        cidRepository.deleteAll();
    }

    @Test
    @DisplayName("deve registrar cid com sucesso")
    public void deveRegistrarCidComSucesso() {
        CidModel cidModel = new CidModel("X00", "cid teste", null);
        CidModel cidModelSaved = cidRepository.save(cidModel);
        assertThat(cidModelSaved).isInstanceOf(CidModel.class).isNotNull();
        assertThat(cidModelSaved.getCodigo()).isEqualTo(cidModel.getCodigo());
        assertThat(cidModelSaved.getDescricao()).isEqualTo(cidModel.getDescricao());
        assertThat(cidModelSaved.getTratamentos()).isEqualTo(cidModel.getTratamentos());
    }

    @Test
    @DisplayName("deve consultar cid com sucesso")
    public void deveConsultarCidComSucesso() {
        CidModel cidModel = cidRepository.save(new CidModel("X00", "cid teste", null));
        Optional<CidModel> optionalCid = cidRepository.findById(cidModel.getCodigo());
        assertThat(optionalCid).isInstanceOf(Optional.class).isNotNull();
        if (optionalCid.isEmpty())
            throw new NoSuchElementException("cid não encontrado");

        assertThat(optionalCid.get()).isInstanceOf(CidModel.class).isNotNull();
        assertThat(optionalCid.get().getCodigo()).isEqualTo(cidModel.getCodigo());
        assertThat(optionalCid.get().getDescricao()).isEqualTo(cidModel.getDescricao());
        assertThat(optionalCid.get().getTratamentos()).isEqualTo(cidModel.getTratamentos());
    }

    @Test
    @DisplayName("deve apagar cid com sucesso")
    public void deveApagarCidComSucesso() {
        CidModel cidModel = cidRepository.save(new CidModel("X00", "cid teste", null));
        cidRepository.deleteById(cidModel.getCodigo());
        Optional<CidModel> optionalCid = cidRepository.findById(cidModel.getCodigo());
        assertThat(optionalCid).isInstanceOf(Optional.class).isEmpty();
    }

    @Test
    @DisplayName("deve listar cids com sucesso")
    public void deveListarCidsComSucesso() {
        CidModel cidModel1 = new CidModel("X00", "cid teste", null);
        CidModel cidModel2 = new CidModel("X01", "cid teste", null);
        cidRepository.saveAll(List.of(cidModel1, cidModel2));
        List<CidModel> cidsReturned = cidRepository.findAll();
        assertThat(cidsReturned).isNotNull().hasSize(2);
    }

}
