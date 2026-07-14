package vista.swing;

import vista.interfaces.IVistaCargarResultados;
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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import modelo.Determinacion;
import presentador.ResultadoPresenter;

public class VistaCargarResultados extends JPanel implements IVistaCargarResultados {

    private ResultadoPresenter presenter;
    private boolean calculando = false;
    private boolean cargandoDatos = false;

    private JWindow ventanaSugerenciasOS;
    private JList<String> listaSugerenciasOS;
    private DefaultListModel<String> modeloSugerenciasOS;

    private JWindow ventanaSugerenciasMed;
    private JList<String> listaSugerenciasMed;
    private DefaultListModel<String> modeloSugerenciasMed;

    // ── Paleta BIOTEC Profesional ────────────────────────────────────
    private final Color C_NAVY         = new Color(10, 25, 47);    
    private final Color C_FONDO        = new Color(238, 242, 246);
    private final Color C_BLANCO       = Color.WHITE;
    private final Color C_TEXTO_FUERTE = new Color(40, 50, 60);
    private final Color C_TEXTO_SUAVE  = new Color(100, 115, 130);
    private final Color C_BORDE        = new Color(215, 225, 235);
    private final Color C_AZUL_MEDIO   = new Color(30, 110, 180);
    private final Color C_VERDE        = new Color(35, 160, 115);
    private final Color C_ROJO         = new Color(220, 53, 69);
    private final Color C_CAMPO        = new Color(250, 252, 254);
    private final Color C_CABECERA_TBL = new Color(245, 248, 252);
    private final Color C_FILA_PAR     = new Color(252, 254, 255);
    private final Color C_HEADER_TEXT  = new Color(175, 205, 235);
    private final Color C_SELECCION    = new Color(210, 232, 250);
    private final Color C_NARANJA      = new Color(230, 126, 34);

    public VistaCargarResultados() {
        initComponents();
        aplicarEsteticaProfesional();
        configurarBuscadoresDinamicos();
        configurarNavegacionEnter();
        configurarDobleClicReferencia(); // ← Nuevo evento para expandir textos
        setMinimumSize(new Dimension(900, 600));

        java.awt.EventQueue.invokeLater(() -> {
            if (grillaResultados.getRowCount() > 0) {
                grillaResultados.setRowSelectionInterval(0, 0);
                grillaResultados.setColumnSelectionInterval(3, 3);
                grillaResultados.editCellAt(0, 3);
                grillaResultados.requestFocusInWindow();
            }
        });
    }

    // ════════════════════════════════════════════════════════════════
    //  ESTÉTICA Y UX - Diseño Profesional
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

        configurarBotonRetroceso(btnCerrar);

        JPanel pnlDerHeader = new JPanel(new GridBagLayout());
        pnlDerHeader.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        
        jLabel2.setForeground(C_HEADER_TEXT);
        jLabel2.setFont(new Font("Segoe UI", Font.BOLD, 11));
        jLabel3.setForeground(C_HEADER_TEXT);
        jLabel3.setFont(new Font("Segoe UI", Font.BOLD, 11));
        
        g.gridx = 0; g.gridy = 0; g.anchor = GridBagConstraints.WEST; g.insets = new Insets(0, 0, 4, 12);
        pnlDerHeader.add(jLabel2, g);
        g.gridx = 1; g.insets = new Insets(0, 0, 4, 0);
        pnlDerHeader.add(jLabel3, g);
        
        g.gridx = 0; g.gridy = 1; g.insets = new Insets(0, 0, 0, 12);
        pnlDerHeader.add(txtObraSocialBusqueda, g);
        g.gridx = 1; g.insets = new Insets(0, 0, 0, 0);
        pnlDerHeader.add(txtMedicoSolicitante, g);

        jPanelHeader.add(pnlDerHeader, BorderLayout.EAST);

        estilizarCampoBusqueda(txtObraSocialBusqueda);
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

        lblTituloTabla = new JLabel("DETERMINACIONES A CARGAR");
        lblTituloTabla.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTituloTabla.setForeground(C_TEXTO_FUERTE);
        lblTituloTabla.setBorder(new EmptyBorder(10, 14, 8, 14));

