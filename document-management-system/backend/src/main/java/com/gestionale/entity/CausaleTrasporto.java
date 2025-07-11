package com.gestionale.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity per la gestione delle causali di trasporto
 */
@Entity
@Table(name = "causale_trasporto")
public class CausaleTrasporto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Codice causale trasporto obbligatorio")
    private String codice;

    @Column(nullable = false)
    @NotBlank(message = "Descrizione causale trasporto obbligatoria")
    private String descrizione;

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
    public CausaleTrasporto() {
        this.attiva = true;
    }

    /**
     * Costruttore con parametri principali
     */
    public CausaleTrasporto(String codice, String descrizione) {
        this();
        this.codice = codice;
        this.descrizione = descrizione;
    }

    /**
     * Verifica se la causale è attiva
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
        CausaleTrasporto that = (CausaleTrasporto) obj;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "CausaleTrasporto{" +
                "id=" + id +
                ", codice='" + codice + '\'' +
                ", descrizione='" + descrizione + '\'' +
                ", attiva=" + attiva +
                '}';
    }
}
