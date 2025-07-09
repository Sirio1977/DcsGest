package com.gestionale.repository;

import com.gestionale.entity.ArticoloFornitore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticoloFornitoreRepository extends JpaRepository<ArticoloFornitore, Long> {
    
    // Trova per codice
    Optional<ArticoloFornitore> findByCodice(String codice);
    
    // Trova per codice e fornitore
    Optional<ArticoloFornitore> findByCodiceAndFornitorePartitaIva(String codice, String fornitorePartitaIva);
    
    // Trova per fornitore
    List<ArticoloFornitore> findByFornitorePartitaIva(String fornitorePartitaIva);
    
    // Trova per ragione sociale fornitore
    List<ArticoloFornitore> findByFornitoreRagioneSocialeContainingIgnoreCase(String ragioneSociale);
    
    // Trova per descrizione articolo
    List<ArticoloFornitore> findByDescrizioneContainingIgnoreCase(String descrizione);
    
    // Trova per categoria fornitore
    List<ArticoloFornitore> findByFornitoreCategoriaIgnoreCase(String categoria);
    
    // Query personalizzata per cercare articoli
    @Query("SELECT a FROM ArticoloFornitore a WHERE " +
           "LOWER(a.codice) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.descrizione) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.fornitoreRagioneSociale) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<ArticoloFornitore> searchArticoli(@Param("search") String search);
    
    // Verifica esistenza per codice
    boolean existsByCodice(String codice);
    
    // Verifica esistenza per codice e fornitore
    boolean existsByCodiceAndFornitorePartitaIva(String codice, String fornitorePartitaIva);
    
    // Conta articoli per fornitore
    long countByFornitorePartitaIva(String fornitorePartitaIva);
    
    // Trova tutti ordinati per data documento desc
    List<ArticoloFornitore> findAllByOrderByDataDocumentoDesc();
    
    // Trova per range di prezzi
    @Query("SELECT a FROM ArticoloFornitore a WHERE a.prezzoUnitario BETWEEN :minPrezzo AND :maxPrezzo")
    List<ArticoloFornitore> findByPrezzoRange(@Param("minPrezzo") Double minPrezzo, @Param("maxPrezzo") Double maxPrezzo);
}
