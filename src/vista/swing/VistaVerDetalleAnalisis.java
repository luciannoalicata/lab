package vista.swing;

import vista.interfaces.IVistaVerDetalleAnalisis;
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
import java.awt.Window;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import modelo.ResultadoAnalisis;
import presentador.DetalleAnalisisPresenter;

public class VistaVerDetalleAnalisis extends JPanel implements IVistaVerDetalleAnalisis {

    private DetalleAnalisisPresenter presenter;
    private int idAnalisisActual = -1;
    private ListSelectionListener listenerSeleccionTabla;
    private boolean calculando = false;
    private boolean cargandoDatos = false;

    private JWindow ventanaSugerenciasMed;
    private JList<String> listaSugerenciasMed;
    private DefaultListModel<String> modeloSugerenciasMed;

    // ── Paleta BIOTEC Profesional ────────────────────────────────────
    private final Color C_NAVY = new Color(10, 25, 47);
    private final Color C_FONDO = new Color(238, 242, 246);
    private final Color C_BLANCO = Color.WHITE;
    private final Color C_TEXTO_FUERTE = new Color(40, 50, 60);
    private final Color C_TEXTO_SUAVE = new Color(100, 115, 130);
    private final Color C_BORDE = new Color(215, 225, 235);
    private final Color C_AZUL_MEDIO = new Color(30, 110, 180);
    private final Color C_VERDE = new Color(35, 160, 115);
    private final Color C_ROJO = new Color(220, 53, 69);
    private final Color C_CAMPO = new Color(250, 252, 254);
    private final Color C_CABECERA_TBL = new Color(245, 248, 252);
    private final Color C_FILA_PAR = new Color(252, 254, 255);
    private final Color C_HEADER_TEXT = new Color(175, 205, 235);
    private final Color C_NARANJA_ALERTA = new Color(230, 126, 34);
    private final Color C_SELECCION = new Color(210, 232, 250);

    public VistaVerDetalleAnalisis() {
        initComponents();
        aplicarEsteticaProfesional();
        configurarBuscadorMedicoDinamico();
        configurarNavegacionEnter();
        configurarDobleClicReferencia(); // ← Nuevo evento para expandir textos largos
        configurarFocoCelda();
        setMinimumSize(new Dimension(900, 600));
    }

    @Override
    public void setPresenter(DetalleAnalisisPresenter presenter) {
        this.presenter = presenter;
        
        limpiarListeners(btnImprimir);
        limpiarListeners(btnEditar);
        limpiarListeners(btnEliminarFila);
        limpiarListeners(btnCerrar);
        
        btnImprimir.addActionListener(e -> presenter.onImprimir());
        btnEditar.addActionListener(e -> presenter.onEditar());
        btnEliminarFila.addActionListener(e -> presenter.onEliminarFila());
        btnCerrar.addActionListener(e -> presenter.onVolver());
        
        if (listenerSeleccionTabla != null) {
            grillaDetallesAnalisis.getSelectionModel().removeListSelectionListener(listenerSeleccionTabla);
        }
        
        listenerSeleccionTabla = e -> {
            if (!e.getValueIsAdjusting() && !cargandoDatos) {
                presenter.onSeleccionarAnalisis();
            }
        };
        grillaDetallesAnalisis.getSelectionModel().addListSelectionListener(listenerSeleccionTabla);
    }

    private void limpiarListeners(JButton btn) {
        for (java.awt.event.ActionListener al : btn.getActionListeners()) {
            btn.removeActionListener(al);
        }
    }

