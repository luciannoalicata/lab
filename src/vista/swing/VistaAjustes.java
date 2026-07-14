package vista.swing;

import vista.interfaces.IVistaAjustes;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;
import javax.swing.border.*;
import presentador.AjustesPresenter;

public class VistaAjustes extends javax.swing.JDialog implements IVistaAjustes {

    private AjustesPresenter presenter;

    // ── Tipografía Compacta pero Legible ────────────────────────────
    private final Font F_SECTION = new Font("Segoe UI", Font.BOLD, 12);
    private final Font F_LABEL   = new Font("Segoe UI", Font.PLAIN, 11);
    private final Font F_CAMPO   = new Font("Segoe UI", Font.PLAIN, 12);
    private final Font F_BTN     = new Font("Segoe UI", Font.BOLD, 11);
    private final Font F_UB      = new Font("Segoe UI", Font.BOLD, 28);
    private final Font F_TAB     = new Font("Segoe UI", Font.BOLD, 12);

    // ── Paleta BIOTEC ─────────────────────────────────────────────────
    private final Color C_NAVY      = new Color(0, 35, 75);
    private final Color C_AZUL      = new Color(0, 51, 102);
    private final Color C_AZUL_MED  = new Color(0, 102, 153);
    private final Color C_VERDE     = new Color(0, 153, 102);
    private final Color C_GRIS      = new Color(100, 115, 130);
    private final Color C_FONDO     = new Color(238, 244, 250);
    private final Color C_BLANCO    = Color.WHITE;
    private final Color C_BORDE     = new Color(210, 220, 232);
    private final Color C_CAMPO = new Color(248, 251, 255);
    private final Color C_CAMPO_RO = new Color(242, 245, 248);
    private final Color C_TEXTO = new Color(40, 60, 85);
    private final Color C_LABEL_HDR = new Color(160, 200, 230);

    private JFileChooser fileChooser;

    public VistaAjustes(Object parentView) {
        super(parentView instanceof java.awt.Frame ? (java.awt.Frame) parentView : null, true);
        initComponents();
        aplicarEstiloCompacto();
        poblarCombos();

        fileChooser = new JFileChooser();

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(700, 480));
        setPreferredSize(new Dimension(780, 540));

