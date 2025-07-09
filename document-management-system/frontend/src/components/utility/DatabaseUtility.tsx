import React from 'react';
import { Card, Alert, Typography } from 'antd';

const { Title, Paragraph } = Typography;

const DatabaseUtility: React.FC = () => {
  return (
    <Card>
      <div style={{ textAlign: 'center', marginBottom: '24px' }}>
        <Title level={4}>Utility Database (In Sviluppo)</Title>
        <Paragraph type="secondary">
          Strumenti per backup, restore e manutenzione database
        </Paragraph>
      </div>

      <Alert
        message="Funzionalità in Sviluppo"
        description="Le utility database saranno implementate nel prossimo sprint di sviluppo."
        type="info"
        showIcon
      />
    </Card>
  );
};

export default DatabaseUtility;
