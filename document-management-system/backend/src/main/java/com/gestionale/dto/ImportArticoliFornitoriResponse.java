package com.gestionale.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ImportArticoliFornitoriResponse {
    
    private int articoliImportati;
    private int articoliScartati;
    private int articoliDuplicati;
    private int articoliAggiornati;
    private List<String> errori;
    private List<ArticoloFornitoreDto> articoliValidi;
    private List<ArticoloFornitoreDto> articoliInvalidi;
    
    public ImportArticoliFornitoriResponse() {
        this.errori = new ArrayList<>();
        this.articoliValidi = new ArrayList<>();
        this.articoliInvalidi = new ArrayList<>();
    }
    
    // Inner class per rappresentare un articolo fornitore
    public static class ArticoloFornitoreDto {
        private String codice;
        private String descrizione;
        private Double quantita;
        private Double prezzoUnitario;
        private Double importo;
        private String unitaMisura;
        private Integer aliquotaIVA;
        private FornitoreInfoDto fornitore;
        private LocalDate dataDocumento;
        private LocalDate dataUltimoAggiornamento;
        private String codiceInterno;
        private List<PrezzoStoricoDto> prezziStorico;
        private String errore;
        
        public ArticoloFornitoreDto() {
            this.prezziStorico = new ArrayList<>();
        }
        
        // Getters e Setters
        public String getCodice() { return codice; }
        public void setCodice(String codice) { this.codice = codice; }
        
        public String getDescrizione() { return descrizione; }
        public void setDescrizione(String descrizione) { this.descrizione = descrizione; }
        
        public Double getQuantita() { return quantita; }
        public void setQuantita(Double quantita) { this.quantita = quantita; }
        
        public Double getPrezzoUnitario() { return prezzoUnitario; }
        public void setPrezzoUnitario(Double prezzoUnitario) { this.prezzoUnitario = prezzoUnitario; }
        
        public Double getImporto() { return importo; }
        public void setImporto(Double importo) { this.importo = importo; }
        
        public String getUnitaMisura() { return unitaMisura; }
        public void setUnitaMisura(String unitaMisura) { this.unitaMisura = unitaMisura; }
        
        public Integer getAliquotaIVA() { return aliquotaIVA; }
        public void setAliquotaIVA(Integer aliquotaIVA) { this.aliquotaIVA = aliquotaIVA; }
        
        public FornitoreInfoDto getFornitore() { return fornitore; }
        public void setFornitore(FornitoreInfoDto fornitore) { this.fornitore = fornitore; }
        
        public LocalDate getDataDocumento() { return dataDocumento; }
        public void setDataDocumento(LocalDate dataDocumento) { this.dataDocumento = dataDocumento; }
        
        public LocalDate getDataUltimoAggiornamento() { return dataUltimoAggiornamento; }
        public void setDataUltimoAggiornamento(LocalDate dataUltimoAggiornamento) { this.dataUltimoAggiornamento = dataUltimoAggiornamento; }
        
        public String getCodiceInterno() { return codiceInterno; }
        public void setCodiceInterno(String codiceInterno) { this.codiceInterno = codiceInterno; }
        
        public List<PrezzoStoricoDto> getPrezziStorico() { return prezziStorico; }
        public void setPrezziStorico(List<PrezzoStoricoDto> prezziStorico) { this.prezziStorico = prezziStorico; }
        
        public String getErrore() { return errore; }
        public void setErrore(String errore) { this.errore = errore; }
    }
    
    // Inner class per info fornitore
    public static class FornitoreInfoDto {
        private String partitaIva;
        private String ragioneSociale;
        private String categoria;
        
        // Getters e Setters
        public String getPartitaIva() { return partitaIva; }
        public void setPartitaIva(String partitaIva) { this.partitaIva = partitaIva; }
        
        public String getRagioneSociale() { return ragioneSociale; }
        public void setRagioneSociale(String ragioneSociale) { this.ragioneSociale = ragioneSociale; }
        
        public String getCategoria() { return categoria; }
        public void setCategoria(String categoria) { this.categoria = categoria; }
    }
    
    // Inner class per prezzo storico
    public static class PrezzoStoricoDto {
        private Double prezzo;
        private LocalDate data;
        private String fornitore;
        private Double quantita;
        
        // Getters e Setters
        public Double getPrezzo() { return prezzo; }
        public void setPrezzo(Double prezzo) { this.prezzo = prezzo; }
        
        public LocalDate getData() { return data; }
        public void setData(LocalDate data) { this.data = data; }
        
        public String getFornitore() { return fornitore; }
        public void setFornitore(String fornitore) { this.fornitore = fornitore; }
        
        public Double getQuantita() { return quantita; }
        public void setQuantita(Double quantita) { this.quantita = quantita; }
    }
    
    // Getters e Setters principali
    public int getArticoliImportati() { return articoliImportati; }
    public void setArticoliImportati(int articoliImportati) { this.articoliImportati = articoliImportati; }
    
    public int getArticoliScartati() { return articoliScartati; }
    public void setArticoliScartati(int articoliScartati) { this.articoliScartati = articoliScartati; }
    
    public int getArticoliDuplicati() { return articoliDuplicati; }
    public void setArticoliDuplicati(int articoliDuplicati) { this.articoliDuplicati = articoliDuplicati; }
    
    public int getArticoliAggiornati() { return articoliAggiornati; }
    public void setArticoliAggiornati(int articoliAggiornati) { this.articoliAggiornati = articoliAggiornati; }
    
    public List<String> getErrori() { return errori; }
    public void setErrori(List<String> errori) { this.errori = errori; }
    
    public List<ArticoloFornitoreDto> getArticoliValidi() { return articoliValidi; }
    public void setArticoliValidi(List<ArticoloFornitoreDto> articoliValidi) { this.articoliValidi = articoliValidi; }
    
    public List<ArticoloFornitoreDto> getArticoliInvalidi() { return articoliInvalidi; }
    public void setArticoliInvalidi(List<ArticoloFornitoreDto> articoliInvalidi) { this.articoliInvalidi = articoliInvalidi; }
    
    public void incrementaImportati() { this.articoliImportati++; }
    public void incrementaScartati() { this.articoliScartati++; }
    public void incrementaDuplicati() { this.articoliDuplicati++; }
    public void incrementaAggiornati() { this.articoliAggiornati++; }
    
    public void aggiungiErrore(String errore) { this.errori.add(errore); }
    public void aggiungiArticoloValido(ArticoloFornitoreDto articolo) { this.articoliValidi.add(articolo); }
    public void aggiungiArticoloInvalido(ArticoloFornitoreDto articolo) { this.articoliInvalidi.add(articolo); }
}
