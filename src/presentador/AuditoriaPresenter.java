package presentador;
/**
 *
 * @author luciano
 */

import dao.AuditoriaDAO;
import dao.UsuarioDAO;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import modelo.Auditoria;
import modelo.Usuario;
import vista.IVistaAuditoria;
import vista.IVistaPrincipal;

public class AuditoriaPresenter implements ActionListener {

    private IVistaAuditoria vauditoria;
    private IVistaPrincipal vp;
    private AuditoriaDAO auditoriaDAO;
    private UsuarioDAO usuarioDAO;

    public AuditoriaPresenter(IVistaAuditoria vauditoria, IVistaPrincipal vp, AuditoriaDAO auditoriaDAO, UsuarioDAO usuarioDAO) {
        this.vauditoria = vauditoria;
        this.vp = vp;
        this.auditoriaDAO = auditoriaDAO;
        this.usuarioDAO = usuarioDAO;
        
        this.vauditoria.setControlador(this);
    }

    public void iniciar() {
        // 1. Cargamos el combobox de usuarios
        List<Usuario> listaU = usuarioDAO.listarTodos();
        List<String> nombres = listaU.stream().map(Usuario::getUsername).toList();
        vauditoria.cargarComboUsuarios(nombres);

        // 2. Cargamos la tabla inicialmente con todo
        vauditoria.cargarTabla(auditoriaDAO.listarConFiltros("Todos", null));

        // 3. Activamos el modo inmersión
        vp.activarModoInmersion();
        vp.mostrarSeccion("auditoria");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();

        switch (comando) {
            case IVistaAuditoria.BTN_DETALLAR_CAMBIOS:
                detallarCambios();
                break;
            case IVistaAuditoria.BTN_FILTRAR_USUARIO:
            case IVistaAuditoria.BTN_FILTRAR_FECHA:
                filtrarAuditoria();
                break;
            case IVistaAuditoria.BTN_SALIR:
                vp.desactivarModoInmersion();
                vp.volverInicio();
                break;
        }
    }

    private void detallarCambios() {
        Auditoria log = vauditoria.getAuditoriaSeleccionada();
        if (log != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("Usuario: ").append(log.getUsuarioNombre()).append("\n");
            sb.append("Tabla: ").append(log.getTablaAfectada().toUpperCase()).append("\n");
            sb.append("Acción: ").append(log.getAccion()).append("\n");
            sb.append("Fecha: ").append(log.getFechaHora()).append("\n");
            sb.append("------------------------------------------\n");
            sb.append("VALOR ANTERIOR:\n").append(log.getValorAnterior() != null ? log.getValorAnterior() : "[NADA]").append("\n\n");
            sb.append("VALOR NUEVO:\n").append(log.getValorNuevo()).append("\n");
            sb.append("------------------------------------------\n");
            sb.append("RESUMEN: ").append(log.getDetalle());

            // El presentador envía el texto puro; no le importa si es un JOptionPane o un alert web
            vauditoria.mostrarDetalleCambios("Detalle de Cambios", sb.toString());
        }
    }

    private void filtrarAuditoria() {
        String userFiltro = vauditoria.getUsuarioSeleccionado();
        Date fechaFiltro = vauditoria.getFechaSeleccionada();

        ArrayList<Auditoria> filtrados = auditoriaDAO.listarConFiltros(userFiltro, fechaFiltro);
        vauditoria.cargarTabla(filtrados);
    }
}