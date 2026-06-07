package vista.swing;

import vista.interfaces.IVistaLogin;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import presentador.SesionPresenter;

public class VistaLogin extends javax.swing.JDialog implements IVistaLogin {

    private SesionPresenter presenter;

    // ── Paleta BIOTEC Minimalista ────────────────────────────────────
    private final Color C_NAVY         = new Color(10, 25, 47);    
    private final Color C_AZUL_MEDIO   = new Color(30, 110, 180);
    private final Color C_AZUL_HOVER   = new Color(40, 130, 200);
    private final Color C_FONDO        = new Color(238, 242, 246);
    private final Color C_BLANCO       = Color.WHITE;
    private final Color C_TEXTO_FUERTE = new Color(40, 50, 60);
    private final Color C_TEXTO_SUAVE  = new Color(120, 135, 150);
    private final Color C_BORDE        = new Color(215, 225, 235);
    private final Color C_CAMPO        = new Color(250, 252, 254);

    public VistaLogin() {
        super((java.awt.Frame) null, true); 
        initComponents();
        aplicarEsteticaProfesional();
        configurarNavegacionTeclado();
        
        setTitle("Acceso al Sistema - BIOTEC LIS");
        setSize(new Dimension(500, 650));
        setResizable(false);
        setLocationRelativeTo(null);
    }

    // ════════════════════════════════════════════════════════════════
    //  INTERFAZ IVistaLogin - MÉTODOS MVP
    // ════════════════════════════════════════════════════════════════
    @Override
    public void setPresenter(SesionPresenter presenter) {
        this.presenter = presenter;
        
        // ¡MAGIA MVP! Conectamos el botón al método del presentador
        btnIngresar.addActionListener(e -> presenter.onIngresar());
    }

    @Override public void ejecutar() { setVisible(true); }
    @Override public String getUsuario() { return txtUsuario.getText().trim(); }
    @Override public String getClave() { return new String(txtClave.getPassword()); }
    @Override public void mostrarMensaje(String mensaje) { JOptionPane.showMessageDialog(this, mensaje); }
    
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

    // ════════════════════════════════════════════════════════════════
    //  ESTÉTICA Y UX
    // ════════════════════════════════════════════════════════════════
    private void aplicarEsteticaProfesional() {
        getContentPane().setBackground(C_FONDO);

        pnlLoginCard.setBackground(C_BLANCO);
        pnlLoginCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 230, 240), 1, true),
            new EmptyBorder(40, 45, 40, 45)
        ));

        lblTitulo.setForeground(C_NAVY);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 36));
        
        lblSubtitulo.setForeground(C_TEXTO_SUAVE);
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        Font fontLabel = new Font("Segoe UI", Font.BOLD, 12);
        lblUser.setFont(fontLabel);
        lblUser.setForeground(C_TEXTO_SUAVE);
        lblPass.setFont(fontLabel);
        lblPass.setForeground(C_TEXTO_SUAVE);

        estilizarCampo(txtUsuario);
        estilizarCampo(txtClave);

        btnIngresar.setBackground(C_AZUL_MEDIO);
        btnIngresar.setForeground(C_BLANCO);
        btnIngresar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnIngresar.setFocusPainted(false);
        btnIngresar.setBorderPainted(false);
        btnIngresar.setOpaque(true);
        btnIngresar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnIngresar.setPreferredSize(new Dimension(0, 48));

        btnIngresar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { btnIngresar.setBackground(C_AZUL_HOVER); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { btnIngresar.setBackground(C_AZUL_MEDIO); }
        });

        lblFooter.setForeground(new Color(150, 165, 180));
        lblFooter.setFont(new Font("Segoe UI", Font.PLAIN, 11));
    }

    private void estilizarCampo(JTextField tf) {
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        tf.setBackground(C_CAMPO);
        tf.setForeground(C_TEXTO_FUERTE);
        tf.setCaretColor(C_AZUL_MEDIO);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDE),
            new EmptyBorder(10, 12, 10, 12)
        ));
        tf.setPreferredSize(new Dimension(0, 45));
        
        tf.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent evt) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, C_AZUL_MEDIO),
                    new EmptyBorder(10, 12, 10, 12)
                ));
                tf.setBackground(C_BLANCO);
            }
            @Override public void focusLost(java.awt.event.FocusEvent evt) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDE),
                    new EmptyBorder(10, 12, 10, 12)
                ));
                tf.setBackground(C_CAMPO);
            }
        });
    }

    private void configurarNavegacionTeclado() {
        java.awt.event.KeyAdapter enterAdapter = new java.awt.event.KeyAdapter() {
            @Override public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    if (e.getSource() == txtUsuario) txtClave.requestFocus();
                    else if (e.getSource() == txtClave) btnIngresar.doClick();
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
        lblSubtitulo = new JLabel("SISTEMA DE GESTIÓN DE LABORATORIO");
        lblUser      = new JLabel("USUARIO");
        lblPass      = new JLabel("CONTRASEÑA");
        txtUsuario   = new JTextField();
        txtClave     = new JPasswordField();
        btnIngresar  = new JButton("INGRESAR");
        lblFooter    = new JLabel("Acceso restringido a personal autorizado.");

        getContentPane().setLayout(new GridBagLayout());

        pnlLoginCard.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1.0;
        int r = 0;

        JPanel pnlTitulos = new JPanel();
        pnlTitulos.setOpaque(false);
        pnlTitulos.setLayout(new BoxLayout(pnlTitulos, BoxLayout.Y_AXIS));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlTitulos.add(lblTitulo);
        pnlTitulos.add(Box.createVerticalStrut(5));
        pnlTitulos.add(lblSubtitulo);

        gc.gridx = 0;
        gc.gridy = r++; gc.insets = new Insets(0, 0, 40, 0);
        pnlLoginCard.add(pnlTitulos, gc);

        gc.gridy = r++; gc.insets = new Insets(0, 0, 5, 0);  pnlLoginCard.add(lblUser, gc);
        gc.gridy = r++; gc.insets = new Insets(0, 0, 25, 0); pnlLoginCard.add(txtUsuario, gc);

        gc.gridy = r++; gc.insets = new Insets(0, 0, 5, 0);  pnlLoginCard.add(lblPass, gc);
        gc.gridy = r++; gc.insets = new Insets(0, 0, 40, 0); pnlLoginCard.add(txtClave, gc);

        gc.gridy = r++; gc.insets = new Insets(0, 0, 30, 0); pnlLoginCard.add(btnIngresar, gc);

        JPanel pnlFoot = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        pnlFoot.setOpaque(false);
        pnlFoot.add(lblFooter);
        gc.gridy = r++; gc.insets = new Insets(0, 0, 0, 0); pnlLoginCard.add(pnlFoot, gc);

        pnlLoginCard.setPreferredSize(new Dimension(380, 480));
        getContentPane().add(pnlLoginCard, new GridBagConstraints());
    }

    private JPanel pnlLoginCard;
    private JLabel lblTitulo, lblSubtitulo, lblUser, lblPass, lblFooter;
    private JTextField txtUsuario;
    private JPasswordField txtClave;
    private JButton btnIngresar;

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
}