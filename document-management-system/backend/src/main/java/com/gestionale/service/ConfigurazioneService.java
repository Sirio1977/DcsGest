package com.gestionale.service;

import com.gestionale.entity.Configurazione;
import com.gestionale.repository.ConfigurazioneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConfigurazioneService {
    
    @Autowired
    private ConfigurazioneRepository configurazioneRepository;
    
    public List<Configurazione> getAllConfigurazioni() {
        return configurazioneRepository.findAll();
    }
    
    public Optional<Configurazione> getConfigurazioneById(Long id) {
        return configurazioneRepository.findById(id);
    }
    
    public Optional<Configurazione> getConfigurazioneByChiave(String chiave) {
        return configurazioneRepository.findByChiave(chiave);
    }
    
    public String getValoreByChiave(String chiave, String defaultValue) {
        return configurazioneRepository.findByChiave(chiave)
                .map(Configurazione::getValore)
                .orElse(defaultValue);
    }
    
    public Configurazione saveConfigurazione(Configurazione configurazione) {
        return configurazioneRepository.save(configurazione);
    }
    
    public Configurazione createConfigurazione(Configurazione configurazione) {
        return configurazioneRepository.save(configurazione);
    }
    
    public Optional<Configurazione> updateConfigurazione(Long id, Configurazione configurazione) {
        return configurazioneRepository.findById(id).map(existing -> {
            existing.setChiave(configurazione.getChiave());
            existing.setValore(configurazione.getValore());
            existing.setDescrizione(configurazione.getDescrizione());
            existing.setTipo(configurazione.getTipo());
            return configurazioneRepository.save(existing);
        });
    }
    
    public Configurazione saveOrUpdateByChiave(String chiave, String valore, String descrizione) {
        Optional<Configurazione> esistente = configurazioneRepository.findByChiave(chiave);
        
        if (esistente.isPresent()) {
            Configurazione config = esistente.get();
            config.setValore(valore);
            if (descrizione != null) {
                config.setDescrizione(descrizione);
            }
            return configurazioneRepository.save(config);
        } else {
            return configurazioneRepository.save(new Configurazione(chiave, valore, descrizione));
        }
    }
    
    public boolean deleteConfigurazione(Long id) {
        if (configurazioneRepository.existsById(id)) {
            configurazioneRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    // Metodi di utilità per configurazioni specifiche
    public void initDefaultConfigurations() {
        // IVA Aliquote
        saveOrUpdateByChiave("IVA_STANDARD", "22", "Aliquota IVA standard");
        saveOrUpdateByChiave("IVA_RIDOTTA", "10", "Aliquota IVA ridotta");
        saveOrUpdateByChiave("IVA_MINIMA", "4", "Aliquota IVA minima");
        saveOrUpdateByChiave("IVA_ESENTE", "0", "IVA esente");
        
        // Numerazioni
        saveOrUpdateByChiave("NUM_FATTURA", "1", "Prossimo numero fattura");
        saveOrUpdateByChiave("NUM_DDT", "1", "Prossimo numero DDT");
        saveOrUpdateByChiave("NUM_NC", "1", "Prossimo numero Nota Credito");
        saveOrUpdateByChiave("NUM_ND", "1", "Prossimo numero Nota Debito");
        
        // Modalità Pagamento
        saveOrUpdateByChiave("PAG_CONTANTI", "Contanti", "Pagamento in contanti");
        saveOrUpdateByChiave("PAG_BONIFICO", "Bonifico Bancario", "Pagamento con bonifico");
        saveOrUpdateByChiave("PAG_CARTA", "Carta di Credito", "Pagamento con carta");
        saveOrUpdateByChiave("PAG_ASSEGNO", "Assegno", "Pagamento con assegno");
    }
}
