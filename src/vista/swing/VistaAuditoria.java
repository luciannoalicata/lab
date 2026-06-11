package vista.swing;

import vista.interfaces.IVistaAuditoria;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import modelo.Auditoria;
import presentador.AuditoriaPresenter;

/**
 * Vista Auditoría de Seguridad - BIOTEC LIS
 * Diseño consistente con VistaPaciente y VistaGestionUsuarios
 */
public class VistaAuditoria extends JPanel implements IVistaAuditoria {

    private AuditoriaPresenter presenter;    
    private ArrayList<Auditoria> datosActuales;

    // ── Paleta BIOTEC Minimalista ────────────────────────────────────
    private final Color C_NAVY         = new Color(10, 25, 47);    
    private final Color C_FONDO        = new Color(238, 242, 246);
    private final Color C_BLANCO       = Color.WHITE;
    private final Color C_TEXTO_FUERTE = new Color(40, 50, 60);
    private final Color C_TEXTO_SUAVE  = new Color(100, 115, 130);
    private final Color C_BORDE        = new Color(215, 225, 235);
    private final Color C_AZUL_MEDIO   = new Color(30, 110, 180);
    private final Color C_VERDE        = new Color(35, 160, 115);
    private final Color C_CAMPO        = new Color(250, 252, 254);
    private final Color C_CABECERA_TBL = new Color(245, 248, 252);
    private final Color C_FILA_PAR     = new Color(252, 254, 255);
    private final Color C_HEADER_TEXT  = new Color(175, 205, 235);
    private final Color C_GRIS_CLARO   = new Color(245, 248, 250);

