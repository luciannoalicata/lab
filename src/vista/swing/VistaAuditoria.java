package vista.swing;

import vista.interfaces.IVistaAuditoria;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Date;
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
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import modelo.Auditoria;
import modelo.Usuario;
import presentador.AuditoriaPresenter;

/**
 * Vista Auditoría de Seguridad - BIOTEC LIS
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

    public VistaAuditoria() {
        initComponents();
        aplicarEstilo();

        btnDetallarCambios.setEnabled(false);
        grillaAuditoria.getSelectionModel().addListSelectionListener(e -> {
            btnDetallarCambios.setEnabled(grillaAuditoria.getSelectedRow() != -1);
        });
    }

    // ══════════════════════════════════════════════════════════════════
    //  INTERFAZ IVistaAuditoria - MÉTODOS MVP
    // ══════════════════════════════════════════════════════════════════

    @Override
    public void setPresenter(AuditoriaPresenter presenter) {
        this.presenter = presenter;
        
        // ¡MAGIA MVP! Conexión directa
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
        return javax.swing.JOptionPane.showConfirmDialog(
                this, 
                mensaje, 
                titulo, 
                javax.swing.JOptionPane.YES_NO_OPTION
        );
    }

    @Override 
    public void mostrarDetalleCambios(String titulo, String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, JOptionPane.INFORMATION_MESSAGE);
    }

    @Override public void mostrarMensaje(String mensaje) { JOptionPane.showMessageDialog(this, mensaje); }
    @Override public Date getFechaSeleccionada() { return jdFechaFiltro.getDate(); }

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
    
    @Override public void habilitarBotonDetalle(boolean b) { btnDetallarCambios.setEnabled(b); }

    // ══════════════════════════════════════════════════════════════════
    //  ESTILO Y UX
    // ══════════════════════════════════════════════════════════════════
    private void aplicarEstilo() {
        setBackground(C_FONDO);

        pnlHeader.setBackground(C_NAVY);
        pnlHeader.setBorder(new EmptyBorder(15, 30, 15, 30));
        
        lblTituloHeader.setForeground(C_BLANCO);
        lblTituloHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));

        configurarBotonRetroceso(btnVolver);

        pnlFiltros.setBackground(C_BLANCO);
        pnlFiltros.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(12, 20, 0, 20),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDE, 1, true),
                new EmptyBorder(14, 20, 14, 20)
            )
        ));

        Font fontLabel = new Font("Segoe UI", Font.BOLD, 12);
        lblBusqueda.setFont(fontLabel);
        lblBusqueda.setForeground(C_TEXTO_SUAVE);
        lblFecha.setFont(fontLabel);
        lblFecha.setForeground(C_TEXTO_SUAVE);

        cbxUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbxUsuario.setBackground(C_CAMPO);
        cbxUsuario.setForeground(C_TEXTO_FUERTE);
        cbxUsuario.setPreferredSize(new Dimension(220, 40));
        cbxUsuario.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, C_AZUL_MEDIO),
            new EmptyBorder(4, 8, 4, 8)
        ));

        jdFechaFiltro.setPreferredSize(new Dimension(180, 40));
        jdFechaFiltro.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        jdFechaFiltro.setBackground(C_CAMPO);
        if (jdFechaFiltro.getDateEditor() instanceof com.toedter.calendar.JTextFieldDateEditor) {
            com.toedter.calendar.JTextFieldDateEditor editor =
                (com.toedter.calendar.JTextFieldDateEditor) jdFechaFiltro.getDateEditor();
            editor.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            editor.setBackground(C_CAMPO);
            editor.setForeground(C_TEXTO_FUERTE);
            editor.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDE),
                new EmptyBorder(4, 8, 4, 8)
            ));
        }

        configurarBoton(btnFiltrarUsuario, C_AZUL_MEDIO, "FILTRAR USUARIO", 160, 40);
        configurarBoton(btnFiltrarFecha,   C_AZUL_MEDIO, "FILTRAR FECHA",   160, 40);

        pnlTablaWrapper.setBackground(C_BLANCO);
        pnlTablaWrapper.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(12, 20, 0, 20),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDE, 1, true),
                new EmptyBorder(1, 1, 1, 1)
            )
        ));

        grillaAuditoria.setRowHeight(38);
        grillaAuditoria.setFont(new Font("Segoe UI", Font.PLAIN, 13));
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
        grillaAuditoria.getTableHeader().setPreferredSize(new Dimension(0, 42));
        grillaAuditoria.getTableHeader().setBorder(BorderFactory.createMatteBorder(1, 0, 2, 0, C_BORDE));
        grillaAuditoria.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer centerR = crearRenderer(SwingConstants.CENTER);
        DefaultTableCellRenderer leftR   = crearRenderer(SwingConstants.LEFT);
        
        grillaAuditoria.getColumnModel().getColumn(0).setCellRenderer(centerR);
        grillaAuditoria.getColumnModel().getColumn(1).setCellRenderer(centerR);
        grillaAuditoria.getColumnModel().getColumn(2).setCellRenderer(leftR);
        grillaAuditoria.getColumnModel().getColumn(3).setCellRenderer(centerR);
        grillaAuditoria.getColumnModel().getColumn(4).setCellRenderer(leftR);

        grillaAuditoria.getColumnModel().getColumn(0).setPreferredWidth(130);
        grillaAuditoria.getColumnModel().getColumn(0).setMaxWidth(160);
        grillaAuditoria.getColumnModel().getColumn(1).setPreferredWidth(160);
        grillaAuditoria.getColumnModel().getColumn(1).setMaxWidth(190);
        grillaAuditoria.getColumnModel().getColumn(2).setPreferredWidth(160);
        grillaAuditoria.getColumnModel().getColumn(2).setMaxWidth(200);
        grillaAuditoria.getColumnModel().getColumn(3).setPreferredWidth(130);
        grillaAuditoria.getColumnModel().getColumn(3).setMaxWidth(160);
        grillaAuditoria.getColumnModel().getColumn(4).setPreferredWidth(500);

        jScrollPane1.setBorder(BorderFactory.createEmptyBorder());
        jScrollPane1.getViewport().setBackground(C_BLANCO);

        pnlFooter.setBackground(C_FONDO);
        pnlFooter.setBorder(new EmptyBorder(15, 20, 15, 20));

        configurarBoton(btnDetallarCambios, C_VERDE, "🔍 VER DETALLE", 180, 44);
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
        } catch (Exception e) {  }
        return null;
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

    // ══════════════════════════════════════════════════════════════════
    //  UI BUILDER
    // ══════════════════════════════════════════════════════════════════
    private void initComponents() {

        pnlHeader          = new JPanel();
        lblTituloHeader    = new JLabel("Registro Global de Auditoría");
        btnVolver          = new JButton();

        pnlFiltros         = new JPanel();
        lblBusqueda        = new JLabel("FILTRAR POR USUARIO");
        lblFecha           = new JLabel("FILTRAR POR FECHA");
        cbxUsuario         = new javax.swing.JComboBox<>();
        jdFechaFiltro      = new com.toedter.calendar.JDateChooser();
        btnFiltrarUsuario  = new JButton();
        btnFiltrarFecha    = new JButton();

        pnlTablaWrapper    = new JPanel();
        jScrollPane1       = new JScrollPane();
        grillaAuditoria    = new JTable();

        pnlFooter          = new JPanel();
        btnDetallarCambios = new JButton();

        grillaAuditoria.setModel(new DefaultTableModel(
            new Object[][]{},
            new String[]{"USUARIO", "FECHA Y HORA", "ACCIÓN", "TABLA", "DETALLE"}
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
        jScrollPane1.setViewportView(grillaAuditoria);

        setLayout(new BorderLayout());

        pnlHeader.setLayout(new BorderLayout());
        
        JPanel pnlIzqHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlIzqHeader.setOpaque(false);
        pnlIzqHeader.add(btnVolver);
        
        lblTituloHeader.setHorizontalAlignment(SwingConstants.LEFT);
        lblTituloHeader.setBorder(new EmptyBorder(0, 10, 0, 0)); 
        pnlIzqHeader.add(lblTituloHeader);
        
        pnlHeader.add(pnlIzqHeader, BorderLayout.WEST);
        add(pnlHeader, BorderLayout.NORTH);

        JPanel pnlCentro = new JPanel(new BorderLayout(0, 0));
        pnlCentro.setBackground(C_FONDO);

        pnlFiltros.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 30, 0));

        JPanel blqUsuario = new JPanel(new java.awt.GridBagLayout());
        blqUsuario.setOpaque(false);
        java.awt.GridBagConstraints gu = new java.awt.GridBagConstraints();
        gu.gridx = 0; gu.gridy = 0; gu.anchor = java.awt.GridBagConstraints.WEST;
        gu.insets = new java.awt.Insets(0, 0, 4, 0);
        blqUsuario.add(lblBusqueda, gu);
        gu.gridy = 1; gu.insets = new java.awt.Insets(0, 0, 0, 0);
        blqUsuario.add(cbxUsuario, gu);
        gu.gridx = 1; gu.insets = new java.awt.Insets(0, 15, 0, 0);
        blqUsuario.add(btnFiltrarUsuario, gu);

        javax.swing.JSeparator sep = new javax.swing.JSeparator(javax.swing.JSeparator.VERTICAL);
        sep.setPreferredSize(new Dimension(1, 45));
        sep.setForeground(C_BORDE);

        JPanel blqFecha = new JPanel(new java.awt.GridBagLayout());
        blqFecha.setOpaque(false);
        java.awt.GridBagConstraints gf2 = new java.awt.GridBagConstraints();
        gf2.gridx = 0; gf2.gridy = 0; gf2.anchor = java.awt.GridBagConstraints.WEST;
        gf2.insets = new java.awt.Insets(0, 0, 4, 0);
        blqFecha.add(lblFecha, gf2);
        gf2.gridy = 1; gf2.insets = new java.awt.Insets(0, 0, 0, 0);
        blqFecha.add(jdFechaFiltro, gf2);
        gf2.gridx = 1; gf2.insets = new java.awt.Insets(0, 15, 0, 0);
        blqFecha.add(btnFiltrarFecha, gf2);

        pnlFiltros.add(blqUsuario);
        pnlFiltros.add(sep);
        pnlFiltros.add(blqFecha);

        pnlCentro.add(pnlFiltros, BorderLayout.NORTH);

        pnlTablaWrapper.setLayout(new BorderLayout());
        pnlTablaWrapper.add(jScrollPane1, BorderLayout.CENTER);
        pnlCentro.add(pnlTablaWrapper, BorderLayout.CENTER);

        add(pnlCentro, BorderLayout.CENTER);

        pnlFooter.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pnlFooter.add(btnDetallarCambios);

        add(pnlFooter, BorderLayout.SOUTH);
    }

    // ── Variables ────────────────────────────────────────────────────
    private JPanel                              pnlHeader;
    private JLabel                              lblTituloHeader;
    private JPanel                              pnlFiltros;
    private JLabel                              lblBusqueda;
    private JLabel                              lblFecha;
    private JPanel                              pnlTablaWrapper;
    private JPanel                              pnlFooter;
    private JButton                             btnDetallarCambios;
    private JButton                             btnFiltrarFecha;
    private JButton                             btnFiltrarUsuario;
    private JButton                             btnVolver;
    private javax.swing.JComboBox<String>       cbxUsuario;
    private JTable                              grillaAuditoria;
    private JScrollPane                         jScrollPane1;
    private com.toedter.calendar.JDateChooser   jdFechaFiltro;
}