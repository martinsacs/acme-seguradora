package com.acme.seguro.cotacoes.config;

import com.acme.seguro.cotacoes.model.MonthlyPremiumAmount;
import com.acme.seguro.cotacoes.model.output.ConsultaOferta;
import com.acme.seguro.cotacoes.model.output.ConsultaProduto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Configuration
public class ConsultaCatalogoMockServerConfig {

    private WireMockServer wireMockServer;

    @PostConstruct
    public void startServer() throws JsonProcessingException {
        wireMockServer = new WireMockServer(8081);
        wireMockServer.start();

        WireMock.configureFor("localhost", 8081);

        WireMock.stubFor(WireMock.get("/v1/consulta-produto/.*")
            .willReturn(WireMock.aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(generateMockObjetoConsultaProduto()))));

        WireMock.stubFor(WireMock.get("/v1/consulta-oferta")
            .willReturn(WireMock.aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(generateMockObjetoConsultaOferta()))));
    }

    @PreDestroy
    public void stopServer() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    public static ResponseEntity<ConsultaProduto> mockResponseEntity(ConsultaProduto body, HttpStatus status) {
        return new ResponseEntity<>(body, status);
    }

    public static ConsultaProduto generateMockObjetoConsultaProduto() {
        return new ConsultaProduto() {{
            setId("1b2da7cc-b367-4196-8a78-9cfeec21f587");
            setName("Seguro de Vida");
            setActive(true);
            setCreatedAt(LocalDateTime.of(2021, 7, 1, 0, 0, 0));
            setOffers(generateOffers());
        }};
    }

    private static List<String> generateOffers() {
        return new ArrayList<>(List.of(
            "adc56d77-348c-4bf0-908f-22d402ee715c",
            "bdc56d77-348c-4bf0-908f-22d402ee715c",
            "cdc56d77-348c-4bf0-908f-22d402ee715c"));

    }
    public static ConsultaOferta generateMockObjetoConsultaOferta() {
        ConsultaOferta consultaOferta = new ConsultaOferta();
        consultaOferta.setId("adc56d77-348c-4bf0-908f-22d402ee715c");
        consultaOferta.setProductId("1b2da7cc-b367-4196-8a78-9cfeec21f587");
        consultaOferta.setName("Seguro de Vida Familiar");
        consultaOferta.setCreatedAt(LocalDateTime.of(2021,07,01, 00, 00, 00));
        consultaOferta.setActive(true);
        consultaOferta.setCoverages(generateCoverages());
        consultaOferta.setAssistances(generateAssistances());
        consultaOferta.setMonthlyPremiumAmount(generateMonthlyPremiumAmount());
        return consultaOferta;
    }

    private static List<String> generateAssistances() {
        return new ArrayList<>(List.of("Encanador", "Eletricista", "Chaveiro 24h", "Assistência Funerária"));
    }

    private static MonthlyPremiumAmount generateMonthlyPremiumAmount() {
        return new MonthlyPremiumAmount() {{
            setMaxAmount(BigDecimal.valueOf(100.74));
            setMinAmount(BigDecimal.valueOf(50.00));
            setSuggestedAmount(BigDecimal.valueOf(60.25));
        }};
    }

    private static Map<String, BigDecimal> generateCoverages() {
        return Map.of(
            "Incêndio", BigDecimal.valueOf(500_000.00),
            "Desastres naturais", BigDecimal.valueOf(600_000.00),
            "Responsabilidade civil", BigDecimal.valueOf(80_000.00),
            "Roubo", BigDecimal.valueOf(100_000.00)
        );
    }
}