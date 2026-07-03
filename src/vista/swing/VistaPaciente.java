package vista.swing;

import vista.interfaces.IVistaPaciente;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import modelo.Paciente;
import presentador.PacientePresenter;

public class VistaPaciente extends JPanel implements IVistaPaciente {

    private PacientePresenter presenter;
    private JPopupMenu        popupSugerencias;
    private JList<String>     listaSugerencias;
    private DefaultListModel<String> modeloSugerencias;
    
    // Bandera de seguridad para el autocompletado
    private boolean ignorarBusquedaOS = false;

    // ── Paleta ───────────────────────────────────────────────────────
    private final Color C_NAVY         = new Color(10, 25, 47);
    private final Color C_FONDO        = new Color(238, 242, 246);
    private final Color C_BLANCO       = Color.WHITE;
    private final Color C_TEXTO_FUERTE = new Color(40, 50, 60);
    private final Color C_TEXTO_SUAVE  = new Color(100, 115, 130);
    private final Color C_BORDE        = new Color(215, 225, 235);
    private final Color C_AZUL_MEDIO   = new Color(30, 110, 180);
    private final Color C_VERDE        = new Color(35, 160, 115);
    private final Color C_CAMPO        = new Color(250, 252, 254);
    private final Color C_CABECERA_TBL = new Color(245, 248, 252);
    private final Color C_FILA_PAR     = new Color(252, 254, 255);
    private final Color C_HEADER_TEXT  = new Color(175, 205, 235);

    public VistaPaciente() {
        initComponents();
        aplicarEstilo();
        configurarBuscadorOS();
        configurarNavegacionEnter();
        configurarDeseleccionPorClic();
        configurarValidacionesCampos();

        btnCargarResultados.setEnabled(false);
        btnVerHistorial.setEnabled(false);

        // Buscador Principal en tiempo real
        txtBuscar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate (javax.swing.event.DocumentEvent e) { buscar(); }
            @Override public void removeUpdate (javax.swing.event.DocumentEvent e) { buscar(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { buscar(); }
            private void buscar() { if (presenter != null) presenter.onBuscarPaciente(); }
        });
    }

