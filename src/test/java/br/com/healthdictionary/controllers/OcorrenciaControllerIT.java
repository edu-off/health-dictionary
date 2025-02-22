package br.com.healthdictionary.controllers;

import br.com.healthdictionary.dto.OcorrenciaDTO;
import br.com.healthdictionary.enums.UsuarioRole;
import br.com.healthdictionary.models.OcorrenciaModel;
import br.com.healthdictionary.models.PacienteModel;
import br.com.healthdictionary.models.UsuarioModel;
import br.com.healthdictionary.repositories.OcorrenciaRepository;
import br.com.healthdictionary.repositories.PacienteRepository;
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

import java.time.LocalDate;
import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OcorrenciaControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private OcorrenciaRepository ocorrenciaRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TokenService tokenService;

    private OcorrenciaModel ocorrenciaModel;
    private String adminValidToken;
    private String userValidToken;

    @BeforeEach
    public void setup() {
        RestAssured.port  = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        PacienteModel pacienteModel = pacienteRepository.save(new PacienteModel("1", "test", LocalDate.parse("1990-01-01"), "1", null));
        ocorrenciaModel = ocorrenciaRepository.save(new OcorrenciaModel(null, "test", "test", LocalDateTime.now(), pacienteModel));

        UsuarioModel adminUser = usuarioRepository.save(new UsuarioModel(null, "admin", "pass", UsuarioRole.ADMIN));
        adminValidToken = tokenService.generateToken(adminUser);

        UsuarioModel user = usuarioRepository.save(new UsuarioModel(null, "user", "pass", UsuarioRole.USUARIO));
        userValidToken = tokenService.generateToken(user);
    }

    @AfterEach
    public void tearDown() {
        ocorrenciaRepository.deleteAll();
        pacienteRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    @Test
    @DisplayName("deve recuperar ocorrencia corretamente")
    public void deveRecuperarOcorrenciaCorretamente() {
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + adminValidToken)
                .when().get("/health-dictionary/ocorrencia/{ocorrenciaId}", ocorrenciaModel.getId())
                .then().statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("./schemas/OcorrenciaResponseSchema.json"));
    }

    @Test
    @DisplayName("deve retornar not found para ocorrencia que nao existe na consulta")
    public void deveRetornarNotFoundParaOcorrenciaQueNaoExisteNaConsulta() {
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + adminValidToken)
                .when().get("/health-dictionary/ocorrencia/{ocorrenciaId}", 0L)
                .then().statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("deve restringir consulta de ocorrencia para perfil não autorizado")
    public void deveRestringirConsultaDeOcorrenciaParaPerfilNaoAutorizado() {
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + userValidToken)
                .when().get("/health-dictionary/ocorrencia/{ocorrenciaId}", ocorrenciaModel.getId())
                .then().statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("deve restringir consulta de ocorrencia para perfil sem identificacao")
    public void deveRestringirConsultaDeOcorrenciaParaPerfilSemIdentificacao() {
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/health-dictionary/ocorrencia/{ocorrenciaId}", ocorrenciaModel.getId())
                .then().statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("deve recuperar ocorrencias por cpf corretamente")
    public void deveRecuperarOcorrenciasPorCpfCorretamente() {
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + adminValidToken)
                .when().get("/health-dictionary/ocorrencia/cpf/{pacienteCpf}", "1")
                .then().statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("./schemas/ListaOcorrenciaResponseSchema.json"));
    }

    @Test
    @DisplayName("deve restringir consulta de ocorrencias por cpf para perfil não autorizado")
    public void deveRestringirConsultaDeOcorrenciasPorCpfParaPerfilNaoAutorizado() {
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + userValidToken)
                .when().get("/health-dictionary/ocorrencia/cpf/{pacienteCpf}", "1")
                .then().statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("deve restringir consulta de ocorrencias por cpf para perfil sem identificacao")
    public void deveRestringirConsultaDeOcorrenciasPorCpfParaPerfilSemIdentificacao() {
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/health-dictionary/ocorrencia/cpf/{pacienteCpf}", "1")
                .then().statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("deve cadastrar ocorrencia corretamente")
    public void deveCadastrarOcorrenciaCorretamente() {
        OcorrenciaDTO ocorrenciaDto = new OcorrenciaDTO(null, "test", "test", LocalDateTime.now());
        given().contentType(MediaType.APPLICATION_JSON_VALUE).body(ocorrenciaDto)
                .header("Authorization", "Bearer " + adminValidToken)
                .when().post("/health-dictionary/ocorrencia/cpf/{cpfToNewOcorrencia}/save-ocorrencia", "1")
                .then().statusCode(HttpStatus.CREATED.value())
                .body(matchesJsonSchemaInClasspath("./schemas/OcorrenciaResponseSchema.json"));
    }

    @Test
    @DisplayName("deve restringir cadastro de ocorrencia para perfil não autorizado")
    public void deveRestringirCadastroDeOcorrenciaParaPerfilNaoAutorizado() {
        OcorrenciaDTO ocorrenciaDto = new OcorrenciaDTO(null, "test", "test", LocalDateTime.now());
        given().contentType(MediaType.APPLICATION_JSON_VALUE).body(ocorrenciaDto)
                .header("Authorization", "Bearer " + userValidToken)
                .when().post("/health-dictionary/ocorrencia/cpf/{cpfToNewOcorrencia}/save-ocorrencia", "1")
                .then().statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("deve restringir cadastro de ocorrencia para perfil sem identificacao")
    public void deveRestringCadastroDeOcorrenciaParaPerfilSemIdentificacao() {
        OcorrenciaDTO ocorrenciaDto = new OcorrenciaDTO(null, "test", "test", LocalDateTime.now());
        given().contentType(MediaType.APPLICATION_JSON_VALUE).body(ocorrenciaDto)
                .when().post("/health-dictionary/ocorrencia/cpf/{cpfToNewOcorrencia}/save-ocorrencia", "1")
                .then().statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("deve atualizar ocorrencia corretamente")
    public void deveAtualizarOcorrenciaCorretamente() {
        OcorrenciaDTO ocorrenciaDto = new OcorrenciaDTO(null, "test", "test", LocalDateTime.now());
        given().contentType(MediaType.APPLICATION_JSON_VALUE).body(ocorrenciaDto)
                .header("Authorization", "Bearer " + adminValidToken)
                .when().put("/health-dictionary/ocorrencia/{ocorrenciaId}/update-ocorrencia", ocorrenciaModel.getId())
                .then().statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("./schemas/OcorrenciaResponseSchema.json"));
    }

    @Test
    @DisplayName("deve retornar not found para ocorrencia que nao existe na atualizacao")
    public void deveRetornarNotFoundParaOcorrenciaQueNaoExisteNaAtualizacao() {
        OcorrenciaDTO ocorrenciaDto = new OcorrenciaDTO(null, "test", "test", LocalDateTime.now());
        given().contentType(MediaType.APPLICATION_JSON_VALUE).body(ocorrenciaDto)
                .header("Authorization", "Bearer " + adminValidToken)
                .when().put("/health-dictionary/ocorrencia/{ocorrenciaId}/update-ocorrencia", 0L)
                .then().statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("deve restringir atualizacao de ocorrencia para perfil não autorizado")
    public void deveRestringirAtualizacaoDeOcorrenciaParaPerfilNaoAutorizado() {
        OcorrenciaDTO ocorrenciaDto = new OcorrenciaDTO(null, "test", "test", LocalDateTime.now());
        given().contentType(MediaType.APPLICATION_JSON_VALUE).body(ocorrenciaDto)
                .header("Authorization", "Bearer " + userValidToken)
                .when().put("/health-dictionary/ocorrencia/{ocorrenciaId}/update-ocorrencia", ocorrenciaModel.getId())
                .then().statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("deve restringir atualizacao de ocorrencia para perfil sem identificacao")
    public void deveRestringAtualizacaoDeOcorrenciaParaPerfilSemIdentificacao() {
        OcorrenciaDTO ocorrenciaDto = new OcorrenciaDTO(null, "test", "test", LocalDateTime.now());
        given().contentType(MediaType.APPLICATION_JSON_VALUE).body(ocorrenciaDto)
                .when().put("/health-dictionary/ocorrencia/{ocorrenciaId}/update-ocorrencia", ocorrenciaModel.getId())
                .then().statusCode(HttpStatus.FORBIDDEN.value());
    }

}
