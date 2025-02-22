package br.com.healthdictionary.repositories;

import br.com.healthdictionary.models.CidModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CidRepository extends JpaRepository<CidModel, String> {
}
