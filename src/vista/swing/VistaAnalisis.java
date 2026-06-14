package vista.swing;

import vista.interfaces.IVistaAnalisis;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.util.ArrayList;
import javax.swing.BorderFactory;
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
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import modelo.Analisis;
import presentador.AnalisisPresenter;

public class VistaAnalisis extends JPanel implements IVistaAnalisis {

    private AnalisisPresenter presenter;
    
    // ── Paleta BIOTEC Minimalista ────────────────────────────────────
    private final Color C_NAVY         = new Color(10, 25, 47);    
    private final Color C_FONDO        = new Color(238, 242, 246);
    private final Color C_BLANCO       = Color.WHITE;
    private final Color C_TEXTO_FUERTE = new Color(40, 50, 60);
    private final Color C_TEXTO_SUAVE  = new Color(100, 115, 130);
    private final Color C_BORDE        = new Color(215, 225, 235);
    private final Color C_AZUL_MEDIO   = new Color(30, 110, 180);
    private final Color C_VERDE        = new Color(35, 160, 115);
    private final Color C_VERDE_FILA   = new Color(220, 245, 220);
    private final Color C_VERDE_TEXTO  = new Color(20, 100, 50);
    private final Color C_CABECERA_TBL = new Color(245, 248, 252);
    private final Color C_FILA_PAR     = new Color(252, 254, 255);
    private final Color C_HEADER_TEXT  = new Color(175, 205, 235);
    private final Color C_SELECCION    = new Color(220, 235, 250);
    private final Color C_CAMPO        = new Color(250, 252, 254);

    // Componentes
    private JPanel pnlHeader, pnlContenedorBlanco, pnlCuerpo, pnlFooter, pnlTablaWrapper;
    private JLabel lblTituloHeader, lblContador, lblTituloTabla;
    private JButton btnVerDetallesAnalisis, btnImprimirAnalisis, btnVolver;
    private JTable grillaAnalisis;
    private JScrollPane jScrollPane1;
    private JTextField txtBuscar;
    private javax.swing.event.ListSelectionListener listenerSeleccionTabla;
    private javax.swing.event.DocumentListener listenerBuscador;
    private TableRowSorter<DefaultTableModel> rowSorter;

    public VistaAnalisis() {
        initComponents();
        aplicarEstilo();
    }

    // ══════════════════════════════════════════════════════════════════
    //  INTERFAZ IVistaAnalisis - MÉTODOS MVP
    // ══════════════════════════════════════════════════════════════════

