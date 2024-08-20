package com.acme.seguro.cotacoes.model.db;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Entity
@Data
public class CotacaoSeguroEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long insurancePolicyId;

    private String productId;

    private String offerId;

    private String category;

    private BigDecimal totalMonthlyPremiumAmount;

    private BigDecimal totalCoverageAmount;

    @OneToMany
    private List<CoberturaEntity> coverages;

    private List<String> assistances;

    @OneToOne
    private CustomerEntity customer;

    public CotacaoSeguroEntity(String productId, String offerId, String category, BigDecimal totalMonthlyPremiumAmount, BigDecimal totalCoverageAmount, List<CoberturaEntity> coverages, List<String> assistances, CustomerEntity customer) {
        this.productId = productId;
        this.offerId = offerId;
        this.category = category;
        this.totalMonthlyPremiumAmount = totalMonthlyPremiumAmount;
        this.totalCoverageAmount = totalCoverageAmount;
        this.coverages = coverages;
        this.assistances = assistances;
        this.customer = customer;
    }

    public CotacaoSeguroEntity() {

    }
}
