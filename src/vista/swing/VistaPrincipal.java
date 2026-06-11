package vista.swing;

import vista.interfaces.IVistaPrincipal;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.*;
import modelo.Usuario;
import presentador.PrincipalPresenter;

public class VistaPrincipal extends javax.swing.JFrame implements IVistaPrincipal {

    private PrincipalPresenter presenter;
    private javax.swing.JDialog dialogoEspera;
    
    // ── Paleta BIOTEC Mejorada (Estética Minimalista) ────────────────
    private final Color C_NAVY        = new Color(10, 25, 47);    
    private final Color C_AZUL_OSCURO = new Color(0, 51, 102);
    private final Color C_AZUL_MEDIO  = new Color(30, 110, 180);
    private final Color C_VERDE       = new Color(35, 160, 115);
    private final Color C_ROJO        = new Color(220, 53, 69);
    private final Color C_ROJO_HOV    = new Color(200, 35, 51);
    private final Color C_BTN_CLI     = new Color(245, 248, 252); 
    private final Color C_BTN_ADM     = new Color(250, 248, 245); 
    private final Color C_FONDO       = new Color(238, 242, 246); 
    private final Color C_BLANCO      = Color.WHITE;
    private final Color C_BORDE       = new Color(215, 225, 235);
    private final Color C_TEXTO_SUAVE = new Color(100, 115, 130);
    private final Color C_TEXTO_FUERTE= new Color(40, 50, 60);
    private final Color C_LABEL_HDR   = new Color(175, 205, 235);

    private javax.swing.JButton btnNBU;
    private javax.swing.JButton btnGestionUsuarios;
    private javax.swing.JButton btnAuditoria;
    private javax.swing.JButton btnMedicos;
    private javax.swing.JButton btnAnalisis;
    private javax.swing.JButton btnObrasSociales;

    private javax.swing.JLabel lblNombreUsuario;
    private javax.swing.JLabel lblRolUsuario;
    private javax.swing.JLabel lblAvatar;
    private javax.swing.JPanel pnlUsuarioInfo;
    
    private CardLayout cardLayout;
    private JPanel pnlContenido;
    
    private JPanel pnlMenuIzquierdo;
    private JPanel pnlMenuDerecho;
    private JPanel pnlFooterCerrar;

    public VistaPrincipal() {
        btnNBU             = new javax.swing.JButton();
        btnGestionUsuarios = new javax.swing.JButton();
        btnAuditoria       = new javax.swing.JButton();
        btnMedicos         = new javax.swing.JButton();
        btnAnalisis        = new javax.swing.JButton();
        btnObrasSociales   = new javax.swing.JButton();
        lblNombreUsuario   = new javax.swing.JLabel();
        lblRolUsuario      = new javax.swing.JLabel();
        lblAvatar          = new javax.swing.JLabel();
        pnlUsuarioInfo     = new javax.swing.JPanel();

        initComponents();
        construirUI();
    }

    // ════════════════════════════════════════════════════════════════
    //  CONEXIÓN DE BOTONES (MVP)
    // ════════════════════════════════════════════════════════════════
    // Asegúrate de que esto esté dentro de VistaPrincipal
    @Override
    public void setPresenter(PrincipalPresenter presenter) {
        this.presenter = presenter;
        
        System.out.println("DEBUG: Conectando botones en VistaPrincipal...");
        
        btnPacientes.addActionListener(e -> presenter.onPacientes());
        btnAnalisis.addActionListener(e -> presenter.onAnalisis());
        btnMedicos.addActionListener(e -> presenter.onMedicos());
        btnObrasSociales.addActionListener(e -> presenter.onObrasSociales());
        btnNBU.addActionListener(e -> presenter.onNBU());
        btnAjustes.addActionListener(e -> presenter.onAjustes());
        btnGestionUsuarios.addActionListener(e -> presenter.onGestionUsuarios());
        btnAuditoria.addActionListener(e -> presenter.onAuditoria());
        btnCerrarSesion.addActionListener(e -> presenter.onCerrarSesion());
        
        // El truco para los botones con imágenes/paneles internos:
        enlazarClics(btnPacientes, btnPacientes);
        enlazarClics(btnAnalisis, btnAnalisis);
        enlazarClics(btnMedicos, btnMedicos);
        enlazarClics(btnObrasSociales, btnObrasSociales);
        enlazarClics(btnNBU, btnNBU);
        enlazarClics(btnAjustes, btnAjustes);
        enlazarClics(btnGestionUsuarios, btnGestionUsuarios);
        enlazarClics(btnAuditoria, btnAuditoria);
        enlazarClics(btnCerrarSesion, btnCerrarSesion);
    }

