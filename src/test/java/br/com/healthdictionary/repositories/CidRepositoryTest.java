package br.com.healthdictionary.repositories;

import br.com.healthdictionary.models.CidModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class CidRepositoryTest  {

    @Mock
    private CidRepository cidRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("deve registrar cid com sucesso")
    public void deveRegistrarCidComSucesso() {
        CidModel cidModel = new CidModel("X00", "cid teste", null);
        when(cidRepository.save(cidModel)).thenReturn(cidModel);
        CidModel cidModelSaved = cidRepository.save(cidModel);
        assertThat(cidModelSaved).isInstanceOf(CidModel.class).isNotNull();
        assertThat(cidModelSaved.getCodigo()).isEqualTo(cidModel.getCodigo());
        assertThat(cidModelSaved.getDescricao()).isEqualTo(cidModel.getDescricao());
        assertThat(cidModelSaved.getTratamentos()).isEqualTo(cidModel.getTratamentos());
    }

    @Test
    @DisplayName("deve consultar cid com sucesso")
    public void deveConsultarCidComSucesso() {
        CidModel cidModel = new CidModel("X00", "cid teste", null);
        when(cidRepository.findById(cidModel.getCodigo())).thenReturn(Optional.of(cidModel));
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
        String codigo = "X00";
        doNothing().when(cidRepository).deleteById(codigo);
        cidRepository.deleteById(codigo);
        verify(cidRepository, times(1)).deleteById(codigo);
    }

    @Test
    @DisplayName("deve listar cids com sucesso")
    public void deveListarCidsComSucesso() {
        CidModel cidModel1 = new CidModel("X00", "cid teste", null);
        CidModel cidModel2 = new CidModel("X01", "cid teste", null);
        List<CidModel> cids = List.of(cidModel1, cidModel2);
        when(cidRepository.findAll()).thenReturn(cids);
        List<CidModel> cidsReturned = cidRepository.findAll();

        assertThat(cidsReturned).isNotNull().hasSize(2)
                .containsExactlyInAnyOrder(cidModel1, cidModel2);
    }

}
