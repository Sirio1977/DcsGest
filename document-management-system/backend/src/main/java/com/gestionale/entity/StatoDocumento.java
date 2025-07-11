package com.gestionale.entity;

/**
 * Enum per gli stati del documento
 */
public enum StatoDocumento {
    BOZZA("Bozza", "Il documento è in fase di creazione"),
    EMESSO("Emesso", "Il documento è stato emesso"),
    STAMPATO("Stampato", "Il documento è stato stampato"),
    INVIATO("Inviato", "Il documento è stato inviato"),
    PAGATO("Pagato", "Il documento è stato pagato"),
    ANNULLATO("Annullato", "Il documento è stato annullato");

    private final String descrizione;
    private final String dettaglio;

    StatoDocumento(String descrizione, String dettaglio) {
        this.descrizione = descrizione;
        this.dettaglio = dettaglio;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public String getDettaglio() {
        return dettaglio;
    }

    /**
     * Verifica se lo stato consente modifiche
     */
    public boolean isModificabile() {
        return this == BOZZA;
    }

    /**
     * Verifica se lo stato consente eliminazione
     */
    public boolean isEliminabile() {
        return this == BOZZA || this == ANNULLATO;
    }

    /**
     * Verifica se lo stato consente stampa
     */
    public boolean isStampabile() {
        return this != ANNULLATO;
    }

    /**
     * Verifica se lo stato consente invio
     */
    public boolean isInviabile() {
        return this == EMESSO || this == STAMPATO;
    }

    /**
     * Ottiene il prossimo stato logico
     */
    public StatoDocumento nextStato() {
        switch (this) {
            case BOZZA:
                return EMESSO;
            case EMESSO:
                return STAMPATO;
            case STAMPATO:
                return INVIATO;
            case INVIATO:
                return PAGATO;
            default:
                return this;
        }
    }
}
