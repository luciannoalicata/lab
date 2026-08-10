package vista.interfaces;

import java.util.List;
import modelo.Determinacion;
import presentador.NBUPresenter;

// @author lucianoalicata

public interface IVistaNBU {
    
    String BTN_GUARDAR_CAMBIOS = "guardar_nbu";
    String BTN_SALIR = "cerrar_nbu";
    
    public void ejecutar();
    void setPresenter(NBUPresenter presenter);    
    int confirmarAccion(String mensaje, String titulo);
    void limpiarFocos();
    int confirmarSalidaConGuardado(); 
    String pedirNombreNuevoComponente();
    void mostrarMensaje(String mensaje); 
    String getBusqueda();
    int getCantidadFilas();
    void cargarHijos(java.util.List<modelo.Determinacion> listaHijos);
    String getCodigoPadreSeleccionado();
    String getCodigoHijoSeleccionado();
    void seleccionarFilaPorCodigo(String codigo);
    int getCantidadFilasPadre();
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
    boolean hayCambiosPendientes();
}