        grillaResultados.setRowHeight(34); // Altura base
        grillaResultados.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        grillaResultados.setGridColor(new Color(235, 240, 245));
        grillaResultados.setShowHorizontalLines(true);
        grillaResultados.setShowVerticalLines(false);
        grillaResultados.setSelectionBackground(C_SELECCION);
        grillaResultados.setSelectionForeground(C_NAVY);
        grillaResultados.setIntercellSpacing(new Dimension(0, 1));
        grillaResultados.setFocusable(true);
        grillaResultados.setBorder(BorderFactory.createEmptyBorder());
        grillaResultados.setFillsViewportHeight(true);

        grillaResultados.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        grillaResultados.getTableHeader().setBackground(C_CABECERA_TBL);
        grillaResultados.getTableHeader().setForeground(C_TEXTO_SUAVE);
        grillaResultados.getTableHeader().setPreferredSize(new Dimension(0, 34));
        grillaResultados.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDE));
        grillaResultados.getTableHeader().setReorderingAllowed(false);

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

        configurarBoton(btnGuardarResultados, C_VERDE, "GUARDAR RESULTADOS", 180, 38);

        JPanel pnlFooterAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlFooterAcciones.setOpaque(false);
        pnlFooterAcciones.add(btnGuardarResultados);
        jPanelFooter.add(pnlFooterAcciones, BorderLayout.EAST);

        add(jPanelFooter, BorderLayout.SOUTH);

        aplicarColumnas();
        aplicarRenderer();
        aplicarCellEditor(); 
        
        this.revalidate();
        this.repaint();
    }

    private void configurarDobleClicReferencia() {
        grillaResultados.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int col = grillaResultados.columnAtPoint(e.getPoint());
                    int row = grillaResultados.rowAtPoint(e.getPoint());
                    // 5 es la columna de Valores de Referencia
                    if (col == 5 && row >= 0) {
                        Object val = grillaResultados.getModel().getValueAt(row, 5);
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
        
        if (grillaResultados.getColumnCount() > 3) {
            grillaResultados.getColumnModel().getColumn(3).setCellEditor(cellEditor);
        }
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
        tf.setMinimumSize(new Dimension(220, 34));
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
            @Override public void mouseExited(java.awt.event.MouseEvent e) { btn.setForeground(C_HEADER_TEXT); }
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

    private void aplicarColumnas() {
        if (grillaResultados.getColumnCount() < 6) return;

        grillaResultados.getColumnModel().getColumn(0).setMinWidth(0);
        grillaResultados.getColumnModel().getColumn(0).setMaxWidth(0);
        grillaResultados.getColumnModel().getColumn(0).setWidth(0);
        grillaResultados.getColumnModel().getColumn(0).setPreferredWidth(0);

        grillaResultados.getColumnModel().getColumn(1).setPreferredWidth(75);
        grillaResultados.getColumnModel().getColumn(1).setMaxWidth(85);
        grillaResultados.getColumnModel().getColumn(1).setMinWidth(65);

        grillaResultados.getColumnModel().getColumn(2).setPreferredWidth(240);
        grillaResultados.getColumnModel().getColumn(2).setMinWidth(180);
        
        grillaResultados.getColumnModel().getColumn(3).setPreferredWidth(120);
        grillaResultados.getColumnModel().getColumn(3).setMinWidth(100);

        grillaResultados.getColumnModel().getColumn(4).setPreferredWidth(90);
        grillaResultados.getColumnModel().getColumn(4).setMaxWidth(100);
        grillaResultados.getColumnModel().getColumn(4).setMinWidth(80);

        grillaResultados.getColumnModel().getColumn(5).setPreferredWidth(320);
        grillaResultados.getColumnModel().getColumn(5).setMinWidth(230);
    }

    private void aplicarRenderer() {
        if (grillaResultados.getColumnCount() < 6) return;

        grillaResultados.getColumnModel().getColumn(0).setMinWidth(0);
        grillaResultados.getColumnModel().getColumn(0).setMaxWidth(0);
        grillaResultados.getColumnModel().getColumn(0).setWidth(0);

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table,
                    Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

                Object codigo = table.getModel().getValueAt(row, 1);
                boolean esTitulo = (codigo == null || codigo.toString().trim().isEmpty());

                String codigoFila = table.getModel().getValueAt(row, 1) != null ? table.getModel().getValueAt(row, 1).toString().trim() : "";
                String nombreFila = table.getModel().getValueAt(row, 2) != null ? table.getModel().getValueAt(row, 2).toString().replaceAll("<[^>]*>", "").trim() : "";

                boolean esTituloGenerado = codigoFila.isEmpty();
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
                    
                    Object objRes = table.getModel().getValueAt(row, 3);
                    Object objRef = table.getModel().getValueAt(row, 5);
                    String resActual = objRes != null ? objRes.toString() : "";
                    String refActual = objRef != null ? objRef.toString() : "";

                    if (col == 3 && !resActual.isEmpty()) {
                        setForeground(evaluarAlertaResultado(resActual, refActual));
                        if (getForeground().equals(C_ROJO) || getForeground().equals(C_NARANJA)) {
                            setFont(new Font("Segoe UI", Font.BOLD, 14));
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
                    
                    if (hasFocus && col == 3) {
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
                        
                        // Si es la columna de referencias, añadimos un tooltip indicando el doble clic
                        if (col == 5) setToolTipText("<html>" + textoOriginal.replace(";", "<br>") + "<br><br><i>(Doble clic para expandir)</i></html>");
                        else setToolTipText(null);
                        
                    } else {
                        setText(textoOriginal);
                        if (col == 5 && textoOriginal.length() > 30) setToolTipText("<html>" + textoOriginal + "<br><br><i>(Doble clic para expandir)</i></html>");
                        else setToolTipText(null);
                    }
                }
                
                // NOTA: Eliminamos el setRowHeight de aquí porque causaba bucles en Swing. 
                // Ahora se maneja limpiamente en ajustarAlturaFilas()

                return this;
            }
        };

        for (int i = 0; i < grillaResultados.getColumnCount(); i++) {
            grillaResultados.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }

    private void ajustarAlturaFilas() {
        for (int row = 0; row < grillaResultados.getRowCount(); row++) {
            Object nom = grillaResultados.getValueAt(row, 2);
            boolean esTitulo = nom != null && nom.toString().startsWith("---");
            
            if (esTitulo) {
                grillaResultados.setRowHeight(row, 34);
                continue;
            }
            
            Object valRef = grillaResultados.getValueAt(row, 5);
            Object valNom = grillaResultados.getValueAt(row, 2);
            
            int lineasRef = valRef != null && valRef.toString().contains(";") ? valRef.toString().split(";").length : 1;
            int lineasNom = valNom != null && valNom.toString().contains(";") ? valNom.toString().split(";").length : 1;
            
            int maxLineas = Math.max(lineasRef, lineasNom);
            int alturaCalculada = Math.max(34, maxLineas * 18 + 8);
            
            // Capped at 75px max to preserve visual harmony
            grillaResultados.setRowHeight(row, Math.min(alturaCalculada, 75));
        }
    }

    @Override
    public double pedirPrecioManual() {
        return -1;
    }

    private void configurarBuscadoresDinamicos() {
        modeloSugerenciasOS = new DefaultListModel<>();
        listaSugerenciasOS = new JList<>(modeloSugerenciasOS);
        configurarLista(listaSugerenciasOS, "OS");

        txtObraSocialBusqueda.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) { gestionarTeclas(e, ventanaSugerenciasOS, listaSugerenciasOS, modeloSugerenciasOS, txtObraSocialBusqueda); }
            @Override public void keyReleased(KeyEvent e) {
                if (esTeclaNav(e)) return;
                String t = txtObraSocialBusqueda.getText().trim();
                if (t.length() >= 1 && presenter != null) {
                    presenter.onBuscarSugerenciasOS(); 
                } else if (ventanaSugerenciasOS != null) {
                    ventanaSugerenciasOS.setVisible(false);
                }
            }
        });

        modeloSugerenciasMed = new DefaultListModel<>();
        listaSugerenciasMed = new JList<>(modeloSugerenciasMed);
        configurarLista(listaSugerenciasMed, "MED");

        txtMedicoSolicitante.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) { gestionarTeclas(e, ventanaSugerenciasMed, listaSugerenciasMed, modeloSugerenciasMed, txtMedicoSolicitante); }
            @Override public void keyReleased(KeyEvent e) {
                if (esTeclaNav(e)) return;
                String t = txtMedicoSolicitante.getText().trim();
                if (t.length() >= 1 && presenter != null) {
                    presenter.onBuscarSugerenciasMed();
                } else if (ventanaSugerenciasMed != null) {
                    ventanaSugerenciasMed.setVisible(false);
                }
            }
        });
    }

    private void configurarLista(JList<String> lista, String tipo) {
        lista.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lista.setFixedCellHeight(30);
        lista.setBackground(C_BLANCO);
        lista.setSelectionBackground(C_SELECCION);
        lista.setSelectionForeground(C_NAVY);
        lista.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (tipo.equals("OS")) elegir(ventanaSugerenciasOS, listaSugerenciasOS, txtObraSocialBusqueda);
                else elegir(ventanaSugerenciasMed, listaSugerenciasMed, txtMedicoSolicitante);
            }
        });
    }

    private void gestionarTeclas(KeyEvent e, JWindow win, JList<String> lista, DefaultListModel<String> mod, JTextField txt) {
        if (win == null || !win.isVisible() || mod.isEmpty()) return;
        int idx = lista.getSelectedIndex(), sz = mod.getSize();
        switch (e.getKeyCode()) {
            case KeyEvent.VK_DOWN:   lista.setSelectedIndex(Math.min(idx+1,sz-1)); lista.ensureIndexIsVisible(lista.getSelectedIndex()); e.consume(); break;
            case KeyEvent.VK_UP:     lista.setSelectedIndex(Math.max(idx-1,0));    lista.ensureIndexIsVisible(lista.getSelectedIndex()); e.consume(); break;
            case KeyEvent.VK_ENTER:  if (idx != -1) { elegir(win, lista, txt); e.consume(); } break;
            case KeyEvent.VK_ESCAPE: win.setVisible(false); break;
        }
    }

    private void elegir(JWindow win, JList<String> lista, JTextField txt) {
        String sel = lista.getSelectedValue();
        if (sel != null) {
            txt.setText(sel);
            win.setVisible(false);
            if (txt == txtObraSocialBusqueda) txtMedicoSolicitante.requestFocus();
        }
    }

    private boolean esTeclaNav(KeyEvent e) {
        int k = e.getKeyCode();
        return k == KeyEvent.VK_DOWN || k == KeyEvent.VK_UP || k == KeyEvent.VK_ENTER || k == KeyEvent.VK_ESCAPE;
    }

    private JWindow crearPopup(JList<String> lista) {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JWindow win = new JWindow(parentWindow);
        win.setAlwaysOnTop(true);
        win.setFocusableWindowState(false);
        JScrollPane sc = new JScrollPane(lista);
        sc.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(C_AZUL_MEDIO, 1), new EmptyBorder(2, 0, 2, 0)));
        win.getContentPane().add(sc);
        return win;
    }

    private void actualizarPopup(JWindow win, DefaultListModel<String> mod, JList<String> lista, JTextField txt, List<String> sugs) {
        mod.clear();
        sugs.forEach(mod::addElement);
        if (sugs.isEmpty()) { win.setVisible(false); return; }
        try {
            java.awt.Point p = txt.getLocationOnScreen();
            win.setBounds(p.x, p.y + txt.getHeight(), txt.getWidth(), Math.min(180, mod.size()*30+6));
            win.setVisible(true);
            lista.setSelectedIndex(0);
        } catch (Exception ex) {}
    }

    @Override
    public void mostrarSugerenciasOS(List<String> sugerencias) {
        if (ventanaSugerenciasOS == null) ventanaSugerenciasOS = crearPopup(listaSugerenciasOS);
        actualizarPopup(ventanaSugerenciasOS, modeloSugerenciasOS, listaSugerenciasOS, txtObraSocialBusqueda, sugerencias);
    }

    @Override
    public void mostrarSugerenciasMedicos(List<String> sugerencias) {
        if (ventanaSugerenciasMed == null) ventanaSugerenciasMed = crearPopup(listaSugerenciasMed);
        actualizarPopup(ventanaSugerenciasMed, modeloSugerenciasMed, listaSugerenciasMed, txtMedicoSolicitante, sugerencias);
    }

    // ════════════════════════════════════════════════════════════════
    //  CARGAR DETERMINACIONES Y CÁLCULOS AUTOMÁTICOS
    // ════════════════════════════════════════════════════════════════
    @Override
    public void cargarDeterminaciones(List<Determinacion> lista) {
        cargandoDatos = true;
        
        DefaultTableModel modelo = new DefaultTableModel(
            new Object[][]{},
            new String[]{"N°", "CÓDIGO", "DETERMINACIÓN", "RESULTADO", "UNIDAD", "VALORES REF."}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                if (column != 3) return false;
                Object codigo = getValueAt(row, 1);
                if (codigo == null || codigo.toString().trim().isEmpty()) return false;
                Object nombre = getValueAt(row, 2);
                if (nombre != null) {
                    String nombreFila = nombre.toString().trim();
                    if (nombreFila.startsWith("---") && nombreFila.endsWith("---")) return false;
                }
                return true;
            }
        };

        grillaResultados.setModel(modelo);

        int filaReal = 1;
        for (Determinacion d : lista) {
            boolean esTitulo = (d.getCodigo() == null || d.getCodigo().isEmpty());
            String resultadoPorDefecto = "";
            String referenciaAMostrar = d.getReferencia();

            if (!esTitulo) {
                String nombreLimpio = d.getNombre() != null ? d.getNombre().trim() : "";
                String referenciaLimpia = d.getReferencia() != null ? d.getReferencia().trim() : "";
                boolean esMetodo = nombreLimpio.equalsIgnoreCase("Método") || nombreLimpio.equalsIgnoreCase("Metodo");
                if (esMetodo && !referenciaLimpia.isEmpty()) {
                    resultadoPorDefecto = referenciaLimpia;
                    referenciaAMostrar = ""; 
                }
            }

            modelo.addRow(new Object[]{
                esTitulo ? "" : filaReal++,
                d.getCodigo(),
                d.getNombre(),
                resultadoPorDefecto,
                esTitulo ? "" : d.getUnidad(),
                esTitulo ? "" : referenciaAMostrar
            });
        }

        modelo.addTableModelListener(e -> {
            if (!calculando && e.getColumn() == 3 && e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                SwingUtilities.invokeLater(() -> calcularIndicesHematimetricos());
            }
        });

        aplicarRenderer();
        aplicarColumnas();
        aplicarCellEditor();
        ajustarAlturaFilas(); // ← Aplicamos la altura correcta y segura al final
        
        cargandoDatos = false;
    }

    private void calcularIndicesHematimetricos() {
        calculando = true;
        try {
            int filas = grillaResultados.getRowCount();
            double rbc = -1, hb = -1, hto = -1;
            int idxVCM = -1, idxHCM = -1, idxCHCM = -1;

            for (int i = 0; i < filas; i++) {
                String nombreOriginal = getNombrePrueba(i);
                String res = getResultado(i);
                
                if (nombreOriginal == null || nombreOriginal.isEmpty()) continue;
                
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
                grillaResultados.setValueAt(df.format((hto * 10) / rbc), idxVCM, 3);
            }
            if (rbc > 0 && hb > 0 && idxHCM != -1) {
                grillaResultados.setValueAt(df.format((hb * 10) / rbc), idxHCM, 3);
            }
            if (hto > 0 && hb > 0 && idxCHCM != -1) {
                grillaResultados.setValueAt(df.format((hb * 100) / hto), idxCHCM, 3);
            }

        } catch (Exception e) {
        } finally {
            calculando = false;
        }
    }
    
    @Override
    public void detenerEdicionTabla() {
        if (grillaResultados.isEditing())
            grillaResultados.getCellEditor().stopCellEditing();
    }

    @Override public void setNombrePaciente(String n)   { lblNombrePaciente.setText(n.toUpperCase()); }
    @Override public void setObraSocial(String os)      { txtObraSocialBusqueda.setText(os); }
    @Override public String getObraSocial()             { return txtObraSocialBusqueda.getText().trim(); }
    @Override public String getMedicoSolicitante()      { return txtMedicoSolicitante.getText().trim(); }
    @Override public void setMedicoSolicitante(String m) { txtMedicoSolicitante.setText(m); }
    @Override public int getCantidadFilas()             { return grillaResultados.getRowCount(); }
    
    @Override 
    public String getCodigo(int f) { 
        Object v = grillaResultados.getValueAt(f, 1); 
        return v != null ? v.toString() : ""; 
    }
    
    @Override 
    public String getNombrePrueba(int f) { 
        Object v = grillaResultados.getValueAt(f, 2); 
        return v != null ? v.toString() : ""; 
    }
    @Override public String getResultado(int f)         { Object v = grillaResultados.getValueAt(f, 3); return v != null ? v.toString() : ""; }
    @Override public String getUnidad(int f)            { Object v = grillaResultados.getValueAt(f, 4); return v != null ? v.toString() : ""; }
    @Override public String getReferencia(int f)        { Object v = grillaResultados.getValueAt(f, 5); return v != null ? v.toString() : ""; }
    @Override public boolean getImprimir(int f)         { return true; }
    @Override public double getPrecio()                 { return 0.0; }
    @Override public void mostrarMensaje(String m)      { JOptionPane.showMessageDialog(this, m); }
    @Override public void ejecutar()                    { setVisible(true); }

    @Override
    public void setPresenter(ResultadoPresenter presenter) {
        this.presenter = presenter;
        
        for (java.awt.event.ActionListener al : btnGuardarResultados.getActionListeners()) {
            btnGuardarResultados.removeActionListener(al);
        }
        for (java.awt.event.ActionListener al : btnCerrar.getActionListeners()) {
            btnCerrar.removeActionListener(al);
        }
        
        btnGuardarResultados.addActionListener(e -> presenter.onGuardarResultados());
        btnCerrar.addActionListener(e -> presenter.onVolver());
    }
    
    @Override
    public void limpiarFocos() {
        this.requestFocusInWindow();
    }

    @Override
    public int confirmarAccion(String mensaje, String titulo) {
        return JOptionPane.showConfirmDialog(this, mensaje, titulo, JOptionPane.YES_NO_CANCEL_OPTION);
    }
    
    public void ocultarSugerenciasFlotantes() {
        if (ventanaSugerenciasOS != null) ventanaSugerenciasOS.setVisible(false);
        if (ventanaSugerenciasMed != null) ventanaSugerenciasMed.setVisible(false);
    }

    // ════════════════════════════════════════════════════════════════
    //  NAVEGACIÓN ENTER Y AUTO-FORMATEO
    // ════════════════════════════════════════════════════════════════
    private void configurarNavegacionEnter() {
        javax.swing.InputMap im = grillaResultados.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        javax.swing.ActionMap am = grillaResultados.getActionMap();
        im.put(javax.swing.KeyStroke.getKeyStroke("ENTER"), "enterAction");
        
        am.put("enterAction", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                int row = grillaResultados.getSelectedRow();
                
                if (grillaResultados.isEditing()) {
                    grillaResultados.getCellEditor().stopCellEditing();
                }

                int total = grillaResultados.getRowCount();
                for (int sig = row + 1; sig < total; sig++) {
                    Object cod = grillaResultados.getModel().getValueAt(sig, 1);
                    boolean esTitulo = (cod == null || cod.toString().trim().isEmpty());
                    Object nombreObj = grillaResultados.getModel().getValueAt(sig, 2);
                    boolean esSubtitulo = (nombreObj != null && nombreObj.toString().trim().startsWith("---") && nombreObj.toString().trim().endsWith("---"));
                    
                    if (!esTitulo && !esSubtitulo) {
                        grillaResultados.setRowSelectionInterval(sig, sig);
                        grillaResultados.setColumnSelectionInterval(3, 3);
                        grillaResultados.scrollRectToVisible(grillaResultados.getCellRect(sig, 3, true));
                        grillaResultados.editCellAt(sig, 3);
                        Component ed = grillaResultados.getEditorComponent();
                        if (ed != null) ed.requestFocusInWindow();
                        return;
                    }
                }
                
                btnGuardarResultados.requestFocusInWindow();
            }
        });
    }

    private Color evaluarAlertaResultado(String resStr, String refStr) {
        if (resStr == null || resStr.isEmpty() || refStr == null || refStr.isEmpty()) return C_TEXTO_FUERTE;

        String refBaja = refStr.toLowerCase();
        if (refBaja.contains("\n") || refBaja.contains("hombre") || refBaja.contains("mujer") ||
            refBaja.contains("niño") || refBaja.contains("fase") || refBaja.contains("varon")) {
            return C_TEXTO_FUERTE;
        }

        try {
            double valorIngresado = parsearNumeroLab(resStr);
            if (Double.isNaN(valorIngresado)) return C_TEXTO_FUERTE;

            if (refBaja.contains("hasta") || refBaja.contains("<")) {
                int pos = refBaja.contains("hasta") ? refBaja.indexOf("hasta") + 5 : refBaja.indexOf("<") + 1;
                double max = parsearNumeroLab(refBaja.substring(pos));
                if (Double.isNaN(max)) return C_TEXTO_FUERTE;
                return (valorIngresado <= max) ? C_VERDE : C_ROJO;
            }

            String separador = refBaja.contains(" a ") ? " a " : (refBaja.contains("-") ? "-" : null);
            if (separador != null) {
                String[] partes = refBaja.split(separador);
                if (partes.length == 2) {
                    double min = parsearNumeroLab(partes[0]);
                    double max = parsearNumeroLab(partes[1]);
                    if (!Double.isNaN(min) && !Double.isNaN(max)) {
                        if (min > max) { double temp = min; min = max; max = temp; }
                        if (valorIngresado >= min && valorIngresado <= max) {
                            return C_VERDE;
                        } else {
                            double tolerancia = (max - min) * 0.15;
                            if (valorIngresado >= (min - tolerancia) && valorIngresado <= (max + tolerancia)) {
                                return C_NARANJA;
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
        
        if (s.contains(".") && s.contains(",")) {
            return Double.parseDouble(s.replace(".", "").replace(",", "."));
        }
        if (s.contains(",")) {
            return Double.parseDouble(s.replace(",", "."));
        }
        
        long cantPuntos = s.chars().filter(ch -> ch == '.').count();
        if (cantPuntos > 1) {
            return Double.parseDouble(s.replace(".", ""));
        }
        return Double.parseDouble(s);
    }

    private void initComponents() {
        jPanelHeader          = new JPanel();
        lblNombrePaciente     = new JLabel();
        jLabel1               = new JLabel("Carga de Resultados para:");
        btnCerrar             = new JButton(); 

        jLabel2               = new JLabel("OBRA SOCIAL");
        txtObraSocialBusqueda = new JTextField();
        jLabel3               = new JLabel("MÉDICO SOLICITANTE");
        txtMedicoSolicitante  = new JTextField();

        pnlContenedorBlanco   = new JPanel();
        pnlCuerpo             = new JPanel();
        pnlTablaWrapper       = new JPanel();
        jScrollPane1          = new JScrollPane();
        
        grillaResultados      = new JTable() {
            @Override
            public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {
                super.changeSelection(rowIndex, 3, toggle, extend);
            }
        };

        jPanelFooter          = new JPanel();
        btnGuardarResultados  = new JButton();

        grillaResultados.setModel(new DefaultTableModel(
            new Object[][]{},
            new String[]{"N°", "CÓDIGO", "DETERMINACIÓN", "RESULTADO", "UNIDAD", "VALORES REF."}
        ) {
            boolean[] canEdit = {false, false, false, true, false, false};
            @Override public boolean isCellEditable(int r, int c) { return canEdit[c]; }
        });
        jScrollPane1.setViewportView(grillaResultados);
    }

    private JButton      btnCerrar;
    private JButton      btnGuardarResultados;
    private JTable       grillaResultados;
    private JLabel       jLabel1, jLabel2, jLabel3;
    private JPanel       jPanelHeader, jPanelFooter;
    private JPanel       pnlContenedorBlanco, pnlCuerpo, pnlTablaWrapper;
    private JLabel       lblTituloTabla;
    private JScrollPane  jScrollPane1;
    private JLabel       lblNombrePaciente;
    private JTextField   txtMedicoSolicitante;
    private JTextField   txtObraSocialBusqueda;
}