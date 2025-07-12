package com.gestionale.controller;

import com.gestionale.entity.Cliente;
import com.gestionale.service.ClienteService;
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
@RequestMapping("/api/clienti")
@CrossOrigin(origins = "*")
public class ClienteController {
    
    @Autowired
    private ClienteService clienteService;
    
    @GetMapping
    public ResponseEntity<Page<Cliente>> getAllClienti(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "ragioneSociale") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Pageable pageable = PageRequest.of(page, size, 
            sortDir.equals("desc") ? 
                org.springframework.data.domain.Sort.by(sortBy).descending() : 
                org.springframework.data.domain.Sort.by(sortBy).ascending()
        );
        Page<Cliente> clienti = clienteService.getClientiPaginati(pageable);
        return ResponseEntity.ok(clienti);
    }
    
    @GetMapping("/all")
    public List<Cliente> getAllClientiNonPaginati() {
        return clienteService.getAllClienti();
    }
    
    @GetMapping("/paginati")
    public ResponseEntity<Page<Cliente>> getClientiPaginati(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Cliente> clienti = clienteService.getClientiPaginati(pageable);
        return ResponseEntity.ok(clienti);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> getClienteById(@PathVariable Long id) {
        Optional<Cliente> cliente = clienteService.getClienteById(id);
        return cliente.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/ragione-sociale/{ragioneSociale}")
    public ResponseEntity<List<Cliente>> getClientiByRagioneSociale(@PathVariable String ragioneSociale) {
        List<Cliente> clienti = clienteService.getClientiByRagioneSociale(ragioneSociale);
        return ResponseEntity.ok(clienti);
    }
    
    @GetMapping("/partita-iva/{partitaIva}")
    public ResponseEntity<Cliente> getClienteByPartitaIva(@PathVariable String partitaIva) {
        Optional<Cliente> cliente = clienteService.getClienteByPartitaIva(partitaIva);
        return cliente.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/codice-fiscale/{codiceFiscale}")
    public ResponseEntity<Cliente> getClienteByCodiceFiscale(@PathVariable String codiceFiscale) {
        Optional<Cliente> cliente = clienteService.getClienteByCodiceFiscale(codiceFiscale);
        return cliente.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/attivi")
    public List<Cliente> getClientiAttivi() {
        return clienteService.getClientiAttivi();
    }
    
    @GetMapping("/search")
    public List<Cliente> searchClienti(@RequestParam String query) {
        return clienteService.searchClienti(query);
    }
    
    @GetMapping("/tipo/{tipo}")
    public List<Cliente> getClientiByTipo(@PathVariable String tipo) {
        return clienteService.getClientiByTipo(tipo);
    }
    
    @PostMapping
    public ResponseEntity<Cliente> createCliente(@RequestBody Cliente cliente) {
        try {
            Cliente nuovoCliente = clienteService.createCliente(cliente);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuovoCliente);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> updateCliente(@PathVariable Long id, @RequestBody Cliente cliente) {
        Optional<Cliente> clienteAggiornato = clienteService.updateCliente(id, cliente);
        return clienteAggiornato.map(ResponseEntity::ok)
                                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCliente(@PathVariable Long id) {
        boolean deleted = clienteService.deleteCliente(id);
        return deleted ? ResponseEntity.noContent().build() 
                      : ResponseEntity.notFound().build();
    }
    
    @PatchMapping("/{id}/disattiva")
    public ResponseEntity<Void> disattivaCliente(@PathVariable Long id) {
        boolean disattivato = clienteService.disattivaCliente(id);
        return disattivato ? ResponseEntity.ok().build() 
                          : ResponseEntity.notFound().build();
    }
    
    @PatchMapping("/{id}/attiva")
    public ResponseEntity<Void> attivaCliente(@PathVariable Long id) {
        boolean attivato = clienteService.attivaCliente(id);
        return attivato ? ResponseEntity.ok().build() 
                       : ResponseEntity.notFound().build();
    }
    
    @GetMapping("/stats/count-attivi")
    public ResponseEntity<Long> countClientiAttivi() {
        return ResponseEntity.ok(clienteService.countClientiAttivi());
    }
    
    @GetMapping("/stats/count-inattivi")
    public ResponseEntity<Long> countClientiInattivi() {
        return ResponseEntity.ok(clienteService.countClientiInattivi());
    }
    
    @GetMapping("/stats/tipi")
    public ResponseEntity<List<String>> getAllTipiClienti() {
        return ResponseEntity.ok(clienteService.getAllTipiClienti());
    }
}
