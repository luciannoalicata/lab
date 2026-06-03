package vista;

import presentador.Controlador;
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

/**
 * Vista de Carga de Resultados - BIOTEC LIS
 * @author luciano
 */
public class VistaCargarResultados extends JPanel implements IVistaCargarResultados {

    private Controlador controlador;
    private boolean calculando = false;

    private JWindow ventanaSugerenciasOS;
    private JList<String> listaSugerenciasOS;
    private DefaultListModel<String> modeloSugerenciasOS;

    private JWindow ventanaSugerenciasMed;
    private JList<String> listaSugerenciasMed;
    private DefaultListModel<String> modeloSugerenciasMed;

    // ── Paleta BIOTEC Minimalista ────────────────────────────────────
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

    public VistaCargarResultados() {
        initComponents();
        aplicarEsteticaPersonalizada();
        configurarBuscadoresDinamicos();
        configurarNavegacionEnter();

        // Enfoque inicial en la tabla
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
    //  ESTÉTICA Y UX
    // ════════════════════════════════════════════════════════════════
    private void aplicarEsteticaPersonalizada() {
        setBackground(C_FONDO);

        // ── HEADER ───────────────────────────────────────────────────
        jPanelHeader.setBackground(C_NAVY);
        jPanelHeader.setBorder(new EmptyBorder(15, 30, 15, 30));

        jLabel1.setForeground(C_HEADER_TEXT);
        jLabel1.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        lblNombrePaciente.setForeground(C_BLANCO);
        lblNombrePaciente.setFont(new Font("Segoe UI", Font.BOLD, 22));

        configurarBotonRetroceso(btnCerrar); // El botón CERRAR actúa como volver en esta vista

        jLabel2.setForeground(C_HEADER_TEXT);
        jLabel2.setFont(new Font("Segoe UI", Font.BOLD, 11));

        jLabel3.setForeground(C_HEADER_TEXT);
        jLabel3.setFont(new Font("Segoe UI", Font.BOLD, 11));

        estilizarCampoBusqueda(txtObraSocialBusqueda);
        estilizarCampoBusqueda(txtMedicoSolicitante);

        // ── TABLA ENVOLTORIO ─────────────────────────────────────────
        pnlTablaWrapper.setBackground(C_BLANCO);
        pnlTablaWrapper.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(C_BORDE, 1, true),
            new EmptyBorder(1, 1, 1, 1)
        ));

        // Configuración de la Grilla
        grillaResultados.setRowHeight(36);
        grillaResultados.setFont(new Font("Segoe UI", Font.PLAIN, 14)); 
        grillaResultados.setGridColor(new Color(235, 240, 245));
        grillaResultados.setShowHorizontalLines(true);
        grillaResultados.setShowVerticalLines(false);
        grillaResultados.setSelectionBackground(new Color(210, 232, 250));
        grillaResultados.setSelectionForeground(C_NAVY);
        grillaResultados.setIntercellSpacing(new Dimension(0, 1));
        grillaResultados.setFocusable(true);
        grillaResultados.setBorder(BorderFactory.createEmptyBorder());

        grillaResultados.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        grillaResultados.getTableHeader().setBackground(C_CABECERA_TBL);
        grillaResultados.getTableHeader().setForeground(C_TEXTO_SUAVE);
        grillaResultados.getTableHeader().setPreferredSize(new Dimension(0, 42));
        grillaResultados.getTableHeader().setBorder(BorderFactory.createMatteBorder(1, 0, 2, 0, C_BORDE));
        grillaResultados.getTableHeader().setReorderingAllowed(false);

        jScrollPane1.setBorder(BorderFactory.createEmptyBorder());
        jScrollPane1.getViewport().setBackground(C_BLANCO);

        aplicarColumnas();
        aplicarRenderer();

