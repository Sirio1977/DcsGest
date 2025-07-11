package com.gestionale.entity;

/**
 * Enum per i tipi di documento supportati
 */
public enum TipoDocumento {
    PREVENTIVO("Preventivo", "PV", false, false),
    ORDINE("Ordine", "OR", false, false),
    DDT("Documento di Trasporto", "DDT", false, false),
    FATTURA("Fattura", "FT", true, true),
    FATTURA_ELETTRONICA("Fattura Elettronica", "FE", true, true),
    NOTA_CREDITO("Nota di Credito", "NC", true, false),
    NOTA_DEBITO("Nota di Debito", "ND", true, true),
    RICEVUTA("Ricevuta", "RC", true, false);

    private final String descrizione;
    private final String codice;
    private final boolean fiscale;
    private final boolean generaScadenze;

    TipoDocumento(String descrizione, String codice, boolean fiscale, boolean generaScadenze) {
        this.descrizione = descrizione;
        this.codice = codice;
        this.fiscale = fiscale;
        this.generaScadenze = generaScadenze;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public String getCodice() {
        return codice;
    }

    public boolean isFiscale() {
        return fiscale;
    }

    public boolean isGeneraScadenze() {
        return generaScadenze;
    }

    /**
     * Verifica se il tipo documento può essere stampato
     */
    public boolean isStampabile() {
        return true; // Tutti i documenti sono stampabili
    }

    /**
     * Verifica se il tipo documento può essere inviato
     */
    public boolean isInviabile() {
        return fiscale; // Solo documenti fiscali possono essere inviati
    }

    /**
     * Verifica se il tipo documento richiede validazioni fiscali
     */
    public boolean richiedeValidazioniFiscali() {
        return fiscale;
    }
}
