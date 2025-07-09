import React from 'react';
import { Form, Input, Button, Row, Col, Upload, message, Select, Typography } from 'antd';
import { InboxOutlined, SaveOutlined, ArrowRightOutlined } from '@ant-design/icons';

const { Title, Paragraph } = Typography;
const { TextArea } = Input;
const { Option } = Select;
const { Dragger } = Upload;

interface Props {
  onNext: (data: any) => void;
  initialData?: any;
}

const AziendaSetupForm: React.FC<Props> = ({ onNext, initialData }) => {
  const [form] = Form.useForm();

  const handleSubmit = (values: any) => {
    console.log('Dati azienda:', values);
    onNext(values);
  };

  const uploadProps = {
    name: 'logo',
    multiple: false,
    accept: 'image/*',
    beforeUpload: (file: File) => {
      const isImage = file.type.startsWith('image/');
      if (!isImage) {
        message.error('Puoi caricare solo file immagine!');
      }
      const isLt2M = file.size / 1024 / 1024 < 2;
      if (!isLt2M) {
        message.error('Il logo deve essere più piccolo di 2MB!');
      }
      return false; // Prevent upload, just store file
    },
    onChange: (info: any) => {
      const { status } = info.file;
      if (status === 'done') {
        message.success(`${info.file.name} caricato con successo.`);
      } else if (status === 'error') {
        message.error(`${info.file.name} caricamento fallito.`);
      }
    },
  };

  return (
    <div>
      <div style={{ marginBottom: '24px' }}>
        <Title level={3}>Dati Azienda Emittente</Title>
        <Paragraph type="secondary">
          Inserisci i dati fiscali e anagrafici della tua azienda. Questi dati 
          appariranno su tutti i documenti emessi.
        </Paragraph>
      </div>

      <Form
        form={form}
        layout="vertical"
        onFinish={handleSubmit}
        initialValues={initialData}
        style={{ maxWidth: '800px' }}
      >
        <Row gutter={[16, 16]}>
          {/* Dati Anagrafici */}
          <Col xs={24}>
            <Title level={4}>Dati Anagrafici</Title>
          </Col>
          
          <Col xs={24} md={12}>
            <Form.Item
              label="Ragione Sociale"
              name="ragioneSociale"
              rules={[{ required: true, message: 'Inserisci la ragione sociale' }]}
            >
              <Input placeholder="Es. Mario Rossi S.r.l." />
            </Form.Item>
          </Col>

          <Col xs={24} md={12}>
            <Form.Item
              label="Nome Commerciale"
              name="nomeCommerciale"
            >
              <Input placeholder="Es. Rossi Store" />
            </Form.Item>
          </Col>

          {/* Dati Fiscali */}
          <Col xs={24}>
            <Title level={4}>Dati Fiscali</Title>
          </Col>

          <Col xs={24} md={8}>
            <Form.Item
              label="Partita IVA"
              name="partitaIva"
              rules={[
                { required: true, message: 'Inserisci la Partita IVA' },
                { pattern: /^[0-9]{11}$/, message: 'Partita IVA deve essere di 11 cifre' }
              ]}
            >
              <Input placeholder="12345678901" maxLength={11} />
            </Form.Item>
          </Col>

          <Col xs={24} md={8}>
            <Form.Item
              label="Codice Fiscale"
              name="codiceFiscale"
              rules={[
                { required: true, message: 'Inserisci il Codice Fiscale' },
                { min: 11, max: 16, message: 'Codice Fiscale non valido' }
              ]}
            >
              <Input placeholder="RSSMRA80A01H501Z" style={{ textTransform: 'uppercase' }} />
            </Form.Item>
          </Col>

          <Col xs={24} md={8}>
            <Form.Item
              label="Codice SDI"
              name="codiceSdi"
              rules={[
                { required: true, message: 'Inserisci il Codice SDI' },
                { len: 7, message: 'Il Codice SDI deve essere di 7 caratteri' }
              ]}
            >
              <Input placeholder="XXXXXXX" style={{ textTransform: 'uppercase' }} />
            </Form.Item>
          </Col>

          {/* Indirizzo */}
          <Col xs={24}>
            <Title level={4}>Sede Legale</Title>
          </Col>

          <Col xs={24} md={16}>
            <Form.Item
              label="Indirizzo"
              name="indirizzo"
              rules={[{ required: true, message: 'Inserisci l\'indirizzo' }]}
            >
              <Input placeholder="Via Roma, 123" />
            </Form.Item>
          </Col>

          <Col xs={24} md={8}>
            <Form.Item
              label="CAP"
              name="cap"
              rules={[
                { required: true, message: 'Inserisci il CAP' },
                { pattern: /^[0-9]{5}$/, message: 'CAP deve essere di 5 cifre' }
              ]}
            >
              <Input placeholder="00100" maxLength={5} />
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

          <Col xs={24} md={6}>
            <Form.Item
              label="Provincia"
              name="provincia"
              rules={[
                { required: true, message: 'Inserisci la provincia' },
                { len: 2, message: 'Provincia deve essere di 2 caratteri' }
              ]}
            >
              <Input placeholder="RM" style={{ textTransform: 'uppercase' }} maxLength={2} />
            </Form.Item>
          </Col>

          <Col xs={24} md={6}>
            <Form.Item
              label="Nazione"
              name="nazione"
              rules={[{ required: true, message: 'Seleziona la nazione' }]}
              initialValue="IT"
            >
              <Select>
                <Option value="IT">Italia</Option>
                <Option value="SM">San Marino</Option>
                <Option value="VA">Vaticano</Option>
              </Select>
            </Form.Item>
          </Col>

          {/* Contatti */}
          <Col xs={24}>
            <Title level={4}>Contatti</Title>
          </Col>

          <Col xs={24} md={8}>
            <Form.Item
              label="Telefono"
              name="telefono"
            >
              <Input placeholder="+39 06 12345678" />
            </Form.Item>
          </Col>

          <Col xs={24} md={8}>
            <Form.Item
              label="Email"
              name="email"
              rules={[
                { required: true, message: 'Inserisci l\'email' },
                { type: 'email', message: 'Email non valida' }
              ]}
            >
              <Input placeholder="info@azienda.it" />
            </Form.Item>
          </Col>

          <Col xs={24} md={8}>
            <Form.Item
              label="PEC"
              name="pec"
              rules={[
                { type: 'email', message: 'PEC non valida' }
              ]}
            >
              <Input placeholder="azienda@pec.it" />
            </Form.Item>
          </Col>

          <Col xs={24} md={12}>
            <Form.Item
              label="Sito Web"
              name="sitoWeb"
            >
              <Input placeholder="https://www.azienda.it" />
            </Form.Item>
          </Col>

          {/* Logo */}
          <Col xs={24}>
            <Title level={4}>Logo Aziendale</Title>
          </Col>

          <Col xs={24}>
            <Form.Item
              label="Logo"
              name="logo"
            >
              <Dragger {...uploadProps} style={{ height: '120px' }}>
                <p className="ant-upload-drag-icon">
                  <InboxOutlined />
                </p>
                <p className="ant-upload-text">
                  Clicca o trascina il logo aziendale qui
                </p>
                <p className="ant-upload-hint">
                  Formati supportati: JPG, PNG. Dimensione massima: 2MB
                </p>
              </Dragger>
            </Form.Item>
          </Col>

          {/* Note */}
          <Col xs={24}>
            <Form.Item
              label="Note aggiuntive"
              name="note"
            >
              <TextArea 
                rows={3} 
                placeholder="Note interne sull'azienda..."
              />
            </Form.Item>
          </Col>
        </Row>

        <div style={{ marginTop: '32px', textAlign: 'right' }}>
          <Button 
            type="primary" 
            htmlType="submit" 
            size="large"
            icon={<ArrowRightOutlined />}
          >
            Continua
          </Button>
        </div>
      </Form>
    </div>
  );
};

export default AziendaSetupForm;
