-- V4__create_documenti_tables.sql
-- Creazione tabelle per gestione documenti
-- Data: 2025-07-11
-- Descrizione: Schema completo per documenti commerciali italiani

-- =====================================================
-- TABELLA NUMERAZIONI
-- =====================================================
CREATE TABLE numerazioni (
    id BIGSERIAL PRIMARY KEY,
    tipo_documento VARCHAR(20) NOT NULL,
    anno INTEGER NOT NULL,
    ultimo_numero BIGINT DEFAULT 0,
    prefisso VARCHAR(10) DEFAULT '',
    suffisso VARCHAR(10) DEFAULT '',
    lunghezza_numero INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT uk_numerazioni_tipo_anno UNIQUE(tipo_documento, anno)
);

-- =====================================================
-- TABELLA DOCUMENTI (TESTATA)
-- =====================================================
CREATE TABLE documenti (
    id BIGSERIAL PRIMARY KEY,
    tipo_documento VARCHAR(20) NOT NULL,
    numero BIGINT NOT NULL,
    anno INTEGER NOT NULL,
    data_documento DATE NOT NULL,
    data_registrazione DATE DEFAULT CURRENT_DATE,
    
    -- Soggetto (cliente/fornitore)
    soggetto_id BIGINT NOT NULL,
    ragione_sociale VARCHAR(255) NOT NULL,
    partita_iva VARCHAR(11),
    codice_fiscale VARCHAR(16),
    indirizzo_completo TEXT,
    codice_destinatario VARCHAR(7),
    pec VARCHAR(100),
    
    -- Dati commerciali
    modalita_pagamento_id BIGINT,
    agente_id BIGINT,
    listino_id BIGINT,
    
    -- Trasporto (per DDT)
    causale_trasporto_id BIGINT,
    data_trasporto DATE,
    trasportatore VARCHAR(255),
    colli INTEGER DEFAULT 0,
    peso DECIMAL(10,3) DEFAULT 0,
    
    -- Totali documento
    totale_imponibile DECIMAL(15,2) DEFAULT 0,
    totale_iva DECIMAL(15,2) DEFAULT 0,
    totale_ritenuta DECIMAL(15,2) DEFAULT 0,
    totale_documento DECIMAL(15,2) DEFAULT 0,
    
    -- Stati documento
    stato VARCHAR(20) DEFAULT 'BOZZA',
    stampato BOOLEAN DEFAULT false,
    inviato BOOLEAN DEFAULT false,
    data_invio TIMESTAMP,
    
    -- Note
    note TEXT,
    note_interne TEXT,
    
    -- Riferimenti
    documento_origine_id BIGINT,
    numero_ordine VARCHAR(50),
    data_ordine DATE,
    
    -- Audit
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    
    -- Constraints
    CONSTRAINT chk_documenti_tipo CHECK (tipo_documento IN ('PREVENTIVO', 'ORDINE', 'DDT', 'FATTURA', 'NOTA_CREDITO', 'NOTA_DEBITO', 'RICEVUTA')),
    CONSTRAINT chk_documenti_stato CHECK (stato IN ('BOZZA', 'EMESSO', 'STAMPATO', 'INVIATO', 'PAGATO', 'ANNULLATO')),
    CONSTRAINT chk_documenti_numero_positivo CHECK (numero > 0),
    CONSTRAINT chk_documenti_anno_valido CHECK (anno >= 2000 AND anno <= 2099),
    CONSTRAINT chk_documenti_totali_positivi CHECK (totale_imponibile >= 0 AND totale_iva >= 0 AND totale_documento >= 0),
    
    -- Unique constraint per numerazione
    CONSTRAINT uk_documenti_tipo_numero_anno UNIQUE(tipo_documento, numero, anno)
);

-- =====================================================
-- TABELLA RIGHE_DOCUMENTI (DETTAGLIO)
-- =====================================================
CREATE TABLE righe_documenti (
    id BIGSERIAL PRIMARY KEY,
    documento_id BIGINT NOT NULL,
    riga_numero INTEGER NOT NULL,
    
    -- Articolo/Servizio
    articolo_id BIGINT,
    codice_articolo VARCHAR(50),
    descrizione VARCHAR(255) NOT NULL,
    descrizione_estesa TEXT,
    unita_misura VARCHAR(10) DEFAULT 'NR',
    
    -- Quantità e prezzi
    quantita DECIMAL(15,3) NOT NULL DEFAULT 1,
    prezzo_unitario DECIMAL(15,4) NOT NULL,
    sconto1 DECIMAL(5,2) DEFAULT 0,
    sconto2 DECIMAL(5,2) DEFAULT 0,
    sconto3 DECIMAL(5,2) DEFAULT 0,
    
    -- IVA
    aliquota_iva_id BIGINT NOT NULL,
    percentuale_iva DECIMAL(5,2) NOT NULL,
    natura_iva VARCHAR(10), -- Per esenzioni/non imponibili
    
    -- Totali riga (calcolati)
    imponibile_riga DECIMAL(15,2) NOT NULL,
    iva_riga DECIMAL(15,2) NOT NULL,
    totale_riga DECIMAL(15,2) NOT NULL,
    
    -- Dati aggiuntivi
    note_riga TEXT,
    
    -- Audit
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraints
    CONSTRAINT chk_righe_quantita_positiva CHECK (quantita > 0),
    CONSTRAINT chk_righe_prezzo_positivo CHECK (prezzo_unitario >= 0),
    CONSTRAINT chk_righe_sconti_validi CHECK (sconto1 >= 0 AND sconto1 <= 100 AND sconto2 >= 0 AND sconto2 <= 100 AND sconto3 >= 0 AND sconto3 <= 100),
    CONSTRAINT chk_righe_totali_positivi CHECK (imponibile_riga >= 0 AND iva_riga >= 0 AND totale_riga >= 0),
    
    -- Unique constraint per riga
    CONSTRAINT uk_righe_documento_numero UNIQUE(documento_id, riga_numero)
);

