package vista;
import java.util.List;
import modelo.Usuario;

public interface IVistaGestionUsuarios {
    String BTN_GUARDAR = "guardar_usuario";
    String BTN_ELIMINAR = "eliminar_usuario";
    String BTN_VOLVER = "volver_usuarios";

    void setControlador(presentador.Controlador control);
    void cargarUsuarios(List<Usuario> lista);
    String getUsername();
    String getPassword();
    String getRol();
    int getUsuarioSeleccionadoId();
    void mostrarMensaje(String mensaje);
    void ejecutar();
}
