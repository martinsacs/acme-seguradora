package com.acme.seguro.cotacoes.model.db;

import com.acme.seguro.cotacoes.model.Customer;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Entity
@Data
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

    @OneToMany
    private List<CoberturaDb> coverages;

    private List<String> assistances;

    //@OneToOne - ENTIDADE 'CUSTOMER' NÃO SERÁ CRIADA NESTA ENTREGA
    private Customer customer;


    public CotacaoSeguro(String productId, String offerId, String category, BigDecimal totalMonthlyPremiumAmount, BigDecimal totalCoverageAmount, List<CoberturaDb> coverages, List<String> assistances, Customer customer) {
        this.productId = productId;
        this.offerId = offerId;
        this.category = category;
        this.totalMonthlyPremiumAmount = totalMonthlyPremiumAmount;
        this.totalCoverageAmount = totalCoverageAmount;
        this.coverages = coverages;
        this.assistances = assistances;
        this.customer = customer;
    }
}
