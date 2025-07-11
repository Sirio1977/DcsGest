package com.gestionale.entity;

import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entità base per soggetti (clienti e fornitori)
 */
@Entity
@Table(name = "soggetti")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_soggetto", discriminatorType = DiscriminatorType.STRING)
public abstract class Soggetto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Column(name = "codice_destinatario", length = 7)
    @Size(max = 7, message = "Codice destinatario non valido")
    private String codiceDestinatario;

    @Column(length = 100)
    @Email(message = "PEC non valida")
    private String pec;

    // ==================== INDIRIZZO ====================
    @Column(columnDefinition = "TEXT")
    private String indirizzo;

    @Column(length = 100)
    private String citta;

    @Column(length = 2)
    private String provincia;

    @Column(length = 5)
    private String cap;

    @Column(length = 3)
    private String nazione = "IT";

    // ==================== CONTATTI ====================
    @Column(length = 20)
    private String telefono;

    @Column(length = 100)
    @Email(message = "Email non valida")
    private String email;

    @Column(length = 100)
    private String referente;

    // ==================== DATI COMMERCIALI ====================
    @Column(name = "attivo")
    private Boolean attivo = true;

    @Column(name = "ritenuta_acconto")
    private Boolean ritenutaAcconto = false;

    @Column(name = "split_payment")
    private Boolean splitPayment = false;

    // ==================== RELAZIONI ====================
    @OneToMany(mappedBy = "soggetto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Documento> documenti = new ArrayList<>();

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

    // ==================== METODI BUSINESS ====================

    /**
     * Verifica se il soggetto ha dati fiscali completi
     */
    public boolean hasDatiFiscali() {
        return partitaIva != null || codiceFiscale != null;
    }

    /**
     * Restituisce l'identificativo fiscale principale
     */
    public String getIdentificativoFiscale() {
        return partitaIva != null ? partitaIva : codiceFiscale;
    }

    /**
     * Restituisce l'indirizzo completo formattato
     */
    public String getIndirizzoCompleto() {
        StringBuilder sb = new StringBuilder();
        if (indirizzo != null) {
            sb.append(indirizzo);
        }
        if (citta != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(citta);
        }
        if (provincia != null) {
            if (sb.length() > 0) sb.append(" (");
            sb.append(provincia);
            sb.append(")");
        }
        if (cap != null) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(cap);
        }
        return sb.toString();
    }

    /**
     * Verifica se il soggetto è attivo
     */
    public boolean isAttivo() {
        return attivo != null && attivo;
    }

    /**
     * Tipo di soggetto (astratto)
     */
    public abstract String getTipoSoggetto();

    // ==================== GETTER E SETTER ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRagioneSociale() {
        return ragioneSociale;
    }

    public void setRagioneSociale(String ragioneSociale) {
        this.ragioneSociale = ragioneSociale;
    }

    public String getPartitaIva() {
        return partitaIva;
    }

    public void setPartitaIva(String partitaIva) {
        this.partitaIva = partitaIva;
    }

    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    public void setCodiceFiscale(String codiceFiscale) {
        this.codiceFiscale = codiceFiscale;
    }

    public String getCodiceDestinatario() {
        return codiceDestinatario;
    }

    public void setCodiceDestinatario(String codiceDestinatario) {
        this.codiceDestinatario = codiceDestinatario;
    }

    public String getPec() {
        return pec;
    }

    public void setPec(String pec) {
        this.pec = pec;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public String getCitta() {
        return citta;
    }

    public void setCitta(String citta) {
        this.citta = citta;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getCap() {
        return cap;
    }

    public void setCap(String cap) {
        this.cap = cap;
    }

    public String getNazione() {
        return nazione;
    }

    public void setNazione(String nazione) {
        this.nazione = nazione;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getReferente() {
        return referente;
    }

    public void setReferente(String referente) {
        this.referente = referente;
    }

    public Boolean getAttivo() {
        return attivo;
    }

    public void setAttivo(Boolean attivo) {
        this.attivo = attivo;
    }

    public Boolean getRitenutaAcconto() {
        return ritenutaAcconto;
    }

    public void setRitenutaAcconto(Boolean ritenutaAcconto) {
        this.ritenutaAcconto = ritenutaAcconto;
    }

    public Boolean getSplitPayment() {
        return splitPayment;
    }

    public void setSplitPayment(Boolean splitPayment) {
        this.splitPayment = splitPayment;
    }

    public List<Documento> getDocumenti() {
        return documenti;
    }

    public void setDocumenti(List<Documento> documenti) {
        this.documenti = documenti;
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

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    // ==================== EQUALS & HASH CODE ====================

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Soggetto soggetto = (Soggetto) obj;
        return Objects.equals(id, soggetto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "id=" + id +
                ", ragioneSociale='" + ragioneSociale + '\'' +
                ", partitaIva='" + partitaIva + '\'' +
                ", codiceFiscale='" + codiceFiscale + '\'' +
                ", attivo=" + attivo +
                '}';
    }
}
