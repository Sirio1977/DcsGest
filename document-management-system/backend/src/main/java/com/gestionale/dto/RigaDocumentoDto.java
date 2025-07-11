package com.gestionale.dto;

import javax.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO per le righe documento
 */
@Data
public class RigaDocumentoDto {

    @NotNull(message = "L'articolo è obbligatorio")
    private Long articoloId;

    @NotBlank(message = "La descrizione è obbligatoria")
    @Size(max = 500, message = "La descrizione non può superare i 500 caratteri")
    private String descrizione;

    @NotNull(message = "La quantità è obbligatoria")
    @DecimalMin(value = "0.01", message = "La quantità deve essere maggiore di 0")
    @Digits(integer = 10, fraction = 3, message = "La quantità può avere massimo 10 cifre intere e 3 decimali")
    private BigDecimal quantita;

    @Size(max = 10, message = "L'unità di misura non può superare i 10 caratteri")
    private String unitaMisura;

    @NotNull(message = "Il prezzo unitario è obbligatorio")
    @DecimalMin(value = "0.00", message = "Il prezzo unitario deve essere maggiore o uguale a 0")
    @Digits(integer = 10, fraction = 4, message = "Il prezzo unitario può avere massimo 10 cifre intere e 4 decimali")
    private BigDecimal prezzoUnitario;

    @DecimalMin(value = "0.00", message = "Lo sconto deve essere maggiore o uguale a 0")
    @DecimalMax(value = "100.00", message = "Lo sconto non può superare il 100%")
    @Digits(integer = 3, fraction = 2, message = "Lo sconto può avere massimo 3 cifre intere e 2 decimali")
    private BigDecimal scontoPercentuale;

    @DecimalMin(value = "0.00", message = "Lo sconto deve essere maggiore o uguale a 0")
    @Digits(integer = 10, fraction = 4, message = "Lo sconto può avere massimo 10 cifre intere e 4 decimali")
    private BigDecimal scontoImporto;

    @NotNull(message = "L'aliquota IVA è obbligatoria")
    @DecimalMin(value = "0.00", message = "L'aliquota IVA deve essere maggiore o uguale a 0")
    @DecimalMax(value = "100.00", message = "L'aliquota IVA non può superare il 100%")
    @Digits(integer = 2, fraction = 2, message = "L'aliquota IVA può avere massimo 2 cifre intere e 2 decimali")
    private BigDecimal aliquotaIva;

    @Size(max = 255, message = "Le note non possono superare i 255 caratteri")
    private String note;

    @Min(value = 1, message = "L'ordine deve essere maggiore di 0")
    private Integer ordine;

    // ==================== GETTERS AND SETTERS ====================
    public Long getArticoloId() { return articoloId; }
    public void setArticoloId(Long articoloId) { this.articoloId = articoloId; }

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

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public Integer getOrdine() { return ordine; }
    public void setOrdine(Integer ordine) { this.ordine = ordine; }
}
