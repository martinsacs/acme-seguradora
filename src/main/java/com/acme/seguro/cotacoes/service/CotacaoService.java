package com.acme.seguro.cotacoes.service;

import com.acme.seguro.cotacoes.model.MonthlyPremiumAmount;
import com.acme.seguro.cotacoes.model.input.SolicitacaoCotacao;
import com.acme.seguro.cotacoes.model.output.ConsultaCotacao;
import com.acme.seguro.cotacoes.model.output.ConsultaOferta;
import com.acme.seguro.cotacoes.model.output.ConsultaProduto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.*;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class CotacaoService {
    @Autowired
    private ConsultaCatalogoService consultaCatalogoService;
    public ResponseEntity<String> cotar(SolicitacaoCotacao solicitacao) {
        try {
            ResponseEntity<ConsultaProduto> responseProduto = consultaCatalogoService.consultarProduto(solicitacao.getProductId());
            ResponseEntity<ConsultaOferta> responseOferta = consultaCatalogoService.consultarOferta(solicitacao.getOfferId());

            if (produtoOfertaValidos(responseProduto, responseOferta)) {
                Map<String, Integer> validationResult = validarSolicitacao(solicitacao, responseOferta);

                if (validationResult.containsValue(200)) {
                    // persistir no banco de dados
                    // postar no kafka


                    return new ResponseEntity<>("Cotação realizada com sucesso!", HttpStatusCode.valueOf(200));
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

    public ResponseEntity<ConsultaCotacao> consultar(Long id) {

        return null;
    }

    private Map<String, Integer> validarSolicitacao(SolicitacaoCotacao solicitacao, ResponseEntity<ConsultaOferta> responseOferta) {
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
            result.put("ERRO: Valores de prêmio não compatíveis com a oferta.", HttpStatusCode.valueOf(200).value());
            return result;
        }

        if (!valorTotalCoberturasValido(solicitacao.getCoverages(), solicitacao.getTotalCoverageAmount())) {
            result.put("ERRO: A soma dos valores de cobertura não corresponde ao valor total informado.", HttpStatusCode.valueOf(500).value());
            return result;
        }

        result.put("Validação ok.", 200);
        return result;
    }
    private boolean produtoOfertaValidos(ResponseEntity<ConsultaProduto> responseProduto, ResponseEntity<ConsultaOferta> responseOferta) {
        return responseProduto.getStatusCode().is2xxSuccessful() &&
                responseOferta.getStatusCode().is2xxSuccessful() &&
                responseProduto.getBody() != null &&
                responseOferta.getBody() != null &&
                responseProduto.getBody().getActive() &&
                responseOferta.getBody().getActive();
    }

    private static boolean coberturasValidas(Map<String,BigDecimal> coberturaEsperada, Map<String, BigDecimal> coberturaReal) {
        for (Map.Entry<String, BigDecimal> entry : coberturaEsperada.entrySet()) {
            String key = entry.getKey();
            BigDecimal valorEsperado = entry.getValue();

            if (!coberturaReal.containsKey(key)) {
                return false;
            }

            BigDecimal valorReal = coberturaReal.get(key);
            if (valorEsperado.compareTo(valorReal) > 0) {
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
    public static BigDecimal somarValores(Map<String, BigDecimal> map) {
        BigDecimal soma = BigDecimal.ZERO;

        for (BigDecimal value : map.values()) {
            soma = soma.add(value);
        }

        return soma;
    }

}
