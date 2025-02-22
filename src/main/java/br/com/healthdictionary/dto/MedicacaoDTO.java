package br.com.healthdictionary.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MedicacaoDTO {

    private Long id;

    @NotNull(message = "o campo nome não pode ser nulo")
    @NotEmpty(message = "o campo nome não pode ser vazio")
    private String medicacao;

    @NotNull(message = "o campo dosagem não pode ser nulo")
    @Positive(message = "o campo dosagem nao pode ser zero ou menor que zero")
    @Digits(integer = 3, fraction = 2, message = "o campo dosagem não pode ter mais que 2 casas decimais e não pode ter mais que 3 dígitos")
    private Double dosagem;

    @NotNull(message = "o campo quantidade de dias não pode ser nulo")
    @Positive(message = "o campo quantidade de dias nao pode ser zero ou menor que zero")
    @Digits(integer = 4, fraction = 0, message = "o campo quantidade de dias não pode ter casas decimais e não pode ter mais que 4 dígitos")
    private Integer quantidadeDias;

    private String observacao;

}
