// API service per gestione clienti - Integrazione con backend Spring Boot

import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';
import {
  Cliente,
  ClienteCreateDto,
  ClienteUpdateDto,
  ClientiPagedResponse,
  ClienteFilter,
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

export const clientiApi = createApi({
  reducerPath: 'clientiApi',
  baseQuery,
  tagTypes: ['Cliente'],
  endpoints: (builder) => ({
    
    // ========== CRUD CLIENTI ==========
    
    // Recupera clienti con filtri e paginazione
    getClienti: builder.query<ClientiPagedResponse, {
      page?: number;
      size?: number;
      sortBy?: string;
      sortDir?: 'asc' | 'desc';
      filter?: ClienteFilter;
    }>({
      query: ({ page = 0, size = 20, sortBy = 'ragioneSociale', sortDir = 'asc', filter = {} }) => {
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
        return `clienti?${params}`;
      },
      providesTags: (result) =>
        result && result.content
          ? [
              ...result.content.map(({ id }) => ({ type: 'Cliente' as const, id })),
              { type: 'Cliente', id: 'LIST' },
            ]
          : [{ type: 'Cliente', id: 'LIST' }],
    }),

    // Recupera tutti i clienti (senza paginazione)
    getAllClienti: builder.query<Cliente[], void>({
      query: () => 'clienti/all',
      providesTags: [{ type: 'Cliente', id: 'LIST' }],
    }),

    // Recupera singolo cliente per ID
    getCliente: builder.query<Cliente, number>({
      query: (id) => `clienti/${id}`,
      providesTags: (result, error, id) => [{ type: 'Cliente', id }],
    }),

    // Recupera cliente per partita IVA
    getClienteByPartitaIva: builder.query<Cliente, string>({
      query: (partitaIva) => `clienti/partita-iva/${partitaIva}`,
      providesTags: (result, error, partitaIva) => [{ type: 'Cliente', id: partitaIva }],
    }),

    // Crea nuovo cliente
    createCliente: builder.mutation<ApiResponse<Cliente>, ClienteCreateDto>({
      query: (cliente) => ({
        url: 'clienti',
        method: 'POST',
        body: cliente,
      }),
      invalidatesTags: [{ type: 'Cliente', id: 'LIST' }],
    }),

    // Aggiorna cliente esistente
    updateCliente: builder.mutation<ApiResponse<Cliente>, ClienteUpdateDto>({
      query: ({ id, ...resto }) => ({
        url: `clienti/${id}`,
        method: 'PUT',
        body: resto,
      }),
      invalidatesTags: (result, error, { id }) => [
        { type: 'Cliente', id },
        { type: 'Cliente', id: 'LIST' },
      ],
    }),

    // Elimina cliente
    deleteCliente: builder.mutation<ApiResponse<void>, number>({
      query: (id) => ({
        url: `clienti/${id}`,
        method: 'DELETE',
      }),
      invalidatesTags: (result, error, id) => [
        { type: 'Cliente', id },
        { type: 'Cliente', id: 'LIST' },
      ],
    }),

    // ========== OPERAZIONI SPECIFICHE ==========

    // Attiva/disattiva cliente
    toggleClienteStatus: builder.mutation<ApiResponse<Cliente>, { id: number; attivo: boolean }>({
      query: ({ id, attivo }) => ({
        url: `clienti/${id}/status`,
        method: 'PATCH',
        body: { attivo },
      }),
      invalidatesTags: (result, error, { id }) => [
        { type: 'Cliente', id },
        { type: 'Cliente', id: 'LIST' },
      ],
    }),

    // Cerca clienti per ragione sociale (ricerca libera)
    searchClienti: builder.query<Cliente[], string>({
      query: (searchTerm) => `clienti/search?q=${encodeURIComponent(searchTerm)}`,
      providesTags: [{ type: 'Cliente', id: 'SEARCH' }],
    }),

    // Verifica disponibilità partita IVA
    checkPartitaIvaDisponibile: builder.query<{ disponibile: boolean }, string>({
      query: (partitaIva) => `clienti/check-partita-iva?partitaIva=${encodeURIComponent(partitaIva)}`,
    }),

    // Recupera clienti per tipo
    getClientiByTipo: builder.query<Cliente[], string>({
      query: (tipo) => `clienti/tipo/${tipo}`,
      providesTags: (result, error, tipo) => [{ type: 'Cliente', id: `TIPO_${tipo}` }],
    }),

    // Recupera clienti attivi
    getClientiAttivi: builder.query<Cliente[], void>({
      query: () => 'clienti/attivi',
      providesTags: [{ type: 'Cliente', id: 'ATTIVI' }],
    }),

    // ========== STATISTICHE ==========

    // Recupera statistiche clienti
    getStatisticheClienti: builder.query<{
      totaleClienti: number;
      clientiAttivi: number;
      clientiInattivi: number;
      nuoviClienteMese: number;
    }, void>({
      query: () => 'clienti/statistiche',
    }),

    // ========== IMPORTAZIONE ==========

    // Importa clienti da file JSON
    importClienti: builder.mutation<ApiResponse<{ 
      importati: number; 
      errori: number; 
      dettagli: string[] 
    }>, FormData>({
      query: (formData) => ({
        url: 'clienti/import',
        method: 'POST',
        body: formData,
      }),
      invalidatesTags: [{ type: 'Cliente', id: 'LIST' }],
    }),

  }),
});

// Export degli hook generati automaticamente
export const {
  useGetClientiQuery,
  useGetAllClientiQuery,
  useGetClienteQuery,
  useGetClienteByPartitaIvaQuery,
  useCreateClienteMutation,
  useUpdateClienteMutation,
  useDeleteClienteMutation,
  useToggleClienteStatusMutation,
  useSearchClientiQuery,
  useCheckPartitaIvaDisponibileQuery,
  useGetClientiByTipoQuery,
  useGetClientiAttiviQuery,
  useGetStatisticheClientiQuery,
  useImportClientiMutation,
} = clientiApi;
