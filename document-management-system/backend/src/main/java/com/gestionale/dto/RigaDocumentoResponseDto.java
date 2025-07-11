package com.gestionale.dto;

import java.math.BigDecimal;

/**
 * DTO per la risposta delle righe documento
 */
public class RigaDocumentoResponseDto {

    private Long id;
    private Long articoloId;
    private String articoloCodice;
    private String articoloDescrizione;
    private String descrizione;
    private BigDecimal quantita;
    private String unitaMisura;
    private BigDecimal prezzoUnitario;
    private BigDecimal scontoPercentuale;
    private BigDecimal scontoImporto;
    private BigDecimal aliquotaIva;
    private BigDecimal importoNetto;
    private BigDecimal importoIva;
    private BigDecimal importoTotale;
    private String note;
    private Integer ordine;

    // Getter e Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getArticoloId() { return articoloId; }
    public void setArticoloId(Long articoloId) { this.articoloId = articoloId; }

    public String getArticoloCodice() { return articoloCodice; }
    public void setArticoloCodice(String articoloCodice) { this.articoloCodice = articoloCodice; }

    public String getArticoloDescrizione() { return articoloDescrizione; }
    public void setArticoloDescrizione(String articoloDescrizione) { this.articoloDescrizione = articoloDescrizione; }

    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }

    public BigDecimal getQuantita() { return quantita; }
    public void setQuantita(BigDecimal quantita) { this.quantita = quantita; }

    public String getUnitaMisura() { return unitaMisura; }
    public void setUnitaMisura(String unitaMisura) { this.unitaMisura = unitaMisura; }

    public BigDecimal getPrezzoUnitario() { return prezzoUnitario; }
    public void setPrezzoUnitario(BigDecimal prezzoUnitario) { this.prezzoUnitario = prezzoUnitario; }

    public BigDecimal getScontoPercentuale() { return scontoPercentuale; }
    public void setScontoPercentuale(BigDecimal scontoPercentuale) { this.scontoPercentuale = scontoPercentuale; }

    public BigDecimal getScontoImporto() { return scontoImporto; }
    public void setScontoImporto(BigDecimal scontoImporto) { this.scontoImporto = scontoImporto; }

    public BigDecimal getAliquotaIva() { return aliquotaIva; }
    public void setAliquotaIva(BigDecimal aliquotaIva) { this.aliquotaIva = aliquotaIva; }

    public BigDecimal getImportoNetto() { return importoNetto; }
    public void setImportoNetto(BigDecimal importoNetto) { this.importoNetto = importoNetto; }

    public BigDecimal getImportoIva() { return importoIva; }
    public void setImportoIva(BigDecimal importoIva) { this.importoIva = importoIva; }

    public BigDecimal getImportoTotale() { return importoTotale; }
    public void setImportoTotale(BigDecimal importoTotale) { this.importoTotale = importoTotale; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public Integer getOrdine() { return ordine; }
    public void setOrdine(Integer ordine) { this.ordine = ordine; }
}
