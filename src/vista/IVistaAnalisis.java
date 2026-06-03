package vista;

import presentador.Controlador;
import java.util.ArrayList;
import modelo.Analisis;

/**
 *
 * @author luciano
 */
public interface IVistaAnalisis {
    
    String BTN_VER_DETALLES = "ver_info_analisis";
    String BTN_IMPRIMIR_ANALISIS = "imprimir_analisis";
    String BTN_VOLVER_VLA = "volver_analisis";
    
    public void ejecutar();
    public void setControlador(Controlador control);
    
    
    public String getTextoBusqueda();
    void habilitarBotonVerDetalles(boolean b);
    void habilitarBotonImprimir(boolean b);
    
    public void cargarAnalisisEnTabla(ArrayList<Analisis> obs);
    public Analisis getAnalisisSeleccionado();
    
    
    public void mostrarMensaje(String mensaje); 
}
