package vista.swing;

import vista.interfaces.IVistaObraSocial;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import modelo.ObraSocial;
import presentador.ObraSocialPresenter;

public class VistaObraSocial extends JPanel implements IVistaObraSocial {

    private ObraSocialPresenter presenter;
    
    // ── Paleta BIOTEC Minimalista ────────────────────────────────────
    private final Color C_NAVY         = new Color(10, 25, 47);
    private final Color C_FONDO        = new Color(238, 242, 246);
    private final Color C_BLANCO       = Color.WHITE;
    private final Color C_TEXTO_FUERTE = new Color(40, 50, 60);
    private final Color C_TEXTO_SUAVE  = new Color(100, 115, 130);
    private final Color C_BORDE        = new Color(215, 225, 235);
    private final Color C_AZUL_MEDIO   = new Color(30, 110, 180);
    private final Color C_VERDE        = new Color(35, 160, 115);
    private final Color C_ROJO         = new Color(220, 53, 69);
    private final Color C_CAMPO        = new Color(250, 252, 254);
    private final Color C_CABECERA_TBL = new Color(245, 248, 252);
    private final Color C_FILA_PAR     = new Color(252, 254, 255);
    private final Color C_HEADER_TEXT  = new Color(175, 205, 235);

    public VistaObraSocial() {
        initComponents();
        aplicarEstilo();
        configurarNavegacionEnter();
    }

