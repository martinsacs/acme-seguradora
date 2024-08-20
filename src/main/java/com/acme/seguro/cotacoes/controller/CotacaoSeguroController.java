package com.acme.seguro.cotacoes.controller;

import com.acme.seguro.cotacoes.model.input.SolicitacaoCotacao;
import com.acme.seguro.cotacoes.model.output.ConsultaCotacao;
import com.acme.seguro.cotacoes.service.CotacaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@ResponseBody
@RequestMapping("/v1/solicitacoes-cotacao")
public class CotacaoSeguroController {
    @Autowired
    private CotacaoService cotacaoService;

    @PostMapping
    public ResponseEntity solicitarCotacao(@RequestBody @Valid SolicitacaoCotacao solicitacaoCotacao) {
        ResponseEntity<String> response = cotacaoService.cotar(solicitacaoCotacao);

        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity consultarCotacao(@PathVariable Long id) {
        ResponseEntity<ConsultaCotacao> response = cotacaoService.consultar(id);

        return null;
    }
}
