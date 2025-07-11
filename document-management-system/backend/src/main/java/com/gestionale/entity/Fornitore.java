package com.gestionale.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity per la gestione dei fornitori
 */
@Entity
@Table(name = "fornitori")
@Data
@EqualsAndHashCode(exclude = {"id"})
@ToString
public class Fornitore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ragione_sociale", nullable = false)
    @NotBlank(message = "Ragione sociale obbligatoria")
    private String ragioneSociale;

    @Column(name = "partita_iva", unique = true)
    private String partitaIva;

    @Column(name = "codice_fiscale")
    private String codiceFiscale;

    @Column(name = "indirizzo")
    private String indirizzo;

    @Column(name = "citta")
    private String citta;

    @Column(name = "cap")
    private String cap;

    @Column(name = "provincia")
    private String provincia;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "email")
    private String email;

    @Column(name = "pec")
    private String pec;

    @Column(name = "attivo")
    private Boolean attivo = true;

    @Column(name = "note")
    private String note;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Costruttore di default
     */
    public Fornitore() {
        this.attivo = true;
    }

    /**
     * Costruttore con parametri principali
     */
    public Fornitore(String ragioneSociale, String partitaIva) {
        this();
        this.ragioneSociale = ragioneSociale;
        this.partitaIva = partitaIva;
    }

    /**
     * Verifica se il fornitore è attivo
     */
    public boolean isAttivo() {
        return attivo != null && attivo;
    }

    /**
     * Verifica se il fornitore ha la partita IVA
     */
    public boolean hasPartitaIva() {
        return partitaIva != null && !partitaIva.trim().isEmpty();
    }

    /**
     * Verifica se il fornitore ha il codice fiscale
     */
    public boolean hasCodiceFiscale() {
        return codiceFiscale != null && !codiceFiscale.trim().isEmpty();
    }
}
