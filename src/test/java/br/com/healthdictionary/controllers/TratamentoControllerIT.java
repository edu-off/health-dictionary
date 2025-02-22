package br.com.healthdictionary.controllers;

import br.com.healthdictionary.dto.MedicacaoDTO;
import br.com.healthdictionary.dto.TratamentoDTO;
import br.com.healthdictionary.enums.UsuarioRole;
import br.com.healthdictionary.models.CidModel;
import br.com.healthdictionary.models.MedicacaoModel;
import br.com.healthdictionary.models.TratamentoModel;
import br.com.healthdictionary.models.UsuarioModel;
import br.com.healthdictionary.repositories.CidRepository;
import br.com.healthdictionary.repositories.MedicacaoRepository;
import br.com.healthdictionary.repositories.TratamentoRepository;
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

import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TratamentoControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TratamentoRepository tratamentoRepository;

    @Autowired
    private MedicacaoRepository medicacaoRepository;

    @Autowired
    private CidRepository cidRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TokenService tokenService;

    private TratamentoModel tratamentoModel;
    private MedicacaoModel medicacaoModel;
    private String adminValidToken;
    private String userValidToken;

    @BeforeEach
    public void setup() {
        RestAssured.port  = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        CidModel cidModel = cidRepository.save(new CidModel("1", "test", null));
        tratamentoModel = tratamentoRepository.save(new TratamentoModel(null, "test", "test", cidModel, null));
        medicacaoModel = medicacaoRepository.save(new MedicacaoModel(null, "test", 1.1, 1, "test", tratamentoModel));

        UsuarioModel adminUser = usuarioRepository.save(new UsuarioModel(null, "admin", "pass", UsuarioRole.ADMIN));
        adminValidToken = tokenService.generateToken(adminUser);

        UsuarioModel user = usuarioRepository.save(new UsuarioModel(null, "user", "pass", UsuarioRole.USUARIO));
        userValidToken = tokenService.generateToken(user);
    }

    @AfterEach
    public void tearDown() {
        medicacaoRepository.deleteAll();
        tratamentoRepository.deleteAll();
        cidRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    @Test
    @DisplayName("deve recuperar tratamento corretamente")
    public void deveRecuperarTratamentoCorretamente() {
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + adminValidToken)
                .when().get("/health-dictionary/tratamento/{tratamentoId}", tratamentoModel.getId())
                .then().statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("./schemas/TratamentoResponseSchema.json"));
    }

    @Test
    @DisplayName("deve retornar not found para tratamento que nao existe na consulta")
    public void deveRetornarNotFoundParaTratamentoQueNaoExisteNaConsulta() {
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + adminValidToken)
                .when().get("/health-dictionary/tratamento/{tratamentoId}", 0L)
                .then().statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("deve restringir consulta de tratamento para perfil não autorizado")
    public void deveRestringirConsultaDeTratamentoParaPerfilNaoAutorizado() {
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + userValidToken)
                .when().get("/health-dictionary/tratamento/{tratamentoId}", tratamentoModel.getId())
                .then().statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("deve restringir consulta de tratamento para perfil sem identificacao")
    public void deveRestringirConsultaDeTratamentoParaPerfilSemIdentificacao() {
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/health-dictionary/tratamento/{tratamentoId}", tratamentoModel.getId())
                .then().statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("deve recuperar tratamentos por codigo do cid corretamente")
    public void deveRecuperarTratamentosPorCodigoDoCidCorretamente() {
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + adminValidToken)
                .when().get("/health-dictionary/tratamento/codigo-cid/{codigoCid}", tratamentoModel.getCid().getCodigo())
                .then().statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("./schemas/ListaTratamentoResponseSchema.json"));
    }

    @Test
    @DisplayName("deve restringir consulta de tratamentos por codigo do cid para perfil não autorizado")
    public void deveRestringirConsultaDeTratamentosPorCodigoDoCidParaPerfilNaoAutorizado() {
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + userValidToken)
                .when().get("/health-dictionary/tratamento/codigo-cid/{codigoCid}", tratamentoModel.getCid().getCodigo())
                .then().statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("deve restringir consulta de tratamentos por codigo do cid para perfil sem identificacao")
    public void deveRestringirConsultaDeTratamentosPorCodigoDoCidParaPerfilSemIdentificacao() {
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/health-dictionary/tratamento/codigo-cid/{codigoCid}", tratamentoModel.getCid().getCodigo())
                .then().statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("deve cadastrar tratamento corretamente")
    public void deveCadastrarTratamentoCorretamente() {
        TratamentoDTO tratamentoDto = new TratamentoDTO(null, "test", "test", List.of());
        given().contentType(MediaType.APPLICATION_JSON_VALUE).body(tratamentoDto)
                .header("Authorization", "Bearer " + adminValidToken)
                .when().post("/health-dictionary/tratamento/codigo-cid/{codigoCid}/save-tratamento", tratamentoModel.getCid().getCodigo())
                .then().statusCode(HttpStatus.CREATED.value())
                .body(matchesJsonSchemaInClasspath("./schemas/TratamentoResponseSchema.json"));
    }

    @Test
    @DisplayName("deve restringir cadastro de tratamento para perfil não autorizado")
    public void deveRestringirCadastroDeTratamentoParaPerfilNaoAutorizado() {
        TratamentoDTO tratamentoDto = new TratamentoDTO(null, "test", "test", List.of());
        given().contentType(MediaType.APPLICATION_JSON_VALUE).body(tratamentoDto)
                .header("Authorization", "Bearer " + userValidToken)
                .when().post("/health-dictionary/tratamento/codigo-cid/{codigoCid}/save-tratamento", tratamentoModel.getCid().getCodigo())
                .then().statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("deve restringir cadastro de tratamento para perfil sem identificacao")
    public void deveRestringCadastroDeTratamentoParaPerfilSemIdentificacao() {
        TratamentoDTO tratamentoDto = new TratamentoDTO(null, "test", "test", List.of());
        given().contentType(MediaType.APPLICATION_JSON_VALUE).body(tratamentoDto)
                .when().post("/health-dictionary/tratamento/codigo-cid/{codigoCid}/save-tratamento", tratamentoModel.getCid().getCodigo())
                .then().statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("deve atualizar tratamento corretamente")
    public void deveAtualizarTratamentoCorretamente() {
        TratamentoDTO tratamentoDto = new TratamentoDTO(null, "test", "test", List.of());
        given().contentType(MediaType.APPLICATION_JSON_VALUE).body(tratamentoDto)
                .header("Authorization", "Bearer " + adminValidToken)
                .when().put("/health-dictionary/tratamento/{tratamentoId}/update-tratamento", tratamentoModel.getId())
                .then().statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("./schemas/TratamentoResponseSchema.json"));
    }

    @Test
    @DisplayName("deve retornar not found para tratamento que nao existe na atualizacao")
    public void deveRetornarNotFoundParaTratamentoQueNaoExisteNaAtualizacao() {
        TratamentoDTO tratamentoDto = new TratamentoDTO(null, "test", "test", List.of());
        given().contentType(MediaType.APPLICATION_JSON_VALUE).body(tratamentoDto)
                .header("Authorization", "Bearer " + adminValidToken)
                .when().put("/health-dictionary/tratamento/{tratamentoId}/update-tratamento", 0L)
                .then().statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("deve restringir atualizacao de tratamento para perfil não autorizado")
    public void deveRestringirAtualizacaoDeTratamentoParaPerfilNaoAutorizado() {
        TratamentoDTO tratamentoDto = new TratamentoDTO(null, "test", "test", List.of());
        given().contentType(MediaType.APPLICATION_JSON_VALUE).body(tratamentoDto)
                .header("Authorization", "Bearer " + userValidToken)
                .when().put("/health-dictionary/tratamento/{tratamentoId}/update-tratamento", tratamentoModel.getId())
                .then().statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("deve restringir atualizacao de tratamento para perfil sem identificacao")
    public void deveRestringAtualizacaoDeTratamentoParaPerfilSemIdentificacao() {
        TratamentoDTO tratamentoDto = new TratamentoDTO(null, "test", "test", List.of());
        given().contentType(MediaType.APPLICATION_JSON_VALUE).body(tratamentoDto)
                .when().put("/health-dictionary/tratamento/{tratamentoId}/update-tratamento", tratamentoModel.getId())
                .then().statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("deve cadastrar medicacao corretamente")
    public void deveCadastrarMedicacaoCorretamente() {
        MedicacaoDTO medicacaoDto = new MedicacaoDTO(null, "test", 1.1, 1, "test");
        given().contentType(MediaType.APPLICATION_JSON_VALUE).body(medicacaoDto)
                .header("Authorization", "Bearer " + adminValidToken)
                .when().post("/health-dictionary/tratamento/{tratamentoId}/medicacao/save-medicacao", tratamentoModel.getId())
                .then().statusCode(HttpStatus.CREATED.value())
                .body(matchesJsonSchemaInClasspath("./schemas/MedicacaoResponseSchema.json"));
    }

    @Test
    @DisplayName("deve restringir cadastro de medicacao para perfil não autorizado")
    public void deveRestringirCadastroDeMedicacaoParaPerfilNaoAutorizado() {
        MedicacaoDTO medicacaoDto = new MedicacaoDTO(null, "test", 1.1, 1, "test");
        given().contentType(MediaType.APPLICATION_JSON_VALUE).body(medicacaoDto)
                .header("Authorization", "Bearer " + userValidToken)
                .when().post("/health-dictionary/tratamento/{tratamentoId}/medicacao/save-medicacao", tratamentoModel.getId())
                .then().statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("deve restringir cadastro de medicacao para perfil sem identificacao")
    public void deveRestringCadastroDeMedicacaoParaPerfilSemIdentificacao() {
        MedicacaoDTO medicacaoDto = new MedicacaoDTO(null, "test", 1.1, 1, "test");
        given().contentType(MediaType.APPLICATION_JSON_VALUE).body(medicacaoDto)
                .when().post("/health-dictionary/tratamento/{tratamentoId}/medicacao/save-medicacao", tratamentoModel.getId())
                .then().statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("deve excluir medicacao corretamente")
    public void deveExcluirMedicacaoCorretamente() {
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + adminValidToken)
                .when().delete("/health-dictionary/tratamento/medicacao/{medicacaoId}/delete-medicacao", medicacaoModel.getId())
                .then().statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("deve retornar not found para medicacao que nao existe na exclusao")
    public void deveRetornarNotFoundParaMedicacaoQueNaoExisteNaExclusao() {
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + adminValidToken)
                .when().delete("/health-dictionary/tratamento/medicacao/{medicacaoId}/delete-medicacao", 0L)
                .then().statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("deve restringir exclusao de medicacao para perfil não autorizado")
    public void deveRestringirExclusaoDeMedicacaoParaPerfilNaoAutorizado() {
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + userValidToken)
                .when().delete("/health-dictionary/tratamento/medicacao/{medicacaoId}/delete-medicacao", medicacaoModel.getId())
                .then().statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("deve restringir exclusao de medicacao para perfil sem identificacao")
    public void deveRestringExclusaoDeMedicacaoParaPerfilSemIdentificacao() {
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/health-dictionary/tratamento/medicacao/{medicacaoId}/delete-medicacao", medicacaoModel.getId())
                .then().statusCode(HttpStatus.FORBIDDEN.value());
    }

}
