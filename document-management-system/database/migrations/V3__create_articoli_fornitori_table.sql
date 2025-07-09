-- Creazione tabella articoli_fornitori
CREATE TABLE IF NOT EXISTS articoli_fornitori (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    codice VARCHAR(50) NOT NULL,
    descrizione VARCHAR(500) NOT NULL,
    quantita DECIMAL(10,3) NOT NULL,
    prezzo_unitario DECIMAL(10,2) NOT NULL,
    importo DECIMAL(12,2) NOT NULL,
    unita_misura VARCHAR(10) NOT NULL,
    aliquota_iva INT NOT NULL,
    fornitore_partita_iva VARCHAR(20) NOT NULL,
    fornitore_ragione_sociale VARCHAR(200) NOT NULL,
    fornitore_categoria VARCHAR(100),
    data_documento DATE,
    data_ultimo_aggiornamento DATE,
    codice_interno VARCHAR(50),
    prezzi_storici TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Indici per migliorare le performance
    INDEX idx_codice (codice),
    INDEX idx_fornitore_piva (fornitore_partita_iva),
    INDEX idx_codice_fornitore (codice, fornitore_partita_iva),
    INDEX idx_data_documento (data_documento),
    INDEX idx_fornitore_ragione_sociale (fornitore_ragione_sociale),
    
    -- Constraints
    CONSTRAINT chk_quantita_positive CHECK (quantita > 0),
    CONSTRAINT chk_prezzo_positive CHECK (prezzo_unitario > 0),
    CONSTRAINT chk_importo_positive CHECK (importo > 0),
    CONSTRAINT chk_aliquota_iva_range CHECK (aliquota_iva >= 0 AND aliquota_iva <= 100)
);

-- Commenti per documentazione
ALTER TABLE articoli_fornitori 
COMMENT = 'Tabella per la gestione degli articoli fornitori con storico prezzi';

ALTER TABLE articoli_fornitori 
MODIFY COLUMN codice VARCHAR(50) NOT NULL COMMENT 'Codice identificativo univoco dell articolo';

ALTER TABLE articoli_fornitori 
MODIFY COLUMN descrizione VARCHAR(500) NOT NULL COMMENT 'Descrizione dettagliata dell articolo';

ALTER TABLE articoli_fornitori 
MODIFY COLUMN quantita DECIMAL(10,3) NOT NULL COMMENT 'Quantità dell articolo';

ALTER TABLE articoli_fornitori 
MODIFY COLUMN prezzo_unitario DECIMAL(10,2) NOT NULL COMMENT 'Prezzo unitario dell articolo';

ALTER TABLE articoli_fornitori 
MODIFY COLUMN importo DECIMAL(12,2) NOT NULL COMMENT 'Importo totale (quantita * prezzo_unitario)';

ALTER TABLE articoli_fornitori 
MODIFY COLUMN unita_misura VARCHAR(10) NOT NULL COMMENT 'Unità di misura (KG, PZ, L, etc.)';

ALTER TABLE articoli_fornitori 
MODIFY COLUMN aliquota_iva INT NOT NULL COMMENT 'Aliquota IVA in percentuale (0-100)';

ALTER TABLE articoli_fornitori 
MODIFY COLUMN fornitore_partita_iva VARCHAR(20) NOT NULL COMMENT 'Partita IVA del fornitore';

ALTER TABLE articoli_fornitori 
MODIFY COLUMN fornitore_ragione_sociale VARCHAR(200) NOT NULL COMMENT 'Ragione sociale del fornitore';

ALTER TABLE articoli_fornitori 
MODIFY COLUMN fornitore_categoria VARCHAR(100) COMMENT 'Categoria del fornitore (merci, servizi, etc.)';

ALTER TABLE articoli_fornitori 
MODIFY COLUMN data_documento DATE COMMENT 'Data del documento di riferimento';

ALTER TABLE articoli_fornitori 
MODIFY COLUMN data_ultimo_aggiornamento DATE COMMENT 'Data ultimo aggiornamento dell articolo';

ALTER TABLE articoli_fornitori 
MODIFY COLUMN codice_interno VARCHAR(50) COMMENT 'Codice interno di riferimento';

ALTER TABLE articoli_fornitori 
MODIFY COLUMN prezzi_storici TEXT COMMENT 'Storico prezzi in formato JSON';

ALTER TABLE articoli_fornitori 
MODIFY COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creazione record';

ALTER TABLE articoli_fornitori 
MODIFY COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Data ultimo aggiornamento record';
