package com.gestionale.repository;

import com.gestionale.entity.ModalitaPagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository per la gestione delle modalità di pagamento
 */
@Repository
public interface ModalitaPagamentoRepository extends JpaRepository<ModalitaPagamento, Long> {

    /**
     * Trova modalità per codice
     */
    Optional<ModalitaPagamento> findByCodice(String codice);

    /**
     * Trova modalità per codice ignorando case
     */
    Optional<ModalitaPagamento> findByCodiceIgnoreCase(String codice);

    /**
     * Trova modalità attive
     */
    List<ModalitaPagamento> findByAttivaTrue();

    /**
     * Trova modalità per descrizione (case-insensitive)
     */
    @Query("SELECT mp FROM ModalitaPagamento mp WHERE LOWER(mp.descrizione) LIKE LOWER(CONCAT('%', :descrizione, '%'))")
    List<ModalitaPagamento> findByDescrizioneLike(@Param("descrizione") String descrizione);

    /**
     * Trova modalità ratali
     */
    @Query("SELECT mp FROM ModalitaPagamento mp WHERE mp.numeroRate > 1")
    List<ModalitaPagamento> findRatali();

    /**
     * Trova modalità a pagamento dilazionato
     */
    @Query("SELECT mp FROM ModalitaPagamento mp WHERE mp.giorniPagamento > 0")
    List<ModalitaPagamento> findDilazionate();

    /**
     * Verifica se esiste una modalità con il codice specificato
     */
    boolean existsByCodice(String codice);

    /**
     * Verifica se esiste una modalità con il codice specificato escludendo un ID
     */
    boolean existsByCodiceAndIdNot(String codice, Long id);
}