        // ── FOOTER ───────────────────────────────────────────────────
        jPanelFooter.setBackground(C_BLANCO);
        jPanelFooter.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(1, 0, 0, 0, C_BORDE),
            new EmptyBorder(15, 25, 15, 25)
        ));

        configurarBoton(btnGuardarResultados, C_VERDE, "💾 GUARDAR RESULTADOS", 240, 48);
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
        // Aumentado el ancho de los campos a 360px
        tf.setPreferredSize(new Dimension(360, 42)); 
        tf.setMinimumSize(new Dimension(250, 42));
    }

    private void configurarBoton(JButton btn, Color bg, String texto, int w, int h) {
        btn.setText(texto);
        btn.setBackground(bg);
        btn.setForeground(C_BLANCO);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
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

    // ── Anchos de columna ──────────────────────────────────────────────
    private void aplicarColumnas() {
        if (grillaResultados.getColumnCount() < 6) return;

        grillaResultados.getColumnModel().getColumn(0).setMinWidth(0);
        grillaResultados.getColumnModel().getColumn(0).setMaxWidth(0);
        grillaResultados.getColumnModel().getColumn(0).setWidth(0);
        grillaResultados.getColumnModel().getColumn(0).setPreferredWidth(0);

        grillaResultados.getColumnModel().getColumn(1).setPreferredWidth(80);
        grillaResultados.getColumnModel().getColumn(1).setMaxWidth(90);

        // Reducimos sutilmente Determinación (antes 310)
        grillaResultados.getColumnModel().getColumn(2).setPreferredWidth(260);
        
        grillaResultados.getColumnModel().getColumn(3).setPreferredWidth(120);

        // Ensanchamos la Unidad para que no se corte (antes 48)
        grillaResultados.getColumnModel().getColumn(4).setPreferredWidth(98);
        grillaResultados.getColumnModel().getColumn(4).setMaxWidth(110);

        grillaResultados.getColumnModel().getColumn(5).setPreferredWidth(340);
    } 

    // ── Renderer de la tabla (Títulos sin cortes y colores especiales) ────────────
   // ── Renderer de la tabla (Títulos sin cortes, HTML y colores) ────────────
    // ── Renderer de la tabla (Títulos sin cortes, HTML y colores) ────────────
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
                    
                    // ── 1. ¡CAMBIO AQUÍ! Centramos el texto del título ──
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
                        // ── 2. ¡CAMBIO AQUÍ! Quitamos el margen izquierdo (EmptyBorder en 0) para un centrado perfecto ──
                        setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createMatteBorder(1, 0, 1, 0, accentColor),
                                new EmptyBorder(0, 0, 0, 0)));
                    } else {
                        setText(""); 
                        setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, accentColor));
                    }

                } else {
                    // ── FILA NORMAL DE COMPONENTE ──
                    setOpaque(true);
                    setFont(col == 3 ? new Font("Segoe UI", Font.BOLD, 14) : new Font("Segoe UI", Font.PLAIN, 13));
                    // ── APLICAMOS EL SEMÁFORO DE COLORES ──
                    Object objRes = table.getModel().getValueAt(row, 3);
                    Object objRef = table.getModel().getValueAt(row, 5);
                    String resActual = objRes != null ? objRes.toString() : "";
                    String refActual = objRef != null ? objRef.toString() : "";

                    // Solo pintamos la columna del Resultado y si tiene un valor cargado
                    if (col == 3 && !resActual.isEmpty()) {
                        setForeground(evaluarAlertaResultado(resActual, refActual));
                        // Un toque extra: ponemos en negrita los valores alterados
                        if (getForeground().equals(new Color(220, 53, 69)) || getForeground().equals(new Color(230, 126, 34))) {
                            setFont(new Font("Segoe UI", Font.BOLD, 15));
                        }
                    } else {
                        setForeground(C_TEXTO_FUERTE);
                    }
                    
                    // Respetamos el color de la fila seleccionada
                    if (isSelected) {
                        setBackground(new Color(210, 232, 250)); 
                    } else {
                        setBackground(row % 2 == 0 ? C_BLANCO : C_FILA_PAR);
                    }

                    // 1. Centramos TODAS las columnas visualmente
                    setHorizontalAlignment(SwingConstants.CENTER);
                    
                    // 2. Contorno Negro Fuerte para saber dónde estamos navegando
                    if (hasFocus) {
                        setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(Color.BLACK, 2), 
                            new EmptyBorder(0, 8, 0, 8)
                        ));
                    } else {
                        setBorder(new EmptyBorder(0, 10, 0, 10));
                    }

                    Object val = table.getModel().getValueAt(row, col);
                    String textoOriginal = val != null ? val.toString() : "";
                    
                    // 3. Magia HTML para saltos de línea (sin Tooltips)
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
                    } else {
                        setText(textoOriginal);
                    }
                    
                    // ── ELIMINAMOS EL GLOBO TOOLTIP MOLESTO ──
                    setToolTipText(null);
                }
                
                // ── MAGIA UX: AUTO-AJUSTE DE ALTURA DE FILA ──
                int alturaPreferida = getPreferredSize().height + 10; 
                int alturaActual = table.getRowHeight(row);
                
                if (alturaPreferida > alturaActual) {
                    table.setRowHeight(row, alturaPreferida);
                }

                return this;
            }
        };

        for (int i = 0; i < grillaResultados.getColumnCount(); i++) {
            grillaResultados.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  PRECIO MANUAL
    // ════════════════════════════════════════════════════════════════
    @Override
    public double pedirPrecioManual() {
        JPanel pnl = new JPanel(new BorderLayout(0, 0));
        pnl.setBackground(C_BLANCO);
        pnl.setPreferredSize(new Dimension(380, 180));

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 14));
        header.setBackground(C_NAVY);
        JLabel lblT = new JLabel("PARTICULAR — Ingreso de Precio");
        lblT.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblT.setForeground(C_BLANCO);
        header.add(lblT);
        pnl.add(header, BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 10));
        body.setBackground(C_BLANCO);
        body.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel lblSub = new JLabel("Ingrese el precio total del estudio ($):");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(C_TEXTO_SUAVE);

        JTextField txtPrecio = new JTextField();
        txtPrecio.setFont(new Font("Segoe UI", Font.BOLD, 18));
        txtPrecio.setForeground(C_NAVY);
        txtPrecio.setCaretColor(C_NAVY);
        txtPrecio.setHorizontalAlignment(JTextField.CENTER);
        txtPrecio.setBackground(C_CAMPO);
        txtPrecio.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(C_BORDE, 1, true),
            new EmptyBorder(6, 10, 6, 10)
        ));
        txtPrecio.setPreferredSize(new Dimension(0, 46));
        txtPrecio.addKeyListener(new KeyAdapter() {
            @Override public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                String actual = txtPrecio.getText();
                if (!Character.isDigit(c) && c != '.' && c != ',' && c != KeyEvent.VK_BACK_SPACE) e.consume();
                if ((c == '.' || c == ',') && (actual.contains(".") || actual.contains(","))) e.consume();
            }
        });

        body.add(lblSub, BorderLayout.NORTH);
        body.add(txtPrecio, BorderLayout.CENTER);
        pnl.add(body, BorderLayout.CENTER);

        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        int resultado = JOptionPane.showConfirmDialog(parentWindow, pnl, "Precio PARTICULAR",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
        if (resultado != JOptionPane.OK_OPTION) return -1;
        String texto = txtPrecio.getText().trim().replace(',', '.');
        if (texto.isEmpty()) return -1;
        try {
            double valor = Double.parseDouble(texto);
            return valor >= 0 ? valor : -1;
        } catch (NumberFormatException ex) {
            mostrarMensaje("El precio ingresado no es válido.");
            return -1;
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  BUSCADORES
    // ════════════════════════════════════════════════════════════════
    private void configurarBuscadoresDinamicos() {
        modeloSugerenciasOS = new DefaultListModel<>();
        listaSugerenciasOS  = new JList<>(modeloSugerenciasOS);
        configurarLista(listaSugerenciasOS, "OS");

        txtObraSocialBusqueda.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                gestionarTeclas(e, ventanaSugerenciasOS, listaSugerenciasOS, modeloSugerenciasOS, txtObraSocialBusqueda);
            }
            @Override public void keyReleased(KeyEvent e) {
                if (esTeclaNav(e)) return;
                String t = txtObraSocialBusqueda.getText().trim();
                if (t.length() >= 1) controlador.buscarSugerenciasOSAnalisis(t);
                else if (ventanaSugerenciasOS != null) ventanaSugerenciasOS.setVisible(false);
            }
        });

        modeloSugerenciasMed = new DefaultListModel<>();
        listaSugerenciasMed  = new JList<>(modeloSugerenciasMed);
        configurarLista(listaSugerenciasMed, "MED");

        txtMedicoSolicitante.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                gestionarTeclas(e, ventanaSugerenciasMed, listaSugerenciasMed, modeloSugerenciasMed, txtMedicoSolicitante);
            }
            @Override public void keyReleased(KeyEvent e) {
                if (esTeclaNav(e)) return;
                String t = txtMedicoSolicitante.getText().trim();
                if (t.length() >= 1) controlador.buscarSugerenciasMedicoSolicitante();
                else if (ventanaSugerenciasMed != null) ventanaSugerenciasMed.setVisible(false);
            }
        });
    }

    private void configurarLista(JList<String> lista, String tipo) {
        lista.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lista.setFixedCellHeight(32);
        lista.setBackground(C_BLANCO);
        lista.setSelectionBackground(new Color(210, 232, 250));
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
            win.setBounds(p.x, p.y + txt.getHeight(), txt.getWidth(), Math.min(200, mod.size()*32+6));
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
    //  CARGAR DETERMINACIONES
    // ════════════════════════════════════════════════════════════════
    @Override
    public void cargarDeterminaciones(List<Determinacion> lista) {
        DefaultTableModel modelo = new DefaultTableModel(
            new Object[][]{},
            new String[]{"N°", "CÓDIGO", "DETERMINACIÓN", "RESULTADO", "UNIDAD", "VALORES REF."}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Solo la columna de Resultado es editable
                if (column != 3) return false;
                
                Object codigo = getValueAt(row, 1);
                // Previene la edición si la fila de código está vacía (títulos virtuales)
                if (codigo == null || codigo.toString().trim().isEmpty()) return false;
                
                // ── BLINDAJE: Previene la edición si es un "Subtítulo" (--- TITULO ---) ──
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

        aplicarRenderer();
        aplicarColumnas();
    }

    @Override
    public void detenerEdicionTabla() {
        if (grillaResultados.isEditing())
            grillaResultados.getCellEditor().stopCellEditing();
    }

    // ════════════════════════════════════════════════════════════════
    //  GETTERS / SETTERS E INTERFAZ
    // ════════════════════════════════════════════════════════════════
    @Override public void setNombrePaciente(String n)   { lblNombrePaciente.setText(n.toUpperCase()); }
    @Override public void setObraSocial(String os)      { txtObraSocialBusqueda.setText(os); }
    @Override public String getObraSocial()             { return txtObraSocialBusqueda.getText().trim(); }
    @Override public String getMedicoSolicitante()      { return txtMedicoSolicitante.getText().trim(); }
    @Override public void setMedicoSolicitante(String m) { txtMedicoSolicitante.setText(m); }
    @Override public int getCantidadFilas()             { return grillaResultados.getRowCount(); }
    @Override public String getCodigo(int f)            { return grillaResultados.getValueAt(f, 1).toString(); }
    @Override public String getNombrePrueba(int f)      { return grillaResultados.getValueAt(f, 2).toString(); }
    @Override public String getResultado(int f)         { Object v = grillaResultados.getValueAt(f, 3); return v != null ? v.toString() : ""; }
    @Override public String getUnidad(int f)            { Object v = grillaResultados.getValueAt(f, 4); return v != null ? v.toString() : ""; }
    @Override public String getReferencia(int f)        { Object v = grillaResultados.getValueAt(f, 5); return v != null ? v.toString() : ""; }
    @Override public boolean getImprimir(int f)         { return true; }
    @Override public double getPrecio()                 { return 0.0; }
    @Override public void mostrarMensaje(String m)      { JOptionPane.showMessageDialog(this, m); }
    @Override public void ejecutar()                    { setVisible(true); }

    @Override
    public void setControlador(Controlador control) {
        this.controlador = control;
        btnGuardarResultados.setActionCommand(BTN_GUARDAR_RESULTADOS);
        btnGuardarResultados.addActionListener(control);
        
        btnCerrar.setActionCommand(BTN_CERRAR); 
        btnCerrar.addActionListener(control);
    }
    
    // Método para evitar "ghosting" de los buscadores flotantes
    public void ocultarSugerenciasFlotantes() {
        if (ventanaSugerenciasOS != null) ventanaSugerenciasOS.setVisible(false);
        if (ventanaSugerenciasMed != null) ventanaSugerenciasMed.setVisible(false);
    }

    // ════════════════════════════════════════════════════════════════
    //  NAVEGACIÓN ENTER
    // ════════════════════════════════════════════════════════════════
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
                
                // 1. Detenemos la edición para capturar lo que el usuario escribió
                if (grillaResultados.isEditing()) {
                    grillaResultados.getCellEditor().stopCellEditing();
                }

                // ── MAGIA UX: AUTO-FORMATEO DE MILES Y DECIMALES ──
                if (row != -1) {
                    Object val = grillaResultados.getModel().getValueAt(row, 3);
                    if (val != null) {
                        String texto = val.toString().trim();
                        
                        // REGEX: Detecta números de 4 o más cifras, y opcionalmente permite decimales (punto o coma)
                        if (texto.matches("^-?\\d{4,}([.,]\\d+)?$")) {
                            try {
                                // 1. Separamos la parte entera de los decimales
                                String[] partes = texto.split("[.,]");
                                String parteEntera = partes[0];
                                String parteDecimal = partes.length > 1 ? partes[1] : "";
                                
                                // 2. Detectamos si usó punto o coma para respetárselo
                                char separadorOriginal = partes.length > 1 ? texto.charAt(parteEntera.length()) : '.';

                                // 3. Le ponemos el punto de miles SOLO a la parte entera
                                java.text.DecimalFormatSymbols dfs = new java.text.DecimalFormatSymbols();
                                dfs.setGroupingSeparator('.');
                                java.text.DecimalFormat df = new java.text.DecimalFormat("#,###", dfs);
                                
                                String enteroFormateado = df.format(Long.parseLong(parteEntera));
                                
                                // 4. Volvemos a armar el número completo
                                String formateado = enteroFormateado;
                                if (partes.length > 1) {
                                    formateado += separadorOriginal + parteDecimal;
                                }
                                
                                grillaResultados.getModel().setValueAt(formateado, row, 3);
                            } catch (Exception ex) {
                                // Si algo falla, lo deja como estaba
                            }
                        }
                    }
                }

                // 2. Buscamos la siguiente fila válida para saltar
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
                        return; // Termina la acción si encontró fila
                    }
                }
                
                // 3. Si no hay más filas abajo, mandamos el foco al botón de guardar
                btnGuardarResultados.requestFocusInWindow();
            }
        });
    }

    // ════════════════════════════════════════════════════════════════
    //  UI BUILDER (Layout Programático)
    // ════════════════════════════════════════════════════════════════
    private void initComponents() {
        jPanelHeader          = new JPanel();
        lblNombrePaciente     = new JLabel();
        jLabel1               = new JLabel("Carga de Resultados para:");
        btnCerrar             = new JButton(); 

        jLabel2               = new JLabel("OBRA SOCIAL");
        txtObraSocialBusqueda = new JTextField();
        jLabel3               = new JLabel("MÉDICO SOLICITANTE");
        txtMedicoSolicitante  = new JTextField();

        pnlTablaWrapper       = new JPanel();
        jScrollPane1          = new JScrollPane();
        grillaResultados      = new JTable();

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

        // ── ROOT ─────────────────────────────────────────────────────
        setLayout(new BorderLayout(0, 0));

        // ── HEADER ───────────────────────────────────────────────────
        jPanelHeader.setLayout(new BorderLayout());

        JPanel pnlIzqHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlIzqHeader.setOpaque(false);
        pnlIzqHeader.add(btnCerrar); 

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

        // Inputs en el Header (Obra Social y Médico)
        JPanel pnlDerHeader = new JPanel(new GridBagLayout());
        pnlDerHeader.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        
        g.gridx = 0; g.gridy = 0; g.anchor = GridBagConstraints.WEST; g.insets = new Insets(0, 0, 4, 15);
        pnlDerHeader.add(jLabel2, g);
        g.gridx = 1; g.insets = new Insets(0, 0, 4, 0);
        pnlDerHeader.add(jLabel3, g);
        
        g.gridx = 0; g.gridy = 1; g.insets = new Insets(0, 0, 0, 15);
        pnlDerHeader.add(txtObraSocialBusqueda, g);
        g.gridx = 1; g.insets = new Insets(0, 0, 0, 0);
        pnlDerHeader.add(txtMedicoSolicitante, g);

        jPanelHeader.add(pnlDerHeader, BorderLayout.EAST);
        add(jPanelHeader, BorderLayout.NORTH);

        // ── CUERPO (Tabla) ───────────────────────────────────────────
        pnlTablaWrapper.setLayout(new BorderLayout());
        pnlTablaWrapper.add(jScrollPane1, BorderLayout.CENTER);

        JPanel wrapperCuerpo = new JPanel(new BorderLayout());
        wrapperCuerpo.setOpaque(false);
        wrapperCuerpo.setBorder(new EmptyBorder(25, 25, 25, 25));
        wrapperCuerpo.add(pnlTablaWrapper, BorderLayout.CENTER);

        add(wrapperCuerpo, BorderLayout.CENTER);

        // ── FOOTER ───────────────────────────────────────────────────
        jPanelFooter.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        jPanelFooter.add(btnGuardarResultados);

        add(jPanelFooter, BorderLayout.SOUTH);
    }
    
    
    
    
    // ── MOTOR DE EVALUACIÓN CLÍNICA (PARSER) ────────────
    private Color evaluarAlertaResultado(String resStr, String refStr) {
        if (resStr == null || resStr.isEmpty() || refStr == null || refStr.isEmpty()) return C_TEXTO_FUERTE;

        // 1. SEGURO MÉDICO: Si la referencia requiere contexto del paciente, abortamos coloración.
        String refBaja = refStr.toLowerCase();
        if (refBaja.contains("\n") || refBaja.contains("hombre") || refBaja.contains("mujer") ||
            refBaja.contains("niño") || refBaja.contains("fase") || refBaja.contains("varon")) {
            return C_TEXTO_FUERTE; // Queda en negro estándar
        }

        try {
            double valorIngresado = parsearNumeroLab(resStr);
            if (Double.isNaN(valorIngresado)) return C_TEXTO_FUERTE;

            // 2. Lógica para "Hasta X", "Menor a X", "< X"
            if (refBaja.contains("hasta") || refBaja.contains("<")) {
                int pos = refBaja.contains("hasta") ? refBaja.indexOf("hasta") + 5 : refBaja.indexOf("<") + 1;
                double max = parsearNumeroLab(refBaja.substring(pos));
                
                if (Double.isNaN(max)) return C_TEXTO_FUERTE;
                return (valorIngresado <= max) ? new Color(35, 160, 115) : new Color(220, 53, 69); // Verde o Rojo
            }

            // 3. Lógica para Rangos "X - Y" o "X a Y"
            String separador = refBaja.contains(" a ") ? " a " : (refBaja.contains("-") ? "-" : null);
            if (separador != null) {
                String[] partes = refBaja.split(separador);
                if (partes.length == 2) {
                    double min = parsearNumeroLab(partes[0]);
                    double max = parsearNumeroLab(partes[1]);
                    
                    if (!Double.isNaN(min) && !Double.isNaN(max)) {
                        if (min > max) { double temp = min; min = max; max = temp; } // Ordenamos por las dudas
                        
                        if (valorIngresado >= min && valorIngresado <= max) {
                            return new Color(35, 160, 115); // Verde (Normal)
                        } else {
                            // ¿Está levemente fuera de rango? (15% de tolerancia)
                            double tolerancia = (max - min) * 0.15;
                            if (valorIngresado >= (min - tolerancia) && valorIngresado <= (max + tolerancia)) {
                                return new Color(230, 126, 34); // Naranja (Alerta Leve)
                            } else {
                                return new Color(220, 53, 69); // Rojo (Crítico)
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Falla silenciosa: si escribe "Positivo", simplemente lo ignora y no pita.
        }

        return C_TEXTO_FUERTE;
    }

    // ── HEURÍSTICA DE NÚMEROS ARGENTINOS ────────────
    private double parsearNumeroLab(String numStr) {
        String s = numStr.replaceAll("[^0-9.,]", ""); // Quita letras y espacios
        if (s.isEmpty()) return Double.NaN;

        // Si usó coma (ej: 14,5), es el decimal estándar
        if (s.contains(",")) {
            return Double.parseDouble(s.replace(".", "").replace(",", "."));
        }

        // El problema del Punto: ¿Es 4.900 (miles) o 7.36 (decimal)?
        if (s.contains(".")) {
            long cantPuntos = s.chars().filter(ch -> ch == '.').count();
            if (cantPuntos > 1) {
                return Double.parseDouble(s.replace(".", "")); // Son millones (3.950.000)
            } else {
                String[] partes = s.split("\\.");
                if (partes.length == 2 && partes[1].length() == 3) {
                    return Double.parseDouble(s.replace(".", "")); // Si hay exactos 3 dígitos post-punto, son miles (4.900)
                }
                // Si no, es un decimal clínico puro (7.36)
            }
        }
        return Double.parseDouble(s);
    }

    // ── Variables ────────────────────────────────────────────────────
    private JButton      btnCerrar;
    private JButton      btnGuardarResultados;
    private JTable       grillaResultados;
    private JLabel       jLabel1, jLabel2, jLabel3;
    private JPanel       jPanelHeader, jPanelFooter, pnlTablaWrapper;
    private JScrollPane  jScrollPane1;
    private JLabel       lblNombrePaciente;
    private JTextField   txtMedicoSolicitante;
    private JTextField   txtObraSocialBusqueda;
}