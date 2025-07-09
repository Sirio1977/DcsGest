package com.gestionale.repository;

import com.gestionale.entity.Azienda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AziendaRepository extends JpaRepository<Azienda, Long> {
    // Custom query methods can be defined here
}
