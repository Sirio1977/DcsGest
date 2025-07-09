package com.gestionale.dto;

import java.util.List;
import java.util.Map;

public class ImportFornitoriResponse {
    private boolean success;
    private int totalRecords;
    private int successfulRecords;
    private int failedRecords;
    private List<String> errors;
    private String message;
    
    // Nuovi campi per compatibilità
    private int totaleFornitoriElaborati;
    private int fornitoriImportati;
    private int fornitoriConErrori;
    private List<Map<String, Object>> fornitoriValidi;
    private List<String> duplicati;
    
    // Costruttori
    public ImportFornitoriResponse() {}
    
    public ImportFornitoriResponse(boolean success, int totalRecords, 
                                   int successfulRecords, int failedRecords,
                                   List<String> errors, String message) {
        this.success = success;
        this.totalRecords = totalRecords;
        this.successfulRecords = successfulRecords;
        this.failedRecords = failedRecords;
        this.errors = errors;
        this.message = message;
    }
    
    // Getters e Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public int getTotalRecords() {
        return totalRecords;
    }
    
    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }
    
    public int getSuccessfulRecords() {
        return successfulRecords;
    }
    
    public void setSuccessfulRecords(int successfulRecords) {
        this.successfulRecords = successfulRecords;
    }
    
    public int getFailedRecords() {
        return failedRecords;
    }
    
    public void setFailedRecords(int failedRecords) {
        this.failedRecords = failedRecords;
    }
    
    public List<String> getErrors() {
        return errors;
    }
    
    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    // Getter e Setter per i nuovi campi
    public int getTotaleFornitoriElaborati() {
        return totaleFornitoriElaborati;
    }

    public void setTotaleFornitoriElaborati(int totaleFornitoriElaborati) {
        this.totaleFornitoriElaborati = totaleFornitoriElaborati;
    }

    public int getFornitoriImportati() {
        return fornitoriImportati;
    }

    public void setFornitoriImportati(int fornitoriImportati) {
        this.fornitoriImportati = fornitoriImportati;
    }

    public int getFornitoriConErrori() {
        return fornitoriConErrori;
    }

    public void setFornitoriConErrori(int fornitoriConErrori) {
        this.fornitoriConErrori = fornitoriConErrori;
    }

    public List<Map<String, Object>> getFornitoriValidi() {
        return fornitoriValidi;
    }

    public void setFornitoriValidi(List<Map<String, Object>> fornitoriValidi) {
        this.fornitoriValidi = fornitoriValidi;
    }

    public List<String> getDuplicati() {
        return duplicati;
    }

    public void setDuplicati(List<String> duplicati) {
        this.duplicati = duplicati;
    }
}
