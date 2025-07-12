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
import { useGetArticoliQuery, useDeleteArticoloMutation } from '../store/api/articoliApi';
import { ArticoloForm } from './forms';
import type { Articolo } from '../types/entities';

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

  const { data: articoliResponse, isLoading, error, refetch } = useGetArticoliQuery({});
  const [deleteArticolo] = useDeleteArticoloMutation();

  // Estrae gli articoli dalla risposta paginata
  const articoli = articoliResponse?.content || [];

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
    console.log("🔥 DEBUG ArticoliManagement: handleView called with:", articolo);
    console.log("🔥 DEBUG ArticoliManagement: isDetailModalVisible before:", isDetailModalVisible);
    
    // Debug DOM prima del cambiamento
    setTimeout(() => {
      const modals = document.querySelectorAll('.ant-modal-wrap');
      console.log(`🔍 Modals nel DOM prima: ${modals.length}`);
    }, 100);
    
    setSelectedArticolo(articolo);
    setIsDetailModalVisible(true);
    
    // Debug DOM dopo il cambiamento
    setTimeout(() => {
      const modals = document.querySelectorAll('.ant-modal-wrap');
      console.log(`🔍 Modals nel DOM dopo: ${modals.length}`);
      modals.forEach((modal, index) => {
        const style = window.getComputedStyle(modal);
        console.log(`🔍 Modal ${index}:`, {
          display: style.display,
          visibility: style.visibility,
          opacity: style.opacity,
          zIndex: style.zIndex
        });
      });
    }, 500);
    
    console.log("🔥 DEBUG ArticoliManagement: isDetailModalVisible after:", true);
  };

  const handleAddNew = () => {
    console.log("🔥 DEBUG ArticoliManagement: handleAddNew called");
    console.log("🔥 DEBUG ArticoliManagement: isFormVisible before:", isFormVisible);
    setSelectedArticolo(null);
    setIsEditMode(false);
    setIsFormVisible(true);
    console.log("🔥 DEBUG ArticoliManagement: isFormVisible after:", true);
  };

  const handleEdit = (articolo: Articolo) => {
    console.log("🔥 DEBUG ArticoliManagement: handleEdit called with:", articolo);
    console.log("🔥 DEBUG ArticoliManagement: isFormVisible before:", isFormVisible);
    setSelectedArticolo(articolo);
    setIsEditMode(true);
    setIsFormVisible(true);
    console.log("🔥 DEBUG ArticoliManagement: isFormVisible after (edit):", true);
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
      
      const matchesCategoria = !categoriaFilter || articolo.tipo === categoriaFilter;
      const matchesStatus = !statusFilter || 
        (statusFilter === 'attivi' && articolo.attivo) ||
        (statusFilter === 'inattivi' && !articolo.attivo);
      
      return matchesSearch && matchesCategoria && matchesStatus;
    });
  }, [articoli, searchTerm, categoriaFilter, statusFilter]);

  // Estrai tipi unici per il filtro
  const tipi = React.useMemo(() => {
    if (!articoli) return [];
    const tipiSet = new Set(
      articoli
        .map((a: Articolo) => a.tipo)
        .filter((tipo) => Boolean(tipo))
    );
    return Array.from(tipiSet);
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
      title: 'Tipo',
      dataIndex: 'tipo',
      key: 'tipo',
      width: 150,
      render: (tipo: string) => tipo || '-',
      sorter: (a: Articolo, b: Articolo) => (a.tipo || '').localeCompare(b.tipo || ''),
    },
    {
      title: 'Prezzo Vendita',
      dataIndex: 'prezzoVendita',
      key: 'prezzoVendita',
      width: 120,
      render: (prezzo: number) => prezzo != null ? `€ ${prezzo.toFixed(2)}` : '€ 0.00',
      sorter: (a: Articolo, b: Articolo) => a.prezzoVendita - b.prezzoVendita,
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
            onConfirm={() => record.id && handleDelete(record.id)}
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
              placeholder="Tipo"
              value={categoriaFilter}
              onChange={setCategoriaFilter}
              allowClear
              style={{ width: '100%' }}
            >
              {tipi.map((tipo: string) => (
                <Option key={tipo} value={tipo}>
                  {tipo}
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
          tableLayout="fixed"
          sticky={false}
        />
      </Card>

      {/* Modal dettagli articolo */}
      {console.log("🔥 DEBUG ArticoliManagement: Rendering Detail Modal, isDetailModalVisible:", isDetailModalVisible)}
      {console.log("🔥 DEBUG ArticoliManagement: selectedArticolo:", selectedArticolo)}
      <Modal
        title="Dettagli Articolo"
        open={isDetailModalVisible}
        onCancel={() => {
          console.log("🔥 DEBUG ArticoliManagement: Detail Modal cancelled");
          setIsDetailModalVisible(false);
        }}
        footer={[
          <Button key="close" onClick={() => {
            console.log("🔥 DEBUG ArticoliManagement: Detail Modal close button clicked");
            setIsDetailModalVisible(false);
          }}>
            Chiudi
          </Button>,
          <Button
            key="edit"
            type="primary"
            onClick={() => {
              console.log("🔥 DEBUG ArticoliManagement: Detail Modal edit button clicked");
              setIsDetailModalVisible(false);
              handleEdit(selectedArticolo as Articolo);
            }}
          >
            Modifica
          </Button>,
        ]}
        width={600}
        forceRender={true}
        getContainer={() => document.body}
        zIndex={1000}
      >
        {selectedArticolo && (
          <div>
            <Row gutter={16}>
              <Col span={12}>
                <p><strong>Codice:</strong> {selectedArticolo.codice}</p>
                <p><strong>Descrizione:</strong> {selectedArticolo.descrizione}</p>
                <p><strong>Tipo:</strong> {selectedArticolo.tipo || 'Non specificato'}</p>
                <p><strong>Unità di Misura:</strong> {selectedArticolo.unitaMisura}</p>
              </Col>
              <Col span={12}>
                <p><strong>Prezzo Vendita:</strong> € {selectedArticolo.prezzoVendita != null ? selectedArticolo.prezzoVendita.toFixed(2) : '0.00'}</p>
                <p><strong>Costo:</strong> € {selectedArticolo.costo != null ? selectedArticolo.costo.toFixed(2) : '0.00'}</p>
                <p><strong>Aliquota IVA:</strong> {selectedArticolo.aliquotaIva}%</p>
                <p><strong>Stato:</strong> 
                  <Tag color={selectedArticolo.attivo ? 'green' : 'red'} style={{ marginLeft: 8 }}>
                    {selectedArticolo.attivo ? 'Attivo' : 'Inattivo'}
                  </Tag>
                </p>
                {selectedArticolo.createdAt && (
                  <p><strong>Creato il:</strong> {new Date(selectedArticolo.createdAt).toLocaleDateString('it-IT')}</p>
                )}
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

      {console.log("🔥 DEBUG ArticoliManagement: Rendering Drawer, isFormVisible:", isFormVisible)}
      {console.log("🔥 DEBUG ArticoliManagement: isEditMode:", isEditMode)}
      <Drawer
        title={isEditMode ? "Modifica Articolo" : "Nuovo Articolo"}
        placement="right"
        width={720}
        onClose={() => {
          console.log("🔥 DEBUG ArticoliManagement: Drawer onClose called");
          handleFormCancel();
        }}
        open={isFormVisible}
        destroyOnClose={true}
        forceRender={true}
        getContainer={() => document.body}
        zIndex={1050}
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
