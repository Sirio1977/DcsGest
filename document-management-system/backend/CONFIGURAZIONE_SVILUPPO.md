# Configurazione per lo sviluppo locale
# Questo file contiene le istruzioni per configurare l'ambiente di sviluppo

## Problema Build - Risoluzione Java Version

### Problema Identificato:
- Java 24 installato nel sistema
- Maven compiler plugin non compatibile con Java 24
- Errore: TypeTag::UNKNOWN

### Soluzioni Possibili:

#### Opzione 1: Installare Java 17 o 21
1. Scaricare Java 17 da: https://adoptium.net/
2. Installare Java 17 in: C:\Program Files\Java\jdk-17
3. Configurare JAVA_HOME: 
   ```powershell
   $env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
   $env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
   ```

#### Opzione 2: Configurare Maven con Java 17
1. Creare file .mvn/jvm.config con:
   ```
   -Djava.version=17
   ```
2. Configurare maven-compiler-plugin per Java 17

#### Opzione 3: Usare Maven Wrapper con Java 17
1. Configurare Maven Wrapper per usare Java 17 specifico
2. Aggiungere configurazione toolchains.xml

### Configurazione Attuale:
- Spring Boot: 3.1.5
- Java Target: 21
- Maven Compiler Plugin: 3.11.0

### Test Ambiente:
Per testare la configurazione:
```bash
mvn clean compile
```

### Endpoint da Testare:
Una volta risolto il build, testare questi endpoint:

#### Documenti:
- GET /api/documenti - Lista documenti
- POST /api/documenti - Crea documento
- GET /api/documenti/{id} - Dettaglio documento
- PUT /api/documenti/{id} - Aggiorna documento
- DELETE /api/documenti/{id} - Elimina documento

#### Modalità Pagamento:
- GET /api/modalita-pagamento - Lista modalità
- POST /api/modalita-pagamento - Crea modalità
- GET /api/modalita-pagamento/attive - Solo attive
- GET /api/modalita-pagamento/ratali - Solo ratali

#### Causali Trasporto:
- GET /api/causale-trasporto - Lista causali
- POST /api/causale-trasporto - Crea causale
- GET /api/causale-trasporto/attive - Solo attive

#### Fornitori:
- GET /api/fornitori - Lista fornitori
- POST /api/fornitori - Crea fornitore
- GET /api/fornitori/{id} - Dettaglio fornitore

### Database:
Eseguire le migration:
```bash
mvn flyway:migrate
```

### Validazione:
1. Verificare che tutte le entità siano mappate correttamente
2. Testare le validazioni fiscali
3. Verificare i calcoli automatici
4. Testare la numerazione automatica

### Prossimi Passi:
1. Risolvere il problema di build
2. Eseguire i test unitari
3. Testare gli endpoint REST
4. Integrare con il frontend
5. Deployment in produzione
