package com.gestionale.dto;

import java.util.List;

public class ImportArticoliResponse {
    private boolean success;
    private int totalRecords;
    private int successfulRecords;
    private int failedRecords;
    private List<String> errors;
    private String message;
    
    // Costruttori
    public ImportArticoliResponse() {}
    
    public ImportArticoliResponse(boolean success, int totalRecords, 
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
}
