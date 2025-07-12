import React, { useState } from 'react';
import { 
  Card, 
  Tabs, 
  Typography, 
  Alert, 
  Row, 
  Col,
  Statistic,
  Space,
  Button,
  Divider,
  Modal,
  Form
} from 'antd';
import {
  ToolOutlined,
  ImportOutlined,
  ExportOutlined,
  DatabaseOutlined,
  FileTextOutlined,
  CloudUploadOutlined,
  DownloadOutlined
} from '@ant-design/icons';
import ImportClientiJson from './utility/ImportClientiJson';
import ImportFornitoriJson from './utility/ImportFornitoriJson';
import ImportArticoliJson from './utility/ImportArticoliJson';
import ImportArticoliFornitoriJson from './utility/ImportArticoliFornitoriJson';
import DataExportUtility from './utility/DataExportUtility';
import DatabaseUtility from './utility/DatabaseUtility';

const { Title, Paragraph } = Typography;
const { TabPane } = Tabs;

const UtilityManagement: React.FC = () => {
  const [activeTab, setActiveTab] = useState('import-clienti');
  const [stats, setStats] = useState({
    ultimaImportazione: '2025-07-09',
    recordImportati: 0,
    erroriUltimaImportazione: 0
  });
  const [modalVisible, setModalVisible] = useState(false);

  const utilityTabs = [
    {
      key: 'import-clienti',
      label: (
        <span>
          <ImportOutlined />
          Import Clienti
        </span>
      ),
      icon: <ImportOutlined />,
      title: 'Importazione Clienti da JSON',
      description: 'Importa clienti da file JSON con mapping automatico dei campi',
      children: <ImportClientiJson onStatsUpdate={setStats} />
    },
    {
      key: 'import-fornitori',
      label: (
        <span>
          <DatabaseOutlined />
          Import Fornitori
        </span>
      ),
      icon: <DatabaseOutlined />,
      title: 'Importazione Fornitori da JSON',
      description: 'Importa fornitori da file JSON con validazione automatica',
      children: <ImportFornitoriJson onStatsUpdate={setStats} />
    },
    {
      key: 'import-articoli',
      label: (
        <span>
          <FileTextOutlined />
          Import Articoli
        </span>
      ),
      icon: <FileTextOutlined />,
      title: 'Importazione Articoli da JSON',
      description: 'Importa articoli da file JSON/CSV con validazione prezzi',
      children: <ImportArticoliJson onStatsUpdate={setStats} />
    },
    {
      key: 'import-articoli-fornitori',
      label: (
        <span>
          <CloudUploadOutlined />
          Import Articoli Fornitori
        </span>
      ),
      icon: <CloudUploadOutlined />,
      title: 'Importazione Articoli Fornitori da JSON',
      description: 'Importa articoli fornitori da file JSON con validazione e mapping automatico',
      children: <ImportArticoliFornitoriJson onImportComplete={(result: any) => {
        setStats(prev => ({
          ...prev,
          ultimaImportazione: new Date().toISOString().split('T')[0],
          recordImportati: result.successfulRecords || 0,
          erroriUltimaImportazione: result.failedRecords || 0
        }));
      }} />
    },
    {
      key: 'export-data',
      label: (
        <span>
          <ExportOutlined />
          Export Dati
        </span>
      ),
      icon: <ExportOutlined />,
      title: 'Esportazione Dati',
      description: 'Esporta dati del sistema in vari formati (JSON, CSV, Excel)',
      children: <DataExportUtility />
    },
    {
      key: 'database',
      label: (
        <span>
          <DatabaseOutlined />
          Database
        </span>
      ),
      icon: <DatabaseOutlined />,
      title: 'Utility Database',
      description: 'Backup, restore e manutenzione database',
      children: <DatabaseUtility />
    }
  ];

  const currentUtility = utilityTabs.find(tab => tab.key === activeTab);

  const handleModalSubmit = (values: any) => {
    console.log('Modal form values:', values);
    setModalVisible(false);
  };

  return (
    <div style={{ padding: '24px' }}>
      {/* Header */}
      <div style={{ marginBottom: '24px' }}>
        <Title level={2}>
          <ToolOutlined style={{ marginRight: '12px', color: '#1890ff' }} />
          Utility di Sistema
        </Title>
        <Paragraph type="secondary" style={{ fontSize: '16px' }}>
          Strumenti per importazione, esportazione e gestione dati del sistema documentale
        </Paragraph>
      </div>

      {/* Stats Overview */}
      <Row gutter={[16, 16]} style={{ marginBottom: '24px' }}>
        <Col xs={24} sm={8}>
          <Card size="small">
            <Statistic
              title="Ultima Importazione"
              value={stats.ultimaImportazione}
              prefix={<CloudUploadOutlined />}
              valueStyle={{ color: '#3f8600' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={8}>
          <Card size="small">
            <Statistic
              title="Record Importati"
              value={stats.recordImportati}
              prefix={<DatabaseOutlined />}
              valueStyle={{ color: '#1890ff' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={8}>
          <Card size="small">
            <Statistic
              title="Errori Ultima Importazione"
              value={stats.erroriUltimaImportazione}
              prefix={<ExportOutlined />}
              valueStyle={{ 
                color: stats.erroriUltimaImportazione > 0 ? '#cf1322' : '#3f8600' 
              }}
            />
          </Card>
        </Col>
      </Row>

      {/* Alert informativo */}
      <Alert
        message="Importante - Backup dei Dati"
        description="Prima di eseguire importazioni massive, si consiglia di effettuare un backup del database. Le operazioni di importazione possono modificare i dati esistenti."
        type="warning"
        showIcon
        closable
        style={{ marginBottom: '24px' }}
      />

      {/* Utility Content */}
      <Card>
        <Tabs
          activeKey={activeTab}
          onChange={setActiveTab}
          type="card"
          size="large"
          tabPosition="left"
          style={{ minHeight: '600px' }}
          items={utilityTabs}
        />
      </Card>

      {/* Footer Actions */}
      <div style={{ marginTop: '24px', textAlign: 'center' }}>
        <Space size="large">
          <Button 
            type="default" 
            icon={<DownloadOutlined />}
            onClick={() => {
              // Download template files
              console.log('Download template files');
            }}
          >
            Scarica Template
          </Button>
          <Button 
            type="default" 
            icon={<FileTextOutlined />}
            onClick={() => {
              // Open documentation
              console.log('Open documentation');
            }}
          >
            Documentazione
          </Button>
        </Space>
      </div>

      {/* Modal Example */}
      <Modal
        open={modalVisible}
        onCancel={() => setModalVisible(false)}
        footer={null}
      >
        <Form onFinish={handleModalSubmit}>
          {/* Form content */}
        </Form>
      </Modal>
    </div>
  );
};

export default UtilityManagement;
