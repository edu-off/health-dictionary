package br.com.healthdictionary.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OcorrenciaDTO {

    private Long id;

    @NotNull(message = "o campo sintomas não pode ser nulo")
    @NotEmpty(message = "o campo sintomas não pode ser vazio")
    private String sintomas;

    @NotNull(message = "o campo diagnostico não pode ser nulo")
    @NotEmpty(message = "o campo diagnostico não pode ser vazio")
    private String diagnostico;

    private LocalDateTime registro;

}
