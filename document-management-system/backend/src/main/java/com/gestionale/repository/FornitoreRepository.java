package com.gestionale.repository;

import com.gestionale.entity.Fornitore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository per la gestione dei fornitori
 */
@Repository
public interface FornitoreRepository extends JpaRepository<Fornitore, Long> {

    /**
     * Trova fornitore per partita IVA
     */
    Optional<Fornitore> findByPartitaIva(String partitaIva);

    /**
     * Trova fornitore per codice fiscale
     */
    Optional<Fornitore> findByCodiceFiscale(String codiceFiscale);

    /**
     * Trova fornitori per ragione sociale (case-insensitive)
     */
    @Query("SELECT f FROM Fornitore f WHERE LOWER(f.ragioneSociale) LIKE LOWER(CONCAT('%', :ragioneSociale, '%'))")
    List<Fornitore> findByRagioneSocialeLike(@Param("ragioneSociale") String ragioneSociale);

    /**
     * Trova fornitori attivi
     */
    List<Fornitore> findByAttivoTrue();

    /**
     * Trova fornitori per città
     */
    List<Fornitore> findByCittaIgnoreCase(String citta);

    /**
     * Trova fornitori per provincia
     */
    List<Fornitore> findByProvinciaIgnoreCase(String provincia);

    /**
     * Verifica se esiste un fornitore con la partita IVA specificata
     */
    boolean existsByPartitaIva(String partitaIva);

    /**
     * Verifica se esiste un fornitore con il codice fiscale specificato
     */
    boolean existsByCodiceFiscale(String codiceFiscale);

    /**
     * Verifica se esiste un fornitore con la partita IVA specificata escludendo un ID
     */
    boolean existsByPartitaIvaAndIdNot(String partitaIva, Long id);

    /**
     * Verifica se esiste un fornitore con il codice fiscale specificato escludendo un ID
     */
    boolean existsByCodiceFiscaleAndIdNot(String codiceFiscale, Long id);

    /**
     * Ricerca fornitori per testo libero (ragione sociale, città, email)
     */
    @Query("SELECT f FROM Fornitore f WHERE " +
           "LOWER(f.ragioneSociale) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(f.citta) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(f.email) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    List<Fornitore> searchByText(@Param("searchText") String searchText);
}
