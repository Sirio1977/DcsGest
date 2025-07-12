package com.gestionale.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO per la risposta contenente i dati completi di un documento
 */
public class DocumentoResponseDto {

    private Long id;
    
    private String tipoDocumento;
    
    private Long numero;
    
    private Integer anno;
    
    private LocalDate dataDocumento;
    
    private LocalDate dataRegistrazione;
    
    private String statoDocumento;
    
    // Dati del soggetto
    private Long soggettoId;
    private String soggettoRagioneSociale;
    private String soggettoCodiceFiscale;
    private String soggettoPartitaIva;
    private String soggettoIndirizzo;
    private String soggettoCap;
    private String soggettoCitta;
    private String soggettoProvincia;
    private String soggettoNazione;
    
    // Righe del documento
    private List<RigaDocumentoDto> righe;
    
    // Scadenze di pagamento
    private List<ScadenzaDto> scadenze;
    
    // Riepiloghi IVA
    private List<RiepilogoIvaDto> riepiloghiIva;
    
    // Totali documento
    private BigDecimal totaleImponibile;
    private BigDecimal totaleImposta;
    private BigDecimal totaleDocumento;
    
    // Modalità di pagamento
    private String modalitaPagamento;
    
    // Note
    private String note;
    private String noteInterne;
    
    // Causale trasporto (per DDT)
    private String causaleTrasporto;
    
    // Riferimenti esterni
    private String riferimentoOrdine;
    private LocalDate dataOrdine;
    
    // Informazioni aggiuntive
    private Long agenteId;
    private String agenteNome;
    private Long listinoId;
    private String listinoNome;
    
    // Tracciamento
    private LocalDateTime dataCreazione;
    private LocalDateTime dataModifica;
    private String creatoUtenteId;
    private String modificatoUtenteId;

    // Default constructor
    public DocumentoResponseDto() {}

