package com.gestionale.entity;

import javax.persistence.*;
import javax.validation.constraints.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity per la gestione delle numerazioni dei documenti
 */
@Entity
@Table(name = "numerazioni")
@Data
public class Numerazione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_documento", nullable = false, length = 20)
    @NotBlank(message = "Tipo documento obbligatorio")
    @Size(max = 20, message = "Tipo documento troppo lungo")
    private String tipoDocumento;

    @Column(name = "anno", nullable = false)
    @NotNull(message = "Anno obbligatorio")
    @Min(value = 2000, message = "Anno non valido")
    @Max(value = 2099, message = "Anno non valido")
    private Integer anno;

    @Column(name = "ultimo_numero", nullable = false)
    @NotNull(message = "Ultimo numero obbligatorio")
    @Min(value = 0, message = "Ultimo numero non può essere negativo")
    private Long ultimoNumero = 0L;

    @Column(name = "prefisso", length = 10)
    @Size(max = 10, message = "Prefisso troppo lungo")
    private String prefisso = "";

    @Column(name = "suffisso", length = 10)
    @Size(max = 10, message = "Suffisso troppo lungo")
    private String suffisso = "";

    @Column(name = "lunghezza_numero")
    @Min(value = 0, message = "Lunghezza numero non può essere negativa")
    @Max(value = 20, message = "Lunghezza numero troppo grande")
    private Integer lunghezzaNumero = 0;

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

    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public Integer getAnno() { return anno; }
    public void setAnno(Integer anno) { this.anno = anno; }

    public Long getUltimoNumero() { return ultimoNumero; }
    public void setUltimoNumero(Long ultimoNumero) { this.ultimoNumero = ultimoNumero; }

    public String getPrefisso() { return prefisso; }
    public void setPrefisso(String prefisso) { this.prefisso = prefisso; }

    public String getSuffisso() { return suffisso; }
    public void setSuffisso(String suffisso) { this.suffisso = suffisso; }

    public Integer getLunghezzaNumero() { return lunghezzaNumero; }
    public void setLunghezzaNumero(Integer lunghezzaNumero) { this.lunghezzaNumero = lunghezzaNumero; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // ==================== METODI BUSINESS ====================

    /**
     * Ottiene il prossimo numero disponibile
     */
    public Long getNextNumero() {
        return ultimoNumero + 1;
    }

    /**
     * Incrementa il numero e restituisce il valore aggiornato
     */
    public Long incrementaNumero() {
        ultimoNumero++;
        return ultimoNumero;
    }

    /**
     * Formatta il numero con prefisso, suffisso e padding
     */
    public String formatNumero(Long numero) {
        StringBuilder formatted = new StringBuilder();
        
        // Aggiunge prefisso
        if (prefisso != null && !prefisso.isEmpty()) {
            formatted.append(prefisso);
        }
        
        // Aggiunge numero con padding
        if (lunghezzaNumero != null && lunghezzaNumero > 0) {
            String numeroStr = String.format("%0" + lunghezzaNumero + "d", numero);
            formatted.append(numeroStr);
        } else {
            formatted.append(numero);
        }
        
        // Aggiunge suffisso
        if (suffisso != null && !suffisso.isEmpty()) {
            formatted.append(suffisso);
        }
        
        return formatted.toString();
    }

    /**
     * Formatta il prossimo numero
     */
    public String formatNextNumero() {
        return formatNumero(getNextNumero());
    }

    /**
     * Reimposta la numerazione per un nuovo anno
     */
    public void resetPerAnno(Integer nuovoAnno) {
        this.anno = nuovoAnno;
        this.ultimoNumero = 0L;
    }

    /**
     * Verifica se è necessario un reset per nuovo anno
     */
    public boolean needsResetForYear(Integer currentYear) {
        return !currentYear.equals(this.anno);
    }

    /**
     * Verifica se la numerazione è valida
     */
    public boolean isValida() {
        return tipoDocumento != null && !tipoDocumento.trim().isEmpty() &&
               anno != null && anno >= 2000 && anno <= 2099 &&
               ultimoNumero != null && ultimoNumero >= 0;
    }

    // ==================== VALIDATION ====================
    @PrePersist
    @PreUpdate
    private void validate() {
        if (!isValida()) {
            throw new IllegalStateException("Numerazione non valida");
        }
        
        // Normalizza stringhe
        if (prefisso == null) {
            prefisso = "";
        }
        if (suffisso == null) {
            suffisso = "";
        }
        if (lunghezzaNumero == null) {
            lunghezzaNumero = 0;
        }
    }
}
