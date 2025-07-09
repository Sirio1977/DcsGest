import React, { useEffect } from 'react';
import {
  Modal,
  Form,
  Input,
  InputNumber,
  Select,
  Switch,
  Space,
  Button,
  message,
  Descriptions,
  Typography,
} from 'antd';
import {
  useCreateArticoloFornitoreMutation,
  useUpdateArticoloFornitoreMutation,
  type ArticoloFornitore,
} from '../../store/api/documentiApi';

const { Option } = Select;
const { TextArea } = Input;
const { Text } = Typography;

interface ArticoloFornitoreFormProps {
  visible: boolean;
  onClose: () => void;
  onSuccess: () => void;
  articoloFornitore?: ArticoloFornitore | null;
  isEditMode?: boolean;
}

export const ArticoloFornitoreForm: React.FC<ArticoloFornitoreFormProps> = ({
  visible,
  onClose,
  onSuccess,
  articoloFornitore,
  isEditMode = false,
}) => {
  const [form] = Form.useForm();
  const [createArticoloFornitore, { isLoading: isCreating }] = useCreateArticoloFornitoreMutation();
  const [updateArticoloFornitore, { isLoading: isUpdating }] = useUpdateArticoloFornitoreMutation();

  const isLoading = isCreating || isUpdating;

  useEffect(() => {
    if (visible && articoloFornitore && isEditMode) {
      form.setFieldsValue(articoloFornitore);
    } else if (visible && !isEditMode) {
      form.resetFields();
    }
  }, [visible, articoloFornitore, isEditMode, form]);

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      
      if (isEditMode && articoloFornitore?.id) {
        await updateArticoloFornitore({ 
          id: articoloFornitore.id, 
          ...values 
        }).unwrap();
        message.success('Articolo fornitore aggiornato con successo');
      } else {
        await createArticoloFornitore(values).unwrap();
        message.success('Articolo fornitore creato con successo');
      }
      
      onSuccess();
    } catch (error) {
      console.error('Errore durante il salvataggio:', error);
      message.error('Errore durante il salvataggio dell\'articolo fornitore');
    }
  };

  const handleCancel = () => {
    form.resetFields();
    onClose();
  };

  return (
    <Modal
      title={isEditMode ? 'Modifica Articolo Fornitore' : 'Nuovo Articolo Fornitore'}
      open={visible}
      onCancel={handleCancel}
      width={800}
      footer={[
        <Button key="cancel" onClick={handleCancel}>
          Annulla
        </Button>,
        <Button key="submit" type="primary" loading={isLoading} onClick={handleSubmit}>
          {isEditMode ? 'Aggiorna' : 'Crea'}
        </Button>,
      ]}
    >
      <Form
        form={form}
        layout="vertical"
        initialValues={{
          aliquotaIVA: 22,
        }}
      >
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
          <Form.Item
            name="codice"
            label="Codice Articolo"
            rules={[
              { required: true, message: 'Il codice è obbligatorio' },
              { max: 50, message: 'Il codice non può superare i 50 caratteri' }
            ]}
          >
            <Input placeholder="Inserisci codice articolo" />
          </Form.Item>

          <Form.Item
            name="codiceInterno"
            label="Codice Interno"
            rules={[
              { max: 50, message: 'Il codice interno non può superare i 50 caratteri' }
            ]}
          >
            <Input placeholder="Codice interno (opzionale)" />
          </Form.Item>
        </div>

        <Form.Item
          name="descrizione"
          label="Descrizione"
          rules={[
            { required: true, message: 'La descrizione è obbligatoria' },
            { max: 255, message: 'La descrizione non può superare i 255 caratteri' }
          ]}
        >
          <Input placeholder="Inserisci descrizione articolo" />
        </Form.Item>

        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '16px' }}>
          <Form.Item
            name="prezzoUnitario"
            label="Prezzo Unitario (€)"
            rules={[
              { required: true, message: 'Il prezzo è obbligatorio' },
              { type: 'number', min: 0, message: 'Il prezzo deve essere positivo' }
            ]}
          >
            <InputNumber
              style={{ width: '100%' }}
              placeholder="0.00"
              precision={2}
              min={0}
              step={0.01}
            />
          </Form.Item>

          <Form.Item
            name="unitaMisura"
            label="Unità di Misura"
            rules={[
              { required: true, message: 'L\'unità di misura è obbligatoria' }
            ]}
          >
            <Select placeholder="Seleziona unità">
              <Option value="pz">Pezzi (pz)</Option>
              <Option value="kg">Chilogrammi (kg)</Option>
              <Option value="g">Grammi (g)</Option>
              <Option value="l">Litri (l)</Option>
              <Option value="ml">Millilitri (ml)</Option>
              <Option value="m">Metri (m)</Option>
              <Option value="cm">Centimetri (cm)</Option>
              <Option value="m2">Metri quadri (m²)</Option>
              <Option value="m3">Metri cubi (m³)</Option>
              <Option value="ore">Ore</Option>
              <Option value="giorni">Giorni</Option>
              <Option value="conf">Confezioni</Option>
              <Option value="kit">Kit</Option>
            </Select>
          </Form.Item>

          <Form.Item
            name="aliquotaIVA"
            label="Aliquota IVA (%)"
            rules={[
              { required: true, message: 'L\'aliquota IVA è obbligatoria' },
              { type: 'number', min: 0, max: 100, message: 'L\'aliquota deve essere tra 0 e 100' }
            ]}
          >
            <Select placeholder="Seleziona aliquota">
              <Option value={0}>0% - Esente</Option>
              <Option value={4}>4% - Ridotta</Option>
              <Option value={5}>5% - Ridotta</Option>
              <Option value={10}>10% - Ridotta</Option>
              <Option value={22}>22% - Ordinaria</Option>
            </Select>
          </Form.Item>
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '16px' }}>
          <Form.Item
            name="fornitoreRagioneSociale"
            label="Ragione Sociale Fornitore"
            rules={[
              { max: 255, message: 'La ragione sociale non può superare i 255 caratteri' }
            ]}
          >
            <Input placeholder="Ragione sociale fornitore" />
          </Form.Item>

          <Form.Item
            name="fornitorePartitaIva"
            label="P.IVA Fornitore"
            rules={[
              { pattern: /^[0-9]{11}$/, message: 'P.IVA deve avere 11 cifre' }
            ]}
          >
            <Input placeholder="12345678901" />
          </Form.Item>

          <Form.Item
            name="fornitoreCategoria"
            label="Categoria Fornitore"
            rules={[
              { max: 100, message: 'La categoria non può superare i 100 caratteri' }
            ]}
          >
            <Input placeholder="Categoria merceologica" />
          </Form.Item>
        </div>

        {isEditMode && articoloFornitore?.createdAt && (
          <Descriptions size="small" column={2} style={{ marginTop: 16 }}>
            <Descriptions.Item label="Creato il">
              <Text type="secondary">
                {new Date(articoloFornitore.createdAt).toLocaleString('it-IT')}
              </Text>
            </Descriptions.Item>
            {articoloFornitore.updatedAt && (
              <Descriptions.Item label="Ultima modifica">
                <Text type="secondary">
                  {new Date(articoloFornitore.updatedAt).toLocaleString('it-IT')}
                </Text>
              </Descriptions.Item>
            )}
          </Descriptions>
        )}
      </Form>
    </Modal>
  );
};
