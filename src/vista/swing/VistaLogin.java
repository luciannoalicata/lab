package vista.swing;

// @author lucianoalicata

import vista.interfaces.IVistaLogin;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.*;
import presentador.SesionPresenter;

public class VistaLogin extends javax.swing.JDialog implements IVistaLogin {

    private final Color C_PRIMARIO      = new Color(0, 70, 128);      
    private final Color C_ACENTO        = new Color(0, 150, 200);     
    private final Color C_FONDO         = new Color(240, 245, 250);    
    private final Color C_BLANCO        = Color.WHITE;
    private final Color C_TEXTO_PRIMARIO = new Color(30, 40, 50);
    private final Color C_TEXTO_SECUNDARIO = new Color(100, 115, 130);
    private final Color C_BORDE_CAMPO    = new Color(210, 225, 235);
    private final Color C_CAMPO          = new Color(248, 250, 252);

    public VistaLogin() {
        super((java.awt.Frame) null, true);
        initComponents();
        aplicarEsteticaProfesional();
        configurarNavegacionTeclado();
        
        setTitle("BIOTEC LIS - Acceso al Sistema");
        setUndecorated(true);
        
        pack(); 
        setLocationRelativeTo(null);
        
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
    }

    @Override
    public void setPresenter(SesionPresenter presenter) {
        for (java.awt.event.ActionListener al : btnIngresar.getActionListeners()) {
            btnIngresar.removeActionListener(al);
        }
        btnIngresar.addActionListener(e -> presenter.onIngresar());
    }

    @Override 
    public void ejecutar() { 
        SwingUtilities.invokeLater(() -> {
            txtUsuario.requestFocusInWindow();
        });
        setVisible(true); 
    }
    
    @Override 
    public String getUsuario() { return txtUsuario.getText().trim(); }
    
    @Override 
    public String getClave() { return new String(txtClave.getPassword()); }
    
    @Override 
    public void mostrarMensaje(String mensaje) { JOptionPane.showMessageDialog(this, mensaje); }
    
    @Override
    public void limpiarCampos() {
        txtUsuario.setText("");
        txtClave.setText("");
        txtUsuario.requestFocus();
    }
    
    @Override
    public void cerrarPantalla() { this.dispose(); }
    
    @Override
    public void limpiarFocos() { this.requestFocusInWindow(); }

    @Override
    public int confirmarAccion(String mensaje, String titulo) {
        return JOptionPane.showConfirmDialog(this, mensaje, titulo, JOptionPane.YES_NO_OPTION);
    }

    private void aplicarEsteticaProfesional() {
        getContentPane().setBackground(C_FONDO);
        
        pnlLoginCard.setOpaque(true);
        pnlLoginCard.setBackground(C_BLANCO);
        pnlLoginCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 225, 230), 1),
            new EmptyBorder(25, 42, 35, 42) 
        ));
        
        JPanel pnlLogo = new JPanel();
        pnlLogo.setOpaque(false);
        pnlLogo.setLayout(new BoxLayout(pnlLogo, BoxLayout.Y_AXIS));
        
        lblTitulo.setForeground(C_PRIMARIO);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 34));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        lblSubtitulo.setForeground(C_TEXTO_SECUNDARIO);
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
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

        btnIngresar.setBackground(C_PRIMARIO);
        btnIngresar.setForeground(C_BLANCO);
        btnIngresar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnIngresar.setFocusPainted(false);
        btnIngresar.setBorderPainted(false);
        btnIngresar.setOpaque(true);
        btnIngresar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnIngresar.setPreferredSize(new Dimension(320, 48)); // Ancho base de 320px
        
        btnIngresar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { btnIngresar.setBackground(C_ACENTO); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { btnIngresar.setBackground(C_PRIMARIO); }
        });

        lblFooter.setForeground(C_TEXTO_SECUNDARIO);
        lblFooter.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(220, 225, 230));
        separator.setPreferredSize(new Dimension(0, 1));
        
        JButton btnCerrar = new JButton("X");
        btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnCerrar.setForeground(C_TEXTO_SECUNDARIO);
        btnCerrar.setBackground(C_BLANCO);
        btnCerrar.setBorderPainted(false);
        btnCerrar.setFocusPainted(false);
        btnCerrar.setContentAreaFilled(false);
        btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrar.setMargin(new Insets(0, 0, 0, 0));
        
        btnCerrar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { btnCerrar.setForeground(C_PRIMARIO); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { btnCerrar.setForeground(C_TEXTO_SECUNDARIO); }
        });
        btnCerrar.addActionListener(e -> System.exit(0));

        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pnlHeader.setOpaque(false);
        pnlHeader.add(btnCerrar);
        
        pnlLoginCard.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1.0;
        int r = 0;

        gc.gridy = r++; gc.insets = new Insets(0, 0, 10, -10); 
        pnlLoginCard.add(pnlHeader, gc);

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
        tf.setPreferredSize(new Dimension(320, 46)); 
        
        tf.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent evt) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, C_PRIMARIO),
                    new EmptyBorder(10, 14, 10, 14)
                ));
                tf.setBackground(C_BLANCO);
            }
            @Override public void focusLost(java.awt.event.FocusEvent evt) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDE_CAMPO),
                    new EmptyBorder(10, 14, 10, 14)
                ));
                tf.setBackground(C_CAMPO);
            }
        });
    }

    private void configurarNavegacionTeclado() {
        this.getRootPane().setDefaultButton(btnIngresar);
    }

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