    public VistaAuditoria() {
        initComponents();
        aplicarEstilo();

        btnDetallarCambios.setEnabled(false);
        grillaAuditoria.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                btnDetallarCambios.setEnabled(grillaAuditoria.getSelectedRow() != -1);
            }
        });
    }

    // ══════════════════════════════════════════════════════════════════
    //  INTERFAZ IVistaAuditoria - MÉTODOS MVP
    // ══════════════════════════════════════════════════════════════════

    @Override
    public void setPresenter(AuditoriaPresenter presenter) {
        this.presenter = presenter;
        
        btnFiltrarFecha.addActionListener(e -> presenter.onFiltrarFecha());
        btnFiltrarUsuario.addActionListener(e -> presenter.onFiltrarUsuario());
        btnDetallarCambios.addActionListener(e -> presenter.onDetallarCambios());
        btnVolver.addActionListener(e -> presenter.onVolver());
    }

    @Override public void ejecutar() { setVisible(true); }

    @Override
    public void limpiarFocos() {
        this.requestFocusInWindow();
    }

    @Override
    public int confirmarAccion(String mensaje, String titulo) {
        return JOptionPane.showConfirmDialog(this, mensaje, titulo, JOptionPane.YES_NO_OPTION);
    }

    @Override 
    public void mostrarDetalleCambios(String titulo, String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, JOptionPane.INFORMATION_MESSAGE);
    }

    @Override public void mostrarMensaje(String mensaje) { 
        JOptionPane.showMessageDialog(this, mensaje); 
    }
    
    @Override public Date getFechaSeleccionada() { 
        return jdFechaFiltro.getDate(); 
    }

    @Override
    public void cargarTabla(ArrayList<Auditoria> lista) {
        this.datosActuales = lista;
        DefaultTableModel m = (DefaultTableModel) grillaAuditoria.getModel();
        m.setRowCount(0);
        if (lista != null) {
            for (Auditoria a : lista) {
                m.addRow(new Object[]{
                    a.getUsuarioNombre(),
                    a.getFechaHora(),
                    a.getAccion(),
                    a.getTablaAfectada(),
                    a.getDetalle()
                });
            }
        }
    }

    @Override
    public void cargarComboUsuarios(List<String> usuarios) {
        cbxUsuario.removeAllItems();
        cbxUsuario.addItem("Todos");
        for (String u : usuarios) cbxUsuario.addItem(u);
    }

    @Override
    public String getUsuarioSeleccionado() {
        return cbxUsuario.getSelectedItem().toString();
    }

    @Override
    public Auditoria getAuditoriaSeleccionada() {
        int fila = grillaAuditoria.getSelectedRow();
        if (fila != -1 && datosActuales != null) return datosActuales.get(fila);
        return null;
    }
    
    @Override public void habilitarBotonDetalle(boolean b) { 
        btnDetallarCambios.setEnabled(b); 
    }

    // ══════════════════════════════════════════════════════════════════
    //  ESTILO Y UX - Mismo que VistaPaciente
    // ══════════════════════════════════════════════════════════════════
    private void aplicarEstilo() {
        setBackground(C_FONDO);

        // ========== HEADER (mismo que VistaPaciente) ==========
        pnlHeader.setBackground(C_NAVY);
        pnlHeader.setBorder(new EmptyBorder(14, 28, 14, 28));
        lblTituloHeader.setForeground(C_BLANCO);
        lblTituloHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));

        configurarBotonRetroceso(btnVolver);

        // ========== CONTENEDOR PRINCIPAL BLANCO ==========
        pnlContenedorBlanco.setBackground(C_BLANCO);
        pnlContenedorBlanco.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 1, 1, 1, C_BORDE),
            new EmptyBorder(24, 28, 24, 28)
        ));
        pnlContenedorBlanco.setLayout(new BorderLayout());

        // ========== PANEL DE FILTROS (estilo moderno) ==========
        pnlFiltros.setBackground(C_BLANCO);
        pnlFiltros.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(C_BORDE, 1, true),
            new EmptyBorder(20, 24, 20, 24)
        ));
        pnlFiltros.setLayout(new GridBagLayout());
        
        Font fontLabel = new Font("Segoe UI", Font.BOLD, 11);
        lblBusqueda.setFont(fontLabel);
        lblBusqueda.setForeground(C_TEXTO_SUAVE);
        lblFecha.setFont(fontLabel);
        lblFecha.setForeground(C_TEXTO_SUAVE);

        // Estilo para el combo de usuarios
        cbxUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbxUsuario.setBackground(C_CAMPO);
        cbxUsuario.setForeground(C_TEXTO_FUERTE);
        cbxUsuario.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDE),
            new EmptyBorder(8, 12, 8, 12)
        ));
        cbxUsuario.setPreferredSize(new Dimension(220, 38));

        // Estilo para el selector de fecha
        jdFechaFiltro.setPreferredSize(new Dimension(200, 38));
        jdFechaFiltro.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        jdFechaFiltro.setBackground(C_CAMPO);
        if (jdFechaFiltro.getDateEditor() instanceof com.toedter.calendar.JTextFieldDateEditor) {
            com.toedter.calendar.JTextFieldDateEditor editor =
                (com.toedter.calendar.JTextFieldDateEditor) jdFechaFiltro.getDateEditor();
            editor.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            editor.setBackground(C_CAMPO);
            editor.setForeground(C_TEXTO_FUERTE);
            editor.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDE),
                new EmptyBorder(8, 12, 8, 12)
            ));
        }

        configurarBoton(btnFiltrarUsuario, C_AZUL_MEDIO, "FILTRAR", 140, 38);
        configurarBoton(btnFiltrarFecha,   C_AZUL_MEDIO, "FILTRAR", 140, 38);

        // Layout de filtros usando GridBagLayout para mejor alineación
        GridBagConstraints gf = new GridBagConstraints();
        gf.insets = new Insets(0, 0, 0, 40);
        gf.anchor = GridBagConstraints.WEST;
        
        // Bloque Usuario
        gf.gridx = 0; gf.gridy = 0;
        pnlFiltros.add(lblBusqueda, gf);
        gf.gridy = 1; gf.insets = new Insets(4, 0, 0, 40);
        pnlFiltros.add(cbxUsuario, gf);
        gf.gridy = 1; gf.gridx = 1; gf.insets = new Insets(4, 0, 0, 0);
        pnlFiltros.add(btnFiltrarUsuario, gf);
        
        // Separador vertical
        JSeparator sep = new JSeparator(JSeparator.VERTICAL);
        sep.setPreferredSize(new Dimension(1, 50));
        sep.setForeground(C_BORDE);
        gf.gridx = 2; gf.gridy = 0; gf.gridheight = 2;
        gf.insets = new Insets(0, 0, 0, 40);
        pnlFiltros.add(sep, gf);
        gf.gridheight = 1;
        
        // Bloque Fecha
        gf.gridx = 3; gf.gridy = 0; gf.insets = new Insets(0, 0, 0, 40);
        pnlFiltros.add(lblFecha, gf);
        gf.gridy = 1; gf.insets = new Insets(4, 0, 0, 40);
        pnlFiltros.add(jdFechaFiltro, gf);
        gf.gridy = 1; gf.gridx = 4; gf.insets = new Insets(4, 0, 0, 0);
        pnlFiltros.add(btnFiltrarFecha, gf);
        
        // Espaciador elástico a la derecha
        gf.gridx = 5; gf.weightx = 1.0; gf.insets = new Insets(0, 0, 0, 0);
        pnlFiltros.add(new JPanel() {{ setOpaque(false); }}, gf);
        gf.weightx = 0;

        pnlContenedorBlanco.add(pnlFiltros, BorderLayout.NORTH);

        // ========== TABLA WRAPPER ==========
        pnlTablaWrapper.setBackground(C_BLANCO);
        pnlTablaWrapper.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(20, 0, 0, 0),
            BorderFactory.createLineBorder(C_BORDE, 1, true)
        ));

        lblTituloTabla.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTituloTabla.setForeground(C_TEXTO_FUERTE);
        lblTituloTabla.setBorder(new EmptyBorder(14, 16, 12, 16));

        // Tabla mejorada
        grillaAuditoria.setRowHeight(36);
        grillaAuditoria.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        grillaAuditoria.setGridColor(new Color(235, 240, 245));
        grillaAuditoria.setShowHorizontalLines(true);
        grillaAuditoria.setShowVerticalLines(false);
        grillaAuditoria.setSelectionBackground(new Color(220, 235, 250));
        grillaAuditoria.setSelectionForeground(C_TEXTO_FUERTE);
        grillaAuditoria.setIntercellSpacing(new Dimension(0, 0));
        grillaAuditoria.setFillsViewportHeight(true);
        grillaAuditoria.setBorder(BorderFactory.createEmptyBorder());

        grillaAuditoria.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        grillaAuditoria.getTableHeader().setBackground(C_CABECERA_TBL);
        grillaAuditoria.getTableHeader().setForeground(C_TEXTO_SUAVE);
        grillaAuditoria.getTableHeader().setPreferredSize(new Dimension(0, 40));
        grillaAuditoria.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDE));
        grillaAuditoria.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer centerR = crearRenderer(SwingConstants.CENTER);
        DefaultTableCellRenderer leftR   = crearRenderer(SwingConstants.LEFT);
        
        grillaAuditoria.getColumnModel().getColumn(0).setCellRenderer(centerR);
        grillaAuditoria.getColumnModel().getColumn(1).setCellRenderer(centerR);
        grillaAuditoria.getColumnModel().getColumn(2).setCellRenderer(leftR);
        grillaAuditoria.getColumnModel().getColumn(3).setCellRenderer(centerR);
        grillaAuditoria.getColumnModel().getColumn(4).setCellRenderer(leftR);

        // Anchos de columna optimizados
        grillaAuditoria.getColumnModel().getColumn(0).setPreferredWidth(120);
        grillaAuditoria.getColumnModel().getColumn(0).setMinWidth(100);
        grillaAuditoria.getColumnModel().getColumn(1).setPreferredWidth(150);
        grillaAuditoria.getColumnModel().getColumn(1).setMinWidth(130);
        grillaAuditoria.getColumnModel().getColumn(2).setPreferredWidth(140);
        grillaAuditoria.getColumnModel().getColumn(2).setMinWidth(120);
        grillaAuditoria.getColumnModel().getColumn(3).setPreferredWidth(120);
        grillaAuditoria.getColumnModel().getColumn(3).setMinWidth(100);
        grillaAuditoria.getColumnModel().getColumn(4).setPreferredWidth(450);
        grillaAuditoria.getColumnModel().getColumn(4).setMinWidth(200);

        jScrollPane1.setBorder(BorderFactory.createEmptyBorder());
        jScrollPane1.getViewport().setBackground(C_BLANCO);

        pnlTablaWrapper.setLayout(new BorderLayout());
        pnlTablaWrapper.add(lblTituloTabla, BorderLayout.NORTH);
        pnlTablaWrapper.add(jScrollPane1, BorderLayout.CENTER);

        pnlContenedorBlanco.add(pnlTablaWrapper, BorderLayout.CENTER);

        add(pnlContenedorBlanco, BorderLayout.CENTER);

        // ========== FOOTER ==========
        pnlFooter.setBackground(C_FONDO);
        pnlFooter.setBorder(new EmptyBorder(10, 16, 14, 16));
        pnlFooter.setLayout(new BorderLayout());
        
        JPanel pnlAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        pnlAcciones.setOpaque(false);
        configurarBoton(btnDetallarCambios, C_VERDE, "VER DESCRIPCIÓN", 180, 42);
        pnlAcciones.add(btnDetallarCambios);
        pnlFooter.add(pnlAcciones, BorderLayout.EAST);
        
        add(pnlFooter, BorderLayout.SOUTH);
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

    private DefaultTableCellRenderer crearRenderer(int alineacion) {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setHorizontalAlignment(alineacion);
                setBorder(new EmptyBorder(0, 12, 0, 12));
                if (!sel) {
                    setBackground(row % 2 == 0 ? C_BLANCO : C_FILA_PAR);
                    setForeground(C_TEXTO_FUERTE);
                }
                return this;
            }
        };
    }

    // ══════════════════════════════════════════════════════════════════
    //  initComponents
    // ══════════════════════════════════════════════════════════════════
    private void initComponents() {

        pnlHeader = new JPanel();
        lblTituloHeader = new JLabel("REGISTRO GLOBAL DE AUDITORÍA");
        btnVolver = new JButton();

        pnlContenedorBlanco = new JPanel();
        pnlFiltros = new JPanel();
        lblBusqueda = new JLabel("FILTRAR POR USUARIO");
        lblFecha = new JLabel("FILTRAR POR FECHA");
        cbxUsuario = new javax.swing.JComboBox<>();
        jdFechaFiltro = new com.toedter.calendar.JDateChooser();
        btnFiltrarUsuario = new JButton();
        btnFiltrarFecha = new JButton();

        pnlTablaWrapper = new JPanel();
        lblTituloTabla = new JLabel("Eventos Registrados");
        jScrollPane1 = new JScrollPane();
        grillaAuditoria = new JTable();
        pnlFooter = new JPanel();
        btnDetallarCambios = new JButton();

        grillaAuditoria.setModel(new DefaultTableModel(
            new Object[][]{},
            new String[]{"USUARIO", "FECHA Y HORA", "ACCIÓN", "TABLA", "DESCRIPCIÓN"}
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
        jScrollPane1.setViewportView(grillaAuditoria);

        setLayout(new BorderLayout());

        // HEADER
        pnlHeader.setLayout(new BorderLayout());
        JPanel pnlIzqHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlIzqHeader.setOpaque(false);
        pnlIzqHeader.add(btnVolver);
        lblTituloHeader.setHorizontalAlignment(SwingConstants.LEFT);
        lblTituloHeader.setBorder(new EmptyBorder(0, 10, 0, 0));
        pnlIzqHeader.add(lblTituloHeader);
        pnlHeader.add(pnlIzqHeader, BorderLayout.WEST);
        add(pnlHeader, BorderLayout.NORTH);

        // El estilo se aplica en aplicarEstilo()
    }

    // ── Variables ────────────────────────────────────────────────────
    private JPanel                              pnlHeader;
    private JLabel                              lblTituloHeader;
    private JButton                             btnVolver;
    private JPanel                              pnlContenedorBlanco;
    private JPanel                              pnlFiltros;
    private JLabel                              lblBusqueda;
    private JLabel                              lblFecha;
    private JPanel                              pnlTablaWrapper;
    private JLabel                              lblTituloTabla;
    private JPanel                              pnlFooter;
    private JButton                             btnDetallarCambios;
    private JButton                             btnFiltrarFecha;
    private JButton                             btnFiltrarUsuario;
    private javax.swing.JComboBox<String>       cbxUsuario;
    private JTable                              grillaAuditoria;
    private JScrollPane                         jScrollPane1;
    private com.toedter.calendar.JDateChooser   jdFechaFiltro;
}