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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Date;
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
import modelo.ResultadoAnalisis;
import presentador.DetalleAnalisisPresenter;

/**
 * Vista de Resultados Detallados - BIOTEC LIS
 *
 * @author luciano
 */
public class VistaVerDetalleAnalisis extends JPanel implements IVistaVerDetalleAnalisis {

    private DetalleAnalisisPresenter presenter;
    private int idAnalisisActual = -1;

    private JWindow ventanaSugerenciasMed;
    private JList<String> listaSugerenciasMed;
    private DefaultListModel<String> modeloSugerenciasMed;

    // ── Paleta BIOTEC Minimalista ────────────────────────────────────
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

    public VistaVerDetalleAnalisis() {
        initComponents();
        aplicarEsteticaPersonalizada();
        configurarBuscadorMedicoDinamico();
        configurarNavegacionEnter();

        java.awt.EventQueue.invokeLater(() -> {
            if (grillaDetallesAnalisis.getRowCount() > 0) {
                grillaDetallesAnalisis.setRowSelectionInterval(0, 0);
                grillaDetallesAnalisis.setColumnSelectionInterval(3, 3);
                grillaDetallesAnalisis.editCellAt(0, 3);
                grillaDetallesAnalisis.requestFocusInWindow();
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

        lblFechaAnalisis.setForeground(new Color(200, 220, 240));
        lblFechaAnalisis.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        configurarBotonRetroceso(btnCerrar); // Flecha de retorno

        jLabel4.setForeground(C_HEADER_TEXT);
        jLabel4.setFont(new Font("Segoe UI", Font.BOLD, 11));

        estilizarCampoBusqueda(txtMedicoSolicitante);

        // ── TABLA ENVOLTORIO ─────────────────────────────────────────
        pnlTablaWrapper.setBackground(C_BLANCO);
        pnlTablaWrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDE, 1, true),
                new EmptyBorder(1, 1, 1, 1)
        ));

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

        grillaDetallesAnalisis.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        grillaDetallesAnalisis.getTableHeader().setBackground(C_CABECERA_TBL);
        grillaDetallesAnalisis.getTableHeader().setForeground(C_TEXTO_SUAVE);
        grillaDetallesAnalisis.getTableHeader().setPreferredSize(new Dimension(0, 42));
        grillaDetallesAnalisis.getTableHeader().setBorder(BorderFactory.createMatteBorder(1, 0, 2, 0, C_BORDE));
        grillaDetallesAnalisis.getTableHeader().setReorderingAllowed(false);

        jScrollPane1.setBorder(BorderFactory.createEmptyBorder());
        jScrollPane1.getViewport().setBackground(C_BLANCO);

        aplicarColumnas();
        aplicarRenderersConTitulos();

        // ── FOOTER ───────────────────────────────────────────────────
        jPanelFooter.setBackground(C_BLANCO);
        jPanelFooter.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 0, 0, 0, C_BORDE),
                new EmptyBorder(15, 25, 15, 25)
        ));

        jLabel3.setFont(new Font("Segoe UI", Font.BOLD, 12));
        jLabel3.setForeground(C_TEXTO_SUAVE);
        jdFechaInforme.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        configurarBoton(btnEliminarFila, C_ROJO, "✕ ELIMINAR FILA", 160, 44);
        configurarBoton(btnImprimir, C_AZUL_MEDIO, "⎙ IMPRIMIR", 140, 44);
        configurarBoton(btnEditar, C_VERDE, "✔ GUARDAR CAMBIOS", 200, 44);
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
        tf.setPreferredSize(new Dimension(360, 42));
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
        if (ico != null) {
            btn.setIcon(ico);
        }

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setForeground(C_BLANCO);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
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
        } catch (Exception e) {
        }
        return null;
    }

    // ── Anchos de columna ──────────────────────────────────────────────
    // ── Anchos de columna ──────────────────────────────────────────────
    private void aplicarColumnas() {
        if (grillaDetallesAnalisis.getColumnCount() < 6) {
            return;
        }

        grillaDetallesAnalisis.getColumnModel().getColumn(0).setMinWidth(0);
        grillaDetallesAnalisis.getColumnModel().getColumn(0).setMaxWidth(0);
        grillaDetallesAnalisis.getColumnModel().getColumn(0).setWidth(0);
        grillaDetallesAnalisis.getColumnModel().getColumn(0).setPreferredWidth(0);

        grillaDetallesAnalisis.getColumnModel().getColumn(1).setPreferredWidth(85);
        grillaDetallesAnalisis.getColumnModel().getColumn(1).setMaxWidth(100);

        // Reducimos Determinación para igualar a la otra vista
        grillaDetallesAnalisis.getColumnModel().getColumn(2).setPreferredWidth(260);

        grillaDetallesAnalisis.getColumnModel().getColumn(3).setPreferredWidth(130);

        // Ensanchamos Unidad
        grillaDetallesAnalisis.getColumnModel().getColumn(4).setPreferredWidth(98);
        grillaDetallesAnalisis.getColumnModel().getColumn(4).setMaxWidth(110);

        grillaDetallesAnalisis.getColumnModel().getColumn(5).setPreferredWidth(330);
    }

    // ── Renderer de la tabla (Títulos sin cortes, HTML y colores) ────────────
    private void aplicarRenderersConTitulos() {
        if (grillaDetallesAnalisis.getColumnCount() < 6) {
            return;
        }

        grillaDetallesAnalisis.getColumnModel().getColumn(0).setMinWidth(0);
        grillaDetallesAnalisis.getColumnModel().getColumn(0).setMaxWidth(0);
        grillaDetallesAnalisis.getColumnModel().getColumn(0).setWidth(0);

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table,
                    Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

                Object id = table.getModel().getValueAt(row, 0);
                boolean esTituloGenerado = false;
                try {
                    esTituloGenerado = (id != null && Integer.parseInt(id.toString()) == -1);
                } catch (Exception e) {
                }

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
                    // ── FILA NORMAL DE COMPONENTE ──
                    setOpaque(true);
                    setFont(col == 3 ? new Font("Segoe UI", Font.BOLD, 14) : new Font("Segoe UI", Font.PLAIN, 13));
                    setForeground(C_TEXTO_FUERTE);

                    if (isSelected) {
                        setBackground(new Color(210, 232, 250));
                    } else {
                        setBackground(row % 2 == 0 ? C_BLANCO : C_FILA_PAR);
                    }

                    setHorizontalAlignment(SwingConstants.CENTER);

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
                    } else {
                        setText(textoOriginal);
                    }

                    setToolTipText(null);
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

    // ════════════════════════════════════════════════════════════════
    //  BUSCADORES
    // ════════════════════════════════════════════════════════════════
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

                // Usamos la variable 'presenter' correcta
                if (texto.length() >= 1 && presenter != null) {

                    // ¡MAGIA MVP! Llamada directa, clara y limpia.
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
        java.awt.Point p = txtMedicoSolicitante.getLocationOnScreen();
        ventanaSugerenciasMed.setBounds(p.x, p.y + txtMedicoSolicitante.getHeight(),
                txtMedicoSolicitante.getWidth(), Math.min(150, sugerencias.size() * 30 + 5));
        ventanaSugerenciasMed.setVisible(true);
        listaSugerenciasMed.setSelectedIndex(0);
    }

    // ════════════════════════════════════════════════════════════════
    //  GETTERS / SETTERS E INTERFAZ
    // ════════════════════════════════════════════════════════════════
    @Override
    public void ejecutar() {
        setVisible(true);
    }

    @Override
    public void setPresenter(DetalleAnalisisPresenter presenter) {
        this.presenter = presenter;
        
        // ¡MAGIA MVP! Los botones llaman a los métodos exactos, sin "switch"
        btnImprimir.addActionListener(e -> presenter.onImprimir());
        btnEditar.addActionListener( e -> presenter.onEditar());
        btnEliminarFila.addActionListener(e -> presenter.onEliminarFila());
        btnCerrar.addActionListener(e -> presenter.onVolver());

        // ── AQUÍ AGREGAS EL LISTENER DE LA TABLA ──
        grillaDetallesAnalisis.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                presenter.onSeleccionarAnalisis();
            }
        });
    }

    @Override
    public void limpiarFocos() {
        // Le quita el foco a cualquier botón y lo devuelve a la ventana principal
        this.requestFocusInWindow();
    }

    // ── IMPLEMENTACIÓN DEL MÉTODO CONFIRMAR ACCIÓN ──
    @Override
    public int confirmarAccion(String mensaje, String titulo) {
        // La vista encapsula el JOptionPane, el presentador ni se entera que existe Swing
        return javax.swing.JOptionPane.showConfirmDialog(
                this, 
                mensaje, 
                titulo, 
                javax.swing.JOptionPane.YES_NO_OPTION
        );
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
    public void setNombrePaciente(String n) {
        lblNombrePaciente.setText(n.toUpperCase());
    }

    @Override
    public void setFechaAnalisis(String f) {
        lblFechaAnalisis.setText(f);
    }

    @Override
    public void setMedicoSolicitante(String m) {
        txtMedicoSolicitante.setText(m);
    }

    @Override
    public String getMedicoSolicitante() {
        return txtMedicoSolicitante.getText().trim();
    }

    @Override
    public int getCantidadFilas() {
        return grillaDetallesAnalisis.getRowCount();
    }

    @Override
    public int getIdResultado(int f) {
        Object val = grillaDetallesAnalisis.getModel().getValueAt(f, 0);
        if (val == null) {
            return -1;
        }
        try {
            return Integer.parseInt(val.toString());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    @Override
    public String getResultadoEditado(int f) {
        Object val = grillaDetallesAnalisis.getModel().getValueAt(f, 3);
        return val != null ? val.toString() : "";
    }

    @Override
    public JTable getGrilla() {
        return grillaDetallesAnalisis;
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
    public void mostrarMensaje(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }

    @Override
    public void setFechaInforme(Date fecha) {
        jdFechaInforme.setDate(fecha);
    }

    @Override
    public Date getFechaSeleccionada() {
        return jdFechaInforme.getDate() != null ? jdFechaInforme.getDate() : new Date();
    }

    @Override
    public void detenerEdicionTabla() {
        if (grillaDetallesAnalisis.isEditing()) {
            grillaDetallesAnalisis.getCellEditor().stopCellEditing();
        }
    }

    @Override
    public void bloquearMedicoSolicitante() {
        txtMedicoSolicitante.setEditable(false);
        txtMedicoSolicitante.setFocusable(false);
        txtMedicoSolicitante.setForeground(new Color(180, 200, 220));
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

                // ── BLINDAJE: Bloqueamos los subtítulos (--- TITULO ---) ──
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
    }

    // ════════════════════════════════════════════════════════════════
    //  NAVEGACIÓN ENTER
    // ════════════════════════════════════════════════════════════════
    private void configurarNavegacionEnter() {
        javax.swing.InputMap im = grillaDetallesAnalisis.getInputMap(javax.swing.JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        javax.swing.ActionMap am = grillaDetallesAnalisis.getActionMap();

        im.put(javax.swing.KeyStroke.getKeyStroke("ENTER"), "enterNavegar");
        am.put("enterNavegar", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                // 1. Detenemos la edición
                if (grillaDetallesAnalisis.isEditing()) {
                    grillaDetallesAnalisis.getCellEditor().stopCellEditing();
                }

                int filaActual = grillaDetallesAnalisis.getSelectedRow();
                
                // ── MAGIA UX: AUTO-FORMATEO DE MILES Y DECIMALES ──
                if (filaActual != -1) {
                    Object val = grillaDetallesAnalisis.getModel().getValueAt(filaActual, 3);
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
                                
                                grillaDetallesAnalisis.getModel().setValueAt(formateado, filaActual, 3);
                            } catch (Exception ex) {
                                // Si algo falla, lo deja como estaba
                            }
                        }
                    }
                }

                int totalFilas = grillaDetallesAnalisis.getRowCount();

                // 2. Buscamos la siguiente fila válida
                for (int sig = filaActual + 1; sig < totalFilas; sig++) {
                    Object id = grillaDetallesAnalisis.getModel().getValueAt(sig, 0);
                    boolean esTitulo = false;
                    try {
                        esTitulo = (id != null && Integer.parseInt(id.toString()) == -1);
                    } catch (NumberFormatException ex) {
                    }
                    
                    // Verificamos que no sea un subtítulo decorativo para no trabar el cursor ahí
                    Object nombreObj = grillaDetallesAnalisis.getModel().getValueAt(sig, 2);
                    boolean esSubtitulo = (nombreObj != null && nombreObj.toString().trim().startsWith("---") && nombreObj.toString().trim().endsWith("---"));

                    if (!esTitulo && !esSubtitulo) {
                        grillaDetallesAnalisis.setRowSelectionInterval(sig, sig);
                        grillaDetallesAnalisis.setColumnSelectionInterval(3, 3);
                        grillaDetallesAnalisis.scrollRectToVisible(grillaDetallesAnalisis.getCellRect(sig, 3, true));
                        grillaDetallesAnalisis.editCellAt(sig, 3);
                        Component ed = grillaDetallesAnalisis.getEditorComponent();
                        if (ed != null) {
                            ed.requestFocusInWindow();
                        }
                        return; // Termina la búsqueda
                    }
                }
                
                // 3. Si no hay más filas, manda el foco al botón de guardar cambios
                btnEditar.requestFocusInWindow();
            }
        });
    }

    // ════════════════════════════════════════════════════════════════
    //  UI BUILDER (Layout Programático)
    // ════════════════════════════════════════════════════════════════
    private void initComponents() {
        jPanelHeader = new JPanel();
        lblNombrePaciente = new JLabel();
        lblFechaAnalisis = new JLabel();
        jLabel1 = new JLabel("RESULTADOS PARA:");
        btnCerrar = new JButton();

        jLabel4 = new JLabel("MÉDICO SOLICITANTE");
        txtMedicoSolicitante = new JTextField();

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
        lblFechaAnalisis.setAlignmentX(Component.LEFT_ALIGNMENT);

        pnlTitulos.add(jLabel1);
        pnlTitulos.add(Box.createVerticalStrut(2));
        pnlTitulos.add(lblNombrePaciente);
        pnlTitulos.add(Box.createVerticalStrut(2));
        pnlTitulos.add(lblFechaAnalisis);

        pnlIzqHeader.add(pnlTitulos);
        jPanelHeader.add(pnlIzqHeader, BorderLayout.WEST);

        // Inputs en el Header (Médico)
        JPanel pnlDerHeader = new JPanel(new GridBagLayout());
        pnlDerHeader.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();

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
        jPanelFooter.setLayout(new BorderLayout());

        JPanel pnlFooterIzq = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        pnlFooterIzq.setOpaque(false);
        pnlFooterIzq.add(jLabel3);
        jdFechaInforme.setPreferredSize(new Dimension(160, 38));
        pnlFooterIzq.add(jdFechaInforme);

        jPanelFooter.add(pnlFooterIzq, BorderLayout.WEST);

        JPanel pnlFooterDer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        pnlFooterDer.setOpaque(false);
        pnlFooterDer.add(btnEliminarFila);
        pnlFooterDer.add(btnImprimir);
        pnlFooterDer.add(btnEditar);

        jPanelFooter.add(pnlFooterDer, BorderLayout.EAST);

        add(jPanelFooter, BorderLayout.SOUTH);
    }

    // ── Variables ────────────────────────────────────────────────────
    private JButton btnCerrar;
    private JButton btnEditar;
    private JButton btnEliminarFila;
    private JButton btnImprimir;
    private JTable grillaDetallesAnalisis;
    private JLabel jLabel1, jLabel3, jLabel4;
    private JPanel jPanelHeader, jPanelFooter, pnlTablaWrapper;
    private JScrollPane jScrollPane1;
    private JLabel lblNombrePaciente;
    private JLabel lblFechaAnalisis;
    private JTextField txtMedicoSolicitante;
    private com.toedter.calendar.JDateChooser jdFechaInforme;
}