    private void configurarDobleClicReferencia() {
        grillaDetallesAnalisis.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int col = grillaDetallesAnalisis.columnAtPoint(e.getPoint());
                    int row = grillaDetallesAnalisis.rowAtPoint(e.getPoint());
                    // 5 es la columna de Valores de Referencia
                    if (col == 5 && row >= 0) {
                        Object val = grillaDetallesAnalisis.getModel().getValueAt(row, 5);
                        if (val != null && !val.toString().trim().isEmpty()) {
                            mostrarValoresReferenciaCompletos(val.toString());
                        }
                    }
                }
            }
        });
    }

    private void mostrarValoresReferenciaCompletos(String referencia) {
        javax.swing.JTextArea txtArea = new javax.swing.JTextArea(referencia.replace(";", "\n"));
        txtArea.setEditable(false);
        txtArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtArea.setLineWrap(true);
        txtArea.setWrapStyleWord(true);
        txtArea.setBackground(new Color(250, 252, 254));
        txtArea.setForeground(C_TEXTO_FUERTE);
        txtArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scroll = new JScrollPane(txtArea);
        scroll.setPreferredSize(new Dimension(380, 250));
        scroll.setBorder(BorderFactory.createLineBorder(C_AZUL_MEDIO, 1));
        
        JOptionPane.showMessageDialog(this, scroll, "Valores de Referencia", JOptionPane.PLAIN_MESSAGE);
    }

    private void configurarFocoCelda() {
        grillaDetallesAnalisis.setColumnSelectionAllowed(true);
        grillaDetallesAnalisis.setRowSelectionAllowed(true);
        
        grillaDetallesAnalisis.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                int row = grillaDetallesAnalisis.getSelectedRow();
                if (row >= 0 && esFilaEditable(row)) {
                    grillaDetallesAnalisis.setColumnSelectionInterval(3, 3);
                }
            }
        });
    }

    private void configurarNavegacionEnter() {
        javax.swing.InputMap im = grillaDetallesAnalisis.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        javax.swing.ActionMap am = grillaDetallesAnalisis.getActionMap();
        im.put(javax.swing.KeyStroke.getKeyStroke("ENTER"), "enterAction");

        am.put("enterAction", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                int row = grillaDetallesAnalisis.getSelectedRow();
                
                if (grillaDetallesAnalisis.isEditing()) {
                    grillaDetallesAnalisis.getCellEditor().stopCellEditing();
                }

                int total = grillaDetallesAnalisis.getRowCount();
                for (int sig = row + 1; sig < total; sig++) {
                    if (esFilaEditable(sig)) {
                        grillaDetallesAnalisis.setRowSelectionInterval(sig, sig);
                        grillaDetallesAnalisis.setColumnSelectionInterval(3, 3);
                        grillaDetallesAnalisis.scrollRectToVisible(grillaDetallesAnalisis.getCellRect(sig, 3, true));
                        grillaDetallesAnalisis.editCellAt(sig, 3);
                        Component ed = grillaDetallesAnalisis.getEditorComponent();
                        if (ed != null) ed.requestFocusInWindow();
                        return;
                    }
                }
                btnEditar.requestFocusInWindow();
            }
        });
    }

    private void aplicarCellEditor() {
        JTextField editorField = new JTextField();
        editorField.setFont(new Font("Segoe UI", Font.BOLD, 13));
        editorField.setBorder(new EmptyBorder(0, 8, 0, 8));
        editorField.setHorizontalAlignment(JTextField.CENTER);

        DefaultCellEditor cellEditor = new DefaultCellEditor(editorField) {
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                String texto = value != null ? value.toString() : "";
                if (texto.matches("^-?[\\d.,]+$")) {
                    texto = texto.replace(".", "");
                }
                return super.getTableCellEditorComponent(table, texto, isSelected, row, column);
            }

            @Override
            public Object getCellEditorValue() {
                Object value = super.getCellEditorValue();
                if (value == null) return "";
                String texto = value.toString().trim();
                
                if (texto.matches("^-?\\d{4,}([.,]\\d+)?$")) {
                    try {
                        String[] partes = texto.split("[.,]");
                        String parteEntera = partes[0];
                        String parteDecimal = partes.length > 1 ? partes[1] : "";
                        
                        java.text.DecimalFormatSymbols dfs = new java.text.DecimalFormatSymbols();
                        dfs.setGroupingSeparator('.');
                        java.text.DecimalFormat df = new java.text.DecimalFormat("#,###", dfs);
                        
                        String formateado = df.format(Long.parseLong(parteEntera));
                        if (partes.length > 1) {
                            formateado += "," + parteDecimal; 
                        }
                        return formateado;
                    } catch (Exception ex) {
                        return texto;
                    }
                }
                return texto;
            }
        };
        
        if (grillaDetallesAnalisis.getColumnCount() > 3) {
            grillaDetallesAnalisis.getColumnModel().getColumn(3).setCellEditor(cellEditor);
        }
    }
    
    private boolean esFilaEditable(int row) {
        Object id = grillaDetallesAnalisis.getModel().getValueAt(row, 0);
        if (id == null) return false;
        try {
            if (Integer.parseInt(id.toString()) == -1) return false;
        } catch (NumberFormatException e) {
            return false;
        }
        Object nombre = grillaDetallesAnalisis.getModel().getValueAt(row, 2);
        if (nombre != null) {
            String nombreFila = nombre.toString().trim();
            if (nombreFila.startsWith("---") && nombreFila.endsWith("---")) {
                return false;
            }
        }
        return true;
    }

    // ════════════════════════════════════════════════════════════════
    //  ESTÉTICA PROFESIONAL
    // ════════════════════════════════════════════════════════════════
    private void aplicarEsteticaProfesional() {
        setBackground(C_FONDO);
        setLayout(new BorderLayout());

        // ── HEADER ──────────────────────────────────────────────────────
        jPanelHeader.setBackground(C_NAVY);
        jPanelHeader.setBorder(new EmptyBorder(10, 20, 10, 20));
        jPanelHeader.setLayout(new BorderLayout());

        JPanel pnlIzqHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlIzqHeader.setOpaque(false);
        pnlIzqHeader.add(btnCerrar);

        jLabel1.setForeground(C_HEADER_TEXT);
        jLabel1.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        lblNombrePaciente.setForeground(C_BLANCO);
        lblNombrePaciente.setFont(new Font("Segoe UI", Font.BOLD, 18));

        lblFechaAnalisis.setForeground(new Color(200, 220, 240));
        lblFechaAnalisis.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JPanel pnlTitulos = new JPanel();
        pnlTitulos.setOpaque(false);
        pnlTitulos.setLayout(new BoxLayout(pnlTitulos, BoxLayout.Y_AXIS));
        jLabel1.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblNombrePaciente.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblFechaAnalisis.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlTitulos.add(jLabel1);
        pnlTitulos.add(Box.createVerticalStrut(2));
        pnlTitulos.add(lblNombrePaciente);
        pnlTitulos.add(Box.createVerticalStrut(2));
        pnlTitulos.add(lblFechaAnalisis);

        pnlIzqHeader.add(pnlTitulos);
        jPanelHeader.add(pnlIzqHeader, BorderLayout.WEST);

        configurarBotonRetroceso(btnCerrar);

        JPanel pnlDerHeader = new JPanel(new GridBagLayout());
        pnlDerHeader.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();

        jLabel4.setForeground(C_HEADER_TEXT);
        jLabel4.setFont(new Font("Segoe UI", Font.BOLD, 11));

        g.gridx = 0; g.gridy = 0; g.anchor = GridBagConstraints.WEST; g.insets = new Insets(0, 0, 4, 0);
        pnlDerHeader.add(jLabel4, g);

        g.gridx = 0; g.gridy = 1; g.insets = new Insets(0, 0, 0, 0);
        pnlDerHeader.add(txtMedicoSolicitante, g);

        jPanelHeader.add(pnlDerHeader, BorderLayout.EAST);

        estilizarCampoBusqueda(txtMedicoSolicitante);

        add(jPanelHeader, BorderLayout.NORTH);

        // ── CONTENEDOR PRINCIPAL ──────────────────────────────────────
        pnlContenedorBlanco = new JPanel(new BorderLayout());
        pnlContenedorBlanco.setBackground(C_BLANCO);
        pnlContenedorBlanco.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 1, 1, 1, C_BORDE),
            new EmptyBorder(10, 14, 10, 14)
        ));

        // ── CUERPO ──────────────────────────────────────────────────────
        pnlCuerpo.setBackground(C_BLANCO);
        pnlCuerpo.setLayout(new BorderLayout());

        // ── TABLA WRAPPER ──────────────────────────────────────────────
        pnlTablaWrapper.setBackground(C_BLANCO);
        pnlTablaWrapper.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(0, 0, 0, 0),
            BorderFactory.createLineBorder(C_BORDE, 1, true)
        ));

        lblTituloTabla = new JLabel("RESULTADOS DEL ANÁLISIS");
        lblTituloTabla.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTituloTabla.setForeground(C_TEXTO_FUERTE);
        lblTituloTabla.setBorder(new EmptyBorder(10, 14, 8, 14));

        grillaDetallesAnalisis.setRowHeight(34);
        grillaDetallesAnalisis.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        grillaDetallesAnalisis.setGridColor(new Color(235, 240, 245));
        grillaDetallesAnalisis.setShowHorizontalLines(true);
        grillaDetallesAnalisis.setShowVerticalLines(false);
        grillaDetallesAnalisis.setSelectionBackground(C_SELECCION);
        grillaDetallesAnalisis.setSelectionForeground(C_NAVY);
        grillaDetallesAnalisis.setIntercellSpacing(new Dimension(0, 1));
        grillaDetallesAnalisis.setFocusable(true);
        grillaDetallesAnalisis.setBorder(BorderFactory.createEmptyBorder());
        grillaDetallesAnalisis.setFillsViewportHeight(true);

        grillaDetallesAnalisis.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        grillaDetallesAnalisis.getTableHeader().setBackground(C_CABECERA_TBL);
        grillaDetallesAnalisis.getTableHeader().setForeground(C_TEXTO_SUAVE);
        grillaDetallesAnalisis.getTableHeader().setPreferredSize(new Dimension(0, 34));
        grillaDetallesAnalisis.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDE));
        grillaDetallesAnalisis.getTableHeader().setReorderingAllowed(false);

        jScrollPane1.setBorder(BorderFactory.createEmptyBorder());
        jScrollPane1.getViewport().setBackground(C_BLANCO);

        pnlTablaWrapper.setLayout(new BorderLayout());
        pnlTablaWrapper.add(lblTituloTabla, BorderLayout.NORTH);
        pnlTablaWrapper.add(jScrollPane1, BorderLayout.CENTER);

        pnlCuerpo.add(pnlTablaWrapper, BorderLayout.CENTER);
        pnlContenedorBlanco.add(pnlCuerpo, BorderLayout.CENTER);
        add(pnlContenedorBlanco, BorderLayout.CENTER);

        // ── FOOTER ──────────────────────────────────────────────────────
        jPanelFooter.setBackground(C_BLANCO);
        jPanelFooter.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(1, 0, 0, 0, C_BORDE),
            new EmptyBorder(10, 14, 10, 14)
        ));
        jPanelFooter.setLayout(new BorderLayout());

        jLabel3.setFont(new Font("Segoe UI", Font.BOLD, 12));
        jLabel3.setForeground(C_TEXTO_SUAVE);
        jdFechaInforme.setPreferredSize(new Dimension(150, 34));
        jdFechaInforme.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        if (jdFechaInforme.getDateEditor() instanceof com.toedter.calendar.JTextFieldDateEditor) {
            com.toedter.calendar.JTextFieldDateEditor editor =
                (com.toedter.calendar.JTextFieldDateEditor) jdFechaInforme.getDateEditor();
            editor.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            editor.setBackground(C_CAMPO);
            editor.setForeground(C_TEXTO_FUERTE);
            editor.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDE),
                new EmptyBorder(4, 8, 4, 8)
            ));
        }

        JPanel pnlFooterIzq = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlFooterIzq.setOpaque(false);
        pnlFooterIzq.add(jLabel3);
        pnlFooterIzq.add(jdFechaInforme);
        jPanelFooter.add(pnlFooterIzq, BorderLayout.WEST);

        configurarBoton(btnEliminarFila, C_ROJO, "ELIMINAR FILA", 130, 36);
        configurarBoton(btnImprimir, C_AZUL_MEDIO, "IMPRIMIR", 120, 36);
        configurarBoton(btnEditar, C_VERDE, "GUARDAR CAMBIOS", 150, 36);

        JPanel pnlFooterDer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlFooterDer.setOpaque(false);
        pnlFooterDer.add(btnEliminarFila);
        pnlFooterDer.add(btnImprimir);
        pnlFooterDer.add(btnEditar);
        jPanelFooter.add(pnlFooterDer, BorderLayout.EAST);

        add(jPanelFooter, BorderLayout.SOUTH);

        aplicarColumnas();
        aplicarRenderersConTitulos();
        aplicarCellEditor(); 
        
        this.revalidate();
        this.repaint();
    }

    private void estilizarCampoBusqueda(JTextField tf) {
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBackground(new Color(25, 45, 75));
        tf.setForeground(C_BLANCO);
        tf.setCaretColor(C_BLANCO);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 80, 120), 1, true),
            new EmptyBorder(6, 10, 6, 10)
        ));
        tf.setPreferredSize(new Dimension(280, 34));
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

        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setForeground(C_BLANCO); }
            @Override public void mouseExited(MouseEvent e) { btn.setForeground(C_HEADER_TEXT); }
        });
    }

    private ImageIcon icon(String ruta, int w, int h) {
        try {
            java.net.URL url = getClass().getResource(ruta);
            if (url != null) {
                Image img = new ImageIcon(url).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
                return new ImageIcon(img);
            }
        } catch (Exception e) {}
        return null;
    }

    private void aplicarColumnas() {
        if (grillaDetallesAnalisis.getColumnCount() < 6) return;

        grillaDetallesAnalisis.getColumnModel().getColumn(0).setMinWidth(0);
        grillaDetallesAnalisis.getColumnModel().getColumn(0).setMaxWidth(0);
        grillaDetallesAnalisis.getColumnModel().getColumn(0).setWidth(0);
        grillaDetallesAnalisis.getColumnModel().getColumn(0).setPreferredWidth(0);

        grillaDetallesAnalisis.getColumnModel().getColumn(1).setPreferredWidth(80);
        grillaDetallesAnalisis.getColumnModel().getColumn(1).setMaxWidth(95);
        grillaDetallesAnalisis.getColumnModel().getColumn(1).setMinWidth(70);

        grillaDetallesAnalisis.getColumnModel().getColumn(2).setPreferredWidth(240);
        grillaDetallesAnalisis.getColumnModel().getColumn(2).setMinWidth(180);

        grillaDetallesAnalisis.getColumnModel().getColumn(3).setPreferredWidth(120);
        grillaDetallesAnalisis.getColumnModel().getColumn(3).setMinWidth(100);

        grillaDetallesAnalisis.getColumnModel().getColumn(4).setPreferredWidth(90);
        grillaDetallesAnalisis.getColumnModel().getColumn(4).setMaxWidth(100);
        grillaDetallesAnalisis.getColumnModel().getColumn(4).setMinWidth(80);

        grillaDetallesAnalisis.getColumnModel().getColumn(5).setPreferredWidth(320);
        grillaDetallesAnalisis.getColumnModel().getColumn(5).setMinWidth(230);
    }

    private void aplicarRenderersConTitulos() {
        if (grillaDetallesAnalisis.getColumnCount() < 6) return;

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table,
                    Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

                Object id = table.getModel().getValueAt(row, 0);
                boolean esTituloGenerado = false;
                try {
                    esTituloGenerado = (id != null && Integer.parseInt(id.toString()) == -1);
                } catch (Exception e) {}

                String nombreFila = table.getModel().getValueAt(row, 2) != null ? table.getModel().getValueAt(row, 2).toString().replaceAll("<[^>]*>", "").trim() : "";
                boolean esSubtitulo = nombreFila.startsWith("---") && nombreFila.endsWith("---");
                boolean esModoTitulo = esTituloGenerado || esSubtitulo;

                if (esModoTitulo) {
                    setOpaque(true);
                    String nombreLimpio = nombreFila.replace("---", "").trim();
                    setFont(nombreLimpio.length() > 30 ? new Font("Segoe UI", Font.BOLD, 12) : new Font("Segoe UI", Font.BOLD, 13));

                    Color bgColor = new Color(225, 235, 245);
                    Color fgColor = new Color(10, 35, 75);
                    Color accentColor = new Color(0, 102, 153);

                    if (esSubtitulo && !esTituloGenerado) {
                        bgColor = new Color(240, 245, 250);
                        fgColor = new Color(60, 80, 100);
                        accentColor = new Color(150, 180, 200);
                    }

                    setBackground(bgColor);
                    setForeground(fgColor);
                    setHorizontalAlignment(SwingConstants.CENTER);
                    setToolTipText(null);

                    if (col == 0) {
                        setText("");
                        setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createMatteBorder(1, 4, 1, 0, accentColor),
                                new EmptyBorder(0, 0, 0, 0)));
                    } else if (col == 1) {
                        setText("");
                        setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, accentColor));
                    } else if (col == 2) {
                        setText(nombreLimpio);
                        setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createMatteBorder(1, 0, 1, 0, accentColor),
                                new EmptyBorder(0, 0, 0, 0)));
                    } else {
                        setText("");
                        setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, accentColor));
                    }

                } else {
                    setOpaque(true);
                    setFont(col == 3 ? new Font("Segoe UI", Font.BOLD, 13) : new Font("Segoe UI", Font.PLAIN, 13));
                    
                    if (col == 3) {
                        Object objRes = table.getModel().getValueAt(row, 3);
                        Object objRef = table.getModel().getValueAt(row, 5);
                        String resActual = objRes != null ? objRes.toString() : "";
                        String refActual = objRef != null ? objRef.toString() : "";
                        
                        if (!resActual.isEmpty()) {
                            setForeground(evaluarAlertaResultado(resActual, refActual));
                            if (getForeground().equals(C_ROJO) || getForeground().equals(C_NARANJA_ALERTA)) {
                                setFont(new Font("Segoe UI", Font.BOLD, 14));
                            }
                        } else {
                            setForeground(C_TEXTO_FUERTE);
                        }
                    } else {
                        setForeground(C_TEXTO_FUERTE);
                    }

                    if (isSelected) {
                        setBackground(C_SELECCION);
                    } else {
                        setBackground(row % 2 == 0 ? C_BLANCO : C_FILA_PAR);
                    }

                    setHorizontalAlignment(SwingConstants.CENTER);

                    if (hasFocus && col == 3 && esFilaEditable(row)) {
                        setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(Color.BLACK, 2),
                                new EmptyBorder(0, 8, 0, 8)
                        ));
                    } else {
                        setBorder(new EmptyBorder(0, 10, 0, 10));
                    }

                    Object val = table.getModel().getValueAt(row, col);
                    String textoOriginal = val != null ? val.toString() : "";

                    if ((col == 5 || col == 2) && textoOriginal.contains(";")) {
                        String[] lineas = textoOriginal.split(";");
                        StringBuilder sb = new StringBuilder("<html>");
                        for (int i = 0; i < lineas.length; i++) {
                            String lineaBlindada = lineas[i].trim()
                                    .replace("<", "&lt;")
                                    .replace(">", "&gt;")
                                    .replace(" ", "&nbsp;");
                            sb.append(lineaBlindada);
                            if (i < lineas.length - 1) sb.append("<br>");
                        }
                        sb.append("</html>");
                        setText(sb.toString());
                        
                        if (col == 5) setToolTipText("<html>" + textoOriginal.replace(";", "<br>") + "<br><br><i>(Doble clic para expandir)</i></html>");
                        else setToolTipText(null);
                    } else {
                        setText(textoOriginal);
                        if (col == 5 && textoOriginal.length() > 30) setToolTipText("<html>" + textoOriginal + "<br><br><i>(Doble clic para expandir)</i></html>");
                        else setToolTipText(null);
                    }
                }

                return this;
            }
        };

        for (int i = 0; i < grillaDetallesAnalisis.getColumnCount(); i++) {
            grillaDetallesAnalisis.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }

    private void ajustarAlturaFilas() {
        for (int row = 0; row < grillaDetallesAnalisis.getRowCount(); row++) {
            Object nom = grillaDetallesAnalisis.getValueAt(row, 2);
            boolean esTitulo = nom != null && nom.toString().startsWith("---");
            
            if (esTitulo) {
                grillaDetallesAnalisis.setRowHeight(row, 34);
                continue;
            }
            
            Object valRef = grillaDetallesAnalisis.getValueAt(row, 5);
            Object valNom = grillaDetallesAnalisis.getValueAt(row, 2);
            
            int lineasRef = valRef != null && valRef.toString().contains(";") ? valRef.toString().split(";").length : 1;
            int lineasNom = valNom != null && valNom.toString().contains(";") ? valNom.toString().split(";").length : 1;
            
            int maxLineas = Math.max(lineasRef, lineasNom);
            int alturaCalculada = Math.max(34, maxLineas * 18 + 8);
            
            // Capped at 75px max to preserve visual harmony
            grillaDetallesAnalisis.setRowHeight(row, Math.min(alturaCalculada, 75));
        }
    }

    private Color evaluarAlertaResultado(String resStr, String refStr) {
        if (resStr == null || resStr.isEmpty() || refStr == null || refStr.isEmpty()) {
            return C_TEXTO_FUERTE;
        }

        String refBaja = refStr.toLowerCase();
        if (refBaja.contains("\n") || refBaja.contains("hombre") || refBaja.contains("mujer") ||
            refBaja.contains("niño") || refBaja.contains("fase") || refBaja.contains("varon") ||
            refBaja.contains("positivo") || refBaja.contains("negativo")) {
            return C_TEXTO_FUERTE;
        }

        try {
            double valorIngresado = parsearNumeroLab(resStr);
            if (Double.isNaN(valorIngresado)) {
                return C_TEXTO_FUERTE;
            }

            if (refBaja.contains("hasta") || refBaja.contains("<")) {
                int pos = refBaja.contains("hasta") ? refBaja.indexOf("hasta") + 5 : refBaja.indexOf("<") + 1;
                double max = parsearNumeroLab(refBaja.substring(pos));
                if (Double.isNaN(max)) {
                    return C_TEXTO_FUERTE;
                }
                return (valorIngresado <= max) ? C_VERDE : C_ROJO;
            }

            String separador = refBaja.contains(" a ") ? " a " : (refBaja.contains("-") ? "-" : null);
            if (separador != null) {
                String[] partes = refBaja.split(separador);
                if (partes.length == 2) {
                    double min = parsearNumeroLab(partes[0]);
                    double max = parsearNumeroLab(partes[1]);
                    if (!Double.isNaN(min) && !Double.isNaN(max)) {
                        if (min > max) {
                            double temp = min;
                            min = max;
                            max = temp;
                        }

                        if (valorIngresado >= min && valorIngresado <= max) {
                            return C_VERDE;
                        } else {
                            double tolerancia = (max - min) * 0.15;
                            if (valorIngresado >= (min - tolerancia) && valorIngresado <= (max + tolerancia)) {
                                return C_NARANJA_ALERTA;
                            } else {
                                return C_ROJO;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {}
        return C_TEXTO_FUERTE;
    }

    private double parsearNumeroLab(String numStr) {
        String s = numStr.replaceAll("[^0-9.,]", "");
        if (s.isEmpty()) return Double.NaN;
        if (s.contains(",")) return Double.parseDouble(s.replace(".", "").replace(",", "."));
        if (s.contains(".")) {
            long cantPuntos = s.chars().filter(ch -> ch == '.').count();
            if (cantPuntos > 1) return Double.parseDouble(s.replace(".", ""));
            String[] partes = s.split("\\.");
            if (partes.length == 2 && partes[1].length() == 3) return Double.parseDouble(s.replace(".", ""));
        }
        return Double.parseDouble(s);
    }

    private void configurarBuscadorMedicoDinamico() {
        modeloSugerenciasMed = new DefaultListModel<>();
        listaSugerenciasMed = new JList<>(modeloSugerenciasMed);
        listaSugerenciasMed.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        listaSugerenciasMed.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaSugerenciasMed.setFixedCellHeight(28);
        listaSugerenciasMed.setBackground(C_BLANCO);
        listaSugerenciasMed.setSelectionBackground(C_SELECCION);
        listaSugerenciasMed.setSelectionForeground(C_NAVY);

        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        ventanaSugerenciasMed = new JWindow(parentWindow);
        ventanaSugerenciasMed.setAlwaysOnTop(true);
        ventanaSugerenciasMed.setFocusableWindowState(false);
        JScrollPane scroll = new JScrollPane(listaSugerenciasMed);
        scroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_AZUL_MEDIO, 1),
                new EmptyBorder(2, 0, 2, 0)
        ));
        ventanaSugerenciasMed.getContentPane().add(scroll);

        txtMedicoSolicitante.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (ventanaSugerenciasMed.isVisible() && !modeloSugerenciasMed.isEmpty()) {
                    int index = listaSugerenciasMed.getSelectedIndex();
                    int size = modeloSugerenciasMed.getSize();
                    if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                        listaSugerenciasMed.setSelectedIndex((index + 1) % size);
                        e.consume();
                    } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                        listaSugerenciasMed.setSelectedIndex((index - 1 + size) % size);
                        e.consume();
                    } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        txtMedicoSolicitante.setText(listaSugerenciasMed.getSelectedValue());
                        ventanaSugerenciasMed.setVisible(false);
                        e.consume();
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_ENTER) {
                    return;
                }

                String texto = txtMedicoSolicitante.getText().trim();
                if (texto.length() >= 1 && presenter != null) {
                    presenter.onBuscarSugerenciasMedicos();
                } else if (ventanaSugerenciasMed != null) {
                    ventanaSugerenciasMed.setVisible(false);
                }
            }
        });

        listaSugerenciasMed.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String sel = listaSugerenciasMed.getSelectedValue();
                if (sel != null) {
                    txtMedicoSolicitante.setText(sel);
                    ventanaSugerenciasMed.setVisible(false);
                }
            }
        });
    }

    @Override
    public void mostrarSugerenciasMedicos(List<String> sugerencias) {
        modeloSugerenciasMed.clear();
        sugerencias.forEach(modeloSugerenciasMed::addElement);
        if (sugerencias.isEmpty()) {
            ventanaSugerenciasMed.setVisible(false);
            return;
        }
        try {
            java.awt.Point p = txtMedicoSolicitante.getLocationOnScreen();
            ventanaSugerenciasMed.setBounds(p.x, p.y + txtMedicoSolicitante.getHeight(),
                    txtMedicoSolicitante.getWidth(), Math.min(180, sugerencias.size() * 28 + 5));
            ventanaSugerenciasMed.setVisible(true);
            listaSugerenciasMed.setSelectedIndex(0);
        } catch (Exception ex) {}
    }

    @Override
    public void bloquearEdicionTabla() {
        grillaDetallesAnalisis.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "CÓDIGO", "DETERMINACIÓN", "RESULTADO", "UNIDAD", "REFERENCIA"}
        ) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        });
        aplicarRenderersConTitulos();
        aplicarColumnas();
    }
    
    @Override
    public void cargarResultadosDetalle(ArrayList<ResultadoAnalisis> lista) {
        cargandoDatos = true;
        
        DefaultTableModel modelo = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "CÓDIGO", "DETERMINACIÓN", "RESULTADO", "UNIDAD", "REFERENCIA"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                if (column != 3) return false;

                Object id = getValueAt(row, 0);
                if (id == null) return false;
                try {
                    if (Integer.parseInt(id.toString()) == -1) return false;
                } catch (NumberFormatException e) {
                    return false;
                }

                Object nombre = getValueAt(row, 2);
                if (nombre != null) {
                    String nombreFila = nombre.toString().trim();
                    if (nombreFila.startsWith("---") && nombreFila.endsWith("---")) {
                        return false;
                    }
                }
                return true;
            }
        };

        grillaDetallesAnalisis.setModel(modelo);

        for (ResultadoAnalisis r : lista) {
            modelo.addRow(new Object[]{
                r.getIdResultado(),
                r.getCodigo(),
                r.getNombrePrueba(),
                r.getResultado(),
                r.getUnidad(),
                r.getReferencia()
            });
        }
        
        modelo.addTableModelListener(e -> {
            if (!calculando && e.getColumn() == 3 && e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                SwingUtilities.invokeLater(() -> calcularIndicesHematimetricos());
            }
        });

        aplicarRenderersConTitulos();
        aplicarColumnas();
        aplicarCellEditor();
        ajustarAlturaFilas(); // ← Aplicamos la altura calculada al finalizar la carga
        
        cargandoDatos = false;
    }

    private void calcularIndicesHematimetricos() {
        calculando = true;
        try {
            int filas = grillaDetallesAnalisis.getRowCount();
            double rbc = -1, hb = -1, hto = -1;
            int idxVCM = -1, idxHCM = -1, idxCHCM = -1;

            for (int i = 0; i < filas; i++) {
                Object objNombre = grillaDetallesAnalisis.getValueAt(i, 2);
                String nombreOriginal = objNombre != null ? objNombre.toString() : "";
                String res = getResultadoEditado(i);
                
                if (nombreOriginal.isEmpty()) continue;
                
                String nombreStr = nombreOriginal.trim().toLowerCase();

                if (nombreStr.equals("glob. rojos")) {
                    rbc = parsearNumeroLab(res);
                } else if (nombreStr.equals("hemoglobina")) {
                    hb = parsearNumeroLab(res);
                } else if (nombreStr.equals("hematocrito")) {
                    hto = parsearNumeroLab(res);
                } else if (nombreStr.equals("vcm")) {
                    idxVCM = i;
                } else if (nombreStr.equals("hcm")) {
                    idxHCM = i;
                } else if (nombreStr.equals("chcm")) {
                    idxCHCM = i;
                }
            }

            if (rbc > 0 && rbc > 100) {
                rbc = rbc / 1000000.0;
            }

            java.text.DecimalFormatSymbols dfs = new java.text.DecimalFormatSymbols();
            dfs.setDecimalSeparator(',');
            java.text.DecimalFormat df = new java.text.DecimalFormat("0.0", dfs);

            if (rbc > 0 && hto > 0 && idxVCM != -1) {
                grillaDetallesAnalisis.setValueAt(df.format((hto * 10) / rbc), idxVCM, 3);
            }
            if (rbc > 0 && hb > 0 && idxHCM != -1) {
                grillaDetallesAnalisis.setValueAt(df.format((hb * 10) / rbc), idxHCM, 3);
            }
            if (hto > 0 && hb > 0 && idxCHCM != -1) {
                grillaDetallesAnalisis.setValueAt(df.format((hb * 100) / hto), idxCHCM, 3);
            }

        } catch (Exception e) {
        } finally {
            calculando = false;
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  UI BUILDER
    // ════════════════════════════════════════════════════════════════
    private void initComponents() {
        jPanelHeader = new JPanel();
        lblNombrePaciente = new JLabel();
        lblFechaAnalisis = new JLabel();
        jLabel1 = new JLabel("RESULTADOS PARA:");
        btnCerrar = new JButton();

        jLabel4 = new JLabel("MÉDICO SOLICITANTE");
        txtMedicoSolicitante = new JTextField();

        pnlContenedorBlanco = new JPanel();
        pnlCuerpo = new JPanel();
        pnlTablaWrapper = new JPanel();
        jScrollPane1 = new JScrollPane();
        grillaDetallesAnalisis = new JTable() {
            @Override
            public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {
                super.changeSelection(rowIndex, 3, toggle, extend);
            }
        };

        jPanelFooter = new JPanel();
        btnEditar = new JButton();
        btnEliminarFila = new JButton();
        btnImprimir = new JButton();

        jLabel3 = new JLabel("Fecha para el Informe:");
        jdFechaInforme = new com.toedter.calendar.JDateChooser();

        grillaDetallesAnalisis.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "CÓDIGO", "DETERMINACIÓN", "RESULTADO", "UNIDAD", "REFERENCIA"}
        ) {
            boolean[] canEdit = {false, false, false, true, false, false};
            @Override public boolean isCellEditable(int r, int c) { return canEdit[c]; }
        });
        jScrollPane1.setViewportView(grillaDetallesAnalisis);
    }

    // ── Variables ────────────────────────────────────────────────────
    private JButton btnCerrar, btnEditar, btnEliminarFila, btnImprimir;
    private JTable grillaDetallesAnalisis;
    private JLabel jLabel1, jLabel3, jLabel4, lblTituloTabla, lblNombrePaciente, lblFechaAnalisis;
    private JPanel jPanelHeader, jPanelFooter, pnlContenedorBlanco, pnlCuerpo, pnlTablaWrapper;
    private JScrollPane jScrollPane1;
    private JTextField txtMedicoSolicitante;
    private com.toedter.calendar.JDateChooser jdFechaInforme;

    // ════════════════════════════════════════════════════════════════
    //  IMPLEMENTACIONES DE LA INTERFAZ
    // ════════════════════════════════════════════════════════════════
    
    @Override public void ejecutar() { setVisible(true); }
    @Override public void limpiarFocos() { this.requestFocusInWindow(); }

    @Override
    public int confirmarAccion(String mensaje, String titulo) {
        return JOptionPane.showConfirmDialog(this, mensaje, titulo, JOptionPane.YES_NO_OPTION);
    }

    @Override public void setNombrePaciente(String nombre) { lblNombrePaciente.setText(nombre.toUpperCase()); }
    @Override public void mostrarMensaje(String mensaje) { JOptionPane.showMessageDialog(this, mensaje); }
    @Override public void setFechaAnalisis(String fecha) { lblFechaAnalisis.setText(fecha); }
    @Override public int getCantidadFilas() { return grillaDetallesAnalisis.getRowCount(); }

    @Override
    public int getIdResultado(int fila) {
        Object val = grillaDetallesAnalisis.getModel().getValueAt(fila, 0);
        if (val == null) return -1;
        try {
            return Integer.parseInt(val.toString());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    @Override public void setMedicoSolicitante(String medico) { txtMedicoSolicitante.setText(medico); }
    @Override public String getMedicoSolicitante() { return txtMedicoSolicitante.getText().trim(); }
    @Override public JTable getGrilla() { return grillaDetallesAnalisis; }
    
    @Override
    public String getResultadoEditado(int fila) {
        Object val = grillaDetallesAnalisis.getModel().getValueAt(fila, 3);
        return val != null ? val.toString() : "";
    }

    @Override
    public void detenerEdicionTabla() {
        if (grillaDetallesAnalisis.isEditing()) {
            grillaDetallesAnalisis.getCellEditor().stopCellEditing();
        }
    }

    @Override public void habilitarBotonGuardar(boolean b) { btnEditar.setEnabled(b); }
    @Override public void habilitarBotonEliminar(boolean b) { btnEliminarFila.setEnabled(b); }
    @Override public void habilitarBotonImprimir(boolean b) { btnImprimir.setEnabled(b); }

    @Override
    public void bloquearMedicoSolicitante() {
        txtMedicoSolicitante.setEditable(false);
        txtMedicoSolicitante.setFocusable(false);
        txtMedicoSolicitante.setForeground(new Color(180, 200, 220));
    }

    @Override public void setIdAnalisis(int id) { this.idAnalisisActual = id; }
    @Override public int getIdAnalisis() { return idAnalisisActual; }
    @Override public void setFechaInforme(Date fecha) { jdFechaInforme.setDate(fecha); }

    @Override
    public Date getFechaSeleccionada() {
        return jdFechaInforme.getDate() != null ? jdFechaInforme.getDate() : new Date();
    }
}