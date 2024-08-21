package com.acme.seguro.cotacoes.model.db;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name="tb_coberturas")
@Data
public class CoberturaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descricao;

    private BigDecimal valor;

    public CoberturaEntity(String descricao, BigDecimal valor) {
        this.descricao = descricao;
        this.valor = valor;
    }
}
