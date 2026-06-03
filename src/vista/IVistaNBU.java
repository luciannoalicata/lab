package vista;

import presentador.Controlador;
import java.util.List;
import modelo.Determinacion;
/**
 *
 * @author luciano
 */
public interface IVistaNBU {
    
    String BTN_GUARDAR_CAMBIOS = "guardar_nbu";
    String BTN_SALIR = "cerrar_nbu";
    
    public void ejecutar();
    public void setControlador(Controlador control);
    public void mostrarMensaje(String mensaje); 
    public String getBusqueda();
    int getCantidadFilas();
    
    // Métodos para la gestión de Padre-Hijo en NBU
    void cargarHijos(java.util.List<modelo.Determinacion> listaHijos);
    String getCodigoPadreSeleccionado();
    String getCodigoHijoSeleccionado();
    void seleccionarFilaPorCodigo(String codigo);
    String getCodigoHijoFila(int fila);
    String getNombreHijoFila(int fila);
     int getIndiceHijoSeleccionado();
     void seleccionarHijoPorIndice(int indice);
     int getIndicePadreSeleccionado();
     String getCodigoPadreFila(int fila);
     void seleccionarPadrePorIndice(int indice);

    int getIdDeterminacion(int fila);
    String getUnidad(int fila);
    String getReferencia(int fila);

    void cargarDeterminaciones(List<Determinacion> lista);
    void detenerEdicionTabla();
}
