package com.gestionale.validation;

import com.gestionale.entity.Documento;
import com.gestionale.entity.TipoDocumento;
import com.gestionale.exception.DocumentoFiscalValidationException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * Validatore per i documenti secondo le normative fiscali italiane
 */
@Component
public class DocumentoFiscalValidator {

    private static final Pattern PARTITA_IVA_PATTERN = Pattern.compile("^[0-9]{11}$");
    private static final Pattern CODICE_FISCALE_PATTERN = Pattern.compile("^[A-Z]{6}[0-9]{2}[A-Z][0-9]{2}[A-Z][0-9]{3}[A-Z]$");

    /**
     * Valida un documento secondo le normative fiscali italiane
     */
    public void validateDocumento(Documento documento) {
        switch (documento.getTipoDocumento()) {
            case FATTURA:
                validateFattura(documento);
                break;
            case NOTA_CREDITO:
                validateNotaCredito(documento);
                break;
            case NOTA_DEBITO:
                validateNotaDebito(documento);
                break;
            case DDT:
                validateDdt(documento);
                break;
            case PREVENTIVO:
                validatePreventivo(documento);
                break;
            default:
                throw new DocumentoFiscalValidationException("Tipo documento non supportato: " + documento.getTipoDocumento());
        }
    }

    /**
     * Valida una fattura tradizionale
     */
    private void validateFattura(Documento fattura) {
        // Validazioni comuni
        validateDocumentoComune(fattura);
        
        // Validazioni specifiche fattura
        if (fattura.getRighe() == null || fattura.getRighe().isEmpty()) {
            throw new DocumentoFiscalValidationException("La fattura deve avere almeno una riga");
        }
        
        if (fattura.getTotaleDocumento() == null || fattura.getTotaleDocumento().compareTo(BigDecimal.ZERO) <= 0) {
            throw new DocumentoFiscalValidationException("Il totale della fattura deve essere positivo");
        }
        
        // Validazione dati fiscali cliente
        validateDatiFiscaliCliente(fattura);
        
        // Validazione numerazione
        validateNumerazione(fattura);
    }

    /**
     * Valida una fattura elettronica
     */
    private void validateFatturaElettronica(Documento fattura) {
        validateFattura(fattura);
        
        // Validazioni aggiuntive per fattura elettronica
        if (StringUtils.isBlank(fattura.getCodiceDestinatario()) && 
            StringUtils.isBlank(fattura.getPecDestinatario())) {
            throw new DocumentoFiscalValidationException(
                "Per la fattura elettronica è obbligatorio il codice destinatario o la PEC");
        }
        
        // Validazione formato codice destinatario
        if (StringUtils.isNotBlank(fattura.getCodiceDestinatario()) && 
            fattura.getCodiceDestinatario().length() != 7) {
            throw new DocumentoFiscalValidationException(
                "Il codice destinatario deve essere di 7 caratteri");
        }
        
        // Validazione formato PEC
        if (StringUtils.isNotBlank(fattura.getPecDestinatario()) && 
            !isValidEmail(fattura.getPecDestinatario())) {
            throw new DocumentoFiscalValidationException(
                "Formato PEC non valido");
        }
    }

    /**
     * Valida una nota di credito
     */
    private void validateNotaCredito(Documento notaCredito) {
        validateDocumentoComune(notaCredito);
        
        // Deve riferire una fattura origine
        if (notaCredito.getDocumentoOrigine() == null) {
            throw new DocumentoFiscalValidationException(
                "La nota di credito deve riferire una fattura origine");
        }
        
        // La fattura origine deve essere una fattura
        if (notaCredito.getDocumentoOrigine().getTipoDocumento() != TipoDocumento.FATTURA &&
            notaCredito.getDocumentoOrigine().getTipoDocumento() != TipoDocumento.FATTURA_ELETTRONICA) {
            throw new DocumentoFiscalValidationException(
                "La nota di credito può riferire solo fatture");
        }
        
        // Motivo della nota obbligatorio
        if (StringUtils.isBlank(notaCredito.getMotivoNota())) {
            throw new DocumentoFiscalValidationException(
                "Il motivo della nota di credito è obbligatorio");
        }
    }

