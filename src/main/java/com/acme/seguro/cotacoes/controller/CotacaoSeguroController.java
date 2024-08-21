package com.acme.seguro.cotacoes.controller;

import com.acme.seguro.cotacoes.model.db.CotacaoSeguroEntity;
import com.acme.seguro.cotacoes.model.input.SolicitacaoCotacaoInput;
import com.acme.seguro.cotacoes.repository.CotacaoSeguroRepository;
import com.acme.seguro.cotacoes.service.CotacaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

@RestController
@ResponseBody
@RequestMapping("/v1/solicitacoes-cotacao")
public class CotacaoSeguroController {
    @Autowired
    private CotacaoService cotacaoService;

    @Autowired
    private CotacaoSeguroRepository cotacaoSeguroRepository;

    @PostMapping
    public ResponseEntity<String> solicitarCotacao(@RequestBody @Valid SolicitacaoCotacaoInput solicitacaoCotacaoInput, UriComponentsBuilder uriBuilder) {
        try {
            ResponseEntity<String> response = cotacaoService.cotar(solicitacaoCotacaoInput, uriBuilder);
            return response;
        } catch(Exception ex) {
            System.out.println("Ocorreu um erro ao solicitar cotação. " + ex.getMessage());
            return new ResponseEntity<>("Ocorreu um erro no processamento.", HttpStatusCode.valueOf(500));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CotacaoSeguroEntity> consultarCotacao(@PathVariable Long id) {
        Optional<CotacaoSeguroEntity> cotacao = cotacaoSeguroRepository.findById(id);
        return cotacao.isPresent() ? ResponseEntity.ok().body(cotacao.get()) : ResponseEntity.noContent().build();

    }

    @GetMapping
    public ResponseEntity<List<CotacaoSeguroEntity>> consultarCotacoes() {
        List<CotacaoSeguroEntity> cotacoes = cotacaoSeguroRepository.findAll();
        return cotacoes.isEmpty() ? ResponseEntity.noContent().build() : new ResponseEntity<List<CotacaoSeguroEntity>>(cotacoes, HttpStatus.OK);
    }
}
