import React from 'react';
import { Card, Alert, Typography } from 'antd';

const { Title, Paragraph } = Typography;

const DataExportUtility: React.FC = () => {
  return (
    <Card>
      <div style={{ textAlign: 'center', marginBottom: '24px' }}>
        <Title level={4}>Esportazione Dati (In Sviluppo)</Title>
        <Paragraph type="secondary">
          Strumenti per esportare dati in vari formati
        </Paragraph>
      </div>

      <Alert
        message="Funzionalità in Sviluppo"
        description="L'esportazione dati sarà implementata nel prossimo sprint di sviluppo."
        type="info"
        showIcon
      />
    </Card>
  );
};

export default DataExportUtility;
