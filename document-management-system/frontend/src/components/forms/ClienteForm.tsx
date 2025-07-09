import React, { useState, useEffect } from 'react';
import {
  Form,
  Input,
  Button,
  Select,
  Switch,
  Tabs,
  message,
  Space,
  Divider,
  Card,
  Typography,
  Row,
  Col
} from 'antd';
import { SaveOutlined, CloseOutlined } from '@ant-design/icons';
import { useCreateClienteMutation, useUpdateClienteMutation, Cliente } from '../../store/api/documentiApi';

const { TabPane } = Tabs;
const { Title } = Typography;
const { TextArea } = Input;
const { Option } = Select;

// Tipo Cliente Enum per dropdown
const tipiClienti = ['CLIENTE', 'FORNITORE', 'CLIENTE_FORNITORE'] as const;

interface ClienteFormProps {
  cliente?: Cliente;
  onSuccess?: () => void;
  onCancel?: () => void;
}

interface ClienteFormProps {
  cliente?: Cliente;
  onSuccess?: () => void;
  onCancel?: () => void;
}

// Province italiane
const provinceItaliane = [
  { value: 'AG', label: 'Agrigento' },
  { value: 'AL', label: 'Alessandria' },
  { value: 'AN', label: 'Ancona' },
  { value: 'AO', label: 'Aosta' },
  { value: 'AR', label: 'Arezzo' },
  { value: 'AP', label: 'Ascoli Piceno' },
  { value: 'AT', label: 'Asti' },
  { value: 'AV', label: 'Avellino' },
  { value: 'BA', label: 'Bari' },
  { value: 'BT', label: 'Barletta-Andria-Trani' },
  { value: 'BL', label: 'Belluno' },
  { value: 'BN', label: 'Benevento' },
  { value: 'BG', label: 'Bergamo' },
  { value: 'BI', label: 'Biella' },
  { value: 'BO', label: 'Bologna' },
  { value: 'BZ', label: 'Bolzano' },
  { value: 'BS', label: 'Brescia' },
  { value: 'BR', label: 'Brindisi' },
  { value: 'CA', label: 'Cagliari' },
  { value: 'CL', label: 'Caltanissetta' },
  { value: 'CB', label: 'Campobasso' },
  { value: 'CE', label: 'Caserta' },
  { value: 'CT', label: 'Catania' },
  { value: 'CZ', label: 'Catanzaro' },
  { value: 'CH', label: 'Chieti' },
  { value: 'CO', label: 'Como' },
  { value: 'CS', label: 'Cosenza' },
  { value: 'CR', label: 'Cremona' },
  { value: 'KR', label: 'Crotone' },
  { value: 'CN', label: 'Cuneo' },
  { value: 'EN', label: 'Enna' },
  { value: 'FM', label: 'Fermo' },
  { value: 'FE', label: 'Ferrara' },
  { value: 'FI', label: 'Firenze' },
  { value: 'FG', label: 'Foggia' },
  { value: 'FC', label: 'Forlì-Cesena' },
  { value: 'FR', label: 'Frosinone' },
  { value: 'GE', label: 'Genova' },
  { value: 'GO', label: 'Gorizia' },
  { value: 'GR', label: 'Grosseto' },
  { value: 'IM', label: 'Imperia' },
  { value: 'IS', label: 'Isernia' },
  { value: 'SP', label: 'La Spezia' },
  { value: 'AQ', label: 'L\'Aquila' },
  { value: 'LT', label: 'Latina' },
  { value: 'LE', label: 'Lecce' },
  { value: 'LC', label: 'Lecco' },
  { value: 'LI', label: 'Livorno' },
  { value: 'LO', label: 'Lodi' },
  { value: 'LU', label: 'Lucca' },
  { value: 'MC', label: 'Macerata' },
  { value: 'MN', label: 'Mantova' },
  { value: 'MS', label: 'Massa-Carrara' },
  { value: 'MT', label: 'Matera' },
  { value: 'ME', label: 'Messina' },
  { value: 'MI', label: 'Milano' },
  { value: 'MO', label: 'Modena' },
  { value: 'MB', label: 'Monza e Brianza' },
  { value: 'NA', label: 'Napoli' },
  { value: 'NO', label: 'Novara' },
  { value: 'NU', label: 'Nuoro' },
  { value: 'OR', label: 'Oristano' },
  { value: 'PD', label: 'Padova' },
  { value: 'PA', label: 'Palermo' },
  { value: 'PR', label: 'Parma' },
  { value: 'PV', label: 'Pavia' },
  { value: 'PG', label: 'Perugia' },
  { value: 'PU', label: 'Pesaro e Urbino' },
  { value: 'PE', label: 'Pescara' },
  { value: 'PC', label: 'Piacenza' },
  { value: 'PI', label: 'Pisa' },
  { value: 'PT', label: 'Pistoia' },
  { value: 'PN', label: 'Pordenone' },
  { value: 'PZ', label: 'Potenza' },
  { value: 'PO', label: 'Prato' },
  { value: 'RG', label: 'Ragusa' },
  { value: 'RA', label: 'Ravenna' },
  { value: 'RC', label: 'Reggio Calabria' },
  { value: 'RE', label: 'Reggio Emilia' },
  { value: 'RI', label: 'Rieti' },
  { value: 'RN', label: 'Rimini' },
  { value: 'RM', label: 'Roma' },
  { value: 'RO', label: 'Rovigo' },
  { value: 'SA', label: 'Salerno' },
  { value: 'SS', label: 'Sassari' },
  { value: 'SV', label: 'Savona' },
  { value: 'SI', label: 'Siena' },
  { value: 'SR', label: 'Siracusa' },
  { value: 'SO', label: 'Sondrio' },
  { value: 'SU', label: 'Sud Sardegna' },
  { value: 'TA', label: 'Taranto' },
  { value: 'TE', label: 'Teramo' },
  { value: 'TR', label: 'Terni' },
  { value: 'TO', label: 'Torino' },
  { value: 'TP', label: 'Trapani' },
  { value: 'TN', label: 'Trento' },
  { value: 'TV', label: 'Treviso' },
  { value: 'TS', label: 'Trieste' },
  { value: 'UD', label: 'Udine' },
  { value: 'VA', label: 'Varese' },
  { value: 'VE', label: 'Venezia' },
  { value: 'VB', label: 'Verbano-Cusio-Ossola' },
  { value: 'VC', label: 'Vercelli' },
  { value: 'VR', label: 'Verona' },
  { value: 'VV', label: 'Vibo Valentia' },
  { value: 'VI', label: 'Vicenza' },
  { value: 'VT', label: 'Viterbo' }
];

