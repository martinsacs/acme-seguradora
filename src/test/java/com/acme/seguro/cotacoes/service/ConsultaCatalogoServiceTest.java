package com.acme.seguro.cotacoes.service;

import com.acme.seguro.cotacoes.model.output.mock.ConsultaOfertaOutput;
import com.acme.seguro.cotacoes.model.output.mock.ConsultaProdutoOutput;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ConsultaCatalogoService consultaCatalogoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConsultarProduto_Success() throws Exception {
        String productId = "prod123";
        String mockResponse = "{\"id\":\"1b2da7cc-b367-4196-8a78-9cfeec21f587\",\"name\":\"Seguro de Vida\",\"active\":true,\"createdAt\":\"2021-07-01T00:00:00Z\",\"offers\":[\"adc56d77-348c-4bf0-908f-22d402ee715c\"]}";
        ConsultaProdutoOutput mockProdutoOutput = new ConsultaProdutoOutput();
        mockProdutoOutput.setId("1b2da7cc-b367-4196-8a78-9cfeec21f587");
        mockProdutoOutput.setName("Seguro de Vida");
        mockProdutoOutput.setActive(true);

        when(restTemplate.getForEntity(anyString(), eq(String.class), eq(productId))).thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));
        when(objectMapper.readValue(mockResponse, ConsultaProdutoOutput.class)).thenReturn(mockProdutoOutput);

        ResponseEntity<ConsultaProdutoOutput> response = consultaCatalogoService.consultarProduto(productId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockProdutoOutput, response.getBody());
    }

    @Test
    void testConsultarProduto_Failure() {
        String productId = "prod123";
        when(restTemplate.getForEntity(anyString(), eq(String.class), eq(productId))).thenThrow(new RuntimeException("Erro"));

        ResponseEntity<String> response = consultaCatalogoService.consultarProduto(productId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("ERRO: Não foi possível consultar o catálogo de produtos.", response.getBody());
    }

    @Test
    void testConsultarOferta_Success() throws Exception {
        String offerId = "offer123";
        String mockResponse = "{\"id\":\"adc56d77-348c-4bf0-908f-22d402ee715c\",\"productId\":\"1b2da7cc-b367-4196-8a78-9cfeec21f587\",\"name\":\"Seguro de Vida Familiar\",\"active\":true,\"createdAt\":\"2021-07-01T00:00:00Z\"}";
        ConsultaOfertaOutput mockOfertaOutput = new ConsultaOfertaOutput();
        mockOfertaOutput.setId("adc56d77-348c-4bf0-908f-22d402ee715c");
        mockOfertaOutput.setProductId("1b2da7cc-b367-4196-8a78-9cfeec21f587");
        mockOfertaOutput.setName("Seguro de Vida Familiar");
        mockOfertaOutput.setActive(true);

        when(restTemplate.getForEntity(anyString(), eq(String.class), eq(offerId))).thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));
        when(objectMapper.readValue(mockResponse, ConsultaOfertaOutput.class)).thenReturn(mockOfertaOutput);

        ResponseEntity<ConsultaOfertaOutput> response = consultaCatalogoService.consultarOferta(offerId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockOfertaOutput, response.getBody());
    }

    @Test
    void testConsultarOferta_Failure() {
        String offerId = "offer123";
        when(restTemplate.getForEntity(anyString(), eq(String.class), eq(offerId))).thenThrow(new RuntimeException("Erro"));

        ResponseEntity<String> response = consultaCatalogoService.consultarOferta(offerId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("ERRO: Não foi possível consultar o catálogo de ofertas.", response.getBody());
    }
}