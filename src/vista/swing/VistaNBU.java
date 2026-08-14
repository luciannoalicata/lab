package vista.swing;

// @author lucianoalicata

import vista.interfaces.IVistaNBU;
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
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import modelo.Determinacion;
import presentador.NBUPresenter;

public class VistaNBU extends JPanel implements IVistaNBU {

    private NBUPresenter presenter;
    private boolean actualizandoVista = false;
    private boolean cargandoDatos = false;

    private final Color C_NAVY = new Color(10, 25, 47);
    private final Color C_FONDO = new Color(238, 242, 246);
    private final Color C_BLANCO = Color.WHITE;
    private final Color C_TEXTO_FUERTE = new Color(40, 50, 60);
    private final Color C_TEXTO_SUAVE = new Color(100, 115, 130);
    private final Color C_BORDE = new Color(215, 225, 235);
    private final Color C_AZUL_MEDIO = new Color(30, 110, 180);
    private final Color C_ROJO = new Color(220, 53, 69);
    private final Color C_VERDE = new Color(35, 160, 115);
    private final Color C_CABECERA_TBL = new Color(245, 248, 252);
    private final Color C_FILA_PAR = new Color(252, 254, 255);
    private final Color C_HEADER_TEXT = new Color(175, 205, 235);
    private final Font F_TBL_HEADER = new Font("Segoe UI", Font.BOLD, 12);
    private final Font F_TBL_CELL = new Font("Segoe UI", Font.PLAIN, 13);

    public VistaNBU() {
        initComponents();
        aplicarEsteticaProfesional();
        configurarDobleClicReferencia();
        setMinimumSize(new Dimension(900, 600));
    }

    @Override
    public void ejecutar() {
        setVisible(true);
    }

