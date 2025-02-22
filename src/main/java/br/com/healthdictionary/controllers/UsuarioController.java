package br.com.healthdictionary.controllers;

import br.com.healthdictionary.dto.UsuarioChangePasswordDTO;
import br.com.healthdictionary.dto.UsuarioDTO;
import br.com.healthdictionary.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping(value = "/get-all-usuarios", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UsuarioDTO>> recuperaTodosUsuarios() {
        return ResponseEntity.ok(usuarioService.recuperaTodosUsuarios());
    }

    @GetMapping(value = "/{login}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UsuarioDTO> recuperaUsuarioPeloLogin(@PathVariable String login) {
        UsuarioDTO response = usuarioService.recuperaUsuarioPeloLogin(login);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/save-usuario", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UsuarioDTO> cadastraUsuario(@RequestBody UsuarioDTO request) {
        UsuarioDTO response = usuarioService.cadastraUsuario(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping(value = "/change-password", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> trocaSenha(@RequestBody UsuarioChangePasswordDTO request, @RequestHeader("Authorization") String token) {
        usuarioService.trocaPassword(request, token);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
