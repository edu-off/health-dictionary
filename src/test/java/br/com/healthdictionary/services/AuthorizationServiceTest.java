package br.com.healthdictionary.services;

import br.com.healthdictionary.enums.UsuarioRole;
import br.com.healthdictionary.models.UsuarioModel;
import br.com.healthdictionary.repositories.UsuarioRepository;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class AuthorizationServiceTest {

    @InjectMocks
    private AuthorizationService authService;

    @Mock
    private UsuarioService usuarioService;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("deve retornar usuario pelo login")
    public void deveRetornarUsuarioPeloLogin() {
        String login = "admin";
        UsuarioModel usuarioModel = new UsuarioModel(1L, login, "test", UsuarioRole.ADMIN);
        when(usuarioService.recuperaUsuarioPeloUsername(login)).thenReturn(usuarioModel);
        UserDetails userDetails = authService.loadUserByUsername(login);
        assertThat(userDetails).isInstanceOf(UserDetails.class).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(usuarioModel.getLogin());
        assertThat(userDetails.getPassword()).isEqualTo(usuarioModel.getPassword());
    }

    @Test
    @DisplayName("deve retornar null pelo login")
    public void deveRetornarNullPeloLogin() {
        String login = "admin";
        when(usuarioService.recuperaUsuarioPeloUsername(login)).thenReturn(null);
        UserDetails userDetails = authService.loadUserByUsername(login);
        assertThat(userDetails).isNull();
    }

}
