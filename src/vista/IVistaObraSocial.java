package vista;

import java.util.ArrayList;
import modelo.ObraSocial;
/**
 *
 * @author luciano
 */
public interface IVistaObraSocial {
    
    String BTN_AGREGAR_OS = "agregar_obra_social";
    String BTN_ELIMINAR_OS = "eliminar_obra_social";
    String BTN_MODIFICAR_ARANCEL_OS = "modificar_arancel";
    String BTN_VOLVER_OS = "volver_os";
  
    void ejecutar();
    void setControlador(java.awt.event.ActionListener presentador);
    int confirmarAccion(String mensaje, String titulo);
    String pedirDato(String mensaje, String titulo);
    String getCodigoObraSocial();
    String getNombreObraSocial();
    double getArancel();
    String getTextoBusqueda();
    void cargarObrasSocialesEnTabla(ArrayList<ObraSocial> obs);
    ObraSocial getObraSocialSeleccionada();
    void limpiarCampos();
    void habilitarBotonAgregar(boolean b);
    void habilitarBotonEliminar(boolean b);
    void mostrarMensaje(String mensaje); 
}
