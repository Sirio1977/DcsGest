// API service per gestione documenti - Integrazione con backend Spring Boot

import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';
import { 
  Documento, 
  DocumentoCreateDto, 
  DocumentoResponseDto,
  DocumentiPagedResponse,
  DocumentoFilter,
  TipoDocumento,
  StatoDocumento
} from '../../types/documento';

// Base query con configurazione per backend Spring Boot
const baseQuery = fetchBaseQuery({
  baseUrl: 'http://localhost:8080/api/',
  prepareHeaders: (headers) => {
    headers.set('Content-Type', 'application/json');
    // Aggiungiamo headers CORS se necessario
    headers.set('Access-Control-Allow-Origin', '*');
    return headers;
  },
});

export const documentiApi = createApi({
  reducerPath: 'documentiApi',
  baseQuery,
  tagTypes: ['Documento', 'Documenti'],
  endpoints: (builder) => ({
    
    // ========== CRUD DOCUMENTI ==========
    
    // Recupera documenti con filtri e paginazione
    getDocumenti: builder.query<DocumentiPagedResponse, {
      page?: number;
      size?: number;
      sortBy?: string;
      sortDir?: 'asc' | 'desc';
      filter?: DocumentoFilter;
    }>({
      query: (params = {}) => {
        const { page = 0, size = 20, sortBy = 'dataDocumento', sortDir = 'desc', filter = {} } = params;
        const searchParams = new URLSearchParams({
          page: page.toString(),
          size: size.toString(),
          sortBy,
          sortDir,
          ...Object.fromEntries(
            Object.entries(filter).map(([key, value]) => [key, String(value)])
          )
        });
        return `documenti?${searchParams}`;
      },
      providesTags: ['Documenti'],
    }),

    // Recupera documento per ID
    getDocumento: builder.query<DocumentoResponseDto, number>({
      query: (id) => `documenti/${id}`,
      providesTags: (result, error, id) => [{ type: 'Documento', id }],
    }),

    // Crea nuovo documento
    createDocumento: builder.mutation<DocumentoResponseDto, DocumentoCreateDto>({
      query: (documento) => ({
        url: 'documenti',
        method: 'POST',
        body: documento,
      }),
      invalidatesTags: ['Documenti'],
    }),

    // Aggiorna documento esistente
    updateDocumento: builder.mutation<DocumentoResponseDto, { id: number; documento: DocumentoCreateDto }>({
      query: ({ id, documento }) => ({
        url: `documenti/${id}`,
        method: 'PUT',
        body: documento,
      }),
      invalidatesTags: (result, error, { id }) => [
        { type: 'Documento', id },
        'Documenti'
      ],
    }),

    // Elimina documento
    deleteDocumento: builder.mutation<void, number>({
      query: (id) => ({
        url: `documenti/${id}`,
        method: 'DELETE',
      }),
      invalidatesTags: (result, error, id) => [
        { type: 'Documento', id },
        'Documenti'
      ],
    }),

    // ========== GESTIONE STATI ==========

    // Cambia stato documento
    cambiaStatoDocumento: builder.mutation<DocumentoResponseDto, { id: number; nuovoStato: StatoDocumento }>({
      query: ({ id, nuovoStato }) => ({
        url: `documenti/${id}/stato?nuovoStato=${nuovoStato}`,
        method: 'PATCH',
      }),
      invalidatesTags: (result, error, { id }) => [
        { type: 'Documento', id },
        'Documenti'
      ],
    }),

    // Duplica documento
    duplicaDocumento: builder.mutation<DocumentoResponseDto, { id: number; nuovoTipo: TipoDocumento }>({
      query: ({ id, nuovoTipo }) => ({
        url: `documenti/${id}/duplica?nuovoTipo=${nuovoTipo}`,
        method: 'POST',
      }),
      invalidatesTags: ['Documenti'],
    }),

    // ========== UTILITIES ==========

    // Genera PDF
    generaPdfDocumento: builder.mutation<Blob, number>({
      query: (id) => ({
        url: `documenti/${id}/pdf`,
        method: 'GET',
        responseHandler: (response) => response.blob(),
      }),
    }),

    // Invia documento via email
    inviaDocumento: builder.mutation<DocumentoResponseDto, { id: number; emailDestinatario: string }>({
      query: ({ id, emailDestinatario }) => ({
        url: `documenti/${id}/invia?emailDestinatario=${emailDestinatario}`,
        method: 'POST',
      }),
      invalidatesTags: (result, error, { id }) => [
        { type: 'Documento', id },
        'Documenti'
      ],
    }),

    // ========== ENDPOINT SPECIFICI PER TIPO ==========

    // Fatture
    createFattura: builder.mutation<DocumentoResponseDto, DocumentoCreateDto>({
      query: (fattura) => ({
        url: 'documenti/fatture',
        method: 'POST',
        body: fattura,
      }),
      invalidatesTags: ['Documenti'],
    }),

    getFatture: builder.query<DocumentiPagedResponse, {
      page?: number;
      size?: number;
      sortBy?: string;
      sortDir?: 'asc' | 'desc';
      statoDocumento?: StatoDocumento;
      dataInizio?: string;
      dataFine?: string;
    }>({
      query: ({ page = 0, size = 20, sortBy = 'dataDocumento', sortDir = 'desc', ...filters }) => {
        const params = new URLSearchParams({
          page: page.toString(),
          size: size.toString(),
          sortBy,
          sortDir,
          ...Object.fromEntries(
            Object.entries(filters).map(([key, value]) => [key, String(value)])
          )
        });
        return `documenti/fatture?${params}`;
      },
      providesTags: ['Documenti'],
    }),

    // Preventivi
    createPreventivo: builder.mutation<DocumentoResponseDto, DocumentoCreateDto>({
      query: (preventivo) => ({
        url: 'documenti/preventivi',
        method: 'POST',
        body: preventivo,
      }),
      invalidatesTags: ['Documenti'],
    }),

    getPreventivi: builder.query<DocumentiPagedResponse, {
      page?: number;
      size?: number;
      sortBy?: string;
      sortDir?: 'asc' | 'desc';
      statoDocumento?: StatoDocumento;
      dataInizio?: string;
      dataFine?: string;
    }>({
      query: ({ page = 0, size = 20, sortBy = 'dataDocumento', sortDir = 'desc', ...filters }) => {
        const params = new URLSearchParams({
          page: page.toString(),
          size: size.toString(),
          sortBy,
          sortDir,
          ...Object.fromEntries(
            Object.entries(filters).map(([key, value]) => [key, String(value)])
          )
        });
        return `documenti/preventivi?${params}`;
      },
      providesTags: ['Documenti'],
    }),

    // DDT
    createDdt: builder.mutation<DocumentoResponseDto, DocumentoCreateDto>({
      query: (ddt) => ({
        url: 'documenti/ddt',
        method: 'POST',
        body: ddt,
      }),
      invalidatesTags: ['Documenti'],
    }),

    getDdt: builder.query<DocumentiPagedResponse, {
      page?: number;
      size?: number;
      sortBy?: string;
      sortDir?: 'asc' | 'desc';
      statoDocumento?: StatoDocumento;
      dataInizio?: string;
      dataFine?: string;
    }>({
      query: ({ page = 0, size = 20, sortBy = 'dataDocumento', sortDir = 'desc', ...filters }) => {
        const params = new URLSearchParams({
          page: page.toString(),
          size: size.toString(),
          sortBy,
          sortDir,
          ...Object.fromEntries(
            Object.entries(filters).map(([key, value]) => [key, String(value)])
          )
        });
        return `documenti/ddt?${params}`;
      },
      providesTags: ['Documenti'],
    }),

    // Note di Credito
    createNotaCredito: builder.mutation<DocumentoResponseDto, DocumentoCreateDto>({
      query: (notaCredito) => ({
        url: 'documenti/note-credito',
        method: 'POST',
        body: notaCredito,
      }),
      invalidatesTags: ['Documenti'],
    }),

    getNoteCredito: builder.query<DocumentiPagedResponse, {
      page?: number;
      size?: number;
      sortBy?: string;
      sortDir?: 'asc' | 'desc';
      statoDocumento?: StatoDocumento;
      dataInizio?: string;
      dataFine?: string;
    }>({
      query: ({ page = 0, size = 20, sortBy = 'dataDocumento', sortDir = 'desc', ...filters }) => {
        const params = new URLSearchParams({
          page: page.toString(),
          size: size.toString(),
          sortBy,
          sortDir,
          ...Object.fromEntries(
            Object.entries(filters).map(([key, value]) => [key, String(value)])
          )
        });
        return `documenti/note-credito?${params}`;
      },
      providesTags: ['Documenti'],
    }),

  }),
});

export const {
  // Query hooks
  useGetDocumentiQuery,
  useGetDocumentoQuery,
  useGetFattureQuery,
  useGetPreventiviQuery,
  useGetDdtQuery,
  useGetNoteCreditoQuery,
  
  // Mutation hooks
  useCreateDocumentoMutation,
  useUpdateDocumentoMutation,
  useDeleteDocumentoMutation,
  useCambiaStatoDocumentoMutation,
  useDuplicaDocumentoMutation,
  useGeneraPdfDocumentoMutation,
  useInviaDocumentoMutation,
  
  // Tipo-specifici
  useCreateFatturaMutation,
  useCreatePreventivoMutation,
  useCreateDdtMutation,
  useCreateNotaCreditoMutation,
} = documentiApi;
