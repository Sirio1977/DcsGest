import React from 'react';
import { Card, Button, Typography, Alert } from 'antd';

const { Title, Paragraph } = Typography;

interface Props {
  children: React.ReactNode;
}

interface State {
  hasError: boolean;
  error: Error | null;
  errorInfo: React.ErrorInfo | null;
}

export class ErrorBoundary extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = { 
      hasError: false, 
      error: null, 
      errorInfo: null 
    };
  }

  static getDerivedStateFromError(error: Error): State {
    return { 
      hasError: true, 
      error, 
      errorInfo: null 
    };
  }

  componentDidCatch(error: Error, errorInfo: React.ErrorInfo) {
    console.error('Error Boundary catturato:', error, errorInfo);
    this.setState({
      error,
      errorInfo
    });
  }

  handleReload = () => {
    window.location.reload();
  };

  render() {
    if (this.state.hasError) {
      return (
        <Card style={{ margin: '24px' }}>
          <Alert
            message="Errore dell'Applicazione"
            description="Si è verificato un errore inaspettato."
            type="error"
            showIcon
            style={{ marginBottom: '16px' }}
          />
          
          <Title level={4}>Qualcosa è andato storto</Title>
          <Paragraph>
            Si è verificato un errore durante il caricamento di questa pagina.
          </Paragraph>
          
          <div style={{ marginBottom: '16px' }}>
            <Button type="primary" onClick={this.handleReload}>
              Ricarica Pagina
            </Button>
          </div>
          
          {process.env.NODE_ENV === 'development' && (
            <details style={{ whiteSpace: 'pre-wrap', fontSize: '12px', background: '#f5f5f5', padding: '8px', borderRadius: '4px' }}>
              <summary>Dettagli Errore (solo in sviluppo)</summary>
              <p><strong>Errore:</strong> {this.state.error?.toString()}</p>
              <p><strong>Stack:</strong> {this.state.error?.stack}</p>
              <p><strong>Component Stack:</strong> {this.state.errorInfo?.componentStack}</p>
            </details>
          )}
        </Card>
      );
    }

    return this.props.children;
  }
}
