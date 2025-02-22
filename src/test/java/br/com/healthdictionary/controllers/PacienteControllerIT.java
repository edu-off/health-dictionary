package br.com.healthdictionary.controllers;

import br.com.healthdictionary.dto.PacienteDTO;
import br.com.healthdictionary.enums.UsuarioRole;
import br.com.healthdictionary.models.PacienteModel;
import br.com.healthdictionary.models.UsuarioModel;
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

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PacienteControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TokenService tokenService;

    private PacienteModel pacienteModel;
    private String adminValidToken;
    private String medicoValidToken;

    @BeforeEach
    public void setup() {
        RestAssured.port  = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        pacienteModel = pacienteRepository.save(new PacienteModel("1", "test", LocalDate.parse("1990-01-01"), "1", null));

        UsuarioModel adminUser = usuarioRepository.save(new UsuarioModel(null, "admin", "pass", UsuarioRole.ADMIN));
        adminValidToken = tokenService.generateToken(adminUser);

        UsuarioModel user = usuarioRepository.save(new UsuarioModel(null, "medico", "pass", UsuarioRole.MEDICO));
        medicoValidToken = tokenService.generateToken(user);
    }

    @AfterEach
    public void tearDown() {
        pacienteRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    @Test
    @DisplayName("deve recuperar paciente corretamente")
    public void deveRecuperarPacienteCorretamente() {
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + adminValidToken)
                .when().get("/health-dictionary/paciente/{cpfToGetPaciente}", pacienteModel.getCpf())
                .then().statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("./schemas/PacienteResponseSchema.json"));
    }

    @Test
    @DisplayName("deve retornar not found para paciente que nao existe na consulta")
    public void deveRetornarNotFoundParaPacienteQueNaoExisteNaConsulta() {
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + adminValidToken)
                .when().get("/health-dictionary/paciente/{cpfToGetPaciente}", pacienteModel.getCpf() + "1")
                .then().statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("deve restringir consulta de paciente para perfil sem identificacao")
    public void deveRestringirConsultaDePacienteParaPerfilSemIdentificacao() {
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/health-dictionary/paciente/{cpfToGetPaciente}", pacienteModel.getCpf())
                .then().statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("deve cadastrar paciente corretamente")
    public void deveCadastrarPacienteCorretamente() {
        PacienteDTO pacienteDto = new PacienteDTO("2", "test", LocalDate.parse("1990-01-01"), "1");
        given().contentType(MediaType.APPLICATION_JSON_VALUE).body(pacienteDto)
                .header("Authorization", "Bearer " + adminValidToken)
                .when().post("/health-dictionary/paciente/save-paciente")
                .then().statusCode(HttpStatus.CREATED.value())
                .body(matchesJsonSchemaInClasspath("./schemas/PacienteResponseSchema.json"));
    }

    @Test
    @DisplayName("deve retornar bad request para paciente que ja existe no cadastro")
    public void deveRetornarBadRequestParaOcorrenciaQueJaExisteNoCadastro() {
        PacienteDTO pacienteDto = new PacienteDTO("1", "test", LocalDate.parse("1990-01-01"), "1");
        given().contentType(MediaType.APPLICATION_JSON_VALUE).body(pacienteDto)
                .header("Authorization", "Bearer " + adminValidToken)
                .when().post("/health-dictionary/paciente/save-paciente")
                .then().statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("deve restringir cadastro de paciente para perfil não autorizado")
    public void deveRestringirCadastroDePacienteParaPerfilNaoAutorizado() {
        PacienteDTO pacienteDto = new PacienteDTO("2", "test", LocalDate.parse("1990-01-01"), "1");
        given().contentType(MediaType.APPLICATION_JSON_VALUE).body(pacienteDto)
                .header("Authorization", "Bearer " + medicoValidToken)
                .when().post("/health-dictionary/paciente/save-paciente")
                .then().statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("deve restringir cadastro de paciente para perfil sem identificacao")
    public void deveRestringCadastroDePacienteParaPerfilSemIdentificacao() {
        PacienteDTO pacienteDto = new PacienteDTO("2", "test", LocalDate.parse("1990-01-01"), "1");
        given().contentType(MediaType.APPLICATION_JSON_VALUE).body(pacienteDto)
                .when().post("/health-dictionary/paciente/save-paciente")
                .then().statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("deve atualizar paciente corretamente")
    public void deveAtualizarPacienteCorretamente() {
        PacienteDTO pacienteDto = new PacienteDTO("1", "test", LocalDate.parse("1990-01-01"), "1");
        given().contentType(MediaType.APPLICATION_JSON_VALUE).body(pacienteDto)
                .header("Authorization", "Bearer " + adminValidToken)
                .when().put("/health-dictionary/paciente/update-paciente/{cpfToUpdatePaciente}", pacienteModel.getCpf())
                .then().statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("./schemas/PacienteResponseSchema.json"));
    }

    @Test
    @DisplayName("deve retornar not found para paciente que nao existe na atualizacao")
    public void deveRetornarNotFoundParaPacienteQueNaoExisteNaAtualizacao() {
        PacienteDTO pacienteDto = new PacienteDTO("22", "test", LocalDate.parse("1990-01-01"), "1");
        given().contentType(MediaType.APPLICATION_JSON_VALUE).body(pacienteDto)
                .header("Authorization", "Bearer " + adminValidToken)
                .when().put("/health-dictionary/paciente/update-paciente/{cpfToUpdatePaciente}", pacienteModel.getCpf() + "2")
                .then().statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("deve restringir atualizacao de paciente para perfil não autorizado")
    public void deveRestringirAtualizacaoDePacienteParaPerfilNaoAutorizado() {
        PacienteDTO pacienteDto = new PacienteDTO("1", "test", LocalDate.parse("1990-01-01"), "1");
        given().contentType(MediaType.APPLICATION_JSON_VALUE).body(pacienteDto)
                .header("Authorization", "Bearer " + medicoValidToken)
                .when().put("/health-dictionary/paciente/update-paciente/{cpfToUpdatePaciente}", pacienteModel.getCpf())
                .then().statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("deve restringir atualizacao de paciente para perfil sem identificacao")
    public void deveRestringAtualizacaoDePacienteParaPerfilSemIdentificacao() {
        PacienteDTO pacienteDto = new PacienteDTO("1", "test", LocalDate.parse("1990-01-01"), "1");
        given().contentType(MediaType.APPLICATION_JSON_VALUE).body(pacienteDto)
                .when().put("/health-dictionary/paciente/update-paciente/{cpfToUpdatePaciente}", pacienteModel.getCpf())
                .then().statusCode(HttpStatus.FORBIDDEN.value());
    }

}