        // ── ICONO ──
        try {
            java.net.URL url = getClass().getResource("/reportes/img/logo_sw.png");
            if (url != null) {
                Image img = new ImageIcon(url).getImage();
                setIconImage(img);
            }
        } catch (Exception e) {
        }

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                if (presenter != null) {
                    presenter.onVolver();
                }
            }
        });
    }

    // ════════════════════════════════════════════════════════════════
    //  INTERFAZ IVistaAjustes - MÉTODOS MVP
    // ════════════════════════════════════════════════════════════════
    @Override
    public void setPresenter(AjustesPresenter presenter) {
        this.presenter = presenter;

        limpiarListeners(btnActualizarClave);
        limpiarListeners(btnActualizarDatos);
        limpiarListeners(btnGuardarConfiguracion);
        limpiarListeners(btnGuardarUB);
        limpiarListeners(btnBuscarLogo);
        limpiarListeners(btnBuscarFirma);
        limpiarListeners(btnBuscarRutaPdf);
        limpiarListeners(btnBuscarRutaBackup);

        btnActualizarClave.addActionListener(e -> presenter.onActualizarClave());
        btnActualizarDatos.addActionListener(e -> presenter.onActualizarDatos());
        btnGuardarConfiguracion.addActionListener(e -> presenter.onGuardarConfiguracion());
        btnGuardarUB.addActionListener(e -> presenter.onGuardarUB());

        btnBuscarLogo.addActionListener(e -> seleccionarImagen(txtBuscarLogo, "Seleccionar Logo", false));
        btnBuscarFirma.addActionListener(e -> seleccionarImagen(txtFirma, "Seleccionar Firma", false));
        btnBuscarRutaPdf.addActionListener(e -> seleccionarDirectorio(txtRutaPdf, "Carpeta Informes PDF"));
        btnBuscarRutaBackup.addActionListener(e -> seleccionarDirectorio(txtRutaBackup, "Carpeta Backups"));
    }
    
    private void limpiarListeners(JButton btn) {
        for (ActionListener al : btn.getActionListeners()) {
            btn.removeActionListener(al);
        }
    }
    
    private void seleccionarImagen(JTextField campo, String titulo, boolean carpeta) {
        SwingUtilities.invokeLater(() -> {
            fileChooser.setDialogTitle(titulo);
            fileChooser.setFileSelectionMode(carpeta ? JFileChooser.DIRECTORIES_ONLY : JFileChooser.FILES_ONLY);
            
            if (!carpeta) {
                fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                    "Imágenes (*.png, *.jpg, *.jpeg, *.gif)", "png", "jpg", "jpeg", "gif"));
            } else {
                fileChooser.setFileFilter(null);
            }
            
            int resultado = fileChooser.showOpenDialog(this);
            if (resultado == JFileChooser.APPROVE_OPTION) {
                campo.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });
    }
    
    private void seleccionarDirectorio(JTextField campo, String titulo) {
        SwingUtilities.invokeLater(() -> {
            fileChooser.setDialogTitle(titulo);
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setFileFilter(null);
            
            int resultado = fileChooser.showOpenDialog(this);
            if (resultado == JFileChooser.APPROVE_OPTION) {
                campo.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });
    }

    @Override
    public void limpiarFocos() { this.requestFocusInWindow(); }

    @Override
    public int confirmarAccion(String mensaje, String titulo) {
        return JOptionPane.showConfirmDialog(this, mensaje, titulo, JOptionPane.YES_NO_OPTION);
    }
    
    @Override
    public void cerrarPantalla() { this.dispose(); }

    @Override public void ejecutar() { setVisible(true); }
    @Override public void mostrarMensaje(String m) { JOptionPane.showMessageDialog(this, m); }
    @Override public void limpiarCampos() { 
        txtClaveActual.setText(""); 
        txtNuevaClave.setText(""); 
        txtRepetirNuevaClave.setText(""); 
    }
    
    @Override public String getClaveActual()   { return new String(txtClaveActual.getPassword()); }
    @Override public String getNuevaClave()    { return new String(txtNuevaClave.getPassword()); }
    @Override public String getRepetirNuevaClave() { return new String(txtRepetirNuevaClave.getPassword()); }
    @Override public String getNombreLaboratorio()  { return txtNombreLaboratorio.getText().trim(); }
    @Override public String getDireccion()     { return txtDireccion.getText().trim(); }
    @Override public String getLocalidad()     { return txtLocalidad.getText().trim(); }
    @Override public String getTelefono()      { return txtTelefono.getText().trim(); }
    @Override public String getBioquimico()    { return txtBioquimico.getText().trim(); }
    @Override public String getMatricula()     { return txtMatricula.getText().trim(); }
    @Override public String getLogo()          { return txtBuscarLogo.getText().trim(); }
    @Override public String getFirma()         { return txtFirma.getText().trim(); }
    @Override public String getRutaPdf()       { return txtRutaPdf.getText().trim(); }
    @Override public String getRutaBackup()    { return txtRutaBackup.getText().trim(); }
    @Override public String getTamanoHoja()    { return cbxTamanoHoja.getSelectedItem().toString(); }
    @Override public String getOrientacion()   { return cbxOrientacion.getSelectedItem().toString(); }
    @Override public boolean isIncluirLogo()   { return chkIncluirLogo.isSelected(); }
    @Override public boolean isAutoPrint()     { return chkImprimirAutom.isSelected(); }
    @Override public String getValorUB()       { return txtValorUB.getText().trim(); }
    
    @Override public void setUsuarioActual(String u)           { lblUsuarioActual.setText(u.toUpperCase()); }
    @Override public void setNombreLaboratorioACtual(String n) { txtNombreLaboratorio.setText(n); }
    @Override public void setDireccion(String d)   { txtDireccion.setText(d); }
    @Override public void setLocalidad(String l)   { txtLocalidad.setText(l); }
    @Override public void setTelefono(String t)    { txtTelefono.setText(t); }
    @Override public void setBioquimico(String b)  { txtBioquimico.setText(b); }
    @Override public void setMatricula(String m)   { txtMatricula.setText(m); }
    @Override public void setLogo(String l)        { txtBuscarLogo.setText(l); }
    @Override public void setFirma(String f)       { txtFirma.setText(f); }
    @Override public void setRutaBackup(String r)  { txtRutaBackup.setText(r); }
    @Override public void setRutaPdf(String p)     { txtRutaPdf.setText(p); }
    @Override public void setTamanoHoja(String t)  { cbxTamanoHoja.setSelectedItem(t); }
    @Override public void setOrientacion(String o) { cbxOrientacion.setSelectedItem(o); }
    @Override public void setIncluirLogo(boolean i){ chkIncluirLogo.setSelected(i); }
    @Override public void setAutoPrint(boolean a)  { chkImprimirAutom.setSelected(a); }
    @Override public void setValorUB(String v)     { txtValorUB.setText(v); }
    @Override public void setBackup(String b)      { txtRutaBackup.setText(b); }
    @Override public String getMatriculaFirma()    { return ""; }
    @Override public String getAclaracionFirma()   { return ""; }
    @Override public void setMatriculaFirma(String m) {}
    @Override public void setAclaracionFirma(String a) {}

    @Override
    public void habilitarSeccionAranceles(boolean h) {
        txtValorUB.setEnabled(h);
        btnGuardarUB.setEnabled(h);
    }

    // ════════════════════════════════════════════════════════════════
    //  ESTILO COMPACTO - Optimizado para 14"
    // ════════════════════════════════════════════════════════════════
    private void aplicarEstiloCompacto() {
        setTitle("CONFIGURACIÓN DEL SISTEMA");
        setResizable(true);
        getContentPane().setBackground(C_FONDO);

        jTabbedPane1.setFont(F_TAB);
        jTabbedPane1.setBackground(C_FONDO);
        jTabbedPane1.setBorder(new EmptyBorder(0, 0, 0, 0));

        // Campos de rutas
        for (JTextField f : new JTextField[]{txtBuscarLogo, txtFirma, txtRutaPdf, txtRutaBackup}) {
            f.setEditable(false);
            f.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            f.setBackground(C_CAMPO_RO);
            f.setForeground(C_TEXTO);
            f.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, C_BORDE),
                new EmptyBorder(4, 8, 4, 8)
            ));
            f.setPreferredSize(new Dimension(420, 30));
        }

        // Campos de texto institucionales
        for (JTextField f : new JTextField[]{txtNombreLaboratorio, txtDireccion,
                txtLocalidad, txtTelefono, txtBioquimico, txtMatricula}) {
            estilizarCampo(f, 280, 32);
        }

        // Campos de contraseña
        for (JPasswordField f : new JPasswordField[]{txtClaveActual, txtNuevaClave, txtRepetirNuevaClave}) {
            f.setFont(F_CAMPO);
            f.setBackground(C_CAMPO);
            f.setForeground(C_TEXTO);
            f.setCaretColor(C_AZUL_MED);
            f.setPreferredSize(new Dimension(240, 32));
            f.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 2, 0, C_AZUL_MED),
                new EmptyBorder(4, 8, 4, 8)
            ));
        }

        txtValorUB.setFont(F_UB);
        txtValorUB.setBackground(C_CAMPO);
        txtValorUB.setForeground(C_AZUL);
        txtValorUB.setHorizontalAlignment(JTextField.CENTER);
        txtValorUB.setPreferredSize(new Dimension(150, 50));
        txtValorUB.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(C_AZUL_MED, 2, true),
            new EmptyBorder(4, 10, 4, 10)
        ));

        for (JComboBox<?> cb : new JComboBox[]{cbxTamanoHoja, cbxOrientacion}) {
            cb.setFont(F_CAMPO);
            cb.setBackground(C_CAMPO);
            cb.setPreferredSize(new Dimension(140, 30));
        }

        estilizarBtn(btnActualizarClave, C_AZUL_MED, "ACTUALIZAR", 180, 32);
        estilizarBtn(btnActualizarDatos, C_VERDE, "GUARDAR", 150, 32);
        estilizarBtn(btnGuardarConfiguracion, C_AZUL_MED, "GUARDAR", 180, 32);
        estilizarBtn(btnGuardarUB, C_VERDE, "GUARDAR", 150, 36);

        ImageIcon iconCarpeta = icon("/reportes/img/carpeta_icon.png", 16, 16);

        for (JButton b : new JButton[]{btnBuscarLogo, btnBuscarFirma, btnBuscarRutaPdf, btnBuscarRutaBackup}) {
            if (iconCarpeta != null) {
                b.setIcon(iconCarpeta);
                b.setText("");
            } else {
                b.setText("📁");
            }
            b.setBackground(new Color(215, 228, 245));
            b.setForeground(C_AZUL);
            b.setFont(new Font("Segoe UI", Font.BOLD, 12));
            b.setCursor(new Cursor(Cursor.HAND_CURSOR));
            b.setFocusPainted(false);
            b.setBorderPainted(false);
            b.setPreferredSize(new Dimension(36, 30));
        }

        for (JCheckBox chk : new JCheckBox[]{chkIncluirLogo, chkImprimirAutom}) {
            chk.setFont(F_CAMPO);
            chk.setForeground(C_TEXTO);
            chk.setOpaque(false);
        }

        lblUsuarioActual.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUsuarioActual.setForeground(C_BLANCO);

        pack();
        setLocationRelativeTo(null);
    }

    private void estilizarCampo(JTextField tf, int w, int h) {
        tf.setFont(F_CAMPO);
        tf.setBackground(C_CAMPO);
        tf.setForeground(C_TEXTO);
        tf.setCaretColor(C_AZUL_MED);
        tf.setPreferredSize(new Dimension(w, h));
        tf.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(C_BORDE, 1, true),
            new EmptyBorder(4, 8, 4, 8)
        ));
    }

    private void estilizarBtn(JButton b, Color bg, String txt, int w, int h) {
        b.setText(txt);
        b.setBackground(bg);
        b.setForeground(C_BLANCO);
        b.setFont(F_BTN);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(w, h));
    }

    private void poblarCombos() {
        cbxTamanoHoja.setModel(new DefaultComboBoxModel<>(new String[]{"A4", "A5"}));
        cbxOrientacion.setModel(new DefaultComboBoxModel<>(new String[]{"Vertical", "Horizontal"}));
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
    //  initComponents
    // ════════════════════════════════════════════════════════════════
    @SuppressWarnings("unchecked")
    private void initComponents() {
        jTabbedPane1         = new JTabbedPane();
        pnlSeguridad         = new JPanel(new GridBagLayout());
        pnlLaboratorio       = new JPanel(new GridBagLayout());
        pnlImpresion         = new JPanel(new GridBagLayout());
        pnlAranceles         = new JPanel(new GridBagLayout());

        lblUsuarioActual     = new JLabel("USUARIO");
        txtClaveActual       = new JPasswordField();
        txtNuevaClave        = new JPasswordField();
        txtRepetirNuevaClave = new JPasswordField();
        btnActualizarClave   = new JButton();

        txtNombreLaboratorio = new JTextField();
        txtDireccion         = new JTextField();
        txtLocalidad         = new JTextField();
        txtTelefono          = new JTextField();
        txtBioquimico        = new JTextField();
        txtMatricula         = new JTextField();
        txtBuscarLogo        = new JTextField();
        txtFirma             = new JTextField();
        txtRutaPdf           = new JTextField();
        txtRutaBackup        = new JTextField();
        btnBuscarLogo        = new JButton("📁");
        btnBuscarFirma       = new JButton("📁");
        btnBuscarRutaPdf     = new JButton("📁");
        btnBuscarRutaBackup  = new JButton("📁");
        btnActualizarDatos   = new JButton();

        cbxTamanoHoja        = new JComboBox<>();
        cbxOrientacion       = new JComboBox<>();
        chkIncluirLogo       = new JCheckBox("Incluir logotipo");
        chkImprimirAutom     = new JCheckBox("Exportar PDF automático");
        btnGuardarConfiguracion = new JButton();

        txtValorUB           = new JTextField();
        btnGuardarUB         = new JButton();

        setLayout(new BorderLayout());

        JPanel pnlHeaderGlobal = new JPanel(new BorderLayout());
        pnlHeaderGlobal.setBackground(C_NAVY);
        pnlHeaderGlobal.setBorder(new EmptyBorder(8, 16, 8, 16));

        JPanel pnlHdrIzq = new JPanel();
        pnlHdrIzq.setOpaque(false);
        pnlHdrIzq.setLayout(new BoxLayout(pnlHdrIzq, BoxLayout.Y_AXIS));

        JLabel lblHdrTag = new JLabel("SISTEMA BIOTEC — CONFIGURACIÓN");
        lblHdrTag.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lblHdrTag.setForeground(C_LABEL_HDR);
        lblHdrTag.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblHdrTitulo = new JLabel("Ajustes del Sistema");
        lblHdrTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHdrTitulo.setForeground(C_BLANCO);
        lblHdrTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        pnlHdrIzq.add(lblHdrTag);
        pnlHdrIzq.add(Box.createVerticalStrut(2));
        pnlHdrIzq.add(lblHdrTitulo);
        pnlHeaderGlobal.add(pnlHdrIzq, BorderLayout.WEST);

        JPanel pnlHdrDer = new JPanel();
        pnlHdrDer.setOpaque(false);
        pnlHdrDer.setLayout(new BoxLayout(pnlHdrDer, BoxLayout.Y_AXIS));

        JLabel lblTagUser = new JLabel("USUARIO");
        lblTagUser.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lblTagUser.setForeground(C_LABEL_HDR);
        lblTagUser.setAlignmentX(Component.RIGHT_ALIGNMENT);

        lblUsuarioActual.setAlignmentX(Component.RIGHT_ALIGNMENT);
        pnlHdrDer.add(lblTagUser);
        pnlHdrDer.add(Box.createVerticalStrut(2));
        pnlHdrDer.add(lblUsuarioActual);
        pnlHeaderGlobal.add(pnlHdrDer, BorderLayout.EAST);

        add(pnlHeaderGlobal, BorderLayout.NORTH);

        for (JPanel tab : new JPanel[]{pnlSeguridad, pnlLaboratorio, pnlImpresion, pnlAranceles}) {
            tab.setBackground(C_FONDO);
        }

        // ── Seguridad ──────────────────────────────────────────────────
        JPanel cardClave = card("CAMBIO DE CONTRASEÑA");
        GridBagConstraints gf = new GridBagConstraints();
        gf.fill = GridBagConstraints.HORIZONTAL; gf.weightx = 1.0; gf.gridx = 0;

        gf.gridy = 1; gf.insets = new Insets(0,0,2,0);    cardClave.add(fldLabel("Contraseña actual"), gf);
        gf.gridy = 2; gf.insets = new Insets(0,0,8,0);   cardClave.add(txtClaveActual, gf);
        gf.gridy = 3; gf.insets = new Insets(0,0,2,0);    cardClave.add(fldLabel("Nueva contraseña"), gf);
        gf.gridy = 4; gf.insets = new Insets(0,0,8,0);   cardClave.add(txtNuevaClave, gf);
        gf.gridy = 5; gf.insets = new Insets(0,0,2,0);    cardClave.add(fldLabel("Confirmar nueva"), gf);
        gf.gridy = 6; gf.insets = new Insets(0,0,12,0);   cardClave.add(txtRepetirNuevaClave, gf);

        gf.gridy = 7; gf.fill = GridBagConstraints.NONE; gf.anchor = GridBagConstraints.EAST;
        gf.insets = new Insets(0,0,0,0);
        cardClave.add(btnActualizarClave, gf);

        addCenteredCard(pnlSeguridad, cardClave, 16);

        // ── Laboratorio ─────────────────────────────────────────────────
        JPanel cardDatos = card("DATOS INSTITUCIONALES");
        GridBagConstraints gd = new GridBagConstraints();
        gd.fill = GridBagConstraints.HORIZONTAL; gd.weightx = 1.0; gd.gridx = 0;

        int rd = 1;
        for (String[] lf : new String[][]{
                {"Nombre del laboratorio"}, {"Dirección"},
                {"Localidad"}, {"Teléfono"},
                {"Bioquímico"}, {"Matrícula"}}) {
            gd.gridy = rd++; gd.insets = new Insets(0,0,1,0);
            cardDatos.add(fldLabel(lf[0]), gd);
            gd.gridy = rd++; gd.insets = new Insets(0,0,6,0);
        }
        
        rd = 2;
        for (JTextField tf : new JTextField[]{txtNombreLaboratorio, txtDireccion,
                txtLocalidad, txtTelefono, txtBioquimico, txtMatricula}) {
            gd.gridy = rd; gd.insets = new Insets(0,0,6,0);
            cardDatos.add(tf, gd); rd += 2;
        }

        JPanel cardRutas = card("RECURSOS Y RUTAS");
        GridBagConstraints gr = new GridBagConstraints();
        gr.fill = GridBagConstraints.HORIZONTAL; gr.weightx = 1.0; gr.gridx = 0;
        int rr = 1;
        String[] labelsRutas = {"Logotipo", "Firma", "Informes PDF", "Backups"};
        JTextField[] camposRutas = {txtBuscarLogo, txtFirma, txtRutaPdf, txtRutaBackup};
        JButton[] botonesRutas   = {btnBuscarLogo, btnBuscarFirma, btnBuscarRutaPdf, btnBuscarRutaBackup};

        for (int i = 0; i < labelsRutas.length; i++) {
            gr.gridy = rr++; gr.gridwidth = 2; gr.insets = new Insets(0,0,1,0);
            cardRutas.add(fldLabel(labelsRutas[i]), gr);

            JPanel filaRuta = new JPanel(new BorderLayout(4, 0));
            filaRuta.setOpaque(false);
            filaRuta.add(camposRutas[i], BorderLayout.CENTER);
            filaRuta.add(botonesRutas[i], BorderLayout.EAST);

            gr.gridy = rr++; gr.gridwidth = 2; gr.insets = new Insets(0,0,6,0);
            cardRutas.add(filaRuta, gr);
        }

        GridBagConstraints gbs = new GridBagConstraints();
        gbs.gridx = 2; gbs.gridy = rr; gbs.gridwidth = 1;
        gbs.fill = GridBagConstraints.NONE; gbs.anchor = GridBagConstraints.SOUTHEAST;
        gbs.insets = new Insets(6, 0, 0, 0);
        cardRutas.add(btnActualizarDatos, gbs);

        GridBagConstraints gcL = new GridBagConstraints();
        gcL.gridy = 0; gcL.weighty = 1.0; 
        gcL.fill = GridBagConstraints.HORIZONTAL; 
        gcL.anchor = GridBagConstraints.NORTH;    
        gcL.gridx = 0; gcL.weightx = 0.50; gcL.insets = new Insets(8, 10, 8, 6);
        pnlLaboratorio.add(cardDatos, gcL);
        gcL.gridx = 1; gcL.weightx = 0.50; gcL.insets = new Insets(8, 6, 8, 10);
        pnlLaboratorio.add(cardRutas, gcL);

        // ── Impresión ──────────────────────────────────────────────────
        JPanel cardFormato = card("CONFIGURACIÓN DE PÁGINA");

        JPanel filaCombos = new JPanel(new GridLayout(1, 2, 16, 0));
        filaCombos.setOpaque(false);
        filaCombos.add(comboPanel("Tamaño", cbxTamanoHoja));
        filaCombos.add(comboPanel("Orientación", cbxOrientacion));

        GridBagConstraints gcCombos = new GridBagConstraints();
        gcCombos.gridx = 0; gcCombos.gridy = 1; gcCombos.gridwidth = 2;
        gcCombos.fill = GridBagConstraints.HORIZONTAL; gcCombos.weightx = 1.0;
        gcCombos.insets = new Insets(4, 0, 6, 0);
        cardFormato.add(filaCombos, gcCombos);

        JPanel cardOpc = card("PREFERENCIAS");
        GridBagConstraints gcChk = new GridBagConstraints();
        gcChk.gridx = 0; gcChk.fill = GridBagConstraints.HORIZONTAL; gcChk.weightx = 1.0;

        gcChk.gridy = 1; gcChk.insets = new Insets(2, 0, 4, 0);
        cardOpc.add(chkIncluirLogo, gcChk);
        gcChk.gridy = 2; gcChk.insets = new Insets(0, 0, 4, 0);
        cardOpc.add(chkImprimirAutom, gcChk);
        gcChk.gridy = 3; gcChk.fill = GridBagConstraints.NONE;
        gcChk.anchor = GridBagConstraints.EAST; gcChk.insets = new Insets(10, 0, 0, 0);
        cardOpc.add(btnGuardarConfiguracion, gcChk);

        JPanel colImp = new JPanel();
        colImp.setOpaque(false);
        colImp.setLayout(new BoxLayout(colImp, BoxLayout.Y_AXIS));
        colImp.add(cardFormato);
        colImp.add(Box.createVerticalStrut(8));
        colImp.add(cardOpc);
        
        addCenteredCard(pnlImpresion, colImp, 16);

        // ── Aranceles ──────────────────────────────────────────────────
        JPanel cardAran = card("UNIDAD BIOQUÍMICA (UB)");

        JLabel lblDesc = new JLabel("Valor base para el cálculo automático del precio de los análisis.");
        lblDesc.setFont(F_CAMPO); 
        lblDesc.setForeground(C_GRIS);

        GridBagConstraints gcDesc = new GridBagConstraints();
        gcDesc.gridx = 0; gcDesc.gridy = 1; gcDesc.gridwidth = 2;
        gcDesc.fill = GridBagConstraints.HORIZONTAL; gcDesc.weightx = 1.0;
        gcDesc.insets = new Insets(2, 0, 12, 0);
        cardAran.add(lblDesc, gcDesc);

        JPanel filaUB = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        filaUB.setOpaque(false);

        JLabel lblSymbol = new JLabel("$");
        lblSymbol.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblSymbol.setForeground(C_VERDE);

        filaUB.add(lblSymbol);
        filaUB.add(txtValorUB);

        GridBagConstraints gcUB = new GridBagConstraints();
        gcUB.gridx = 0; gcUB.gridy = 2; gcUB.gridwidth = 2;
        gcUB.fill = GridBagConstraints.HORIZONTAL; gcUB.insets = new Insets(0, 0, 12, 0);
        cardAran.add(filaUB, gcUB);

        GridBagConstraints gcBtnUB = new GridBagConstraints();
        gcBtnUB.gridx = 0; gcBtnUB.gridy = 3; gcBtnUB.gridwidth = 2;
        gcBtnUB.fill = GridBagConstraints.NONE; gcBtnUB.anchor = GridBagConstraints.CENTER;
        cardAran.add(btnGuardarUB, gcBtnUB);

        addCenteredCard(pnlAranceles, cardAran, 30);

        jTabbedPane1.addTab("Seguridad", pnlSeguridad);
        jTabbedPane1.addTab("Laboratorio", pnlLaboratorio);
        jTabbedPane1.addTab("Impresión", pnlImpresion);
        jTabbedPane1.addTab("Aranceles", pnlAranceles);

        add(jTabbedPane1, BorderLayout.CENTER);
    }

    // ════════════════════════════════════════════════════════════════
    //  HELPERS
    // ════════════════════════════════════════════════════════════════
    private JPanel card(String titulo) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(C_BLANCO);
        p.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(C_BORDE, 1, true),
            new EmptyBorder(12, 18, 12, 18)
        ));
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1.0;
        g.insets = new Insets(0, 0, 10, 0);
        p.add(sectionHeader(titulo), g);
        return p;
    }

    private JPanel sectionHeader(String titulo) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        JLabel lbl = new JLabel(titulo.toUpperCase());
        lbl.setFont(F_SECTION);
        lbl.setForeground(C_AZUL_MED);
        lbl.setBorder(new EmptyBorder(0, 0, 3, 0));

        JPanel linea = new JPanel();
        linea.setBackground(C_AZUL_MED);
        linea.setPreferredSize(new Dimension(0, 2));

        p.add(lbl, BorderLayout.NORTH);
        p.add(linea, BorderLayout.SOUTH);
        return p;
    }

    private JLabel fldLabel(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(F_LABEL);
        l.setForeground(C_GRIS);
        return l;
    }

    private JPanel comboPanel(String titulo, JComboBox<?> cb) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1.0;
        g.gridy = 0; g.insets = new Insets(0, 0, 2, 0);
        p.add(fldLabel(titulo), g);
        g.gridy = 1; g.insets = new Insets(0, 0, 0, 0);
        p.add(cb, g);
        return p;
    }

    private void addCenteredCard(JPanel tab, JComponent card, int vMargin) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.gridy = 0;
        g.weightx = 1.0; g.weighty = 1.0;
        g.fill = GridBagConstraints.NONE; 
        g.anchor = GridBagConstraints.CENTER; 
        g.insets = new Insets(vMargin, 10, vMargin, 10);
        tab.add(card, g);
    }

    // ════════════════════════════════════════════════════════════════
    //  VARIABLES
    // ════════════════════════════════════════════════════════════════
    private JTabbedPane jTabbedPane1;
    private JPanel pnlSeguridad, pnlLaboratorio, pnlImpresion, pnlAranceles;
    private JLabel lblUsuarioActual;
    private JPasswordField txtClaveActual, txtNuevaClave, txtRepetirNuevaClave;
    private JTextField txtNombreLaboratorio, txtDireccion, txtLocalidad;
    private JTextField txtTelefono, txtBioquimico, txtMatricula;
    private JTextField txtBuscarLogo, txtFirma, txtRutaPdf, txtRutaBackup, txtValorUB;
    private JComboBox<String> cbxTamanoHoja, cbxOrientacion;
    private JCheckBox chkIncluirLogo, chkImprimirAutom;
    private JButton btnActualizarClave, btnActualizarDatos;
    private JButton btnGuardarConfiguracion, btnGuardarUB;
    private JButton btnBuscarLogo, btnBuscarFirma, btnBuscarRutaPdf, btnBuscarRutaBackup;
}