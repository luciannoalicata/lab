package vista;

import presentador.Controlador;
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

/**
 * Vista Nomenclador Bioquímico Único - BIOTEC LIS
 * Diseño Maestro-Detalle para gestión de componentes (Padre-Hijo)
 */
public class VistaNBU extends JPanel implements IVistaNBU {

    private Controlador controlador;

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
    
    // Tipografía accesible
    private final Font F_TBL_HEADER = new Font("Segoe UI", Font.BOLD, 13);
    private final Font F_TBL_CELL   = new Font("Segoe UI", Font.PLAIN, 14);

    public VistaNBU() {
        initComponents();
        aplicarEsteticaProfesional();

        // Listener del buscador
        txtBuscarNBU.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e)  { buscar(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e)  { buscar(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { buscar(); }
            private void buscar() { if (controlador != null) controlador.buscarEnNBU(getBusqueda()); }
        });

        // Listener visual para cambiar el título del panel derecho
        grillaNBU.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && grillaNBU.getSelectedRow() != -1) {
                String nombrePadre = grillaNBU.getValueAt(grillaNBU.getSelectedRow(), 2).toString();
                lblPadreSeleccionado.setText(nombrePadre);
                
                // MAGIA UX: Si el texto es muy largo, al dejar el mouse encima se leerá completo
                lblPadreSeleccionado.setToolTipText(nombrePadre); 
            }
        });
    }

    // ════════════════════════════════════════════════════════════════
    //  ESTÉTICA Y UX
    // ════════════════════════════════════════════════════════════════
    private void aplicarEsteticaProfesional() {
        setBackground(C_FONDO);

        // ── HEADER ────────────────────────────────────────────────────
        pnlHeader.setBackground(C_NAVY);
        pnlHeader.setBorder(new EmptyBorder(15, 30, 15, 30));
        lblTituloHeader.setForeground(C_BLANCO);
        lblTituloHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));

        estilizarCampoBuscador(txtBuscarNBU);
        configurarBotonRetroceso(btnVolver);

        // ── TABLA PRINCIPAL (PADRES) ──────────────────────────────────
        configurarTabla(grillaNBU);
        grillaNBU.getColumnModel().getColumn(0).setPreferredWidth(60);  // Espacio para 4 dígitos
        grillaNBU.getColumnModel().getColumn(0).setMaxWidth(60);
        grillaNBU.getColumnModel().getColumn(0).setMinWidth(60);
        grillaNBU.getColumnModel().getColumn(0).setResizable(false);
        
        grillaNBU.getColumnModel().getColumn(1).setPreferredWidth(80);  // Código
        grillaNBU.getColumnModel().getColumn(1).setMaxWidth(100);
        grillaNBU.getColumnModel().getColumn(1).setResizable(false);
        
        grillaNBU.getColumnModel().getColumn(2).setPreferredWidth(300); // Nombre
        grillaNBU.getColumnModel().getColumn(2).setResizable(false);
        
        grillaNBU.getColumnModel().getColumn(3).setPreferredWidth(60);  // UB
        grillaNBU.getColumnModel().getColumn(3).setMaxWidth(80);
        grillaNBU.getColumnModel().getColumn(3).setResizable(false);

        // ── TABLA SECUNDARIA (HIJOS) ──────────────────────────────────
        configurarTabla(grillaHijos);
        // Ocultamos la columna Código visualmente (Índice 0)
        grillaHijos.getColumnModel().getColumn(0).setMinWidth(0);
        grillaHijos.getColumnModel().getColumn(0).setMaxWidth(0);
        grillaHijos.getColumnModel().getColumn(0).setPreferredWidth(0);
        grillaHijos.getColumnModel().getColumn(0).setResizable(false);
        
        grillaHijos.getColumnModel().getColumn(1).setPreferredWidth(55);  // ORDEN
        grillaHijos.getColumnModel().getColumn(1).setMaxWidth(65);
        grillaHijos.getColumnModel().getColumn(1).setResizable(false);
        
        grillaHijos.getColumnModel().getColumn(2).setPreferredWidth(230); // COMPONENTE
        grillaHijos.getColumnModel().getColumn(2).setResizable(false);
        
        grillaHijos.getColumnModel().getColumn(3).setPreferredWidth(120); // UNIDAD (Ensanchado)
        grillaHijos.getColumnModel().getColumn(3).setMaxWidth(160);
        grillaHijos.getColumnModel().getColumn(3).setResizable(false);
        
        grillaHijos.getColumnModel().getColumn(4).setResizable(false);    // REFERENCIA

        // Habilita moverse celda por celda con las flechas del teclado
        grillaHijos.setCellSelectionEnabled(true);

        // ── PANELES CONTENEDORES ──────────────────────────────────────
        estilizarContenedor(pnlTablaContainer);
        estilizarContenedor(pnlDetalleHijos);
        
        lblTituloHijos.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTituloHijos.setForeground(C_TEXTO_SUAVE);
        
        // Reducimos la fuente de 18 a 16 para ganar espacio de lectura
        lblPadreSeleccionado.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblPadreSeleccionado.setForeground(C_AZUL_MEDIO);
        
        // ── CEPO AMPLIADO: Pasamos de 350px a 600px ──
        lblPadreSeleccionado.setPreferredSize(new Dimension(600, 25));
        lblPadreSeleccionado.setMinimumSize(new Dimension(600, 25));
        lblPadreSeleccionado.setMaximumSize(new Dimension(600, 25));

        // ── BOTONES ───────────────────────────────────────────────────
        configurarBoton(btnGuardarCambios, C_VERDE, "💾 GUARDAR CAMBIOS", 200, 44);
        configurarBoton(btnAgregarHijo, C_AZUL_MEDIO, "+ VINCULAR", 130, 38);
        configurarBoton(btnQuitarHijo, C_ROJO, "- QUITAR", 130, 38);
        
        configurarBotonIcono(btnSubirHijo, "/reportes/img/flecha_arriba_icon.png", C_NAVY);
        configurarBotonIcono(btnBajarHijo, "/reportes/img/flecha_abajo_icon.png", C_NAVY);
        configurarBotonIcono(btnSubirPadre, "/reportes/img/flecha_arriba_icon.png", C_NAVY);
        configurarBotonIcono(btnBajarPadre, "/reportes/img/flecha_abajo_icon.png", C_NAVY);
        pnlFooter.setBackground(C_FONDO);
        pnlFooter.setBorder(new EmptyBorder(10, 0, 0, 0));
    }

    private void estilizarContenedor(JPanel pnl) {
        pnl.setBackground(C_BLANCO);
        pnl.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(C_BORDE, 1, true),
            new EmptyBorder(10, 10, 10, 10)
        ));
    }

    private void configurarTabla(JTable tabla) {
        tabla.setRowHeight(40);
        tabla.setFont(F_TBL_CELL);
        tabla.setGridColor(new Color(230, 238, 245));
        tabla.setShowHorizontalLines(true);
        tabla.setShowVerticalLines(false);
        tabla.setSelectionBackground(new Color(210, 232, 250));
        tabla.setSelectionForeground(C_NAVY);
        tabla.setIntercellSpacing(new Dimension(0, 1));
        tabla.setBorder(BorderFactory.createEmptyBorder());
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tabla.getTableHeader().setFont(F_TBL_HEADER);
        tabla.getTableHeader().setBackground(C_CABECERA_TBL);
        tabla.getTableHeader().setForeground(C_TEXTO_SUAVE);
        tabla.getTableHeader().setPreferredSize(new Dimension(0, 45));
        tabla.getTableHeader().setBorder(BorderFactory.createMatteBorder(1, 0, 2, 0, C_BORDE));
        tabla.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean sel, boolean focus, int row, int col) {
                
                // ── MAGIA HTML PARA MULTILÍNEA Y CARACTERES ESPECIALES ──
                String textoOriginal = value != null ? value.toString() : "";
                String textoFormateado = textoOriginal.replace("<", "&lt;").replace(">", "&gt;");
                
                // Si el texto contiene el separador ";"
                if (textoOriginal.contains(";")) {
                    String[] lineas = textoOriginal.split(";");
                    StringBuilder sb = new StringBuilder("<html>");
                    
                    for (int i = 0; i < lineas.length; i++) {
                        // BLINDAJE EXTREMO: Convertimos TODOS los espacios de este renglón en "pegamento" (&nbsp;)
                        String lineaBlindada = lineas[i].trim()
                                .replace("<", "&lt;")
                                .replace(">", "&gt;")
                                .replace(" ", "&nbsp;"); 
                                
                        sb.append(lineaBlindada);
                        
                        if (i < lineas.length - 1) {
                            sb.append("<br>");
                        }
                    }
                    sb.append("</html>");
                    textoFormateado = sb.toString();
                }
                
                Component c = super.getTableCellRendererComponent(t, textoFormateado, sel, focus, row, col);
                
                // Tooltip flotante
                if (textoOriginal.contains(";")) {
                    setToolTipText("<html><div style='padding:5px;'>" + textoOriginal.replace(";", "<br>") + "</div></html>");
                } else {
                    setToolTipText(null);
                }
                if (col == 0 || col == 1 || (t.getColumnCount() == 4 && col == 3) || (t.getColumnCount() == 5 && col == 3)) {
                    setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT);
                }
                
                Object valComponente = t.getModel().getValueAt(row, t.getColumnCount() == 5 ? 2 : 2);
                if (valComponente != null && valComponente.toString().startsWith("---") && valComponente.toString().endsWith("---")) {
                    setBackground(new Color(235, 242, 248));
                    setForeground(C_AZUL_MEDIO);
                    setFont(new Font("Segoe UI", Font.BOLD, 13));
                } else if (!sel) {
                    setBackground(row % 2 == 0 ? C_BLANCO : C_FILA_PAR);
                    setForeground(C_TEXTO_FUERTE);
                    setFont(F_TBL_CELL);
                } else {
                    setBackground(new Color(210, 232, 250));
                    setForeground(C_NAVY);
                    setFont(F_TBL_CELL);
                }

                if (focus) {
                    setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(0, 120, 215), 2), 
                        new EmptyBorder(0, 8, 0, 8)
                    ));
                } else {
                    setBorder(new EmptyBorder(0, 10, 0, 10)); 
                }

                // ── MAGIA UX: AUTO-AJUSTE DE ALTURA DE FILA ──
                int alturaPreferida = c.getPreferredSize().height + 10; 
                int alturaActual = t.getRowHeight(row);
                
                if (alturaPreferida > alturaActual) {
                    t.setRowHeight(row, alturaPreferida);
                }

                return c; 
            }
        });
    }

    private void estilizarCampoBuscador(JTextField tf) {
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        tf.setBackground(new Color(25, 45, 75)); 
        tf.setForeground(C_BLANCO);
        tf.setCaretColor(C_BLANCO);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 80, 120), 1, true),
            new EmptyBorder(8, 14, 8, 14)
        ));
        tf.setPreferredSize(new Dimension(380, 42));
    }

    private void configurarBoton(JButton btn, Color bg, String texto, int w, int h) {
        btn.setText(texto);
        btn.setBackground(bg);
        btn.setForeground(C_BLANCO);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
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
        btn.setPreferredSize(new Dimension(37, 37));
        btn.setMargin(new Insets(0, 0, 0, 0));

        try {
            java.net.URL url = getClass().getResource(rutaIcono);
            if (url != null) {
                Image img = new ImageIcon(url).getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH);
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
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false); 
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(0, 0, 0, 20));

        try {
            java.net.URL url = getClass().getResource("/reportes/img/flecha_icon.png");
            if (url != null) {
                Image img = new ImageIcon(url).getImage().getScaledInstance(43, 43, Image.SCALE_SMOOTH);
                btn.setIcon(new ImageIcon(img));
            } else {
                btn.setText("← VOLVER");
                btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
            }
        } catch (Exception e) {}
    }

    // ══════════════════════════════════════════════════════════════════
    //  INTERFAZ IVistaNBU
    // ══════════════════════════════════════════════════════════════════
    @Override public void ejecutar() { setVisible(true); }

    @Override
    public void setControlador(Controlador control) {
        this.controlador = control;
        btnGuardarCambios.setActionCommand(BTN_GUARDAR_CAMBIOS);
        btnGuardarCambios.addActionListener(control);
        
        btnVolver.setActionCommand(BTN_SALIR); 
        btnVolver.addActionListener(control);

        btnAgregarHijo.setActionCommand("BTN_AGREGAR_HIJO");
        btnAgregarHijo.addActionListener(control);
        btnQuitarHijo.setActionCommand("BTN_QUITAR_HIJO");
        btnQuitarHijo.addActionListener(control);
        btnSubirHijo.setActionCommand("BTN_SUBIR_HIJO");
        btnSubirHijo.addActionListener(control);
        btnBajarHijo.setActionCommand("BTN_BAJAR_HIJO");
        btnBajarHijo.addActionListener(control);
        
        btnSubirPadre.setActionCommand("BTN_SUBIR_PADRE");
        btnSubirPadre.addActionListener(control);
        btnBajarPadre.setActionCommand("BTN_BAJAR_PADRE");
        btnBajarPadre.addActionListener(control);

        grillaNBU.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && grillaNBU.getSelectedRow() != -1) {
                // ── ¡ESCUDO DE TITANIO! ──
                // Obligamos a la celda a guardar su valor y cerrarse ANTES de cambiar de práctica
                detenerEdicionTabla(); 
                
                controlador.seleccionarPadreNBU(getCodigoPadreSeleccionado());
            }
        });
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
                // ── MAGIA UX: changeSelection obliga a pintar la celda y la fila ──
                // Le pasamos el índice de la fila y el número 2 (que es la columna "COMPONENTE")
                // Los 'false, false' significan que no estamos usando las teclas Shift ni Ctrl
                grillaHijos.changeSelection(indice, 2, false, false);
                
                // Le devolvemos el foco a la tabla para anclar la visión
                grillaHijos.requestFocusInWindow();
            }
        });
    }

    @Override
    public void cargarDeterminaciones(List<Determinacion> lista) {
        // ── MAGIA UX: Memorizamos dónde estaba mirando el usuario ──
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
        
        // ── MAGIA UX: Restauramos la cámara al mismo lugar exacto ──
        javax.swing.SwingUtilities.invokeLater(() -> {
            jScrollPane1.getVerticalScrollBar().setValue(scrollVertical);
        });
        
        lblPadreSeleccionado.setText("Seleccione una práctica...");
        ((DefaultTableModel) grillaHijos.getModel()).setRowCount(0); 
    }

    @Override
    public void cargarHijos(List<Determinacion> listaHijos) {
        DefaultTableModel m = (DefaultTableModel) grillaHijos.getModel();
        m.setRowCount(0);
        for (Determinacion d : listaHijos) {
            m.addRow(new Object[]{
                d.getCodigo(), d.getPrioridad(), d.getNombre(), d.getUnidad(), d.getReferencia()
            });
        }
        m.fireTableDataChanged();
    }

    @Override
    public void seleccionarFilaPorCodigo(String codigo) {
        for (int i = 0; i < grillaNBU.getRowCount(); i++) {
            Object valorCelda = grillaNBU.getModel().getValueAt(i, 1);
            if (valorCelda != null && valorCelda.toString().trim().equals(codigo)) {
                grillaNBU.setRowSelectionInterval(i, i);
                
                // ── BLOQUEAMOS EL SALTO DE CÁMARA ──
                // Al quitar esta línea, la fila se selecciona (se pinta de azul) 
                // pero la pantalla no viaja hacia ella obligatoriamente.
                // grillaNBU.scrollRectToVisible(grillaNBU.getCellRect(i, 0, true)); 
                
                break;
            }
        }
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

    // ══════════════════════════════════════════════════════════════════
    //  UI BUILDER (Master-Detail Layout)
    // ══════════════════════════════════════════════════════════════════
    private void initComponents() {
        pnlHeader         = new JPanel();
        lblTituloHeader   = new JLabel("NOMENCLADOR BIOQUÍMICO ÚNICO");
        txtBuscarNBU      = new JTextField();
        btnVolver         = new JButton();
        pnlFooter         = new JPanel();
        btnGuardarCambios = new JButton();

        // ── TABLA PRINCIPAL (PADRES) ──
        pnlTablaContainer = new JPanel();
        jScrollPane1      = new JScrollPane();
        grillaNBU         = new JTable();
        grillaNBU.setModel(new DefaultTableModel(
            new Object[][]{},
            new String[]{"Nº", "CÓDIGO", "PRÁCTICA (ESTUDIO PADRE)", "UB"}
        ) {
            boolean[] canEdit = {false, false, false, false};
            @Override public boolean isCellEditable(int r, int c) { return canEdit[c]; }
        });
        jScrollPane1.setViewportView(grillaNBU);
        jScrollPane1.getViewport().setBackground(C_BLANCO);
        jScrollPane1.setBorder(BorderFactory.createEmptyBorder());
        
        btnSubirPadre = new JButton();
        btnBajarPadre = new JButton();

        // ── TABLA SECUNDARIA (HIJOS) ──
        pnlDetalleHijos      = new JPanel(new BorderLayout(10, 10));
        jScrollPaneHijos     = new JScrollPane();
        grillaHijos          = new JTable();
        lblTituloHijos       = new JLabel("COMPONENTES DE LA PRÁCTICA:");
        lblPadreSeleccionado = new JLabel("Seleccione una práctica...");
        btnAgregarHijo       = new JButton();
        btnQuitarHijo        = new JButton();
        btnSubirHijo         = new JButton();
        btnBajarHijo         = new JButton();

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
        jScrollPaneHijos.getViewport().setBackground(C_BLANCO);
        jScrollPaneHijos.setBorder(BorderFactory.createLineBorder(C_BORDE));

        // ── ARMADO DEL LAYOUT ──────────────────────────────────────────
        setLayout(new BorderLayout());

        // 1. Header
        pnlHeader.setLayout(new BorderLayout());
        JPanel pnlIzqHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlIzqHeader.setOpaque(false);
        pnlIzqHeader.add(btnVolver);
        lblTituloHeader.setBorder(new EmptyBorder(0, 10, 0, 0)); 
        pnlIzqHeader.add(lblTituloHeader);
        
        JPanel pnlDerHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pnlDerHeader.setOpaque(false);
        
        // Creamos el botón de ayuda
        btnAyuda = new JButton("?");
        btnAyuda.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnAyuda.setBackground(C_AZUL_MEDIO);
        btnAyuda.setForeground(C_BLANCO);
        btnAyuda.setFocusPainted(false);
        btnAyuda.setBorder(new EmptyBorder(5, 12, 5, 12));
        btnAyuda.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAyuda.addActionListener(e -> mostrarAyudaRapida());
        
        JLabel lblLupa = new JLabel("Buscar:  ");
        lblLupa.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblLupa.setForeground(C_HEADER_TEXT);
        
        pnlDerHeader.add(btnAyuda);
        pnlDerHeader.add(Box.createHorizontalStrut(15)); // Espacio entre ayuda y lupa
        pnlDerHeader.add(lblLupa);
        pnlDerHeader.add(txtBuscarNBU);

        // ──> ¡AQUÍ ESTABAN LAS LÍNEAS QUE FALTABAN! <──
        pnlHeader.add(pnlIzqHeader, BorderLayout.WEST);
        pnlHeader.add(pnlDerHeader, BorderLayout.EAST);
        add(pnlHeader, BorderLayout.NORTH);

        // 2. Cuerpo Central
        JPanel pnlCuerpo = new JPanel(new GridBagLayout());
        pnlCuerpo.setBackground(C_FONDO);
        pnlCuerpo.setBorder(new EmptyBorder(10, 10, 10, 10));

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.BOTH;
        gc.weighty = 1.0;

        // Izquierda (Padres)
        pnlTablaContainer.setLayout(new BorderLayout());
        pnlTablaContainer.add(jScrollPane1, BorderLayout.CENTER);
        
        JPanel pnlOrdenPadres = new JPanel();
        pnlOrdenPadres.setLayout(new BoxLayout(pnlOrdenPadres, BoxLayout.Y_AXIS));
        pnlOrdenPadres.setOpaque(false);
        pnlOrdenPadres.setBorder(new EmptyBorder(0, 10, 0, 0)); 
        pnlOrdenPadres.add(btnSubirPadre);
        pnlOrdenPadres.add(Box.createVerticalStrut(10));
        pnlOrdenPadres.add(btnBajarPadre);
        
        pnlTablaContainer.add(pnlOrdenPadres, BorderLayout.EAST);
        
        gc.gridx = 0; gc.weightx = 0.42; 
        gc.insets = new Insets(0, 0, 0, 10);
        pnlCuerpo.add(pnlTablaContainer, gc);

        // Derecha (Hijos)
        JPanel pnlTituloHijos = new JPanel(new BorderLayout(0, 5)); // BorderLayout en lugar de BoxLayout
        pnlTituloHijos.setOpaque(false);
        pnlTituloHijos.add(lblTituloHijos, BorderLayout.NORTH);
        pnlTituloHijos.add(lblPadreSeleccionado, BorderLayout.CENTER);
        pnlTituloHijos.setBorder(new EmptyBorder(0, 0, 10, 0));

        JPanel pnlBotonesHijos = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlBotonesHijos.setOpaque(false);
        pnlBotonesHijos.add(btnAgregarHijo);
        pnlBotonesHijos.add(btnQuitarHijo);

        JPanel pnlOrdenHijos = new JPanel();
        pnlOrdenHijos.setLayout(new BoxLayout(pnlOrdenHijos, BoxLayout.Y_AXIS));
        pnlOrdenHijos.setOpaque(false);
        pnlOrdenHijos.add(btnSubirHijo);
        pnlOrdenHijos.add(Box.createVerticalStrut(10));
        pnlOrdenHijos.add(btnBajarHijo);

        pnlDetalleHijos.add(pnlTituloHijos, BorderLayout.NORTH);
        pnlDetalleHijos.add(jScrollPaneHijos, BorderLayout.CENTER);
        pnlDetalleHijos.add(pnlBotonesHijos, BorderLayout.SOUTH);
        pnlDetalleHijos.add(pnlOrdenHijos, BorderLayout.EAST);

        gc.gridx = 1; gc.weightx = 0.58; 
        gc.insets = new Insets(0, 10, 0, 0);
        pnlCuerpo.add(pnlDetalleHijos, gc);

        add(pnlCuerpo, BorderLayout.CENTER);

        // 3. Footer
        pnlFooter.setLayout(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        pnlFooter.add(btnGuardarCambios);
        add(pnlFooter, BorderLayout.SOUTH);
    }
    
    private void mostrarAyudaRapida() {
        String msj = "<html><body style='width: 400px; font-family: Segoe UI; color: #28323c;'>"
            + "<h2 style='color: #1e6eb4; margin-top: 0;'>💡 Tips para la Carga de Datos</h2>"
            + "<hr style='color: #d7e1eb;'>" // Línea sutil divisoria
            
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
            
            + "<h3 style='color: #23a073; margin-top: 15px; margin-bottom: 5px;'>3. Orden y Prioridades de Impresión y Carga</h3>"
            + "Seleccione una fila en cualquiera de las tablas y utilice los botones de las <b>flechas laterales (▲ / ▼)</b> para subir o bajar su posición en la lista. El sistema guardará el nuevo orden automáticamente. Este órden se verá reflejado a la hora de cargar resultados e imprimir el análisis.\n\n";
           
            
        JOptionPane.showMessageDialog(this, msj, "Ayuda y Atajos de BIOTEC", JOptionPane.INFORMATION_MESSAGE);
    }

    // ── HERRAMIENTAS PARA MOVER PADRES ──
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
                grillaNBU.setRowSelectionInterval(indice, indice);
                grillaNBU.scrollRectToVisible(grillaNBU.getCellRect(indice, 0, true));
                grillaNBU.requestFocusInWindow();
            }
        });
    }

    // ── Variables ────────────────────────────────────────────────────
    private JButton      btnGuardarCambios, btnVolver;
    private JButton      btnAgregarHijo, btnQuitarHijo;
    private JButton      btnSubirHijo, btnBajarHijo, btnSubirPadre, btnBajarPadre;
    private JTable       grillaNBU, grillaHijos;
    private JPanel       pnlHeader, pnlTablaContainer, pnlFooter, pnlDetalleHijos;
    private JLabel       lblTituloHeader, lblTituloHijos, lblPadreSeleccionado;
    private JScrollPane  jScrollPane1, jScrollPaneHijos;
    private JTextField   txtBuscarNBU;
    private JButton      btnAyuda;
}