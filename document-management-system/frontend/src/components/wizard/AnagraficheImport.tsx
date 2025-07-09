import React, { useState } from 'react';
import { 
  Form, 
  Button, 
  Row, 
  Col, 
  Typography, 
  Tabs, 
  Upload, 
  message,
  Table,
  Input,
  Select,
  Modal,
  Card,
  Alert
} from 'antd';
import { 
  ArrowLeftOutlined, 
  ArrowRightOutlined, 
  TeamOutlined,
  UploadOutlined,
  PlusOutlined,
  InboxOutlined,
  FileExcelOutlined,
  UserOutlined,
  ShopOutlined,
  TagsOutlined
} from '@ant-design/icons';

const { Title, Paragraph } = Typography;
const { TabPane } = Tabs;
const { Dragger } = Upload;

interface Props {
  onNext: (data: any) => void;
  onPrev: () => void;
  initialData?: any;
}

interface Cliente {
  id: number;
  ragioneSociale: string;
  partitaIva: string;
  email: string;
  citta: string;
}

interface Fornitore {
  id: number;
  ragioneSociale: string;
  partitaIva: string;
  email: string;
  citta: string;
  settore?: string;
}

interface Articolo {
  id: number;
  codice: string;
  descrizione: string;
  prezzo: number;
  categoria: string;
  unitaMisura: string;
}

