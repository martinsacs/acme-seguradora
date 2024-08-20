package com.acme.seguro.cotacoes.model.db;

import com.acme.seguro.cotacoes.model.Customer;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Entity
public class CotacaoSeguro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Optional<Long> insurancePolicyId;

    private String productId;

    private String offerId;

    private String category;

    private BigDecimal totalMonthlyPremiumAmount;

    private BigDecimal totalCoverageAmount;

    private Map<String, BigDecimal> coverages;

    private List<String> assistances;

    private Customer customer;
}
