// Types per il sistema di gestione documenti - Allineati con backend DTO

export enum TipoDocumento {
  FATTURA = 'FATTURA',
  FATTURA_ELETTRONICA = 'FATTURA_ELETTRONICA',
  NOTA_CREDITO = 'NOTA_CREDITO',
  NOTA_DEBITO = 'NOTA_DEBITO',
  DDT = 'DDT',
  PREVENTIVO = 'PREVENTIVO',
  ORDINE = 'ORDINE',
  RICEVUTA = 'RICEVUTA'
}

export enum StatoDocumento {
  BOZZA = 'BOZZA',
  EMESSO = 'EMESSO',
  STAMPATO = 'STAMPATO',
  INVIATO = 'INVIATO',
  PAGATO = 'PAGATO',
  ANNULLATO = 'ANNULLATO'
}

export enum AliquotaIva {
  ESENTE = 0,
  RIDOTTA_4 = 4,
  RIDOTTA_5 = 5,
  RIDOTTA_10 = 10,
  ORDINARIA_22 = 22
}

// Entità principali
export interface Soggetto {
  id: number;
  ragioneSociale: string;
  partitaIva?: string;
  codiceFiscale?: string;
  indirizzo?: string;
  cap?: string;
  citta?: string;
  provincia?: string;
  telefono?: string;
  email?: string;
  pec?: string;
  attivo: boolean;
}

export interface RigaDocumento {
  id?: number;
  articoloId?: number;
  descrizione: string;
  quantita: number;
  prezzoUnitario: number;
  scontoPercentuale: number;
  aliquotaIva: AliquotaIva;
  totaleRiga: number;
  totaleIva: number;
  totaleConIva: number;
}

export interface RiepilogoIva {
  aliquota: AliquotaIva;
  imponibile: number;
  imposta: number;
  totale: number;
}

export interface Scadenza {
  id?: number;
  dataScadenza: string;
  importo: number;
  pagato: boolean;
  dataPagamento?: string;
  note?: string;
}

export interface Documento {
  id: number;
  tipoDocumento: TipoDocumento;
  numero: number;
  anno: number;
  dataDocumento: string;
  dataScadenza?: string;
  soggetto: Soggetto;
  titolo: string;
  descrizione?: string;
  stato: StatoDocumento;
  
  // Totali
  totaleImponibile: number;
  totaleIva: number;
  totaleDocumento: number;
  
  // Righe e riepiloghi
  righe: RigaDocumento[];
  riepiloghi: RiepilogoIva[];
  scadenze: Scadenza[];
  
  // Campi specifici per tipo
  motivoNota?: string;
  documentoOrigineId?: number;
  causaleDescrizione?: string;
  dataTrasporto?: string;
  validitaOfferta?: string;
  codiceDestinatario?: string;
  pecDestinatario?: string;
  
  // Audit
  inviato: boolean;
  dataInvio?: string;
  stampato: boolean;
  dataStampa?: string;
  dataCreazione: string;
  dataModifica: string;
}

// DTO per API
export interface DocumentoCreateDto {
  tipoDocumento: TipoDocumento;
  soggettoId: number;
  dataDocumento: string;
  dataScadenza?: string;
  titolo: string;
  descrizione?: string;
  note?: string;
  righe?: RigaDocumentoDto[];
  scadenze?: ScadenzaDto[];
  
  // Campi specifici
  motivoNota?: string;
  documentoOrigineId?: number;
  causaleDescrizione?: string;
  dataTrasporto?: string;
  validitaOfferta?: string;
  codiceDestinatario?: string;
  pecDestinatario?: string;
}

export interface RigaDocumentoDto {
  articoloId?: number;
  descrizione: string;
  quantita: number;
  prezzoUnitario: number;
  scontoPercentuale: number;
  aliquotaIva: AliquotaIva;
}

export interface ScadenzaDto {
  dataScadenza: string;
  importo: number;
  note?: string;
}

export interface DocumentoResponseDto extends Documento {}

// Filtri per ricerca
export interface DocumentoFilter {
  tipoDocumento?: TipoDocumento;
  statoDocumento?: StatoDocumento;
  dataInizio?: string;
  dataFine?: string;
  soggettoId?: number;
  soggettoFilter?: string;
  numeroDocumento?: string;
  inviato?: boolean;
  stampato?: boolean;
  partitaIva?: string;
  codiceFiscale?: string;
}

// Response paginate
export interface PagedResponse<T> {
  content: T[];
  pageable: {
    pageNumber: number;
    pageSize: number;
    sort: {
      sorted: boolean;
      direction: string;
      property: string;
    };
  };
  totalElements: number;
  totalPages: number;
  last: boolean;
  first: boolean;
  numberOfElements: number;
  size: number;
  number: number;
}

export type DocumentiPagedResponse = PagedResponse<DocumentoResponseDto>;
