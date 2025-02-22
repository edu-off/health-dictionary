package br.com.healthdictionary.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PacienteDTO {


    @NotNull(message = "o campo cpf não pode ser nulo")
    @NotEmpty(message = "o campo cpf não pode ser vazio")
    private String cpf;

    @NotNull(message = "o campo nome não pode ser nulo")
    @NotEmpty(message = "o campo nome não pode ser vazio")
    private String nome;

    @NotNull(message = "o campo data de nascimento não pode ser nulo")
    @NotEmpty(message = "o campo data de nascimento não pode ser vazio")
    private LocalDate dataNascimento;

    @NotNull(message = "o campo codigo do sus não pode ser nulo")
    @NotEmpty(message = "o campo codigo do sus não pode ser vazio")
    private String codigoSus;

}
