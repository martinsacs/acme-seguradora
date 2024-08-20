package com.acme.seguro.cotacoes.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MonthlyPremiumAmount {
    @JsonProperty("max_amount")
    private BigDecimal maxAmount;

    @JsonProperty("min_amount")
    private BigDecimal minAmount;

    @JsonProperty("suggested_amount")
    private BigDecimal suggestedAmount;
}
