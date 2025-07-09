package com.gestionale.controller;

import com.gestionale.entity.Azienda;
import com.gestionale.service.AziendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/aziende")
public class AziendaController {

    @Autowired
    private AziendaService aziendaService;

    @GetMapping
    public List<Azienda> getAllAziende() {
        return aziendaService.getAllAziende();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Azienda> getAziendaById(@PathVariable Long id) {
        return aziendaService.getAziendaById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Azienda createAzienda(@RequestBody Azienda azienda) {
        return aziendaService.createAzienda(azienda);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Azienda> updateAzienda(@PathVariable Long id, @RequestBody Azienda azienda) {
        return aziendaService.updateAzienda(id, azienda)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAzienda(@PathVariable Long id) {
        if (aziendaService.deleteAzienda(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
