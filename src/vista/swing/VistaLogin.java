package vista.swing;

import vista.interfaces.IVistaLogin;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.*;
import presentador.SesionPresenter;

public class VistaLogin extends javax.swing.JDialog implements IVistaLogin {

    private SesionPresenter presenter;

    // ── Paleta BIOTEC Profesional ────────────────────────────────────
    private final Color C_PRIMARIO      = new Color(0, 70, 128);      // Azul institucional profundo
    private final Color C_PRIMARIO_OSCURO = new Color(0, 50, 90);
    private final Color C_ACENTO        = new Color(0, 150, 200);      // Azul vivo para hover
    private final Color C_FONDO         = new Color(240, 245, 250);     // Fondo suave
    private final Color C_BLANCO        = Color.WHITE;
    private final Color C_TEXTO_PRIMARIO = new Color(30, 40, 50);
    private final Color C_TEXTO_SECUNDARIO = new Color(100, 115, 130);
    private final Color C_BORDE_CAMPO    = new Color(210, 225, 235);
    private final Color C_CAMPO          = new Color(248, 250, 252);
    private final Color C_EXITO          = new Color(40, 180, 130);
    private final Color C_SOMBRA         = new Color(0, 0, 0, 30);

    public VistaLogin() {
        super((java.awt.Frame) null, true);
        initComponents();
        aplicarEsteticaProfesional();
        configurarNavegacionTeclado();
        
        setTitle("BIOTEC LIS - Acceso al Sistema");
        setUndecorated(true);
        setSize(new Dimension(480, 580));
        setLocationRelativeTo(null);
        setShape(new RoundRectangle2D.Double(0, 0, 480, 580, 20, 20));
    }

    // ════════════════════════════════════════════════════════════════
    //  INTERFAZ IVistaLogin - MÉTODOS MVP
    // ════════════════════════════════════════════════════════════════
    @Override
    public void setPresenter(SesionPresenter presenter) {
        this.presenter = presenter;

        // Limpiar listeners existentes para evitar duplicados
        for (java.awt.event.ActionListener al : btnIngresar.getActionListeners()) {
            btnIngresar.removeActionListener(al);
        }

        // Conectar el botón al método del presentador
        btnIngresar.addActionListener(e -> presenter.onIngresar());
    }

    @Override 
    public void ejecutar() { 
        setVisible(true); 
    }
    
    @Override 
    public String getUsuario() { 
        return txtUsuario.getText().trim(); 
    }
    
    @Override 
    public String getClave() { 
        return new String(txtClave.getPassword()); 
    }
    
    @Override 
    public void mostrarMensaje(String mensaje) { 
        JOptionPane.showMessageDialog(this, mensaje); 
    }
    
    @Override
    public void limpiarCampos() {
        txtUsuario.setText("");
        txtClave.setText("");
        txtUsuario.requestFocus();
    }
    
    @Override
    public void cerrarPantalla() {
        this.dispose();
    }
    
    @Override
    public void limpiarFocos() {
        this.requestFocusInWindow();
    }

    @Override
    public int confirmarAccion(String mensaje, String titulo) {
        return JOptionPane.showConfirmDialog(this, mensaje, titulo, JOptionPane.YES_NO_OPTION);
    }

