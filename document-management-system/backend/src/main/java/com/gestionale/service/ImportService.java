package com.gestionale.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestionale.dto.ImportResultDto;
import com.gestionale.dto.ImportArticoliResponse;
import com.gestionale.dto.ImportFornitoriResponse;
import com.gestionale.dto.ImportArticoliFornitoriResponse;
import com.gestionale.entity.Cliente;
import com.gestionale.entity.Articolo;
import com.gestionale.entity.ArticoloFornitore;
import com.gestionale.repository.ClienteRepository;
import com.gestionale.repository.ArticoloRepository;
import com.gestionale.repository.ArticoloFornitoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class ImportService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ArticoloRepository articoloRepository;

    @Autowired
    private ArticoloFornitoreRepository articoloFornitoreRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Pattern per validazione Partita IVA italiana
    private static final Pattern PARTITA_IVA_PATTERN = Pattern.compile("^[0-9]{11}$");

    @Transactional
    public ImportResultDto importClientiFromJson(MultipartFile file, boolean preview) throws IOException {
        List<Map<String, Object>> rawData = parseJsonFile(file);
        List<String> errors = new ArrayList<>();
        List<Cliente> clientiDaSalvare = new ArrayList<>();
        List<Map<String, Object>> previewData = new ArrayList<>();
        
        int totalRecords = rawData.size();
        int successfulRecords = 0;

        for (int i = 0; i < rawData.size(); i++) {
            Map<String, Object> record = rawData.get(i);
            
            try {
                Cliente cliente = mapJsonToCliente(record, i + 1);
                
                if (cliente != null) {
                    // Validazioni business
                    List<String> recordErrors = validateCliente(cliente, i + 1);
                    
                    if (recordErrors.isEmpty()) {
                        // Controlla duplicati
                        if (!isDuplicateCliente(cliente)) {
                            clientiDaSalvare.add(cliente);
                            successfulRecords++;
                            
                            // Aggiungi ai dati di preview (primi 10)
                            if (previewData.size() < 10) {
                                Map<String, Object> previewRecord = createPreviewRecord(cliente, record);
                                previewData.add(previewRecord);
                            }
                        } else {
                            errors.add(String.format("Riga %d: Cliente già esistente (P.IVA: %s)", 
                                i + 1, cliente.getPartitaIva()));
                        }
                    } else {
                        errors.addAll(recordErrors);
                    }
                }
            } catch (Exception e) {
                errors.add(String.format("Riga %d: Errore nel parsing - %s", i + 1, e.getMessage()));
            }
        }

        // Se non è preview, salva effettivamente i dati
        if (!preview && !clientiDaSalvare.isEmpty()) {
            try {
                clienteRepository.saveAll(clientiDaSalvare);
            } catch (Exception e) {
                return new ImportResultDto(false, 
                    "Errore durante il salvataggio: " + e.getMessage(), 
                    totalRecords, 0, Arrays.asList(e.getMessage()));
            }
        }

        String message = preview ? 
            String.format("Preview completato: %d/%d record validi", successfulRecords, totalRecords) :
            String.format("Importazione completata: %d/%d record importati", successfulRecords, totalRecords);

        ImportResultDto result = new ImportResultDto(
            errors.isEmpty() || successfulRecords > 0, 
            message, 
            totalRecords, 
            successfulRecords, 
            errors, 
            previewData
        );

        // Aggiungi statistiche
        Map<String, Object> stats = new HashMap<>();
        stats.put("successRate", result.getSuccessRate());
        stats.put("duplicatesFound", totalRecords - successfulRecords - errors.size());
        stats.put("preview", preview);
        result.setStatistics(stats);

        return result;
    }

    public ImportResultDto validateClientiJson(MultipartFile file) throws IOException {
        return importClientiFromJson(file, true); // Preview mode
    }

    public Map<String, Object> getClientiStructure() {
        Map<String, Object> structure = new HashMap<>();
        
        // Struttura del JSON clienti attesa
        Map<String, String> expectedFields = new LinkedHashMap<>();
        expectedFields.put("ragioneSociale", "string (obbligatorio) - Ragione sociale del cliente");
        expectedFields.put("partitaIva", "string (obbligatorio) - Partita IVA (11 cifre)");
        expectedFields.put("categoria", "string - Categoria cliente (azienda/privato)");
        expectedFields.put("indirizzo", "string - Indirizzo completo");
        expectedFields.put("email", "string - Email del cliente");
        expectedFields.put("telefono", "string - Numero di telefono");
        expectedFields.put("pec", "string - Indirizzo PEC");
        expectedFields.put("dataUltimoAggiornamento", "string - Data ultimo aggiornamento (ISO format)");

        structure.put("expectedFields", expectedFields);
        structure.put("requiredFields", Arrays.asList("ragioneSociale", "partitaIva"));
        
        // Esempio di record valido
        Map<String, Object> exampleRecord = new HashMap<>();
        exampleRecord.put("ragioneSociale", "ESEMPIO SRL");
        exampleRecord.put("partitaIva", "12345678901");
        exampleRecord.put("categoria", "azienda");
        exampleRecord.put("indirizzo", "Via Roma 1, 20100 Milano MI");
        exampleRecord.put("email", "info@esempio.it");
        exampleRecord.put("telefono", "02-1234567");
        exampleRecord.put("pec", "esempio@pec.it");
        exampleRecord.put("dataUltimoAggiornamento", "2025-07-09T10:00:00.000Z");
        
        structure.put("example", exampleRecord);
        
        return structure;
    }

    private List<Map<String, Object>> parseJsonFile(MultipartFile file) throws IOException {
        return objectMapper.readValue(file.getInputStream(), new TypeReference<List<Map<String, Object>>>() {});
    }

    private Cliente mapJsonToCliente(Map<String, Object> record, int rowNumber) {
        String ragioneSociale = getString(record, "ragioneSociale");
        String partitaIva = cleanPartitaIva(getString(record, "partitaIva"));

        if (ragioneSociale == null || ragioneSociale.trim().isEmpty()) {
            throw new IllegalArgumentException("Ragione sociale obbligatoria");
        }

        if (partitaIva == null || partitaIva.trim().isEmpty()) {
            throw new IllegalArgumentException("Partita IVA obbligatoria");
        }

        Cliente cliente = new Cliente();
        cliente.setRagioneSociale(ragioneSociale.trim());
        cliente.setPartitaIva(partitaIva);
        
        // Campi opzionali
        String indirizzo = getString(record, "indirizzo");
        if (indirizzo != null && !indirizzo.trim().isEmpty()) {
            parseIndirizzo(cliente, indirizzo);
        }

        String email = getString(record, "email");
        if (email != null && !email.trim().isEmpty()) {
            cliente.setEmail(email.trim());
        }

        String telefono = getString(record, "telefono");
        if (telefono != null && !telefono.trim().isEmpty()) {
            cliente.setTelefono(telefono.trim());
        }

        String pec = getString(record, "pec");
        if (pec != null && !pec.trim().isEmpty()) {
            cliente.setPec(pec.trim());
        }

        // Imposta il tipo in base alla categoria
        String categoria = getString(record, "categoria");
        if ("azienda".equalsIgnoreCase(categoria)) {
            cliente.setTipo(Cliente.TipoCliente.CLIENTE);
        } else {
            cliente.setTipo(Cliente.TipoCliente.CLIENTE);
        }

        cliente.setAttivo(true);
        cliente.setCreatedAt(LocalDateTime.now());

        return cliente;
    }

    private void parseIndirizzo(Cliente cliente, String indirizzoCompleto) {
        // Parsing semplificato dell'indirizzo
        // Formato atteso: "Via Roma 1, 20100 Milano MI"
        String[] parti = indirizzoCompleto.split(",");
        
        if (parti.length >= 1) {
            cliente.setIndirizzo(parti[0].trim());
        }
        
        if (parti.length >= 2) {
            String secondaParte = parti[1].trim();
            // Estrai CAP, città e provincia
            String[] tokens = secondaParte.split("\\s+");
            
            if (tokens.length >= 1 && tokens[0].matches("\\d{5}")) {
                cliente.setCap(tokens[0]);
            }
            
            if (tokens.length >= 2) {
                // Tutto tranne l'ultimo token per la città
                StringBuilder citta = new StringBuilder();
                for (int i = 1; i < tokens.length - 1; i++) {
                    if (citta.length() > 0) citta.append(" ");
                    citta.append(tokens[i]);
                }
                if (citta.length() > 0) {
                    cliente.setCitta(citta.toString());
                }
                
                // Ultimo token per la provincia
                if (tokens.length > 2) {
                    String lastToken = tokens[tokens.length - 1];
                    if (lastToken.length() == 2) {
                        cliente.setProvincia(lastToken);
                    } else if (citta.length() == 0) {
                        cliente.setCitta(lastToken);
                    }
                } else if (tokens.length == 2) {
                    cliente.setCitta(tokens[1]);
                }
            }
        }
    }

    private List<String> validateCliente(Cliente cliente, int rowNumber) {
        List<String> errors = new ArrayList<>();

        // Validazione Partita IVA
        if (!isValidPartitaIva(cliente.getPartitaIva())) {
            errors.add(String.format("Riga %d: Partita IVA non valida (%s)", rowNumber, cliente.getPartitaIva()));
        }

        // Validazione email se presente
        if (cliente.getEmail() != null && !isValidEmail(cliente.getEmail())) {
            errors.add(String.format("Riga %d: Email non valida (%s)", rowNumber, cliente.getEmail()));
        }

        // Validazione lunghezza campi
        if (cliente.getRagioneSociale().length() > 255) {
            errors.add(String.format("Riga %d: Ragione sociale troppo lunga (max 255 caratteri)", rowNumber));
        }

        return errors;
    }

    private boolean isDuplicateCliente(Cliente cliente) {
        return clienteRepository.existsByPartitaIva(cliente.getPartitaIva());
    }

    private Map<String, Object> createPreviewRecord(Cliente cliente, Map<String, Object> originalRecord) {
        Map<String, Object> preview = new HashMap<>();
        preview.put("original", originalRecord);
        
        Map<String, Object> mapped = new HashMap<>();
        mapped.put("ragioneSociale", cliente.getRagioneSociale());
        mapped.put("partitaIva", cliente.getPartitaIva());
        mapped.put("indirizzo", cliente.getIndirizzo());
        mapped.put("citta", cliente.getCitta());
        mapped.put("cap", cliente.getCap());
        mapped.put("provincia", cliente.getProvincia());
        mapped.put("email", cliente.getEmail());
        mapped.put("telefono", cliente.getTelefono());
        mapped.put("pec", cliente.getPec());
        mapped.put("tipo", cliente.getTipo().toString());
        
        preview.put("mapped", mapped);
        return preview;
    }

    @Transactional
    public ImportArticoliResponse importArticoliFromJson(MultipartFile file, boolean preview) throws IOException {
        List<Map<String, Object>> rawData = parseJsonFile(file);
        List<String> errors = new ArrayList<>();
        List<Articolo> articoliDaSalvare = new ArrayList<>();
        Set<String> codiciProcessati = new HashSet<>();
        
        int totalRecords = rawData.size();
        int successfulRecords = 0;

        for (int i = 0; i < rawData.size(); i++) {
            Map<String, Object> record = rawData.get(i);
            
            try {
                Articolo articolo = mapJsonToArticolo(record, i + 1);
                
                if (articolo != null) {
                    // Validazioni business
                    List<String> recordErrors = validateArticolo(articolo, i + 1);
                    
                    if (recordErrors.isEmpty()) {
                        // Controlla duplicati nel file corrente
                        if (!codiciProcessati.contains(articolo.getCodice())) {
                            // Controlla duplicati nel database
                            if (!isDuplicateArticolo(articolo)) {
                                articoliDaSalvare.add(articolo);
                                codiciProcessati.add(articolo.getCodice());
                                successfulRecords++;
                            } else {
                                errors.add(String.format("Riga %d: Articolo già esistente (Codice: %s)", 
                                    i + 1, articolo.getCodice()));
                            }
                        } else {
                            errors.add(String.format("Riga %d: Codice articolo duplicato nel file (Codice: %s)", 
                                i + 1, articolo.getCodice()));
                        }
                    } else {
                        errors.addAll(recordErrors);
                    }
                }
            } catch (Exception e) {
                errors.add(String.format("Riga %d: Errore nel parsing - %s", i + 1, e.getMessage()));
            }
        }

        // Se non è preview, salva effettivamente i dati
        if (!preview && !articoliDaSalvare.isEmpty()) {
            try {
                articoloRepository.saveAll(articoliDaSalvare);
            } catch (Exception e) {
                return new ImportArticoliResponse(false, totalRecords, 0, totalRecords,
                    Arrays.asList("Errore durante il salvataggio: " + e.getMessage()),
                    "Errore durante il salvataggio");
            }
        }

        String message = preview ? 
            String.format("Preview completato: %d/%d record validi", successfulRecords, totalRecords) :
            String.format("Importazione completata: %d/%d record importati", successfulRecords, totalRecords);

        return new ImportArticoliResponse(
            errors.isEmpty() || successfulRecords > 0,
            totalRecords,
            successfulRecords,
            totalRecords - successfulRecords,
            errors,
            message
        );
    }

    public ImportArticoliResponse validateArticoliJson(MultipartFile file) throws IOException {
        return importArticoliFromJson(file, true); // Preview mode
    }

    public Map<String, Object> getArticoliStructure() {
        Map<String, Object> structure = new HashMap<>();
        
        // Struttura del JSON articoli attesa
        Map<String, String> expectedFields = new LinkedHashMap<>();
        expectedFields.put("codice", "string (obbligatorio) - Codice articolo univoco");
        expectedFields.put("descrizione", "string (obbligatorio) - Descrizione articolo");
        expectedFields.put("quantita", "number - Quantità venduta");
        expectedFields.put("prezzoUnitario", "number (obbligatorio) - Prezzo unitario");
        expectedFields.put("importo", "number - Importo totale");
        expectedFields.put("unitaMisura", "string - Unità di misura (KG, PZ, ecc.)");
        expectedFields.put("aliquotaIVA", "number - Aliquota IVA (percentuale)");
        expectedFields.put("cliente", "object - Dati cliente associato");
        expectedFields.put("dataDocumento", "string - Data documento");
        expectedFields.put("numeroFattura", "string - Numero fattura");

        structure.put("expectedFields", expectedFields);
        structure.put("requiredFields", Arrays.asList("codice", "descrizione", "prezzoUnitario"));
        
        // Esempio di record valido
        Map<String, Object> exampleRecord = new HashMap<>();
        exampleRecord.put("codice", "ART001");
        exampleRecord.put("descrizione", "ARTICOLO ESEMPIO");
        exampleRecord.put("quantita", 10.5);
        exampleRecord.put("prezzoUnitario", 15.50);
        exampleRecord.put("importo", 162.75);
        exampleRecord.put("unitaMisura", "KG");
        exampleRecord.put("aliquotaIVA", 22);
        
        Map<String, Object> clienteExample = new HashMap<>();
        clienteExample.put("partitaIva", "12345678901");
        clienteExample.put("ragioneSociale", "CLIENTE ESEMPIO SRL");
        clienteExample.put("categoria", "azienda");
        exampleRecord.put("cliente", clienteExample);
        
        exampleRecord.put("dataDocumento", "2025-07-09");
        exampleRecord.put("numeroFattura", "001");
        
        structure.put("example", exampleRecord);
        
        return structure;
    }

    private Articolo mapJsonToArticolo(Map<String, Object> record, int rowNumber) {
        String codice = getString(record, "codice");
        String descrizione = getString(record, "descrizione");
        
        if (codice == null || codice.trim().isEmpty()) {
            return null;
        }
        
        if (descrizione == null || descrizione.trim().isEmpty()) {
            return null;
        }
        
        Articolo articolo = new Articolo();
        articolo.setCodice(codice.trim().toUpperCase());
        articolo.setDescrizione(descrizione.trim());
        
        // Prezzo unitario - obbligatorio
        Object prezzoObj = record.get("prezzoUnitario");
        if (prezzoObj != null) {
            try {
                BigDecimal prezzo = getBigDecimal(prezzoObj);
                articolo.setPrezzo(prezzo);
            } catch (Exception e) {
                // Se il prezzo non è valido, l'articolo non sarà processato
                return null;
            }
        } else {
            return null;
        }
        
        // Unità di misura
        String unitaMisura = getString(record, "unitaMisura");
        if (unitaMisura != null && !unitaMisura.trim().isEmpty()) {
            articolo.setUnitaMisura(unitaMisura.trim().toUpperCase());
        } else {
            articolo.setUnitaMisura("PZ"); // Default
        }
        
        // Aliquota IVA
        Object aliquotaObj = record.get("aliquotaIVA");
        if (aliquotaObj != null) {
            try {
                BigDecimal aliquota = getBigDecimal(aliquotaObj);
                articolo.setAliquotaIva(aliquota);
            } catch (Exception e) {
                articolo.setAliquotaIva(new BigDecimal("22.00")); // Default IVA italiana
            }
        } else {
            articolo.setAliquotaIva(new BigDecimal("22.00")); // Default
        }
        
        // Tipo articolo - per default PRODOTTO
        articolo.setTipo(Articolo.TipoArticolo.PRODOTTO);
        
        // Stato attivo
        articolo.setAttivo(true);
        
        return articolo;
    }

    private List<String> validateArticolo(Articolo articolo, int rowNumber) {
        List<String> errors = new ArrayList<>();
        
        // Validazione codice
        if (articolo.getCodice() == null || articolo.getCodice().trim().isEmpty()) {
            errors.add(String.format("Riga %d: Codice articolo obbligatorio", rowNumber));
        } else if (articolo.getCodice().length() > 50) {
            errors.add(String.format("Riga %d: Codice articolo troppo lungo (max 50 caratteri)", rowNumber));
        }
        
        // Validazione descrizione
        if (articolo.getDescrizione() == null || articolo.getDescrizione().trim().isEmpty()) {
            errors.add(String.format("Riga %d: Descrizione articolo obbligatoria", rowNumber));
        } else if (articolo.getDescrizione().length() > 255) {
            errors.add(String.format("Riga %d: Descrizione troppo lunga (max 255 caratteri)", rowNumber));
        }
        
        // Validazione prezzo
        if (articolo.getPrezzo() == null) {
            errors.add(String.format("Riga %d: Prezzo obbligatorio", rowNumber));
        } else if (articolo.getPrezzo().compareTo(BigDecimal.ZERO) < 0) {
            errors.add(String.format("Riga %d: Prezzo non può essere negativo", rowNumber));
        }
        
        // Validazione aliquota IVA
        if (articolo.getAliquotaIva() != null && 
            (articolo.getAliquotaIva().compareTo(BigDecimal.ZERO) < 0 || 
             articolo.getAliquotaIva().compareTo(new BigDecimal("100")) > 0)) {
            errors.add(String.format("Riga %d: Aliquota IVA deve essere tra 0 e 100", rowNumber));
        }
        
        return errors;
    }

    private boolean isDuplicateArticolo(Articolo articolo) {
        return articoloRepository.findByCodice(articolo.getCodice()).isPresent();
    }

    private BigDecimal getBigDecimal(Object value) {
        if (value == null) return BigDecimal.ZERO;
        
        if (value instanceof Number) {
            return new BigDecimal(value.toString());
        }
        
        if (value instanceof String) {
            String strValue = ((String) value).trim().replace(",", ".");
            if (strValue.isEmpty()) return BigDecimal.ZERO;
            return new BigDecimal(strValue);
        }
        
        return BigDecimal.ZERO;
    }

    // Utility methods
    private String getString(Map<String, Object> record, String key) {
        Object value = record.get(key);
        return value != null ? value.toString() : null;
    }

    private String cleanPartitaIva(String partitaIva) {
        if (partitaIva == null) return null;
        return partitaIva.replaceAll("[^0-9]", "");
    }

    private boolean isValidPartitaIva(String partitaIva) {
        return partitaIva != null && PARTITA_IVA_PATTERN.matcher(partitaIva).matches();
    }

    private boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }

    @Transactional
    public ImportFornitoriResponse importFornitoriFromJson(MultipartFile file, boolean preview) throws IOException {
        List<Map<String, Object>> rawData = parseJsonFile(file);
        List<String> errors = new ArrayList<>();
        List<Cliente> fornitoriDaSalvare = new ArrayList<>();
        
        int totalRecords = rawData.size();
        int successfulRecords = 0;

        for (int i = 0; i < rawData.size(); i++) {
            Map<String, Object> record = rawData.get(i);
            
            try {
                Cliente fornitore = mapJsonToFornitore(record, i + 1);
                
                if (fornitore != null) {
                    // Validazioni business
                    List<String> recordErrors = validateFornitore(fornitore, i + 1);
                    
                    if (recordErrors.isEmpty()) {
                        // Controlla duplicati
                        if (!isDuplicateFornitore(fornitore)) {
                            fornitoriDaSalvare.add(fornitore);
                            successfulRecords++;
                        } else {
                            errors.add(String.format("Riga %d: Fornitore già esistente (P.IVA: %s)", 
                                i + 1, fornitore.getPartitaIva()));
                        }
                    } else {
                        errors.addAll(recordErrors);
                    }
                }
            } catch (Exception e) {
                errors.add(String.format("Riga %d: Errore nel parsing - %s", i + 1, e.getMessage()));
            }
        }

        // Se non è preview, salva effettivamente i dati
        if (!preview && !fornitoriDaSalvare.isEmpty()) {
            try {
                clienteRepository.saveAll(fornitoriDaSalvare);
            } catch (Exception e) {
                return new ImportFornitoriResponse(false, totalRecords, 0, totalRecords,
                    Arrays.asList("Errore durante il salvataggio: " + e.getMessage()),
                    "Errore durante il salvataggio");
            }
        }

        String message = preview ? 
            String.format("Preview completato: %d/%d record validi", successfulRecords, totalRecords) :
            String.format("Importazione completata: %d/%d record importati", successfulRecords, totalRecords);

        return new ImportFornitoriResponse(
            errors.isEmpty() || successfulRecords > 0,
            totalRecords,
            successfulRecords,
            totalRecords - successfulRecords,
            errors,
            message
        );
    }

    public ImportFornitoriResponse validateFornitoriJson(MultipartFile file) throws IOException {
        return importFornitoriFromJson(file, true); // Preview mode
    }

    public Map<String, Object> getFornitoriStructure() {
        Map<String, Object> structure = new HashMap<>();
        
        // Struttura del JSON fornitori attesa
        Map<String, String> expectedFields = new LinkedHashMap<>();
        expectedFields.put("ragioneSociale", "string (obbligatorio) - Ragione sociale del fornitore");
        expectedFields.put("partitaIva", "string (obbligatorio) - Partita IVA (11 cifre)");
        expectedFields.put("categoria", "string - Categoria fornitore (servizi/merci)");
        expectedFields.put("indirizzo", "string - Indirizzo completo");
        expectedFields.put("email", "string - Email del fornitore");
        expectedFields.put("telefono", "string - Numero di telefono");
        expectedFields.put("pec", "string - Indirizzo PEC");
        expectedFields.put("dataUltimoAggiornamento", "string - Data ultimo aggiornamento (ISO format)");

        structure.put("expectedFields", expectedFields);
        structure.put("requiredFields", Arrays.asList("ragioneSociale", "partitaIva"));
        
        // Esempio di record valido
        Map<String, Object> exampleRecord = new HashMap<>();
        exampleRecord.put("ragioneSociale", "FORNITORE ESEMPIO SRL");
        exampleRecord.put("partitaIva", "12345678901");
        exampleRecord.put("categoria", "servizi");
        exampleRecord.put("indirizzo", "Via Roma 1, 20100 Milano MI");
        exampleRecord.put("email", "info@fornitore.it");
        exampleRecord.put("telefono", "02-1234567");
        exampleRecord.put("pec", "fornitore@pec.it");
        exampleRecord.put("dataUltimoAggiornamento", "2025-07-09T10:00:00.000Z");
        
        structure.put("example", exampleRecord);
        
        return structure;
    }

    private Cliente mapJsonToFornitore(Map<String, Object> record, int rowNumber) {
        String ragioneSociale = getString(record, "ragioneSociale");
        String partitaIva = cleanPartitaIva(getString(record, "partitaIva"));
        
        if (ragioneSociale == null || ragioneSociale.trim().isEmpty()) {
            return null;
        }
        
        if (partitaIva == null || partitaIva.trim().isEmpty()) {
            return null;
        }
        
        Cliente fornitore = new Cliente();
        fornitore.setRagioneSociale(ragioneSociale.trim());
        fornitore.setPartitaIva(partitaIva);
        fornitore.setTipo(Cliente.TipoCliente.FORNITORE); // Impostiamo come FORNITORE
        
        // Categoria dal JSON (servizi/merci) -> non c'è un campo specifico nell'entity Cliente per questo
        String categoria = getString(record, "categoria");
        if (categoria != null) {
            // Possiamo salvarlo nelle note o creare un campo apposito
            fornitore.setNote("Categoria: " + categoria.trim());
        }
        
        // Indirizzo completo
        String indirizzo = getString(record, "indirizzo");
        if (indirizzo != null && !indirizzo.trim().isEmpty()) {
            fornitore.setIndirizzo(indirizzo.trim());
            
            // Prova a estrarre città, provincia, CAP dall'indirizzo
            Map<String, String> indirizzoComponents = parseIndirizzoFornitore(indirizzo);
            fornitore.setCitta(indirizzoComponents.get("citta"));
            fornitore.setProvincia(indirizzoComponents.get("provincia"));
            fornitore.setCap(indirizzoComponents.get("cap"));
        }
        
        // Contatti
        String email = getString(record, "email");
        if (email != null && !email.trim().isEmpty()) {
            fornitore.setEmail(email.trim());
        }
        
        String telefono = getString(record, "telefono");
        if (telefono != null && !telefono.trim().isEmpty()) {
            fornitore.setTelefono(telefono.trim());
        }
        
        String pec = getString(record, "pec");
        if (pec != null && !pec.trim().isEmpty()) {
            fornitore.setPec(pec.trim());
        }
        
        // Stato attivo di default
        fornitore.setAttivo(true);
        
        return fornitore;
    }

    private Map<String, String> parseIndirizzoFornitore(String indirizzo) {
        Map<String, String> result = new HashMap<>();
        result.put("citta", "");
        result.put("provincia", "");
        result.put("cap", "");
        
        if (indirizzo == null || indirizzo.trim().isEmpty()) {
            return result;
        }
        
        // Pattern per riconoscere CAP (5 cifre), Provincia (2 lettere), Città
        // Esempio: "VIA ROMA 123  20100 MILANO  MI"
        String indirizzoPattern = "(.+?)\\s+(\\d{5})\\s+([A-Z\\s]+?)\\s+([A-Z]{2})\\s*$";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(indirizzoPattern, java.util.regex.Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher matcher = pattern.matcher(indirizzo.trim());
        
        if (matcher.find()) {
            result.put("cap", matcher.group(2));
            result.put("citta", matcher.group(3).trim());
            result.put("provincia", matcher.group(4).trim().toUpperCase());
        } else {
            // Fallback: cerca solo il CAP
            java.util.regex.Pattern capPattern = java.util.regex.Pattern.compile("(\\d{5})");
            java.util.regex.Matcher capMatcher = capPattern.matcher(indirizzo);
            if (capMatcher.find()) {
                result.put("cap", capMatcher.group(1));
            }
        }
        
        return result;
    }

    private List<String> validateFornitore(Cliente fornitore, int rowNumber) {
        List<String> errors = new ArrayList<>();
        
        // Validazione ragione sociale
        if (fornitore.getRagioneSociale() == null || fornitore.getRagioneSociale().trim().isEmpty()) {
            errors.add(String.format("Riga %d: Ragione sociale obbligatoria", rowNumber));
        } else if (fornitore.getRagioneSociale().length() > 255) {
            errors.add(String.format("Riga %d: Ragione sociale troppo lunga (max 255 caratteri)", rowNumber));
        }
        
        // Validazione partita IVA
        if (fornitore.getPartitaIva() == null || fornitore.getPartitaIva().trim().isEmpty()) {
            errors.add(String.format("Riga %d: Partita IVA obbligatoria", rowNumber));
        } else if (!PARTITA_IVA_PATTERN.matcher(fornitore.getPartitaIva()).matches()) {
            errors.add(String.format("Riga %d: Partita IVA non valida (deve essere di 11 cifre)", rowNumber));
        }
        
        // Validazione email
        if (fornitore.getEmail() != null && !fornitore.getEmail().isEmpty()) {
            if (!isValidEmail(fornitore.getEmail())) {
                errors.add(String.format("Riga %d: Email non valida", rowNumber));
            }
        }
        
        return errors;
    }

    private boolean isDuplicateFornitore(Cliente fornitore) {
        return clienteRepository.findByPartitaIva(fornitore.getPartitaIva()).isPresent();
    }

    // Métodos per importare fornitori
    public List<Map<String, Object>> parseFornitori(String jsonContent) {
        try {
            return objectMapper.readValue(jsonContent, new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Errore durante il parsing del JSON", e);
        }
    }

    public ImportFornitoriResponse validateFornitori(List<Map<String, Object>> fornitori) {
        ImportFornitoriResponse response = new ImportFornitoriResponse();
        List<Map<String, Object>> validi = new ArrayList<>();
        List<String> errori = new ArrayList<>();
        
        for (int i = 0; i < fornitori.size(); i++) {
            Map<String, Object> fornitore = fornitori.get(i);
            String ragioneSociale = (String) fornitore.get("ragioneSociale");
            String partitaIva = (String) fornitore.get("partitaIva");
            
            if (ragioneSociale == null || ragioneSociale.trim().isEmpty()) {
                errori.add("Riga " + (i + 1) + ": Ragione sociale mancante");
                continue;
            }
            
            if (partitaIva == null || partitaIva.trim().isEmpty()) {
                errori.add("Riga " + (i + 1) + ": Partita IVA mancante");
                continue;
            }
            
            // Validazione partita IVA italiana (11 cifre)
            if (!partitaIva.matches("\\d{11}")) {
                errori.add("Riga " + (i + 1) + ": Partita IVA non valida (deve essere di 11 cifre): " + partitaIva);
                continue;
            }
            
            validi.add(fornitore);
        }
        
        response.setFornitoriValidi(validi);
        response.setErrors(errori);
        response.setTotaleFornitoriElaborati(fornitori.size());
        response.setFornitoriImportati(validi.size()); // Corretto: numero di fornitori validi
        response.setFornitoriConErrori(errori.size());
        
        return response;
    }

    public ImportFornitoriResponse importFornitori(List<Map<String, Object>> fornitori) {
        ImportFornitoriResponse response = validateFornitori(fornitori);
        
        if (response.getFornitoriValidi().isEmpty()) {
            return response;
        }
        
        List<Cliente> fornitorireate = new ArrayList<>();
        List<String> duplicati = new ArrayList<>();
        List<String> erroriImport = new ArrayList<>();
        
        for (Map<String, Object> fornitoreData : response.getFornitoriValidi()) {
            try {
                String partitaIva = (String) fornitoreData.get("partitaIva");
                
                // Verifica duplicati
                if (clienteRepository.existsByPartitaIva(partitaIva)) {
                    duplicati.add("Fornitore con P.IVA " + partitaIva + " già esistente");
                    continue;
                }
                
                Cliente fornitore = mapToFornitore(fornitoreData);
                clienteRepository.save(fornitore);
                fornitorireate.add(fornitore);
                
            } catch (Exception e) {
                erroriImport.add("Errore durante l'importazione: " + e.getMessage());
            }
        }
        
        response.setFornitoriImportati(fornitorireate.size());
        response.setDuplicati(duplicati);
        response.getErrors().addAll(erroriImport);
        
        return response;
    }

    private Cliente mapToFornitore(Map<String, Object> fornitoreData) {
        Cliente fornitore = new Cliente();
        
        fornitore.setRagioneSociale((String) fornitoreData.get("ragioneSociale"));
        fornitore.setPartitaIva((String) fornitoreData.get("partitaIva"));
        fornitore.setTipo(Cliente.TipoCliente.FORNITORE);
        
        // Parsing dell'indirizzo
        String indirizzo = (String) fornitoreData.get("indirizzo");
        parseAndSetAddress(fornitore, indirizzo);
        
        // Email
        String email = (String) fornitoreData.get("email");
        if (email != null && !email.trim().isEmpty()) {
            fornitore.setEmail(email.trim());
        }
        
        // Telefono
        String telefono = (String) fornitoreData.get("telefono");
        if (telefono != null && !telefono.trim().isEmpty()) {
            fornitore.setTelefono(telefono.trim());
        }
        
        // PEC
        String pec = (String) fornitoreData.get("pec");
        if (pec != null && !pec.trim().isEmpty()) {
            fornitore.setPec(pec.trim());
        }
        
        // Categoria (come note)
        String categoria = (String) fornitoreData.get("categoria");
        if (categoria != null && !categoria.trim().isEmpty()) {
            fornitore.setNote("Categoria: " + categoria);
        }
        
        return fornitore;
    }

    private void parseAndSetAddress(Cliente cliente, String indirizzoCompleto) {
        if (indirizzoCompleto == null || indirizzoCompleto.trim().isEmpty()) {
            return;
        }
        
        // Parsing dell'indirizzo nel formato "VIA/PIAZZA NOME  CAP CITTA  PROVINCIA"
        String[] parti = indirizzoCompleto.trim().split("\\s+");
        
        if (parti.length >= 3) {
            StringBuilder via = new StringBuilder();
            String cap = null;
            StringBuilder citta = new StringBuilder();
            String provincia = null;
            
            // Cerca il CAP (5 cifre consecutive)
            int capIndex = -1;
            for (int i = 0; i < parti.length; i++) {
                if (parti[i].matches("\\d{5}")) {
                    cap = parti[i];
                    capIndex = i;
                    break;
                }
            }
            
            if (capIndex > 0) {
                // Costruisce la via dalle parti prima del CAP
                for (int i = 0; i < capIndex; i++) {
                    if (via.length() > 0) via.append(" ");
                    via.append(parti[i]);
                }
                cliente.setIndirizzo(via.toString());
                cliente.setCap(cap);
                
                // Costruisce la città dalle parti dopo il CAP
                for (int i = capIndex + 1; i < parti.length; i++) {
                    // L'ultima parte di 2 caratteri è probabilmente la provincia
                    if (i == parti.length - 1 && parti[i].length() == 2) {
                        provincia = parti[i];
                    } else {
                        if (citta.length() > 0) citta.append(" ");
                        citta.append(parti[i]);
                    }
                }
                
                cliente.setCitta(citta.toString());
                if (provincia != null) {
                    cliente.setProvincia(provincia);
                }
            } else {
                // Se non trova il CAP, mette tutto come via
                cliente.setIndirizzo(indirizzoCompleto);
            }
        } else {
            cliente.setIndirizzo(indirizzoCompleto);
        }
    }
    
    // ===================== ARTICOLI FORNITORI IMPORT =====================
    
    @Transactional
    public ImportArticoliFornitoriResponse importArticoliFornitoriFromJson(MultipartFile file, boolean preview) throws IOException {
        List<Map<String, Object>> rawData = parseJsonFile(file);
        return processArticoliFornitori(rawData, preview);
    }
    
    public ImportArticoliFornitoriResponse validateArticoliFornitoriJson(MultipartFile file) throws IOException {
        return importArticoliFornitoriFromJson(file, true);
    }
    
    public Map<String, Object> getArticoliFornitoriStructure() {
        Map<String, Object> structure = new HashMap<>();
        structure.put("codice", "String - Codice articolo (obbligatorio)");
        structure.put("descrizione", "String - Descrizione articolo (obbligatorio)");
        structure.put("quantita", "Number - Quantità (obbligatorio)");
        structure.put("prezzoUnitario", "Number - Prezzo unitario (obbligatorio)");
        structure.put("importo", "Number - Importo totale (obbligatorio)");
        structure.put("unitaMisura", "String - Unità di misura (es: KG, PZ) (obbligatorio)");
        structure.put("aliquotaIVA", "Number - Aliquota IVA in percentuale (obbligatorio)");
        
        Map<String, Object> fornitoreStructure = new HashMap<>();
        fornitoreStructure.put("partitaIva", "String - Partita IVA fornitore (obbligatorio)");
        fornitoreStructure.put("ragioneSociale", "String - Ragione sociale fornitore (obbligatorio)");
        fornitoreStructure.put("categoria", "String - Categoria fornitore (opzionale)");
        structure.put("fornitore", fornitoreStructure);
        
        structure.put("dataDocumento", "String - Data documento (formato: dd/MM/yyyy) (opzionale)");
        structure.put("dataUltimoAggiornamento", "String - Data ultimo aggiornamento (formato: dd/MM/yyyy) (opzionale)");
        structure.put("codiceInterno", "String - Codice interno (opzionale)");
        
        List<Map<String, Object>> prezziStoricoStructure = new ArrayList<>();
        Map<String, Object> prezzoStorico = new HashMap<>();
        prezzoStorico.put("prezzo", "Number - Prezzo storico");
        prezzoStorico.put("data", "String - Data prezzo (formato: dd/MM/yyyy)");
        prezzoStorico.put("fornitore", "String - Nome fornitore");
        prezzoStorico.put("quantita", "Number - Quantità");
        prezziStoricoStructure.add(prezzoStorico);
        structure.put("prezziStorico", prezziStoricoStructure);
        
        return structure;
    }
    
    public List<Map<String, Object>> parseArticoliFornitori(String jsonContent) {
        try {
            return objectMapper.readValue(jsonContent, new TypeReference<List<Map<String, Object>>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Errore nel parsing del JSON degli articoli fornitori: " + e.getMessage(), e);
        }
    }
    
    public ImportArticoliFornitoriResponse validateArticoliFornitori(List<Map<String, Object>> articoli) {
        ImportArticoliFornitoriResponse response = new ImportArticoliFornitoriResponse();
        
        for (int i = 0; i < articoli.size(); i++) {
            Map<String, Object> articoloData = articoli.get(i);
            ImportArticoliFornitoriResponse.ArticoloFornitoreDto articolo = validateSingleArticoloFornitore(articoloData, i + 1);
            
            if (articolo.getErrore() == null || articolo.getErrore().isEmpty()) {
                response.aggiungiArticoloValido(articolo);
            } else {
                response.aggiungiArticoloInvalido(articolo);
                response.incrementaScartati();
            }
        }
        
        return response;
    }
    
    public ImportArticoliFornitoriResponse importArticoliFornitori(List<Map<String, Object>> articoli) {
        ImportArticoliFornitoriResponse response = new ImportArticoliFornitoriResponse();
        
        for (int i = 0; i < articoli.size(); i++) {
            Map<String, Object> articoloData = articoli.get(i);
            ImportArticoliFornitoriResponse.ArticoloFornitoreDto articoloDto = validateSingleArticoloFornitore(articoloData, i + 1);
            
            if (articoloDto.getErrore() == null || articoloDto.getErrore().isEmpty()) {
                try {
                    // Verifica duplicati
                    String codice = articoloDto.getCodice();
                    String partitaIvaFornitore = articoloDto.getFornitore().getPartitaIva();
                    
                    Optional<ArticoloFornitore> existing = articoloFornitoreRepository.findByCodiceAndFornitorePartitaIva(codice, partitaIvaFornitore);
                    
                    if (existing.isPresent()) {
                        // Aggiorna articolo esistente
                        ArticoloFornitore articoloEsistente = existing.get();
                        mapDtoToEntity(articoloDto, articoloEsistente);
                        articoloFornitoreRepository.save(articoloEsistente);
                        response.incrementaAggiornati();
                    } else {
                        // Crea nuovo articolo
                        ArticoloFornitore nuovoArticolo = new ArticoloFornitore();
                        mapDtoToEntity(articoloDto, nuovoArticolo);
                        articoloFornitoreRepository.save(nuovoArticolo);
                        response.incrementaImportati();
                    }
                    
                    response.aggiungiArticoloValido(articoloDto);
                } catch (Exception e) {
                    articoloDto.setErrore("Errore durante il salvataggio: " + e.getMessage());
                    response.aggiungiArticoloInvalido(articoloDto);
                    response.incrementaScartati();
                    response.aggiungiErrore("Errore riga " + (i + 1) + ": " + e.getMessage());
                }
            } else {
                response.aggiungiArticoloInvalido(articoloDto);
                response.incrementaScartati();
            }
        }
        
        return response;
    }
    
    // ===================== METODI PRIVATI ARTICOLI FORNITORI =====================
    
    private ImportArticoliFornitoriResponse processArticoliFornitori(List<Map<String, Object>> rawData, boolean preview) {
        ImportArticoliFornitoriResponse response = new ImportArticoliFornitoriResponse();
        
        if (rawData == null || rawData.isEmpty()) {
            response.aggiungiErrore("Il file JSON è vuoto o non valido");
            return response;
        }
        
        for (int i = 0; i < rawData.size(); i++) {
            Map<String, Object> articoloData = rawData.get(i);
            ImportArticoliFornitoriResponse.ArticoloFornitoreDto articolo = validateSingleArticoloFornitore(articoloData, i + 1);
            
            if (articolo.getErrore() == null || articolo.getErrore().isEmpty()) {
                if (!preview) {
                    try {
                        // Verifica duplicati
                        String codice = articolo.getCodice();
                        String partitaIvaFornitore = articolo.getFornitore().getPartitaIva();
                        
                        Optional<ArticoloFornitore> existing = articoloFornitoreRepository.findByCodiceAndFornitorePartitaIva(codice, partitaIvaFornitore);
                        
                        if (existing.isPresent()) {
                            // Aggiorna articolo esistente
                            ArticoloFornitore articoloEsistente = existing.get();
                            mapDtoToEntity(articolo, articoloEsistente);
                            articoloFornitoreRepository.save(articoloEsistente);
                            response.incrementaAggiornati();
                        } else {
                            // Crea nuovo articolo
                            ArticoloFornitore nuovoArticolo = new ArticoloFornitore();
                            mapDtoToEntity(articolo, nuovoArticolo);
                            articoloFornitoreRepository.save(nuovoArticolo);
                            response.incrementaImportati();
                        }
                    } catch (Exception e) {
                        articolo.setErrore("Errore durante il salvataggio: " + e.getMessage());
                        response.aggiungiArticoloInvalido(articolo);
                        response.incrementaScartati();
                        response.aggiungiErrore("Errore riga " + (i + 1) + ": " + e.getMessage());
                        continue;
                    }
                }
                response.aggiungiArticoloValido(articolo);
            } else {
                response.aggiungiArticoloInvalido(articolo);
                response.incrementaScartati();
            }
        }
        
        return response;
    }
    
    private ImportArticoliFornitoriResponse.ArticoloFornitoreDto validateSingleArticoloFornitore(Map<String, Object> data, int rowNumber) {
        ImportArticoliFornitoriResponse.ArticoloFornitoreDto articolo = new ImportArticoliFornitoriResponse.ArticoloFornitoreDto();
        List<String> errors = new ArrayList<>();
        
        // Validazione campi obbligatori
        String codice = getString(data, "codice");
        if (codice == null || codice.trim().isEmpty()) {
            errors.add("Codice articolo obbligatorio");
        } else {
            articolo.setCodice(codice.trim());
        }
        
        String descrizione = getString(data, "descrizione");
        if (descrizione == null || descrizione.trim().isEmpty()) {
            errors.add("Descrizione obbligatoria");
        } else {
            articolo.setDescrizione(descrizione.trim());
        }
        
        // Validazione quantità
        Double quantita = getDoubleValue(data, "quantita");
        if (quantita == null) {
            errors.add("Quantità obbligatoria");
        } else if (quantita <= 0) {
            errors.add("Quantità deve essere maggiore di zero");
        } else {
            articolo.setQuantita(quantita);
        }
        
        // Validazione prezzo unitario
        Double prezzoUnitario = getDoubleValue(data, "prezzoUnitario");
        if (prezzoUnitario == null) {
            errors.add("Prezzo unitario obbligatorio");
        } else if (prezzoUnitario <= 0) {
            errors.add("Prezzo unitario deve essere maggiore di zero");
        } else {
            articolo.setPrezzoUnitario(prezzoUnitario);
        }
        
        // Validazione importo
        Double importo = getDoubleValue(data, "importo");
        if (importo == null) {
            errors.add("Importo obbligatorio");
        } else if (importo <= 0) {
            errors.add("Importo deve essere maggiore di zero");
        } else {
            articolo.setImporto(importo);
        }
        
        // Validazione unità di misura
        String unitaMisura = getString(data, "unitaMisura");
        if (unitaMisura == null || unitaMisura.trim().isEmpty()) {
            errors.add("Unità di misura obbligatoria");
        } else {
            articolo.setUnitaMisura(unitaMisura.trim().toUpperCase());
        }
        
        // Validazione aliquota IVA
        Integer aliquotaIVA = getIntegerValue(data, "aliquotaIVA");
        if (aliquotaIVA == null) {
            errors.add("Aliquota IVA obbligatoria");
        } else if (aliquotaIVA < 0 || aliquotaIVA > 100) {
            errors.add("Aliquota IVA deve essere tra 0 e 100");
        } else {
            articolo.setAliquotaIVA(aliquotaIVA);
        }
        
        // Validazione fornitore
        @SuppressWarnings("unchecked")
        Map<String, Object> fornitoreData = (Map<String, Object>) data.get("fornitore");
        if (fornitoreData == null) {
            errors.add("Informazioni fornitore obbligatorie");
        } else {
            ImportArticoliFornitoriResponse.FornitoreInfoDto fornitore = validateFornitoreInfo(fornitoreData, errors);
            articolo.setFornitore(fornitore);
        }
        
        // Campi opzionali
        articolo.setDataDocumento(parseLocalDate(getString(data, "dataDocumento")));
        articolo.setDataUltimoAggiornamento(parseLocalDate(getString(data, "dataUltimoAggiornamento")));
        articolo.setCodiceInterno(getString(data, "codiceInterno"));
        
        // Prezzi storici
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> prezziStorici = (List<Map<String, Object>>) data.get("prezziStorico");
        if (prezziStorici != null) {
            List<ImportArticoliFornitoriResponse.PrezzoStoricoDto> prezziStoriciDto = new ArrayList<>();
            for (Map<String, Object> prezzoData : prezziStorici) {
                ImportArticoliFornitoriResponse.PrezzoStoricoDto prezzoDto = new ImportArticoliFornitoriResponse.PrezzoStoricoDto();
                prezzoDto.setPrezzo(getDoubleValue(prezzoData, "prezzo"));
                prezzoDto.setData(parseLocalDate(getString(prezzoData, "data")));
                prezzoDto.setFornitore(getString(prezzoData, "fornitore"));
                prezzoDto.setQuantita(getDoubleValue(prezzoData, "quantita"));
                prezziStoriciDto.add(prezzoDto);
            }
            articolo.setPrezziStorico(prezziStoriciDto);
        }
        
        if (!errors.isEmpty()) {
            articolo.setErrore("Riga " + rowNumber + ": " + String.join(", ", errors));
        }
        
        return articolo;
    }
    
    private ImportArticoliFornitoriResponse.FornitoreInfoDto validateFornitoreInfo(Map<String, Object> fornitoreData, List<String> errors) {
        ImportArticoliFornitoriResponse.FornitoreInfoDto fornitore = new ImportArticoliFornitoriResponse.FornitoreInfoDto();
        
        String partitaIva = getString(fornitoreData, "partitaIva");
        if (partitaIva == null || partitaIva.trim().isEmpty()) {
            errors.add("Partita IVA fornitore obbligatoria");
        } else {
            partitaIva = partitaIva.trim().replaceAll("\\s+", "");
            if (!PARTITA_IVA_PATTERN.matcher(partitaIva).matches()) {
                errors.add("Partita IVA fornitore non valida (deve essere 11 cifre)");
            }
            fornitore.setPartitaIva(partitaIva);
        }
        
        String ragioneSociale = getString(fornitoreData, "ragioneSociale");
        if (ragioneSociale == null || ragioneSociale.trim().isEmpty()) {
            errors.add("Ragione sociale fornitore obbligatoria");
        } else {
            fornitore.setRagioneSociale(ragioneSociale.trim());
        }
        
        String categoria = getString(fornitoreData, "categoria");
        if (categoria != null && !categoria.trim().isEmpty()) {
            fornitore.setCategoria(categoria.trim());
        }
        
        return fornitore;
    }
    
    private void mapDtoToEntity(ImportArticoliFornitoriResponse.ArticoloFornitoreDto dto, ArticoloFornitore entity) {
        entity.setCodice(dto.getCodice());
        entity.setDescrizione(dto.getDescrizione());
        entity.setQuantita(dto.getQuantita());
        entity.setPrezzoUnitario(dto.getPrezzoUnitario());
        entity.setImporto(dto.getImporto());
        entity.setUnitaMisura(dto.getUnitaMisura());
        entity.setAliquotaIVA(dto.getAliquotaIVA());
        
        if (dto.getFornitore() != null) {
            entity.setFornitorePartitaIva(dto.getFornitore().getPartitaIva());
            entity.setFornitoreRagioneSociale(dto.getFornitore().getRagioneSociale());
            entity.setFornitoreCategoria(dto.getFornitore().getCategoria());
        }
        
        entity.setDataDocumento(dto.getDataDocumento());
        entity.setDataUltimoAggiornamento(dto.getDataUltimoAggiornamento());
        entity.setCodiceInterno(dto.getCodiceInterno());
        
        // Serializza prezzi storici come JSON se presenti
        if (dto.getPrezziStorico() != null && !dto.getPrezziStorico().isEmpty()) {
            try {
                entity.setPrezziStorici(objectMapper.writeValueAsString(dto.getPrezziStorico()));
            } catch (IOException e) {
                // Log dell'errore ma non blocca l'importazione
                System.err.println("Errore nella serializzazione dei prezzi storici: " + e.getMessage());
            }
        }
    }

    // ===================== METODI DI UTILITÀ AGGIUNTIVI =====================
    
    private Double getDoubleValue(Map<String, Object> record, String key) {
        Object value = record.get(key);
        if (value == null) return null;
        
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        
        if (value instanceof String) {
            String strValue = ((String) value).trim().replace(",", ".");
            if (strValue.isEmpty()) return null;
            try {
                return Double.parseDouble(strValue);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        
        return null;
    }
    
    private Integer getIntegerValue(Map<String, Object> record, String key) {
        Object value = record.get(key);
        if (value == null) return null;
        
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        
        if (value instanceof String) {
            String strValue = ((String) value).trim();
            if (strValue.isEmpty()) return null;
            try {
                return Integer.parseInt(strValue);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        
        return null;
    }
    
    private LocalDate parseLocalDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        
        try {
            // Prova formato dd/MM/yyyy
            if (dateString.contains("/")) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                return LocalDate.parse(dateString.trim(), formatter);
            }
            
            // Prova formato yyyy-MM-dd
            if (dateString.contains("-")) {
                return LocalDate.parse(dateString.trim());
            }
            
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
