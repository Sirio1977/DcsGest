import { createSlice, PayloadAction } from '@reduxjs/toolkit';

interface ConfigState {
  theme: 'light' | 'dark';
  language: 'it' | 'en';
  apiBaseUrl: string;
}

const initialState: ConfigState = {
  theme: 'light',
  language: 'it',
  apiBaseUrl: 'http://localhost:8080/api',
};

const configSlice = createSlice({
  name: 'config',
  initialState,
  reducers: {
    setTheme: (state, action: PayloadAction<'light' | 'dark'>) => {
      state.theme = action.payload;
    },
    setLanguage: (state, action: PayloadAction<'it' | 'en'>) => {
      state.language = action.payload;
    },
    setApiBaseUrl: (state, action: PayloadAction<string>) => {
      state.apiBaseUrl = action.payload;
    },
  },
});

export const { setTheme, setLanguage, setApiBaseUrl } = configSlice.actions;
export default configSlice.reducer;
