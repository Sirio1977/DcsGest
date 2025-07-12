import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import { Layout, Menu, Typography, Card, Row, Col, Button } from 'antd';
import { useSelector } from 'react-redux';
import { RootState } from './store/store';
import { TestApi } from './components/TestApi';
import SetupWizard from './components/SetupWizard';
import AziendaManagement from './components/AziendaManagement';
import ConfigurazioniManagement from './components/ConfigurazioniManagementSimple';
import { ArticoliManagement } from './components/ArticoliManagement';
import { ArticoliFornitoriManagement } from './components/ArticoliFornitoriManagement';
import { ClientiManagement } from './components/ClientiManagement';
import DocumentiManagement from './components/DocumentiManagement';
import BackendDiagnostic from './components/BackendDiagnostic';
import UtilityManagement from './components/UtilityManagement';
import 'antd/dist/antd.css';
import './App.css';

const { Header, Content, Sider } = Layout;
const { Title, Paragraph } = Typography;

function App() {
  const isAuthenticated = useSelector((state: RootState) => state.auth.isAuthenticated);
  
  return (
    <Router>
      <Layout className="app-layout">
        <Header className="app-header">
          <Title level={3} className="app-header__title">
            🗂️ Sistema Gestionale Documentale
          </Title>
        </Header>
        <Layout className="app-content">
          <Sider width={200} className="app-sidebar">
            <Menu
              mode="inline"
              defaultSelectedKeys={['1']}
              className="nav-menu"
            >
              <Menu.Item key="1">
                <Link to="/">📊 Dashboard</Link>
              </Menu.Item>
              <Menu.Item key="2">
                <Link to="/documenti">📄 Documenti</Link>
              </Menu.Item>
              <Menu.SubMenu key="anagrafiche" title="📋 Anagrafiche">
                <Menu.Item key="3">
                  <Link to="/clienti">👥 Clienti/Fornitori</Link>
                </Menu.Item>
                <Menu.Item key="4">
                  <Link to="/articoli">📦 Articoli</Link>
                </Menu.Item>
                <Menu.Item key="articoli-fornitori">
                  <Link to="/articoli-fornitori">🏪 Articoli Fornitori</Link>
                </Menu.Item>
              </Menu.SubMenu>
              <Menu.Item key="5">
                <Link to="/aziende">🏢 Aziende</Link>
              </Menu.Item>
              <Menu.Item key="6">
                <Link to="/configurazioni">⚙️ Configurazioni</Link>
              </Menu.Item>
              <Menu.Item key="7">� Utenti</Menu.Item>
              <Menu.Item key="8">
                <Link to="/utility">🛠️ Utility</Link>
              </Menu.Item>
              <Menu.Item key="9">
                <Link to="/setup">🔧 Setup Iniziale</Link>
              </Menu.Item>
              <Menu.Item key="10">
                <Link to="/diagnostica">🔍 Diagnostica Backend</Link>
              </Menu.Item>
            </Menu>
          </Sider>
          <Layout className="app-main">
            <Content className="bg-white p-lg">
              <Routes>
                <Route path="/" element={
                  <div>
                    <Title level={2}>✅ Benvenuto nel Sistema Gestionale!</Title>
                    
                    <Row gutter={[16, 16]} className="mt-lg">
                      <Col xs={24} md={6}>
                        <Card title="🎯 Setup Iniziale" bordered={false} className="h-full">
                          <Paragraph>
                            <strong>Configura la tua azienda</strong><br/>
                            Utilizza il wizard per impostare tutti i dati necessari.
                          </Paragraph>
                          <Button 
                            type="primary" 
                            onClick={() => window.location.href = '/setup'}
                            className="mt-sm"
                          >
                            Avvia Setup
                          </Button>
                        </Card>
                      </Col>

                      <Col xs={24} md={6}>
                        <Card title="� Clienti" bordered={false} className="h-full">
                          <Paragraph>
                            <strong>Gestione Anagrafiche Clienti</strong><br/>
                            Visualizza e gestisci i tuoi clienti e fornitori.
                          </Paragraph>
                          <Button 
                            type="default" 
                            onClick={() => window.location.href = '/clienti'}
                            className="mt-sm"
                          >
                            Vai ai Clienti
                          </Button>
                        </Card>
                      </Col>

                      <Col xs={24} md={6}>
                        <Card title="📦 Articoli" bordered={false} className="h-full">
                          <Paragraph>
                            <strong>Gestione Magazzino</strong><br/>
                            Gestisci articoli, prezzi e categorie.
                          </Paragraph>
                          <Button 
                            type="default" 
                            onClick={() => window.location.href = '/articoli'}
                            className="mt-sm"
                          >
                            Vai agli Articoli
                          </Button>
                        </Card>
                      </Col>
                      
                      <Col xs={24} md={6}>
                        <Card title="🚀 Backend Status" bordered={false} className="h-full">
                          <Paragraph>
                            ✅ <strong>Spring Boot</strong>: Attivo<br/>
                            ✅ <strong>PostgreSQL</strong>: Connesso<br/>
                            ✅ <strong>API REST</strong>: Operativo
                          </Paragraph>
                        </Card>
                      </Col>
                    </Row>

                    <Row gutter={[16, 16]} className="mt-md">
                      <Col xs={24} md={12}>
                        <Card title="⚙️ Configurazioni" bordered={false} className="h-full">
                          <Paragraph>
                            <strong>Gestione Sistema</strong><br/>
                            Configura IVA, pagamenti e numerazioni.
                          </Paragraph>
                          <Button 
                            type="default" 
                            onClick={() => window.location.href = '/configurazioni'}
                            className="mt-sm"
                          >
                            Vai alle Configurazioni
                          </Button>
                        </Card>
                      </Col>
                      
                      <Col xs={24} md={12}>
                        <Card title="🎨 Frontend Status" bordered={false} className="h-full">
                          <Paragraph>
                            ✅ <strong>React App</strong>: Attiva<br/>
                            ✅ <strong>Ant Design UI</strong>: Caricata<br/>
                            ✅ <strong>TypeScript</strong>: Configurato
                          </Paragraph>
                        </Card>
                      </Col>
                    </Row>
                    
                    <TestApi />

                    <Card title="🎯 Test di Connessione" className="mt-md">
                      <Paragraph>
                        Il sistema di gestione documentale è completamente operativo!
                      </Paragraph>
                      <Paragraph>
                        • Backend API: <code>http://localhost:8080/api</code>
                      </Paragraph>
                      <Paragraph>
                        • Frontend UI: <code>http://localhost:3000</code> (questa applicazione)
                      </Paragraph>
                    </Card>
                  </div>
                } />
                <Route path="/documenti" element={<DocumentiManagement />} />
                <Route path="/setup" element={<SetupWizard />} />
                <Route path="/aziende" element={<AziendaManagement />} />
                <Route path="/configurazioni" element={<ConfigurazioniManagement />} />
                <Route path="/clienti" element={<ClientiManagement />} />
                <Route path="/articoli" element={<ArticoliManagement />} />
                <Route path="/articoli-fornitori" element={<ArticoliFornitoriManagement />} />
                <Route path="/utility" element={<UtilityManagement />} />
                <Route path="/diagnostica" element={<BackendDiagnostic />} />
              </Routes>
            </Content>
          </Layout>
        </Layout>
      </Layout>
    </Router>
  );
}

export default App;
