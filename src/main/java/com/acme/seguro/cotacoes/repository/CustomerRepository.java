package com.acme.seguro.cotacoes.repository;

import com.acme.seguro.cotacoes.model.db.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {

}
