package br.com.healthdictionary.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ocorrencia")
public class OcorrenciaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sintomas;

    private String diagnostico;

    private LocalDateTime registro;

    @ManyToOne
    @JoinColumn(columnDefinition = "paciente_cpf", referencedColumnName = "cpf")
    private PacienteModel paciente;

}
