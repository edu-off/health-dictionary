package br.com.healthdictionary.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "paciente")
public class PacienteModel {

    @Id
    private String cpf;

    private String nome;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Column(name = "codigo_sus")
    private String codigoSus;

    @OneToMany(mappedBy = "paciente")
    private List<OcorrenciaModel> ocorrencias;

}
