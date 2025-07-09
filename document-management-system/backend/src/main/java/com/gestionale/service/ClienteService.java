package com.gestionale.service;

import com.gestionale.entity.Cliente;
import com.gestionale.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    public List<Cliente> getAllClienti() {
        return clienteRepository.findAll();
    }
    
    public Page<Cliente> getClientiPaginati(Pageable pageable) {
        return clienteRepository.findAll(pageable);
    }
    
    public Optional<Cliente> getClienteById(Long id) {
        return clienteRepository.findById(id);
    }
    
    public List<Cliente> getClientiByRagioneSociale(String ragioneSociale) {
        return clienteRepository.findByRagioneSocialeContainingIgnoreCase(ragioneSociale);
    }
    
    public Optional<Cliente> getClienteByPartitaIva(String partitaIva) {
        return clienteRepository.findByPartitaIva(partitaIva);
    }
    
    public Optional<Cliente> getClienteByCodiceFiscale(String codiceFiscale) {
        return clienteRepository.findByCodiceFiscale(codiceFiscale);
    }
    
    public List<Cliente> getClientiAttivi() {
        return clienteRepository.findByAttivoTrue();
    }
    
    public List<Cliente> searchClienti(String query) {
        return clienteRepository.searchClienti(query);
    }
    
    public List<Cliente> getClientiByTipo(String tipo) {
        try {
            Cliente.TipoCliente tipoEnum = Cliente.TipoCliente.valueOf(tipo);
            return clienteRepository.findByTipo(tipoEnum);
        } catch (IllegalArgumentException e) {
            return List.of();
        }
    }
    
    @Transactional
    public Cliente createCliente(Cliente cliente) {
        cliente.setCreatedAt(LocalDateTime.now());
        return clienteRepository.save(cliente);
    }
    
    @Transactional
    public Optional<Cliente> updateCliente(Long id, Cliente clienteAggiornato) {
        return clienteRepository.findById(id)
                .map(cliente -> {
                    // Aggiorna solo i campi non null
                    if (clienteAggiornato.getRagioneSociale() != null) {
                        cliente.setRagioneSociale(clienteAggiornato.getRagioneSociale());
                    }
                    if (clienteAggiornato.getPartitaIva() != null) {
                        cliente.setPartitaIva(clienteAggiornato.getPartitaIva());
                    }
                    if (clienteAggiornato.getCodiceFiscale() != null) {
                        cliente.setCodiceFiscale(clienteAggiornato.getCodiceFiscale());
                    }
                    if (clienteAggiornato.getIndirizzo() != null) {
                        cliente.setIndirizzo(clienteAggiornato.getIndirizzo());
                    }
                    if (clienteAggiornato.getCitta() != null) {
                        cliente.setCitta(clienteAggiornato.getCitta());
                    }
                    if (clienteAggiornato.getCap() != null) {
                        cliente.setCap(clienteAggiornato.getCap());
                    }
                    if (clienteAggiornato.getProvincia() != null) {
                        cliente.setProvincia(clienteAggiornato.getProvincia());
                    }
                    if (clienteAggiornato.getTelefono() != null) {
                        cliente.setTelefono(clienteAggiornato.getTelefono());
                    }
                    if (clienteAggiornato.getEmail() != null) {
                        cliente.setEmail(clienteAggiornato.getEmail());
                    }
                    if (clienteAggiornato.getPec() != null) {
                        cliente.setPec(clienteAggiornato.getPec());
                    }
                    if (clienteAggiornato.getTipo() != null) {
                        cliente.setTipo(clienteAggiornato.getTipo());
                    }
                    if (clienteAggiornato.getAttivo() != null) {
                        cliente.setAttivo(clienteAggiornato.getAttivo());
                    }
                    if (clienteAggiornato.getNote() != null) {
                        cliente.setNote(clienteAggiornato.getNote());
                    }
                    cliente.setUpdatedAt(LocalDateTime.now());
                    return clienteRepository.save(cliente);
                });
    }
    
    @Transactional
    public boolean deleteCliente(Long id) {
        return clienteRepository.findById(id)
                .map(cliente -> {
                    clienteRepository.delete(cliente);
                    return true;
                })
                .orElse(false);
    }
    
    @Transactional
    public boolean disattivaCliente(Long id) {
        return clienteRepository.findById(id)
                .map(cliente -> {
                    cliente.setAttivo(false);
                    cliente.setUpdatedAt(LocalDateTime.now());
                    clienteRepository.save(cliente);
                    return true;
                })
                .orElse(false);
    }
    
    @Transactional
    public boolean attivaCliente(Long id) {
        return clienteRepository.findById(id)
                .map(cliente -> {
                    cliente.setAttivo(true);
                    cliente.setUpdatedAt(LocalDateTime.now());
                    clienteRepository.save(cliente);
                    return true;
                })
                .orElse(false);
    }
    
    public long countClientiAttivi() {
        return clienteRepository.countByAttivoTrue();
    }
    
    public long countClientiInattivi() {
        return clienteRepository.countByAttivoFalse();
    }
    
    public List<String> getAllTipiClienti() {
        return Arrays.stream(Cliente.TipoCliente.values())
                .map(Enum::name)
                .toList();
    }
}
