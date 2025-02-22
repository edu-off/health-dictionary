package br.com.healthdictionary.controllers;

import br.com.healthdictionary.dto.CidDTO;
import br.com.healthdictionary.services.CidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cid")
public class CidController {

    private final CidService cidService;

    @Autowired
    public CidController(CidService cidService) {
        this.cidService = cidService;
    }

    @GetMapping(value = "/get-all-cids", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CidDTO>> recuperaTodosOsCids() {
        List<CidDTO> response = cidService.recuperaTodosOsCids();
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/{codigoCid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CidDTO> recuperaCidPeloCodigo(@PathVariable String codigoCid) {
        CidDTO response = cidService.recuperaCidPeloCodigo(codigoCid);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/save-cid", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CidDTO> cadastraCid(@RequestBody CidDTO request) {
        CidDTO response = cidService.cadastraCid(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping(value = "/update-cid/{codigoCid}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CidDTO> atualizaCid(@PathVariable String codigoCid, @RequestBody CidDTO request) {
        CidDTO response = cidService.atualizaCid(codigoCid, request);
        return ResponseEntity.ok(response);
    }

}
