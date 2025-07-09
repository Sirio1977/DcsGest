// This file contains API service definitions for making requests to the backend.

import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';
import { Documento } from '../../types/documento';

// Interface per Articolo
export interface Articolo {
  id: number;
  codice: string;
  descrizione: string;
  descrizioneEstesa?: string;
  categoria?: string;
  prezzo: number;
  costo: number;
  unitaMisura: string;
  aliquotaIva: number;
  giacenza: number;
  giacenzaMinima: number;
  tipo: 'PRODOTTO' | 'SERVIZIO' | 'MATERIA_PRIMA' | 'SEMILAVORATO';
  attivo: boolean;
  fornitore?: string;
  codiceFornitore?: string;
  note?: string;
  createdAt: string;
  updatedAt?: string;
}

// Interface per Cliente
export interface Cliente {
  id: number;
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
  tipo: 'CLIENTE' | 'FORNITORE' | 'CLIENTE_FORNITORE';
  attivo: boolean;
  note?: string;
  createdAt: string;
  updatedAt?: string;
}

// Interface per ArticoloFornitore
export interface ArticoloFornitore {
  id: number;
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
  prezziStorici?: string;
  createdAt: string;
  updatedAt?: string;
}

export const documentiApi = createApi({
  reducerPath: 'documentiApi',
  baseQuery: fetchBaseQuery({ baseUrl: 'http://localhost:8080/api' }),
  tagTypes: ['Documento', 'Articolo', 'Cliente', 'ArticoloFornitore'],
  endpoints: (builder) => ({
    // Endpoints Documenti
    getDocumenti: builder.query<Documento[], void>({
      query: () => '/documenti',
      providesTags: ['Documento'],
    }),
    getDocumentoById: builder.query<Documento, number>({
      query: (id) => `/documenti/${id}`,
      providesTags: ['Documento'],
    }),
    createDocumento: builder.mutation<Documento, Partial<Documento>>({
      query: (documento) => ({
        url: '/documenti',
        method: 'POST',
        body: documento,
      }),
      invalidatesTags: ['Documento'],
    }),
    updateDocumento: builder.mutation<Documento, Partial<Documento> & { id: number }>({
      query: ({ id, ...rest }) => ({
        url: `/documenti/${id}`,
        method: 'PUT',
        body: rest,
      }),
      invalidatesTags: ['Documento'],
    }),
    deleteDocumento: builder.mutation<void, number>({
      query: (id) => ({
        url: `/documenti/${id}`,
        method: 'DELETE',
      }),
      invalidatesTags: ['Documento'],
    }),

    // Endpoints Articoli
    getArticoli: builder.query<Articolo[], void>({
      query: () => '/articoli',
      providesTags: ['Articolo'],
    }),
    getArticoloById: builder.query<Articolo, number>({
      query: (id) => `/articoli/${id}`,
      providesTags: ['Articolo'],
    }),
    createArticolo: builder.mutation<Articolo, Partial<Articolo>>({
      query: (articolo) => ({
        url: '/articoli',
        method: 'POST',
        body: articolo,
      }),
      invalidatesTags: ['Articolo'],
    }),
    updateArticolo: builder.mutation<Articolo, Partial<Articolo> & { id: number }>({
      query: ({ id, ...rest }) => ({
        url: `/articoli/${id}`,
        method: 'PUT',
        body: rest,
      }),
      invalidatesTags: ['Articolo'],
    }),
    deleteArticolo: builder.mutation<void, number>({
      query: (id) => ({
        url: `/articoli/${id}`,
        method: 'DELETE',
      }),
      invalidatesTags: ['Articolo'],
    }),

    // Endpoints Articoli Fornitori
    getArticoliFornitori: builder.query<ArticoloFornitore[], void>({
      query: () => '/articoli-fornitori',
      providesTags: ['ArticoloFornitore'],
    }),
    getArticoloFornitoreById: builder.query<ArticoloFornitore, number>({
      query: (id) => `/articoli-fornitori/${id}`,
      providesTags: ['ArticoloFornitore'],
    }),
    createArticoloFornitore: builder.mutation<ArticoloFornitore, Partial<ArticoloFornitore>>({
      query: (articoloFornitore) => ({
        url: '/articoli-fornitori',
        method: 'POST',
        body: articoloFornitore,
      }),
      invalidatesTags: ['ArticoloFornitore'],
    }),
    updateArticoloFornitore: builder.mutation<ArticoloFornitore, Partial<ArticoloFornitore> & { id: number }>({
      query: ({ id, ...rest }) => ({
        url: `/articoli-fornitori/${id}`,
        method: 'PUT',
        body: rest,
      }),
      invalidatesTags: ['ArticoloFornitore'],
    }),
    deleteArticoloFornitore: builder.mutation<void, number>({
      query: (id) => ({
        url: `/articoli-fornitori/${id}`,
        method: 'DELETE',
      }),
      invalidatesTags: ['ArticoloFornitore'],
    }),

    // Endpoints Clienti
    getClienti: builder.query<Cliente[], void>({
      query: () => '/clienti',
      providesTags: ['Cliente'],
    }),
    getClienteById: builder.query<Cliente, number>({
      query: (id) => `/clienti/${id}`,
      providesTags: ['Cliente'],
    }),
    createCliente: builder.mutation<Cliente, Partial<Cliente>>({
      query: (cliente) => ({
        url: '/clienti',
        method: 'POST',
        body: cliente,
      }),
      invalidatesTags: ['Cliente'],
    }),
    updateCliente: builder.mutation<Cliente, Partial<Cliente> & { id: number }>({
      query: ({ id, ...rest }) => ({
        url: `/clienti/${id}`,
        method: 'PUT',
        body: rest,
      }),
      invalidatesTags: ['Cliente'],
    }),
    deleteCliente: builder.mutation<void, number>({
      query: (id) => ({
        url: `/clienti/${id}`,
        method: 'DELETE',
      }),
      invalidatesTags: ['Cliente'],
    }),
  }),
});

export const {
  useGetDocumentiQuery,
  useGetDocumentoByIdQuery,
  useCreateDocumentoMutation,
  useUpdateDocumentoMutation,
  useDeleteDocumentoMutation,
  
  useGetArticoliQuery,
  useGetArticoloByIdQuery,
  useCreateArticoloMutation,
  useUpdateArticoloMutation,
  useDeleteArticoloMutation,
  
  useGetArticoliFornitoriQuery,
  useGetArticoloFornitoreByIdQuery,
  useCreateArticoloFornitoreMutation,
  useUpdateArticoloFornitoreMutation,
  useDeleteArticoloFornitoreMutation,
  
  useGetClientiQuery,
  useGetClienteByIdQuery,
  useCreateClienteMutation,
  useUpdateClienteMutation,
  useDeleteClienteMutation,
} = documentiApi;