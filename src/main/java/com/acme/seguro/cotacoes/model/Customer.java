package com.acme.seguro.cotacoes.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class Customer {
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
}
