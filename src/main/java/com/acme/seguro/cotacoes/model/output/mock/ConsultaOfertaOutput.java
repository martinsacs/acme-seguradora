package com.acme.seguro.cotacoes.model.output.mock;

import com.acme.seguro.cotacoes.model.MonthlyPremiumAmount;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Data
public class ConsultaOfertaOutput {
    private String id;

    @JsonProperty("product_id")
    private String productId;

    private String name;

    @JsonProperty("created_at")
    private Timestamp createdAt;

    private Boolean active;

    private Map<String, BigDecimal> coverages;

    private List<String> assistances;

    @JsonProperty("monthly_premium_amount")
    private MonthlyPremiumAmount monthlyPremiumAmount;
}
