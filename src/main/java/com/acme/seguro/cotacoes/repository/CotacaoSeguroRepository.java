package com.acme.seguro.cotacoes.repository;

import com.acme.seguro.cotacoes.model.db.CotacaoSeguroEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CotacaoSeguroRepository extends JpaRepository<CotacaoSeguroEntity, Long> {

}
