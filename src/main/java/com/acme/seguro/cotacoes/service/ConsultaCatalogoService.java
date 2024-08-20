package com.acme.seguro.cotacoes.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ConsultaCatalogoService {
    @Autowired
    private RestTemplate restTemplate;

    public ResponseEntity consultarProduto(String productId) {
        ResponseEntity<String> response;
        String url = "http://localhost:8081/v1/consulta-produto/{product_id}";
        try {
            response = restTemplate.getForEntity(url, String.class, productId);
        } catch (Exception ex){
            System.out.println("ERRO: Não foi possível consultar o catálogo de produtos.");
            response = new ResponseEntity<>("ERRO: Não foi possível consultar o catálogo de produtos.", HttpStatusCode.valueOf(500));
        }

        return response;
    }

    public ResponseEntity consultarOferta(String offerId) {
        ResponseEntity<String> response;
        String url = "http://localhost:8081/v1/consulta-oferta/{offer_id}";
        try {
            response = restTemplate.getForEntity(url, String.class, offerId);
        } catch (Exception ex){
            System.out.println("ERRO: Não foi possível consultar o catálogo de ofertas.");
            response = new ResponseEntity<>("ERRO: Não foi possível consultar o catálogo de ofertas.", HttpStatusCode.valueOf(500));
        }
        return response;
    }
}
