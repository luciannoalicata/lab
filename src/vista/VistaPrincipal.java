package vista;

import presentador.Controlador;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.*;
import modelo.Usuario;

public class VistaPrincipal extends javax.swing.JFrame implements IVistaPrincipal {

    // ── Paleta BIOTEC Mejorada (Estética Minimalista) ────────────────
    private final Color C_NAVY        = new Color(10, 25, 47);    // Azul más profundo y moderno
    private final Color C_AZUL_OSCURO = new Color(0, 51, 102);
    private final Color C_AZUL_MEDIO  = new Color(30, 110, 180);
    private final Color C_VERDE       = new Color(35, 160, 115);
    private final Color C_ROJO        = new Color(220, 53, 69);
    private final Color C_ROJO_HOV    = new Color(200, 35, 51);
    private final Color C_BTN_CLI     = new Color(245, 248, 252); // Fondo botones clínicos
    private final Color C_BTN_ADM     = new Color(250, 248, 245); // Fondo botones admin
    private final Color C_FONDO       = new Color(238, 242, 246); // Gris azulado suave para el fondo
    private final Color C_BLANCO      = Color.WHITE;
    private final Color C_BORDE       = new Color(215, 225, 235);
    private final Color C_TEXTO_SUAVE = new Color(100, 115, 130);
    private final Color C_TEXTO_FUERTE= new Color(40, 50, 60);
    private final Color C_LABEL_HDR   = new Color(175, 205, 235);

    // ── Botones manuales ─────────────────────────────────────────────
    private javax.swing.JButton btnNBU;
    private javax.swing.JButton btnGestionUsuarios;
    private javax.swing.JButton btnAuditoria;
    private javax.swing.JButton btnMedicos;
    private javax.swing.JButton btnAnalisis;
    private javax.swing.JButton btnObrasSociales;

    // ── Header ───────────────────────────────────────────────────────
    private javax.swing.JLabel lblNombreUsuario;
    private javax.swing.JLabel lblRolUsuario;
    private javax.swing.JLabel lblAvatar;
    private javax.swing.JPanel pnlUsuarioInfo;
    
    private CardLayout cardLayout;
    private JPanel pnlContenido;
    
