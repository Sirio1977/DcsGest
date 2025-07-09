import React, { useEffect, useState } from 'react';
import { Card, Table, Button, Space, Typography, Row, Col, Tag, Modal, Form, Input, message, Select } from 'antd';
import { SettingOutlined, EditOutlined, DeleteOutlined, PlusOutlined, ReloadOutlined } from '@ant-design/icons';

const { Title } = Typography;
const { Option } = Select;

interface Configurazione {
  id: number;
  chiave: string;
  valore: string;
  descrizione?: string;
  tipo: string;
  updatedAt: string;
}

const ConfigurazioniManagement: React.FC = () => {
  const [configurazioni, setConfigurazioni] = useState<Configurazione[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);
  const [editingConfig, setEditingConfig] = useState<Configurazione | null>(null);
  const [form] = Form.useForm();

  // Carica le configurazioni dal backend
  const loadConfigurazioni = async () => {
    setLoading(true);
    try {
      const response = await fetch('http://localhost:8080/api/configurazioni');
      if (response.ok) {
        const data = await response.json();
        setConfigurazioni(data);
      } else if (response.status === 404) {
        message.warning('Endpoint configurazioni non disponibile. Riavviare il backend.');
        console.log('Backend probabilmente non riavviato con le nuove modifiche');
      } else {
        message.error('Errore nel caricamento delle configurazioni');
      }
    } catch (error) {
      console.error('Errore:', error);
      message.error('Errore di connessione al server. Verificare che il backend sia in esecuzione.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadConfigurazioni();
  }, []);

  // Inizializza configurazioni di default
  const initDefaults = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/configurazioni/init-defaults', {
        method: 'POST'
      });
      
      if (response.ok) {
        message.success('Configurazioni di default inizializzate');
        loadConfigurazioni();
      } else {
        message.error('Errore nell\'inizializzazione');
      }
    } catch (error) {
      console.error('Errore:', error);
      message.error('Errore di connessione');
    }
  };

  // Salva o aggiorna configurazione
  const handleSave = async (values: any) => {
    try {
      const url = editingConfig 
        ? `http://localhost:8080/api/configurazioni/${editingConfig.id}`
        : 'http://localhost:8080/api/configurazioni';
      
      const method = editingConfig ? 'PUT' : 'POST';
      
      const response = await fetch(url, {
        method,
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(values)
      });

      if (response.ok) {
        message.success(editingConfig ? 'Configurazione aggiornata' : 'Configurazione creata');
        setModalVisible(false);
        setEditingConfig(null);
        form.resetFields();
        loadConfigurazioni();
      } else {
        message.error('Errore nel salvataggio');
      }
    } catch (error) {
      console.error('Errore:', error);
      message.error('Errore di connessione');
    }
  };

  // Elimina configurazione
  const handleDelete = async (id: number) => {
    Modal.confirm({
      title: 'Conferma eliminazione',
      content: 'Sei sicuro di voler eliminare questa configurazione?',
      onOk: async () => {
        try {
          const response = await fetch(`http://localhost:8080/api/configurazioni/${id}`, {
            method: 'DELETE'
          });

          if (response.ok) {
            message.success('Configurazione eliminata');
            loadConfigurazioni();
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
  const handleEdit = (config: Configurazione) => {
    setEditingConfig(config);
    form.setFieldsValue(config);
    setModalVisible(true);
  };

  // Apri modal per nuova configurazione
  const handleAdd = () => {
    setEditingConfig(null);
    form.resetFields();
    setModalVisible(true);
  };

  const columns = [
    {
      title: 'Chiave',
      dataIndex: 'chiave',
      key: 'chiave',
      render: (text: string) => <code style={{ backgroundColor: '#f5f5f5', padding: '2px 6px', borderRadius: '3px' }}>{text}</code>
    },
    {
      title: 'Valore',
      dataIndex: 'valore',
      key: 'valore',
      width: 200,
      render: (text: string) => (
        <div style={{ wordBreak: 'break-word', maxWidth: '200px' }}>
          {text && text.length > 50 ? `${text.substring(0, 50)}...` : text}
        </div>
      )
    },
    {
      title: 'Tipo',
      dataIndex: 'tipo',
      key: 'tipo',
      render: (tipo: string) => {
        const color = tipo === 'STRING' ? 'blue' : tipo === 'INTEGER' ? 'green' : 'orange';
        return <Tag color={color}>{tipo}</Tag>;
      }
    },
    {
      title: 'Descrizione',
      dataIndex: 'descrizione',
      key: 'descrizione',
      width: 250,
      render: (text: string) => (
        <div style={{ wordBreak: 'break-word', maxWidth: '250px' }}>
          {text}
        </div>
      )
    },
    {
      title: 'Ultimo aggiornamento',
      dataIndex: 'updatedAt',
      key: 'updatedAt',
      render: (date: string) => new Date(date).toLocaleString('it-IT')
    },
    {
      title: 'Azioni',
      key: 'actions',
      render: (record: Configurazione) => (
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
            <SettingOutlined style={{ marginRight: '12px', color: '#1890ff' }} />
            Configurazioni Sistema
          </Title>
        </Col>
        <Col>
          <Space>
            <Button 
              icon={<ReloadOutlined />}
              onClick={initDefaults}
            >
              Inizializza Default
            </Button>
            <Button 
              type="primary" 
              icon={<PlusOutlined />}
              onClick={handleAdd}
              size="large"
            >
              Nuova Configurazione
            </Button>
          </Space>
        </Col>
      </Row>

      <Card>
        <Table 
          columns={columns}
          dataSource={configurazioni}
          rowKey="id"
          loading={loading}
          pagination={{ pageSize: 15 }}
          scroll={{ x: 'max-content' }}
        />
      </Card>

      <Modal
        title={editingConfig ? 'Modifica Configurazione' : 'Nuova Configurazione'}
        open={modalVisible}
        onCancel={() => {
          setModalVisible(false);
          setEditingConfig(null);
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
            name="chiave"
            label="Chiave"
            rules={[{ required: true, message: 'Inserisci la chiave' }]}
          >
            <Input placeholder="Es. IVA_STANDARD" disabled={!!editingConfig} />
          </Form.Item>

          <Form.Item
            name="valore"
            label="Valore"
            rules={[{ required: true, message: 'Inserisci il valore' }]}
          >
            <Input.TextArea rows={3} placeholder="Valore della configurazione" />
          </Form.Item>

          <Form.Item
            name="tipo"
            label="Tipo"
            initialValue="STRING"
          >
            <Select>
              <Option value="STRING">STRING</Option>
              <Option value="INTEGER">INTEGER</Option>
              <Option value="BOOLEAN">BOOLEAN</Option>
              <Option value="JSON">JSON</Option>
            </Select>
          </Form.Item>

          <Form.Item
            name="descrizione"
            label="Descrizione"
          >
            <Input.TextArea rows={2} placeholder="Descrizione opzionale" />
          </Form.Item>

          <Form.Item style={{ marginBottom: 0, textAlign: 'right' }}>
            <Space>
              <Button onClick={() => {
                setModalVisible(false);
                setEditingConfig(null);
                form.resetFields();
              }}>
                Annulla
              </Button>
              <Button type="primary" htmlType="submit">
                {editingConfig ? 'Aggiorna' : 'Crea'}
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default ConfigurazioniManagement;
