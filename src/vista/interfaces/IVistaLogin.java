package vista.interfaces;

import presentador.SesionPresenter;

/**
 *
 * @author luciano
 */
public interface IVistaLogin {

    String BTN_INGRESAR = "iniciar_sesion";

    void ejecutar();
    void setPresenter(SesionPresenter presenter);    
    int confirmarAccion(String mensaje, String titulo);
    void limpiarFocos();
    void cerrarPantalla();
    String getUsuario();
    String getClave();
    void mostrarMensaje(String mensaje);
    void limpiarCampos();
}
