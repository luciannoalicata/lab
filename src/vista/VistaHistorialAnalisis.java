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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import modelo.Analisis;

/**
 * Vista Historial Clínico - BIOTEC LIS
 * @author luciano
 */
public class VistaHistorialAnalisis extends JPanel implements IVistaHistorialAnalisis {

    private Controlador controlador;
    private ArrayList<Analisis> historialCargado = new ArrayList<>();

    // ── Paleta BIOTEC Minimalista ────────────────────────────────────
    private final Color C_NAVY         = new Color(10, 25, 47);    
    private final Color C_FONDO        = new Color(238, 242, 246);
    private final Color C_BLANCO       = Color.WHITE;
    private final Color C_TEXTO_FUERTE = new Color(40, 50, 60);
    private final Color C_TEXTO_SUAVE  = new Color(100, 115, 130);
    private final Color C_BORDE        = new Color(215, 225, 235);
    private final Color C_AZUL_MEDIO   = new Color(30, 110, 180);
    private final Color C_VERDE        = new Color(35, 160, 115);
    private final Color C_CABECERA_TBL = new Color(245, 248, 252);
    private final Color C_FILA_PAR     = new Color(252, 254, 255);
    private final Color C_HEADER_TEXT  = new Color(175, 205, 235);
    
    // Colores de estado
    private final Color COLOR_VERDE_FILA   = new Color(210, 245, 220);
    private final Color COLOR_VERDE_TEXTO  = new Color(0, 100, 50);

