package vista.swing;

import vista.interfaces.IVistaPaciente;
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
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JWindow;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import modelo.Paciente;
import presentador.MedicoPresenter;
import presentador.PacientePresenter;

public class VistaPaciente extends JPanel implements IVistaPaciente {

    private PacientePresenter presenter;
    private JWindow ventanaSugerencias;
    private JList<String> listaSugerencias;
    private DefaultListModel<String> modeloSugerencias;

    // ── Paleta BIOTEC Minimalista ────────────────────────────────────
    private final Color C_NAVY         = new Color(10, 25, 47);    // Azul Encabezado
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

    public VistaPaciente() {
        initComponents();
        aplicarEstilo();
        configurarBuscadorOS();
        configurarNavegacionEnter();

        btnCargarResultados.setEnabled(false);
        btnVerHistorial.setEnabled(false);
   
    }

    private void aplicarEstilo() {
        setBackground(C_FONDO);

        // ── HEADER (Azul institucional con flecha) ───────────────────
        pnlHeader.setBackground(C_NAVY);
        pnlHeader.setBorder(new EmptyBorder(15, 30, 15, 30));
        
        lblTituloHeader.setForeground(C_BLANCO);
        lblTituloHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));

        estilizarCampoBuscador(txtBuscar);

        // ── FORMULARIO ──────────────────────────────────────────────
        pnlFormulario.setBackground(C_BLANCO);
        pnlFormulario.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(0, 0, 0, 20), // Separación con la tabla
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDE, 1, true),
                new EmptyBorder(25, 30, 25, 30) // Respiro interno
            )
        ));

        Font fontLabel = new Font("Segoe UI", Font.BOLD, 11);
        JLabel[] labels = {lblDNI, lblNombre, lblApellido, lblSexo, lblEdad, lblDir, lblLoc, lblOS, lblNAfiliado, lblCel};
        for (JLabel lbl : labels) {
            lbl.setFont(fontLabel);
            lbl.setForeground(C_TEXTO_SUAVE);
        }

        javax.swing.JTextField[] campos = {txtDni, txtNombre, txtApellido, txtEdad, txtDireccion, txtLocalidad, txtObraSocial, txtNAfiliado, txtCelular};
        for (javax.swing.JTextField txt : campos) {
            estilizarCampo(txt);
        }

        cbxSexo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbxSexo.setBackground(C_CAMPO);
        cbxSexo.setForeground(C_TEXTO_FUERTE);
        cbxSexo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDE),
            new EmptyBorder(4, 4, 4, 4)
        ));
        cbxSexo.setPreferredSize(new Dimension(0, 36));

        // ── BOTONES ─────────────────────────────────────────────────
        configurarBoton(btnGuardarPaciente, C_VERDE, "GUARDAR PACIENTE");
        configurarBoton(btnEditarPaciente, C_AZUL_MEDIO, "ACTUALIZAR DATOS");
        configurarBotonRetroceso(btnVolver);
        
        configurarBoton(btnCargarResultados, C_AZUL_MEDIO, "＋ NUEVO ANÁLISIS");
        configurarBoton(btnVerHistorial, new Color(70, 130, 180), "☰ VER HISTORIAL");

        // ── TABLA ───────────────────────────────────────────────────
        pnlTablaWrapper.setBackground(C_BLANCO);
        pnlTablaWrapper.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(C_BORDE, 1, true),
            new EmptyBorder(1, 1, 1, 1)
        ));

        lblTituloTabla.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTituloTabla.setForeground(C_TEXTO_FUERTE);
        lblTituloTabla.setBorder(new EmptyBorder(12, 15, 12, 15));

        grillaPacientes.setRowHeight(38);
        grillaPacientes.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        grillaPacientes.setGridColor(new Color(235, 240, 245));
        grillaPacientes.setShowHorizontalLines(true);
        grillaPacientes.setShowVerticalLines(false);
        grillaPacientes.setSelectionBackground(new Color(220, 235, 250));
        grillaPacientes.setSelectionForeground(C_TEXTO_FUERTE);
        grillaPacientes.setIntercellSpacing(new Dimension(0, 0));
        grillaPacientes.setBorder(BorderFactory.createEmptyBorder());

        grillaPacientes.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        grillaPacientes.getTableHeader().setBackground(C_CABECERA_TBL);
        grillaPacientes.getTableHeader().setForeground(C_TEXTO_SUAVE);
        grillaPacientes.getTableHeader().setPreferredSize(new Dimension(0, 42));
        grillaPacientes.getTableHeader().setBorder(BorderFactory.createMatteBorder(1, 0, 2, 0, C_BORDE));
        grillaPacientes.getTableHeader().setReorderingAllowed(false);

        jScrollPane1.setBorder(BorderFactory.createEmptyBorder());
        jScrollPane1.getViewport().setBackground(C_BLANCO);

        // ── FOOTER ──────────────────────────────────────────────────
        pnlFooter.setBackground(C_FONDO);
        pnlFooter.setBorder(new EmptyBorder(15, 0, 0, 0));
    }

    private void estilizarCampo(javax.swing.JTextField tf) {
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBackground(C_CAMPO);
        tf.setForeground(C_TEXTO_FUERTE);
        tf.setCaretColor(C_AZUL_MEDIO);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDE),
            new EmptyBorder(6, 10, 6, 10)
        ));
        tf.setPreferredSize(new Dimension(0, 36));
        
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

    private void estilizarCampoBuscador(javax.swing.JTextField tf) {
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setBackground(new Color(25, 45, 75)); // Azul un poco más claro que el fondo
        tf.setForeground(C_BLANCO);
        tf.setCaretColor(C_BLANCO);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 80, 120), 1, true),
            new EmptyBorder(8, 14, 8, 14)
        ));
        tf.setPreferredSize(new Dimension(380, 42));
    }

    private void configurarBoton(javax.swing.JButton btn, Color bg, String texto) {
        btn.setText(texto);
        btn.setBackground(bg);
        btn.setForeground(C_BLANCO);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(160, 42));
    }

    private void configurarBotonRetroceso(javax.swing.JButton btn) {
        btn.setText(" "); // Espacio para separar del icono
        btn.setBackground(C_NAVY);
        btn.setForeground(C_HEADER_TEXT);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false); // Transparente
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(0, 0, 0, 20));

        // Cargar logo flecha_icon.png
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

    // ══════════════════════════════════════════════════════════════════
    //  LÓGICA
    // ══════════════════════════════════════════════════════════════════
    private void configurarBuscadorOS() {
        modeloSugerencias = new DefaultListModel<>();
        listaSugerencias = new JList<>(modeloSugerencias);
        listaSugerencias.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        listaSugerencias.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaSugerencias.setFixedCellHeight(32);
        listaSugerencias.setBackground(C_BLANCO);
        listaSugerencias.setSelectionBackground(new Color(220, 235, 250));
        listaSugerencias.setSelectionForeground(C_TEXTO_FUERTE);

        txtObraSocial.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (ventanaSugerencias == null || !ventanaSugerencias.isVisible() || modeloSugerencias.isEmpty()) return;
                int index = listaSugerencias.getSelectedIndex();
                int size  = modeloSugerencias.getSize();
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    index = Math.min(index + 1, size - 1);
                    listaSugerencias.setSelectedIndex(index);
                    listaSugerencias.ensureIndexIsVisible(index);
                    e.consume();
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    index = Math.max(index - 1, 0);
                    listaSugerencias.setSelectedIndex(index);
                    listaSugerencias.ensureIndexIsVisible(index);
                    e.consume();
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (index != -1) { seleccionarSugerencia(); e.consume(); }
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    ventanaSugerencias.setVisible(false);
                }
            }

            // 3. DENTRO DE configurarBuscadorOS() (Al soltar una tecla):
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    return;
                }

                String texto = txtObraSocial.getText().trim();
                if (texto.length() >= 1) {
                    if (presenter != null) {
                        presenter.onBuscarSugerenciaOS();
                    }
                } else if (ventanaSugerencias != null) {
                    ventanaSugerencias.setVisible(false);
                }
            }
        });

        listaSugerencias.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { seleccionarSugerencia(); }
        });
    }

    @Override
    public void mostrarSugerenciasOS(List<String> sugerencias) {
        modeloSugerencias.clear();
        for (String s : sugerencias) modeloSugerencias.addElement(s);

        if (modeloSugerencias.isEmpty()) {
            if (ventanaSugerencias != null) ventanaSugerencias.setVisible(false);
            return;
        }

        if (ventanaSugerencias == null) {
            JScrollPane scroll = new JScrollPane(listaSugerencias);
            scroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDE, 1),
                new EmptyBorder(2, 0, 2, 0)
            ));
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            ventanaSugerencias = new JWindow(parentWindow);
            ventanaSugerencias.setAlwaysOnTop(true);
            ventanaSugerencias.getContentPane().add(scroll);
            ventanaSugerencias.setFocusableWindowState(false);
        }

        java.awt.Point p = txtObraSocial.getLocationOnScreen();
        int alto = Math.min(200, modeloSugerencias.size() * 32 + 5);
        ventanaSugerencias.setBounds(p.x, p.y + txtObraSocial.getHeight(), txtObraSocial.getWidth(), alto);
        ventanaSugerencias.setVisible(true);
        listaSugerencias.setSelectedIndex(0);
    }

    private void seleccionarSugerencia() {
        String sel = listaSugerencias.getSelectedValue();
        if (sel != null) {
            txtObraSocial.setText(sel);
            ventanaSugerencias.setVisible(false);
            txtNAfiliado.requestFocus();
        }
    }

    private void configurarNavegacionEnter() {
        KeyAdapter navAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (ventanaSugerencias != null && ventanaSugerencias.isVisible()
                            && !listaSugerencias.isSelectionEmpty()) return;
                    Object s = e.getSource();
                    // Secuencia lógica de carga
                    if      (s == txtDni)        txtNombre.requestFocus();
                    else if (s == txtNombre)     txtApellido.requestFocus();
                    else if (s == txtApellido)   cbxSexo.requestFocus();
                    else if (s == cbxSexo)       txtEdad.requestFocus();
                    else if (s == txtEdad)       txtDireccion.requestFocus();
                    else if (s == txtDireccion)  txtLocalidad.requestFocus();
                    else if (s == txtLocalidad)  txtCelular.requestFocus();
                    else if (s == txtCelular)    txtObraSocial.requestFocus();
                    else if (s == txtObraSocial) txtNAfiliado.requestFocus();
                    else if (s == txtNAfiliado)  btnGuardarPaciente.doClick();
                }
            }
        };
        txtDni.addKeyListener(navAdapter);
        txtNombre.addKeyListener(navAdapter);
        txtApellido.addKeyListener(navAdapter);
        txtEdad.addKeyListener(navAdapter);
        txtDireccion.addKeyListener(navAdapter);
        txtLocalidad.addKeyListener(navAdapter);
        txtCelular.addKeyListener(navAdapter);
        txtObraSocial.addKeyListener(navAdapter);
        txtNAfiliado.addKeyListener(navAdapter);
    }

    @Override public void ejecutar() { setVisible(true); }

    @Override
    public void setPresenter(PacientePresenter presenter) {
        this.presenter = presenter;
        
        // ¡MAGIA MVP! Los botones llaman a los métodos exactos, sin "switch"
        btnGuardarPaciente.addActionListener(e -> presenter.onGuardarPaciente());
        btnEditarPaciente.addActionListener( e -> presenter.onEditarPaciente());
        btnCargarResultados.addActionListener(e -> presenter.onCargarResultados());
        btnVerHistorial.addActionListener(e -> presenter.onVerHistorial());
        btnVolver.addActionListener(e -> presenter.onVolver());
        
        // Buscador
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                presenter.onBuscarPaciente();
            }
        });

        // ── AQUÍ AGREGAS EL LISTENER DE LA TABLA ──
        grillaPacientes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                presenter.onSeleccionarPaciente();
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

    @Override public String getDni()           { return txtDni.getText().trim(); }
    @Override public String getNombre()        { return txtNombre.getText().trim(); }
    @Override public String getApellido()      { return txtApellido.getText().trim(); }
    @Override public String getEdad()          { return txtEdad.getText().trim(); }
    @Override public String getDireccion()     { return txtDireccion.getText().trim(); }
    @Override public String getLocalidad()     { return txtLocalidad.getText().trim(); }
    @Override public String getNumAfiliado()   { return txtNAfiliado.getText().trim(); }
    @Override public String getCelular()       { return txtCelular.getText().trim(); }
    @Override public String getSexo()          { return (cbxSexo.getSelectedIndex() >= 0) ? cbxSexo.getSelectedItem().toString() : ""; }
    @Override public String getObraSocial()    { return txtObraSocial.getText().trim(); }
    @Override public String getTextoBusqueda() { return txtBuscar.getText().trim(); }

    @Override public void habilitarBotonGuardar(boolean b)          { btnGuardarPaciente.setEnabled(b); }
    @Override public void habilitarBotonEditar(boolean b)           { btnEditarPaciente.setEnabled(b); }
    @Override public void habilitarBotonCargarResultados(boolean b) { btnCargarResultados.setEnabled(b); }
    @Override public void habilitarBotonNuevoAnalisis(boolean b)    { btnCargarResultados.setEnabled(b); }

    @Override
    public void cargarPacientesEnTabla(ArrayList<Paciente> pacientes) {
        DefaultTableModel modelo = new DefaultTableModel(
            new Object[][]{},
            new String[]{"ID", "APELLIDO", "NOMBRE", "DNI", "ÚLTIMO ANÁLISIS"}
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };

        modelo.setRowCount(0);
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
        for (Paciente p : pacientes) {
            String fecha = (p.getFechaUltimoAnalisis() != null) ? sdf.format(p.getFechaUltimoAnalisis()) : "Sin análisis";
            modelo.addRow(new Object[]{ p.getIdPaciente(), p.getApellido().toUpperCase(), p.getNombre().toUpperCase(), p.getDni(), fecha });
        }
        grillaPacientes.setModel(modelo);

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(javax.swing.JTable t,
                    Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                if (!sel) {
                    setBackground(row % 2 == 0 ? C_BLANCO : C_FILA_PAR);
                    setForeground(C_TEXTO_FUERTE);
                }
                return this;
            }
        };
        for (int i = 0; i < grillaPacientes.getColumnCount(); i++) {
            grillaPacientes.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
        
        grillaPacientes.getColumnModel().getColumn(0).setPreferredWidth(60);
        grillaPacientes.getColumnModel().getColumn(0).setMaxWidth(80);
    }

    @Override
    public Paciente getPacienteSeleccionado() {
        int fila = grillaPacientes.getSelectedRow();
        if (fila == -1) return null;
        Paciente p = new Paciente();
        p.setIdPaciente(Integer.parseInt(grillaPacientes.getValueAt(fila, 0).toString()));
        return p;
    }

    @Override
    public void cargarDatosPaciente(Paciente p) {
        if (p == null) return;
        txtDni.setText(p.getDni());
        txtNombre.setText(p.getNombre());
        txtApellido.setText(p.getApellido());
        txtEdad.setText(p.getEdad());
        txtDireccion.setText(p.getDireccion());
        txtLocalidad.setText(p.getLocalidad());
        txtNAfiliado.setText(p.getNroAfiliado());
        txtCelular.setText(p.getCelular());
        cbxSexo.setSelectedItem(p.getSexo());
        txtObraSocial.setText(p.getObraSocial());
    }

    @Override
    public void limpiarCampos() {
        txtDni.setText(""); txtApellido.setText(""); txtNombre.setText("");
        txtEdad.setText(""); txtDireccion.setText(""); txtLocalidad.setText(""); 
        txtNAfiliado.setText(""); txtCelular.setText(""); cbxSexo.setSelectedIndex(0); 
        txtObraSocial.setText(""); txtBuscar.setText("");
        txtDni.requestFocus();
    }

    @Override public void mostrarMensaje(String mensaje) { JOptionPane.showMessageDialog(this, mensaje); }

    // ══════════════════════════════════════════════════════════════════
    //  UI BUILDER (Layout Estructurado)
    // ══════════════════════════════════════════════════════════════════
    @SuppressWarnings("unchecked")
    private void initComponents() {

        pnlHeader          = new JPanel();
        lblTituloHeader    = new JLabel("GESTIÓN DE PACIENTES");
        txtBuscar          = new javax.swing.JTextField();
        
        pnlCuerpo          = new JPanel();
        pnlFormulario      = new JPanel();
        pnlTablaWrapper    = new JPanel();
        lblTituloTabla     = new JLabel("Pacientes Registrados");
        pnlFooter          = new JPanel();

        lblDNI             = new JLabel("DNI / DOCUMENTO");
        lblNombre          = new JLabel("NOMBRE");
        lblApellido        = new JLabel("APELLIDO");
        lblSexo            = new JLabel("SEXO");
        lblEdad            = new JLabel("EDAD");
        lblDir             = new JLabel("DIRECCIÓN");
        lblLoc             = new JLabel("LOCALIDAD");
        lblOS              = new JLabel("OBRA SOCIAL");
        lblNAfiliado       = new JLabel("N° AFILIADO");
        lblCel             = new JLabel("CELULAR");

        txtDni             = new javax.swing.JTextField();
        txtNombre          = new javax.swing.JTextField();
        txtApellido        = new javax.swing.JTextField();
        cbxSexo            = new javax.swing.JComboBox<>();
        txtEdad            = new javax.swing.JTextField();
        txtDireccion       = new javax.swing.JTextField();
        txtLocalidad       = new javax.swing.JTextField();
        txtObraSocial      = new javax.swing.JTextField();
        txtNAfiliado       = new javax.swing.JTextField();
        txtCelular         = new javax.swing.JTextField();

        pnlBotonesEdicion  = new JPanel();
        btnGuardarPaciente = new javax.swing.JButton();
        btnEditarPaciente  = new javax.swing.JButton();
        
        grillaPacientes    = new JTable();
        jScrollPane1       = new javax.swing.JScrollPane();
        
        btnCargarResultados= new javax.swing.JButton();
        btnVerHistorial    = new javax.swing.JButton();
        btnVolver          = new javax.swing.JButton();

        cbxSexo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"M","F","X"}));
        
        // ── ROOT ─────────────────────────────────────────────────────
        setLayout(new BorderLayout());

        // ── HEADER ───────────────────────────────────────────────────
        pnlHeader.setLayout(new BorderLayout());
        
        // 1. Panel Izquierdo: Agrupamos el botón Volver y el Título
        JPanel pnlIzqHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlIzqHeader.setOpaque(false);
        pnlIzqHeader.add(btnVolver);
        
        lblTituloHeader.setHorizontalAlignment(SwingConstants.LEFT);
        lblTituloHeader.setBorder(new EmptyBorder(0, 10, 0, 0)); 
        pnlIzqHeader.add(lblTituloHeader);
        
        // 2. Panel Derecho: Buscador
        JPanel pnlDerHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pnlDerHeader.setOpaque(false);
        JLabel lblLupa = new JLabel("Buscar paciente:  ");
        lblLupa.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblLupa.setForeground(C_HEADER_TEXT);
        pnlDerHeader.add(lblLupa);
        pnlDerHeader.add(txtBuscar);
        
        // 3. Ensamblamos el Header
        pnlHeader.add(pnlIzqHeader, BorderLayout.WEST);
        pnlHeader.add(pnlDerHeader, BorderLayout.EAST);
        
        add(pnlHeader, BorderLayout.NORTH);

        // ── CUERPO ───────────────────────────────────────────────────
        pnlCuerpo.setBackground(C_FONDO);
        pnlCuerpo.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.BOTH;
        gc.weighty = 1.0;

        // Formulario (Izquierda)
        gc.gridx = 0; gc.gridy = 0; gc.weightx = 0;
        pnlFormulario.setPreferredSize(new Dimension(440, 0));
        pnlFormulario.setMinimumSize(new Dimension(400, 0));
        pnlCuerpo.add(pnlFormulario, gc);

        // Tabla (Derecha)
        gc.gridx = 1; gc.gridy = 0; gc.weightx = 1.0;
        pnlCuerpo.add(pnlTablaWrapper, gc);

        add(pnlCuerpo, BorderLayout.CENTER);

        // ── FORMULARIO: Layout en Grilla ─────────────────────────────
        pnlFormulario.setLayout(new GridBagLayout());
        GridBagConstraints gf = new GridBagConstraints();
        gf.fill = GridBagConstraints.HORIZONTAL;
        gf.weightx = 1.0;
        int r = 0;
        
        // Función auxiliar para agrupar Label + TextField
        java.util.function.BiFunction<JLabel, Component, JPanel> crearCampo = (lbl, cmp) -> {
            JPanel p = new JPanel(new BorderLayout(0, 4));
            p.setOpaque(false);
            p.add(lbl, BorderLayout.NORTH);
            p.add(cmp, BorderLayout.CENTER);
            return p;
        };

        // Función auxiliar para fila de 2 columnas
        java.util.function.BiFunction<Component, Component, JPanel> crearFilaDoble = (c1, c2) -> {
            JPanel p = new JPanel(new GridLayout(1, 2, 15, 0));
            p.setOpaque(false);
            p.add(c1); p.add(c2);
            return p;
        };

        // Función auxiliar para títulos de sección
        java.util.function.Function<String, JPanel> crearSeparador = (titulo) -> {
            JPanel p = new JPanel(new BorderLayout(8, 0));
            p.setOpaque(false);
            p.setBorder(new EmptyBorder(15, 0, 10, 0));
            JLabel l = new JLabel(titulo);
            l.setFont(new Font("Segoe UI", Font.BOLD, 12));
            l.setForeground(C_AZUL_MEDIO);
            JPanel linea = new JPanel(); linea.setBackground(C_BORDE); linea.setPreferredSize(new Dimension(0, 1));
            JPanel pL = new JPanel(new GridBagLayout()); pL.setOpaque(false);
            GridBagConstraints gl = new GridBagConstraints(); gl.fill = GridBagConstraints.HORIZONTAL; gl.weightx = 1.0;
            pL.add(linea, gl);
            p.add(l, BorderLayout.WEST); p.add(pL, BorderLayout.CENTER);
            return p;
        };

        // Construcción limpia y alineada
        gf.gridx = 0;
        gf.insets = new Insets(0, 0, 12, 0);

        gf.gridy = r++; pnlFormulario.add(crearSeparador.apply("DATOS PERSONALES"), gf);
        gf.gridy = r++; pnlFormulario.add(crearCampo.apply(lblDNI, txtDni), gf);
        gf.gridy = r++; pnlFormulario.add(crearFilaDoble.apply(crearCampo.apply(lblNombre, txtNombre), crearCampo.apply(lblApellido, txtApellido)), gf);
        gf.gridy = r++; pnlFormulario.add(crearFilaDoble.apply(crearCampo.apply(lblSexo, cbxSexo), crearCampo.apply(lblEdad, txtEdad)), gf);
        
        gf.gridy = r++; pnlFormulario.add(crearSeparador.apply("CONTACTO"), gf);
        gf.gridy = r++; pnlFormulario.add(crearCampo.apply(lblDir, txtDireccion), gf);
        gf.gridy = r++; pnlFormulario.add(crearFilaDoble.apply(crearCampo.apply(lblLoc, txtLocalidad), crearCampo.apply(lblCel, txtCelular)), gf);

        gf.gridy = r++; pnlFormulario.add(crearSeparador.apply("COBERTURA MÉDICA"), gf);
        gf.gridy = r++; pnlFormulario.add(crearCampo.apply(lblOS, txtObraSocial), gf);
        gf.gridy = r++; pnlFormulario.add(crearCampo.apply(lblNAfiliado, txtNAfiliado), gf);

        // Botones guardar y editar
        pnlBotonesEdicion.setOpaque(false);
        pnlBotonesEdicion.setLayout(new java.awt.GridLayout(1, 2, 10, 0));
        pnlBotonesEdicion.add(btnEditarPaciente);
        pnlBotonesEdicion.add(btnGuardarPaciente);
        
        gf.gridy = r++; gf.insets = new Insets(20, 0, 0, 0); 
        pnlFormulario.add(pnlBotonesEdicion, gf);
        
        gf.gridy = r++; gf.weighty = 1.0; gf.fill = GridBagConstraints.VERTICAL;
        pnlFormulario.add(new JPanel() {{ setOpaque(false); }}, gf); // Spacer para empujar hacia arriba

        // ── TABLA ENVOLTORIO ─────────────────────────────────────────
        pnlTablaWrapper.setLayout(new BorderLayout());
        pnlTablaWrapper.add(lblTituloTabla, BorderLayout.NORTH);
        jScrollPane1.setViewportView(grillaPacientes);
        pnlTablaWrapper.add(jScrollPane1, BorderLayout.CENTER);

        // ── FOOTER ───────────────────────────────────────────────────
        pnlFooter.setLayout(new BorderLayout());
        JPanel pnlFooterAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        pnlFooterAcciones.setOpaque(false);
        pnlFooterAcciones.add(btnVerHistorial);
        pnlFooterAcciones.add(btnCargarResultados);
        
        pnlFooter.add(pnlFooterAcciones, BorderLayout.EAST);
        add(pnlFooter, BorderLayout.SOUTH);
    }

    // ── Variables ────────────────────────────────────────────────────
    private javax.swing.JButton btnCargarResultados;
    private javax.swing.JButton btnEditarPaciente;
    private javax.swing.JButton btnGuardarPaciente;
    private javax.swing.JButton btnVerHistorial;
    private javax.swing.JButton btnVolver;
    private javax.swing.JComboBox<String> cbxSexo;
    private javax.swing.JTable grillaPacientes;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblDNI;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblApellido;
    private javax.swing.JLabel lblSexo;
    private javax.swing.JLabel lblEdad;
    private javax.swing.JLabel lblDir;
    private javax.swing.JLabel lblLoc;
    private javax.swing.JLabel lblOS;
    private javax.swing.JLabel lblNAfiliado;
    private javax.swing.JLabel lblCel;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPanel pnlCuerpo;
    private javax.swing.JPanel pnlFormulario;
    private javax.swing.JPanel pnlBotonesEdicion;
    private javax.swing.JPanel pnlTablaWrapper;
    private javax.swing.JPanel pnlFooter;
    private javax.swing.JLabel lblTituloHeader;
    private javax.swing.JLabel lblTituloTabla;
    private javax.swing.JTextField txtApellido;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtCelular;
    private javax.swing.JTextField txtDireccion;
    private javax.swing.JTextField txtDni;
    private javax.swing.JTextField txtLocalidad;
    private javax.swing.JTextField txtNAfiliado;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtEdad;
    private javax.swing.JTextField txtObraSocial;

}