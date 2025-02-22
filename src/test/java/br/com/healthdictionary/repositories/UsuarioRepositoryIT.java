package br.com.healthdictionary.repositories;

import br.com.healthdictionary.enums.UsuarioRole;
import br.com.healthdictionary.models.UsuarioModel;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase
public class UsuarioRepositoryIT {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @AfterEach
    public void tearDown() {
        usuarioRepository.deleteAll();
    }

    @Test
    @DisplayName("deve registrar usuario com sucesso")
    public void deveRegistrarUsuarioComSucesso() {
        UsuarioModel usuarioModel = new UsuarioModel(null, "test", "test", UsuarioRole.USUARIO);
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
        UsuarioModel usuarioModel = new UsuarioModel(null, "test", "test", UsuarioRole.USUARIO);
        UsuarioModel usuarioModelSaved = usuarioRepository.save(usuarioModel);
        Optional<UsuarioModel> optionalUsuario = usuarioRepository.findById(usuarioModelSaved.getId());
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
        UsuarioModel usuarioModel = new UsuarioModel(null, "test", "test", UsuarioRole.USUARIO);
        UsuarioModel usuarioModelSaved = usuarioRepository.save(usuarioModel);
        usuarioRepository.deleteById(usuarioModelSaved.getId());
        Optional<UsuarioModel> optionalUsuario = usuarioRepository.findById(usuarioModelSaved.getId());
        assertThat(optionalUsuario).isInstanceOf(Optional.class).isEmpty();
    }

    @Test
    @DisplayName("deve listar usuarios com sucesso")
    public void deveListarUsuariosComSucesso() {
        UsuarioModel usuarioModel1 = new UsuarioModel(null, "test1", "test", UsuarioRole.USUARIO);
        UsuarioModel usuarioModel2 = new UsuarioModel(null, "test2", "test", UsuarioRole.USUARIO);
        usuarioRepository.saveAll(List.of(usuarioModel1, usuarioModel2));
        List<UsuarioModel> usuariosReturned = usuarioRepository.findAll();
        assertThat(usuariosReturned).isNotNull().hasSize(2);
    }

    @Test
    @DisplayName("deve consultar usuario pelo login com sucesso")
    public void deveConsultarUsuariosPeloLoginComSucesso() {
        UsuarioModel usuarioModel = new UsuarioModel(null, "test", "test", UsuarioRole.USUARIO);
        UsuarioModel usuarioModelSaved = usuarioRepository.save(usuarioModel);
        UserDetails usuarioReturned = usuarioRepository.findByLogin(usuarioModelSaved.getLogin());
        assertThat(usuarioReturned).isInstanceOf(UsuarioModel.class).isNotNull();
        assertThat(usuarioReturned.getUsername()).isEqualTo(usuarioModel.getUsername());
        assertThat(usuarioReturned.getPassword()).isEqualTo(usuarioModel.getPassword());
    }

}
