package br.com.healthdictionary.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tratamento")
public class TratamentoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tratamento;

    @Column(name = "referencia_bibliografica")
    private String referenciaBibliografica;

    @ManyToOne
    @JoinColumn(columnDefinition = "cid_codigo", referencedColumnName = "codigo")
    private CidModel cid;

    @OneToMany(mappedBy = "tratamento")
    private List<MedicacaoModel> medicacoes;

}
