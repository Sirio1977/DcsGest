package com.gestionale.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO per la risposta contenente i dati completi di un documento
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentoResponseDto {

    private Long id;
    
    private String tipoDocumento;
    
    private Long numero;
    
    private Integer anno;
    
    private LocalDate dataDocumento;
    
    private LocalDate dataRegistrazione;
    
    private String statoDocumento;
    
    // Dati del soggetto
    private Long soggettoId;
    private String soggettoRagioneSociale;
    private String soggettoCodiceFiscale;
    private String soggettoPartitaIva;
    private String soggettoIndirizzo;
    private String soggettoCap;
    private String soggettoCitta;
    private String soggettoProvincia;
    private String soggettoNazione;
    
    // Righe del documento
    private List<RigaDocumentoDto> righe;
    
    // Scadenze di pagamento
    private List<ScadenzaDto> scadenze;
    
    // Riepiloghi IVA
    private List<RiepilogoIvaDto> riepiloghiIva;
    
    // Totali documento
    private BigDecimal totaleImponibile;
    private BigDecimal totaleImposta;
    private BigDecimal totaleDocumento;
    
    // Modalità di pagamento
    private String modalitaPagamento;
    
    // Note
    private String note;
    private String noteInterne;
    
    // Causale trasporto (per DDT)
    private String causaleTrasporto;
    
    // Riferimenti esterni
    private String riferimentoOrdine;
    private LocalDate dataOrdine;
    
    // Informazioni aggiuntive
    private Long agenteId;
    private String agenteNome;
    private Long listinoId;
    private String listinoNome;
    
    // Tracciamento
    private LocalDateTime dataCreazione;
    private LocalDateTime dataModifica;
    private String creatoUtenteId;
    private String modificatoUtenteId;
}
