package com.acme.seguro.cotacoes.model.db;

import com.acme.seguro.cotacoes.utils.JsonUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Entity
@Data
@Table(name="tb_cotacoes")
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

    @JsonIgnore
    @Column(name = "coverages", columnDefinition = "TEXT")
    private String coveragesJson;

    @Transient
    private Map<String, BigDecimal> coverages;

    private List<String> assistances;

    @OneToOne
    private CustomerEntity customer;

    public CotacaoSeguroEntity(String productId, String offerId, String category, BigDecimal totalMonthlyPremiumAmount, BigDecimal totalCoverageAmount, Map<String, BigDecimal> coverages, List<String> assistances, CustomerEntity customer) {
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

    @PrePersist
    @PreUpdate
    private void convertCoveragesToJson() throws JsonProcessingException {
        this.coveragesJson = JsonUtils.convertMapToJson(this.coverages);
    }

    @PostLoad
    private void convertJsonToCoverages() throws JsonProcessingException, JsonProcessingException {
        this.coverages = JsonUtils.convertJsonToMap(this.coveragesJson);
    }
}
