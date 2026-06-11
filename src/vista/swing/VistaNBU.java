package vista.swing;

import vista.interfaces.IVistaNBU;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import modelo.Determinacion;
import presentador.NBUPresenter;

public class VistaNBU extends JPanel implements IVistaNBU {

    private NBUPresenter presenter;
    private boolean actualizandoVista = false; // ← PREVIENE BUCLE DE EVENTOS

    // ── Paleta BIOTEC Minimalista ────────────────────────────────────
    private final Color C_NAVY         = new Color(10, 25, 47);
    private final Color C_FONDO        = new Color(238, 242, 246);
    private final Color C_BLANCO       = Color.WHITE;
    private final Color C_TEXTO_FUERTE = new Color(40, 50, 60);
    private final Color C_TEXTO_SUAVE  = new Color(100, 115, 130);
    private final Color C_BORDE        = new Color(215, 225, 235);
    private final Color C_AZUL_MEDIO   = new Color(30, 110, 180);
    private final Color C_VERDE        = new Color(35, 160, 115);
    private final Color C_ROJO         = new Color(220, 53, 69);
    private final Color C_CABECERA_TBL = new Color(245, 248, 252);
    private final Color C_FILA_PAR     = new Color(252, 254, 255);
    private final Color C_HEADER_TEXT  = new Color(175, 205, 235);
    private final Color C_CAMPO        = new Color(250, 252, 254);
    private final Color C_SELECCION    = new Color(210, 232, 250);
    
    private final Font F_TBL_HEADER = new Font("Segoe UI", Font.BOLD, 13);
    private final Font F_TBL_CELL   = new Font("Segoe UI", Font.PLAIN, 14);

    public VistaNBU() {
        initComponents();
        aplicarEsteticaProfesional();
    }

    // ══════════════════════════════════════════════════════════════════
    //  INTERFAZ IVistaNBU - MÉTODOS DE CONEXIÓN
    // ══════════════════════════════════════════════════════════════════
    @Override public void ejecutar() { setVisible(true); }

