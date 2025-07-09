import React, { useEffect, useState } from 'react';
import { Card, Table, Button, Space, Typography, Row, Col, Tag, Modal, Form, Input, message } from 'antd';
import { EditOutlined, DeleteOutlined, PlusOutlined, BankOutlined } from '@ant-design/icons';

const { Title } = Typography;

interface Azienda {
  id: number;
  ragioneSociale: string;
  partitaIva: string;
  email: string;
  telefono?: string;
  indirizzo?: string;
  createdAt: string;
}

const AziendaManagement: React.FC = () => {
  const [aziende, setAziende] = useState<Azienda[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);
  const [editingAzienda, setEditingAzienda] = useState<Azienda | null>(null);
  const [form] = Form.useForm();

  // Carica le aziende dal backend
  const loadAziende = async () => {
    setLoading(true);
    try {
      const response = await fetch('http://localhost:8080/api/aziende');
      if (response.ok) {
        const data = await response.json();
        setAziende(data);
      } else {
        message.error('Errore nel caricamento delle aziende');
      }
    } catch (error) {
      console.error('Errore:', error);
      message.error('Errore di connessione al server');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadAziende();
  }, []);

  // Salva o aggiorna azienda
  const handleSave = async (values: any) => {
    try {
      const url = editingAzienda 
        ? `http://localhost:8080/api/aziende/${editingAzienda.id}`
        : 'http://localhost:8080/api/aziende';
      
      const method = editingAzienda ? 'PUT' : 'POST';
      
      const response = await fetch(url, {
        method,
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(values)
      });

      if (response.ok) {
        message.success(editingAzienda ? 'Azienda aggiornata' : 'Azienda creata');
        setModalVisible(false);
        setEditingAzienda(null);
        form.resetFields();
        loadAziende();
      } else {
        message.error('Errore nel salvataggio');
      }
    } catch (error) {
      console.error('Errore:', error);
      message.error('Errore di connessione');
    }
  };

  // Elimina azienda
  const handleDelete = async (id: number) => {
    Modal.confirm({
      title: 'Conferma eliminazione',
      content: 'Sei sicuro di voler eliminare questa azienda?',
      onOk: async () => {
        try {
          const response = await fetch(`http://localhost:8080/api/aziende/${id}`, {
            method: 'DELETE'
          });

          if (response.ok) {
            message.success('Azienda eliminata');
            loadAziende();
          } else {
            message.error('Errore nell\'eliminazione');
          }
        } catch (error) {
          console.error('Errore:', error);
          message.error('Errore di connessione');
        }
      }
    });
  };

  // Apri modal per modifica
  const handleEdit = (azienda: Azienda) => {
    setEditingAzienda(azienda);
    form.setFieldsValue(azienda);
    setModalVisible(true);
  };

  // Apri modal per nuova azienda
  const handleAdd = () => {
    setEditingAzienda(null);
    form.resetFields();
    setModalVisible(true);
  };

  const columns = [
    {
      title: 'Ragione Sociale',
      dataIndex: 'ragioneSociale',
      key: 'ragioneSociale',
      render: (text: string) => <strong>{text}</strong>
    },
    {
      title: 'P.IVA',
      dataIndex: 'partitaIva',
      key: 'partitaIva',
      render: (text: string) => <Tag color="blue">{text}</Tag>
    },
    {
      title: 'Email',
      dataIndex: 'email',
      key: 'email'
    },
    {
      title: 'Telefono',
      dataIndex: 'telefono',
      key: 'telefono'
    },
    {
      title: 'Creata il',
      dataIndex: 'createdAt',
      key: 'createdAt',
      render: (date: string) => new Date(date).toLocaleDateString('it-IT')
    },
    {
      title: 'Azioni',
      key: 'actions',
      render: (record: Azienda) => (
        <Space>
          <Button 
            icon={<EditOutlined />} 
            onClick={() => handleEdit(record)}
            size="small"
            type="primary"
          >
            Modifica
          </Button>
          <Button 
            icon={<DeleteOutlined />} 
            onClick={() => handleDelete(record.id)}
            size="small"
            danger
          >
            Elimina
          </Button>
        </Space>
      )
    }
  ];

  return (
    <div style={{ padding: '24px' }}>
      <Row justify="space-between" align="middle" style={{ marginBottom: '24px' }}>
        <Col>
          <Title level={2}>
            <BankOutlined style={{ marginRight: '12px', color: '#1890ff' }} />
            Gestione Aziende
          </Title>
        </Col>
        <Col>
          <Button 
            type="primary" 
            icon={<PlusOutlined />}
            onClick={handleAdd}
            size="large"
          >
            Nuova Azienda
          </Button>
        </Col>
      </Row>

      <Card>
        <Table 
          columns={columns}
          dataSource={aziende}
          rowKey="id"
          loading={loading}
          pagination={{ pageSize: 10 }}
        />
      </Card>

      <Modal
        title={editingAzienda ? 'Modifica Azienda' : 'Nuova Azienda'}
        open={modalVisible}
        onCancel={() => {
          setModalVisible(false);
          setEditingAzienda(null);
          form.resetFields();
        }}
        footer={null}
        width={600}
      >
        <Form
          form={form}
          layout="vertical"
          onFinish={handleSave}
        >
          <Form.Item
            name="ragioneSociale"
            label="Ragione Sociale"
            rules={[{ required: true, message: 'Inserisci la ragione sociale' }]}
          >
            <Input placeholder="Es. Mario Rossi S.r.l." />
          </Form.Item>

          <Form.Item
            name="partitaIva"
            label="Partita IVA"
            rules={[
              { required: true, message: 'Inserisci la partita IVA' },
              { len: 11, message: 'La partita IVA deve essere di 11 cifre' }
            ]}
          >
            <Input placeholder="12345678901" maxLength={11} />
          </Form.Item>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="email"
                label="Email"
                rules={[
                  { required: true, message: 'Inserisci l\'email' },
                  { type: 'email', message: 'Email non valida' }
                ]}
              >
                <Input placeholder="info@azienda.it" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="telefono"
                label="Telefono"
              >
                <Input placeholder="123-456-7890" />
              </Form.Item>
            </Col>
          </Row>

          <Form.Item
            name="indirizzo"
            label="Indirizzo"
          >
            <Input.TextArea placeholder="Via Roma, 123 - 00100 Roma (RM)" rows={3} />
          </Form.Item>

          <Form.Item style={{ marginBottom: 0, textAlign: 'right' }}>
            <Space>
              <Button onClick={() => {
                setModalVisible(false);
                setEditingAzienda(null);
                form.resetFields();
              }}>
                Annulla
              </Button>
              <Button type="primary" htmlType="submit">
                {editingAzienda ? 'Aggiorna' : 'Crea'}
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default AziendaManagement;