export const ClienteForm: React.FC<ClienteFormProps> = ({
  cliente,
  onSuccess,
  onCancel
}) => {
  const [form] = Form.useForm();
  const [activeTab, setActiveTab] = useState('1');
  
  const [createCliente, { isLoading: isCreating }] = useCreateClienteMutation();
  const [updateCliente, { isLoading: isUpdating }] = useUpdateClienteMutation();

  const isLoading = isCreating || isUpdating;
  const isEditMode = !!cliente?.id;

  useEffect(() => {
    if (cliente) {
      form.setFieldsValue({
        ...cliente
      });
    }
  }, [cliente, form]);

  // Validazione personalizzata per P.IVA italiana
  const validatePartitaIVA = (_: any, value: string) => {
    if (!value) {
      return Promise.resolve();
    }

    // Rimuovi eventuali spazi e controlla la lunghezza
    const cleanValue = value.replace(/\s/g, '');
    if (cleanValue.length !== 11) {
      return Promise.reject('La partita IVA deve essere di 11 cifre');
    }

    // Controlla che siano solo numeri
    if (!/^\d+$/.test(cleanValue)) {
      return Promise.reject('La partita IVA deve contenere solo cifre');
    }

    return Promise.resolve();
  };

  // Validazione personalizzata per Codice Fiscale italiano
  const validateCF = (_: any, value: string) => {
    if (!value) {
      return Promise.resolve();
    }

    // Rimuovi eventuali spazi e converti in maiuscolo
    const cleanValue = value.replace(/\s/g, '').toUpperCase();
    
    // Controlla lunghezza
    if (cleanValue.length !== 16) {
      return Promise.reject('Il codice fiscale deve essere di 16 caratteri');
    }

    // Formato regex semplificato
    const cfRegex = /^[A-Z]{6}\d{2}[A-Z]\d{2}[A-Z]\d{3}[A-Z]$/;
    if (!cfRegex.test(cleanValue)) {
      return Promise.reject('Formato codice fiscale non valido');
    }

    return Promise.resolve();
  };

  const onFinish = async (values: any) => {
    try {
      if (isEditMode) {
        await updateCliente({
          id: cliente.id,
          ...values
        }).unwrap();
        message.success('Cliente aggiornato con successo');
      } else {
        await createCliente(values).unwrap();
        message.success('Cliente creato con successo');
        form.resetFields();
      }
      
      if (onSuccess) {
        onSuccess();
      }
    } catch (error) {
      console.error('Errore durante il salvataggio:', error);
      message.error('Si è verificato un errore durante il salvataggio');
    }
  };

  return (
    <Card>
      <Title level={4}>{isEditMode ? 'Modifica Cliente' : 'Nuovo Cliente'}</Title>
      <Form
        form={form}
        layout="vertical"
        onFinish={onFinish}
        initialValues={{
          attivo: true,
          tipo: 'CLIENTE',
        }}
      >
        <Tabs activeKey={activeTab} onChange={setActiveTab}>
          {/* Scheda Anagrafica Base */}
          <TabPane tab="Anagrafica Base" key="1">
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
              <Form.Item
                name="ragioneSociale"
                label="Ragione Sociale / Nome e Cognome"
                rules={[{ required: true, message: 'Campo obbligatorio' }]}
                style={{ gridColumn: '1 / span 2' }}
              >
                <Input autoComplete="off" placeholder="Ragione sociale completa" />
              </Form.Item>

              <Form.Item
                name="partitaIva"
                label="Partita IVA"
                rules={[
                  { validator: validatePartitaIVA }
                ]}
              >
                <Input autoComplete="off" placeholder="Partita IVA (11 cifre)" maxLength={11} />
              </Form.Item>

              <Form.Item
                name="codiceFiscale"
                label="Codice Fiscale"
                rules={[
                  { validator: validateCF }
                ]}
              >
                <Input autoComplete="off" placeholder="Codice Fiscale (16 caratteri)" maxLength={16} />
              </Form.Item>

              <Form.Item
                name="tipo"
                label="Tipologia"
                rules={[{ required: true, message: 'Seleziona il tipo di cliente' }]}
              >
                <Select>
                  {tipiClienti.map((tipo) => (
                    <Option key={tipo} value={tipo}>
                      {tipo.replace(/_/g, ' ')}
                    </Option>
                  ))}
                </Select>
              </Form.Item>

              <Form.Item
                name="attivo"
                label="Stato"
                valuePropName="checked"
              >
                <Switch
                  checkedChildren="Attivo"
                  unCheckedChildren="Non attivo"
                />
              </Form.Item>
            </div>
          </TabPane>

          {/* Scheda Indirizzo e Contatti */}
          <TabPane tab="Indirizzo e Contatti" key="2">
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
              <Form.Item
                name="indirizzo"
                label="Indirizzo"
                style={{ gridColumn: '1 / span 2' }}
              >
                <Input placeholder="Via/Piazza, numero civico" />
              </Form.Item>

              <Form.Item
                name="citta"
                label="Città"
              >
                <Input placeholder="Città" />
              </Form.Item>

              <Form.Item
                name="cap"
                label="CAP"
              >
                <Input placeholder="Codice Postale" maxLength={5} />
              </Form.Item>

              <Form.Item
                name="provincia"
                label="Provincia"
              >
                <Select
                  showSearch
                  placeholder="Seleziona provincia"
                  optionFilterProp="children"
                  filterOption={(input, option) =>
                    (option?.label ?? '').toLowerCase().includes(input.toLowerCase())
                  }
                  options={provinceItaliane}
                />
              </Form.Item>

              <Form.Item
                name="telefono"
                label="Telefono"
              >
                <Input placeholder="Numero di telefono" />
              </Form.Item>
            </div>
          </TabPane>

          {/* Scheda PEC e Email */}
          <TabPane tab="Email e PEC" key="3">
            <div style={{ display: 'grid', gridTemplateColumns: '1fr', gap: '16px' }}>
              <Form.Item
                name="email"
                label="Email"
                rules={[
                  { type: 'email', message: 'Formato email non valido' }
                ]}
              >
                <Input placeholder="Indirizzo email" />
              </Form.Item>

              <Form.Item
                name="pec"
                label="PEC (Posta Elettronica Certificata)"
                rules={[
                  { type: 'email', message: 'Formato PEC non valido' }
                ]}
                tooltip="Obbligatorio per la fatturazione elettronica"
              >
                <Input placeholder="Indirizzo PEC" />
              </Form.Item>
            </div>
          </TabPane>

          {/* Scheda Note e Informazioni Aggiuntive */}
          <TabPane tab="Note e Info Aggiuntive" key="4">
            <Form.Item
              name="note"
              label="Note"
            >
              <TextArea
                rows={6}
                placeholder="Note aggiuntive sul cliente"
              />
            </Form.Item>

            {isEditMode && cliente?.createdAt && (
              <Row gutter={16}>
                <Col span={12}>
                  <p>
                    <strong>Data creazione:</strong> {new Date(cliente.createdAt).toLocaleDateString('it-IT')}
                  </p>
                </Col>
                {cliente?.updatedAt && (
                  <Col span={12}>
                    <p>
                      <strong>Ultima modifica:</strong> {new Date(cliente.updatedAt).toLocaleDateString('it-IT')}
                    </p>
                  </Col>
                )}
              </Row>
            )}
          </TabPane>
        </Tabs>

        <Divider />

        <Form.Item>
          <Space>
            <Button type="primary" htmlType="submit" loading={isLoading} icon={<SaveOutlined />}>
              {isEditMode ? 'Aggiorna' : 'Salva'}
            </Button>
            <Button onClick={onCancel} icon={<CloseOutlined />}>
              Annulla
            </Button>
          </Space>
        </Form.Item>
      </Form>
    </Card>
  );
};

export default ClienteForm;
