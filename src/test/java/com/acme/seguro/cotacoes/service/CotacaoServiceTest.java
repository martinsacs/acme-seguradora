package com.acme.seguro.cotacoes.service;


import com.acme.seguro.cotacoes.model.MonthlyPremiumAmount;
import com.acme.seguro.cotacoes.model.db.CotacaoSeguroEntity;
import com.acme.seguro.cotacoes.model.input.SolicitacaoCotacaoInput;
import com.acme.seguro.cotacoes.model.output.mock.ConsultaOfertaOutput;
import com.acme.seguro.cotacoes.model.output.mock.ConsultaProdutoOutput;
import com.acme.seguro.cotacoes.repository.CotacaoSeguroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CotacaoServiceTest {

	@Mock
	private ConsultaCatalogoService consultaCatalogoService;

	@Mock
	private CotacaoSeguroRepository cotacaoSeguroRepository;

	@InjectMocks
	private CotacaoService cotacaoService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testCotar_Success() {
		SolicitacaoCotacaoInput solicitacao = gerarObjetoSolicitacao(BigDecimal.valueOf(100), BigDecimal.ZERO, Collections.emptyMap(), Collections.emptyList());

		ConsultaProdutoOutput produtoOutput = new ConsultaProdutoOutput();
		produtoOutput.setActive(true);

		ConsultaOfertaOutput ofertaOutput = gerarObjetoConsultaOferta(true, new MonthlyPremiumAmount(BigDecimal.valueOf(50), BigDecimal.valueOf(150), BigDecimal.valueOf(60)), Collections.emptyMap(), Collections.emptyList());

		when(consultaCatalogoService.consultarProduto("prod123")).thenReturn(new ResponseEntity<>(produtoOutput, HttpStatus.OK));
		when(consultaCatalogoService.consultarOferta("offer123")).thenReturn(new ResponseEntity<>(ofertaOutput, HttpStatus.OK));
		when(cotacaoSeguroRepository.save(any(CotacaoSeguroEntity.class))).thenReturn(new CotacaoSeguroEntity());

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance();
		ResponseEntity response = cotacaoService.cotar(solicitacao, uriBuilder);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());
	}

	@Test
	void testCotar_inexistentProductAndOfferError() {
		SolicitacaoCotacaoInput solicitacao = new SolicitacaoCotacaoInput();
		solicitacao.setProductId("prod123");
		solicitacao.setOfferId("offer123");

		when(consultaCatalogoService.consultarProduto("prod123")).thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
		when(consultaCatalogoService.consultarOferta("offer123")).thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance();
		ResponseEntity response = cotacaoService.cotar(solicitacao, uriBuilder);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertTrue(response.getBody().toString().contains("inexistente(s) e/ou inativo(s)"));
	}

	@Test
	void testCotar_inexistentProductError() {
		SolicitacaoCotacaoInput solicitacao = new SolicitacaoCotacaoInput();
		solicitacao.setProductId("prod123");
		solicitacao.setOfferId("offer123");

		when(consultaCatalogoService.consultarProduto("prod123")).thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
		when(consultaCatalogoService.consultarOferta("offer123")).thenReturn(new ResponseEntity<>(HttpStatus.OK));

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance();
		ResponseEntity response = cotacaoService.cotar(solicitacao, uriBuilder);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertTrue(response.getBody().toString().contains("inexistente(s) e/ou inativo(s)"));
	}

	@Test
	void testCotar_inexistentOfferError() {
		SolicitacaoCotacaoInput solicitacao = new SolicitacaoCotacaoInput();
		solicitacao.setProductId("prod123");
		solicitacao.setOfferId("offer123");

		when(consultaCatalogoService.consultarProduto("prod123")).thenReturn(new ResponseEntity<>(HttpStatus.OK));
		when(consultaCatalogoService.consultarOferta("offer123")).thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance();
		ResponseEntity response = cotacaoService.cotar(solicitacao, uriBuilder);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertTrue(response.getBody().toString().contains("inexistente(s) e/ou inativo(s)"));

	}

	@Test
	void testCotar_inactiveProductError() {
		SolicitacaoCotacaoInput solicitacao = new SolicitacaoCotacaoInput();
		solicitacao.setProductId("prod123");
		solicitacao.setOfferId("offer123");

		ConsultaProdutoOutput produtoOutput = new ConsultaProdutoOutput();
		produtoOutput.setActive(false);

		ConsultaOfertaOutput ofertaOutput = gerarObjetoConsultaOferta(true, new MonthlyPremiumAmount(BigDecimal.valueOf(50), BigDecimal.valueOf(150), BigDecimal.valueOf(60)), Collections.emptyMap(), Collections.emptyList());

		when(consultaCatalogoService.consultarProduto("prod123")).thenReturn(new ResponseEntity<>(produtoOutput, HttpStatus.OK));
		when(consultaCatalogoService.consultarOferta("offer123")).thenReturn(new ResponseEntity<>(ofertaOutput, HttpStatus.OK));

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance();
		ResponseEntity response = cotacaoService.cotar(solicitacao, uriBuilder);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertTrue(response.getBody().toString().contains("inexistente(s) e/ou inativo(s)"));
	}

	@Test
	void testCotar_inactiveOfferError() {
		SolicitacaoCotacaoInput solicitacao = new SolicitacaoCotacaoInput();
		solicitacao.setProductId("prod123");
		solicitacao.setOfferId("offer123");

		ConsultaProdutoOutput produtoOutput = new ConsultaProdutoOutput();
		produtoOutput.setActive(true);

		ConsultaOfertaOutput ofertaOutput = gerarObjetoConsultaOferta(false, new MonthlyPremiumAmount(BigDecimal.valueOf(50), BigDecimal.valueOf(150), BigDecimal.valueOf(60)), Collections.emptyMap(), Collections.emptyList());

		when(consultaCatalogoService.consultarProduto("prod123")).thenReturn(new ResponseEntity<>(produtoOutput, HttpStatus.OK));
		when(consultaCatalogoService.consultarOferta("offer123")).thenReturn(new ResponseEntity<>(ofertaOutput, HttpStatus.OK));

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance();
		ResponseEntity response = cotacaoService.cotar(solicitacao, uriBuilder);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertTrue(response.getBody().toString().contains("inexistente(s) e/ou inativo(s)"));
	}

	@Test
	void testCotar_inactiveOfferAndProductError() {
		SolicitacaoCotacaoInput solicitacao = new SolicitacaoCotacaoInput();
		solicitacao.setProductId("prod123");
		solicitacao.setOfferId("offer123");

		ConsultaProdutoOutput produtoOutput = new ConsultaProdutoOutput();
		produtoOutput.setActive(false);

		ConsultaOfertaOutput ofertaOutput = gerarObjetoConsultaOferta(false, new MonthlyPremiumAmount(BigDecimal.valueOf(50), BigDecimal.valueOf(150), BigDecimal.valueOf(60)), Collections.emptyMap(), Collections.emptyList());

		when(consultaCatalogoService.consultarProduto("prod123")).thenReturn(new ResponseEntity<>(produtoOutput, HttpStatus.OK));
		when(consultaCatalogoService.consultarOferta("offer123")).thenReturn(new ResponseEntity<>(ofertaOutput, HttpStatus.OK));

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance();
		ResponseEntity response = cotacaoService.cotar(solicitacao, uriBuilder);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertTrue(response.getBody().toString().contains("inexistente(s) e/ou inativo(s)"));
	}

	@Test
	void testCotar_ValidationValoresPremioMensalInvalidosError() {
		SolicitacaoCotacaoInput	solicitacao = gerarObjetoSolicitacao(BigDecimal.valueOf(200), BigDecimal.valueOf(1000), Collections.emptyMap(), Collections.emptyList());

		ConsultaProdutoOutput produtoOutput = new ConsultaProdutoOutput();
		produtoOutput.setActive(true);

		ConsultaOfertaOutput ofertaOutput = gerarObjetoConsultaOferta(true, new MonthlyPremiumAmount(BigDecimal.valueOf(50), BigDecimal.valueOf(150), BigDecimal.valueOf(60)), Collections.emptyMap(), Collections.emptyList());

		when(consultaCatalogoService.consultarProduto("prod123")).thenReturn(new ResponseEntity<>(produtoOutput, HttpStatus.OK));
		when(consultaCatalogoService.consultarOferta("offer123")).thenReturn(new ResponseEntity<>(ofertaOutput, HttpStatus.OK));

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance();
		ResponseEntity response = cotacaoService.cotar(solicitacao, uriBuilder);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertTrue(response.getBody().toString().contains("Valores de prêmio"));
	}

	@Test
	void testCotar_ValidationValoresCoberturasInvalidosError() {
		SolicitacaoCotacaoInput solicitacao = gerarObjetoSolicitacao(BigDecimal.valueOf(100), BigDecimal.valueOf(1000), generateCoverages(), Collections.emptyList());

		ConsultaProdutoOutput produtoOutput = new ConsultaProdutoOutput();
		produtoOutput.setActive(true);

		ConsultaOfertaOutput ofertaOutput = gerarObjetoConsultaOferta(true, new MonthlyPremiumAmount(BigDecimal.valueOf(50), BigDecimal.valueOf(150), BigDecimal.valueOf(60)), generateCoverages(), Collections.emptyList());

		when(consultaCatalogoService.consultarProduto("prod123")).thenReturn(new ResponseEntity<>(produtoOutput, HttpStatus.OK));
		when(consultaCatalogoService.consultarOferta("offer123")).thenReturn(new ResponseEntity<>(ofertaOutput, HttpStatus.OK));

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance();
		ResponseEntity response = cotacaoService.cotar(solicitacao, uriBuilder);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertTrue(response.getBody().toString().contains("valores de cobertura"));
	}

	@Test
	void testCotar_ValidationAssistenciasNaoAtendidasError() {
		SolicitacaoCotacaoInput solicitacao = gerarObjetoSolicitacao(BigDecimal.valueOf(100), BigDecimal.ZERO, Collections.emptyMap(), Arrays.asList("Assistência Funerária"));

		ConsultaProdutoOutput produtoOutput = new ConsultaProdutoOutput();
		produtoOutput.setActive(true);

		ConsultaOfertaOutput ofertaOutput = gerarObjetoConsultaOferta(true, new MonthlyPremiumAmount(BigDecimal.valueOf(50), BigDecimal.valueOf(150), BigDecimal.valueOf(60)), Collections.emptyMap(), Arrays.asList("Chaveiro 24h"));

		when(consultaCatalogoService.consultarProduto("prod123")).thenReturn(new ResponseEntity<>(produtoOutput, HttpStatus.OK));
		when(consultaCatalogoService.consultarOferta("offer123")).thenReturn(new ResponseEntity<>(ofertaOutput, HttpStatus.OK));

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance();
		ResponseEntity response = cotacaoService.cotar(solicitacao, uriBuilder);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertTrue(response.getBody().toString().contains("assistências exigidas"));
	}

	@Test
	void testCotar_ValidationCoberturasNaoAtendidasError() {
		SolicitacaoCotacaoInput solicitacao = gerarObjetoSolicitacao(BigDecimal.valueOf(100), BigDecimal.ZERO, generateCoverages(), Arrays.asList("Assistência Funerária"));

		ConsultaProdutoOutput produtoOutput = new ConsultaProdutoOutput();
		produtoOutput.setActive(true);

		ConsultaOfertaOutput ofertaOutput = gerarObjetoConsultaOferta(true, new MonthlyPremiumAmount(BigDecimal.valueOf(50), BigDecimal.valueOf(150), BigDecimal.valueOf(60)), Collections.emptyMap(), Arrays.asList("Chaveiro 24h"));

		when(consultaCatalogoService.consultarProduto("prod123")).thenReturn(new ResponseEntity<>(produtoOutput, HttpStatus.OK));
		when(consultaCatalogoService.consultarOferta("offer123")).thenReturn(new ResponseEntity<>(ofertaOutput, HttpStatus.OK));

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance();
		ResponseEntity response = cotacaoService.cotar(solicitacao, uriBuilder);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertTrue(response.getBody().toString().contains("coberturas exigidas"));
	}

	private SolicitacaoCotacaoInput gerarObjetoSolicitacao(BigDecimal totalMonthlyPremiumAmount, BigDecimal totalCoverageAmount, Map<String, BigDecimal> coverages, List<String> assistencias) {
		SolicitacaoCotacaoInput solicitacao = new SolicitacaoCotacaoInput();
		solicitacao.setProductId("prod123");
		solicitacao.setOfferId("offer123");
		solicitacao.setTotalMonthlyPremiumAmount(totalMonthlyPremiumAmount);
		solicitacao.setTotalCoverageAmount(totalCoverageAmount);
		solicitacao.setCoverages(coverages);
		solicitacao.setAssistances(assistencias);
		return solicitacao;
	}
	private ConsultaOfertaOutput gerarObjetoConsultaOferta(Boolean active, MonthlyPremiumAmount monthlyPremiumAmount, Map<String, BigDecimal> coverages, List<String> assistances) {
		ConsultaOfertaOutput ofertaOutput = new ConsultaOfertaOutput();
		ofertaOutput.setActive(active);
		ofertaOutput.setMonthlyPremiumAmount(monthlyPremiumAmount);
		ofertaOutput.setCoverages(coverages);
		ofertaOutput.setAssistances(assistances);
		return ofertaOutput;
	}

	public static Map<String, BigDecimal> generateCoverages() {
		Map<String, BigDecimal> coverages = new HashMap<>();
		coverages.put("Incêndio", BigDecimal.valueOf(500_000.00));
		coverages.put("Desastres naturais", BigDecimal.valueOf(600_000.00));
		coverages.put("Responsabilidade civil", BigDecimal.valueOf(80_000.00));
		coverages.put("Roubo", BigDecimal.valueOf(100_000.00));
		return coverages;
	}
}
