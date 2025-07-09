export interface Documento {
  id: number;
  titolo: string;
  descrizione?: string;
  dataCreazione: string;
  tipoDocumento: string;
  azienda: {
    id: number;
    ragioneSociale: string;
  };
}

export interface CreateDocumentoRequest {
  titolo: string;
  descrizione?: string;
  tipoDocumento: string;
  aziendaId: number;
}

export interface UpdateDocumentoRequest {
  titolo?: string;
  descrizione?: string;
  tipoDocumento?: string;
}
