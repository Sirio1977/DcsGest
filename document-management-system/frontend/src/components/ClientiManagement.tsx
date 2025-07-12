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
  Descriptions,
  Drawer,
  Tooltip,
} from 'antd';
import {
  PlusOutlined,
  SearchOutlined,
  EditOutlined,
  DeleteOutlined,
  EyeOutlined,
  UserOutlined,
  ShopOutlined,
} from '@ant-design/icons';
import { useGetClientiQuery, useDeleteClienteMutation } from '../store/api/clientiApi';
import { ClienteForm } from './forms';
import type { Cliente } from '../types/entities';

const { Title } = Typography;
const { Option } = Select;

interface ClientiManagementProps {
  clienti?: Cliente[];
  onSuccess?: () => void;
}

export const ClientiManagement: React.FC<ClientiManagementProps> = ({ clienti = [], onSuccess = () => {} }) => {
  const [isFormVisible, setIsFormVisible] = useState(false);
  const [isEditMode, setIsEditMode] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  const [tipoFilter, setTipoFilter] = useState<string>('');
  const [statusFilter, setStatusFilter] = useState<string>('');
  const [selectedCliente, setSelectedCliente] = useState<Cliente | null>(null);
  const [isDetailModalVisible, setIsDetailModalVisible] = useState(false);

  const { data: clientiResponse, isLoading, error, refetch } = useGetClientiQuery({});
  const [deleteCliente] = useDeleteClienteMutation();

  // Estrae i clienti dalla risposta paginata
  const clientiData = clientiResponse?.content || [];

  const handleDelete = async (id: number) => {
    try {
      await deleteCliente(id).unwrap();
      message.success('Cliente eliminato con successo');
      refetch();
    } catch (error) {
      message.error('Errore durante l\'eliminazione del cliente');
    }
  };

  const handleView = (cliente: Cliente) => {
    setSelectedCliente(cliente);
    setIsDetailModalVisible(true);
  };
  
  const handleAddNew = () => {
    setSelectedCliente(null);
    setIsEditMode(false);
    setIsFormVisible(true);
  };

  const handleEdit = (cliente: Cliente) => {
    setSelectedCliente(cliente);
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
  const filteredClienti = React.useMemo(() => {
    if (!clientiData) return [];
    
    return clientiData.filter((cliente: Cliente) => {
      const matchesSearch = !searchTerm || 
        cliente.ragioneSociale.toLowerCase().includes(searchTerm.toLowerCase()) ||
        (cliente.partitaIva && cliente.partitaIva.includes(searchTerm)) ||
        (cliente.codiceFiscale && cliente.codiceFiscale.toLowerCase().includes(searchTerm.toLowerCase())) ||
        (cliente.email && cliente.email.toLowerCase().includes(searchTerm.toLowerCase()));
      
      const matchesTipo = !tipoFilter || cliente.tipo === tipoFilter;
      const matchesStatus = !statusFilter || 
        (statusFilter === 'attivi' && cliente.attivo) ||
        (statusFilter === 'inattivi' && !cliente.attivo);
      
      return matchesSearch && matchesTipo && matchesStatus;
    });
  }, [clientiData, searchTerm, tipoFilter, statusFilter]);

  const getTipoIcon = (tipo: string) => {
    switch (tipo) {
      case 'CLIENTE':
        return <UserOutlined />;
      case 'FORNITORE':
        return <ShopOutlined />;
      case 'CLIENTE_FORNITORE':
        return <><UserOutlined /> <ShopOutlined /></>;
      default:
        return <UserOutlined />;
    }
  };

  const getTipoColor = (tipo: string) => {
    switch (tipo) {
      case 'CLIENTE':
        return 'blue';
      case 'FORNITORE':
        return 'green';
      case 'CLIENTE_FORNITORE':
        return 'purple';
      default:
        return 'blue';
    }
  };

  const columns = [
    {
      title: 'Ragione Sociale',
      dataIndex: 'ragioneSociale',
      key: 'ragioneSociale',
      ellipsis: true,
      sorter: (a: Cliente, b: Cliente) => a.ragioneSociale.localeCompare(b.ragioneSociale),
    },
    {
      title: 'P.IVA / Cod.Fiscale',
      key: 'fiscalData',
      width: 160,
      render: (_: any, record: Cliente) => (
        <div>
          {record.partitaIva && <div><small>P.IVA: {record.partitaIva}</small></div>}
          {record.codiceFiscale && <div><small>C.F.: {record.codiceFiscale}</small></div>}
        </div>
      ),
    },
    {
      title: 'Città',
      key: 'location',
      width: 150,
      render: (_: any, record: Cliente) => (
        <div>
          {record.citta && <div>{record.citta}</div>}
          {record.provincia && <small>({record.provincia})</small>}
        </div>
      ),
    },
    {
      title: 'Contatti',
      key: 'contacts',
      width: 180,
      render: (_: any, record: Cliente) => (
        <div>
          {record.telefono && <div><small>Tel: {record.telefono}</small></div>}
          {record.email && <div><small>{record.email}</small></div>}
        </div>
      ),
    },
    {
      title: 'Categoria/Note',
      dataIndex: 'note',
      key: 'note',
      width: 150,
      ellipsis: true,
      render: (note: string, record: Cliente) => {
        if (record.tipo === 'FORNITORE' && note && note.startsWith('Categoria: ')) {
          const categoria = note.replace('Categoria: ', '');
          return <Tag color="orange">{categoria}</Tag>;
        }
        return note ? (
          <Tooltip title={note}>
            <span style={{ color: '#666' }}>{note.length > 20 ? note.substring(0, 20) + '...' : note}</span>
          </Tooltip>
        ) : '-';
      }
    },
    {
      title: 'Tipo',
      dataIndex: 'tipo',
      key: 'tipo',
      width: 130,
      render: (tipo: string) => (
        <Tag color={getTipoColor(tipo)} icon={getTipoIcon(tipo)}>
          {tipo.replace('_', ' ')}
        </Tag>
      ),
      filters: [
        { text: 'Cliente', value: 'CLIENTE' },
        { text: 'Fornitore', value: 'FORNITORE' },
        { text: 'Cliente/Fornitore', value: 'CLIENTE_FORNITORE' },
      ],
      onFilter: (value: any, record: Cliente) => record.tipo === value,
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
      onFilter: (value: any, record: Cliente) => record.attivo === value,
    },
    {
      title: 'Azioni',
      key: 'actions',
      width: 150,
      render: (_: any, record: Cliente) => (
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
            title="Sei sicuro di voler eliminare questo cliente?"
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
          <Title level={4}>Errore nel caricamento dei clienti</Title>
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
          <Title level={2}>Gestione Clienti/Fornitori</Title>
        </Col>
        <Col>
          <Button
            type="primary"
            icon={<PlusOutlined />}
            onClick={handleAddNew}
          >
            Nuovo Cliente
          </Button>
        </Col>
      </Row>

      <Card>
        <Row gutter={16} style={{ marginBottom: 16 }}>
          <Col xs={24} sm={12} md={10}>
            <Input
              placeholder="Cerca per ragione sociale, P.IVA, C.F., email..."
              prefix={<SearchOutlined />}
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              allowClear
            />
          </Col>
          <Col xs={24} sm={6} md={4}>
            <Select
              placeholder="Tipo"
              value={tipoFilter}
              onChange={setTipoFilter}
              allowClear
              style={{ width: '100%' }}
            >
              <Option value="CLIENTE">Cliente</Option>
              <Option value="FORNITORE">Fornitore</Option>
              <Option value="CLIENTE_FORNITORE">Cliente/Fornitore</Option>
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
          <Col xs={24} sm={24} md={6}>
            <Space>
              <span>Totale: {filteredClienti.length} clienti</span>
              {clienti && (
                <span>
                  (Attivi: {clienti.filter((c: Cliente) => c.attivo).length})
                </span>
              )}
            </Space>
          </Col>
        </Row>

        <Table
          columns={columns}
          dataSource={filteredClienti}
          rowKey="id"
          loading={isLoading}
          pagination={{
            pageSize: 20,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total, range) =>
              `${range[0]}-${range[1]} di ${total} clienti`,
          }}
          scroll={{ x: 1200 }}
          size="small"
          tableLayout="fixed"
          sticky={false}
        />
      </Card>

      {/* Modal dettagli cliente */}
      <Modal
        title={
          <Space>
            {selectedCliente && getTipoIcon(selectedCliente.tipo)}
            Dettagli Cliente
          </Space>
        }
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
              handleEdit(selectedCliente as Cliente);
            }}
          >
            Modifica
          </Button>,
        ]}
        width={700}
      >
        {selectedCliente && (
          <Descriptions bordered column={2} size="small">
            <Descriptions.Item label="Ragione Sociale" span={2}>
              {selectedCliente.ragioneSociale}
            </Descriptions.Item>
            
            <Descriptions.Item label="Partita IVA">
              {selectedCliente.partitaIva || 'Non specificata'}
            </Descriptions.Item>
            <Descriptions.Item label="Codice Fiscale">
              {selectedCliente.codiceFiscale || 'Non specificato'}
            </Descriptions.Item>
            
            <Descriptions.Item label="Indirizzo" span={2}>
              {selectedCliente.indirizzo || 'Non specificato'}
            </Descriptions.Item>
            
            <Descriptions.Item label="Città">
              {selectedCliente.citta || 'Non specificata'}
            </Descriptions.Item>
            <Descriptions.Item label="Provincia">
              {selectedCliente.provincia || 'Non specificata'}
            </Descriptions.Item>
            
            <Descriptions.Item label="CAP">
              {selectedCliente.cap || 'Non specificato'}
            </Descriptions.Item>
            <Descriptions.Item label="Telefono">
              {selectedCliente.telefono || 'Non specificato'}
            </Descriptions.Item>
            
            <Descriptions.Item label="Email">
              {selectedCliente.email || 'Non specificata'}
            </Descriptions.Item>
            <Descriptions.Item label="PEC">
              {selectedCliente.pec || 'Non specificata'}
            </Descriptions.Item>
            
            <Descriptions.Item label="Tipo">
              <Tag color={getTipoColor(selectedCliente.tipo)} icon={getTipoIcon(selectedCliente.tipo)}>
                {selectedCliente.tipo.replace('_', ' ')}
              </Tag>
            </Descriptions.Item>
            
            {selectedCliente.tipo === 'FORNITORE' && selectedCliente.note && selectedCliente.note.startsWith('Categoria: ') && (
              <Descriptions.Item label="Categoria">
                <Tag color="orange">{selectedCliente.note.replace('Categoria: ', '')}</Tag>
              </Descriptions.Item>
            )}
            
            <Descriptions.Item label="Stato">
              <Tag color={selectedCliente.attivo ? 'green' : 'red'}>
                {selectedCliente.attivo ? 'Attivo' : 'Inattivo'}
              </Tag>
            </Descriptions.Item>
            
            <Descriptions.Item label="Creato il" span={2}>
              {selectedCliente.createdAt ? new Date(selectedCliente.createdAt).toLocaleDateString('it-IT') : 'N/A'}
            </Descriptions.Item>
            
            {selectedCliente.note && (
              <Descriptions.Item label="Note" span={2}>
                <div style={{ background: '#f5f5f5', padding: 8, borderRadius: 4 }}>
                  {selectedCliente.note}
                </div>
              </Descriptions.Item>
            )}
          </Descriptions>
        )}
      </Modal>
      
      <Drawer
        title={isEditMode ? "Modifica Cliente" : "Nuovo Cliente"}
        placement="right"
        width={720}
        onClose={handleFormCancel}
        open={isFormVisible}
        destroyOnClose={true}
      >
        <ClienteForm
          cliente={selectedCliente || undefined}
          onSuccess={handleFormSuccess}
          onCancel={handleFormCancel}
        />
      </Drawer>
    </div>
  );
};
