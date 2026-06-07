package vista.interfaces;
/**
 *
 * @author luciano
 */
import java.util.List;
import modelo.Usuario;
import presentador.UsuarioPresenter;

public interface IVistaGestionUsuarios {
    
    String BTN_GUARDAR = "guardar_usuario";
    String BTN_ELIMINAR = "eliminar_usuario";
    String BTN_VOLVER = "volver_usuarios";

    void setPresenter(UsuarioPresenter presenter);    
    int confirmarAccion(String mensaje, String titulo);
    void limpiarFocos();
    void limpiarCampos();
    void cargarUsuarios(List<Usuario> lista);
    String getUsername();
    String getPassword();
    String getRol();
    int getUsuarioSeleccionadoId();
    void mostrarMensaje(String mensaje);
    void ejecutar();

}
