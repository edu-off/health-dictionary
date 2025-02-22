package br.com.healthdictionary.services;

import br.com.healthdictionary.dto.UsuarioChangePasswordDTO;
import br.com.healthdictionary.dto.UsuarioDTO;
import br.com.healthdictionary.enums.UsuarioRole;
import br.com.healthdictionary.models.UsuarioModel;
import br.com.healthdictionary.repositories.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ModelMapper mapper;
    private final TokenService tokenService;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, ModelMapper mapper, TokenService tokenService) {
        this.usuarioRepository = usuarioRepository;
        this.mapper = mapper;
        this.tokenService = tokenService;
    }

    public List<UsuarioDTO> recuperaTodosUsuarios() {
        List<UsuarioModel> usuariosModel = usuarioRepository.findAll();
        List<UsuarioDTO> usuariosDto = new ArrayList<>();
        usuariosModel.forEach(usuarioModel -> {
            UsuarioDTO usuarioDto = mapper.map(usuarioModel, UsuarioDTO.class);
            usuarioDto.setRole(usuarioModel.getRole().getRole());
            usuariosDto.add(usuarioDto);
        });
        return usuariosDto;
    }

    public UsuarioDTO recuperaUsuarioPeloLogin(String login) {
        UsuarioModel usuarioModelReturned = usuarioRepository.findByLogin(login);

        if (Objects.isNull(usuarioModelReturned))
            throw new UsernameNotFoundException("usuário não encontrado");

        return mapper.map(usuarioModelReturned, UsuarioDTO.class);
    }

    public UsuarioDTO cadastraUsuario(UsuarioDTO usuarioDto) {
        if (usuarioRepository.findByLogin(usuarioDto.getLogin()) != null)
            throw new DuplicateKeyException("login já existe");

        String encryptedPassword = new BCryptPasswordEncoder().encode(usuarioDto.getPassword());
        UsuarioModel usuarioModel = mapper.map(usuarioDto, UsuarioModel.class);
        usuarioModel.setPassword(encryptedPassword);
        usuarioModel.setRole(UsuarioRole.get(usuarioDto.getRole()));
        UsuarioModel usuarioModelSaved = usuarioRepository.save(usuarioModel);
        return mapper.map(usuarioModelSaved, UsuarioDTO.class);
    }

    public UserDetails recuperaUsuarioPeloUsername(String username) {
        UserDetails userDetails = usuarioRepository.findByLogin(username);
        if (Objects.isNull(userDetails))
            throw new UsernameNotFoundException("usuário não encontrado");
        return userDetails;
    }

    public void trocaPassword(UsuarioChangePasswordDTO dto, String token) {
        UsuarioModel usuarioModel = usuarioRepository.findByLogin(dto.getLogin());
        if (Objects.isNull(usuarioModel))
            throw new UsernameNotFoundException("login não encontrado");

        String login = tokenService.validateToken(token.replace("Bearer ", ""));
        if (!dto.getLogin().equals(login))
            throw new AuthorizationDeniedException("o usuário logado não deve trocar a senha de outro usuário");

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if (!encoder.matches(dto.getOldPassword(), usuarioModel.getPassword()))
            throw new AuthorizationDeniedException("senha incorreta");

        if (dto.getOldPassword().equals(dto.getNewPassword()))
            throw new BadCredentialsException("nova senha não pode ser igual a anterior");

        usuarioModel.setPassword(encoder.encode(dto.getNewPassword()));
        usuarioRepository.save(usuarioModel);
    }

}