    @Override
    public void setPresenter(NBUPresenter presenter) {
        this.presenter = presenter;

        limpiarListeners(btnVolver);
        limpiarListeners(btnAgregarHijo);
        limpiarListeners(btnQuitarHijo);
        limpiarListeners(btnSubirHijo);
        limpiarListeners(btnBajarHijo);
        limpiarListeners(btnSubirPadre);
        limpiarListeners(btnBajarPadre);

        btnVolver.addActionListener(e -> presenter.onVolver());
        btnAgregarHijo.addActionListener(e -> presenter.onAgregarHijo());
        btnQuitarHijo.addActionListener(e -> presenter.onQuitarHijo());
        btnSubirHijo.addActionListener(e -> presenter.onSubirHijo());
        btnBajarHijo.addActionListener(e -> presenter.onBajarHijo());
        btnSubirPadre.addActionListener(e -> presenter.onSubirPadre());
        btnBajarPadre.addActionListener(e -> presenter.onBajarPadre());

        for (java.awt.event.KeyListener kl : txtBuscarNBU.getKeyListeners()) {
            txtBuscarNBU.removeKeyListener(kl);
        }

        txtBuscarNBU.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                if (!actualizandoVista && !cargandoDatos) {
                    presenter.onBuscarNBU();
                }
            }
        });

        grillaNBU.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && !actualizandoVista && !cargandoDatos
                    && grillaNBU.getSelectedRow() != -1) {
                Object nombre = grillaNBU.getValueAt(grillaNBU.getSelectedRow(), 2);
                lblPadreSeleccionado.setText(
                        nombre != null ? nombre.toString() : "Seleccione una práctica...");
                detenerEdicionTabla();
                presenter.onSeleccionarPadre();
            }
        });

        grillaHijos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && !actualizandoVista && !cargandoDatos) {
                boolean hay = grillaHijos.getSelectedRow() != -1;
                btnQuitarHijo.setEnabled(hay);
                btnSubirHijo.setEnabled(hay);
                btnBajarHijo.setEnabled(hay);
            }
        });
    }

    private void limpiarListeners(JButton btn) {
        for (java.awt.event.ActionListener al : btn.getActionListeners()) {
            btn.removeActionListener(al);
        }
    }

    @Override
    public boolean hayCambiosPendientes() {
        return false;
    }

    @Override
    public int confirmarSalidaConGuardado() {
        return 1;
    }

    @Override
    public void limpiarFocos() {
        requestFocusInWindow();
    }

    @Override
    public void mostrarMensaje(String m) {
        JOptionPane.showMessageDialog(this, m);
    }

    @Override
    public String getBusqueda() {
        return txtBuscarNBU.getText().trim();
    }

    @Override
    public int getCantidadFilas() {
        return grillaHijos.getRowCount();
    }

    @Override
    public int getIdDeterminacion(int f) {
        return 0;
    }

    @Override
    public int getIndiceHijoSeleccionado() {
        return grillaHijos.getSelectedRow();
    }

    @Override
    public int getIndicePadreSeleccionado() {
        return grillaNBU.getSelectedRow();
    }

    @Override
    public int getCantidadFilasPadre() {
        return grillaNBU.getRowCount();
    }

    @Override
    public int confirmarAccion(String mensaje, String titulo) {
        return JOptionPane.showConfirmDialog(this, mensaje, titulo, JOptionPane.YES_NO_OPTION);
    }

    @Override
    public String pedirNombreNuevoComponente() {
        javax.swing.JTextField txtNombre = new javax.swing.JTextField(25) {
            @Override
            public void addNotify() {
                super.addNotify();
                SwingUtilities.invokeLater(this::requestFocusInWindow);
            }
        };

        javax.swing.JLabel lblTip = new javax.swing.JLabel(
                "<html><div style='width:300px; margin-top:8px;'><small style='color:#4b5563; font-family: Segoe UI;'>"
                + "💡 <b>TIP DE FORMATO:</b><br>"
                + "Para crear un título o separador visual, escríbalo entre 3 (TRES) guiones.<br>"
                + "<br><i>Ejemplo:</i> <b style='color:#0a192f;'>--- FÓRMULA LEUCOCITARIA ---</b>"
                + "</small></div></html>");

        JLabel lblTitulo = new JLabel("<html><b style='font-family: Segoe UI; font-size: 14px;'>Nombre del nuevo componente:</b></html>");

        Object[] msg = {lblTitulo, txtNombre, lblTip};

        int op = JOptionPane.showConfirmDialog(this, msg, "Vincular Nuevo Componente",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        return op == JOptionPane.OK_OPTION ? txtNombre.getText().trim() : null;
    }

    @Override
    public String getCodigoHijoFila(int f) {
        return grillaHijos.getModel().getValueAt(f, 0).toString();
    }

    @Override
    public String getUnidad(int f) {
        Object v = grillaHijos.getModel().getValueAt(f, 3);
        return v != null ? v.toString() : "";
    }

    @Override
    public String getReferencia(int f) {
        Object v = grillaHijos.getModel().getValueAt(f, 4);
        return v != null ? v.toString() : "";
    }

    @Override
    public String getCodigoPadreSeleccionado() {
        int f = grillaNBU.getSelectedRow();
        return f == -1 ? null : grillaNBU.getModel().getValueAt(f, 1).toString();
    }

    @Override
    public String getCodigoHijoSeleccionado() {
        int f = grillaHijos.getSelectedRow();
        return f == -1 ? null : grillaHijos.getModel().getValueAt(f, 0).toString();
    }

    @Override
    public String getNombreHijoFila(int f) {
        Object v = grillaHijos.getModel().getValueAt(f, 2);
        return v != null ? v.toString() : "";
    }

    @Override
    public String getCodigoPadreFila(int f) {
        if (f < 0 || f >= grillaNBU.getRowCount()) {
            return "";
        }
        Object v = grillaNBU.getModel().getValueAt(f, 1);
        return v != null ? v.toString() : "";
    }

    @Override
    public void seleccionarHijoPorIndice(int indice) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            if (indice >= 0 && indice < grillaHijos.getRowCount()) {
                actualizandoVista = true;
                grillaHijos.setRowSelectionInterval(indice, indice);
                grillaHijos.scrollRectToVisible(grillaHijos.getCellRect(indice, 0, true));
                actualizandoVista = false;
                btnQuitarHijo.setEnabled(true);
                btnSubirHijo.setEnabled(true);
                btnBajarHijo.setEnabled(true);
                grillaHijos.requestFocusInWindow();
            }
        });
    }

    @Override
    public void seleccionarPadrePorIndice(int indice) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            if (indice >= 0 && indice < grillaNBU.getRowCount()) {
                actualizandoVista = true;
                grillaNBU.setRowSelectionInterval(indice, indice);
                grillaNBU.scrollRectToVisible(grillaNBU.getCellRect(indice, 0, true));
                actualizandoVista = false;
                grillaNBU.requestFocusInWindow();
            }
        });
    }

    @Override
    public void seleccionarFilaPorCodigo(String codigo) {
        actualizandoVista = true;
        for (int i = 0; i < grillaNBU.getRowCount(); i++) {
            Object v = grillaNBU.getModel().getValueAt(i, 1);
            if (v != null && v.toString().trim().equals(codigo)) {
                grillaNBU.setRowSelectionInterval(i, i);
                grillaNBU.scrollRectToVisible(grillaNBU.getCellRect(i, 0, true));
                break;
            }
        }
        actualizandoVista = false;
    }

    @Override
    public void detenerEdicionTabla() {
        if (grillaNBU.isEditing()) {
            grillaNBU.getCellEditor().stopCellEditing();
        }
        if (grillaHijos.isEditing()) {
            grillaHijos.getCellEditor().stopCellEditing();
        }
    }

    @Override
    public void cargarDeterminaciones(List<Determinacion> lista) {
        cargandoDatos = true;
        actualizandoVista = true;

        DefaultTableModel m = (DefaultTableModel) grillaNBU.getModel();
        m.setRowCount(0);
        int orden = 1;
        for (Determinacion d : lista) {
            boolean sep = d.getCodigo() == null || d.getCodigo().trim().isEmpty();
            m.addRow(new Object[]{sep ? "" : orden++, d.getCodigo(), d.getNombre(),
                sep ? "" : d.getUb()});
        }
        m.fireTableDataChanged();

        lblPadreSeleccionado.setText("Seleccione una práctica...");
        ((DefaultTableModel) grillaHijos.getModel()).setRowCount(0);
        grillaNBU.clearSelection();

        actualizandoVista = false;
        cargandoDatos = false;
    }

    @Override
    public void cargarHijos(List<Determinacion> listaHijos) {
        cargandoDatos = true;
        actualizandoVista = true;

        int indiceAnterior = grillaHijos.getSelectedRow();

        DefaultTableModel m = (DefaultTableModel) grillaHijos.getModel();
        m.setRowCount(0);
        for (Determinacion d : listaHijos) {
            m.addRow(new Object[]{d.getCodigo(), d.getPrioridad(), d.getNombre(),
                d.getUnidad(), d.getReferencia()});
        }
        m.fireTableDataChanged();

        for (int row = 0; row < grillaHijos.getRowCount(); row++) {
            recalcularAlturaFila(row);
        }

        actualizandoVista = false;
        cargandoDatos = false;

        if (indiceAnterior >= 0 && m.getRowCount() > 0) {
            int nuevoIndice = Math.min(indiceAnterior, m.getRowCount() - 1);
            actualizandoVista = true;
            grillaHijos.setRowSelectionInterval(nuevoIndice, nuevoIndice);
            grillaHijos.scrollRectToVisible(grillaHijos.getCellRect(nuevoIndice, 0, true));
            actualizandoVista = false;
            btnQuitarHijo.setEnabled(true);
            btnSubirHijo.setEnabled(true);
            btnBajarHijo.setEnabled(true);
        } else {
            grillaHijos.clearSelection();
            btnQuitarHijo.setEnabled(false);
            btnSubirHijo.setEnabled(false);
            btnBajarHijo.setEnabled(false);
        }
    }

    private void recalcularAlturaFila(int row) {
        int altura = 36;
        for (int col = 0; col < grillaHijos.getColumnCount(); col++) {
            Object v = grillaHijos.getModel().getValueAt(row, col);
            if (v != null && v.toString().contains(";")) {
                int lineas = v.toString().split(";", -1).length;
                int req = Math.max(36, lineas * 20 + 8);
                if (req > altura) {
                    altura = Math.min(req, 120);
                }
            }
        }
        grillaHijos.setRowHeight(row, altura);
    }

    private void configurarDobleClicReferencia() {
        grillaHijos.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = grillaHijos.rowAtPoint(e.getPoint());
                    int col = grillaHijos.columnAtPoint(e.getPoint());

                    if (row >= 0) {
                        Object nombreObj = grillaHijos.getModel().getValueAt(row, 2);
                        boolean esSubtitulo = (nombreObj != null && nombreObj.toString().trim().startsWith("---"));

                        if (!esSubtitulo && col == 4) {
                            Object val = grillaHijos.getModel().getValueAt(row, col);
                            String valorActual = val != null ? val.toString() : "";
                            abrirEditorTextoLargo(row, col, valorActual);
                        }
                    }
                }
            }
        });
    }

    private void abrirEditorTextoLargo(int row, int col, String textoInicial) {
        String textoParaEditar = textoInicial.replace(";", "\n");

        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Editar Valores de Referencia", JDialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(550, 400);
        dialog.setLocationRelativeTo(this);

        JPanel pnlContenido = new JPanel(new BorderLayout(5, 10));
        pnlContenido.setBorder(new EmptyBorder(15, 15, 15, 15));
        pnlContenido.setBackground(C_FONDO);

        JLabel lblSugerencia = new JLabel("<html><div style='font-family: Segoe UI; font-size: 20pt; padding-bottom:10px;'>"
                + "💡 <b>¡Escriba ordenado!</b> Todo lo que ingrese aquí <b>saldrá tal cual en el informe final</b>.<br>"
                + "Puede utilizar la tecla <b>ENTER</b> libremente para saltar de renglón."
                + "</div></html>");
        lblSugerencia.setForeground(C_TEXTO_FUERTE);

        JTextArea txtArea = new JTextArea(textoParaEditar);
        txtArea.setFont(F_TBL_CELL);
        txtArea.setLineWrap(true);
        txtArea.setWrapStyleWord(true);
        txtArea.setBorder(new EmptyBorder(8, 8, 8, 8));

        JScrollPane scroll = new JScrollPane(txtArea);
        scroll.setBorder(BorderFactory.createLineBorder(C_AZUL_MEDIO, 1));

        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlBotones.setOpaque(false);

        JButton btnGuardar = new JButton();
        configurarBoton(btnGuardar, C_VERDE, "GUARDAR", 160, 36);

        JButton btnCancelar = new JButton();
        configurarBoton(btnCancelar, C_ROJO, "CANCELAR", 120, 36);

        btnGuardar.addActionListener(e -> {
            String nuevoTexto = txtArea.getText().replace("\n", ";").replaceAll(";+", ";").trim();
            if (nuevoTexto.endsWith(";")) {
                nuevoTexto = nuevoTexto.substring(0, nuevoTexto.length() - 1);
            }

            grillaHijos.setValueAt(nuevoTexto, row, col);
            recalcularAlturaFila(row);
            dialog.dispose();

            if (grillaHijos.isEditing()) {
                grillaHijos.getCellEditor().stopCellEditing();
            }
        });

        btnCancelar.addActionListener(e -> dialog.dispose());

        pnlBotones.add(btnCancelar);
        pnlBotones.add(btnGuardar);

        pnlContenido.add(lblSugerencia, BorderLayout.NORTH);
        pnlContenido.add(scroll, BorderLayout.CENTER);
        pnlContenido.add(pnlBotones, BorderLayout.SOUTH);

        dialog.add(pnlContenido);

        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowOpened(java.awt.event.WindowEvent e) {
                txtArea.requestFocusInWindow();
            }
        });

        dialog.setVisible(true);
    }

    private class RendererTabla extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int col) {

            super.getTableCellRendererComponent(table, value, false, false, row, col);

            String textoOriginal = value != null ? value.toString() : "";

            if (textoOriginal.contains(";")) {
                String[] lineas = textoOriginal.split(";");
                StringBuilder sb = new StringBuilder("<html>");
                for (int i = 0; i < lineas.length; i++) {
                    sb.append(lineas[i].trim()
                            .replace("<", "&lt;").replace(">", "&gt;"));
                    if (i < lineas.length - 1) {
                        sb.append("<br>");
                    }
                }
                sb.append("</html>");
                setText(sb.toString());

                if (table == grillaHijos && col == 4) {
                    setToolTipText("<html><div style='padding:5px; font-family: Segoe UI;'>"
                            + textoOriginal.replace(";", "<br>") + "<br><br><b style='color:#1e6eb4;'><i>(Doble clic para editar los renglones)</i></b></div></html>");
                } else {
                    setToolTipText("<html><div style='padding:5px;'>"
                            + textoOriginal.replace(";", "<br>") + "</div></html>");
                }
            } else {
                setText(textoOriginal);
                if (table == grillaHijos && col == 4) {
                    setToolTipText("<html><div style='font-family: Segoe UI;'><b style='color:#1e6eb4;'><i>(Doble clic para editar los renglones)</i></b></div></html>");
                } else {
                    setToolTipText(textoOriginal.length() > 50 ? textoOriginal : null);
                }
            }

            boolean centro = col == 0 || col == 1
                    || (table.getColumnCount() == 4 && col == 3)
                    || (table.getColumnCount() == 5 && col == 3);
            setHorizontalAlignment(centro ? SwingConstants.CENTER : SwingConstants.LEFT);

            Object nom = table.getModel().getValueAt(row, 2);
            boolean esTitulo = nom != null
                    && nom.toString().startsWith("---")
                    && nom.toString().endsWith("---");

            boolean filaSeleccionada = table.isRowSelected(row);

            if (filaSeleccionada) {
                setBackground(table.getSelectionBackground());
                setForeground(C_NAVY);
                setFont(new Font("Segoe UI", Font.BOLD, 12));
            } else if (esTitulo) {
                setBackground(new Color(235, 242, 248));
                setForeground(C_AZUL_MEDIO);
                setFont(new Font("Segoe UI", Font.BOLD, 12));
            } else {
                setBackground(row % 2 == 0 ? C_BLANCO : C_FILA_PAR);
                setForeground(C_TEXTO_FUERTE);
                setFont(F_TBL_CELL);
            }

            setBorder(new EmptyBorder(4, 10, 4, 10));
            setOpaque(true);

            return this;
        }
    }

    private void aplicarEsteticaProfesional() {
        setBackground(C_FONDO);
        setLayout(new BorderLayout());

        // Header
        pnlHeader.setBackground(C_NAVY);
        pnlHeader.setBorder(new EmptyBorder(10, 20, 10, 20));
        pnlHeader.setLayout(new BorderLayout());

        JPanel pnlIzqHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlIzqHeader.setOpaque(false);
        pnlIzqHeader.add(btnVolver);
        lblTituloHeader.setForeground(C_BLANCO);
        lblTituloHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTituloHeader.setBorder(new EmptyBorder(0, 10, 0, 0));
        pnlIzqHeader.add(lblTituloHeader);
        pnlHeader.add(pnlIzqHeader, BorderLayout.WEST);

        JPanel pnlDerHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlDerHeader.setOpaque(false);
        pnlDerHeader.add(btnAyuda);
        JLabel lblLupa = new JLabel("Buscar:");
        lblLupa.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblLupa.setForeground(C_HEADER_TEXT);
        pnlDerHeader.add(lblLupa);
        txtBuscarNBU.setColumns(15);
        pnlDerHeader.add(txtBuscarNBU);
        pnlHeader.add(pnlDerHeader, BorderLayout.EAST);
        add(pnlHeader, BorderLayout.NORTH);

        pnlContenedorBlanco = new JPanel(new BorderLayout());
        pnlContenedorBlanco.setBackground(C_BLANCO);
        pnlContenedorBlanco.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 1, 1, C_BORDE),
                new EmptyBorder(10, 14, 10, 14)
        ));

        pnlCuerpo = new JPanel();
        pnlCuerpo.setBackground(C_BLANCO);
        pnlCuerpo.setLayout(new GridBagLayout());

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.BOTH;
        gc.weighty = 1.0;

        RendererTabla renderer = new RendererTabla();

        estilizarContenedor(pnlTablaContainer);
        pnlTablaContainer.setMinimumSize(new Dimension(280, 200));

        configurarTabla(grillaNBU, renderer);
        grillaNBU.getColumnModel().getColumn(0).setPreferredWidth(45);
        grillaNBU.getColumnModel().getColumn(0).setMaxWidth(55);
        grillaNBU.getColumnModel().getColumn(1).setPreferredWidth(85);
        grillaNBU.getColumnModel().getColumn(1).setMaxWidth(110);
        grillaNBU.getColumnModel().getColumn(2).setPreferredWidth(220);
        grillaNBU.getColumnModel().getColumn(3).setPreferredWidth(55);
        grillaNBU.getColumnModel().getColumn(3).setMaxWidth(75);

        JPanel pnlOrdenPadres = panelFlechas(btnSubirPadre, btnBajarPadre);
        pnlTablaContainer.setLayout(new BorderLayout(0, 0));
        pnlTablaContainer.add(jScrollPane1, BorderLayout.CENTER);
        pnlTablaContainer.add(pnlOrdenPadres, BorderLayout.EAST);

        estilizarContenedor(pnlDetalleHijos);
        pnlDetalleHijos.setMinimumSize(new Dimension(280, 200));
        pnlDetalleHijos.setLayout(new BorderLayout(6, 6));

        configurarTabla(grillaHijos, renderer);
        grillaHijos.getModel().addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                int fila = e.getFirstRow();
                if (fila >= 0 && fila < grillaHijos.getRowCount()) {
                    recalcularAlturaFila(fila);
                }
            }
        });
        grillaHijos.setSelectionBackground(new Color(130, 175, 220));
        grillaHijos.setCellSelectionEnabled(false);
        grillaHijos.setColumnSelectionAllowed(false);

        grillaHijos.getColumnModel().getColumn(0).setMinWidth(0);
        grillaHijos.getColumnModel().getColumn(0).setMaxWidth(0);
        grillaHijos.getColumnModel().getColumn(0).setPreferredWidth(0);
        grillaHijos.getColumnModel().getColumn(0).setResizable(false);
        grillaHijos.getColumnModel().getColumn(1).setPreferredWidth(45);
        grillaHijos.getColumnModel().getColumn(1).setMaxWidth(65);
        grillaHijos.getColumnModel().getColumn(2).setPreferredWidth(200);
        grillaHijos.getColumnModel().getColumn(3).setPreferredWidth(100);
        grillaHijos.getColumnModel().getColumn(3).setMaxWidth(140);
        grillaHijos.getColumnModel().getColumn(4).setPreferredWidth(140);

        lblTituloHijos.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTituloHijos.setForeground(C_TEXTO_SUAVE);
        lblPadreSeleccionado.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPadreSeleccionado.setForeground(C_AZUL_MEDIO);

        JPanel pnlTituloHijos = new JPanel(new BorderLayout(0, 3));
        pnlTituloHijos.setOpaque(false);
        pnlTituloHijos.setBorder(new EmptyBorder(0, 0, 6, 0));
        pnlTituloHijos.add(lblTituloHijos, BorderLayout.NORTH);
        pnlTituloHijos.add(lblPadreSeleccionado, BorderLayout.CENTER);

        JPanel pnlBotonesHijos = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pnlBotonesHijos.setOpaque(false);
        pnlBotonesHijos.add(btnAgregarHijo);
        pnlBotonesHijos.add(btnQuitarHijo);

        JPanel pnlOrdenHijos = panelFlechas(btnSubirHijo, btnBajarHijo);

        pnlDetalleHijos.add(pnlTituloHijos, BorderLayout.NORTH);
        pnlDetalleHijos.add(jScrollPaneHijos, BorderLayout.CENTER);
        pnlDetalleHijos.add(pnlBotonesHijos, BorderLayout.SOUTH);
        pnlDetalleHijos.add(pnlOrdenHijos, BorderLayout.EAST);

        gc.gridx = 0;
        gc.weightx = 0.50;
        gc.insets = new Insets(0, 0, 0, 8);
        pnlCuerpo.add(pnlTablaContainer, gc);
        gc.gridx = 1;
        gc.weightx = 0.50;
        gc.insets = new Insets(0, 8, 0, 0);
        pnlCuerpo.add(pnlDetalleHijos, gc);

        pnlContenedorBlanco.add(pnlCuerpo, BorderLayout.CENTER);
        add(pnlContenedorBlanco, BorderLayout.CENTER);

        configurarBoton(btnAgregarHijo, C_AZUL_MEDIO, "+ VINCULAR", 110, 34);
        configurarBoton(btnQuitarHijo, C_ROJO, "- QUITAR", 110, 34);
        configurarBotonRetroceso(btnVolver);
        configurarBotonAyuda(btnAyuda);

        configurarBotonFlecha(btnSubirHijo, "/reportes/img/flecha_arriba_icon.png");
        configurarBotonFlecha(btnBajarHijo, "/reportes/img/flecha_abajo_icon.png");
        configurarBotonFlecha(btnSubirPadre, "/reportes/img/flecha_arriba_icon.png");
        configurarBotonFlecha(btnBajarPadre, "/reportes/img/flecha_abajo_icon.png");

        btnQuitarHijo.setEnabled(false);
        btnSubirHijo.setEnabled(false);
        btnBajarHijo.setEnabled(false);

        estilizarCampoBuscador(txtBuscarNBU);
    }

    private void configurarTabla(JTable tabla, RendererTabla renderer) {
        tabla.setRowHeight(36);
        tabla.setFont(F_TBL_CELL);
        tabla.setShowHorizontalLines(true);
        tabla.setShowVerticalLines(false);
        tabla.setSelectionBackground(new Color(180, 210, 240));
        tabla.setSelectionForeground(C_NAVY);
        tabla.setIntercellSpacing(new Dimension(0, 1));
        tabla.setBorder(BorderFactory.createEmptyBorder());
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setDefaultRenderer(Object.class, renderer);
        tabla.setRowSelectionAllowed(true);
        tabla.setColumnSelectionAllowed(false);
        tabla.setFillsViewportHeight(true);

        tabla.getTableHeader().setFont(F_TBL_HEADER);
        tabla.getTableHeader().setBackground(C_CABECERA_TBL);
        tabla.getTableHeader().setForeground(C_TEXTO_SUAVE);
        tabla.getTableHeader().setPreferredSize(new Dimension(0, 34));
        tabla.getTableHeader().setBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDE));
        tabla.getTableHeader().setReorderingAllowed(false);
    }

    private JPanel panelFlechas(JButton subir, JButton bajar) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(0, 6, 0, 0));
        subir.setAlignmentX(Component.CENTER_ALIGNMENT);
        bajar.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(Box.createVerticalGlue());
        p.add(subir);
        p.add(Box.createVerticalStrut(8));
        p.add(bajar);
        p.add(Box.createVerticalGlue());
        return p;
    }

    private void estilizarContenedor(JPanel pnl) {
        pnl.setBackground(C_BLANCO);
        pnl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDE, 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));
    }

    private void estilizarCampoBuscador(JTextField tf) {
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBackground(new Color(25, 45, 75));
        tf.setForeground(C_BLANCO);
        tf.setCaretColor(C_BLANCO);
        tf.setColumns(15);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(50, 80, 120), 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
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
        btn.setMaximumSize(new Dimension(w + 10, h));
        btn.setMinimumSize(new Dimension(w - 20, h));
    }

    private void configurarBotonFlecha(JButton btn, String ruta) {
        btn.setText("");
        btn.setBackground(new Color(228, 234, 242));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(30, 30));
        btn.setMaximumSize(new Dimension(30, 30));
        btn.setMinimumSize(new Dimension(28, 28));
        btn.setMargin(new Insets(0, 0, 0, 0));
        try {
            java.net.URL url = getClass().getResource(ruta);
            if (url != null) {
                Image img = new ImageIcon(url).getImage()
                        .getScaledInstance(16, 16, Image.SCALE_SMOOTH);
                btn.setIcon(new ImageIcon(img));
            } else {
                btn.setText(ruta.contains("arriba") ? "▲" : "▼");
                btn.setForeground(C_AZUL_MEDIO);
            }
        } catch (Exception e) {
            btn.setText(ruta.contains("arriba") ? "▲" : "▼");
            btn.setForeground(C_AZUL_MEDIO);
        }
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
        btn.setBorder(new EmptyBorder(0, 0, 0, 14));
        try {
            java.net.URL url = getClass().getResource("/reportes/img/flecha_icon.png");
            if (url != null) {
                Image img = new ImageIcon(url).getImage()
                        .getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                btn.setIcon(new ImageIcon(img));
            } else {
                btn.setText("←");
                btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
            }
        } catch (Exception e) {
        }
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setForeground(C_BLANCO);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setForeground(C_HEADER_TEXT);
            }
        });
    }

    private void configurarBotonAyuda(JButton btn) {
        btn.setText("?");
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(C_AZUL_MEDIO);
        btn.setForeground(C_BLANCO);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(32, 32));
        btn.setMinimumSize(new Dimension(30, 30));
        btn.setMaximumSize(new Dimension(34, 34));
        btn.setMargin(new Insets(0, 0, 0, 0));
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        btn.setVerticalAlignment(SwingConstants.CENTER);
        btn.setIcon(null);
        btn.addActionListener(e -> mostrarAyudaRapida());
    }

    private void mostrarAyudaRapida() {
        String msj = "<html><body style='width:380px;font-family:Segoe UI;color:#28323c;'>"
                + "<h2 style='color:#1e6eb4;margin-top:0;'>💡 Tips para la Carga de Datos</h2>"
                + "<hr style='color:#d7e1eb;'>"
                + "<h3 style='color:#23a073;margin-top:10px;margin-bottom:5px;'>1. Textos Largos (Doble Clic)</h3>"
                + "Haga <b>doble clic</b> sobre un Valor de Referencia para abrir el editor avanzado. "
                + "Allí podrá usar Enter normalmente para saltar de renglón.<br>"
                + "<h3 style='color:#23a073;margin-top:15px;margin-bottom:5px;'>2. Títulos / Separadores</h3>"
                + "Al vincular, escriba el nombre entre 3 (TRES) guiones: <b>--- TÍTULO ---</b><br>"
                + "<h3 style='color:#23a073;margin-top:15px;margin-bottom:5px;'>3. Prioridades</h3>"
                + "Seleccione una fila y use <b>▲ / ▼</b> para moverla y definir la prioridad de las prácticas. De esta manera saldrán en el informe y se acomodarán al momento de cargar resultados.</body></html>";
        JOptionPane.showMessageDialog(this, msj, "Ayuda BIOTEC", JOptionPane.INFORMATION_MESSAGE);
    }

    private void initComponents() {
        pnlHeader = new JPanel();
        lblTituloHeader = new JLabel("NOMENCLADOR BIOQUÍMICO ÚNICO");
        txtBuscarNBU = new JTextField();
        btnVolver = new JButton();
        btnAyuda = new JButton();
        pnlContenedorBlanco = new JPanel();
        pnlCuerpo = new JPanel();
        pnlTablaContainer = new JPanel();
        jScrollPane1 = new JScrollPane();
        grillaNBU = new JTable();
        btnSubirPadre = new JButton();
        btnBajarPadre = new JButton();
        pnlDetalleHijos = new JPanel();
        jScrollPaneHijos = new JScrollPane();
        grillaHijos = new JTable();
        lblTituloHijos = new JLabel("COMPONENTES DE LA PRÁCTICA:");
        lblPadreSeleccionado = new JLabel("Seleccione una práctica...");
        btnAgregarHijo = new JButton();
        btnQuitarHijo = new JButton();
        btnSubirHijo = new JButton();
        btnBajarHijo = new JButton();

        grillaNBU.setModel(new DefaultTableModel(
                new Object[][]{}, new String[]{"Nº", "CÓDIGO", "PRÁCTICA (ESTUDIO PADRE)", "UB"}
        ) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        });
        jScrollPane1.setViewportView(grillaNBU);
        jScrollPane1.setBorder(BorderFactory.createEmptyBorder());
        jScrollPane1.getViewport().setBackground(C_BLANCO);

        grillaHijos.setModel(new DefaultTableModel(
                new Object[][]{}, new String[]{"CÓDIGO_OCULTO", "ORDEN", "COMPONENTE", "UNIDAD", "REFERENCIA"}
        ) {
            @Override
            public boolean isCellEditable(int r, int c) {
                if (c == 3) {
                    return true;
                }
                return false;
            }
        });
        jScrollPaneHijos.setViewportView(grillaHijos);
        jScrollPaneHijos.setBorder(BorderFactory.createLineBorder(C_BORDE));
        jScrollPaneHijos.getViewport().setBackground(C_BLANCO);

        btnSubirPadre.setPreferredSize(new Dimension(30, 30));
        btnBajarPadre.setPreferredSize(new Dimension(30, 30));
        btnSubirHijo.setPreferredSize(new Dimension(30, 30));
        btnBajarHijo.setPreferredSize(new Dimension(30, 30));

        setLayout(new BorderLayout());
        add(pnlHeader, BorderLayout.NORTH);
    }

    private JButton btnVolver, btnAyuda;
    private JButton btnAgregarHijo, btnQuitarHijo;
    private JButton btnSubirHijo, btnBajarHijo, btnSubirPadre, btnBajarPadre;
    private JTable grillaNBU, grillaHijos;
    private JPanel pnlHeader, pnlContenedorBlanco, pnlCuerpo;
    private JPanel pnlTablaContainer, pnlDetalleHijos;
    private JLabel lblTituloHeader, lblTituloHijos, lblPadreSeleccionado;
    private JScrollPane jScrollPane1, jScrollPaneHijos;
    private JTextField txtBuscarNBU;
}