    /**
     * Valida una nota di debito
     */
    private void validateNotaDebito(Documento notaDebito) {
        validateDocumentoComune(notaDebito);
        
        // Motivo della nota obbligatorio
        if (StringUtils.isBlank(notaDebito.getMotivoNota())) {
            throw new DocumentoFiscalValidationException(
                "Il motivo della nota di debito è obbligatorio");
        }
    }

    /**
     * Valida un DDT
     */
    private void validateDdt(Documento ddt) {
        validateDocumentoComune(ddt);
        
        // Causale trasporto obbligatoria
        if (StringUtils.isBlank(ddt.getCausaleDescrizione())) {
            throw new DocumentoFiscalValidationException(
                "La causale di trasporto è obbligatoria per il DDT");
        }
        
        // Controlli specifici per trasporto
        if (ddt.getDataTrasporto() == null) {
            throw new DocumentoFiscalValidationException(
                "La data di trasporto è obbligatoria per il DDT");
        }
    }

    /**
     * Valida un preventivo
     */
    private void validatePreventivo(Documento preventivo) {
        validateDocumentoComune(preventivo);
        
        // Validità offerta obbligatoria
        if (preventivo.getValiditaOfferta() == null) {
            throw new DocumentoFiscalValidationException(
                "La validità dell'offerta è obbligatoria per il preventivo");
        }
        
        // Validità offerta non può essere precedente alla data documento
        if (preventivo.getValiditaOfferta().isBefore(preventivo.getDataDocumento())) {
            throw new DocumentoFiscalValidationException(
                "La validità dell'offerta non può essere precedente alla data documento");
        }
    }

    /**
     * Validazioni comuni a tutti i documenti
     */
    private void validateDocumentoComune(Documento documento) {
        if (documento.getDataDocumento() == null) {
            throw new DocumentoFiscalValidationException("La data documento è obbligatoria");
        }
        
        if (documento.getCliente() == null) {
            throw new DocumentoFiscalValidationException("Il cliente è obbligatorio");
        }
        
        if (StringUtils.isBlank(documento.getTitolo())) {
            throw new DocumentoFiscalValidationException("Il titolo del documento è obbligatorio");
        }
    }

    /**
     * Valida i dati fiscali del cliente
     */
    private void validateDatiFiscaliCliente(Documento documento) {
        String partitaIva = documento.getCliente().getPartitaIva();
        String codiceFiscale = documento.getCliente().getCodiceFiscale();
        
        if (StringUtils.isBlank(partitaIva) && StringUtils.isBlank(codiceFiscale)) {
            throw new DocumentoFiscalValidationException(
                "Il cliente deve avere almeno partita IVA o codice fiscale");
        }
        
        if (StringUtils.isNotBlank(partitaIva) && !isValidPartitaIva(partitaIva)) {
            throw new DocumentoFiscalValidationException(
                "Formato partita IVA non valido: " + partitaIva);
        }
        
        if (StringUtils.isNotBlank(codiceFiscale) && !isValidCodiceFiscale(codiceFiscale)) {
            throw new DocumentoFiscalValidationException(
                "Formato codice fiscale non valido: " + codiceFiscale);
        }
    }

    /**
     * Valida la numerazione del documento
     */
    private void validateNumerazione(Documento documento) {
        if (documento.getNumero() == null || documento.getNumero() <= 0) {
            throw new DocumentoFiscalValidationException(
                "Il numero del documento deve essere positivo");
        }
        
        if (documento.getAnno() == null || documento.getAnno() < 1900 || documento.getAnno() > 2100) {
            throw new DocumentoFiscalValidationException(
                "L'anno del documento non è valido");
        }
    }

    /**
     * Valida formato partita IVA
     */
    private boolean isValidPartitaIva(String partitaIva) {
        return PARTITA_IVA_PATTERN.matcher(partitaIva).matches();
    }

    /**
     * Valida formato codice fiscale
     */
    private boolean isValidCodiceFiscale(String codiceFiscale) {
        return CODICE_FISCALE_PATTERN.matcher(codiceFiscale.toUpperCase()).matches();
    }

    /**
     * Valida formato email
     */
    private boolean isValidEmail(String email) {
        return StringUtils.isNotBlank(email) && 
               email.contains("@") && 
               email.contains(".");
    }
}
