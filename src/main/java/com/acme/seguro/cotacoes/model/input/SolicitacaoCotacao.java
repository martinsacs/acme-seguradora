package com.acme.seguro.cotacoes.model.input;

import com.acme.seguro.cotacoes.model.Customer;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Data
public class SolicitacaoCotacao {
    @JsonProperty("product_id")
    private String productId;

    @JsonProperty("offer_id")
    private String offerId;

    private String category;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private Optional<LocalDateTime> updatedAt;

    @JsonProperty("total_monthly_premium_amount")
    private BigDecimal totalMonthlyPremiumAmount;

    @JsonProperty("total_coverage_amount")
    private BigDecimal totalCoverageAmount;

    @JsonProperty("coverages")
    private Map<String, BigDecimal> coverages;

    private List<String> assistances;

    private Customer customer;
}
