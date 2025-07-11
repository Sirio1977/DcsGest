package com.gestionale.dto;

import javax.validation.constraints.NotBlank;

/**
 * DTO per la creazione di una causale di trasporto
 */
public class CausaleTrasportoDto {

    private Long id;

    @NotBlank(message = "Codice causale trasporto obbligatorio")
    private String codice;

    @NotBlank(message = "Descrizione causale trasporto obbligatoria")
    private String descrizione;

    private Boolean attiva;

    // Default constructor
    public CausaleTrasportoDto() {
    }

    // All-args constructor
    public CausaleTrasportoDto(Long id, String codice, String descrizione, Boolean attiva) {
        this.id = id;
        this.codice = codice;
        this.descrizione = descrizione;
        this.attiva = attiva;
    }

    // Constructor for new causale di trasporto
    public CausaleTrasportoDto(String codice, String descrizione) {
        this.codice = codice;
        this.descrizione = descrizione;
        this.attiva = true;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getCodice() { return codice; }
    public void setCodice(String codice) { this.codice = codice; }
    
    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }
    
    public Boolean getAttiva() { return attiva; }
    public void setAttiva(Boolean attiva) { this.attiva = attiva; }
}
