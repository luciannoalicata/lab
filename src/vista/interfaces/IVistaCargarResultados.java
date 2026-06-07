package vista.interfaces;

import java.util.List;
import modelo.Determinacion;
import presentador.ResultadoPresenter;
/*
 * @author luciano
 */
public interface IVistaCargarResultados {
    
    String BTN_GUARDAR_RESULTADOS = "guardar_resultados";
    String BTN_CERRAR = "cerrar";

    void ejecutar();
    void setPresenter(ResultadoPresenter presenter);    
    int confirmarAccion(String mensaje, String titulo);
    void limpiarFocos();
    void ocultarSugerenciasFlotantes();
    void setNombrePaciente(String nombre);
    void setObraSocial(String obraSocial);
    String getObraSocial();
    void detenerEdicionTabla();
    String getMedicoSolicitante();
    void setMedicoSolicitante(String m);
    int getCantidadFilas();
    String getCodigo(int fila);
    void cargarDeterminaciones(List<Determinacion> lista);
    void mostrarSugerenciasOS(List<String> sugerencias);
    String getNombrePrueba(int fila);
    double pedirPrecioManual();
    String getResultado(int fila);
    void mostrarSugerenciasMedicos(List<String> sugerencias);
    String getUnidad(int fila);
    String getReferencia(int fila);
    boolean getImprimir(int fila);
    void mostrarMensaje(String mensaje);
    double getPrecio();

}
