package com.acme.seguro.cotacoes;


import com.acme.seguro.cotacoes.model.MonthlyPremiumAmount;
import com.acme.seguro.cotacoes.model.db.CoberturaEntity;
import com.acme.seguro.cotacoes.model.db.CotacaoSeguroEntity;
import com.acme.seguro.cotacoes.model.input.SolicitacaoCotacaoInput;
import com.acme.seguro.cotacoes.model.output.mock.ConsultaOfertaOutput;
import com.acme.seguro.cotacoes.model.output.mock.ConsultaProdutoOutput;
import com.acme.seguro.cotacoes.repository.CotacaoSeguroRepository;
import com.acme.seguro.cotacoes.service.ConsultaCatalogoService;
import com.acme.seguro.cotacoes.service.CotacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static com.acme.seguro.cotacoes.config.ConsultaCatalogoMockServerConfig.generateCoverages;
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
		SolicitacaoCotacaoInput solicitacao = new SolicitacaoCotacaoInput();
		solicitacao.setProductId("prod123");
		solicitacao.setOfferId("offer123");
		solicitacao.setTotalMonthlyPremiumAmount(BigDecimal.valueOf(100));
		solicitacao.setTotalCoverageAmount(BigDecimal.ZERO);
		solicitacao.setCoverages(Collections.emptyList());
		solicitacao.setAssistances(Collections.emptyList());

		ConsultaProdutoOutput produtoOutput = new ConsultaProdutoOutput();
		produtoOutput.setActive(true);

		ConsultaOfertaOutput ofertaOutput = new ConsultaOfertaOutput();
		ofertaOutput.setActive(true);
		ofertaOutput.setMonthlyPremiumAmount(new MonthlyPremiumAmount(BigDecimal.valueOf(50), BigDecimal.valueOf(150), BigDecimal.valueOf(60)));
		ofertaOutput.setCoverages(Collections.emptyList());
		ofertaOutput.setAssistances(Collections.emptyList());

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

		ConsultaOfertaOutput ofertaOutput = new ConsultaOfertaOutput();
		ofertaOutput.setActive(true);
		ofertaOutput.setMonthlyPremiumAmount(new MonthlyPremiumAmount(BigDecimal.valueOf(50), BigDecimal.valueOf(150), BigDecimal.valueOf(60)));
		ofertaOutput.setCoverages(Collections.emptyList());
		ofertaOutput.setAssistances(Collections.emptyList());

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

		ConsultaOfertaOutput ofertaOutput = new ConsultaOfertaOutput();
		ofertaOutput.setActive(false);
		ofertaOutput.setMonthlyPremiumAmount(new MonthlyPremiumAmount(BigDecimal.valueOf(50), BigDecimal.valueOf(150), BigDecimal.valueOf(60)));
		ofertaOutput.setCoverages(Collections.emptyList());
		ofertaOutput.setAssistances(Collections.emptyList());

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

		ConsultaOfertaOutput ofertaOutput = new ConsultaOfertaOutput();
		ofertaOutput.setActive(false);
		ofertaOutput.setMonthlyPremiumAmount(new MonthlyPremiumAmount(BigDecimal.valueOf(50), BigDecimal.valueOf(150), BigDecimal.valueOf(60)));
		ofertaOutput.setCoverages(Collections.emptyList());
		ofertaOutput.setAssistances(Collections.emptyList());

		when(consultaCatalogoService.consultarProduto("prod123")).thenReturn(new ResponseEntity<>(produtoOutput, HttpStatus.OK));
		when(consultaCatalogoService.consultarOferta("offer123")).thenReturn(new ResponseEntity<>(ofertaOutput, HttpStatus.OK));

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance();
		ResponseEntity response = cotacaoService.cotar(solicitacao, uriBuilder);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertTrue(response.getBody().toString().contains("inexistente(s) e/ou inativo(s)"));
	}

	@Test
	void testCotar_ValidationValoresPremioMensalInvalidosError() {
		SolicitacaoCotacaoInput solicitacao = new SolicitacaoCotacaoInput();
		solicitacao.setProductId("prod123");
		solicitacao.setOfferId("offer123");
		solicitacao.setTotalMonthlyPremiumAmount(BigDecimal.valueOf(200));
		solicitacao.setTotalCoverageAmount(BigDecimal.valueOf(1000));
		solicitacao.setCoverages(Collections.emptyList());
		solicitacao.setAssistances(Collections.emptyList());

		ConsultaProdutoOutput produtoOutput = new ConsultaProdutoOutput();
		produtoOutput.setActive(true);

		ConsultaOfertaOutput ofertaOutput = new ConsultaOfertaOutput();
		ofertaOutput.setActive(true);
		ofertaOutput.setMonthlyPremiumAmount(new MonthlyPremiumAmount(BigDecimal.valueOf(50), BigDecimal.valueOf(150), BigDecimal.valueOf(60)));
		ofertaOutput.setCoverages(Collections.emptyList());
		ofertaOutput.setAssistances(Collections.emptyList());

		when(consultaCatalogoService.consultarProduto("prod123")).thenReturn(new ResponseEntity<>(produtoOutput, HttpStatus.OK));
		when(consultaCatalogoService.consultarOferta("offer123")).thenReturn(new ResponseEntity<>(ofertaOutput, HttpStatus.OK));

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance();
		ResponseEntity response = cotacaoService.cotar(solicitacao, uriBuilder);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertTrue(response.getBody().toString().contains("Valores de prêmio"));
	}

	@Test
	void testCotar_ValidationValoresCoberturasInvalidosError() {
		SolicitacaoCotacaoInput solicitacao = new SolicitacaoCotacaoInput();
		solicitacao.setProductId("prod123");
		solicitacao.setOfferId("offer123");
		solicitacao.setTotalMonthlyPremiumAmount(BigDecimal.valueOf(100));
		solicitacao.setTotalCoverageAmount(BigDecimal.valueOf(1000));
		solicitacao.setCoverages(generateCoverages());
		solicitacao.setAssistances(Collections.emptyList());

		ConsultaProdutoOutput produtoOutput = new ConsultaProdutoOutput();
		produtoOutput.setActive(true);

		ConsultaOfertaOutput ofertaOutput = new ConsultaOfertaOutput();
		ofertaOutput.setActive(true);
		ofertaOutput.setMonthlyPremiumAmount(new MonthlyPremiumAmount(BigDecimal.valueOf(50), BigDecimal.valueOf(150), BigDecimal.valueOf(60)));
		ofertaOutput.setCoverages(generateCoverages());
		ofertaOutput.setAssistances(Collections.emptyList());

		when(consultaCatalogoService.consultarProduto("prod123")).thenReturn(new ResponseEntity<>(produtoOutput, HttpStatus.OK));
		when(consultaCatalogoService.consultarOferta("offer123")).thenReturn(new ResponseEntity<>(ofertaOutput, HttpStatus.OK));

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance();
		ResponseEntity response = cotacaoService.cotar(solicitacao, uriBuilder);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertTrue(response.getBody().toString().contains("valores de cobertura"));
	}

	@Test
	void testCotar_ValidationAssistenciasNaoAtendidasError() {
		SolicitacaoCotacaoInput solicitacao = new SolicitacaoCotacaoInput();
		solicitacao.setProductId("prod123");
		solicitacao.setOfferId("offer123");
		solicitacao.setTotalMonthlyPremiumAmount(BigDecimal.valueOf(100));
		solicitacao.setTotalCoverageAmount(BigDecimal.ZERO);
		solicitacao.setCoverages(Collections.emptyList());
		solicitacao.setAssistances(Arrays.asList("Assistência Funerária"));

		ConsultaProdutoOutput produtoOutput = new ConsultaProdutoOutput();
		produtoOutput.setActive(true);

		ConsultaOfertaOutput ofertaOutput = new ConsultaOfertaOutput();
		ofertaOutput.setActive(true);
		ofertaOutput.setMonthlyPremiumAmount(new MonthlyPremiumAmount(BigDecimal.valueOf(50), BigDecimal.valueOf(150), BigDecimal.valueOf(60)));
		ofertaOutput.setCoverages(Collections.emptyList());
		ofertaOutput.setAssistances(Arrays.asList("Chaveiro 24h"));

		when(consultaCatalogoService.consultarProduto("prod123")).thenReturn(new ResponseEntity<>(produtoOutput, HttpStatus.OK));
		when(consultaCatalogoService.consultarOferta("offer123")).thenReturn(new ResponseEntity<>(ofertaOutput, HttpStatus.OK));

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance();
		ResponseEntity response = cotacaoService.cotar(solicitacao, uriBuilder);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertTrue(response.getBody().toString().contains("assistências exigidas"));
	}

	@Test
	void testCotar_ValidationCoberturasNaoAtendidasError() {
		SolicitacaoCotacaoInput solicitacao = new SolicitacaoCotacaoInput();
		solicitacao.setProductId("prod123");
		solicitacao.setOfferId("offer123");
		solicitacao.setTotalMonthlyPremiumAmount(BigDecimal.valueOf(100));
		solicitacao.setTotalCoverageAmount(BigDecimal.ZERO);
		solicitacao.setCoverages(generateCoverages());
		solicitacao.setAssistances(Arrays.asList("Assistência Funerária"));

		ConsultaProdutoOutput produtoOutput = new ConsultaProdutoOutput();
		produtoOutput.setActive(true);

		ConsultaOfertaOutput ofertaOutput = new ConsultaOfertaOutput();
		ofertaOutput.setActive(true);
		ofertaOutput.setMonthlyPremiumAmount(new MonthlyPremiumAmount(BigDecimal.valueOf(50), BigDecimal.valueOf(150), BigDecimal.valueOf(60)));
		ofertaOutput.setCoverages(Collections.emptyList());
		ofertaOutput.setAssistances(Arrays.asList("Chaveiro 24h"));

		when(consultaCatalogoService.consultarProduto("prod123")).thenReturn(new ResponseEntity<>(produtoOutput, HttpStatus.OK));
		when(consultaCatalogoService.consultarOferta("offer123")).thenReturn(new ResponseEntity<>(ofertaOutput, HttpStatus.OK));

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance();
		ResponseEntity response = cotacaoService.cotar(solicitacao, uriBuilder);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertTrue(response.getBody().toString().contains("coberturas exigidas"));
	}
}
