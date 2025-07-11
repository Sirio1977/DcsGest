package com.gestionale.repository;

import com.gestionale.entity.Articolo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ArticoloRepository extends JpaRepository<Articolo, Long> {
    
    Optional<Articolo> findByCodice(String codice);
    
    List<Articolo> findByDescrizioneContainingIgnoreCase(String descrizione);
    
    List<Articolo> findByTipo(Articolo.TipoArticolo tipo);
    
    List<Articolo> findByAttivoTrue();
    
    List<Articolo> findByAttivoFalse();
    
    List<Articolo> findByCategoria(String categoria);
    
    @Query("SELECT a FROM Articolo a WHERE a.giacenza <= a.giacenzaMinima AND a.attivo = true")
    List<Articolo> findArticoliSottoScorta();
    
    @Query("SELECT a FROM Articolo a WHERE a.giacenza = 0 AND a.attivo = true")
    List<Articolo> findArticoliEsauriti();
    
    @Query("SELECT a FROM Articolo a WHERE " +
           "LOWER(a.codice) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.descrizione) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.categoria) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Articolo> searchArticoli(@Param("search") String search);
    
    @Query("SELECT a FROM Articolo a WHERE a.prezzoVendita BETWEEN :prezzoMin AND :prezzoMax")
    List<Articolo> findByPrezzoRange(@Param("prezzoMin") BigDecimal prezzoMin, 
                                    @Param("prezzoMax") BigDecimal prezzoMax);
    
    @Query("SELECT DISTINCT a.categoria FROM Articolo a WHERE a.categoria IS NOT NULL ORDER BY a.categoria")
    List<String> findDistinctCategorie();
    
    @Query("SELECT DISTINCT a.fornitore FROM Articolo a WHERE a.fornitore IS NOT NULL ORDER BY a.fornitore")
    List<String> findAllFornitori();
    
    boolean existsByCodice(String codice);
    
    long countByAttivoTrue();
    
    long countByAttivoFalse();
    
    long countByAttivo(boolean attivo);
}
