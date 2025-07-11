package com.gestionale.controller;

import com.gestionale.dto.*;
import com.gestionale.entity.StatoDocumento;
import com.gestionale.entity.TipoDocumento;
import com.gestionale.service.DocumentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

/**
 * Controller REST per la gestione dei documenti
 */
@RestController
@RequestMapping("/api/documenti")
@CrossOrigin(origins = "*")
public class DocumentoController {

    @Autowired
    private DocumentoService documentoService;

    /**
     * Crea un nuovo documento
     */
    @PostMapping
    public ResponseEntity<DocumentoResponseDto> creaDocumento(@Valid @RequestBody DocumentoCreateDto createDto) {
        DocumentoResponseDto documento = documentoService.creaDocumento(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(documento);
    }

    /**
     * Recupera un documento per ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<DocumentoResponseDto> getDocumento(@PathVariable Long id) {
        DocumentoResponseDto documento = documentoService.getDocumento(id);
        return ResponseEntity.ok(documento);
    }

    /**
     * Recupera documenti con filtri e paginazione
     */
    @GetMapping
    public ResponseEntity<Page<DocumentoResponseDto>> getDocumenti(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "dataDocumento") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) TipoDocumento tipoDocumento,
            @RequestParam(required = false) StatoDocumento statoDocumento,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInizio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFine,
            @RequestParam(required = false) String soggettoFilter,
            @RequestParam(required = false) Long soggettoId,
            @RequestParam(required = false) String numeroDocumento,
            @RequestParam(required = false) Boolean inviato,
            @RequestParam(required = false) Boolean stampato,
            @RequestParam(required = false) String partitaIva,
            @RequestParam(required = false) String codiceFiscale) {

        // Crea filtro
        DocumentoFilter filter = DocumentoFilter.builder()
                .tipoDocumento(tipoDocumento)
                .statoDocumento(statoDocumento)
                .dataInizio(dataInizio)
                .dataFine(dataFine)
                .soggettoFilter(soggettoFilter)
                .soggettoId(soggettoId)
                .numeroDocumento(numeroDocumento)
                .inviato(inviato)
                .stampato(stampato)
                .partitaIva(partitaIva)
                .codiceFiscale(codiceFiscale)
                .build();

        // Crea paginazione
        Sort sort = Sort.by(sortDir.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<DocumentoResponseDto> documenti = documentoService.getDocumenti(filter, pageable);
        return ResponseEntity.ok(documenti);
    }

    /**
     * Aggiorna un documento esistente
     */
    @PutMapping("/{id}")
    public ResponseEntity<DocumentoResponseDto> aggiornaDocumento(
            @PathVariable Long id,
            @Valid @RequestBody DocumentoCreateDto updateDto) {
        DocumentoResponseDto documento = documentoService.aggiornaDocumento(id, updateDto);
        return ResponseEntity.ok(documento);
    }

    /**
     * Elimina un documento
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminaDocumento(@PathVariable Long id) {
        documentoService.eliminaDocumento(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Cambia lo stato di un documento
     */
    @PatchMapping("/{id}/stato")
    public ResponseEntity<DocumentoResponseDto> cambiaStato(
            @PathVariable Long id,
            @RequestParam StatoDocumento nuovoStato) {
        DocumentoResponseDto documento = documentoService.cambiaStato(id, nuovoStato);
        return ResponseEntity.ok(documento);
    }

    /**
     * Duplica un documento
     */
    @PostMapping("/{id}/duplica")
    public ResponseEntity<DocumentoResponseDto> duplicaDocumento(
            @PathVariable Long id,
            @RequestParam TipoDocumento nuovoTipo) {
        DocumentoResponseDto documento = documentoService.duplicaDocumento(id, nuovoTipo);
        return ResponseEntity.status(HttpStatus.CREATED).body(documento);
    }

    /**
     * Genera PDF del documento
     */
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> generaPdf(@PathVariable Long id) {
        byte[] pdfData = documentoService.generaPdf(id);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(org.springframework.http.ContentDisposition.attachment()
                .filename("documento_" + id + ".pdf")
                .build());
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfData);
    }

    /**
     * Invia documento via email
     */
    @PostMapping("/{id}/invia")
    public ResponseEntity<DocumentoResponseDto> inviaDocumento(
            @PathVariable Long id,
            @RequestParam String emailDestinatario) {
        DocumentoResponseDto documento = documentoService.inviaDocumento(id, emailDestinatario);
        return ResponseEntity.ok(documento);
    }

    // ==================== ENDPOINT SPECIFICI PER TIPO DOCUMENTO ====================

    /**
     * Crea una nuova fattura
     */
    @PostMapping("/fatture")
    public ResponseEntity<DocumentoResponseDto> creaFattura(@Valid @RequestBody DocumentoCreateDto createDto) {
        createDto.setTipoDocumento(TipoDocumento.FATTURA);
        DocumentoResponseDto documento = documentoService.creaDocumento(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(documento);
    }

    /**
     * Recupera solo le fatture
     */
    @GetMapping("/fatture")
    public ResponseEntity<Page<DocumentoResponseDto>> getFatture(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "dataDocumento") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) StatoDocumento statoDocumento,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInizio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFine) {

        DocumentoFilter filter = DocumentoFilter.builder()
                .tipoDocumento(TipoDocumento.FATTURA)
                .statoDocumento(statoDocumento)
                .dataInizio(dataInizio)
                .dataFine(dataFine)
                .build();

        Sort sort = Sort.by(sortDir.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<DocumentoResponseDto> fatture = documentoService.getDocumenti(filter, pageable);
        return ResponseEntity.ok(fatture);
    }

    /**
     * Crea un nuovo preventivo
     */
    @PostMapping("/preventivi")
    public ResponseEntity<DocumentoResponseDto> creaPreventivo(@Valid @RequestBody DocumentoCreateDto createDto) {
        createDto.setTipoDocumento(TipoDocumento.PREVENTIVO);
        DocumentoResponseDto documento = documentoService.creaDocumento(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(documento);
    }

    /**
     * Recupera solo i preventivi
     */
    @GetMapping("/preventivi")
    public ResponseEntity<Page<DocumentoResponseDto>> getPreventivi(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "dataDocumento") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) StatoDocumento statoDocumento,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInizio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFine) {

        DocumentoFilter filter = DocumentoFilter.builder()
                .tipoDocumento(TipoDocumento.PREVENTIVO)
                .statoDocumento(statoDocumento)
                .dataInizio(dataInizio)
                .dataFine(dataFine)
                .build();

        Sort sort = Sort.by(sortDir.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<DocumentoResponseDto> preventivi = documentoService.getDocumenti(filter, pageable);
        return ResponseEntity.ok(preventivi);
    }

    /**
     * Crea un nuovo DDT
     */
    @PostMapping("/ddt")
    public ResponseEntity<DocumentoResponseDto> creaDdt(@Valid @RequestBody DocumentoCreateDto createDto) {
        createDto.setTipoDocumento(TipoDocumento.DDT);
        DocumentoResponseDto documento = documentoService.creaDocumento(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(documento);
    }

    /**
     * Recupera solo i DDT
     */
    @GetMapping("/ddt")
    public ResponseEntity<Page<DocumentoResponseDto>> getDdt(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "dataDocumento") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) StatoDocumento statoDocumento,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInizio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFine) {

        DocumentoFilter filter = DocumentoFilter.builder()
                .tipoDocumento(TipoDocumento.DDT)
                .statoDocumento(statoDocumento)
                .dataInizio(dataInizio)
                .dataFine(dataFine)
                .build();

        Sort sort = Sort.by(sortDir.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<DocumentoResponseDto> ddt = documentoService.getDocumenti(filter, pageable);
        return ResponseEntity.ok(ddt);
    }

    /**
     * Crea una nuova nota di credito
     */
    @PostMapping("/note-credito")
    public ResponseEntity<DocumentoResponseDto> creaNotaCredito(@Valid @RequestBody DocumentoCreateDto createDto) {
        createDto.setTipoDocumento(TipoDocumento.NOTA_CREDITO);
        DocumentoResponseDto documento = documentoService.creaDocumento(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(documento);
    }

    /**
     * Recupera solo le note di credito
     */
    @GetMapping("/note-credito")
    public ResponseEntity<Page<DocumentoResponseDto>> getNoteCredito(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "dataDocumento") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) StatoDocumento statoDocumento,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInizio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFine) {

        DocumentoFilter filter = DocumentoFilter.builder()
                .tipoDocumento(TipoDocumento.NOTA_CREDITO)
                .statoDocumento(statoDocumento)
                .dataInizio(dataInizio)
                .dataFine(dataFine)
                .build();

        Sort sort = Sort.by(sortDir.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<DocumentoResponseDto> noteCredito = documentoService.getDocumenti(filter, pageable);
        return ResponseEntity.ok(noteCredito);
    }
}
