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
  Modal,
  message,
  Tooltip,
  Descriptions,
  Drawer,
  Popconfirm,
} from 'antd';
import {
  PlusOutlined,
  SearchOutlined,
  EditOutlined,
  DeleteOutlined,
  EyeOutlined,
  UploadOutlined,
} from '@ant-design/icons';
import {
  useGetArticoliFornitoriQuery,
  useDeleteArticoloFornitoreMutation,
  type ArticoloFornitore,
} from '../store/api/documentiApi';
import { ArticoloFornitoreForm } from './forms';
import ImportArticoliFornitoriJson from './utility/ImportArticoliFornitoriJson';

const { Title, Text } = Typography;
const { Option } = Select;

export const ArticoliFornitoriManagement: React.FC = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [fornitoreFilter, setFornitoreFilter] = useState<string>('');
  const [categoriaFilter, setCategoriaFilter] = useState<string>('');
  const [selectedArticoloFornitore, setSelectedArticoloFornitore] = useState<ArticoloFornitore | null>(null);
  const [isDetailModalVisible, setIsDetailModalVisible] = useState(false);
  const [isFormVisible, setIsFormVisible] = useState(false);
  const [isEditMode, setIsEditMode] = useState(false);
  const [isImportVisible, setIsImportVisible] = useState(false);

  const { data: articoliFornitori, isLoading, error, refetch } = useGetArticoliFornitoriQuery();
  const [deleteArticoloFornitore] = useDeleteArticoloFornitoreMutation();

  const handleDelete = async (id: number) => {
    try {
      await deleteArticoloFornitore(id).unwrap();
      message.success('Articolo fornitore eliminato con successo');
      refetch();
    } catch (error) {
      message.error('Errore durante l\'eliminazione dell\'articolo fornitore');
    }
  };

  const handleView = (articoloFornitore: ArticoloFornitore) => {
    setSelectedArticoloFornitore(articoloFornitore);
    setIsDetailModalVisible(true);
  };

  const handleAddNew = () => {
    setSelectedArticoloFornitore(null);
    setIsEditMode(false);
    setIsFormVisible(true);
  };

  const handleEdit = (articoloFornitore: ArticoloFornitore) => {
    setSelectedArticoloFornitore(articoloFornitore);
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

  const handleImportSuccess = () => {
    setIsImportVisible(false);
    refetch();
  };

  // Filtri per i dati
  const filteredArticoliFornitori = React.useMemo(() => {
    if (!articoliFornitori) return [];
    
    return articoliFornitori.filter((articoloFornitore: ArticoloFornitore) => {
      const matchesSearch = !searchTerm || 
        articoloFornitore.codice.toLowerCase().includes(searchTerm.toLowerCase()) ||
        articoloFornitore.descrizione.toLowerCase().includes(searchTerm.toLowerCase()) ||
        (articoloFornitore.fornitoreRagioneSociale && 
         articoloFornitore.fornitoreRagioneSociale.toLowerCase().includes(searchTerm.toLowerCase()));
      
      const matchesFornitore = !fornitoreFilter || 
        (articoloFornitore.fornitoreRagioneSociale && 
         articoloFornitore.fornitoreRagioneSociale.toLowerCase().includes(fornitoreFilter.toLowerCase()));
      
      const matchesCategoria = !categoriaFilter || 
        articoloFornitore.fornitoreCategoria === categoriaFilter;

      return matchesSearch && matchesFornitore && matchesCategoria;
    });
  }, [articoliFornitori, searchTerm, fornitoreFilter, categoriaFilter]);

  const columns = [
    {
      title: 'Codice',
      dataIndex: 'codice',
      key: 'codice',
      width: 150,
      render: (text: string) => <Text code>{text}</Text>,
      sorter: (a: ArticoloFornitore, b: ArticoloFornitore) => a.codice.localeCompare(b.codice),
    },
    {
      title: 'Descrizione',
      dataIndex: 'descrizione',
      key: 'descrizione',
      ellipsis: true,
      render: (text: string) => (
        <Tooltip title={text}>
          {text}
        </Tooltip>
      ),
      sorter: (a: ArticoloFornitore, b: ArticoloFornitore) => a.descrizione.localeCompare(b.descrizione),
    },
    {
      title: 'Fornitore',
      key: 'fornitore',
      width: 200,
      render: (_: any, record: ArticoloFornitore) => (
        <div>
          <div><strong>{record.fornitoreRagioneSociale}</strong></div>
          {record.fornitorePartitaIva && (
            <small>P.IVA: {record.fornitorePartitaIva}</small>
          )}
        </div>
      ),
    },
    {
      title: 'Categoria',
      key: 'categoria',
      width: 100,
      render: (_: any, record: ArticoloFornitore) => (
        record.fornitoreCategoria ? (
          <Tag color="blue">{record.fornitoreCategoria}</Tag>
        ) : '-'
      ),
      filters: [
        { text: 'Merci', value: 'merci' },
        { text: 'Servizi', value: 'servizi' },
      ],
      onFilter: (value: any, record: ArticoloFornitore) => record.fornitoreCategoria === value,
    },
    {
      title: 'Prezzo',
      dataIndex: 'prezzoUnitario',
      key: 'prezzoUnitario',
      width: 100,
      render: (prezzo: number) => `€ ${prezzo.toFixed(2)}`,
      sorter: (a: ArticoloFornitore, b: ArticoloFornitore) => a.prezzoUnitario - b.prezzoUnitario,
    },
    {
      title: 'U.M.',
      dataIndex: 'unitaMisura',
      key: 'unitaMisura',
      width: 80,
    },
    {
      title: 'IVA',
      dataIndex: 'aliquotaIVA',
      key: 'aliquotaIVA',
      width: 80,
      render: (iva: number) => `${iva}%`,
    },
    {
      title: 'Azioni',
      key: 'actions',
      width: 150,
      render: (_: any, record: ArticoloFornitore) => (
        <Space size="small">
          <Button
            type="link"
            icon={<EyeOutlined />}
            size="small"
            onClick={() => handleView(record)}
          >
            Dettagli
          </Button>
          <Button
            type="link"
            icon={<EditOutlined />}
            size="small"
            onClick={() => handleEdit(record)}
          >
            Modifica
          </Button>
          <Popconfirm
            title="Sei sicuro di voler eliminare questo articolo fornitore?"
            onConfirm={() => handleDelete(record.id)}
            okText="Sì"
            cancelText="No"
          >
            <Button
              type="link"
              danger
              icon={<DeleteOutlined />}
              size="small"
            >
              Elimina
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  if (error) {
    console.error('Errore completo:', error);
    return (
      <div>
        <Title level={2}>Gestione Articoli Fornitori</Title>
        <div style={{ textAlign: 'center', padding: '50px' }}>
          <Text type="danger">Errore nel caricamento degli articoli fornitori</Text>
          <br />
          <Text type="secondary">
            {error && typeof error === 'object' && 'status' in error 
              ? `Errore HTTP ${error.status}` 
              : 'Errore di connessione al backend'}
          </Text>
          <br />
          <Button onClick={() => refetch()} style={{ marginTop: 16 }}>
            Riprova
          </Button>
        </div>
      </div>
    );
  }

  return (
    <div>
      <Row justify="space-between" align="middle" style={{ marginBottom: 20 }}>
        <Col>
          <Title level={2}>Gestione Articoli Fornitori</Title>
        </Col>
        <Col>
          <Space>
            <Button
              icon={<UploadOutlined />}
              onClick={() => setIsImportVisible(true)}
            >
              Importa JSON
            </Button>
            <Button
              type="primary"
              icon={<PlusOutlined />}
              onClick={handleAddNew}
            >
              Nuovo Articolo
            </Button>
          </Space>
        </Col>
      </Row>

      {/* Filtri */}
      <Row gutter={16} style={{ marginBottom: 16 }}>
        <Col xs={24} sm={8} md={6}>
          <Input
            placeholder="Cerca per codice, descrizione o fornitore..."
            prefix={<SearchOutlined />}
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            allowClear
          />
        </Col>
        <Col xs={24} sm={8} md={6}>
          <Input
            placeholder="Filtra per fornitore..."
            value={fornitoreFilter}
            onChange={(e) => setFornitoreFilter(e.target.value)}
            allowClear
          />
        </Col>
        <Col xs={24} sm={8} md={6}>
          <Select
            placeholder="Categoria"
            value={categoriaFilter}
            onChange={setCategoriaFilter}
            allowClear
            style={{ width: '100%' }}
          >
            <Option value="merci">Merci</Option>
            <Option value="servizi">Servizi</Option>
          </Select>
        </Col>
        <Col>
          <Text type="secondary">
            Totale: {filteredArticoliFornitori.length} articoli fornitori
          </Text>
        </Col>
      </Row>

      {/* Tabella */}
      <Table
        columns={columns}
        dataSource={filteredArticoliFornitori}
        rowKey="id"
        loading={isLoading}
        pagination={{
          pageSize: 10,
          showSizeChanger: true,
          showQuickJumper: true,
          showTotal: (total, range) => 
            `${range[0]}-${range[1]} di ${total} articoli fornitori`,
        }}
        size="small"
        scroll={{ x: 1000 }}
      />

      {/* Form Modifica/Nuovo */}
      <ArticoloFornitoreForm
        visible={isFormVisible}
        onClose={handleFormCancel}
        onSuccess={handleFormSuccess}
        articoloFornitore={selectedArticoloFornitore}
        isEditMode={isEditMode}
      />

      {/* Modal Dettaglio */}
      <Modal
        title="Dettaglio Articolo Fornitore"
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
              handleEdit(selectedArticoloFornitore!);
            }}
          >
            Modifica
          </Button>,
        ]}
        width={800}
      >
        {selectedArticoloFornitore && (
          <Descriptions column={2} bordered size="small">
            <Descriptions.Item label="Codice" span={1}>
              <Text code>{selectedArticoloFornitore.codice}</Text>
            </Descriptions.Item>
            <Descriptions.Item label="Codice Interno" span={1}>
              {selectedArticoloFornitore.codiceInterno || 'Non specificato'}
            </Descriptions.Item>
            <Descriptions.Item label="Descrizione" span={2}>
              {selectedArticoloFornitore.descrizione}
            </Descriptions.Item>
            <Descriptions.Item label="Prezzo Unitario" span={1}>
              € {selectedArticoloFornitore.prezzoUnitario.toFixed(2)}
            </Descriptions.Item>
            <Descriptions.Item label="Unità di Misura" span={1}>
              {selectedArticoloFornitore.unitaMisura}
            </Descriptions.Item>
            <Descriptions.Item label="Aliquota IVA" span={1}>
              {selectedArticoloFornitore.aliquotaIVA}%
            </Descriptions.Item>
            <Descriptions.Item label="Ragione Sociale Fornitore" span={1}>
              <strong>{selectedArticoloFornitore.fornitoreRagioneSociale}</strong>
            </Descriptions.Item>
            {selectedArticoloFornitore.fornitorePartitaIva && (
              <Descriptions.Item label="P.IVA Fornitore" span={1}>
                {selectedArticoloFornitore.fornitorePartitaIva}
              </Descriptions.Item>
            )}
            {selectedArticoloFornitore.fornitoreCategoria && (
              <Descriptions.Item label="Categoria Fornitore" span={1}>
                <Tag color="blue">{selectedArticoloFornitore.fornitoreCategoria}</Tag>
              </Descriptions.Item>
            )}
            <Descriptions.Item label="Creato il" span={1}>
              <Text type="secondary">
                {new Date(selectedArticoloFornitore.createdAt).toLocaleString('it-IT')}
              </Text>
            </Descriptions.Item>
            {selectedArticoloFornitore.updatedAt && (
              <Descriptions.Item label="Ultima modifica" span={1}>
                <Text type="secondary">
                  {new Date(selectedArticoloFornitore.updatedAt).toLocaleString('it-IT')}
                </Text>
              </Descriptions.Item>
            )}
          </Descriptions>
        )}
      </Modal>

      {/* Drawer Import */}
      <Drawer
        title="Importa Articoli Fornitori da JSON"
        placement="right"
        onClose={() => setIsImportVisible(false)}
        open={isImportVisible}
        width={600}
      >
        <ImportArticoliFornitoriJson onSuccess={handleImportSuccess} />
      </Drawer>
    </div>
  );
};

export default ArticoliFornitoriManagement;
