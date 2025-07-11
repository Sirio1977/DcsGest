package com.gestionale.dto;

import com.gestionale.entity.StatoDocumento;
import com.gestionale.entity.TipoDocumento;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO per filtrare i documenti
 */
@Data
public class DocumentoFilter {
    
    private TipoDocumento tipoDocumento;
    private StatoDocumento statoDocumento;
    private LocalDate dataInizio;
    private LocalDate dataFine;
    private String soggettoFilter;
    private Long soggettoId;
    private String numeroDocumento;
    private Boolean inviato;
    private Boolean stampato;
    private String partitaIva;
    private String codiceFiscale;

    // Builder pattern implementation
    public static DocumentoFilterBuilder builder() {
        return new DocumentoFilterBuilder();
    }

    public static class DocumentoFilterBuilder {
        private TipoDocumento tipoDocumento;
        private StatoDocumento statoDocumento;
        private LocalDate dataInizio;
        private LocalDate dataFine;
        private String soggettoFilter;
        private Long soggettoId;
        private String numeroDocumento;
        private Boolean inviato;
        private Boolean stampato;
        private String partitaIva;
        private String codiceFiscale;

        public DocumentoFilterBuilder tipoDocumento(TipoDocumento tipoDocumento) {
            this.tipoDocumento = tipoDocumento;
            return this;
        }

        public DocumentoFilterBuilder statoDocumento(StatoDocumento statoDocumento) {
            this.statoDocumento = statoDocumento;
            return this;
        }

        public DocumentoFilterBuilder dataInizio(LocalDate dataInizio) {
            this.dataInizio = dataInizio;
            return this;
        }

        public DocumentoFilterBuilder dataFine(LocalDate dataFine) {
            this.dataFine = dataFine;
            return this;
        }

        public DocumentoFilterBuilder soggettoFilter(String soggettoFilter) {
            this.soggettoFilter = soggettoFilter;
            return this;
        }

        public DocumentoFilterBuilder soggettoId(Long soggettoId) {
            this.soggettoId = soggettoId;
            return this;
        }

        public DocumentoFilterBuilder numeroDocumento(String numeroDocumento) {
            this.numeroDocumento = numeroDocumento;
            return this;
        }

        public DocumentoFilterBuilder inviato(Boolean inviato) {
            this.inviato = inviato;
            return this;
        }

        public DocumentoFilterBuilder stampato(Boolean stampato) {
            this.stampato = stampato;
            return this;
        }

        public DocumentoFilterBuilder partitaIva(String partitaIva) {
            this.partitaIva = partitaIva;
            return this;
        }

        public DocumentoFilterBuilder codiceFiscale(String codiceFiscale) {
            this.codiceFiscale = codiceFiscale;
            return this;
        }

        public DocumentoFilter build() {
            DocumentoFilter filter = new DocumentoFilter();
            filter.tipoDocumento = this.tipoDocumento;
            filter.statoDocumento = this.statoDocumento;
            filter.dataInizio = this.dataInizio;
            filter.dataFine = this.dataFine;
            filter.soggettoFilter = this.soggettoFilter;
            filter.soggettoId = this.soggettoId;
            filter.numeroDocumento = this.numeroDocumento;
            filter.inviato = this.inviato;
            filter.stampato = this.stampato;
            filter.partitaIva = this.partitaIva;
            filter.codiceFiscale = this.codiceFiscale;
            return filter;
        }
    }

    // Costruttori
    public DocumentoFilter() {
    }

    public DocumentoFilter(TipoDocumento tipoDocumento, StatoDocumento statoDocumento, LocalDate dataInizio, LocalDate dataFine, String soggettoFilter, Long soggettoId, String numeroDocumento, Boolean inviato, Boolean stampato, String partitaIva, String codiceFiscale) {
        this.tipoDocumento = tipoDocumento;
        this.statoDocumento = statoDocumento;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.soggettoFilter = soggettoFilter;
        this.soggettoId = soggettoId;
        this.numeroDocumento = numeroDocumento;
        this.inviato = inviato;
        this.stampato = stampato;
        this.partitaIva = partitaIva;
        this.codiceFiscale = codiceFiscale;
    }

    // Getter e Setter
    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public StatoDocumento getStatoDocumento() {
        return statoDocumento;
    }

    public void setStatoDocumento(StatoDocumento statoDocumento) {
        this.statoDocumento = statoDocumento;
    }

    public LocalDate getDataInizio() {
        return dataInizio;
    }

    public void setDataInizio(LocalDate dataInizio) {
        this.dataInizio = dataInizio;
    }

    public LocalDate getDataFine() {
        return dataFine;
    }

    public void setDataFine(LocalDate dataFine) {
        this.dataFine = dataFine;
    }

    public String getSoggettoFilter() {
        return soggettoFilter;
    }

    public void setSoggettoFilter(String soggettoFilter) {
        this.soggettoFilter = soggettoFilter;
    }

    public Long getSoggettoId() {
        return soggettoId;
    }

    public void setSoggettoId(Long soggettoId) {
        this.soggettoId = soggettoId;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public Boolean getInviato() {
        return inviato;
    }

    public void setInviato(Boolean inviato) {
        this.inviato = inviato;
    }

    public Boolean getStampato() {
        return stampato;
    }

    public void setStampato(Boolean stampato) {
        this.stampato = stampato;
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
}
