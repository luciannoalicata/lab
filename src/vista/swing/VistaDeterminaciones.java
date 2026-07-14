package vista.swing;

import vista.interfaces.IVistaDeterminaciones;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import modelo.Determinacion;
import presentador.DeterminacionesPresenter;

public class VistaDeterminaciones extends javax.swing.JDialog implements IVistaDeterminaciones {

    private DeterminacionesPresenter presenter;

    private JPopupMenu popupSugerencias;
    private JList<String> listaSugerencias;
    private DefaultListModel<String> modeloSugerencias;
    private boolean cargandoDatos = false;

    // ── Paleta BIOTEC Profesional ────────────────────────────────────
    private final Color COLOR_AZUL_OSCURO  = new Color(0, 51, 102);
    private final Color COLOR_AZUL_MEDIO   = new Color(0, 102, 153);
    private final Color COLOR_VERDE        = new Color(0, 153, 102);
    private final Color COLOR_ROJO         = new Color(220, 70, 70);
    private final Color COLOR_FONDO        = new Color(245, 248, 250);
    private final Color COLOR_BLANCO       = Color.WHITE;
    private final Color COLOR_BORDE        = new Color(210, 220, 230);
    private final Color COLOR_CABECERA_TBL = new Color(235, 242, 248);
    private final Color COLOR_FILA_PAR     = new Color(250, 253, 255);
    private final Color COLOR_TEXTO_LABEL  = new Color(60, 80, 100);
    private final Color COLOR_SELECCION    = new Color(200, 225, 245);

    public VistaDeterminaciones() {
        super((java.awt.Frame) null, true);
        initComponents();
        configurarEsteticaProfesional();
        configurarBuscadorDeterminaciones();
        setMinimumSize(new Dimension(750, 550));
        setLocationRelativeTo(null); // Centrar en pantalla

        ((DefaultTableModel) grillaDeterminaciones.getModel()).setRowCount(0);

        txtDeterminacion.addActionListener(e -> {
            if (popupSugerencias != null && popupSugerencias.isVisible()
                    && !listaSugerencias.isSelectionEmpty()) {
                seleccionarSugerencia();
            } else {
                btnAgregarDeterminacion.doClick();
            }
        });

        txtDeterminacion.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e)  { buscar(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e)  { buscar(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { buscar(); }
            private void buscar() { if (presenter != null) presenter.onBuscarSugerencias(); }
        });
    }

    // ════════════════════════════════════════════════════════════════
    //  INTERFAZ MVP
    // ════════════════════════════════════════════════════════════════

    @Override
    public void setPresenter(DeterminacionesPresenter presenter) {
        this.presenter = presenter;
        
        limpiarListeners(btnAgregarDeterminacion);
        limpiarListeners(btnEliminar);
        limpiarListeners(btnContinuar);
        
        btnAgregarDeterminacion.addActionListener(e -> presenter.onAgregarDeterminacion());
        btnEliminar.addActionListener(e -> presenter.onEliminarDeterminacion());
        btnContinuar.addActionListener(e -> presenter.onContinuar());
    }

    private void limpiarListeners(JButton btn) {
        for (java.awt.event.ActionListener al : btn.getActionListeners()) {
            btn.removeActionListener(al);
        }
    }

    @Override public void ejecutar()                     { setVisible(true); }
    @Override public void limpiarFocos()                 { this.requestFocusInWindow(); }
    @Override public void limpiarCampos()                { txtDeterminacion.setText(""); }
    @Override public void mostrarMensaje(String mensaje) { JOptionPane.showMessageDialog(this, mensaje); }
    @Override public String getDeterminacion()           { return txtDeterminacion.getText().trim(); }

