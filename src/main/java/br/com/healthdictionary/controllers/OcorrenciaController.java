package br.com.healthdictionary.controllers;

import br.com.healthdictionary.dto.OcorrenciaDTO;
import br.com.healthdictionary.services.OcorrenciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ocorrencia")
public class OcorrenciaController {

    private final OcorrenciaService ocorrenciaService;

    @Autowired
    public OcorrenciaController(OcorrenciaService ocorrenciaService) {
        this.ocorrenciaService = ocorrenciaService;
    }

    @GetMapping(value = "/{ocorrenciaId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OcorrenciaDTO> recuperaOcorrenciaPeloId(@PathVariable Long ocorrenciaId) {
        OcorrenciaDTO response = ocorrenciaService.recuperaOcorrenciaPeloId(ocorrenciaId);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/cpf/{pacienteCpf}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<OcorrenciaDTO>> recuperaOcorrenciaPeloCodigo(@PathVariable String pacienteCpf) {
        List<OcorrenciaDTO> response = ocorrenciaService.recuperaOcorrenciasPeloCpf(pacienteCpf);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/cpf/{cpfToNewOcorrencia}/save-ocorrencia", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OcorrenciaDTO> cadastraOcorrencia(@PathVariable String cpfToNewOcorrencia, @RequestBody OcorrenciaDTO request) {
        OcorrenciaDTO response = ocorrenciaService.cadastraOcorrencia(cpfToNewOcorrencia, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping(value = "/{ocorrenciaId}/update-ocorrencia", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OcorrenciaDTO> atualizaOcorrencia(@PathVariable Long ocorrenciaId, @RequestBody OcorrenciaDTO request) {
        OcorrenciaDTO response = ocorrenciaService.atualizaOcorrencia(ocorrenciaId, request);
        return ResponseEntity.ok(response);
    }

}
