package com.gestionale.repository;

import com.gestionale.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    
    Optional<Cliente> findByPartitaIva(String partitaIva);
    
    Optional<Cliente> findByCodiceFiscale(String codiceFiscale);
    
    List<Cliente> findByRagioneSocialeContainingIgnoreCase(String ragioneSociale);
    
    List<Cliente> findByTipo(Cliente.TipoCliente tipo);
    
    List<Cliente> findByAttivoTrue();
    
    List<Cliente> findByAttivoFalse();
    
    @Query("SELECT c FROM Cliente c WHERE c.attivo = true AND c.tipo IN (:tipi)")
    List<Cliente> findByTipiAndAttivo(@Param("tipi") List<Cliente.TipoCliente> tipi);
    
    @Query("SELECT c FROM Cliente c WHERE " +
           "LOWER(c.ragioneSociale) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.partitaIva) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Cliente> searchClienti(@Param("search") String search);
    
    boolean existsByPartitaIva(String partitaIva);
    
    boolean existsByCodiceFiscale(String codiceFiscale);

    long countByAttivoTrue(); // Conta i clienti attivi
    
    long countByAttivoFalse(); // Conta i clienti inattivi
}