    // Método blindado: Si hay un error al abrir la ventana, te avisará en vez de quedarse mudo
    private void ejecutarSeguro(String seccion, Runnable accion) {
        try {
            System.out.println("Abriendo sección: " + seccion + "...");
            accion.run();
        } catch (Exception ex) {
            System.err.println("Error crítico al abrir " + seccion);
            ex.printStackTrace();
            mostrarMensaje("Error interno al abrir la sección " + seccion + ":\n" + ex.getMessage());
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  UI PRINCIPAL Y DISEÑO
    // ════════════════════════════════════════════════════════════════
    private void construirUI() {
        setTitle("BIOTEC LABORATORIOS — Sistema de Gestión de Laboratorio Clínico");
        setExtendedState(MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1520, 950)); 
        construirHeader();
        construirCuerpo();
        setLocationRelativeTo(null);
    }

    private void construirHeader() {
        if (pnlHeader == null) return;
        pnlHeader.setBackground(C_NAVY);
        pnlHeader.setBorder(new EmptyBorder(0, 36, 0, 36));
        pnlHeader.setPreferredSize(new Dimension(0, 85)); 
        pnlHeader.setLayout(new BorderLayout());

        JPanel pnlIzq = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 28));
        pnlIzq.setOpaque(false);
        pnlIzq.setPreferredSize(new Dimension(300, 85)); 
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
        pnlDer.setPreferredSize(new Dimension(300, 85)); 

        pnlUsuarioInfo.setOpaque(false);
        pnlUsuarioInfo.setLayout(new FlowLayout(FlowLayout.RIGHT, 14, 0));

        JPanel pnlTextoUser = new JPanel();
        pnlTextoUser.setOpaque(false);
        pnlTextoUser.setLayout(new BoxLayout(pnlTextoUser, BoxLayout.Y_AXIS));

        lblNombreUsuario.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblNombreUsuario.setForeground(C_BLANCO);
        lblNombreUsuario.setAlignmentX(Component.RIGHT_ALIGNMENT);

        lblRolUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblRolUsuario.setForeground(C_LABEL_HDR);
        lblRolUsuario.setAlignmentX(Component.RIGHT_ALIGNMENT);

        pnlTextoUser.add(lblNombreUsuario);
        pnlTextoUser.add(Box.createVerticalStrut(2));
        pnlTextoUser.add(lblRolUsuario);

        lblAvatar.setPreferredSize(new Dimension(46, 46));
        lblAvatar.setHorizontalAlignment(SwingConstants.CENTER);
        lblAvatar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblAvatar.setForeground(C_BLANCO);
        lblAvatar.setOpaque(true);
        lblAvatar.setBackground(C_AZUL_MEDIO);
        lblAvatar.setBorder(BorderFactory.createLineBorder(C_LABEL_HDR, 1));

        ImageIcon icoUser = icon("/reportes/img/monigote_icon.png", 28, 28);
        if (icoUser != null) { lblAvatar.setIcon(icoUser); lblAvatar.setText(""); }

        pnlUsuarioInfo.add(pnlTextoUser);
        pnlUsuarioInfo.add(lblAvatar);
        pnlDer.add(pnlUsuarioInfo);
        
