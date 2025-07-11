package com.gestionale.service;

import com.gestionale.entity.Numerazione;
import com.gestionale.entity.TipoDocumento;
import com.gestionale.repository.NumerazioneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service per la gestione delle numerazioni documenti
 */
@Service
@Transactional
public class NumerazioneService {

    @Autowired
    private NumerazioneRepository numerazioneRepository;

    /**
     * Ottiene il prossimo numero per un tipo documento
     */
    public Long getNextNumber(TipoDocumento tipoDocumento) {
        return getNextNumber(tipoDocumento, LocalDate.now().getYear());
    }

    /**
     * Ottiene il prossimo numero per un tipo documento e anno specifico
     */
    public Long getNextNumber(TipoDocumento tipoDocumento, Integer anno) {
        String tipoStr = tipoDocumento.name();
        
        // Cerca numerazione esistente
        Optional<Numerazione> numerazioneOpt = numerazioneRepository
            .findByTipoDocumentoAndAnno(tipoStr, anno);
        
        if (numerazioneOpt.isPresent()) {
            Numerazione numerazione = numerazioneOpt.get();
            
            // Verifica se è necessario reset per nuovo anno
            if (numerazione.needsResetForYear(anno)) {
                numerazione.resetPerAnno(anno);
                numerazioneRepository.save(numerazione);
            }
            
            return numerazione.incrementaNumero();
        } else {
            // Crea nuova numerazione
            Numerazione nuovaNumerazione = new Numerazione();
            nuovaNumerazione.setTipoDocumento(tipoStr);
            nuovaNumerazione.setAnno(anno);
            nuovaNumerazione.setUltimoNumero(1L);
            
            numerazioneRepository.save(nuovaNumerazione);
            return 1L;
        }
    }

    /**
     * Preview del prossimo numero senza incrementare
     */
    public Long previewNextNumber(TipoDocumento tipoDocumento) {
        return previewNextNumber(tipoDocumento, LocalDate.now().getYear());
    }

    /**
     * Preview del prossimo numero senza incrementare per anno specifico
     */
    public Long previewNextNumber(TipoDocumento tipoDocumento, Integer anno) {
        String tipoStr = tipoDocumento.name();
        
        Optional<Numerazione> numerazioneOpt = numerazioneRepository
            .findByTipoDocumentoAndAnno(tipoStr, anno);
        
        if (numerazioneOpt.isPresent()) {
            Numerazione numerazione = numerazioneOpt.get();
            
            // Se è un anno diverso, il prossimo numero sarebbe 1
            if (numerazione.needsResetForYear(anno)) {
                return 1L;
            }
            
            return numerazione.getNextNumero();
        } else {
            return 1L;
        }
    }

    /**
     * Formatta il numero con prefisso e suffisso
     */
    public String formatNumber(TipoDocumento tipoDocumento, Long numero) {
        return formatNumber(tipoDocumento, numero, LocalDate.now().getYear());
    }

    /**
     * Formatta il numero con prefisso e suffisso per anno specifico
     */
    public String formatNumber(TipoDocumento tipoDocumento, Long numero, Integer anno) {
        String tipoStr = tipoDocumento.name();
        
        Optional<Numerazione> numerazioneOpt = numerazioneRepository
            .findByTipoDocumentoAndAnno(tipoStr, anno);
        
        if (numerazioneOpt.isPresent()) {
            return numerazioneOpt.get().formatNumero(numero);
        } else {
            return numero.toString();
        }
    }

    /**
     * Ottiene la numerazione per tipo documento e anno
     */
    public Optional<Numerazione> getNumerazione(TipoDocumento tipoDocumento, Integer anno) {
        return numerazioneRepository.findByTipoDocumentoAndAnno(tipoDocumento.name(), anno);
    }

    /**
     * Crea o aggiorna la configurazione di numerazione
     */
    public Numerazione saveNumerazione(Numerazione numerazione) {
        return numerazioneRepository.save(numerazione);
    }

    /**
     * Imposta configurazione numerazione per un tipo documento
     */
    public void configuraNumerazione(TipoDocumento tipoDocumento, Integer anno,
                                   String prefisso, String suffisso, Integer lunghezza) {
        String tipoStr = tipoDocumento.name();
        
        Optional<Numerazione> numerazioneOpt = numerazioneRepository
            .findByTipoDocumentoAndAnno(tipoStr, anno);
        
        Numerazione numerazione;
        if (numerazioneOpt.isPresent()) {
            numerazione = numerazioneOpt.get();
        } else {
            numerazione = new Numerazione();
            numerazione.setTipoDocumento(tipoStr);
            numerazione.setAnno(anno);
            numerazione.setUltimoNumero(0L);
        }
        
        numerazione.setPrefisso(prefisso);
        numerazione.setSuffisso(suffisso);
        numerazione.setLunghezzaNumero(lunghezza);
        
        numerazioneRepository.save(numerazione);
    }

