package vista.swing;

import vista.interfaces.IVistaGestionUsuarios;
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
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import modelo.Usuario;
import presentador.UsuarioPresenter;

public class VistaGestionUsuarios extends JPanel implements IVistaGestionUsuarios {

    private UsuarioPresenter presenter;
    
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
    private final Color C_CARD_BG = new Color(248, 250, 252);
    private final Color C_ROL_ADMIN = new Color(30, 110, 180);
    private final Color C_ROL_BIO = new Color(35, 160, 115);
    private final Color C_ROL_TEC = new Color(220, 120, 50);
    private final Color C_ROL_LEC = new Color(100, 115, 130);

    public VistaGestionUsuarios() {
        initComponents();
        aplicarEstilo();
        configurarNavegacionEnter();
    }

    // ══════════════════════════════════════════════════════════════════
    //  INTERFAZ IVistaGestionUsuarios - MÉTODOS MVP
    // ══════════════════════════════════════════════════════════════════

    @Override
    public void setPresenter(UsuarioPresenter presenter) {
        this.presenter = presenter;
        
        // 1. PURGA DE EVENTOS EN BOTONES (La cura para los carteles duplicados)
        limpiarListeners(btnGuardar);
        limpiarListeners(btnEliminar);
        limpiarListeners(btnVolver);
        
        // 2. CONEXIÓN LIMPIA
        btnGuardar.addActionListener(e -> presenter.onGuardar());
        btnEliminar.addActionListener(e -> presenter.onEliminar());
        btnVolver.addActionListener(e -> presenter.onVolver());
        
        // 3. PURGA DE EVENTOS EN LA TABLA
        javax.swing.DefaultListSelectionModel modeloSeleccion = 
                (javax.swing.DefaultListSelectionModel) grillaUsuarios.getSelectionModel();

        for (javax.swing.event.ListSelectionListener lsl : modeloSeleccion.getListSelectionListeners()) {
            modeloSeleccion.removeListSelectionListener(lsl);
        }

        // 4. CONEXIÓN DE LA TABLA
        modeloSeleccion.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && grillaUsuarios.getSelectedRow() != -1) {
                presenter.onSeleccionarUsuario();
            }
        });
    }

    // Método auxiliar obligatorio para purgar la memoria
    private void limpiarListeners(javax.swing.JButton btn) {
        for (java.awt.event.ActionListener al : btn.getActionListeners()) {
            btn.removeActionListener(al);
        }
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

    @Override public void ejecutar() { setVisible(true); }
    @Override public String getUsername() { return txtUsername.getText().trim(); }
    @Override public String getPassword() { return new String(txtPassword.getPassword()); }
    @Override public String getRol() { return cbxRol.getSelectedItem().toString(); }

    @Override
    public int getUsuarioSeleccionadoId() {
        int fila = grillaUsuarios.getSelectedRow();
        return (fila != -1) ? (int) grillaUsuarios.getValueAt(fila, 0) : -1;
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
    public void limpiarCampos() {
        txtUsername.setText("");
        txtPassword.setText("");
        cbxRol.setSelectedIndex(0);
        grillaUsuarios.clearSelection();
    }

    @Override
    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }

    public void setDatosFormulario(String username, String rol) {
        txtUsername.setText(username);
        txtPassword.setText("");
        cbxRol.setSelectedItem(rol);
    }

    // ══════════════════════════════════════════════════════════════════
    //  ESTILO Y UX
    // ══════════════════════════════════════════════════════════════════
    private void aplicarEstilo() {
        setBackground(C_FONDO);

        // ========== HEADER ==========
        pnlHeader.setBackground(C_NAVY);
        pnlHeader.setBorder(new EmptyBorder(14, 28, 14, 28));
        lblTituloHeader.setForeground(C_BLANCO);
        lblTituloHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));

        // ========== CONTENEDOR PRINCIPAL BLANCO ==========
        pnlContenedorBlanco.setBackground(C_BLANCO);
        pnlContenedorBlanco.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 1, 1, 1, C_BORDE),
            new EmptyBorder(24, 28, 24, 28)
        ));
        pnlContenedorBlanco.setLayout(new BorderLayout());

        // ========== CUERPO PRINCIPAL ==========
        pnlCuerpo.setBackground(C_BLANCO);
        pnlCuerpo.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.BOTH;
        gc.weighty = 1.0;
        gc.insets = new Insets(0, 0, 0, 0);

        // Columna izquierda: formulario MÁS ANCHO (530px)
        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = 0;
        pnlFormulario.setPreferredSize(new Dimension(530, 0));
        pnlFormulario.setMinimumSize(new Dimension(460, 0));
        pnlCuerpo.add(pnlFormulario, gc);

        // Columna derecha: tabla + permisos
        gc.gridx = 1;
        gc.gridy = 0;
        gc.weightx = 1.0;
        pnlCuerpo.add(pnlDerechoWrapper, gc);

        pnlContenedorBlanco.add(pnlCuerpo, BorderLayout.CENTER);
        
        // ========== FOOTER ==========
        pnlFooter.setBackground(C_FONDO);
        pnlFooter.setBorder(new EmptyBorder(10, 16, 14, 16));
        pnlFooter.setLayout(new BorderLayout());
        
        JPanel pnlAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        pnlAcciones.setOpaque(false);
        pnlFooter.add(pnlAcciones, BorderLayout.EAST);
        
        add(pnlContenedorBlanco, BorderLayout.CENTER);
        add(pnlFooter, BorderLayout.SOUTH);

        // ========== ESTILO FORMULARIO (más ancho) ==========
        pnlFormulario.setBackground(C_BLANCO);
        pnlFormulario.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(16, 16, 16, 16),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDE, 1, true),
                new EmptyBorder(28, 32, 28, 32)
            )
        ));

        Font fontLabel = new Font("Segoe UI", Font.BOLD, 12);
        for (JLabel lbl : new JLabel[]{lblUsername, lblPassword, lblRol}) {
            lbl.setFont(fontLabel);
            lbl.setForeground(C_TEXTO_SUAVE);
        }

        estilizarCampo(txtUsername, true);
        estilizarCampo(txtPassword, true);

        cbxRol.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbxRol.setBackground(C_CAMPO);
        cbxRol.setForeground(C_TEXTO_FUERTE);
        cbxRol.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDE),
            new EmptyBorder(8, 12, 8, 12)
        ));
        cbxRol.setPreferredSize(new Dimension(0, 42));

        configurarBoton(btnGuardar, C_VERDE, "GUARDAR", 190, 46);
        configurarBoton(btnEliminar, C_ROJO, "ELIMINAR", 190, 46);
        configurarBotonRetroceso(btnVolver);

        // ========== PANEL DERECHO ==========
        pnlDerechoWrapper.setBackground(C_BLANCO);
        pnlDerechoWrapper.setLayout(new BorderLayout(0, 20));

        // ========== TABLA WRAPPER (más compacta) ==========
        pnlTablaWrapper.setBackground(C_BLANCO);
        pnlTablaWrapper.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(0, 0, 0, 0),
            BorderFactory.createLineBorder(C_BORDE, 1, true)
        ));

        lblTituloTabla.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTituloTabla.setForeground(C_TEXTO_FUERTE);
        lblTituloTabla.setBorder(new EmptyBorder(12, 16, 10, 16));

        // Tabla más compacta
        grillaUsuarios.setRowHeight(34);
        grillaUsuarios.setFont(new Font("Segoe UI", Font.PLAIN, 12));
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
        grillaUsuarios.getTableHeader().setPreferredSize(new Dimension(0, 38));
        grillaUsuarios.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDE));
        grillaUsuarios.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer centerR = crearRenderer(SwingConstants.CENTER);
        DefaultTableCellRenderer leftR = crearRenderer(SwingConstants.LEFT);

        grillaUsuarios.getColumnModel().getColumn(0).setCellRenderer(centerR);
        grillaUsuarios.getColumnModel().getColumn(1).setCellRenderer(leftR);
        grillaUsuarios.getColumnModel().getColumn(2).setCellRenderer(centerR);
        grillaUsuarios.getColumnModel().getColumn(3).setCellRenderer(centerR);

        // Anchos de columna más compactos
        grillaUsuarios.getColumnModel().getColumn(0).setPreferredWidth(45);
        grillaUsuarios.getColumnModel().getColumn(0).setMaxWidth(55);
        grillaUsuarios.getColumnModel().getColumn(1).setPreferredWidth(180);
        grillaUsuarios.getColumnModel().getColumn(2).setPreferredWidth(130);
        grillaUsuarios.getColumnModel().getColumn(3).setPreferredWidth(60);
        grillaUsuarios.getColumnModel().getColumn(3).setMaxWidth(75);

        jScrollPane1.setBorder(BorderFactory.createEmptyBorder());
        jScrollPane1.getViewport().setBackground(C_BLANCO);

        pnlTablaWrapper.setLayout(new BorderLayout());
        pnlTablaWrapper.add(lblTituloTabla, BorderLayout.NORTH);
        pnlTablaWrapper.add(jScrollPane1, BorderLayout.CENTER);

        // ========== PANEL DE PERMISOS (más grande) ==========
        pnlInfoRoles.setBackground(C_BLANCO);
        pnlInfoRoles.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(C_BORDE, 1, true),
            new EmptyBorder(24, 28, 24, 28)
        ));
        pnlInfoRoles.setLayout(new BorderLayout(0, 20));

        // Título
        JPanel headerRoles = new JPanel(new BorderLayout());
        headerRoles.setOpaque(false);
        JLabel lblRolesTitle = new JLabel("PERMISOS Y ACCESOS DEL SISTEMA");
        lblRolesTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblRolesTitle.setForeground(C_AZUL_MEDIO);
        JSeparator separator = new JSeparator();
        separator.setForeground(C_BORDE);
        headerRoles.add(lblRolesTitle, BorderLayout.WEST);
        headerRoles.add(separator, BorderLayout.CENTER);
        headerRoles.setBorder(new EmptyBorder(0, 0, 12, 0));
        pnlInfoRoles.add(headerRoles, BorderLayout.NORTH);

        // Grid de roles 2x2 más espaciado
        JPanel gridRoles = new JPanel(new GridBagLayout());
        gridRoles.setOpaque(false);
        GridBagConstraints gr = new GridBagConstraints();
        gr.fill = GridBagConstraints.BOTH;
        gr.weightx = 1.0;
        gr.weighty = 1.0;
        gr.insets = new Insets(0, 0, 20, 24);
        
        gr.gridx = 0; gr.gridy = 0;
        gridRoles.add(crearCardRol("ADMINISTRADOR", 
            "Control total del sistema\n• Gestión de usuarios\n• Configuración global\n• Auditoría y respaldos", 
            C_ROL_ADMIN), gr);
        gr.gridx = 1; gr.gridy = 0;
        gridRoles.add(crearCardRol("BIOQUÍMICO", 
            "Gestión completa del laboratorio\n• Parámetros de referencia\n• Unidades de medida\n• Validación de resultados", 
            C_ROL_BIO), gr);
        gr.gridx = 0; gr.gridy = 1;
        gridRoles.add(crearCardRol("TÉCNICO", 
            "Operaciones diarias\n• Carga de pacientes\n• Registro de análisis\n• Impresión de estudios", 
            C_ROL_TEC), gr);
        gr.gridx = 1; gr.gridy = 1;
        gridRoles.add(crearCardRol("LECTOR", 
            "Consulta y visualización\n• Listado de pacientes\n• Visualización de análisis\n• Impresión autorizada", 
            C_ROL_LEC), gr);
        
        pnlInfoRoles.add(gridRoles, BorderLayout.CENTER);
        
        // Footer con nota informativa
        JPanel footerRoles = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        footerRoles.setOpaque(false);
        footerRoles.setBorder(new EmptyBorder(16, 0, 8, 0));
        JLabel lblNota = new JLabel("⚠️ Solo usuarios con rol ADMINISTRADOR pueden gestionar usuarios del sistema");
        lblNota.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblNota.setForeground(new Color(200, 120, 30));
        footerRoles.add(lblNota);
        pnlInfoRoles.add(footerRoles, BorderLayout.SOUTH);

        pnlDerechoWrapper.add(pnlTablaWrapper, BorderLayout.CENTER);
        pnlDerechoWrapper.add(pnlInfoRoles, BorderLayout.SOUTH);

        // ========== LAYOUT DEL FORMULARIO (más espaciado) ==========
        pnlFormulario.setLayout(new GridBagLayout());
        GridBagConstraints gf = new GridBagConstraints();
        gf.fill = GridBagConstraints.HORIZONTAL;
        gf.weightx = 1.0;
        gf.gridx = 0;
        int r = 0;

        java.util.function.BiFunction<JLabel, Component, JPanel> crearCampo = (lbl, cmp) -> {
            JPanel p = new JPanel(new BorderLayout(0, 8));
            p.setOpaque(false);
            p.add(lbl, BorderLayout.NORTH);
            p.add(cmp, BorderLayout.CENTER);
            return p;
        };

        gf.insets = new Insets(0, 0, 28, 0);

        gf.gridy = r++;
        pnlFormulario.add(crearCampo.apply(lblUsername, txtUsername), gf);
        
        gf.gridy = r++;
        pnlFormulario.add(crearCampo.apply(lblPassword, txtPassword), gf);
        
        gf.gridy = r++;
        pnlFormulario.add(crearCampo.apply(lblRol, cbxRol), gf);

        // Botones
        pnlBotonesEdicion.setOpaque(false);
        pnlBotonesEdicion.setLayout(new GridLayout(1, 2, 16, 0));
        pnlBotonesEdicion.add(btnEliminar);
        pnlBotonesEdicion.add(btnGuardar);

        gf.gridy = r++;
        gf.insets = new Insets(16, 0, 0, 0);
        pnlFormulario.add(pnlBotonesEdicion, gf);

        // Spacer elástico
        gf.gridy = r++;
        gf.weighty = 1.0;
        gf.fill = GridBagConstraints.VERTICAL;
        gf.insets = new Insets(0, 0, 0, 0);
        pnlFormulario.add(new JPanel() {{ setOpaque(false); }}, gf);
    }
    
    private JPanel crearCardRol(String titulo, String descripcion, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(0, 12));
        card.setBackground(C_CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(225, 230, 240), 1),
            new EmptyBorder(16, 18, 16, 18)
        ));
        
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitulo.setForeground(color);
        
        JLabel lblDesc = new JLabel("<html>" + descripcion.replace("\n", "<br>") + "</html>");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDesc.setForeground(C_TEXTO_SUAVE);
        
        card.add(lblTitulo, BorderLayout.NORTH);
        card.add(lblDesc, BorderLayout.CENTER);
        
        return card;
    }

    private void estilizarCampo(javax.swing.JTextField tf, boolean ancho) {
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setBackground(C_CAMPO);
        tf.setForeground(C_TEXTO_FUERTE);
        tf.setCaretColor(C_AZUL_MEDIO);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDE),
            new EmptyBorder(8, 12, 8, 12)
        ));
        tf.setPreferredSize(new Dimension(0, 42));

        tf.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, C_AZUL_MEDIO),
                    new EmptyBorder(8, 12, 8, 12)
                ));
                tf.setBackground(C_BLANCO);
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDE),
                    new EmptyBorder(8, 12, 8, 12)
                ));
                tf.setBackground(C_CAMPO);
            }
        });
    }

    private void configurarBoton(javax.swing.JButton btn, Color bg, String texto, int w, int h) {
        btn.setText(texto);
        btn.setBackground(bg);
        btn.setForeground(C_BLANCO);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
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
        } catch (Exception e) { }
        return null;
    }

    private void configurarNavegacionEnter() {
        KeyAdapter enterAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (e.getSource() == txtUsername) {
                        txtPassword.requestFocus();
                    } else if (e.getSource() == txtPassword) {
                        cbxRol.requestFocus();
                    } else if (e.getSource() == cbxRol) {
                        btnGuardar.doClick();
                    }
                }
            }
        };
        txtUsername.addKeyListener(enterAdapter);
        txtPassword.addKeyListener(enterAdapter);
        cbxRol.addKeyListener(enterAdapter);
    }

    // ══════════════════════════════════════════════════════════════════
    //  initComponents
    // ══════════════════════════════════════════════════════════════════
    @SuppressWarnings("unchecked")
    private void initComponents() {

        pnlHeader = new JPanel();
        lblTituloHeader = new JLabel("GESTIÓN DE USUARIOS Y ACCESOS");
        btnVolver = new javax.swing.JButton();

        pnlContenedorBlanco = new JPanel();
        pnlCuerpo = new JPanel();
        pnlFormulario = new JPanel();
        pnlDerechoWrapper = new JPanel();
        pnlTablaWrapper = new JPanel();
        lblTituloTabla = new JLabel("Usuarios Registrados");
        pnlInfoRoles = new JPanel();
        pnlFooter = new JPanel();

        lblUsername = new JLabel("NOMBRE DE USUARIO");
        lblPassword = new JLabel("CONTRASEÑA");
        lblRol = new JLabel("NIVEL DE ACCESO");

        txtUsername = new javax.swing.JTextField();
        txtPassword = new javax.swing.JPasswordField();
        cbxRol = new javax.swing.JComboBox<>();
        grillaUsuarios = new JTable();
        jScrollPane1 = new javax.swing.JScrollPane();

        pnlBotonesEdicion = new JPanel();
        btnGuardar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();

        cbxRol.setModel(new javax.swing.DefaultComboBoxModel<>(
            new String[]{"ADMIN", "BIOQUIMICO", "TECNICO", "LECTOR"}
        ));

        grillaUsuarios.setModel(new DefaultTableModel(
            new Object[][]{},
            new String[]{"ID", "USUARIO", "ROL", "ACTIVO"}
        ) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        });
        jScrollPane1.setViewportView(grillaUsuarios);

        setLayout(new BorderLayout());

        // HEADER
        pnlHeader.setLayout(new BorderLayout());
        JPanel pnlIzqHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlIzqHeader.setOpaque(false);
        pnlIzqHeader.add(btnVolver);
        lblTituloHeader.setHorizontalAlignment(SwingConstants.LEFT);
        lblTituloHeader.setBorder(new EmptyBorder(0, 10, 0, 0));
        pnlIzqHeader.add(lblTituloHeader);
        pnlHeader.add(pnlIzqHeader, BorderLayout.WEST);
        add(pnlHeader, BorderLayout.NORTH);
    }

    // ── Variables ────────────────────────────────────────────────────
    private JPanel pnlHeader;
    private JLabel lblTituloHeader;
    private JPanel pnlContenedorBlanco;
    private JPanel pnlCuerpo;
    private JPanel pnlFormulario;
    private JPanel pnlDerechoWrapper;
    private JPanel pnlTablaWrapper;
    private JLabel lblTituloTabla;
    private JPanel pnlInfoRoles;
    private JPanel pnlFooter;
    private JLabel lblUsername;
    private JLabel lblPassword;
    private JLabel lblRol;
    private javax.swing.JTextField txtUsername;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JComboBox<String> cbxRol;
    private JTable grillaUsuarios;
    private javax.swing.JScrollPane jScrollPane1;
    private JPanel pnlBotonesEdicion;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnVolver;
}