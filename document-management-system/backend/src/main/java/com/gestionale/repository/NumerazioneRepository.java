package com.gestionale.repository;

import com.gestionale.entity.Numerazione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Repository per la gestione delle numerazioni
 */
@Repository
public interface NumerazioneRepository extends JpaRepository<Numerazione, Long> {

    /**
     * Trova numerazione per tipo documento e anno
     */
    Optional<Numerazione> findByTipoDocumentoAndAnno(String tipoDocumento, Integer anno);

    /**
     * Trova tutte le numerazioni per tipo documento
     */
    List<Numerazione> findByTipoDocumentoOrderByAnnoDesc(String tipoDocumento);

    /**
     * Trova tutte le numerazioni per anno
     */
    List<Numerazione> findByAnnoOrderByTipoDocumento(Integer anno);

    /**
     * Verifica esistenza numerazione per tipo e anno
     */
    boolean existsByTipoDocumentoAndAnno(String tipoDocumento, Integer anno);

    /**
     * Ottiene e incrementa il numero atomicamente
     */
    @Modifying
    @Transactional
    @Query("UPDATE Numerazione n SET n.ultimoNumero = n.ultimoNumero + 1 WHERE " +
           "n.tipoDocumento = :tipoDocumento AND n.anno = :anno")
    int incrementaNumero(@Param("tipoDocumento") String tipoDocumento, @Param("anno") Integer anno);

    /**
     * Ottiene il prossimo numero senza incrementare
     */
    @Query("SELECT n.ultimoNumero + 1 FROM Numerazione n WHERE " +
           "n.tipoDocumento = :tipoDocumento AND n.anno = :anno")
    Optional<Long> getNextNumero(@Param("tipoDocumento") String tipoDocumento, @Param("anno") Integer anno);

    /**
     * Reimposta numerazione per nuovo anno
     */
    @Modifying
    @Transactional
    @Query("UPDATE Numerazione n SET n.ultimoNumero = 0 WHERE " +
           "n.tipoDocumento = :tipoDocumento AND n.anno = :anno")
    int resetNumerazione(@Param("tipoDocumento") String tipoDocumento, @Param("anno") Integer anno);

    /**
     * Imposta ultimo numero
     */
    @Modifying
    @Transactional
    @Query("UPDATE Numerazione n SET n.ultimoNumero = :ultimoNumero WHERE " +
           "n.tipoDocumento = :tipoDocumento AND n.anno = :anno")
    int setUltimoNumero(@Param("tipoDocumento") String tipoDocumento, 
                        @Param("anno") Integer anno, 
                        @Param("ultimoNumero") Long ultimoNumero);

    /**
     * Trova numerazioni che necessitano reset per nuovo anno
     */
    @Query("SELECT n FROM Numerazione n WHERE n.anno < :annoCorrente")
    List<Numerazione> findNumerazioniDaAggiornare(@Param("annoCorrente") Integer annoCorrente);
}
