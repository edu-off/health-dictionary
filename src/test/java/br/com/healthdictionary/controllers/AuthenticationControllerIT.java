package br.com.healthdictionary.controllers;

import br.com.healthdictionary.dto.UsuarioAuthDTO;
import br.com.healthdictionary.enums.UsuarioRole;
import br.com.healthdictionary.models.UsuarioModel;
import br.com.healthdictionary.repositories.UsuarioRepository;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private UsuarioModel usuarioModel;

    @BeforeEach
    public void setup() {
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        String encryptedPassword = new BCryptPasswordEncoder().encode("pass");
        usuarioModel = usuarioRepository.save(new UsuarioModel(null, "user", encryptedPassword, UsuarioRole.USUARIO));
    }

    @AfterEach
    public void tearDown() {
        usuarioRepository.deleteAll();
    }

    @Test
    @DisplayName("deve efetuar login corretamente")
    public void deveEfetuarLoginCorretamente() {
        UsuarioAuthDTO usuarioAuthDto = new UsuarioAuthDTO(usuarioModel.getLogin(), "pass");
        given().contentType(MediaType.APPLICATION_JSON_VALUE).body(usuarioAuthDto)
                .when().post("/health-dictionary/auth/login")
                .then().statusCode(HttpStatus.OK.value())
                .body("$", hasKey("token"))
                .body("token", notNullValue());
    }

    @Test
    @DisplayName("deve falhar ao efetuar login")
    public void deveFalharAoEfetuarLogin() {
        UsuarioAuthDTO usuarioAuthDto = new UsuarioAuthDTO(usuarioModel.getLogin() + "1", "pass");
        given().contentType(MediaType.APPLICATION_JSON_VALUE).body(usuarioAuthDto)
                .when().post("/health-dictionary/auth/login")
                .then().statusCode(HttpStatus.UNAUTHORIZED.value());
    }

}
