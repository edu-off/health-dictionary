package br.com.healthdictionary.repositories;

import br.com.healthdictionary.enums.UsuarioRole;
import br.com.healthdictionary.models.UsuarioModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class UsuarioRepositoryTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("deve registrar usuario com sucesso")
    public void deveRegistrarUsuarioComSucesso() {
        UsuarioModel usuarioModel = new UsuarioModel(null, "test", "test", UsuarioRole.USUARIO);

        when(usuarioRepository.save(usuarioModel)).thenReturn(usuarioModel);
        UsuarioModel usuarioModelSaved = usuarioRepository.save(usuarioModel);
        assertThat(usuarioModelSaved).isInstanceOf(UsuarioModel.class).isNotNull();
        assertThat(usuarioModelSaved.getId()).isEqualTo(usuarioModel.getId());
        assertThat(usuarioModelSaved.getLogin()).isEqualTo(usuarioModel.getLogin());
        assertThat(usuarioModelSaved.getPassword()).isEqualTo(usuarioModel.getPassword());
        assertThat(usuarioModelSaved.getRole()).isEqualTo(usuarioModel.getRole());
    }

    @Test
    @DisplayName("deve consultar usuario com sucesso")
    public void deveConsultarUsuarioComSucesso() {
        UsuarioModel usuarioModel = new UsuarioModel(1L, "test", "test", UsuarioRole.USUARIO);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioModel));
        Optional<UsuarioModel> optionalUsuario = usuarioRepository.findById(1L);
        assertThat(optionalUsuario).isInstanceOf(Optional.class).isNotNull();

        if (optionalUsuario.isEmpty())
            throw new NoSuchElementException("usuário não encontrado");

        assertThat(optionalUsuario.get()).isInstanceOf(UsuarioModel.class).isNotNull();
        assertThat(optionalUsuario.get().getId()).isEqualTo(usuarioModel.getId());
        assertThat(optionalUsuario.get().getLogin()).isEqualTo(usuarioModel.getLogin());
        assertThat(optionalUsuario.get().getPassword()).isEqualTo(usuarioModel.getPassword());
        assertThat(optionalUsuario.get().getRole()).isEqualTo(usuarioModel.getRole());
    }

    @Test
    @DisplayName("deve apagar usuario com sucesso")
    public void deveApagarUsuarioComSucesso() {
        Long id = 1L;
        doNothing().when(usuarioRepository).deleteById(id);
        usuarioRepository.deleteById(id);
        verify(usuarioRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("deve listar usuarios com sucesso")
    public void deveListarUsuariosComSucesso() {
        UsuarioModel usuarioModel1 = new UsuarioModel(1L, "test", "test", UsuarioRole.USUARIO);
        UsuarioModel usuarioModel2 = new UsuarioModel(2L, "test", "test", UsuarioRole.USUARIO);

        List<UsuarioModel> usuarios = List.of(usuarioModel1, usuarioModel2);
        when(usuarioRepository.findAll()).thenReturn(usuarios);
        List<UsuarioModel> usuariosReturned = usuarioRepository.findAll();

        assertThat(usuariosReturned).isNotNull().hasSize(2)
                .containsExactlyInAnyOrder(usuarioModel1, usuarioModel2);
    }

    @Test
    @DisplayName("deve consultar usuario pelo login com sucesso")
    public void deveConsultarUsuariosPeloLoginComSucesso() {
        UsuarioModel usuarioModel = new UsuarioModel(1L, "test", "test", UsuarioRole.USUARIO);

        when(usuarioRepository.findByLogin("test")).thenReturn(usuarioModel);
        UserDetails usuarioReturned = usuarioRepository.findByLogin("test");

        assertThat(usuarioReturned).isInstanceOf(UsuarioModel.class).isNotNull();
        assertThat(usuarioReturned.getUsername()).isEqualTo(usuarioModel.getUsername());
        assertThat(usuarioReturned.getPassword()).isEqualTo(usuarioModel.getPassword());
    }

}
