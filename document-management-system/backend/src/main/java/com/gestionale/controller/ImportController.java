package com.gestionale.controller;

import com.gestionale.dto.ImportResultDto;
import com.gestionale.dto.ImportArticoliResponse;
import com.gestionale.dto.ImportFornitoriResponse;
import com.gestionale.dto.ImportArticoliFornitoriResponse;
import com.gestionale.service.ImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/import")
@CrossOrigin(origins = "*")
public class ImportController {

    @Autowired
    private ImportService importService;

    @PostMapping("/clienti/json")
    public ResponseEntity<ImportResultDto> importClientiFromJson(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "preview", defaultValue = "false") boolean preview) {
        
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ImportResultDto(false, "File vuoto", 0, 0, null));
            }

            ImportResultDto result = importService.importClientiFromJson(file, preview);
            
            if (result.isSuccess()) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body(result);
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ImportResultDto(false, "Errore durante l'importazione: " + e.getMessage(), 0, 0, null));
        }
    }

    @PostMapping("/clienti/validate")
    public ResponseEntity<ImportResultDto> validateClientiJson(
            @RequestParam("file") MultipartFile file) {
        
        try {
            ImportResultDto result = importService.validateClientiJson(file);
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ImportResultDto(false, "Errore durante la validazione: " + e.getMessage(), 0, 0, null));
        }
    }

    @GetMapping("/clienti/preview")
    public ResponseEntity<Map<String, Object>> previewClientiStructure() {
        try {
            Map<String, Object> structure = importService.getClientiStructure();
            return ResponseEntity.ok(structure);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/articoli/json")
    public ResponseEntity<ImportArticoliResponse> importArticoliFromJson(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "preview", defaultValue = "false") boolean preview) {
        
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ImportArticoliResponse(false, 0, 0, 0, null, "File vuoto"));
            }

            ImportArticoliResponse result = importService.importArticoliFromJson(file, preview);
            
            if (result.isSuccess()) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body(result);
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ImportArticoliResponse(false, 0, 0, 0, null, 
                     "Errore durante l'importazione: " + e.getMessage()));
        }
    }

    @PostMapping("/articoli/validate")
    public ResponseEntity<ImportArticoliResponse> validateArticoliJson(
            @RequestParam("file") MultipartFile file) {
        
        try {
            ImportArticoliResponse result = importService.validateArticoliJson(file);
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ImportArticoliResponse(false, 0, 0, 0, null,
                     "Errore durante la validazione: " + e.getMessage()));
        }
    }

    @GetMapping("/articoli/preview")
    public ResponseEntity<Map<String, Object>> previewArticoliStructure() {
        try {
            Map<String, Object> structure = importService.getArticoliStructure();
            return ResponseEntity.ok(structure);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Endpoints per fornitori
    @PostMapping("/fornitori/json")
    public ResponseEntity<ImportFornitoriResponse> importFornitoriFromJson(
            @RequestParam("file") MultipartFile file) {
        
        try {
            if (file.isEmpty()) {
                ImportFornitoriResponse response = new ImportFornitoriResponse();
                response.setSuccess(false);
                response.setMessage("File vuoto");
                return ResponseEntity.badRequest().body(response);
            }

            String content = new String(file.getBytes());
            var fornitori = importService.parseFornitori(content);
            ImportFornitoriResponse result = importService.importFornitori(fornitori);
            
            result.setSuccess(result.getFornitoriImportati() > 0);
            result.setMessage("Importazione completata");
            
            if (result.isSuccess()) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body(result);
            }
            
        } catch (Exception e) {
            ImportFornitoriResponse response = new ImportFornitoriResponse();
            response.setSuccess(false);
            response.setMessage("Errore durante l'importazione: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/fornitori/validate")
    public ResponseEntity<ImportFornitoriResponse> validateFornitoriJson(
            @RequestParam("file") MultipartFile file) {
        
        try {
            if (file.isEmpty()) {
                ImportFornitoriResponse response = new ImportFornitoriResponse();
                response.setSuccess(false);
                response.setMessage("File vuoto");
                return ResponseEntity.badRequest().body(response);
            }

            String content = new String(file.getBytes());
            var fornitori = importService.parseFornitori(content);
            ImportFornitoriResponse result = importService.validateFornitori(fornitori);
            
            result.setSuccess(result.getErrors().isEmpty());
            result.setMessage("Validazione completata");
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            ImportFornitoriResponse response = new ImportFornitoriResponse();
            response.setSuccess(false);
            response.setMessage("Errore durante la validazione: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/fornitori/preview")
    public ResponseEntity<Map<String, Object>> previewFornitoriJson(
            @RequestParam("file") MultipartFile file) {
        
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "File vuoto"));
            }

            String content = new String(file.getBytes());
            var fornitori = importService.parseFornitori(content);
            
            return ResponseEntity.ok(Map.of(
                "fornitori", fornitori.stream().limit(10).toList(), // Mostra solo i primi 10
                "total", fornitori.size()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Errore durante il preview: " + e.getMessage()));
        }
    }

    // ===================== ARTICOLI FORNITORI ENDPOINTS =====================
    
    @PostMapping("/articoli-fornitori/json")
    public ResponseEntity<ImportArticoliFornitoriResponse> importArticoliFornitoriFromJson(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "preview", defaultValue = "false") boolean preview) {
        
        try {
            if (file.isEmpty()) {
                ImportArticoliFornitoriResponse errorResponse = new ImportArticoliFornitoriResponse();
                errorResponse.aggiungiErrore("File vuoto");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            ImportArticoliFornitoriResponse result = importService.importArticoliFornitoriFromJson(file, preview);
            
            if (result.getErrori().isEmpty() || result.getArticoliImportati() > 0) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body(result);
            }
            
        } catch (Exception e) {
            ImportArticoliFornitoriResponse errorResponse = new ImportArticoliFornitoriResponse();
            errorResponse.aggiungiErrore("Errore durante l'importazione: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/articoli-fornitori/validate")
    public ResponseEntity<ImportArticoliFornitoriResponse> validateArticoliFornitoriJson(
            @RequestParam("file") MultipartFile file) {
        
        try {
            if (file.isEmpty()) {
                ImportArticoliFornitoriResponse errorResponse = new ImportArticoliFornitoriResponse();
                errorResponse.aggiungiErrore("File vuoto");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            ImportArticoliFornitoriResponse result = importService.validateArticoliFornitoriJson(file);
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            ImportArticoliFornitoriResponse errorResponse = new ImportArticoliFornitoriResponse();
            errorResponse.aggiungiErrore("Errore durante la validazione: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/articoli-fornitori/structure")
    public ResponseEntity<Map<String, Object>> getArticoliFornitoriStructure() {
        try {
            Map<String, Object> structure = importService.getArticoliFornitoriStructure();
            return ResponseEntity.ok(structure);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Errore durante il recupero della struttura: " + e.getMessage()));
        }
    }

    @PostMapping("/articoli-fornitori/preview")
    public ResponseEntity<Map<String, Object>> previewArticoliFornitori(
            @RequestParam("file") MultipartFile file) {
        
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "File vuoto"));
            }

            String jsonContent = new String(file.getBytes());
            var articoli = importService.parseArticoliFornitori(jsonContent);
            
            return ResponseEntity.ok(Map.of(
                "articoli", articoli.stream().limit(10).toList(), // Mostra solo i primi 10
                "total", articoli.size()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Errore durante il preview: " + e.getMessage()));
        }
    }
}
