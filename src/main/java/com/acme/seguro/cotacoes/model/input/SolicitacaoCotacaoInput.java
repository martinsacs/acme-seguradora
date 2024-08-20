package com.acme.seguro.cotacoes.model.input;

import com.acme.seguro.cotacoes.model.db.CustomerEntity;
import com.acme.seguro.cotacoes.model.db.CoberturaEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Data
public class SolicitacaoCotacaoInput {
    @JsonProperty("product_id")
    private String productId;

    @JsonProperty("offer_id")
    private String offerId;

    private String category;

    @JsonProperty("created_at")
    private Timestamp createdAt;

    @JsonProperty("updated_at")
    private Optional<Timestamp> updatedAt;

    @JsonProperty("total_monthly_premium_amount")
    private BigDecimal totalMonthlyPremiumAmount;

    @JsonProperty("total_coverage_amount")
    private BigDecimal totalCoverageAmount;

    @JsonProperty("coverages")
    private List<CoberturaEntity> coverages;

    private List<String> assistances;

    private CustomerEntity customer;
}
