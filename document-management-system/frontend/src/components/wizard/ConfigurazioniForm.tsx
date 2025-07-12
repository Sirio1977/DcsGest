import React, { useState } from 'react';
import { Form, Input, Button, Row, Col, Select, InputNumber, Switch, Typography, Divider, Tabs, Modal } from 'antd';
import { ArrowLeftOutlined, ArrowRightOutlined, SettingOutlined, PlusOutlined } from '@ant-design/icons';

const { Title, Paragraph } = Typography;
const { Option } = Select;
const { TabPane } = Tabs;

interface Props {
  onNext: (data: any) => void;
  onPrev: () => void;
  initialData?: any;
}

const ConfigurazioniForm: React.FC<Props> = ({ onNext, onPrev, initialData }) => {
  const [form] = Form.useForm();
  const [modalVisible, setModalVisible] = useState(false);

  const handleSubmit = (values: any) => {
    console.log('Configurazioni:', values);
    onNext(values);
  };

  const handleModalSubmit = (values: any) => {
    console.log('Modal Form Values:', values);
    setModalVisible(false);
  };

  const aliquoteIvaPredefinte = [
    { valore: 22, descrizione: 'Ordinaria 22%' },
    { valore: 10, descrizione: 'Ridotta 10%' },
    { valore: 4, descrizione: 'Ridotta 4%' },
    { valore: 5, descrizione: 'Ridotta 5%' },
    { valore: 0, descrizione: 'Esente/Non soggetto' },
  ];

  const modalitaPagamentoPredefinite = [
    'Contanti',
    'Bonifico bancario',
    'Assegno',
    'Carta di credito/debito',
    'PayPal',
    'Rimessa diretta',
    'Compensazione',
    'Bollettino postale',
    'MAV',
    'RID',
  ];

  const configurazioniTabs = [
    {
      key: 'numerazioni',
      label: 'Numerazioni Documenti',
      children: (
        <>
          <Paragraph type="secondary">
            Imposta le numerazioni per i diversi tipi di documento
          </Paragraph>

          <Row gutter={[16, 16]}>
            <Col xs={24} md={8}>
              <Form.Item
                label="Anno di riferimento"
                name="annoCorrente"
                rules={[{ required: true, message: 'Inserisci l\'anno' }]}
              >
                <InputNumber 
                  min={2020} 
                  max={2030} 
                  style={{ width: '100%' }}
                  placeholder={new Date().getFullYear().toString()}
                />
              </Form.Item>
            </Col>

            <Col xs={24} md={8}>
              <Form.Item
                label="Prossimo numero Fattura"
                name="prossimoNumeroFattura"
                rules={[{ required: true, message: 'Inserisci il numero fattura' }]}
                initialValue={1}
              >
                <InputNumber min={1} style={{ width: '100%' }} />
              </Form.Item>
            </Col>

            <Col xs={24} md={8}>
              <Form.Item
                label="Prossimo numero DDT"
                name="prossimoNumeroDdt"
                rules={[{ required: true, message: 'Inserisci il numero DDT' }]}
                initialValue={1}
              >
                <InputNumber min={1} style={{ width: '100%' }} />
              </Form.Item>
            </Col>

            <Col xs={24} md={8}>
              <Form.Item
                label="Prossimo numero Preventivo"
                name="prossimoNumeroPreventivo"
                initialValue={1}
              >
                <InputNumber min={1} style={{ width: '100%' }} />
              </Form.Item>
            </Col>

            <Col xs={24} md={8}>
              <Form.Item
                label="Prossimo numero Ordine"
                name="prossimoNumeroOrdine"
                initialValue={1}
              >
                <InputNumber min={1} style={{ width: '100%' }} />
              </Form.Item>
            </Col>

            <Col xs={24} md={8}>
              <Form.Item
                label="Prossimo numero NC/ND"
                name="prossimoNumeroNota"
                initialValue={1}
              >
                <InputNumber min={1} style={{ width: '100%' }} />
              </Form.Item>
            </Col>
          </Row>
        </>
      ),
    },
    {
      key: 'aliquote',
      label: 'Aliquote IVA',
      children: (
        <>
          <Paragraph type="secondary">
            Le aliquote IVA standard sono già preconfigurate. Puoi modificarle dopo la configurazione iniziale.
          </Paragraph>

          <Row gutter={[16, 16]}>
            <Col xs={24}>
              <Form.List name="aliquoteIva">
                {(fields, { add, remove }) => (
                  <>
                    {fields.map(({ key, name, ...restField }) => (
                      <Row key={key} gutter={[16, 8]} align="middle">
                        <Col xs={12} md={8}>
                          <Form.Item
                            {...restField}
                            name={[name, 'valore']}
                            rules={[{ required: true, message: 'Inserisci la percentuale' }]}
                          >
                            <InputNumber
                              min={0}
                              max={100}
                              step={0.01}
                              style={{ width: '100%' }}
                              placeholder="Percentuale"
                              addonAfter="%"
                            />
                          </Form.Item>
                        </Col>
                        <Col xs={12} md={14}>
                          <Form.Item
                            {...restField}
                            name={[name, 'descrizione']}
                            rules={[{ required: true, message: 'Inserisci la descrizione' }]}
                          >
                            <Input placeholder="Descrizione aliquota" />
                          </Form.Item>
                        </Col>
                        <Col xs={24} md={2}>
                          <Button
                            type="link"
                            danger
                            onClick={() => remove(name)}
                            style={{ width: '100%' }}
                          >
                            Rimuovi
                          </Button>
                        </Col>
                      </Row>
                    ))}
                    <Button
                      type="dashed"
                      onClick={() => add()}
                      block
                      icon={<PlusOutlined />}
                    >
                      Aggiungi Aliquota IVA
                    </Button>
                  </>
                )}
              </Form.List>
            </Col>
          </Row>
        </>
      ),
    },
    {
      key: 'pagamenti',
      label: 'Modalità di Pagamento',
      children: (
        <>
          <Paragraph type="secondary">
            Le modalità di pagamento più comuni sono già preconfigurate.
          </Paragraph>

          <Row gutter={[16, 16]}>
            <Col xs={24}>
              <Form.List name="modalitaPagamento">
                {(fields, { add, remove }) => (
                  <>
                    {fields.map(({ key, name, ...restField }) => (
                      <Row key={key} gutter={[16, 8]} align="middle">
                        <Col xs={20} md={22}>
                          <Form.Item
                            {...restField}
                            name={name}
                            rules={[{ required: true, message: 'Inserisci la modalità di pagamento' }]}
                          >
                            <Input placeholder="Es. Bonifico bancario" />
                          </Form.Item>
                        </Col>
                        <Col xs={4} md={2}>
                          <Button
                            type="link"
                            danger
                            onClick={() => remove(name)}
                            style={{ width: '100%' }}
                          >
                            Rimuovi
                          </Button>
                        </Col>
                      </Row>
                    ))}
                    <Button
                      type="dashed"
                      onClick={() => add()}
                      block
                      icon={<PlusOutlined />}
                    >
                      Aggiungi Modalità di Pagamento
                    </Button>
                  </>
                )}
              </Form.List>
            </Col>
          </Row>
        </>
      ),
    },
    {
      key: 'avanzate',
      label: 'Impostazioni Avanzate',
      children: (
        <Row gutter={[16, 16]}>
          <Col xs={24} md={12}>
            <Form.Item
              label="Regime Fiscale"
              name="regimeFiscale"
              rules={[{ required: true, message: 'Seleziona il regime fiscale' }]}
            >
              <Select placeholder="Seleziona regime">
                <Option value="RF01">Ordinario</Option>
                <Option value="RF02">Contribuenti minimi</Option>
                <Option value="RF04">Agricoltura e attività connesse</Option>
                <Option value="RF05">Vendita sali e tabacchi</Option>
                <Option value="RF06">Commercio fiammiferi</Option>
                <Option value="RF07">Editoria</Option>
                <Option value="RF08">Gestione servizi telefonia</Option>
                <Option value="RF09">Rivendita documenti di trasporto</Option>
                <Option value="RF10">Intrattenimenti, giochi e altre attività</Option>
                <Option value="RF11">Agenzie viaggi e turismo</Option>
                <Option value="RF12">Agriturismo</Option>
                <Option value="RF13">Vendite a domicilio</Option>
                <Option value="RF14">Rivendita beni usati, oggetti d'arte</Option>
                <Option value="RF15">Agenzie aste pubbliche</Option>
                <Option value="RF16">IVA per cassa P.A.</Option>
                <Option value="RF17">IVA per cassa</Option>
                <Option value="RF19">Regime forfettario</Option>
                <Option value="RF18">Altro</Option>
              </Select>
            </Form.Item>
          </Col>

          <Col xs={24} md={12}>
            <Form.Item
              label="Valuta predefinita"
              name="valutaPredefinita"
              initialValue="EUR"
            >
              <Select>
                <Option value="EUR">Euro (€)</Option>
                <Option value="USD">Dollaro USA ($)</Option>
                <Option value="GBP">Sterlina Inglese (£)</Option>
                <Option value="CHF">Franco Svizzero (CHF)</Option>
              </Select>
            </Form.Item>
          </Col>

          <Col xs={24} md={12}>
            <Form.Item
              label="Giorni scadenza pagamento"
              name="giorniScadenzaPagamento"
              initialValue={30}
            >
              <InputNumber min={0} max={365} style={{ width: '100%' }} />
            </Form.Item>
          </Col>

          <Col xs={24} md={12}>
            <Form.Item
              label="Ritenuta d'acconto predefinita"
              name="ritenutaAcconto"
              initialValue={0}
            >
              <InputNumber
                min={0}
                max={100}
                step={0.01}
                style={{ width: '100%' }}
                addonAfter="%"
              />
            </Form.Item>
          </Col>

          <Col xs={24} md={12}>
            <Form.Item
              label="Fatturazione Elettronica attiva"
              name="fatturazioneElettronica"
              valuePropName="checked"
              initialValue={true}
            >
              <Switch />
            </Form.Item>
          </Col>

          <Col xs={24} md={12}>
            <Form.Item
              label="Calcolo automatico totali"
              name="calcoloAutomaticoTotali"
              valuePropName="checked"
              initialValue={true}
            >
              <Switch />
            </Form.Item>
          </Col>
        </Row>
      ),
    },
  ];

  return (
    <div>
      <div style={{ marginBottom: '24px' }}>
        <Title level={3}>
          <SettingOutlined style={{ marginRight: '8px' }} />
          Configurazioni Sistema
        </Title>
        <Paragraph type="secondary">
          Imposta le configurazioni base per numerazioni, IVA, pagamenti e altre impostazioni operative.
        </Paragraph>
      </div>

      <Tabs items={configurazioniTabs} />

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
          htmlType="submit" 
          size="large"
          icon={<ArrowRightOutlined />}
        >
          Continua
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

export default ConfigurazioniForm;