    @Override
    public void setPresenter(NBUPresenter presenter) {
        this.presenter = presenter;
        
        limpiarListeners(btnVolver);
        limpiarListeners(btnAgregarHijo);
        limpiarListeners(btnQuitarHijo);
        limpiarListeners(btnSubirHijo);
        limpiarListeners(btnBajarHijo);
        limpiarListeners(btnSubirPadre);
        limpiarListeners(btnBajarPadre);
        
        btnVolver.addActionListener(e -> presenter.onVolver());
        btnAgregarHijo.addActionListener(e -> presenter.onAgregarHijo());
        btnQuitarHijo.addActionListener(e -> presenter.onQuitarHijo());
        btnSubirHijo.addActionListener(e -> presenter.onSubirHijo());
        btnBajarHijo.addActionListener(e -> presenter.onBajarHijo());
        btnSubirPadre.addActionListener(e -> presenter.onSubirPadre());
        btnBajarPadre.addActionListener(e -> presenter.onBajarPadre());

        for (java.awt.event.KeyListener kl : txtBuscarNBU.getKeyListeners()) {
            txtBuscarNBU.removeKeyListener(kl);
        }
        txtBuscarNBU.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyReleased(java.awt.event.KeyEvent e) {
                if (!actualizandoVista) presenter.onBuscarNBU();
            }
        });

        // ⭐⭐⭐ EVENTO DE SELECCIÓN CORREGIDO ⭐⭐⭐
        grillaNBU.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && !actualizandoVista && grillaNBU.getSelectedRow() != -1) {
                String nombrePadre = grillaNBU.getValueAt(grillaNBU.getSelectedRow(), 2).toString();
                lblPadreSeleccionado.setText(nombrePadre);
                detenerEdicionTabla(); 
                presenter.onSeleccionarPadre();
            }
        });
        
        // ⭐⭐⭐ EVENTO DE SELECCIÓN PARA TABLA DE HIJOS - SOLO HABILITAR BOTONES ⭐⭐⭐
        grillaHijos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && !actualizandoVista) {
                boolean haySeleccion = grillaHijos.getSelectedRow() != -1;
                btnQuitarHijo.setEnabled(haySeleccion);
                btnSubirHijo.setEnabled(haySeleccion);
                btnBajarHijo.setEnabled(haySeleccion);
            }
        });
    }

    private void limpiarListeners(JButton btn) {
        for (java.awt.event.ActionListener al : btn.getActionListeners()) {
            btn.removeActionListener(al);
        }
    }

    @Override public boolean hayCambiosPendientes() { return false; }
    @Override public int confirmarSalidaConGuardado() { return 1; }

    @Override public void limpiarFocos() { this.requestFocusInWindow(); }

    @Override
    public int confirmarAccion(String mensaje, String titulo) {
        return JOptionPane.showConfirmDialog(this, mensaje, titulo, JOptionPane.YES_NO_OPTION);
    }

    @Override
    public String pedirNombreNuevoComponente() {
        javax.swing.JTextField txtNombre = new javax.swing.JTextField();
        txtNombre.addAncestorListener(new javax.swing.event.AncestorListener() {
            @Override public void ancestorAdded(javax.swing.event.AncestorEvent event) {
                java.awt.Window window = javax.swing.SwingUtilities.getWindowAncestor(txtNombre);
                if (window != null) {
                    window.addWindowFocusListener(new java.awt.event.WindowAdapter() {
                        @Override public void windowGainedFocus(java.awt.event.WindowEvent e) { txtNombre.requestFocusInWindow(); }
                    });
                }
            }
            @Override public void ancestorRemoved(javax.swing.event.AncestorEvent event) {}
            @Override public void ancestorMoved(javax.swing.event.AncestorEvent event) {}
        });

        javax.swing.JLabel lblTip = new javax.swing.JLabel("<html><small style='color:gray;'>Tip: Para crear un título/separador visual, escríbalo entre guiones.<br>Ejemplo: <b>--- FÓRMULA ---</b></small></html>");
        Object[] mensaje = { "Nombre del nuevo componente:", txtNombre, " ", lblTip };

        int opcion = JOptionPane.showConfirmDialog(this, mensaje, "Agregar Nuevo Componente", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        return (opcion == JOptionPane.OK_OPTION) ? txtNombre.getText().trim() : null;
    }

    @Override public void mostrarMensaje(String mensaje) { JOptionPane.showMessageDialog(this, mensaje); }
    @Override public String getBusqueda()                { return txtBuscarNBU.getText().trim(); }
    @Override public int getCantidadFilas()              { return grillaHijos.getRowCount(); }
    @Override public int getIdDeterminacion(int fila)    { return 0; }
    
    @Override
    public String getCodigoHijoFila(int fila) {
        return grillaHijos.getModel().getValueAt(fila, 0).toString();
    }
    
    @Override
    public String getUnidad(int fila) {
        Object val = grillaHijos.getModel().getValueAt(fila, 3);
        return val != null ? val.toString() : "";
    }

    @Override
    public String getReferencia(int fila) {
        Object val = grillaHijos.getModel().getValueAt(fila, 4);
        return val != null ? val.toString() : "";
    }

    @Override
    public String getCodigoPadreSeleccionado() {
        int fila = grillaNBU.getSelectedRow();
        if (fila == -1) return null;
        return grillaNBU.getModel().getValueAt(fila, 1).toString();
    }
    
    @Override
    public String getCodigoHijoSeleccionado() {
        int fila = grillaHijos.getSelectedRow();
        if (fila == -1) return null;
        return grillaHijos.getModel().getValueAt(fila, 0).toString();
    }
    
    @Override
    public int getIndiceHijoSeleccionado() {
        return grillaHijos.getSelectedRow();
    }
    
    @Override
    public void seleccionarHijoPorIndice(int indice) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            if (indice >= 0 && indice < grillaHijos.getRowCount()) {
                actualizandoVista = true;
                grillaHijos.setRowSelectionInterval(indice, indice);
                actualizandoVista = false;
                grillaHijos.requestFocusInWindow();
            }
        });
    }

    @Override
    public void cargarDeterminaciones(List<Determinacion> lista) {
        actualizandoVista = true;
        
        int scrollVertical = jScrollPane1.getVerticalScrollBar().getValue();

        DefaultTableModel m = (DefaultTableModel) grillaNBU.getModel();
        m.setRowCount(0);
        
        int ordenVisual = 1;
        for (Determinacion d : lista) {
            boolean esSeparador = (d.getCodigo() == null || d.getCodigo().trim().isEmpty());
            m.addRow(new Object[]{
                esSeparador ? "" : ordenVisual++, 
                d.getCodigo(), 
                d.getNombre(), 
                esSeparador ? "" : d.getUb()
            });
        }
        m.fireTableDataChanged();
        
        javax.swing.SwingUtilities.invokeLater(() -> {
            jScrollPane1.getVerticalScrollBar().setValue(scrollVertical);
        });
        
        lblPadreSeleccionado.setText("Seleccione una práctica...");
        ((DefaultTableModel) grillaHijos.getModel()).setRowCount(0);
        
        grillaNBU.clearSelection();
        
        actualizandoVista = false;
    }

    @Override
    public void cargarHijos(List<Determinacion> listaHijos) {
        actualizandoVista = true;
        
        DefaultTableModel m = (DefaultTableModel) grillaHijos.getModel();
        m.setRowCount(0);
        for (Determinacion d : listaHijos) {
            m.addRow(new Object[]{
                d.getCodigo(), d.getPrioridad(), d.getNombre(), d.getUnidad(), d.getReferencia()
            });
        }
        m.fireTableDataChanged();
        
        grillaHijos.clearSelection();
        btnQuitarHijo.setEnabled(false);
        btnSubirHijo.setEnabled(false);
        btnBajarHijo.setEnabled(false);
        
        actualizandoVista = false;
    }

    @Override
    public void seleccionarFilaPorCodigo(String codigo) {
        actualizandoVista = true;
        for (int i = 0; i < grillaNBU.getRowCount(); i++) {
            Object valorCelda = grillaNBU.getModel().getValueAt(i, 1);
            if (valorCelda != null && valorCelda.toString().trim().equals(codigo)) {
                grillaNBU.setRowSelectionInterval(i, i);
                break;
            }
        }
        actualizandoVista = false;
    }

    @Override
    public int getCantidadFilasPadre() {
        return grillaNBU.getRowCount();
    }

    @Override
    public void detenerEdicionTabla() {
        if (grillaNBU.isEditing()) grillaNBU.getCellEditor().stopCellEditing();
        if (grillaHijos.isEditing()) grillaHijos.getCellEditor().stopCellEditing();
    }
    
    @Override
    public String getNombreHijoFila(int fila) {
        Object val = grillaHijos.getModel().getValueAt(fila, 2);
        return val != null ? val.toString() : "";
    }

    @Override
    public int getIndicePadreSeleccionado() {
        return grillaNBU.getSelectedRow();
    }

    @Override
    public String getCodigoPadreFila(int fila) {
        if (fila < 0 || fila >= grillaNBU.getRowCount()) {
            return "";
        }
        Object val = grillaNBU.getModel().getValueAt(fila, 1);
        return val != null ? val.toString() : "";
    }
    
    @Override
    public void seleccionarPadrePorIndice(int indice) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            if (indice >= 0 && indice < grillaNBU.getRowCount()) {
                actualizandoVista = true;
                grillaNBU.setRowSelectionInterval(indice, indice);
                grillaNBU.scrollRectToVisible(grillaNBU.getCellRect(indice, 0, true));
                actualizandoVista = false;
                grillaNBU.requestFocusInWindow();
            }
        });
    }

    // ════════════════════════════════════════════════════════════════
    //  RENDERER CORREGIDO - NO MODIFICA LA SELECCIÓN
    // ════════════════════════════════════════════════════════════════
    private class RendererTabla extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int col) {
            
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            
            String textoOriginal = value != null ? value.toString() : "";
            
            if (textoOriginal.contains(";")) {
                String[] lineas = textoOriginal.split(";");
                StringBuilder sb = new StringBuilder("<html>");
                for (int i = 0; i < lineas.length; i++) {
                    String lineaBlindada = lineas[i].trim()
                            .replace("<", "&lt;")
                            .replace(">", "&gt;");
                    sb.append(lineaBlindada);
                    if (i < lineas.length - 1) sb.append("<br>");
                }
                sb.append("</html>");
                setText(sb.toString());
                setToolTipText("<html><div style='padding:5px;'>" + textoOriginal.replace(";", "<br>") + "</div></html>");
            } else {
                setText(textoOriginal);
                setToolTipText(null);
            }
            
            // Alineación
            if (col == 0 || col == 1 || (table.getColumnCount() == 4 && col == 3) || (table.getColumnCount() == 5 && col == 3)) {
                setHorizontalAlignment(SwingConstants.CENTER);
            } else {
                setHorizontalAlignment(SwingConstants.LEFT);
            }
            
            // Detectar títulos
            Object nombreObj = table.getModel().getValueAt(row, table.getColumnCount() == 5 ? 2 : 2);
            boolean esTitulo = (nombreObj != null && 
                                nombreObj.toString().startsWith("---") && 
                                nombreObj.toString().endsWith("---"));
            
            // ⭐⭐⭐ CRUCIAL: NO MODIFICAR NADA si la fila está seleccionada ⭐⭐⭐
            // El JTable se encarga automáticamente de los colores de selección
            if (!isSelected) {
                if (esTitulo) {
                    setBackground(new Color(235, 242, 248));
                    setForeground(C_AZUL_MEDIO);
                    setFont(new Font("Segoe UI", Font.BOLD, 13));
                } else {
                    setBackground(row % 2 == 0 ? C_BLANCO : C_FILA_PAR);
                    setForeground(C_TEXTO_FUERTE);
                    setFont(F_TBL_CELL);
                }
                setBorder(new EmptyBorder(0, 10, 0, 10));
            } else {
                // Cuando está seleccionada, solo cambiamos la fuente a negrita
                setFont(new Font("Segoe UI", Font.BOLD, 13));
                // NO tocamos background/foreground - la tabla lo maneja
                setBorder(new EmptyBorder(0, 10, 0, 10));
            }
            
            return this;
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  ESTÉTICA Y UX
    // ════════════════════════════════════════════════════════════════
    private void aplicarEsteticaProfesional() {
        setBackground(C_FONDO);

        pnlHeader.setBackground(C_NAVY);
        pnlHeader.setBorder(new EmptyBorder(14, 28, 14, 28));
        lblTituloHeader.setForeground(C_BLANCO);
        lblTituloHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));

        estilizarCampoBuscador(txtBuscarNBU);
        configurarBotonRetroceso(btnVolver);
        configurarBotonAyuda(btnAyuda);

        pnlContenedorBlanco.setBackground(C_BLANCO);
        pnlContenedorBlanco.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 1, 1, 1, C_BORDE),
            new EmptyBorder(24, 28, 24, 28)
        ));
        pnlContenedorBlanco.setLayout(new BorderLayout());

        pnlCuerpo.setBackground(C_BLANCO);
        pnlCuerpo.setLayout(new GridBagLayout());

        estilizarContenedor(pnlTablaContainer);
        
        RendererTabla rendererMaestro = new RendererTabla();
        
        // ── GRÍLLA PADRES ──
        grillaNBU.setRowHeight(40);
        grillaNBU.setFont(F_TBL_CELL);
        grillaNBU.setShowHorizontalLines(true);
        grillaNBU.setShowVerticalLines(false);
        grillaNBU.setSelectionBackground(C_SELECCION);
        grillaNBU.setSelectionForeground(C_NAVY);
        grillaNBU.setIntercellSpacing(new Dimension(0, 1));
        grillaNBU.setBorder(BorderFactory.createEmptyBorder());
        grillaNBU.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        grillaNBU.setDefaultRenderer(Object.class, rendererMaestro);
        grillaNBU.setRowSelectionAllowed(true);
        grillaNBU.setColumnSelectionAllowed(false);

        grillaNBU.getTableHeader().setFont(F_TBL_HEADER);
        grillaNBU.getTableHeader().setBackground(C_CABECERA_TBL);
        grillaNBU.getTableHeader().setForeground(C_TEXTO_SUAVE);
        grillaNBU.getTableHeader().setPreferredSize(new Dimension(0, 40));
        grillaNBU.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDE));
        grillaNBU.getTableHeader().setReorderingAllowed(false);
        
        grillaNBU.getColumnModel().getColumn(0).setPreferredWidth(60);  
        grillaNBU.getColumnModel().getColumn(0).setMaxWidth(60);
        grillaNBU.getColumnModel().getColumn(0).setMinWidth(60);
        grillaNBU.getColumnModel().getColumn(0).setResizable(false);
        grillaNBU.getColumnModel().getColumn(1).setPreferredWidth(80);  
        grillaNBU.getColumnModel().getColumn(1).setMaxWidth(100);
        grillaNBU.getColumnModel().getColumn(1).setResizable(false);
        grillaNBU.getColumnModel().getColumn(2).setPreferredWidth(300); 
        grillaNBU.getColumnModel().getColumn(3).setPreferredWidth(60);  
        grillaNBU.getColumnModel().getColumn(3).setMaxWidth(80);
        grillaNBU.getColumnModel().getColumn(3).setResizable(false);

        JPanel pnlOrdenPadres = new JPanel();
        pnlOrdenPadres.setLayout(new BoxLayout(pnlOrdenPadres, BoxLayout.Y_AXIS));
        pnlOrdenPadres.setOpaque(false);
        pnlOrdenPadres.setBorder(new EmptyBorder(0, 12, 0, 0));
        pnlOrdenPadres.add(btnSubirPadre);
        pnlOrdenPadres.add(Box.createVerticalStrut(10));
        pnlOrdenPadres.add(btnBajarPadre);
        
        pnlTablaContainer.setLayout(new BorderLayout(0, 0));
        pnlTablaContainer.add(jScrollPane1, BorderLayout.CENTER);
        pnlTablaContainer.add(pnlOrdenPadres, BorderLayout.EAST);

        // ── GRÍLLA HIJOS ──
        estilizarContenedor(pnlDetalleHijos);
        pnlDetalleHijos.setLayout(new BorderLayout(10, 10));
        
        grillaHijos.setRowHeight(40);
        grillaHijos.setFont(F_TBL_CELL);
        grillaHijos.setShowHorizontalLines(true);
        grillaHijos.setShowVerticalLines(false);
        grillaHijos.setSelectionBackground(C_SELECCION);
        grillaHijos.setSelectionForeground(C_NAVY);
        grillaHijos.setIntercellSpacing(new Dimension(0, 1));
        grillaHijos.setBorder(BorderFactory.createEmptyBorder());
        grillaHijos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        grillaHijos.setDefaultRenderer(Object.class, rendererMaestro);
        grillaHijos.setRowSelectionAllowed(true);
        grillaHijos.setColumnSelectionAllowed(false);
        
        grillaHijos.getTableHeader().setFont(F_TBL_HEADER);
        grillaHijos.getTableHeader().setBackground(C_CABECERA_TBL);
        grillaHijos.getTableHeader().setForeground(C_TEXTO_SUAVE);
        grillaHijos.getTableHeader().setPreferredSize(new Dimension(0, 40));
        grillaHijos.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDE));
        grillaHijos.getTableHeader().setReorderingAllowed(false);
        
        grillaHijos.getColumnModel().getColumn(0).setMinWidth(0);
        grillaHijos.getColumnModel().getColumn(0).setMaxWidth(0);
        grillaHijos.getColumnModel().getColumn(0).setPreferredWidth(0);
        grillaHijos.getColumnModel().getColumn(0).setResizable(false);
        grillaHijos.getColumnModel().getColumn(1).setPreferredWidth(55);  
        grillaHijos.getColumnModel().getColumn(1).setMaxWidth(65);
        grillaHijos.getColumnModel().getColumn(1).setResizable(false);
        grillaHijos.getColumnModel().getColumn(2).setPreferredWidth(230); 
        grillaHijos.getColumnModel().getColumn(3).setPreferredWidth(120); 
        grillaHijos.getColumnModel().getColumn(3).setMaxWidth(160);
        
        grillaHijos.setCellSelectionEnabled(false);
        grillaHijos.setColumnSelectionAllowed(false);
        grillaHijos.setRowSelectionAllowed(true);

        lblTituloHijos.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTituloHijos.setForeground(C_TEXTO_SUAVE);
        lblPadreSeleccionado.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPadreSeleccionado.setForeground(C_AZUL_MEDIO);

        JPanel pnlTituloHijos = new JPanel(new BorderLayout(0, 5)); 
        pnlTituloHijos.setOpaque(false);
        pnlTituloHijos.add(lblTituloHijos, BorderLayout.NORTH);
        pnlTituloHijos.add(lblPadreSeleccionado, BorderLayout.CENTER);
        pnlTituloHijos.setBorder(new EmptyBorder(0, 0, 12, 0));

        JPanel pnlBotonesHijos = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlBotonesHijos.setOpaque(false);
        pnlBotonesHijos.add(btnAgregarHijo);
        pnlBotonesHijos.add(btnQuitarHijo);

        JPanel pnlOrdenHijos = new JPanel();
        pnlOrdenHijos.setLayout(new BoxLayout(pnlOrdenHijos, BoxLayout.Y_AXIS));
        pnlOrdenHijos.setOpaque(false);
        pnlOrdenHijos.setBorder(new EmptyBorder(0, 12, 0, 0));
        pnlOrdenHijos.add(btnSubirHijo);
        pnlOrdenHijos.add(Box.createVerticalStrut(10));
        pnlOrdenHijos.add(btnBajarHijo);

        pnlDetalleHijos.add(pnlTituloHijos, BorderLayout.NORTH);
        pnlDetalleHijos.add(jScrollPaneHijos, BorderLayout.CENTER);
        pnlDetalleHijos.add(pnlBotonesHijos, BorderLayout.SOUTH);
        pnlDetalleHijos.add(pnlOrdenHijos, BorderLayout.EAST);

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.BOTH;
        gc.weighty = 1.0;
        gc.insets = new Insets(0, 0, 0, 20);

        gc.gridx = 0;
        gc.weightx = 0.42;
        pnlCuerpo.add(pnlTablaContainer, gc);

        gc.gridx = 1;
        gc.weightx = 0.58;
        gc.insets = new Insets(0, 0, 0, 0);
        pnlCuerpo.add(pnlDetalleHijos, gc);

        pnlContenedorBlanco.add(pnlCuerpo, BorderLayout.CENTER);
        add(pnlContenedorBlanco, BorderLayout.CENTER);

        configurarBoton(btnAgregarHijo, C_AZUL_MEDIO, "+ VINCULAR", 120, 36);
        configurarBoton(btnQuitarHijo, C_ROJO, "- QUITAR", 120, 36);
        
        configurarBotonIcono(btnSubirHijo, "/reportes/img/flecha_arriba_icon.png", C_NAVY);
        configurarBotonIcono(btnBajarHijo, "/reportes/img/flecha_abajo_icon.png", C_NAVY);
        configurarBotonIcono(btnSubirPadre, "/reportes/img/flecha_arriba_icon.png", C_NAVY);
        configurarBotonIcono(btnBajarPadre, "/reportes/img/flecha_abajo_icon.png", C_NAVY);
        
        btnQuitarHijo.setEnabled(false);
        btnSubirHijo.setEnabled(false);
        btnBajarHijo.setEnabled(false);
    }

    private void estilizarContenedor(JPanel pnl) {
        pnl.setBackground(C_BLANCO);
        pnl.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(C_BORDE, 1, true),
            new EmptyBorder(16, 16, 16, 16)
        ));
    }

    private void estilizarCampoBuscador(JTextField tf) {
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setBackground(new Color(25, 45, 75)); 
        tf.setForeground(C_BLANCO);
        tf.setCaretColor(C_BLANCO);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 80, 120), 1, true),
            new EmptyBorder(8, 14, 8, 14)
        ));
        tf.setPreferredSize(new Dimension(320, 38));
    }

    private void configurarBoton(JButton btn, Color bg, String texto, int w, int h) {
        btn.setText(texto);
        btn.setBackground(bg);
        btn.setForeground(C_BLANCO);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(w, h));
    }

    private void configurarBotonIcono(JButton btn, String rutaIcono, Color bg) {
        btn.setText(""); 
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(36, 36));
        btn.setMargin(new Insets(0, 0, 0, 0));

        try {
            java.net.URL url = getClass().getResource(rutaIcono);
            if (url != null) {
                Image img = new ImageIcon(url).getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH);
                btn.setIcon(new ImageIcon(img));
            } else {
                btn.setText("?");
                btn.setForeground(C_BLANCO);
            }
        } catch (Exception e) {
            btn.setText("?");
        }
    }

    private void configurarBotonRetroceso(JButton btn) {
        btn.setText(" ");
        btn.setBackground(C_NAVY);
        btn.setForeground(C_HEADER_TEXT);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(0, 0, 0, 20));

        try {
            java.net.URL url = getClass().getResource("/reportes/img/flecha_icon.png");
            if (url != null) {
                Image img = new ImageIcon(url).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                btn.setIcon(new ImageIcon(img));
            } else {
                btn.setText("←");
                btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
            }
        } catch (Exception e) {}
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { btn.setForeground(C_BLANCO); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { btn.setForeground(C_HEADER_TEXT); }
        });
    }

    private void configurarBotonAyuda(JButton btn) {
        btn.setText("?");
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBackground(C_AZUL_MEDIO);
        btn.setForeground(C_BLANCO);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(36, 36));
        btn.setMinimumSize(new Dimension(36, 36));
        btn.setMaximumSize(new Dimension(36, 36));
        btn.setMargin(new Insets(0, 0, 0, 0));
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        btn.setVerticalAlignment(SwingConstants.CENTER);
        btn.setIcon(null);
        btn.addActionListener(e -> mostrarAyudaRapida());
    }

    private void mostrarAyudaRapida() {
        String msj = "<html><body style='width: 400px; font-family: Segoe UI; color: #28323c;'>"
            + "<h2 style='color: #1e6eb4; margin-top: 0;'>💡 Tips para la Carga de Datos</h2>"
            + "<hr style='color: #d7e1eb;'>" 
            + "<h3 style='color: #23a073; margin-top: 10px; margin-bottom: 5px;'>1. Saltos de Línea en Valores</h3>"
            + "Utilice el símbolo <b style='color: #dc3545;'>;</b> para separar los renglones.<br>"
            + "<i style='color: #647382; font-size: 11px;'>Ejemplo que usted tipea:</i><br>"
            + "<span style='background-color: #f5f8fc;'>Varones: 100-150 ; Mujeres: 150-200</span><br>"
            + "<i style='color: #647382; font-size: 11px;'>Resultado visual:</i><br>"
            + "Varones: 100-150<br>"
            + "Mujeres: 150-200<br>"
            + "<h3 style='color: #23a073; margin-top: 15px; margin-bottom: 5px;'>2. Títulos / Separadores Visuales</h3>"
            + "Para crear un título, escríbalo entre guiones al Vincular.<br>"
            + "<i style='color: #647382; font-size: 11px;'>Ejemplo:</i> <b>--- TÍTULO ---</b><br>"
            + "<h3 style='color: #23a073; margin-top: 15px; margin-bottom: 5px;'>3. Orden y Prioridades</h3>"
            + "Seleccione una fila y utilice los botones de las <b>flechas laterales (▲ / ▼)</b> para subir o bajar su posición.";
            
        JOptionPane.showMessageDialog(this, msj, "Ayuda y Atajos de BIOTEC", JOptionPane.INFORMATION_MESSAGE);
    }

    // ══════════════════════════════════════════════════════════════════
    //  initComponents
    // ══════════════════════════════════════════════════════════════════
    private void initComponents() {
        pnlHeader = new JPanel();
        lblTituloHeader = new JLabel("NOMENCLADOR BIOQUÍMICO ÚNICO");
        txtBuscarNBU = new JTextField();
        btnVolver = new JButton();
        btnAyuda = new JButton();
        
        pnlContenedorBlanco = new JPanel();
        pnlCuerpo = new JPanel();
        
        pnlTablaContainer = new JPanel();
        jScrollPane1 = new JScrollPane();
        grillaNBU = new JTable();
        btnSubirPadre = new JButton();
        btnBajarPadre = new JButton();
        
        pnlDetalleHijos = new JPanel();
        jScrollPaneHijos = new JScrollPane();
        grillaHijos = new JTable();
        lblTituloHijos = new JLabel("COMPONENTES DE LA PRÁCTICA:");
        lblPadreSeleccionado = new JLabel("Seleccione una práctica...");
        btnAgregarHijo = new JButton();
        btnQuitarHijo = new JButton();
        btnSubirHijo = new JButton();
        btnBajarHijo = new JButton();

        grillaNBU.setModel(new DefaultTableModel(
            new Object[][]{},
            new String[]{"Nº", "CÓDIGO", "PRÁCTICA (ESTUDIO PADRE)", "UB"}
        ) {
            boolean[] canEdit = {false, false, false, false};
            @Override public boolean isCellEditable(int r, int c) { return canEdit[c]; }
        });
        jScrollPane1.setViewportView(grillaNBU);
        jScrollPane1.setBorder(BorderFactory.createEmptyBorder());

        grillaHijos.setModel(new DefaultTableModel(
            new Object[][]{},
            new String[]{"CÓDIGO_OCULTO", "ORDEN", "COMPONENTE", "UNIDAD", "REFERENCIA"}
        ) {
            @Override public boolean isCellEditable(int r, int c) { 
                if (c == 2 || c == 3 || c == 4) {
                    Object nombreComponente = getValueAt(r, 2);
                    if ((c == 3 || c == 4) && nombreComponente != null && nombreComponente.toString().startsWith("---") && nombreComponente.toString().endsWith("---")) {
                        return false; 
                    }
                    return true;
                }
                return false; 
            }
        });
        jScrollPaneHijos.setViewportView(grillaHijos);
        jScrollPaneHijos.setBorder(BorderFactory.createLineBorder(C_BORDE));

        setLayout(new BorderLayout());

        pnlHeader.setLayout(new BorderLayout());
        JPanel pnlIzqHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlIzqHeader.setOpaque(false);
        pnlIzqHeader.add(btnVolver);
        lblTituloHeader.setBorder(new EmptyBorder(0, 10, 0, 0));
        pnlIzqHeader.add(lblTituloHeader);
        
        JPanel pnlDerHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        pnlDerHeader.setOpaque(false);
        pnlDerHeader.add(btnAyuda);
        JLabel lblLupa = new JLabel("Buscar:");
        lblLupa.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblLupa.setForeground(C_HEADER_TEXT);
        pnlDerHeader.add(lblLupa);
        pnlDerHeader.add(txtBuscarNBU);
        
        pnlHeader.add(pnlIzqHeader, BorderLayout.WEST);
        pnlHeader.add(pnlDerHeader, BorderLayout.EAST);
        add(pnlHeader, BorderLayout.NORTH);
    }

    // ── Variables ────────────────────────────────────────────────────
    private JButton      btnVolver, btnAyuda;
    private JButton      btnAgregarHijo, btnQuitarHijo;
    private JButton      btnSubirHijo, btnBajarHijo, btnSubirPadre, btnBajarPadre;
    private JTable       grillaNBU, grillaHijos;
    private JPanel       pnlHeader, pnlContenedorBlanco, pnlCuerpo;
    private JPanel       pnlTablaContainer, pnlDetalleHijos;
    private JLabel       lblTituloHeader, lblTituloHijos, lblPadreSeleccionado;
    private JScrollPane  jScrollPane1, jScrollPaneHijos;
    private JTextField   txtBuscarNBU;
}