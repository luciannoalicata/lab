package vista.swing;

import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import presentador.EstadisticasPresenter;
import vista.interfaces.IVistaEstadistica;

public class VistaEstadistica extends JPanel implements IVistaEstadistica {

    private EstadisticasPresenter presenter;
    private boolean ignorarBusqueda = false;

    private JPopupMenu popupOS, popupMed, popupDet;
    private JList<String> listaOS, listaMed, listaDet;
    private DefaultListModel<String> modeloOS, modeloMed, modeloDet;

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

    private JPanel pnlHeader;
    private JLabel lblTitulo;
    private JButton btnVolver, btnFiltrar, btnLimpiar, btnExportar;

    private JDateChooser jdDesde, jdHasta;
    private JTextField txtOS, txtMedico, txtDeterminacion;

    private JLabel lblTotalAnalisis, lblTotalFacturado;

    private JTable grillaResultados;
    private DefaultTableModel modeloTabla;
    private JScrollPane scrollGrilla;

    private PanelGrafico pnlGraficoOS;
    private PanelGrafico pnlGraficoPracticas;

    public VistaEstadistica() {
        this.setOpaque(true);
        this.setBackground(C_FONDO);
        initComponents();
        construirLayout();
        configurarAutocompletados();
        configurarDobleClickGrilla();
        setFechasPorDefecto();
    }

    @Override
    public void setPresenter(EstadisticasPresenter presenter) {
        limpiarFocos(); 
        this.presenter = presenter;

        limpiarListeners(btnFiltrar);
        limpiarListeners(btnLimpiar);
        limpiarListeners(btnExportar);
        limpiarListeners(btnVolver);

        btnFiltrar.addActionListener(e -> presenter.onFiltrar());
        btnLimpiar.addActionListener(e -> onLimpiarFiltros());
        btnExportar.addActionListener(e -> presenter.onExportar());
        btnVolver.addActionListener(e -> presenter.onVolver());
    }

    // ── MÉTODOS SOLICITADOS ──────────────────────────────────────────
    private void limpiarListeners(JButton btn) {
        for (ActionListener al : btn.getActionListeners()) {
            btn.removeActionListener(al);
        }
    }

    private void setFechasPorDefecto() {
        Calendar cal = Calendar.getInstance();
        jdHasta.setDate(cal.getTime());
        cal.add(Calendar.DAY_OF_MONTH, -10);
        jdDesde.setDate(cal.getTime());
    }
    // ─────────────────────────────────────────────────────────────────

    @Override public void ejecutar() { setVisible(true); }

    @Override
    public void limpiarFocos() {
        if (popupOS != null) popupOS.setVisible(false);
        if (popupMed != null) popupMed.setVisible(false);
        if (popupDet != null) popupDet.setVisible(false);
        
        if (modeloTabla != null) modeloTabla.setRowCount(0);
        if (pnlGraficoOS != null) pnlGraficoOS.limpiar();
        if (pnlGraficoPracticas != null) pnlGraficoPracticas.limpiar();
        if (lblTotalAnalisis != null) lblTotalAnalisis.setText("0");
        if (lblTotalFacturado != null) lblTotalFacturado.setText("$ 0,00");
        
        requestFocusInWindow();
    }

    @Override public void mostrarMensaje(String m) { JOptionPane.showMessageDialog(this, m, "Estadísticas", JOptionPane.INFORMATION_MESSAGE); }
    @Override public int confirmarAccion(String m, String t) { return JOptionPane.showConfirmDialog(this, m, t, JOptionPane.YES_NO_OPTION); }

    @Override public Date getFechaDesde() { return jdDesde.getDate(); }
    @Override public Date getFechaHasta() { return jdHasta.getDate(); }
    @Override public String getObraSocialFiltro() { return (txtOS.getText().trim().equalsIgnoreCase("TODAS")) ? "" : txtOS.getText().trim(); }
    @Override public String getMedicoFiltro() { return (txtMedico.getText().trim().equalsIgnoreCase("TODOS")) ? "" : txtMedico.getText().trim(); }
    @Override public String getDeterminacionFiltro() { return (txtDeterminacion.getText().trim().equalsIgnoreCase("TODAS")) ? "" : txtDeterminacion.getText().trim(); }

