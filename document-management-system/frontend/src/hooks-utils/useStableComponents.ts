import { useState, useEffect, useCallback } from 'react';

/**
 * Hook per gestire debouncing dei valori di input
 * Riduce le chiamate API eccessive che possono causare ResizeObserver loops
 */
export const useDebounce = <T>(value: T, delay: number): T => {
  const [debouncedValue, setDebouncedValue] = useState<T>(value);

  useEffect(() => {
    const handler = setTimeout(() => {
      setDebouncedValue(value);
    }, delay);

    return () => {
      clearTimeout(handler);
    };
  }, [value, delay]);

  return debouncedValue;
};

/**
 * Hook per stabilizzare i resize degli elementi
 * Previene loop infiniti di ResizeObserver
 */
export const useStableResize = () => {
  const [isResizing, setIsResizing] = useState(false);

  const throttledResize = useCallback(() => {
    if (isResizing) return;
    
    setIsResizing(true);
    
    // Usa requestAnimationFrame per il timing ottimale
    requestAnimationFrame(() => {
      // Timeout per prevenire resize eccessivi
      setTimeout(() => {
        setIsResizing(false);
      }, 100);
    });
  }, [isResizing]);

  useEffect(() => {
    window.addEventListener('resize', throttledResize);
    return () => window.removeEventListener('resize', throttledResize);
  }, [throttledResize]);

  return { isResizing };
};

/**
 * Hook per gestire table states in modo stabile
 * Previene aggiornamenti troppo frequenti che causano ResizeObserver errors
 */
export const useStableTable = () => {
  const [isUpdating, setIsUpdating] = useState(false);

  const stableUpdate = useCallback((updateFn: () => void) => {
    if (isUpdating) return;
    
    setIsUpdating(true);
    
    // Batching degli aggiornamenti
    requestAnimationFrame(() => {
      updateFn();
      
      // Timeout per prevenire aggiornamenti troppo frequenti
      setTimeout(() => {
        setIsUpdating(false);
      }, 50);
    });
  }, [isUpdating]);

  return { stableUpdate, isUpdating };
};
