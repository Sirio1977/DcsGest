// API service per gestione articoli - Integrazione con backend Spring Boot

import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';
import {
  Articolo,
  ArticoloCreateDto,
  ArticoloUpdateDto,
  ArticoliPagedResponse,
  ArticoloFilter,
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

export const articoliApi = createApi({
  reducerPath: 'articoliApi',
  baseQuery,
  tagTypes: ['Articolo'],
  endpoints: (builder) => ({
    
    // ========== CRUD ARTICOLI ==========
    
    // Recupera articoli con filtri e paginazione
    getArticoli: builder.query<ArticoliPagedResponse, {
      page?: number;
      size?: number;
      sortBy?: string;
      sortDir?: 'asc' | 'desc';
      filter?: ArticoloFilter;
    }>({
      query: ({ page = 0, size = 20, sortBy = 'descrizione', sortDir = 'asc', filter = {} }) => {
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
        return `articoli?${params}`;
      },
      providesTags: (result) =>
        result && result.content
          ? [
              ...result.content.map(({ id }) => ({ type: 'Articolo' as const, id })),
              { type: 'Articolo', id: 'LIST' },
            ]
          : [{ type: 'Articolo', id: 'LIST' }],
    }),

    // Recupera tutti gli articoli (senza paginazione)
    getAllArticoli: builder.query<Articolo[], void>({
      query: () => 'articoli/all',
      providesTags: [{ type: 'Articolo', id: 'LIST' }],
    }),

    // Recupera singolo articolo per ID
    getArticolo: builder.query<Articolo, number>({
      query: (id) => `articoli/${id}`,
      providesTags: (result, error, id) => [{ type: 'Articolo', id }],
    }),

    // Recupera articolo per codice
    getArticoloByCodice: builder.query<Articolo, string>({
      query: (codice) => `articoli/codice/${codice}`,
      providesTags: (result, error, codice) => [{ type: 'Articolo', id: codice }],
    }),

    // Crea nuovo articolo
    createArticolo: builder.mutation<ApiResponse<Articolo>, ArticoloCreateDto>({
      query: (articolo) => ({
        url: 'articoli',
        method: 'POST',
        body: articolo,
      }),
      invalidatesTags: [{ type: 'Articolo', id: 'LIST' }],
    }),

    // Aggiorna articolo esistente
    updateArticolo: builder.mutation<ApiResponse<Articolo>, ArticoloUpdateDto>({
      query: ({ id, ...resto }) => ({
        url: `articoli/${id}`,
        method: 'PUT',
        body: resto,
      }),
      invalidatesTags: (result, error, { id }) => [
        { type: 'Articolo', id },
        { type: 'Articolo', id: 'LIST' },
      ],
    }),

    // Elimina articolo
    deleteArticolo: builder.mutation<ApiResponse<void>, number>({
      query: (id) => ({
        url: `articoli/${id}`,
        method: 'DELETE',
      }),
      invalidatesTags: (result, error, id) => [
        { type: 'Articolo', id },
        { type: 'Articolo', id: 'LIST' },
      ],
    }),

    // ========== OPERAZIONI SPECIFICHE ==========

    // Attiva/disattiva articolo
    toggleArticoloStatus: builder.mutation<ApiResponse<Articolo>, { id: number; attivo: boolean }>({
      query: ({ id, attivo }) => ({
        url: `articoli/${id}/status`,
        method: 'PATCH',
        body: { attivo },
      }),
      invalidatesTags: (result, error, { id }) => [
        { type: 'Articolo', id },
        { type: 'Articolo', id: 'LIST' },
      ],
    }),

    // Aggiorna giacenza articolo
    updateGiacenza: builder.mutation<ApiResponse<Articolo>, { id: number; giacenza: number; note?: string }>({
      query: ({ id, giacenza, note }) => ({
        url: `articoli/${id}/giacenza`,
        method: 'PATCH',
        body: { giacenza, note },
      }),
      invalidatesTags: (result, error, { id }) => [
        { type: 'Articolo', id },
        { type: 'Articolo', id: 'LIST' },
      ],
    }),

    // Aggiorna prezzo di vendita
    updatePrezzoVendita: builder.mutation<ApiResponse<Articolo>, { id: number; prezzoVendita: number }>({
      query: ({ id, prezzoVendita }) => ({
        url: `articoli/${id}/prezzo`,
        method: 'PATCH',
        body: { prezzoVendita },
      }),
      invalidatesTags: (result, error, { id }) => [
        { type: 'Articolo', id },
        { type: 'Articolo', id: 'LIST' },
      ],
    }),

    // Cerca articoli per descrizione (ricerca libera)
    searchArticoli: builder.query<Articolo[], string>({
      query: (searchTerm) => `articoli/search?q=${encodeURIComponent(searchTerm)}`,
      providesTags: [{ type: 'Articolo', id: 'SEARCH' }],
    }),

    // Verifica disponibilità codice
    checkCodiceDisponibile: builder.query<{ disponibile: boolean }, string>({
      query: (codice) => `articoli/check-codice?codice=${encodeURIComponent(codice)}`,
    }),

  }),
});

// Export degli hook generati automaticamente
export const {
  useGetArticoliQuery,
  useGetAllArticoliQuery,
  useGetArticoloQuery,
  useGetArticoloByCodiceQuery,
  useCreateArticoloMutation,
  useUpdateArticoloMutation,
  useDeleteArticoloMutation,
  useToggleArticoloStatusMutation,
  useUpdateGiacenzaMutation,
  useUpdatePrezzoVenditaMutation,
  useSearchArticoliQuery,
  useCheckCodiceDisponibileQuery,
} = articoliApi;
