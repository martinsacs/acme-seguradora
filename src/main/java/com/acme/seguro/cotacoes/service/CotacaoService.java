package com.acme.seguro.cotacoes.service;

import com.acme.seguro.cotacoes.model.MonthlyPremiumAmount;
import com.acme.seguro.cotacoes.model.db.CotacaoSeguroEntity;
import com.acme.seguro.cotacoes.model.db.CustomerEntity;
import com.acme.seguro.cotacoes.model.input.SolicitacaoCotacaoInput;
import com.acme.seguro.cotacoes.model.output.mock.ConsultaOfertaOutput;
import com.acme.seguro.cotacoes.model.output.mock.ConsultaProdutoOutput;
import com.acme.seguro.cotacoes.repository.CotacaoSeguroRepository;
import com.acme.seguro.cotacoes.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.*;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
public class CotacaoService {
    @Autowired
    private ConsultaCatalogoService consultaCatalogoService;

    @Autowired
    private CotacaoSeguroRepository cotacaoSeguroRepository;

    @Autowired
    private CustomerRepository customerRepository;

    public ResponseEntity cotar(SolicitacaoCotacaoInput solicitacao, UriComponentsBuilder uriBuilder) {
        try {
            ResponseEntity<ConsultaProdutoOutput> responseProduto = consultaCatalogoService.consultarProduto(solicitacao.getProductId());
            ResponseEntity<ConsultaOfertaOutput> responseOferta = consultaCatalogoService.consultarOferta(solicitacao.getOfferId());

            if (produtoOfertaValidos(responseProduto, responseOferta)) {
                Map<String, Integer> validationResult = validarSolicitacao(solicitacao, responseOferta);

                if (validationResult.containsValue(200)) {
                    ResponseEntity<CotacaoSeguroEntity> response = persistirBanco(solicitacao, uriBuilder);

                    // postar no kafka
                    return response;
                } else {
                    String errorMessage = validationResult.keySet().iterator().next();
                    int statusCode = validationResult.get(errorMessage);
                    return new ResponseEntity<>(errorMessage, HttpStatusCode.valueOf(statusCode));
                }
            } else {
                return new ResponseEntity<>("ERRO: Produto e/ou oferta inexistente(s) e/ou inativo(s).", BAD_REQUEST);
            }
        } catch (Exception ex) {
            System.out.println("Ocorreu um erro: " + ex.getMessage());
            return new ResponseEntity<>("Ocorreu um erro no processamento.", HttpStatusCode.valueOf(500));
        }
    }

    private Map<String, Integer> validarSolicitacao(SolicitacaoCotacaoInput solicitacao, ResponseEntity<ConsultaOfertaOutput> responseOferta) {
        Map<String, Integer> result = new HashMap<>();

        if (!coberturasValidas(solicitacao.getCoverages(), responseOferta.getBody().getCoverages())) {
            result.put("ERRO: As coberturas exigidas não são atendidas nessa oferta.", BAD_REQUEST.value());
            return result;
        }

        if (!assistenciasValidas(solicitacao.getAssistances(), responseOferta.getBody().getAssistances())) {
            result.put("ERRO: As assistências exigidas não são atendidas nessa oferta.", BAD_REQUEST.value());
            return result;
        }

        if (!valoresPremioMensalValidos(solicitacao.getTotalMonthlyPremiumAmount(), responseOferta.getBody().getMonthlyPremiumAmount())) {
            result.put("ERRO: Valores de prêmio não compatíveis com a oferta.", BAD_REQUEST.value());
            return result;
        }

        if (!valorTotalCoberturasValido(solicitacao.getCoverages(), solicitacao.getTotalCoverageAmount())) {
            result.put("ERRO: A soma dos valores de cobertura não corresponde ao valor total informado.", BAD_REQUEST.value());
            return result;
        }

        result.put("Validação ok.", 200);
        return result;
    }
    private boolean produtoOfertaValidos(ResponseEntity<ConsultaProdutoOutput> responseProduto, ResponseEntity<ConsultaOfertaOutput> responseOferta) {
        return responseProduto.getStatusCode().is2xxSuccessful() &&
                responseOferta.getStatusCode().is2xxSuccessful() &&
                responseProduto.getBody() != null &&
                responseOferta.getBody() != null &&
                responseProduto.getBody().getActive() &&
                responseOferta.getBody().getActive();
    }

    private static boolean coberturasValidas(Map<String, BigDecimal> coberturaEsperada, Map<String, BigDecimal> coberturaReal) {
        for (Map.Entry<String, BigDecimal> entry : coberturaEsperada.entrySet()) {
            String descricao = entry.getKey();
            BigDecimal valorEsperado = entry.getValue();

            BigDecimal valorReal = coberturaReal.get(descricao);

            if (valorReal == null || valorEsperado.compareTo(valorReal) > 0) {
                return false;
            }
        }
        return true;
    }

    private static boolean assistenciasValidas(List<String> assistenciasEsperada, List<String> assistenciasReal) {
        Set<String> setAssistenciasReal = new HashSet<>(assistenciasReal);

        for (String item : assistenciasEsperada) {
            if (!setAssistenciasReal.contains(item)) {
                return false;
            }
        }

        return true;
    }

    private static boolean valoresPremioMensalValidos(BigDecimal valoresCotacao, MonthlyPremiumAmount valoresOferta) {
        return valoresCotacao.compareTo(valoresOferta.getMinAmount()) == 1 &&
                valoresOferta.getMaxAmount().compareTo(valoresCotacao) == 1;
    }

    private static boolean valorTotalCoberturasValido(Map<String, BigDecimal> coverages, BigDecimal totalCoverageAmount) {
        return somarValores(coverages).compareTo(totalCoverageAmount) == 0;
    }
    private static BigDecimal somarValores(Map<String, BigDecimal> coberturas) {
        BigDecimal soma = BigDecimal.ZERO;

        for (BigDecimal valor : coberturas.values()) {
            soma = soma.add(valor);
        }

        return soma;
    }

    private ResponseEntity<CotacaoSeguroEntity> persistirBanco(SolicitacaoCotacaoInput solicitacao, UriComponentsBuilder uriBuilder) {
        if(solicitacao.getCustomer() != null) {
            customerRepository.save(solicitacao.getCustomer());
        }

        CotacaoSeguroEntity cotacao = deParaSolicitacaoCotacaoDb(solicitacao);
        cotacaoSeguroRepository.save(cotacao);

        URI uri = uriBuilder.path("/solicitacoes-cotacao/{id}").buildAndExpand(cotacao.getId()).toUri();
        return ResponseEntity.created(uri).body(cotacao);
    }

    private CotacaoSeguroEntity deParaSolicitacaoCotacaoDb(SolicitacaoCotacaoInput solicitacao) {
        return new CotacaoSeguroEntity(solicitacao.getProductId(), solicitacao.getOfferId(),
                solicitacao.getCategory(), solicitacao.getTotalMonthlyPremiumAmount(),
                solicitacao.getTotalCoverageAmount(), solicitacao.getCoverages(),
                solicitacao.getAssistances(), solicitacao.getCustomer());
    }
}
