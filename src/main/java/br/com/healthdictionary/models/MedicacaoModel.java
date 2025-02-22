package br.com.healthdictionary.models;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "medicacao")
public class MedicacaoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String medicacao;

    private Double dosagem;

    @Column(name = "quantidade_dias")
    private Integer quantidadeDias;

    private String observacao;

    @ManyToOne
    @JoinColumn(columnDefinition = "tratamento_id", referencedColumnName = "id")
    private TratamentoModel tratamento;

}
