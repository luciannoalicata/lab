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
import javax.swing.border.LineBorder;
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

    private JWindow ventanaSugerenciasMed;
    private JList<String> listaSugerenciasMed;
    private DefaultListModel<String> modeloSugerenciasMed;

    // Paleta de colores
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
    private final Color C_FOCUS_BORDER = new Color(0, 0, 0);

    public VistaVerDetalleAnalisis() {
        initComponents();
        aplicarEsteticaPersonalizada();
        configurarBuscadorMedicoDinamico();
        configurarNavegacionEnter();
        configurarFocoCelda();
    }

    @Override
    public void setPresenter(DetalleAnalisisPresenter presenter) {
        this.presenter = presenter;
        
        // Limpiar listeners existentes de botones
        for (java.awt.event.ActionListener al : btnImprimir.getActionListeners()) {
            btnImprimir.removeActionListener(al);
        }
        for (java.awt.event.ActionListener al : btnEditar.getActionListeners()) {
            btnEditar.removeActionListener(al);
        }
        for (java.awt.event.ActionListener al : btnEliminarFila.getActionListeners()) {
            btnEliminarFila.removeActionListener(al);
        }
        for (java.awt.event.ActionListener al : btnCerrar.getActionListeners()) {
            btnCerrar.removeActionListener(al);
        }
        
        // Agregar listeners nuevos
        btnImprimir.addActionListener(e -> presenter.onImprimir());
        btnEditar.addActionListener(e -> presenter.onEditar());
        btnEliminarFila.addActionListener(e -> presenter.onEliminarFila());
        btnCerrar.addActionListener(e -> presenter.onVolver());
        
        // Limpiar listener de selección de tabla de forma segura
        if (listenerSeleccionTabla != null) {
            grillaDetallesAnalisis.getSelectionModel().removeListSelectionListener(listenerSeleccionTabla);
        }
        
        // Crear y registrar nuevo listener
        listenerSeleccionTabla = e -> {
            if (!e.getValueIsAdjusting()) {
                presenter.onSeleccionarAnalisis();
            }
        };
        grillaDetallesAnalisis.getSelectionModel().addListSelectionListener(listenerSeleccionTabla);
    }

    private void configurarFocoCelda() {
        // Forzar que la selección se mantenga en la columna 3
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

    // ── NAVEGACIÓN INTELIGENTE (Solo saltos) ──
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

    // ── EL CEREBRO DEL FORMATEO AUTOMÁTICO ──
    private void aplicarCellEditor() {
        JTextField editorField = new JTextField();
        editorField.setFont(new Font("Segoe UI", Font.BOLD, 14));
        editorField.setBorder(new EmptyBorder(0, 8, 0, 8));
        editorField.setHorizontalAlignment(JTextField.CENTER);

        DefaultCellEditor cellEditor = new DefaultCellEditor(editorField) {
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                String texto = value != null ? value.toString() : "";
                
                // Si es número formateado, le quitamos los puntos de miles para poder editarlo cómodo
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
                        // Estandarizamos los decimales siempre con coma (,)
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

    private void aplicarEsteticaPersonalizada() {
        setBackground(C_FONDO);

        jPanelHeader.setBackground(C_NAVY);
        jPanelHeader.setBorder(new EmptyBorder(14, 28, 14, 28));
        jPanelHeader.removeAll();
        jPanelHeader.setLayout(new BorderLayout());

        JPanel pnlIzqHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlIzqHeader.setOpaque(false);
        pnlIzqHeader.add(btnCerrar);

        jLabel1.setForeground(C_HEADER_TEXT);
        jLabel1.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        lblNombrePaciente.setForeground(C_BLANCO);
        lblNombrePaciente.setFont(new Font("Segoe UI", Font.BOLD, 22));

        lblFechaAnalisis.setForeground(new Color(200, 220, 240));
        lblFechaAnalisis.setFont(new Font("Segoe UI", Font.PLAIN, 13));

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

        g.gridx = 0;
        g.gridy = 0;
        g.anchor = GridBagConstraints.WEST;
        g.insets = new Insets(0, 0, 4, 0);
        pnlDerHeader.add(jLabel4, g);

        g.gridx = 0;
        g.gridy = 1;
        g.insets = new Insets(0, 0, 0, 0);
        pnlDerHeader.add(txtMedicoSolicitante, g);

        jPanelHeader.add(pnlDerHeader, BorderLayout.EAST);

        estilizarCampoBusqueda(txtMedicoSolicitante);

        pnlContenedorBlanco.setBackground(C_BLANCO);
        pnlContenedorBlanco.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 1, 1, 1, C_BORDE),
            new EmptyBorder(24, 28, 24, 28)
        ));
        pnlContenedorBlanco.removeAll();
        pnlContenedorBlanco.setLayout(new BorderLayout());

        pnlCuerpo.setBackground(C_BLANCO);
        pnlCuerpo.removeAll();
        pnlCuerpo.setLayout(new BorderLayout());

        pnlTablaWrapper.setBackground(C_BLANCO);
        pnlTablaWrapper.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(0, 0, 0, 0),
            BorderFactory.createLineBorder(C_BORDE, 1, true)
        ));

        lblTituloTabla = new JLabel("RESULTADOS DEL ANÁLISIS");
        lblTituloTabla.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTituloTabla.setForeground(C_TEXTO_FUERTE);
        lblTituloTabla.setBorder(new EmptyBorder(14, 16, 12, 16));

        grillaDetallesAnalisis.setRowHeight(36);
        grillaDetallesAnalisis.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        grillaDetallesAnalisis.setGridColor(new Color(235, 240, 245));
        grillaDetallesAnalisis.setShowHorizontalLines(true);
        grillaDetallesAnalisis.setShowVerticalLines(false);
        grillaDetallesAnalisis.setSelectionBackground(new Color(210, 232, 250));
        grillaDetallesAnalisis.setSelectionForeground(C_NAVY);
        grillaDetallesAnalisis.setIntercellSpacing(new Dimension(0, 1));
        grillaDetallesAnalisis.setFocusable(true);
        grillaDetallesAnalisis.setBorder(BorderFactory.createEmptyBorder());
        grillaDetallesAnalisis.setFillsViewportHeight(true);

        grillaDetallesAnalisis.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        grillaDetallesAnalisis.getTableHeader().setBackground(C_CABECERA_TBL);
        grillaDetallesAnalisis.getTableHeader().setForeground(C_TEXTO_SUAVE);
        grillaDetallesAnalisis.getTableHeader().setPreferredSize(new Dimension(0, 40));
        grillaDetallesAnalisis.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDE));
        grillaDetallesAnalisis.getTableHeader().setReorderingAllowed(false);

        jScrollPane1.setBorder(BorderFactory.createEmptyBorder());
        jScrollPane1.getViewport().setBackground(C_BLANCO);

        pnlTablaWrapper.removeAll();
        pnlTablaWrapper.setLayout(new BorderLayout());
        pnlTablaWrapper.add(lblTituloTabla, BorderLayout.NORTH);
        pnlTablaWrapper.add(jScrollPane1, BorderLayout.CENTER);

        pnlCuerpo.add(pnlTablaWrapper, BorderLayout.CENTER);
        pnlContenedorBlanco.add(pnlCuerpo, BorderLayout.CENTER);

        jPanelFooter.setBackground(C_BLANCO);
        jPanelFooter.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(1, 0, 0, 0, C_BORDE),
            new EmptyBorder(16, 28, 16, 28)
        ));
        jPanelFooter.removeAll();
        jPanelFooter.setLayout(new BorderLayout());

        jLabel3.setFont(new Font("Segoe UI", Font.BOLD, 12));
        jLabel3.setForeground(C_TEXTO_SUAVE);
        jdFechaInforme.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        jdFechaInforme.setPreferredSize(new Dimension(160, 36));

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

        JPanel pnlFooterIzq = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        pnlFooterIzq.setOpaque(false);
        pnlFooterIzq.add(jLabel3);
        pnlFooterIzq.add(jdFechaInforme);
        jPanelFooter.add(pnlFooterIzq, BorderLayout.WEST);

        configurarBoton(btnEliminarFila, C_ROJO, "ELIMINAR FILA", 150, 42);
        configurarBoton(btnImprimir, C_AZUL_MEDIO, "IMPRIMIR", 140, 42);
        configurarBoton(btnEditar, C_VERDE, "GUARDAR CAMBIOS", 180, 42);

        JPanel pnlFooterDer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        pnlFooterDer.setOpaque(false);
        pnlFooterDer.add(btnEliminarFila);
        pnlFooterDer.add(btnImprimir);
        pnlFooterDer.add(btnEditar);
        jPanelFooter.add(pnlFooterDer, BorderLayout.EAST);

        this.removeAll();
        this.setLayout(new BorderLayout());
        this.add(jPanelHeader, BorderLayout.NORTH);
        this.add(pnlContenedorBlanco, BorderLayout.CENTER);
        this.add(jPanelFooter, BorderLayout.SOUTH);

        aplicarColumnas();
        aplicarRenderersConTitulos();
        
        this.revalidate();
        this.repaint();
    }

    private void estilizarCampoBusqueda(JTextField tf) {
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setBackground(new Color(25, 45, 75));
        tf.setForeground(C_BLANCO);
        tf.setCaretColor(C_BLANCO);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 80, 120), 1, true),
            new EmptyBorder(8, 14, 8, 14)
        ));
        tf.setPreferredSize(new Dimension(320, 40));
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

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setForeground(C_BLANCO);
            }
            @Override
            public void mouseExited(MouseEvent e) {
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
        } catch (Exception e) {}
        return null;
    }

    private void aplicarColumnas() {
        if (grillaDetallesAnalisis.getColumnCount() < 6) return;

        grillaDetallesAnalisis.getColumnModel().getColumn(0).setMinWidth(0);
        grillaDetallesAnalisis.getColumnModel().getColumn(0).setMaxWidth(0);
        grillaDetallesAnalisis.getColumnModel().getColumn(0).setWidth(0);
        grillaDetallesAnalisis.getColumnModel().getColumn(0).setPreferredWidth(0);

        grillaDetallesAnalisis.getColumnModel().getColumn(1).setPreferredWidth(85);
        grillaDetallesAnalisis.getColumnModel().getColumn(1).setMaxWidth(100);
        grillaDetallesAnalisis.getColumnModel().getColumn(1).setMinWidth(75);

        grillaDetallesAnalisis.getColumnModel().getColumn(2).setPreferredWidth(260);
        grillaDetallesAnalisis.getColumnModel().getColumn(2).setMinWidth(200);

        grillaDetallesAnalisis.getColumnModel().getColumn(3).setPreferredWidth(130);
        grillaDetallesAnalisis.getColumnModel().getColumn(3).setMinWidth(110);

        grillaDetallesAnalisis.getColumnModel().getColumn(4).setPreferredWidth(98);
        grillaDetallesAnalisis.getColumnModel().getColumn(4).setMaxWidth(110);
        grillaDetallesAnalisis.getColumnModel().getColumn(4).setMinWidth(85);

        grillaDetallesAnalisis.getColumnModel().getColumn(5).setPreferredWidth(330);
        grillaDetallesAnalisis.getColumnModel().getColumn(5).setMinWidth(250);
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
                    setFont(col == 3 ? new Font("Segoe UI", Font.BOLD, 14) : new Font("Segoe UI", Font.PLAIN, 13));
                    
                    if (col == 3) {
                        Object objRes = table.getModel().getValueAt(row, 3);
                        Object objRef = table.getModel().getValueAt(row, 5);
                        String resActual = objRes != null ? objRes.toString() : "";
                        String refActual = objRef != null ? objRef.toString() : "";
                        
                        if (!resActual.isEmpty()) {
                            setForeground(evaluarAlertaResultado(resActual, refActual));
                            if (getForeground().equals(C_ROJO) || getForeground().equals(C_NARANJA_ALERTA)) {
                                setFont(new Font("Segoe UI", Font.BOLD, 15));
                            }
                        } else {
                            setForeground(C_TEXTO_FUERTE);
                        }
                    } else {
                        setForeground(C_TEXTO_FUERTE);
                    }

                    if (isSelected) {
                        setBackground(new Color(210, 232, 250));
                    } else {
                        setBackground(row % 2 == 0 ? C_BLANCO : C_FILA_PAR);
                    }

                    setHorizontalAlignment(SwingConstants.CENTER);

                    if (hasFocus && col == 3 && esFilaEditable(row)) {
                        setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(C_FOCUS_BORDER, 2),
                                new EmptyBorder(0, 8, 0, 8)
                        ));
                    } else if (isSelected && col == 3) {
                        setBorder(new EmptyBorder(0, 12, 0, 12));
                    } else {
                        setBorder(new EmptyBorder(0, 12, 0, 12));
                    }

                    Object val = table.getModel().getValueAt(row, col);
                    String textoOriginal = val != null ? val.toString() : "";

                    // ── ESTA ES LA SECCIÓN DEL TOOLTIP Y SALTOS DE LÍNEA PARA LOS VALORES DE REFERENCIA ──
                    if ((col == 5 || col == 2) && textoOriginal.contains(";")) {
                        String[] lineas = textoOriginal.split(";");
                        StringBuilder sb = new StringBuilder("<html>");

                        for (int i = 0; i < lineas.length; i++) {
                            String lineaBlindada = lineas[i].trim()
                                    .replace("<", "&lt;")
                                    .replace(">", "&gt;")
                                    .replace(" ", "&nbsp;");

                            sb.append(lineaBlindada);
                            if (i < lineas.length - 1) {
                                sb.append("<br>");
                            }
                        }
                        sb.append("</html>");
                        setText(sb.toString());
                        setToolTipText("<html><div style='padding:5px;'>" + textoOriginal.replace(";", "<br>") + "</div></html>");
                    } else {
                        setText(textoOriginal);
                        setToolTipText(null);
                    }
                }

                int alturaPreferida = getPreferredSize().height + 10;
                int alturaActual = table.getRowHeight(row);

                if (alturaPreferida > alturaActual) {
                    table.setRowHeight(row, alturaPreferida);
                }

                return this;
            }
        };

        for (int i = 0; i < grillaDetallesAnalisis.getColumnCount(); i++) {
            grillaDetallesAnalisis.getColumnModel().getColumn(i).setCellRenderer(renderer);
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
        listaSugerenciasMed.setFixedCellHeight(30);
        listaSugerenciasMed.setBackground(C_BLANCO);
        listaSugerenciasMed.setSelectionBackground(new Color(210, 232, 250));
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

        listaSugerenciasMed.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
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
                    txtMedicoSolicitante.getWidth(), Math.min(200, sugerencias.size() * 32 + 5));
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
        DefaultTableModel modelo = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "CÓDIGO", "DETERMINACIÓN", "RESULTADO", "UNIDAD", "REFERENCIA"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                if (column != 3) {
                    return false;
                }

                Object id = getValueAt(row, 0);
                if (id == null) {
                    return false;
                }
                try {
                    if (Integer.parseInt(id.toString()) == -1) {
                        return false;
                    }
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

        aplicarRenderersConTitulos();
        aplicarColumnas();
        aplicarCellEditor(); // Inyectamos el CellEditor Inteligente
    }

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
        grillaDetallesAnalisis = new JTable();

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

            @Override
            public boolean isCellEditable(int r, int c) {
                return canEdit[c];
            }
        });
        jScrollPane1.setViewportView(grillaDetallesAnalisis);
    }

    private JButton btnCerrar, btnEditar, btnEliminarFila, btnImprimir;
    private JTable grillaDetallesAnalisis;
    private JLabel jLabel1, jLabel3, jLabel4, lblTituloTabla, lblNombrePaciente, lblFechaAnalisis;
    private JPanel jPanelHeader, jPanelFooter, pnlContenedorBlanco, pnlCuerpo, pnlTablaWrapper;
    private JScrollPane jScrollPane1;
    private JTextField txtMedicoSolicitante;
    private com.toedter.calendar.JDateChooser jdFechaInforme;

    // ════════════════════════════════════════════════════════════════
    //  IMPLEMENTACIONES DE LA INTERFAZ FALTANTES
    // ════════════════════════════════════════════════════════════════
    
    @Override
    public void ejecutar() {
        setVisible(true);
    }

    @Override
    public void limpiarFocos() {
        this.requestFocusInWindow();
    }

    @Override
    public int confirmarAccion(String mensaje, String titulo) {
        return JOptionPane.showConfirmDialog(this, mensaje, titulo, JOptionPane.YES_NO_OPTION);
    }

    @Override
    public void setNombrePaciente(String nombre) {
        lblNombrePaciente.setText(nombre.toUpperCase());
    }

    @Override
    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }

    @Override
    public void setFechaAnalisis(String fecha) {
        lblFechaAnalisis.setText(fecha);
    }

    @Override
    public int getCantidadFilas() {
        return grillaDetallesAnalisis.getRowCount();
    }

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

    @Override
    public void setMedicoSolicitante(String medico) {
        txtMedicoSolicitante.setText(medico);
    }

    @Override
    public String getMedicoSolicitante() {
        return txtMedicoSolicitante.getText().trim();
    }

    @Override
    public JTable getGrilla() {
        return grillaDetallesAnalisis;
    }

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

    @Override
    public void habilitarBotonGuardar(boolean b) {
        btnEditar.setEnabled(b);
    }

    @Override
    public void habilitarBotonEliminar(boolean b) {
        btnEliminarFila.setEnabled(b);
    }

    @Override
    public void habilitarBotonImprimir(boolean b) {
        btnImprimir.setEnabled(b);
    }

    @Override
    public void bloquearMedicoSolicitante() {
        txtMedicoSolicitante.setEditable(false);
        txtMedicoSolicitante.setFocusable(false);
        txtMedicoSolicitante.setForeground(new Color(180, 200, 220));
    }

    @Override
    public void setIdAnalisis(int id) {
        this.idAnalisisActual = id;
    }

    @Override
    public int getIdAnalisis() {
        return idAnalisisActual;
    }

    @Override
    public void setFechaInforme(Date fecha) {
        jdFechaInforme.setDate(fecha);
    }

    @Override
    public Date getFechaSeleccionada() {
        return jdFechaInforme.getDate() != null ? jdFechaInforme.getDate() : new Date();
    }
}