const AnagraficheImport: React.FC<Props> = ({ onNext, onPrev, initialData }) => {
  const [activeTab, setActiveTab] = useState('clienti');
  const [clienti, setClienti] = useState<Cliente[]>([]);
  const [fornitori, setFornitori] = useState<Fornitore[]>([]);
  const [articoli, setArticoli] = useState<Articolo[]>([]);
  const [modalVisible, setModalVisible] = useState(false);
  const [modalType, setModalType] = useState<'cliente' | 'fornitore' | 'articolo'>('cliente');

  const handleSubmit = () => {
    const data = {
      clienti,
      fornitori,
      articoli,
      importMethod: 'manual' // o 'json' se importato da file
    };
    console.log('Anagrafiche:', data);
    onNext(data);
  };

  const uploadProps = {
    name: 'file',
    multiple: false,
    accept: '.json,.csv,.xlsx',
    beforeUpload: (file: File) => {
      const isValidType = file.type === 'application/json' || 
                         file.name.endsWith('.csv') || 
                         file.name.endsWith('.xlsx');
      if (!isValidType) {
        message.error('Puoi caricare solo file JSON, CSV o Excel!');
        return false;
      }
      
      // Qui implementeremo la logica di parsing del file
      const reader = new FileReader();
      reader.onload = (e) => {
        try {
          if (file.type === 'application/json') {
            const data = JSON.parse(e.target?.result as string);
            handleImportData(data);
          } else {
            message.info('Parsing CSV/Excel sarà implementato nella prossima versione');
          }
        } catch (error) {
          message.error('Errore nel parsing del file');
        }
      };
      reader.readAsText(file);
      return false;
    },
  };

  const handleImportData = (data: any) => {
    if (data.clienti) {
      setClienti(data.clienti);
      message.success(`Importati ${data.clienti.length} clienti`);
    }
    if (data.fornitori) {
      setFornitori(data.fornitori);
      message.success(`Importati ${data.fornitori.length} fornitori`);
    }
    if (data.articoli) {
      setArticoli(data.articoli);
      message.success(`Importati ${data.articoli.length} articoli`);
    }
  };

  const openModal = (type: 'cliente' | 'fornitore' | 'articolo') => {
    setModalType(type);
    setModalVisible(true);
  };

  const handleModalSubmit = (values: any) => {
    const newItem = { ...values, id: Date.now() };
    
    switch (modalType) {
      case 'cliente':
        setClienti([...clienti, newItem]);
        break;
      case 'fornitore':
        setFornitori([...fornitori, newItem]);
        break;
      case 'articolo':
        setArticoli([...articoli, newItem]);
        break;
    }
    
    setModalVisible(false);
    message.success(`${modalType} aggiunto con successo`);
  };

  const clientiColumns = [
    { title: 'Ragione Sociale', dataIndex: 'ragioneSociale', key: 'ragioneSociale' },
    { title: 'Partita IVA', dataIndex: 'partitaIva', key: 'partitaIva' },
    { title: 'Email', dataIndex: 'email', key: 'email' },
    { title: 'Città', dataIndex: 'citta', key: 'citta' },
  ];

  const fornitoriColumns = [
    { title: 'Ragione Sociale', dataIndex: 'ragioneSociale', key: 'ragioneSociale' },
    { title: 'Partita IVA', dataIndex: 'partitaIva', key: 'partitaIva' },
    { title: 'Email', dataIndex: 'email', key: 'email' },
    { title: 'Settore', dataIndex: 'settore', key: 'settore' },
  ];

  const articoliColumns = [
    { title: 'Codice', dataIndex: 'codice', key: 'codice' },
    { title: 'Descrizione', dataIndex: 'descrizione', key: 'descrizione' },
    { title: 'Prezzo', dataIndex: 'prezzo', key: 'prezzo', render: (val: number) => `€${val?.toFixed(2)}` },
    { title: 'Categoria', dataIndex: 'categoria', key: 'categoria' },
  ];

  return (
    <div>
      <div style={{ marginBottom: '24px' }}>
        <Title level={3}>
          <TeamOutlined style={{ marginRight: '8px' }} />
          Anagrafiche e Articoli
        </Title>
        <Paragraph type="secondary">
          Importa i tuoi clienti, fornitori e articoli da file JSON esistenti, 
          oppure inizia creandoli manualmente.
        </Paragraph>
      </div>

      {/* Import da File */}
      <Card 
        title={
          <span>
            <FileExcelOutlined style={{ marginRight: '8px' }} />
            Importa da File
          </span>
        }
        style={{ marginBottom: '24px' }}
      >
        <Alert
          message="Formato File JSON"
          description={
            <div>
              <p>Il file JSON deve avere questa struttura:</p>
              <pre style={{ fontSize: '12px', background: '#f5f5f5', padding: '8px', borderRadius: '4px' }}>
{`{
  "clienti": [{"ragioneSociale": "...", "partitaIva": "...", ...}],
  "fornitori": [{"ragioneSociale": "...", "partitaIva": "...", ...}],
  "articoli": [{"codice": "...", "descrizione": "...", "prezzo": 0, ...}]
}`}
              </pre>
            </div>
          }
          type="info"
          style={{ marginBottom: '16px' }}
        />
        
        <Dragger {...uploadProps} style={{ height: '100px' }}>
          <p className="ant-upload-drag-icon">
            <InboxOutlined />
          </p>
          <p className="ant-upload-text">
            Clicca o trascina il file qui per importare
          </p>
          <p className="ant-upload-hint">
            Formati supportati: JSON, CSV, Excel
          </p>
        </Dragger>
      </Card>

      {/* Gestione Manuale */}
      <Card
        title="Gestione Manuale"
        extra={
          <Button.Group>
            <Button 
              icon={<UserOutlined />} 
              onClick={() => openModal('cliente')}
            >
              Nuovo Cliente
            </Button>
            <Button 
              icon={<ShopOutlined />} 
              onClick={() => openModal('fornitore')}
            >
              Nuovo Fornitore
            </Button>
            <Button 
              icon={<TagsOutlined />} 
              onClick={() => openModal('articolo')}
            >
              Nuovo Articolo
            </Button>
          </Button.Group>
        }
      >
        <Tabs activeKey={activeTab} onChange={setActiveTab}>
          <TabPane 
            tab={
              <span>
                <UserOutlined />
                Clienti ({clienti.length})
              </span>
            } 
            key="clienti"
          >
            <Table
              dataSource={clienti}
              columns={clientiColumns}
              rowKey="id"
              size="small"
              pagination={{ pageSize: 5 }}
              locale={{ emptyText: 'Nessun cliente inserito' }}
            />
          </TabPane>

          <TabPane 
            tab={
              <span>
                <ShopOutlined />
                Fornitori ({fornitori.length})
              </span>
            } 
            key="fornitori"
          >
            <Table
              dataSource={fornitori}
              columns={fornitoriColumns}
              rowKey="id"
              size="small"
              pagination={{ pageSize: 5 }}
              locale={{ emptyText: 'Nessun fornitore inserito' }}
            />
          </TabPane>

          <TabPane 
            tab={
              <span>
                <TagsOutlined />
                Articoli ({articoli.length})
              </span>
            } 
            key="articoli"
          >
            <Table
              dataSource={articoli}
              columns={articoliColumns}
              rowKey="id"
              size="small"
              pagination={{ pageSize: 5 }}
              locale={{ emptyText: 'Nessun articolo inserito' }}
            />
          </TabPane>
        </Tabs>
      </Card>

      {/* Modal per aggiunta manuale */}
      <Modal
        title={`Nuovo ${modalType}`}
        open={modalVisible}
        onCancel={() => setModalVisible(false)}
        footer={null}
        width={600}
      >
        <Form
          layout="vertical"
          onFinish={handleModalSubmit}
        >
          {modalType === 'articolo' ? (
            <Row gutter={[16, 16]}>
              <Col xs={24} md={12}>
                <Form.Item
                  label="Codice Articolo"
                  name="codice"
                  rules={[{ required: true, message: 'Inserisci il codice' }]}
                >
                  <Input placeholder="ART001" />
                </Form.Item>
              </Col>
              <Col xs={24} md={12}>
                <Form.Item
                  label="Categoria"
                  name="categoria"
                >
                  <Select placeholder="Seleziona categoria">
                    <Select.Option value="prodotti">Prodotti</Select.Option>
                    <Select.Option value="servizi">Servizi</Select.Option>
                    <Select.Option value="materiali">Materiali</Select.Option>
                  </Select>
                </Form.Item>
              </Col>
              <Col xs={24}>
                <Form.Item
                  label="Descrizione"
                  name="descrizione"
                  rules={[{ required: true, message: 'Inserisci la descrizione' }]}
                >
                  <Input placeholder="Descrizione articolo" />
                </Form.Item>
              </Col>
              <Col xs={24} md={12}>
                <Form.Item
                  label="Prezzo"
                  name="prezzo"
                  rules={[{ required: true, message: 'Inserisci il prezzo' }]}
                >
                  <Input 
                    type="number" 
                    step="0.01" 
                    placeholder="0.00"
                    addonAfter="€"
                  />
                </Form.Item>
              </Col>
              <Col xs={24} md={12}>
                <Form.Item
                  label="Unità di Misura"
                  name="unitaMisura"
                  initialValue="pz"
                >
                  <Select>
                    <Select.Option value="pz">Pezzo</Select.Option>
                    <Select.Option value="kg">Chilogrammo</Select.Option>
                    <Select.Option value="m">Metro</Select.Option>
                    <Select.Option value="h">Ora</Select.Option>
                  </Select>
                </Form.Item>
              </Col>
            </Row>
          ) : (
            <Row gutter={[16, 16]}>
              <Col xs={24}>
                <Form.Item
                  label="Ragione Sociale"
                  name="ragioneSociale"
                  rules={[{ required: true, message: 'Inserisci la ragione sociale' }]}
                >
                  <Input placeholder="Nome azienda" />
                </Form.Item>
              </Col>
              <Col xs={24} md={12}>
                <Form.Item
                  label="Partita IVA"
                  name="partitaIva"
                  rules={[
                    { required: true, message: 'Inserisci la Partita IVA' },
                    { pattern: /^[0-9]{11}$/, message: 'Partita IVA non valida' }
                  ]}
                >
                  <Input placeholder="12345678901" maxLength={11} />
                </Form.Item>
              </Col>
              <Col xs={24} md={12}>
                <Form.Item
                  label="Email"
                  name="email"
                  rules={[
                    { required: true, message: 'Inserisci l\'email' },
                    { type: 'email', message: 'Email non valida' }
                  ]}
                >
                  <Input placeholder="email@esempio.it" />
                </Form.Item>
              </Col>
              <Col xs={24} md={12}>
                <Form.Item
                  label="Città"
                  name="citta"
                  rules={[{ required: true, message: 'Inserisci la città' }]}
                >
                  <Input placeholder="Roma" />
                </Form.Item>
              </Col>
              {modalType === 'fornitore' && (
                <Col xs={24} md={12}>
                  <Form.Item
                    label="Settore"
                    name="settore"
                  >
                    <Input placeholder="Es. Informatica" />
                  </Form.Item>
                </Col>
              )}
            </Row>
          )}
          
          <div style={{ textAlign: 'right', marginTop: '24px' }}>
            <Button onClick={() => setModalVisible(false)} style={{ marginRight: '8px' }}>
              Annulla
            </Button>
            <Button type="primary" htmlType="submit">
              Salva
            </Button>
          </div>
        </Form>
      </Modal>

      <div style={{ marginTop: '32px', display: 'flex', justifyContent: 'space-between' }}>
        <Button 
          size="large"
          onClick={onPrev}
          icon={<ArrowLeftOutlined />}
        >
          Indietro
        </Button>
        
        <Button 
          type="primary" 
          onClick={handleSubmit}
          size="large"
          icon={<ArrowRightOutlined />}
        >
          Continua
        </Button>
      </div>
    </div>
  );
};

export default AnagraficheImport;