    @Override public void mostrarSugerenciasOS(List<String> s) { mostrarPopup(popupOS, listaOS, modeloOS, txtOS, s); }
    @Override public void mostrarSugerenciasMedicos(List<String> s) { mostrarPopup(popupMed, listaMed, modeloMed, txtMedico, s); }
    @Override public void mostrarSugerenciasDeterminaciones(List<String> s) { mostrarPopup(popupDet, listaDet, modeloDet, txtDeterminacion, s); }

    @Override
    public void mostrarResultados(Object[][] datos) {
        modeloTabla.setRowCount(0);
        for (Object[] fila : datos) modeloTabla.addRow(fila);
        grillaResultados.clearSelection();
    }

    @Override
    public void setResumen(String totalAnalisis, String totalFacturado) {
        lblTotalAnalisis.setText(totalAnalisis);
        lblTotalFacturado.setText(totalFacturado);
    }

    @Override
    public void actualizarGraficoOS(Map<String, Integer> datos) {
        pnlGraficoOS.setDatos(datos, TipoGrafico.TORTA, "Distribución por Obra Social");
    }

    @Override
    public void actualizarGraficoPracticas(Map<String, Integer> datos) {
        pnlGraficoPracticas.setDatos(datos, TipoGrafico.BARRAS, "Prácticas más realizadas");
    }

    @Override
    public void mostrarDetallePracticas(String titulo, String detalle) {
        JTextArea area = new JTextArea(detalle);
        area.setEditable(false); 
        area.setFont(new Font("Segoe UI", Font.PLAIN, 13)); 
        area.setBackground(C_CAMPO); 
        area.setForeground(C_TEXTO_FUERTE); 
        area.setBorder(new EmptyBorder(12, 12, 12, 12));
        JScrollPane scroll = new JScrollPane(area); 
        scroll.setPreferredSize(new Dimension(460, 300)); 
        scroll.setBorder(BorderFactory.createLineBorder(C_BORDE));
        JOptionPane.showMessageDialog(this, scroll, titulo, JOptionPane.INFORMATION_MESSAGE);
    }

    private void initComponents() {
        pnlHeader = new JPanel(); lblTitulo = new JLabel("ESTADÍSTICAS Y FACTURACIÓN"); btnVolver = new JButton();
        jdDesde = new JDateChooser(); jdHasta = new JDateChooser();
        txtOS = new JTextField(); txtMedico = new JTextField(); txtDeterminacion = new JTextField();
        btnFiltrar = new JButton("FILTRAR"); btnLimpiar = new JButton("LIMPIAR"); btnExportar = new JButton("EXPORTAR");
        lblTotalAnalisis = new JLabel("0"); lblTotalFacturado = new JLabel("$ 0,00");
        
        String[] cols = {"ID","FECHA","DNI","PACIENTE","MÉDICO","OBRA SOCIAL","PRÁCTICAS"};
        modeloTabla = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        grillaResultados = new JTable(modeloTabla); scrollGrilla = new JScrollPane(grillaResultados);

        pnlGraficoOS = new PanelGrafico(); pnlGraficoPracticas = new PanelGrafico();

        modeloOS = new DefaultListModel<>(); listaOS = new JList<>(modeloOS);
        modeloMed = new DefaultListModel<>(); listaMed = new JList<>(modeloMed);
        modeloDet = new DefaultListModel<>(); listaDet = new JList<>(modeloDet);
        popupOS = crearPopup(listaOS); popupMed = crearPopup(listaMed); popupDet = crearPopup(listaDet);
    }

