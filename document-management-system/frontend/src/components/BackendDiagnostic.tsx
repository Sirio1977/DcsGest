import React, { useEffect, useState } from 'react';
import { Card, Alert, Button, Space, Spin, Typography, Divider } from 'antd';
import { CheckCircleOutlined, CloseCircleOutlined, ReloadOutlined } from '@ant-design/icons';

const { Title, Text } = Typography;

interface BackendStatus {
  service: string;
  endpoint: string;
  status: 'checking' | 'success' | 'error';
  message: string;
  response?: any;
}

const BackendDiagnostic: React.FC = () => {
  const [diagnostics, setDiagnostics] = useState<BackendStatus[]>([]);
  const [isRunning, setIsRunning] = useState(false);

  const endpoints = [
    { service: 'Aziende', endpoint: '/api/aziende' },
    { service: 'Aziende Test', endpoint: '/api/aziende/test' },
    { service: 'Configurazioni', endpoint: '/api/configurazioni' },
    { service: 'Documenti', endpoint: '/api/documenti' },
  ];

  useEffect(() => {
    runDiagnostics();
  }, []);

  const runDiagnostics = async () => {
    setIsRunning(true);
    const results: BackendStatus[] = [];

    for (const ep of endpoints) {
      const status: BackendStatus = {
        service: ep.service,
        endpoint: ep.endpoint,
        status: 'checking',
        message: 'Verifica in corso...'
      };
      
      results.push(status);
      setDiagnostics([...results]);

      try {
        const response = await fetch(`http://localhost:8080${ep.endpoint}`);
        
        if (response.ok) {
          const data = await response.text();
          status.status = 'success';
          status.message = `✅ Funziona (Status: ${response.status})`;
          status.response = data.length > 100 ? data.substring(0, 100) + '...' : data;
        } else {
          status.status = 'error';
          status.message = `❌ Errore ${response.status}: ${response.statusText}`;
        }
      } catch (error) {
        status.status = 'error';
        status.message = `❌ Connessione fallita: ${error instanceof Error ? error.message : 'Errore sconosciuto'}`;
      }

      setDiagnostics([...results]);
      await new Promise(resolve => setTimeout(resolve, 500)); // Pausa tra test
    }

    setIsRunning(false);
  };

  const getStatusIcon = (status: BackendStatus['status']) => {
    switch (status) {
      case 'checking':
        return <Spin size="small" />;
      case 'success':
        return <CheckCircleOutlined style={{ color: '#52c41a' }} />;
      case 'error':
        return <CloseCircleOutlined style={{ color: '#ff4d4f' }} />;
    }
  };

  const getAlertType = () => {
    if (isRunning) return 'info';
    const hasErrors = diagnostics.some(d => d.status === 'error');
    const allSuccess = diagnostics.every(d => d.status === 'success');
    
    if (allSuccess) return 'success';
    if (hasErrors) return 'error';
    return 'warning';
  };

  return (
    <div style={{ padding: '24px' }}>
      <Card 
        title={
          <Space>
            <Title level={4} style={{ margin: 0 }}>🔍 Diagnostica Backend</Title>
          </Space>
        }
        extra={
          <Button 
            type="primary"
            icon={<ReloadOutlined />}
            loading={isRunning}
            onClick={runDiagnostics}
          >
            Riesegui Test
          </Button>
        }
      >
        <Alert
          message={
            isRunning 
              ? "Test in corso..." 
              : `Test completati - ${diagnostics.filter(d => d.status === 'success').length}/${diagnostics.length} servizi funzionanti`
          }
          type={getAlertType()}
          showIcon
          style={{ marginBottom: 16 }}
        />

        <div>
          {diagnostics.map((diag, index) => (
            <div key={index} style={{ marginBottom: 16, padding: 16, border: '1px solid #f0f0f0', borderRadius: 6 }}>
              <Space style={{ width: '100%', justifyContent: 'space-between' }}>
                <Space>
                  {getStatusIcon(diag.status)}
                  <Text strong>{diag.service}</Text>
                  <Text code>{diag.endpoint}</Text>
                </Space>
                <Text type={diag.status === 'success' ? 'success' : diag.status === 'error' ? 'danger' : undefined}>
                  {diag.message}
                </Text>
              </Space>
              
              {diag.response && (
                <>
                  <Divider style={{ margin: '8px 0' }} />
                  <Text type="secondary" style={{ fontSize: '12px' }}>
                    Risposta: {diag.response}
                  </Text>
                </>
              )}
            </div>
          ))}
        </div>

        <Divider />
        
        <Alert
          message="Istruzioni per risolvere i problemi"
          description={
            <ul>
              <li><strong>Errore 500:</strong> Il backend ha problemi interni. Controllare i log di Spring Boot.</li>
              <li><strong>Errore 404:</strong> L'endpoint non è stato mappato correttamente. Verificare @RequestMapping.</li>
              <li><strong>Connessione fallita:</strong> Il backend non è in esecuzione. Avviare Spring Boot sulla porta 8080.</li>
              <li><strong>CORS:</strong> Se il frontend non può accedere al backend, controllare la configurazione CORS.</li>
            </ul>
          }
          type="info"
          showIcon
        />
      </Card>
    </div>
  );
};

export default BackendDiagnostic;
