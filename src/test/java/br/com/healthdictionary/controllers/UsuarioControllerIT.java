package br.com.healthdictionary.controllers;

import br.com.healthdictionary.dto.UsuarioChangePasswordDTO;
import br.com.healthdictionary.dto.UsuarioDTO;
import br.com.healthdictionary.enums.UsuarioRole;
import br.com.healthdictionary.models.UsuarioModel;
import br.com.healthdictionary.repositories.UsuarioRepository;
import br.com.healthdictionary.services.TokenService;
import io.restassured.RestAssured;
import jakarta.transaction.Transactional;
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
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UsuarioControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TokenService tokenService;

    private UsuarioModel adminUser;
    private UsuarioModel user;
    private String adminValidToken;
    private String userValidToken;

    @BeforeEach
    public void setup() {
        RestAssured.port  = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        String password = new BCryptPasswordEncoder().encode("pass");

        adminUser = usuarioRepository.save(new UsuarioModel(null, "admin", password, UsuarioRole.ADMIN));
        adminValidToken = tokenService.generateToken(adminUser);

        user = usuarioRepository.save(new UsuarioModel(null, "medico", password, UsuarioRole.MEDICO));
        userValidToken = tokenService.generateToken(user);
    }

    @AfterEach
    public void tearDown() {
        usuarioRepository.deleteAll();
    }

    @Test
    @DisplayName("deve recuperar todos usuarios corretamente")
    public void deveRecuperarTodosUsuariosCorretamente() {
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + adminValidToken)
                .when().get("/health-dictionary/usuario/get-all-usuarios")
                .then().statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("./schemas/ListaUsuarioResponseSchema.json"));
    }

    @Test
    @DisplayName("deve restringir consulta de usuarios para perfil não autorizado")
    public void deveRestringirConsultaUsuariosParaPerfilNaoAutorizado() {
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + userValidToken)
                .when().get("/health-dictionary/usuario/get-all-usuarios")
                .then().statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("deve restringir consulta de usuarios para perfil sem identificacao")
    public void deveRestringirConsultaUsuariosParaPerfilSemIdentificacao() {
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/health-dictionary/usuario/get-all-usuarios")
                .then().statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("deve recuperar usuario pelo login corretamente")
    public void deveRecuperarUsuarioPeloLoginCorretamente() {
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + adminValidToken)
                .when().get("/health-dictionary/usuario/{login}", adminUser.getLogin())
                .then().statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("./schemas/UsuarioResponseSchema.json"));
    }

    @Test
    @DisplayName("deve retornar not found para usuario nao encontrado na consulta")
    public void deveRetornarNotFoundParaUsuarioNaoEncontradoNaConsulta() {
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + adminValidToken)
                .when().get("/health-dictionary/usuario/{login}", "test")
                .then().statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("deve restringir consulta de usuario por login para perfil não autorizado")
    public void deveRestringirConsultaDeUsuarioPorLoginParaPerfilNaoAutorizado() {
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + userValidToken)
                .when().get("/health-dictionary/usuario/{login}", adminUser.getLogin())
                .then().statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("deve restringir consulta de usuario por login para perfil sem identificacao")
    public void deveRestringirConsultaDeUsuarioPorLoginParaPerfilSemIdentificacao() {
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/health-dictionary/usuario/{login}", adminUser.getLogin())
                .then().statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("deve cadastrar usuario corretamente")
    public void deveCadastrarUsuarioCorretamente() {
        UsuarioDTO usuarioDTO = new UsuarioDTO(null, "user_new", "pass", "USUARIO");
        given().contentType(MediaType.APPLICATION_JSON_VALUE).body(usuarioDTO)
                .header("Authorization", "Bearer " + adminValidToken)
                .when().post("/health-dictionary/usuario/save-usuario")
                .then().statusCode(HttpStatus.CREATED.value())
                .body(matchesJsonSchemaInClasspath("./schemas/UsuarioResponseSchema.json"));
    }

    @Test
    @DisplayName("deve retornar bad request para usuario com login que ja existe no cadastro")
    public void deveRetornarBadRequestParaUsuarioComLoginQueJaExisteNoCadastro() {
        UsuarioDTO usuarioDTO = new UsuarioDTO(null, "medico", "pass", "MEDICO");
        given().contentType(MediaType.APPLICATION_JSON_VALUE).body(usuarioDTO)
                .header("Authorization", "Bearer " + adminValidToken)
                .when().post("/health-dictionary/usuario/save-usuario")
                .then().statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("deve restringir cadastro de usuario para perfil não autorizado")
    public void deveRestringirCadastroDeUsuarioParaPerfilNaoAutorizado() {
        UsuarioDTO usuarioDTO = new UsuarioDTO(null, "user", "pass", "USUARIO");
        given().contentType(MediaType.APPLICATION_JSON_VALUE).body(usuarioDTO)
                .header("Authorization", "Bearer " + userValidToken)
                .when().get("/health-dictionary/usuario/save-usuario")
                .then().statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("deve restringir cadastro de usuario para perfil sem identificacao")
    public void deveRestringCadastroDeUsuarioParaPerfilSemIdentificacao() {
        UsuarioDTO usuarioDTO = new UsuarioDTO(null, "user", "pass", "USUARIO");
        given().contentType(MediaType.APPLICATION_JSON_VALUE).body(usuarioDTO)
                .when().get("/health-dictionary/usuario/save-usuario")
                .then().statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("deve trocar de senha corretamente")
    public void deveTrocaDeSenhaCorretamente() {
        UsuarioChangePasswordDTO dto = new UsuarioChangePasswordDTO("admin", "pass", "new_pass");
        given().contentType(MediaType.APPLICATION_JSON_VALUE).body(dto)
                .header("Authorization", "Bearer " + adminValidToken)
                .when().put("/health-dictionary/usuario/change-password")
                .then().statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("deve retornar not found para usuario não encontrado")
    public void deveRetornarNotFoundParaUsuarioNaoEncontrado() {
        UsuarioChangePasswordDTO dto = new UsuarioChangePasswordDTO("admin1", "pass", "new_pass");
        given().contentType(MediaType.APPLICATION_JSON_VALUE).body(dto)
                .header("Authorization", "Bearer " + adminValidToken)
                .when().put("/health-dictionary/usuario/change-password")
                .then().statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("deve retornar forbidden para troca de senha de outro usuario")
    public void deveRetornarForbiddenParaTrocaDeSenhaDeOutroUsuario() {
        UsuarioChangePasswordDTO dto = new UsuarioChangePasswordDTO(user.getLogin(), "pass", "new_pass");
        given().contentType(MediaType.APPLICATION_JSON_VALUE).body(dto)
                .header("Authorization", "Bearer " + adminValidToken)
                .when().put("/health-dictionary/usuario/change-password")
                .then().statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("deve retornar forbidden para senha atual incorreta")
    public void deveRetornarForbiddenParaSenhaAtualIncorreta() {
        UsuarioChangePasswordDTO dto = new UsuarioChangePasswordDTO("admin", "old_pass", "new_pass");
        given().contentType(MediaType.APPLICATION_JSON_VALUE).body(dto)
                .header("Authorization", "Bearer " + adminValidToken)
                .when().put("/health-dictionary/usuario/change-password")
                .then().statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("deve retornar bad request para nova senha igual senha atual")
    public void deveRetornarBadRequestParaNovaSenhaIgualSenhaAtual() {
        UsuarioChangePasswordDTO dto = new UsuarioChangePasswordDTO("admin", "pass", "pass");
        given().contentType(MediaType.APPLICATION_JSON_VALUE).body(dto)
                .header("Authorization", "Bearer " + adminValidToken)
                .when().put("/health-dictionary/usuario/change-password")
                .then().statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("deve restringir troca de senha de para perfil nao autorizado")
    public void deveRestringirTrocaDeSenhaDeParaPerfilNaoAutorizado() {
        UsuarioChangePasswordDTO dto = new UsuarioChangePasswordDTO("admin", "pass", "pass");
        given().contentType(MediaType.APPLICATION_JSON_VALUE).body(dto)
                .header("Authorization", "Bearer " + userValidToken)
                .when().get("/health-dictionary/usuario/change-password")
                .then().statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("deve restringir troca de senha de para perfil sem identificacao")
    public void deveRestringirTrocaDeSenhaDeParaPerfilSemIdentificacao() {
        UsuarioChangePasswordDTO dto = new UsuarioChangePasswordDTO("admin", "pass", "pass");
        given().contentType(MediaType.APPLICATION_JSON_VALUE).body(dto)
                .when().get("/health-dictionary/usuario/change-password")
                .then().statusCode(HttpStatus.FORBIDDEN.value());
    }

}