    private void construirLayout() {
        setLayout(new BorderLayout());

        pnlHeader.setBackground(C_NAVY);
        pnlHeader.setBorder(new EmptyBorder(10, 20, 10, 20)); 
        pnlHeader.setLayout(new BorderLayout());
        
        configurarBotonRetroceso(btnVolver);
        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        izq.setOpaque(false);
        izq.add(btnVolver);
        
        lblTitulo.setForeground(C_BLANCO);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setBorder(new EmptyBorder(0, 8, 0, 0));
        izq.add(lblTitulo);
        
        pnlHeader.add(izq, BorderLayout.WEST);
        add(pnlHeader, BorderLayout.NORTH);

        // CONTENEDOR SCROLL Y ALINEADO
        JPanel pnlCuerpoScroll = new JPanel();
        pnlCuerpoScroll.setLayout(new BoxLayout(pnlCuerpoScroll, BoxLayout.Y_AXIS));
        pnlCuerpoScroll.setBackground(C_FONDO);
        pnlCuerpoScroll.setBorder(new EmptyBorder(15, 15, 15, 15));

        // 1. FILTROS Y TARJETAS
        JPanel pnlTop = new JPanel(new BorderLayout(14, 0));
        pnlTop.setOpaque(false);
        pnlTop.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        pnlTop.add(construirPanelFiltros(), BorderLayout.CENTER);
        pnlTop.add(construirTarjetas(), BorderLayout.EAST);
        pnlCuerpoScroll.add(pnlTop);
        pnlCuerpoScroll.add(Box.createVerticalStrut(15));

        // 2. GRÁFICOS
        JPanel pnlGraficosContainer = construirPanelGraficos();
        pnlGraficosContainer.setPreferredSize(new Dimension(0, 300));
        pnlGraficosContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        pnlCuerpoScroll.add(pnlGraficosContainer);
        pnlCuerpoScroll.add(Box.createVerticalStrut(15));

        // 3. TABLA
        JPanel pnlTablaContainer = construirPanelGrilla();
        pnlTablaContainer.setPreferredSize(new Dimension(0, 450));
        pnlCuerpoScroll.add(pnlTablaContainer);

        // WRAPPER ALINEAR ARRIBA Y SCROLL
        JPanel wrapperAlignTop = new JPanel(new BorderLayout());
        wrapperAlignTop.setBackground(C_FONDO);
        wrapperAlignTop.add(pnlCuerpoScroll, BorderLayout.NORTH);

        JScrollPane scrollRaiz = new JScrollPane(wrapperAlignTop);
        scrollRaiz.setBorder(null);
        scrollRaiz.getVerticalScrollBar().setUnitIncrement(20);
        add(scrollRaiz, BorderLayout.CENTER);
    }

