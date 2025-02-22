package br.com.healthdictionary.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cid")
public class CidModel {

    @Id
    private String codigo;

    private String descricao;

    @OneToMany(mappedBy = "cid")
    private List<TratamentoModel> tratamentos;

}
