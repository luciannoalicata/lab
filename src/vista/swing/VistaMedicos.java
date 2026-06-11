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
import javax.swing.table.TableRowSorter;
import modelo.Medico;
import presentador.MedicoPresenter;

public class VistaMedicos extends JPanel implements IVistaMedicos {

    private MedicoPresenter presenter;
    private boolean actualizandoVista = false;
    
    // ── Paleta BIOTEC Minimalista (Sincronizada con Principal) ───────
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

    public VistaMedicos() {
        initComponents();
        aplicarEstilo();
        configurarNavegacionEnter();
    }

    // ════════════════════════════════════════════════════════════════
    //  ESTILO Y UX - Consistente con VistaPaciente
    // ════════════════════════════════════════════════════════════════
    private void aplicarEstilo() {
        setBackground(C_FONDO);

        // ── HEADER (mismos márgenes que VistaPaciente) ────────────────
        pnlHeader.setBackground(C_NAVY);
        pnlHeader.setBorder(new EmptyBorder(14, 28, 14, 28));
        
        // Reconstruir el header correctamente
        pnlHeader.removeAll();
        pnlHeader.setLayout(new BorderLayout());
        
        // Panel izquierdo: botón volver + título
        JPanel pnlIzqHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlIzqHeader.setOpaque(false);
        pnlIzqHeader.add(btnVolver);
        
        lblTituloHeader.setForeground(C_BLANCO);
        lblTituloHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTituloHeader.setHorizontalAlignment(SwingConstants.LEFT);
        lblTituloHeader.setBorder(new EmptyBorder(0, 10, 0, 0));
        pnlIzqHeader.add(lblTituloHeader);
        
        // Panel derecho: buscador
        JPanel pnlDerHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        pnlDerHeader.setOpaque(false);
        JLabel lblLupa = new JLabel("Buscar médico:");
        lblLupa.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblLupa.setForeground(C_HEADER_TEXT);
        pnlDerHeader.add(lblLupa);
        pnlDerHeader.add(txtBuscarMedico);
        
        pnlHeader.add(pnlIzqHeader, BorderLayout.WEST);
        pnlHeader.add(pnlDerHeader, BorderLayout.EAST);

        estilizarCampoBuscador(txtBuscarMedico);
        configurarBotonRetroceso(btnVolver);

        // ── CONTENEDOR PRINCIPAL BLANCO (con borde sin superior) ──────
        pnlContenedorBlanco.setBackground(C_BLANCO);
        pnlContenedorBlanco.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 1, 1, 1, C_BORDE),
            new EmptyBorder(24, 28, 24, 28)
        ));
        pnlContenedorBlanco.removeAll();
        pnlContenedorBlanco.setLayout(new BorderLayout());

        // ── CUERPO (formulario izquierda + tabla derecha) ─────────────
        pnlCuerpo.setBackground(C_BLANCO);
        pnlCuerpo.removeAll();
        pnlCuerpo.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.BOTH;
        gc.weighty = 1.0;
        gc.insets = new Insets(0, 0, 0, 0);

        // Columna izquierda: formulario
        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = 0;
        pnlFormulario.setPreferredSize(new Dimension(460, 0));
        pnlFormulario.setMinimumSize(new Dimension(420, 0));
        pnlCuerpo.add(pnlFormulario, gc);

        // Columna derecha: tabla
        gc.gridx = 1;
        gc.gridy = 0;
        gc.weightx = 1.0;
        pnlCuerpo.add(pnlTablaWrapper, gc);

        pnlContenedorBlanco.add(pnlCuerpo, BorderLayout.CENTER);

        // ── FORMULARIO (más espacioso) ────────────────────────────────
        pnlFormulario.setBackground(C_BLANCO);
        pnlFormulario.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(16, 16, 16, 16),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDE, 1, true),
                new EmptyBorder(24, 28, 24, 28)
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
        cbxEspecialidad.setPreferredSize(new Dimension(0, 38));

        txtObservaciones.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtObservaciones.setBackground(C_CAMPO);
        txtObservaciones.setForeground(C_TEXTO_FUERTE);
        txtObservaciones.setCaretColor(C_AZUL_MEDIO);
        txtObservaciones.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(C_BORDE, 1, true),
            new EmptyBorder(8, 10, 8, 10)
        ));
        jScrollPane2.setBorder(BorderFactory.createEmptyBorder());

        // ── BOTONES ───────────────────────────────────────────────────
        configurarBoton(btnGuardarMedico, C_VERDE, "GUARDAR", 160, 42);
        configurarBoton(btnEliminarMedico, C_ROJO, "ELIMINAR", 160, 42);

        // ── TABLA WRAPPER (igual que VistaPaciente) ───────────────────
        pnlTablaWrapper.setBackground(C_BLANCO);
        pnlTablaWrapper.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(0, 0, 0, 0),
            BorderFactory.createLineBorder(C_BORDE, 1, true)
        ));

        lblTituloTabla.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTituloTabla.setForeground(C_TEXTO_FUERTE);
        lblTituloTabla.setBorder(new EmptyBorder(14, 16, 12, 16));

        grillaMedicos.setRowHeight(36);
        grillaMedicos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        grillaMedicos.setGridColor(new Color(235, 240, 245));
        grillaMedicos.setShowHorizontalLines(true);
        grillaMedicos.setShowVerticalLines(false);
        grillaMedicos.setSelectionBackground(new Color(220, 235, 250));
        grillaMedicos.setSelectionForeground(C_TEXTO_FUERTE);
        grillaMedicos.setIntercellSpacing(new Dimension(0, 0));
        grillaMedicos.setBorder(BorderFactory.createEmptyBorder());
        grillaMedicos.setFillsViewportHeight(true);

        grillaMedicos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        grillaMedicos.getTableHeader().setBackground(C_CABECERA_TBL);
        grillaMedicos.getTableHeader().setForeground(C_TEXTO_SUAVE);
        grillaMedicos.getTableHeader().setPreferredSize(new Dimension(0, 40));
        grillaMedicos.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDE));
        grillaMedicos.getTableHeader().setReorderingAllowed(false);

        jScrollPane1.setBorder(BorderFactory.createEmptyBorder());
        jScrollPane1.getViewport().setBackground(C_BLANCO);

        pnlTablaWrapper.removeAll();
        pnlTablaWrapper.setLayout(new BorderLayout());
        pnlTablaWrapper.add(lblTituloTabla, BorderLayout.NORTH);
        pnlTablaWrapper.add(jScrollPane1, BorderLayout.CENTER);

        // ── FOOTER (mismos márgenes que VistaPaciente) ────────────────
        pnlFooter.setBackground(C_FONDO);
        pnlFooter.setBorder(new EmptyBorder(10, 16, 14, 16));
        pnlFooter.removeAll();
        pnlFooter.setLayout(new BorderLayout());
        
        JPanel pnlFooterAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        pnlFooterAcciones.setOpaque(false);
        pnlFooter.add(pnlFooterAcciones, BorderLayout.EAST);

        // ── ARMADO FINAL DEL LAYOUT ───────────────────────────────────
        this.removeAll();
        this.setLayout(new BorderLayout());
        this.add(pnlHeader, BorderLayout.NORTH);
        this.add(pnlContenedorBlanco, BorderLayout.CENTER);
        this.add(pnlFooter, BorderLayout.SOUTH);

        // ── LAYOUT DEL FORMULARIO ─────────────────────────────────────
        pnlFormulario.removeAll();
        pnlFormulario.setLayout(new GridBagLayout());
        GridBagConstraints gf = new GridBagConstraints();
        gf.fill = GridBagConstraints.HORIZONTAL;
        gf.weightx = 1.0;
        int r = 0;
        
        gf.gridx = 0;
        
        gf.gridy = r++; gf.insets = new Insets(0, 0, 4, 0);  pnlFormulario.add(lblMatricula, gf);
        gf.gridy = r++; gf.insets = new Insets(0, 0, 20, 0); pnlFormulario.add(txtMatricula, gf);
        
        gf.gridy = r++; gf.insets = new Insets(0, 0, 4, 0);  pnlFormulario.add(lblNombre, gf);
        gf.gridy = r++; gf.insets = new Insets(0, 0, 20, 0); pnlFormulario.add(txtNombreMedico, gf);
        
        gf.gridy = r++; gf.insets = new Insets(0, 0, 4, 0);  pnlFormulario.add(lblApellido, gf);
        gf.gridy = r++; gf.insets = new Insets(0, 0, 20, 0); pnlFormulario.add(txtApellidoMedico, gf);
        
        gf.gridy = r++; gf.insets = new Insets(0, 0, 4, 0);  pnlFormulario.add(lblEspecialidad, gf);
        gf.gridy = r++; gf.insets = new Insets(0, 0, 20, 0); pnlFormulario.add(cbxEspecialidad, gf);
        
        gf.gridy = r++; gf.insets = new Insets(0, 0, 4, 0);  pnlFormulario.add(lblObservaciones, gf);
        gf.gridy = r++; gf.weighty = 0.5; gf.fill = GridBagConstraints.BOTH;
        gf.insets = new Insets(0, 0, 20, 0); pnlFormulario.add(jScrollPane2, gf);

        // Botones
        pnlBotonesEdicion.setOpaque(false);
        pnlBotonesEdicion.removeAll();
        pnlBotonesEdicion.setLayout(new java.awt.GridLayout(1, 2, 12, 0));
        pnlBotonesEdicion.add(btnEliminarMedico);
        pnlBotonesEdicion.add(btnGuardarMedico);
        
        gf.gridy = r++; gf.weighty = 0; gf.fill = GridBagConstraints.HORIZONTAL;
        gf.insets = new Insets(0, 0, 0, 0); 
        pnlFormulario.add(pnlBotonesEdicion, gf);

        // Spacer elástico
        gf.gridy = r++; gf.weighty = 1.0; gf.fill = GridBagConstraints.VERTICAL;
        gf.insets = new Insets(0, 0, 0, 0);
        pnlFormulario.add(new JPanel() {{ setOpaque(false); }}, gf);
        
        // Forzar actualización
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
            new EmptyBorder(8, 12, 8, 12)
        ));
        tf.setPreferredSize(new Dimension(0, 38));
        
        tf.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent evt) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, C_AZUL_MEDIO),
                    new EmptyBorder(8, 12, 8, 12)
                ));
                tf.setBackground(C_BLANCO);
            }
            @Override public void focusLost(java.awt.event.FocusEvent evt) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDE),
                    new EmptyBorder(8, 12, 8, 12)
                ));
                tf.setBackground(C_CAMPO);
            }
        });
    }

    private void estilizarCampoBuscador(JTextField tf) {
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setBackground(new Color(25, 45, 75));
        tf.setForeground(C_BLANCO);
        tf.setCaretColor(C_BLANCO);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 80, 120), 1, true),
            new EmptyBorder(8, 14, 8, 14)
        ));
        tf.setPreferredSize(new Dimension(320, 38));
    }

    private void configurarBoton(javax.swing.JButton btn, Color bg, String texto, int w, int h) {
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

    private void configurarBotonRetroceso(javax.swing.JButton btn) {
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
                    if      (c == txtMatricula)       txtNombreMedico.requestFocus();
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
        
        btnGuardarMedico.addActionListener(e -> presenter.onGuardarMedico());
        btnEliminarMedico.addActionListener(e -> presenter.onEliminarMedico());
        btnVolver.addActionListener(e -> presenter.onVolver());
        
        txtBuscarMedico.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                presenter.onBuscarMedico();
            }
        });

        grillaMedicos.getSelectionModel().addListSelectionListener(e -> {
            // ← EVITAR que se ejecute durante la limpieza/recarga
            if (!e.getValueIsAdjusting() && presenter != null) {
                presenter.onSeleccionarMedico();
            }
        });
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
        
        actualizandoVista = true;
        
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
        grillaMedicos.clearSelection();

        actualizandoVista = false;

        grillaMedicos.setModel(modelo);
        grillaMedicos.setRowSorter(new TableRowSorter<>(modelo));

        DefaultTableCellRenderer render = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(column == 0 ? SwingConstants.CENTER : SwingConstants.LEFT);
                setBorder(new EmptyBorder(0, 12, 0, 12));
                if (!isSelected) {
                    setBackground(row % 2 == 0 ? C_BLANCO : C_FILA_PAR);
                    setForeground(C_TEXTO_FUERTE);
                }
                return this;
            }
        };
        for (int i = 0; i < grillaMedicos.getColumnCount(); i++)
            grillaMedicos.getColumnModel().getColumn(i).setCellRenderer(render);

        grillaMedicos.getColumnModel().getColumn(0).setPreferredWidth(100);
        grillaMedicos.getColumnModel().getColumn(0).setMaxWidth(130);
        grillaMedicos.getColumnModel().getColumn(1).setPreferredWidth(180);
        grillaMedicos.getColumnModel().getColumn(2).setPreferredWidth(180);
        grillaMedicos.getColumnModel().getColumn(3).setPreferredWidth(200);
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
        txtMatricula.requestFocus();
    }

    @Override
    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }

    // ════════════════════════════════════════════════════════════════
    //  UI BUILDER (Estructura base)
    // ════════════════════════════════════════════════════════════════
    @SuppressWarnings("unchecked")
    private void initComponents() {
        pnlHeader          = new JPanel();
        lblTituloHeader    = new JLabel("GESTIÓN DE PROFESIONALES");
        txtBuscarMedico    = new JTextField();
        btnVolver          = new JButton();

        pnlContenedorBlanco = new JPanel();
        pnlCuerpo          = new JPanel();
        pnlFormulario      = new JPanel();
        pnlTablaWrapper    = new JPanel();
        lblTituloTabla     = new JLabel("Profesionales Registrados");
        pnlFooter          = new JPanel();

        lblMatricula       = new JLabel("MATRÍCULA");
        lblNombre          = new JLabel("NOMBRE");
        lblApellido        = new JLabel("APELLIDO");
        lblEspecialidad    = new JLabel("ESPECIALIDAD");
        lblObservaciones   = new JLabel("OBSERVACIONES / DETALLES");

        txtMatricula       = new JTextField();
        txtNombreMedico    = new JTextField();
        txtApellidoMedico  = new JTextField();
        cbxEspecialidad    = new javax.swing.JComboBox<>();
        txtObservaciones   = new JTextArea(4, 20);
        
        jScrollPane2       = new JScrollPane();
        grillaMedicos      = new JTable();
        jScrollPane1       = new JScrollPane();

        pnlBotonesEdicion  = new JPanel();
        btnGuardarMedico   = new javax.swing.JButton();
        btnEliminarMedico  = new javax.swing.JButton();

        cbxEspecialidad.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{
            "GENERALISTA","ALERGISTA","CABEZA Y CUELLO","CARDIOLOGIA","CARDIOLOGIA CLINICA",
            "CARDIOLOGIA INFANTIL","CIRUGIA","CIRUGIA CABEZA Y CUELLO","CIRUGIA INFANTIL",
            "CIRUGIA PLASTICA","CIRUJANO CARDIOVASCULAR","CLINICA MEDICA","CLINICA MEDICA Y DOLOR",
            "DERMATOLOGIA","ENDOCRINOLOGIA","FLEBOLOGIA","FLEBOLOGIA Y LINFOLOGIA","FONOAUDIOLOGIA",
            "GASTROENTEROLOGIA","GERONTOLOGIA","GINECOLOGIA","GINECOLOGIA Y OBSTETRICIA","HEMATOLOGIA",
            "HEMODINAMIA Y CARDIOLOGIA","INFECTOLOGIA","INMUNOLOGIA","INSTITUCION","NEFROLOGIA",
            "NEFROLOGIA INFANTIL","NEUMONOLOGIA","NEUROCIRUGIA","NEUROLOGIA","NEUROLOGIA INFANTIL",
            "NUTRICIONISTA","OBSTETRICIA","ODONTOLOGIA","OFTALMOLOGIA","ONCOLOGIA",
            "ORTOPEDIA Y TRAUMATOLOGIA","OTORRINOLARINGOLOGIA","PEDIATRIA","PEDIATRIA - NEONATOLOGIA",
            "PROCTOLOGIA","PSIQUIATRIA","REUMATOLOGIA","TOCOGINECOLOGIA","TRAUMATOLOGIA",
            "TRAUMATOLOGIA Y ORTOPEDIA","UROLOGIA","UROLOGIA INFANTIL"
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
    private javax.swing.JButton btnGuardarMedico;
    private javax.swing.JButton btnEliminarMedico;
    private javax.swing.JButton btnVolver;
    private javax.swing.JComboBox<String> cbxEspecialidad;
    private javax.swing.JTable grillaMedicos;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblMatricula;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblApellido;
    private javax.swing.JLabel lblEspecialidad;
    private javax.swing.JLabel lblObservaciones;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPanel pnlContenedorBlanco;
    private javax.swing.JPanel pnlCuerpo;
    private javax.swing.JPanel pnlFormulario;
    private javax.swing.JPanel pnlBotonesEdicion;
    private javax.swing.JPanel pnlTablaWrapper;
    private javax.swing.JPanel pnlFooter;
    private javax.swing.JLabel lblTituloHeader;
    private javax.swing.JLabel lblTituloTabla;
    private javax.swing.JTextField txtApellidoMedico;
    private javax.swing.JTextField txtBuscarMedico;
    private javax.swing.JTextField txtMatricula;
    private javax.swing.JTextField txtNombreMedico;
    private javax.swing.JTextArea txtObservaciones;
}