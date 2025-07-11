package com.gestionale.entity;

import javax.persistence.*;
import javax.validation.constraints.*;
import com.gestionale.enums.AliquotaIva;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * Entity per i riepiloghi IVA dei documenti
 */
@Entity
@Table(name = "riepiloghi_iva")
@Data
@EqualsAndHashCode(exclude = {"documento"})
@ToString(exclude = {"documento"})
public class RiepilogoIva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "documento_id", nullable = false)
    private Documento documento;

    @Enumerated(EnumType.STRING)
    @Column(name = "aliquota_iva", nullable = false)
    private AliquotaIva aliquotaIva;

    @Column(name = "imponibile", nullable = false, precision = 19, scale = 2)
    private BigDecimal imponibile = BigDecimal.ZERO;

    @Column(name = "imposta", nullable = false, precision = 19, scale = 2)
    private BigDecimal imposta = BigDecimal.ZERO;

    @Column(name = "totale", nullable = false, precision = 19, scale = 2)
    private BigDecimal totale = BigDecimal.ZERO;

    @Size(max = 255)
    @Column(name = "descrizione")
    private String descrizione;

    // Costruttori
    public RiepilogoIva() {}

    public RiepilogoIva(Documento documento, AliquotaIva aliquotaIva, BigDecimal imponibile) {
        this.documento = documento;
        this.aliquotaIva = aliquotaIva;
        this.imponibile = imponibile;
        calcolaImposta();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Documento getDocumento() { return documento; }
    public void setDocumento(Documento documento) { this.documento = documento; }

    public AliquotaIva getAliquotaIva() { return aliquotaIva; }
    public void setAliquotaIva(AliquotaIva aliquotaIva) { this.aliquotaIva = aliquotaIva; }

    public BigDecimal getImponibile() { return imponibile; }
    public void setImponibile(BigDecimal imponibile) { this.imponibile = imponibile; }

    public BigDecimal getImposta() { return imposta; }
    public void setImposta(BigDecimal imposta) { this.imposta = imposta; }

    public BigDecimal getTotale() { return totale; }
    public void setTotale(BigDecimal totale) { this.totale = totale; }

    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }

    // Additional getters for Documento compatibility
    public Double getPercentualeIva() { 
        return aliquotaIva != null ? aliquotaIva.getPercentuale() : 0.0; 
    }
    public void setPercentualeIva(Double percentuale) { 
        // This is derived from aliquotaIva, no setter needed but required for compilation
    }

    public String getNaturaIva() { 
        return aliquotaIva != null ? aliquotaIva.getNatura() : null; 
    }
    public void setNaturaIva(String natura) { 
        // This is derived from aliquotaIva, no setter needed but required for compilation
    }

    // Metodi di business
    public void calcolaImposta() {
        if (aliquotaIva != null && imponibile != null) {
            this.imposta = aliquotaIva.calcolaImposta(imponibile);
            this.totale = imponibile.add(imposta);
        }
    }

    public void aggiungiImponibile(BigDecimal importo) {
        if (importo != null) {
            this.imponibile = this.imponibile.add(importo);
            calcolaImposta();
        }
    }
}
