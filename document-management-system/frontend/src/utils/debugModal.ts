// Debug utility per modal
export const debugModal = () => {
  console.log("🔍 DEBUG MODAL STATE:");
  
  // Controlla tutti i modal nel DOM
  const modals = document.querySelectorAll('.ant-modal-wrap');
  console.log(`🔍 Trovati ${modals.length} modal wrap nel DOM`);
  
  modals.forEach((modal, index) => {
    const computedStyle = window.getComputedStyle(modal);
    console.log(`🔍 Modal ${index}:`, {
      display: computedStyle.display,
      visibility: computedStyle.visibility,
      opacity: computedStyle.opacity,
      zIndex: computedStyle.zIndex,
      position: computedStyle.position,
      element: modal
    });
  });
  
  // Controlla tutti i drawer nel DOM
  const drawers = document.querySelectorAll('.ant-drawer');
  console.log(`🔍 Trovati ${drawers.length} drawer nel DOM`);
  
  drawers.forEach((drawer, index) => {
    const computedStyle = window.getComputedStyle(drawer);
    console.log(`🔍 Drawer ${index}:`, {
      display: computedStyle.display,
      visibility: computedStyle.visibility,
      opacity: computedStyle.opacity,
      zIndex: computedStyle.zIndex,
      position: computedStyle.position,
      element: drawer
    });
  });
  
  // Controlla il container root
  const root = document.getElementById('root');
  if (root) {
    const rootStyle = window.getComputedStyle(root);
    console.log(`🔍 Root container:`, {
      position: rootStyle.position,
      zIndex: rootStyle.zIndex,
      overflow: rootStyle.overflow,
      element: root
    });
  }
};

// Esporta per l'uso globale
(window as any).debugModal = debugModal;
