import React, { useState } from 'react';
import {
  Card,
  Upload,
  Button,
  Table,
  Alert,
  Progress,
  Typography,
  Space,
  Tag,
  Modal,
  Form,
  Input,
  Select,
  Divider,
  Row,
  Col,
  Statistic,
  message,
  Tooltip
} from 'antd';
import {
  InboxOutlined,
  CheckCircleOutlined,
  ExclamationCircleOutlined,
  EyeOutlined,
  SaveOutlined,
  ClearOutlined,
  InfoCircleOutlined
} from '@ant-design/icons';

const { Dragger } = Upload;
const { Title, Paragraph, Text } = Typography;
const { Option } = Select;

interface ClienteImportato {
  id?: string;
  ragioneSociale: string;
  partitaIva: string;
  categoria: 'azienda' | 'privato';
  indirizzo?: string;
  email?: string;
  telefono?: string;
  pec?: string;
  dataUltimoAggiornamento?: string;
  // Campi mappati per il database
  tipoCliente?: 'CLIENTE' | 'FORNITORE' | 'CLIENTE_FORNITORE';
  codiceFiscale?: string;
  citta?: string;
  provincia?: string;
  cap?: string;
  stato?: 'valido' | 'errore' | 'duplicato';
  errori?: string[];
}

interface Props {
  onStatsUpdate?: (stats: any) => void;
}

