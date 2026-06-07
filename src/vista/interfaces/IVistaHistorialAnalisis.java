package vista.interfaces;

import java.util.ArrayList;
import java.util.Date;
import modelo.Analisis;
import presentador.HistorialPresenter;
/**
 *
 * @author luciano
 */
public interface IVistaHistorialAnalisis {
    
    String BTN_GENERAR_INFORME = "generar_informe";
    String BTN_VER_DETALLES = "ver_detalle";
    String BTN_CERRAR = "cerrar_historial";
    
    void ejecutar();
    void setPresenter(HistorialPresenter presenter);    
    void setFechaSeleccionada(Date fecha);
    void setNombrePaciente(String nombre);
    void mostrarMensaje(String mensaje); 
    void habilitarBotonVerDetalles(boolean b);void habilitarBotonImprimir(boolean b);
    int getAnalisisSeleccionadoId();
    Date getFechaSeleccionada();
    void cargarHistorial(ArrayList<Analisis> lista);
    int confirmarAccion(String mensaje, String titulo);
    void limpiarFocos();
}
