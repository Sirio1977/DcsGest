package com.gestionale.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Email;

/**
 * DTO per la creazione di un fornitore
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FornitoreDto {

    private Long id;

    @NotBlank(message = "Ragione sociale obbligatoria")
    private String ragioneSociale;

    private String partitaIva;

    private String codiceFiscale;

    private String indirizzo;

    private String citta;

    private String cap;

    private String provincia;

    private String telefono;

    @Email(message = "Email non valida")
    private String email;

    @Email(message = "PEC non valida")
    private String pec;

    private Boolean attivo;

    private String note;

    /**
     * Costruttore per nuovo fornitore
     */
    public FornitoreDto(String ragioneSociale, String partitaIva) {
        this.ragioneSociale = ragioneSociale;
        this.partitaIva = partitaIva;
        this.attivo = true;
    }
}