const ImportClientiJson: React.FC<Props> = ({ onStatsUpdate }) => {
  const [clientiDaImportare, setClientiDaImportare] = useState<ClienteImportato[]>([]);
  const [isImporting, setIsImporting] = useState(false);
  const [importProgress, setImportProgress] = useState(0);
  const [importStats, setImportStats] = useState({
    totali: 0,
    validi: 0,
    errori: 0,
    duplicati: 0
  });
  const [showPreview, setShowPreview] = useState(false);
  const [selectedCliente, setSelectedCliente] = useState<ClienteImportato | null>(null);
  const [mappingRules, setMappingRules] = useState({
    tipoClienteDefault: 'CLIENTE' as const,
    categoriaMapping: {
      'azienda': 'CLIENTE',
      'privato': 'CLIENTE'
    }
  });

  // Funzione per parsare l'indirizzo e estrarre città, provincia, CAP
  const parseIndirizzo = (indirizzo: string) => {
    if (!indirizzo) return { citta: '', provincia: '', cap: '' };
    
    // Pattern per riconoscere CAP (5 cifre), Provincia (2 lettere), Città
    const indirizzoPattern = /(.+?)\s+(\d{5})\s+([A-Z]{2,})\s*([A-Z]{2})?$/i;
    const match = indirizzo.match(indirizzoPattern);
    
    if (match) {
      return {
        citta: match[3]?.trim() || '',
        provincia: match[4]?.trim() || '',
        cap: match[2] || ''
      };
    }
    
    // Fallback: cerca solo il CAP
    const capPattern = /(\d{5})/;
    const capMatch = indirizzo.match(capPattern);
    
    return {
      citta: '',
      provincia: '',
      cap: capMatch ? capMatch[1] : ''
    };
  };

  // Validazione partita IVA italiana
  const validatePartitaIva = (partitaIva: string): boolean => {
    if (!partitaIva || partitaIva.length !== 11) return false;
    
    // Rimuovi spazi e caratteri non numerici
    const cleanPIva = partitaIva.replace(/\D/g, '');
    if (cleanPIva.length !== 11) return false;
    
    // Calcolo check digit
    const digits = cleanPIva.split('').map(Number);
    let sum = 0;
    
    for (let i = 0; i < 10; i++) {
      let digit = digits[i];
      if (i % 2 === 1) {
        digit *= 2;
        if (digit > 9) digit = Math.floor(digit / 10) + (digit % 10);
      }
      sum += digit;
    }
    
    const checkDigit = (10 - (sum % 10)) % 10;
    return checkDigit === digits[10];
  };

  // Validazione e mapping dei dati
  const validateAndMapCliente = (cliente: any): ClienteImportato => {
    const errori: string[] = [];
    const { citta, provincia, cap } = parseIndirizzo(cliente.indirizzo || '');

    // Validazioni
    if (!cliente.ragioneSociale?.trim()) {
      errori.push('Ragione sociale obbligatoria');
    }
    
    if (!cliente.partitaIva?.trim()) {
      errori.push('Partita IVA obbligatoria');
    } else if (!validatePartitaIva(cliente.partitaIva)) {
      errori.push('Partita IVA non valida');
    }

    const clienteMappato: ClienteImportato = {
      ...cliente,
      ragioneSociale: cliente.ragioneSociale?.trim() || '',
      partitaIva: cliente.partitaIva?.replace(/\D/g, '') || '',
      categoria: cliente.categoria === 'azienda' ? 'azienda' : 'privato',
      tipoCliente: mappingRules.categoriaMapping[cliente.categoria as keyof typeof mappingRules.categoriaMapping] as any || mappingRules.tipoClienteDefault,
      citta,
      provincia,
      cap,
      email: cliente.email?.trim() || '',
      telefono: cliente.telefono?.trim() || '',
      pec: cliente.pec?.trim() || '',
      stato: errori.length > 0 ? 'errore' : 'valido',
      errori
    };

    return clienteMappato;
  };

  // Gestione upload file
  const handleFileUpload = (file: File) => {
    const reader = new FileReader();
    
    reader.onload = (e) => {
      try {
        const jsonData = JSON.parse(e.target?.result as string);
        
        if (!Array.isArray(jsonData)) {
          message.error('Il file deve contenere un array di clienti');
          return;
        }

        // Mappa e valida i clienti
        const clientiMappati = jsonData.map((cliente, index) => ({
          ...validateAndMapCliente(cliente),
          id: `import_${index}`
        }));

        // Calcola statistiche
        const stats = {
          totali: clientiMappati.length,
          validi: clientiMappati.filter(c => c.stato === 'valido').length,
          errori: clientiMappati.filter(c => c.stato === 'errore').length,
          duplicati: 0 // TODO: implementare controllo duplicati
        };

        setClientiDaImportare(clientiMappati);
        setImportStats(stats);
        setShowPreview(true);
        
        message.success(`Caricati ${clientiMappati.length} clienti dal file JSON`);
        
        if (onStatsUpdate) {
          onStatsUpdate({
            ultimaImportazione: new Date().toISOString().split('T')[0],
            recordImportati: stats.validi,
            erroriUltimaImportazione: stats.errori
          });
        }

      } catch (error) {
        message.error('Errore nel parsing del file JSON. Verificare il formato.');
        console.error('Errore parsing JSON:', error);
      }
    };

    reader.readAsText(file);
    return false; // Previene upload automatico
  };

  // Esegui importazione nel database
  const executeImport = async () => {
    setIsImporting(true);
    setImportProgress(0);

    try {
      // Prepara dati per il backend
      const clientiJson = clientiDaImportare
        .filter(c => c.stato === 'valido')
        .map(cliente => ({
          ragioneSociale: cliente.ragioneSociale,
          partitaIva: cliente.partitaIva,
          categoria: cliente.categoria,
          indirizzo: cliente.indirizzo || '',
          email: cliente.email || '',
          telefono: cliente.telefono || '',
          pec: cliente.pec || '',
          dataUltimoAggiornamento: new Date().toISOString()
        }));

      // Crea un Blob con i dati JSON
      const jsonBlob = new Blob([JSON.stringify(clientiJson)], { 
        type: 'application/json' 
      });

      // Crea FormData per l'upload
      const formData = new FormData();
      formData.append('file', jsonBlob, 'clienti.json');
      formData.append('preview', 'false');

      // Chiamata API per importazione
      const response = await fetch('http://localhost:8080/api/import/clienti/json', {
        method: 'POST',
        body: formData
      });

      if (!response.ok) {
        throw new Error(`Errore HTTP: ${response.status}`);
      }

      const result = await response.json();
      
      // Simula progress per UX
      setImportProgress(50);
      await new Promise(resolve => setTimeout(resolve, 500));
      setImportProgress(100);
      
      if (result.success) {
        message.success(`Importazione completata! Salvati ${result.successfulRecords} clienti su ${result.totalRecords}`);
        
        if (result.errors && result.errors.length > 0) {
          console.warn('Errori durante importazione:', result.errors);
        }
      } else {
        message.warning(`Importazione parziale: ${result.successfulRecords}/${result.totalRecords} clienti salvati`);
        
        if (result.errors && result.errors.length > 0) {
          message.error(`Errori: ${result.errors.slice(0, 3).join(', ')}${result.errors.length > 3 ? '...' : ''}`);
        }
      }
      
      // Reset stato
      setClientiDaImportare([]);
      setShowPreview(false);
      setImportProgress(0);

      // Aggiorna statistiche
      if (onStatsUpdate) {
        onStatsUpdate({
          ultimaImportazione: new Date().toISOString().split('T')[0],
          recordImportati: result.successfulRecords,
          erroriUltimaImportazione: result.failedRecords || 0
        });
      }

    } catch (error) {
      message.error('Errore durante l\'importazione: ' + (error as Error).message);
      console.error('Errore importazione:', error);
    } finally {
      setIsImporting(false);
    }
  };

  // Colonne tabella preview
  const columns = [
    {
      title: 'Stato',
      key: 'stato',
      width: 80,
      render: (record: ClienteImportato) => {
        if (record.stato === 'valido') {
          return <CheckCircleOutlined style={{ color: '#52c41a', fontSize: '16px' }} />;
        } else {
          return (
            <Tooltip title={record.errori?.join(', ')}>
              <ExclamationCircleOutlined style={{ color: '#ff4d4f', fontSize: '16px' }} />
            </Tooltip>
          );
        }
      }
    },
    {
      title: 'Ragione Sociale',
      dataIndex: 'ragioneSociale',
      key: 'ragioneSociale',
      width: 200,
      render: (text: string) => <strong>{text}</strong>
    },
    {
      title: 'P.IVA',
      dataIndex: 'partitaIva',
      key: 'partitaIva',
      width: 120,
      render: (text: string) => <Text code>{text}</Text>
    },
    {
      title: 'Tipo',
      dataIndex: 'tipoCliente',
      key: 'tipoCliente',
      width: 100,
      render: (tipo: string) => (
        <Tag color={tipo === 'CLIENTE' ? 'blue' : 'green'}>
          {tipo}
        </Tag>
      )
    },
    {
      title: 'Città',
      dataIndex: 'citta',
      key: 'citta',
      width: 120
    },
    {
      title: 'Email',
      dataIndex: 'email',
      key: 'email',
      width: 150,
      ellipsis: true
    },
    {
      title: 'Azioni',
      key: 'actions',
      width: 100,
      render: (record: ClienteImportato) => (
        <Button
          type="link"
          icon={<EyeOutlined />}
          onClick={() => setSelectedCliente(record)}
        >
          Dettagli
        </Button>
      )
    }
  ];

  return (
    <div>
      {/* Upload Area */}
      {!showPreview && (
        <Card>
          <div style={{ textAlign: 'center', marginBottom: '24px' }}>
            <Title level={4}>Carica File JSON Clienti</Title>
            <Paragraph type="secondary">
              Supporta il formato JSON con la struttura del file clienti.json fornito
            </Paragraph>
          </div>

          <Dragger
            beforeUpload={handleFileUpload}
            accept=".json"
            showUploadList={false}
            style={{ marginBottom: '24px' }}
          >
            <p className="ant-upload-drag-icon">
              <InboxOutlined style={{ fontSize: '48px', color: '#1890ff' }} />
            </p>
            <p className="ant-upload-text">
              Clicca o trascina il file JSON in quest'area per caricarlo
            </p>
            <p className="ant-upload-hint">
              Supporta solo file .json. Il file deve contenere un array di oggetti cliente.
            </p>
          </Dragger>

          {/* Esempio formato */}
          <Alert
            message="Formato File Supportato"
            description={
              <div>
                <p>Il file JSON deve avere questa struttura:</p>
                <pre style={{ background: '#f5f5f5', padding: '8px', borderRadius: '4px' }}>
{`[
  {
    "ragioneSociale": "NOME AZIENDA SRL",
    "partitaIva": "01234567890",
    "categoria": "azienda",
    "indirizzo": "VIA ESEMPIO 123 12345 MILANO MI",
    "email": "info@azienda.it",
    "telefono": "02-1234567",
    "pec": "pec@azienda.it"
  }
]`}
                </pre>
              </div>
            }
            type="info"
            showIcon
          />
        </Card>
      )}

      {/* Preview e Import */}
      {showPreview && (
        <div>
          {/* Statistiche Import */}
          <Row gutter={[16, 16]} style={{ marginBottom: '24px' }}>
            <Col span={6}>
              <Card size="small">
                <Statistic
                  title="Totali"
                  value={importStats.totali}
                  valueStyle={{ color: '#1890ff' }}
                />
              </Card>
            </Col>
            <Col span={6}>
              <Card size="small">
                <Statistic
                  title="Validi"
                  value={importStats.validi}
                  valueStyle={{ color: '#52c41a' }}
                />
              </Card>
            </Col>
            <Col span={6}>
              <Card size="small">
                <Statistic
                  title="Errori"
                  value={importStats.errori}
                  valueStyle={{ color: '#ff4d4f' }}
                />
              </Card>
            </Col>
            <Col span={6}>
              <Card size="small">
                <Statistic
                  title="Duplicati"
                  value={importStats.duplicati}
                  valueStyle={{ color: '#faad14' }}
                />
              </Card>
            </Col>
          </Row>

          {/* Progress Bar */}
          {isImporting && (
            <Card style={{ marginBottom: '16px' }}>
              <div style={{ textAlign: 'center' }}>
                <Title level={5}>Importazione in corso...</Title>
                <Progress 
                  percent={importProgress} 
                  status="active"
                  strokeColor="#52c41a"
                />
                <Text type="secondary">
                  Elaborazione cliente {Math.round((importProgress / 100) * importStats.validi)} di {importStats.validi}
                </Text>
              </div>
            </Card>
          )}

          {/* Tabella Preview */}
          <Card
            title={
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <span>Anteprima Clienti da Importare</span>
                <Space>
                  <Button
                    icon={<ClearOutlined />}
                    onClick={() => {
                      setShowPreview(false);
                      setClientiDaImportare([]);
                    }}
                  >
                    Annulla
                  </Button>
                  <Button
                    type="primary"
                    icon={<SaveOutlined />}
                    loading={isImporting}
                    disabled={importStats.validi === 0}
                    onClick={executeImport}
                  >
                    Importa {importStats.validi} Clienti
                  </Button>
                </Space>
              </div>
            }
          >
            <Table
              columns={columns}
              dataSource={clientiDaImportare}
              rowKey="id"
              pagination={{ pageSize: 10 }}
              size="small"
              scroll={{ x: 1000 }}
            />
          </Card>
        </div>
      )}

      {/* Modal Dettaglio Cliente */}
      <Modal
        title="Dettaglio Cliente"
        open={!!selectedCliente}
        onCancel={() => setSelectedCliente(null)}
        footer={null}
        width={800}
      >
        {selectedCliente && (
          <div>
            <Row gutter={[16, 16]}>
              <Col span={12}>
                <strong>Ragione Sociale:</strong><br />
                {selectedCliente.ragioneSociale}
              </Col>
              <Col span={12}>
                <strong>Partita IVA:</strong><br />
                {selectedCliente.partitaIva}
              </Col>
              <Col span={12}>
                <strong>Tipo Cliente:</strong><br />
                <Tag color="blue">{selectedCliente.tipoCliente}</Tag>
              </Col>
              <Col span={12}>
                <strong>Categoria:</strong><br />
                {selectedCliente.categoria}
              </Col>
              <Col span={24}>
                <strong>Indirizzo Completo:</strong><br />
                {selectedCliente.indirizzo}
              </Col>
              <Col span={8}>
                <strong>Città:</strong><br />
                {selectedCliente.citta}
              </Col>
              <Col span={8}>
                <strong>Provincia:</strong><br />
                {selectedCliente.provincia}
              </Col>
              <Col span={8}>
                <strong>CAP:</strong><br />
                {selectedCliente.cap}
              </Col>
              <Col span={12}>
                <strong>Email:</strong><br />
                {selectedCliente.email}
              </Col>
              <Col span={12}>
                <strong>Telefono:</strong><br />
                {selectedCliente.telefono}
              </Col>
            </Row>

            {selectedCliente.errori && selectedCliente.errori.length > 0 && (
              <div style={{ marginTop: '16px' }}>
                <Alert
                  message="Errori di Validazione"
                  description={
                    <ul>
                      {selectedCliente.errori.map((errore, index) => (
                        <li key={index}>{errore}</li>
                      ))}
                    </ul>
                  }
                  type="error"
                  showIcon
                />
              </div>
            )}
          </div>
        )}
      </Modal>
    </div>
  );
};

export default ImportClientiJson;
