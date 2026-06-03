package presentador;
/**
 *
 * @author luciano
 */
import dao.UsuarioDAO;
import modelo.Usuario;
import vista.IVistaGestionUsuarios;
import vista.IVistaPrincipal;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UsuarioPresenter implements ActionListener {

    private IVistaGestionUsuarios vgu;
    private IVistaPrincipal vp;
    private UsuarioDAO usuarioDAO;
    private Usuario usuarioLogueado; // <- Dependencia de seguridad

    public UsuarioPresenter(IVistaGestionUsuarios vgu, IVistaPrincipal vp, UsuarioDAO usuarioDAO, Usuario usuarioLogueado) {
        this.vgu = vgu;
        this.vp = vp;
        this.usuarioDAO = usuarioDAO;
        this.usuarioLogueado = usuarioLogueado;
        
        this.vgu.setControlador(this);
    }

    public void iniciar() {
        vgu.limpiarCampos();
        vgu.cargarUsuarios(usuarioDAO.listarTodos());
        vp.activarModoInmersion();
        vp.mostrarSeccion("usuarios");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();

        switch (comando) {
            case IVistaGestionUsuarios.BTN_GUARDAR:
                guardarUsuario();
                break;
            case IVistaGestionUsuarios.BTN_ELIMINAR:
                eliminarUsuario();
                break;
            case IVistaGestionUsuarios.BTN_VOLVER:
                vp.desactivarModoInmersion();
                vp.volverInicio();
                break;
        }
    }

    private void guardarUsuario() {
        String nuevoUser = vgu.getUsername();
        String nuevaPass = vgu.getPassword();
        String nuevoRol = vgu.getRol();

        if (nuevoUser.isEmpty() || nuevaPass.isEmpty()) {
            vgu.mostrarMensaje("Nombre y contraseña son obligatorios.");
            return;
        }

        Usuario uNuevo = new Usuario();
        uNuevo.setUsername(nuevoUser);
        uNuevo.setRol(nuevoRol);

        if (usuarioDAO.guardar(uNuevo, nuevaPass)) {
            vgu.mostrarMensaje("Usuario creado con éxito.");
            vgu.limpiarCampos(); // Limpiamos las cajas de texto
            vgu.cargarUsuarios(usuarioDAO.listarTodos());
        } else {
            vgu.mostrarMensaje("Error al crear usuario (quizás ya existe).");
        }
    }

    private void eliminarUsuario() {
        int idSeleccionado = vgu.getUsuarioSeleccionadoId();

        if (idSeleccionado == -1) {
            vgu.mostrarMensaje("Seleccione un usuario de la tabla.");
            return;
        }

        // --- VALIDACIÓN DE SEGURIDAD CRÍTICA ---
        if (idSeleccionado == this.usuarioLogueado.getIdUsuario()) {
            vgu.mostrarMensaje("Seguridad: No puede eliminarse a sí mismo mientras está en sesión.");
            return;
        }
        // ---------------------------------------

        int confirm = vgu.confirmarAccion("¿Está seguro de eliminar este usuario? Esta acción no se puede deshacer.", "Confirmar Eliminación");

        if (confirm == 0) { // 0 es YES_OPTION
            if (usuarioDAO.eliminar(idSeleccionado)) {
                vgu.mostrarMensaje("Usuario eliminado correctamente.");
                vgu.cargarUsuarios(usuarioDAO.listarTodos());
            } else {
                vgu.mostrarMensaje("Error al intentar eliminar el usuario.");
            }
        }
    }
}