        pnlHeader.add(pnlDer, BorderLayout.EAST);
    }

    private void construirCuerpo() {
        if (pnlEscritorio == null) return;

        pnlEscritorio.setLayout(new BorderLayout());
        pnlEscritorio.setBackground(C_FONDO); 

        cardLayout = new CardLayout();
        pnlContenido = new JPanel(cardLayout);
        pnlContenido.setBackground(C_BLANCO); 
        
        // ¡CORRECCIÓN CRÍTICA! Quitamos todos los bordes internos
        pnlContenido.setBorder(null);

        pnlContenido.add(construirCentro(), "inicio");

        JPanel wrapperCentro = new JPanel(new BorderLayout());
        wrapperCentro.setBackground(C_FONDO);
        
        // ¡CORRECCIÓN CRÍTICA! Quitamos el margen de 25px para que ocupe el 100% de la pantalla
        wrapperCentro.setBorder(new EmptyBorder(15, 30, 30, 30));
        wrapperCentro.add(pnlContenido, BorderLayout.CENTER);

        pnlMenuIzquierdo = construirMenuLateral(true);
        pnlMenuDerecho = construirMenuLateral(false);

        pnlEscritorio.add(pnlMenuIzquierdo, BorderLayout.WEST);
        pnlEscritorio.add(wrapperCentro, BorderLayout.CENTER);
        pnlEscritorio.add(pnlMenuDerecho, BorderLayout.EAST);

        pnlFooterCerrar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 36, 12));
        pnlFooterCerrar.setBackground(C_BLANCO);
        pnlFooterCerrar.setBorder(new MatteBorder(1, 0, 0, 0, C_BORDE));
        buildBtnCerrar(btnCerrarSesion);
        pnlFooterCerrar.add(btnCerrarSesion);
        
        pnlEscritorio.add(pnlFooterCerrar, BorderLayout.SOUTH);
    }

    private JPanel construirMenuLateral(boolean esIzquierdo) {
        JPanel panel = new JPanel();
        panel.setBackground(C_BLANCO);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(380, 0)); 

        if (esIzquierdo) {
            panel.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 0, 1, C_BORDE),
                new EmptyBorder(30, 25, 20, 25) 
            ));
        } else {
            panel.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 1, 0, 0, C_BORDE),
                new EmptyBorder(30, 25, 20, 25)
            ));
        }

        String tituloGrupo = esIzquierdo ? "ÁREA CLÍNICA" : "ÁREA ADMINISTRATIVA";
        Color colorGrupo   = esIzquierdo ? C_AZUL_MEDIO : new Color(0, 120, 140);

        JPanel pnlTitulo = new JPanel(new BorderLayout());
        pnlTitulo.setOpaque(false);
        pnlTitulo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        pnlTitulo.setBorder(new EmptyBorder(0, 6, 16, 0));

        JLabel lblGrupo = new JLabel(tituloGrupo);
        lblGrupo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblGrupo.setForeground(C_TEXTO_SUAVE);

        pnlTitulo.add(lblGrupo, BorderLayout.NORTH);
        panel.add(pnlTitulo);

        if (esIzquierdo) {
            panel.add(mkBtn(btnPacientes,     "PACIENTES", "Ingresos clínicos",      "paciente_icon.png",  colorGrupo, C_BTN_CLI));
            panel.add(Box.createVerticalStrut(10));
            panel.add(mkBtn(btnAnalisis,      "LISTA ANÁLISIS",     "Resultados",      "auditoria_icon.png", colorGrupo, C_BTN_CLI));
            panel.add(Box.createVerticalStrut(10));
            panel.add(mkBtn(btnMedicos,       "PROFESIONALES",        "Médicos Solicitantes",             "medico_icon.png",    colorGrupo, C_BTN_CLI));
            panel.add(Box.createVerticalStrut(10));
            panel.add(mkBtn(btnObrasSociales, "OBRAS SOCIALES",       "Coberturas y aranceles", "obs_icon.png",       colorGrupo, C_BTN_CLI));
        } else {
            panel.add(mkBtn(btnNBU,             "NBU",    "Prácticas y Determinaciones",       "nbu_icon.png",       colorGrupo, C_BTN_ADM));
            panel.add(Box.createVerticalStrut(10));
            panel.add(mkBtn(btnAuditoria,       "AUDITORÍA",          "Seguridad y Eventos",   "auditoria_icon.png", colorGrupo, C_BTN_ADM));
            panel.add(Box.createVerticalStrut(10));
            panel.add(mkBtn(btnGestionUsuarios, "USUARIOS", "Permisos y accesos",     "usuarios_icon.png",  colorGrupo, C_BTN_ADM));
            panel.add(Box.createVerticalStrut(10));
            panel.add(mkBtn(btnAjustes,         "CONFIGURACIÓN",  "Ajustes del sistema",     "ajustes_icon.png",   colorGrupo, C_BTN_ADM));
        }

        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private JPanel mkBtn(JButton btn, String titulo, String subtitulo, String iconoFile, Color iconColor, Color hoverBg) {
        btn.setLayout(new BorderLayout(0, 0));
        btn.setText("");
        btn.setBackground(C_BLANCO);
        btn.setForeground(C_TEXTO_FUERTE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btn.setPreferredSize(new Dimension(330, 95));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 95));
        btn.setBorder(new EmptyBorder(10, 15, 10, 15));

        JLabel lblIco = new JLabel();
        lblIco.setPreferredSize(new Dimension(60, 60));
        lblIco.setHorizontalAlignment(SwingConstants.CENTER);
        ImageIcon ico = icon("/reportes/img/" + iconoFile, 48, 48); 
        if (ico != null) lblIco.setIcon(ico);

        JPanel pnlTexto = new JPanel();
        pnlTexto.setOpaque(false);
        pnlTexto.setLayout(new BoxLayout(pnlTexto, BoxLayout.Y_AXIS));
        pnlTexto.setBorder(new EmptyBorder(0, 16, 0, 0));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 19)); 
        lblTitulo.setForeground(C_TEXTO_FUERTE);

        JLabel lblSub = new JLabel(subtitulo);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14)); 
        lblSub.setForeground(C_TEXTO_SUAVE);

        pnlTexto.add(lblTitulo);
        pnlTexto.add(Box.createVerticalStrut(4));
        pnlTexto.add(lblSub);

        JLabel flecha = new JLabel("›");
        flecha.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        flecha.setForeground(new Color(190, 205, 220));
        flecha.setBorder(new EmptyBorder(0, 4, 0, 6));

        JPanel contenido = new JPanel(new BorderLayout(0, 0));
        contenido.setOpaque(false);
        contenido.add(lblIco,    BorderLayout.WEST);
        contenido.add(pnlTexto,  BorderLayout.CENTER);
        contenido.add(flecha,    BorderLayout.EAST);

        btn.add(contenido);

        // ¡MAGIA!: Hacemos que cualquier clic dentro de los textos o íconos active el botón
        enlazarClics(contenido, btn);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(hoverBg);
                flecha.setForeground(iconColor); 
                lblTitulo.setForeground(iconColor);
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(C_BLANCO);
                flecha.setForeground(new Color(190, 205, 220));
                lblTitulo.setForeground(C_TEXTO_FUERTE);
            }
        });

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 95));
        wrapper.add(btn, BorderLayout.CENTER);
        return wrapper;
    }

    // 🔥 Este método obliga a que los textos y paneles internos NO se coman el clic 🔥
    private void enlazarClics(Container contenedor, JButton botonDestino) {
        for (Component c : contenedor.getComponents()) {
            c.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    botonDestino.doClick(); // Fuerza al botón principal a activarse
                }
            });
            if (c instanceof Container) {
                enlazarClics((Container) c, botonDestino); // Lo aplica recursivamente
            }
        }
    }

    private JPanel construirCentro() {
        JPanel centro = new JPanel(new GridBagLayout());
        centro.setBackground(C_BLANCO); 

        JPanel card = new JPanel();
        card.setBackground(C_BLANCO);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        if (lblLogoHero != null) {
            lblLogoHero.setAlignmentX(Component.CENTER_ALIGNMENT);
            lblLogoHero.setHorizontalAlignment(SwingConstants.CENTER);
            try {
                java.net.URL url = getClass().getResource("/reportes/img/biotec_logo.png");
                if (url != null) {
                    Image img = new ImageIcon(url).getImage().getScaledInstance(350, -1, Image.SCALE_SMOOTH);
                    lblLogoHero.setIcon(new ImageIcon(img));
                    lblLogoHero.setText("");
                } else {
                    lblLogoHero.setText("BIOTEC");
                    lblLogoHero.setFont(new Font("Segoe UI", Font.BOLD, 48));
                    lblLogoHero.setForeground(C_AZUL_OSCURO);
                }
            } catch (Exception e) {
                lblLogoHero.setText("BIOTEC");
                lblLogoHero.setFont(new Font("Segoe UI", Font.BOLD, 48));
                lblLogoHero.setForeground(C_AZUL_OSCURO);
            }
            card.add(lblLogoHero);
        }

        card.add(Box.createVerticalStrut(45));

        String fechaStr = new SimpleDateFormat("EEEE, dd 'de' MMMM 'de' yyyy").format(new Date());
        fechaStr = fechaStr.substring(0, 1).toUpperCase() + fechaStr.substring(1);
        
        JLabel lblFechaCard = new JLabel(fechaStr);
        lblFechaCard.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblFechaCard.setForeground(C_TEXTO_SUAVE);
        lblFechaCard.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblFechaCard);

        card.add(Box.createVerticalStrut(36));

        JPanel pnlChips = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 0));
        pnlChips.setOpaque(false);
        pnlChips.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlChips.add(chip("● Sistema Operativo", C_VERDE));
        pnlChips.add(chip("● Servidor Conectado", C_AZUL_MEDIO));
        card.add(pnlChips);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        centro.add(card, gbc);
        return centro;
    }

    private void buildBtnCerrar(JButton btn) {
        btn.setText("  ⏻  CERRAR SESIÓN");
        btn.setBackground(C_ROJO);
        btn.setForeground(C_BLANCO);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(200, 42)); 
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        ImageIcon ico = icon("/reportes/img/cerrar_icon.png", 18, 18);
        if (ico != null) btn.setIcon(ico);
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(C_ROJO_HOV); }
            @Override public void mouseExited (java.awt.event.MouseEvent e) { btn.setBackground(C_ROJO); }
        });
    }

    private JLabel chip(String texto, Color color) {
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
            if (url != null) {
                Image img = new ImageIcon(url).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
                return new ImageIcon(img);
            }
        } catch (Exception e) {}
        return null;
    }

    // ════════════════════════════════════════════════════════════════
    //  INTERFACE IVistaPrincipal
    // ════════════════════════════════════════════════════════════════
    public void setUsuarioLogueado(String nombreUsuario) {
        lblNombreUsuario.setText(nombreUsuario.toUpperCase()); 
    }

    @Override public void ejecutar() { setVisible(true); }

    @Override
    public void mostrarAvisoBackup(boolean mostrar) {
        if (mostrar) {
            Object[] options = {}; 
            javax.swing.JOptionPane pane = new javax.swing.JOptionPane(
                    "Generando copia de seguridad...\nPor favor, no cierre el programa.",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE, 
                    javax.swing.JOptionPane.DEFAULT_OPTION, null, options, null);

            dialogoEspera = pane.createDialog(this, "Copia de Seguridad en curso");
            dialogoEspera.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

            new Thread(() -> dialogoEspera.setVisible(true)).start();
        } else {
            if (dialogoEspera != null) dialogoEspera.dispose();
        }
    }

    @Override
    public void mostrarMensaje(String mensaje) {
        javax.swing.JOptionPane.showMessageDialog(this, mensaje);
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
        return javax.swing.JOptionPane.showConfirmDialog(
                this, mensaje, titulo, javax.swing.JOptionPane.YES_NO_OPTION);
    }

    @Override public void habilitarBotonPacientes(boolean b)       { btnPacientes.setEnabled(b); }
    @Override public void habilitarBotonAnalisis(boolean b)        { btnAnalisis.setEnabled(b); }
    @Override public void habilitarBotonMedicos(boolean b)         { btnMedicos.setEnabled(b); }
    @Override public void habilitarBotonObrasSociales(boolean b)   { btnObrasSociales.setEnabled(b); }
    @Override public void habilitarBotonNBU(boolean b)             { btnNBU.setEnabled(b); }
    @Override public void habilitarBotonAjustes(boolean b)         { btnAjustes.setEnabled(b); }
    @Override public void habilitarBotonGestionUsuarios(boolean b) { btnGestionUsuarios.setEnabled(b); }
    @Override public void habilitarBotonAuditoria(boolean b)       { btnAuditoria.setEnabled(b); }

    @Override
    public void registrarPanel(Object vista, String nombre) {
        this.pnlContenido.add((javax.swing.JPanel) vista, nombre);
    }

    public void mostrarSeccion(String nombre) {
        cardLayout.show(pnlContenido, nombre);
        pnlContenido.revalidate();
        pnlContenido.repaint();
    }

    @Override
    public void volverInicio() {
        cardLayout.show(pnlContenido, "inicio");
        pnlContenido.revalidate();
        pnlContenido.repaint();
    }
    
    @Override
    public void activarModoInmersion() {
        if (pnlMenuIzquierdo != null) pnlMenuIzquierdo.setVisible(false);
        if (pnlMenuDerecho != null) pnlMenuDerecho.setVisible(false);
        if (pnlFooterCerrar != null) pnlFooterCerrar.setVisible(false); 
        this.getContentPane().invalidate();
        this.getContentPane().validate();
        this.getContentPane().repaint();
    }

    @Override
    public void desactivarModoInmersion() {
        if (pnlMenuIzquierdo != null) pnlMenuIzquierdo.setVisible(true);
        if (pnlMenuDerecho != null) pnlMenuDerecho.setVisible(true);
        if (pnlFooterCerrar != null) pnlFooterCerrar.setVisible(true); 
        this.revalidate();
        this.repaint();
    }

    // ════════════════════════════════════════════════════════════════
    //  initComponents — NetBeans
    // ════════════════════════════════════════════════════════════════
    @SuppressWarnings("unchecked")
    private void initComponents() {
        pnlFondo      = new javax.swing.JPanel();
        pnlSidebar    = new javax.swing.JPanel();
        lblMenuTitulo = new javax.swing.JLabel();
        btnPacientes  = new javax.swing.JButton();
        btnAjustes    = new javax.swing.JButton();
        btnCerrarSesion = new javax.swing.JButton();
        pnlCuerpo     = new javax.swing.JPanel();
        pnlHeader     = new javax.swing.JPanel();
        lblFecha      = new javax.swing.JLabel();
        lblBienvenida = new javax.swing.JLabel();
        pnlEscritorio = new javax.swing.JPanel();
        lblLogoHero   = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        pnlFondo.setLayout(new java.awt.BorderLayout());

        pnlSidebar.setPreferredSize(new java.awt.Dimension(0, 0));
        pnlSidebar.setVisible(false);
        pnlFondo.add(pnlSidebar, java.awt.BorderLayout.WEST);

        pnlCuerpo.setLayout(new java.awt.BorderLayout());

        pnlHeader.setBackground(new java.awt.Color(0, 35, 75));
        pnlHeader.setPreferredSize(new java.awt.Dimension(0, 76));
        pnlHeader.setLayout(new java.awt.BorderLayout());
        lblFecha.setText("FECHA");
        lblBienvenida.setText("BIOTEC");
        pnlCuerpo.add(pnlHeader, java.awt.BorderLayout.NORTH);

        pnlEscritorio.setLayout(new java.awt.BorderLayout());
        lblLogoHero.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLogoHero.setText("BIOTEC");
        pnlEscritorio.add(lblLogoHero, java.awt.BorderLayout.CENTER);
        pnlCuerpo.add(pnlEscritorio, java.awt.BorderLayout.CENTER);

        pnlFondo.add(pnlCuerpo, java.awt.BorderLayout.CENTER);
        getContentPane().add(pnlFondo, java.awt.BorderLayout.CENTER);
        pack();
    }

    // ── Variables NetBeans ───────────────────────────────────────────
    private javax.swing.JButton btnAjustes;
    private javax.swing.JButton btnCerrarSesion;
    private javax.swing.JButton btnPacientes;
    private javax.swing.JLabel  lblBienvenida;
    private javax.swing.JLabel  lblFecha;
    private javax.swing.JLabel  lblLogoHero;
    private javax.swing.JLabel  lblMenuTitulo;
    private javax.swing.JPanel  pnlCuerpo;
    private javax.swing.JPanel  pnlEscritorio;
    private javax.swing.JPanel  pnlFondo;
    private javax.swing.JPanel  pnlHeader;
    private javax.swing.JPanel  pnlSidebar;
}