package com.gestionale.dto;

import javax.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO per le scadenze documento
 */
@Data
public class ScadenzaDto {

    @NotNull(message = "La data scadenza è obbligatoria")
    private LocalDate dataScadenza;

    @NotNull(message = "L'importo è obbligatorio")
    @DecimalMin(value = "0.01", message = "L'importo deve essere maggiore di 0")
    @Digits(integer = 10, fraction = 2, message = "L'importo può avere massimo 10 cifre intere e 2 decimali")
    private BigDecimal importo;

    @NotBlank(message = "Il tipo pagamento è obbligatorio")
    @Size(max = 50, message = "Il tipo pagamento non può superare i 50 caratteri")
    private String tipoPagamento;

    @Size(max = 255, message = "Le note non possono superare i 255 caratteri")
    private String note;

    private Boolean pagata = false;

    private LocalDate dataPagamento;

    @DecimalMin(value = "0.00", message = "L'importo pagato deve essere maggiore o uguale a 0")
    @Digits(integer = 10, fraction = 2, message = "L'importo pagato può avere massimo 10 cifre intere e 2 decimali")
    private BigDecimal importoPagato;

    @Size(max = 255, message = "Le note pagamento non possono superare i 255 caratteri")
    private String notePagamento;

    @Min(value = 1, message = "L'ordine deve essere maggiore di 0")
    private Integer ordine;

    // ==================== GETTERS AND SETTERS ====================
    public LocalDate getDataScadenza() { return dataScadenza; }
    public void setDataScadenza(LocalDate dataScadenza) { this.dataScadenza = dataScadenza; }

    public BigDecimal getImporto() { return importo; }
    public void setImporto(BigDecimal importo) { this.importo = importo; }

    public String getTipoPagamento() { return tipoPagamento; }
    public void setTipoPagamento(String tipoPagamento) { this.tipoPagamento = tipoPagamento; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public Boolean getPagata() { return pagata; }
    public void setPagata(Boolean pagata) { this.pagata = pagata; }

    public LocalDate getDataPagamento() { return dataPagamento; }
    public void setDataPagamento(LocalDate dataPagamento) { this.dataPagamento = dataPagamento; }

    public BigDecimal getImportoPagato() { return importoPagato; }
    public void setImportoPagato(BigDecimal importoPagato) { this.importoPagato = importoPagato; }

    public String getNotePagamento() { return notePagamento; }
    public void setNotePagamento(String notePagamento) { this.notePagamento = notePagamento; }

    public Integer getOrdine() { return ordine; }
    public void setOrdine(Integer ordine) { this.ordine = ordine; }
}
