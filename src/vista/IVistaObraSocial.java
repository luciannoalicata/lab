package vista;

import presentador.Controlador;
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
    
    public void ejecutar();
    public void setControlador(Controlador control);
    
    public String getCodigoObraSocial();
    public String getNombreObraSocial();
    public double getArancel();
    public String getTextoBusqueda();
     public void cargarObrasSocialesEnTabla(ArrayList<ObraSocial> obs);
    public ObraSocial getObraSocialSeleccionada();
    
    public void limpiarCampos();
    void habilitarBotonAgregar(boolean b);
    void habilitarBotonEliminar(boolean b);
    
    
    public void mostrarMensaje(String mensaje); 
}
