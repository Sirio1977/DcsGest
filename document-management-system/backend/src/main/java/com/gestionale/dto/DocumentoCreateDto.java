package com.gestionale.dto;

import com.gestionale.entity.TipoDocumento;
import javax.validation.Valid;
import javax.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO per la creazione di un nuovo documento
 */
@Data
public class DocumentoCreateDto {

    @NotNull(message = "Il tipo documento è obbligatorio")
    private TipoDocumento tipoDocumento;

    @NotNull(message = "Il cliente è obbligatorio")
    private Long clienteId;

    @NotBlank(message = "Il titolo è obbligatorio")
    @Size(max = 255, message = "Il titolo non può superare i 255 caratteri")
    private String titolo;

    @Size(max = 1000, message = "La descrizione non può superare i 1000 caratteri")
    private String descrizione;

    @NotNull(message = "La data documento è obbligatoria")
    private LocalDate dataDocumento;

    private LocalDate dataScadenza;

    @Size(max = 50, message = "Il numero documento non può superare i 50 caratteri")
    private String numeroDocumento;

    @Size(max = 255, message = "Il riferimento non può superare i 255 caratteri")
    private String riferimento;

    @Valid
    @NotEmpty(message = "Il documento deve avere almeno una riga")
    private List<RigaDocumentoDto> righe;

    @Valid
    private List<ScadenzaDto> scadenze;

    @Size(max = 1000, message = "Le note non possono superare i 1000 caratteri")
    private String note;

    // Campi per fatture
    private String partitaIva;
    private String codiceFiscale;
    private String codiceDestinatario;
    private String pecDestinatario;
    
    // Campi per DDT
    private String causaleDescrizione;
    private String tipoConsegna;
    private String aspettoBeni;
    private String vettore;
    private String numeroColli;
    private BigDecimal pesoLordo;
    private BigDecimal pesoNetto;
    
    // Campi per preventivi
    private LocalDate validitaOfferta;
    private String condizioniPagamento;
    private String terminiConsegna;
    
    // Campi per note di credito/debito
    private Long documentoOrigineId;
    private String motivoNota;

    // Costruttori
    public DocumentoCreateDto() {
    }

    // Getter e Setter
    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public LocalDate getDataDocumento() {
        return dataDocumento;
    }

    public void setDataDocumento(LocalDate dataDocumento) {
        this.dataDocumento = dataDocumento;
    }

    public LocalDate getDataScadenza() {
        return dataScadenza;
    }

    public void setDataScadenza(LocalDate dataScadenza) {
        this.dataScadenza = dataScadenza;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getRiferimento() {
        return riferimento;
    }

    public void setRiferimento(String riferimento) {
        this.riferimento = riferimento;
    }

    public List<RigaDocumentoDto> getRighe() {
        return righe;
    }

    public void setRighe(List<RigaDocumentoDto> righe) {
        this.righe = righe;
    }

    public List<ScadenzaDto> getScadenze() {
        return scadenze;
    }

    public void setScadenze(List<ScadenzaDto> scadenze) {
        this.scadenze = scadenze;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPartitaIva() {
        return partitaIva;
    }

    public void setPartitaIva(String partitaIva) {
        this.partitaIva = partitaIva;
    }

    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    public void setCodiceFiscale(String codiceFiscale) {
        this.codiceFiscale = codiceFiscale;
    }

    public String getCodiceDestinatario() {
        return codiceDestinatario;
    }

    public void setCodiceDestinatario(String codiceDestinatario) {
        this.codiceDestinatario = codiceDestinatario;
    }

    public String getPecDestinatario() {
        return pecDestinatario;
    }

    public void setPecDestinatario(String pecDestinatario) {
        this.pecDestinatario = pecDestinatario;
    }

    public String getCausaleDescrizione() {
        return causaleDescrizione;
    }

    public void setCausaleDescrizione(String causaleDescrizione) {
        this.causaleDescrizione = causaleDescrizione;
    }

    public String getTipoConsegna() {
        return tipoConsegna;
    }

    public void setTipoConsegna(String tipoConsegna) {
        this.tipoConsegna = tipoConsegna;
    }

    public String getAspettoBeni() {
        return aspettoBeni;
    }

    public void setAspettoBeni(String aspettoBeni) {
        this.aspettoBeni = aspettoBeni;
    }

    public String getVettore() {
        return vettore;
    }

    public void setVettore(String vettore) {
        this.vettore = vettore;
    }

    public String getNumeroColli() {
        return numeroColli;
    }

    public void setNumeroColli(String numeroColli) {
        this.numeroColli = numeroColli;
    }

    public BigDecimal getPesoLordo() {
        return pesoLordo;
    }

    public void setPesoLordo(BigDecimal pesoLordo) {
        this.pesoLordo = pesoLordo;
    }

    public BigDecimal getPesoNetto() {
        return pesoNetto;
    }

    public void setPesoNetto(BigDecimal pesoNetto) {
        this.pesoNetto = pesoNetto;
    }

    public LocalDate getValiditaOfferta() {
        return validitaOfferta;
    }

    public void setValiditaOfferta(LocalDate validitaOfferta) {
        this.validitaOfferta = validitaOfferta;
    }

    public String getCondizioniPagamento() {
        return condizioniPagamento;
    }

    public void setCondizioniPagamento(String condizioniPagamento) {
        this.condizioniPagamento = condizioniPagamento;
    }

    public String getTerminiConsegna() {
        return terminiConsegna;
    }

    public void setTerminiConsegna(String terminiConsegna) {
        this.terminiConsegna = terminiConsegna;
    }

    public Long getDocumentoOrigineId() {
        return documentoOrigineId;
    }

    public void setDocumentoOrigineId(Long documentoOrigineId) {
        this.documentoOrigineId = documentoOrigineId;
    }

    public String getMotivoNota() {
        return motivoNota;
    }

    public void setMotivoNota(String motivoNota) {
        this.motivoNota = motivoNota;
    }
}
