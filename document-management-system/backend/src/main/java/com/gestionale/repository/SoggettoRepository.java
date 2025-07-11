package com.gestionale.repository;

import com.gestionale.entity.Soggetto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository per gestire i soggetti (clienti e fornitori)
 */
@Repository
public interface SoggettoRepository extends JpaRepository<Soggetto, Long>, JpaSpecificationExecutor<Soggetto> {

    /**
     * Trova un soggetto per partita IVA
     */
    Optional<Soggetto> findByPartitaIva(String partitaIva);

    /**
     * Trova un soggetto per codice fiscale
     */
    Optional<Soggetto> findByCodiceFiscale(String codiceFiscale);

    /**
     * Trova un soggetto per partita IVA o codice fiscale
     */
    @Query("SELECT s FROM Soggetto s WHERE s.partitaIva = :partitaIva OR s.codiceFiscale = :codiceFiscale")
    Optional<Soggetto> findByPartitaIvaOrCodiceFiscale(@Param("partitaIva") String partitaIva, 
                                                      @Param("codiceFiscale") String codiceFiscale);

    /**
     * Trova soggetti per ragione sociale (ricerca parziale)
     */
    @Query("SELECT s FROM Soggetto s WHERE LOWER(s.ragioneSociale) LIKE LOWER(CONCAT('%', :ragioneSociale, '%'))")
    List<Soggetto> findByRagioneSocialeContainingIgnoreCase(@Param("ragioneSociale") String ragioneSociale);

    /**
     * Trova soggetti attivi
     */
    List<Soggetto> findByAttivoTrue();

    /**
     * Trova soggetti per tipo
     */
    @Query("SELECT s FROM Soggetto s WHERE TYPE(s) = :tipoSoggetto")
    List<Soggetto> findByTipoSoggetto(@Param("tipoSoggetto") Class<? extends Soggetto> tipoSoggetto);

    /**
     * Trova soggetti per città
     */
    List<Soggetto> findByCittaIgnoreCase(String citta);

    /**
     * Trova soggetti per provincia
     */
    List<Soggetto> findByProvinciaIgnoreCase(String provincia);

    /**
     * Verifica se esiste un soggetto con partita IVA
     */
    boolean existsByPartitaIva(String partitaIva);

    /**
     * Verifica se esiste un soggetto con codice fiscale
     */
    boolean existsByCodiceFiscale(String codiceFiscale);

    /**
     * Verifica se esiste un soggetto con partita IVA o codice fiscale
     */
    @Query("SELECT COUNT(s) > 0 FROM Soggetto s WHERE s.partitaIva = :partitaIva OR s.codiceFiscale = :codiceFiscale")
    boolean existsByPartitaIvaOrCodiceFiscale(@Param("partitaIva") String partitaIva, 
                                            @Param("codiceFiscale") String codiceFiscale);

    /**
     * Conta i documenti associati a un soggetto
     */
    @Query("SELECT COUNT(d) FROM Documento d WHERE d.soggetto = :soggetto")
    long countDocumentiBysoggetto(@Param("soggetto") Soggetto soggetto);
}
