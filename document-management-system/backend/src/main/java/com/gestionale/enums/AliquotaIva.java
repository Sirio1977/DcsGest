package com.gestionale.enums;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Enum che rappresenta le aliquote IVA italiane standard
 */
public enum AliquotaIva {
    
    ZERO(0.0, "0%", "Operazioni esenti o non soggette", "N1"),
    QUATTRO(4.0, "4%", "Aliquota ridotta - beni di prima necessità", null),
    CINQUE(5.0, "5%", "Aliquota ridotta - beni specifici", null),
    DIECI(10.0, "10%", "Aliquota ridotta - servizi turistici, alimentari", null),
    VENTIDUE(22.0, "22%", "Aliquota ordinaria", null);
    
    private final Double percentuale;
    private final String codice;
    private final String descrizione;
    private final String natura; // Natura IVA per operazioni esenti
    
    AliquotaIva(Double percentuale, String codice, String descrizione, String natura) {
        this.percentuale = percentuale;
        this.codice = codice;
        this.descrizione = descrizione;
        this.natura = natura;
    }
    
    public Double getPercentuale() {
        return percentuale;
    }
    
    public String getCodice() {
        return codice;
    }
    
    public String getDescrizione() {
        return descrizione;
    }
    
    public String getNatura() {
        return natura;
    }
    
    /**
     * Calcola l'importo IVA per un dato imponibile (BigDecimal)
     */
    public BigDecimal calcolaImposta(BigDecimal imponibile) {
        if (imponibile == null || imponibile.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal percentualeBd = BigDecimal.valueOf(percentuale);
        return imponibile.multiply(percentualeBd.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP))
                .setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Calcola l'importo IVA per un dato imponibile (Double)
     */
    public Double calcolaIva(Double imponibile) {
        if (imponibile == null || imponibile <= 0) {
            return 0.0;
        }
        return imponibile * (percentuale / 100);
    }
    
    /**
     * Calcola il totale (imponibile + IVA) per un dato imponibile
     */
    public Double calcolaTotale(Double imponibile) {
        if (imponibile == null || imponibile <= 0) {
            return 0.0;
        }
        return imponibile + calcolaIva(imponibile);
    }
    
    /**
     * Calcola il totale (imponibile + IVA) per un dato imponibile (BigDecimal)
     */
    public BigDecimal calcolaTotale(BigDecimal imponibile) {
        if (imponibile == null || imponibile.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return imponibile.add(calcolaImposta(imponibile));
    }
    
    /**
     * Trova un'aliquota per percentuale
     */
    public static AliquotaIva byPercentuale(Double percentuale) {
        if (percentuale == null) {
            return ZERO;
        }
        
        for (AliquotaIva aliquota : values()) {
            if (aliquota.percentuale.equals(percentuale)) {
                return aliquota;
            }
        }
        
        // Se non trova la percentuale esatta, ritorna l'aliquota ordinaria
        return VENTIDUE;
    }
    
    @Override
    public String toString() {
        return codice + " - " + descrizione;
    }
}
