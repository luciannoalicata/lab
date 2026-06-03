package vista;

import presentador.Controlador;
import java.util.ArrayList;
import java.util.Date;
import modelo.Analisis;

/**
 *
 * @author luciano
 */
public interface IVistaHistorialAnalisis {
    
    String BTN_GENERAR_INFORME = "generar_informe";
    String BTN_VER_DETALLES = "ver_detalle";
    String BTN_CERRAR = "cerrar_historial";
    
    public void ejecutar();
    public void setControlador(Controlador control);
    public void setFechaSeleccionada(Date fecha);
    public void setNombrePaciente(String nombre);
    public void mostrarMensaje(String mensaje); 
    void habilitarBotonVerDetalles(boolean b);
void habilitarBotonImprimir(boolean b);
    public int getAnalisisSeleccionadoId();
    public Date getFechaSeleccionada();
public void cargarHistorial(ArrayList<Analisis> lista);
    
    
    
    
}
