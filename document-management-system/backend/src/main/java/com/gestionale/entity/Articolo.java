package com.gestionale.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "articoli")
public class Articolo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String codice;
    
    @Column(nullable = false)
    private String descrizione;
    
    private String descrizioneEstesa;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal prezzo = BigDecimal.ZERO;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal costo = BigDecimal.ZERO;
    
    @Column(name = "aliquota_iva", precision = 5, scale = 2)
    private BigDecimal aliquotaIva = new BigDecimal("22.00");
    
    private String unitaMisura = "PZ";
    
    @Column(precision = 10, scale = 3)
    private BigDecimal giacenza = BigDecimal.ZERO;
    
    @Column(precision = 10, scale = 3)
    private BigDecimal giacenzaMinima = BigDecimal.ZERO;
    
    @Enumerated(EnumType.STRING)
    private TipoArticolo tipo = TipoArticolo.PRODOTTO;
    
    @Column(nullable = false)
    private Boolean attivo = true;
    
    private String categoria;
    private String fornitore;
    private String codiceFornitore;
    private String note;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum TipoArticolo {
        PRODOTTO, SERVIZIO, MATERIA_PRIMA, SEMILAVORATO
    }
    
    // Costruttori
    public Articolo() {}
    
    public Articolo(String codice, String descrizione, BigDecimal prezzo) {
        this.codice = codice;
        this.descrizione = descrizione;
        this.prezzo = prezzo;
    }
    
    // Getters e Setters
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
    
    public String getDescrizioneEstesa() {
        return descrizioneEstesa;
    }
    
    public void setDescrizioneEstesa(String descrizioneEstesa) {
        this.descrizioneEstesa = descrizioneEstesa;
    }
    
    public BigDecimal getPrezzo() {
        return prezzo;
    }
    
    public void setPrezzo(BigDecimal prezzo) {
        this.prezzo = prezzo;
    }
    
    public BigDecimal getCosto() {
        return costo;
    }
    
    public void setCosto(BigDecimal costo) {
        this.costo = costo;
    }
    
    public BigDecimal getAliquotaIva() {
        return aliquotaIva;
    }
    
    public void setAliquotaIva(BigDecimal aliquotaIva) {
        this.aliquotaIva = aliquotaIva;
    }
    
    public String getUnitaMisura() {
        return unitaMisura;
    }
    
    public void setUnitaMisura(String unitaMisura) {
        this.unitaMisura = unitaMisura;
    }
    
    public BigDecimal getGiacenza() {
        return giacenza;
    }
    
    public void setGiacenza(BigDecimal giacenza) {
        this.giacenza = giacenza;
    }
    
    public BigDecimal getGiacenzaMinima() {
        return giacenzaMinima;
    }
    
    public void setGiacenzaMinima(BigDecimal giacenzaMinima) {
        this.giacenzaMinima = giacenzaMinima;
    }
    
    public TipoArticolo getTipo() {
        return tipo;
    }
    
    public void setTipo(TipoArticolo tipo) {
        this.tipo = tipo;
    }
    
    public Boolean getAttivo() {
        return attivo;
    }
    
    public void setAttivo(Boolean attivo) {
        this.attivo = attivo;
    }
    
    public String getCategoria() {
        return categoria;
    }
    
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    
    public String getFornitore() {
        return fornitore;
    }
    
    public void setFornitore(String fornitore) {
        this.fornitore = fornitore;
    }
    
    public String getCodiceFornitore() {
        return codiceFornitore;
    }
    
    public void setCodiceFornitore(String codiceFornitore) {
        this.codiceFornitore = codiceFornitore;
    }
    
    public String getNote() {
        return note;
    }
    
    public void setNote(String note) {
        this.note = note;
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
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