    // ════════════════════════════════════════════════════════════════
    //  ESTÉTICA Y UX - Diseño Profesional Moderno
    // ════════════════════════════════════════════════════════════════
    private void aplicarEsteticaProfesional() {
        getContentPane().setBackground(C_FONDO);
        
        // Panel principal con sombra y bordes redondeados
        pnlLoginCard.setOpaque(true);
        pnlLoginCard.setBackground(C_BLANCO);
        pnlLoginCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 235, 240), 1),
            new EmptyBorder(35, 42, 35, 42)
        ));
        
        // Sombra para el panel
        pnlLoginCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 225, 230), 1),
            new EmptyBorder(35, 42, 35, 42)
        ));

        // Logo o icono decorativo
        JPanel pnlLogo = new JPanel();
        pnlLogo.setOpaque(false);
        pnlLogo.setLayout(new BoxLayout(pnlLogo, BoxLayout.Y_AXIS));
        
        JLabel lblIcono = new JLabel("🔬", SwingConstants.CENTER);
        lblIcono.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        lblIcono.setForeground(C_PRIMARIO);
        lblIcono.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        lblTitulo.setForeground(C_PRIMARIO);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 34));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        lblSubtitulo.setForeground(C_TEXTO_SECUNDARIO);
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        pnlLogo.add(lblIcono);
        pnlLogo.add(Box.createVerticalStrut(10));
        pnlLogo.add(lblTitulo);
        pnlLogo.add(Box.createVerticalStrut(5));
        pnlLogo.add(lblSubtitulo);

        Font fontLabel = new Font("Segoe UI", Font.BOLD, 12);
        lblUser.setFont(fontLabel);
        lblUser.setForeground(C_TEXTO_PRIMARIO);
        lblPass.setFont(fontLabel);
        lblPass.setForeground(C_TEXTO_PRIMARIO);

        estilizarCampo(txtUsuario);
        estilizarCampo(txtClave);

        // Botón de ingreso con diseño moderno
        btnIngresar.setBackground(C_PRIMARIO);
        btnIngresar.setForeground(C_BLANCO);
        btnIngresar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnIngresar.setFocusPainted(false);
        btnIngresar.setBorderPainted(false);
        btnIngresar.setOpaque(true);
        btnIngresar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnIngresar.setPreferredSize(new Dimension(0, 48));
        
        // Efecto hover
        btnIngresar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override 
            public void mouseEntered(java.awt.event.MouseEvent e) { 
                btnIngresar.setBackground(C_ACENTO); 
            }
            @Override 
            public void mouseExited(java.awt.event.MouseEvent e) { 
                btnIngresar.setBackground(C_PRIMARIO); 
            }
        });

        // Footer elegante
        lblFooter.setForeground(C_TEXTO_SECUNDARIO);
        lblFooter.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        // Línea decorativa en el footer
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(220, 225, 230));
        separator.setPreferredSize(new Dimension(0, 1));
        
        // Botón de cerrar (X) personalizado
        JButton btnCerrar = new JButton("✕");
        btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCerrar.setForeground(C_TEXTO_SECUNDARIO);
        btnCerrar.setBackground(C_BLANCO);
        btnCerrar.setBorderPainted(false);
        btnCerrar.setFocusPainted(false);
        btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrar.setBounds(440, 15, 30, 30);
        
        btnCerrar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { 
                btnCerrar.setForeground(C_PRIMARIO); 
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { 
                btnCerrar.setForeground(C_TEXTO_SECUNDARIO); 
            }
        });
        btnCerrar.addActionListener(e -> System.exit(0));
        
        pnlLoginCard.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1.0;
        int r = 0;

        gc.gridy = r++; gc.insets = new Insets(0, 0, 30, 0);
        pnlLoginCard.add(pnlLogo, gc);

        gc.gridy = r++; gc.insets = new Insets(0, 0, 5, 0);
        pnlLoginCard.add(lblUser, gc);
        
        gc.gridy = r++; gc.insets = new Insets(0, 0, 25, 0);
        pnlLoginCard.add(txtUsuario, gc);

        gc.gridy = r++; gc.insets = new Insets(0, 0, 5, 0);
        pnlLoginCard.add(lblPass, gc);
        
        gc.gridy = r++; gc.insets = new Insets(0, 0, 35, 0);
        pnlLoginCard.add(txtClave, gc);

        gc.gridy = r++; gc.insets = new Insets(0, 0, 25, 0);
        pnlLoginCard.add(btnIngresar, gc);

        gc.gridy = r++; gc.insets = new Insets(0, 0, 15, 0);
        pnlLoginCard.add(separator, gc);
        
        JPanel pnlFoot = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        pnlFoot.setOpaque(false);
        pnlFoot.add(lblFooter);
        gc.gridy = r++; gc.insets = new Insets(0, 0, 0, 0);
        pnlLoginCard.add(pnlFoot, gc);

        getContentPane().setLayout(new GridBagLayout());
        getContentPane().add(pnlLoginCard, new GridBagConstraints());
        
        // Agregar botón cerrar al JDialog (no al panel)
        ((JPanel)getContentPane()).add(btnCerrar);
        ((JPanel)getContentPane()).setComponentZOrder(btnCerrar, 0);
    }

    private void estilizarCampo(JTextField tf) {
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setBackground(C_CAMPO);
        tf.setForeground(C_TEXTO_PRIMARIO);
        tf.setCaretColor(C_PRIMARIO);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDE_CAMPO),
            new EmptyBorder(10, 14, 10, 14)
        ));
        tf.setPreferredSize(new Dimension(0, 46));
        
        tf.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override 
            public void focusGained(java.awt.event.FocusEvent evt) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, C_PRIMARIO),
                    new EmptyBorder(10, 14, 10, 14)
                ));
                tf.setBackground(C_BLANCO);
            }
            @Override 
            public void focusLost(java.awt.event.FocusEvent evt) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDE_CAMPO),
                    new EmptyBorder(10, 14, 10, 14)
                ));
                tf.setBackground(C_CAMPO);
            }
        });
    }

    private void estilizarCampo(JPasswordField pf) {
        pf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pf.setBackground(C_CAMPO);
        pf.setForeground(C_TEXTO_PRIMARIO);
        pf.setCaretColor(C_PRIMARIO);
        pf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDE_CAMPO),
            new EmptyBorder(10, 14, 10, 14)
        ));
        pf.setPreferredSize(new Dimension(0, 46));
        
        pf.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override 
            public void focusGained(java.awt.event.FocusEvent evt) {
                pf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, C_PRIMARIO),
                    new EmptyBorder(10, 14, 10, 14)
                ));
                pf.setBackground(C_BLANCO);
            }
            @Override 
            public void focusLost(java.awt.event.FocusEvent evt) {
                pf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDE_CAMPO),
                    new EmptyBorder(10, 14, 10, 14)
                ));
                pf.setBackground(C_CAMPO);
            }
        });
    }

    private void configurarNavegacionTeclado() {
        java.awt.event.KeyAdapter enterAdapter = new java.awt.event.KeyAdapter() {
            @Override 
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    if (e.getSource() == txtUsuario) {
                        txtClave.requestFocus();
                    } else if (e.getSource() == txtClave) {
                        btnIngresar.doClick();
                    }
                }
            }
        };
        txtUsuario.addKeyListener(enterAdapter);
        txtClave.addKeyListener(enterAdapter);
        this.getRootPane().setDefaultButton(btnIngresar);
    }

    // ════════════════════════════════════════════════════════════════
    //  UI BUILDER
    // ════════════════════════════════════════════════════════════════
    private void initComponents() {
        pnlLoginCard = new JPanel();
        lblTitulo    = new JLabel("BIOTEC");
        lblSubtitulo = new JLabel("SISTEMA GESTOR DE LABORATORIO");
        lblUser      = new JLabel("USUARIO");
        lblPass      = new JLabel("CONTRASEÑA");
        txtUsuario   = new JTextField();
        txtClave     = new JPasswordField();
        btnIngresar  = new JButton("INGRESAR AL SISTEMA");
        lblFooter    = new JLabel("© 2026 BIOTEC - Todos los derechos reservados");

        getContentPane().setLayout(new BorderLayout());
        pnlLoginCard.setLayout(new BoxLayout(pnlLoginCard, BoxLayout.Y_AXIS));
    }

    private JPanel pnlLoginCard;
    private JLabel lblTitulo, lblSubtitulo, lblUser, lblPass, lblFooter;
    private JTextField txtUsuario;
    private JPasswordField txtClave;
    private JButton btnIngresar;
}