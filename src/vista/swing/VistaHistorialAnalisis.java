package vista.swing;

import vista.interfaces.IVistaHistorialAnalisis;
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
import presentador.HistorialPresenter;

/**
 * Vista Historial Clínico - BIOTEC LIS
 * Diseño consistente con VistaPaciente y VistaMedicos
 */
public class VistaHistorialAnalisis extends JPanel implements IVistaHistorialAnalisis {

    private HistorialPresenter presenter;
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
    private final Color C_CAMPO        = new Color(250, 252, 254);
    
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
                if (presenter != null && haySeleccion) {
                    presenter.onSeleccionarAnalisis();
                }
            }
        });
    }

    // ══════════════════════════════════════════════════════════════════
    //  ESTILO Y UX - Consistente con VistaPaciente y VistaMedicos
    // ══════════════════════════════════════════════════════════════════
    private void aplicarEsteticaPersonalizada() {
        setBackground(C_FONDO);

        // ── HEADER (mismos márgenes que VistaPaciente) ────────────────
        jPanelHeader.setBackground(C_NAVY);
        jPanelHeader.setBorder(new EmptyBorder(14, 28, 14, 28));
        
        // Reconstruir el header correctamente
        jPanelHeader.removeAll();
        jPanelHeader.setLayout(new BorderLayout());
        
        // Panel izquierdo: botón volver + título + nombre paciente
        JPanel pnlIzqHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlIzqHeader.setOpaque(false);
        pnlIzqHeader.add(btnVolver);
        
        jLabel1.setForeground(C_HEADER_TEXT);
        jLabel1.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        lblNombrePaciente.setForeground(C_BLANCO);
        lblNombrePaciente.setFont(new Font("Segoe UI", Font.BOLD, 22));
        
        JPanel pnlTitulos = new JPanel();
        pnlTitulos.setOpaque(false);
        pnlTitulos.setLayout(new BoxLayout(pnlTitulos, BoxLayout.Y_AXIS));
        jLabel1.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblNombrePaciente.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlTitulos.add(jLabel1);
        pnlTitulos.add(Box.createVerticalStrut(4));
        pnlTitulos.add(lblNombrePaciente);
        
        pnlIzqHeader.add(pnlTitulos);
        
        jPanelHeader.add(pnlIzqHeader, BorderLayout.WEST);

        configurarBotonRetroceso(btnVolver);

        // ── CONTENEDOR PRINCIPAL BLANCO (con borde sin superior) ──────
        pnlContenedorBlanco.setBackground(C_BLANCO);
        pnlContenedorBlanco.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 1, 1, 1, C_BORDE),
            new EmptyBorder(24, 28, 24, 28)
        ));
        pnlContenedorBlanco.removeAll();
        pnlContenedorBlanco.setLayout(new BorderLayout());

        // ── CUERPO (Tabla) ────────────────────────────────────────────
        pnlCuerpo.setBackground(C_BLANCO);
        pnlCuerpo.removeAll();
        pnlCuerpo.setLayout(new BorderLayout());

        // ── TABLA WRAPPER (igual que VistaPaciente) ───────────────────
        pnlTablaWrapper.setBackground(C_BLANCO);
        pnlTablaWrapper.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(0, 0, 0, 0),
            BorderFactory.createLineBorder(C_BORDE, 1, true)
        ));

        lblTituloTabla = new JLabel("ANÁLISIS REALIZADOS");
        lblTituloTabla.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTituloTabla.setForeground(C_TEXTO_FUERTE);
        lblTituloTabla.setBorder(new EmptyBorder(14, 16, 12, 16));

        // Configuración de la tabla
        grillaHistorial.setRowHeight(36);
        grillaHistorial.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        grillaHistorial.setGridColor(new Color(235, 240, 245));
        grillaHistorial.setShowHorizontalLines(true);
        grillaHistorial.setShowVerticalLines(false);
        grillaHistorial.setSelectionBackground(new Color(210, 232, 250));
        grillaHistorial.setSelectionForeground(C_NAVY);
        grillaHistorial.setIntercellSpacing(new Dimension(0, 1));
        grillaHistorial.setBorder(BorderFactory.createEmptyBorder());
        grillaHistorial.setFillsViewportHeight(true);

        grillaHistorial.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        grillaHistorial.getTableHeader().setBackground(C_CABECERA_TBL);
        grillaHistorial.getTableHeader().setForeground(C_TEXTO_SUAVE);
        grillaHistorial.getTableHeader().setPreferredSize(new Dimension(0, 40));
        grillaHistorial.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDE));
        grillaHistorial.getTableHeader().setReorderingAllowed(false);

        // Anchos de columna (nuevas columnas: OBRA SOCIAL y MÉDICO)
        grillaHistorial.getColumnModel().getColumn(0).setPreferredWidth(60);
        grillaHistorial.getColumnModel().getColumn(0).setMaxWidth(80);
        grillaHistorial.getColumnModel().getColumn(0).setMinWidth(50);
        
        grillaHistorial.getColumnModel().getColumn(1).setPreferredWidth(100);
        grillaHistorial.getColumnModel().getColumn(1).setMinWidth(90);
        
        grillaHistorial.getColumnModel().getColumn(2).setPreferredWidth(180);
        grillaHistorial.getColumnModel().getColumn(2).setMinWidth(150);
        
        grillaHistorial.getColumnModel().getColumn(3).setPreferredWidth(150);
        grillaHistorial.getColumnModel().getColumn(3).setMinWidth(120);
        
        grillaHistorial.getColumnModel().getColumn(4).setPreferredWidth(100);
        grillaHistorial.getColumnModel().getColumn(4).setMinWidth(80);
        
        grillaHistorial.getColumnModel().getColumn(5).setPreferredWidth(100);
        grillaHistorial.getColumnModel().getColumn(5).setMaxWidth(120);
        grillaHistorial.getColumnModel().getColumn(5).setMinWidth(80);

        jScrollPane1.setBorder(BorderFactory.createEmptyBorder());
        jScrollPane1.getViewport().setBackground(C_BLANCO);

        pnlTablaWrapper.removeAll();
        pnlTablaWrapper.setLayout(new BorderLayout());
        pnlTablaWrapper.add(lblTituloTabla, BorderLayout.NORTH);
        pnlTablaWrapper.add(jScrollPane1, BorderLayout.CENTER);

        pnlCuerpo.add(pnlTablaWrapper, BorderLayout.CENTER);
        pnlContenedorBlanco.add(pnlCuerpo, BorderLayout.CENTER);

        // ── FOOTER (mismos márgenes que VistaPaciente) ────────────────
        jPanelFooter.setBackground(C_BLANCO);
        jPanelFooter.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(1, 0, 0, 0, C_BORDE),
            new EmptyBorder(16, 28, 16, 28)
        ));
        jPanelFooter.removeAll();
        jPanelFooter.setLayout(new BorderLayout());

        jLabel2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        jLabel2.setForeground(C_TEXTO_SUAVE);

        // Estilo para el selector de fecha
        jdFechaInforme.setPreferredSize(new Dimension(180, 38));
        jdFechaInforme.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        if (jdFechaInforme.getDateEditor() instanceof com.toedter.calendar.JTextFieldDateEditor) {
            com.toedter.calendar.JTextFieldDateEditor editor =
                (com.toedter.calendar.JTextFieldDateEditor) jdFechaInforme.getDateEditor();
            editor.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            editor.setBackground(C_CAMPO);
            editor.setForeground(C_TEXTO_FUERTE);
            editor.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDE),
                new EmptyBorder(6, 10, 6, 10)
            ));
        }

        pnlFechaConfig.setOpaque(false);
        pnlFechaConfig.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 0));
        pnlFechaConfig.add(jLabel2);
        pnlFechaConfig.add(jdFechaInforme);
        jPanelFooter.add(pnlFechaConfig, BorderLayout.WEST);

        configurarBoton(btnVerDetalles, C_AZUL_MEDIO, "VER DETALLES", 160, 42);
        configurarBoton(btnGenerarInforme, C_VERDE, "IMPRIMIR", 150, 42);

        pnlBotonesAccion.setOpaque(false);
        pnlBotonesAccion.setLayout(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        pnlBotonesAccion.add(btnVerDetalles);
        pnlBotonesAccion.add(btnGenerarInforme);
        jPanelFooter.add(pnlBotonesAccion, BorderLayout.EAST);

        // ── ARMADO FINAL DEL LAYOUT ───────────────────────────────────
        this.removeAll();
        this.setLayout(new BorderLayout());
        this.add(jPanelHeader, BorderLayout.NORTH);
        this.add(pnlContenedorBlanco, BorderLayout.CENTER);
        this.add(jPanelFooter, BorderLayout.SOUTH);

        configurarRenderizadorColor();
        
        // Forzar actualización
        this.revalidate();
        this.repaint();
    }

    private void configurarBoton(JButton btn, Color bg, String texto, int w, int h) {
        btn.setText(texto);
        btn.setBackground(bg);
        btn.setForeground(C_BLANCO);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
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

    @Override
    public void setFechaSeleccionada(Date fecha) {
        if (fecha != null) jdFechaInforme.setDate(fecha);
    }

    // ── RENDERIZADOR DE COLORES PARA FILAS COMPLETADAS ────────────────
    private void configurarRenderizadorColor() {
        DefaultTableCellRenderer renderizadorColores = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                // Centrar todas las columnas para mayor orden visual
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(new EmptyBorder(0, 12, 0, 12));

                if (isSelected) {
                    setBackground(table.getSelectionBackground());
                    setForeground(table.getSelectionForeground());
                } else {
                    Object estadoObj = table.getValueAt(row, 5); // Índice de columna ESTADO
                    String estado = (estadoObj != null) ? estadoObj.toString() : "COMPLETO";
                    if ("GENERADO".equals(estado) || "COMPLETO".equals(estado)) {
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
    //  INTERFAZ IVistaHistorialAnalisis
    // ══════════════════════════════════════════════════════════════════
    @Override public void ejecutar() { setVisible(true); }

    @Override
    public void setPresenter(HistorialPresenter presenter) {
        this.presenter = presenter;
        
        btnGenerarInforme.addActionListener(e -> presenter.onGenerarInforme());
        btnVerDetalles.addActionListener(e -> presenter.onVerDetalles());
        btnVolver.addActionListener(e -> presenter.onVolver());
    }
    
    @Override
    public void limpiarFocos() {
        this.requestFocusInWindow();
    }

    @Override
    public int confirmarAccion(String mensaje, String titulo) {
        return JOptionPane.showConfirmDialog(this, mensaje, titulo, JOptionPane.YES_NO_OPTION);
    }

    @Override public void mostrarMensaje(String mensaje) { 
        JOptionPane.showMessageDialog(this, mensaje); 
    }
    
    @Override public void setNombrePaciente(String nombre) { 
        lblNombrePaciente.setText(nombre.toUpperCase()); 
    }
    
    @Override public void habilitarBotonVerDetalles(boolean b) { 
        btnVerDetalles.setEnabled(b); 
    }
    
    @Override public void habilitarBotonImprimir(boolean b) { 
        btnGenerarInforme.setEnabled(b); 
    }

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
            String obraSocialStr = (a.getObraSocial() != null && !a.getObraSocial().trim().isEmpty()) 
                                    ? a.getObraSocial() : "-";
            String medicoStr = (a.getMedicoSolicitante() != null && !a.getMedicoSolicitante().trim().isEmpty()) 
                                ? a.getMedicoSolicitante().toUpperCase() : "-";
            modelo.addRow(new Object[]{ 
                a.getIdAnalisis(), 
                sdf.format(a.getFecha()),
                obraSocialStr,
                medicoStr,
                String.format("$ %.2f", a.getPrecio()), 
                estadoLabel 
            });
        }
        configurarRenderizadorColor();
    }

    @Override
    public Date getFechaSeleccionada() {
        Date fecha = jdFechaInforme.getDate();
        return (fecha != null) ? fecha : new Date();
    }

    // ══════════════════════════════════════════════════════════════════
    //  UI BUILDER (Estructura base)
    // ══════════════════════════════════════════════════════════════════
    private void initComponents() {

        jPanelHeader      = new JPanel();
        pnlHeaderTexto    = new JPanel();
        jLabel1           = new JLabel("HISTORIAL CLÍNICO");
        lblNombrePaciente = new JLabel();
        btnVolver         = new JButton();

        pnlContenedorBlanco = new JPanel();
        pnlCuerpo         = new JPanel();
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

        // Nuevo modelo de tabla con 6 columnas
        grillaHistorial.setModel(new DefaultTableModel(
            new Object[][]{},
            new String[]{"ID", "FECHA", "OBRA SOCIAL", "MÉDICO", "PRECIO", "ESTADO"}
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
        jScrollPane1.setViewportView(grillaHistorial);
    }

    // Variables declaration
    private JButton btnGenerarInforme;
    private JButton btnVerDetalles;
    private JButton btnVolver;
    private JTable  grillaHistorial;
    private JLabel  jLabel1;
    private JLabel  jLabel2;
    private JPanel  jPanelHeader;
    private JPanel  jPanelFooter;
    private JPanel  pnlHeaderTexto;
    private JPanel  pnlBotonesAccion;
    private JPanel  pnlFechaConfig;
    private JPanel  pnlContenedorBlanco;
    private JPanel  pnlCuerpo;
    private JPanel  pnlTablaWrapper;
    private JLabel  lblTituloTabla;
    private JScrollPane jScrollPane1;
    private com.toedter.calendar.JDateChooser jdFechaInforme;
    private JLabel  lblNombrePaciente;
}