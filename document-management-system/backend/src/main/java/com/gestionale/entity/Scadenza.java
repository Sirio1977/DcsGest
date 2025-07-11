package com.gestionale.entity;

import javax.persistence.*;
import javax.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity per le scadenze dei documenti
 */
@Entity
@Table(name = "scadenze")
@Data
@EqualsAndHashCode(exclude = {"documento"})
@ToString(exclude = {"documento"})
public class Scadenza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "documento_id", nullable = false)
    @NotNull(message = "Documento obbligatorio")
    private Documento documento;

    @Column(name = "numero_rata", nullable = false)
    @NotNull(message = "Numero rata obbligatorio")
    @Min(value = 1, message = "Numero rata deve essere maggiore di 0")
    private Integer numeroRata = 1;

    @Column(name = "data_scadenza", nullable = false)
    @NotNull(message = "Data scadenza obbligatoria")
    private LocalDate dataScadenza;

    @Column(name = "importo", nullable = false, precision = 15, scale = 2)
    @NotNull(message = "Importo obbligatorio")
    @DecimalMin(value = "0.01", message = "Importo deve essere maggiore di 0")
    private BigDecimal importo;

    @Column(name = "importo_pagato", precision = 15, scale = 2)
    @DecimalMin(value = "0.0", message = "Importo pagato non può essere negativo")
    private BigDecimal importoPagato = BigDecimal.ZERO;

    @Column(name = "saldato")
    private Boolean saldato = false;

    @Column(name = "data_saldo")
    private LocalDate dataSaldo;

    // ==================== MODALITÀ PAGAMENTO ====================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modalita_pagamento_id")
    private ModalitaPagamento modalitaPagamento;

    @Column(name = "banca", length = 255)
    @Size(max = 255, message = "Nome banca troppo lungo")
    private String banca;

    @Column(name = "iban", length = 34)
    @Size(max = 34, message = "IBAN troppo lungo")
    private String iban;

    // ==================== NOTE ====================
    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

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

    public Integer getNumeroRata() { return numeroRata; }
    public void setNumeroRata(Integer numeroRata) { this.numeroRata = numeroRata; }

    public LocalDate getDataScadenza() { return dataScadenza; }
    public void setDataScadenza(LocalDate dataScadenza) { this.dataScadenza = dataScadenza; }

    public BigDecimal getImporto() { return importo; }
    public void setImporto(BigDecimal importo) { this.importo = importo; }

    public BigDecimal getImportoPagato() { return importoPagato; }
    public void setImportoPagato(BigDecimal importoPagato) { this.importoPagato = importoPagato; }

    public Boolean getSaldato() { return saldato; }
    public void setSaldato(Boolean saldato) { this.saldato = saldato; }

    // Compatibility method for some services
    public Boolean getPagata() { return saldato; }
    public void setPagata(Boolean pagata) { this.saldato = pagata; }

    public LocalDate getDataSaldo() { return dataSaldo; }
    public void setDataSaldo(LocalDate dataSaldo) { this.dataSaldo = dataSaldo; }

    // Compatibility method for some services
    public LocalDate getDataPagamento() { return dataSaldo; }
    public void setDataPagamento(LocalDate dataPagamento) { this.dataSaldo = dataPagamento; }

    public ModalitaPagamento getModalitaPagamento() { return modalitaPagamento; }
    public void setModalitaPagamento(ModalitaPagamento modalitaPagamento) { this.modalitaPagamento = modalitaPagamento; }

    public String getBanca() { return banca; }
    public void setBanca(String banca) { this.banca = banca; }

    public String getIban() { return iban; }
    public void setIban(String iban) { this.iban = iban; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // ==================== METODI BUSINESS ====================

    /**
     * Calcola il residuo da pagare
     */
    public BigDecimal getResiduo() {
        return importo.subtract(importoPagato);
    }

    /**
     * Verifica se la scadenza è completamente saldata
     */
    public boolean isSaldata() {
        return saldato != null && saldato;
    }

    /**
     * Verifica se la scadenza è scaduta
     */
    public boolean isScaduta() {
        return !isSaldata() && dataScadenza.isBefore(LocalDate.now());
    }

    /**
     * Verifica se la scadenza è in scadenza (entro i prossimi giorni)
     */
    public boolean isInScadenza(int giorni) {
        if (isSaldata()) {
            return false;
        }
        LocalDate dataLimite = LocalDate.now().plusDays(giorni);
        return dataScadenza.isBefore(dataLimite) || dataScadenza.isEqual(dataLimite);
    }

    /**
     * Calcola i giorni di ritardo
     */
    public long getGiorniRitardo() {
        if (isSaldata() || !isScaduta()) {
            return 0;
        }
        return LocalDate.now().toEpochDay() - dataScadenza.toEpochDay();
    }

    /**
     * Registra un pagamento parziale
     */
    public void registraPagamento(BigDecimal importoDaPagare, LocalDate dataPagamento) {
        if (importoDaPagare.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Importo da pagare deve essere positivo");
        }
        
        if (importoDaPagare.compareTo(getResiduo()) > 0) {
            throw new IllegalArgumentException("Importo da pagare superiore al residuo");
        }
        
        this.importoPagato = this.importoPagato.add(importoDaPagare);
        
        // Verifica se è completamente saldata
        if (getResiduo().compareTo(new BigDecimal("0.01")) < 0) {
            this.saldato = true;
            this.dataSaldo = dataPagamento;
        }
    }

    /**
     * Salda completamente la scadenza
     */
    public void salda(LocalDate dataSaldo) {
        this.importoPagato = this.importo;
        this.saldato = true;
        this.dataSaldo = dataSaldo;
    }

    /**
     * Riapre la scadenza (annulla il saldo)
     */
    public void riapri() {
        this.saldato = false;
        this.dataSaldo = null;
        this.importoPagato = BigDecimal.ZERO;
    }

    /**
     * Verifica se la scadenza è valida
     */
    public boolean isValida() {
        return dataScadenza != null &&
               importo != null && importo.compareTo(BigDecimal.ZERO) > 0 &&
               importoPagato != null && importoPagato.compareTo(BigDecimal.ZERO) >= 0 &&
               importoPagato.compareTo(importo) <= 0 &&
               numeroRata != null && numeroRata > 0;
    }

    /**
     * Calcola la percentuale di pagamento
     */
    public BigDecimal getPercentualePagamento() {
        if (importo.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        return importoPagato.divide(importo, 4, BigDecimal.ROUND_HALF_UP)
                           .multiply(new BigDecimal("100"))
                           .setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    // ==================== VALIDATION ====================
    @PrePersist
    @PreUpdate
    private void validate() {
        if (!isValida()) {
            throw new IllegalStateException("Scadenza non valida");
        }
        
        // Verifica coerenza saldo
        if (saldato != null && saldato && getResiduo().compareTo(new BigDecimal("0.01")) >= 0) {
            throw new IllegalStateException("Scadenza marcata come saldata ma ha ancora un residuo");
        }
        
        // Verifica data saldo
        if (saldato != null && saldato && dataSaldo == null) {
            dataSaldo = LocalDate.now();
        }
    }
}
