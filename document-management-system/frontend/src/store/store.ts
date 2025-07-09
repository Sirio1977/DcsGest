import { configureStore } from '@reduxjs/toolkit';
import { documentiApi } from './api/documentiApi';
import authReducer from './slices/authSlice';
import uiReducer from './slices/uiSlice';
import configReducer from './slices/configSlice';

const store = configureStore({
  reducer: {
    [documentiApi.reducerPath]: documentiApi.reducer,
    auth: authReducer,
    ui: uiReducer,
    config: configReducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(documentiApi.middleware),
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;

export default store;