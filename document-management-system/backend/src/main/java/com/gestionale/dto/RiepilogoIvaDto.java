package com.gestionale.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO per il riepilogo IVA di un documento
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiepilogoIvaDto {

    private Long id;
    
    private String aliquotaIva;
    
    private BigDecimal percentuale;
    
    private String natura;
    
    private BigDecimal imponibile;
    
    private BigDecimal imposta;
    
    private String descrizione;
}
