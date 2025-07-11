package com.gestionale.exception;

/**
 * Eccezione lanciata quando si tenta di modificare un documento in uno stato non valido
 */
public class DocumentoInvalidStateException extends RuntimeException {
    
    public DocumentoInvalidStateException(String message) {
        super(message);
    }
    
    public DocumentoInvalidStateException(String message, Throwable cause) {
        super(message, cause);
    }
}
