package br.com.healthdictionary.controllers;

import br.com.healthdictionary.dto.CidDTO;
import br.com.healthdictionary.enums.UsuarioRole;
import br.com.healthdictionary.models.CidModel;
import br.com.healthdictionary.models.UsuarioModel;
import br.com.healthdictionary.repositories.CidRepository;
import br.com.healthdictionary.repositories.UsuarioRepository;
import br.com.healthdictionary.services.TokenService;
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

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CidControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private CidRepository cidRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TokenService tokenService;

    private CidModel cidModel;
    private String adminValidToken;
    private String userValidToken;

    @BeforeEach
    public void setup() {
        RestAssured.port  = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        cidModel = cidRepository.save(new CidModel("1", "test", null));

        UsuarioModel adminUser = usuarioRepository.save(new UsuarioModel(null, "admin", "pass", UsuarioRole.ADMIN));
        adminValidToken = tokenService.generateToken(adminUser);

        UsuarioModel user = usuarioRepository.save(new UsuarioModel(null, "user", "pass", UsuarioRole.USUARIO));
        userValidToken = tokenService.generateToken(user);
    }

    @AfterEach
    public void tearDown() {
        cidRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    @Test
    @DisplayName("deve recuperar cid corretamente")
    public void deveRecuperarCidCorretamente() {
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + adminValidToken)
                .when().get("/health-dictionary/cid/{codigoCid}", cidModel.getCodigo())
                .then().statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("./schemas/CidResponseSchema.json"));
    }

    @Test
    @DisplayName("deve retornar not found para cid que nao existe")
    public void deveRetornarNotFoundParaCidQueNaoExiste() {
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + adminValidToken)
                .when().get("/health-dictionary/cid/{codigoCid}", "0")
                .then().statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("deve restringir consulta de cid para perfil não autorizado")
    public void deveRestringirConsultaDeCidParaPerfilNaoAutorizado() {
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + userValidToken)
                .when().get("/health-dictionary/cid/{codigoCid}", cidModel.getCodigo())
                .then().statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("deve restringir consulta de cid para perfil sem identificacao")
    public void deveRestringirConsultaDeCidParaPerfilSemIdentificacao() {
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/health-dictionary/cid/{codigoCid}", cidModel.getCodigo())
                .then().statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("deve recuperar todos os cids corretamente")
    public void deveRecuperarTodosOsCidsCorretamente() {
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + adminValidToken)
                .when().get("/health-dictionary/cid/get-all-cids")
                .then().statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("./schemas/ListaCidResponseSchema.json"));
    }

    @Test
    @DisplayName("deve restringir consulta de todos os cids para perfil não autorizado")
    public void deveRestringirConsultaDeTodosOsCidsParaPerfilNaoAutorizado() {
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + userValidToken)
                .when().get("/health-dictionary/cid/get-all-cids")
                .then().statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("deve restringir consulta de todos os cids para perfil sem identificacao")
    public void deveRestringirConsultaDeTodosOsCidsParaPerfilSemIdentificacao() {
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/health-dictionary/cid/get-all-cids")
                .then().statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("deve cadastrar cid corretamente")
    public void deveCadastrarCidCorretamente() {
        CidDTO cidDto = new CidDTO("2", "test");
        given().contentType(MediaType.APPLICATION_JSON_VALUE).body(cidDto)
                .header("Authorization", "Bearer " + adminValidToken)
                .when().post("/health-dictionary/cid/save-cid")
                .then().statusCode(HttpStatus.CREATED.value())
                .body(matchesJsonSchemaInClasspath("./schemas/CidResponseSchema.json"));
    }

    @Test
    @DisplayName("deve retornar bad request para cid que ja existe no cadastro")
    public void deveRetornarBadRequestParaCidQueJaExisteNoCadastro() {
        CidDTO cidDto = new CidDTO("1", "test");
        given().contentType(MediaType.APPLICATION_JSON_VALUE).body(cidDto)
                .header("Authorization", "Bearer " + adminValidToken)
                .when().post("/health-dictionary/cid/save-cid")
                .then().statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("deve restringir cadastro de cid para perfil não autorizado")
    public void deveRestringirCadastroDeCidParaPerfilNaoAutorizado() {
        CidDTO cidDto = new CidDTO("1", "test");
        given().contentType(MediaType.APPLICATION_JSON_VALUE).body(cidDto)
                .header("Authorization", "Bearer " + userValidToken)
                .when().post("/health-dictionary/cid/save-cid")
                .then().statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("deve restringir cadastro de cid para perfil sem identificacao")
    public void deveRestringirCadastroDeCidParaPerfilSemIdentificacao() {
        CidDTO cidDto = new CidDTO("1", "test");
        given().contentType(MediaType.APPLICATION_JSON_VALUE).body(cidDto)
                .when().post("/health-dictionary/cid/save-cid")
                .then().statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("deve atualizar cid corretamente")
    public void deveAtualizarCidCorretamente() {
        CidDTO cidDto = new CidDTO("1", "test updated");
        given().contentType(MediaType.APPLICATION_JSON_VALUE).body(cidDto)
                .header("Authorization", "Bearer " + adminValidToken)
                .when().put("/health-dictionary/cid/update-cid/{codigoCid}", cidModel.getCodigo())
                .then().statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("./schemas/CidResponseSchema.json"));
    }

    @Test
    @DisplayName("deve retornar not found para cid que nao existe na atualizacao")
    public void deveRetornarNotFoundParaCidQueNaoExisteNaAtualizacao() {
        CidDTO cidDto = new CidDTO("0", "test updated");
        given().contentType(MediaType.APPLICATION_JSON_VALUE).body(cidDto)
                .header("Authorization", "Bearer " + adminValidToken)
                .when().put("/health-dictionary/cid/update-cid/{codigoCid}", 0)
                .then().statusCode(HttpStatus.NOT_FOUND.value());
    }


    @Test
    @DisplayName("deve restringir atualizacao de cid para perfil não autorizado")
    public void deveRestringirAtualizacaoDeCidParaPerfilNaoAutorizado() {
        CidDTO cidDto = new CidDTO("1", "test updated");
        given().contentType(MediaType.APPLICATION_JSON_VALUE).body(cidDto)
                .header("Authorization", "Bearer " + userValidToken)
                .when().put("/health-dictionary/cid/update-cid/{codigoCid}", cidModel.getCodigo())
                .then().statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("deve restringir atualizacao de cid para perfil sem identificacao")
    public void deveRestringirAtualizacaoDeCidParaPerfilSemIdentificacao() {
        CidDTO cidDto = new CidDTO("1", "test updated");
        given().contentType(MediaType.APPLICATION_JSON_VALUE).body(cidDto)
                .when().put("/health-dictionary/cid/update-cid/{codigoCid}", cidModel.getCodigo())
                .then().statusCode(HttpStatus.FORBIDDEN.value());
    }

}
