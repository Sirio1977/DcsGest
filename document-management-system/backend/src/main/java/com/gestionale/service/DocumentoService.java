package com.gestionale.service;

import com.gestionale.dto.*;
import com.gestionale.entity.*;
import com.gestionale.exception.DocumentoNotFoundException;
import com.gestionale.mapper.DocumentoMapper;
import com.gestionale.repository.DocumentoRepository;
import com.gestionale.repository.SoggettoRepository;
import com.gestionale.repository.ArticoloRepository;
import com.gestionale.service.NumerazioneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DocumentoService {

    private final DocumentoRepository documentoRepository;
    private final SoggettoRepository soggettoRepository;
    private final ArticoloRepository articoloRepository;
    private final DocumentoMapper documentoMapper;
    private final NumerazioneService numerazioneService;

    @Autowired
    public DocumentoService(DocumentoRepository documentoRepository,
                           SoggettoRepository soggettoRepository,
                           ArticoloRepository articoloRepository,
                           DocumentoMapper documentoMapper,
                           NumerazioneService numerazioneService) {
        this.documentoRepository = documentoRepository;
        this.soggettoRepository = soggettoRepository;
        this.articoloRepository = articoloRepository;
        this.documentoMapper = documentoMapper;
        this.numerazioneService = numerazioneService;
    }

    /**
     * Crea un nuovo documento
     */
    public DocumentoResponseDto creaDocumento(DocumentoCreateDto createDto) {
        Documento documento = documentoMapper.toEntity(createDto);
        
        // Assegna numero documento
        Long numeroDocumento = numerazioneService.getNextNumber(
            createDto.getTipoDocumento(), 
            createDto.getDataDocumento().getYear()
        );
        documento.setNumero(numeroDocumento);
        documento.setAnno(createDto.getDataDocumento().getYear());
        
        // Imposta stato iniziale
        documento.setStato(StatoDocumento.BOZZA);
        
        // Salva il documento
        documento = documentoRepository.save(documento);
        
        return documentoMapper.toResponseDto(documento);
    }

    /**
     * Recupera un documento per ID
     */
    public DocumentoResponseDto getDocumento(Long id) {
        Documento documento = documentoRepository.findById(id)
            .orElseThrow(() -> new DocumentoNotFoundException("Documento non trovato: " + id));
        return documentoMapper.toResponseDto(documento);
    }

    /**
     * Recupera documenti con filtri e paginazione
     */
    public Page<DocumentoResponseDto> getDocumenti(DocumentoFilter filter, Pageable pageable) {
        Specification<Documento> spec = buildSpecification(filter);
        Page<Documento> documenti = documentoRepository.findAll(spec, pageable);
        return documenti.map(documentoMapper::toResponseDto);
    }

    /**
     * Aggiorna un documento esistente
     */
    public DocumentoResponseDto aggiornaDocumento(Long id, DocumentoCreateDto updateDto) {
        Documento documento = documentoRepository.findById(id)
            .orElseThrow(() -> new DocumentoNotFoundException("Documento non trovato: " + id));
        
        // Verifica che il documento sia modificabile
        if (!documento.isModificabile()) {
            throw new IllegalStateException("Documento non modificabile nello stato: " + documento.getStato());
        }
        
        // Aggiorna i campi
        documentoMapper.updateEntityFromDto(updateDto, documento);
        documento = documentoRepository.save(documento);
        
        return documentoMapper.toResponseDto(documento);
    }

    /**
     * Elimina un documento
     */
    public void eliminaDocumento(Long id) {
        Documento documento = documentoRepository.findById(id)
            .orElseThrow(() -> new DocumentoNotFoundException("Documento non trovato: " + id));
        
        if (!documento.isModificabile()) {
            throw new IllegalStateException("Documento non eliminabile nello stato: " + documento.getStato());
        }
        
        documentoRepository.delete(documento);
    }

    /**
     * Cambia lo stato di un documento
     */
    public DocumentoResponseDto cambiaStato(Long id, StatoDocumento nuovoStato) {
        Documento documento = documentoRepository.findById(id)
            .orElseThrow(() -> new DocumentoNotFoundException("Documento non trovato: " + id));
        
        documento.setStato(nuovoStato);
        documento = documentoRepository.save(documento);
        
        return documentoMapper.toResponseDto(documento);
    }

    /**
     * Duplica un documento
     */
    public DocumentoResponseDto duplicaDocumento(Long id, TipoDocumento nuovoTipo) {
        Documento originale = documentoRepository.findById(id)
            .orElseThrow(() -> new DocumentoNotFoundException("Documento non trovato: " + id));
        
        // Crea nuovo documento
        Documento duplicato = new Documento();
        duplicato.setTipoDocumento(nuovoTipo);
        duplicato.setDataDocumento(LocalDate.now());
        duplicato.setAnno(LocalDate.now().getYear());
        duplicato.setSoggetto(originale.getSoggetto());
        duplicato.setStato(StatoDocumento.BOZZA);
        
        // Assegna nuovo numero
        Long numeroDocumento = numerazioneService.getNextNumber(
            nuovoTipo, 
            LocalDate.now().getYear()
        );
        duplicato.setNumero(numeroDocumento);
        
        duplicato = documentoRepository.save(duplicato);
        
        return documentoMapper.toResponseDto(duplicato);
    }

    /**
     * Genera PDF del documento (stub implementation)
     */
    public byte[] generaPdf(Long id) {
        // TODO: Implementare generazione PDF
        return "PDF placeholder".getBytes();
    }

    /**
     * Invia documento via email (stub implementation)
     */
    public DocumentoResponseDto inviaDocumento(Long id, String emailDestinatario) {
        Documento documento = documentoRepository.findById(id)
            .orElseThrow(() -> new DocumentoNotFoundException("Documento non trovato: " + id));
        
        // TODO: Implementare invio email
        documento.setInviato(true);
        documento.setDataInvio(LocalDateTime.now());
        documento = documentoRepository.save(documento);
        
        return documentoMapper.toResponseDto(documento);
    }

    /**
     * Costruisce la specifica per il filtro
     */
    private Specification<Documento> buildSpecification(DocumentoFilter filter) {
        return (root, query, criteriaBuilder) -> {
            var predicates = new java.util.ArrayList<javax.persistence.criteria.Predicate>();
            
            if (filter.getTipoDocumento() != null) {
                predicates.add(criteriaBuilder.equal(root.get("tipoDocumento"), filter.getTipoDocumento()));
            }
            
            if (filter.getStatoDocumento() != null) {
                predicates.add(criteriaBuilder.equal(root.get("stato"), filter.getStatoDocumento()));
            }
            
            if (filter.getDataInizio() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("dataDocumento"), filter.getDataInizio()));
            }
            
            if (filter.getDataFine() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("dataDocumento"), filter.getDataFine()));
            }
            
            return criteriaBuilder.and(predicates.toArray(new javax.persistence.criteria.Predicate[0]));
        };
    }

    // Metodi legacy per compatibilità
    public List<Documento> getAllDocumenti() {
        return documentoRepository.findAll();
    }

    public Optional<Documento> getDocumentoById(Long id) {
        return documentoRepository.findById(id);
    }

    public Optional<Documento> updateDocumento(Long id, Documento documento) {
        return documentoRepository.findById(id).map(existingDocumento -> {
            existingDocumento.setTitolo(documento.getTitolo());
            existingDocumento.setTipoDocumento(documento.getTipoDocumento());
            return documentoRepository.save(existingDocumento);
        });
    }

    public boolean deleteDocumento(Long id) {
        if (documentoRepository.existsById(id)) {
            documentoRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