    @Override
    public void setPresenter(AnalisisPresenter presenter) {
        this.presenter = presenter; 
        
        // ── LIMPIEZA DE LISTENERS ──
        for (java.awt.event.ActionListener al : btnVerDetallesAnalisis.getActionListeners()) {
            btnVerDetallesAnalisis.removeActionListener(al);
        }
        for (java.awt.event.ActionListener al : btnImprimirAnalisis.getActionListeners()) {
            btnImprimirAnalisis.removeActionListener(al);
        }
        for (java.awt.event.ActionListener al : btnVolver.getActionListeners()) {
            btnVolver.removeActionListener(al);
        }
        
        btnVerDetallesAnalisis.addActionListener(e -> presenter.onVerDetalles());
        btnImprimirAnalisis.addActionListener(e -> presenter.onImprimirAnalisis());
        btnVolver.addActionListener(e -> presenter.onVolver());
        
        // ── LIMPIEZA DE SELECCIÓN DE TABLA ──
        if (listenerSeleccionTabla != null) {
            grillaAnalisis.getSelectionModel().removeListSelectionListener(listenerSeleccionTabla);
        }
        
        listenerSeleccionTabla = e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hay = grillaAnalisis.getSelectedRow() != -1;
                habilitarBotonVerDetalles(hay);
                habilitarBotonImprimir(hay);
            }
        };
        grillaAnalisis.getSelectionModel().addListSelectionListener(listenerSeleccionTabla);

        // ── LIMPIEZA DEL BUSCADOR ──
        if (listenerBuscador != null) {
            txtBuscar.getDocument().removeDocumentListener(listenerBuscador);
        }
        
        listenerBuscador = new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e)  { disparar(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e)  { disparar(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { disparar(); }

            private void disparar() {
                if (VistaAnalisis.this.presenter != null) {
                    VistaAnalisis.this.presenter.onBuscarAnalisis();
                }
            }
        };
        txtBuscar.getDocument().addDocumentListener(listenerBuscador);
    }

    @Override public void ejecutar() { setVisible(true); }
    @Override public String getTextoBusqueda() { return txtBuscar.getText().trim(); }
    @Override public void habilitarBotonVerDetalles(boolean b) { btnVerDetallesAnalisis.setEnabled(b); }
    @Override public void habilitarBotonImprimir(boolean b) { btnImprimirAnalisis.setEnabled(b); }
    @Override public void mostrarMensaje(String m) { JOptionPane.showMessageDialog(this, m); }
    
    @Override
    public void limpiarFocos() {
        this.requestFocusInWindow();
    }

    @Override
    public int confirmarAccion(String mensaje, String titulo) {
        return JOptionPane.showConfirmDialog(this, mensaje, titulo, JOptionPane.YES_NO_OPTION);
    }

    @Override
    public void cargarAnalisisEnTabla(ArrayList<Analisis> lista) {
        DefaultTableModel modelo = (DefaultTableModel) grillaAnalisis.getModel();
        modelo.setRowCount(0);
        
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");

        for (Analisis a : lista) {
            String obraSocialStr = (a.getObraSocial() != null && !a.getObraSocial().trim().isEmpty()) 
                                    ? a.getObraSocial() : "-";
            
            String estado = a.getEstado() != null ? a.getEstado().toUpperCase() : "";
                                    
            modelo.addRow(new Object[]{
                a.getIdAnalisis(),
                sdf.format(a.getFecha()),
                a.getPacienteNombreCompleto().toUpperCase(),
                a.getPacienteDni() != null ? a.getPacienteDni() : "-",
                obraSocialStr.toUpperCase(),
                String.format("$ %.2f", a.getPrecio()),
                estado  // ← COLUMNA DE ESTADO AGREGADA
            });
        }

        rowSorter = new TableRowSorter<>(modelo);
        grillaAnalisis.setRowSorter(rowSorter);
        aplicarRenderers();

        lblContador.setText(lista.size() + (lista.size() == 1 ? " registro encontrado" : " registros encontrados"));

        habilitarBotonVerDetalles(false);
        habilitarBotonImprimir(false);
    }

    @Override
    public Analisis getAnalisisSeleccionado() {
        int row = grillaAnalisis.getSelectedRow();
        if (row == -1) return null;
        int modelRow = grillaAnalisis.convertRowIndexToModel(row);
        Analisis a = new Analisis();
        a.setIdAnalisis((int) grillaAnalisis.getModel().getValueAt(modelRow, 0));
        return a;
    }

    // ══════════════════════════════════════════════════════════════════
    //  ESTILO Y UX
    // ══════════════════════════════════════════════════════════════════
    private void aplicarEstilo() {
        setBackground(C_FONDO);

        // HEADER
        pnlHeader.setBackground(C_NAVY);
        pnlHeader.setBorder(new EmptyBorder(14, 28, 14, 28));
        pnlHeader.removeAll();
        pnlHeader.setLayout(new BorderLayout());
        
        JPanel pnlIzqHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlIzqHeader.setOpaque(false);
        pnlIzqHeader.add(btnVolver);
        
        lblTituloHeader.setForeground(C_BLANCO);
        lblTituloHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTituloHeader.setHorizontalAlignment(SwingConstants.LEFT);
        lblTituloHeader.setBorder(new EmptyBorder(0, 10, 0, 0));
        pnlIzqHeader.add(lblTituloHeader);
        
        JPanel pnlDerHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        pnlDerHeader.setOpaque(false);
        JLabel lblLupa = new JLabel("Buscar análisis:");
        lblLupa.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblLupa.setForeground(C_HEADER_TEXT);
        pnlDerHeader.add(lblLupa);
        pnlDerHeader.add(txtBuscar);
        
        pnlHeader.add(pnlIzqHeader, BorderLayout.WEST);
        pnlHeader.add(pnlDerHeader, BorderLayout.EAST);

        estilizarCampoBuscador(txtBuscar);
        configurarBotonRetroceso(btnVolver);

        // CONTENEDOR PRINCIPAL
        pnlContenedorBlanco.setBackground(C_BLANCO);
        pnlContenedorBlanco.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 1, 1, 1, C_BORDE),
            new EmptyBorder(24, 28, 24, 28)
        ));
        pnlContenedorBlanco.removeAll();
        pnlContenedorBlanco.setLayout(new BorderLayout());

        // CUERPO
        pnlCuerpo.setBackground(C_BLANCO);
        pnlCuerpo.removeAll();
        pnlCuerpo.setLayout(new BorderLayout());

        // TABLA WRAPPER
        pnlTablaWrapper.setBackground(C_BLANCO);
        pnlTablaWrapper.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(0, 0, 0, 0),
            BorderFactory.createLineBorder(C_BORDE, 1, true)
        ));

        lblTituloTabla.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTituloTabla.setForeground(C_TEXTO_FUERTE);
        lblTituloTabla.setBorder(new EmptyBorder(14, 16, 12, 16));

        // Configuración de la tabla
        grillaAnalisis.setRowHeight(36);
        grillaAnalisis.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        grillaAnalisis.setShowVerticalLines(false);
        grillaAnalisis.setShowHorizontalLines(true);
        grillaAnalisis.setGridColor(new Color(235, 240, 245));
        grillaAnalisis.setSelectionBackground(C_SELECCION);
        grillaAnalisis.setSelectionForeground(C_TEXTO_FUERTE);
        grillaAnalisis.setFillsViewportHeight(true);
        grillaAnalisis.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        grillaAnalisis.getTableHeader().setReorderingAllowed(false);
        grillaAnalisis.setBorder(BorderFactory.createEmptyBorder());

        JTableHeader header = grillaAnalisis.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(C_CABECERA_TBL);
        header.setForeground(C_TEXTO_SUAVE);
        header.setBorder(new MatteBorder(0, 0, 2, 0, C_BORDE));
        header.setPreferredSize(new Dimension(0, 40));

        jScrollPane1.setBorder(BorderFactory.createEmptyBorder());
        jScrollPane1.getViewport().setBackground(C_BLANCO);

        // Contador de registros
        lblContador.setForeground(C_TEXTO_SUAVE);
        lblContador.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblContador.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        JPanel pnlContadorContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlContadorContainer.setBackground(C_BLANCO);
        pnlContadorContainer.setBorder(new MatteBorder(1, 0, 0, 0, C_BORDE));
        pnlContadorContainer.add(lblContador);

        pnlTablaWrapper.removeAll();
        pnlTablaWrapper.setLayout(new BorderLayout());
        pnlTablaWrapper.add(lblTituloTabla, BorderLayout.NORTH);
        pnlTablaWrapper.add(jScrollPane1, BorderLayout.CENTER);
        pnlTablaWrapper.add(pnlContadorContainer, BorderLayout.SOUTH);

        pnlCuerpo.add(pnlTablaWrapper, BorderLayout.CENTER);
        pnlContenedorBlanco.add(pnlCuerpo, BorderLayout.CENTER);

        // FOOTER
        pnlFooter.setBackground(C_FONDO);
        pnlFooter.setBorder(new EmptyBorder(10, 16, 14, 16));
        pnlFooter.removeAll();
        pnlFooter.setLayout(new BorderLayout());
        
        configurarBoton(btnVerDetallesAnalisis, C_AZUL_MEDIO, "VER DETALLES", 160, 42);
        configurarBoton(btnImprimirAnalisis, C_VERDE, "IMPRIMIR", 150, 42);

        JPanel pnlFooterAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        pnlFooterAcciones.setOpaque(false);
        pnlFooterAcciones.add(btnVerDetallesAnalisis);
        pnlFooterAcciones.add(btnImprimirAnalisis);
        pnlFooter.add(pnlFooterAcciones, BorderLayout.EAST);

        // ARMADO FINAL
        this.removeAll();
        this.setLayout(new BorderLayout());
        this.add(pnlHeader, BorderLayout.NORTH);
        this.add(pnlContenedorBlanco, BorderLayout.CENTER);
        this.add(pnlFooter, BorderLayout.SOUTH);
        
        this.revalidate();
        this.repaint();
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
        btn.setBorder(new EmptyBorder(0, 0, 0, 16));

        ImageIcon ico = icon("/reportes/img/flecha_icon.png", 40, 40);
        if (ico != null) btn.setIcon(ico);
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setForeground(C_BLANCO);
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setForeground(C_HEADER_TEXT);
            }
        });
    }

    private ImageIcon icon(String ruta, int w, int h) {
        try {
            java.net.URL url = getClass().getResource(ruta);
            if (url != null) {
                Image img = new ImageIcon(url).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
                return new ImageIcon(img);
            }
        } catch (Exception e) { }
        return null;
    }

    // ══════════════════════════════════════════════════════════════
    //  RENDERERS DE TABLA
    // ══════════════════════════════════════════════════════════════
    private void aplicarRenderers() {
        DefaultTableCellRenderer renderizadorColores = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // Alineación según la columna
                if (column == 0 || column == 1 || column == 3) setHorizontalAlignment(SwingConstants.CENTER);
                else if (column == 5) setHorizontalAlignment(SwingConstants.RIGHT);
                else setHorizontalAlignment(SwingConstants.LEFT);

                setBorder(new EmptyBorder(0, 12, 0, 12));

                if (isSelected) {
                    setBackground(table.getSelectionBackground());
                    setForeground(table.getSelectionForeground());
                } else {
                    // Obtener el estado de la columna 6 (ESTADO)
                    Object estadoObj = table.getValueAt(row, 6);
                    String estado = (estadoObj != null) ? estadoObj.toString() : "";
                    
                    if ("GENERADO".equals(estado)) {
                        setBackground(C_VERDE_FILA);
                        setForeground(C_VERDE_TEXTO);
                    } else {
                        setBackground(row % 2 == 0 ? C_BLANCO : C_FILA_PAR);
                        setForeground(C_TEXTO_FUERTE);
                    }
                }
                return this;
            }
        };

        for (int i = 0; i < grillaAnalisis.getColumnCount(); i++) {
            grillaAnalisis.getColumnModel().getColumn(i).setCellRenderer(renderizadorColores);
        }

        // Anchos de columna
        int[] anchos = {70, 100, 280, 120, 200, 110, 80};
        for (int i = 0; i < anchos.length; i++) {
            grillaAnalisis.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
            grillaAnalisis.getColumnModel().getColumn(i).setMinWidth(anchos[i] - 20);
        }
        
        grillaAnalisis.getColumnModel().getColumn(0).setMaxWidth(90);
        grillaAnalisis.getColumnModel().getColumn(1).setMaxWidth(130);
        grillaAnalisis.getColumnModel().getColumn(3).setMaxWidth(150);
        grillaAnalisis.getColumnModel().getColumn(5).setMaxWidth(140);
        grillaAnalisis.getColumnModel().getColumn(6).setMaxWidth(90);
    }

    // ══════════════════════════════════════════════════════════════════
    //  UI BUILDER
    // ══════════════════════════════════════════════════════════════════
    private void initComponents() {
        pnlHeader       = new JPanel();
        lblTituloHeader = new JLabel("HISTORIAL GLOBAL DE ANÁLISIS");
        txtBuscar       = new JTextField();
        btnVolver       = new JButton();

        pnlContenedorBlanco = new JPanel();
        pnlCuerpo       = new JPanel();
        pnlTablaWrapper = new JPanel();
        lblTituloTabla  = new JLabel("ANÁLISIS REGISTRADOS");
        lblContador     = new JLabel("0 registros encontrados");
        pnlFooter       = new JPanel();
        
        grillaAnalisis  = new JTable();
        jScrollPane1    = new JScrollPane();
        btnVerDetallesAnalisis = new JButton();
        btnImprimirAnalisis    = new JButton();

        // Modelo con columna de ESTADO
        DefaultTableModel modelo = new DefaultTableModel(
            new Object[][]{},
            new String[]{"ID", "FECHA", "PACIENTE", "DNI", "OBRA SOCIAL", "TOTAL $", "ESTADO"}
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) {
                return c == 0 ? Integer.class : String.class;
            }
        };
        grillaAnalisis.setModel(modelo);
        jScrollPane1.setViewportView(grillaAnalisis);
    }
}