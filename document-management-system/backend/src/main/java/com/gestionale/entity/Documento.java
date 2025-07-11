package com.gestionale.entity;

import javax.persistence.*;
import javax.validation.constraints.*;
import com.gestionale.enums.AliquotaIva;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Entity per la gestione dei documenti commerciali
 * Supporta fatture, DDT, preventivi, note di credito/debito
 */
@Entity
@Table(name = "documenti")
@Data
@EqualsAndHashCode(exclude = {"righe", "riepiloghi", "scadenze"})
@ToString(exclude = {"righe", "riepiloghi", "scadenze"})
public class Documento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false)
    @NotNull(message = "Tipo documento obbligatorio")
    private TipoDocumento tipoDocumento;

    @Column(nullable = false)
    @NotNull(message = "Numero documento obbligatorio")
    @Min(value = 1, message = "Numero documento deve essere maggiore di 0")
    private Long numero;

    @Column(nullable = false)
    @NotNull(message = "Anno documento obbligatorio")
    @Min(value = 2000, message = "Anno non valido")
    @Max(value = 2099, message = "Anno non valido")
    private Integer anno;

    @Column(name = "data_documento", nullable = false)
    @NotNull(message = "Data documento obbligatoria")
    private LocalDate dataDocumento;

    @Column(name = "data_registrazione")
    private LocalDate dataRegistrazione;

    // ==================== SOGGETTO ====================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "soggetto_id", nullable = false)
    @NotNull(message = "Soggetto obbligatorio")
    private Soggetto soggetto;

    @Column(name = "ragione_sociale", nullable = false)
    @NotBlank(message = "Ragione sociale obbligatoria")
    @Size(max = 255, message = "Ragione sociale troppo lunga")
    private String ragioneSociale;

    @Column(name = "partita_iva", length = 11)
    @Size(max = 11, message = "Partita IVA non valida")
    private String partitaIva;

    @Column(name = "codice_fiscale", length = 16)
    @Size(max = 16, message = "Codice fiscale non valido")
    private String codiceFiscale;

    @Column(name = "indirizzo_completo", columnDefinition = "TEXT")
    private String indirizzoCompleto;

    @Column(name = "codice_destinatario", length = 7)
    @Size(max = 7, message = "Codice destinatario non valido")
    private String codiceDestinatario;

    @Column(length = 100)
    @Email(message = "PEC non valida")
    private String pec;

    // ==================== CAMPI AGGIUNTIVI ====================
    @Column(name = "titolo", length = 255)
    @Size(max = 255, message = "Titolo troppo lungo")
    private String titolo;

    // Campo per compatibilità con validation (riferimento al soggetto come cliente)
    @Transient
    public Soggetto getCliente() {
        return this.soggetto;
    }

    // Campo per compatibilità con validation 
    @Transient
    public String getPecDestinatario() {
        return this.pec;
    }

    @Column(name = "motivo_nota", length = 500)
    @Size(max = 500, message = "Motivo nota troppo lungo")
    private String motivoNota;

    @Column(name = "causale_descrizione", length = 255)
    @Size(max = 255, message = "Causale descrizione troppo lunga")
    private String causaleDescrizione;

    @Column(name = "validita_offerta")
    private LocalDate validitaOfferta;

    // ==================== DATI COMMERCIALI ====================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modalita_pagamento_id")
    private ModalitaPagamento modalitaPagamento;

    @Column(name = "agente_id")
    private Long agenteId;

    @Column(name = "listino_id")
    private Long listinoId;

    // ==================== TRASPORTO (per DDT) ====================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "causale_trasporto_id")
    private CausaleTrasporto causaleTrasporto;

    @Column(name = "data_trasporto")
    private LocalDate dataTrasporto;

    @Column(length = 255)
    private String trasportatore;

    @Column
    @Min(value = 0, message = "Numero colli non può essere negativo")
    private Integer colli = 0;

    @Column(precision = 10, scale = 3)
    @DecimalMin(value = "0.0", message = "Peso non può essere negativo")
    private BigDecimal peso = BigDecimal.ZERO;

    // ==================== TOTALI DOCUMENTO ====================
    @Column(name = "totale_imponibile", precision = 15, scale = 2)
    @DecimalMin(value = "0.0", message = "Totale imponibile non può essere negativo")
    private BigDecimal totaleImponibile = BigDecimal.ZERO;

    @Column(name = "totale_iva", precision = 15, scale = 2)
    @DecimalMin(value = "0.0", message = "Totale IVA non può essere negativo")
    private BigDecimal totaleIva = BigDecimal.ZERO;

    @Column(name = "totale_ritenuta", precision = 15, scale = 2)
    @DecimalMin(value = "0.0", message = "Totale ritenuta non può essere negativo")
    private BigDecimal totaleRitenuta = BigDecimal.ZERO;

    @Column(name = "totale_documento", precision = 15, scale = 2)
    @DecimalMin(value = "0.0", message = "Totale documento non può essere negativo")
    private BigDecimal totaleDocumento = BigDecimal.ZERO;

    // ==================== STATI DOCUMENTO ====================
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private StatoDocumento stato = StatoDocumento.BOZZA;

    @Column
    private Boolean stampato = false;

    @Column
    private Boolean inviato = false;

    @Column(name = "data_invio")
    private LocalDateTime dataInvio;

    // ==================== NOTE ====================
    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(name = "note_interne", columnDefinition = "TEXT")
    private String noteInterne;

    // ==================== RIFERIMENTI ====================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "documento_origine_id")
    private Documento documentoOrigine;

    @Column(name = "numero_ordine", length = 50)
    private String numeroOrdine;

    @Column(name = "data_ordine")
    private LocalDate dataOrdine;

    // ==================== RELAZIONI ====================
    @OneToMany(mappedBy = "documento", cascade = CascadeType.ALL, 
               orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("rigaNumero ASC")
    private List<RigaDocumento> righe = new ArrayList<>();

    @OneToMany(mappedBy = "documento", cascade = CascadeType.ALL, 
               orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RiepilogoIva> riepiloghi = new ArrayList<>();

    @OneToMany(mappedBy = "documento", cascade = CascadeType.ALL, 
               orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Scadenza> scadenze = new ArrayList<>();

    // ==================== AUDIT ====================
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    // ==================== GETTERS AND SETTERS ====================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public TipoDocumento getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(TipoDocumento tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public Long getNumero() { return numero; }
    public void setNumero(Long numero) { this.numero = numero; }

    public Integer getAnno() { return anno; }
    public void setAnno(Integer anno) { this.anno = anno; }

    public LocalDate getDataDocumento() { return dataDocumento; }
    public void setDataDocumento(LocalDate dataDocumento) { this.dataDocumento = dataDocumento; }

    public LocalDate getDataRegistrazione() { return dataRegistrazione; }
    public void setDataRegistrazione(LocalDate dataRegistrazione) { this.dataRegistrazione = dataRegistrazione; }

    public Soggetto getSoggetto() { return soggetto; }
    public void setSoggetto(Soggetto soggetto) { this.soggetto = soggetto; }

    public String getRagioneSociale() { return ragioneSociale; }
    public void setRagioneSociale(String ragioneSociale) { this.ragioneSociale = ragioneSociale; }

    public String getPartitaIva() { return partitaIva; }
    public void setPartitaIva(String partitaIva) { this.partitaIva = partitaIva; }

    public String getCodiceFiscale() { return codiceFiscale; }
    public void setCodiceFiscale(String codiceFiscale) { this.codiceFiscale = codiceFiscale; }

    public String getIndirizzoCompleto() { return indirizzoCompleto; }
    public void setIndirizzoCompleto(String indirizzoCompleto) { this.indirizzoCompleto = indirizzoCompleto; }

    public String getCodiceDestinatario() { return codiceDestinatario; }
    public void setCodiceDestinatario(String codiceDestinatario) { this.codiceDestinatario = codiceDestinatario; }

    public String getPec() { return pec; }
    public void setPec(String pec) { this.pec = pec; }

    public ModalitaPagamento getModalitaPagamento() { return modalitaPagamento; }
    public void setModalitaPagamento(ModalitaPagamento modalitaPagamento) { this.modalitaPagamento = modalitaPagamento; }

    public Long getAgenteId() { return agenteId; }
    public void setAgenteId(Long agenteId) { this.agenteId = agenteId; }

    public Long getListinoId() { return listinoId; }
    public void setListinoId(Long listinoId) { this.listinoId = listinoId; }

    public CausaleTrasporto getCausaleTrasporto() { return causaleTrasporto; }
    public void setCausaleTrasporto(CausaleTrasporto causaleTrasporto) { this.causaleTrasporto = causaleTrasporto; }

    public LocalDate getDataTrasporto() { return dataTrasporto; }
    public void setDataTrasporto(LocalDate dataTrasporto) { this.dataTrasporto = dataTrasporto; }

    public String getTrasportatore() { return trasportatore; }
    public void setTrasportatore(String trasportatore) { this.trasportatore = trasportatore; }

    public Integer getColli() { return colli; }
    public void setColli(Integer colli) { this.colli = colli; }

    public BigDecimal getPeso() { return peso; }
    public void setPeso(BigDecimal peso) { this.peso = peso; }

    public BigDecimal getTotaleImponibile() { return totaleImponibile; }
    public void setTotaleImponibile(BigDecimal totaleImponibile) { this.totaleImponibile = totaleImponibile; }

    public BigDecimal getTotaleIva() { return totaleIva; }
    public void setTotaleIva(BigDecimal totaleIva) { this.totaleIva = totaleIva; }

    public BigDecimal getTotaleRitenuta() { return totaleRitenuta; }
    public void setTotaleRitenuta(BigDecimal totaleRitenuta) { this.totaleRitenuta = totaleRitenuta; }

    public BigDecimal getTotaleDocumento() { return totaleDocumento; }
    public void setTotaleDocumento(BigDecimal totaleDocumento) { this.totaleDocumento = totaleDocumento; }

    public StatoDocumento getStato() { return stato; }
    public void setStato(StatoDocumento stato) { this.stato = stato; }

    // Compatibility method for some services
    public StatoDocumento getStatoDocumento() { return stato; }
    public void setStatoDocumento(StatoDocumento stato) { this.stato = stato; }

    public Boolean getStampato() { return stampato; }
    public void setStampato(Boolean stampato) { this.stampato = stampato; }

    public Boolean getInviato() { return inviato; }
    public void setInviato(Boolean inviato) { this.inviato = inviato; }

    public LocalDateTime getDataInvio() { return dataInvio; }
    public void setDataInvio(LocalDateTime dataInvio) { this.dataInvio = dataInvio; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getNoteInterne() { return noteInterne; }
    public void setNoteInterne(String noteInterne) { this.noteInterne = noteInterne; }

    public Documento getDocumentoOrigine() { return documentoOrigine; }
    public void setDocumentoOrigine(Documento documentoOrigine) { this.documentoOrigine = documentoOrigine; }

    public String getNumeroOrdine() { return numeroOrdine; }
    public void setNumeroOrdine(String numeroOrdine) { this.numeroOrdine = numeroOrdine; }

    public LocalDate getDataOrdine() { return dataOrdine; }
    public void setDataOrdine(LocalDate dataOrdine) { this.dataOrdine = dataOrdine; }

    public List<RigaDocumento> getRighe() { return righe; }
    public void setRighe(List<RigaDocumento> righe) { this.righe = righe; }

    public List<RiepilogoIva> getRiepiloghi() { return riepiloghi; }
    public void setRiepiloghi(List<RiepilogoIva> riepiloghi) { this.riepiloghi = riepiloghi; }

    public List<Scadenza> getScadenze() { return scadenze; }
    public void setScadenze(List<Scadenza> scadenze) { this.scadenze = scadenze; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }

    public Long getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(Long updatedBy) { this.updatedBy = updatedBy; }

    // Getters/setters per campi aggiuntivi
    public String getTitolo() { return titolo; }
    public void setTitolo(String titolo) { this.titolo = titolo; }

    public String getMotivoNota() { return motivoNota; }
    public void setMotivoNota(String motivoNota) { this.motivoNota = motivoNota; }

    public String getCausaleDescrizione() { return causaleDescrizione; }
    public void setCausaleDescrizione(String causaleDescrizione) { this.causaleDescrizione = causaleDescrizione; }

    public LocalDate getValiditaOfferta() { return validitaOfferta; }
    public void setValiditaOfferta(LocalDate validitaOfferta) { this.validitaOfferta = validitaOfferta; }

    // ==================== METODI BUSINESS ====================

    /**
     * Aggiunge una riga al documento
     */
    public void addRiga(RigaDocumento riga) {
        if (righe == null) {
            righe = new ArrayList<>();
        }
        righe.add(riga);
        riga.setDocumento(this);
        
        // Ricalcola i totali
        ricalcolaTotali();
    }

    /**
     * Rimuove una riga dal documento
     */
    public void removeRiga(RigaDocumento riga) {
        if (righe != null) {
            righe.remove(riga);
            riga.setDocumento(null);
        }
        
        // Ricalcola i totali
        ricalcolaTotali();
    }

    /**
     * Aggiunge una scadenza al documento
     */
    public void addScadenza(Scadenza scadenza) {
        if (scadenze == null) {
            scadenze = new ArrayList<>();
        }
        scadenze.add(scadenza);
        scadenza.setDocumento(this);
    }

    /**
     * Rimuove una scadenza dal documento
     */
    public void removeScadenza(Scadenza scadenza) {
        if (scadenze != null) {
            scadenze.remove(scadenza);
            scadenza.setDocumento(null);
        }
    }

    /**
     * Ricalcola tutti i totali del documento
     */
    public void ricalcolaTotali() {
        if (righe == null || righe.isEmpty()) {
            azzeraTotali();
            return;
        }

        // Calcola totali dalle righe
        this.totaleImponibile = righe.stream()
            .map(RigaDocumento::getImponibileRiga)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.totaleIva = righe.stream()
            .map(RigaDocumento::getIvaRiga)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.totaleDocumento = totaleImponibile.add(totaleIva).subtract(totaleRitenuta);

        // Ricalcola riepiloghi IVA
        ricalcolaRiepiloghi();
    }

    /**
     * Ricalcola i riepiloghi IVA
     */
    private void ricalcolaRiepiloghi() {
        if (righe == null || righe.isEmpty()) {
            if (riepiloghi != null) {
                riepiloghi.clear();
            }
            return;
        }

        // Raggruppa righe per aliquota IVA
        Map<AliquotaIva, List<RigaDocumento>> righePerAliquota = righe.stream()
            .collect(Collectors.groupingBy(RigaDocumento::getAliquotaIva));

        // Pulisce riepiloghi esistenti
        if (riepiloghi == null) {
            riepiloghi = new ArrayList<>();
        } else {
            riepiloghi.clear();
        }

        // Crea nuovi riepiloghi
        righePerAliquota.forEach((aliquota, righeAliquota) -> {
            BigDecimal imponibile = righeAliquota.stream()
                .map(RigaDocumento::getImponibileRiga)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal imposta = righeAliquota.stream()
                .map(RigaDocumento::getIvaRiga)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            RiepilogoIva riepilogo = new RiepilogoIva();
            riepilogo.setDocumento(this);
            riepilogo.setAliquotaIva(aliquota);
            riepilogo.setPercentualeIva(aliquota.getPercentuale());
            riepilogo.setNaturaIva(aliquota.getNatura());
            riepilogo.setImponibile(imponibile);
            riepilogo.setImposta(imposta);

            riepiloghi.add(riepilogo);
        });
    }

    /**
     * Azzera tutti i totali
     */
    private void azzeraTotali() {
        this.totaleImponibile = BigDecimal.ZERO;
        this.totaleIva = BigDecimal.ZERO;
        this.totaleDocumento = BigDecimal.ZERO;
        
        if (riepiloghi != null) {
            riepiloghi.clear();
        }
    }

    /**
     * Verifica se il documento è modificabile
     */
    public boolean isModificabile() {
        return stato == StatoDocumento.BOZZA;
    }

    /**
     * Verifica se il documento è stampabile
     */
    public boolean isStampabile() {
        return stato != StatoDocumento.ANNULLATO && !righe.isEmpty();
    }

    /**
     * Verifica se il documento genera scadenze
     */
    public boolean generaScadenze() {
        return tipoDocumento.isGeneraScadenze() && 
               totaleDocumento.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Copia i dati del soggetto nel documento
     */
    public void copiaUltimiDatiSoggetto() {
        if (soggetto != null) {
            this.ragioneSociale = soggetto.getRagioneSociale();
            this.partitaIva = soggetto.getPartitaIva();
            this.codiceFiscale = soggetto.getCodiceFiscale();
            this.codiceDestinatario = soggetto.getCodiceDestinatario();
            this.pec = soggetto.getPec();
            
            // Costruisce indirizzo completo
            StringBuilder indirizzo = new StringBuilder();
            if (soggetto.getIndirizzo() != null) {
                indirizzo.append(soggetto.getIndirizzo());
            }
            if (soggetto.getCap() != null || soggetto.getCitta() != null) {
                indirizzo.append("\n");
                if (soggetto.getCap() != null) {
                    indirizzo.append(soggetto.getCap()).append(" ");
                }
                if (soggetto.getCitta() != null) {
                    indirizzo.append(soggetto.getCitta());
                }
                if (soggetto.getProvincia() != null) {
                    indirizzo.append(" (").append(soggetto.getProvincia()).append(")");
                }
            }
            this.indirizzoCompleto = indirizzo.toString();
        }
    }

    /**
     * Valida il documento prima del salvataggio
     */
    public void valida() {
        if (righe == null || righe.isEmpty()) {
            throw new IllegalStateException("Documento senza righe");
        }

        if (totaleDocumento.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Totale documento deve essere positivo");
        }

        // Validazioni specifiche per tipo documento
        switch (tipoDocumento) {
            case FATTURA:
            case FATTURA_ELETTRONICA:
            case NOTA_CREDITO:
            case NOTA_DEBITO:
                validaDocumentoFiscale();
                break;
            case DDT:
                validaDdt();
                break;
            case PREVENTIVO:
            case ORDINE:
            case RICEVUTA:
                // Validazioni base già eseguite
                break;
        }
    }

    /**
     * Validazioni per documenti fiscali
     */
    private void validaDocumentoFiscale() {
        if (partitaIva == null && codiceFiscale == null) {
            throw new IllegalStateException("Documento fiscale senza dati fiscali del soggetto");
        }
        
        if (riepiloghi == null || riepiloghi.isEmpty()) {
            throw new IllegalStateException("Documento fiscale senza riepiloghi IVA");
        }
    }

    /**
     * Validazioni per DDT
     */
    private void validaDdt() {
        if (causaleTrasporto == null) {
            throw new IllegalStateException("DDT senza causale di trasporto");
        }
        
        if (dataTrasporto == null) {
            throw new IllegalStateException("DDT senza data di trasporto");
        }
    }

    // ==================== CONSTRAINT VALIDATION ====================
    @PrePersist
    @PreUpdate
    private void validateAndCalculate() {
        // Assicura che la data di registrazione sia impostata
        if (dataRegistrazione == null) {
            dataRegistrazione = LocalDate.now();
        }
        
        // Copia i dati del soggetto se non già presenti
        if (soggetto != null && ragioneSociale == null) {
            copiaUltimiDatiSoggetto();
        }
        
        // Ricalcola i totali
        ricalcolaTotali();
        
        // Valida il documento
        valida();
    }
}
