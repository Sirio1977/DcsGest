-- Migrazione V2: Aggiunta tabella articolo_fornitore

CREATE TABLE articolo_fornitore (
    id BIGSERIAL PRIMARY KEY,
    codice VARCHAR(100) NOT NULL,
    descrizione VARCHAR(500) NOT NULL,
    prezzo_unitario DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    unita_misura VARCHAR(20) NOT NULL DEFAULT 'PZ',
    aliquota_iva DECIMAL(5,2) NOT NULL DEFAULT 22.00,
    fornitore_partita_iva VARCHAR(11),
    fornitore_ragione_sociale VARCHAR(255),
    fornitore_categoria VARCHAR(100),
    codice_interno VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indici per migliorare le performance
CREATE INDEX idx_articolo_fornitore_codice ON articolo_fornitore(codice);
CREATE INDEX idx_articolo_fornitore_fornitore_piva ON articolo_fornitore(fornitore_partita_iva);
CREATE INDEX idx_articolo_fornitore_codice_fornitore ON articolo_fornitore(codice, fornitore_partita_iva);

-- Commenti per documentare la tabella
COMMENT ON TABLE articolo_fornitore IS 'Tabella per gestire gli articoli fornitori importati';
COMMENT ON COLUMN articolo_fornitore.codice IS 'Codice identificativo del prodotto presso il fornitore';
COMMENT ON COLUMN articolo_fornitore.descrizione IS 'Descrizione completa del prodotto';
COMMENT ON COLUMN articolo_fornitore.prezzo_unitario IS 'Prezzo unitario del prodotto (senza IVA)';
COMMENT ON COLUMN articolo_fornitore.unita_misura IS 'Unità di misura (KG, PZ, LT, etc.)';
COMMENT ON COLUMN articolo_fornitore.aliquota_iva IS 'Aliquota IVA applicata al prodotto';
COMMENT ON COLUMN articolo_fornitore.fornitore_partita_iva IS 'Partita IVA del fornitore';
COMMENT ON COLUMN articolo_fornitore.fornitore_ragione_sociale IS 'Ragione sociale del fornitore';
COMMENT ON COLUMN articolo_fornitore.fornitore_categoria IS 'Categoria del fornitore';
COMMENT ON COLUMN articolo_fornitore.codice_interno IS 'Codice interno per uso aziendale';
