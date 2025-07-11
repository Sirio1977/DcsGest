package com.gestionale.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Min;

/**
 * DTO per la creazione di una modalità di pagamento
 */
public class ModalitaPagamentoDto {

    private Long id;

    @NotBlank(message = "Codice modalità pagamento obbligatorio")
    private String codice;

    @NotBlank(message = "Descrizione modalità pagamento obbligatoria")
    private String descrizione;

    @Min(value = 0, message = "Giorni pagamento deve essere maggiore o uguale a 0")
    private Integer giorniPagamento;

    private Boolean fineMese;

    @NotNull(message = "Numero rate obbligatorio")
    @Min(value = 1, message = "Numero rate deve essere maggiore di 0")
    private Integer numeroRate;

    @Min(value = 0, message = "Giorni tra rate deve essere maggiore o uguale a 0")
    private Integer giorniTraRate;

    private Boolean attiva;

    // Costruttori
    public ModalitaPagamentoDto() {
    }

    public ModalitaPagamentoDto(Long id, String codice, String descrizione, Integer giorniPagamento, Boolean fineMese, Integer numeroRate, Integer giorniTraRate, Boolean attiva) {
        this.id = id;
        this.codice = codice;
        this.descrizione = descrizione;
        this.giorniPagamento = giorniPagamento;
        this.fineMese = fineMese;
        this.numeroRate = numeroRate;
        this.giorniTraRate = giorniTraRate;
        this.attiva = attiva;
    }

    /**
     * Costruttore per nuova modalità di pagamento
     */
    public ModalitaPagamentoDto(String codice, String descrizione, Integer giorniPagamento) {
        this.codice = codice;
        this.descrizione = descrizione;
        this.giorniPagamento = giorniPagamento;
        this.fineMese = false;
        this.numeroRate = 1;
        this.attiva = true;
    }

    // Getter e Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodice() {
        return codice;
    }

    public void setCodice(String codice) {
        this.codice = codice;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public Integer getGiorniPagamento() {
        return giorniPagamento;
    }

    public void setGiorniPagamento(Integer giorniPagamento) {
        this.giorniPagamento = giorniPagamento;
    }

    public Boolean getFineMese() {
        return fineMese;
    }

    public void setFineMese(Boolean fineMese) {
        this.fineMese = fineMese;
    }

    public Integer getNumeroRate() {
        return numeroRate;
    }

    public void setNumeroRate(Integer numeroRate) {
        this.numeroRate = numeroRate;
    }

    public Integer getGiorniTraRate() {
        return giorniTraRate;
    }

    public void setGiorniTraRate(Integer giorniTraRate) {
        this.giorniTraRate = giorniTraRate;
    }

    public Boolean getAttiva() {
        return attiva;
    }

    public void setAttiva(Boolean attiva) {
        this.attiva = attiva;
    }
}
