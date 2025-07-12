import React, { useState } from 'react';
import { Modal, Button, Card, Space } from 'antd';

const TestModal: React.FC = () => {
  const [isModalVisible, setIsModalVisible] = useState(false);

  const showModal = () => {
    console.log("🔥 TEST: showModal called");
    
    // Debug DOM prima del cambiamento
    setTimeout(() => {
      const modals = document.querySelectorAll('.ant-modal-wrap');
      console.log(`🔍 TEST Modals nel DOM prima: ${modals.length}`);
    }, 100);
    
    setIsModalVisible(true);
    
    // Debug DOM dopo il cambiamento
    setTimeout(() => {
      const modals = document.querySelectorAll('.ant-modal-wrap');
      console.log(`🔍 TEST Modals nel DOM dopo: ${modals.length}`);
      modals.forEach((modal, index) => {
        const style = window.getComputedStyle(modal);
        console.log(`🔍 TEST Modal ${index}:`, {
          display: style.display,
          visibility: style.visibility,
          opacity: style.opacity,
          zIndex: style.zIndex
        });
      });
    }, 500);
  };

  const handleCancel = () => {
    console.log("🔥 TEST: handleCancel called");
    setIsModalVisible(false);
  };

  console.log("🔥 TEST: Component render, isModalVisible:", isModalVisible);

  return (
    <Card title="Test Modal Component" style={{ margin: '20px' }}>
      <Space>
        <Button type="primary" onClick={showModal}>
          Apri Modal Test
        </Button>
      </Space>

      {console.log("🔥 TEST: About to render Modal with open =", isModalVisible)}
      <Modal
        title="Modal di Test"
        open={isModalVisible}
        onCancel={handleCancel}
        footer={[
          <Button key="close" onClick={handleCancel}>
            Chiudi
          </Button>
        ]}
        forceRender={true}
        getContainer={() => document.body}
        zIndex={1000}
      >
        <p>Questo è un modal di test per verificare il funzionamento.</p>
        <p>Se vedi questo messaggio, il modal funziona correttamente!</p>
      </Modal>
    </Card>
  );
};

export default TestModal;
