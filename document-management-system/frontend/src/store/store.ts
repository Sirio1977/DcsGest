import { configureStore } from '@reduxjs/toolkit';
import { documentiApi } from './api/documentiApi';
import { articoliApi } from './api/articoliApi';
import { clientiApi } from './api/clientiApi';
import { articoliFornitoriApi } from './api/articoliFornitoriApi';
import authReducer from './slices/authSlice';
import uiReducer from './slices/uiSlice';
import configReducer from './slices/configSlice';

const store = configureStore({
  reducer: {
    [documentiApi.reducerPath]: documentiApi.reducer,
    [articoliApi.reducerPath]: articoliApi.reducer,
    [clientiApi.reducerPath]: clientiApi.reducer,
    [articoliFornitoriApi.reducerPath]: articoliFornitoriApi.reducer,
    auth: authReducer,
    ui: uiReducer,
    config: configReducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(
      documentiApi.middleware,
      articoliApi.middleware,
      clientiApi.middleware,
      articoliFornitoriApi.middleware
    ),
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;

export default store;