    /**
     * Reimposta numerazione per nuovo anno
     */
    public void resetNumerazionePerAnno(TipoDocumento tipoDocumento, Integer anno) {
        String tipoStr = tipoDocumento.name();
        
        Optional<Numerazione> numerazioneOpt = numerazioneRepository
            .findByTipoDocumentoAndAnno(tipoStr, anno);
        
        if (numerazioneOpt.isPresent()) {
            Numerazione numerazione = numerazioneOpt.get();
            numerazione.resetPerAnno(anno);
            numerazioneRepository.save(numerazione);
        }
    }

    /**
     * Inizializza numerazioni per tutti i tipi documento per l'anno corrente
     */
    public void inizializzaNumerazioniAnnoCorrente() {
        Integer annoCorrente = LocalDate.now().getYear();
        
        for (TipoDocumento tipo : TipoDocumento.values()) {
            String tipoStr = tipo.name();
            
            if (!numerazioneRepository.existsByTipoDocumentoAndAnno(tipoStr, annoCorrente)) {
                Numerazione numerazione = new Numerazione();
                numerazione.setTipoDocumento(tipoStr);
                numerazione.setAnno(annoCorrente);
                numerazione.setUltimoNumero(0L);
                
                numerazioneRepository.save(numerazione);
            }
        }
    }

    /**
     * Ottiene tutte le numerazioni per tipo documento
     */
    public List<Numerazione> getNumerazioniByTipo(TipoDocumento tipoDocumento) {
        return numerazioneRepository.findByTipoDocumentoOrderByAnnoDesc(tipoDocumento.name());
    }

    /**
     * Ottiene tutte le numerazioni per anno
     */
    public List<Numerazione> getNumerazioniByAnno(Integer anno) {
        return numerazioneRepository.findByAnnoOrderByTipoDocumento(anno);
    }

    /**
     * Aggiorna automaticamente le numerazioni per il nuovo anno
     */
    public void aggiornaPerNuovoAnno() {
        Integer annoCorrente = LocalDate.now().getYear();
        
        // Trova numerazioni da aggiornare
        List<Numerazione> numerazioniVecchie = numerazioneRepository
            .findNumerazioniDaAggiornare(annoCorrente);
        
        // Crea numerazioni per anno corrente basate su quelle vecchie
        for (Numerazione vecchia : numerazioniVecchie) {
            if (!numerazioneRepository.existsByTipoDocumentoAndAnno(
                    vecchia.getTipoDocumento(), annoCorrente)) {
                
                Numerazione nuova = new Numerazione();
                nuova.setTipoDocumento(vecchia.getTipoDocumento());
                nuova.setAnno(annoCorrente);
                nuova.setUltimoNumero(0L);
                nuova.setPrefisso(vecchia.getPrefisso());
                nuova.setSuffisso(vecchia.getSuffisso());
                nuova.setLunghezzaNumero(vecchia.getLunghezzaNumero());
                
                numerazioneRepository.save(nuova);
            }
        }
    }

    /**
     * Verifica se un numero è già stato utilizzato
     */
    public boolean isNumeroGiaUtilizzato(TipoDocumento tipoDocumento, Long numero, Integer anno) {
        Optional<Numerazione> numerazioneOpt = numerazioneRepository
            .findByTipoDocumentoAndAnno(tipoDocumento.name(), anno);
        
        return numerazioneOpt.isPresent() && numero <= numerazioneOpt.get().getUltimoNumero();
    }

    /**
     * Sincronizza numerazione con ultimo numero documento esistente
     */
    public void sincronizzaNumerazione(TipoDocumento tipoDocumento, Integer anno, Long ultimoNumero) {
        String tipoStr = tipoDocumento.name();
        
        Optional<Numerazione> numerazioneOpt = numerazioneRepository
            .findByTipoDocumentoAndAnno(tipoStr, anno);
        
        if (numerazioneOpt.isPresent()) {
            Numerazione numerazione = numerazioneOpt.get();
            if (ultimoNumero > numerazione.getUltimoNumero()) {
                numerazione.setUltimoNumero(ultimoNumero);
                numerazioneRepository.save(numerazione);
            }
        } else {
            Numerazione nuova = new Numerazione();
            nuova.setTipoDocumento(tipoStr);
            nuova.setAnno(anno);
            nuova.setUltimoNumero(ultimoNumero);
            numerazioneRepository.save(nuova);
        }
    }
}