    // ════════════════════════════════════════════════════════════════
    //  VALIDACIONES EN TIEMPO REAL
    // ════════════════════════════════════════════════════════════════
    private void configurarValidacionesCampos() {
        
        // Validación de NOMBRE: solo letras, espacios, acentos y ñ
        txtNombre.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isLetter(c) && c != ' ' && c != KeyEvent.VK_BACK_SPACE && 
                    c != 'á' && c != 'é' && c != 'í' && c != 'ó' && c != 'ú' &&
                    c != 'Á' && c != 'É' && c != 'Í' && c != 'Ó' && c != 'Ú' &&
                    c != 'ñ' && c != 'Ñ') {
                    e.consume(); 
                    mostrarMensajeTemporal("El nombre solo puede contener letras y espacios.");
                }
            }
        });
        
        // Validación de APELLIDO: solo letras, espacios, acentos y ñ
        txtApellido.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isLetter(c) && c != ' ' && c != KeyEvent.VK_BACK_SPACE &&
                    c != 'á' && c != 'é' && c != 'í' && c != 'ó' && c != 'ú' &&
                    c != 'Á' && c != 'É' && c != 'Í' && c != 'Ó' && c != 'Ú' &&
                    c != 'ñ' && c != 'Ñ') {
                    e.consume();
                    mostrarMensajeTemporal("El apellido solo puede contener letras y espacios.");
                }
            }
        });
        
        // Validación de DNI: solo números, máximo 8 dígitos
        txtDni.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                String textoActual = txtDni.getText();
                
                if (!Character.isDigit(c)) {
                    e.consume();
                    mostrarMensajeTemporal("El DNI solo puede contener números.");
                    return;
                }
                
                if (textoActual.length() >= 8) {
                    e.consume();
                    mostrarMensajeTemporal("El DNI no puede tener más de 8 dígitos.");
                }
            }
        });
        
        // Validación de EDAD: solo números, máximo 3 dígitos (0-120)
        txtEdad.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                String textoActual = txtEdad.getText();
                
                if (!Character.isDigit(c)) {
                    e.consume();
                    mostrarMensajeTemporal("La edad solo puede contener números.");
                    return;
                }
                
                if (textoActual.length() >= 3) {
                    e.consume();
                    mostrarMensajeTemporal("La edad no puede tener más de 3 dígitos.");
                }
            }
            
            @Override
            public void keyReleased(KeyEvent e) {
                String texto = txtEdad.getText().trim();
                if (!texto.isEmpty()) {
                    try {
                        int edad = Integer.parseInt(texto);
                        if (edad < 0 || edad > 120) {
                            mostrarMensajeTemporal("La edad debe estar entre 0 y 120 años.");
                        }
                    } catch (NumberFormatException ex) {
                        // Ya validado en keyTyped
                    }
                }
            }
        });
    }
    
    private void mostrarMensajeTemporal(String mensaje) {
        System.out.println("Validación: " + mensaje);
    }

    // ════════════════════════════════════════════════════════════════
    //  DESELECCIÓN DE FILA AL HACER CLIC FUERA DE LA TABLA
    // ════════════════════════════════════════════════════════════════
    private void configurarDeseleccionPorClic() {
        JPanel[] panelesParaDeseleccion = {
            pnlCuerpo, pnlFormulario, pnlTablaWrapper, pnlFooter,
            pnlBotonesEdicion, pnlHeader
        };
        
        MouseAdapter deseleccionador = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Component componenteOrigen = e.getComponent();
                if (componenteOrigen == grillaPacientes || 
                    componenteOrigen == jScrollPane1 ||
                    componenteOrigen == grillaPacientes.getTableHeader()) {
                    return; 
                }
                
                if (componenteOrigen instanceof javax.swing.JButton ||
                    componenteOrigen instanceof javax.swing.JTextField ||
                    componenteOrigen instanceof javax.swing.JComboBox ||
                    componenteOrigen instanceof javax.swing.JTextArea) {
                    return; 
                }
                
                if (grillaPacientes.getSelectedRow() != -1) {
                    grillaPacientes.clearSelection();
                    limpiarCampos();
                }
            }
        };
        
        for (JPanel panel : panelesParaDeseleccion) {
            if (panel != null) panel.addMouseListener(deseleccionador);
        }
        this.addMouseListener(deseleccionador);
    }

    // ════════════════════════════════════════════════════════════════
    //  MVP
    // ════════════════════════════════════════════════════════════════
    @Override
    public void setPresenter(PacientePresenter presenter) {
        this.presenter = presenter;
        
        limpiarListeners(btnGuardarPaciente);
        limpiarListeners(btnEditarPaciente);
        limpiarListeners(btnCargarResultados);
        limpiarListeners(btnVerHistorial);
        limpiarListeners(btnVolver);
        
        btnGuardarPaciente.addActionListener(e -> presenter.onGuardarPaciente());
        btnEditarPaciente.addActionListener(e -> presenter.onEditarPaciente());
        btnCargarResultados.addActionListener(e -> presenter.onCargarResultados());
        btnVerHistorial.addActionListener(e -> presenter.onVerHistorial());
        btnVolver.addActionListener(e -> presenter.onVolver());

        grillaPacientes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hay = grillaPacientes.getSelectedRow() != -1;
                btnCargarResultados.setEnabled(hay);
                btnVerHistorial    .setEnabled(hay);
                if (hay) presenter.onSeleccionarPaciente();
            }
        });
    }
    
    private void limpiarListeners(javax.swing.JButton btn) {
        for (java.awt.event.ActionListener al : btn.getActionListeners()) {
            btn.removeActionListener(al);
        }
    }

    @Override public void ejecutar()  { setVisible(true); }
    @Override public void limpiarFocos() { requestFocusInWindow(); }

    @Override
    public int confirmarAccion(String mensaje, String titulo) {
        return JOptionPane.showConfirmDialog(this, mensaje, titulo, JOptionPane.YES_NO_OPTION);
    }

    // ════════════════════════════════════════════════════════════════
    //  GETTERS
    // ════════════════════════════════════════════════════════════════
    @Override public String getApellido()      { return txtApellido.getText().trim(); }
    @Override public String getNombre()        { return txtNombre.getText().trim(); }
    @Override public String getDni()           { return txtDni.getText().trim(); }
    @Override public String getEdad()          { return txtEdad.getText().trim(); }
    @Override public String getDireccion()     { return txtDireccion.getText().trim(); }
    @Override public String getLocalidad()     { return txtLocalidad.getText().trim(); }
    @Override public String getNumAfiliado()   { return txtNAfiliado.getText().trim(); }
    @Override public String getCelular()       { return txtCelular.getText().trim(); }
    @Override public String getSexo()          { return cbxSexo.getSelectedIndex() >= 0 ? cbxSexo.getSelectedItem().toString() : ""; }
    @Override public String getObraSocial()    { return txtObraSocial.getText().trim(); }
    @Override public String getTextoBusqueda() { return txtBuscar.getText().trim(); }

    @Override public void habilitarBotonGuardar(boolean b)          { btnGuardarPaciente.setEnabled(b); }
    @Override public void habilitarBotonEditar(boolean b)           { btnEditarPaciente.setEnabled(b); }
    @Override public void habilitarBotonCargarResultados(boolean b) { btnCargarResultados.setEnabled(b); }
    @Override public void habilitarBotonNuevoAnalisis(boolean b)    { btnCargarResultados.setEnabled(b); }

    // ════════════════════════════════════════════════════════════════
    //  TABLA
    // ════════════════════════════════════════════════════════════════
    @Override
    public void cargarPacientesEnTabla(ArrayList<Paciente> pacientes) {
        DefaultTableModel modelo = new DefaultTableModel(
            new Object[][]{},
            new String[]{"ID", "APELLIDO", "NOMBRE", "DNI", "EDAD", "ÚLTIMO ANÁLISIS"}
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
        for (Paciente p : pacientes) {
            String fecha = p.getFechaUltimoAnalisis() != null
                    ? sdf.format(p.getFechaUltimoAnalisis()) : "Sin análisis";
            modelo.addRow(new Object[]{
                p.getIdPaciente(),
                p.getApellido().toUpperCase(),
                p.getNombre().toUpperCase(),
                p.getDni(),
                p.getEdad(),
                fecha
            });
        }
        grillaPacientes.setModel(modelo);

        DefaultTableCellRenderer centradoRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setHorizontalAlignment(SwingConstants.CENTER); 
                setBorder(new EmptyBorder(0, 10, 0, 8));
                if (!sel) {
                    setBackground(row % 2 == 0 ? C_BLANCO : C_FILA_PAR);
                    setForeground(C_TEXTO_FUERTE);
                }
                return this;
            }
        };

        for (int i = 0; i < grillaPacientes.getColumnCount(); i++) {
            grillaPacientes.getColumnModel().getColumn(i).setCellRenderer(centradoRenderer);
        }

        // Anchos relativos (PreferredWidth) sin restringir a los monitores grandes
        grillaPacientes.getColumnModel().getColumn(0).setPreferredWidth(50);
        grillaPacientes.getColumnModel().getColumn(0).setMaxWidth(70); // ID sí queda fijo
        grillaPacientes.getColumnModel().getColumn(1).setPreferredWidth(180);
        grillaPacientes.getColumnModel().getColumn(2).setPreferredWidth(160);
        grillaPacientes.getColumnModel().getColumn(3).setPreferredWidth(100);
        grillaPacientes.getColumnModel().getColumn(4).setPreferredWidth(60);  
        grillaPacientes.getColumnModel().getColumn(4).setMaxWidth(80); // Edad sí queda fija
        grillaPacientes.getColumnModel().getColumn(5).setPreferredWidth(130);
    }

    @Override
    public int getPacienteSeleccionadoId() {
        int fila = grillaPacientes.getSelectedRow();
        if (fila == -1) return -1;
        int modelRow = grillaPacientes.convertRowIndexToModel(fila);
        return (int) grillaPacientes.getModel().getValueAt(modelRow, 0);
    }

    @Override
    public Paciente getPacienteSeleccionado() {
        int fila = grillaPacientes.getSelectedRow();
        if (fila == -1) return null;
        
        int modelRow = grillaPacientes.convertRowIndexToModel(fila);
        Paciente p = new Paciente();
        p.setIdPaciente((int) grillaPacientes.getModel().getValueAt(modelRow, 0));
        p.setApellido((String) grillaPacientes.getModel().getValueAt(modelRow, 1));
        p.setNombre((String) grillaPacientes.getModel().getValueAt(modelRow, 2));
        p.setDni((String) grillaPacientes.getModel().getValueAt(modelRow, 3));
        p.setEdad(String.valueOf(grillaPacientes.getModel().getValueAt(modelRow, 4))); 
        return p;
    }

    @Override
    public void cargarDatosPaciente(Paciente p) {
        if (p == null) return;
        ignorarBusquedaOS = true; 
        
        txtApellido .setText(p.getApellido());
        txtNombre   .setText(p.getNombre());
        txtDni      .setText(p.getDni());
        txtEdad     .setText(String.valueOf(p.getEdad()));
        txtDireccion.setText(p.getDireccion());
        txtLocalidad.setText(p.getLocalidad());
        txtNAfiliado.setText(p.getNroAfiliado());
        txtCelular  .setText(p.getCelular());
        cbxSexo     .setSelectedItem(p.getSexo());
        txtObraSocial.setText(p.getObraSocial());
        
        ignorarBusquedaOS = false; 
    }

    @Override
    public void limpiarCampos() {
        ignorarBusquedaOS = true; 
        
        txtApellido .setText(""); txtNombre   .setText(""); txtDni      .setText("");
        txtEdad     .setText(""); txtDireccion.setText(""); txtLocalidad.setText("");
        txtNAfiliado.setText(""); txtCelular  .setText(""); txtObraSocial.setText("");
        txtBuscar   .setText(""); cbxSexo.setSelectedIndex(0);
        
        grillaPacientes.clearSelection();
        txtApellido.requestFocus();
        
        ignorarBusquedaOS = false; 
    }

    @Override public void mostrarMensaje(String msg) { JOptionPane.showMessageDialog(this, msg); }

    // ════════════════════════════════════════════════════════════════
    //  AUTOCOMPLETADO OBRA SOCIAL
    // ════════════════════════════════════════════════════════════════
    private void configurarBuscadorOS() {
        modeloSugerencias = new DefaultListModel<>();
        listaSugerencias  = new JList<>(modeloSugerencias);
        listaSugerencias.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        listaSugerencias.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaSugerencias.setFixedCellHeight(32);
        listaSugerencias.setBackground(C_BLANCO);
        listaSugerencias.setSelectionBackground(new Color(220, 235, 250));
        listaSugerencias.setSelectionForeground(C_TEXTO_FUERTE);

        listaSugerencias.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                seleccionarSugerencia();
            }
        });

        txtObraSocial.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (popupSugerencias == null || !popupSugerencias.isVisible() || modeloSugerencias.isEmpty()) return;
                int index = listaSugerencias.getSelectedIndex();
                int size  = modeloSugerencias.getSize();
                
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_DOWN:
                        index = Math.min(index + 1, size - 1);
                        listaSugerencias.setSelectedIndex(index);
                        listaSugerencias.ensureIndexIsVisible(index);
                        e.consume(); break;
                    case KeyEvent.VK_UP:
                        index = Math.max(index - 1, 0);
                        listaSugerencias.setSelectedIndex(index);
                        listaSugerencias.ensureIndexIsVisible(index);
                        e.consume(); break;
                    case KeyEvent.VK_ENTER:
                        if (index != -1) { seleccionarSugerencia(); e.consume(); } break;
                    case KeyEvent.VK_ESCAPE:
                        popupSugerencias.setVisible(false); break;
                }
            }
        });

        txtObraSocial.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { dispararBusqueda(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { dispararBusqueda(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { dispararBusqueda(); }

            private void dispararBusqueda() {
                if (ignorarBusquedaOS) return;
                
                String texto = txtObraSocial.getText().trim();
                if (texto.length() >= 1) {
                    if (presenter != null) presenter.onBuscarSugerenciaOS();
                } else {
                    if (popupSugerencias != null) popupSugerencias.setVisible(false);
                }
            }
        });
    }

    @Override
    public void mostrarSugerenciasOS(List<String> sugerencias) {
        modeloSugerencias.clear();
        for (String s : sugerencias) modeloSugerencias.addElement(s);

        if (modeloSugerencias.isEmpty()) {
            if (popupSugerencias != null) popupSugerencias.setVisible(false);
            return;
        }

        if (popupSugerencias == null) {
            popupSugerencias = new JPopupMenu();
            popupSugerencias.setFocusable(false);
            popupSugerencias.setBorder(BorderFactory.createEmptyBorder());
            JScrollPane scroll = new JScrollPane(listaSugerencias);
            scroll.setBorder(BorderFactory.createLineBorder(C_BORDE, 1));
            popupSugerencias.add(scroll);
        }

        int alto = Math.min(200, modeloSugerencias.getSize() * 32 + 4);
        popupSugerencias.setPopupSize(txtObraSocial.getWidth(), alto);
        popupSugerencias.show(txtObraSocial, 0, txtObraSocial.getHeight());
        txtObraSocial.requestFocusInWindow();
        listaSugerencias.setSelectedIndex(0);
    }

    private void seleccionarSugerencia() {
        String sel = listaSugerencias.getSelectedValue();
        if (sel != null) {
            ignorarBusquedaOS = true;
            txtObraSocial.setText(sel);
            ignorarBusquedaOS = false;
            
            if (popupSugerencias != null) popupSugerencias.setVisible(false);
            txtNAfiliado.requestFocus();
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  NAVEGACIÓN ENTER
    // ════════════════════════════════════════════════════════════════
    private void configurarNavegacionEnter() {
        KeyAdapter nav = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() != KeyEvent.VK_ENTER) return;
                if (popupSugerencias != null && popupSugerencias.isVisible()
                        && !listaSugerencias.isSelectionEmpty()) return;
                        
                Object s = e.getSource();
                if      (s == txtApellido)   txtNombre.requestFocus();
                else if (s == txtNombre)     txtDni.requestFocus();
                else if (s == txtDni)        cbxSexo.requestFocus();
                else if (s == cbxSexo)       txtEdad.requestFocus();
                else if (s == txtEdad)       txtDireccion.requestFocus();
                else if (s == txtDireccion)  txtLocalidad.requestFocus();
                else if (s == txtLocalidad)  txtCelular.requestFocus();
                else if (s == txtCelular)    txtObraSocial.requestFocus();
                else if (s == txtObraSocial) txtNAfiliado.requestFocus();
                else if (s == txtNAfiliado)  btnGuardarPaciente.doClick();
            }
        };
        for (javax.swing.JTextField tf : new javax.swing.JTextField[]{
                txtApellido, txtNombre, txtDni, txtEdad,
                txtDireccion, txtLocalidad, txtCelular, txtObraSocial, txtNAfiliado}) {
            tf.addKeyListener(nav);
        }
        cbxSexo.addKeyListener(nav);
    }

    // ════════════════════════════════════════════════════════════════
    //  ESTILO
    // ════════════════════════════════════════════════════════════════
    private void aplicarEstilo() {
        setBackground(C_FONDO);

        pnlHeader.setBackground(C_NAVY);
        pnlHeader.setBorder(new EmptyBorder(14, 28, 14, 28));

        lblTituloHeader.setForeground(C_BLANCO);
        lblTituloHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));

        estilizarCampoBuscador(txtBuscar);

        pnlFormulario.setBackground(C_BLANCO);
        pnlFormulario.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(16, 16, 16, 16),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDE, 1, true),
                new EmptyBorder(20, 24, 16, 24)
            )
        ));

        Font fontLabel = new Font("Segoe UI", Font.BOLD, 11);
        for (JLabel lbl : new JLabel[]{lblApellido, lblNombre, lblDNI, lblSexo, lblEdad,
                                        lblDir, lblLoc, lblOS, lblNAfiliado, lblCel}) {
            lbl.setFont(fontLabel);
            lbl.setForeground(C_TEXTO_SUAVE);
        }

        for (javax.swing.JTextField tf : new javax.swing.JTextField[]{
                txtApellido, txtNombre, txtDni, txtEdad,
                txtDireccion, txtLocalidad, txtObraSocial, txtNAfiliado, txtCelular}) {
            estilizarCampo(tf);
        }

        cbxSexo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbxSexo.setBackground(C_CAMPO);
        cbxSexo.setForeground(C_TEXTO_FUERTE);
        cbxSexo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDE),
            new EmptyBorder(8, 10, 8, 10) // Márgenes dinámicos en lugar de setPreferredSize
        ));

        configurarBoton(btnGuardarPaciente,  C_VERDE,      "GUARDAR");
        configurarBoton(btnEditarPaciente,   C_AZUL_MEDIO, "MODIFICAR");
        configurarBotonRetroceso(btnVolver);
        configurarBoton(btnCargarResultados, C_AZUL_MEDIO, "＋ NUEVO ANÁLISIS");
        configurarBoton(btnVerHistorial,     new Color(70, 130, 180), "☰ HISTORIAL");

        pnlTablaWrapper.setBackground(C_BLANCO);
        pnlTablaWrapper.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(16, 0, 16, 16),
            BorderFactory.createLineBorder(C_BORDE, 1, true)
        ));

        lblTituloTabla.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTituloTabla.setForeground(C_TEXTO_FUERTE);
        lblTituloTabla.setBorder(new EmptyBorder(10, 14, 10, 14));

        grillaPacientes.setRowHeight(38);
        grillaPacientes.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        grillaPacientes.setGridColor(new Color(235, 240, 245));
        grillaPacientes.setShowHorizontalLines(true);
        grillaPacientes.setShowVerticalLines(false);
        grillaPacientes.setSelectionBackground(new Color(220, 235, 250));
        grillaPacientes.setSelectionForeground(C_TEXTO_FUERTE);
        grillaPacientes.setIntercellSpacing(new Dimension(0, 0));
        grillaPacientes.setBorder(BorderFactory.createEmptyBorder());
        grillaPacientes.setFillsViewportHeight(true);

        grillaPacientes.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        grillaPacientes.getTableHeader().setBackground(C_CABECERA_TBL);
        grillaPacientes.getTableHeader().setForeground(C_TEXTO_SUAVE);
        grillaPacientes.getTableHeader().setPreferredSize(new Dimension(0, 40));
        grillaPacientes.getTableHeader().setBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDE));
        grillaPacientes.getTableHeader().setReorderingAllowed(false);

        jScrollPane1.setBorder(BorderFactory.createEmptyBorder());
        jScrollPane1.getViewport().setBackground(C_BLANCO);

        pnlFooter.setBackground(C_FONDO);
        pnlFooter.setBorder(new EmptyBorder(10, 16, 14, 16));
    }

    private void estilizarCampo(javax.swing.JTextField tf) {
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBackground(C_CAMPO);
        tf.setForeground(C_TEXTO_FUERTE);
        tf.setCaretColor(C_AZUL_MEDIO);
        // Márgenes dinámicos en lugar de alturas fijas en píxeles
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDE),
            new EmptyBorder(10, 12, 10, 12) 
        ));
        
        tf.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, C_AZUL_MEDIO),
                    new EmptyBorder(10, 12, 10, 12)));
                tf.setBackground(C_BLANCO);
            }
            @Override public void focusLost(java.awt.event.FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDE),
                    new EmptyBorder(10, 12, 10, 12)));
                tf.setBackground(C_CAMPO);
            }
        });
    }

    private void estilizarCampoBuscador(javax.swing.JTextField tf) {
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setBackground(new Color(25, 45, 75));
        tf.setForeground(C_BLANCO);
        tf.setCaretColor(C_BLANCO);
        // Se definen columnas en vez de dimensiones fijas
        tf.setColumns(20); 
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 80, 120), 1, true),
            new EmptyBorder(8, 14, 8, 14)
        ));
    }

    private void configurarBoton(javax.swing.JButton btn, Color bg, String texto) {
        btn.setText(texto);
        btn.setBackground(bg);
        btn.setForeground(C_BLANCO);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // Ajuste mediante padding para que el botón crezca proporcionalmente con el texto y DPI
        btn.setBorder(new EmptyBorder(10, 20, 10, 20)); 
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
        btn.setBorder(new EmptyBorder(0, 0, 0, 16));
        ImageIcon ico = icon("/reportes/img/flecha_icon.png", 40, 40);
        if (ico != null) btn.setIcon(ico);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { btn.setForeground(C_BLANCO); }
            @Override public void mouseExited (java.awt.event.MouseEvent e) { btn.setForeground(C_HEADER_TEXT); }
        });
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
    //  initComponents
    // ════════════════════════════════════════════════════════════════
    @SuppressWarnings("unchecked")
    private void initComponents() {

        pnlHeader          = new JPanel();
        lblTituloHeader    = new JLabel("GESTIÓN DE PACIENTES");
        txtBuscar          = new javax.swing.JTextField();

        pnlCuerpo          = new JPanel();
        pnlFormulario      = new JPanel();
        pnlTablaWrapper    = new JPanel();
        lblTituloTabla     = new JLabel("Pacientes Registrados");
        pnlFooter          = new JPanel();

        lblApellido        = new JLabel("APELLIDO");
        lblNombre          = new JLabel("NOMBRE");
        lblDNI             = new JLabel("DNI / DOCUMENTO");
        lblSexo            = new JLabel("SEXO");
        lblEdad            = new JLabel("EDAD");
        lblDir             = new JLabel("DIRECCIÓN");
        lblLoc             = new JLabel("LOCALIDAD");
        lblCel             = new JLabel("CELULAR");
        lblOS              = new JLabel("OBRA SOCIAL");
        lblNAfiliado       = new JLabel("N° AFILIADO");

        txtApellido        = new javax.swing.JTextField();
        txtNombre          = new javax.swing.JTextField();
        txtDni             = new javax.swing.JTextField();
        cbxSexo            = new javax.swing.JComboBox<>();
        txtEdad            = new javax.swing.JTextField();
        txtDireccion       = new javax.swing.JTextField();
        txtLocalidad       = new javax.swing.JTextField();
        txtCelular         = new javax.swing.JTextField();
        txtObraSocial      = new javax.swing.JTextField();
        txtNAfiliado       = new javax.swing.JTextField();

        pnlBotonesEdicion  = new JPanel();
        btnGuardarPaciente = new javax.swing.JButton();
        btnEditarPaciente  = new javax.swing.JButton();
        grillaPacientes    = new JTable();
        jScrollPane1       = new javax.swing.JScrollPane();
        btnCargarResultados= new javax.swing.JButton();
        btnVerHistorial    = new javax.swing.JButton();
        btnVolver          = new javax.swing.JButton();

        cbxSexo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"","M", "F", "X"}));

        setLayout(new BorderLayout());

        pnlHeader.setLayout(new BorderLayout());

        JPanel pnlIzqHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlIzqHeader.setOpaque(false);
        pnlIzqHeader.add(btnVolver);
        lblTituloHeader.setBorder(new EmptyBorder(0, 10, 0, 0));
        pnlIzqHeader.add(lblTituloHeader);

        JPanel pnlDerHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlDerHeader.setOpaque(false);
        JLabel lblLupa = new JLabel("Buscar paciente:");
        lblLupa.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblLupa.setForeground(C_HEADER_TEXT);
        pnlDerHeader.add(lblLupa);
        pnlDerHeader.add(txtBuscar);

        pnlHeader.add(pnlIzqHeader, BorderLayout.WEST);
        pnlHeader.add(pnlDerHeader, BorderLayout.EAST);
        add(pnlHeader, BorderLayout.NORTH);

        pnlCuerpo.setBackground(C_FONDO);
        pnlCuerpo.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill    = GridBagConstraints.BOTH;
        gc.weighty = 1.0;
        gc.insets  = new Insets(0, 0, 0, 0);

        // FORMULARIO: Pasa a usar porcentajes relativos (35% de la pantalla)
        gc.gridx = 0; gc.gridy = 0;
        gc.weightx = 0.35; 
        pnlCuerpo.add(pnlFormulario, gc);

        // TABLA: Ocupa el 65% restante de la pantalla
        gc.gridx = 1;
        gc.weightx = 0.65;
        pnlCuerpo.add(pnlTablaWrapper, gc);

        add(pnlCuerpo, BorderLayout.CENTER);

        pnlFormulario.setLayout(new GridBagLayout());
        GridBagConstraints gf = new GridBagConstraints();
        gf.fill    = GridBagConstraints.HORIZONTAL;
        gf.weightx = 1.0;
        gf.gridx   = 0;
        int r = 0;

        java.util.function.BiFunction<JLabel, Component, JPanel> campo = (lbl, cmp) -> {
            JPanel p = new JPanel(new BorderLayout(0, 3));
            p.setOpaque(false);
            p.add(lbl, BorderLayout.NORTH);
            p.add(cmp, BorderLayout.CENTER);
            return p;
        };

        java.util.function.BiFunction<Component, Component, JPanel> fila2 = (c1, c2) -> {
            JPanel p = new JPanel(new GridLayout(1, 2, 12, 0));
            p.setOpaque(false);
            p.add(c1); p.add(c2);
            return p;
        };

        java.util.function.Function<String, JPanel> sep = titulo -> {
            JPanel p = new JPanel(new BorderLayout(8, 0));
            p.setOpaque(false);
            p.setBorder(new EmptyBorder(14, 0, 8, 0));
            JLabel l = new JLabel(titulo);
            l.setFont(new Font("Segoe UI", Font.BOLD, 11));
            l.setForeground(C_AZUL_MEDIO);
            JPanel linea = new JPanel();
            linea.setBackground(new Color(C_BORDE.getRed(), C_BORDE.getGreen(), C_BORDE.getBlue(), 180));
            linea.setPreferredSize(new Dimension(0, 1));
            JPanel pL = new JPanel(new GridBagLayout());
            pL.setOpaque(false);
            GridBagConstraints gl = new GridBagConstraints();
            gl.fill = GridBagConstraints.HORIZONTAL; gl.weightx = 1.0;
            pL.add(linea, gl);
            p.add(l, BorderLayout.WEST);
            p.add(pL, BorderLayout.CENTER);
            return p;
        };

        gf.insets = new Insets(0, 0, 10, 0);

        gf.gridy = r++; pnlFormulario.add(sep.apply("DATOS PERSONALES"), gf);
        gf.gridy = r++; pnlFormulario.add(
            fila2.apply(campo.apply(lblApellido, txtApellido), campo.apply(lblNombre, txtNombre)), gf);
        gf.gridy = r++; pnlFormulario.add(campo.apply(lblDNI, txtDni), gf);
        gf.gridy = r++; pnlFormulario.add(
            fila2.apply(campo.apply(lblSexo, cbxSexo), campo.apply(lblEdad, txtEdad)), gf);

        gf.gridy = r++; pnlFormulario.add(sep.apply("CONTACTO"), gf);
        gf.gridy = r++; pnlFormulario.add(campo.apply(lblDir, txtDireccion), gf);
        gf.gridy = r++; pnlFormulario.add(
            fila2.apply(campo.apply(lblLoc, txtLocalidad), campo.apply(lblCel, txtCelular)), gf);

        gf.gridy = r++; pnlFormulario.add(sep.apply("COBERTURA MÉDICA"), gf);
        gf.gridy = r++; pnlFormulario.add(campo.apply(lblOS, txtObraSocial), gf);
        gf.gridy = r++; pnlFormulario.add(campo.apply(lblNAfiliado, txtNAfiliado), gf);

        pnlBotonesEdicion.setOpaque(false);
        pnlBotonesEdicion.setLayout(new GridLayout(1, 2, 10, 0));
        pnlBotonesEdicion.add(btnEditarPaciente);
        pnlBotonesEdicion.add(btnGuardarPaciente);

        gf.gridy = r++; gf.insets = new Insets(14, 0, 4, 0);
        pnlFormulario.add(pnlBotonesEdicion, gf);

        gf.gridy = r++; gf.weighty = 1.0; gf.fill = GridBagConstraints.VERTICAL;
        gf.insets = new Insets(0, 0, 0, 0);
        pnlFormulario.add(new JPanel() {{ setOpaque(false); }}, gf);

        pnlTablaWrapper.setLayout(new BorderLayout());
        pnlTablaWrapper.add(lblTituloTabla, BorderLayout.NORTH);
        jScrollPane1.setViewportView(grillaPacientes);
        pnlTablaWrapper.add(jScrollPane1, BorderLayout.CENTER);

        pnlFooter.setLayout(new BorderLayout());
        JPanel pnlAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        pnlAcciones.setOpaque(false);
        pnlAcciones.add(btnVerHistorial);
        pnlAcciones.add(btnCargarResultados);
        pnlFooter.add(pnlAcciones, BorderLayout.EAST);
        add(pnlFooter, BorderLayout.SOUTH);
    }

    // ── Variables ────────────────────────────────────────────────────
    private javax.swing.JButton        btnCargarResultados;
    private javax.swing.JButton        btnEditarPaciente;
    private javax.swing.JButton        btnGuardarPaciente;
    private javax.swing.JButton        btnVerHistorial;
    private javax.swing.JButton        btnVolver;
    private javax.swing.JComboBox<String> cbxSexo;
    private JTable                     grillaPacientes;
    private javax.swing.JScrollPane    jScrollPane1;
    private JLabel lblApellido, lblNombre, lblDNI, lblSexo, lblEdad;
    private JLabel lblDir, lblLoc, lblOS, lblNAfiliado, lblCel;
    private JPanel pnlHeader, pnlCuerpo, pnlFormulario, pnlBotonesEdicion;
    private JPanel pnlTablaWrapper, pnlFooter;
    private JLabel lblTituloHeader, lblTituloTabla;
    private javax.swing.JTextField txtApellido, txtNombre, txtDni, txtEdad;
    private javax.swing.JTextField txtDireccion, txtLocalidad, txtCelular;
    private javax.swing.JTextField txtObraSocial, txtNAfiliado, txtBuscar;
}