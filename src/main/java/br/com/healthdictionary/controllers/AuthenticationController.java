package br.com.healthdictionary.controllers;

import br.com.healthdictionary.dto.ResponseAuthDTO;
import br.com.healthdictionary.dto.UsuarioAuthDTO;
import br.com.healthdictionary.models.UsuarioModel;
import br.com.healthdictionary.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationManager authManager;
    private final TokenService tokenService;

    @Autowired
    public AuthenticationController(AuthenticationManager authManager, TokenService tokenService) {
        this.authManager = authManager;
        this.tokenService = tokenService;
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseAuthDTO> login(@RequestBody UsuarioAuthDTO request) {
        UsernamePasswordAuthenticationToken usernamePassword = new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword());
        Authentication auth = authManager.authenticate(usernamePassword);
        String token = tokenService.generateToken((UsuarioModel) auth.getPrincipal());
        return ResponseEntity.ok(new ResponseAuthDTO(token));
    }

}
