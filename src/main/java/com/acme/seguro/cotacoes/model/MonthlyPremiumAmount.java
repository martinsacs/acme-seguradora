package com.acme.seguro.cotacoes.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MonthlyPremiumAmount {
    @JsonProperty("min_amount")
    private BigDecimal minAmount;

    @JsonProperty("max_amount")
    private BigDecimal maxAmount;

    @JsonProperty("suggested_amount")
    private BigDecimal suggestedAmount;

    public MonthlyPremiumAmount(BigDecimal minAmount, BigDecimal maxAmount, BigDecimal suggestedAmount) {
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.suggestedAmount = suggestedAmount;
    }

    public MonthlyPremiumAmount() {
    }
}
