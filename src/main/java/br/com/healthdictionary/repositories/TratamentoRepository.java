package br.com.healthdictionary.repositories;

import br.com.healthdictionary.models.TratamentoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TratamentoRepository extends JpaRepository<TratamentoModel, Long> {

    List<TratamentoModel> findAllByCidCodigo(String cidCodigo);

}
