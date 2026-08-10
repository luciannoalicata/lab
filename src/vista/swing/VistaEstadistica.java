package vista.swing;

import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import presentador.EstadisticasPresenter;
import vista.interfaces.IVistaEstadistica;

public class VistaEstadistica extends JPanel implements IVistaEstadistica {

    private EstadisticasPresenter presenter;
    private boolean cargandoDatos = false;

    // ── Popups sugerencias ────────────────────────────────────────────
    private JWindow ventanaSugerenciasOS;
    private JWindow ventanaSugerenciasMed;
    private JWindow ventanaSugerenciasPracticas;
    private JList<String>        listaSugerenciasOS;
    private JList<String>        listaSugerenciasMed;
    private JList<String>        listaSugerenciasPracticas;
    private DefaultListModel<String> modeloSugerenciasOS;
    private DefaultListModel<String> modeloSugerenciasMed;
    private DefaultListModel<String> modeloSugerenciasPracticas;

    // ── Paleta BIOTEC ─────────────────────────────────────────────────
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
    private final Color C_SELECCION    = new Color(220, 235, 250);

    private final Font F_TBL_HEADER = new Font("Segoe UI", Font.BOLD, 11);
    private final Font F_TBL_CELL   = new Font("Segoe UI", Font.PLAIN, 12);

    // ── Componentes ───────────────────────────────────────────────────
    private JDateChooser  jdDesde, jdHasta;
    private JTextField    txtObraSocial, txtMedico, txtPractica;
    private JButton       btnFiltrar, btnExportar, btnVolver, btnLimpiarFiltros;
    private JLabel        lblTotalAnalisis, lblTotalFacturado;
    private JPanel        pnlGraficoTorta, pnlGraficoEvolucion, pnlGraficoPracticas;
    private JTable        grillaFacturacion;
    private DefaultTableModel modeloTabla;
    private JPanel        pnlHeader;
    private JLabel        lblTituloHeader;
    private JScrollPane   jScrollPane1;

    public VistaEstadistica() {
        initComponents();
        aplicarEstilo();
        configurarBuscadores();
        configurarDobleClickTabla();
        setFechasPorDefecto();
        setPlaceholders();
        setMinimumSize(new Dimension(1024, 680));
    }

    // ═════════════════════════════════════════════════════════════════
    //  INTERFAZ
    // ═════════════════════════════════════════════════════════════════

    @Override
    public void setPresenter(EstadisticasPresenter presenter) {
        // Limpiar estado anterior ANTES de reasignar
        limpiarFocos();
        this.presenter = presenter;
        limpiarListeners(btnFiltrar);
        limpiarListeners(btnExportar);
        limpiarListeners(btnVolver);
        limpiarListeners(btnLimpiarFiltros);
        btnFiltrar      .addActionListener(e -> presenter.onFiltrar());
        btnExportar     .addActionListener(e -> presenter.onExportarPlanilla());
        btnVolver       .addActionListener(e -> presenter.onVolver());
        btnLimpiarFiltros.addActionListener(e -> limpiarFiltros());
    }

    private void limpiarListeners(JButton btn) {
        for (ActionListener al : btn.getActionListeners()) btn.removeActionListener(al);
    }

    @Override public Date getFechaDesde()  { return jdDesde.getDate(); }
    @Override public Date getFechaHasta()  { return jdHasta.getDate(); }

    @Override public String getObraSocialFiltro() {
        String t = txtObraSocial.getText().trim();
        return (t.isEmpty() || t.equals("TODAS")) ? "TODAS" : t;
    }
    @Override public String getMedicoFiltro() {
        String t = txtMedico.getText().trim();
        return (t.isEmpty() || t.equals("TODOS")) ? "TODOS" : t;
    }
    @Override public String getPracticaFiltro() {
        String t = txtPractica.getText().trim();
        return (t.isEmpty() || t.equals("TODAS")) ? "TODAS" : t;
    }

    @Override public void cargarComboObrasSociales(List<String> obras) { /* no usado */ }
    @Override public void setTotalAnalisis (String t) { lblTotalAnalisis .setText(t); }
    @Override public void setTotalFacturado(String t) { lblTotalFacturado.setText(t); }

