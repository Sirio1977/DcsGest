import React, { useEffect, useState } from 'react';
import { Card, Table, Button, Space, Typography, Alert, Spin } from 'antd';
import { SettingOutlined, ReloadOutlined, PlusOutlined } from '@ant-design/icons';

const { Title } = Typography;

interface Configurazione {
  id: number;
  chiave: string;
  valore: string;
  descrizione?: string;
  tipo: string;
}

const ConfigurazioniManagement: React.FC = () => {
  const [configurazioni, setConfigurazioni] = useState<Configurazione[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [backendStatus, setBackendStatus] = useState<'checking' | 'available' | 'unavailable'>('checking');

  useEffect(() => {
    checkBackendAndLoad();
  }, []);

  const checkBackendAndLoad = async () => {
    setLoading(true);
    setError(null);
    
    try {
      // Prima controlliamo se il backend è raggiungibile
      const healthCheck = await fetch('http://localhost:8080/api/aziende');
      
      if (!healthCheck.ok) {
        throw new Error('Backend non raggiungibile');
      }

      // Poi proviamo l'endpoint configurazioni
      const response = await fetch('http://localhost:8080/api/configurazioni');
      
      if (response.ok) {
        const data = await response.json();
        setConfigurazioni(data);
        setBackendStatus('available');
      } else if (response.status === 404) {
        setError('Endpoint configurazioni non disponibile. Riavviare il backend con le nuove modifiche.');
        setBackendStatus('unavailable');
      } else {
        throw new Error(`Errore ${response.status}`);
      }
    } catch (err) {
      setError('Backend non disponibile. Verificare che Spring Boot sia in esecuzione sulla porta 8080.');
      setBackendStatus('unavailable');
    } finally {
      setLoading(false);
    }
  };

  const initDefaults = async () => {
    setLoading(true);
    try {
      const response = await fetch('http://localhost:8080/api/configurazioni/init-defaults', {
        method: 'POST'
      });
      
      if (response.ok) {
        await checkBackendAndLoad(); // Ricarica i dati
      } else {
        setError('Errore nell\'inizializzazione delle configurazioni di default');
      }
    } catch (err) {
      setError('Errore di connessione durante l\'inizializzazione');
    } finally {
      setLoading(false);
    }
  };

  const columns = [
    {
      title: 'Chiave',
      dataIndex: 'chiave',
      key: 'chiave',
      render: (text: string) => <strong>{text}</strong>
    },
    {
      title: 'Valore',
      dataIndex: 'valore',
      key: 'valore'
    },
    {
      title: 'Tipo',
      dataIndex: 'tipo',
      key: 'tipo'
    },
    {
      title: 'Descrizione',
      dataIndex: 'descrizione',
      key: 'descrizione'
    }
  ];

  return (
    <div style={{ padding: '24px' }}>
      <Card 
        title={
          <Space>
            <SettingOutlined />
            <Title level={4} style={{ margin: 0 }}>Gestione Configurazioni</Title>
          </Space>
        }
        extra={
          <Space>
            <Button 
              icon={<ReloadOutlined />}
              onClick={checkBackendAndLoad}
              loading={loading}
            >
              Ricarica
            </Button>
            {backendStatus === 'available' && configurazioni.length === 0 && (
              <Button 
                type="primary"
                icon={<PlusOutlined />}
                onClick={initDefaults}
                loading={loading}
              >
                Inizializza Default
              </Button>
            )}
          </Space>
        }
      >
        {error && (
          <Alert
            message="Attenzione"
            description={error}
            type="warning"
            showIcon
            style={{ marginBottom: 16 }}
            action={
              <Button size="small" onClick={checkBackendAndLoad}>
                Riprova
              </Button>
            }
          />
        )}

        {backendStatus === 'checking' && (
          <div style={{ textAlign: 'center', padding: '40px' }}>
            <Spin size="large" />
            <p style={{ marginTop: 16 }}>Verifica connessione backend...</p>
          </div>
        )}

        {backendStatus === 'unavailable' && (
          <Alert
            message="Backend Non Disponibile"
            description={
              <div>
                <p>Il backend Spring Boot non è raggiungibile. Verificare che:</p>
                <ul>
                  <li>Il server sia in esecuzione sulla porta 8080</li>
                  <li>Il ConfigurazioneController sia stato compilato correttamente</li>
                  <li>Le nuove modifiche siano state applicate con un riavvio</li>
                </ul>
              </div>
            }
            type="error"
            showIcon
          />
        )}

        {backendStatus === 'available' && (
          <Table
            columns={columns}
            dataSource={configurazioni}
            rowKey="id"
            loading={loading}
            pagination={{
              pageSize: 10,
              showTotal: (total) => `Totale ${total} configurazioni`
            }}
          />
        )}
      </Card>
    </div>
  );
};

export default ConfigurazioniManagement;
