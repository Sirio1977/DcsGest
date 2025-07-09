package com.gestionale.entity;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "articoli_fornitori")
public class ArticoloFornitore {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "codice", length = 50, nullable = false)
    private String codice;
    
    @Column(name = "descrizione", length = 500, nullable = false)
    private String descrizione;
    
    @Column(name = "quantita", nullable = false)
    private Double quantita;
    
    @Column(name = "prezzo_unitario", nullable = false)
    private Double prezzoUnitario;
    
    @Column(name = "importo", nullable = false)
    private Double importo;
    
    @Column(name = "unita_misura", length = 10, nullable = false)
    private String unitaMisura;
    
    @Column(name = "aliquota_iva", nullable = false)
    private Integer aliquotaIVA;
    
    // Informazioni fornitore
    @Column(name = "fornitore_partita_iva", length = 20, nullable = false)
    private String fornitorePartitaIva;
    
    @Column(name = "fornitore_ragione_sociale", length = 200, nullable = false)
    private String fornitoreRagioneSociale;
    
    @Column(name = "fornitore_categoria", length = 100)
    private String fornitoreCategoria;
    
    @Column(name = "data_documento")
    private LocalDate dataDocumento;
    
    @Column(name = "data_ultimo_aggiornamento")
    private LocalDate dataUltimoAggiornamento;
    
    @Column(name = "codice_interno", length = 50)
    private String codiceInterno;
    
    // Prezzi storici come JSON
    @Column(name = "prezzi_storici", columnDefinition = "TEXT")
    private String prezziStorici;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Costruttori
    public ArticoloFornitore() {
        this.createdAt = LocalDateTime.now();
    }
    
    public ArticoloFornitore(String codice, String descrizione) {
        this();
        this.codice = codice;
        this.descrizione = descrizione;
    }
    
    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getCodice() { return codice; }
    public void setCodice(String codice) { this.codice = codice; }
    
    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }
    
    public Double getQuantita() { return quantita; }
    public void setQuantita(Double quantita) { this.quantita = quantita; }
    
    public Double getPrezzoUnitario() { return prezzoUnitario; }
    public void setPrezzoUnitario(Double prezzoUnitario) { this.prezzoUnitario = prezzoUnitario; }
    
    public Double getImporto() { return importo; }
    public void setImporto(Double importo) { this.importo = importo; }
    
    public String getUnitaMisura() { return unitaMisura; }
    public void setUnitaMisura(String unitaMisura) { this.unitaMisura = unitaMisura; }
    
    public Integer getAliquotaIVA() { return aliquotaIVA; }
    public void setAliquotaIVA(Integer aliquotaIVA) { this.aliquotaIVA = aliquotaIVA; }
    
    public String getFornitorePartitaIva() { return fornitorePartitaIva; }
    public void setFornitorePartitaIva(String fornitorePartitaIva) { this.fornitorePartitaIva = fornitorePartitaIva; }
    
    public String getFornitoreRagioneSociale() { return fornitoreRagioneSociale; }
    public void setFornitoreRagioneSociale(String fornitoreRagioneSociale) { this.fornitoreRagioneSociale = fornitoreRagioneSociale; }
    
    public String getFornitoreCategoria() { return fornitoreCategoria; }
    public void setFornitoreCategoria(String fornitoreCategoria) { this.fornitoreCategoria = fornitoreCategoria; }
    
    public LocalDate getDataDocumento() { return dataDocumento; }
    public void setDataDocumento(LocalDate dataDocumento) { this.dataDocumento = dataDocumento; }
    
    public LocalDate getDataUltimoAggiornamento() { return dataUltimoAggiornamento; }
    public void setDataUltimoAggiornamento(LocalDate dataUltimoAggiornamento) { this.dataUltimoAggiornamento = dataUltimoAggiornamento; }
    
    public String getCodiceInterno() { return codiceInterno; }
    public void setCodiceInterno(String codiceInterno) { this.codiceInterno = codiceInterno; }
    
    public String getPrezziStorici() { return prezziStorici; }
    public void setPrezziStorici(String prezziStorici) { this.prezziStorici = prezziStorici; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    @Override
    public String toString() {
        return "ArticoloFornitore{" +
                "id=" + id +
                ", codice='" + codice + '\'' +
                ", descrizione='" + descrizione + '\'' +
                ", quantita=" + quantita +
                ", prezzoUnitario=" + prezzoUnitario +
                ", importo=" + importo +
                ", unitaMisura='" + unitaMisura + '\'' +
                ", aliquotaIVA=" + aliquotaIVA +
                ", fornitorePartitaIva='" + fornitorePartitaIva + '\'' +
                ", fornitoreRagioneSociale='" + fornitoreRagioneSociale + '\'' +
                ", fornitoreCategoria='" + fornitoreCategoria + '\'' +
                ", dataDocumento=" + dataDocumento +
                ", dataUltimoAggiornamento=" + dataUltimoAggiornamento +
                ", codiceInterno='" + codiceInterno + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
