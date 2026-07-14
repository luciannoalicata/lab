package vista.swing;

import vista.interfaces.IVistaMedicos;
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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import modelo.Medico;
import presentador.MedicoPresenter;

public class VistaMedicos extends JPanel implements IVistaMedicos {

    private MedicoPresenter presenter;
    private boolean actualizandoVista = false;
    private boolean cargandoDatos = false;
    
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

    public VistaMedicos() {
        initComponents();
        aplicarEstiloProfesional();
        configurarNavegacionEnter();
        configurarDeseleccionPorClic();
        setMinimumSize(new Dimension(900, 600));
    }

    // ════════════════════════════════════════════════════════════════
    //  DESELECCIÓN DE FILA AL HACER CLIC FUERA DE LA TABLA
    // ════════════════════════════════════════════════════════════════
    private void configurarDeseleccionPorClic() {
        JPanel[] paneles = {pnlCuerpo, pnlFormulario, pnlTablaWrapper, pnlFooter, pnlBotonesEdicion, pnlHeader};
        java.awt.event.MouseAdapter deseleccionador = new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                Component origen = e.getComponent();
                if (origen == grillaMedicos || origen == jScrollPane1 || 
                    origen == grillaMedicos.getTableHeader()) return;
                if (origen instanceof javax.swing.JButton || origen instanceof javax.swing.JTextField ||
                    origen instanceof javax.swing.JComboBox || origen instanceof javax.swing.JTextArea) return;
                if (grillaMedicos.getSelectedRow() != -1 && !cargandoDatos) {
                    grillaMedicos.clearSelection();
                    limpiarCampos();
                    habilitarBotonEliminar(false);
                }
            }
        };
        for (JPanel p : paneles) if (p != null) p.addMouseListener(deseleccionador);
        this.addMouseListener(deseleccionador);
    }

    // ════════════════════════════════════════════════════════════════
    //  ESTILO Y UX - Diseño Profesional y Responsive
    // ════════════════════════════════════════════════════════════════
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
        txtBuscarMedico.setColumns(18);
        pnlDerHeader.add(txtBuscarMedico);
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
        JLabel[] labels = {lblMatricula, lblNombre, lblApellido, lblEspecialidad, lblObservaciones};
        for (JLabel lbl : labels) {
            lbl.setFont(fontLabel);
            lbl.setForeground(C_TEXTO_SUAVE);
        }

        estilizarCampo(txtMatricula);
        estilizarCampo(txtNombreMedico);
        estilizarCampo(txtApellidoMedico);

        cbxEspecialidad.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbxEspecialidad.setBackground(C_CAMPO);
        cbxEspecialidad.setForeground(C_TEXTO_FUERTE);
        cbxEspecialidad.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDE),
            new EmptyBorder(6, 10, 6, 10)
        ));

        txtObservaciones.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtObservaciones.setBackground(C_CAMPO);
        txtObservaciones.setForeground(C_TEXTO_FUERTE);
        txtObservaciones.setCaretColor(C_AZUL_MEDIO);
        txtObservaciones.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(C_BORDE, 1, true),
            new EmptyBorder(6, 10, 6, 10)
        ));
        txtObservaciones.setRows(3);
        jScrollPane2.setBorder(BorderFactory.createEmptyBorder());
        jScrollPane2.setPreferredSize(new Dimension(0, 60));

        // Layout del formulario
        pnlFormulario.setLayout(new GridBagLayout());
        GridBagConstraints gf = new GridBagConstraints();
        gf.fill = GridBagConstraints.HORIZONTAL;
        gf.weightx = 1.0;
        gf.gridx = 0;
        int r = 0;

        gf.insets = new Insets(0, 0, 5, 0);
        gf.gridy = r++; pnlFormulario.add(lblMatricula, gf);
        gf.insets = new Insets(0, 0, 12, 0);
        gf.gridy = r++; pnlFormulario.add(txtMatricula, gf);

        gf.insets = new Insets(0, 0, 5, 0);
        gf.gridy = r++; pnlFormulario.add(lblNombre, gf);
        gf.insets = new Insets(0, 0, 12, 0);
        gf.gridy = r++; pnlFormulario.add(txtNombreMedico, gf);

        gf.insets = new Insets(0, 0, 5, 0);
        gf.gridy = r++; pnlFormulario.add(lblApellido, gf);
        gf.insets = new Insets(0, 0, 12, 0);
        gf.gridy = r++; pnlFormulario.add(txtApellidoMedico, gf);

        gf.insets = new Insets(0, 0, 5, 0);
        gf.gridy = r++; pnlFormulario.add(lblEspecialidad, gf);
        gf.insets = new Insets(0, 0, 12, 0);
        gf.gridy = r++; pnlFormulario.add(cbxEspecialidad, gf);

        gf.insets = new Insets(0, 0, 5, 0);
        gf.gridy = r++; pnlFormulario.add(lblObservaciones, gf);
        gf.insets = new Insets(0, 0, 10, 0);
        gf.gridy = r++; gf.fill = GridBagConstraints.BOTH;
        gf.weighty = 0.6;
        pnlFormulario.add(jScrollPane2, gf);

        // Botones
        pnlBotonesEdicion.setOpaque(false);
        pnlBotonesEdicion.setLayout(new GridLayout(1, 2, 10, 0));
        pnlBotonesEdicion.add(btnEliminarMedico);
        pnlBotonesEdicion.add(btnGuardarMedico);

        gf.gridy = r++; gf.weighty = 0;
        gf.fill = GridBagConstraints.HORIZONTAL;
        gf.insets = new Insets(8, 0, 0, 0);
        pnlFormulario.add(pnlBotonesEdicion, gf);

        gf.gridy = r++; gf.weighty = 1.0;
        gf.fill = GridBagConstraints.VERTICAL;
        gf.insets = new Insets(0, 0, 0, 0);
        pnlFormulario.add(new JPanel() {{ setOpaque(false); }}, gf);

        // ── TABLA ──────────────────────────────────────────────────────
        pnlTablaWrapper.setBackground(C_BLANCO);
        pnlTablaWrapper.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(8, 0, 8, 8),
            BorderFactory.createLineBorder(C_BORDE, 1, true)
        ));

        lblTituloTabla.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTituloTabla.setForeground(C_TEXTO_FUERTE);
        lblTituloTabla.setBorder(new EmptyBorder(10, 14, 10, 14));

        grillaMedicos.setRowHeight(34);
        grillaMedicos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        grillaMedicos.setGridColor(new Color(235, 240, 245));
        grillaMedicos.setShowHorizontalLines(true);
        grillaMedicos.setShowVerticalLines(false);
        grillaMedicos.setSelectionBackground(C_SELECCION);
        grillaMedicos.setSelectionForeground(C_TEXTO_FUERTE);
        grillaMedicos.setIntercellSpacing(new Dimension(0, 0));
        grillaMedicos.setBorder(BorderFactory.createEmptyBorder());
        grillaMedicos.setFillsViewportHeight(true);

        grillaMedicos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        grillaMedicos.getTableHeader().setBackground(C_CABECERA_TBL);
        grillaMedicos.getTableHeader().setForeground(C_TEXTO_SUAVE);
        grillaMedicos.getTableHeader().setPreferredSize(new Dimension(0, 34));
        grillaMedicos.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDE));
        grillaMedicos.getTableHeader().setReorderingAllowed(false);

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
        gc.weightx = 0.38;
        gc.insets = new Insets(6, 8, 0, 6);
        pnlCuerpo.add(scrollFormulario, gc);

        gc.gridx = 1;
        gc.weightx = 0.62;
        gc.insets = new Insets(6, 6, 0, 8);
        pnlCuerpo.add(pnlTablaWrapper, gc);

        pnlContenedorBlanco.add(pnlCuerpo, BorderLayout.CENTER);
        add(pnlContenedorBlanco, BorderLayout.CENTER);

        // ── FOOTER ──────────────────────────────────────────────────────
        pnlFooter.setBackground(C_FONDO);
        pnlFooter.setBorder(new EmptyBorder(6, 12, 10, 12));
        pnlFooter.setLayout(new BorderLayout());
        add(pnlFooter, BorderLayout.SOUTH);

        // ── CONFIGURAR BOTONES ────────────────────────────────────────
        configurarBoton(btnGuardarMedico, C_VERDE, "GUARDAR", 140, 36);
        configurarBoton(btnEliminarMedico, C_ROJO, "ELIMINAR", 140, 36);
        configurarBotonRetroceso(btnVolver);
        estilizarCampoBuscador(txtBuscarMedico);

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

    // ════════════════════════════════════════════════════════════════
    //  LÓGICA
    // ════════════════════════════════════════════════════════════════
    private void configurarNavegacionEnter() {
        KeyAdapter enterKeyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    Component c = (Component) e.getSource();
                    if (c == txtMatricula)       txtNombreMedico.requestFocus();
                    else if (c == txtNombreMedico)    txtApellidoMedico.requestFocus();
                    else if (c == txtApellidoMedico)  cbxEspecialidad.requestFocus();
                    else if (c == cbxEspecialidad)    txtObservaciones.requestFocus();
                }
            }
        };
        txtMatricula.addKeyListener(enterKeyAdapter);
        txtNombreMedico.addKeyListener(enterKeyAdapter);
        txtApellidoMedico.addKeyListener(enterKeyAdapter);
        cbxEspecialidad.addKeyListener(enterKeyAdapter);
    }

    @Override public void ejecutar() { setVisible(true); }

    @Override
    public void setPresenter(MedicoPresenter presenter) {
        this.presenter = presenter;
        
        limpiarListeners(btnGuardarMedico);
        limpiarListeners(btnEliminarMedico);
        limpiarListeners(btnVolver);
        
        btnGuardarMedico.addActionListener(e -> presenter.onGuardarMedico());
        btnEliminarMedico.addActionListener(e -> presenter.onEliminarMedico());
        btnVolver.addActionListener(e -> presenter.onVolver());
        
        for (java.awt.event.KeyListener kl : txtBuscarMedico.getKeyListeners()) {
            txtBuscarMedico.removeKeyListener(kl);
        }
        txtBuscarMedico.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                if (presenter != null) presenter.onBuscarMedico();
            }
        });

        grillaMedicos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && !cargandoDatos && presenter != null) {
                boolean hay = grillaMedicos.getSelectedRow() != -1;
                habilitarBotonEliminar(hay);
                if (hay) presenter.onSeleccionarMedico();
            }
        });
    }

    private void limpiarListeners(JButton btn) {
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

    @Override public String getMatriculaMedico()     { return txtMatricula.getText().trim(); }
    @Override public String getNombreMedico()        { return txtNombreMedico.getText().trim(); }
    @Override public String getApellidoMedico()      { return txtApellidoMedico.getText().trim(); }
    @Override public String getEspecialidad()        { return cbxEspecialidad.getSelectedIndex() >= 0 ? cbxEspecialidad.getSelectedItem().toString() : ""; }
    @Override public String getObservacionesMedico() { return txtObservaciones.getText().trim(); }
    @Override public String getTextoBusqueda()       { return txtBuscarMedico.getText().trim(); }
    
    @Override public void habilitarBotonGuardar(boolean b)  { btnGuardarMedico.setEnabled(b); }
    @Override public void habilitarBotonEliminar(boolean b) { btnEliminarMedico.setEnabled(b); }

    @Override
    public void cargarMedicosEnTabla(ArrayList<Medico> medicos) {
        cargandoDatos = true;
        
        DefaultTableModel modelo = new DefaultTableModel(
            new Object[][]{},
            new String[]{"MATRÍCULA", "APELLIDO", "NOMBRE", "ESPECIALIDAD"}
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };

        for (Medico m : medicos) {
            modelo.addRow(new Object[]{
                m.getMatricula(),
                m.getApellidoMedico().toUpperCase(),
                m.getNombreMedico().toUpperCase(),
                m.getEspecialidad()
            });
        }
        grillaMedicos.setModel(modelo);
        grillaMedicos.clearSelection();

        DefaultTableCellRenderer render = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(column == 0 ? SwingConstants.CENTER : SwingConstants.LEFT);
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
        for (int i = 0; i < grillaMedicos.getColumnCount(); i++) {
            grillaMedicos.getColumnModel().getColumn(i).setCellRenderer(render);
        }

        grillaMedicos.getColumnModel().getColumn(0).setPreferredWidth(90);
        grillaMedicos.getColumnModel().getColumn(0).setMaxWidth(120);
        grillaMedicos.getColumnModel().getColumn(1).setPreferredWidth(160);
        grillaMedicos.getColumnModel().getColumn(2).setPreferredWidth(160);
        grillaMedicos.getColumnModel().getColumn(3).setPreferredWidth(180);

        habilitarBotonEliminar(false);
        cargandoDatos = false;
    }

    @Override
    public Medico getMedicoSeleccionado() {
        int filaView = grillaMedicos.getSelectedRow();
        if (filaView == -1) return null;
        int filaModel = grillaMedicos.convertRowIndexToModel(filaView);
        Medico m = new Medico();
        m.setMatricula(grillaMedicos.getModel().getValueAt(filaModel, 0).toString());
        m.setApellidoMedico(grillaMedicos.getModel().getValueAt(filaModel, 1).toString());
        m.setNombreMedico(grillaMedicos.getModel().getValueAt(filaModel, 2).toString());
        m.setEspecialidad(grillaMedicos.getModel().getValueAt(filaModel, 3).toString());
        return m;
    }

    @Override
    public void cargarDatosMedico(Medico m) {
        if (m == null) return;
        txtMatricula.setText(m.getMatricula());
        txtNombreMedico.setText(m.getNombreMedico());
        txtApellidoMedico.setText(m.getApellidoMedico());
        cbxEspecialidad.setSelectedItem(m.getEspecialidad());
        txtObservaciones.setText(m.getObservaciones());
    }

    @Override
    public void limpiarCampos() {
        txtMatricula.setText("");
        txtNombreMedico.setText("");
        txtApellidoMedico.setText("");
        cbxEspecialidad.setSelectedIndex(0);
        txtObservaciones.setText("");
        txtBuscarMedico.setText("");
        grillaMedicos.clearSelection();
        habilitarBotonEliminar(false);
        txtMatricula.requestFocus();
    }

    @Override
    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }

    // ════════════════════════════════════════════════════════════════
    //  UI BUILDER
    // ════════════════════════════════════════════════════════════════
    @SuppressWarnings("unchecked")
    private void initComponents() {
        pnlHeader = new JPanel();
        lblTituloHeader = new JLabel("GESTIÓN DE PROFESIONALES");
        txtBuscarMedico = new JTextField();
        btnVolver = new JButton();

        pnlContenedorBlanco = new JPanel();
        pnlCuerpo = new JPanel();
        pnlFormulario = new JPanel();
        pnlTablaWrapper = new JPanel();
        lblTituloTabla = new JLabel("Profesionales Registrados");
        pnlFooter = new JPanel();

        lblMatricula = new JLabel("MATRÍCULA");
        lblNombre = new JLabel("NOMBRE");
        lblApellido = new JLabel("APELLIDO");
        lblEspecialidad = new JLabel("ESPECIALIDAD");
        lblObservaciones = new JLabel("OBSERVACIONES");

        txtMatricula = new JTextField();
        txtNombreMedico = new JTextField();
        txtApellidoMedico = new JTextField();
        cbxEspecialidad = new javax.swing.JComboBox<>();
        txtObservaciones = new JTextArea(3, 20);
        
        jScrollPane2 = new JScrollPane();
        grillaMedicos = new JTable();
        jScrollPane1 = new JScrollPane();

        pnlBotonesEdicion = new JPanel();
        btnGuardarMedico = new JButton();
        btnEliminarMedico = new JButton();

        cbxEspecialidad.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{
            "GENERALISTA","ALERGISTA","CARDIOLOGIA","CIRUGIA","CLINICA MEDICA",
            "DERMATOLOGIA","ENDOCRINOLOGIA","GASTROENTEROLOGIA","GERONTOLOGIA",
            "GINECOLOGIA","HEMATOLOGIA","INFECTOLOGIA","NEFROLOGIA","NEUMONOLOGIA",
            "NEUROCIRUGIA","NEUROLOGIA","NUTRICIONISTA","OBSTETRICIA","ODONTOLOGIA",
            "OFTALMOLOGIA","ONCOLOGIA","ORTOPEDIA","OTORRINOLARINGOLOGIA","PEDIATRIA",
            "PSIQUIATRIA","REUMATOLOGIA","TRAUMATOLOGIA","UROLOGIA"
        }));

        txtObservaciones.setLineWrap(true);
        txtObservaciones.setWrapStyleWord(true);
        jScrollPane2.setViewportView(txtObservaciones);

        grillaMedicos.setModel(new DefaultTableModel(
            new Object[][]{},
            new String[]{"MATRÍCULA", "APELLIDO", "NOMBRE", "ESPECIALIDAD"}
        ));
        jScrollPane1.setViewportView(grillaMedicos);
    }

    // ── Variables ────────────────────────────────────────────────────
    private JButton btnGuardarMedico;
    private JButton btnEliminarMedico;
    private JButton btnVolver;
    private javax.swing.JComboBox<String> cbxEspecialidad;
    private JTable grillaMedicos;
    private JScrollPane jScrollPane1;
    private JScrollPane jScrollPane2;
    private JLabel lblMatricula;
    private JLabel lblNombre;
    private JLabel lblApellido;
    private JLabel lblEspecialidad;
    private JLabel lblObservaciones;
    private JPanel pnlHeader;
    private JPanel pnlContenedorBlanco;
    private JPanel pnlCuerpo;
    private JPanel pnlFormulario;
    private JPanel pnlBotonesEdicion;
    private JPanel pnlTablaWrapper;
    private JPanel pnlFooter;
    private JLabel lblTituloHeader;
    private JLabel lblTituloTabla;
    private JTextField txtApellidoMedico;
    private JTextField txtBuscarMedico;
    private JTextField txtMatricula;
    private JTextField txtNombreMedico;
    private JTextArea txtObservaciones;
}