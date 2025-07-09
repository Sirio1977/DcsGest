package com.gestionale.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "configurazioni")
public class Configurazione {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "chiave", unique = true, nullable = false, length = 100)
    private String chiave;
    
    @Column(name = "valore", columnDefinition = "TEXT")
    private String valore;
    
    @Column(name = "descrizione", columnDefinition = "TEXT")
    private String descrizione;
    
    @Column(name = "tipo", length = 20)
    private String tipo = "STRING";
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public Configurazione() {}
    
    public Configurazione(String chiave, String valore, String descrizione) {
        this.chiave = chiave;
        this.valore = valore;
        this.descrizione = descrizione;
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getChiave() {
        return chiave;
    }
    
    public void setChiave(String chiave) {
        this.chiave = chiave;
    }
    
    public String getValore() {
        return valore;
    }
    
    public void setValore(String valore) {
        this.valore = valore;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getDescrizione() {
        return descrizione;
    }
    
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
    
    public String getTipo() {
        return tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
