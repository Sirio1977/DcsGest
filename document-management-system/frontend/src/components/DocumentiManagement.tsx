import React, { useState } from 'react';
import { 
  Card, 
  Table, 
  Button, 
  Space, 
  Select, 
  DatePicker, 
  Input, 
  Tag, 
  Typography, 
  Row, 
  Col,
  Modal,
  message,
  Popconfirm,
  Tooltip,
  Form,
  InputNumber,
  Divider,
  Switch,
  Tabs
} from 'antd';
import { 
  PlusOutlined, 
  EditOutlined, 
  DeleteOutlined, 
  FilePdfOutlined,
  MailOutlined,
  CopyOutlined,
  SearchOutlined,
  ReloadOutlined,
  MinusCircleOutlined
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { 
  useGetDocumentiQuery,
  useCreateDocumentoMutation,
  useDeleteDocumentoMutation,
  useCambiaStatoDocumentoMutation,
  useDuplicaDocumentoMutation,
  useGeneraPdfDocumentoMutation,
  useInviaDocumentoMutation
} from '../store/api/documentiApi';
import { useGetClientiQuery } from '../store/api/clientiApi';
import { 
  DocumentoResponseDto, 
  DocumentoCreateDto,
  TipoDocumento, 
  StatoDocumento, 
  DocumentoFilter,
  RigaDocumentoDto,
  ScadenzaDto,
  AliquotaIva
} from '../types/documento';

const { Title } = Typography;
const { Option } = Select;
const { RangePicker } = DatePicker;
const { Search, TextArea } = Input;

// Configurazione colori per stati
const statoColors: Record<StatoDocumento, string> = {
  [StatoDocumento.BOZZA]: 'default',
  [StatoDocumento.EMESSO]: 'processing',
  [StatoDocumento.STAMPATO]: 'warning',
  [StatoDocumento.INVIATO]: 'success',
  [StatoDocumento.PAGATO]: 'green',
  [StatoDocumento.ANNULLATO]: 'error',
};

// Configurazione colori per tipi documento
const tipoColors: Record<TipoDocumento, string> = {
  [TipoDocumento.FATTURA]: 'blue',
  [TipoDocumento.FATTURA_ELETTRONICA]: 'cyan',
  [TipoDocumento.NOTA_CREDITO]: 'orange',
  [TipoDocumento.NOTA_DEBITO]: 'red',
  [TipoDocumento.DDT]: 'green',
  [TipoDocumento.PREVENTIVO]: 'purple',
  [TipoDocumento.ORDINE]: 'magenta',
  [TipoDocumento.RICEVUTA]: 'gold',
};



const DocumentiManagement: React.FC = () => {
  // Stati locali per filtri e paginazione
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(20);
  const [sortBy, setSortBy] = useState('dataDocumento');
  const [sortDir, setSortDir] = useState<'asc' | 'desc'>('desc');
  const [filtri, setFiltri] = useState<DocumentoFilter>({});
  
  // Stati per il form di creazione
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [documentoForm] = Form.useForm(); // Rinominato per chiarezza
  
  // Hook per API
  const { 
    data: documentiData, 
    isLoading, 
    isError, 
    refetch 
  } = useGetDocumentiQuery({ 
    page, 
    size, 
    sortBy, 
    sortDir, 
    filter: filtri 
  });

  // Hook per recuperare clienti
  const { 
    data: clientiData, 
    isLoading: isLoadingClienti 
  } = useGetClientiQuery({ 
    size: 1000 // Recuperiamo tutti i clienti per il dropdown
  });

  const [createDocumento, { isLoading: isCreating }] = useCreateDocumentoMutation();
  const [deleteDocumento] = useDeleteDocumentoMutation();
  const [cambiaStato] = useCambiaStatoDocumentoMutation();
  const [duplicaDocumento] = useDuplicaDocumentoMutation();
  const [generaPdf] = useGeneraPdfDocumentoMutation();
  const [inviaDocumento] = useInviaDocumentoMutation();

  // Handlers per azioni
  const handleDelete = async (id: number) => {
    try {
      await deleteDocumento(id).unwrap();
      message.success('Documento eliminato con successo');
    } catch (error) {
      message.error('Errore durante l\'eliminazione del documento');
    }
  };

  // Handler per apertura modal creazione
  const handleNuovoDocumento = () => {
    console.log("🔥 DEBUG: handleNuovoDocumento called");
    console.log("🔥 DEBUG: documentoForm:", documentoForm);
    console.log("🔥 DEBUG: isModalVisible before:", isModalVisible);
    
    try {
      documentoForm.resetFields();
      // Imposta valori di default
      documentoForm.setFieldsValue({
        tipoDocumento: TipoDocumento.FATTURA,
        dataDocumento: new Date().toISOString().split('T')[0],
        righe: [{
          descrizione: '',
          quantita: 1,
          prezzoUnitario: 0,
          scontoPercentuale: 0,
          aliquotaIva: AliquotaIva.ORDINARIA_22
        }],
        scadenze: [{
          dataScadenza: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString().split('T')[0], // +30 giorni
          importo: 0
        }]
      });
      
      console.log("🔥 DEBUG: Setting isModalVisible to true");
      setIsModalVisible(true);
      console.log("🔥 DEBUG: isModalVisible after:", true);
    } catch (error) {
      console.error("🔥 ERROR in handleNuovoDocumento:", error);
    }
  };

  // Handler per chiusura modal
  const handleModalCancel = () => {
    console.log("🔥 DEBUG: handleModalCancel called");
    console.log("🔥 DEBUG: isModalVisible before:", isModalVisible);
    setIsModalVisible(false);
    documentoForm.resetFields(); // Pulisce il form quando si chiude il modal
    console.log("🔥 DEBUG: Modal closed, isModalVisible after:", false);
  };

  // Handler per creazione documento
  const handleCreaDocumento = async (values: any) => {
    try {
      // Prepara i dati per l'API
      const documento: DocumentoCreateDto = {
        tipoDocumento: values.tipoDocumento,
        soggettoId: values.soggettoId,
        dataDocumento: values.dataDocumento,
        dataScadenza: values.dataScadenza,
        titolo: values.titolo,
        descrizione: values.descrizione,
        note: values.note,
        righe: values.righe?.map((riga: any) => ({
          articoloId: riga.articoloId,
          descrizione: riga.descrizione,
          quantita: riga.quantita,
          prezzoUnitario: riga.prezzoUnitario,
          scontoPercentuale: riga.scontoPercentuale || 0,
          aliquotaIva: riga.aliquotaIva
        })) || [],
        scadenze: values.scadenze?.map((scadenza: any) => ({
          dataScadenza: scadenza.dataScadenza,
          importo: scadenza.importo,
          note: scadenza.note
        })) || [],
        // Campi specifici per tipo documento
        motivoNota: values.motivoNota,
        documentoOrigineId: values.documentoOrigineId,
        causaleDescrizione: values.causaleDescrizione,
        dataTrasporto: values.dataTrasporto,
        validitaOfferta: values.validitaOfferta,
        codiceDestinatario: values.codiceDestinatario,
        pecDestinatario: values.pecDestinatario
      };

      await createDocumento(documento).unwrap();
      message.success('Documento creato con successo');
      setIsModalVisible(false);
      documentoForm.resetFields();
    } catch (error: any) {
      console.error('Errore creazione documento:', error);
      message.error(error?.data?.message || 'Errore durante la creazione del documento');
    }
  };

  // Calcola totale riga
  const calcolaTotaleRiga = (riga: any) => {
    if (!riga) return 0;
    const { quantita = 0, prezzoUnitario = 0, scontoPercentuale = 0 } = riga;
    const subtotale = quantita * prezzoUnitario;
    const sconto = subtotale * (scontoPercentuale / 100);
    return subtotale - sconto;
  };

  const handleCambiaStato = async (id: number, nuovoStato: StatoDocumento) => {
    try {
      await cambiaStato({ id, nuovoStato }).unwrap();
      message.success('Stato documento aggiornato');
    } catch (error) {
      message.error('Errore durante l\'aggiornamento dello stato');
    }
  };

  const handleDuplica = async (id: number, tipoDocumento: TipoDocumento) => {
    try {
      await duplicaDocumento({ id, nuovoTipo: tipoDocumento }).unwrap();
      message.success('Documento duplicato con successo');
    } catch (error) {
      message.error('Errore durante la duplicazione del documento');
    }
  };

  const handleGeneraPdf = async (id: number) => {
    try {
      const pdfBlob = await generaPdf(id).unwrap();
      const url = window.URL.createObjectURL(pdfBlob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `documento_${id}.pdf`;
      link.click();
      window.URL.revokeObjectURL(url);
      message.success('PDF generato con successo');
    } catch (error) {
      message.error('Errore durante la generazione del PDF');
    }
  };

  const handleInviaEmail = async (id: number) => {
    let emailValue = '';
    
    Modal.confirm({
      title: 'Invia documento via email',
      content: (
        <Input 
          placeholder="Inserisci email destinatario"
          onChange={(e) => { emailValue = e.target.value; }}
        />
      ),
      onOk: async () => {
        if (emailValue) {
          try {
            await inviaDocumento({ id, emailDestinatario: emailValue }).unwrap();
            message.success('Email inviata con successo');
          } catch (error) {
            message.error('Errore durante l\'invio dell\'email');
          }
        }
      },
    });
  };

  // Configurazione colonne tabella
  const columns: ColumnsType<DocumentoResponseDto> = [
    {
      title: 'Numero',
      dataIndex: 'numero',
      key: 'numero',
      width: 100,
      render: (numero: number, record: DocumentoResponseDto) => 
        `${numero}/${record.anno}`,
      sorter: true,
    },
    {
      title: 'Tipo',
      dataIndex: 'tipoDocumento',
      key: 'tipoDocumento',
      width: 140,
      render: (tipo: TipoDocumento) => (
        <Tag color={tipoColors[tipo]}>
          {tipo.replace('_', ' ')}
        </Tag>
      ),
      filters: Object.values(TipoDocumento).map(tipo => ({
        text: tipo.replace('_', ' '),
        value: tipo,
      })),
    },
    {
      title: 'Data',
      dataIndex: 'dataDocumento',
      key: 'dataDocumento',
      width: 120,
      render: (data: string) => new Date(data).toLocaleDateString('it-IT'),
      sorter: true,
    },
    {
      title: 'Cliente/Fornitore',
      dataIndex: ['soggetto', 'ragioneSociale'],
      key: 'soggetto',
      ellipsis: true,
    },
    {
      title: 'Titolo',
      dataIndex: 'titolo',
      key: 'titolo',
      ellipsis: true,
    },
    {
      title: 'Totale',
      dataIndex: 'totaleDocumento',
      key: 'totaleDocumento',
      width: 120,
      render: (totale: number) => `€ ${totale.toFixed(2)}`,
      align: 'right',
      sorter: true,
    },
    {
      title: 'Stato',
      dataIndex: 'stato',
      key: 'stato',
      width: 120,
      render: (stato: StatoDocumento) => (
        <Tag color={statoColors[stato]}>
          {stato}
        </Tag>
      ),
      filters: Object.values(StatoDocumento).map(stato => ({
        text: stato,
        value: stato,
      })),
    },
    {
      title: 'Azioni',
      key: 'azioni',
      width: 200,
      render: (_, record: DocumentoResponseDto) => (
        <Space>
          <Tooltip title="Modifica">
            <Button 
              size="small" 
              icon={<EditOutlined />} 
              onClick={() => {/* TODO: Apri form modifica */}}
            />
          </Tooltip>
          
          <Tooltip title="Genera PDF">
            <Button 
              size="small" 
              icon={<FilePdfOutlined />} 
              onClick={() => handleGeneraPdf(record.id)}
            />
          </Tooltip>
          
          <Tooltip title="Invia Email">
            <Button 
              size="small" 
              icon={<MailOutlined />} 
              onClick={() => handleInviaEmail(record.id)}
            />
          </Tooltip>
          
          <Tooltip title="Duplica">
            <Button 
              size="small" 
              icon={<CopyOutlined />} 
              onClick={() => handleDuplica(record.id, record.tipoDocumento)}
            />
          </Tooltip>
          
          <Popconfirm
            title="Sei sicuro di voler eliminare questo documento?"
            onConfirm={() => handleDelete(record.id)}
            okText="Sì"
            cancelText="No"
          >
            <Tooltip title="Elimina">
              <Button 
                size="small" 
                danger 
                icon={<DeleteOutlined />} 
              />
            </Tooltip>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  // Handler per cambio filtri
  const handleFiltroChange = (key: keyof DocumentoFilter, value: any) => {
    setFiltri(prev => ({
      ...prev,
      [key]: value,
    }));
    setPage(0); // Reset alla prima pagina
  };

  // Handler per reset filtri
  const handleResetFiltri = () => {
    setFiltri({});
    setPage(0);
  };

  const tabItems = [
    {
      key: 'clienti',
      label: 'Clienti',
      children: (
        <Table
          dataSource={clientiData?.content?.map(cliente => ({
            id: cliente.id || 0,
            tipoDocumento: TipoDocumento.FATTURA,
            numero: 0,
            anno: new Date().getFullYear(),
            dataDocumento: '',
            soggetto: { ...cliente, id: cliente.id || 0 },
            titolo: '',
            totaleDocumento: 0,
            totaleImponibile: 0,
            totaleIva: 0,
            righe: [],
            stato: StatoDocumento.BOZZA,
            riepiloghi: [],
            scadenze: [],
            inviato: false,
            stampato: false,
            dataCreazione: new Date().toISOString(),
            dataModifica: new Date().toISOString(),
          })) || []}
          columns={columns}
        />
      ),
    },
    {
      key: 'fornitori',
      label: 'Fornitori',
      children: (
        <Table
          dataSource={clientiData?.content?.map(cliente => ({
            id: cliente.id || 0,
            tipoDocumento: TipoDocumento.FATTURA,
            numero: 0,
            anno: new Date().getFullYear(),
            dataDocumento: '',
            soggetto: { ...cliente, id: cliente.id || 0 },
            titolo: '',
            totaleDocumento: 0,
            totaleImponibile: 0,
            totaleIva: 0,
            righe: [],
            stato: StatoDocumento.BOZZA,
            riepiloghi: [],
            scadenze: [],
            inviato: false,
            stampato: false,
            dataCreazione: new Date().toISOString(),
            dataModifica: new Date().toISOString(),
          })) || []}
          columns={columns}
        />
      ),
    },
  ];

  return (
    <div className="p-lg">
      <Card>
        <Row gutter={[16, 16]} align="middle" justify="space-between">
          <Col>
            <Title level={2} className="m-0">
              📄 Gestione Documenti
            </Title>
          </Col>
          <Col>
            <Space>
              <Button
                type="primary"
                icon={<PlusOutlined />}
                onClick={handleNuovoDocumento}
              >
                Nuovo Documento
              </Button>
              <Button
                icon={<ReloadOutlined />}
                onClick={() => refetch()}
              >
                Aggiorna
              </Button>
            </Space>
          </Col>
        </Row>
        
        {/* Filtri */}
        <Row gutter={[16, 16]} className="my-md">
          <Col xs={24} sm={12} md={6}>
            <Select
              placeholder="Tipo documento"
              allowClear
              className="w-full"
              value={filtri.tipoDocumento}
              onChange={(value) => handleFiltroChange('tipoDocumento', value)}
            >
              {Object.values(TipoDocumento).map(tipo => (
                <Option key={tipo} value={tipo}>
                  {tipo.replace('_', ' ')}
                </Option>
              ))}
            </Select>
          </Col>
          
          <Col xs={24} sm={12} md={6}>
            <Select
              placeholder="Stato documento"
              allowClear
              className="w-full"
              value={filtri.statoDocumento}
              onChange={(value) => handleFiltroChange('statoDocumento', value)}
            >
              {Object.values(StatoDocumento).map(stato => (
                <Option key={stato} value={stato}>
                  {stato}
                </Option>
              ))}
            </Select>
          </Col>
          
          <Col xs={24} sm={12} md={6}>
            <RangePicker
              className="w-full"
              placeholder={['Data inizio', 'Data fine']}
              onChange={(dates) => {
                if (dates) {
                  handleFiltroChange('dataInizio', dates[0]?.format('YYYY-MM-DD'));
                  handleFiltroChange('dataFine', dates[1]?.format('YYYY-MM-DD'));
                } else {
                  handleFiltroChange('dataInizio', undefined);
                  handleFiltroChange('dataFine', undefined);
                }
              }}
            />
          </Col>
          
          <Col xs={24} sm={12} md={6}>
            <Search
              placeholder="Cerca cliente/numero..."
              allowClear
              onSearch={(value) => handleFiltroChange('soggettoFilter', value)}
            />
          </Col>
        </Row>
        
        <Row>
          <Col span={24}>
            <Button onClick={handleResetFiltri}>
              Reset Filtri
            </Button>
          </Col>
        </Row>
        
        {/* Tabella documenti */}
        <Table
          columns={columns}
          dataSource={documentiData?.content || []}
          rowKey="id"
          loading={isLoading}
          pagination={{
            current: page + 1,
            pageSize: size,
            total: documentiData?.totalElements || 0,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total, range) => 
              `${range[0]}-${range[1]} di ${total} documenti`,
            onChange: (newPage, newSize) => {
              setPage((newPage || 1) - 1);
              setSize(newSize || 20);
            },
          }}
          onChange={(pagination, filters, sorter) => {
            // Handle sorting
            if (sorter && !Array.isArray(sorter)) {
              setSortBy(sorter.field as string || 'dataDocumento');
              setSortDir(sorter.order === 'ascend' ? 'asc' : 'desc');
            }
          }}
          scroll={{ x: 1200 }}
          size="small"
          tableLayout="fixed"
          sticky={false}
        />
        
        {/* Modal per creazione documento */}
        {console.log("🔥 DEBUG: Rendering Modal, isModalVisible:", isModalVisible)}
        <Modal
          title="🆕 Nuovo Documento"
          open={isModalVisible}
          onCancel={handleModalCancel}
          footer={null}
          width={1200}
          destroyOnClose={true}
          centered={true}
          maskClosable={false}
          keyboard={true}
          style={{ top: 20 }}
          bodyStyle={{ 
            maxHeight: '70vh', 
            overflowY: 'auto',
            padding: '24px'
          }}
          forceRender={true}
          getContainer={() => document.body}
          zIndex={1000}
        >
          <Form
            form={documentoForm}
            layout="vertical"
            onFinish={handleCreaDocumento}
            scrollToFirstError
            preserve={false}
          >
            <Row gutter={[16, 16]}>
              {/* Informazioni base */}
              <Col span={24}>
                <Title level={4}>📋 Informazioni Base</Title>
              </Col>
              
              <Col xs={24} sm={12} md={8}>
                <Form.Item
                  name="tipoDocumento"
                  label="Tipo Documento"
                  rules={[{ required: true, message: 'Seleziona il tipo documento' }]}
                >
                  <Select placeholder="Seleziona tipo">
                    {Object.values(TipoDocumento).map(tipo => (
                      <Option key={tipo} value={tipo}>
                        {tipo.replace('_', ' ')}
                      </Option>
                    ))}
                  </Select>
                </Form.Item>
              </Col>
              
              <Col xs={24} sm={12} md={8}>
                <Form.Item
                  name="soggettoId"
                  label="Cliente/Fornitore"
                  rules={[{ required: true, message: 'Seleziona il cliente' }]}
                >
                  <Select 
                    placeholder="Seleziona cliente"
                    showSearch
                    loading={isLoadingClienti}
                    filterOption={(input, option) =>
                      String(option?.children || '').toLowerCase().includes(input.toLowerCase())
                    }
                  >
                    {clientiData?.content?.map((cliente) => (
                      <Option key={cliente.id} value={cliente.id}>
                        {cliente.ragioneSociale} - {cliente.partitaIva}
                      </Option>
                    )) || []}
                  </Select>
                </Form.Item>
              </Col>
              
              <Col xs={24} sm={12} md={8}>
                <Form.Item
                  name="dataDocumento"
                  label="Data Documento"
                  rules={[{ required: true, message: 'Inserisci la data' }]}
                >
                  <Input type="date" />
                </Form.Item>
              </Col>
              
              <Col xs={24} sm={12} md={8}>
                <Form.Item
                  name="dataScadenza"
                  label="Data Scadenza"
                >
                  <Input type="date" />
                </Form.Item>
              </Col>
              
              <Col xs={24} sm={12} md={16}>
                <Form.Item
                  name="titolo"
                  label="Titolo/Oggetto"
                  rules={[{ required: true, message: 'Inserisci il titolo' }]}
                >
                  <Input placeholder="es: Fattura per servizi consulenza..." />
                </Form.Item>
              </Col>
              
              <Col span={24}>
                <Form.Item
                  name="descrizione"
                  label="Descrizione"
                >
                  <TextArea rows={2} placeholder="Descrizione aggiuntiva del documento..." />
                </Form.Item>
              </Col>
              
              <Col span={24}>
                <Form.Item
                  name="note"
                  label="Note"
                >
                  <TextArea rows={2} placeholder="Note interne..." />
                </Form.Item>
              </Col>
            </Row>

            <Divider />

            {/* Righe documento */}
            <Row gutter={[16, 16]}>
              <Col span={24}>
                <Title level={4}>📝 Righe Documento</Title>
              </Col>
              
              <Col span={24}>
                <Form.List name="righe">
                  {(fields, { add, remove }) => (
                    <>
                      {fields.map(({ key, name, ...restField }) => (
                        <Card 
                          key={key} 
                          size="small" 
                          style={{ marginBottom: 16 }}
                          title={`Riga ${name + 1}`}
                          extra={
                            fields.length > 1 && (
                              <Button
                                type="text"
                                danger
                                icon={<MinusCircleOutlined />}
                                onClick={() => remove(name)}
                              />
                            )
                          }
                        >
                          <Row gutter={[8, 8]}>
                            <Col xs={24} sm={12} md={8}>
                              <Form.Item
                                {...restField}
                                name={[name, 'descrizione']}
                                label="Descrizione"
                                rules={[{ required: true, message: 'Inserisci descrizione' }]}
                              >
                                <Input placeholder="Descrizione articolo/servizio" />
                              </Form.Item>
                            </Col>
                            
                            <Col xs={12} sm={6} md={4}>
                              <Form.Item
                                {...restField}
                                name={[name, 'quantita']}
                                label="Quantità"
                                rules={[{ required: true, message: 'Quantità' }]}
                              >
                                <InputNumber min={0} step={0.01} style={{ width: '100%' }} />
                              </Form.Item>
                            </Col>
                            
                            <Col xs={12} sm={6} md={4}>
                              <Form.Item
                                {...restField}
                                name={[name, 'prezzoUnitario']}
                                label="Prezzo"
                                rules={[{ required: true, message: 'Prezzo' }]}
                              >
                                <InputNumber 
                                  min={0} 
                                  step={0.01} 
                                  style={{ width: '100%' }}
                                  addonAfter="€"
                                />
                              </Form.Item>
                            </Col>
                            
                            <Col xs={12} sm={6} md={4}>
                              <Form.Item
                                {...restField}
                                name={[name, 'scontoPercentuale']}
                                label="Sconto %"
                              >
                                <InputNumber 
                                  min={0} 
                                  max={100} 
                                  step={0.01} 
                                  style={{ width: '100%' }}
                                  addonAfter="%"
                                />
                              </Form.Item>
                            </Col>
                            
                            <Col xs={12} sm={6} md={4}>
                              <Form.Item
                                {...restField}
                                name={[name, 'aliquotaIva']}
                                label="IVA %"
                                rules={[{ required: true, message: 'IVA' }]}
                              >
                                <Select>
                                  {Object.entries(AliquotaIva).map(([key, value]) => (
                                    <Option key={key} value={value}>
                                      {value}%
                                    </Option>
                                  ))}
                                </Select>
                              </Form.Item>
                            </Col>
                          </Row>
                        </Card>
                      ))}
                      
                      <Button
                        type="dashed"
                        onClick={() => add({
                          descrizione: '',
                          quantita: 1,
                          prezzoUnitario: 0,
                          scontoPercentuale: 0,
                          aliquotaIva: AliquotaIva.ORDINARIA_22
                        })}
                        block
                        icon={<PlusOutlined />}
                      >
                        Aggiungi Riga
                      </Button>
                    </>
                  )}
                </Form.List>
              </Col>
            </Row>

            <Divider />

            {/* Scadenze */}
            <Row gutter={[16, 16]}>
              <Col span={24}>
                <Title level={4}>💰 Scadenze di Pagamento</Title>
              </Col>
              
              <Col span={24}>
                <Form.List name="scadenze">
                  {(fields, { add, remove }) => (
                    <>
                      {fields.map(({ key, name, ...restField }) => (
                        <Card 
                          key={key} 
                          size="small" 
                          style={{ marginBottom: 16 }}
                          title={`Scadenza ${name + 1}`}
                          extra={
                            fields.length > 1 && (
                              <Button
                                type="text"
                                danger
                                icon={<MinusCircleOutlined />}
                                onClick={() => remove(name)}
                              />
                            )
                          }
                        >
                          <Row gutter={[8, 8]}>
                            <Col xs={24} sm={8}>
                              <Form.Item
                                {...restField}
                                name={[name, 'dataScadenza']}
                                label="Data Scadenza"
                                rules={[{ required: true, message: 'Data scadenza richiesta' }]}
                              >
                                <Input type="date" />
                              </Form.Item>
                            </Col>
                            
                            <Col xs={24} sm={8}>
                              <Form.Item
                                {...restField}
                                name={[name, 'importo']}
                                label="Importo"
                                rules={[{ required: true, message: 'Importo richiesto' }]}
                              >
                                <InputNumber 
                                  min={0} 
                                  step={0.01} 
                                  style={{ width: '100%' }}
                                  addonAfter="€"
                                />
                              </Form.Item>
                            </Col>
                            
                            <Col xs={24} sm={8}>
                              <Form.Item
                                {...restField}
                                name={[name, 'note']}
                                label="Note"
                              >
                                <Input placeholder="Note per questa scadenza" />
                              </Form.Item>
                            </Col>
                          </Row>
                        </Card>
                      ))}
                      
                      <Button
                        type="dashed"
                        onClick={() => add({
                          dataScadenza: '',
                          importo: 0,
                          note: ''
                        })}
                        block
                        icon={<PlusOutlined />}
                      >
                        Aggiungi Scadenza
                      </Button>
                    </>
                  )}
                </Form.List>
              </Col>
            </Row>

            {/* Campi specifici per tipo documento */}
            <Form.Item
              shouldUpdate={(prevValues, currentValues) => 
                prevValues.tipoDocumento !== currentValues.tipoDocumento
              }
            >
              {({ getFieldValue }) => {
                const tipoDocumento = getFieldValue('tipoDocumento');
                
                if (tipoDocumento === TipoDocumento.NOTA_CREDITO || tipoDocumento === TipoDocumento.NOTA_DEBITO) {
                  return (
                    <>
                      <Divider />
                      <Row gutter={[16, 16]}>
                        <Col span={24}>
                          <Title level={4}>📋 Informazioni Nota</Title>
                        </Col>
                        <Col xs={24} sm={12}>
                          <Form.Item
                            name="motivoNota"
                            label="Motivo della Nota"
                          >
                            <TextArea rows={2} placeholder="Motivo della nota di credito/debito" />
                          </Form.Item>
                        </Col>
                        <Col xs={24} sm={12}>
                          <Form.Item
                            name="documentoOrigineId"
                            label="Documento di Origine"
                          >
                            <InputNumber style={{ width: '100%' }} placeholder="ID documento origine" />
                          </Form.Item>
                        </Col>
                      </Row>
                    </>
                  );
                }
                
                if (tipoDocumento === TipoDocumento.DDT) {
                  return (
                    <>
                      <Divider />
                      <Row gutter={[16, 16]}>
                        <Col span={24}>
                          <Title level={4}>🚚 Informazioni Trasporto</Title>
                        </Col>
                        <Col xs={24} sm={12}>
                          <Form.Item
                            name="causaleDescrizione"
                            label="Causale del Trasporto"
                          >
                            <Input placeholder="es: Vendita" />
                          </Form.Item>
                        </Col>
                        <Col xs={24} sm={12}>
                          <Form.Item
                            name="dataTrasporto"
                            label="Data Trasporto"
                          >
                            <Input type="date" />
                          </Form.Item>
                        </Col>
                      </Row>
                    </>
                  );
                }
                
                if (tipoDocumento === TipoDocumento.PREVENTIVO) {
                  return (
                    <>
                      <Divider />
                      <Row gutter={[16, 16]}>
                        <Col span={24}>
                          <Title level={4}>⏰ Validità Offerta</Title>
                        </Col>
                        <Col xs={24} sm={12}>
                          <Form.Item
                            name="validitaOfferta"
                            label="Validità Offerta"
                          >
                            <Input placeholder="es: 30 giorni" />
                          </Form.Item>
                        </Col>
                      </Row>
                    </>
                  );
                }
                
                if (tipoDocumento === TipoDocumento.FATTURA_ELETTRONICA) {
                  return (
                    <>
                      <Divider />
                      <Row gutter={[16, 16]}>
                        <Col span={24}>
                          <Title level={4}>📧 Fatturazione Elettronica</Title>
                        </Col>
                        <Col xs={24} sm={12}>
                          <Form.Item
                            name="codiceDestinatario"
                            label="Codice Destinatario"
                          >
                            <Input placeholder="es: 0000000" />
                          </Form.Item>
                        </Col>
                        <Col xs={24} sm={12}>
                          <Form.Item
                            name="pecDestinatario"
                            label="PEC Destinatario"
                          >
                            <Input type="email" placeholder="pec@esempio.it" />
                          </Form.Item>
                        </Col>
                      </Row>
                    </>
                  );
                }
                
                return null;
              }}
            </Form.Item>

            <Divider />

            {/* Azioni */}
            <Row justify="end" gutter={[8, 8]}>
              <Col>
                <Button onClick={() => setIsModalVisible(false)}>
                  Annulla
                </Button>
              </Col>
              <Col>
                <Button 
                  type="primary" 
                  htmlType="submit" 
                  loading={isCreating}
                  icon={<PlusOutlined />}
                >
                  Crea Documento
                </Button>
              </Col>
            </Row>
          </Form>
        </Modal>

        <Tabs items={tabItems} />
      </Card>
    </div>
  );
};

export default DocumentiManagement;
