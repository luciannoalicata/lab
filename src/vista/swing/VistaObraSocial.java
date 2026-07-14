package vista.swing;

import vista.interfaces.IVistaObraSocial;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import modelo.ObraSocial;
import presentador.ObraSocialPresenter;

public class VistaObraSocial extends JPanel implements IVistaObraSocial {

    private ObraSocialPresenter presenter;
    private ListSelectionListener listenerSeleccionTabla;
    
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
    private final Color C_SELECCION    = new Color(220, 235, 250);
    
    private boolean seleccionandoProgramaticamente = false;
    private boolean cargandoDatos = false;

    public VistaObraSocial() {
        initComponents();
        aplicarEstiloProfesional();
        configurarNavegacionEnter();
        configurarDeseleccionPorClic();
        setMinimumSize(new Dimension(900, 550));
    }

    // ══════════════════════════════════════════════════════════════════
    //  DESELECCIÓN DE FILA AL HACER CLIC FUERA DE LA TABLA
    // ══════════════════════════════════════════════════════════════════
    private void configurarDeseleccionPorClic() {
        JPanel[] paneles = {pnlCuerpo, pnlFormulario, pnlTablaWrapper, pnlFooter, pnlBotonesEdicion, pnlHeader};
        java.awt.event.MouseAdapter deseleccionador = new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                Component origen = e.getComponent();
                if (origen == grillaObrasSociales || origen == jScrollPane1 || 
                    origen == grillaObrasSociales.getTableHeader()) return;
                if (origen instanceof javax.swing.JButton || origen instanceof javax.swing.JTextField ||
                    origen instanceof javax.swing.JComboBox) return;
                if (grillaObrasSociales.getSelectedRow() != -1 && !cargandoDatos) {
                    grillaObrasSociales.clearSelection();
                    limpiarCampos();
                    habilitarBotonEliminar(false);
                    btnCambiarArancel.setEnabled(false);
                }
            }
        };
        for (JPanel p : paneles) if (p != null) p.addMouseListener(deseleccionador);
        this.addMouseListener(deseleccionador);
    }

    // ══════════════════════════════════════════════════════════════════
    //  ESTILO Y UX - Diseño Profesional y Responsive
    // ══════════════════════════════════════════════════════════════════
    private void aplicarEstiloProfesional() {
        setBackground(C_FONDO);
        setLayout(new BorderLayout());

        // ── HEADER ──────────────────────────────────────────────────────
        pnlHeader.setBackground(C_NAVY);
        pnlHeader.setBorder(new EmptyBorder(10, 20, 10, 20));
        pnlHeader.setLayout(new BorderLayout());

        JPanel pnlIzqHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlIzqHeader.setOpaque(false);
        pnlIzqHeader.add(btnVolver);
        lblTituloHeader.setForeground(C_BLANCO);
        lblTituloHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTituloHeader.setBorder(new EmptyBorder(0, 8, 0, 0));
        pnlIzqHeader.add(lblTituloHeader);
        pnlHeader.add(pnlIzqHeader, BorderLayout.WEST);

        JPanel pnlDerHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlDerHeader.setOpaque(false);
        JLabel lblLupa = new JLabel("Buscar:");
        lblLupa.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblLupa.setForeground(C_HEADER_TEXT);
        pnlDerHeader.add(lblLupa);
        txtBuscarObraSocial.setColumns(18);
        pnlDerHeader.add(txtBuscarObraSocial);
        pnlHeader.add(pnlDerHeader, BorderLayout.EAST);

        add(pnlHeader, BorderLayout.NORTH);

        // ── CONTENEDOR PRINCIPAL ──────────────────────────────────────
        pnlContenedorBlanco = new JPanel(new BorderLayout());
        pnlContenedorBlanco.setBackground(C_BLANCO);
        pnlContenedorBlanco.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 1, 1, 1, C_BORDE),
            new EmptyBorder(10, 12, 10, 12)
        ));

        // ── CUERPO ──────────────────────────────────────────────────────
        pnlCuerpo.setBackground(C_BLANCO);
        pnlCuerpo.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.BOTH;
        gc.weighty = 1.0;

        // ── FORMULARIO ──────────────────────────────────────────────────
        pnlFormulario.setBackground(C_BLANCO);
        pnlFormulario.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(8, 10, 8, 10),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDE, 1, true),
                new EmptyBorder(12, 16, 12, 16)
            )
        ));

        Font fontLabel = new Font("Segoe UI", Font.BOLD, 11);
        JLabel[] labels = {lblCodigo, lblNombre, lblArancel};
        for (JLabel lbl : labels) {
            lbl.setFont(fontLabel);
            lbl.setForeground(C_TEXTO_SUAVE);
        }

        estilizarCampo(txtCodigoObraSocial);
        estilizarCampo(txtNombreObraSocial);
        estilizarCampo(txtArancelObraSocial);

        // ── BOTONES ───────────────────────────────────────────────────
        configurarBoton(btnAgregarObraSocial, C_VERDE, "GUARDAR", 130, 36);
        configurarBoton(btnCambiarArancel, C_AZUL_MEDIO, "ARANCEL", 130, 36);
        configurarBoton(btnEliminarObraSocial, C_ROJO, "ELIMINAR", 130, 36);

        btnCambiarArancel.setEnabled(false);
        habilitarBotonEliminar(false);

        // ── LAYOUT DEL FORMULARIO ─────────────────────────────────────
        pnlFormulario.setLayout(new GridBagLayout());
        GridBagConstraints gf = new GridBagConstraints();
        gf.fill = GridBagConstraints.HORIZONTAL;
        gf.weightx = 1.0;
        gf.gridx = 0;
        int r = 0;

        gf.insets = new Insets(0, 0, 5, 0);
        gf.gridy = r++; pnlFormulario.add(lblCodigo, gf);
        gf.insets = new Insets(0, 0, 10, 0);
        gf.gridy = r++; pnlFormulario.add(txtCodigoObraSocial, gf);

        gf.insets = new Insets(0, 0, 5, 0);
        gf.gridy = r++; pnlFormulario.add(lblNombre, gf);
        gf.insets = new Insets(0, 0, 10, 0);
        gf.gridy = r++; pnlFormulario.add(txtNombreObraSocial, gf);

        gf.insets = new Insets(0, 0, 5, 0);
        gf.gridy = r++; pnlFormulario.add(lblArancel, gf);
        gf.insets = new Insets(0, 0, 14, 0);
        gf.gridy = r++; pnlFormulario.add(txtArancelObraSocial, gf);

        // ── BOTONES DE ACCIÓN ────────────────────────────────────────
        pnlBotonesEdicion.setOpaque(false);
        pnlBotonesEdicion.setLayout(new GridLayout(2, 1, 0, 8));
        
        JPanel pnlFilaArriba = new JPanel(new GridLayout(1, 2, 8, 0));
        pnlFilaArriba.setOpaque(false);
        pnlFilaArriba.add(btnEliminarObraSocial);
        pnlFilaArriba.add(btnAgregarObraSocial);
        
        JPanel pnlFilaAbajo = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        pnlFilaAbajo.setOpaque(false);
        pnlFilaAbajo.add(btnCambiarArancel);
        
        pnlBotonesEdicion.add(pnlFilaArriba);
        pnlBotonesEdicion.add(pnlFilaAbajo);
        
        gf.insets = new Insets(0, 0, 0, 0);
        gf.gridy = r++; gf.fill = GridBagConstraints.HORIZONTAL;
        pnlFormulario.add(pnlBotonesEdicion, gf);

        gf.gridy = r++; gf.weighty = 1.0;
        gf.fill = GridBagConstraints.VERTICAL;
        pnlFormulario.add(new JPanel() {{ setOpaque(false); }}, gf);

        // ── TABLA ──────────────────────────────────────────────────────
        pnlTablaWrapper.setBackground(C_BLANCO);
        pnlTablaWrapper.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(8, 0, 8, 8),
            BorderFactory.createLineBorder(C_BORDE, 1, true)
        ));

        lblTituloTabla.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTituloTabla.setForeground(C_TEXTO_FUERTE);
        lblTituloTabla.setBorder(new EmptyBorder(10, 14, 8, 14));

        grillaObrasSociales.setRowHeight(34);
        grillaObrasSociales.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        grillaObrasSociales.setGridColor(new Color(235, 240, 245));
        grillaObrasSociales.setShowHorizontalLines(true);
        grillaObrasSociales.setShowVerticalLines(false);
        grillaObrasSociales.setSelectionBackground(C_SELECCION);
        grillaObrasSociales.setSelectionForeground(C_TEXTO_FUERTE);
        grillaObrasSociales.setIntercellSpacing(new Dimension(0, 0));
        grillaObrasSociales.setBorder(BorderFactory.createEmptyBorder());
        grillaObrasSociales.setFillsViewportHeight(true);

        grillaObrasSociales.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        grillaObrasSociales.getTableHeader().setBackground(C_CABECERA_TBL);
        grillaObrasSociales.getTableHeader().setForeground(C_TEXTO_SUAVE);
        grillaObrasSociales.getTableHeader().setPreferredSize(new Dimension(0, 34));
        grillaObrasSociales.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDE));
        grillaObrasSociales.getTableHeader().setReorderingAllowed(false);

        jScrollPane1.setBorder(BorderFactory.createEmptyBorder());
        jScrollPane1.getViewport().setBackground(C_BLANCO);

        pnlTablaWrapper.setLayout(new BorderLayout());
        pnlTablaWrapper.add(lblTituloTabla, BorderLayout.NORTH);
        pnlTablaWrapper.add(jScrollPane1, BorderLayout.CENTER);

        // ── DISTRIBUCIÓN ──────────────────────────────────────────────
        JScrollPane scrollFormulario = new JScrollPane(pnlFormulario);
        scrollFormulario.setBorder(BorderFactory.createEmptyBorder());
        scrollFormulario.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollFormulario.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollFormulario.getVerticalScrollBar().setUnitIncrement(16);
        scrollFormulario.getViewport().setBackground(C_BLANCO);

        gc.gridx = 0;
        gc.weightx = 0.35;
        gc.insets = new Insets(6, 8, 0, 6);
        pnlCuerpo.add(scrollFormulario, gc);

        gc.gridx = 1;
        gc.weightx = 0.65;
        gc.insets = new Insets(6, 6, 0, 8);
        pnlCuerpo.add(pnlTablaWrapper, gc);

        pnlContenedorBlanco.add(pnlCuerpo, BorderLayout.CENTER);
        add(pnlContenedorBlanco, BorderLayout.CENTER);

        // ── FOOTER ──────────────────────────────────────────────────────
        pnlFooter.setBackground(C_FONDO);
        pnlFooter.setBorder(new EmptyBorder(6, 12, 10, 12));
        pnlFooter.setLayout(new BorderLayout());
        add(pnlFooter, BorderLayout.SOUTH);

        // ── ESTILIZAR CAMPOS ──────────────────────────────────────────
        estilizarCampoBuscador(txtBuscarObraSocial);
        configurarBotonRetroceso(btnVolver);

        this.revalidate();
        this.repaint();
    }

    private void estilizarCampo(JTextField tf) {
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBackground(C_CAMPO);
        tf.setForeground(C_TEXTO_FUERTE);
        tf.setCaretColor(C_AZUL_MEDIO);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDE),
            new EmptyBorder(6, 10, 6, 10)
        ));
        tf.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent evt) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, C_AZUL_MEDIO),
                    new EmptyBorder(6, 10, 6, 10)
                ));
                tf.setBackground(C_BLANCO);
            }
            @Override public void focusLost(java.awt.event.FocusEvent evt) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDE),
                    new EmptyBorder(6, 10, 6, 10)
                ));
                tf.setBackground(C_CAMPO);
            }
        });
    }

    private void estilizarCampoBuscador(JTextField tf) {
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBackground(new Color(25, 45, 75));
        tf.setForeground(C_BLANCO);
        tf.setCaretColor(C_BLANCO);
        tf.setColumns(18);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 80, 120), 1, true),
            new EmptyBorder(6, 12, 6, 12)
        ));
    }

    private void configurarBoton(JButton btn, Color bg, String texto, int w, int h) {
        btn.setText(texto);
        btn.setBackground(bg);
        btn.setForeground(C_BLANCO);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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

    // ══════════════════════════════════════════════════════════════════
    //  LÓGICA
    // ══════════════════════════════════════════════════════════════════
    private void configurarNavegacionEnter() {
        KeyAdapter enterAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    Component src = (Component) e.getSource();
                    if (src == txtCodigoObraSocial)  txtNombreObraSocial.requestFocus();
                    else if (src == txtNombreObraSocial)  txtArancelObraSocial.requestFocus();
                    else if (src == txtArancelObraSocial) btnAgregarObraSocial.doClick();
                }
            }
        };
        txtCodigoObraSocial.addKeyListener(enterAdapter);
        txtNombreObraSocial.addKeyListener(enterAdapter);
        txtArancelObraSocial.addKeyListener(enterAdapter);
    }

    @Override
    public void setPresenter(ObraSocialPresenter presenter) {
        this.presenter = presenter;
        
        limpiarListeners(btnAgregarObraSocial);
        limpiarListeners(btnEliminarObraSocial);
        limpiarListeners(btnCambiarArancel);
        limpiarListeners(btnVolver);
        
        btnAgregarObraSocial.addActionListener(e -> presenter.onAgregarOS());
        btnEliminarObraSocial.addActionListener(e -> presenter.onEliminarOS());
        btnCambiarArancel.addActionListener(e -> presenter.onCambiarArancel());
        btnVolver.addActionListener(e -> presenter.onVolver());
        
        for (java.awt.event.KeyListener kl : txtBuscarObraSocial.getKeyListeners()) {
            txtBuscarObraSocial.removeKeyListener(kl);
        }
        txtBuscarObraSocial.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                if (presenter != null) presenter.onBuscarOS();
            }
        });

        if (listenerSeleccionTabla != null) {
            grillaObrasSociales.getSelectionModel().removeListSelectionListener(listenerSeleccionTabla);
        }

        listenerSeleccionTabla = e -> {
            if (!e.getValueIsAdjusting() && !seleccionandoProgramaticamente && !cargandoDatos) {
                boolean filaSeleccionada = grillaObrasSociales.getSelectedRow() != -1;
                habilitarBotonEliminar(filaSeleccionada);
                btnCambiarArancel.setEnabled(filaSeleccionada);
                if (filaSeleccionada && presenter != null) {
                    presenter.onSeleccionarOS();
                }
            }
        };
        grillaObrasSociales.getSelectionModel().addListSelectionListener(listenerSeleccionTabla);
    }

    private void limpiarListeners(javax.swing.JButton btn) {
        for (java.awt.event.ActionListener al : btn.getActionListeners()) {
            btn.removeActionListener(al);
        }
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
    public String pedirDato(String mensaje, String titulo) {
        return JOptionPane.showInputDialog(this, mensaje, titulo, JOptionPane.QUESTION_MESSAGE);
    }
    
    @Override public String getCodigoObraSocial() { return txtCodigoObraSocial.getText().trim(); }
    @Override public String getNombreObraSocial() { return txtNombreObraSocial.getText().trim(); }

    @Override
    public double getArancel() {
        try {
            return Double.parseDouble(txtArancelObraSocial.getText().replace(",", "."));
        } catch (Exception e) {
            return 0;
        }
    }

    @Override public String getTextoBusqueda() { return txtBuscarObraSocial.getText().trim(); }

    @Override
    public void cargarObrasSocialesEnTabla(ArrayList<ObraSocial> obs) {
        cargandoDatos = true;
        seleccionandoProgramaticamente = true;
        
        DefaultTableModel modelo = (DefaultTableModel) grillaObrasSociales.getModel();
        modelo.setRowCount(0);
        
        for (ObraSocial o : obs) {
            modelo.addRow(new Object[]{o.getCodigo(), o.getNombre(), String.format("$ %.2f", o.getArancel())});
        }
        
        grillaObrasSociales.clearSelection();
        
        DefaultTableCellRenderer render = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(column == 0 || column == 2 ? SwingConstants.CENTER : SwingConstants.LEFT);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                if (!isSelected) {
                    setBackground(row % 2 == 0 ? C_BLANCO : C_FILA_PAR);
                    setForeground(C_TEXTO_FUERTE);
                } else {
                    setBackground(C_SELECCION);
                    setForeground(C_TEXTO_FUERTE);
                }
                return this;
            }
        };
        
        for (int i = 0; i < grillaObrasSociales.getColumnCount(); i++) {
            grillaObrasSociales.getColumnModel().getColumn(i).setCellRenderer(render);
        }
        
        grillaObrasSociales.getColumnModel().getColumn(0).setPreferredWidth(100);
        grillaObrasSociales.getColumnModel().getColumn(0).setMaxWidth(130);
        grillaObrasSociales.getColumnModel().getColumn(1).setPreferredWidth(260);
        grillaObrasSociales.getColumnModel().getColumn(2).setPreferredWidth(130);
        grillaObrasSociales.getColumnModel().getColumn(2).setMaxWidth(160);
        
        habilitarBotonEliminar(false);
        btnCambiarArancel.setEnabled(false);
        
        seleccionandoProgramaticamente = false;
        cargandoDatos = false;
    }

    @Override
    public ObraSocial getObraSocialSeleccionada() {
        int fila = grillaObrasSociales.getSelectedRow();
        if (fila == -1) return null;
        
        ObraSocial o = new ObraSocial();
        o.setCodigo(grillaObrasSociales.getValueAt(fila, 0).toString());
        o.setNombre(grillaObrasSociales.getValueAt(fila, 1).toString());
        return o;
    }

    @Override
    public void limpiarCampos() {
        txtCodigoObraSocial.setText("");
        txtNombreObraSocial.setText("");
        txtArancelObraSocial.setText("");
        txtBuscarObraSocial.setText("");
        grillaObrasSociales.clearSelection();
        habilitarBotonEliminar(false);
        btnCambiarArancel.setEnabled(false);
        txtCodigoObraSocial.requestFocus();
    }

    @Override public void habilitarBotonAgregar(boolean b)  { btnAgregarObraSocial.setEnabled(b); }
    @Override public void habilitarBotonEliminar(boolean b) { btnEliminarObraSocial.setEnabled(b); }
    @Override public void mostrarMensaje(String mensaje)    { JOptionPane.showMessageDialog(this, mensaje); }
    @Override public void ejecutar()                        { setVisible(true); }

    // ══════════════════════════════════════════════════════════════════
    //  UI BUILDER
    // ══════════════════════════════════════════════════════════════════
    private void initComponents() {
        pnlHeader = new JPanel();
        lblTituloHeader = new JLabel("GESTIÓN DE OBRAS SOCIALES");
        txtBuscarObraSocial = new JTextField();
        btnVolver = new JButton();

        pnlContenedorBlanco = new JPanel();
        pnlCuerpo = new JPanel();
        pnlFormulario = new JPanel();
        pnlTablaWrapper = new JPanel();
        lblTituloTabla = new JLabel("Obras Sociales Registradas");
        pnlFooter = new JPanel();

        lblCodigo = new JLabel("CÓDIGO ÚNICO");
        lblNombre = new JLabel("NOMBRE COMPLETO");
        lblArancel = new JLabel("VALOR ARANCEL ($)");

        txtCodigoObraSocial = new JTextField();
        txtNombreObraSocial = new JTextField();
        txtArancelObraSocial = new JTextField();

        grillaObrasSociales = new JTable();
        jScrollPane1 = new JScrollPane();

        pnlBotonesEdicion = new JPanel();
        btnAgregarObraSocial = new JButton();
        btnCambiarArancel = new JButton();
        btnEliminarObraSocial = new JButton();

        grillaObrasSociales.setModel(new DefaultTableModel(
            new Object[][]{},
            new String[]{"CÓDIGO", "NOMBRE DE OBRA SOCIAL", "ARANCEL"}
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
        jScrollPane1.setViewportView(grillaObrasSociales);
    }

    // ── Variables ────────────────────────────────────────────────────
    private JPanel pnlHeader;
    private JLabel lblTituloHeader;
    private JPanel pnlContenedorBlanco;
    private JPanel pnlCuerpo;
    private JPanel pnlFormulario;
    private JPanel pnlTablaWrapper;
    private JPanel pnlFooter;
    private JLabel lblTituloTabla;
    private JLabel lblCodigo;
    private JLabel lblNombre;
    private JLabel lblArancel;
    private JTextField txtCodigoObraSocial;
    private JTextField txtNombreObraSocial;
    private JTextField txtArancelObraSocial;
    private JTextField txtBuscarObraSocial;
    private JTable grillaObrasSociales;
    private JScrollPane jScrollPane1;
    private JPanel pnlBotonesEdicion;
    private JButton btnAgregarObraSocial;
    private JButton btnCambiarArancel;
    private JButton btnEliminarObraSocial;
    private JButton btnVolver;
}