    // ══════════════════════════════════════════════════════════════════
    //  ESTILO Y UX - Consistente con VistaPaciente y VistaMedicos
    // ══════════════════════════════════════════════════════════════════
    private void aplicarEstilo() {
        setBackground(C_FONDO);

        // ── HEADER (mismos márgenes que VistaPaciente) ────────────────
        pnlHeader.setBackground(C_NAVY);
        pnlHeader.setBorder(new EmptyBorder(14, 28, 14, 28));
        
        // Reconstruir el header correctamente
        pnlHeader.removeAll();
        pnlHeader.setLayout(new BorderLayout());
        
        // Panel izquierdo: botón volver + título
        JPanel pnlIzqHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlIzqHeader.setOpaque(false);
        pnlIzqHeader.add(btnVolver);
        
        lblTituloHeader.setForeground(C_BLANCO);
        lblTituloHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTituloHeader.setHorizontalAlignment(SwingConstants.LEFT);
        lblTituloHeader.setBorder(new EmptyBorder(0, 10, 0, 0));
        pnlIzqHeader.add(lblTituloHeader);
        
        // Panel derecho: buscador
        JPanel pnlDerHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        pnlDerHeader.setOpaque(false);
        JLabel lblLupa = new JLabel("Buscar obra social:");
        lblLupa.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblLupa.setForeground(C_HEADER_TEXT);
        pnlDerHeader.add(lblLupa);
        pnlDerHeader.add(txtBuscarObraSocial);
        
        pnlHeader.add(pnlIzqHeader, BorderLayout.WEST);
        pnlHeader.add(pnlDerHeader, BorderLayout.EAST);

        estilizarCampoBuscador(txtBuscarObraSocial);
        configurarBotonRetroceso(btnVolver);

        // ── CONTENEDOR PRINCIPAL BLANCO (con borde sin superior) ──────
        pnlContenedorBlanco.setBackground(C_BLANCO);
        pnlContenedorBlanco.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 1, 1, 1, C_BORDE),
            new EmptyBorder(24, 28, 24, 28)
        ));
        pnlContenedorBlanco.removeAll();
        pnlContenedorBlanco.setLayout(new BorderLayout());

        // ── CUERPO (formulario izquierda + tabla derecha) ─────────────
        pnlCuerpo.setBackground(C_BLANCO);
        pnlCuerpo.removeAll();
        pnlCuerpo.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.BOTH;
        gc.weighty = 1.0;
        gc.insets = new Insets(0, 0, 0, 0);

        // Columna izquierda: formulario
        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = 0;
        pnlFormulario.setPreferredSize(new Dimension(460, 0));
        pnlFormulario.setMinimumSize(new Dimension(420, 0));
        pnlCuerpo.add(pnlFormulario, gc);

        // Columna derecha: tabla
        gc.gridx = 1;
        gc.gridy = 0;
        gc.weightx = 1.0;
        pnlCuerpo.add(pnlTablaWrapper, gc);

        pnlContenedorBlanco.add(pnlCuerpo, BorderLayout.CENTER);

        // ── FORMULARIO (más espacioso) ────────────────────────────────
        pnlFormulario.setBackground(C_BLANCO);
        pnlFormulario.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(16, 16, 16, 16),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDE, 1, true),
                new EmptyBorder(24, 28, 24, 28)
            )
        ));

        Font fontLabel = new Font("Segoe UI", Font.BOLD, 11);
        JLabel[] labels = {lblCodigo, lblNombre, lblArancel};
        for (JLabel lbl : labels) {
            lbl.setFont(fontLabel);
            lbl.setForeground(C_TEXTO_SUAVE);
        }

        estilizarCampo(txtCodigoObraSocial);
        estilizarCampo(txtNombreObraSocial);
        estilizarCampo(txtArancelObraSocial);

        // ── BOTONES ───────────────────────────────────────────────────
        configurarBoton(btnAgregarObraSocial, C_VERDE, "GUARDAR", 160, 42);
        configurarBoton(btnCambiarArancel, C_AZUL_MEDIO, "EDITAR ARANCEL", 160, 42);
        configurarBoton(btnEliminarObraSocial, C_ROJO, "ELIMINAR", 160, 42);

        btnCambiarArancel.setEnabled(false);
        habilitarBotonEliminar(false);

        // ── TABLA WRAPPER (igual que VistaPaciente) ───────────────────
        pnlTablaWrapper.setBackground(C_BLANCO);
        pnlTablaWrapper.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(0, 0, 0, 0),
            BorderFactory.createLineBorder(C_BORDE, 1, true)
        ));

        lblTituloTabla.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTituloTabla.setForeground(C_TEXTO_FUERTE);
        lblTituloTabla.setBorder(new EmptyBorder(14, 16, 12, 16));

        // Configuración de la Grilla
        grillaObrasSociales.setRowHeight(36);
        grillaObrasSociales.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        grillaObrasSociales.setGridColor(new Color(235, 240, 245));
        grillaObrasSociales.setShowHorizontalLines(true);
        grillaObrasSociales.setShowVerticalLines(false);
        grillaObrasSociales.setSelectionBackground(new Color(220, 235, 250));
        grillaObrasSociales.setSelectionForeground(C_TEXTO_FUERTE);
        grillaObrasSociales.setIntercellSpacing(new Dimension(0, 0));
        grillaObrasSociales.setBorder(BorderFactory.createEmptyBorder());
        grillaObrasSociales.setFillsViewportHeight(true);

        // Header de la tabla
        grillaObrasSociales.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        grillaObrasSociales.getTableHeader().setBackground(C_CABECERA_TBL);
        grillaObrasSociales.getTableHeader().setForeground(C_TEXTO_SUAVE);
        grillaObrasSociales.getTableHeader().setPreferredSize(new Dimension(0, 40));
        grillaObrasSociales.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDE));
        grillaObrasSociales.getTableHeader().setReorderingAllowed(false);

        jScrollPane1.setBorder(BorderFactory.createEmptyBorder());
        jScrollPane1.getViewport().setBackground(C_BLANCO);

        pnlTablaWrapper.removeAll();
        pnlTablaWrapper.setLayout(new BorderLayout());
        pnlTablaWrapper.add(lblTituloTabla, BorderLayout.NORTH);
        pnlTablaWrapper.add(jScrollPane1, BorderLayout.CENTER);

        // ── FOOTER (mismos márgenes que VistaPaciente) ────────────────
        pnlFooter.setBackground(C_FONDO);
        pnlFooter.setBorder(new EmptyBorder(10, 16, 14, 16));
        pnlFooter.removeAll();
        pnlFooter.setLayout(new BorderLayout());
        
        JPanel pnlFooterAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        pnlFooterAcciones.setOpaque(false);
        pnlFooter.add(pnlFooterAcciones, BorderLayout.EAST);

        // ── ARMADO FINAL DEL LAYOUT ───────────────────────────────────
        this.removeAll();
        this.setLayout(new BorderLayout());
        this.add(pnlHeader, BorderLayout.NORTH);
        this.add(pnlContenedorBlanco, BorderLayout.CENTER);
        this.add(pnlFooter, BorderLayout.SOUTH);

        // ── LAYOUT DEL FORMULARIO ─────────────────────────────────────
        pnlFormulario.removeAll();
        pnlFormulario.setLayout(new GridBagLayout());
        GridBagConstraints gf = new GridBagConstraints();
        gf.fill = GridBagConstraints.HORIZONTAL;
        gf.weightx = 1.0;
        int r = 0;
        
        gf.gridx = 0;
        
        gf.gridy = r++; gf.insets = new Insets(0, 0, 4, 0);  pnlFormulario.add(lblCodigo, gf);
        gf.gridy = r++; gf.insets = new Insets(0, 0, 20, 0); pnlFormulario.add(txtCodigoObraSocial, gf);
        
        gf.gridy = r++; gf.insets = new Insets(0, 0, 4, 0);  pnlFormulario.add(lblNombre, gf);
        gf.gridy = r++; gf.insets = new Insets(0, 0, 20, 0); pnlFormulario.add(txtNombreObraSocial, gf);
        
        gf.gridy = r++; gf.insets = new Insets(0, 0, 4, 0);  pnlFormulario.add(lblArancel, gf);
        gf.gridy = r++; gf.insets = new Insets(0, 0, 25, 0); pnlFormulario.add(txtArancelObraSocial, gf);
        
        // ── BOTONES DE ACCIÓN ────────────────────────────────────────
        pnlBotonesEdicion.setOpaque(false);
        pnlBotonesEdicion.removeAll();
        pnlBotonesEdicion.setLayout(new java.awt.GridLayout(2, 1, 0, 12));
        
        // Fila Superior (Eliminar y Guardar lado a lado)
        JPanel pnlFilaArriba = new JPanel(new java.awt.GridLayout(1, 2, 12, 0));
        pnlFilaArriba.setOpaque(false);
        pnlFilaArriba.add(btnEliminarObraSocial);
        pnlFilaArriba.add(btnAgregarObraSocial);
        
        // Fila Inferior (Editar Arancel centrado)
        JPanel pnlFilaAbajo = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        pnlFilaAbajo.setOpaque(false);
        pnlFilaAbajo.add(btnCambiarArancel);
        
        pnlBotonesEdicion.add(pnlFilaArriba);
        pnlBotonesEdicion.add(pnlFilaAbajo);
        
        gf.gridy = r++; gf.weighty = 0; gf.fill = GridBagConstraints.HORIZONTAL;
        gf.insets = new Insets(0, 0, 0, 0); 
        pnlFormulario.add(pnlBotonesEdicion, gf);

        // Spacer elástico
        gf.gridy = r++; gf.weighty = 1.0; gf.fill = GridBagConstraints.VERTICAL;
        gf.insets = new Insets(0, 0, 0, 0);
        pnlFormulario.add(new JPanel() {{ setOpaque(false); }}, gf);
        
        // Forzar actualización
        this.revalidate();
        this.repaint();
    }

    private void estilizarCampo(JTextField tf) {
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBackground(C_CAMPO);
        tf.setForeground(C_TEXTO_FUERTE);
        tf.setCaretColor(C_AZUL_MEDIO);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDE),
            new EmptyBorder(8, 12, 8, 12)
        ));
        tf.setPreferredSize(new Dimension(0, 38));
        
        tf.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent evt) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, C_AZUL_MEDIO),
                    new EmptyBorder(8, 12, 8, 12)
                ));
                tf.setBackground(C_BLANCO);
            }
            @Override public void focusLost(java.awt.event.FocusEvent evt) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDE),
                    new EmptyBorder(8, 12, 8, 12)
                ));
                tf.setBackground(C_CAMPO);
            }
        });
    }

    private void estilizarCampoBuscador(JTextField tf) {
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setBackground(new Color(25, 45, 75));
        tf.setForeground(C_BLANCO);
        tf.setCaretColor(C_BLANCO);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 80, 120), 1, true),
            new EmptyBorder(8, 14, 8, 14)
        ));
        tf.setPreferredSize(new Dimension(320, 38));
    }

    private void configurarBoton(JButton btn, Color bg, String texto, int w, int h) {
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

    private void configurarBotonRetroceso(JButton btn) {
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
        if (ico != null) btn.setIcon(ico);
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setForeground(C_BLANCO);
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setForeground(C_HEADER_TEXT);
            }
        });
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

    // ══════════════════════════════════════════════════════════════════
    //  LÓGICA
    // ══════════════════════════════════════════════════════════════════
    private void configurarNavegacionEnter() {
        KeyAdapter enterAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    Component src = (Component) e.getSource();
                    if      (src == txtCodigoObraSocial)  txtNombreObraSocial.requestFocus();
                    else if (src == txtNombreObraSocial)  txtArancelObraSocial.requestFocus();
                    else if (src == txtArancelObraSocial) btnAgregarObraSocial.doClick();
                }
            }
        };
        txtCodigoObraSocial.addKeyListener(enterAdapter);
        txtNombreObraSocial.addKeyListener(enterAdapter);
        txtArancelObraSocial.addKeyListener(enterAdapter);
    }

    @Override
    public void setPresenter(ObraSocialPresenter presenter) {
        this.presenter = presenter;
        
        // 1. PURGA DE EVENTOS EN BOTONES (La cura definitiva para la duplicación)
        limpiarListeners(btnAgregarObraSocial);
        limpiarListeners(btnEliminarObraSocial);
        limpiarListeners(btnCambiarArancel);
        limpiarListeners(btnVolver);
        
        // 2. CONEXIÓN LIMPIA
        btnAgregarObraSocial.addActionListener(e -> presenter.onAgregarOS());
        btnEliminarObraSocial.addActionListener(e -> presenter.onEliminarOS());
        btnCambiarArancel.addActionListener(e -> presenter.onCambiarArancel());
        btnVolver.addActionListener(e -> presenter.onVolver());
        
        // 3. PURGA Y CONEXIÓN DEL BUSCADOR
        for (java.awt.event.KeyListener kl : txtBuscarObraSocial.getKeyListeners()) {
            txtBuscarObraSocial.removeKeyListener(kl);
        }
        txtBuscarObraSocial.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                presenter.onBuscarOS();
            }
        });

        // 4. PURGA Y CONEXIÓN DE LA TABLA
        javax.swing.DefaultListSelectionModel modeloSeleccion = 
                (javax.swing.DefaultListSelectionModel) grillaObrasSociales.getSelectionModel();

        for (javax.swing.event.ListSelectionListener lsl : modeloSeleccion.getListSelectionListeners()) {
            modeloSeleccion.removeListSelectionListener(lsl);
        }

        modeloSeleccion.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean filaSeleccionada = grillaObrasSociales.getSelectedRow() != -1;

                // Habilitar/deshabilitar botones visualmente
                habilitarBotonEliminar(filaSeleccionada);
                btnCambiarArancel.setEnabled(filaSeleccionada);

                // ← NO mostrar mensajes aquí, solo llamar al presenter
                if (filaSeleccionada && presenter != null) {
                    presenter.onSeleccionarOS();
                }
            }
        });
    }

    private void limpiarListeners(javax.swing.JButton btn) {
        for (java.awt.event.ActionListener al : btn.getActionListeners()) {
            btn.removeActionListener(al);
        }
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
    public String pedirDato(String mensaje, String titulo) {
        return JOptionPane.showInputDialog(this, mensaje, titulo, JOptionPane.QUESTION_MESSAGE);
    }
    
    @Override public String getCodigoObraSocial() { return txtCodigoObraSocial.getText().trim(); }
    @Override public String getNombreObraSocial() { return txtNombreObraSocial.getText().trim(); }

    @Override
    public double getArancel() {
        try {
            return Double.parseDouble(txtArancelObraSocial.getText().replace(",", "."));
        } catch (Exception e) {
            return 0;
        }
    }

    @Override public String getTextoBusqueda() { return txtBuscarObraSocial.getText().trim(); }

    @Override
    public void cargarObrasSocialesEnTabla(ArrayList<ObraSocial> obs) {
        DefaultTableModel modelo = (DefaultTableModel) grillaObrasSociales.getModel();
        modelo.setRowCount(0);
        for (ObraSocial o : obs) {
            modelo.addRow(new Object[]{o.getCodigo(), o.getNombre(), String.format("$ %.2f", o.getArancel())});
        }
        grillaObrasSociales.clearSelection();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modelo);
        grillaObrasSociales.setRowSorter(sorter);

        DefaultTableCellRenderer render = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(column == 0 || column == 2 ? SwingConstants.CENTER : SwingConstants.LEFT);
                setBorder(new EmptyBorder(0, 12, 0, 12));
                if (!isSelected) {
                    setBackground(row % 2 == 0 ? C_BLANCO : C_FILA_PAR);
                    setForeground(C_TEXTO_FUERTE);
                }
                return this;
            }
        };
        
        for (int i = 0; i < grillaObrasSociales.getColumnCount(); i++) {
            grillaObrasSociales.getColumnModel().getColumn(i).setCellRenderer(render);
        }
            habilitarBotonEliminar(false);
            btnCambiarArancel.setEnabled(false);
        grillaObrasSociales.getColumnModel().getColumn(0).setPreferredWidth(120);
        grillaObrasSociales.getColumnModel().getColumn(0).setMaxWidth(150);
        grillaObrasSociales.getColumnModel().getColumn(2).setPreferredWidth(150);
        grillaObrasSociales.getColumnModel().getColumn(2).setMaxWidth(180);
    }

    @Override
    public ObraSocial getObraSocialSeleccionada() {
        int fila = grillaObrasSociales.getSelectedRow();
        if (fila == -1) return null;
        int modelRow = grillaObrasSociales.convertRowIndexToModel(fila);
        ObraSocial o = new ObraSocial();
        o.setCodigo(grillaObrasSociales.getModel().getValueAt(modelRow, 0).toString());
        o.setNombre(grillaObrasSociales.getModel().getValueAt(modelRow, 1).toString());
        return o;
    }

    @Override
    public void limpiarCampos() {
        txtCodigoObraSocial.setText("");
        txtNombreObraSocial.setText("");
        txtArancelObraSocial.setText("");
        txtBuscarObraSocial.setText("");
        txtCodigoObraSocial.requestFocus();
    }

    @Override public void habilitarBotonAgregar(boolean b)  { btnAgregarObraSocial.setEnabled(b); }
    @Override public void habilitarBotonEliminar(boolean b) { btnEliminarObraSocial.setEnabled(b); }
    @Override public void mostrarMensaje(String mensaje)    { JOptionPane.showMessageDialog(this, mensaje); }
    @Override public void ejecutar()                        { setVisible(true); }

    // ══════════════════════════════════════════════════════════════════
    //  UI BUILDER (Estructura base)
    // ══════════════════════════════════════════════════════════════════
    private void initComponents() {
        pnlHeader           = new JPanel();
        lblTituloHeader     = new JLabel("GESTIÓN DE OBRAS SOCIALES");
        txtBuscarObraSocial = new JTextField();
        btnVolver           = new JButton();

        pnlContenedorBlanco = new JPanel();
        pnlCuerpo           = new JPanel();
        pnlFormulario       = new JPanel();
        pnlTablaWrapper     = new JPanel();
        lblTituloTabla      = new JLabel("Obras Sociales Registradas");
        pnlFooter           = new JPanel();

        lblCodigo           = new JLabel("CÓDIGO ÚNICO");
        lblNombre           = new JLabel("NOMBRE COMPLETO");
        lblArancel          = new JLabel("VALOR ARANCEL ($)");

        txtCodigoObraSocial  = new JTextField();
        txtNombreObraSocial  = new JTextField();
        txtArancelObraSocial = new JTextField();

        grillaObrasSociales  = new JTable();
        jScrollPane1         = new JScrollPane();

        pnlBotonesEdicion     = new JPanel();
        btnAgregarObraSocial  = new JButton();
        btnCambiarArancel     = new JButton();
        btnEliminarObraSocial = new JButton();

        // Modelo tabla
        grillaObrasSociales.setModel(new DefaultTableModel(
            new Object[][]{},
            new String[]{"CÓDIGO", "NOMBRE DE OBRA SOCIAL", "ARANCEL"}
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
        jScrollPane1.setViewportView(grillaObrasSociales);
    }

    // ── Variables ────────────────────────────────────────────────────
    private JPanel                          pnlHeader;
    private JLabel                          lblTituloHeader;
    private JPanel                          pnlContenedorBlanco;
    private JPanel                          pnlCuerpo;
    private JPanel                          pnlFormulario;
    private JPanel                          pnlTablaWrapper;
    private JPanel                          pnlFooter;
    private JLabel                          lblTituloTabla;
    private JLabel                          lblCodigo;
    private JLabel                          lblNombre;
    private JLabel                          lblArancel;
    private JTextField                      txtCodigoObraSocial;
    private JTextField                      txtNombreObraSocial;
    private JTextField                      txtArancelObraSocial;
    private JTextField                      txtBuscarObraSocial;
    private JTable                          grillaObrasSociales;
    private JScrollPane                     jScrollPane1;
    private JPanel                          pnlBotonesEdicion;
    private JButton                         btnAgregarObraSocial;
    private JButton                         btnCambiarArancel;
    private JButton                         btnEliminarObraSocial;
    private JButton                         btnVolver;
}