package com.gestionale.exception;

/**
 * Eccezione lanciata quando un documento non viene trovato
 */
public class DocumentoNotFoundException extends RuntimeException {
    
    public DocumentoNotFoundException(Long id) {
        super("Documento non trovato con ID: " + id);
    }
    
    public DocumentoNotFoundException(String message) {
        super(message);
    }
}