    @Override
    public int[] getFilasSeleccionadas() {
        int[] seleccionados = grillaDeterminaciones.getSelectedRows();
        if (seleccionados.length == 0) return new int[0];

        Set<Integer> filasExpandidas = new TreeSet<>();

        for (int fila : seleccionados) {
            filasExpandidas.add(fila);
            
            String nombreFila = grillaDeterminaciones.getValueAt(fila, 1) != null ? 
                                grillaDeterminaciones.getValueAt(fila, 1).toString() : "";
            
            if (nombreFila.startsWith("---") && nombreFila.endsWith("---")) {
                for (int i = fila + 1; i < grillaDeterminaciones.getRowCount(); i++) {
                    String subFila = grillaDeterminaciones.getValueAt(i, 1) != null ? 
                                     grillaDeterminaciones.getValueAt(i, 1).toString() : "";
                    
                    if (subFila.startsWith("---") && subFila.endsWith("---")) {
                        break;
                    }
                    filasExpandidas.add(i);
                }
            }
        }

        int[] resultado = new int[filasExpandidas.size()];
        int indice = 0;
        for (Integer f : filasExpandidas) {
            resultado[indice++] = f;
        }
        return resultado;
    }
    
    @Override
    public void cerrarPantalla() {
        this.dispose();
    }

    @Override
    public int confirmarAccion(String mensaje, String titulo) {
        return JOptionPane.showConfirmDialog(this, mensaje, titulo, JOptionPane.YES_NO_OPTION);
    }

    @Override
    public void cargarTablaConTitulos(List<Determinacion> lista) {
        cargandoDatos = true;
        
        DefaultTableModel model = (DefaultTableModel) grillaDeterminaciones.getModel();
        model.setRowCount(0);
        for (Determinacion d : lista) {
            String codFila = (d.getCodigo() == null || d.getCodigo().isEmpty()) ? "" : d.getCodigo();
            model.addRow(new Object[]{codFila, d.getNombre()});
        }
        
        grillaDeterminaciones.clearSelection();
        txtDeterminacion.setText("");
        if (popupSugerencias != null) popupSugerencias.setVisible(false);
        txtDeterminacion.requestFocus();
        
        cargandoDatos = false;
    }

