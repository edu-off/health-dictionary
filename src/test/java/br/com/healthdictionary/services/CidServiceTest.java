package br.com.healthdictionary.services;

import br.com.healthdictionary.dto.CidDTO;
import br.com.healthdictionary.models.CidModel;
import br.com.healthdictionary.repositories.CidRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DuplicateKeyException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class CidServiceTest {

    @InjectMocks
    private CidService cidService;

    @Mock
    private CidRepository cidRepository;

    @Mock
    private ModelMapper mapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("deve recuperar cid corretamente")
    public void deveRecuperarCidCorretamente() {
        CidModel cidModel = new CidModel("1", "test", null);
        CidDTO cidDto = new CidDTO("1", "test");
        Optional<CidModel> optionalCid = Optional.of(cidModel);
        when(cidRepository.findById("1")).thenReturn(optionalCid);
        when(mapper.map(cidModel, CidDTO.class)).thenReturn(cidDto);
        CidDTO cidDtoReturned = cidService.recuperaCidPeloCodigo("1");
        assertThat(cidDtoReturned).isInstanceOf(CidDTO.class).isNotNull();
        assertThat(cidDtoReturned.getCodigo()).isEqualTo(cidModel.getCodigo());
        assertThat(cidDtoReturned.getDescricao()).isEqualTo(cidModel.getDescricao());
    }

    @Test
    @DisplayName("deve recuperar todos os cids corretamente")
    public void deveRecuperarTodosOsCidsCorretamente() {
        CidModel cidModel = new CidModel("1", "test", null);
        CidDTO cidDto = new CidDTO("1", "test");
        when(cidRepository.findAll()).thenReturn(List.of(cidModel));
        when(mapper.map(cidModel, CidDTO.class)).thenReturn(cidDto);
        List<CidDTO> cidsDto = cidService.recuperaTodosOsCids();
        assertThat(cidsDto).isInstanceOf(List.class).isNotEmpty().hasSize(1);
    }

    @Test
    @DisplayName("deve lancar exception para cid nao encontrado na consulta")
    public void deveLancarExceptionParaCidNaoEncontradoNaConsulta() {
        when(cidRepository.findById("1")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> cidService.recuperaCidPeloCodigo("1"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("cid não encontrado");
    }

    @Test
    @DisplayName("deve cadastrar cid corretamente")
    public void deveCadastrarCidCorretamente() {
        CidModel cidModel = new CidModel("1", "test", null);
        CidDTO cidDto = new CidDTO("1", "test");
        when(cidRepository.findById("1")).thenReturn(Optional.empty());
        when(cidRepository.save(cidModel)).thenReturn(cidModel);
        when(mapper.map(cidDto, CidModel.class)).thenReturn(cidModel);
        when(mapper.map(cidModel, CidDTO.class)).thenReturn(cidDto);
        CidDTO cidDtoSaved = cidService.cadastraCid(cidDto);
        assertThat(cidDtoSaved).isInstanceOf(CidDTO.class).isNotNull();
        assertThat(cidDtoSaved.getCodigo()).isEqualTo(cidDto.getCodigo());
        assertThat(cidDtoSaved.getDescricao()).isEqualTo(cidDto.getDescricao());
    }

    @Test
    @DisplayName("deve lancar exception no cadastro para cid ja cadastrado no cadastro")
    public void deveLancarExceptionNoCadastroParaCidJaCadastradoNoCadastro() {
        CidModel cidModel = new CidModel("1", "test", null);
        Optional<CidModel> optionalCid = Optional.of(cidModel);
        when(cidRepository.findById("1")).thenReturn(optionalCid);
        CidDTO cidDto = new CidDTO("1", "test");
        assertThatThrownBy(() -> cidService.cadastraCid(cidDto))
                .isInstanceOf(DuplicateKeyException.class)
                .hasMessage("cid já existe");
    }

    @Test
    @DisplayName("deve atualizar cid corretamente")
    public void deveAtualizarCidCorretamente() {
        CidModel cidModel = new CidModel("1", "test updated", null);
        CidDTO cidDto = new CidDTO("1", "test");
        Optional<CidModel> optionalCid = Optional.of(cidModel);
        when(cidRepository.findById("1")).thenReturn(optionalCid);
        when(cidRepository.save(cidModel)).thenReturn(cidModel);
        when(mapper.map(cidModel, CidDTO.class)).thenReturn(cidDto);
        CidDTO cidDtoUpdated = cidService.atualizaCid(cidDto.getCodigo(), cidDto);
        assertThat(cidDtoUpdated).isInstanceOf(CidDTO.class).isNotNull();
        assertThat(cidDtoUpdated.getCodigo()).isEqualTo(cidDto.getCodigo());
        assertThat(cidDtoUpdated.getDescricao()).isEqualTo(cidDto.getDescricao());
    }

    @Test
    @DisplayName("deve lancar exception na atualizacao para cid ja cadastrado")
    public void deveLancarExceptionNaAtualizacaoParaCidJaCadastrado() {
        when(cidRepository.findById("1")).thenReturn(Optional.empty());
        CidDTO cidDto = new CidDTO("2", "test updated");
        assertThatThrownBy(() -> cidService.atualizaCid(cidDto.getCodigo(), cidDto))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("cid não encontrado");
    }

}
