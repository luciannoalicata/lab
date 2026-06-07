package presentador;

import dao.AuditoriaDAO;
import dao.UsuarioDAO;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import modelo.Auditoria;
import modelo.Usuario;
import presentador.router.AppRouter;
import vista.interfaces.IVistaAuditoria;

// 1. Adiós al implements ActionListener
public class AuditoriaPresenter {

    private final IVistaAuditoria vauditoria;
    private final AppRouter router; // 2. El router toma el control de la navegación
    private final AuditoriaDAO auditoriaDAO;
    private final UsuarioDAO usuarioDAO;

    // 3. Constructor actualizado
    public AuditoriaPresenter(IVistaAuditoria vauditoria, AppRouter router, AuditoriaDAO auditoriaDAO, UsuarioDAO usuarioDAO) {
        this.vauditoria = vauditoria;
        this.router = router;
        this.auditoriaDAO = auditoriaDAO;
        this.usuarioDAO = usuarioDAO;
    }

    public void iniciar() {
        vauditoria.setPresenter(this); // Conectamos la vista al presentador
        
        // 1. Cargamos el combobox de usuarios
        List<Usuario> listaU = usuarioDAO.listarTodos();
        List<String> nombres = listaU.stream().map(Usuario::getUsername).toList();
        vauditoria.cargarComboUsuarios(nombres);

        // 2. Cargamos la tabla inicialmente con todo
        vauditoria.cargarTabla(auditoriaDAO.listarConFiltros("Todos", null));

        // El AppRouter se encarga de mostrar la vista en pantalla
    }

    // ════════════════════════════════════════════════════════════════
    //  MÉTODOS EXPLÍCITOS LLAMADOS POR LA VISTA (MVP Puro)
    // ════════════════════════════════════════════════════════════════

    public void onDetallarCambios() {
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

    public void onFiltrarFecha() {
        filtrarAuditoria();
    }

    public void onFiltrarUsuario() {
        filtrarAuditoria();
    }

    public void onVolver() {
        router.irAInicio();
    }

    // ════════════════════════════════════════════════════════════════
    //  MÉTODOS PRIVADOS DE APOYO
    // ════════════════════════════════════════════════════════════════

    private void filtrarAuditoria() {
        String userFiltro = vauditoria.getUsuarioSeleccionado();
        Date fechaFiltro = vauditoria.getFechaSeleccionada();

        ArrayList<Auditoria> filtrados = auditoriaDAO.listarConFiltros(userFiltro, fechaFiltro);
        vauditoria.cargarTabla(filtrados);
    }
}