    // Constructor with all fields
    public DocumentoResponseDto(Long id, String tipoDocumento, Long numero, Integer anno, 
                               LocalDate dataDocumento, LocalDate dataRegistrazione, String statoDocumento,
                               Long soggettoId, String soggettoRagioneSociale, String soggettoCodiceFiscale,
                               String soggettoPartitaIva, String soggettoIndirizzo, String soggettoCap,
                               String soggettoCitta, String soggettoProvincia, String soggettoNazione,
                               List<RigaDocumentoDto> righe, List<ScadenzaDto> scadenze,
                               List<RiepilogoIvaDto> riepiloghiIva, BigDecimal totaleImponibile,
                               BigDecimal totaleImposta, BigDecimal totaleDocumento, String modalitaPagamento,
                               String note, String noteInterne, String causaleTrasporto,
                               String riferimentoOrdine, LocalDate dataOrdine, Long agenteId,
                               String agenteNome, Long listinoId, String listinoNome,
                               LocalDateTime dataCreazione, LocalDateTime dataModifica,
                               String creatoUtenteId, String modificatoUtenteId) {
        this.id = id;
        this.tipoDocumento = tipoDocumento;
        this.numero = numero;
        this.anno = anno;
        this.dataDocumento = dataDocumento;
        this.dataRegistrazione = dataRegistrazione;
        this.statoDocumento = statoDocumento;
        this.soggettoId = soggettoId;
        this.soggettoRagioneSociale = soggettoRagioneSociale;
        this.soggettoCodiceFiscale = soggettoCodiceFiscale;
        this.soggettoPartitaIva = soggettoPartitaIva;
        this.soggettoIndirizzo = soggettoIndirizzo;
        this.soggettoCap = soggettoCap;
        this.soggettoCitta = soggettoCitta;
        this.soggettoProvincia = soggettoProvincia;
        this.soggettoNazione = soggettoNazione;
        this.righe = righe;
        this.scadenze = scadenze;
        this.riepiloghiIva = riepiloghiIva;
        this.totaleImponibile = totaleImponibile;
        this.totaleImposta = totaleImposta;
        this.totaleDocumento = totaleDocumento;
        this.modalitaPagamento = modalitaPagamento;
        this.note = note;
        this.noteInterne = noteInterne;
        this.causaleTrasporto = causaleTrasporto;
        this.riferimentoOrdine = riferimentoOrdine;
        this.dataOrdine = dataOrdine;
        this.agenteId = agenteId;
        this.agenteNome = agenteNome;
        this.listinoId = listinoId;
        this.listinoNome = listinoNome;
        this.dataCreazione = dataCreazione;
        this.dataModifica = dataModifica;
        this.creatoUtenteId = creatoUtenteId;
        this.modificatoUtenteId = modificatoUtenteId;
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String tipoDocumento;
        private Long numero;
        private Integer anno;
        private LocalDate dataDocumento;
        private LocalDate dataRegistrazione;
        private String statoDocumento;
        private Long soggettoId;
        private String soggettoRagioneSociale;
        private String soggettoCodiceFiscale;
        private String soggettoPartitaIva;
        private String soggettoIndirizzo;
        private String soggettoCap;
        private String soggettoCitta;
        private String soggettoProvincia;
        private String soggettoNazione;
        private List<RigaDocumentoDto> righe;
        private List<ScadenzaDto> scadenze;
        private List<RiepilogoIvaDto> riepiloghiIva;
        private BigDecimal totaleImponibile;
        private BigDecimal totaleImposta;
        private BigDecimal totaleDocumento;
        private String modalitaPagamento;
        private String note;
        private String noteInterne;
        private String causaleTrasporto;
        private String riferimentoOrdine;
        private LocalDate dataOrdine;
        private Long agenteId;
        private String agenteNome;
        private Long listinoId;
        private String listinoNome;
        private LocalDateTime dataCreazione;
        private LocalDateTime dataModifica;
        private String creatoUtenteId;
        private String modificatoUtenteId;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder tipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; return this; }
        public Builder numero(Long numero) { this.numero = numero; return this; }
        public Builder anno(Integer anno) { this.anno = anno; return this; }
        public Builder dataDocumento(LocalDate dataDocumento) { this.dataDocumento = dataDocumento; return this; }
        public Builder dataRegistrazione(LocalDate dataRegistrazione) { this.dataRegistrazione = dataRegistrazione; return this; }
        public Builder statoDocumento(String statoDocumento) { this.statoDocumento = statoDocumento; return this; }
        public Builder soggettoId(Long soggettoId) { this.soggettoId = soggettoId; return this; }
        public Builder soggettoRagioneSociale(String soggettoRagioneSociale) { this.soggettoRagioneSociale = soggettoRagioneSociale; return this; }
        public Builder soggettoCodiceFiscale(String soggettoCodiceFiscale) { this.soggettoCodiceFiscale = soggettoCodiceFiscale; return this; }
        public Builder soggettoPartitaIva(String soggettoPartitaIva) { this.soggettoPartitaIva = soggettoPartitaIva; return this; }
        public Builder soggettoIndirizzo(String soggettoIndirizzo) { this.soggettoIndirizzo = soggettoIndirizzo; return this; }
        public Builder soggettoCap(String soggettoCap) { this.soggettoCap = soggettoCap; return this; }
        public Builder soggettoCitta(String soggettoCitta) { this.soggettoCitta = soggettoCitta; return this; }
        public Builder soggettoProvincia(String soggettoProvincia) { this.soggettoProvincia = soggettoProvincia; return this; }
        public Builder soggettoNazione(String soggettoNazione) { this.soggettoNazione = soggettoNazione; return this; }
        public Builder righe(List<RigaDocumentoDto> righe) { this.righe = righe; return this; }
        public Builder scadenze(List<ScadenzaDto> scadenze) { this.scadenze = scadenze; return this; }
        public Builder riepiloghiIva(List<RiepilogoIvaDto> riepiloghiIva) { this.riepiloghiIva = riepiloghiIva; return this; }
        public Builder totaleImponibile(BigDecimal totaleImponibile) { this.totaleImponibile = totaleImponibile; return this; }
        public Builder totaleImposta(BigDecimal totaleImposta) { this.totaleImposta = totaleImposta; return this; }
        public Builder totaleDocumento(BigDecimal totaleDocumento) { this.totaleDocumento = totaleDocumento; return this; }
        public Builder modalitaPagamento(String modalitaPagamento) { this.modalitaPagamento = modalitaPagamento; return this; }
        public Builder note(String note) { this.note = note; return this; }
        public Builder noteInterne(String noteInterne) { this.noteInterne = noteInterne; return this; }
        public Builder causaleTrasporto(String causaleTrasporto) { this.causaleTrasporto = causaleTrasporto; return this; }
        public Builder riferimentoOrdine(String riferimentoOrdine) { this.riferimentoOrdine = riferimentoOrdine; return this; }
        public Builder dataOrdine(LocalDate dataOrdine) { this.dataOrdine = dataOrdine; return this; }
        public Builder agenteId(Long agenteId) { this.agenteId = agenteId; return this; }
        public Builder agenteNome(String agenteNome) { this.agenteNome = agenteNome; return this; }
        public Builder listinoId(Long listinoId) { this.listinoId = listinoId; return this; }
        public Builder listinoNome(String listinoNome) { this.listinoNome = listinoNome; return this; }
        public Builder dataCreazione(LocalDateTime dataCreazione) { this.dataCreazione = dataCreazione; return this; }
        public Builder dataModifica(LocalDateTime dataModifica) { this.dataModifica = dataModifica; return this; }
        public Builder creatoUtenteId(String creatoUtenteId) { this.creatoUtenteId = creatoUtenteId; return this; }
        public Builder modificatoUtenteId(String modificatoUtenteId) { this.modificatoUtenteId = modificatoUtenteId; return this; }

