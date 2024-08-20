package com.acme.seguro.cotacoes;

import com.acme.seguro.cotacoes.service.ConsultaCatalogoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class ConsultaCatalogoServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ConsultaCatalogoService consultaCatalogoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConsultarProduto_Success() {
        String productId = "prod123";
        String mockResponse = "{\"id\":\"prod123\",\"name\":\"Produto Teste\"}";
        when(restTemplate.getForEntity(anyString(), eq(String.class), anyString())).thenReturn(new ResponseEntity<String>(mockResponse, HttpStatus.OK));

        ResponseEntity<String> response = consultaCatalogoService.consultarProduto(productId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
    }

    @Test
    void testConsultarProduto_Failure() {
        String productId = "prod123";
        when(restTemplate.getForEntity(anyString(), eq(String.class), anyString())).thenThrow(new RuntimeException("Erro"));

        ResponseEntity<String> response = consultaCatalogoService.consultarProduto(productId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("ERRO: Não foi possível consultar o catálogo de produtos.", response.getBody());
    }

    @Test
    void testConsultarOferta_Success() {
        String offerId = "offer123";
        String mockResponse = "{\"id\":\"offer123\",\"name\":\"Oferta Teste\"}";
        when(restTemplate.getForEntity(anyString(), eq(String.class), anyString())).thenReturn(new ResponseEntity<String>(mockResponse, HttpStatus.OK));

        ResponseEntity<String> response = consultaCatalogoService.consultarOferta(offerId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
    }

    @Test
    void testConsultarOferta_Failure() {
        String offerId = "offer123";
        when(restTemplate.getForEntity(anyString(), eq(String.class), anyString())).thenThrow(new RuntimeException("Erro"));

        ResponseEntity<String> response = consultaCatalogoService.consultarOferta(offerId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("ERRO: Não foi possível consultar o catálogo de ofertas.", response.getBody());
    }
}