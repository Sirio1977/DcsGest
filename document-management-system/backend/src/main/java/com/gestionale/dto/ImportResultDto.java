package com.gestionale.dto;

import java.util.List;
import java.util.Map;

public class ImportResultDto {
    private boolean success;
    private String message;
    private int totalRecords;
    private int successfulRecords;
    private int failedRecords;
    private List<String> errors;
    private List<Map<String, Object>> previewData;
    private Map<String, Object> statistics;

    // Costruttori
    public ImportResultDto() {}

    public ImportResultDto(boolean success, String message, int totalRecords, int successfulRecords, List<String> errors) {
        this.success = success;
        this.message = message;
        this.totalRecords = totalRecords;
        this.successfulRecords = successfulRecords;
        this.failedRecords = totalRecords - successfulRecords;
        this.errors = errors;
    }

    public ImportResultDto(boolean success, String message, int totalRecords, int successfulRecords, 
                          List<String> errors, List<Map<String, Object>> previewData) {
        this(success, message, totalRecords, successfulRecords, errors);
        this.previewData = previewData;
    }

    // Getters e Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public List<Map<String, Object>> getPreviewData() {
        return previewData;
    }

    public void setPreviewData(List<Map<String, Object>> previewData) {
        this.previewData = previewData;
    }

    public Map<String, Object> getStatistics() {
        return statistics;
    }

    public void setStatistics(Map<String, Object> statistics) {
        this.statistics = statistics;
    }

    // Metodi utility
    public void addError(String error) {
        if (this.errors == null) {
            this.errors = new java.util.ArrayList<>();
        }
        this.errors.add(error);
    }

    public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }

    public double getSuccessRate() {
        if (totalRecords == 0) return 0.0;
        return (double) successfulRecords / totalRecords * 100.0;
    }
}
