package com.gestionale.controller;

import com.gestionale.entity.Articolo;
import com.gestionale.service.ArticoloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/articoli")
@CrossOrigin(origins = "*")
public class ArticoloController {
    
    @Autowired
    private ArticoloService articoloService;
    
    @GetMapping
    public List<Articolo> getAllArticoli() {
        return articoloService.getAllArticoli();
    }
    
    @GetMapping("/paginati")
    public ResponseEntity<Page<Articolo>> getArticoliPaginati(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Articolo> articoli = articoloService.getArticoliPaginati(pageable);
        return ResponseEntity.ok(articoli);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Articolo> getArticoloById(@PathVariable Long id) {
        Optional<Articolo> articolo = articoloService.getArticoloById(id);
        return articolo.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/codice/{codice}")
    public ResponseEntity<Articolo> getArticoloByCodice(@PathVariable String codice) {
        Optional<Articolo> articolo = articoloService.getArticoloByCodice(codice);
        return articolo.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/attivi")
    public List<Articolo> getArticoliAttivi() {
        return articoloService.getArticoliAttivi();
    }
    
    @GetMapping("/search")
    public List<Articolo> searchArticoli(@RequestParam String query) {
        return articoloService.searchArticoli(query);
    }
    
    @GetMapping("/categoria/{categoria}")
    public List<Articolo> getArticoliByCategoria(@PathVariable String categoria) {
        return articoloService.getArticoliByCategoria(categoria);
    }
    
    @GetMapping("/prezzo-range")
    public List<Articolo> getArticoliByPrezzoRange(
            @RequestParam BigDecimal minPrezzo,
            @RequestParam BigDecimal maxPrezzo) {
        return articoloService.getArticoliByPrezzoRange(minPrezzo, maxPrezzo);
    }
    
    @PostMapping
    public ResponseEntity<Articolo> createArticolo(@RequestBody Articolo articolo) {
        try {
            Articolo nuovoArticolo = articoloService.createArticolo(articolo);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuovoArticolo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Articolo> updateArticolo(@PathVariable Long id, @RequestBody Articolo articolo) {
        Optional<Articolo> articoloAggiornato = articoloService.updateArticolo(id, articolo);
        return articoloAggiornato.map(ResponseEntity::ok)
                                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticolo(@PathVariable Long id) {
        boolean deleted = articoloService.deleteArticolo(id);
        return deleted ? ResponseEntity.noContent().build() 
                      : ResponseEntity.notFound().build();
    }
    
    @PatchMapping("/{id}/disattiva")
    public ResponseEntity<Void> disattivaArticolo(@PathVariable Long id) {
        boolean disattivato = articoloService.disattivaArticolo(id);
        return disattivato ? ResponseEntity.ok().build() 
                          : ResponseEntity.notFound().build();
    }
    
    @PatchMapping("/{id}/attiva")
    public ResponseEntity<Void> attivaArticolo(@PathVariable Long id) {
        boolean attivato = articoloService.attivaArticolo(id);
        return attivato ? ResponseEntity.ok().build() 
                       : ResponseEntity.notFound().build();
    }
    
    @GetMapping("/stats/count-attivi")
    public ResponseEntity<Long> countArticoliAttivi() {
        return ResponseEntity.ok(articoloService.countArticoliAttivi());
    }
    
    @GetMapping("/stats/count-inattivi")
    public ResponseEntity<Long> countArticoliInattivi() {
        return ResponseEntity.ok(articoloService.countArticoliInattivi());
    }
    
    @GetMapping("/stats/categorie")
    public ResponseEntity<List<String>> getAllCategorie() {
        return ResponseEntity.ok(articoloService.getAllCategorie());
    }
}
