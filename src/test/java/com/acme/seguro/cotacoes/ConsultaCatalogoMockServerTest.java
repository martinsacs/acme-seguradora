package com.acme.seguro.cotacoes;

import com.acme.seguro.cotacoes.config.ConsultaCatalogoMockServerConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;

import static com.acme.seguro.cotacoes.config.ConsultaCatalogoMockServerConfig.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = {CotacoesApplication.class, ConsultaCatalogoMockServerConfig.class})
public class ConsultaCatalogoMockServerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testMockServerConsultaProdutoResponse() throws JsonProcessingException {
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8081/v1/consulta-produto/123", String.class);
        assertThat(response.getBody()).isEqualTo(new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(generateMockObjetoConsultaProduto()));
    }

    @Test
    public void testMockServerConsultaOfertaResponse() throws JsonProcessingException {
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8081/v1/consulta-oferta/123", String.class);
        assertThat(response.getBody()).isEqualTo(new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(generateMockObjetoConsultaOferta()));
    }
}
