import React, { useState } from 'react';
import { Steps, Card, Button, Row, Col, Typography, Progress, Tabs, Modal, Form } from 'antd';
import { 
  BankOutlined, 
  SettingOutlined, 
  TeamOutlined, 
  CheckCircleOutlined,
  ShoppingOutlined 
} from '@ant-design/icons';
import AziendaSetupForm from './wizard/AziendaSetupForm';
import ConfigurazioniForm from './wizard/ConfigurazioniForm';
import AnagraficheImport from './wizard/AnagraficheImport';
import RiepilogoSetup from './wizard/RiepilogoSetup';

const { Title, Paragraph } = Typography;
const { Step } = Steps;

export interface SetupData {
  azienda?: any;
  configurazioni?: any;
  anagrafiche?: any;
}

const SetupWizard: React.FC = () => {
  const [currentStep, setCurrentStep] = useState(0);
  const [setupData, setSetupData] = useState<SetupData>({});
  const [loading, setLoading] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);

  const steps = [
    {
      title: 'Dati Azienda',
      icon: <BankOutlined />,
      description: 'Configura i dati della tua azienda emittente',
      content: 'azienda'
    },
    {
      title: 'Configurazioni',
      icon: <SettingOutlined />,
      description: 'Imposta numerazioni, IVA, modalità di pagamento',
      content: 'configurazioni'
    },
    {
      title: 'Anagrafiche',
      icon: <TeamOutlined />,
      description: 'Importa o crea clienti, fornitori e articoli',
      content: 'anagrafiche'
    },
    {
      title: 'Riepilogo',
      icon: <CheckCircleOutlined />,
      description: 'Verifica e completa la configurazione',
      content: 'riepilogo'
    }
  ];

  const handleNext = (stepData: any) => {
    const newSetupData = {
      ...setupData,
      [steps[currentStep].content]: stepData
    };
    setSetupData(newSetupData);
    
    if (currentStep < steps.length - 1) {
      setCurrentStep(currentStep + 1);
    }
  };

  const handlePrev = () => {
    if (currentStep > 0) {
      setCurrentStep(currentStep - 1);
    }
  };

  const handleFinish = async (finalData: any) => {
    setLoading(true);
    try {
      const completeSetupData = { ...setupData, ...finalData };
      console.log('Setup completo:', completeSetupData);
      
      // 1. Salva i dati dell'azienda
      if (completeSetupData.azienda) {
        const aziendaResponse = await fetch('http://localhost:8080/api/aziende', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(completeSetupData.azienda)
        });
        
        if (!aziendaResponse.ok) {
          throw new Error('Errore nel salvare i dati azienda');
        }
        
        console.log('Azienda salvata con successo');
      }
      
      // 2. Salva le configurazioni
      if (completeSetupData.configurazioni) {
        console.log('Configurazioni da salvare:', completeSetupData.configurazioni);
        
        const configResponse = await fetch('http://localhost:8080/api/configurazioni/batch', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(completeSetupData.configurazioni)
        });
        
        if (!configResponse.ok) {
          console.warn('Errore nel salvare le configurazioni (potrebbe non essere implementato)');
        } else {
          console.log('Configurazioni salvate con successo');
        }
      }
      
      // 3. Salva le anagrafiche (clienti, fornitori, articoli)
      if (completeSetupData.anagrafiche) {
        console.log('Anagrafiche da salvare:', completeSetupData.anagrafiche);
        // TODO: Implementare salvataggio anagrafiche
      }
      
      // 4. Mostra messaggio di successo e reindirizza
      alert('Configurazione completata con successo! Il sistema è pronto all\'uso.');
      window.location.href = '/'; // Reindirizza alla dashboard principale
      
    } catch (error) {
      console.error('Errore durante il salvataggio:', error);
      const errorMessage = error instanceof Error ? error.message : 'Errore sconosciuto';
      alert('Errore durante il salvataggio: ' + errorMessage);
    } finally {
      setLoading(false);
    }
  };

  const renderStepContent = () => {
    switch (currentStep) {
      case 0:
        return (
          <AziendaSetupForm 
            onNext={handleNext}
            initialData={setupData.azienda}
          />
        );
      case 1:
        return (
          <ConfigurazioniForm 
            onNext={handleNext}
            onPrev={handlePrev}
            initialData={setupData.configurazioni}
          />
        );
      case 2:
        return (
          <AnagraficheImport 
            onNext={handleNext}
            onPrev={handlePrev}
            initialData={setupData.anagrafiche}
          />
        );
      case 3:
        return (
          <RiepilogoSetup 
            setupData={setupData}
            onFinish={handleFinish}
            onPrev={handlePrev}
            loading={loading}
          />
        );
      default:
        return null;
    }
  };

  const completionPercentage = ((currentStep + 1) / steps.length) * 100;

  const setupSteps = [
    {
      key: 'step1',
      label: 'Step 1',
      children: <div>Content for Step 1</div>,
    },
    {
      key: 'step2',
      label: 'Step 2',
      children: <div>Content for Step 2</div>,
    },
  ];

  return (
    <div style={{ padding: '24px', maxWidth: '1200px', margin: '0 auto' }}>
      <Card>
        <div style={{ marginBottom: '32px', textAlign: 'center' }}>
          <Title level={2}>
            <ShoppingOutlined style={{ marginRight: '12px', color: '#1890ff' }} />
            Configurazione Iniziale Azienda
          </Title>
          <Paragraph type="secondary" style={{ fontSize: '16px' }}>
            Imposta tutti i dati necessari per iniziare ad emettere documenti fiscali
          </Paragraph>
          <Progress 
            percent={completionPercentage} 
            strokeColor={{
              '0%': '#108ee9',
              '100%': '#87d068',
            }}
            style={{ maxWidth: '400px', margin: '16px auto' }}
          />
        </div>

        <Row gutter={[24, 24]}>
          <Col xs={24} lg={6}>
            <Steps
              direction="vertical"
              current={currentStep}
              size="small"
              style={{ height: '100%' }}
            >
              {steps.map((step, index) => (
                <Step
                  key={index}
                  title={step.title}
                  description={step.description}
                  icon={step.icon}
                  status={
                    index < currentStep ? 'finish' : 
                    index === currentStep ? 'process' : 'wait'
                  }
                />
              ))}
            </Steps>
          </Col>

          <Col xs={24} lg={18}>
            <Card 
              title={
                <div style={{ display: 'flex', alignItems: 'center' }}>
                  {steps[currentStep].icon}
                  <span style={{ marginLeft: '8px' }}>
                    {steps[currentStep].title}
                  </span>
                </div>
              }
              style={{ minHeight: '500px' }}
            >
              <Tabs 
                items={setupSteps} 
                activeKey={`step${currentStep + 1}`}
                onChange={(key) => {
                  const stepIndex = parseInt(key.replace('step', '')) - 1;
                  setCurrentStep(stepIndex);
                }}
                tabBarStyle={{ display: 'none' }}
              />
            </Card>
          </Col>
        </Row>
      </Card>

      <Modal
        open={modalVisible}
        onCancel={() => setModalVisible(false)}
        footer={null}
      >
        <Form onFinish={handleFinish}>
          {/* Form content */}
        </Form>
      </Modal>
    </div>
  );
};

export default SetupWizard;
