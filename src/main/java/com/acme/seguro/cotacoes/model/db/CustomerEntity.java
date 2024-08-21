package com.acme.seguro.cotacoes.model.db;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name="tb_customers")
public class CustomerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("customer_id")
    private Long id;

    @JsonProperty("document_number")
    private String documentNumber;

    private String name;

    private String type;

    private String gender;

    @JsonProperty("date_of_birth")
    private Date dateOfBirth;

    private String email;

    @JsonProperty("phone_number")
    private String phoneNumber;

    public CustomerEntity() {
    }
}
