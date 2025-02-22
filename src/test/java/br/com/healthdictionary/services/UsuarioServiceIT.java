package br.com.healthdictionary.services;

import br.com.healthdictionary.dto.UsuarioChangePasswordDTO;
import br.com.healthdictionary.dto.UsuarioDTO;
import br.com.healthdictionary.enums.UsuarioRole;
import br.com.healthdictionary.models.UsuarioModel;
import br.com.healthdictionary.repositories.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase
public class UsuarioServiceIT {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TokenService tokenService;

    private UsuarioModel usuarioModel;
    private String token;

    @BeforeEach
    public void setup() {
        String pass = new BCryptPasswordEncoder().encode("pass");
        usuarioRepository.save(new UsuarioModel(null, "user1", pass, UsuarioRole.ADMIN));
        usuarioModel = usuarioRepository.save(new UsuarioModel(null, "user", pass, UsuarioRole.ADMIN));
        token = tokenService.generateToken(usuarioModel);
    }

    @AfterEach
    public void tearDown() {
        usuarioRepository.deleteAll();
    }

    @Test
    @DisplayName("deve recuperar todos usuarios corretamente")
    public void deveRecuperarTodosUsuariosCorretamente() {
        List<UsuarioDTO> usuarios = usuarioService.recuperaTodosUsuarios();
        assertThat(usuarios.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("deve recuperar usuario por login corretamente")
    public void deveRecuperarUsuarioPorLoginCorretamente() {
        UsuarioDTO usuarioDto = usuarioService.recuperaUsuarioPeloLogin(usuarioModel.getLogin());
        assertThat(usuarioDto).isInstanceOf(UsuarioDTO.class).isNotNull();
        assertThat(usuarioDto.getId()).isNotNegative();
        assertThat(usuarioDto.getLogin()).isEqualTo(usuarioModel.getLogin());
    }

    @Test
    @DisplayName("deve lancar exception para usuario nao encontrado na consulta por login")
    public void deveLancarExceptionParaUsuarioNaoEncontradoNaConsultaPorLogin() {
        assertThatThrownBy(() -> usuarioService.recuperaUsuarioPeloLogin("test"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("usuário não encontrado");
    }

    @Test
    @DisplayName("deve cadastrar usuario corretamente")
    public void deveCadastrarUsuarioCorretamente() {
        UsuarioDTO usuarioDto = new UsuarioDTO(null, usuarioModel.getPassword(), "pass", "ADMIN");
        UsuarioDTO usuarioDtoSaved = usuarioService.cadastraUsuario(usuarioDto);
        assertThat(usuarioDtoSaved).isInstanceOf(UsuarioDTO.class).isNotNull();
        assertThat(usuarioDtoSaved.getId()).isNotNegative();
    }

    @Test
    @DisplayName("deve lancar exception para login ja cadastrado")
    public void deveLancarExceptionParaLoginJaCadastrado() {
        UsuarioDTO usuarioDto = new UsuarioDTO(null, usuarioModel.getLogin(), "pass", "ADMIN");
        assertThatThrownBy(() -> usuarioService.cadastraUsuario(usuarioDto))
                .isInstanceOf(DuplicateKeyException.class)
                .hasMessage("login já existe");
    }

    @Test
    @DisplayName("deve recuperar usuario pelo login corretamente")
    public void deveRecuperarUsuarioPeloLoginCorretamente() {
        UserDetails userDetails = usuarioService.recuperaUsuarioPeloUsername(usuarioModel.getLogin());
        assertThat(userDetails.getUsername()).isEqualTo("user");
        assertThat(userDetails.getPassword()).isNotNull().isNotEmpty();
        assertThat(userDetails.getAuthorities()).isNotEmpty();
    }

    @Test
    @DisplayName("deve lancar exception para usuario nao encontrado")
    public void deveLancarExceptionParaUsuarioNaoEncontrado() {
        assertThatThrownBy(() -> usuarioService.recuperaUsuarioPeloUsername("user2"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("usuário não encontrado");
    }

    @Test
    @DisplayName("deve lancar exception para usuario nao encontrado ao trocar senha")
    public void deveLancarExceptionParaUsuarioNaoEncontradoAoTrocarSenha() {
        UsuarioChangePasswordDTO dto = new UsuarioChangePasswordDTO("test", "pass", "pass");
        assertThatThrownBy(() -> usuarioService.trocaPassword(dto, token))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("login não encontrado");
    }

    @Test
    @DisplayName("deve lancar exception para senha atual incorreta ao trocar senha")
    public void deveLancarExceptionParaSenhaAtualIncorretaAoTrocarSenha() {
        UsuarioChangePasswordDTO dto = new UsuarioChangePasswordDTO(usuarioModel.getLogin(), "test", "pass");
        assertThatThrownBy(() -> usuarioService.trocaPassword(dto, token))
                .isInstanceOf(AuthorizationDeniedException.class)
                .hasMessage("senha incorreta");
    }

    @Test
    @DisplayName("deve lancar exception para nova senha igual a senha atual ao trocar senha")
    public void deveLancarExceptionParaNovaSenhaIgualASenhaAtualAoTrocarSenha() {
        UsuarioChangePasswordDTO dto = new UsuarioChangePasswordDTO(usuarioModel.getLogin(), "pass", "pass");
        assertThatThrownBy(() -> usuarioService.trocaPassword(dto, token))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("nova senha não pode ser igual a anterior");
    }

    @Test
    @DisplayName("deve lancar exception para usuario tentando trocar senha de outro usuario")
    public void deveLancarExceptionParaUsuarioTentandoTrocarSenhaDeOutroUsuario() {
        UsuarioChangePasswordDTO dto = new UsuarioChangePasswordDTO(usuarioModel.getLogin() + "1", "pass", "pass");
        assertThatThrownBy(() -> usuarioService.trocaPassword(dto, token))
                .isInstanceOf(AuthorizationDeniedException.class)
                .hasMessage("o usuário logado não deve trocar a senha de outro usuário");
    }


    @Test
    @DisplayName("deve trocar senha corretamente")
    public void deveTrocarSenhaCorretamente() {
        UsuarioChangePasswordDTO dto = new UsuarioChangePasswordDTO(usuarioModel.getLogin(), "pass", "new_pass");
        usuarioService.trocaPassword(dto, token);
    }

}
