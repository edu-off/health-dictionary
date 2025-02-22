package br.com.healthdictionary.repositories;

import br.com.healthdictionary.models.OcorrenciaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OcorrenciaRepository extends JpaRepository<OcorrenciaModel, Long> {

    List<OcorrenciaModel> findAllByPacienteCpf(String pacienteCpf);

}
