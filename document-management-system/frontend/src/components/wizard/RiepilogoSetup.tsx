import React, { useState } from 'react';
import { 
  Button, 
  Row, 
  Col, 
  Typography, 
  Card, 
  Descriptions, 
  Divider,
  Alert,
  Spin,
  Tag,
  List,
  Tabs,
  Modal,
  Form
} from 'antd';
import { 
  ArrowLeftOutlined, 
  CheckCircleOutlined, 
  SaveOutlined,
  BankOutlined,
  SettingOutlined,
  TeamOutlined
} from '@ant-design/icons';
import { SetupData } from '../SetupWizard';

const { Title, Paragraph } = Typography;
const { TabPane } = Tabs;

interface Props {
  setupData: SetupData;
  onFinish: (data: any) => void;
  onPrev: () => void;
  loading: boolean;
}

const RiepilogoSetup: React.FC<Props> = ({ setupData, onFinish, onPrev, loading }) => {
  const [modalVisible, setModalVisible] = useState(false);

  const handleFinish = () => {
    const finalData = {
      completedAt: new Date().toISOString(),
      version: '1.0.0'
    };
    onFinish(finalData);
  };

  const handleModalSubmit = (values: any) => {
    console.log('Modal form values:', values);
    setModalVisible(false);
  };

  const { azienda, configurazioni, anagrafiche } = setupData;

  const riepilogoTabs = [
    {
      key: 'azienda',
      label: (
        <span>
          <BankOutlined style={{ marginRight: '8px' }} />
          Dati Azienda
        </span>
      ),
      children: (
        <Card 
          style={{ marginBottom: '16px' }}
          size="small"
        >
          {azienda ? (
            <Descriptions size="small" column={2}>
              <Descriptions.Item label="Ragione Sociale">
                {azienda.ragioneSociale}
              </Descriptions.Item>
              <Descriptions.Item label="Partita IVA">
                {azienda.partitaIva}
              </Descriptions.Item>
              <Descriptions.Item label="Codice Fiscale">
                {azienda.codiceFiscale}
              </Descriptions.Item>
              <Descriptions.Item label="Codice SDI">
                {azienda.codiceSdi}
              </Descriptions.Item>
              <Descriptions.Item label="Email">
                {azienda.email}
              </Descriptions.Item>
              <Descriptions.Item label="Telefono">
                {azienda.telefono || 'Non specificato'}
              </Descriptions.Item>
              <Descriptions.Item label="Indirizzo" span={2}>
                {`${azienda.indirizzo}, ${azienda.cap} ${azienda.citta} (${azienda.provincia})`}
              </Descriptions.Item>
            </Descriptions>
          ) : (
            <Alert message="Dati azienda non configurati" type="warning" />
          )}
        </Card>
      ),
    },
    {
      key: 'configurazioni',
      label: (
        <span>
          <SettingOutlined style={{ marginRight: '8px' }} />
          Configurazioni Sistema
        </span>
      ),
      children: (
        <Card 
          style={{ marginBottom: '16px' }}
          size="small"
        >
          {configurazioni ? (
            <Row gutter={[16, 16]}>
              <Col xs={24} md={12}>
                <Descriptions size="small" title="Numerazioni" column={1}>
                  <Descriptions.Item label="Anno di riferimento">
                    {configurazioni.annoCorrente}
                  </Descriptions.Item>
                  <Descriptions.Item label="Prossimo numero Fattura">
                    {configurazioni.prossimoNumeroFattura}
                  </Descriptions.Item>
                  <Descriptions.Item label="Prossimo numero DDT">
                    {configurazioni.prossimoNumeroDdt}
                  </Descriptions.Item>
                </Descriptions>
              </Col>
              <Col xs={24} md={12}>
                <Descriptions size="small" title="Impostazioni" column={1}>
                  <Descriptions.Item label="Regime Fiscale">
                    {configurazioni.regimeFiscale}
                  </Descriptions.Item>
                  <Descriptions.Item label="Valuta">
                    {configurazioni.valutaPredefinita}
                  </Descriptions.Item>
                  <Descriptions.Item label="Giorni scadenza">
                    {configurazioni.giorniScadenzaPagamento} giorni
                  </Descriptions.Item>
                </Descriptions>
              </Col>
              
              {configurazioni.aliquoteIva && configurazioni.aliquoteIva.length > 0 && (
                <Col xs={24}>
                  <Paragraph strong>Aliquote IVA configurate:</Paragraph>
                  <div>
                    {configurazioni.aliquoteIva.map((aliquota: any, index: number) => (
                      <Tag key={index} color="blue" style={{ marginBottom: '4px' }}>
                        {aliquota.valore}% - {aliquota.descrizione}
                      </Tag>
                    ))}
                  </div>
                </Col>
              )}

              {configurazioni.modalitaPagamento && configurazioni.modalitaPagamento.length > 0 && (
                <Col xs={24}>
                  <Paragraph strong>Modalità di Pagamento:</Paragraph>
                  <div>
                    {configurazioni.modalitaPagamento.map((modalita: string, index: number) => (
                      <Tag key={index} color="green" style={{ marginBottom: '4px' }}>
                        {modalita}
                      </Tag>
                    ))}
                  </div>
                </Col>
              )}
            </Row>
          ) : (
            <Alert message="Configurazioni non impostate" type="warning" />
          )}
        </Card>
      ),
    },
    {
      key: 'anagrafiche',
      label: (
        <span>
          <TeamOutlined style={{ marginRight: '8px' }} />
          Anagrafiche
        </span>
      ),
      children: (
        <Card 
          style={{ marginBottom: '16px' }}
          size="small"
        >
          {anagrafiche ? (
            <Row gutter={[16, 16]}>
              <Col xs={24} md={8}>
                <Card size="small" title="Clienti" bordered={false}>
                  <div style={{ textAlign: 'center' }}>
                    <Title level={2} style={{ color: '#1890ff', margin: 0 }}>
                      {anagrafiche.clienti?.length || 0}
                    </Title>
                    <Paragraph type="secondary">clienti configurati</Paragraph>
                  </div>
                  {anagrafiche.clienti && anagrafiche.clienti.length > 0 && (
                    <List
                      size="small"
                      dataSource={anagrafiche.clienti.slice(0, 3)}
                      renderItem={(cliente: any) => (
                        <List.Item>
                          <Typography.Text ellipsis>{cliente.ragioneSociale}</Typography.Text>
                        </List.Item>
                      )}
                    />
                  )}
                  {anagrafiche.clienti && anagrafiche.clienti.length > 3 && (
                    <Paragraph type="secondary" style={{ textAlign: 'center', margin: 0 }}>
                      ... e altri {anagrafiche.clienti.length - 3}
                    </Paragraph>
                  )}
                </Card>
              </Col>

              <Col xs={24} md={8}>
                <Card size="small" title="Fornitori" bordered={false}>
                  <div style={{ textAlign: 'center' }}>
                    <Title level={2} style={{ color: '#52c41a', margin: 0 }}>
                      {anagrafiche.fornitori?.length || 0}
                    </Title>
                    <Paragraph type="secondary">fornitori configurati</Paragraph>
                  </div>
                  {anagrafiche.fornitori && anagrafiche.fornitori.length > 0 && (
                    <List
                      size="small"
                      dataSource={anagrafiche.fornitori.slice(0, 3)}
                      renderItem={(fornitore: any) => (
                        <List.Item>
                          <Typography.Text ellipsis>{fornitore.ragioneSociale}</Typography.Text>
                        </List.Item>
                      )}
                    />
                  )}
                  {anagrafiche.fornitori && anagrafiche.fornitori.length > 3 && (
                    <Paragraph type="secondary" style={{ textAlign: 'center', margin: 0 }}>
                      ... e altri {anagrafiche.fornitori.length - 3}
                    </Paragraph>
                  )}
                </Card>
              </Col>

              <Col xs={24} md={8}>
                <Card size="small" title="Articoli" bordered={false}>
                  <div style={{ textAlign: 'center' }}>
                    <Title level={2} style={{ color: '#f5222d', margin: 0 }}>
                      {anagrafiche.articoli?.length || 0}
                    </Title>
                    <Paragraph type="secondary">articoli configurati</Paragraph>
                  </div>
                  {anagrafiche.articoli && anagrafiche.articoli.length > 0 && (
                    <List
                      size="small"
                      dataSource={anagrafiche.articoli.slice(0, 3)}
                      renderItem={(articolo: any) => (
                        <List.Item>
                          <Typography.Text ellipsis>
                            {articolo.codice} - {articolo.descrizione}
                          </Typography.Text>
                        </List.Item>
                      )}
                    />
                  )}
                  {anagrafiche.articoli && anagrafiche.articoli.length > 3 && (
                    <Paragraph type="secondary" style={{ textAlign: 'center', margin: 0 }}>
                      ... e altri {anagrafiche.articoli.length - 3}
                    </Paragraph>
                  )}
                </Card>
              </Col>
            </Row>
          ) : (
            <Alert message="Nessuna anagrafica configurata" type="info" />
          )}
        </Card>
      ),
    },
  ];

  return (
    <div>
      <div style={{ marginBottom: '24px', textAlign: 'center' }}>
        <Title level={3}>
          <CheckCircleOutlined style={{ marginRight: '8px', color: '#52c41a' }} />
          Riepilogo Configurazione
        </Title>
        <Paragraph type="secondary">
          Verifica tutti i dati inseriti prima di completare la configurazione iniziale
        </Paragraph>
      </div>

      <Alert
        message="Configurazione Quasi Completata!"
        description="Stai per completare la configurazione iniziale del sistema. Tutti i dati verranno salvati nel database."
        type="success"
        showIcon
        style={{ marginBottom: '24px' }}
      />

      <Tabs defaultActiveKey="azienda" items={riepilogoTabs} />

      <Divider />

      <Alert
        message="Importante"
        description="Dopo aver completato la configurazione, potrai sempre modificare questi dati dalle impostazioni del sistema."
        type="info"
        showIcon
        style={{ marginBottom: '24px' }}
      />

      <div style={{ display: 'flex', justifyContent: 'space-between' }}>
        <Button 
          size="large"
          onClick={onPrev}
          icon={<ArrowLeftOutlined />}
          disabled={loading}
        >
          Indietro
        </Button>
        
        <Button 
          type="primary" 
          onClick={handleFinish}
          size="large"
          icon={loading ? <Spin size="small" /> : <SaveOutlined />}
          loading={loading}
        >
          {loading ? 'Salvataggio...' : 'Completa Configurazione'}
        </Button>
      </div>

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

export default RiepilogoSetup;
