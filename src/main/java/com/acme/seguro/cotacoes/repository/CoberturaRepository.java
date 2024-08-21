package com.acme.seguro.cotacoes.repository;

import com.acme.seguro.cotacoes.model.db.CoberturaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoberturaRepository extends JpaRepository<CoberturaEntity, Long> {

}
