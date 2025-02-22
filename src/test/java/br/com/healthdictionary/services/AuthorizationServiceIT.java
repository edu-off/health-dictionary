package br.com.healthdictionary.services;

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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase
public class AuthorizationServiceIT {

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @BeforeEach
    public void setup() {
        usuarioRepository.save(new UsuarioModel(null, "user", "pass", UsuarioRole.USUARIO));
    }

    @AfterEach
    public void tearDown() {
        usuarioRepository.deleteAll();
    }

    @Test
    @DisplayName("deve retornar usuario pelo login")
    public void deveRetornarUsuarioPeloLogin() {
        UserDetails userDetails = authorizationService.loadUserByUsername("user");
        assertThat(userDetails).isInstanceOf(UsuarioModel.class).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("user");
    }

    @Test
    @DisplayName("deve lancar exception para login nao encontrado")
    public void deveLancarExceptionParaLoginNaoEncontrado() {
        assertThatThrownBy(() -> authorizationService.loadUserByUsername("test"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("usuário não encontrado");
    }

}