    @Override
    public void mostrarSugerencias(List<Determinacion> sugerencias) {
        if (popupSugerencias == null) {
            popupSugerencias = new JPopupMenu();
            popupSugerencias.setFocusable(false);
            popupSugerencias.setBorder(BorderFactory.createEmptyBorder());
            JScrollPane scroll = new JScrollPane(listaSugerencias);
            scroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_AZUL_MEDIO, 1),
                new EmptyBorder(2, 0, 2, 0)
            ));
            popupSugerencias.add(scroll);
        }

        modeloSugerencias.clear();
        for (Determinacion d : sugerencias)
            modeloSugerencias.addElement(d.getCodigo() + " - " + d.getNombre());

        if (modeloSugerencias.isEmpty()) { 
            popupSugerencias.setVisible(false); 
            return; 
        }

        int width = txtDeterminacion.getWidth();
        int height = Math.min(180, sugerencias.size() * 28 + 5);
        popupSugerencias.setPopupSize(width, height);
        popupSugerencias.show(txtDeterminacion, 0, txtDeterminacion.getHeight());
        txtDeterminacion.requestFocusInWindow();
        listaSugerencias.setSelectedIndex(0);
    }

    // ════════════════════════════════════════════════════════════════
    //  BUSCADOR
    // ════════════════════════════════════════════════════════════════

    private void configurarBuscadorDeterminaciones() {
        modeloSugerencias = new DefaultListModel<>();
        listaSugerencias = new JList<>(modeloSugerencias);
        listaSugerencias.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        listaSugerencias.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaSugerencias.setFixedCellHeight(28);
        listaSugerencias.setBackground(COLOR_BLANCO);
        listaSugerencias.setSelectionBackground(new Color(210, 232, 250));
        listaSugerencias.setSelectionForeground(COLOR_AZUL_OSCURO);

        txtDeterminacion.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (popupSugerencias != null && popupSugerencias.isVisible()
                        && !modeloSugerencias.isEmpty()) {
                    int index = listaSugerencias.getSelectedIndex();
                    int size = modeloSugerencias.getSize();
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_DOWN:
                            index = (index + 1) % size;
                            listaSugerencias.setSelectedIndex(index);
                            listaSugerencias.ensureIndexIsVisible(index);
                            e.consume(); break;
                        case KeyEvent.VK_UP:
                            index = (index - 1 + size) % size;
                            listaSugerencias.setSelectedIndex(index);
                            listaSugerencias.ensureIndexIsVisible(index);
                            e.consume(); break;
                        case KeyEvent.VK_ENTER:
                            if (index != -1) { seleccionarSugerencia(); e.consume(); } break;
                        case KeyEvent.VK_ESCAPE:
                            popupSugerencias.setVisible(false); break;
                    }
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {
                int k = e.getKeyCode();
                if (k == KeyEvent.VK_DOWN || k == KeyEvent.VK_UP || k == KeyEvent.VK_ENTER) return;
                String texto = txtDeterminacion.getText().trim();
                if (texto.length() >= 1) { 
                    if (presenter != null) presenter.onBuscarSugerencias(); 
                } else if (popupSugerencias != null) {
                    popupSugerencias.setVisible(false);
                }
            }
        });

        listaSugerencias.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { seleccionarSugerencia(); }
        });
    }

    private void seleccionarSugerencia() {
        String seleccion = listaSugerencias.getSelectedValue();
        if (seleccion != null) {
            txtDeterminacion.setText(seleccion.split(" - ")[0]);
            if (popupSugerencias != null) popupSugerencias.setVisible(false);
            btnAgregarDeterminacion.doClick();
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  ESTÉTICA PROFESIONAL - Diseño Moderno y Responsive
    // ════════════════════════════════════════════════════════════════

    private void configurarEsteticaProfesional() {
        setTitle("Agregar Prácticas — BIOTEC");
        setPreferredSize(new Dimension(850, 620));
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_FONDO);

        // ── HEADER ──────────────────────────────────────────────────────
        pnlHeader.setBackground(COLOR_AZUL_OSCURO);
        pnlHeader.setBorder(new EmptyBorder(12, 24, 12, 24));
        pnlHeader.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        lblTitulo.setForeground(new Color(160, 200, 230));
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTitulo.setText("BÚSQUEDA RÁPIDA — CÓDIGO O NOMBRE");
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
        gbc.insets = new Insets(0, 0, 4, 0);
        pnlHeader.add(lblTitulo, gbc);

        jLabel1.setForeground(COLOR_BLANCO);
        jLabel1.setFont(new Font("Segoe UI", Font.BOLD, 13));
        jLabel1.setText("Práctica:");
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        gbc.insets = new Insets(0, 0, 0, 12);
        pnlHeader.add(jLabel1, gbc);

        txtDeterminacion.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDeterminacion.setBackground(new Color(255, 255, 255, 30));
        txtDeterminacion.setForeground(COLOR_BLANCO);
        txtDeterminacion.setCaretColor(COLOR_BLANCO);
        txtDeterminacion.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(100, 160, 210)),
            new EmptyBorder(6, 10, 6, 10)
        ));
        txtDeterminacion.setOpaque(false);
        txtDeterminacion.setPreferredSize(new Dimension(350, 34));
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 0, 12);
        pnlHeader.add(txtDeterminacion, gbc);

        configurarBoton(btnAgregarDeterminacion, COLOR_AZUL_MEDIO, "AGREGAR");
        btnAgregarDeterminacion.setPreferredSize(new Dimension(130, 34));
        gbc.gridx = 2; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        pnlHeader.add(btnAgregarDeterminacion, gbc);

        getContentPane().add(pnlHeader, BorderLayout.NORTH);

        // ── TABLA ──────────────────────────────────────────────────────
        pnlTablaContainer.setBackground(COLOR_BLANCO);
        pnlTablaContainer.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(12, 16, 10, 16),
            BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(COLOR_BORDE, 1, true),
                    "  Determinaciones Seleccionadas  ",
                    javax.swing.border.TitledBorder.LEFT,
                    javax.swing.border.TitledBorder.TOP,
                    new Font("Segoe UI", Font.BOLD, 12),
                    COLOR_AZUL_OSCURO
                ),
                new EmptyBorder(0, 0, 0, 0)
            )
        ));
        pnlTablaContainer.setLayout(new BorderLayout());

        grillaDeterminaciones.setRowHeight(32);
        grillaDeterminaciones.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        grillaDeterminaciones.setGridColor(new Color(230, 238, 245));
        grillaDeterminaciones.setShowHorizontalLines(true);
        grillaDeterminaciones.setShowVerticalLines(false);
        grillaDeterminaciones.setSelectionBackground(COLOR_SELECCION);
        grillaDeterminaciones.setSelectionForeground(COLOR_AZUL_OSCURO);
        grillaDeterminaciones.setIntercellSpacing(new Dimension(0, 1));
        grillaDeterminaciones.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        grillaDeterminaciones.setFillsViewportHeight(true);

        grillaDeterminaciones.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        grillaDeterminaciones.getTableHeader().setBackground(COLOR_CABECERA_TBL);
        grillaDeterminaciones.getTableHeader().setForeground(COLOR_AZUL_OSCURO);
        grillaDeterminaciones.getTableHeader().setPreferredSize(new Dimension(0, 34));
        grillaDeterminaciones.getTableHeader().setBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, COLOR_AZUL_MEDIO));
        grillaDeterminaciones.getTableHeader().setReorderingAllowed(false);

        grillaDeterminaciones.getColumnModel().getColumn(0).setPreferredWidth(90);
        grillaDeterminaciones.getColumnModel().getColumn(0).setMaxWidth(110);
        grillaDeterminaciones.getColumnModel().getColumn(0).setMinWidth(70);
        grillaDeterminaciones.getColumnModel().getColumn(1).setPreferredWidth(550);

        aplicarRenderers();

        jScrollPane1.setBorder(BorderFactory.createEmptyBorder());
        jScrollPane1.getViewport().setBackground(COLOR_BLANCO);

        pnlTablaContainer.add(jScrollPane1, BorderLayout.CENTER);
        getContentPane().add(pnlTablaContainer, BorderLayout.CENTER);

        // ── FOOTER ──────────────────────────────────────────────────────
        pnlFooter.setBackground(COLOR_BLANCO);
        pnlFooter.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(1, 0, 0, 0, COLOR_BORDE),
            new EmptyBorder(10, 16, 10, 16)
        ));
        pnlFooter.setLayout(new BorderLayout());

        configurarBoton(btnEliminar, COLOR_ROJO, "ELIMINAR PRÁCTICA");
        btnEliminar.setPreferredSize(new Dimension(200, 38));

        configurarBoton(btnContinuar, COLOR_VERDE, "CONTINUAR");
        btnContinuar.setPreferredSize(new Dimension(150, 38));

        JPanel pnlLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlLeft.setOpaque(false);
        pnlLeft.add(btnEliminar);

        JPanel pnlRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlRight.setOpaque(false);
        pnlRight.add(btnContinuar);

        pnlFooter.add(pnlLeft, BorderLayout.WEST);
        pnlFooter.add(pnlRight, BorderLayout.EAST);
        getContentPane().add(pnlFooter, BorderLayout.SOUTH);

        pack();
    }

    private void configurarBoton(JButton btn, Color bg, String texto) {
        btn.setText(texto);
        btn.setBackground(bg);
        btn.setForeground(COLOR_BLANCO);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }

    private void aplicarRenderers() {
        // Renderer columna CÓDIGO
        grillaDeterminaciones.getColumnModel().getColumn(0).setCellRenderer(
            new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable t,
                        Object v, boolean sel, boolean foc, int row, int col) {
                    super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                    String nombreFila = t.getModel().getValueAt(row, 1) != null
                            ? t.getModel().getValueAt(row, 1).toString() : "";
                    boolean esSubtitulo = nombreFila.startsWith("---") && nombreFila.endsWith("---");
                    if (esSubtitulo) {
                        setText("");
                        setBackground(new Color(235, 242, 248));
                        setBorder(BorderFactory.createMatteBorder(1, 4, 1, 0, COLOR_AZUL_MEDIO));
                    } else {
                        setHorizontalAlignment(SwingConstants.CENTER);
                        setFont(new Font("Segoe UI", Font.PLAIN, 13));
                        setBackground(sel ? COLOR_SELECCION : (row % 2 == 0 ? COLOR_BLANCO : COLOR_FILA_PAR));
                        setForeground(sel ? COLOR_AZUL_OSCURO : COLOR_TEXTO_LABEL);
                        setBorder(new EmptyBorder(0, 4, 0, 4));
                    }
                    return this;
                }
            }
        );

        // Renderer columna DETERMINACIÓN
        grillaDeterminaciones.getColumnModel().getColumn(1).setCellRenderer(
            new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable t,
                        Object v, boolean sel, boolean foc, int row, int col) {
                    super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                    String nombreFila = t.getModel().getValueAt(row, 1) != null
                            ? t.getModel().getValueAt(row, 1).toString() : "";
                    boolean esSubtitulo = nombreFila.startsWith("---") && nombreFila.endsWith("---");
                    if (esSubtitulo) {
                        setText(nombreFila.replace("---", "").trim());
                        setFont(new Font("Segoe UI", Font.BOLD, 12));
                        setForeground(COLOR_AZUL_MEDIO);
                        setBackground(sel ? COLOR_SELECCION : new Color(235, 242, 248));
                        setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(200, 215, 230)),
                            new EmptyBorder(0, 10, 0, 10)
                        ));
                    } else {
                        setText(nombreFila);
                        setHorizontalAlignment(SwingConstants.LEFT);
                        setFont(new Font("Segoe UI", Font.PLAIN, 13));
                        setBorder(new EmptyBorder(0, 10, 0, 10));
                        setBackground(sel ? COLOR_SELECCION : (row % 2 == 0 ? COLOR_BLANCO : COLOR_FILA_PAR));
                        setForeground(sel ? COLOR_AZUL_OSCURO : COLOR_TEXTO_LABEL);
                    }
                    return this;
                }
            }
        );
    }

    // ════════════════════════════════════════════════════════════════
    //  initComponents
    // ════════════════════════════════════════════════════════════════

    @SuppressWarnings("unchecked")
    private void initComponents() {
        pnlHeader = new JPanel();
        lblTitulo = new JLabel();
        jLabel1 = new JLabel();
        txtDeterminacion = new JTextField();
        btnAgregarDeterminacion = new JButton();
        pnlTablaContainer = new JPanel();
        jScrollPane1 = new JScrollPane();
        grillaDeterminaciones = new JTable();
        pnlFooter = new JPanel();
        btnContinuar = new JButton();
        btnEliminar = new JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout(0, 0));

        grillaDeterminaciones.setModel(new DefaultTableModel(
            new Object[][]{},
            new String[]{"CÓDIGO", "DETERMINACIÓN"}
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });

        jScrollPane1.setViewportView(grillaDeterminaciones);
        pnlTablaContainer.setLayout(new BorderLayout());
        pnlTablaContainer.add(jScrollPane1, BorderLayout.CENTER);

        pnlFooter.setLayout(new BorderLayout());
    }

    // ── Variables ────────────────────────────────────────────────────
    private JButton btnAgregarDeterminacion;
    private JButton btnContinuar;
    private JButton btnEliminar;
    private JTable grillaDeterminaciones;
    private JLabel jLabel1;
    private JLabel lblTitulo;
    private JPanel pnlHeader;
    private JPanel pnlTablaContainer;
    private JPanel pnlFooter;
    private JScrollPane jScrollPane1;
    private JTextField txtDeterminacion;
}