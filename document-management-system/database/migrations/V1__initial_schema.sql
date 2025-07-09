-- Schema iniziale del database per il sistema di gestione documentale

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