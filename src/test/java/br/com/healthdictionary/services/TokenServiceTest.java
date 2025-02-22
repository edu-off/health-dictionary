package br.com.healthdictionary.services;

import br.com.healthdictionary.dto.UsuarioAuthDTO;
import br.com.healthdictionary.enums.UsuarioRole;
import br.com.healthdictionary.models.UsuarioModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

public class TokenServiceTest {

    @Test
    @DisplayName("deve gerar token corretamente")
    public void deveGerarTokenValidoCorretamente() {
        TokenService tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "secret", "as6c5fa6sf5sd76vb5d7n6f8gnm9fm8fnbdv7s");
        UsuarioModel usuarioModel = new UsuarioModel(1L, "admin", "pass", UsuarioRole.ADMIN);
        String token = tokenService.generateToken(usuarioModel);
        assertThat(token).isNotBlank();
        assertThat(tokenService.validateToken(token)).isEqualTo(usuarioModel.getLogin());
    }

}
