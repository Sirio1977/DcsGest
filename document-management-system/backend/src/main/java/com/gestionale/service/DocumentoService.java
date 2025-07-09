package com.gestionale.service;

import com.gestionale.entity.Documento;
import com.gestionale.repository.DocumentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DocumentoService {

    private final DocumentoRepository documentoRepository;

    @Autowired
    public DocumentoService(DocumentoRepository documentoRepository) {
        this.documentoRepository = documentoRepository;
    }

    public List<Documento> getAllDocumenti() {
        return documentoRepository.findAll();
    }

    public Optional<Documento> getDocumentoById(Long id) {
        return documentoRepository.findById(id);
    }

    public Documento createDocumento(Documento documento) {
        return documentoRepository.save(documento);
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
