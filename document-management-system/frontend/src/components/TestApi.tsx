import React from 'react';
import { Card, Button, Spin, Alert } from 'antd';
import { useGetDocumentiQuery } from '../store/api/documentiApi';

export const TestApi: React.FC = () => {
  const { data: documenti, error, isLoading } = useGetDocumentiQuery({});

  return (
    <Card title="Test API Documenti" style={{ margin: '16px' }}>
      <Button 
        onClick={() => window.location.reload()} 
        style={{ marginBottom: '16px' }}
      >
        Ricarica Dati
      </Button>
      
      {isLoading && <Spin tip="Caricamento documenti..." />}
      
      {error && (
        <Alert
          message="Errore API"
          description="Errore nel caricamento dei documenti. Il backend potrebbe non essere configurato correttamente per CORS o l'endpoint potrebbe richiedere autenticazione."
          type="warning"
          showIcon
        />
      )}
      
      {documenti && documenti.content && (
        <Alert
          message="Successo!"
          description={`Caricati ${documenti.content.length} documenti dal backend.`}
          type="success"
          showIcon
        />
      )}
    </Card>
  );
};
