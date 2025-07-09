package com.gestionale.service;

import com.gestionale.entity.Utente;
import com.gestionale.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UtenteService {

    private final UtenteRepository utenteRepository;

    @Autowired
    public UtenteService(UtenteRepository utenteRepository) {
        this.utenteRepository = utenteRepository;
    }

    public List<Utente> getAllUtenti() {
        return utenteRepository.findAll();
    }

    public Optional<Utente> getUtenteById(Long id) {
        return utenteRepository.findById(id);
    }

    public Utente createUtente(Utente utente) {
        return utenteRepository.save(utente);
    }

    public Optional<Utente> updateUtente(Long id, Utente utente) {
        return utenteRepository.findById(id).map(existingUtente -> {
            existingUtente.setNome(utente.getNome());
            existingUtente.setEmail(utente.getEmail());
            return utenteRepository.save(existingUtente);
        });
    }

    public boolean deleteUtente(Long id) {
        if (utenteRepository.existsById(id)) {
            utenteRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
