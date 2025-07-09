import React, { useState } from 'react';
import {
  Table,
  Button,
  Space,
  Input,
  Select,
  Tag,
  Typography,
  Row,
  Col,
  Card,
  Modal,
  message,
  Popconfirm,
  Drawer,
} from 'antd';
import {
  PlusOutlined,
  SearchOutlined,
  EditOutlined,
  DeleteOutlined,
  EyeOutlined,
} from '@ant-design/icons';
import { useGetArticoliQuery, useDeleteArticoloMutation } from '../store/api/documentiApi';
import { ArticoloForm } from './forms';
import type { Articolo } from '../store/api/documentiApi';

const { Title } = Typography;
const { Option } = Select;

export const ArticoliManagement: React.FC = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [categoriaFilter, setCategoriaFilter] = useState<string>('');
  const [statusFilter, setStatusFilter] = useState<string>('');
  const [selectedArticolo, setSelectedArticolo] = useState<Articolo | null>(null);
  const [isDetailModalVisible, setIsDetailModalVisible] = useState(false);
  const [isFormVisible, setIsFormVisible] = useState(false);
  const [isEditMode, setIsEditMode] = useState(false);

  const { data: articoli, isLoading, error, refetch } = useGetArticoliQuery();
  const [deleteArticolo] = useDeleteArticoloMutation();

  const handleDelete = async (id: number) => {
    try {
      await deleteArticolo(id).unwrap();
      message.success('Articolo eliminato con successo');
      refetch();
    } catch (error) {
      message.error('Errore durante l\'eliminazione dell\'articolo');
    }
  };

  const handleView = (articolo: Articolo) => {
    setSelectedArticolo(articolo);
    setIsDetailModalVisible(true);
  };

  const handleAddNew = () => {
    setSelectedArticolo(null);
    setIsEditMode(false);
    setIsFormVisible(true);
  };

  const handleEdit = (articolo: Articolo) => {
    setSelectedArticolo(articolo);
    setIsEditMode(true);
    setIsFormVisible(true);
  };

  const handleFormSuccess = () => {
    setIsFormVisible(false);
    refetch();
  };
  
  const handleFormCancel = () => {
    setIsFormVisible(false);
  };

  // Filtri per i dati
  const filteredArticoli = React.useMemo(() => {
    if (!articoli) return [];
    
    return articoli.filter((articolo: Articolo) => {
      const matchesSearch = !searchTerm || 
        articolo.codice.toLowerCase().includes(searchTerm.toLowerCase()) ||
        articolo.descrizione.toLowerCase().includes(searchTerm.toLowerCase());
      
      const matchesCategoria = !categoriaFilter || articolo.categoria === categoriaFilter;
      const matchesStatus = !statusFilter || 
        (statusFilter === 'attivi' && articolo.attivo) ||
        (statusFilter === 'inattivi' && !articolo.attivo);
      
      return matchesSearch && matchesCategoria && matchesStatus;
    });
  }, [articoli, searchTerm, categoriaFilter, statusFilter]);

  // Estrai categorie uniche per il filtro
  const categorie = React.useMemo(() => {
    if (!articoli) return [];
    const categorieSet = new Set(
      articoli
        .map((a: Articolo) => a.categoria)
        .filter((cat): cat is string => Boolean(cat))
    );
    return Array.from(categorieSet);
  }, [articoli]);

  const columns = [
    {
      title: 'Codice',
      dataIndex: 'codice',
      key: 'codice',
      width: 120,
      sorter: (a: Articolo, b: Articolo) => a.codice.localeCompare(b.codice),
    },
    {
      title: 'Descrizione',
      dataIndex: 'descrizione',
      key: 'descrizione',
      ellipsis: true,
      sorter: (a: Articolo, b: Articolo) => a.descrizione.localeCompare(b.descrizione),
    },
    {
      title: 'Categoria',
      dataIndex: 'categoria',
      key: 'categoria',
      width: 150,
      render: (categoria: string) => categoria || '-',
      sorter: (a: Articolo, b: Articolo) => (a.categoria || '').localeCompare(b.categoria || ''),
    },
    {
      title: 'Prezzo',
      dataIndex: 'prezzo',
      key: 'prezzo',
      width: 120,
      render: (prezzo: number) => `€ ${prezzo.toFixed(2)}`,
      sorter: (a: Articolo, b: Articolo) => a.prezzo - b.prezzo,
      align: 'right' as const,
    },
    {
      title: 'U.M.',
      dataIndex: 'unitaMisura',
      key: 'unitaMisura',
      width: 80,
    },
    {
      title: 'IVA %',
      dataIndex: 'aliquotaIva',
      key: 'aliquotaIva',
      width: 80,
      render: (iva: number) => `${iva}%`,
      align: 'center' as const,
    },
    {
      title: 'Stato',
      dataIndex: 'attivo',
      key: 'attivo',
      width: 100,
      render: (attivo: boolean) => (
        <Tag color={attivo ? 'green' : 'red'}>
          {attivo ? 'Attivo' : 'Inattivo'}
        </Tag>
      ),
      filters: [
        { text: 'Attivi', value: true },
        { text: 'Inattivi', value: false },
      ],
      onFilter: (value: any, record: Articolo) => record.attivo === value,
    },
    {
      title: 'Azioni',
      key: 'actions',
      width: 150,
      render: (_: any, record: Articolo) => (
        <Space size="small">
          <Button
            type="link"
            icon={<EyeOutlined />}
            onClick={() => handleView(record)}
            title="Visualizza dettagli"
          />
          <Button
            type="link"
            icon={<EditOutlined />}
            onClick={() => handleEdit(record)}
            title="Modifica"
          />
          <Popconfirm
            title="Sei sicuro di voler eliminare questo articolo?"
            onConfirm={() => handleDelete(record.id)}
            okText="Sì"
            cancelText="No"
          >
            <Button
              type="link"
              danger
              icon={<DeleteOutlined />}
              title="Elimina"
            />
          </Popconfirm>
        </Space>
      ),
    },
  ];

  if (error) {
    return (
      <Card>
        <div style={{ textAlign: 'center', padding: '50px' }}>
          <Title level={4}>Errore nel caricamento degli articoli</Title>
          <p>Verificare che il backend sia attivo e accessibile.</p>
          <Button type="primary" onClick={() => refetch()}>
            Riprova
          </Button>
        </div>
      </Card>
    );
  }

  return (
    <div>
      <Row justify="space-between" align="middle" style={{ marginBottom: 20 }}>
        <Col>
          <Title level={2}>Gestione Articoli</Title>
        </Col>
        <Col>
          <Button
            type="primary"
            icon={<PlusOutlined />}
            onClick={handleAddNew}
          >
            Nuovo Articolo
          </Button>
        </Col>
      </Row>

      <Card>
        <Row gutter={16} style={{ marginBottom: 16 }}>
          <Col xs={24} sm={12} md={8}>
            <Input
              placeholder="Cerca per codice o descrizione..."
              prefix={<SearchOutlined />}
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              allowClear
            />
          </Col>
          <Col xs={24} sm={6} md={4}>
            <Select
              placeholder="Categoria"
              value={categoriaFilter}
              onChange={setCategoriaFilter}
              allowClear
              style={{ width: '100%' }}
            >
              {categorie.map((categoria: string) => (
                <Option key={categoria} value={categoria}>
                  {categoria}
                </Option>
              ))}
            </Select>
          </Col>
          <Col xs={24} sm={6} md={4}>
            <Select
              placeholder="Stato"
              value={statusFilter}
              onChange={setStatusFilter}
              allowClear
              style={{ width: '100%' }}
            >
              <Option value="attivi">Attivi</Option>
              <Option value="inattivi">Inattivi</Option>
            </Select>
          </Col>
          <Col xs={24} sm={24} md={8}>
            <Space>
              <span>Totale: {filteredArticoli.length} articoli</span>
              {articoli && (
                <span>
                  (Attivi: {articoli.filter((a: Articolo) => a.attivo).length})
                </span>
              )}
            </Space>
          </Col>
        </Row>

        <Table
          columns={columns}
          dataSource={filteredArticoli}
          rowKey="id"
          loading={isLoading}
          pagination={{
            pageSize: 20,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total, range) =>
              `${range[0]}-${range[1]} di ${total} articoli`,
          }}
          scroll={{ x: 1200 }}
          size="small"
        />
      </Card>

      {/* Modal dettagli articolo */}
      <Modal
        title="Dettagli Articolo"
        open={isDetailModalVisible}
        onCancel={() => setIsDetailModalVisible(false)}
        footer={[
          <Button key="close" onClick={() => setIsDetailModalVisible(false)}>
            Chiudi
          </Button>,
          <Button
            key="edit"
            type="primary"
            onClick={() => {
              setIsDetailModalVisible(false);
              handleEdit(selectedArticolo as Articolo);
            }}
          >
            Modifica
          </Button>,
        ]}
        width={600}
      >
        {selectedArticolo && (
          <div>
            <Row gutter={16}>
              <Col span={12}>
                <p><strong>Codice:</strong> {selectedArticolo.codice}</p>
                <p><strong>Descrizione:</strong> {selectedArticolo.descrizione}</p>
                <p><strong>Categoria:</strong> {selectedArticolo.categoria || 'Non specificata'}</p>
                <p><strong>Unità di Misura:</strong> {selectedArticolo.unitaMisura}</p>
              </Col>
              <Col span={12}>
                <p><strong>Prezzo:</strong> € {selectedArticolo.prezzo.toFixed(2)}</p>
                <p><strong>Aliquota IVA:</strong> {selectedArticolo.aliquotaIva}%</p>
                <p><strong>Stato:</strong> 
                  <Tag color={selectedArticolo.attivo ? 'green' : 'red'} style={{ marginLeft: 8 }}>
                    {selectedArticolo.attivo ? 'Attivo' : 'Inattivo'}
                  </Tag>
                </p>
                <p><strong>Creato il:</strong> {new Date(selectedArticolo.createdAt).toLocaleDateString('it-IT')}</p>
              </Col>
            </Row>
            {selectedArticolo.descrizioneEstesa && (
              <div style={{ marginTop: 16 }}>
                <p><strong>Descrizione Estesa:</strong></p>
                <p style={{ background: '#f5f5f5', padding: 8, borderRadius: 4 }}>
                  {selectedArticolo.descrizioneEstesa}
                </p>
              </div>
            )}
          </div>
        )}
      </Modal>

      <Drawer
        title={isEditMode ? "Modifica Articolo" : "Nuovo Articolo"}
        placement="right"
        width={720}
        onClose={handleFormCancel}
        open={isFormVisible}
        destroyOnClose={true}
      >
        <ArticoloForm
          articolo={selectedArticolo || undefined}
          onSuccess={handleFormSuccess}
          onCancel={handleFormCancel}
        />
      </Drawer>
    </div>
  );
};
