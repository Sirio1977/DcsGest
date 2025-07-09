import React, { useState, useEffect } from 'react';
import { 
  Form, 
  Input, 
  Button, 
  InputNumber, 
  Select, 
  Switch, 
  Tabs, 
  message, 
  Space, 
  Divider, 
  Card,
  Typography
} from 'antd';
import { SaveOutlined, CloseOutlined } from '@ant-design/icons';
import { useCreateArticoloMutation, useUpdateArticoloMutation, Articolo } from '../../store/api/documentiApi';

const { TabPane } = Tabs;
const { Title } = Typography;
const { TextArea } = Input;
const { Option } = Select;

// Tipi Articolo per dropdown
const tipiArticolo = ['PRODOTTO', 'SERVIZIO', 'MATERIA_PRIMA', 'SEMILAVORATO'] as const;

interface ArticoloFormProps {
  articolo?: Articolo;
  onSuccess?: () => void;
  onCancel?: () => void;
}

// Unità di misura comuni in Italia
const unitaMisuraOptions = [
  { value: 'PZ', label: 'Pezzi' },
  { value: 'KG', label: 'Kilogrammi' },
  { value: 'LT', label: 'Litri' },
  { value: 'MT', label: 'Metri' },
  { value: 'MQ', label: 'Metri Quadri' },
  { value: 'MC', label: 'Metri Cubi' },
  { value: 'ORA', label: 'Ore' },
];

// Aliquote IVA comuni in Italia
const aliquoteIvaOptions = [
  { value: 0, label: '0% (Esente)' },
  { value: 4, label: '4% (Beni di prima necessità)' },
  { value: 5, label: '5% (Aliquota ridotta)' },
  { value: 10, label: '10% (Aliquota ridotta)' },
  { value: 22, label: '22% (Aliquota ordinaria)' },
];

