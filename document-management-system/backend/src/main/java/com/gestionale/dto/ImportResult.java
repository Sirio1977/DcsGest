package com.gestionale.dto;

import java.util.List;

public class ImportResult {
    
    private int totaleRecord;
    private int importatiConSuccesso;
    private int errori;
    private List<String> messaggiErrore;
    private String messaggioRiepilogo;
    
    public ImportResult() {}
    
    public ImportResult(int totaleRecord, int importatiConSuccesso, int errori) {
        this.totaleRecord = totaleRecord;
        this.importatiConSuccesso = importatiConSuccesso;
        this.errori = errori;
        this.messaggioRiepilogo = String.format(
            "Importazione completata: %d record elaborati, %d importati con successo, %d errori", 
            totaleRecord, importatiConSuccesso, errori
        );
    }
    
    // Getters e Setters
    public int getTotaleRecord() {
        return totaleRecord;
    }
    
    public void setTotaleRecord(int totaleRecord) {
        this.totaleRecord = totaleRecord;
    }
    
    public int getImportatiConSuccesso() {
        return importatiConSuccesso;
    }
    
    public void setImportatiConSuccesso(int importatiConSuccesso) {
        this.importatiConSuccesso = importatiConSuccesso;
    }
    
    public int getErrori() {
        return errori;
    }
    
    public void setErrori(int errori) {
        this.errori = errori;
    }
    
    public List<String> getMessaggiErrore() {
        return messaggiErrore;
    }
    
    public void setMessaggiErrore(List<String> messaggiErrore) {
        this.messaggiErrore = messaggiErrore;
    }
    
    public String getMessaggioRiepilogo() {
        return messaggioRiepilogo;
    }
    
    public void setMessaggioRiepilogo(String messaggioRiepilogo) {
        this.messaggioRiepilogo = messaggioRiepilogo;
    }
    
    @Override
    public String toString() {
        return "ImportResult{" +
                "totaleRecord=" + totaleRecord +
                ", importatiConSuccesso=" + importatiConSuccesso +
                ", errori=" + errori +
                ", messaggioRiepilogo='" + messaggioRiepilogo + '\'' +
                '}';
    }
}
