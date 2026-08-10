package vista.swing;

// @author lucianoalicata

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
import javax.swing.JTable;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import modelo.Usuario;
import presentador.UsuarioPresenter;

public class VistaGestionUsuarios extends JPanel implements IVistaGestionUsuarios {

    private boolean cargandoDatos = false;
    
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
    private final Color C_SELECCION = new Color(220, 235, 250);
    private final Color C_CARD_BG = new Color(248, 250, 252);
    private final Color C_ROL_ADMIN = new Color(30, 110, 180);
    private final Color C_ROL_BIO = new Color(35, 160, 115);
    private final Color C_ROL_TEC = new Color(220, 120, 50);
    private final Color C_ROL_LEC = new Color(100, 115, 130);

    public VistaGestionUsuarios() {
        initComponents();
        aplicarEstiloProfesional();
        configurarNavegacionEnter();
        setMinimumSize(new Dimension(900, 600));
    }
    
    @Override
    public void setPresenter(UsuarioPresenter presenter) {
        
        limpiarListeners(btnGuardar);
        limpiarListeners(btnEliminar);
        limpiarListeners(btnVolver);
        
        btnGuardar.addActionListener(e -> presenter.onGuardar());
        btnEliminar.addActionListener(e -> presenter.onEliminar());
        btnVolver.addActionListener(e -> presenter.onVolver());
        
        grillaUsuarios.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && !cargandoDatos && grillaUsuarios.getSelectedRow() != -1) {
                presenter.onSeleccionarUsuario();
            }
        });
    }

    private void limpiarListeners(javax.swing.JButton btn) {
        for (java.awt.event.ActionListener al : btn.getActionListeners()) {
            btn.removeActionListener(al);
        }
    }

    @Override
    public void cargarUsuarios(List<Usuario> lista) {
        cargandoDatos = true;
        
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
        
        grillaUsuarios.clearSelection();
        cargandoDatos = false;
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
        txtUsername.requestFocus();
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

    private void aplicarEstiloProfesional() {
        setBackground(C_FONDO);
        setLayout(new BorderLayout());

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

        add(pnlHeader, BorderLayout.NORTH);

        pnlContenedorBlanco = new JPanel(new BorderLayout());
        pnlContenedorBlanco.setBackground(C_BLANCO);
        pnlContenedorBlanco.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 1, 1, 1, C_BORDE),
            new EmptyBorder(10, 12, 10, 12)
        ));

        pnlCuerpo.setBackground(C_BLANCO);
        pnlCuerpo.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.BOTH;
        gc.weighty = 1.0;
        gc.insets = new Insets(0, 0, 0, 0);

        pnlFormulario.setBackground(C_BLANCO);
        pnlFormulario.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(8, 10, 8, 10),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDE, 1, true),
                new EmptyBorder(16, 20, 16, 20)
            )
        ));

        Font fontLabel = new Font("Segoe UI", Font.BOLD, 11);
        for (JLabel lbl : new JLabel[]{lblUsername, lblPassword, lblRol}) {
            lbl.setFont(fontLabel);
            lbl.setForeground(C_TEXTO_SUAVE);
        }

        estilizarCampo(txtUsername);
        estilizarCampoPassword(txtPassword);

        cbxRol.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbxRol.setBackground(C_CAMPO);
        cbxRol.setForeground(C_TEXTO_FUERTE);
        cbxRol.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDE),
            new EmptyBorder(6, 10, 6, 10)
        ));

        configurarBoton(btnGuardar, C_VERDE, "GUARDAR", 160, 38);
        configurarBoton(btnEliminar, C_ROJO, "ELIMINAR", 160, 38);
        configurarBotonRetroceso(btnVolver);

        pnlFormulario.setLayout(new GridBagLayout());
        GridBagConstraints gf = new GridBagConstraints();
        gf.fill = GridBagConstraints.HORIZONTAL;
        gf.weightx = 1.0;
        gf.gridx = 0;
        int r = 0;

        java.util.function.BiFunction<JLabel, Component, JPanel> crearCampo = (lbl, cmp) -> {
            JPanel p = new JPanel(new BorderLayout(0, 4));
            p.setOpaque(false);
            p.add(lbl, BorderLayout.NORTH);
            p.add(cmp, BorderLayout.CENTER);
            return p;
        };

        gf.insets = new Insets(0, 0, 16, 0);

        gf.gridy = r++;
        pnlFormulario.add(crearCampo.apply(lblUsername, txtUsername), gf);
        
        gf.gridy = r++;
        pnlFormulario.add(crearCampo.apply(lblPassword, txtPassword), gf);
        
        gf.gridy = r++;
        pnlFormulario.add(crearCampo.apply(lblRol, cbxRol), gf);

        pnlBotonesEdicion.setOpaque(false);
        pnlBotonesEdicion.setLayout(new GridLayout(1, 2, 10, 0));
        pnlBotonesEdicion.add(btnEliminar);
        pnlBotonesEdicion.add(btnGuardar);

        gf.gridy = r++;
        gf.insets = new Insets(12, 0, 0, 0);
        pnlFormulario.add(pnlBotonesEdicion, gf);

        gf.gridy = r++;
        gf.weighty = 1.0;
        gf.fill = GridBagConstraints.VERTICAL;
        gf.insets = new Insets(0, 0, 0, 0);
        pnlFormulario.add(new JPanel() {{ setOpaque(false); }}, gf);

        pnlDerechoWrapper.setBackground(C_BLANCO);
        pnlDerechoWrapper.setLayout(new BorderLayout(0, 12));

        pnlTablaWrapper.setBackground(C_BLANCO);
        pnlTablaWrapper.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(0, 0, 0, 0),
            BorderFactory.createLineBorder(C_BORDE, 1, true)
        ));

        lblTituloTabla.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTituloTabla.setForeground(C_TEXTO_FUERTE);
        lblTituloTabla.setBorder(new EmptyBorder(10, 14, 8, 14));

        grillaUsuarios.setRowHeight(34);
        grillaUsuarios.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        grillaUsuarios.setGridColor(new Color(235, 240, 245));
        grillaUsuarios.setShowHorizontalLines(true);
        grillaUsuarios.setShowVerticalLines(false);
        grillaUsuarios.setSelectionBackground(C_SELECCION);
        grillaUsuarios.setSelectionForeground(C_TEXTO_FUERTE);
        grillaUsuarios.setIntercellSpacing(new Dimension(0, 0));
        grillaUsuarios.setFillsViewportHeight(true);
        grillaUsuarios.setBorder(BorderFactory.createEmptyBorder());

        grillaUsuarios.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        grillaUsuarios.getTableHeader().setBackground(C_CABECERA_TBL);
        grillaUsuarios.getTableHeader().setForeground(C_TEXTO_SUAVE);
        grillaUsuarios.getTableHeader().setPreferredSize(new Dimension(0, 34));
        grillaUsuarios.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDE));
        grillaUsuarios.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer centerR = crearRenderer(SwingConstants.CENTER);
        DefaultTableCellRenderer leftR = crearRenderer(SwingConstants.LEFT);

        grillaUsuarios.getColumnModel().getColumn(0).setCellRenderer(centerR);
        grillaUsuarios.getColumnModel().getColumn(1).setCellRenderer(leftR);
        grillaUsuarios.getColumnModel().getColumn(2).setCellRenderer(centerR);
        grillaUsuarios.getColumnModel().getColumn(3).setCellRenderer(centerR);

        grillaUsuarios.getColumnModel().getColumn(0).setPreferredWidth(50);
        grillaUsuarios.getColumnModel().getColumn(0).setMaxWidth(60);
        grillaUsuarios.getColumnModel().getColumn(1).setPreferredWidth(180);
        grillaUsuarios.getColumnModel().getColumn(2).setPreferredWidth(120);
        grillaUsuarios.getColumnModel().getColumn(3).setPreferredWidth(60);
        grillaUsuarios.getColumnModel().getColumn(3).setMaxWidth(70);

        jScrollPane1.setBorder(BorderFactory.createEmptyBorder());
        jScrollPane1.getViewport().setBackground(C_BLANCO);

        pnlTablaWrapper.setLayout(new BorderLayout());
        pnlTablaWrapper.add(lblTituloTabla, BorderLayout.NORTH);
        pnlTablaWrapper.add(jScrollPane1, BorderLayout.CENTER);

        pnlInfoRoles.setBackground(C_BLANCO);
        pnlInfoRoles.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(C_BORDE, 1, true),
            new EmptyBorder(12, 14, 12, 14)
        ));
        pnlInfoRoles.setLayout(new BorderLayout(0, 10));

        JPanel headerRoles = new JPanel(new BorderLayout());
        headerRoles.setOpaque(false);
        JLabel lblRolesTitle = new JLabel("PERMISOS Y ACCESOS");
        lblRolesTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblRolesTitle.setForeground(C_AZUL_MEDIO);
        JSeparator separator = new JSeparator();
        separator.setForeground(C_BORDE);
        headerRoles.add(lblRolesTitle, BorderLayout.WEST);
        headerRoles.add(separator, BorderLayout.CENTER);
        pnlInfoRoles.add(headerRoles, BorderLayout.NORTH);

        JPanel gridRoles = new JPanel(new GridBagLayout());
        gridRoles.setOpaque(false);
        GridBagConstraints gr = new GridBagConstraints();
        gr.fill = GridBagConstraints.BOTH;
        gr.weightx = 1.0;
        gr.weighty = 1.0;
        gr.insets = new Insets(0, 0, 8, 10);
        
        gr.gridx = 0; gr.gridy = 0;
        gridRoles.add(crearCardRol("ADMIN", "Control total", C_ROL_ADMIN), gr);
        gr.gridx = 1; gr.gridy = 0;
        gridRoles.add(crearCardRol("BIOQUÍMICO", "Gestión completa", C_ROL_BIO), gr);
        gr.gridx = 0; gr.gridy = 1;
        gridRoles.add(crearCardRol("TÉCNICO", "Operaciones", C_ROL_TEC), gr);
        gr.gridx = 1; gr.gridy = 1;
        gridRoles.add(crearCardRol("LECTOR", "Consulta", C_ROL_LEC), gr);
        
        pnlInfoRoles.add(gridRoles, BorderLayout.CENTER);
        
        pnlDerechoWrapper.add(pnlTablaWrapper, BorderLayout.CENTER);
        pnlDerechoWrapper.add(pnlInfoRoles, BorderLayout.SOUTH);

        gc.gridx = 0;
        gc.weightx = 0.35;
        gc.insets = new Insets(0, 0, 0, 10);
        pnlCuerpo.add(pnlFormulario, gc);

        gc.gridx = 1;
        gc.weightx = 0.65;
        gc.insets = new Insets(0, 10, 0, 0);
        pnlCuerpo.add(pnlDerechoWrapper, gc);

        pnlContenedorBlanco.add(pnlCuerpo, BorderLayout.CENTER);
        add(pnlContenedorBlanco, BorderLayout.CENTER);

        pnlFooter.setBackground(C_FONDO);
        pnlFooter.setBorder(new EmptyBorder(6, 12, 10, 12));
        pnlFooter.setLayout(new BorderLayout());
        add(pnlFooter, BorderLayout.SOUTH);
    }
    
    private JPanel crearCardRol(String titulo, String descripcion, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(0, 4));
        card.setBackground(C_CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(225, 230, 240), 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitulo.setForeground(color);
        
        JLabel lblDesc = new JLabel(descripcion);
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblDesc.setForeground(C_TEXTO_SUAVE);
        
        card.add(lblTitulo, BorderLayout.NORTH);
        card.add(lblDesc, BorderLayout.CENTER);
        
        return card;
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

        tf.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, C_AZUL_MEDIO),
                    new EmptyBorder(6, 10, 6, 10)
                ));
                tf.setBackground(C_BLANCO);
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDE),
                    new EmptyBorder(6, 10, 6, 10)
                ));
                tf.setBackground(C_CAMPO);
            }
        });
    }

    private void estilizarCampoPassword(javax.swing.JPasswordField pf) {
        pf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pf.setBackground(C_CAMPO);
        pf.setForeground(C_TEXTO_FUERTE);
        pf.setCaretColor(C_AZUL_MEDIO);
        pf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDE),
            new EmptyBorder(6, 10, 6, 10)
        ));

        pf.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                pf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, C_AZUL_MEDIO),
                    new EmptyBorder(6, 10, 6, 10)
                ));
                pf.setBackground(C_BLANCO);
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                pf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDE),
                    new EmptyBorder(6, 10, 6, 10)
                ));
                pf.setBackground(C_CAMPO);
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
                } else {
                    setBackground(C_SELECCION);
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

        pnlHeader.setLayout(new BorderLayout());
        JPanel pnlIzqHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlIzqHeader.setOpaque(false);
        pnlIzqHeader.add(btnVolver);
        lblTituloHeader.setBorder(new EmptyBorder(0, 10, 0, 0));
        pnlIzqHeader.add(lblTituloHeader);
        pnlHeader.add(pnlIzqHeader, BorderLayout.WEST);
        add(pnlHeader, BorderLayout.NORTH);
    }

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