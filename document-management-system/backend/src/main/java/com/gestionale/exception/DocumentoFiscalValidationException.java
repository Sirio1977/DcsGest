package com.gestionale.exception;

/**
 * Eccezione lanciata quando i dati fiscali di un documento non sono validi
 */
public class DocumentoFiscalValidationException extends RuntimeException {
    
    public DocumentoFiscalValidationException(String message) {
        super(message);
    }
    
    public DocumentoFiscalValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
