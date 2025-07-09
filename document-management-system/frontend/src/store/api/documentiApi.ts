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

export const documentiApi = createApi({
  reducerPath: 'documentiApi',
  baseQuery: fetchBaseQuery({ baseUrl: 'http://localhost:8080/api' }),
  tagTypes: ['Documento', 'Articolo', 'Cliente'],
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
  
  useGetClientiQuery,
  useGetClienteByIdQuery,
  useCreateClienteMutation,
  useUpdateClienteMutation,
  useDeleteClienteMutation,
} = documentiApi;