    private JPanel construirPanelFiltros() {
        JPanel pnl = new JPanel(new GridBagLayout()); pnl.setBackground(C_BLANCO);
        pnl.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(C_BORDE, 1, true), new EmptyBorder(10, 14, 10, 14)));
        GridBagConstraints gc = new GridBagConstraints(); gc.fill = GridBagConstraints.HORIZONTAL; gc.anchor = GridBagConstraints.WEST; gc.insets = new Insets(3, 4, 3, 4);

        gc.gridy = 0;
        gc.gridx = 0; gc.weightx = 0; pnl.add(lbl("DESDE"), gc);
        gc.gridx = 1; gc.weightx = 1; estilizarDateChooser(jdDesde,120); pnl.add(jdDesde, gc);
        gc.gridx = 2; gc.weightx = 0; pnl.add(lbl("HASTA"), gc);
        gc.gridx = 3; gc.weightx = 1; estilizarDateChooser(jdHasta,120); pnl.add(jdHasta, gc);

        gc.gridy = 1;
        gc.gridx = 0; gc.weightx = 0; pnl.add(lbl("OBRA SOCIAL"), gc);
        gc.gridx = 1; gc.weightx = 1; estilizarCampo(txtOS,160); pnl.add(txtOS, gc);
        gc.gridx = 2; gc.weightx = 0; pnl.add(lbl("MÉDICO"), gc);
        gc.gridx = 3; gc.weightx = 1; estilizarCampo(txtMedico,160); pnl.add(txtMedico, gc);

        gc.gridy = 2;
        gc.gridx = 0; gc.weightx = 0; pnl.add(lbl("DETERMINACIÓN"), gc);
        gc.gridx = 1; gc.weightx = 1; estilizarCampo(txtDeterminacion,160); pnl.add(txtDeterminacion, gc);

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0)); pnlBtns.setOpaque(false);
        configurarBoton(btnFiltrar, C_AZUL_MEDIO, 110, 30); configurarBoton(btnLimpiar, C_TEXTO_SUAVE, 90, 30); configurarBoton(btnExportar, C_VERDE, 120, 30);
        pnlBtns.add(btnFiltrar); pnlBtns.add(btnLimpiar); pnlBtns.add(btnExportar);
        gc.gridx = 2; gc.gridwidth = 2; gc.weightx = 2; pnl.add(pnlBtns, gc);
        return pnl;
    }

    private JPanel construirTarjetas() {
        JPanel pnl = new JPanel(new GridLayout(2, 1, 0, 8)); pnl.setOpaque(false); pnl.setPreferredSize(new Dimension(200, 0));
        pnl.add(crearTarjeta("TOTAL ANÁLISIS", lblTotalAnalisis, C_AZUL_MEDIO)); pnl.add(crearTarjeta("FACTURADO", lblTotalFacturado, C_VERDE));
        return pnl;
    }

    private JPanel crearTarjeta(String titulo, JLabel lblNum, Color color) {
        JPanel t = new JPanel(new BorderLayout(0, 2)); t.setBackground(C_BLANCO);
        t.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(C_BORDE, 1, true), new EmptyBorder(6, 12, 6, 12)));
        JLabel tit = new JLabel(titulo); tit.setFont(new Font("Segoe UI", Font.BOLD, 10)); tit.setForeground(C_TEXTO_SUAVE);
        lblNum.setFont(new Font("Segoe UI", Font.BOLD, 20)); lblNum.setForeground(color);
        t.add(tit, BorderLayout.NORTH); t.add(lblNum, BorderLayout.CENTER); return t;
    }

    private JPanel construirPanelGrilla() {
        JPanel pnl = new JPanel(new BorderLayout(0, 6)); pnl.setBackground(C_BLANCO);
        pnl.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(C_BORDE, 1, true), new EmptyBorder(8, 10, 8, 10)));
        JLabel lbl = new JLabel("Resultados filtrados   (doble clic → ver prácticas del análisis)"); lbl.setFont(new Font("Segoe UI", Font.BOLD, 11)); lbl.setForeground(C_TEXTO_FUERTE);
        pnl.add(lbl, BorderLayout.NORTH);
        configurarEstiloGrilla(); scrollGrilla.setBorder(BorderFactory.createEmptyBorder()); scrollGrilla.getViewport().setBackground(C_BLANCO);
        pnl.add(scrollGrilla, BorderLayout.CENTER); return pnl;
    }

    private void configurarEstiloGrilla() {
        grillaResultados.setRowHeight(30); grillaResultados.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        grillaResultados.setGridColor(new Color(235, 240, 245)); grillaResultados.setShowHorizontalLines(true); grillaResultados.setShowVerticalLines(false);
        grillaResultados.setSelectionBackground(C_SELECCION); grillaResultados.setSelectionForeground(C_TEXTO_FUERTE);
        grillaResultados.setIntercellSpacing(new Dimension(0, 0)); grillaResultados.setFillsViewportHeight(true); grillaResultados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        grillaResultados.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11)); grillaResultados.getTableHeader().setBackground(C_CABECERA_TBL);
        grillaResultados.getTableHeader().setForeground(C_TEXTO_SUAVE); grillaResultados.getTableHeader().setPreferredSize(new Dimension(0, 30));
        grillaResultados.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDE)); grillaResultados.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setHorizontalAlignment(col == 0 ? CENTER : LEFT); setBorder(new EmptyBorder(0, 8, 0, 8));
                if (sel) { setBackground(C_SELECCION); setForeground(C_TEXTO_FUERTE); } else { setBackground(row%2==0?C_BLANCO:C_FILA_PAR); setForeground(C_TEXTO_FUERTE); }
                return this;
            }
        };
        for (int i = 0; i < grillaResultados.getColumnCount(); i++) grillaResultados.getColumnModel().getColumn(i).setCellRenderer(renderer);

        int[] w = {45, 80, 130, 120, 130, 150, 100};
        for (int i = 0; i < w.length && i < grillaResultados.getColumnCount(); i++) grillaResultados.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
        grillaResultados.getColumnModel().getColumn(0).setMaxWidth(55);
    }

    private JPanel construirPanelGraficos() {
        JPanel pnl = new JPanel(new GridLayout(1, 2, 12, 0)); pnl.setBackground(C_FONDO); pnl.setBorder(new EmptyBorder(6, 12, 12, 12));
        pnl.add(envolverGrafico(pnlGraficoOS, "Obra Social más utilizada")); pnl.add(envolverGrafico(pnlGraficoPracticas, "Prácticas más realizadas"));
        return pnl;
    }

    private JPanel envolverGrafico(JPanel grafico, String titulo) {
        JPanel w = new JPanel(new BorderLayout(0, 4)); w.setBackground(C_BLANCO);
        w.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(C_BORDE, 1, true), new EmptyBorder(8, 10, 8, 10)));
        JLabel lbl = new JLabel(titulo, SwingConstants.CENTER); lbl.setFont(new Font("Segoe UI", Font.BOLD, 12)); lbl.setForeground(C_TEXTO_FUERTE);
        w.add(lbl, BorderLayout.NORTH); w.add(grafico, BorderLayout.CENTER); return w;
    }

    private void configurarDobleClickGrilla() {
        grillaResultados.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && presenter != null) {
                    int row = grillaResultados.rowAtPoint(e.getPoint());
                    if (row < 0) return;
                    int mRow = grillaResultados.convertRowIndexToModel(row);
                    Object id = modeloTabla.getValueAt(mRow, 0);
                    if (id != null) {
                        try { presenter.onVerDetallePracticas(Integer.parseInt(id.toString())); } catch (NumberFormatException ex) {}
                    }
                }
            }
        });
    }

    private void configurarAutocompletados() {
        configurarCampoAutocompletado(txtOS, popupOS, listaOS, modeloOS, () -> { if (presenter!=null && !ignorarBusqueda) presenter.onBuscarSugerenciasOS(); }, () -> seleccionar(listaOS, txtOS, popupOS));
        configurarCampoAutocompletado(txtMedico, popupMed, listaMed, modeloMed, () -> { if (presenter!=null && !ignorarBusqueda) presenter.onBuscarSugerenciasMedicos(); }, () -> seleccionar(listaMed, txtMedico, popupMed));
        configurarCampoAutocompletado(txtDeterminacion, popupDet, listaDet, modeloDet, () -> { if (presenter!=null && !ignorarBusqueda) presenter.onBuscarSugerenciasDeterminaciones(); }, () -> seleccionar(listaDet, txtDeterminacion, popupDet));
    }

    private void configurarCampoAutocompletado(JTextField tf, JPopupMenu popup, JList<String> lista, DefaultListModel<String> modelo, Runnable onBuscar, Runnable onSeleccionar) {
        lista.addMouseListener(new MouseAdapter() { @Override public void mouseClicked(MouseEvent e) { onSeleccionar.run(); } });
        tf.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (popup == null || !popup.isVisible() || modelo.isEmpty()) return;
                int idx = lista.getSelectedIndex(); int sz = modelo.getSize();
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_DOWN   -> { lista.setSelectedIndex(Math.min(idx+1,sz-1)); lista.ensureIndexIsVisible(lista.getSelectedIndex()); e.consume(); }
                    case KeyEvent.VK_UP     -> { lista.setSelectedIndex(Math.max(idx-1,0));    lista.ensureIndexIsVisible(lista.getSelectedIndex()); e.consume(); }
                    case KeyEvent.VK_ENTER  -> { if (idx!=-1) { onSeleccionar.run(); e.consume(); } }
                    case KeyEvent.VK_ESCAPE -> popup.setVisible(false);
                }
            }
        });
        tf.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate (javax.swing.event.DocumentEvent e) { disparar(); }
            @Override public void removeUpdate (javax.swing.event.DocumentEvent e) { disparar(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { disparar(); }
            private void disparar() { String t = tf.getText().trim(); if (t.length() >= 1) onBuscar.run(); else popup.setVisible(false); }
        });
    }

    private void mostrarPopup(JPopupMenu popup, JList<String> lista, DefaultListModel<String> modelo, JTextField tf, List<String> sugerencias) {
        modelo.clear(); sugerencias.forEach(modelo::addElement);
        if (sugerencias.isEmpty()) { popup.setVisible(false); return; }
        int alto = Math.min(180, sugerencias.size() * 28 + 6);
        popup.setPopupSize(tf.getWidth(), alto); popup.show(tf, 0, tf.getHeight()); tf.requestFocusInWindow(); lista.setSelectedIndex(0);
    }

    private void seleccionar(JList<String> lista, JTextField tf, JPopupMenu popup) {
        String sel = lista.getSelectedValue();
        if (sel != null) { ignorarBusqueda = true; tf.setText(sel); ignorarBusqueda = false; popup.setVisible(false); if (presenter != null) presenter.onFiltrar(); }
    }

    private JPopupMenu crearPopup(JList<String> lista) {
        JPopupMenu popup = new JPopupMenu(); popup.setFocusable(false); popup.setBorder(BorderFactory.createEmptyBorder());
        lista.setFont(new Font("Segoe UI", Font.PLAIN, 12)); lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); lista.setFixedCellHeight(28);
        lista.setBackground(C_BLANCO); lista.setSelectionBackground(C_SELECCION); lista.setSelectionForeground(C_TEXTO_FUERTE);
        JScrollPane sc = new JScrollPane(lista); sc.setBorder(BorderFactory.createLineBorder(C_BORDE, 1)); popup.add(sc); return popup;
    }

    private void ocultarPopups() { if (popupOS != null) popupOS.setVisible(false); if (popupMed != null) popupMed.setVisible(false); if (popupDet != null) popupDet.setVisible(false); }
    
    private void onLimpiarFiltros() { 
        ignorarBusqueda=true; 
        setFechasPorDefecto(); 
        txtOS.setText(""); 
        txtMedico.setText(""); 
        txtDeterminacion.setText(""); 
        ignorarBusqueda=false; 
        limpiarFocos(); 
        if (presenter != null) presenter.onFiltrar(); 
    }

    private void configurarBotonRetroceso(JButton btn) {
        btn.setText("←"); btn.setFont(new Font("Segoe UI", Font.BOLD, 16)); btn.setBackground(C_NAVY); btn.setForeground(C_HEADER_TEXT);
        btn.setFocusPainted(false); btn.setBorderPainted(false); btn.setContentAreaFilled(false); btn.setOpaque(false); btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); btn.setBorder(new EmptyBorder(0, 0, 0, 6));
        try { java.net.URL url = getClass().getResource("/reportes/img/flecha_icon.png"); if (url != null) { btn.setIcon(new ImageIcon(ImageIO.read(url).getScaledInstance(30, 30, Image.SCALE_SMOOTH))); btn.setText(""); } } catch (Exception e) {}
        btn.addMouseListener(new MouseAdapter() { @Override public void mouseEntered(MouseEvent e) { btn.setForeground(C_BLANCO); } @Override public void mouseExited(MouseEvent e) { btn.setForeground(C_HEADER_TEXT); } });
    }

    private void configurarBoton(JButton btn, Color bg, int w, int h) {
        btn.setBackground(bg); btn.setForeground(C_BLANCO); btn.setFont(new Font("Segoe UI", Font.BOLD, 12)); btn.setFocusPainted(false); btn.setBorderPainted(false); btn.setOpaque(true); btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); btn.setPreferredSize(new Dimension(w, h));
    }

    private void estilizarCampo(JTextField tf, int w) {
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 12)); tf.setBackground(C_CAMPO); tf.setForeground(C_TEXTO_FUERTE); tf.setCaretColor(C_AZUL_MEDIO); tf.setPreferredSize(new Dimension(w, 28));
        tf.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDE), new EmptyBorder(3, 8, 3, 8)));
        tf.addFocusListener(new FocusAdapter() { @Override public void focusGained(FocusEvent e) { tf.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0,0,2,0,C_AZUL_MEDIO), new EmptyBorder(3,8,3,8))); } @Override public void focusLost(FocusEvent e) { tf.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0,0,2,0,C_BORDE), new EmptyBorder(3,8,3,8))); } });
    }

    private void estilizarDateChooser(JDateChooser dc, int w) {
        dc.setPreferredSize(new Dimension(w, 28)); dc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        if (dc.getDateEditor() instanceof com.toedter.calendar.JTextFieldDateEditor ed) { ed.setFont(new Font("Segoe UI", Font.PLAIN, 12)); ed.setBackground(C_CAMPO); ed.setForeground(C_TEXTO_FUERTE); ed.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0,0,2,0,C_BORDE), new EmptyBorder(3,6,3,6))); }
    }

    private JLabel lbl(String t) { JLabel l = new JLabel(t); l.setFont(new Font("Segoe UI", Font.BOLD, 10)); l.setForeground(C_TEXTO_SUAVE); return l; }

    // --- GRÁFICOS NATIVOS ----------------------------------------------------
    private enum TipoGrafico { TORTA, BARRAS }

    private class PanelGrafico extends JPanel {
        private Map<String, Integer> datos; private TipoGrafico tipo = TipoGrafico.TORTA; private String titulo = "";
        private final Color[] COLORES = { new Color(30, 110, 180), new Color(35, 160, 115), new Color(220, 140, 40), new Color(180, 50, 50), new Color(130, 80, 180), new Color(40, 160, 200), new Color(200, 180, 40) };

        PanelGrafico() { setBackground(C_BLANCO); setOpaque(true); }

        void setDatos(Map<String, Integer> datos, TipoGrafico tipo, String titulo) { this.datos = datos; this.tipo = tipo; this.titulo = titulo; repaint(); }
        void limpiar() { this.datos = null; repaint(); }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g); Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int W = getWidth(), H = getHeight();
            if (datos == null || datos.isEmpty()) {
                g2.setColor(C_TEXTO_SUAVE); g2.setFont(new Font("Segoe UI", Font.PLAIN, 12)); String msg = "Sin datos para el período seleccionado"; FontMetrics fm = g2.getFontMetrics(); g2.drawString(msg, (W - fm.stringWidth(msg)) / 2, H / 2); g2.dispose(); return;
            }
            if (tipo == TipoGrafico.TORTA) pintarTorta(g2, W, H); else pintarBarras(g2, W, H);
            g2.dispose();
        }

        private void pintarTorta(Graphics2D g2, int W, int H) {
            Map<String, Integer> top = top5(datos); 
            int total = top.values().stream().mapToInt(Integer::intValue).sum(); 
            if (total == 0) return;
            
            // CORRECCIÓN 1: 'leyenda' pasa de 160 a 240. 
            // Esto mueve la torta a la izquierda y da muchísimo más espacio al texto.
            int margen = 12, leyenda = 240, cx = (W - leyenda) / 2 + margen, cy = H / 2;
            int radio = Math.min((W - leyenda - margen * 2) / 2, (H - margen * 2) / 2) - 10; 
            if (radio < 20) return;
            
            int startAngle = 0, idx = 0, lyY = margen + 14;
            for (Map.Entry<String, Integer> e : top.entrySet()) {
                int angle = (int) Math.round(360.0 * e.getValue() / total); 
                Color col = COLORES[idx % COLORES.length];
                
                g2.setColor(col); g2.fillArc(cx - radio, cy - radio, radio * 2, radio * 2, startAngle, angle);
                g2.setColor(C_BLANCO); g2.setStroke(new BasicStroke(1.5f)); g2.drawArc(cx - radio, cy - radio, radio * 2, radio * 2, startAngle, angle);
                
                g2.setColor(col); g2.fillRoundRect(W - leyenda + 4, lyY - 10, 12, 12, 3, 3); 
                g2.setColor(C_TEXTO_FUERTE); g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                
                // CORRECCIÓN 2: Truncamos a 35 letras en lugar de 24 para aprovechar el nuevo espacio
                g2.drawString(truncar(e.getKey(), 35) + " (" + e.getValue() + ")", W - leyenda + 20, lyY);
                startAngle += angle; lyY += 20; idx++;
            }
        }

        private void pintarBarras(Graphics2D g2, int W, int H) {
            Map<String, Integer> top = top6(datos); 
            int total = top.size(); 
            if (total == 0) return;
            
            // CORRECCIÓN 3: 'margenIzq' pasa de 140 a 180. 
            // Empuja las barras a la derecha para que los textos largos no choquen con la pared izquierda.
            int margenIzq = 180; 
            int margenDer = 30, margenTop = 20, margenBot = 20; 
            int areaW = W - margenIzq - margenDer, areaH = H - margenTop - margenBot;
            int maxVal = top.values().stream().mapToInt(Integer::intValue).max().orElse(1); 
            if (maxVal == 0) return;
            
            int barH = Math.max(12, areaH / total - 8), gapY = (areaH - barH * total) / (total + 1), idx = 0;
            
            for (Map.Entry<String, Integer> e : top.entrySet()) {
                int barW = (int) ((double) e.getValue() / maxVal * areaW);
                int x = margenIzq;
                int y = margenTop + gapY + idx * (barH + gapY);
                
                g2.setColor(C_TEXTO_FUERTE); g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                
                // CORRECCIÓN 4: Truncamos a 28 letras en lugar de 22 para aprovechar el nuevo margen
                String label = truncar(e.getKey(), 28); FontMetrics fm = g2.getFontMetrics();
                g2.drawString(label, x - fm.stringWidth(label) - 8, y + barH / 2 + 4);
                
                g2.setColor(COLORES[idx % COLORES.length]); g2.fillRoundRect(x, y, barW, barH, 4, 4);
                
                g2.setColor(C_TEXTO_SUAVE); g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                g2.drawString(String.valueOf(e.getValue()), x + barW + 5, y + barH / 2 + 4);
                
                idx++;
            }
            
            g2.setColor(C_BORDE); g2.setStroke(new BasicStroke(1f)); g2.drawLine(margenIzq, margenTop, margenIzq, H - margenBot);
        }

        private Map<String, Integer> top5(Map<String, Integer> m) { return m.entrySet().stream().filter(e -> e.getValue() > 0).sorted(Map.Entry.<String,Integer>comparingByValue().reversed()).limit(5).collect(java.util.stream.Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a,b)->a, java.util.LinkedHashMap::new)); }
        private Map<String, Integer> top6(Map<String, Integer> m) { return m.entrySet().stream().filter(e -> e.getValue() > 0).sorted(Map.Entry.<String,Integer>comparingByValue().reversed()).limit(6).collect(java.util.stream.Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a,b)->a, java.util.LinkedHashMap::new)); }
        private String truncar(String s, int max) { return s.length() <= max ? s : s.substring(0, max - 1) + "…"; }
    }
}