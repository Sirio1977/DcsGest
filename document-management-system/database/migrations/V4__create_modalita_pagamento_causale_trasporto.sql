-- Migration per creare le tabelle delle modalità di pagamento e causali di trasporto

-- Tabella modalità di pagamento
CREATE TABLE modalita_pagamento (
    id BIGSERIAL PRIMARY KEY,
    codice VARCHAR(50) NOT NULL UNIQUE,
    descrizione VARCHAR(255) NOT NULL,
    giorni_pagamento INTEGER,
    fine_mese BOOLEAN DEFAULT FALSE,
    numero_rate INTEGER DEFAULT 1,
    giorni_tra_rate INTEGER,
    attiva BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabella causale trasporto
CREATE TABLE causale_trasporto (
    id BIGSERIAL PRIMARY KEY,
    codice VARCHAR(50) NOT NULL UNIQUE,
    descrizione VARCHAR(255) NOT NULL,
    attiva BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabella fornitori
CREATE TABLE fornitori (
    id BIGSERIAL PRIMARY KEY,
    ragione_sociale VARCHAR(255) NOT NULL,
    partita_iva VARCHAR(20) UNIQUE,
    codice_fiscale VARCHAR(20),
    indirizzo VARCHAR(255),
    citta VARCHAR(100),
    cap VARCHAR(10),
    provincia VARCHAR(5),
    telefono VARCHAR(50),
    email VARCHAR(100),
    pec VARCHAR(100),
    attivo BOOLEAN DEFAULT TRUE,
    note TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indici per migliorare le performance
CREATE INDEX idx_modalita_pagamento_codice ON modalita_pagamento(codice);
CREATE INDEX idx_modalita_pagamento_attiva ON modalita_pagamento(attiva);
CREATE INDEX idx_causale_trasporto_codice ON causale_trasporto(codice);
CREATE INDEX idx_causale_trasporto_attiva ON causale_trasporto(attiva);
CREATE INDEX idx_fornitori_partita_iva ON fornitori(partita_iva);
CREATE INDEX idx_fornitori_codice_fiscale ON fornitori(codice_fiscale);
CREATE INDEX idx_fornitori_ragione_sociale ON fornitori(ragione_sociale);
CREATE INDEX idx_fornitori_attivo ON fornitori(attivo);

-- Inserimento dati di esempio per modalità di pagamento
INSERT INTO modalita_pagamento (codice, descrizione, giorni_pagamento, fine_mese, numero_rate, attiva) VALUES
('PAG_IMM', 'Pagamento Immediato', 0, FALSE, 1, TRUE),
('PAG_30', 'Pagamento a 30 giorni', 30, FALSE, 1, TRUE),
('PAG_60', 'Pagamento a 60 giorni', 60, FALSE, 1, TRUE),
('PAG_90', 'Pagamento a 90 giorni', 90, FALSE, 1, TRUE),
('PAG_30_FM', 'Pagamento a 30 giorni fine mese', 30, TRUE, 1, TRUE),
('PAG_60_FM', 'Pagamento a 60 giorni fine mese', 60, TRUE, 1, TRUE),
('PAG_30_60', 'Pagamento rateale 30-60 giorni', 30, FALSE, 2, TRUE),
('PAG_30_60_90', 'Pagamento rateale 30-60-90 giorni', 30, FALSE, 3, TRUE);

-- Inserimento dati di esempio per causali di trasporto
INSERT INTO causale_trasporto (codice, descrizione, attiva) VALUES
('VENDITA', 'Vendita', TRUE),
('CONTO_VISIONE', 'Conto visione', TRUE),
('CONTO_DEPOSITO', 'Conto deposito', TRUE),
('RIPARAZIONE', 'Riparazione', TRUE),
('RESO', 'Reso', TRUE),
('OMAGGIO', 'Omaggio', TRUE),
('CAMPIONE', 'Campione', TRUE),
('TRASFERIMENTO', 'Trasferimento', TRUE),
('PRESTITO', 'Prestito d\'uso', TRUE),
('CONSEGNA', 'Consegna', TRUE);
