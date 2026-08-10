package vista.swing;

// @author lucianoalicata

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

public class VistaHistorialAnalisis extends JPanel implements IVistaHistorialAnalisis {

    private HistorialPresenter presenter;
    private ArrayList<Analisis> historialCargado = new ArrayList<>();
    private boolean cargandoDatos = false;

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
    private final Color C_SELECCION    = new Color(220, 235, 250);
    private final Color COLOR_VERDE_FILA  = new Color(210, 245, 220);
    private final Color COLOR_VERDE_TEXTO = new Color(0, 100, 50);

    public VistaHistorialAnalisis() {
        initComponents();
        aplicarEsteticaProfesional();
        setMinimumSize(new Dimension(900, 550));

        jdFechaInforme.setDate(new Date());
        btnGenerarInforme.setEnabled(false);
        btnVerDetalles.setEnabled(false);

        grillaHistorial.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && !cargandoDatos) {
                int fila = grillaHistorial.getSelectedRow();
                boolean haySeleccion = fila != -1;
                btnVerDetalles   .setEnabled(haySeleccion);
                btnGenerarInforme.setEnabled(haySeleccion);
                if (haySeleccion && fila < historialCargado.size()) {
                    Date fechaAnalisis = historialCargado.get(fila).getFecha();
                    if (fechaAnalisis != null) jdFechaInforme.setDate(fechaAnalisis);
                }
                if (presenter != null && haySeleccion) presenter.onSeleccionarAnalisis();
            }
        });
    }

    private void aplicarEsteticaProfesional() {
        setBackground(C_FONDO);
        setLayout(new BorderLayout());

        jPanelHeader.setBackground(C_NAVY);
        jPanelHeader.setBorder(new EmptyBorder(10, 20, 10, 20));
        jPanelHeader.setLayout(new BorderLayout());

        JPanel pnlIzqHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlIzqHeader.setOpaque(false);
        pnlIzqHeader.add(btnVolver);

        jLabel1.setForeground(C_HEADER_TEXT);
        jLabel1.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        lblNombrePaciente.setForeground(C_BLANCO);
        lblNombrePaciente.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JPanel pnlTitulos = new JPanel();
        pnlTitulos.setOpaque(false);
        pnlTitulos.setLayout(new BoxLayout(pnlTitulos, BoxLayout.Y_AXIS));
        jLabel1.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblNombrePaciente.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlTitulos.add(jLabel1);
        pnlTitulos.add(Box.createVerticalStrut(2));
        pnlTitulos.add(lblNombrePaciente);

        pnlIzqHeader.add(pnlTitulos);
        jPanelHeader.add(pnlIzqHeader, BorderLayout.WEST);
        configurarBotonRetroceso(btnVolver);
        add(jPanelHeader, BorderLayout.NORTH);

        pnlContenedorBlanco = new JPanel(new BorderLayout());
        pnlContenedorBlanco.setBackground(C_BLANCO);
        pnlContenedorBlanco.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 1, 1, 1, C_BORDE),
            new EmptyBorder(10, 14, 10, 14)
        ));

        pnlCuerpo.setBackground(C_BLANCO);
        pnlCuerpo.setLayout(new BorderLayout());

        pnlTablaWrapper.setBackground(C_BLANCO);
        pnlTablaWrapper.setBorder(BorderFactory.createLineBorder(C_BORDE, 1, true));

        lblTituloTabla = new JLabel("ANÁLISIS REALIZADOS");
        lblTituloTabla.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTituloTabla.setForeground(C_TEXTO_FUERTE);
        lblTituloTabla.setBorder(new EmptyBorder(10, 14, 8, 14));

        grillaHistorial.setRowHeight(34);
        grillaHistorial.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        grillaHistorial.setGridColor(new Color(235, 240, 245));
        grillaHistorial.setShowHorizontalLines(true);
        grillaHistorial.setShowVerticalLines(false);
        grillaHistorial.setSelectionBackground(C_SELECCION);
        grillaHistorial.setSelectionForeground(C_NAVY);
        grillaHistorial.setIntercellSpacing(new Dimension(0, 1));
        grillaHistorial.setBorder(BorderFactory.createEmptyBorder());
        grillaHistorial.setFillsViewportHeight(true);

        grillaHistorial.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        grillaHistorial.getTableHeader().setBackground(C_CABECERA_TBL);
        grillaHistorial.getTableHeader().setForeground(C_TEXTO_SUAVE);
        grillaHistorial.getTableHeader().setPreferredSize(new Dimension(0, 34));
        grillaHistorial.getTableHeader().setBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDE));
        grillaHistorial.getTableHeader().setReorderingAllowed(false);

        grillaHistorial.getColumnModel().getColumn(0).setPreferredWidth(50);
        grillaHistorial.getColumnModel().getColumn(0).setMaxWidth(70);
        grillaHistorial.getColumnModel().getColumn(0).setMinWidth(40);
        grillaHistorial.getColumnModel().getColumn(1).setPreferredWidth(90);
        grillaHistorial.getColumnModel().getColumn(1).setMinWidth(80);
        grillaHistorial.getColumnModel().getColumn(2).setPreferredWidth(160);
        grillaHistorial.getColumnModel().getColumn(2).setMinWidth(130);
        grillaHistorial.getColumnModel().getColumn(3).setPreferredWidth(130);
        grillaHistorial.getColumnModel().getColumn(3).setMinWidth(100);
        grillaHistorial.getColumnModel().getColumn(4).setPreferredWidth(90);
        grillaHistorial.getColumnModel().getColumn(4).setMinWidth(80);
        grillaHistorial.getColumnModel().getColumn(5).setPreferredWidth(80);
        grillaHistorial.getColumnModel().getColumn(5).setMaxWidth(100);
        grillaHistorial.getColumnModel().getColumn(5).setMinWidth(70);

        jScrollPane1.setBorder(BorderFactory.createEmptyBorder());
        jScrollPane1.getViewport().setBackground(C_BLANCO);

        pnlTablaWrapper.setLayout(new BorderLayout());
        pnlTablaWrapper.add(lblTituloTabla, BorderLayout.NORTH);
        pnlTablaWrapper.add(jScrollPane1,   BorderLayout.CENTER);

        pnlCuerpo.add(pnlTablaWrapper, BorderLayout.CENTER);
        pnlContenedorBlanco.add(pnlCuerpo, BorderLayout.CENTER);
        add(pnlContenedorBlanco, BorderLayout.CENTER);

        jPanelFooter.setBackground(C_BLANCO);
        jPanelFooter.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(1, 0, 0, 0, C_BORDE),
            new EmptyBorder(12, 14, 12, 14) 
        ));
        jPanelFooter.setLayout(new BorderLayout(16, 0));

        jLabel2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        jLabel2.setForeground(C_TEXTO_SUAVE);

        jdFechaInforme.setPreferredSize(new Dimension(170, 36));
        jdFechaInforme.setMinimumSize(new Dimension(170, 36));
        jdFechaInforme.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        forzarPopupHaciaArriba();

        if (jdFechaInforme.getDateEditor()
                instanceof com.toedter.calendar.JTextFieldDateEditor editor) {
            editor.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            editor.setBackground(C_CAMPO);
            editor.setForeground(C_TEXTO_FUERTE);
            editor.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDE),
                new EmptyBorder(4, 8, 4, 8)
            ));
        }

        pnlFechaConfig.setOpaque(false);
        pnlFechaConfig.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlFechaConfig.add(jLabel2);
        pnlFechaConfig.add(jdFechaInforme);
        jPanelFooter.add(pnlFechaConfig, BorderLayout.WEST);

        configurarBoton(btnVerDetalles,    C_AZUL_MEDIO, "VER DETALLES", 140, 36);
        configurarBoton(btnGenerarInforme, C_VERDE,      "IMPRIMIR",     130, 36);

        pnlBotonesAccion.setOpaque(false);
        pnlBotonesAccion.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlBotonesAccion.add(btnVerDetalles);
        pnlBotonesAccion.add(btnGenerarInforme);
        jPanelFooter.add(pnlBotonesAccion, BorderLayout.EAST);

        add(jPanelFooter, BorderLayout.SOUTH);

        configurarRenderizadorColor();
        revalidate();
        repaint();
    }
    
    private void forzarPopupHaciaArriba() {
        for (Component comp : jdFechaInforme.getComponents()) {
            if (comp instanceof JButton btnCalendar) {
                btnCalendar.addActionListener(e -> {
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        reposicionarPopupCalendario();
                    });
                });
            }
        }
    }

    private void reposicionarPopupCalendario() {
        for (java.awt.Window w : java.awt.Window.getWindows()) {
            if (w instanceof javax.swing.JWindow && w.isVisible()) {
                java.awt.Point ubicacionEnPantalla;
                try {
                    ubicacionEnPantalla = jdFechaInforme.getLocationOnScreen();
                } catch (Exception ex) {
                    return;
                }
                int altoPopup  = w.getHeight();
                int xPopup     = ubicacionEnPantalla.x;
                int yPopup     = ubicacionEnPantalla.y - altoPopup - 2;

                java.awt.Dimension pantalla =
                    java.awt.Toolkit.getDefaultToolkit().getScreenSize();
                int yAbajo = ubicacionEnPantalla.y + jdFechaInforme.getHeight() + altoPopup;
                if (yAbajo > pantalla.height - 40) {
                    w.setLocation(xPopup, yPopup);
                }
            }
        }
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
        btn.setBorder(new EmptyBorder(0, 0, 0, 12));
        ImageIcon ico = icon("/reportes/img/flecha_icon.png", 34, 34);
        if (ico != null) btn.setIcon(ico);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { btn.setForeground(C_BLANCO); }
            @Override public void mouseExited (java.awt.event.MouseEvent e) { btn.setForeground(C_HEADER_TEXT); }
        });
    }

    private ImageIcon icon(String ruta, int w, int h) {
        try {
            java.net.URL url = getClass().getResource(ruta);
            if (url != null) {
                Image img = new ImageIcon(url).getImage()
                    .getScaledInstance(w, h, Image.SCALE_SMOOTH);
                return new ImageIcon(img);
            }
        } catch (Exception e) { }
        return null;
    }

    private void configurarRenderizadorColor() {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
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
                    Object estadoObj = table.getValueAt(row, 5);
                    String estado = estadoObj != null ? estadoObj.toString() : "";
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
        for (int i = 0; i < grillaHistorial.getColumnCount(); i++)
            grillaHistorial.getColumnModel().getColumn(i).setCellRenderer(renderer);
    }

    @Override public void ejecutar() { setVisible(true); }

    @Override
    public void setPresenter(HistorialPresenter presenter) {
        this.presenter = presenter;
        limpiarListeners(btnGenerarInforme);
        limpiarListeners(btnVerDetalles);
        limpiarListeners(btnVolver);
        btnGenerarInforme.addActionListener(e -> presenter.onGenerarInforme());
        btnVerDetalles   .addActionListener(e -> presenter.onVerDetalles());
        btnVolver        .addActionListener(e -> presenter.onVolver());
    }

    private void limpiarListeners(JButton btn) {
        for (java.awt.event.ActionListener al : btn.getActionListeners())
            btn.removeActionListener(al);
    }

    @Override public void limpiarFocos() { requestFocusInWindow(); }

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

    @Override public void habilitarBotonVerDetalles(boolean b) { btnVerDetalles.setEnabled(b); }
    @Override public void habilitarBotonImprimir(boolean b)    { btnGenerarInforme.setEnabled(b); }

    @Override
    public void setFechaSeleccionada(Date fecha) {
        if (fecha != null) jdFechaInforme.setDate(fecha);
    }

    @Override
    public int getAnalisisSeleccionadoId() {
        int fila = grillaHistorial.getSelectedRow();
        if (fila == -1) return -1;
        Object valor = grillaHistorial.getValueAt(fila, 0);
        return (valor instanceof Integer) ? (int) valor : Integer.parseInt(valor.toString());
    }

    @Override
    public Date getFechaSeleccionada() {
        Date fecha = jdFechaInforme.getDate();
        return fecha != null ? fecha : new Date();
    }

    @Override
    public void cargarHistorial(ArrayList<Analisis> lista) {
        cargandoDatos = true;
        this.historialCargado = lista != null ? lista : new ArrayList<>();
        DefaultTableModel modelo = (DefaultTableModel) grillaHistorial.getModel();
        modelo.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        for (Analisis a : historialCargado) {
            String estado  = a.getEstado() != null ? a.getEstado() : "COMPLETO";
            String os      = (a.getObraSocial() != null && !a.getObraSocial().trim().isEmpty())
                             ? a.getObraSocial() : "-";
            String medico  = (a.getMedicoSolicitante() != null && !a.getMedicoSolicitante().trim().isEmpty())
                             ? a.getMedicoSolicitante().toUpperCase() : "-";
            modelo.addRow(new Object[]{
                a.getIdAnalisis(),
                sdf.format(a.getFecha()),
                os,
                medico,
                String.format("$ %.2f", a.getPrecio()),
                estado
            });
        }
        grillaHistorial.clearSelection();
        btnVerDetalles   .setEnabled(false);
        btnGenerarInforme.setEnabled(false);
        configurarRenderizadorColor();
        cargandoDatos = false;
    }

    private void initComponents() {
        jPanelHeader      = new JPanel();
        new JPanel();
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

        grillaHistorial.setModel(new DefaultTableModel(
            new Object[][]{},
            new String[]{"ID", "FECHA", "OBRA SOCIAL", "MÉDICO", "PRECIO", "ESTADO"}
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } });
        jScrollPane1.setViewportView(grillaHistorial);
    }

    private JButton  btnGenerarInforme, btnVerDetalles, btnVolver;
    private JTable   grillaHistorial;
    private JLabel   jLabel1, jLabel2, lblNombrePaciente, lblTituloTabla;
    private JPanel   jPanelHeader, jPanelFooter;
    private JPanel   pnlBotonesAccion, pnlFechaConfig;
    private JPanel   pnlContenedorBlanco, pnlCuerpo, pnlTablaWrapper;
    private JScrollPane jScrollPane1;
    private com.toedter.calendar.JDateChooser jdFechaInforme;
}