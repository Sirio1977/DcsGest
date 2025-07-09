package com.gestionale.dto;

import java.time.LocalDateTime;

public class ClienteImportDto {
    
    private String ragioneSociale;
    private String partitaIva;
    private String categoria; // "azienda" o "privato" dal JSON
    private String indirizzo;
    private String email;
    private String telefono;
    private String pec;
    private LocalDateTime dataUltimoAggiornamento;
    
    // Campi mappati
    private String tipoCliente; // CLIENTE, FORNITORE, CLIENTE_FORNITORE
    private String codiceFiscale;
    private String citta;
    private String provincia;
    private String cap;
    private Boolean attivo = true;
    
    // Costruttori
    public ClienteImportDto() {}
    
    public ClienteImportDto(String ragioneSociale, String partitaIva, String categoria) {
        this.ragioneSociale = ragioneSociale;
        this.partitaIva = partitaIva;
        this.categoria = categoria;
        this.attivo = true;
    }
    
    // Getters e Setters
    public String getRagioneSociale() {
        return ragioneSociale;
    }
    
    public void setRagioneSociale(String ragioneSociale) {
        this.ragioneSociale = ragioneSociale;
    }
    
    public String getPartitaIva() {
        return partitaIva;
    }
    
    public void setPartitaIva(String partitaIva) {
        this.partitaIva = partitaIva;
    }
    
    public String getCategoria() {
        return categoria;
    }
    
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    
    public String getIndirizzo() {
        return indirizzo;
    }
    
    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getTelefono() {
        return telefono;
    }
    
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    
    public String getPec() {
        return pec;
    }
    
    public void setPec(String pec) {
        this.pec = pec;
    }
    
    public LocalDateTime getDataUltimoAggiornamento() {
        return dataUltimoAggiornamento;
    }
    
    public void setDataUltimoAggiornamento(LocalDateTime dataUltimoAggiornamento) {
        this.dataUltimoAggiornamento = dataUltimoAggiornamento;
    }
    
    public String getTipoCliente() {
        return tipoCliente;
    }
    
    public void setTipoCliente(String tipoCliente) {
        this.tipoCliente = tipoCliente;
    }
    
    public String getCodiceFiscale() {
        return codiceFiscale;
    }
    
    public void setCodiceFiscale(String codiceFiscale) {
        this.codiceFiscale = codiceFiscale;
    }
    
    public String getCitta() {
        return citta;
    }
    
    public void setCitta(String citta) {
        this.citta = citta;
    }
    
    public String getProvincia() {
        return provincia;
    }
    
    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }
    
    public String getCap() {
        return cap;
    }
    
    public void setCap(String cap) {
        this.cap = cap;
    }
    
    public Boolean getAttivo() {
        return attivo;
    }
    
    public void setAttivo(Boolean attivo) {
        this.attivo = attivo;
    }
    
    @Override
    public String toString() {
        return "ClienteImportDto{" +
                "ragioneSociale='" + ragioneSociale + '\'' +
                ", partitaIva='" + partitaIva + '\'' +
                ", categoria='" + categoria + '\'' +
                ", tipoCliente='" + tipoCliente + '\'' +
                ", citta='" + citta + '\'' +
                ", attivo=" + attivo +
                '}';
    }
}
