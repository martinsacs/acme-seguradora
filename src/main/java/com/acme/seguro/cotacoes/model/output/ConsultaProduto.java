package com.acme.seguro.cotacoes.model.output;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ConsultaProduto {
    private String id;
    private String name;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    private Boolean active;

    private List<String> offers;

}
