package com.gestionale.repository;

import com.gestionale.entity.CausaleTrasporto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository per la gestione delle causali di trasporto
 */
@Repository
public interface CausaleTrasportoRepository extends JpaRepository<CausaleTrasporto, Long> {

    /**
     * Trova causale per codice
     */
    Optional<CausaleTrasporto> findByCodice(String codice);

    /**
     * Trova causale per codice ignorando case
     */
    Optional<CausaleTrasporto> findByCodiceIgnoreCase(String codice);

    /**
     * Trova causali attive
     */
    List<CausaleTrasporto> findByAttivaTrue();

    /**
     * Trova causali per descrizione (case-insensitive)
     */
    @Query("SELECT ct FROM CausaleTrasporto ct WHERE LOWER(ct.descrizione) LIKE LOWER(CONCAT('%', :descrizione, '%'))")
    List<CausaleTrasporto> findByDescrizioneLike(@Param("descrizione") String descrizione);

    /**
     * Verifica se esiste una causale con il codice specificato
     */
    boolean existsByCodice(String codice);

    /**
     * Verifica se esiste una causale con il codice specificato escludendo un ID
     */
    boolean existsByCodiceAndIdNot(String codice, Long id);
}
