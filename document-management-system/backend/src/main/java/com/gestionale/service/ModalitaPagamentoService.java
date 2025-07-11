package com.gestionale.service;

import com.gestionale.dto.ModalitaPagamentoDto;
import com.gestionale.entity.ModalitaPagamento;
import com.gestionale.repository.ModalitaPagamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service per la gestione delle modalità di pagamento
 */
@Service
public class ModalitaPagamentoService {

    private final ModalitaPagamentoRepository repository;

    @Autowired
    public ModalitaPagamentoService(ModalitaPagamentoRepository repository) {
        this.repository = repository;
    }

    /**
     * Recupera tutte le modalità di pagamento
     */
    public List<ModalitaPagamento> findAll() {
        return repository.findAll();
    }

    /**
     * Recupera modalità di pagamento per ID
     */
    public Optional<ModalitaPagamento> findById(Long id) {
        return repository.findById(id);
    }

    /**
     * Recupera modalità di pagamento per codice
     */
    public Optional<ModalitaPagamento> findByCodice(String codice) {
        return repository.findByCodice(codice);
    }

    /**
     * Recupera modalità di pagamento attive
     */
    public List<ModalitaPagamento> findAttive() {
        return repository.findByAttivaTrue();
    }

    /**
     * Crea una nuova modalità di pagamento
     */
    public ModalitaPagamento create(ModalitaPagamentoDto dto) {
        // Verifica duplicati
        if (repository.existsByCodice(dto.getCodice())) {
            throw new IllegalArgumentException("Codice modalità pagamento già esistente: " + dto.getCodice());
        }

        ModalitaPagamento modalita = new ModalitaPagamento();
        modalita.setCodice(dto.getCodice());
        modalita.setDescrizione(dto.getDescrizione());
        modalita.setGiorniPagamento(dto.getGiorniPagamento());
        modalita.setFineMese(dto.getFineMese());
        modalita.setNumeroRate(dto.getNumeroRate());
        modalita.setGiorniTraRate(dto.getGiorniTraRate());
        modalita.setAttiva(dto.getAttiva());

        return repository.save(modalita);
    }

    /**
     * Aggiorna una modalità di pagamento esistente
     */
    public ModalitaPagamento update(Long id, ModalitaPagamentoDto dto) {
        ModalitaPagamento modalita = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Modalità pagamento non trovata: " + id));

        // Verifica duplicati codice
        if (repository.existsByCodiceAndIdNot(dto.getCodice(), id)) {
            throw new IllegalArgumentException("Codice modalità pagamento già esistente: " + dto.getCodice());
        }

        modalita.setCodice(dto.getCodice());
        modalita.setDescrizione(dto.getDescrizione());
        modalita.setGiorniPagamento(dto.getGiorniPagamento());
        modalita.setFineMese(dto.getFineMese());
        modalita.setNumeroRate(dto.getNumeroRate());
        modalita.setGiorniTraRate(dto.getGiorniTraRate());
        modalita.setAttiva(dto.getAttiva());

        return repository.save(modalita);
    }

    /**
     * Elimina una modalità di pagamento
     */
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Modalità pagamento non trovata: " + id);
        }
        repository.deleteById(id);
    }

    /**
     * Ricerca modalità di pagamento per descrizione
     */
    public List<ModalitaPagamento> searchByDescrizione(String descrizione) {
        return repository.findByDescrizioneLike(descrizione);
    }
}
