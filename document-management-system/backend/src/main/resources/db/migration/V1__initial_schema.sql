-- Migrazione iniziale dello schema del database

-- Tabella Anagrafica Azienda
CREATE TABLE azienda (
    id BIGSERIAL PRIMARY KEY,
    ragione_sociale VARCHAR(255) NOT NULL,
    partita_iva VARCHAR(11) NOT NULL UNIQUE,
    indirizzo VARCHAR(255),
    telefono VARCHAR(15),
    email VARCHAR(255) UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabella Documenti
CREATE TABLE documento (
    id BIGSERIAL PRIMARY KEY,
    titolo VARCHAR(255) NOT NULL,
    descrizione TEXT,
    data_creazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    azienda_id BIGINT REFERENCES azienda(id) ON DELETE CASCADE,
    tipo_documento VARCHAR(50) NOT NULL
);

-- Tabella Utenti
CREATE TABLE utente (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    cognome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    ruolo VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabella Configurazioni Sistema
CREATE TABLE configurazioni (
    id BIGSERIAL PRIMARY KEY,
    chiave VARCHAR(100) UNIQUE NOT NULL,
    valore TEXT,
    descrizione TEXT,
    tipo VARCHAR(20) DEFAULT 'STRING',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabella Clienti/Fornitori
CREATE TABLE clienti (
    id BIGSERIAL PRIMARY KEY,
    ragione_sociale VARCHAR(255) NOT NULL,
    partita_iva VARCHAR(11) UNIQUE,
    codice_fiscale VARCHAR(16),
    indirizzo VARCHAR(255),
    citta VARCHAR(100),
    cap VARCHAR(5),
    provincia VARCHAR(2),
    telefono VARCHAR(15),
    email VARCHAR(255),
    pec VARCHAR(255),
    tipo VARCHAR(20) DEFAULT 'CLIENTE',
    attivo BOOLEAN DEFAULT true,
    note TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Tabella Articoli
CREATE TABLE articoli (
    id BIGSERIAL PRIMARY KEY,
    codice VARCHAR(50) UNIQUE NOT NULL,
    descrizione VARCHAR(255) NOT NULL,
    descrizione_estesa TEXT,
    prezzo DECIMAL(10,2) DEFAULT 0.00,
    costo DECIMAL(10,2) DEFAULT 0.00,
    aliquota_iva DECIMAL(5,2) DEFAULT 22.00,
    unita_misura VARCHAR(10) DEFAULT 'PZ',
    giacenza DECIMAL(10,3) DEFAULT 0.000,
    giacenza_minima DECIMAL(10,3) DEFAULT 0.000,
    tipo VARCHAR(20) DEFAULT 'PRODOTTO',
    attivo BOOLEAN DEFAULT true,
    categoria VARCHAR(100),
    fornitore VARCHAR(255),
    codice_fornitore VARCHAR(50),
    note TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);
