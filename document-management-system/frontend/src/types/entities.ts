// Tipi TypeScript per le entità del sistema gestionale

// ========== ENUM TYPES ==========
export enum TipoArticolo {
  PRODOTTO = 'PRODOTTO',
  SERVIZIO = 'SERVIZIO',
  MATERIA_PRIMA = 'MATERIA_PRIMA'
}

export enum TipoCliente {
  CLIENTE = 'CLIENTE',
  FORNITORE = 'FORNITORE',
  CLIENTE_FORNITORE = 'CLIENTE_FORNITORE'
}

// ========== ARTICOLO TYPES ==========
export interface Articolo {
  id?: number;
  codice: string;
  descrizione: string;
  descrizioneEstesa?: string;
  prezzoVendita: number;
  costo: number;
  aliquotaIva: number;
  unitaMisura: string;
  giacenza: number;
  giacenzaMinima: number;
  tipo: TipoArticolo;
  attivo: boolean;
  note?: string;
  codiceFornitore?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface ArticoloCreateDto {
  codice: string;
  descrizione: string;
  descrizioneEstesa?: string;
  prezzoVendita: number;
  costo: number;
  aliquotaIva: number;
  unitaMisura: string;
  giacenza?: number;
  giacenzaMinima?: number;
  tipo: TipoArticolo;
  attivo?: boolean;
  note?: string;
  codiceFornitore?: string;
}

export interface ArticoloUpdateDto extends Partial<ArticoloCreateDto> {
  id: number;
}

// ========== CLIENTE TYPES ==========
export interface Cliente {
  id?: number;
  ragioneSociale: string;
  partitaIva?: string;
  codiceFiscale?: string;
  indirizzo?: string;
  citta?: string;
  cap?: string;
  provincia?: string;
  telefono?: string;
  email?: string;
  pec?: string;
  tipo: TipoCliente;
  attivo: boolean;
  note?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface ClienteCreateDto {
  ragioneSociale: string;
  partitaIva?: string;
  codiceFiscale?: string;
  indirizzo?: string;
  citta?: string;
  cap?: string;
  provincia?: string;
  telefono?: string;
  email?: string;
  pec?: string;
  tipo: TipoCliente;
  attivo?: boolean;
  note?: string;
}

export interface ClienteUpdateDto extends Partial<ClienteCreateDto> {
  id: number;
}

// ========== ARTICOLO FORNITORE TYPES ==========
export interface Fornitore {
  partitaIva: string;
  ragioneSociale: string;
  categoria?: string;
}

export interface ArticoloFornitore {
  id?: number;
  codice: string;
  descrizione: string;
  quantita: number;
  prezzoUnitario: number;
  importo: number;
  unitaMisura: string;
  aliquotaIVA: number;
  fornitorePartitaIva: string;
  fornitoreRagioneSociale: string;
  fornitoreCategoria?: string;
  dataDocumento?: string;
  dataUltimoAggiornamento?: string;
  codiceInterno?: string;
  prezziStorico?: PrezzoStorico[];
}

export interface PrezzoStorico {
  data: string;
  prezzo: number;
  quantita?: number;
}

export interface ArticoloFornitoreCreateDto {
  codice: string;
  descrizione: string;
  quantita: number;
  prezzoUnitario: number;
  unitaMisura: string;
  aliquotaIVA: number;
  fornitorePartitaIva: string;
  fornitoreRagioneSociale: string;
  fornitoreCategoria?: string;
  dataDocumento?: string;
  codiceInterno?: string;
}

export interface ArticoloFornitoreUpdateDto extends Partial<ArticoloFornitoreCreateDto> {
  id: number;
}

// ========== FILTER TYPES ==========
export interface ArticoloFilter {
  codice?: string;
  descrizione?: string;
  tipo?: TipoArticolo;
  attivo?: boolean;
  prezzoMin?: number;
  prezzoMax?: number;
}

export interface ClienteFilter {
  ragioneSociale?: string;
  partitaIva?: string;
  tipo?: TipoCliente;
  attivo?: boolean;
  citta?: string;
  provincia?: string;
}

export interface ArticoloFornitoreFilter {
  codice?: string;
  descrizione?: string;
  fornitorePartitaIva?: string;
  fornitoreRagioneSociale?: string;
  dataDocumentoDal?: string;
  dataDocumentoAl?: string;
  unitaMisura?: string;
}

// ========== RESPONSE TYPES ==========
export interface PagedResponse<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  numberOfElements: number;
  empty: boolean;
}

export type ArticoliPagedResponse = PagedResponse<Articolo>;
export type ClientiPagedResponse = PagedResponse<Cliente>;
export type ArticoliFornitoriPagedResponse = PagedResponse<ArticoloFornitore>;

// ========== API RESPONSE WRAPPERS ==========
export interface ApiResponse<T> {
  data: T;
  message?: string;
  success: boolean;
}

export interface ValidationError {
  field: string;
  message: string;
}

export interface ApiError {
  message: string;
  errors?: ValidationError[];
  timestamp: string;
  status: number;
}
