package vista.swing;

import vista.interfaces.IVistaDeterminaciones;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
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

    public VistaDeterminaciones() {
        super((java.awt.Frame) null, true);
        initComponents();
        configurarEsteticaPersonalizada();
        configurarBuscadorDeterminaciones();

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

    private void limpiarListeners(javax.swing.JButton btn) {
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

        // Usamos un TreeSet para mantener los índices únicos y ordenados de menor a mayor
        java.util.Set<Integer> filasExpandidas = new java.util.TreeSet<>();

        for (int fila : seleccionados) {
            filasExpandidas.add(fila);
            
            // Obtenemos el texto de la fila para saber si es un título (padre)
            String nombreFila = grillaDeterminaciones.getValueAt(fila, 1) != null ? 
                                grillaDeterminaciones.getValueAt(fila, 1).toString() : "";
            
            if (nombreFila.startsWith("---") && nombreFila.endsWith("---")) {
                // Es un TÍTULO. Agregamos las hijas hasta que termine la tabla o encontremos otro título.
                for (int i = fila + 1; i < grillaDeterminaciones.getRowCount(); i++) {
                    String subFila = grillaDeterminaciones.getValueAt(i, 1) != null ? 
                                     grillaDeterminaciones.getValueAt(i, 1).toString() : "";
                    
                    if (subFila.startsWith("---") && subFila.endsWith("---")) {
                        break; // Topamos con otro título, detenemos la selección en cascada
                    }
                    filasExpandidas.add(i);
                }
            }
        }

        // Convertimos el Set expandido en el array int[] final
        int[] resultado = new int[filasExpandidas.size()];
        int indice = 0;
        for (Integer f : filasExpandidas) {
            resultado[indice++] = f;
        }
        return resultado;
    }
    
    @Override
    public void cerrarPantalla() {
        this.dispose(); // Cierre seguro del JDialog modal
    }

    @Override
    public int confirmarAccion(String mensaje, String titulo) {
        return JOptionPane.showConfirmDialog(this, mensaje, titulo, JOptionPane.YES_NO_OPTION);
    }

    @Override
    public void cargarTablaConTitulos(List<Determinacion> lista) {
        DefaultTableModel model = (DefaultTableModel) grillaDeterminaciones.getModel();
        model.setRowCount(0);
        for (Determinacion d : lista) {
            String codFila = (d.getCodigo() == null || d.getCodigo().isEmpty()) ? "" : d.getCodigo();
            model.addRow(new Object[]{codFila, d.getNombre()});
        }
        txtDeterminacion.setText("");
        if (popupSugerencias != null) popupSugerencias.setVisible(false);
        txtDeterminacion.requestFocus();
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

        if (modeloSugerencias.isEmpty()) { popupSugerencias.setVisible(false); return; }

        int width  = txtDeterminacion.getWidth();
        int height = Math.min(200, sugerencias.size() * 30 + 5);
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
        listaSugerencias  = new JList<>(modeloSugerencias);
        listaSugerencias.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        listaSugerencias.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaSugerencias.setFixedCellHeight(30);
        listaSugerencias.setBackground(COLOR_BLANCO);
        listaSugerencias.setSelectionBackground(new Color(210, 232, 250));
        listaSugerencias.setSelectionForeground(COLOR_AZUL_OSCURO);

        txtDeterminacion.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (popupSugerencias != null && popupSugerencias.isVisible()
                        && !modeloSugerencias.isEmpty()) {
                    int index = listaSugerencias.getSelectedIndex();
                    int size  = modeloSugerencias.getSize();
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
                if (texto.length() >= 1) { if (presenter != null) presenter.onBuscarSugerencias(); }
                else if (popupSugerencias != null) popupSugerencias.setVisible(false);
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
    //  ESTÉTICA
    // ════════════════════════════════════════════════════════════════

    private void configurarEsteticaPersonalizada() {
        setTitle("Agregar Prácticas — BIOTEC LIS");
        setMinimumSize(new Dimension(750, 580));
        setPreferredSize(new Dimension(900, 680));
        setLocationRelativeTo(null);

        pnlHeader.setBackground(COLOR_AZUL_OSCURO);

        lblTitulo.setForeground(new Color(160, 200, 230));
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTitulo.setText("BÚSQUEDA RÁPIDA — CÓDIGO O NOMBRE");

        jLabel1.setForeground(COLOR_BLANCO);
        jLabel1.setFont(new Font("Segoe UI", Font.BOLD, 13));
        jLabel1.setText("Práctica:");

        txtDeterminacion.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDeterminacion.setBackground(new Color(255, 255, 255, 30));
        txtDeterminacion.setForeground(COLOR_BLANCO);
        txtDeterminacion.setCaretColor(COLOR_BLANCO);
        txtDeterminacion.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(100, 160, 210)),
            new EmptyBorder(4, 8, 4, 8)
        ));
        txtDeterminacion.setOpaque(false);

        estilizarBoton(btnAgregarDeterminacion, COLOR_AZUL_MEDIO, "＋  AGREGAR");
        btnAgregarDeterminacion.setPreferredSize(new Dimension(140, 36));

        pnlTablaContainer.setBackground(COLOR_BLANCO);
        pnlTablaContainer.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(16, 20, 10, 20),
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

        grillaDeterminaciones.setRowHeight(32);
        grillaDeterminaciones.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        grillaDeterminaciones.setGridColor(new Color(230, 238, 245));
        grillaDeterminaciones.setShowHorizontalLines(true);
        grillaDeterminaciones.setShowVerticalLines(false);
        grillaDeterminaciones.setSelectionBackground(new Color(210, 232, 250));
        grillaDeterminaciones.setSelectionForeground(COLOR_AZUL_OSCURO);
        grillaDeterminaciones.setIntercellSpacing(new Dimension(0, 1));
        grillaDeterminaciones.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        grillaDeterminaciones.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        grillaDeterminaciones.getTableHeader().setBackground(COLOR_CABECERA_TBL);
        grillaDeterminaciones.getTableHeader().setForeground(COLOR_AZUL_OSCURO);
        grillaDeterminaciones.getTableHeader().setPreferredSize(new Dimension(0, 36));
        grillaDeterminaciones.getTableHeader().setBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, COLOR_AZUL_MEDIO));
        grillaDeterminaciones.getTableHeader().setReorderingAllowed(false);

        grillaDeterminaciones.getColumnModel().getColumn(0).setPreferredWidth(90);
        grillaDeterminaciones.getColumnModel().getColumn(0).setMaxWidth(110);
        grillaDeterminaciones.getColumnModel().getColumn(0).setMinWidth(70);
        grillaDeterminaciones.getColumnModel().getColumn(1).setPreferredWidth(600);

        // Renderer columna CÓDIGO
        grillaDeterminaciones.getColumnModel().getColumn(0).setCellRenderer(
            new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(javax.swing.JTable t,
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
                        setBackground(sel ? new Color(210, 232, 250) : (row % 2 == 0 ? COLOR_BLANCO : COLOR_FILA_PAR));
                        setForeground(sel ? COLOR_AZUL_OSCURO : COLOR_TEXTO_LABEL);
                    }
                    return this;
                }
            }
        );

        // Renderer columna DETERMINACIÓN
        grillaDeterminaciones.getColumnModel().getColumn(1).setCellRenderer(
            new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(javax.swing.JTable t,
                        Object v, boolean sel, boolean foc, int row, int col) {
                    super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                    String nombreFila = t.getModel().getValueAt(row, 1) != null
                            ? t.getModel().getValueAt(row, 1).toString() : "";
                    boolean esSubtitulo = nombreFila.startsWith("---") && nombreFila.endsWith("---");
                    if (esSubtitulo) {
                        setText(nombreFila.replace("---", "").trim());
                        setFont(new Font("Segoe UI", Font.BOLD, 12));
                        setForeground(COLOR_AZUL_MEDIO);
                        setBackground(sel ? new Color(210, 232, 250) : new Color(235, 242, 248)); // Coloriza si el padre está seleccionado
                        setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(200, 215, 230)),
                            new EmptyBorder(0, 10, 0, 10)
                        ));
                    } else {
                        setText(nombreFila);
                        setHorizontalAlignment(SwingConstants.LEFT);
                        setFont(new Font("Segoe UI", Font.PLAIN, 13));
                        setBorder(new EmptyBorder(0, 10, 0, 10));
                        setBackground(sel ? new Color(210, 232, 250) : (row % 2 == 0 ? COLOR_BLANCO : COLOR_FILA_PAR));
                        setForeground(sel ? COLOR_AZUL_OSCURO : COLOR_TEXTO_LABEL);
                    }
                    return this;
                }
            }
        );

        jScrollPane1.setBorder(BorderFactory.createEmptyBorder());
        jScrollPane1.getViewport().setBackground(COLOR_BLANCO);

        pnlFooter.setBackground(COLOR_BLANCO);
        pnlFooter.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(1, 0, 0, 0, COLOR_BORDE),
            new EmptyBorder(12, 20, 12, 20)
        ));

        estilizarBoton(btnContinuar, COLOR_VERDE, "CONTINUAR  ›");
        estilizarBoton(btnEliminar,  COLOR_ROJO,  "✕  ELIMINAR SELECCIÓN");
        btnContinuar.setPreferredSize(new Dimension(170, 44));
        btnEliminar.setPreferredSize(new Dimension(250, 44));
    }

    private void estilizarBoton(javax.swing.JButton btn, Color bg, String texto) {
        btn.setText(texto);
        btn.setBackground(bg);
        btn.setForeground(COLOR_BLANCO);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }

    // ════════════════════════════════════════════════════════════════
    //  initComponents 
    // ════════════════════════════════════════════════════════════════

    @SuppressWarnings("unchecked")
    private void initComponents() {
        java.awt.GridBagConstraints gbc;

        pnlHeader             = new javax.swing.JPanel();
        lblTitulo             = new javax.swing.JLabel();
        jLabel1               = new javax.swing.JLabel();
        txtDeterminacion      = new javax.swing.JTextField();
        btnAgregarDeterminacion = new javax.swing.JButton();
        pnlTablaContainer     = new javax.swing.JPanel();
        jScrollPane1          = new javax.swing.JScrollPane();
        grillaDeterminaciones = new javax.swing.JTable();
        pnlFooter             = new javax.swing.JPanel();
        btnContinuar          = new javax.swing.JButton();
        btnEliminar           = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(COLOR_FONDO);
        getContentPane().setLayout(new BorderLayout(0, 0));

        // ── HEADER ───────────────────────────────────────────────────
        pnlHeader.setBackground(new java.awt.Color(0, 51, 102));
        pnlHeader.setLayout(new java.awt.GridBagLayout());
        pnlHeader.setBorder(new EmptyBorder(10, 25, 10, 25));

        gbc = new java.awt.GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        gbc.insets = new java.awt.Insets(0, 0, 4, 0);
        pnlHeader.add(lblTitulo, gbc);

        gbc = new java.awt.GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        gbc.insets = new java.awt.Insets(0, 0, 0, 12);
        pnlHeader.add(jLabel1, gbc);

        txtDeterminacion.setPreferredSize(new Dimension(400, 32));
        gbc = new java.awt.GridBagConstraints();
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new java.awt.Insets(0, 0, 0, 12);
        pnlHeader.add(txtDeterminacion, gbc);

        gbc = new java.awt.GridBagConstraints();
        gbc.gridx = 2; gbc.gridy = 1;
        gbc.anchor = java.awt.GridBagConstraints.EAST;
        pnlHeader.add(btnAgregarDeterminacion, gbc);

        getContentPane().add(pnlHeader, BorderLayout.NORTH);

        // ── TABLA ────────────────────────────────────────────────────
        pnlTablaContainer.setBackground(java.awt.Color.WHITE);
        pnlTablaContainer.setLayout(new BorderLayout());

        grillaDeterminaciones.setModel(new DefaultTableModel(
            new Object[][]{},
            new String[]{"CÓDIGO", "DETERMINACIÓN"}
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });

        jScrollPane1.setViewportView(grillaDeterminaciones);
        pnlTablaContainer.add(jScrollPane1, BorderLayout.CENTER);

        getContentPane().add(pnlTablaContainer, BorderLayout.CENTER);

        // ── FOOTER ───────────────────────────────────────────────────
        pnlFooter.setLayout(new BorderLayout());

        javax.swing.JPanel pnlLeft = new javax.swing.JPanel(new FlowLayout(FlowLayout.LEFT, 20, 17));
        pnlLeft.setOpaque(false);
        pnlLeft.add(btnEliminar);

        javax.swing.JPanel pnlRight = new javax.swing.JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 17));
        pnlRight.setOpaque(false);
        pnlRight.add(btnContinuar);

        pnlFooter.add(pnlLeft,  BorderLayout.WEST);
        pnlFooter.add(pnlRight, BorderLayout.EAST);

        getContentPane().add(pnlFooter, BorderLayout.SOUTH);

        pack();
    }

    // ── Variables ────────────────────────────────────────────────────
    private javax.swing.JButton     btnAgregarDeterminacion;
    private javax.swing.JButton     btnContinuar;
    private javax.swing.JButton     btnEliminar;
    private javax.swing.JTable      grillaDeterminaciones;
    private javax.swing.JLabel      jLabel1;
    private javax.swing.JLabel      lblTitulo;
    private javax.swing.JPanel      pnlHeader;
    private javax.swing.JPanel      pnlTablaContainer;
    private javax.swing.JPanel      pnlFooter;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField  txtDeterminacion;
}