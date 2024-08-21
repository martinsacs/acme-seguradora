package com.acme.seguro.cotacoes.config;

import com.acme.seguro.cotacoes.model.MonthlyPremiumAmount;
import com.acme.seguro.cotacoes.model.db.CoberturaEntity;
import com.acme.seguro.cotacoes.model.output.mock.ConsultaOfertaOutput;
import com.acme.seguro.cotacoes.model.output.mock.ConsultaProdutoOutput;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;

@Configuration
public class ConsultaCatalogoMockServerConfig {

    private WireMockServer wireMockServer;

    @PostConstruct
    public void startServer() throws JsonProcessingException {
       wireMockServer = new WireMockServer(8081);
       wireMockServer.start();

        WireMock.configureFor("localhost", 8081);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        WireMock.stubFor(WireMock.get(urlPathMatching("/v1/consulta-produto/[a-zA-Z0-9-]+"))
            .willReturn(WireMock.aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(objectMapper.writer().withDefaultPrettyPrinter().writeValueAsString(generateMockObjetoConsultaProduto()))));

        WireMock.stubFor(WireMock.get(urlPathMatching("/v1/consulta-oferta/[a-zA-Z0-9-]+"))
            .willReturn(WireMock.aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(objectMapper.writer().withDefaultPrettyPrinter().writeValueAsString(generateMockObjetoConsultaOferta()))));
    }

    @PreDestroy
    public void stopServer() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    public static ResponseEntity<ConsultaProdutoOutput> mockResponseEntity(ConsultaProdutoOutput body, HttpStatus status) {
        return new ResponseEntity<>(body, status);
    }

    public static ConsultaProdutoOutput generateMockObjetoConsultaProduto() {
        return new ConsultaProdutoOutput() {{
            setId("1b2da7cc-b367-4196-8a78-9cfeec21f587");
            setName("Seguro de Vida");
            setActive(true);
            setCreatedAt(Timestamp.from(OffsetDateTime.parse("2021-07-01T00:00:00Z").toInstant()));
            setOffers(generateOffers());
        }};
    }

    private static List<String> generateOffers() {
        return new ArrayList<>(List.of(
            "adc56d77-348c-4bf0-908f-22d402ee715c",
            "bdc56d77-348c-4bf0-908f-22d402ee715c",
            "cdc56d77-348c-4bf0-908f-22d402ee715c"));

    }
    public static ConsultaOfertaOutput generateMockObjetoConsultaOferta() {
        ConsultaOfertaOutput consultaOfertaOutput = new ConsultaOfertaOutput();
        consultaOfertaOutput.setId("adc56d77-348c-4bf0-908f-22d402ee715c");
        consultaOfertaOutput.setProductId("1b2da7cc-b367-4196-8a78-9cfeec21f587");
        consultaOfertaOutput.setName("Seguro de Vida Familiar");
        consultaOfertaOutput.setCreatedAt(Timestamp.from(OffsetDateTime.parse("2021-07-01T00:00:00Z").toInstant()));
        consultaOfertaOutput.setActive(true);
        consultaOfertaOutput.setCoverages(generateCoverages());
        consultaOfertaOutput.setAssistances(generateAssistances());
        consultaOfertaOutput.setMonthlyPremiumAmount(generateMonthlyPremiumAmount());
        return consultaOfertaOutput;
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

    public static List<CoberturaEntity> generateCoverages() {
        return List.of(
                new CoberturaEntity("Incêndio", BigDecimal.valueOf(500_000.00)),
                new CoberturaEntity("Desastres naturais", BigDecimal.valueOf(600_000.00)),
                new CoberturaEntity("Responsabilidade civil", BigDecimal.valueOf(80_000.00)),
                new CoberturaEntity("Roubo", BigDecimal.valueOf(100_000.00))
        );
    }

}