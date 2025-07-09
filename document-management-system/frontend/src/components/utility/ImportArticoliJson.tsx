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

interface ArticoloImportato {
  id?: string;
  codice: string;
  descrizione: string;
  quantita?: number;
  prezzoUnitario: number;
  importo?: number;
  unitaMisura?: string;
  aliquotaIVA?: number;
  cliente?: {
    partitaIva?: string;
    ragioneSociale?: string;
    categoria?: string;
  };
  dataDocumento?: string;
  numeroFattura?: string;
  // Campi mappati per il database
  prezzo?: number;
  aliquotaIva?: number;
  tipo?: 'PRODOTTO' | 'SERVIZIO' | 'MATERIA_PRIMA' | 'SEMILAVORATO';
  stato?: 'valido' | 'errore' | 'duplicato';
  errori?: string[];
}

interface Props {
  onStatsUpdate?: (stats: any) => void;
}

const ImportArticoliJson: React.FC<Props> = ({ onStatsUpdate }) => {
  const [articoliDaImportare, setArticoliDaImportare] = useState<ArticoloImportato[]>([]);
  const [isImporting, setIsImporting] = useState(false);
  const [importProgress, setImportProgress] = useState(0);
  const [importStats, setImportStats] = useState({
    totali: 0,
    validi: 0,
    errori: 0,
    duplicati: 0,
    articoliUnici: 0
  });
  const [showPreview, setShowPreview] = useState(false);
  const [selectedArticolo, setSelectedArticolo] = useState<ArticoloImportato | null>(null);

  // Validazione codice articolo
  const validateCodiceArticolo = (codice: string): boolean => {
    return !!codice && codice.trim().length > 0 && codice.trim().length <= 50;
  };

  // Validazione prezzo
  const validatePrezzo = (prezzo: number): boolean => {
    return prezzo !== null && prezzo !== undefined && prezzo >= 0;
  };

  // Validazione e mapping dei dati
  const validateAndMapArticolo = (articolo: any): ArticoloImportato => {
    const errori: string[] = [];

    // Validazioni
    if (!articolo.codice?.trim()) {
      errori.push('Codice articolo obbligatorio');
    } else if (!validateCodiceArticolo(articolo.codice)) {
      errori.push('Codice articolo non valido');
    }

    if (!articolo.descrizione?.trim()) {
      errori.push('Descrizione obbligatoria');
    }

    if (!validatePrezzo(articolo.prezzoUnitario)) {
      errori.push('Prezzo unitario obbligatorio e deve essere >= 0');
    }

    if (articolo.aliquotaIVA !== undefined && 
        (articolo.aliquotaIVA < 0 || articolo.aliquotaIVA > 100)) {
      errori.push('Aliquota IVA deve essere tra 0 e 100');
    }

    const articoloMappato: ArticoloImportato = {
      ...articolo,
      codice: articolo.codice?.trim().toUpperCase() || '',
      descrizione: articolo.descrizione?.trim() || '',
      prezzoUnitario: Number(articolo.prezzoUnitario) || 0,
      quantita: Number(articolo.quantita) || 0,
      importo: Number(articolo.importo) || 0,
      unitaMisura: articolo.unitaMisura?.trim().toUpperCase() || 'PZ',
      aliquotaIVA: Number(articolo.aliquotaIVA) || 22,
      // Mapping per il database
      prezzo: Number(articolo.prezzoUnitario) || 0,
      aliquotaIva: Number(articolo.aliquotaIVA) || 22,
      tipo: 'PRODOTTO',
      stato: errori.length > 0 ? 'errore' : 'valido',
      errori
    };

    return articoloMappato;
  };

  // Gestione upload file
  const handleFileUpload = (file: File) => {
    const reader = new FileReader();
    
    reader.onload = (e) => {
      try {
        const jsonData = JSON.parse(e.target?.result as string);
        
        if (!Array.isArray(jsonData)) {
          message.error('Il file deve contenere un array di articoli');
          return;
        }

        // Mappa e valida gli articoli
        const articoliMappati = jsonData.map((articolo, index) => ({
          ...validateAndMapArticolo(articolo),
          id: `import_${index}`
        }));

        // Raggruppa articoli per codice per identificare duplicati unici
        const articoliPerCodice = new Map<string, ArticoloImportato[]>();
        articoliMappati.forEach(articolo => {
          if (articolo.codice) {
            if (!articoliPerCodice.has(articolo.codice)) {
              articoliPerCodice.set(articolo.codice, []);
            }
            articoliPerCodice.get(articolo.codice)!.push(articolo);
          }
        });

        // Calcola statistiche
        const stats = {
          totali: articoliMappati.length,
          validi: articoliMappati.filter(a => a.stato === 'valido').length,
          errori: articoliMappati.filter(a => a.stato === 'errore').length,
          duplicati: 0, // TODO: implementare controllo duplicati con DB
          articoliUnici: articoliPerCodice.size
        };

        setArticoliDaImportare(articoliMappati);
        setImportStats(stats);
        setShowPreview(true);
        
        message.success(`Caricati ${articoliMappati.length} articoli dal file JSON (${stats.articoliUnici} codici unici)`);
        
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
      // Raggruppa articoli per codice e prendi il primo record valido per ogni codice
      const articoliUnici = new Map<string, ArticoloImportato>();
      
      articoliDaImportare
        .filter(a => a.stato === 'valido')
        .forEach(articolo => {
          if (!articoliUnici.has(articolo.codice)) {
            articoliUnici.set(articolo.codice, articolo);
          }
        });

      // Prepara dati per il backend (solo articoli unici)
      const articoliJson = Array.from(articoliUnici.values()).map(articolo => ({
        codice: articolo.codice,
        descrizione: articolo.descrizione,
        prezzoUnitario: articolo.prezzoUnitario,
        unitaMisura: articolo.unitaMisura,
        aliquotaIVA: articolo.aliquotaIVA
      }));

      // Crea un Blob con i dati JSON
      const jsonBlob = new Blob([JSON.stringify(articoliJson)], { 
        type: 'application/json' 
      });

      // Crea FormData per l'upload
      const formData = new FormData();
      formData.append('file', jsonBlob, 'articoli.json');
      formData.append('preview', 'false');

      // Chiamata API per importazione
      const response = await fetch('http://localhost:8080/api/import/articoli/json', {
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
        message.success(`Importazione completata! Salvati ${result.successfulRecords} articoli su ${result.totalRecords}`);
        
        if (result.errors && result.errors.length > 0) {
          console.warn('Errori durante importazione:', result.errors);
        }
      } else {
        message.warning(`Importazione parziale: ${result.successfulRecords}/${result.totalRecords} articoli salvati`);
        
        if (result.errors && result.errors.length > 0) {
          message.error(`Errori: ${result.errors.slice(0, 3).join(', ')}${result.errors.length > 3 ? '...' : ''}`);
        }
      }
      
      // Reset stato
      setArticoliDaImportare([]);
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
      render: (record: ArticoloImportato) => {
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
      title: 'Codice',
      dataIndex: 'codice',
      key: 'codice',
      width: 100,
      render: (text: string) => <Text code>{text}</Text>
    },
    {
      title: 'Descrizione',
      dataIndex: 'descrizione',
      key: 'descrizione',
      width: 200,
      render: (text: string) => <strong>{text}</strong>,
      ellipsis: true
    },
    {
      title: 'Prezzo',
      dataIndex: 'prezzoUnitario',
      key: 'prezzoUnitario',
      width: 100,
      render: (prezzo: number) => `€ ${prezzo?.toFixed(2) || '0.00'}`
    },
    {
      title: 'U.M.',
      dataIndex: 'unitaMisura',
      key: 'unitaMisura',
      width: 80,
      render: (um: string) => <Tag color="blue">{um}</Tag>
    },
    {
      title: 'IVA',
      dataIndex: 'aliquotaIVA',
      key: 'aliquotaIVA',
      width: 80,
      render: (iva: number) => `${iva || 22}%`
    },
    {
      title: 'Cliente',
      key: 'cliente',
      width: 150,
      render: (record: ArticoloImportato) => (
        record.cliente?.ragioneSociale ? (
          <Tooltip title={`P.IVA: ${record.cliente.partitaIva}`}>
            <Text ellipsis style={{ maxWidth: 120 }}>
              {record.cliente.ragioneSociale}
            </Text>
          </Tooltip>
        ) : '-'
      ),
      ellipsis: true
    },
    {
      title: 'Azioni',
      key: 'actions',
      width: 100,
      render: (record: ArticoloImportato) => (
        <Button
          type="link"
          icon={<EyeOutlined />}
          onClick={() => setSelectedArticolo(record)}
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
            <Title level={4}>Carica File JSON Articoli</Title>
            <Paragraph type="secondary">
              Supporta il formato JSON con la struttura del file articoliClienti.json fornito
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
              Supporta solo file .json. Il file deve contenere un array di oggetti articolo con vendite.
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
    "codice": "ART001",
    "descrizione": "ARTICOLO ESEMPIO",
    "quantita": 10.5,
    "prezzoUnitario": 15.50,
    "importo": 162.75,
    "unitaMisura": "KG",
    "aliquotaIVA": 22,
    "cliente": {
      "partitaIva": "12345678901",
      "ragioneSociale": "CLIENTE SRL"
    }
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
            <Col span={4}>
              <Card size="small">
                <Statistic
                  title="Totali"
                  value={importStats.totali}
                  valueStyle={{ color: '#1890ff' }}
                />
              </Card>
            </Col>
            <Col span={4}>
              <Card size="small">
                <Statistic
                  title="Unici"
                  value={importStats.articoliUnici}
                  valueStyle={{ color: '#722ed1' }}
                />
              </Card>
            </Col>
            <Col span={4}>
              <Card size="small">
                <Statistic
                  title="Validi"
                  value={importStats.validi}
                  valueStyle={{ color: '#52c41a' }}
                />
              </Card>
            </Col>
            <Col span={4}>
              <Card size="small">
                <Statistic
                  title="Errori"
                  value={importStats.errori}
                  valueStyle={{ color: '#ff4d4f' }}
                />
              </Card>
            </Col>
            <Col span={4}>
              <Card size="small">
                <Statistic
                  title="Duplicati"
                  value={importStats.duplicati}
                  valueStyle={{ color: '#faad14' }}
                />
              </Card>
            </Col>
          </Row>

          {/* Informazioni aggiuntive */}
          <Alert
            message="Informazioni Importazione"
            description={`Verranno importati solo gli articoli unici (${importStats.articoliUnici} codici diversi). Per codici duplicati verrà preso il primo record valido.`}
            type="info"
            showIcon
            style={{ marginBottom: '16px' }}
          />

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
                  Elaborazione articolo {Math.round((importProgress / 100) * importStats.articoliUnici)} di {importStats.articoliUnici}
                </Text>
              </div>
            </Card>
          )}

          {/* Tabella Preview */}
          <Card
            title={
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <span>Anteprima Articoli da Importare</span>
                <Space>
                  <Button
                    icon={<ClearOutlined />}
                    onClick={() => {
                      setShowPreview(false);
                      setArticoliDaImportare([]);
                    }}
                  >
                    Annulla
                  </Button>
                  <Button
                    type="primary"
                    icon={<SaveOutlined />}
                    loading={isImporting}
                    disabled={importStats.articoliUnici === 0}
                    onClick={executeImport}
                  >
                    Importa {importStats.articoliUnici} Articoli Unici
                  </Button>
                </Space>
              </div>
            }
          >
            <Table
              columns={columns}
              dataSource={articoliDaImportare}
              rowKey="id"
              pagination={{ pageSize: 10 }}
              size="small"
              scroll={{ x: 1200 }}
            />
          </Card>
        </div>
      )}

      {/* Modal Dettaglio Articolo */}
      <Modal
        title="Dettaglio Articolo"
        open={!!selectedArticolo}
        onCancel={() => setSelectedArticolo(null)}
        footer={null}
        width={800}
      >
        {selectedArticolo && (
          <div>
            <Row gutter={[16, 16]}>
              <Col span={12}>
                <strong>Codice:</strong><br />
                <Text code>{selectedArticolo.codice}</Text>
              </Col>
              <Col span={12}>
                <strong>Descrizione:</strong><br />
                {selectedArticolo.descrizione}
              </Col>
              <Col span={8}>
                <strong>Prezzo Unitario:</strong><br />
                € {selectedArticolo.prezzoUnitario?.toFixed(2)}
              </Col>
              <Col span={8}>
                <strong>Unità Misura:</strong><br />
                <Tag color="blue">{selectedArticolo.unitaMisura}</Tag>
              </Col>
              <Col span={8}>
                <strong>Aliquota IVA:</strong><br />
                {selectedArticolo.aliquotaIVA}%
              </Col>
              
              {selectedArticolo.quantita && (
                <>
                  <Col span={8}>
                    <strong>Quantità:</strong><br />
                    {selectedArticolo.quantita}
                  </Col>
                  <Col span={8}>
                    <strong>Importo:</strong><br />
                    € {selectedArticolo.importo?.toFixed(2)}
                  </Col>
                  <Col span={8}>
                    <strong>Numero Fattura:</strong><br />
                    {selectedArticolo.numeroFattura}
                  </Col>
                </>
              )}

              {selectedArticolo.cliente && (
                <Col span={24}>
                  <strong>Cliente Associato:</strong><br />
                  <div style={{ background: '#f5f5f5', padding: '8px', borderRadius: '4px', marginTop: '4px' }}>
                    <strong>{selectedArticolo.cliente.ragioneSociale}</strong><br />
                    P.IVA: {selectedArticolo.cliente.partitaIva}<br />
                    Categoria: {selectedArticolo.cliente.categoria}
                  </div>
                </Col>
              )}
            </Row>

            {selectedArticolo.errori && selectedArticolo.errori.length > 0 && (
              <div style={{ marginTop: '16px' }}>
                <Alert
                  message="Errori di Validazione"
                  description={
                    <ul>
                      {selectedArticolo.errori.map((errore, index) => (
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

export default ImportArticoliJson;
