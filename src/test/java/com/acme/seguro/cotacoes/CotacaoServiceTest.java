package com.acme.seguro.cotacoes;


import com.acme.seguro.cotacoes.model.MonthlyPremiumAmount;
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
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
	void testCotar_inexistentProductAndOffer() {
		SolicitacaoCotacaoInput solicitacao = new SolicitacaoCotacaoInput();
		solicitacao.setProductId("prod123");
		solicitacao.setOfferId("offer123");

		when(consultaCatalogoService.consultarProduto("prod123")).thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
		when(consultaCatalogoService.consultarOferta("offer123")).thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance();
		ResponseEntity response = cotacaoService.cotar(solicitacao, uriBuilder);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	@Test
	void testCotar_inexistentProduct() {
		SolicitacaoCotacaoInput solicitacao = new SolicitacaoCotacaoInput();
		solicitacao.setProductId("prod123");
		solicitacao.setOfferId("offer123");

		when(consultaCatalogoService.consultarProduto("prod123")).thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
		when(consultaCatalogoService.consultarOferta("offer123")).thenReturn(new ResponseEntity<>(HttpStatus.OK));

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance();
		ResponseEntity response = cotacaoService.cotar(solicitacao, uriBuilder);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	void testCotar_inexistentOffer() {
		SolicitacaoCotacaoInput solicitacao = new SolicitacaoCotacaoInput();
		solicitacao.setProductId("prod123");
		solicitacao.setOfferId("offer123");

		when(consultaCatalogoService.consultarProduto("prod123")).thenReturn(new ResponseEntity<>(HttpStatus.OK));
		when(consultaCatalogoService.consultarOferta("offer123")).thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance();
		ResponseEntity response = cotacaoService.cotar(solicitacao, uriBuilder);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	@Test
	void testCotar_inactiveProduct() {
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
	}

	@Test
	void testCotar_inactiveOffer() {
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
	}
	@Test
	void testCotar_ValidationErrorValoresPremioMensalInvalidos() {
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
	}

}
