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
public class CidDTO {

    @NotNull(message = "o campo codigo não pode ser nulo")
    @NotEmpty(message = "o campo codigo não pode ser vazio")
    private String codigo;

    @NotNull(message = "o campo descricao não pode ser nulo")
    @NotEmpty(message = "o campo descricao não pode ser vazio")
    private String descricao;

}
