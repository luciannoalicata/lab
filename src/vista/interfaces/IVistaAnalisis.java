package vista.interfaces;

import java.util.ArrayList;
import modelo.Analisis;
import presentador.AnalisisPresenter;

public interface IVistaAnalisis {
    String BTN_VER_DETALLES = "ver_info_analisis";
    String BTN_IMPRIMIR_ANALISIS = "imprimir_analisis";
    String BTN_VOLVER_VLA = "volver_analisis";
    
   void setPresenter(AnalisisPresenter presenter);    
    int confirmarAccion(String mensaje, String titulo);
    void limpiarFocos();
    void cargarAnalisisEnTabla(ArrayList<Analisis> lista);
    Analisis getAnalisisSeleccionado();
    String getTextoBusqueda();
    void habilitarBotonVerDetalles(boolean b);
    void habilitarBotonImprimir(boolean b);
    void mostrarMensaje(String mensaje);
    void ejecutar();
}