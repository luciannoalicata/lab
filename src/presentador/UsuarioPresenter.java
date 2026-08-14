package presentador;

// @author lucianoalicata

import dao.UsuarioDAO;
import modelo.Usuario;
import presentador.router.AppRouter;
import vista.interfaces.IVistaGestionUsuarios;

public class UsuarioPresenter {

    private final IVistaGestionUsuarios vista;
    private final AppRouter router;
    private final UsuarioDAO usuarioDAO;
    private final Usuario usuarioLogueado; 
    private boolean guardando = false; 

    public UsuarioPresenter(IVistaGestionUsuarios vista, AppRouter router, 
                            UsuarioDAO usuarioDAO, Usuario usuarioLogueado) {
        this.vista = vista;
        this.router = router;
        this.usuarioDAO = usuarioDAO;
        this.usuarioLogueado = usuarioLogueado;
    }

    public void iniciar() {
        vista.setPresenter(this);
        vista.limpiarCampos();
        cargarListaUsuarios();
    }

    private void cargarListaUsuarios() {
        vista.cargarUsuarios(usuarioDAO.listarTodos());
    }

    public void onGuardar() {
        if (guardando) return;
        guardando = true;
        
        try {
            String nuevoUser = vista.getUsername();
            String nuevaPass = vista.getPassword();
            String nuevoRol = vista.getRol();

            if (nuevoUser.isEmpty() || nuevaPass.isEmpty()) {
                vista.mostrarMensaje("Nombre de usuario y contraseña son obligatorios.");
                return;
            }

            Usuario uNuevo = new Usuario();
            uNuevo.setUsername(nuevoUser);
            uNuevo.setRol(nuevoRol);

            if (usuarioDAO.guardar(uNuevo, nuevaPass)) {
                vista.mostrarMensaje("Usuario guardado con éxito.");
                vista.limpiarCampos();
                cargarListaUsuarios();
            } else {
                vista.mostrarMensaje("Error al guardar usuario. Puede que el nombre de usuario ya exista.");
            }
        } finally {
            guardando = false;
        }
    }

    public void onEliminar() {
        if (guardando) return;
        guardando = true;
        
        try {
            int idSeleccionado = vista.getUsuarioSeleccionadoId();

            if (idSeleccionado == -1) {
                vista.mostrarMensaje("Seleccione un usuario de la tabla para eliminar.");
                return;
            }
            
            if (idSeleccionado == this.usuarioLogueado.getIdUsuario()) {
                vista.mostrarMensaje("No puede eliminarse a sí mismo mientras está en sesión.");
                return;
            }

            int confirm = vista.confirmarAccion(
                "¿Está seguro de eliminar este usuario? Esta acción no se puede deshacer.", 
                "Confirmar Eliminación"
            );

            if (confirm == 0) { 
                if (usuarioDAO.eliminar(idSeleccionado)) {
                    vista.mostrarMensaje("Usuario eliminado correctamente.");
                    vista.limpiarCampos();
                    cargarListaUsuarios();
                } else {
                    vista.mostrarMensaje("Error al intentar eliminar el usuario.");
                }
            }
        } finally {
            guardando = false;
        }
    }

    public void onVolver() {
        router.irAInicio();
    }

    public void onSeleccionarUsuario() {
    }
}