package br.com.healthdictionary.services;

import br.com.healthdictionary.dto.UsuarioChangePasswordDTO;
import br.com.healthdictionary.dto.UsuarioDTO;
import br.com.healthdictionary.enums.UsuarioRole;
import br.com.healthdictionary.models.UsuarioModel;
import br.com.healthdictionary.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class UsuarioServiceTest {

    @InjectMocks
    private UsuarioService usuarioService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TokenService tokenService;

    @Mock
    private ModelMapper mapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("deve recuperar todos usuarios corretamente")
    public void deveRecuperarTodosUsuariosCorretamente() {
        UsuarioModel usuarioModel = new UsuarioModel(1L, "user", "pass", UsuarioRole.USUARIO);
        UsuarioDTO usuarioDto = new UsuarioDTO(1L, "user", "pass", "USUARIO");
        when(usuarioRepository.findAll()).thenReturn(List.of(usuarioModel));
        when(mapper.map(usuarioModel, UsuarioDTO.class)).thenReturn(usuarioDto);
        List<UsuarioDTO> usuarios = usuarioService.recuperaTodosUsuarios();
        assertThat(usuarios.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("deve recuperar usuario por login corretamente")
    public void deveRecuperarUsuarioPorLoginCorretamente() {
        UsuarioModel usuarioModel = new UsuarioModel(1L, "user", "pass", UsuarioRole.USUARIO);
        UsuarioDTO usuarioDto = new UsuarioDTO(1L, "user", "pass", "USUARIO");
        when(usuarioRepository.findByLogin("user")).thenReturn(usuarioModel);
        when(mapper.map(usuarioModel, UsuarioDTO.class)).thenReturn(usuarioDto);
        UsuarioDTO usuarioDtoReturned = usuarioService.recuperaUsuarioPeloLogin("user");
        assertThat(usuarioDtoReturned).isInstanceOf(UsuarioDTO.class).isNotNull();
        assertThat(usuarioDtoReturned.getId()).isNotNegative();
        assertThat(usuarioDtoReturned.getLogin()).isEqualTo(usuarioModel.getLogin());
    }

    @Test
    @DisplayName("deve lancar exception para usuario nao encontrado na consulta por login")
    public void deveLancarExceptionParaUsuarioNaoEncontradoNaConsultaPorLogin() {
        when(usuarioRepository.findByLogin("user")).thenReturn(null);
        assertThatThrownBy(() -> usuarioService.recuperaUsuarioPeloLogin("user"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("usuário não encontrado");
    }

    @Test
    @DisplayName("deve cadastrar usuario corretamente")
    public void deveCadastrarUsuarioCorretamente() {
        UsuarioModel usuarioModel = new UsuarioModel(1L, "user", "pass", UsuarioRole.USUARIO);
        UsuarioDTO usuarioDto = new UsuarioDTO(1L, "user", "pass", "USUARIO");
        when(usuarioRepository.findByLogin("user")).thenReturn(null);
        when(usuarioRepository.save(usuarioModel)).thenReturn(usuarioModel);
        when(mapper.map(usuarioDto, UsuarioModel.class)).thenReturn(usuarioModel);
        when(mapper.map(usuarioModel, UsuarioDTO.class)).thenReturn(usuarioDto);
        UsuarioDTO usuarioDtoSaved = usuarioService.cadastraUsuario(usuarioDto);
        assertThat(usuarioDtoSaved).isInstanceOf(UsuarioDTO.class).isNotNull();
        assertThat(usuarioDtoSaved.getId()).isNotNegative();
    }

    @Test
    @DisplayName("deve lancar exception para login ja cadastrado")
    public void deveLancarExceptionParaLoginJaCadastrado() {
        UsuarioModel usuarioModel = new UsuarioModel(1L, "user", "pass", UsuarioRole.USUARIO);
        UsuarioDTO usuarioDto = new UsuarioDTO(1L, "user", "pass", "USUARIO");
        when(usuarioRepository.findByLogin("user")).thenReturn(usuarioModel);
        assertThatThrownBy(() -> usuarioService.cadastraUsuario(usuarioDto))
                .isInstanceOf(DuplicateKeyException.class)
                .hasMessage("login já existe");
    }

    @Test
    @DisplayName("deve recuperar usuario pelo login corretamente")
    public void deveRecuperarUsuarioPeloLoginCorretamente() {
        UsuarioModel usuarioModel = new UsuarioModel(1L, "user", "pass", UsuarioRole.USUARIO);
        when(usuarioRepository.findByLogin("user")).thenReturn(usuarioModel);
        UserDetails userDetailsReturned = usuarioService.recuperaUsuarioPeloUsername("user");
        assertThat(userDetailsReturned.getUsername()).isEqualTo(usuarioModel.getUsername());
        assertThat(userDetailsReturned.getPassword()).isNotNull().isNotEmpty();
        assertThat(userDetailsReturned.getAuthorities()).isNotEmpty();
    }

    @Test
    @DisplayName("deve lancar exception para usuario nao encontrado")
    public void deveLancarExceptionParaUsuarioNaoEncontrado() {
        when(usuarioRepository.findByLogin("user")).thenReturn(null);
        assertThatThrownBy(() -> usuarioService.recuperaUsuarioPeloUsername("user"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("usuário não encontrado");
    }

    @Test
    @DisplayName("deve lancar exception para usuario nao encontrado ao trocar senha")
    public void deveLancarExceptionParaUsuarioNaoEncontradoAoTrocarSenha() {
        UsuarioChangePasswordDTO dto = new UsuarioChangePasswordDTO("user", "pass", "pass");
        when(usuarioRepository.findByLogin("user")).thenReturn(null);
        assertThatThrownBy(() -> usuarioService.trocaPassword(dto, "token"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("login não encontrado");
    }

    @Test
    @DisplayName("deve lancar exception para senha atual incorreta ao trocar senha")
    public void deveLancarExceptionParaSenhaAtualIncorretaAoTrocarSenha() {
        UsuarioChangePasswordDTO dto = new UsuarioChangePasswordDTO("user", "old_pass", "pass");
        UsuarioModel usuarioModel = new UsuarioModel(1L, "user", "pass", UsuarioRole.USUARIO);
        when(usuarioRepository.findByLogin("user")).thenReturn(usuarioModel);
        when(tokenService.validateToken("token")).thenReturn("user");
        assertThatThrownBy(() -> usuarioService.trocaPassword(dto, "Bearer token"))
                .isInstanceOf(AuthorizationDeniedException.class)
                .hasMessage("senha incorreta");
    }

    @Test
    @DisplayName("deve lancar exception para nova senha igual a senha atual ao trocar senha")
    public void deveLancarExceptionParaNovaSenhaIgualASenhaAtualAoTrocarSenha() {
        UsuarioChangePasswordDTO dto = new UsuarioChangePasswordDTO("user", "pass", "new_pass");
        UsuarioModel usuarioModel = new UsuarioModel(1L, "user", "pass", UsuarioRole.USUARIO);
        when(usuarioRepository.findByLogin("user")).thenReturn(usuarioModel);
        when(tokenService.validateToken("token")).thenReturn("user");
        assertThatThrownBy(() -> usuarioService.trocaPassword(dto, "Bearer token"))
                .isInstanceOf(AuthorizationDeniedException.class)
                .hasMessage("senha incorreta");
    }

    @Test
    @DisplayName("deve lancar exception para usuario tentando trocar senha de outro usuario")
    public void deveLancarExceptionParaUsuarioTentandoTrocarSenhaDeOutroUsuario() {
        UsuarioChangePasswordDTO dto = new UsuarioChangePasswordDTO("user", "pass", "new_pass");
        UsuarioModel usuarioModel = new UsuarioModel(1L, "user", "pass", UsuarioRole.USUARIO);
        when(usuarioRepository.findByLogin("user")).thenReturn(usuarioModel);
        when(tokenService.validateToken("token")).thenReturn("user1");
        assertThatThrownBy(() -> usuarioService.trocaPassword(dto, "Bearer token"))
                .isInstanceOf(AuthorizationDeniedException.class)
                .hasMessage("o usuário logado não deve trocar a senha de outro usuário");
    }

    @Test
    @DisplayName("deve trocar senha corretamente")
    public void deveTrocarSenhaCorretamente() {
        String encryptedPassword = new BCryptPasswordEncoder().encode("pass");
        UsuarioChangePasswordDTO dto = new UsuarioChangePasswordDTO("user", "pass", "new_pass");
        UsuarioModel usuarioModel = new UsuarioModel(1L, "user", encryptedPassword, UsuarioRole.USUARIO);
        when(usuarioRepository.findByLogin("user")).thenReturn(usuarioModel);
        when(tokenService.validateToken("token")).thenReturn("user");
        when(usuarioRepository.save(usuarioModel)).thenReturn(usuarioModel);
        usuarioService.trocaPassword(dto, "Bearer token");
        verify(usuarioRepository, times(1)).findByLogin("user");
        verify(usuarioRepository, times(1)).save(usuarioModel);
    }

}
