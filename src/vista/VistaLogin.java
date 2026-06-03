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
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * Vista de acceso profesional - BIOTEC LIS
 * @author luciano
 */
public class VistaLogin extends javax.swing.JDialog implements IVistaLogin {

    private Controlador controlador;

    // ── Paleta BIOTEC Minimalista ────────────────────────────────────
    private final Color C_NAVY         = new Color(10, 25, 47);    
    private final Color C_AZUL_MEDIO   = new Color(30, 110, 180);
    private final Color C_AZUL_HOVER   = new Color(40, 130, 200);
    private final Color C_FONDO        = new Color(238, 242, 246); // Gris clínico suave
    private final Color C_BLANCO       = Color.WHITE;
    private final Color C_TEXTO_FUERTE = new Color(40, 50, 60);
    private final Color C_TEXTO_SUAVE  = new Color(120, 135, 150);
    private final Color C_BORDE        = new Color(215, 225, 235);
    private final Color C_CAMPO        = new Color(250, 252, 254);

    public VistaLogin() {
        // Al ser la ventana principal de entrada, no tiene un "parent" definido aún
        super((java.awt.Frame) null, true); 
        initComponents();
        aplicarEsteticaProfesional();
        configurarNavegacionTeclado();
        
        // Configuraciones de ventana
        setTitle("Acceso al Sistema - BIOTEC LIS");
        setSize(new Dimension(500, 650));
        setResizable(false);
        setLocationRelativeTo(null); // Centrar en pantalla
    }

    // ══════════════════════════════════════════════════════════════════
    //  ESTÉTICA Y UX
    // ══════════════════════════════════════════════════════════════════
    private void aplicarEsteticaProfesional() {
        // Fondo general
        getContentPane().setBackground(C_FONDO);

        // Tarjeta de Login (Card central)
        pnlLoginCard.setBackground(C_BLANCO);
        pnlLoginCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 230, 240), 1, true),
            new EmptyBorder(40, 45, 40, 45) // Padding interno
        ));

        // Textos del encabezado
        lblTitulo.setForeground(C_NAVY);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 36));
        
        lblSubtitulo.setForeground(C_TEXTO_SUAVE);
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        // Labels de los campos
        Font fontLabel = new Font("Segoe UI", Font.BOLD, 12);
        lblUser.setFont(fontLabel);
        lblUser.setForeground(C_TEXTO_SUAVE);
        lblPass.setFont(fontLabel);
        lblPass.setForeground(C_TEXTO_SUAVE);

        // Campos de texto reactivos
        estilizarCampo(txtUsuario);
        estilizarCampo(txtClave);

        // Botón principal
        btnIngresar.setBackground(C_AZUL_MEDIO);
        btnIngresar.setForeground(C_BLANCO);
        btnIngresar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnIngresar.setFocusPainted(false);
        btnIngresar.setBorderPainted(false);
        btnIngresar.setOpaque(true);
        btnIngresar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnIngresar.setPreferredSize(new Dimension(0, 48)); // Botón alto y cómodo

        // Efecto Hover en el botón
        btnIngresar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                btnIngresar.setBackground(C_AZUL_HOVER);
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                btnIngresar.setBackground(C_AZUL_MEDIO);
            }
        });

        // Mensaje de seguridad al pie
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
        
        // Animación de foco (Se pinta de azul al clickear)
        tf.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent evt) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, C_AZUL_MEDIO),
                    new EmptyBorder(10, 12, 10, 12)
                ));
                tf.setBackground(C_BLANCO);
            }
            @Override public void focusLost(FocusEvent evt) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDE),
                    new EmptyBorder(10, 12, 10, 12)
                ));
                tf.setBackground(C_CAMPO);
            }
        });
    }

    // ══════════════════════════════════════════════════════════════════
    //  LÓGICA Y EVENTOS
    // ══════════════════════════════════════════════════════════════════
    private void configurarNavegacionTeclado() {
        KeyAdapter enterAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
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
        
        // Botón por defecto al apretar ENTER en cualquier lado
        this.getRootPane().setDefaultButton(btnIngresar);
    }

    @Override
    public void setControlador(Controlador control) {
        this.controlador = control;
        btnIngresar.setActionCommand(BTN_INGRESAR);
        btnIngresar.addActionListener(control);
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

    // ══════════════════════════════════════════════════════════════════
    //  UI BUILDER (Layout programático limpio)
    // ══════════════════════════════════════════════════════════════════
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

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new GridBagLayout()); // Centra el pnlLoginCard automáticamente

        pnlLoginCard.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1.0;
        int r = 0;

        // Títulos centrados
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

        // Campos
        gc.gridy = r++; gc.insets = new Insets(0, 0, 5, 0);  pnlLoginCard.add(lblUser, gc);
        gc.gridy = r++; gc.insets = new Insets(0, 0, 25, 0); pnlLoginCard.add(txtUsuario, gc);

        gc.gridy = r++; gc.insets = new Insets(0, 0, 5, 0);  pnlLoginCard.add(lblPass, gc);
        gc.gridy = r++; gc.insets = new Insets(0, 0, 40, 0); pnlLoginCard.add(txtClave, gc);

        // Botón
        gc.gridy = r++; gc.insets = new Insets(0, 0, 30, 0); pnlLoginCard.add(btnIngresar, gc);

        // Footer centrado
        JPanel pnlFoot = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        pnlFoot.setOpaque(false);
        pnlFoot.add(lblFooter);
        gc.gridy = r++; gc.insets = new Insets(0, 0, 0, 0); pnlLoginCard.add(pnlFoot, gc);

        // Se agrega la tarjeta centrada en el Frame
        GridBagConstraints gRoot = new GridBagConstraints();
        gRoot.gridx = 0; gRoot.gridy = 0;
        // Ajustamos el tamaño preferido de la tarjeta para que se vea rectangular y elegante
        pnlLoginCard.setPreferredSize(new Dimension(380, 480));
        getContentPane().add(pnlLoginCard, gRoot);

        pack();
    }

    // ── Variables ────────────────────────────────────────────────────
    private JPanel         pnlLoginCard;
    private JLabel         lblTitulo;
    private JLabel         lblSubtitulo;
    private JLabel         lblUser;
    private JLabel         lblPass;
    private JLabel         lblFooter;
    private JTextField     txtUsuario;
    private JPasswordField txtClave;
    private JButton        btnIngresar;
}