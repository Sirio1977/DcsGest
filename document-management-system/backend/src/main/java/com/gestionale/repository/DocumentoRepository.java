package com.gestionale.repository;

import com.gestionale.entity.Documento;
import com.gestionale.entity.StatoDocumento;
import com.gestionale.entity.TipoDocumento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository per la gestione dei documenti
 */
@Repository
public interface DocumentoRepository extends JpaRepository<Documento, Long>, JpaSpecificationExecutor<Documento> {

    // ==================== QUERY BASE ====================
    
    /**
     * Trova documenti per tipo
     */
    List<Documento> findByTipoDocumentoOrderByDataDocumentoDesc(TipoDocumento tipoDocumento);

    /**
     * Trova documenti per tipo con paginazione
     */
    Page<Documento> findByTipoDocumentoOrderByDataDocumentoDesc(TipoDocumento tipoDocumento, Pageable pageable);

    /**
     * Trova documenti per soggetto
     */
    List<Documento> findBySoggettoIdOrderByDataDocumentoDesc(Long soggettoId);

    /**
     * Trova documenti per soggetto con paginazione
     */
    Page<Documento> findBySoggettoIdOrderByDataDocumentoDesc(Long soggettoId, Pageable pageable);

    /**
     * Trova documenti per stato
     */
    List<Documento> findByStatoOrderByDataDocumentoDesc(StatoDocumento stato);

    /**
     * Trova documenti per anno
     */
    List<Documento> findByAnnoOrderByDataDocumentoDesc(Integer anno);

    /**
     * Trova documento per tipo, numero e anno
     */
    Optional<Documento> findByTipoDocumentoAndNumeroAndAnno(TipoDocumento tipoDocumento, Long numero, Integer anno);

    /**
     * Verifica esistenza documento per tipo, numero e anno
     */
    boolean existsByTipoDocumentoAndNumeroAndAnno(TipoDocumento tipoDocumento, Long numero, Integer anno);

    // ==================== QUERY FILTRI AVANZATI ====================

    /**
     * Trova documenti con filtri multipli
     */
    @Query("SELECT d FROM Documento d WHERE " +
           "(:tipoDocumento IS NULL OR d.tipoDocumento = :tipoDocumento) AND " +
           "(:dataInizio IS NULL OR d.dataDocumento >= :dataInizio) AND " +
           "(:dataFine IS NULL OR d.dataDocumento <= :dataFine) AND " +
           "(:soggettoId IS NULL OR d.soggetto.id = :soggettoId) AND " +
           "(:stato IS NULL OR d.stato = :stato) AND " +
           "(:soggettoFilter IS NULL OR " +
           " LOWER(d.ragioneSociale) LIKE LOWER(CONCAT('%', :soggettoFilter, '%')) OR " +
           " LOWER(d.partitaIva) LIKE LOWER(CONCAT('%', :soggettoFilter, '%'))) " +
           "ORDER BY d.dataDocumento DESC, d.numero DESC")
    Page<Documento> findWithFilters(
            @Param("tipoDocumento") TipoDocumento tipoDocumento,
            @Param("dataInizio") LocalDate dataInizio,
            @Param("dataFine") LocalDate dataFine,
            @Param("soggettoId") Long soggettoId,
            @Param("stato") StatoDocumento stato,
            @Param("soggettoFilter") String soggettoFilter,
            Pageable pageable);

    /**
     * Trova ultimo numero per tipo documento e anno
     */
    @Query("SELECT MAX(d.numero) FROM Documento d WHERE " +
           "d.tipoDocumento = :tipoDocumento AND " +
           "d.anno = :anno")
    Optional<Long> findLastNumeroByTipoAndAnno(
            @Param("tipoDocumento") TipoDocumento tipoDocumento,
            @Param("anno") Integer anno);

    /**
     * Calcola fatturato per periodo
     */
    @Query("SELECT COALESCE(SUM(d.totaleDocumento), 0) FROM Documento d WHERE " +
           "d.tipoDocumento = :tipoDocumento AND " +
           "d.dataDocumento BETWEEN :dataInizio AND :dataFine AND " +
           "d.stato NOT IN ('BOZZA', 'ANNULLATO')")
    BigDecimal calculateFatturatoByPeriodo(
            @Param("tipoDocumento") TipoDocumento tipoDocumento,
            @Param("dataInizio") LocalDate dataInizio,
            @Param("dataFine") LocalDate dataFine);

    /**
     * Trova documenti per ricerca testuale
     */
    @Query("SELECT d FROM Documento d WHERE " +
           "LOWER(d.ragioneSociale) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(d.partitaIva) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(d.note) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "CAST(d.numero AS string) LIKE CONCAT('%', :searchText, '%') " +
           "ORDER BY d.dataDocumento DESC")
    Page<Documento> findBySearchText(@Param("searchText") String searchText, Pageable pageable);

    /**
     * Conta i documenti che hanno come origine il documento specificato
     */
    @Query("SELECT COUNT(d) FROM Documento d WHERE d.documentoOrigine = :documentoOrigine")
    long countByDocumentoOrigine(@Param("documentoOrigine") Documento documentoOrigine);
}
