package com.gestionale.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO per la risposta delle scadenze
 */
public class ScadenzaResponseDto {

    private Long id;
    private LocalDate dataScadenza;
    private BigDecimal importo;
    private String tipoPagamento;
    private String note;
    private Boolean pagata;
    private LocalDate dataPagamento;
    private BigDecimal importoPagato;
    private String notePagamento;
    private Integer ordine;

    // Getter e Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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
