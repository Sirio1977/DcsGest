package com.gestionale.controller;

import com.gestionale.dto.ModalitaPagamentoDto;
import com.gestionale.entity.ModalitaPagamento;
import com.gestionale.repository.ModalitaPagamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Controller REST per la gestione delle modalità di pagamento
 */
@RestController
@RequestMapping("/api/modalita-pagamento")
@Validated
public class ModalitaPagamentoController {

    private final ModalitaPagamentoRepository repository;

    @Autowired
    public ModalitaPagamentoController(ModalitaPagamentoRepository repository) {
        this.repository = repository;
    }

    /**
     * Recupera tutte le modalità di pagamento
     */
    @GetMapping
    public ResponseEntity<List<ModalitaPagamento>> getAllModalitaPagamento() {
        List<ModalitaPagamento> modalita = repository.findAll();
        return ResponseEntity.ok(modalita);
    }

    /**
     * Recupera tutte le modalità di pagamento attive
     */
    @GetMapping("/attive")
    public ResponseEntity<List<ModalitaPagamento>> getModalitaPagamentoAttive() {
        List<ModalitaPagamento> modalita = repository.findByAttivaTrue();
        return ResponseEntity.ok(modalita);
    }

    /**
     * Recupera modalità di pagamento per ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ModalitaPagamento> getModalitaPagamentoById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Recupera modalità di pagamento per codice
     */
    @GetMapping("/codice/{codice}")
    public ResponseEntity<ModalitaPagamento> getModalitaPagamentoByCodice(@PathVariable String codice) {
        return repository.findByCodice(codice)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Crea una nuova modalità di pagamento
     */
    @PostMapping
    public ResponseEntity<ModalitaPagamento> createModalitaPagamento(@Valid @RequestBody ModalitaPagamentoDto dto) {
        // Verifica duplicati
        if (repository.existsByCodice(dto.getCodice())) {
            return ResponseEntity.badRequest().build();
        }

        ModalitaPagamento modalita = new ModalitaPagamento();
        modalita.setCodice(dto.getCodice());
        modalita.setDescrizione(dto.getDescrizione());
        modalita.setGiorniPagamento(dto.getGiorniPagamento());
        modalita.setFineMese(dto.getFineMese());
        modalita.setNumeroRate(dto.getNumeroRate());
        modalita.setGiorniTraRate(dto.getGiorniTraRate());
        modalita.setAttiva(dto.getAttiva());

        ModalitaPagamento savedModalita = repository.save(modalita);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedModalita);
    }

    /**
     * Aggiorna una modalità di pagamento esistente
     */
    @PutMapping("/{id}")
    public ResponseEntity<ModalitaPagamento> updateModalitaPagamento(@PathVariable Long id,
                                                                      @Valid @RequestBody ModalitaPagamentoDto dto) {
        return repository.findById(id)
                .map(modalita -> {
                    // Verifica duplicati codice
                    if (repository.existsByCodiceAndIdNot(dto.getCodice(), id)) {
                        return ResponseEntity.badRequest().<ModalitaPagamento>build();
                    }

                    modalita.setCodice(dto.getCodice());
                    modalita.setDescrizione(dto.getDescrizione());
                    modalita.setGiorniPagamento(dto.getGiorniPagamento());
                    modalita.setFineMese(dto.getFineMese());
                    modalita.setNumeroRate(dto.getNumeroRate());
                    modalita.setGiorniTraRate(dto.getGiorniTraRate());
                    modalita.setAttiva(dto.getAttiva());

                    ModalitaPagamento updatedModalita = repository.save(modalita);
                    return ResponseEntity.ok(updatedModalita);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Elimina una modalità di pagamento
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteModalitaPagamento(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Ricerca modalità di pagamento per descrizione
     */
    @GetMapping("/search")
    public ResponseEntity<List<ModalitaPagamento>> searchModalitaPagamento(@RequestParam String descrizione) {
        List<ModalitaPagamento> modalita = repository.findByDescrizioneLike(descrizione);
        return ResponseEntity.ok(modalita);
    }

    /**
     * Recupera modalità di pagamento ratali
     */
    @GetMapping("/ratali")
    public ResponseEntity<List<ModalitaPagamento>> getModalitaPagamentoRatali() {
        List<ModalitaPagamento> modalita = repository.findRatali();
        return ResponseEntity.ok(modalita);
    }

    /**
     * Recupera modalità di pagamento dilazionate
     */
    @GetMapping("/dilazionate")
    public ResponseEntity<List<ModalitaPagamento>> getModalitaPagamentoDilazionate() {
        List<ModalitaPagamento> modalita = repository.findDilazionate();
        return ResponseEntity.ok(modalita);
    }
}
