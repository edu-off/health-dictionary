package br.com.healthdictionary.controllers;

import br.com.healthdictionary.dto.MedicacaoDTO;
import br.com.healthdictionary.dto.TratamentoDTO;
import br.com.healthdictionary.services.TratamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tratamento")
public class TratamentoController {

    private final TratamentoService tratamentoService;

    @Autowired
    public TratamentoController(TratamentoService tratamentoService) {
        this.tratamentoService = tratamentoService;
    }

    @GetMapping(value = "/{tratamentoId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TratamentoDTO> recuperaTratamentoPeloId(@PathVariable Long tratamentoId) {
        TratamentoDTO response = tratamentoService.recuperaTratamentoPeloId(tratamentoId);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/codigo-cid/{codigoCid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TratamentoDTO>> recuperaTratamentosPeloCodigoDoCid(@PathVariable String codigoCid) {
        List<TratamentoDTO> response = tratamentoService.recuperaTratamentosPeloCodigoCid(codigoCid);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/codigo-cid/{codigoCid}/save-tratamento", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TratamentoDTO> cadastraTratamento(@PathVariable String codigoCid, @RequestBody TratamentoDTO request) {
        TratamentoDTO response = tratamentoService.cadastraTratamento(codigoCid, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping(value = "/{tratamentoId}/update-tratamento", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TratamentoDTO> atualizaTratamento(@PathVariable Long tratamentoId, @RequestBody TratamentoDTO request) {
        TratamentoDTO response = tratamentoService.atualizaTratamento(tratamentoId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/{tratamentoId}/medicacao/save-medicacao", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MedicacaoDTO> adicionaMedicacaoNoTratamento(@PathVariable Long tratamentoId, @RequestBody MedicacaoDTO request) {
        MedicacaoDTO response = tratamentoService.adicionaMedicacaoNoTratamento(tratamentoId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping(value = "/medicacao/{medicacaoId}/delete-medicacao")
    public ResponseEntity<String> excluiMedicacaoNoTratamento(@PathVariable Long medicacaoId) {
        tratamentoService.excluiMedicacaoNoTratamento(medicacaoId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
