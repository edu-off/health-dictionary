package br.com.healthdictionary.services;

import br.com.healthdictionary.dto.CidDTO;
import br.com.healthdictionary.models.CidModel;
import br.com.healthdictionary.repositories.CidRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase
public class CidServiceIT {

    @Autowired
    private CidService cidService;

    @Autowired
    private CidRepository cidRepository;

    private CidModel cidModel;

    @BeforeEach
    public void setup() {
        cidModel = cidRepository.save(new CidModel("1", "test", null));
    }

    @AfterEach
    public void tearDown() {
        cidRepository.deleteAll();
    }

    @Test
    @DisplayName("deve recuperar cid corretamente")
    public void deveRecuperarCidCorretamente() {
        CidDTO cidDto = cidService.recuperaCidPeloCodigo(cidModel.getCodigo());
        assertThat(cidDto).isInstanceOf(CidDTO.class).isNotNull();
        assertThat(cidDto.getCodigo()).isEqualTo(cidModel.getCodigo());
        assertThat(cidDto.getDescricao()).isEqualTo(cidModel.getDescricao());
    }

    @Test
    @DisplayName("deve lancar exception para cid nao encontrado")
    public void deveLancarExceptionParaCidNaoEncontrado() {
        assertThatThrownBy(() -> cidService.recuperaCidPeloCodigo(cidModel.getCodigo() + "1"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("cid não encontrado");
    }

    @Test
    @DisplayName("deve recuperar todos os cids corretamente")
    public void deveRecuperarTodosOsCidsCorretamente() {
        List<CidDTO> cids = cidService.recuperaTodosOsCids();
        assertThat(cids).isInstanceOf(List.class).isNotEmpty().hasSize(1);
    }

    @Test
    @DisplayName("deve cadastrar cid corretamente")
    public void deveCadastrarCidCorretamente() {
        CidDTO cidDto = new CidDTO("2", "test");
        CidDTO cidDtoSaved = cidService.cadastraCid(cidDto);
        assertThat(cidDtoSaved).isInstanceOf(CidDTO.class).isNotNull();
        assertThat(cidDtoSaved.getCodigo()).isEqualTo(cidDto.getCodigo());
        assertThat(cidDtoSaved.getDescricao()).isEqualTo(cidDto.getDescricao());
    }

    @Test
    @DisplayName("deve lancar exception no cadastro para cid ja cadastrado")
    public void deveLancarExceptionNoCadastroParaCidJaCadastrado() {
        CidDTO cidDto = new CidDTO("1", "test");
        assertThatThrownBy(() -> cidService.cadastraCid(cidDto))
                .isInstanceOf(DuplicateKeyException.class)
                .hasMessage("cid já existe");
    }

    @Test
    @DisplayName("deve atualizar cid corretamente")
    public void deveAtualizarCidCorretamente() {
        CidDTO cidDto = new CidDTO("1", "test updated");
        CidDTO cidDtoUpdated = cidService.atualizaCid(cidDto.getCodigo(), cidDto);
        assertThat(cidDtoUpdated).isInstanceOf(CidDTO.class).isNotNull();
        assertThat(cidDtoUpdated.getCodigo()).isEqualTo(cidDto.getCodigo());
        assertThat(cidDtoUpdated.getDescricao()).isEqualTo(cidDto.getDescricao());
    }

    @Test
    @DisplayName("deve lancar exception na atualizacao para cid ja cadastrado")
    public void deveLancarExceptionNaAtualizacaoParaCidJaCadastrado() {
        CidDTO cidDto = new CidDTO("2", "test updated");
        assertThatThrownBy(() -> cidService.atualizaCid(cidDto.getCodigo(), cidDto))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("cid não encontrado");
    }

}
