package vista;

import presentador.Controlador;

/**
 *
 * @author luciano
 */
public interface IVistaLogin {

    String BTN_INGRESAR = "iniciar_sesion";

    public void ejecutar();

    public void setControlador(Controlador control);

    public String getUsuario();

    public String getClave();

    public void mostrarMensaje(String mensaje);
    
    public void limpiarCampos();
}
