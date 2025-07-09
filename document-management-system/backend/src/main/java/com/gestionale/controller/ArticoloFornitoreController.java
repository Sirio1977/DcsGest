package com.gestionale.controller;

import com.gestionale.entity.ArticoloFornitore;
import com.gestionale.service.ArticoloFornitoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/articoli-fornitori")
@CrossOrigin(origins = "*")
public class ArticoloFornitoreController {
    
    @Autowired
    private ArticoloFornitoreService articoloFornitoreService;
    
    @GetMapping
    public List<ArticoloFornitore> getAllArticoliFornitori() {
        return articoloFornitoreService.getAllArticoliFornitori();
    }
    
    @GetMapping("/paginati")
    public ResponseEntity<Page<ArticoloFornitore>> getArticoliFornitoriPaginati(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ArticoloFornitore> articoliFornitori = articoloFornitoreService.getArticoliFornitoriPaginati(pageable);
        return ResponseEntity.ok(articoliFornitori);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ArticoloFornitore> getArticoloFornitoreById(@PathVariable Long id) {
        Optional<ArticoloFornitore> articoloFornitore = articoloFornitoreService.getArticoloFornitoreById(id);
        return articoloFornitore.map(ResponseEntity::ok)
                              .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/codice/{codice}")
    public ResponseEntity<ArticoloFornitore> getArticoloFornitoreByCodice(@PathVariable String codice) {
        Optional<ArticoloFornitore> articoloFornitore = articoloFornitoreService.getArticoloFornitoreByCodice(codice);
        return articoloFornitore.map(ResponseEntity::ok)
                              .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/fornitore/{partitaIva}")
    public List<ArticoloFornitore> getArticoliByFornitore(@PathVariable String partitaIva) {
        return articoloFornitoreService.getArticoliByFornitore(partitaIva);
    }
    
    @GetMapping("/search")
    public List<ArticoloFornitore> searchArticoliFornitori(@RequestParam String query) {
        return articoloFornitoreService.searchArticoliFornitori(query);
    }
    
    @PostMapping
    public ResponseEntity<ArticoloFornitore> createArticoloFornitore(@RequestBody ArticoloFornitore articoloFornitore) {
        try {
            ArticoloFornitore nuovoArticoloFornitore = articoloFornitoreService.createArticoloFornitore(articoloFornitore);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuovoArticoloFornitore);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ArticoloFornitore> updateArticoloFornitore(@PathVariable Long id, @RequestBody ArticoloFornitore articoloFornitore) {
        Optional<ArticoloFornitore> articoloFornitoreAggiornato = articoloFornitoreService.updateArticoloFornitore(id, articoloFornitore);
        return articoloFornitoreAggiornato.map(ResponseEntity::ok)
                                         .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticoloFornitore(@PathVariable Long id) {
        boolean deleted = articoloFornitoreService.deleteArticoloFornitore(id);
        return deleted ? ResponseEntity.noContent().build() 
                      : ResponseEntity.notFound().build();
    }
    
    @GetMapping("/stats/count")
    public ResponseEntity<Long> countArticoliFornitori() {
        return ResponseEntity.ok(articoloFornitoreService.countArticoliFornitori());
    }
    
    @GetMapping("/stats/fornitori")
    public ResponseEntity<List<String>> getAllFornitori() {
        return ResponseEntity.ok(articoloFornitoreService.getAllFornitori());
    }
}
