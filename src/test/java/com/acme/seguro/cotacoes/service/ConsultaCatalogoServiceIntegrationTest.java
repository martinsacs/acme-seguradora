package com.acme.seguro.cotacoes.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConsultaCatalogoServiceIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ConsultaCatalogoService consultaCatalogoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConsultarProduto_Success() {
        ResponseEntity<String> response = consultaCatalogoService.consultarProduto("1b2da7cc-b367-4196-8a78-9cfeec21f587");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testConsultarProduto_Failure() {
        String productId = "prod123";
        ResponseEntity<String> response = consultaCatalogoService.consultarProduto(productId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("ERRO: Não foi possível consultar o catálogo de produtos.", response.getBody());
    }

    @Test
    void testConsultarOferta_Success() {
        ResponseEntity<String> response = consultaCatalogoService.consultarOferta("adc56d77-348c-4bf0-908f-22d402ee715c");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testConsultarOferta_Failure() {
        String offerId = "offer123";
        ResponseEntity<String> response = consultaCatalogoService.consultarOferta(offerId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("ERRO: Não foi possível consultar o catálogo de ofertas.", response.getBody());
    }
}