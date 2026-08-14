package vista.swing;

// @author lucianoalicata
import vista.interfaces.IVistaPrincipal;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.*;
import presentador.PrincipalPresenter;

public class VistaPrincipal extends javax.swing.JFrame implements IVistaPrincipal {

    private PrincipalPresenter presenter;
    private javax.swing.JDialog dialogoEspera;

    private final Color C_NAVY = new Color(10, 25, 47);
    private final Color C_AZUL_OSCURO = new Color(0, 51, 102);
    private final Color C_AZUL_MEDIO = new Color(30, 110, 180);
    private final Color C_VERDE = new Color(35, 160, 115);
    private final Color C_ROJO = new Color(220, 53, 69);
    private final Color C_ROJO_HOV = new Color(200, 35, 51);
    private final Color C_FONDO = new Color(238, 242, 246);
    private final Color C_BLANCO = Color.WHITE;
    private final Color C_BORDE = new Color(215, 225, 235);
    private final Color C_TEXTO_SUAVE = new Color(100, 115, 130);
    private final Color C_TEXTO_FUERTE = new Color(40, 50, 60);
    private final Color C_LABEL_HDR = new Color(175, 205, 235);
    private final Color C_HOVER_BG = new Color(245, 250, 255);
    private final Color C_HOVER_BORDER = new Color(180, 210, 240);

    private javax.swing.JButton btnNBU;
    private javax.swing.JButton btnGestionUsuarios;
    private javax.swing.JButton btnAuditoria;
    private javax.swing.JButton btnMedicos;
    private javax.swing.JButton btnAnalisis;
    private javax.swing.JButton btnObrasSociales;
    private javax.swing.JButton btnAjustes;
    private javax.swing.JButton btnPacientes;
    private javax.swing.JButton btnCerrarSesion;

    private javax.swing.JLabel lblNombreUsuario;
    private javax.swing.JLabel lblRolUsuario;
    private javax.swing.JLabel lblAvatar;
    private javax.swing.JPanel pnlUsuarioInfo;

    private CardLayout cardLayout;
    private JPanel pnlContenido;

    private JPanel pnlMenuIzquierdo;
    private JPanel pnlMenuDerecho;
    private JPanel pnlFooterCerrar;

    private JPanel pnlCentroWrapper;

    public VistaPrincipal() {
        initComponents();
        construirUI();
        configurarCierreVentana();
    }

    @Override
    public void setPresenter(PrincipalPresenter presenter) {
        this.presenter = presenter;

        btnPacientes.addActionListener(e -> presenter.onPacientes());
        btnAnalisis.addActionListener(e -> presenter.onAnalisis());
        btnMedicos.addActionListener(e -> presenter.onMedicos());
        btnObrasSociales.addActionListener(e -> presenter.onObrasSociales());
        btnNBU.addActionListener(e -> presenter.onNBU());
        btnAjustes.addActionListener(e -> presenter.onAjustes());
        btnGestionUsuarios.addActionListener(e -> presenter.onGestionUsuarios());
        btnAuditoria.addActionListener(e -> presenter.onAuditoria());
        btnCerrarSesion.addActionListener(e -> presenter.onCerrarSesion());
    }

    private void construirUI() {
        setTitle("BIOTEC LABORATORIOS");
        setBackground(C_FONDO);
        construirHeader();
        construirCuerpo();
        pack();

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setMinimumSize(new Dimension(
                (int) (screen.width * 0.85),
                (int) (screen.height * 0.85)
        ));

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        try {
            java.net.URL url = getClass().getResource("/reportes/img/logo_sw.png");
            if (url != null) {
                java.awt.image.BufferedImage imgFrame = javax.imageio.ImageIO.read(url);
                setIconImage(imgFrame);
            } else {
                System.out.println("No se encontró el icono en: /reportes/img/logo_sw.png");
            }
        } catch (Exception e) {
            System.out.println("Error al cargar el icono: " + e.getMessage());
        }
    }

