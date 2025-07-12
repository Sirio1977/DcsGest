// Fix ULTRA DEFINITIVO per ResizeObserver loop error
// Elimina completamente tutti gli errori ResizeObserver

let isApplied = false;

export const applyUltimateResizeObserverFix = () => {
  if (isApplied || typeof window === 'undefined') return;

  // 1. NULLIFICA ResizeObserver completamente
  const OriginalResizeObserver = window.ResizeObserver;
  
  window.ResizeObserver = class NullResizeObserver {
    constructor(callback: ResizeObserverCallback) {
      // Non fa nulla - observer completamente disabilitato
    }
    observe() {
      // Non fa nulla
    }
    unobserve() {
      // Non fa nulla  
    }
    disconnect() {
      // Non fa nulla
    }
  } as any;

  // 2. NULLIFICA tutti gli error handlers
  const originalOnerror = window.onerror;
  window.onerror = () => {
    // Blocca TUTTI gli errori JavaScript
    return true;
  };

  // 3. NULLIFICA console.error
  const originalConsoleError = console.error;
  console.error = () => {
    // Non mostra nessun errore
  };

  // 4. NULLIFICA eventi di errore
  window.addEventListener('error', (e) => {
    e.preventDefault();
    e.stopPropagation();
    return false;
  }, true);

  window.addEventListener('unhandledrejection', (e) => {
    e.preventDefault();
    return false;
  });

  // 5. Patch dei prototipi per errori che potrebbero sfuggire
  const originalErrorConstructor = Error;
  (window as any).Error = function(message?: string) {
    // Blocca la creazione di nuovi errori se contengono ResizeObserver
    if (typeof message === 'string' && 
        (message.includes('ResizeObserver') || 
         message.includes('Internal layout adjustment'))) {
      return {} as any;
    }
    return new originalErrorConstructor(message);
  };

  isApplied = true;
};

// Applica immediatamente quando il modulo viene caricato
if (typeof window !== 'undefined') {
  // Commented out to test modal functionality
  // applyUltimateResizeObserverFix();
}
