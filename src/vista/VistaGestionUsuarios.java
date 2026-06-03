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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import modelo.Usuario;

public class VistaGestionUsuarios extends JPanel implements IVistaGestionUsuarios {

    private Controlador controlador;

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

    public VistaGestionUsuarios() {
        initComponents();
        aplicarEstilo();
        configurarNavegacionEnter();
    }

    // ══════════════════════════════════════════════════════════════════
    //  ESTILO Y UX
    // ══════════════════════════════════════════════════════════════════
    private void aplicarEstilo() {
        setBackground(C_FONDO);

        // ── HEADER (Azul institucional con flecha) ───────────────────
        pnlHeader.setBackground(C_NAVY);
        pnlHeader.setBorder(new EmptyBorder(15, 30, 15, 30));
        
        lblTituloHeader.setForeground(C_BLANCO);
        lblTituloHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));

        // ── FORMULARIO (Más ancho y espacioso) ──────────────────────
        pnlFormulario.setBackground(C_BLANCO);
        pnlFormulario.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(0, 0, 0, 20), // Margen derecho para separar de la tabla
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDE, 1, true),
                new EmptyBorder(30, 35, 30, 35) // Mucho respiro interno
            )
        ));

        Font fontLabel = new Font("Segoe UI", Font.BOLD, 12);
        for (JLabel lbl : new JLabel[]{lblUsername, lblPassword, lblRol}) {
            lbl.setFont(fontLabel);
            lbl.setForeground(C_TEXTO_SUAVE);
        }

        estilizarCampo(txtUsername);
        estilizarCampo(txtPassword);

        cbxRol.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbxRol.setBackground(C_CAMPO);
        cbxRol.setForeground(C_TEXTO_FUERTE);
        cbxRol.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDE),
            new EmptyBorder(4, 4, 4, 4)
        ));
        cbxRol.setPreferredSize(new Dimension(0, 40));

        // ── BOTONES ─────────────────────────────────────────────────
        configurarBoton(btnGuardar, C_VERDE, "CREAR USUARIO", 200, 44);
        configurarBoton(btnEliminar, C_ROJO, "ELIMINAR", 200, 44);
        configurarBotonRetroceso(btnVolver);

        // ── PANEL TABLA Y ROLES ─────────────────────────────────────
        pnlDerechoWrapper.setBackground(C_FONDO); // Fondo transparente
        
        // Estilo de la tabla
        pnlTabla.setBackground(C_BLANCO);
        pnlTabla.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(C_BORDE, 1, true),
            new EmptyBorder(1, 1, 1, 1)
        ));

        lblTituloTabla.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTituloTabla.setForeground(C_TEXTO_FUERTE);
        lblTituloTabla.setBorder(new EmptyBorder(12, 15, 12, 15));

        grillaUsuarios.setRowHeight(38);
        grillaUsuarios.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        grillaUsuarios.setGridColor(new Color(235, 240, 245));
        grillaUsuarios.setShowHorizontalLines(true);
        grillaUsuarios.setShowVerticalLines(false);
        grillaUsuarios.setSelectionBackground(new Color(220, 235, 250));
        grillaUsuarios.setSelectionForeground(C_TEXTO_FUERTE);
        grillaUsuarios.setIntercellSpacing(new Dimension(0, 0));
        grillaUsuarios.setFillsViewportHeight(true);
        grillaUsuarios.setBorder(BorderFactory.createEmptyBorder());

        grillaUsuarios.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        grillaUsuarios.getTableHeader().setBackground(C_CABECERA_TBL);
        grillaUsuarios.getTableHeader().setForeground(C_TEXTO_SUAVE);
        grillaUsuarios.getTableHeader().setPreferredSize(new Dimension(0, 42));
        grillaUsuarios.getTableHeader().setBorder(BorderFactory.createMatteBorder(1, 0, 2, 0, C_BORDE));
        grillaUsuarios.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer centerR = crearRenderer(SwingConstants.CENTER);
        DefaultTableCellRenderer leftR   = crearRenderer(SwingConstants.LEFT);

        grillaUsuarios.getColumnModel().getColumn(0).setCellRenderer(centerR);
        grillaUsuarios.getColumnModel().getColumn(1).setCellRenderer(leftR);
        grillaUsuarios.getColumnModel().getColumn(2).setCellRenderer(centerR);
        grillaUsuarios.getColumnModel().getColumn(3).setCellRenderer(centerR);

        // Ajuste de tabla más delgada
        grillaUsuarios.getColumnModel().getColumn(0).setPreferredWidth(60);
        grillaUsuarios.getColumnModel().getColumn(0).setMaxWidth(80);
        grillaUsuarios.getColumnModel().getColumn(1).setPreferredWidth(200);
        grillaUsuarios.getColumnModel().getColumn(2).setPreferredWidth(140);
        grillaUsuarios.getColumnModel().getColumn(3).setPreferredWidth(90);
        grillaUsuarios.getColumnModel().getColumn(3).setMaxWidth(110);

        jScrollPane1.setBorder(BorderFactory.createEmptyBorder());
        jScrollPane1.getViewport().setBackground(C_BLANCO);

        // ── TARJETA DE INFORMACIÓN DE ROLES (Ampliada y Mejorada) ───
        pnlInfoRoles.setBackground(C_BLANCO);
        pnlInfoRoles.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(C_BORDE, 1, true),
            new EmptyBorder(25, 30, 25, 30) // Más alto y espacioso
        ));

        // Formato HTML avanzado para la descripción de los roles
        String textoRoles = "<html><div style='padding: 5px;'>"
            + "<h3 style='color:#1E6EB4; margin-top:0; margin-bottom:15px; font-family:Segoe UI; font-size:14px; letter-spacing: 1px;'>PERMISOS DE USUARIOS</h3>"
            + "<p style='font-family:Segoe UI; font-size:12px; color:#3C4B5A; line-height: 1.6;'>"
            + "<b style='color:#1E6EB4; text-decoration:underline;'>ADMIN:</b> Acceso a todas las funciones del sistema.<br><br>"
            + "<b style='color:#1E6EB4; text-decoration:underline;'>BIOQUÍMICO:</b> Acceso a: Carga/edición de pacientes. Crear/editar Análisis. Impresión de estudios. Gestión de médicos y obras sociales. Modificar parámetros de referencia y unidades en NBU.<br><br>"
            + "<b style='color:#1E6EB4; text-decoration:underline;'>TÉCNICO:</b> Acceso a: Carga/edición de pacientes. Crear/editar análisis. Listar análisis. Imprimir estudios.<br><br>"
            + "<b style='color:#1E6EB4; text-decoration:underline;'>LECTOR:</b> Acceso a: Listado de pacientes. Listado de análisis. Impresión de estudios."
            + "</p></div></html>";
        lblInfoRoles.setText(textoRoles);
    }

    private void estilizarCampo(javax.swing.JTextField tf) {
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setBackground(C_CAMPO);
        tf.setForeground(C_TEXTO_FUERTE);
        tf.setCaretColor(C_AZUL_MEDIO);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDE),
            new EmptyBorder(8, 10, 8, 10)
        ));
        tf.setPreferredSize(new Dimension(0, 40));
        
        tf.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent evt) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, C_AZUL_MEDIO),
                    new EmptyBorder(8, 10, 8, 10)
                ));
                tf.setBackground(C_BLANCO);
            }
            @Override public void focusLost(java.awt.event.FocusEvent evt) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDE),
                    new EmptyBorder(8, 10, 8, 10)
                ));
                tf.setBackground(C_CAMPO);
            }
        });
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
        btn.setText("  "); // Espacio para separar del icono
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

    private DefaultTableCellRenderer crearRenderer(int alineacion) {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setHorizontalAlignment(alineacion);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                if (!sel) {
                    setBackground(row % 2 == 0 ? C_BLANCO : C_FILA_PAR);
                    setForeground(C_TEXTO_FUERTE);
                }
                return this;
            }
        };
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
    private void configurarNavegacionEnter() {
        KeyAdapter enterAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if      (e.getSource() == txtUsername) txtPassword.requestFocus();
                    else if (e.getSource() == txtPassword) cbxRol.requestFocus();
                    else if (e.getSource() == cbxRol)      btnGuardar.doClick();
                }
            }
        };
        txtUsername.addKeyListener(enterAdapter);
        txtPassword.addKeyListener(enterAdapter);
        cbxRol.addKeyListener(enterAdapter);
    }

    @Override
    public void cargarUsuarios(List<Usuario> lista) {
        DefaultTableModel m = (DefaultTableModel) grillaUsuarios.getModel();
        m.setRowCount(0);
        if (lista != null) {
            for (Usuario u : lista) {
                m.addRow(new Object[]{
                    u.getIdUsuario(),
                    u.getUsername(),
                    u.getRol(),
                    u.isActivo() ? "SÍ" : "NO"
                });
            }
        }
    }

    @Override public void ejecutar()      { setVisible(true); } 
    @Override public String getUsername() { return txtUsername.getText().trim(); }
    @Override public String getPassword() { return new String(txtPassword.getPassword()); }
    @Override public String getRol()      { return cbxRol.getSelectedItem().toString(); }

    @Override
    public int getUsuarioSeleccionadoId() {
        int fila = grillaUsuarios.getSelectedRow();
        return (fila != -1) ? (int) grillaUsuarios.getValueAt(fila, 0) : -1;
    }

    @Override
    public void setControlador(Controlador control) {
        this.controlador = control;
        btnGuardar.addActionListener(control);
        btnGuardar.setActionCommand(BTN_GUARDAR);
        btnEliminar.addActionListener(control);
        btnEliminar.setActionCommand(BTN_ELIMINAR);
        btnVolver.addActionListener(control);
        btnVolver.setActionCommand(BTN_VOLVER);
    }

    @Override
    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }

    // ══════════════════════════════════════════════════════════════════
    //  initComponents 
    // ══════════════════════════════════════════════════════════════════
    @SuppressWarnings("unchecked")
    private void initComponents() {

        pnlHeader          = new JPanel();
        lblTituloHeader    = new JLabel("GESTIÓN DE USUARIOS Y ACCESOS");
        btnVolver          = new javax.swing.JButton();

        pnlCuerpo          = new JPanel();
        pnlFormulario      = new JPanel();
        
        pnlDerechoWrapper  = new JPanel();
        pnlTabla           = new JPanel();
        lblTituloTabla     = new JLabel("Usuarios Registrados");
        
        pnlInfoRoles       = new JPanel();
        lblInfoRoles       = new JLabel();

        lblUsername        = new JLabel("NOMBRE DE USUARIO");
        lblPassword        = new JLabel("CONTRASEÑA");
        lblRol             = new JLabel("NIVEL DE ACCESO");

        txtUsername        = new javax.swing.JTextField();
        txtPassword        = new javax.swing.JPasswordField();
        cbxRol             = new javax.swing.JComboBox<>();
        grillaUsuarios     = new JTable();
        jScrollPane1       = new javax.swing.JScrollPane();

        pnlBotonesEdicion  = new JPanel();
        btnGuardar         = new javax.swing.JButton();
        btnEliminar        = new javax.swing.JButton();

        cbxRol.setModel(new javax.swing.DefaultComboBoxModel<>(
            new String[]{"ADMIN", "BIOQUIMICO", "TECNICO", "LECTOR"}
        ));

        grillaUsuarios.setModel(new DefaultTableModel(
            new Object[][]{},
            new String[]{"ID", "USUARIO", "ROL", "ACTIVO"}
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
        jScrollPane1.setViewportView(grillaUsuarios);

        // ── ROOT ─────────────────────────────────────────────────────
        setLayout(new BorderLayout());

        // ── HEADER ───────────────────────────────────────────────────
        pnlHeader.setLayout(new BorderLayout());
        
        // 1. Panel Izquierdo: Agrupamos el botón Volver y el Título
        JPanel pnlIzqHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlIzqHeader.setOpaque(false);
        pnlIzqHeader.add(btnVolver);
        
        // 2. Alineamos el texto a la izquierda y le damos 10px de separación de la flecha
        lblTituloHeader.setHorizontalAlignment(SwingConstants.LEFT);
        lblTituloHeader.setBorder(new EmptyBorder(0, 10, 0, 0)); 
        pnlIzqHeader.add(lblTituloHeader);
        
        // 3. Añadimos el bloque completo a la izquierda del encabezado
        pnlHeader.add(pnlIzqHeader, BorderLayout.WEST);
        
        add(pnlHeader, BorderLayout.NORTH);
        // ── CUERPO ───────────────────────────────────────────────────
        pnlCuerpo.setBackground(C_FONDO);
        pnlCuerpo.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.BOTH;
        gc.weighty = 1.0;

        // Formulario (Izquierda) -> MÁS ANCHO
        gc.gridx = 0; gc.gridy = 0; gc.weightx = 0;
        pnlFormulario.setPreferredSize(new Dimension(440, 0)); // Ampliado para mejor UX
        pnlFormulario.setMinimumSize(new Dimension(400, 0));
        pnlCuerpo.add(pnlFormulario, gc);

        // Tabla + Roles (Derecha) -> REDUCIDO EN ANCHO RELATIVO
        gc.gridx = 1; gc.gridy = 0; gc.weightx = 1.0;
        pnlCuerpo.add(pnlDerechoWrapper, gc);

        // Padding general del cuerpo
        JPanel wrapperCuerpo = new JPanel(new BorderLayout());
        wrapperCuerpo.setOpaque(false);
        wrapperCuerpo.setBorder(new EmptyBorder(25, 25, 25, 25));
        wrapperCuerpo.add(pnlCuerpo, BorderLayout.CENTER);

        add(wrapperCuerpo, BorderLayout.CENTER);

        // ── FORMULARIO: Layout ───────────────────────────────────────
        pnlFormulario.setLayout(new GridBagLayout());
        GridBagConstraints gf = new GridBagConstraints();
        gf.fill = GridBagConstraints.HORIZONTAL;
        gf.weightx = 1.0;
        int r = 0;

        gf.gridx = 0;
        
        gf.gridy = r++; gf.insets = new Insets(0, 0, 6, 0);  pnlFormulario.add(lblUsername, gf);
        gf.gridy = r++; gf.insets = new Insets(0, 0, 25, 0); pnlFormulario.add(txtUsername, gf);

        gf.gridy = r++; gf.insets = new Insets(0, 0, 6, 0);  pnlFormulario.add(lblPassword, gf);
        gf.gridy = r++; gf.insets = new Insets(0, 0, 25, 0); pnlFormulario.add(txtPassword, gf);

        gf.gridy = r++; gf.insets = new Insets(0, 0, 6, 0);  pnlFormulario.add(lblRol, gf);
        gf.gridy = r++; gf.insets = new Insets(0, 0, 35, 0); pnlFormulario.add(cbxRol, gf);

        // Botones guardar y editar
        pnlBotonesEdicion.setOpaque(false);
        pnlBotonesEdicion.setLayout(new java.awt.GridLayout(1, 2, 10, 0));
        pnlBotonesEdicion.add(btnEliminar);
        pnlBotonesEdicion.add(btnGuardar);
        
        gf.gridy = r++; gf.weighty = 0; gf.fill = GridBagConstraints.HORIZONTAL;
        gf.insets = new Insets(0,0,0,0); 
        pnlFormulario.add(pnlBotonesEdicion, gf);

        // Spacer para empujar hacia arriba
        gf.gridy = r++; gf.weighty = 1.0;
        gf.fill = GridBagConstraints.VERTICAL;
        pnlFormulario.add(new JPanel() {{ setOpaque(false); }}, gf);

        // ── PANEL DERECHO: Tabla (Arriba) y Roles (Abajo) ────────────
        pnlDerechoWrapper.setLayout(new BorderLayout(0, 20)); // Separación de 20px vertical
        
        pnlTabla.setLayout(new BorderLayout());
        pnlTabla.add(lblTituloTabla, BorderLayout.NORTH);
        pnlTabla.add(jScrollPane1, BorderLayout.CENTER);
        
        pnlInfoRoles.setLayout(new BorderLayout());
        pnlInfoRoles.add(lblInfoRoles, BorderLayout.CENTER);

        pnlDerechoWrapper.add(pnlTabla, BorderLayout.CENTER);
        pnlDerechoWrapper.add(pnlInfoRoles, BorderLayout.SOUTH);
    }

    // ── Variables ────────────────────────────────────────────────────
    private JPanel                          pnlHeader;
    private JLabel                          lblTituloHeader;
    private JPanel                          pnlCuerpo;
    private JPanel                          pnlFormulario;
    private JPanel                          pnlDerechoWrapper;
    private JPanel                          pnlTabla;
    private JLabel                          lblTituloTabla;
    private JPanel                          pnlInfoRoles;
    private JLabel                          lblInfoRoles;
    private JLabel                          lblUsername;
    private JLabel                          lblPassword;
    private JLabel                          lblRol;
    private javax.swing.JTextField          txtUsername;
    private javax.swing.JPasswordField      txtPassword;
    private javax.swing.JComboBox<String>   cbxRol;
    private JTable                          grillaUsuarios;
    private javax.swing.JScrollPane         jScrollPane1;
    private JPanel                          pnlBotonesEdicion;
    private javax.swing.JButton             btnGuardar;
    private javax.swing.JButton             btnEliminar;
    private javax.swing.JButton             btnVolver;
}