package com.gestionale.dto;

import java.math.BigDecimal;

/**
 * DTO per la risposta dei riepiloghi IVA
 */
public class RiepilogoIvaResponseDto {

    private Long id;
    private BigDecimal aliquotaIva;
    private BigDecimal imponibile;
    private BigDecimal imposta;
    private BigDecimal totale;
    private String descrizione;

    // Getter e Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BigDecimal getAliquotaIva() { return aliquotaIva; }
    public void setAliquotaIva(BigDecimal aliquotaIva) { this.aliquotaIva = aliquotaIva; }

    public BigDecimal getImponibile() { return imponibile; }
    public void setImponibile(BigDecimal imponibile) { this.imponibile = imponibile; }

    public BigDecimal getImposta() { return imposta; }
    public void setImposta(BigDecimal imposta) { this.imposta = imposta; }

    public BigDecimal getTotale() { return totale; }
    public void setTotale(BigDecimal totale) { this.totale = totale; }

    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }
}