-- =====================================================
-- TABELLA RIEPILOGHI_IVA
-- =====================================================
CREATE TABLE riepiloghi_iva (
    id BIGSERIAL PRIMARY KEY,
    documento_id BIGINT NOT NULL,
    aliquota_iva_id BIGINT NOT NULL,
    percentuale_iva DECIMAL(5,2) NOT NULL,
    natura_iva VARCHAR(10),
    imponibile DECIMAL(15,2) NOT NULL,
    imposta DECIMAL(15,2) NOT NULL,
    
    -- Constraints
    CONSTRAINT chk_riepiloghi_imponibile_positivo CHECK (imponibile >= 0),
    CONSTRAINT chk_riepiloghi_imposta_positiva CHECK (imposta >= 0),
    
    -- Unique constraint per aliquota
    CONSTRAINT uk_riepiloghi_documento_aliquota UNIQUE(documento_id, aliquota_iva_id)
);

-- =====================================================
-- TABELLA SCADENZE
-- =====================================================
CREATE TABLE scadenze (
    id BIGSERIAL PRIMARY KEY,
    documento_id BIGINT NOT NULL,
    numero_rata INTEGER NOT NULL DEFAULT 1,
    data_scadenza DATE NOT NULL,
    importo DECIMAL(15,2) NOT NULL,
    importo_pagato DECIMAL(15,2) DEFAULT 0,
    saldato BOOLEAN DEFAULT false,
    data_saldo DATE,
    
    -- Modalità pagamento
    modalita_pagamento_id BIGINT,
    banca VARCHAR(255),
    iban VARCHAR(34),
    
    -- Note
    note TEXT,
    
    -- Audit
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraints
    CONSTRAINT chk_scadenze_importo_positivo CHECK (importo > 0),
    CONSTRAINT chk_scadenze_pagato_positivo CHECK (importo_pagato >= 0),
    CONSTRAINT chk_scadenze_pagato_max CHECK (importo_pagato <= importo),
    
    -- Unique constraint per rata
    CONSTRAINT uk_scadenze_documento_rata UNIQUE(documento_id, numero_rata)
);

-- =====================================================
-- FOREIGN KEYS
-- =====================================================

-- Documenti
ALTER TABLE documenti ADD CONSTRAINT fk_documenti_soggetto 
    FOREIGN KEY (soggetto_id) REFERENCES soggetti(id);

ALTER TABLE documenti ADD CONSTRAINT fk_documenti_modalita_pagamento 
    FOREIGN KEY (modalita_pagamento_id) REFERENCES modalita_pagamento(id);

ALTER TABLE documenti ADD CONSTRAINT fk_documenti_causale_trasporto 
    FOREIGN KEY (causale_trasporto_id) REFERENCES causali_trasporto(id);

ALTER TABLE documenti ADD CONSTRAINT fk_documenti_origine 
    FOREIGN KEY (documento_origine_id) REFERENCES documenti(id);

-- Righe documenti
ALTER TABLE righe_documenti ADD CONSTRAINT fk_righe_documento 
    FOREIGN KEY (documento_id) REFERENCES documenti(id) ON DELETE CASCADE;

ALTER TABLE righe_documenti ADD CONSTRAINT fk_righe_articolo 
    FOREIGN KEY (articolo_id) REFERENCES articoli(id);

ALTER TABLE righe_documenti ADD CONSTRAINT fk_righe_aliquota_iva 
    FOREIGN KEY (aliquota_iva_id) REFERENCES aliquote_iva(id);

-- Riepiloghi IVA
ALTER TABLE riepiloghi_iva ADD CONSTRAINT fk_riepiloghi_documento 
    FOREIGN KEY (documento_id) REFERENCES documenti(id) ON DELETE CASCADE;

ALTER TABLE riepiloghi_iva ADD CONSTRAINT fk_riepiloghi_aliquota_iva 
    FOREIGN KEY (aliquota_iva_id) REFERENCES aliquote_iva(id);

-- Scadenze
ALTER TABLE scadenze ADD CONSTRAINT fk_scadenze_documento 
    FOREIGN KEY (documento_id) REFERENCES documenti(id) ON DELETE CASCADE;

