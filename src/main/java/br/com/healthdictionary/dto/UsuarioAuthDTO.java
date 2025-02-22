package br.com.healthdictionary.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioAuthDTO {

    @NotNull(message = "o campo login não pode ser nulo")
    @NotEmpty(message = "o campo login não pode ser vazio")
    private String login;

    @NotNull(message = "o campo password não pode ser nulo")
    @NotEmpty(message = "o campo password não pode ser vazio")
    private String password;

}
