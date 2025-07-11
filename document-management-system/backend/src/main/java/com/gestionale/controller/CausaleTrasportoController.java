package com.gestionale.controller;

import com.gestionale.dto.CausaleTrasportoDto;
import com.gestionale.entity.CausaleTrasporto;
import com.gestionale.repository.CausaleTrasportoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Controller REST per la gestione delle causali di trasporto
 */
@RestController
@RequestMapping("/api/causale-trasporto")
@Validated
public class CausaleTrasportoController {

    private final CausaleTrasportoRepository repository;

    // Constructor
    public CausaleTrasportoController(CausaleTrasportoRepository repository) {
        this.repository = repository;
    }

    /**
     * Recupera tutte le causali di trasporto
     */
    @GetMapping
    public ResponseEntity<List<CausaleTrasporto>> getAllCausaliTrasporto() {
        List<CausaleTrasporto> causali = repository.findAll();
        return ResponseEntity.ok(causali);
    }

    /**
     * Recupera tutte le causali di trasporto attive
     */
    @GetMapping("/attive")
    public ResponseEntity<List<CausaleTrasporto>> getCausaliTrasportoAttive() {
        List<CausaleTrasporto> causali = repository.findByAttivaTrue();
        return ResponseEntity.ok(causali);
    }

    /**
     * Recupera causale di trasporto per ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<CausaleTrasporto> getCausaleTrasportoById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Recupera causale di trasporto per codice
     */
    @GetMapping("/codice/{codice}")
    public ResponseEntity<CausaleTrasporto> getCausaleTrasportoByCodice(@PathVariable String codice) {
        return repository.findByCodice(codice)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Crea una nuova causale di trasporto
     */
    @PostMapping
    public ResponseEntity<CausaleTrasporto> createCausaleTrasporto(@Valid @RequestBody CausaleTrasportoDto dto) {
        // Verifica duplicati
        if (repository.existsByCodice(dto.getCodice())) {
            return ResponseEntity.badRequest().build();
        }

        CausaleTrasporto causale = new CausaleTrasporto();
        causale.setCodice(dto.getCodice());
        causale.setDescrizione(dto.getDescrizione());
        causale.setAttiva(dto.getAttiva());

        CausaleTrasporto savedCausale = repository.save(causale);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCausale);
    }

    /**
     * Aggiorna una causale di trasporto esistente
     */
    @PutMapping("/{id}")
    public ResponseEntity<CausaleTrasporto> updateCausaleTrasporto(@PathVariable Long id,
                                                                   @Valid @RequestBody CausaleTrasportoDto dto) {
        return repository.findById(id)
                .map(causale -> {
                    // Verifica duplicati codice
                    if (repository.existsByCodiceAndIdNot(dto.getCodice(), id)) {
                        return ResponseEntity.badRequest().<CausaleTrasporto>build();
                    }

                    causale.setCodice(dto.getCodice());
                    causale.setDescrizione(dto.getDescrizione());
                    causale.setAttiva(dto.getAttiva());

                    CausaleTrasporto updatedCausale = repository.save(causale);
                    return ResponseEntity.ok(updatedCausale);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Elimina una causale di trasporto
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCausaleTrasporto(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Ricerca causali di trasporto per descrizione
     */
    @GetMapping("/search")
    public ResponseEntity<List<CausaleTrasporto>> searchCausaliTrasporto(@RequestParam String descrizione) {
        List<CausaleTrasporto> causali = repository.findByDescrizioneLike(descrizione);
        return ResponseEntity.ok(causali);
    }
}