    public VistaHistorialAnalisis() {
        initComponents();
        aplicarEsteticaPersonalizada();

        jdFechaInforme.setDate(new Date());
        btnGenerarInforme.setEnabled(false);
        btnVerDetalles.setEnabled(false);

        grillaHistorial.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = grillaHistorial.getSelectedRow();
                boolean haySeleccion = fila != -1;
                btnVerDetalles.setEnabled(haySeleccion);
                btnGenerarInforme.setEnabled(haySeleccion);
                if (haySeleccion && fila < historialCargado.size()) {
                    Date fechaAnalisis = historialCargado.get(fila).getFecha();
                    if (fechaAnalisis != null) jdFechaInforme.setDate(fechaAnalisis);
                }
            }
        });
    }

    // ══════════════════════════════════════════════════════════════════
    //  ESTILO Y UX
    // ══════════════════════════════════════════════════════════════════
    private void aplicarEsteticaPersonalizada() {
        setBackground(C_FONDO);

        // ── HEADER (Azul institucional con flecha) ───────────────────
        jPanelHeader.setBackground(C_NAVY);
        jPanelHeader.setBorder(new EmptyBorder(15, 30, 15, 30));

        jLabel1.setForeground(C_HEADER_TEXT);
        jLabel1.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        lblNombrePaciente.setForeground(C_BLANCO);
        lblNombrePaciente.setFont(new Font("Segoe UI", Font.BOLD, 22));

        configurarBotonRetroceso(btnVolver);

        // ── CENTRO / TABLA ────────────────────────────────────────────
        jPanelCenter.setBackground(C_FONDO);
        
        pnlTablaWrapper.setBackground(C_BLANCO);
        pnlTablaWrapper.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(C_BORDE, 1, true),
            new EmptyBorder(1, 1, 1, 1)
        ));

        grillaHistorial.setRowHeight(36);
        grillaHistorial.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        grillaHistorial.setGridColor(new Color(235, 240, 245));
        grillaHistorial.setShowHorizontalLines(true);
        grillaHistorial.setShowVerticalLines(false);
        grillaHistorial.setSelectionBackground(new Color(210, 232, 250));
        grillaHistorial.setSelectionForeground(C_NAVY);
        grillaHistorial.setIntercellSpacing(new Dimension(0, 1));
        grillaHistorial.setBorder(BorderFactory.createEmptyBorder());

        grillaHistorial.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        grillaHistorial.getTableHeader().setBackground(C_CABECERA_TBL);
        grillaHistorial.getTableHeader().setForeground(C_TEXTO_SUAVE);
        grillaHistorial.getTableHeader().setPreferredSize(new Dimension(0, 42));
        grillaHistorial.getTableHeader().setBorder(BorderFactory.createMatteBorder(1, 0, 2, 0, C_BORDE));
        grillaHistorial.getTableHeader().setReorderingAllowed(false);

        jScrollPane1.setBorder(BorderFactory.createEmptyBorder());
        jScrollPane1.getViewport().setBackground(C_BLANCO);

        configurarRenderizadorColor();

        // ── FOOTER ────────────────────────────────────────────────────
        jPanelFooter.setBackground(C_BLANCO);
        jPanelFooter.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(1, 0, 0, 0, C_BORDE),
            new EmptyBorder(15, 25, 15, 25)
        ));

        jLabel2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        jLabel2.setForeground(C_TEXTO_SUAVE);

        jdFechaInforme.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        configurarBoton(btnVerDetalles, C_AZUL_MEDIO, "☰ VER DETALLES", 170, 44);
        configurarBoton(btnGenerarInforme, C_VERDE, "⎙ IMPRIMIR", 160, 44);
    }

    private void configurarBoton(JButton btn, Color bg, String texto, int w, int h) {
        btn.setText(texto);
        btn.setBackground(bg);
        btn.setForeground(C_BLANCO);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
        } catch (Exception e) { /* silencioso */ }
        return null;
    }

    @Override
    public void setFechaSeleccionada(Date fecha) {
        if (fecha != null) jdFechaInforme.setDate(fecha);
    }

    // ── RENDERIZADOR ──────────────────────────────────────────────────
    private void configurarRenderizadorColor() {
        DefaultTableCellRenderer renderizadorColores = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(new EmptyBorder(0, 10, 0, 10));

                if (isSelected) {
                    setBackground(table.getSelectionBackground());
                    setForeground(table.getSelectionForeground());
                } else {
                    Object estadoObj = table.getValueAt(row, 3);
                    String estado = (estadoObj != null) ? estadoObj.toString() : "COMPLETO";
                    if ("GENERADO".equals(estado)) {
                        setBackground(COLOR_VERDE_FILA);
                        setForeground(COLOR_VERDE_TEXTO);
                    } else {
                        setBackground(row % 2 == 0 ? C_BLANCO : C_FILA_PAR);
                        setForeground(C_TEXTO_FUERTE);
                    }
                }
                return this;
            }
        };
        for (int i = 0; i < grillaHistorial.getColumnCount(); i++) {
            grillaHistorial.getColumnModel().getColumn(i).setCellRenderer(renderizadorColores);
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  INTERFAZ
    // ══════════════════════════════════════════════════════════════════
    @Override public void ejecutar() { setVisible(true); }

    @Override
    public void setControlador(Controlador control) {
        this.controlador = control;
        btnGenerarInforme.setActionCommand(BTN_GENERAR_INFORME);
        btnGenerarInforme.addActionListener(control);
        btnVerDetalles.setActionCommand(BTN_VER_DETALLES);
        btnVerDetalles.addActionListener(control);
        
        // Asignamos el comando original que usabas al nuevo botón de la flecha
        btnVolver.setActionCommand(BTN_CERRAR); 
        btnVolver.addActionListener(control);
    }

    @Override public void mostrarMensaje(String mensaje) { JOptionPane.showMessageDialog(this, mensaje); }
    @Override public void setNombrePaciente(String nombre) { lblNombrePaciente.setText(nombre.toUpperCase()); }
    @Override public void habilitarBotonVerDetalles(boolean b) { btnVerDetalles.setEnabled(b); }
    @Override public void habilitarBotonImprimir(boolean b)    { btnGenerarInforme.setEnabled(b); }

    @Override
    public int getAnalisisSeleccionadoId() {
        int fila = grillaHistorial.getSelectedRow();
        if (fila == -1) return -1;
        Object valor = grillaHistorial.getValueAt(fila, 0);
        return (valor instanceof Integer) ? (int) valor : Integer.parseInt(valor.toString());
    }

    @Override
    public void cargarHistorial(ArrayList<Analisis> lista) {
        this.historialCargado = (lista != null) ? lista : new ArrayList<>();  
        DefaultTableModel modelo = (DefaultTableModel) grillaHistorial.getModel();
        modelo.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        for (Analisis a : lista) {
            String estadoLabel = (a.getEstado() != null) ? a.getEstado() : "COMPLETO";
            modelo.addRow(new Object[]{ a.getIdAnalisis(), sdf.format(a.getFecha()), String.format("$ %.2f", a.getPrecio()), estadoLabel });
        }
        configurarRenderizadorColor();
    }

    @Override
    public Date getFechaSeleccionada() {
        Date fecha = jdFechaInforme.getDate();
        return (fecha != null) ? fecha : new Date();
    }

    // ══════════════════════════════════════════════════════════════════
    //  UI BUILDER
    // ══════════════════════════════════════════════════════════════════
    private void initComponents() {

        jPanelHeader      = new JPanel();
        pnlHeaderTexto    = new JPanel();
        jLabel1           = new JLabel("HISTORIAL CLÍNICO");
        lblNombrePaciente = new JLabel();
        btnVolver         = new JButton();

        jPanelCenter      = new JPanel();
        pnlTablaWrapper   = new JPanel();
        jScrollPane1      = new JScrollPane();
        grillaHistorial   = new JTable();

        jPanelFooter      = new JPanel();
        pnlFechaConfig    = new JPanel();
        jLabel2           = new JLabel("Fecha para el Informe:");
        jdFechaInforme    = new com.toedter.calendar.JDateChooser();
        pnlBotonesAccion  = new JPanel();
        btnVerDetalles    = new JButton();
        btnGenerarInforme = new JButton();

        grillaHistorial.setModel(new DefaultTableModel(
            new Object[][]{},
            new String[]{"ID ANÁLISIS", "FECHA", "PRECIO", "ESTADO"}
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
        jScrollPane1.setViewportView(grillaHistorial);

        // ── ROOT ─────────────────────────────────────────────────────
        setLayout(new BorderLayout());

        // ── HEADER ───────────────────────────────────────────────────
        jPanelHeader.setLayout(new BorderLayout());

        JPanel pnlIzqHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlIzqHeader.setOpaque(false);
        pnlIzqHeader.add(btnVolver);

        pnlHeaderTexto.setOpaque(false);
        pnlHeaderTexto.setLayout(new BoxLayout(pnlHeaderTexto, BoxLayout.Y_AXIS));
        jLabel1.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblNombrePaciente.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlHeaderTexto.add(jLabel1);
        pnlHeaderTexto.add(Box.createVerticalStrut(4));
        pnlHeaderTexto.add(lblNombrePaciente);

        pnlIzqHeader.add(pnlHeaderTexto);
        jPanelHeader.add(pnlIzqHeader, BorderLayout.WEST);
        
        add(jPanelHeader, BorderLayout.NORTH);

        // ── CENTRO / TABLA ───────────────────────────────────────────
        jPanelCenter.setLayout(new BorderLayout());

        pnlTablaWrapper.setLayout(new BorderLayout());
        pnlTablaWrapper.add(jScrollPane1, BorderLayout.CENTER);

        JPanel wrapperCuerpo = new JPanel(new BorderLayout());
        wrapperCuerpo.setOpaque(false);
        wrapperCuerpo.setBorder(new EmptyBorder(25, 25, 25, 25));
        wrapperCuerpo.add(pnlTablaWrapper, BorderLayout.CENTER);

        jPanelCenter.add(wrapperCuerpo, BorderLayout.CENTER);
        add(jPanelCenter, BorderLayout.CENTER);

        // ── FOOTER ───────────────────────────────────────────────────
        jPanelFooter.setLayout(new BorderLayout());

        pnlFechaConfig.setOpaque(false);
        pnlFechaConfig.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 0));
        pnlFechaConfig.add(jLabel2);
        jdFechaInforme.setPreferredSize(new Dimension(160, 38));
        pnlFechaConfig.add(jdFechaInforme);
        
        jPanelFooter.add(pnlFechaConfig, BorderLayout.WEST);

        pnlBotonesAccion.setOpaque(false);
        pnlBotonesAccion.setLayout(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        pnlBotonesAccion.add(btnVerDetalles);
        pnlBotonesAccion.add(btnGenerarInforme);
        
        jPanelFooter.add(pnlBotonesAccion, BorderLayout.EAST);

        add(jPanelFooter, BorderLayout.SOUTH);
    }

    // Variables declaration
    private JButton btnGenerarInforme;
    private JButton btnVerDetalles;
    private JButton btnVolver;
    private JTable  grillaHistorial;
    private JLabel  jLabel1;
    private JLabel  jLabel2;
    private JPanel  jPanelCenter;
    private JPanel  jPanelFooter;
    private JPanel  jPanelHeader;
    private JPanel  pnlHeaderTexto;
    private JPanel  pnlBotonesAccion;
    private JPanel  pnlFechaConfig;
    private JPanel  pnlTablaWrapper;
    private JScrollPane jScrollPane1;
    private com.toedter.calendar.JDateChooser jdFechaInforme;
    private JLabel  lblNombrePaciente;
}