// API service per gestione articoli fornitori - Integrazione con backend Spring Boot

import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';
import {
  ArticoloFornitore,
  ArticoloFornitoreCreateDto,
  ArticoloFornitoreUpdateDto,
  ArticoliFornitoriPagedResponse,
  ArticoloFornitoreFilter,
  ApiResponse
} from '../../types/entities';

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

export const articoliFornitoriApi = createApi({
  reducerPath: 'articoliFornitoriApi',
  baseQuery,
  tagTypes: ['ArticoloFornitore'],
  endpoints: (builder) => ({
    
    // ========== CRUD ARTICOLI FORNITORI ==========
    
    // Recupera articoli fornitori con filtri e paginazione
    getArticoliFornitori: builder.query<ArticoliFornitoriPagedResponse, {
      page?: number;
      size?: number;
      sortBy?: string;
      sortDir?: 'asc' | 'desc';
      filter?: ArticoloFornitoreFilter;
    }>({
      query: ({ page = 0, size = 20, sortBy = 'dataDocumento', sortDir = 'desc', filter = {} }) => {
        const params = new URLSearchParams({
          page: page.toString(),
          size: size.toString(),
          sortBy,
          sortDir,
          ...Object.fromEntries(
            Object.entries(filter || {})
              .filter(([, value]) => value !== undefined && value !== null && value !== '')
              .map(([key, value]) => [key, String(value)])
          )
        });
        return `articoli-fornitori?${params}`;
      },
      providesTags: (result) =>
        result && result.content
          ? [
              ...result.content.map(({ id }) => ({ type: 'ArticoloFornitore' as const, id })),
              { type: 'ArticoloFornitore', id: 'LIST' },
            ]
          : [{ type: 'ArticoloFornitore', id: 'LIST' }],
    }),

    // Recupera tutti gli articoli fornitori (senza paginazione)
    getAllArticoliFornitori: builder.query<ArticoloFornitore[], void>({
      query: () => 'articoli-fornitori/all',
      providesTags: [{ type: 'ArticoloFornitore', id: 'LIST' }],
    }),

    // Recupera singolo articolo fornitore per ID
    getArticoloFornitore: builder.query<ArticoloFornitore, number>({
      query: (id) => `articoli-fornitori/${id}`,
      providesTags: (result, error, id) => [{ type: 'ArticoloFornitore', id }],
    }),

    // Recupera articoli per fornitore
    getArticoliByFornitore: builder.query<ArticoloFornitore[], string>({
      query: (partitaIva) => `articoli-fornitori/fornitore/${partitaIva}`,
      providesTags: (result, error, partitaIva) => [{ type: 'ArticoloFornitore', id: `FORNITORE_${partitaIva}` }],
    }),

    // Crea nuovo articolo fornitore
    createArticoloFornitore: builder.mutation<ApiResponse<ArticoloFornitore>, ArticoloFornitoreCreateDto>({
      query: (articoloFornitore) => ({
        url: 'articoli-fornitori',
        method: 'POST',
        body: articoloFornitore,
      }),
      invalidatesTags: [{ type: 'ArticoloFornitore', id: 'LIST' }],
    }),

    // Aggiorna articolo fornitore esistente
    updateArticoloFornitore: builder.mutation<ApiResponse<ArticoloFornitore>, ArticoloFornitoreUpdateDto>({
      query: ({ id, ...resto }) => ({
        url: `articoli-fornitori/${id}`,
        method: 'PUT',
        body: resto,
      }),
      invalidatesTags: (result, error, { id }) => [
        { type: 'ArticoloFornitore', id },
        { type: 'ArticoloFornitore', id: 'LIST' },
      ],
    }),

    // Elimina articolo fornitore
    deleteArticoloFornitore: builder.mutation<ApiResponse<void>, number>({
      query: (id) => ({
        url: `articoli-fornitori/${id}`,
        method: 'DELETE',
      }),
      invalidatesTags: (result, error, id) => [
        { type: 'ArticoloFornitore', id },
        { type: 'ArticoloFornitore', id: 'LIST' },
      ],
    }),

    // ========== OPERAZIONI SPECIFICHE ==========

    // Cerca articoli fornitori per descrizione o codice
    searchArticoliFornitori: builder.query<ArticoloFornitore[], string>({
      query: (searchTerm) => `articoli-fornitori/search?q=${encodeURIComponent(searchTerm)}`,
      providesTags: [{ type: 'ArticoloFornitore', id: 'SEARCH' }],
    }),

    // Recupera fornitori unici
    getFornitori: builder.query<Array<{
      partitaIva: string;
      ragioneSociale: string;
      categoria?: string;
      articoliCount: number;
    }>, void>({
      query: () => 'articoli-fornitori/fornitori',
      providesTags: [{ type: 'ArticoloFornitore', id: 'FORNITORI' }],
    }),

    // Recupera articoli fornitori per categoria
    getArticoliByCategoria: builder.query<ArticoloFornitore[], string>({
      query: (categoria) => `articoli-fornitori/categoria/${categoria}`,
      providesTags: (result, error, categoria) => [{ type: 'ArticoloFornitore', id: `CATEGORIA_${categoria}` }],
    }),

    // Recupera storico prezzi per articolo
    getStoricoPrezzi: builder.query<Array<{
      data: string;
      prezzo: number;
      quantita: number;
      fornitore: string;
    }>, { codice: string; fornitorePartitaIva?: string }>({
      query: ({ codice, fornitorePartitaIva }) => {
        const params = new URLSearchParams({ codice });
        if (fornitorePartitaIva) {
          params.append('fornitorePartitaIva', fornitorePartitaIva);
        }
        return `articoli-fornitori/storico-prezzi?${params}`;
      },
    }),

    // ========== STATISTICHE ==========

    // Recupera statistiche articoli fornitori
    getStatisticheArticoliFornitori: builder.query<{
      totaleArticoli: number;
      totaleFornitori: number;
      totaleValore: number;
      articoliPerCategoria: Record<string, number>;
      ultimiAggiornamentiCount: number;
    }, void>({
      query: () => 'articoli-fornitori/statistiche',
    }),

    // ========== IMPORTAZIONE ==========

    // Importa articoli fornitori da file JSON
    importArticoliFornitori: builder.mutation<ApiResponse<{ 
      importati: number; 
      errori: number; 
      dettagli: string[] 
    }>, FormData>({
      query: (formData) => ({
        url: 'articoli-fornitori/import',
        method: 'POST',
        body: formData,
      }),
      invalidatesTags: [{ type: 'ArticoloFornitore', id: 'LIST' }],
    }),

    // ========== ANALISI E REPORTING ==========

    // Recupera andamento prezzi per codice articolo
    getAndamentoPrezzi: builder.query<Array<{
      mese: string;
      prezzoMedio: number;
      quantitaTotale: number;
      numeroFornitori: number;
    }>, { codice: string; mesi?: number }>({
      query: ({ codice, mesi = 12 }) => `articoli-fornitori/andamento-prezzi?codice=${codice}&mesi=${mesi}`,
    }),

    // Confronta prezzi tra fornitori per stesso articolo
    confrontaPrezzi: builder.query<Array<{
      fornitore: string;
      prezzoUnitario: number;
      quantitaMinima: number;
      dataUltimoOrdine: string;
      disponibilita: string;
    }>, string>({
      query: (codice) => `articoli-fornitori/confronta-prezzi/${codice}`,
    }),

  }),
});

// Export degli hook generati automaticamente
export const {
  useGetArticoliFornitoriQuery,
  useGetAllArticoliFornitoriQuery,
  useGetArticoloFornitoreQuery,
  useGetArticoliByFornitoreQuery,
  useCreateArticoloFornitoreMutation,
  useUpdateArticoloFornitoreMutation,
  useDeleteArticoloFornitoreMutation,
  useSearchArticoliFornitoriQuery,
  useGetFornitoriQuery,
  useGetArticoliByCategoriaQuery,
  useGetStoricoPrezziQuery,
  useGetStatisticheArticoliFornitoriQuery,
  useImportArticoliFornitoriMutation,
  useGetAndamentoPrezziQuery,
  useConfrontaPrezziQuery,
} = articoliFornitoriApi;