export const ArticoloForm: React.FC<ArticoloFormProps> = ({ 
  articolo, 
  onSuccess, 
  onCancel 
}) => {
  const [form] = Form.useForm();
  const [activeTab, setActiveTab] = useState('1');
  
  const [createArticolo, { isLoading: isCreating }] = useCreateArticoloMutation();
  const [updateArticolo, { isLoading: isUpdating }] = useUpdateArticoloMutation();

  const isLoading = isCreating || isUpdating;
  const isEditMode = !!articolo?.id;

  useEffect(() => {
    if (articolo) {
      form.setFieldsValue({
        ...articolo,
        // Assicuriamoci che i numeri siano trattati correttamente
        prezzo: articolo.prezzo || 0,
        costo: articolo.costo || 0,
        aliquotaIva: articolo.aliquotaIva || 22,
        giacenza: articolo.giacenza || 0,
        giacenzaMinima: articolo.giacenzaMinima || 0,
      });
    }
  }, [articolo, form]);

  // Funzione per formattare il prezzo
  const currencyFormatter = (value: number | undefined): string => {
    return value ? `€ ${value}` : '€ 0';
  };
  
  // Funzione per parsare il valore di input
  const currencyParser = (val: string | undefined): number => {
    if (!val) return 0;
    const value = val.replace(/€\s?/g, '');
    return parseFloat(value) || 0;
  };

  const onFinish = async (values: any) => {
    try {
      if (isEditMode) {
        await updateArticolo({ 
          id: articolo.id, 
          ...values 
        }).unwrap();
        message.success('Articolo aggiornato con successo');
      } else {
        await createArticolo(values).unwrap();
        message.success('Articolo creato con successo');
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
      <Title level={4}>{isEditMode ? 'Modifica Articolo' : 'Nuovo Articolo'}</Title>
      <Form
        form={form}
        layout="vertical"
        onFinish={onFinish}
        initialValues={{
          attivo: true,
          tipo: 'PRODOTTO',
          unitaMisura: 'PZ',
          aliquotaIva: 22,
          prezzo: 0,
          costo: 0,
          giacenza: 0,
          giacenzaMinima: 0,
        }}
      >
        <Tabs activeKey={activeTab} onChange={setActiveTab}>
          {/* Scheda Dati Principali */}
          <TabPane tab="Dati Principali" key="1">
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
              <Form.Item
                name="codice"
                label="Codice Articolo"
                rules={[{ required: true, message: 'Inserisci il codice articolo' }]}
              >
                <Input autoComplete="off" placeholder="Codice univoco dell'articolo" />
              </Form.Item>

              <Form.Item
                name="tipo"
                label="Tipologia"
                rules={[{ required: true, message: 'Seleziona il tipo di articolo' }]}
              >
                <Select>
                  {tipiArticolo.map((tipo) => (
                    <Option key={tipo} value={tipo}>
                      {tipo.replace(/_/g, ' ')}
                    </Option>
                  ))}
                </Select>
              </Form.Item>

              <Form.Item
                name="descrizione"
                label="Descrizione"
                rules={[{ required: true, message: 'Inserisci una descrizione' }]}
                style={{ gridColumn: '1 / span 2' }}
              >
                <Input autoComplete="off" placeholder="Descrizione breve dell'articolo" />
              </Form.Item>

              <Form.Item
                name="descrizioneEstesa"
                label="Descrizione Estesa"
                style={{ gridColumn: '1 / span 2' }}
              >
                <TextArea
                  rows={4}
                  placeholder="Descrizione dettagliata dell'articolo (opzionale)"
                />
              </Form.Item>

              <Form.Item
                name="categoria"
                label="Categoria"
              >
                <Input placeholder="Categoria dell'articolo (opzionale)" />
              </Form.Item>

              <Form.Item
                name="attivo"
                label="Stato Articolo"
                valuePropName="checked"
              >
                <Switch
                  checkedChildren="Attivo"
                  unCheckedChildren="Non attivo"
                />
              </Form.Item>
            </div>
          </TabPane>

          {/* Scheda Prezzi e Imposte */}
          <TabPane tab="Prezzi e Imposte" key="2">
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
              <Form.Item
                name="prezzo"
                label="Prezzo di Vendita (€)"
                rules={[{ required: true, message: 'Inserisci il prezzo' }]}
              >
                <InputNumber
                  min={0}
                  step={0.01}
                  style={{ width: '100%' }}
                  formatter={currencyFormatter}
                  parser={currencyParser}
                />
              </Form.Item>

              <Form.Item
                name="costo"
                label="Costo di Acquisto (€)"
              >
                <InputNumber
                  min={0}
                  step={0.01}
                  style={{ width: '100%' }}
                  formatter={currencyFormatter}
                  parser={currencyParser}
                />
              </Form.Item>

              <Form.Item
                name="aliquotaIva"
                label="Aliquota IVA (%)"
                rules={[{ required: true, message: 'Seleziona l\'aliquota IVA' }]}
              >
                <Select>
                  {aliquoteIvaOptions.map(option => (
                    <Option key={option.value} value={option.value}>
                      {option.label}
                    </Option>
                  ))}
                </Select>
              </Form.Item>

              <Form.Item
                name="unitaMisura"
                label="Unità di Misura"
                rules={[{ required: true, message: 'Seleziona l\'unità di misura' }]}
              >
                <Select>
                  {unitaMisuraOptions.map(option => (
                    <Option key={option.value} value={option.value}>
                      {option.label}
                    </Option>
                  ))}
                </Select>
              </Form.Item>
            </div>
          </TabPane>

          {/* Scheda Gestione Magazzino */}
          <TabPane tab="Gestione Magazzino" key="3">
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
              <Form.Item
                name="giacenza"
                label="Giacenza Attuale"
              >
                <InputNumber
                  min={0}
                  step={0.001}
                  style={{ width: '100%' }}
                />
              </Form.Item>

              <Form.Item
                name="giacenzaMinima"
                label="Giacenza Minima"
                tooltip="Soglia per l'avviso di scorta minima"
              >
                <InputNumber
                  min={0}
                  step={0.001}
                  style={{ width: '100%' }}
                />
              </Form.Item>
            </div>
          </TabPane>

          {/* Scheda Fornitore e Note */}
          <TabPane tab="Fornitore e Note" key="4">
            <div style={{ display: 'grid', gridTemplateColumns: '1fr', gap: '16px' }}>
              <Form.Item
                name="fornitore"
                label="Fornitore Principale"
              >
                <Input placeholder="Nome del fornitore principale" />
              </Form.Item>

              <Form.Item
                name="codiceFornitore"
                label="Codice Articolo del Fornitore"
              >
                <Input placeholder="Codice articolo utilizzato dal fornitore" />
              </Form.Item>

              <Form.Item
                name="note"
                label="Note"
              >
                <TextArea
                  rows={4}
                  placeholder="Note aggiuntive sull'articolo"
                />
              </Form.Item>
            </div>
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

export default ArticoloForm;