    private JPanel pnlMenuIzquierdo;
    private JPanel pnlMenuDerecho;
    private JPanel pnlFooterCerrar; // <--- Nuevo panel controlable

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
    //  UI PRINCIPAL
    // ════════════════════════════════════════════════════════════════
    private void construirUI() {
        setTitle("BIOTEC LABORATORIOS — Sistema de Gestión de Laboratorio Clínico");
        setExtendedState(MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1366, 768)); // Estándar moderno
        construirHeader();
        construirCuerpo();
        setLocationRelativeTo(null);
    }

    // ════════════════════════════════════════════════════════════════
    //  HEADER (Rediseño Centrado y Equilibrado)
    // ════════════════════════════════════════════════════════════════
    private void construirHeader() {
        if (pnlHeader == null) return;

        pnlHeader.setBackground(C_NAVY);
        pnlHeader.setBorder(new EmptyBorder(0, 36, 0, 36));
        pnlHeader.setPreferredSize(new Dimension(0, 85)); // Un poco más alto para elegancia
        pnlHeader.setLayout(new BorderLayout());

        // ── Izquierda: Fecha ─────────────────────────────────────────
        JPanel pnlIzq = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 28));
        pnlIzq.setOpaque(false);
        pnlIzq.setPreferredSize(new Dimension(300, 85)); // Ancho fijo para equilibrar
        if (lblFecha != null) {
            lblFecha.setText(new SimpleDateFormat("EEEE dd 'de' MMMM, yyyy").format(new Date()).toUpperCase());
            lblFecha.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lblFecha.setForeground(C_LABEL_HDR);
            pnlIzq.add(lblFecha);
        }
        pnlHeader.add(pnlIzq, BorderLayout.WEST);

        // ── Centro: Título Principal ─────────────────────────────────
        JPanel pnlCentro = new JPanel();
        pnlCentro.setOpaque(false);
        pnlCentro.setLayout(new BoxLayout(pnlCentro, BoxLayout.Y_AXIS));
        
        if (lblBienvenida != null) {
            lblBienvenida.setFont(new Font("Segoe UI", Font.BOLD, 26)); // Título más grande
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

        // ── Derecha: Info de Usuario ─────────────────────────────────
        JPanel pnlDer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 18));
        pnlDer.setOpaque(false);
        pnlDer.setPreferredSize(new Dimension(300, 85)); // Mismo ancho que la izquierda para centrar perfecto

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

    // ════════════════════════════════════════════════════════════════
    //  CUERPO PRINCIPAL
    // ════════════════════════════════════════════════════════════════
    private void construirCuerpo() {
        if (pnlEscritorio == null) return;

        pnlEscritorio.setLayout(new BorderLayout());
        pnlEscritorio.setBackground(C_FONDO); // Fondo general para contraste

        // 1. Instanciamos el contenedor dinámico del centro
        cardLayout = new CardLayout();
        pnlContenido = new JPanel(cardLayout);
        pnlContenido.setBackground(C_BLANCO); 
        
        // Efecto de sombra/borde limpio para la vista central
        pnlContenido.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 210, 220), 1),
            new EmptyBorder(10, 10, 10, 10)
        ));

        pnlContenido.add(construirCentro(), "inicio");

        // Wrapper para darle márgenes al panel central
        JPanel wrapperCentro = new JPanel(new BorderLayout());
        wrapperCentro.setBackground(C_FONDO);
        wrapperCentro.setBorder(new EmptyBorder(25, 25, 25, 25));
        wrapperCentro.add(pnlContenido, BorderLayout.CENTER);

        // 2. Inicializamos los menús laterales y los agregamos al Escritorio
        pnlMenuIzquierdo = construirMenuLateral(true);
        pnlMenuDerecho = construirMenuLateral(false);

        pnlEscritorio.add(pnlMenuIzquierdo, BorderLayout.WEST);
        pnlEscritorio.add(wrapperCentro, BorderLayout.CENTER);
        pnlEscritorio.add(pnlMenuDerecho, BorderLayout.EAST);

        // 3. Franja inferior (Botón a la DERECHA y controlable)
        pnlFooterCerrar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 36, 12));
        pnlFooterCerrar.setBackground(C_BLANCO);
        pnlFooterCerrar.setBorder(new MatteBorder(1, 0, 0, 0, C_BORDE));
        buildBtnCerrar(btnCerrarSesion);
        pnlFooterCerrar.add(btnCerrarSesion);
        
        pnlEscritorio.add(pnlFooterCerrar, BorderLayout.SOUTH);
    }

    // ── Menú lateral (Ensanchado y Letras Grandes) ───────────────────
    private JPanel construirMenuLateral(boolean esIzquierdo) {
        JPanel panel = new JPanel();
        panel.setBackground(C_BLANCO);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(380, 0)); // Menú MUCHO más ancho para textos grandes y accesibilidad

        // Bordes separadores
        if (esIzquierdo) {
            panel.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 0, 1, C_BORDE),
                new EmptyBorder(30, 25, 20, 25) // Padding
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
            panel.add(mkBtn(btnMedicos,       "PROFESIONALES",        "Médicos Solicitantes",              "medico_icon.png",    colorGrupo, C_BTN_CLI));
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

    /** Crea un botón lateral espacioso y legible */
    private JPanel mkBtn(JButton btn, String titulo, String subtitulo, String iconoFile, Color iconColor, Color hoverBg) {
        btn.setLayout(new BorderLayout(0, 0));
        btn.setText("");
        btn.setBackground(C_BLANCO);
        btn.setForeground(C_TEXTO_FUERTE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Botones mucho más altos y cómodos para accesibilidad
        btn.setPreferredSize(new Dimension(330, 95));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 95));
        
        btn.setBorder(new EmptyBorder(10, 15, 10, 15));

        // Ícono más grande
        JLabel lblIco = new JLabel();
        lblIco.setPreferredSize(new Dimension(60, 60));
        lblIco.setHorizontalAlignment(SwingConstants.CENTER);
        ImageIcon ico = icon("/reportes/img/" + iconoFile, 48, 48); // Íconos aumentados a 48x48
        if (ico != null) lblIco.setIcon(ico);

        // Textos más grandes
        JPanel pnlTexto = new JPanel();
        pnlTexto.setOpaque(false);
        pnlTexto.setLayout(new BoxLayout(pnlTexto, BoxLayout.Y_AXIS));
        pnlTexto.setBorder(new EmptyBorder(0, 16, 0, 0));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 19)); // Letra muy grande y visible
        lblTitulo.setForeground(C_TEXTO_FUERTE);

        JLabel lblSub = new JLabel(subtitulo);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Subtítulo claro
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

    // ── Pantalla de Inicio ────────────────────────────────────────────
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
                // Carga la nueva imagen biotec_logo.png
                java.net.URL url = getClass().getResource("/reportes/img/biotec_logo.png");
                if (url != null) {
                    // Mantiene la proporción original de la imagen ajustando el ancho a 350px
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

        card.add(Box.createVerticalStrut(45)); // Mayor separación entre el logo y la fecha

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
        btn.setPreferredSize(new Dimension(200, 42)); // Un poco más pequeño al ir a la derecha
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
        } catch (Exception e) { /* silencioso */ }
        return null;
    }

    // ════════════════════════════════════════════════════════════════
    //  INTERFACE IVistaPrincipal
    // ════════════════════════════════════════════════════════════════
    public void setUsuarioLogueado(Usuario u) {
        if (u == null) return;
        String nombre = u.getUsername().toUpperCase();
        String rol    = u.getRol() != null ? u.getRol() : "";
        lblNombreUsuario.setText(nombre);
        lblRolUsuario.setText(rol);
        if (lblAvatar.getIcon() == null && !nombre.isEmpty())
            lblAvatar.setText(String.valueOf(nombre.charAt(0)));
    }

    @Override public void ejecutar() { setVisible(true); }
    @Override public void mostrarMensaje(String msg) { JOptionPane.showMessageDialog(this, msg); }

    @Override
    public void setControlador(Controlador control) {
        btnPacientes.addActionListener(control);       btnPacientes.setActionCommand(BTN_PACIENTES);
        btnAjustes.addActionListener(control);         btnAjustes.setActionCommand(BTN_AJUSTES);
        btnNBU.addActionListener(control);             btnNBU.setActionCommand(BTN_NBU);
        btnCerrarSesion.addActionListener(control);    btnCerrarSesion.setActionCommand(BTN_CERRAR_SESION);
        btnAuditoria.addActionListener(control);       btnAuditoria.setActionCommand(BTN_AUDITORIA);
        btnMedicos.addActionListener(control);         btnMedicos.setActionCommand(BTN_MEDICOS);
        btnAnalisis.addActionListener(control);        btnAnalisis.setActionCommand(BTN_ANALISIS);
        btnObrasSociales.addActionListener(control);   btnObrasSociales.setActionCommand(BTN_OBRAS_SOCIALES);
        btnGestionUsuarios.addActionListener(control); btnGestionUsuarios.setActionCommand(BTN_GESTION_USUARIOS);
        setUsuarioLogueado(control.getUsuarioLogueado());
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
    public void registrarPanel(Object panel, String nombre) {
        // La vista recibe un objeto genérico (la interfaz) y lo disfraza de JPanel
        pnlContenido.add((javax.swing.JPanel) panel, nombre);
        pnlContenido.revalidate();
        pnlContenido.repaint();
    }

    public void mostrarSeccion(String nombre) {
        cardLayout.show(pnlContenido, nombre);
        // ── ESTA ES LA MAGIA ANTIMANCHAS ──
        pnlContenido.revalidate();
        pnlContenido.repaint();
    }

    @Override
    public void volverInicio() {
        cardLayout.show(pnlContenido, "inicio");
        // ── LIMPIEZA FORZADA ──
        pnlContenido.revalidate();
        pnlContenido.repaint();
    }
    
    @Override
    public void activarModoInmersion() {
        if (pnlMenuIzquierdo != null) pnlMenuIzquierdo.setVisible(false);
        if (pnlMenuDerecho != null) pnlMenuDerecho.setVisible(false);
        // Ocultamos la franja del botón cerrar sesión
        if (pnlFooterCerrar != null) pnlFooterCerrar.setVisible(false); 
        
        // Revalidar para que el centro ocupe todo el espacio hasta abajo
        this.revalidate();
        this.repaint();
    }

    @Override
    public void desactivarModoInmersion() {
        if (pnlMenuIzquierdo != null) pnlMenuIzquierdo.setVisible(true);
        if (pnlMenuDerecho != null) pnlMenuDerecho.setVisible(true);
        // Volvemos a mostrar la franja del botón cerrar sesión
        if (pnlFooterCerrar != null) pnlFooterCerrar.setVisible(true); 
        
        this.revalidate();
        this.repaint();
    }

    // ════════════════════════════════════════════════════════════════
    //  initComponents — NetBeans (NO MODIFICAR)
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