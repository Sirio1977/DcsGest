package com.gestionale.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity per la gestione delle modalità di pagamento
 */
@Entity
@Table(name = "modalita_pagamento")
public class ModalitaPagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Codice modalità pagamento obbligatorio")
    private String codice;

    @Column(nullable = false)
    @NotBlank(message = "Descrizione modalità pagamento obbligatoria")
    private String descrizione;

    @Column(name = "giorni_pagamento")
    private Integer giorniPagamento;

    @Column(name = "fine_mese")
    private Boolean fineMese = false;

    @Column(name = "numero_rate")
    private Integer numeroRate = 1;

    @Column(name = "giorni_tra_rate")
    private Integer giorniTraRate;

    @Column(name = "attiva")
    private Boolean attiva = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Costruttore di default
     */
    public ModalitaPagamento() {
        this.attiva = true;
        this.fineMese = false;
        this.numeroRate = 1;
    }

    /**
     * Costruttore con parametri principali
     */
    public ModalitaPagamento(String codice, String descrizione, Integer giorniPagamento) {
        this();
        this.codice = codice;
        this.descrizione = descrizione;
        this.giorniPagamento = giorniPagamento;
    }

    /**
     * Verifica se la modalità è a rate
     */
    public boolean isRatale() {
        return numeroRate != null && numeroRate > 1;
    }

    /**
     * Verifica se la modalità è attiva
     */
    public boolean isAttiva() {
        return attiva != null && attiva;
    }

    // ==================== GETTER E SETTER ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodice() {
        return codice;
    }

    public void setCodice(String codice) {
        this.codice = codice;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public Integer getGiorniPagamento() {
        return giorniPagamento;
    }

    public void setGiorniPagamento(Integer giorniPagamento) {
        this.giorniPagamento = giorniPagamento;
    }

    public Boolean getFineMese() {
        return fineMese;
    }

    public void setFineMese(Boolean fineMese) {
        this.fineMese = fineMese;
    }

    public Integer getNumeroRate() {
        return numeroRate;
    }

    public void setNumeroRate(Integer numeroRate) {
        this.numeroRate = numeroRate;
    }

    public Integer getGiorniTraRate() {
        return giorniTraRate;
    }

    public void setGiorniTraRate(Integer giorniTraRate) {
        this.giorniTraRate = giorniTraRate;
    }

    public Boolean getAttiva() {
        return attiva;
    }

    public void setAttiva(Boolean attiva) {
        this.attiva = attiva;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // ==================== EQUALS & HASH CODE ====================

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ModalitaPagamento that = (ModalitaPagamento) obj;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ModalitaPagamento{" +
                "id=" + id +
                ", codice='" + codice + '\'' +
                ", descrizione='" + descrizione + '\'' +
                ", giorniPagamento=" + giorniPagamento +
                ", numeroRate=" + numeroRate +
                ", attiva=" + attiva +
                '}';
    }
}
