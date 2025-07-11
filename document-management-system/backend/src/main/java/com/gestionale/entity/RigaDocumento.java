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
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * Entity per le righe dei documenti
 */
@Entity
@Table(name = "righe_documenti")
@Data
@EqualsAndHashCode(exclude = {"documento"})
@ToString(exclude = {"documento"})
public class RigaDocumento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "documento_id", nullable = false)
    @NotNull(message = "Documento obbligatorio")
    private Documento documento;

    @Column(name = "riga_numero", nullable = false)
    @NotNull(message = "Numero riga obbligatorio")
    @Min(value = 1, message = "Numero riga deve essere maggiore di 0")
    private Integer rigaNumero;

    // ==================== ARTICOLO/SERVIZIO ====================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "articolo_id")
    private Articolo articolo;

    @Column(name = "codice_articolo", length = 50)
    @Size(max = 50, message = "Codice articolo troppo lungo")
    private String codiceArticolo;

    @Column(name = "descrizione", nullable = false)
    @NotBlank(message = "Descrizione obbligatoria")
    @Size(max = 255, message = "Descrizione troppo lunga")
    private String descrizione;

    @Column(name = "descrizione_estesa", columnDefinition = "TEXT")
    private String descrizioneEstesa;

    @Column(name = "unita_misura", length = 10)
    @Size(max = 10, message = "Unità di misura troppo lunga")
    private String unitaMisura = "NR";

    // ==================== QUANTITÀ E PREZZI ====================
    @Column(name = "quantita", nullable = false, precision = 15, scale = 3)
    @NotNull(message = "Quantità obbligatoria")
    @DecimalMin(value = "0.001", message = "Quantità deve essere maggiore di 0")
    private BigDecimal quantita = BigDecimal.ONE;

    @Column(name = "prezzo_unitario", nullable = false, precision = 15, scale = 4)
    @NotNull(message = "Prezzo unitario obbligatorio")
    @DecimalMin(value = "0.0", message = "Prezzo unitario non può essere negativo")
    private BigDecimal prezzoUnitario = BigDecimal.ZERO;

    @Column(name = "sconto1", precision = 5, scale = 2)
    @DecimalMin(value = "0.0", message = "Sconto1 non può essere negativo")
    @DecimalMax(value = "100.0", message = "Sconto1 non può essere superiore al 100%")
    private BigDecimal sconto1 = BigDecimal.ZERO;

    @Column(name = "sconto2", precision = 5, scale = 2)
    @DecimalMin(value = "0.0", message = "Sconto2 non può essere negativo")
    @DecimalMax(value = "100.0", message = "Sconto2 non può essere superiore al 100%")
    private BigDecimal sconto2 = BigDecimal.ZERO;

    @Column(name = "sconto3", precision = 5, scale = 2)
    @DecimalMin(value = "0.0", message = "Sconto3 non può essere negativo")
    @DecimalMax(value = "100.0", message = "Sconto3 non può essere superiore al 100%")
    private BigDecimal sconto3 = BigDecimal.ZERO;

    // ==================== IVA ====================
    @Enumerated(EnumType.STRING)
    @Column(name = "aliquota_iva", nullable = false)
    @NotNull(message = "Aliquota IVA obbligatoria")
    private AliquotaIva aliquotaIva;

    @Column(name = "percentuale_iva", nullable = false, precision = 5, scale = 2)
    @NotNull(message = "Percentuale IVA obbligatoria")
    @DecimalMin(value = "0.0", message = "Percentuale IVA non può essere negativa")
    @DecimalMax(value = "100.0", message = "Percentuale IVA non può essere superiore al 100%")
    private BigDecimal percentualeIva = BigDecimal.ZERO;

    @Column(name = "natura_iva", length = 10)
    @Size(max = 10, message = "Natura IVA troppo lunga")
    private String naturaIva;

    // ==================== TOTALI RIGA (CALCOLATI) ====================
    @Column(name = "imponibile_riga", nullable = false, precision = 15, scale = 2)
    @NotNull(message = "Imponibile riga obbligatorio")
    @DecimalMin(value = "0.0", message = "Imponibile riga non può essere negativo")
    private BigDecimal imponibileRiga = BigDecimal.ZERO;

    @Column(name = "iva_riga", nullable = false, precision = 15, scale = 2)
    @NotNull(message = "IVA riga obbligatoria")
    @DecimalMin(value = "0.0", message = "IVA riga non può essere negativa")
    private BigDecimal ivaRiga = BigDecimal.ZERO;

    @Column(name = "totale_riga", nullable = false, precision = 15, scale = 2)
    @NotNull(message = "Totale riga obbligatorio")
    @DecimalMin(value = "0.0", message = "Totale riga non può essere negativo")
    private BigDecimal totaleRiga = BigDecimal.ZERO;

    // ==================== DATI AGGIUNTIVI ====================
    @Column(name = "note_riga", columnDefinition = "TEXT")
    private String noteRiga;

    // ==================== AUDIT ====================
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ==================== GETTERS AND SETTERS ====================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Documento getDocumento() { return documento; }
    public void setDocumento(Documento documento) { this.documento = documento; }

    public Integer getRigaNumero() { return rigaNumero; }
    public void setRigaNumero(Integer rigaNumero) { this.rigaNumero = rigaNumero; }

    public Articolo getArticolo() { return articolo; }
    public void setArticolo(Articolo articolo) { this.articolo = articolo; }

    public String getCodiceArticolo() { return codiceArticolo; }
    public void setCodiceArticolo(String codiceArticolo) { this.codiceArticolo = codiceArticolo; }

    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }

    public String getDescrizioneEstesa() { return descrizioneEstesa; }
    public void setDescrizioneEstesa(String descrizioneEstesa) { this.descrizioneEstesa = descrizioneEstesa; }

    public String getUnitaMisura() { return unitaMisura; }
    public void setUnitaMisura(String unitaMisura) { this.unitaMisura = unitaMisura; }

    public BigDecimal getQuantita() { return quantita; }
    public void setQuantita(BigDecimal quantita) { this.quantita = quantita; }

    public BigDecimal getPrezzoUnitario() { return prezzoUnitario; }
    public void setPrezzoUnitario(BigDecimal prezzoUnitario) { this.prezzoUnitario = prezzoUnitario; }

    public BigDecimal getSconto1() { return sconto1; }
    public void setSconto1(BigDecimal sconto1) { this.sconto1 = sconto1; }

    public BigDecimal getSconto2() { return sconto2; }
    public void setSconto2(BigDecimal sconto2) { this.sconto2 = sconto2; }

    public BigDecimal getSconto3() { return sconto3; }
    public void setSconto3(BigDecimal sconto3) { this.sconto3 = sconto3; }

    public AliquotaIva getAliquotaIva() { return aliquotaIva; }
    public void setAliquotaIva(AliquotaIva aliquotaIva) { this.aliquotaIva = aliquotaIva; }

    public BigDecimal getPercentualeIva() { return percentualeIva; }
    public void setPercentualeIva(BigDecimal percentualeIva) { this.percentualeIva = percentualeIva; }

    public String getNaturaIva() { return naturaIva; }
    public void setNaturaIva(String naturaIva) { this.naturaIva = naturaIva; }

    public BigDecimal getImponibileRiga() { return imponibileRiga; }
    public void setImponibileRiga(BigDecimal imponibileRiga) { this.imponibileRiga = imponibileRiga; }

    public BigDecimal getIvaRiga() { return ivaRiga; }
    public void setIvaRiga(BigDecimal ivaRiga) { this.ivaRiga = ivaRiga; }

    public BigDecimal getTotaleRiga() { return totaleRiga; }
    public void setTotaleRiga(BigDecimal totaleRiga) { this.totaleRiga = totaleRiga; }

    public String getNoteRiga() { return noteRiga; }
    public void setNoteRiga(String noteRiga) { this.noteRiga = noteRiga; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // ==================== METODI BUSINESS ====================

    /**
     * Calcola tutti i totali della riga
     */
    public void calcolaTotali() {
        // Calcolo imponibile con sconti
        BigDecimal imponibile = prezzoUnitario.multiply(quantita);
        
        // Applica sconti in cascata
        imponibile = applicaSconto(imponibile, sconto1);
        imponibile = applicaSconto(imponibile, sconto2);
        imponibile = applicaSconto(imponibile, sconto3);
        
        // Arrotonda imponibile a 2 decimali
        this.imponibileRiga = imponibile.setScale(2, RoundingMode.HALF_UP);
        
        // Calcolo IVA
        if (percentualeIva != null && percentualeIva.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal moltiplicatoreIva = percentualeIva.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
            this.ivaRiga = this.imponibileRiga.multiply(moltiplicatoreIva).setScale(2, RoundingMode.HALF_UP);
        } else {
            this.ivaRiga = BigDecimal.ZERO;
        }
        
        // Totale riga
        this.totaleRiga = this.imponibileRiga.add(this.ivaRiga);
    }

    /**
     * Applica uno sconto percentuale a un importo
     */
    private BigDecimal applicaSconto(BigDecimal importo, BigDecimal percentualeSconto) {
        if (percentualeSconto == null || percentualeSconto.compareTo(BigDecimal.ZERO) == 0) {
            return importo;
        }
        
        BigDecimal moltiplicatoreSconto = percentualeSconto.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
        BigDecimal sconto = importo.multiply(moltiplicatoreSconto);
        return importo.subtract(sconto);
    }

    /**
     * Copia i dati dall'articolo nella riga
     */
    public void copiaDatiArticolo() {
        if (articolo != null) {
            this.codiceArticolo = articolo.getCodice();
            this.descrizione = articolo.getDescrizione();
            this.descrizioneEstesa = articolo.getDescrizioneEstesa();
            this.unitaMisura = articolo.getUnitaMisura();
            this.prezzoUnitario = articolo.getPrezzoVendita();
            // Converte la percentuale IVA dell'articolo nell'enum AliquotaIva
            if (articolo.getAliquotaIva() != null) {
                this.aliquotaIva = AliquotaIva.byPercentuale(articolo.getAliquotaIva().doubleValue());
                this.percentualeIva = articolo.getAliquotaIva();
                this.naturaIva = this.aliquotaIva.getNatura();
            }
        }
    }

    /**
     * Verifica se la riga è completa e valida
     */
    public boolean isValida() {
        return descrizione != null && !descrizione.trim().isEmpty() &&
               quantita != null && quantita.compareTo(BigDecimal.ZERO) > 0 &&
               prezzoUnitario != null && prezzoUnitario.compareTo(BigDecimal.ZERO) >= 0 &&
               aliquotaIva != null &&
               percentualeIva != null;
    }

    /**
     * Calcola il prezzo netto dopo gli sconti
     */
    public BigDecimal getPrezzoNetto() {
        BigDecimal prezzo = prezzoUnitario;
        prezzo = applicaSconto(prezzo, sconto1);
        prezzo = applicaSconto(prezzo, sconto2);
        prezzo = applicaSconto(prezzo, sconto3);
        return prezzo.setScale(4, RoundingMode.HALF_UP);
    }

    /**
     * Calcola la percentuale di sconto totale
     */
    public BigDecimal getPercentualeScontoTotale() {
        if (prezzoUnitario == null || prezzoUnitario.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal prezzoNetto = getPrezzoNetto();
        BigDecimal sconto = prezzoUnitario.subtract(prezzoNetto);
        BigDecimal percentuale = sconto.divide(prezzoUnitario, 4, RoundingMode.HALF_UP)
                                      .multiply(new BigDecimal("100"));
        
        return percentuale.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Verifica se la riga ha sconti
     */
    public boolean hasSconti() {
        return (sconto1 != null && sconto1.compareTo(BigDecimal.ZERO) > 0) ||
               (sconto2 != null && sconto2.compareTo(BigDecimal.ZERO) > 0) ||
               (sconto3 != null && sconto3.compareTo(BigDecimal.ZERO) > 0);
    }

    // ==================== VALIDATION ====================
    @PrePersist
    @PreUpdate
    private void validateAndCalculate() {
        // Copia dati articolo se presente
        if (articolo != null && codiceArticolo == null) {
            copiaDatiArticolo();
        }
        
        // Assicura che la percentuale IVA sia sincronizzata
        if (aliquotaIva != null) {
            this.percentualeIva = BigDecimal.valueOf(aliquotaIva.getPercentuale());
            this.naturaIva = aliquotaIva.getNatura();
        }
        
        // Calcola i totali
        calcolaTotali();
        
        // Validazione business
        if (!isValida()) {
            throw new IllegalStateException("Riga documento non valida");
        }
    }
}