    @Override
    public void mostrarGraficoObrasSociales(JPanel p) {
        pnlGraficoTorta.removeAll();
        if (p != null) pnlGraficoTorta.add(p, BorderLayout.CENTER);
        pnlGraficoTorta.revalidate(); pnlGraficoTorta.repaint();
    }
    @Override
    public void mostrarGraficoEvolucion(JPanel p) {
        pnlGraficoEvolucion.removeAll();
        if (p != null) pnlGraficoEvolucion.add(p, BorderLayout.CENTER);
        pnlGraficoEvolucion.revalidate(); pnlGraficoEvolucion.repaint();
    }
    @Override
    public void mostrarGraficoPracticas(JPanel p) {
        pnlGraficoPracticas.removeAll();
        if (p != null) pnlGraficoPracticas.add(p, BorderLayout.CENTER);
        pnlGraficoPracticas.revalidate(); pnlGraficoPracticas.repaint();
    }

    @Override
    public void mostrarDatosTabla(Object[][] datos) {
        cargandoDatos = true;
        modeloTabla.setRowCount(0);
        for (Object[] fila : datos) modeloTabla.addRow(fila);
        cargandoDatos = false;
    }

    @Override
    public void mostrarDetallePracticas(String titulo, String detalle) {
        javax.swing.JTextArea txtArea = new javax.swing.JTextArea(detalle);
        txtArea.setEditable(false);
        txtArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtArea.setBackground(C_CAMPO);
        txtArea.setForeground(C_TEXTO_FUERTE);
        txtArea.setBorder(new EmptyBorder(14, 14, 14, 14));
        JScrollPane scroll = new JScrollPane(txtArea);
        scroll.setPreferredSize(new Dimension(460, 320));
        scroll.setBorder(new LineBorder(C_AZUL_MEDIO, 1));
        JOptionPane.showMessageDialog(this, scroll, titulo, JOptionPane.INFORMATION_MESSAGE);
    }