    private void construirHeader() {
        if (pnlHeader == null) {
            return;
        }

        pnlHeader.setBackground(C_NAVY);
        pnlHeader.setBorder(new EmptyBorder(0, 36, 0, 36));
        pnlHeader.setPreferredSize(new Dimension(0, 85));
        pnlHeader.setLayout(new BorderLayout());

        JPanel pnlIzq = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 28));
        pnlIzq.setOpaque(false);
        if (lblFecha != null) {
            lblFecha.setText(new SimpleDateFormat("EEEE dd 'de' MMMM, yyyy").format(new Date()).toUpperCase());
            lblFecha.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lblFecha.setForeground(C_LABEL_HDR);
            pnlIzq.add(lblFecha);
        }
        pnlHeader.add(pnlIzq, BorderLayout.WEST);

        JPanel pnlCentro = new JPanel();
        pnlCentro.setOpaque(false);
        pnlCentro.setLayout(new BoxLayout(pnlCentro, BoxLayout.Y_AXIS));

        if (lblBienvenida != null) {
            lblBienvenida.setFont(new Font("Segoe UI", Font.BOLD, 26));
            lblBienvenida.setForeground(C_BLANCO);
            lblBienvenida.setText("BIOTEC");
            lblBienvenida.setAlignmentX(Component.CENTER_ALIGNMENT);
        }
        JLabel lblSub = new JLabel("SISTEMA DE GESTIÓN DE LABORATORIO CLÍNICO");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(C_LABEL_HDR);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        pnlCentro.add(Box.createVerticalStrut(16));
        pnlCentro.add(lblBienvenida);
        pnlCentro.add(Box.createVerticalStrut(4));
        pnlCentro.add(lblSub);
        pnlHeader.add(pnlCentro, BorderLayout.CENTER);

        JPanel pnlDer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 18));
        pnlDer.setOpaque(false);

        pnlUsuarioInfo = new JPanel();
        pnlUsuarioInfo.setOpaque(false);
        pnlUsuarioInfo.setLayout(new FlowLayout(FlowLayout.RIGHT, 14, 0));

        JPanel pnlTextoUser = new JPanel();
        pnlTextoUser.setOpaque(false);
        pnlTextoUser.setLayout(new BoxLayout(pnlTextoUser, BoxLayout.Y_AXIS));

        lblNombreUsuario = new JLabel();
        lblNombreUsuario.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblNombreUsuario.setForeground(C_BLANCO);
        lblNombreUsuario.setAlignmentX(Component.RIGHT_ALIGNMENT);

        lblRolUsuario = new JLabel();
        lblRolUsuario.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblRolUsuario.setForeground(C_AZUL_MEDIO);
        lblRolUsuario.setAlignmentX(Component.RIGHT_ALIGNMENT);

        pnlTextoUser.add(lblNombreUsuario);
        pnlTextoUser.add(Box.createVerticalStrut(2));
        pnlTextoUser.add(lblRolUsuario);

        lblAvatar = new JLabel();
        lblAvatar.setPreferredSize(new Dimension(46, 46));
        lblAvatar.setHorizontalAlignment(SwingConstants.CENTER);
        lblAvatar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblAvatar.setForeground(C_BLANCO);
        lblAvatar.setOpaque(true);
        lblAvatar.setBackground(C_AZUL_MEDIO);
        lblAvatar.setBorder(BorderFactory.createLineBorder(C_LABEL_HDR, 1));

        ImageIcon icoUser = icon("/reportes/img/monigote_icon.png", 28, 28);
        if (icoUser != null) {
            lblAvatar.setIcon(icoUser);
            lblAvatar.setText("");
        }

        pnlUsuarioInfo.add(pnlTextoUser);
        pnlUsuarioInfo.add(lblAvatar);
        pnlDer.add(pnlUsuarioInfo);
        pnlHeader.add(pnlDer, BorderLayout.EAST);
    }

    private void construirCuerpo() {
        if (pnlEscritorio == null) {
            return;
        }

        pnlEscritorio.setLayout(new BorderLayout());
        pnlEscritorio.setBackground(C_FONDO);

        pnlCentroWrapper = new JPanel(new BorderLayout());
        pnlCentroWrapper.setBackground(C_FONDO);
        pnlCentroWrapper.setBorder(new EmptyBorder(16, 20, 20, 20));

        cardLayout = new CardLayout();
        pnlContenido = new JPanel(cardLayout);
        pnlContenido.setBackground(C_BLANCO);
        pnlContenido.setBorder(BorderFactory.createLineBorder(C_BORDE, 1, true));

        pnlContenido.add(construirPanelInicio(), "inicio");

        pnlCentroWrapper.add(pnlContenido, BorderLayout.CENTER);

        pnlMenuIzquierdo = construirMenuLateral(true);
        pnlMenuDerecho = construirMenuLateral(false);

        pnlEscritorio.add(pnlMenuIzquierdo, BorderLayout.WEST);
        pnlEscritorio.add(pnlCentroWrapper, BorderLayout.CENTER);
        pnlEscritorio.add(pnlMenuDerecho, BorderLayout.EAST);

        pnlFooterCerrar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 36, 12));
        pnlFooterCerrar.setBackground(C_BLANCO);
        pnlFooterCerrar.setBorder(new MatteBorder(1, 0, 0, 0, C_BORDE));

        configurarBotonCerrarSesion();
        pnlFooterCerrar.add(btnCerrarSesion);

        pnlEscritorio.add(pnlFooterCerrar, BorderLayout.SOUTH);
    }

    private JPanel construirMenuLateral(boolean esIzquierdo) {
        JPanel panel = new JPanel();
        panel.setBackground(C_BLANCO);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.setPreferredSize(new Dimension(280, 0));
        panel.setMinimumSize(new Dimension(220, 0));

        if (esIzquierdo) {
            panel.setBorder(BorderFactory.createCompoundBorder(
                    new MatteBorder(0, 0, 0, 1, C_BORDE),
                    new EmptyBorder(0, 16, 0, 16)
            ));
        } else {
            panel.setBorder(BorderFactory.createCompoundBorder(
                    new MatteBorder(0, 1, 0, 0, C_BORDE),
                    new EmptyBorder(0, 16, 0, 16)
            ));
        }

        panel.add(Box.createVerticalStrut(20));

        if (esIzquierdo) {
            panel.add(crearBotonMenu(btnPacientes, "PACIENTES", "Gestión de pacientes", "paciente_icon.png"));
            panel.add(Box.createVerticalStrut(10));
            panel.add(crearBotonMenu(btnAnalisis, "LISTA ANÁLISIS", "Resultados cargados", "analisis_icon.png"));
            panel.add(Box.createVerticalStrut(10));
            panel.add(crearBotonMenu(btnMedicos, "PROFESIONALES", "Médicos solicitantes", "medico_icon.png"));
            panel.add(Box.createVerticalStrut(10));
            panel.add(crearBotonMenu(btnObrasSociales, "OBRAS SOCIALES", "Coberturas y aranceles", "obs_icon.png"));
        } else {
            panel.add(crearBotonMenu(btnNBU, "NBU", "Prácticas", "nbu_icon.png"));
            panel.add(Box.createVerticalStrut(10));
            panel.add(crearBotonMenu(btnAuditoria, "AUDITORÍA", "Seguridad y eventos", "auditoria_icon.png"));
            panel.add(Box.createVerticalStrut(10));
            panel.add(crearBotonMenu(btnGestionUsuarios, "USUARIOS", "Permisos y accesos", "usuarios_icon.png"));
            panel.add(Box.createVerticalStrut(10));
            panel.add(crearBotonMenu(btnAjustes, "CONFIGURACIÓN", "Ajustes del sistema", "ajustes_icon.png"));
        }

        panel.add(Box.createVerticalStrut(20));
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel crearBotonMenu(JButton btn, String titulo, String subtitulo, String iconoFile) {
        btn.setLayout(new BorderLayout());
        btn.setText("");
        btn.setBackground(C_BLANCO);
        btn.setForeground(C_TEXTO_FUERTE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.setPreferredSize(new Dimension(260, 80));
        btn.setMinimumSize(new Dimension(190, 75));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 85));

        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDE, 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel lblIco = new JLabel();
        lblIco.setPreferredSize(new Dimension(46, 46));
        lblIco.setHorizontalAlignment(SwingConstants.CENTER);
        ImageIcon ico = icon("/reportes/img/" + iconoFile, 38, 38);
        if (ico != null) {
            lblIco.setIcon(ico);
        }

        JPanel pnlTexto = new JPanel();
        pnlTexto.setOpaque(false);
        pnlTexto.setLayout(new BoxLayout(pnlTexto, BoxLayout.Y_AXIS));
        pnlTexto.setBorder(new EmptyBorder(0, 12, 0, 0));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitulo.setForeground(C_TEXTO_FUERTE);

        JLabel lblSub = new JLabel(subtitulo);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(C_TEXTO_SUAVE);

        pnlTexto.add(lblTitulo);
        pnlTexto.add(Box.createVerticalStrut(2));
        pnlTexto.add(lblSub);

        JLabel flecha = new JLabel("›");
        flecha.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        flecha.setForeground(new Color(200, 210, 220));
        flecha.setBorder(new EmptyBorder(0, 2, 0, 2));

        JPanel contenido = new JPanel(new BorderLayout());
        contenido.setOpaque(false);
        contenido.add(lblIco, BorderLayout.WEST);
        contenido.add(pnlTexto, BorderLayout.CENTER);
        contenido.add(flecha, BorderLayout.EAST);

        btn.add(contenido);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(C_HOVER_BG);
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(C_HOVER_BORDER, 1, true),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
                flecha.setForeground(C_AZUL_MEDIO);
                lblTitulo.setForeground(C_AZUL_OSCURO);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(C_BLANCO);
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(C_BORDE, 1, true),
                        new EmptyBorder(10, 10, 10, 10)
                ));
                flecha.setForeground(new Color(200, 210, 220));
                lblTitulo.setForeground(C_TEXTO_FUERTE);
            }
        });

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        wrapper.add(btn, BorderLayout.CENTER);
        return wrapper;
    }

    private void configurarBotonCerrarSesion() {
        if (btnCerrarSesion == null) {
            btnCerrarSesion = new JButton();
        }
        btnCerrarSesion.setText("CERRAR SESIÓN");
        btnCerrarSesion.setBackground(C_ROJO);
        btnCerrarSesion.setForeground(C_BLANCO);
        btnCerrarSesion.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCerrarSesion.setFocusPainted(false);
        btnCerrarSesion.setBorderPainted(false);
        btnCerrarSesion.setOpaque(true);
        btnCerrarSesion.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCerrarSesion.setPreferredSize(new Dimension(220, 35));
        btnCerrarSesion.setHorizontalAlignment(SwingConstants.CENTER);

        btnCerrarSesion.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnCerrarSesion.setBackground(C_ROJO_HOV);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnCerrarSesion.setBackground(C_ROJO);
            }
        });
    }

    private JPanel construirPanelInicio() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(C_BLANCO);

        JPanel card = new JPanel();
        card.setBackground(C_BLANCO);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        card.add(Box.createVerticalGlue());

        JLabel lblLogo = new JLabel();
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
        try {
            java.net.URL url = getClass().getResource("/reportes/img/biotec_logo.png");
            if (url != null) {
                lblLogo.setIcon(iconAncho("/reportes/img/biotec_logo.png", 280));
                lblLogo.setText("");
            } else {
                lblLogo.setText("BIOTEC");
                lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 48));
                lblLogo.setForeground(C_AZUL_OSCURO);
            }
        } catch (Exception e) {
            lblLogo.setText("BIOTEC");
            lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 48));
            lblLogo.setForeground(C_AZUL_OSCURO);
        }
        card.add(lblLogo);
        card.add(Box.createRigidArea(new Dimension(0, 15)));

        String fechaStr = new SimpleDateFormat("EEEE, dd 'de' MMMM 'de' yyyy").format(new Date());
        fechaStr = fechaStr.substring(0, 1).toUpperCase() + fechaStr.substring(1);

        JLabel lblFechaCard = new JLabel(fechaStr);
        lblFechaCard.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblFechaCard.setForeground(C_TEXTO_SUAVE);
        lblFechaCard.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblFechaCard);
        card.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel pnlChips = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        pnlChips.setOpaque(false);
        pnlChips.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlChips.add(crearChip("● Sistema Operativo", C_VERDE));
        pnlChips.add(crearChip("● Servidor Conectado", C_AZUL_MEDIO));
        card.add(pnlChips);

        card.add(Box.createVerticalGlue());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;

        panel.add(card, gbc);
        return panel;
    }

    private JLabel crearChip(String texto, Color color) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(color);
        lbl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(color.getRed(), color.getGreen(), color.getBlue(), 60), 1, true),
                new EmptyBorder(8, 16, 8, 16)
        ));
        lbl.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 12));
        lbl.setOpaque(true);
        return lbl;
    }

    private ImageIcon icon(String ruta, int w, int h) {
        try {
            java.net.URL url = getClass().getResource(ruta);
            if (url == null) {
                return null;
            }

            java.awt.image.BufferedImage original = javax.imageio.ImageIO.read(url);
            if (original == null) {
                return null;
            }

            java.awt.image.BufferedImage escalada
                    = new java.awt.image.BufferedImage(w, h,
                            java.awt.image.BufferedImage.TYPE_INT_ARGB);

            java.awt.Graphics2D g2d = escalada.createGraphics();
            g2d.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,
                    java.awt.RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2d.setRenderingHint(java.awt.RenderingHints.KEY_RENDERING,
                    java.awt.RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                    java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.drawImage(original, 0, 0, w, h, null);
            g2d.dispose();

            return new ImageIcon(escalada);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void setUsuarioLogueado(String nombreUsuario, String rol) {
        if (lblNombreUsuario != null) {
            lblNombreUsuario.setText(nombreUsuario.toUpperCase());
        }
        if (lblRolUsuario != null) {
            lblRolUsuario.setText(rol.toUpperCase());
        }
    }

    @Override
    public void ejecutar() {
        setVisible(true);
    }

    @Override
    public void mostrarAvisoBackup(boolean mostrar) {
//        if (mostrar) {
//            Object[] options = {};
//            JOptionPane pane = new JOptionPane(
//                    "Generando copia de seguridad...\nPor favor, no cierre el programa.",
//                    JOptionPane.INFORMATION_MESSAGE,
//                    JOptionPane.DEFAULT_OPTION, null, options, null);
//
//            dialogoEspera = pane.createDialog(this, "Copia de Seguridad en curso");
//            dialogoEspera.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
//
//            new Thread(() -> dialogoEspera.setVisible(true)).start();
//        } else {
//            if (dialogoEspera != null) {
//                dialogoEspera.dispose();
//                dialogoEspera = null;
//            }
//        }
    }

    @Override
    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
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

    @Override
    public void habilitarBotonPacientes(boolean b) {
        if (btnPacientes != null) {
            btnPacientes.setEnabled(b);
        }
    }

    @Override
    public void habilitarBotonAnalisis(boolean b) {
        if (btnAnalisis != null) {
            btnAnalisis.setEnabled(b);
        }
    }

    @Override
    public void habilitarBotonMedicos(boolean b) {
        if (btnMedicos != null) {
            btnMedicos.setEnabled(b);
        }
    }

    @Override
    public void habilitarBotonObrasSociales(boolean b) {
        if (btnObrasSociales != null) {
            btnObrasSociales.setEnabled(b);
        }
    }

    @Override
    public void habilitarBotonNBU(boolean b) {
        if (btnNBU != null) {
            btnNBU.setEnabled(b);
        }
    }

    @Override
    public void habilitarBotonAjustes(boolean b) {
        if (btnAjustes != null) {
            btnAjustes.setEnabled(b);
        }
    }

    @Override
    public void habilitarBotonGestionUsuarios(boolean b) {
        if (btnGestionUsuarios != null) {
            btnGestionUsuarios.setEnabled(b);
        }
    }

    @Override
    public void habilitarBotonAuditoria(boolean b) {
        if (btnAuditoria != null) {
            btnAuditoria.setEnabled(b);
        }
    }

    @Override
    public void habilitarCargaPacientes(boolean b) {
        if (presenter != null) {
            presenter.setPermisoCargaPacientes(b);
        }
    }

    @Override
    public void habilitarCargaAnalisis(boolean b) {
        if (presenter != null) {
            presenter.setPermisoCargaAnalisis(b);
        }
    }

    @Override
    public void habilitarModificacionRegistros(boolean b) {
        if (presenter != null) {
            presenter.setPermisoModificacion(b);
        }
    }

    @Override
    public void registrarPanel(Object vista, String nombre) {
        if (pnlContenido != null && vista instanceof JPanel) {
            pnlContenido.add((JPanel) vista, nombre);
        }
    }

    @Override
    public void mostrarSeccion(String nombre) {
        if (cardLayout != null && pnlContenido != null) {
            cardLayout.show(pnlContenido, nombre);
            pnlContenido.revalidate();
            pnlContenido.repaint();
        }
    }

    @Override
    public void volverInicio() {
        if (cardLayout != null && pnlContenido != null) {
            cardLayout.show(pnlContenido, "inicio");
            pnlContenido.revalidate();
            pnlContenido.repaint();
        }
    }

    private void configurarCierreVentana() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (presenter != null) {
                    presenter.onCerrarAplicacionCompleta();
                } else {
                    System.exit(0);
                }
            }
        });
    }

    @Override
    public void activarModoInmersion() {
        if (pnlMenuIzquierdo != null) {
            pnlMenuIzquierdo.setVisible(false);
        }
        if (pnlMenuDerecho != null) {
            pnlMenuDerecho.setVisible(false);
        }
        if (pnlFooterCerrar != null) {
            pnlFooterCerrar.setVisible(false);
        }
        if (pnlCentroWrapper != null) {
            pnlCentroWrapper.setBorder(new EmptyBorder(0, 0, 0, 0));
        }

        revalidate();
        repaint();
    }

    @Override
    public void desactivarModoInmersion() {
        if (pnlMenuIzquierdo != null) {
            pnlMenuIzquierdo.setVisible(true);
        }
        if (pnlMenuDerecho != null) {
            pnlMenuDerecho.setVisible(true);
        }
        if (pnlFooterCerrar != null) {
            pnlFooterCerrar.setVisible(true);
        }
        if (pnlCentroWrapper != null) {
            pnlCentroWrapper.setBorder(new EmptyBorder(16, 20, 20, 20));
        }

        revalidate();
        repaint();
    }

    private ImageIcon iconAncho(String ruta, int w) {
        try {
            java.net.URL url = getClass().getResource(ruta);
            if (url == null) {
                return null;
            }

            java.awt.image.BufferedImage original = javax.imageio.ImageIO.read(url);
            if (original == null) {
                return null;
            }

            // Calcular alto proporcional
            int h = (int) ((double) original.getHeight() / original.getWidth() * w);

            java.awt.image.BufferedImage escalada
                    = new java.awt.image.BufferedImage(w, h,
                            java.awt.image.BufferedImage.TYPE_INT_ARGB);

            java.awt.Graphics2D g2d = escalada.createGraphics();
            g2d.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,
                    java.awt.RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2d.setRenderingHint(java.awt.RenderingHints.KEY_RENDERING,
                    java.awt.RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                    java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.drawImage(original, 0, 0, w, h, null);
            g2d.dispose();

            return new ImageIcon(escalada);
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        pnlFondo = new JPanel();
        pnlSidebar = new JPanel();
        new JLabel();
        pnlCuerpo = new JPanel();
        pnlHeader = new JPanel();
        lblFecha = new JLabel();
        lblBienvenida = new JLabel();
        pnlEscritorio = new JPanel();
        new JLabel();

        btnPacientes = new JButton();
        btnAnalisis = new JButton();
        btnMedicos = new JButton();
        btnObrasSociales = new JButton();
        btnNBU = new JButton();
        btnAjustes = new JButton();
        btnGestionUsuarios = new JButton();
        btnAuditoria = new JButton();
        btnCerrarSesion = new JButton();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBackground(C_FONDO);

        pnlFondo.setLayout(new BorderLayout());
        pnlFondo.setBackground(C_FONDO);

        pnlSidebar.setPreferredSize(new Dimension(0, 0));
        pnlSidebar.setVisible(false);
        pnlFondo.add(pnlSidebar, BorderLayout.WEST);

        pnlCuerpo.setLayout(new BorderLayout());
        pnlCuerpo.setBackground(C_FONDO);

        pnlHeader.setBackground(C_NAVY);
        pnlHeader.setPreferredSize(new Dimension(0, 85));
        pnlHeader.setLayout(new BorderLayout());

        lblFecha.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblFecha.setForeground(C_LABEL_HDR);

        lblBienvenida.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblBienvenida.setForeground(C_BLANCO);
        lblBienvenida.setText("BIOTEC");

        pnlCuerpo.add(pnlHeader, BorderLayout.NORTH);

        pnlEscritorio.setLayout(new BorderLayout());
        pnlEscritorio.setBackground(C_FONDO);
        pnlCuerpo.add(pnlEscritorio, BorderLayout.CENTER);

        pnlFondo.add(pnlCuerpo, BorderLayout.CENTER);
        getContentPane().add(pnlFondo, BorderLayout.CENTER);
        pack();
    }

    private JPanel pnlFondo;
    private JPanel pnlSidebar;
    private JPanel pnlCuerpo;
    private JPanel pnlHeader;
    private JLabel lblFecha;
    private JLabel lblBienvenida;
    private JPanel pnlEscritorio;
}