        public DocumentoResponseDto build() {
            return new DocumentoResponseDto(id, tipoDocumento, numero, anno, dataDocumento, dataRegistrazione, 
                                          statoDocumento, soggettoId, soggettoRagioneSociale, soggettoCodiceFiscale,
                                          soggettoPartitaIva, soggettoIndirizzo, soggettoCap, soggettoCitta,
                                          soggettoProvincia, soggettoNazione, righe, scadenze, riepiloghiIva,
                                          totaleImponibile, totaleImposta, totaleDocumento, modalitaPagamento,
                                          note, noteInterne, causaleTrasporto, riferimentoOrdine, dataOrdine,
                                          agenteId, agenteNome, listinoId, listinoNome, dataCreazione,
                                          dataModifica, creatoUtenteId, modificatoUtenteId);
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public Long getNumero() { return numero; }
    public void setNumero(Long numero) { this.numero = numero; }

    public Integer getAnno() { return anno; }
    public void setAnno(Integer anno) { this.anno = anno; }

    public LocalDate getDataDocumento() { return dataDocumento; }
    public void setDataDocumento(LocalDate dataDocumento) { this.dataDocumento = dataDocumento; }

    public LocalDate getDataRegistrazione() { return dataRegistrazione; }
    public void setDataRegistrazione(LocalDate dataRegistrazione) { this.dataRegistrazione = dataRegistrazione; }

    public String getStatoDocumento() { return statoDocumento; }
    public void setStatoDocumento(String statoDocumento) { this.statoDocumento = statoDocumento; }

    public Long getSoggettoId() { return soggettoId; }
    public void setSoggettoId(Long soggettoId) { this.soggettoId = soggettoId; }

    public String getSoggettoRagioneSociale() { return soggettoRagioneSociale; }
    public void setSoggettoRagioneSociale(String soggettoRagioneSociale) { this.soggettoRagioneSociale = soggettoRagioneSociale; }

    public String getSoggettoCodiceFiscale() { return soggettoCodiceFiscale; }
    public void setSoggettoCodiceFiscale(String soggettoCodiceFiscale) { this.soggettoCodiceFiscale = soggettoCodiceFiscale; }

    public String getSoggettoPartitaIva() { return soggettoPartitaIva; }
    public void setSoggettoPartitaIva(String soggettoPartitaIva) { this.soggettoPartitaIva = soggettoPartitaIva; }

    public String getSoggettoIndirizzo() { return soggettoIndirizzo; }
    public void setSoggettoIndirizzo(String soggettoIndirizzo) { this.soggettoIndirizzo = soggettoIndirizzo; }

    public String getSoggettoCap() { return soggettoCap; }
    public void setSoggettoCap(String soggettoCap) { this.soggettoCap = soggettoCap; }

    public String getSoggettoCitta() { return soggettoCitta; }
    public void setSoggettoCitta(String soggettoCitta) { this.soggettoCitta = soggettoCitta; }

    public String getSoggettoProvincia() { return soggettoProvincia; }
    public void setSoggettoProvincia(String soggettoProvincia) { this.soggettoProvincia = soggettoProvincia; }

    public String getSoggettoNazione() { return soggettoNazione; }
    public void setSoggettoNazione(String soggettoNazione) { this.soggettoNazione = soggettoNazione; }

    public List<RigaDocumentoDto> getRighe() { return righe; }
    public void setRighe(List<RigaDocumentoDto> righe) { this.righe = righe; }

    public List<ScadenzaDto> getScadenze() { return scadenze; }
    public void setScadenze(List<ScadenzaDto> scadenze) { this.scadenze = scadenze; }

    public List<RiepilogoIvaDto> getRiepiloghiIva() { return riepiloghiIva; }
    public void setRiepiloghiIva(List<RiepilogoIvaDto> riepiloghiIva) { this.riepiloghiIva = riepiloghiIva; }

    public BigDecimal getTotaleImponibile() { return totaleImponibile; }
    public void setTotaleImponibile(BigDecimal totaleImponibile) { this.totaleImponibile = totaleImponibile; }

    public BigDecimal getTotaleImposta() { return totaleImposta; }
    public void setTotaleImposta(BigDecimal totaleImposta) { this.totaleImposta = totaleImposta; }

    public BigDecimal getTotaleDocumento() { return totaleDocumento; }
    public void setTotaleDocumento(BigDecimal totaleDocumento) { this.totaleDocumento = totaleDocumento; }

    public String getModalitaPagamento() { return modalitaPagamento; }
    public void setModalitaPagamento(String modalitaPagamento) { this.modalitaPagamento = modalitaPagamento; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getNoteInterne() { return noteInterne; }
    public void setNoteInterne(String noteInterne) { this.noteInterne = noteInterne; }

    public String getCausaleTrasporto() { return causaleTrasporto; }
    public void setCausaleTrasporto(String causaleTrasporto) { this.causaleTrasporto = causaleTrasporto; }

    public String getRiferimentoOrdine() { return riferimentoOrdine; }
    public void setRiferimentoOrdine(String riferimentoOrdine) { this.riferimentoOrdine = riferimentoOrdine; }

    public LocalDate getDataOrdine() { return dataOrdine; }
    public void setDataOrdine(LocalDate dataOrdine) { this.dataOrdine = dataOrdine; }

    public Long getAgenteId() { return agenteId; }
    public void setAgenteId(Long agenteId) { this.agenteId = agenteId; }

    public String getAgenteNome() { return agenteNome; }
    public void setAgenteNome(String agenteNome) { this.agenteNome = agenteNome; }

    public Long getListinoId() { return listinoId; }
    public void setListinoId(Long listinoId) { this.listinoId = listinoId; }

    public String getListinoNome() { return listinoNome; }
    public void setListinoNome(String listinoNome) { this.listinoNome = listinoNome; }

    public LocalDateTime getDataCreazione() { return dataCreazione; }
    public void setDataCreazione(LocalDateTime dataCreazione) { this.dataCreazione = dataCreazione; }

    public LocalDateTime getDataModifica() { return dataModifica; }
    public void setDataModifica(LocalDateTime dataModifica) { this.dataModifica = dataModifica; }

    public String getCreatoUtenteId() { return creatoUtenteId; }
    public void setCreatoUtenteId(String creatoUtenteId) { this.creatoUtenteId = creatoUtenteId; }

    public String getModificatoUtenteId() { return modificatoUtenteId; }
    public void setModificatoUtenteId(String modificatoUtenteId) { this.modificatoUtenteId = modificatoUtenteId; }
}
