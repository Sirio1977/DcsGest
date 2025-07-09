package com.gestionale.repository;

import com.gestionale.entity.Configurazione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfigurazioneRepository extends JpaRepository<Configurazione, Long> {
    
    Optional<Configurazione> findByChiave(String chiave);
    
    boolean existsByChiave(String chiave);
}
