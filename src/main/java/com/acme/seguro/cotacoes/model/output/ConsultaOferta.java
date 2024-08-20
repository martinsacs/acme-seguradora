package com.acme.seguro.cotacoes.model.output;

import com.acme.seguro.cotacoes.model.MonthlyPremiumAmount;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;
import java.util.Map;

@Data
public class ConsultaOferta {
    private String id;

    @JsonProperty("product_id")
    private String productId;

    private String name;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    private Boolean active;

    private Map<String, BigDecimal> coverages;

    private List<String> assistances;

    @JsonProperty("monthly_premium_amount")
    private MonthlyPremiumAmount monthlyPremiumAmount;
}