    @Override public void mostrarMensaje(String m) {
        JOptionPane.showMessageDialog(this, m, "Estadísticas", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override public void ejecutar() { setVisible(true); }

    /**
     * Limpia TODO el estado de la vista.
     * Se llama automáticamente en setPresenter() y cuando el AppRouter
     * navega a otra sección — evita que gráficos y datos "contaminen"
     * otras vistas al volver.
     */
    @Override
    public void limpiarFocos() {
        requestFocusInWindow();

        // ── Ocultar todos los popups flotantes ────────────────────────
        ocultarTodosLosPopups();

        // ── Resetear tabla ────────────────────────────────────────────
        if (modeloTabla   != null) modeloTabla.setRowCount(0);
        if (grillaFacturacion != null) grillaFacturacion.clearSelection();

        // ── Limpiar gráficos ──────────────────────────────────────────
        for (JPanel pnl : new JPanel[]{pnlGraficoTorta, pnlGraficoEvolucion, pnlGraficoPracticas}) {
            if (pnl != null) { pnl.removeAll(); pnl.revalidate(); pnl.repaint(); }
        }

        // ── Resetear tarjetas ─────────────────────────────────────────
        if (lblTotalAnalisis  != null) lblTotalAnalisis .setText("0");
        if (lblTotalFacturado != null) lblTotalFacturado.setText("$ 0.00");
    }

    private void ocultarTodosLosPopups() {
        if (ventanaSugerenciasOS        != null) ventanaSugerenciasOS.setVisible(false);
        if (ventanaSugerenciasMed       != null) ventanaSugerenciasMed.setVisible(false);
        if (ventanaSugerenciasPracticas != null) ventanaSugerenciasPracticas.setVisible(false);
    }

    @Override public int confirmarAccion(String m, String t) {
        return JOptionPane.showConfirmDialog(this, m, t, JOptionPane.YES_NO_OPTION);
    }

    // ═════════════════════════════════════════════════════════════════
    //  PLACEHOLDERS
    // ═════════════════════════════════════════════════════════════════
    private void setPlaceholders() {
        txtObraSocial.setText("TODAS");
        txtMedico    .setText("TODOS");
        txtPractica  .setText("TODAS");

        addPlaceholder(txtObraSocial, "TODAS");
        addPlaceholder(txtMedico,     "TODOS");
        addPlaceholder(txtPractica,   "TODAS");
    }

    private void addPlaceholder(JTextField tf, String placeholder) {
        tf.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) {
                if (tf.getText().equals(placeholder)) tf.setText("");
            }
            @Override public void focusLost(java.awt.event.FocusEvent e) {
                if (tf.getText().trim().isEmpty()) tf.setText(placeholder);
            }
        });
    }

    private void limpiarFiltros() {
        setFechasPorDefecto();
        txtObraSocial.setText("TODAS");
        txtMedico    .setText("TODOS");
        txtPractica  .setText("TODAS");
        ocultarTodosLosPopups();
        if (presenter != null) presenter.onFiltrar();
    }

    // ═════════════════════════════════════════════════════════════════
    //  DOBLE CLIC EN TABLA
    // ═════════════════════════════════════════════════════════════════
    private void configurarDobleClickTabla() {
        grillaFacturacion.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && presenter != null) {
                    int row = grillaFacturacion.rowAtPoint(e.getPoint());
                    if (row < 0) return;
                    // Convertir índice de vista a modelo (por si hay sorting)
                    int modelRow = grillaFacturacion.convertRowIndexToModel(row);
                    Object idObj = modeloTabla.getValueAt(modelRow, 0);
                    if (idObj == null) return;
                    try {
                        presenter.onVerDetallePracticas(Integer.parseInt(idObj.toString()));
                    } catch (NumberFormatException ex) { /* ignorar */ }
                }
            }
        });
    }

    // ═════════════════════════════════════════════════════════════════
    //  BUSCADORES CON AUTOCOMPLETADO
    // ═════════════════════════════════════════════════════════════════
    private void configurarBuscadores() {
        modeloSugerenciasOS        = new DefaultListModel<>();
        modeloSugerenciasMed       = new DefaultListModel<>();
        modeloSugerenciasPracticas = new DefaultListModel<>();

        listaSugerenciasOS        = crearListaSugerencias();
        listaSugerenciasMed       = crearListaSugerencias();
        listaSugerenciasPracticas = crearListaSugerencias();

        listaSugerenciasOS       .setModel(modeloSugerenciasOS);
        listaSugerenciasMed      .setModel(modeloSugerenciasMed);
        listaSugerenciasPracticas.setModel(modeloSugerenciasPracticas);

        // Crear popups DESPUÉS de que el componente esté en la jerarquía
        SwingUtilities.invokeLater(() -> {
            ventanaSugerenciasOS        = crearPopup(listaSugerenciasOS);
            ventanaSugerenciasMed       = crearPopup(listaSugerenciasMed);
            ventanaSugerenciasPracticas = crearPopup(listaSugerenciasPracticas);
        });

        // Click en sugerencia OS
        listaSugerenciasOS.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                elegirSugerencia(listaSugerenciasOS, txtObraSocial, ventanaSugerenciasOS);
            }
        });
        listaSugerenciasMed.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                elegirSugerencia(listaSugerenciasMed, txtMedico, ventanaSugerenciasMed);
            }
        });
        listaSugerenciasPracticas.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                elegirSugerencia(listaSugerenciasPracticas, txtPractica, ventanaSugerenciasPracticas);
            }
        });

        // KeyListeners
        txtObraSocial.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                gestionarTeclas(e, ventanaSugerenciasOS, listaSugerenciasOS, txtObraSocial);
            }
            @Override public void keyReleased(KeyEvent e) {
                if (esTeclaNav(e)) return;
                String t = txtObraSocial.getText().trim();
                if (t.length() >= 1 && !t.equals("TODAS") && presenter != null)
                    presenter.onBuscarSugerenciasOS();
                else if (ventanaSugerenciasOS != null) ventanaSugerenciasOS.setVisible(false);
            }
        });
        txtMedico.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                gestionarTeclas(e, ventanaSugerenciasMed, listaSugerenciasMed, txtMedico);
            }
            @Override public void keyReleased(KeyEvent e) {
                if (esTeclaNav(e)) return;
                String t = txtMedico.getText().trim();
                if (t.length() >= 1 && !t.equals("TODOS") && presenter != null)
                    presenter.onBuscarSugerenciasMedicos();
                else if (ventanaSugerenciasMed != null) ventanaSugerenciasMed.setVisible(false);
            }
        });
        txtPractica.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                gestionarTeclas(e, ventanaSugerenciasPracticas, listaSugerenciasPracticas, txtPractica);
            }
            @Override public void keyReleased(KeyEvent e) {
                if (esTeclaNav(e)) return;
                String t = txtPractica.getText().trim();
                if (t.length() >= 1 && !t.equals("TODAS") && presenter != null)
                    presenter.onBuscarSugerenciasPracticas();
                else if (ventanaSugerenciasPracticas != null) ventanaSugerenciasPracticas.setVisible(false);
            }
        });
    }

    private JList<String> crearListaSugerencias() {
        JList<String> lista = new JList<>();
        lista.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lista.setFixedCellHeight(28);
        lista.setBackground(C_BLANCO);
        lista.setSelectionBackground(C_SELECCION);
        lista.setSelectionForeground(C_NAVY);
        return lista;
    }

    private void elegirSugerencia(JList<String> lista, JTextField txt, JWindow win) {
        String sel = lista.getSelectedValue();
        if (sel != null) {
            txt.setText(sel);
            if (win != null) win.setVisible(false);
            if (presenter != null) presenter.onFiltrar();
        }
    }

    private void gestionarTeclas(KeyEvent e, JWindow win, JList<String> lista, JTextField txt) {
        if (win == null || !win.isVisible()) return;
        DefaultListModel<String> mod = (DefaultListModel<String>) lista.getModel();
        if (mod.isEmpty()) return;
        int idx = lista.getSelectedIndex();
        int sz  = mod.getSize();
        switch (e.getKeyCode()) {
            case KeyEvent.VK_DOWN:
                lista.setSelectedIndex(Math.min(idx + 1, sz - 1));
                lista.ensureIndexIsVisible(lista.getSelectedIndex());
                e.consume(); break;
            case KeyEvent.VK_UP:
                lista.setSelectedIndex(Math.max(idx - 1, 0));
                lista.ensureIndexIsVisible(lista.getSelectedIndex());
                e.consume(); break;
            case KeyEvent.VK_ENTER:
                if (idx != -1) { elegirSugerencia(lista, txt, win); e.consume(); } break;
            case KeyEvent.VK_ESCAPE:
                win.setVisible(false); break;
        }
    }

    private boolean esTeclaNav(KeyEvent e) {
        int k = e.getKeyCode();
        return k == KeyEvent.VK_DOWN || k == KeyEvent.VK_UP
            || k == KeyEvent.VK_ENTER || k == KeyEvent.VK_ESCAPE;
    }

    private JWindow crearPopup(JList<String> lista) {
        Window pw = SwingUtilities.getWindowAncestor(this);
        JWindow win = new JWindow(pw != null ? pw : new JFrame());
        win.setAlwaysOnTop(true);
        win.setFocusableWindowState(false);
        JScrollPane sc = new JScrollPane(lista);
        sc.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(C_AZUL_MEDIO, 1),
            new EmptyBorder(2, 0, 2, 0)));
        win.getContentPane().add(sc);
        return win;
    }

    @Override public void mostrarSugerenciasOS(List<String> s) {
        actualizarPopup(ventanaSugerenciasOS, modeloSugerenciasOS, listaSugerenciasOS, txtObraSocial, s);
    }
    @Override public void mostrarSugerenciasMedicos(List<String> s) {
        actualizarPopup(ventanaSugerenciasMed, modeloSugerenciasMed, listaSugerenciasMed, txtMedico, s);
    }
    @Override public void mostrarSugerenciasPracticas(List<String> s) {
        actualizarPopup(ventanaSugerenciasPracticas, modeloSugerenciasPracticas, listaSugerenciasPracticas, txtPractica, s);
    }

    private void actualizarPopup(JWindow win, DefaultListModel<String> mod,
            JList<String> lista, JTextField txt, List<String> sugs) {
        if (win == null) return;
        mod.clear();
        sugs.forEach(mod::addElement);
        if (sugs.isEmpty()) { win.setVisible(false); return; }
        try {
            java.awt.Point p = txt.getLocationOnScreen();
            win.setBounds(p.x, p.y + txt.getHeight(),
                txt.getWidth(), Math.min(168, mod.size() * 28 + 6));
            win.setVisible(true);
            lista.setSelectedIndex(0);
        } catch (Exception ex) { /* componente no visible aún */ }
    }

    // ═════════════════════════════════════════════════════════════════
    //  INIT COMPONENTS
    // ═════════════════════════════════════════════════════════════════
    private void initComponents() {
        pnlHeader       = new JPanel();
        lblTituloHeader = new JLabel("DASHBOARD ESTADÍSTICO");
        btnVolver       = new JButton();
        btnFiltrar      = new JButton();
        btnExportar     = new JButton();
        btnLimpiarFiltros = new JButton();

        jdDesde       = new JDateChooser();
        jdHasta       = new JDateChooser();
        txtObraSocial = new JTextField();
        txtMedico     = new JTextField();
        txtPractica   = new JTextField();

        lblTotalAnalisis  = new JLabel("0");
        lblTotalFacturado = new JLabel("$ 0.00");

        pnlGraficoTorta     = new JPanel(new BorderLayout());
        pnlGraficoEvolucion = new JPanel(new BorderLayout());
        pnlGraficoPracticas = new JPanel(new BorderLayout());

        String[] cols = {"ID","FECHA","PACIENTE","OBRA SOCIAL","MÉDICO","TOTAL ($)"};
        modeloTabla = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        grillaFacturacion = new JTable(modeloTabla);
        jScrollPane1 = new JScrollPane(grillaFacturacion);
    }

    private void setFechasPorDefecto() {
        Calendar cal = Calendar.getInstance();
        jdHasta.setDate(cal.getTime());
        cal.set(Calendar.DAY_OF_MONTH, 1);
        jdDesde.setDate(cal.getTime());
    }

    // ═════════════════════════════════════════════════════════════════
    //  LAYOUT — todo cabe en pantalla completa sin scrollbar
    // ═════════════════════════════════════════════════════════════════
    private void aplicarEstilo() {
        setBackground(C_FONDO);
        setLayout(new BorderLayout());

        // ── HEADER ──────────────────────────────────────────────────
        pnlHeader.setBackground(C_NAVY);
        pnlHeader.setBorder(new EmptyBorder(0, 16, 0, 16));
        pnlHeader.setPreferredSize(new Dimension(0, 56));
        pnlHeader.setLayout(new BorderLayout());

        JPanel pnlIzq = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlIzq.setOpaque(false);
        configurarBotonRetroceso(btnVolver);
        pnlIzq.add(btnVolver);
        lblTituloHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTituloHeader.setForeground(C_BLANCO);
        lblTituloHeader.setBorder(new EmptyBorder(0, 6, 0, 0));
        pnlIzq.add(lblTituloHeader);
        pnlHeader.add(pnlIzq, BorderLayout.WEST);
        add(pnlHeader, BorderLayout.NORTH);

        // ── CUERPO — GridBagLayout para distribución proporcional ────
        JPanel pnlCuerpo = new JPanel(new GridBagLayout());
        pnlCuerpo.setBackground(C_FONDO);
        pnlCuerpo.setBorder(new EmptyBorder(8, 10, 8, 10));

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill    = GridBagConstraints.BOTH;
        gc.weightx = 1.0;
        gc.insets  = new Insets(0, 0, 6, 0);

        // ── Fila 0: FILTROS ──────────────────────────────────────────
        JPanel pnlFiltros = construirFilaFiltros();
        gc.gridy   = 0;
        gc.weighty = 0;
        pnlCuerpo.add(pnlFiltros, gc);

        // ── Fila 1: TARJETAS ─────────────────────────────────────────
        JPanel pnlTarjetas = new JPanel(new GridLayout(1, 2, 10, 0));
        pnlTarjetas.setOpaque(false);
        pnlTarjetas.add(crearTarjeta("TOTAL ANÁLISIS",  lblTotalAnalisis,  C_AZUL_MEDIO));
        pnlTarjetas.add(crearTarjeta("TOTAL FACTURADO", lblTotalFacturado, C_VERDE));
        gc.gridy   = 1;
        gc.weighty = 0;
        pnlCuerpo.add(pnlTarjetas, gc);

        // ── Fila 2: GRÁFICOS — altura fija proporcional ──────────────
        JPanel pnlGraficos = new JPanel(new GridLayout(1, 3, 8, 0));
        pnlGraficos.setOpaque(false);
        pnlGraficos.add(crearWrapperGrafico("Distribución OS",      pnlGraficoTorta));
        pnlGraficos.add(crearWrapperGrafico("Evolución Mensual",    pnlGraficoEvolucion));
        pnlGraficos.add(crearWrapperGrafico("Top Prácticas",        pnlGraficoPracticas));
        gc.gridy   = 2;
        gc.weighty = 0.30;   // 30% del espacio vertical disponible
        pnlCuerpo.add(pnlGraficos, gc);

        // ── Fila 3: TABLA — expande el resto del espacio ─────────────
        JPanel pnlTablaContenedor = construirPanelTabla();
        gc.gridy   = 3;
        gc.weighty = 0.70;   // 70% del espacio vertical disponible
        gc.insets  = new Insets(0, 0, 0, 0);
        pnlCuerpo.add(pnlTablaContenedor, gc);

        add(pnlCuerpo, BorderLayout.CENTER);
    }

    private JPanel construirFilaFiltros() {
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        pnl.setBackground(C_BLANCO);
        pnl.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(C_BORDE, 1, true),
            new EmptyBorder(3, 8, 3, 8)
        ));

        estilizarDateChooser(jdDesde, 115);
        estilizarDateChooser(jdHasta, 115);
        estilizarCampoBusqueda(txtObraSocial, 130);
        estilizarCampoBusqueda(txtMedico,     130);
        estilizarCampoBusqueda(txtPractica,   130);
        configurarBoton(btnFiltrar,       C_AZUL_MEDIO,  "FILTRAR",  100, 26);
        configurarBoton(btnLimpiarFiltros, C_TEXTO_SUAVE, "LIMPIAR",  80,  26);
        configurarBoton(btnExportar,       C_VERDE,       "EXPORTAR", 100, 26);

        pnl.add(lbl("DESDE:"));   pnl.add(jdDesde);
        pnl.add(lbl("HASTA:"));   pnl.add(jdHasta);
        pnl.add(lbl("OS:"));      pnl.add(txtObraSocial);
        pnl.add(lbl("MÉDICO:"));  pnl.add(txtMedico);
        pnl.add(lbl("PRÁCTICA:")); pnl.add(txtPractica);
        pnl.add(btnFiltrar);
        pnl.add(btnLimpiarFiltros);
        pnl.add(btnExportar);
        return pnl;
    }

    private JPanel construirPanelTabla() {
        JPanel pnl = new JPanel(new BorderLayout(0, 4));
        pnl.setBackground(C_BLANCO);
        pnl.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(C_BORDE, 1, true),
            new EmptyBorder(6, 10, 8, 10)
        ));

        JLabel lblTit = new JLabel("Detalle de Facturación   (doble clic → ver prácticas del análisis)");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTit.setForeground(C_TEXTO_FUERTE);
        pnl.add(lblTit, BorderLayout.NORTH);

        configurarEstiloTabla();
        jScrollPane1.setBorder(BorderFactory.createEmptyBorder());
        jScrollPane1.getViewport().setBackground(C_BLANCO);
        pnl.add(jScrollPane1, BorderLayout.CENTER);
        return pnl;
    }

    // ═════════════════════════════════════════════════════════════════
    //  HELPERS VISUALES
    // ═════════════════════════════════════════════════════════════════
    private JPanel crearTarjeta(String titulo, JLabel lblNum, Color color) {
        JPanel t = new JPanel(new GridLayout(2, 1, 0, 1));
        t.setBackground(C_BLANCO);
        t.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(C_BORDE, 1, true),
            new EmptyBorder(5, 12, 5, 12)
        ));
        JLabel tit = new JLabel(titulo);
        tit.setFont(new Font("Segoe UI", Font.BOLD, 10));
        tit.setForeground(C_TEXTO_SUAVE);
        lblNum.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblNum.setForeground(color);
        t.add(tit); t.add(lblNum);
        return t;
    }

    private JPanel crearWrapperGrafico(String titulo, JPanel inner) {
        JPanel w = new JPanel(new BorderLayout());
        w.setBackground(C_BLANCO);
        w.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(C_BORDE, 1, true),
            new EmptyBorder(4, 4, 4, 4)
        ));
        JLabel lbl = new JLabel(titulo, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(C_TEXTO_FUERTE);
        lbl.setBorder(new EmptyBorder(0, 0, 2, 0));
        inner.setBackground(C_BLANCO);
        w.add(lbl,   BorderLayout.NORTH);
        w.add(inner, BorderLayout.CENTER);
        return w;
    }

    private void configurarBoton(JButton btn, Color bg, String texto, int w, int h) {
        btn.setText(texto);
        btn.setBackground(bg);
        btn.setForeground(C_BLANCO);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(w, h));
    }

    private void configurarBotonRetroceso(JButton btn) {
        btn.setText("←");
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBackground(C_NAVY);
        btn.setForeground(C_HEADER_TEXT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(0, 0, 0, 6));
        ImageIcon ico = icon("/reportes/img/flecha_icon.png", 28, 28);
        if (ico != null) { btn.setIcon(ico); btn.setText(""); }
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setForeground(C_BLANCO); }
            @Override public void mouseExited (MouseEvent e) { btn.setForeground(C_HEADER_TEXT); }
        });
    }

    private void estilizarDateChooser(JDateChooser dc, int w) {
        dc.setPreferredSize(new Dimension(w, 26));
        dc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        if (dc.getDateEditor() instanceof com.toedter.calendar.JTextFieldDateEditor) {
            com.toedter.calendar.JTextFieldDateEditor ed =
                (com.toedter.calendar.JTextFieldDateEditor) dc.getDateEditor();
            ed.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            ed.setBackground(C_CAMPO);
            ed.setForeground(C_TEXTO_FUERTE);
            ed.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDE),
                new EmptyBorder(2, 4, 2, 4)));
        }
    }

    private void estilizarCampoBusqueda(JTextField tf, int w) {
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tf.setBackground(C_CAMPO);
        tf.setForeground(C_TEXTO_FUERTE);
        tf.setCaretColor(C_AZUL_MEDIO);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDE),
            new EmptyBorder(2, 5, 2, 5)));
        tf.setPreferredSize(new Dimension(w, 26));
    }

    private void configurarEstiloTabla() {
        grillaFacturacion.setRowHeight(26);
        grillaFacturacion.setFont(F_TBL_CELL);
        grillaFacturacion.setGridColor(new Color(235, 240, 245));
        grillaFacturacion.setShowHorizontalLines(true);
        grillaFacturacion.setShowVerticalLines(false);
        grillaFacturacion.setSelectionBackground(C_SELECCION);
        grillaFacturacion.setSelectionForeground(C_NAVY);
        grillaFacturacion.setIntercellSpacing(new Dimension(0, 0));
        grillaFacturacion.setBorder(BorderFactory.createEmptyBorder());
        grillaFacturacion.setFillsViewportHeight(true);

        grillaFacturacion.getTableHeader().setFont(F_TBL_HEADER);
        grillaFacturacion.getTableHeader().setBackground(C_CABECERA_TBL);
        grillaFacturacion.getTableHeader().setForeground(C_TEXTO_SUAVE);
        grillaFacturacion.getTableHeader().setPreferredSize(new Dimension(0, 26));
        grillaFacturacion.getTableHeader().setBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDE));
        grillaFacturacion.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(new EmptyBorder(0, 4, 0, 4));
                if (sel) { setBackground(C_SELECCION); setForeground(C_NAVY); }
                else { setBackground(row%2==0?C_BLANCO:C_FILA_PAR); setForeground(C_TEXTO_FUERTE); }
                return this;
            }
        };

        DefaultTableCellRenderer right = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setHorizontalAlignment(SwingConstants.RIGHT);
                setBorder(new EmptyBorder(0, 4, 0, 10));
                if (sel) { setBackground(C_SELECCION); setForeground(C_NAVY); }
                else { setBackground(row%2==0?C_BLANCO:C_FILA_PAR); setForeground(new Color(0,110,60)); }
                return this;
            }
        };

        for (int i = 0; i < grillaFacturacion.getColumnCount(); i++)
            grillaFacturacion.getColumnModel().getColumn(i)
                .setCellRenderer(i == 5 ? right : center);

        grillaFacturacion.getColumnModel().getColumn(0).setMaxWidth(50);
        grillaFacturacion.getColumnModel().getColumn(1).setPreferredWidth(95);
        grillaFacturacion.getColumnModel().getColumn(2).setPreferredWidth(180);
        grillaFacturacion.getColumnModel().getColumn(3).setPreferredWidth(130);
        grillaFacturacion.getColumnModel().getColumn(4).setPreferredWidth(160);
        grillaFacturacion.getColumnModel().getColumn(5).setPreferredWidth(85);
    }

    private JLabel lbl(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.BOLD, 10));
        l.setForeground(C_TEXTO_SUAVE);
        return l;
    }

    // ── Método icon() con BICUBIC ─────────────────────────────────────
    private ImageIcon icon(String ruta, int w, int h) {
        try {
            java.net.URL url = getClass().getResource(ruta);
            if (url == null) return null;
            BufferedImage original = ImageIO.read(url);
            if (original == null) return null;
            BufferedImage escalada = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = escalada.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.drawImage(original, 0, 0, w, h, null);
            g2d.dispose();
            return new ImageIcon(escalada);
        } catch (Exception e) { return null; }
    }
}