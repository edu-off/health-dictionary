package br.com.healthdictionary.repositories;

import br.com.healthdictionary.models.MedicacaoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicacaoRepository extends JpaRepository<MedicacaoModel, Long> {

    List<MedicacaoModel> findAllByTratamentoId(Long tratamentoId);

}
