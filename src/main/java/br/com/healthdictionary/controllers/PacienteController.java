package br.com.healthdictionary.controllers;

import br.com.healthdictionary.dto.PacienteDTO;
import br.com.healthdictionary.services.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/paciente")
public class PacienteController {

    private final PacienteService pacienteService;

    @Autowired
    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @GetMapping(value = "/{cpfToGetPaciente}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PacienteDTO> recuperaPacientePeloCpf(@PathVariable String cpfToGetPaciente) {
        PacienteDTO response = pacienteService.recuperaPacientePeloCpf(cpfToGetPaciente);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/save-paciente", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PacienteDTO> cadastraPaciente(@RequestBody PacienteDTO request) {
        PacienteDTO response = pacienteService.cadastraPaciente(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping(value = "/update-paciente/{cpfToUpdatePaciente}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PacienteDTO> atualizaPaciente(@PathVariable String cpfToUpdatePaciente, @RequestBody PacienteDTO request) {
        PacienteDTO response = pacienteService.atualizaPaciente(cpfToUpdatePaciente, request);
        return ResponseEntity.ok(response);
    }

}
