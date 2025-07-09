package com.gestionale.service;

import com.gestionale.entity.Azienda;
import com.gestionale.repository.AziendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AziendaService {

    private final AziendaRepository aziendaRepository;

    @Autowired
    public AziendaService(AziendaRepository aziendaRepository) {
        this.aziendaRepository = aziendaRepository;
    }

    public List<Azienda> getAllAziende() {
        return aziendaRepository.findAll();
    }

    public Optional<Azienda> getAziendaById(Long id) {
        return aziendaRepository.findById(id);
    }

    public Azienda createAzienda(Azienda azienda) {
        return aziendaRepository.save(azienda);
    }

    public Optional<Azienda> updateAzienda(Long id, Azienda azienda) {
        return aziendaRepository.findById(id).map(existingAzienda -> {
            existingAzienda.setRagioneSociale(azienda.getRagioneSociale());
            existingAzienda.setIndirizzo(azienda.getIndirizzo());
            return aziendaRepository.save(existingAzienda);
        });
    }

    public boolean deleteAzienda(Long id) {
        if (aziendaRepository.existsById(id)) {
            aziendaRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
