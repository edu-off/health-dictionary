package br.com.healthdictionary.services;

import br.com.healthdictionary.enums.UsuarioRole;
import br.com.healthdictionary.models.UsuarioModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class TokenServiceIT {

    @Autowired
    private TokenService tokenService;

    @Test
    @DisplayName("deve gerar token corretamente")
    public void deveGerarTokenValidoCorretamente() {
        UsuarioModel usuarioModel = new UsuarioModel(1L, "admin", "pass", UsuarioRole.ADMIN);
        String token = tokenService.generateToken(usuarioModel);
        assertThat(token).isNotBlank();
        assertThat(tokenService.validateToken(token)).isEqualTo(usuarioModel.getLogin());
    }

}
