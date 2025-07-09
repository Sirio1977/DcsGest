package com.gestionale.service;

import com.gestionale.entity.ArticoloFornitore;
import com.gestionale.repository.ArticoloFornitoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ArticoloFornitoreService {
    
    @Autowired
    private ArticoloFornitoreRepository articoloFornitoreRepository;
    
    public List<ArticoloFornitore> getAllArticoliFornitori() {
        return articoloFornitoreRepository.findAll();
    }
    
    public Page<ArticoloFornitore> getArticoliFornitoriPaginati(Pageable pageable) {
        return articoloFornitoreRepository.findAll(pageable);
    }
    
    public Optional<ArticoloFornitore> getArticoloFornitoreById(Long id) {
        return articoloFornitoreRepository.findById(id);
    }
    
    public Optional<ArticoloFornitore> getArticoloFornitoreByCodice(String codice) {
        return articoloFornitoreRepository.findByCodice(codice);
    }
    
    public List<ArticoloFornitore> getArticoliByFornitore(String partitaIva) {
        return articoloFornitoreRepository.findByFornitorePartitaIva(partitaIva);
    }
    
    public List<ArticoloFornitore> searchArticoliFornitori(String query) {
        return articoloFornitoreRepository.searchArticoli(query);
    }
    
    public ArticoloFornitore createArticoloFornitore(ArticoloFornitore articoloFornitore) {
        // Verifica duplicati basata su codice + partita IVA fornitore
        if (articoloFornitore.getFornitorePartitaIva() != null) {
            Optional<ArticoloFornitore> existing = articoloFornitoreRepository
                .findByCodiceAndFornitorePartitaIva(articoloFornitore.getCodice(), articoloFornitore.getFornitorePartitaIva());
            if (existing.isPresent()) {
                throw new RuntimeException("Articolo fornitore con codice " + articoloFornitore.getCodice() + 
                    " per il fornitore " + articoloFornitore.getFornitorePartitaIva() + " già esistente");
            }
        }
        
        articoloFornitore.setCreatedAt(LocalDateTime.now());
        articoloFornitore.setUpdatedAt(LocalDateTime.now());
        return articoloFornitoreRepository.save(articoloFornitore);
    }
    
    public Optional<ArticoloFornitore> updateArticoloFornitore(Long id, ArticoloFornitore articoloFornitoreAggiornato) {
        return articoloFornitoreRepository.findById(id)
            .map(articoloEsistente -> {
                // Aggiorna i campi
                articoloEsistente.setCodice(articoloFornitoreAggiornato.getCodice());
                articoloEsistente.setDescrizione(articoloFornitoreAggiornato.getDescrizione());
                articoloEsistente.setPrezzoUnitario(articoloFornitoreAggiornato.getPrezzoUnitario());
                articoloEsistente.setUnitaMisura(articoloFornitoreAggiornato.getUnitaMisura());
                articoloEsistente.setAliquotaIVA(articoloFornitoreAggiornato.getAliquotaIVA());
                articoloEsistente.setFornitorePartitaIva(articoloFornitoreAggiornato.getFornitorePartitaIva());
                articoloEsistente.setFornitoreRagioneSociale(articoloFornitoreAggiornato.getFornitoreRagioneSociale());
                articoloEsistente.setFornitoreCategoria(articoloFornitoreAggiornato.getFornitoreCategoria());
                articoloEsistente.setCodiceInterno(articoloFornitoreAggiornato.getCodiceInterno());
                articoloEsistente.setUpdatedAt(LocalDateTime.now());
                
                return articoloFornitoreRepository.save(articoloEsistente);
            });
    }
    
    public boolean deleteArticoloFornitore(Long id) {
        if (articoloFornitoreRepository.existsById(id)) {
            articoloFornitoreRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public Long countArticoliFornitori() {
        return articoloFornitoreRepository.count();
    }
    
    public List<String> getAllFornitori() {
        return articoloFornitoreRepository.findAll()
            .stream()
            .filter(af -> af.getFornitoreRagioneSociale() != null)
            .map(ArticoloFornitore::getFornitoreRagioneSociale)
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }
}
