package presentador;

import dao.UsuarioDAO;
import modelo.Usuario;
import presentador.router.AppRouter;
import vista.interfaces.IVistaGestionUsuarios;

// 1. Adiós al implements ActionListener
public class UsuarioPresenter {

    private final IVistaGestionUsuarios vgu;
    private final AppRouter router; // 2. El Router toma el control de la navegación
    private final UsuarioDAO usuarioDAO;
    private final Usuario usuarioLogueado; 

    // 3. Constructor actualizado
    public UsuarioPresenter(IVistaGestionUsuarios vgu, AppRouter router, UsuarioDAO usuarioDAO, Usuario usuarioLogueado) {
        this.vgu = vgu;
        this.router = router;
        this.usuarioDAO = usuarioDAO;
        this.usuarioLogueado = usuarioLogueado;
    }

    public void iniciar() {
        vgu.setPresenter(this); // Conectamos la vista al presentador
        vgu.limpiarCampos();
        vgu.cargarUsuarios(usuarioDAO.listarTodos());
        // El AppRouter se encarga de mostrar la vista en pantalla
    }

    // ════════════════════════════════════════════════════════════════
    //  MÉTODOS EXPLÍCITOS LLAMADOS POR LA VISTA (MVP Puro)
    // ════════════════════════════════════════════════════════════════

    public void onGuardar() {
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
            vgu.mostrarMensaje("Usuario creado/actualizado con éxito.");
            vgu.limpiarCampos(); 
            vgu.cargarUsuarios(usuarioDAO.listarTodos());
        } else {
            vgu.mostrarMensaje("Error al guardar usuario (quizás ya existe).");
        }
    }

    public void onEliminar() {
        int idSeleccionado = vgu.getUsuarioSeleccionadoId();

        if (idSeleccionado == -1) {
            vgu.mostrarMensaje("Seleccione un usuario de la tabla.");
            return;
        }
        
        if (idSeleccionado == this.usuarioLogueado.getIdUsuario()) {
            vgu.mostrarMensaje("Seguridad: No puede eliminarse a sí mismo mientras está en sesión.");
            return;
        }

        int confirm = vgu.confirmarAccion("¿Está seguro de eliminar este usuario? Esta acción no se puede deshacer.", "Confirmar Eliminación");

        if (confirm == 0) { 
            if (usuarioDAO.eliminar(idSeleccionado)) {
                vgu.mostrarMensaje("Usuario eliminado correctamente.");
                vgu.limpiarCampos(); // Limpiamos por si había algo escrito
                vgu.cargarUsuarios(usuarioDAO.listarTodos());
            } else {
                vgu.mostrarMensaje("Error al intentar eliminar el usuario.");
            }
        }
    }

    public void onVolver() {
        router.irAInicio();
    }

    public void onSeleccionarUsuario() {
        // Opcional: Si quieres que al hacer clic en la tabla se llenen los campos
        // Puedes implementar la lógica aquí (requiere un método en la vista para extraer los datos de la fila).
        // Por ahora, lo dejamos vacío para que solo cumpla con la selección visual.
    }
}