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

interface ArticoloFornitoreImportato {
  id?: string;
  codice: string;
  descrizione: string;
  quantita?: number;
  prezzoUnitario: number;
  importo: number;
  unitaMisura: string;
  aliquotaIVA: number;
  fornitore: {
    partitaIva?: string;
    ragioneSociale?: string;
    categoria?: string;
  };
  dataDocumento?: string;
  dataUltimoAggiornamento?: string;
  codiceInterno?: string;
  stato?: 'valido' | 'errore' | 'duplicato';
  errori?: string[];
}

interface Props {
  onStatsUpdate?: (stats: any) => void;
  onSuccess?: () => void;
  onImportComplete?: (result: any) => void;
}

const ImportArticoliFornitoriJson: React.FC<Props> = ({ onStatsUpdate, onSuccess, onImportComplete }) => {
  const [articoliDaImportare, setArticoliDaImportare] = useState<ArticoloFornitoreImportato[]>([]);
  const [isImporting, setIsImporting] = useState(false);
  const [importProgress, setImportProgress] = useState(0);
  const [importStats, setImportStats] = useState({
    totali: 0,
    validi: 0,
    errori: 0,
    duplicati: 0
  });
  const [showPreview, setShowPreview] = useState(false);
  const [selectedArticolo, setSelectedArticolo] = useState<ArticoloFornitoreImportato | null>(null);

  // Validazione e mapping dei dati
  const validateAndMapArticolo = (articolo: any): ArticoloFornitoreImportato => {
    const errori: string[] = [];

    // Validazioni
    if (!articolo.codice?.trim()) {
      errori.push('Codice articolo obbligatorio');
    }

    if (!articolo.descrizione?.trim()) {
      errori.push('Descrizione obbligatoria');
    }

    if (!articolo.quantita || articolo.quantita <= 0) {
      errori.push('Quantità obbligatoria e deve essere > 0');
    }

    if (!articolo.prezzoUnitario || articolo.prezzoUnitario <= 0) {
      errori.push('Prezzo unitario obbligatorio e deve essere > 0');
    }

    if (!articolo.unitaMisura?.trim()) {
      errori.push('Unità di misura obbligatoria');
    }

    if (!articolo.fornitore?.ragioneSociale?.trim()) {
      errori.push('Fornitore obbligatorio');
    }

    const articoloMappato: ArticoloFornitoreImportato = {
      ...articolo,
      codice: articolo.codice?.trim().toUpperCase() || '',
      descrizione: articolo.descrizione?.trim() || '',
      quantita: Number(articolo.quantita) || 0,
      prezzoUnitario: Number(articolo.prezzoUnitario) || 0,
      importo: Number(articolo.importo) || 0,
      unitaMisura: articolo.unitaMisura?.trim().toUpperCase() || '',
      aliquotaIVA: Number(articolo.aliquotaIVA) || 22,
      fornitore: {
        partitaIva: articolo.fornitore?.partitaIva?.trim() || '',
        ragioneSociale: articolo.fornitore?.ragioneSociale?.trim() || '',
        categoria: articolo.fornitore?.categoria?.trim() || ''
      },
      dataDocumento: articolo.dataDocumento || '',
      dataUltimoAggiornamento: articolo.dataUltimoAggiornamento || '',
      codiceInterno: articolo.codiceInterno?.trim() || '',
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

        // Calcola statistiche
        const stats = {
          totali: articoliMappati.length,
          validi: articoliMappati.filter(a => a.stato === 'valido').length,
          errori: articoliMappati.filter(a => a.stato === 'errore').length,
          duplicati: 0 // TODO: implementare controllo duplicati
        };

        setArticoliDaImportare(articoliMappati);
        setImportStats(stats);
        setShowPreview(true);
        
        message.success(`Caricati ${articoliMappati.length} articoli fornitori dal file JSON`);
        
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
      // Prepara dati per il backend - aggiungi il campo quantita se mancante
      const articoliJson = articoliDaImportare
        .filter(a => a.stato === 'valido')
        .map(articolo => ({
          codice: articolo.codice,
          descrizione: articolo.descrizione,
          quantita: articolo.quantita || 1, // Aggiungi quantita di default se mancante
          prezzoUnitario: articolo.prezzoUnitario,
          importo: articolo.importo,
          unitaMisura: articolo.unitaMisura,
          aliquotaIVA: articolo.aliquotaIVA,
          fornitore: articolo.fornitore,
          dataDocumento: articolo.dataDocumento,
          dataUltimoAggiornamento: articolo.dataUltimoAggiornamento,
          codiceInterno: articolo.codiceInterno
        }));

      // Crea un Blob con i dati JSON
      const jsonBlob = new Blob([JSON.stringify(articoliJson)], { 
        type: 'application/json' 
      });

      // Crea FormData per l'upload
      const formData = new FormData();
      formData.append('file', jsonBlob, 'articoli-fornitori.json');
      formData.append('preview', 'false');

      // Chiamata API per importazione
      const response = await fetch('http://localhost:8080/api/import/articoli-fornitori/json', {
        method: 'POST',
        body: formData
      });

      if (!response.ok) {
        const errorText = await response.text();
        console.error('Errore server:', errorText);
        throw new Error(`Errore HTTP ${response.status}: ${response.statusText}`);
      }

      const contentType = response.headers.get('content-type');
      if (!contentType || !contentType.includes('application/json')) {
        const responseText = await response.text();
        console.error('Risposta non JSON:', responseText);
        throw new Error('Il server ha restituito una risposta non valida (non JSON)');
      }

      const result = await response.json();
      
      // Simula progress per UX
      setImportProgress(50);
      await new Promise(resolve => setTimeout(resolve, 500));
      setImportProgress(100);
      
      // Adatta la risposta del backend al formato atteso dal frontend
      const totalRecords = result.articoliImportati + result.articoliScartati + result.articoliDuplicati + result.articoliAggiornati;
      const successfulRecords = result.articoliImportati + result.articoliAggiornati;
      const hasErrors = result.errori && result.errori.length > 0;
      const isSuccess = successfulRecords > 0 && !hasErrors;
      
      if (isSuccess) {
        message.success(`Importazione completata! Salvati ${successfulRecords} articoli fornitori su ${totalRecords}`);
        
        if (result.errori && result.errori.length > 0) {
          console.warn('Errori durante importazione:', result.errori);
        }
      } else {
        message.warning(`Importazione parziale: ${successfulRecords}/${totalRecords} articoli fornitori salvati`);
        
        if (result.errori && result.errori.length > 0) {
          message.error(`Errori: ${result.errori.slice(0, 3).join(', ')}${result.errori.length > 3 ? '...' : ''}`);
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
          recordImportati: successfulRecords,
          erroriUltimaImportazione: result.articoliScartati || 0
        });
      }

      // Chiama callback di successo
      if (onSuccess) {
        onSuccess();
      }

      // Chiama callback di completamento importazione
      if (onImportComplete) {
        onImportComplete({
          success: isSuccess,
          totalRecords: totalRecords,
          successfulRecords: successfulRecords,
          failedRecords: result.articoliScartati,
          errors: result.errori
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
      render: (record: ArticoloFornitoreImportato) => {
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
      width: 120,
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
      title: 'Quantità',
      dataIndex: 'quantita',
      key: 'quantita',
      width: 80,
      render: (quantita: number) => quantita?.toFixed(2) || '0.00'
    },
    {
      title: 'Fornitore',
      key: 'fornitore',
      width: 180,
      render: (record: ArticoloFornitoreImportato) => (
        <div>
          <div><strong>{record.fornitore.ragioneSociale}</strong></div>
          {record.fornitore.partitaIva && (
            <small>P.IVA: {record.fornitore.partitaIva}</small>
          )}
        </div>
      ),
      ellipsis: true
    },
    {
      title: 'Categoria',
      key: 'categoria',
      width: 100,
      render: (record: ArticoloFornitoreImportato) => (
        record.fornitore.categoria ? (
          <Tag color="blue">{record.fornitore.categoria}</Tag>
        ) : '-'
      )
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
      render: (um: string) => <Tag color="green">{um}</Tag>
    },
    {
      title: 'IVA',
      dataIndex: 'aliquotaIVA',
      key: 'aliquotaIVA',
      width: 80,
      render: (iva: number) => `${iva || 22}%`
    },
    {
      title: 'Azioni',
      key: 'actions',
      width: 100,
      render: (record: ArticoloFornitoreImportato) => (
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
            <Title level={4}>Carica File JSON Articoli Fornitori</Title>
            <Paragraph type="secondary">
              Supporta il formato JSON con la struttura del file articoli.json fornito
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
              Supporta solo file .json. Il file deve contenere un array di oggetti articolo fornitore.
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
    "codice": "CODART001",
    "descrizione": "Descrizione articolo",
    "quantita": 10.5,
    "prezzoUnitario": 15.90,
    "importo": 166.95,
    "unitaMisura": "KG",
    "aliquotaIVA": 10,
    "fornitore": {
      "partitaIva": "12345678901",
      "ragioneSociale": "FORNITORE SRL",
      "categoria": "merci"
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
                  Elaborazione articolo {Math.round((importProgress / 100) * importStats.validi)} di {importStats.validi}
                </Text>
              </div>
            </Card>
          )}

          {/* Tabella Preview */}
          <Card
            title={
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <span>Anteprima Articoli Fornitori da Importare</span>
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
                    disabled={importStats.validi === 0}
                    onClick={executeImport}
                  >
                    Importa {importStats.validi} Articoli
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
        title="Dettaglio Articolo Fornitore"
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
                <strong>Codice Interno:</strong><br />
                {selectedArticolo.codiceInterno || 'Non specificato'}
              </Col>
              <Col span={24}>
                <strong>Descrizione:</strong><br />
                {selectedArticolo.descrizione}
              </Col>
              <Col span={8}>
                <strong>Quantità:</strong><br />
                {selectedArticolo.quantita?.toFixed(2) || '0.00'}
              </Col>
              <Col span={8}>
                <strong>Prezzo Unitario:</strong><br />
                € {selectedArticolo.prezzoUnitario.toFixed(2)}
              </Col>
              <Col span={8}>
                <strong>Importo:</strong><br />
                € {selectedArticolo.importo.toFixed(2)}
              </Col>
              <Col span={8}>
                <strong>Unità di Misura:</strong><br />
                <Tag color="green">{selectedArticolo.unitaMisura}</Tag>
              </Col>
              <Col span={12}>
                <strong>Aliquota IVA:</strong><br />
                {selectedArticolo.aliquotaIVA}%
              </Col>
              <Col span={12}>
                <strong>Data Documento:</strong><br />
                {selectedArticolo.dataDocumento || 'Non specificata'}
              </Col>
              <Col span={24}>
                <strong>Fornitore:</strong><br />
                <div style={{ background: '#f5f5f5', padding: '8px', borderRadius: '4px', marginTop: '4px' }}>
                  <strong>{selectedArticolo.fornitore.ragioneSociale}</strong><br />
                  {selectedArticolo.fornitore.partitaIva && (
                    <>P.IVA: {selectedArticolo.fornitore.partitaIva}<br /></>
                  )}
                  {selectedArticolo.fornitore.categoria && (
                    <>Categoria: <Tag color="blue">{selectedArticolo.fornitore.categoria}</Tag></>
                  )}
                </div>
              </Col>
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

export default ImportArticoliFornitoriJson;
