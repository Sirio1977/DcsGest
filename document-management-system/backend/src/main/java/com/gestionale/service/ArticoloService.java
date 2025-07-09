package com.gestionale.service;

import com.gestionale.entity.Articolo;
import com.gestionale.repository.ArticoloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ArticoloService {
    
    @Autowired
    private ArticoloRepository articoloRepository;
    
    public List<Articolo> getAllArticoli() {
        return articoloRepository.findAll();
    }
    
    public Page<Articolo> getArticoliPaginati(Pageable pageable) {
        return articoloRepository.findAll(pageable);
    }
    
    public Optional<Articolo> getArticoloById(Long id) {
        return articoloRepository.findById(id);
    }
    
    public Optional<Articolo> getArticoloByCodice(String codice) {
        return articoloRepository.findByCodice(codice);
    }
    
    public List<Articolo> getArticoliAttivi() {
        return articoloRepository.findByAttivoTrue();
    }
    
    public List<Articolo> searchArticoli(String query) {
        return articoloRepository.searchArticoli(query);
    }
    
    public List<Articolo> getArticoliByCategoria(String categoria) {
        return articoloRepository.findByCategoria(categoria);
    }
    
    public List<Articolo> getArticoliByPrezzoRange(BigDecimal minPrezzo, BigDecimal maxPrezzo) {
        return articoloRepository.findByPrezzoRange(minPrezzo, maxPrezzo);
    }
    
    public Articolo createArticolo(Articolo articolo) {
        if (articoloRepository.existsByCodice(articolo.getCodice())) {
            throw new RuntimeException("Articolo con codice " + articolo.getCodice() + " già esistente");
        }
        
        articolo.setCreatedAt(LocalDateTime.now());
        articolo.setUpdatedAt(LocalDateTime.now());
        return articoloRepository.save(articolo);
    }
    
    public Optional<Articolo> updateArticolo(Long id, Articolo articoloAggiornato) {
        return articoloRepository.findById(id)
                .map(articolo -> {
                    articolo.setCodice(articoloAggiornato.getCodice());
                    articolo.setDescrizione(articoloAggiornato.getDescrizione());
                    articolo.setPrezzo(articoloAggiornato.getPrezzo());
                    articolo.setCategoria(articoloAggiornato.getCategoria());
                    articolo.setTipo(articoloAggiornato.getTipo());
                    articolo.setGiacenza(articoloAggiornato.getGiacenza());
                    articolo.setGiacenzaMinima(articoloAggiornato.getGiacenzaMinima());
                    articolo.setFornitore(articoloAggiornato.getFornitore());
                    articolo.setUpdatedAt(LocalDateTime.now());
                    return articoloRepository.save(articolo);
                });
    }
    
    public boolean deleteArticolo(Long id) {
        if (articoloRepository.existsById(id)) {
            articoloRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public boolean disattivaArticolo(Long id) {
        return articoloRepository.findById(id)
                .map(articolo -> {
                    articolo.setAttivo(false);
                    articolo.setUpdatedAt(LocalDateTime.now());
                    articoloRepository.save(articolo);
                    return true;
                }).orElse(false);
    }
    
    public boolean attivaArticolo(Long id) {
        return articoloRepository.findById(id)
                .map(articolo -> {
                    articolo.setAttivo(true);
                    articolo.setUpdatedAt(LocalDateTime.now());
                    articoloRepository.save(articolo);
                    return true;
                }).orElse(false);
    }
    
    public Long countArticoliAttivi() {
        return articoloRepository.countByAttivoTrue();
    }
    
    public Long countArticoliInattivi() {
        return articoloRepository.countByAttivoFalse();
    }
    
    public List<String> getAllCategorie() {
        return articoloRepository.findDistinctCategorie();
    }
}
