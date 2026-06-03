package vista;

import presentador.Controlador;
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

public class VistaAnalisis extends JPanel implements IVistaAnalisis {

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
    private final Color C_SELECCION    = new Color(220, 235, 250);

    // Componentes
    private JPanel pnlHeader, pnlCuerpo, pnlFooter, pnlTablaWrapper;
    private JLabel lblTituloHeader, lblContador;
    private JButton btnVerDetallesAnalisis, btnImprimirAnalisis, btnVolver;
    private JTable grillaAnalisis;
    private JScrollPane jScrollPane1;
    private JTextField txtBuscar;

    public VistaAnalisis() {
        initComponents();
        aplicarEstilo();
        registrarListeners();
    }

    // ══════════════════════════════════════════════════════════════════
    //  ESTILO Y UX
    // ══════════════════════════════════════════════════════════════════
    private void aplicarEstilo() {
        setBackground(C_FONDO);

        // ── HEADER (Azul institucional con flecha y buscador) ───────
        pnlHeader.setBackground(C_NAVY);
        pnlHeader.setBorder(new EmptyBorder(15, 30, 15, 30));
        
        lblTituloHeader.setForeground(C_BLANCO);
        lblTituloHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));

        estilizarCampoBuscador(txtBuscar);

        // ── BOTONES ─────────────────────────────────────────────────
        configurarBoton(btnVerDetallesAnalisis, C_AZUL_MEDIO, "☰ VER DETALLES", 185, 44);
        configurarBoton(btnImprimirAnalisis, C_VERDE, "⎙ IMPRIMIR INFORME", 200, 44);
        configurarBotonRetroceso(btnVolver);

        habilitarBotonVerDetalles(false);
        habilitarBotonImprimir(false);

        // ── TABLA ENVOLTORIO ────────────────────────────────────────
        pnlTablaWrapper.setBackground(C_BLANCO);
        pnlTablaWrapper.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(C_BORDE, 1, true),
            new EmptyBorder(1, 1, 1, 1)
        ));

        // Contador
        lblContador.setForeground(C_TEXTO_SUAVE);
        lblContador.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblContador.setBorder(new EmptyBorder(10, 15, 10, 15));

        // Configuración de la Grilla
        grillaAnalisis.setRowHeight(38);
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

        // Header de la tabla
        JTableHeader header = grillaAnalisis.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(C_CABECERA_TBL);
        header.setForeground(C_TEXTO_SUAVE);
        header.setBorder(new MatteBorder(1, 0, 2, 0, C_BORDE));
        header.setPreferredSize(new Dimension(0, 42));

        jScrollPane1.setBorder(BorderFactory.createEmptyBorder());
        jScrollPane1.getViewport().setBackground(C_BLANCO);

        // ── FOOTER ──────────────────────────────────────────────────
        pnlFooter.setBackground(C_FONDO);
        pnlFooter.setBorder(new EmptyBorder(15, 0, 0, 0));
    }

    private void estilizarCampoBuscador(JTextField tf) {
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setBackground(new Color(55, 85, 125)); // Azul oscuro transparente
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
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(w, h));
    }

    private void configurarBotonRetroceso(JButton btn) {
        btn.setText(" "); // Espacio para separar del icono
        btn.setBackground(C_NAVY);
        btn.setForeground(C_HEADER_TEXT);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(0, 0, 0, 20));

        ImageIcon ico = icon("/reportes/img/flecha_icon.png", 43, 43);
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
        } catch (Exception e) { /* silencioso */ }
        return null;
    }

    // ══════════════════════════════════════════════════════════════════
    //  UI BUILDER (Layout Estructurado)
    // ══════════════════════════════════════════════════════════════════
    private void initComponents() {
        pnlHeader       = new JPanel();
        lblTituloHeader = new JLabel("Historial Global de Análisis");
        txtBuscar       = new JTextField();
        btnVolver       = new JButton();

        pnlCuerpo       = new JPanel();
        pnlTablaWrapper = new JPanel();
        lblContador     = new JLabel("0 registros encontrados");
        
        grillaAnalisis  = new JTable();
        jScrollPane1    = new JScrollPane();

        pnlFooter       = new JPanel();
        btnVerDetallesAnalisis = new JButton();
        btnImprimirAnalisis    = new JButton();

        // Modelo Inicial de la Tabla (Ahora con 6 columnas)
        DefaultTableModel modelo = new DefaultTableModel(
            new Object[][]{},
            new String[]{"ID", "FECHA", "PACIENTE", "DNI", "OBRA SOCIAL", "TOTAL $"}
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) {
                return c == 0 ? Integer.class : String.class;
            }
        };
        grillaAnalisis.setModel(modelo);
        jScrollPane1.setViewportView(grillaAnalisis);

        // ── ROOT ─────────────────────────────────────────────────────
        setLayout(new BorderLayout());

        // ── HEADER ───────────────────────────────────────────────────
        pnlHeader.setLayout(new BorderLayout());
        
        JPanel pnlIzqHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlIzqHeader.setOpaque(false);
        pnlIzqHeader.add(btnVolver);
        
        lblTituloHeader.setHorizontalAlignment(SwingConstants.LEFT);
        lblTituloHeader.setBorder(new EmptyBorder(0, 10, 0, 0)); 
        pnlIzqHeader.add(lblTituloHeader);
        
        JPanel pnlDerHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pnlDerHeader.setOpaque(false);
        JLabel lblLupa = new JLabel("Buscar análisis:  ");
        lblLupa.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblLupa.setForeground(C_HEADER_TEXT);
        pnlDerHeader.add(lblLupa);
        pnlDerHeader.add(txtBuscar);
        
        pnlHeader.add(pnlIzqHeader, BorderLayout.WEST);
        pnlHeader.add(pnlDerHeader, BorderLayout.EAST);
        add(pnlHeader, BorderLayout.NORTH);

        // ── CUERPO ───────────────────────────────────────────────────
        pnlCuerpo.setBackground(C_FONDO);
        pnlCuerpo.setLayout(new BorderLayout());

        pnlTablaWrapper.setLayout(new BorderLayout());
        pnlTablaWrapper.add(jScrollPane1, BorderLayout.CENTER);
        
        // Panel para el contador (Abajo de la tabla)
        JPanel pnlContadorContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlContadorContainer.setBackground(C_BLANCO);
        pnlContadorContainer.add(lblContador);
        pnlTablaWrapper.add(pnlContadorContainer, BorderLayout.SOUTH);

        // Padding general del cuerpo
        JPanel wrapperCuerpo = new JPanel(new BorderLayout());
        wrapperCuerpo.setOpaque(false);
        wrapperCuerpo.setBorder(new EmptyBorder(25, 25, 25, 25));
        wrapperCuerpo.add(pnlTablaWrapper, BorderLayout.CENTER);

        // ── FOOTER ───────────────────────────────────────────────────
        pnlFooter.setLayout(new BorderLayout());
        JPanel pnlFooterAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        pnlFooterAcciones.setOpaque(false);
        pnlFooterAcciones.add(btnVerDetallesAnalisis);
        pnlFooterAcciones.add(btnImprimirAnalisis);
        
        pnlFooter.add(pnlFooterAcciones, BorderLayout.EAST);
        
        wrapperCuerpo.add(pnlFooter, BorderLayout.SOUTH);
        add(wrapperCuerpo, BorderLayout.CENTER);
    }

    // ══════════════════════════════════════════════════════════════
    //  RENDERERS DE TABLA
    // ══════════════════════════════════════════════════════════════
    private void aplicarRenderers() {
        DefaultTableCellRenderer center = crearRenderer(SwingConstants.CENTER);
        DefaultTableCellRenderer left   = crearRenderer(SwingConstants.LEFT);
        DefaultTableCellRenderer right  = crearRenderer(SwingConstants.RIGHT);

        grillaAnalisis.getColumnModel().getColumn(0).setCellRenderer(center); // ID
        grillaAnalisis.getColumnModel().getColumn(1).setCellRenderer(center); // FECHA
        grillaAnalisis.getColumnModel().getColumn(2).setCellRenderer(left);   // PACIENTE
        grillaAnalisis.getColumnModel().getColumn(3).setCellRenderer(center); // DNI
        grillaAnalisis.getColumnModel().getColumn(4).setCellRenderer(left);   // OBRA SOCIAL
        grillaAnalisis.getColumnModel().getColumn(5).setCellRenderer(right);  // TOTAL

        // Ajuste de proporciones para hacer lugar a Obra Social
        int[] anchos = {70, 120, 250, 130, 200, 120}; 
        for (int i = 0; i < anchos.length; i++) {
            grillaAnalisis.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }
        
        grillaAnalisis.getColumnModel().getColumn(0).setMaxWidth(90);
        grillaAnalisis.getColumnModel().getColumn(1).setMaxWidth(140);
        grillaAnalisis.getColumnModel().getColumn(3).setMaxWidth(160);
        grillaAnalisis.getColumnModel().getColumn(5).setMaxWidth(150);
    }

    private DefaultTableCellRenderer crearRenderer(int alineacion) {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setHorizontalAlignment(alineacion);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                if (!sel) {
                    setBackground(row % 2 == 0 ? C_BLANCO : C_FILA_PAR);
                    setForeground(C_TEXTO_FUERTE);
                }
                return this;
            }
        };
    }

    // ══════════════════════════════════════════════════════════════
    //  LISTENERS Y CONTROLADOR
    // ══════════════════════════════════════════════════════════════
    private void registrarListeners() {
        grillaAnalisis.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hay = grillaAnalisis.getSelectedRow() != -1;
                habilitarBotonVerDetalles(hay);
                habilitarBotonImprimir(hay);
            }
        });

        txtBuscar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e)  { disparar(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e)  { disparar(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { disparar(); }
            private void disparar() {
                if (controlador != null) controlador.buscarAnalisisAutomatico();
            }
        });
    }

    @Override public void ejecutar() { setVisible(true); }
    @Override public String getTextoBusqueda() { return txtBuscar.getText().trim(); }
    @Override public void habilitarBotonVerDetalles(boolean b) { btnVerDetallesAnalisis.setEnabled(b); }
    @Override public void habilitarBotonImprimir(boolean b) { btnImprimirAnalisis.setEnabled(b); }
    @Override public void mostrarMensaje(String m) { JOptionPane.showMessageDialog(this, m); }

    @Override
    public void setControlador(Controlador control) {
        this.controlador = control;
        btnVerDetallesAnalisis.addActionListener(control);
        btnImprimirAnalisis.addActionListener(control);
        btnVolver.addActionListener(control);
        
        btnVerDetallesAnalisis.setActionCommand(BTN_VER_DETALLES);
        btnImprimirAnalisis.setActionCommand(BTN_IMPRIMIR_ANALISIS);
        btnVolver.setActionCommand(BTN_VOLVER_VLA);
    }

    @Override
    public void cargarAnalisisEnTabla(ArrayList<Analisis> lista) {
        DefaultTableModel modelo = (DefaultTableModel) grillaAnalisis.getModel();
        modelo.setRowCount(0);
        
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");

        for (Analisis a : lista) {
            String obraSocialStr = (a.getObraSocial() != null && !a.getObraSocial().trim().isEmpty()) 
                                    ? a.getObraSocial() : "-";
                                    
            modelo.addRow(new Object[]{
                a.getIdAnalisis(),
                sdf.format(a.getFecha()),
                a.getPacienteNombreCompleto().toUpperCase(),
                a.getPacienteDni() != null ? a.getPacienteDni() : "-",
                obraSocialStr.toUpperCase(),
                String.format("$ %.2f", a.getPrecio())
            });
        }

        grillaAnalisis.setRowSorter(new TableRowSorter<>(modelo));
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
}