package com.acme.seguro.cotacoes.service;

import com.acme.seguro.cotacoes.model.output.mock.ConsultaOfertaOutput;
import com.acme.seguro.cotacoes.model.output.mock.ConsultaProdutoOutput;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ConsultaCatalogoService {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public ResponseEntity consultarProduto(String productId) {
        String url = "http://localhost:8081/v1/consulta-produto/{product_id}";
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class, productId);
            ConsultaProdutoOutput produtoOutput = objectMapper.readValue(response.getBody(), ConsultaProdutoOutput.class);
            return new ResponseEntity<>(produtoOutput, response.getStatusCode());
        } catch (Exception ex){
            System.out.println("ERRO: Não foi possível consultar o catálogo de produtos.");
            return new ResponseEntity<>("ERRO: Não foi possível consultar o catálogo de produtos.", HttpStatusCode.valueOf(500));
        }

    }

    public ResponseEntity consultarOferta(String offerId) {
        String url = "http://localhost:8081/v1/consulta-oferta/{offer_id}";
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class, offerId);
            ConsultaOfertaOutput ofertaOutput = objectMapper.readValue(response.getBody(), ConsultaOfertaOutput.class);
            return new ResponseEntity<>(ofertaOutput, response.getStatusCode());
        } catch (Exception ex) {
            System.out.println("ERRO: Não foi possível consultar o catálogo de ofertas.");
            return new ResponseEntity<>("ERRO: Não foi possível consultar o catálogo de ofertas.", HttpStatusCode.valueOf(500));
        }
    }
}