ALTER TABLE scadenze ADD CONSTRAINT fk_scadenze_modalita_pagamento 
    FOREIGN KEY (modalita_pagamento_id) REFERENCES modalita_pagamento(id);

-- =====================================================
-- INDICI PER PERFORMANCE
-- =====================================================

-- Indici per ricerche frequenti
CREATE INDEX idx_documenti_tipo_data ON documenti(tipo_documento, data_documento);
CREATE INDEX idx_documenti_soggetto ON documenti(soggetto_id);
CREATE INDEX idx_documenti_numero_anno ON documenti(numero, anno);
CREATE INDEX idx_documenti_stato ON documenti(stato);
CREATE INDEX idx_documenti_data_registrazione ON documenti(data_registrazione);

-- Indici per righe
CREATE INDEX idx_righe_documento ON righe_documenti(documento_id);
CREATE INDEX idx_righe_articolo ON righe_documenti(articolo_id);

-- Indici per scadenze
CREATE INDEX idx_scadenze_documento ON scadenze(documento_id);
CREATE INDEX idx_scadenze_data ON scadenze(data_scadenza);
CREATE INDEX idx_scadenze_saldato ON scadenze(saldato);

-- =====================================================
-- TRIGGER PER AGGIORNAMENTO AUTOMATICO
-- =====================================================

-- Trigger per aggiornamento updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_documenti_updated_at BEFORE UPDATE ON documenti
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_righe_documenti_updated_at BEFORE UPDATE ON righe_documenti
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_numerazioni_updated_at BEFORE UPDATE ON numerazioni
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- =====================================================
-- DATI CONFIGURAZIONE BASE
-- =====================================================

-- Inserimento numerazioni base per anno corrente
INSERT INTO numerazioni (tipo_documento, anno, ultimo_numero) VALUES
('PREVENTIVO', EXTRACT(YEAR FROM CURRENT_DATE), 0),
('ORDINE', EXTRACT(YEAR FROM CURRENT_DATE), 0),
('DDT', EXTRACT(YEAR FROM CURRENT_DATE), 0),
('FATTURA', EXTRACT(YEAR FROM CURRENT_DATE), 0),
('NOTA_CREDITO', EXTRACT(YEAR FROM CURRENT_DATE), 0),
('NOTA_DEBITO', EXTRACT(YEAR FROM CURRENT_DATE), 0),
('RICEVUTA', EXTRACT(YEAR FROM CURRENT_DATE), 0);

-- =====================================================
-- VISTE PER REPORTING
-- =====================================================

-- Vista per documenti con dettagli soggetto
CREATE VIEW v_documenti_completi AS
SELECT 
    d.id,
    d.tipo_documento,
    d.numero,
    d.anno,
    d.data_documento,
    d.stato,
    d.totale_documento,
    s.ragione_sociale,
    s.partita_iva,
    s.codice_fiscale,
    s.citta,
    s.provincia,
    mp.descrizione as modalita_pagamento,
    d.note
FROM documenti d
JOIN soggetti s ON d.soggetto_id = s.id
LEFT JOIN modalita_pagamento mp ON d.modalita_pagamento_id = mp.id;

-- Vista per scadenzario
CREATE VIEW v_scadenzario AS
SELECT 
    sc.id,
    sc.data_scadenza,
    sc.importo,
    sc.importo_pagato,
    sc.saldato,
    d.tipo_documento,
    d.numero,
    d.anno,
    d.data_documento,
    s.ragione_sociale,
    s.partita_iva,
    mp.descrizione as modalita_pagamento,
    (sc.importo - sc.importo_pagato) as residuo
FROM scadenze sc
JOIN documenti d ON sc.documento_id = d.id
JOIN soggetti s ON d.soggetto_id = s.id
LEFT JOIN modalita_pagamento mp ON sc.modalita_pagamento_id = mp.id;

-- Vista per fatturato
CREATE VIEW v_fatturato AS
SELECT 
    d.tipo_documento,
    EXTRACT(YEAR FROM d.data_documento) as anno,
    EXTRACT(MONTH FROM d.data_documento) as mese,
    COUNT(*) as numero_documenti,
    SUM(d.totale_imponibile) as totale_imponibile,
    SUM(d.totale_iva) as totale_iva,
    SUM(d.totale_documento) as totale_documento
FROM documenti d
WHERE d.stato NOT IN ('BOZZA', 'ANNULLATO')
GROUP BY d.tipo_documento, EXTRACT(YEAR FROM d.data_documento), EXTRACT(MONTH FROM d.data_documento);

-- =====================================================
-- COMMENTI TABELLE
-- =====================================================

COMMENT ON TABLE documenti IS 'Testata documenti commerciali (fatture, DDT, preventivi, etc.)';
COMMENT ON TABLE righe_documenti IS 'Dettaglio righe documenti con articoli e importi';
COMMENT ON TABLE riepiloghi_iva IS 'Riepiloghi IVA per aliquota';
COMMENT ON TABLE scadenze IS 'Scadenze documenti per gestione incassi/pagamenti';
COMMENT ON TABLE numerazioni IS 'Numerazioni progressive per tipo documento';

-- =====================================================
-- FINE MIGRATION
-- =====================================================
