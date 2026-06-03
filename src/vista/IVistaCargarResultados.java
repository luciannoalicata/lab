package vista;

import presentador.Controlador;
import java.util.List;
import modelo.Determinacion;

/**
 *
 * @author luciano
 */
public interface IVistaCargarResultados {
    
    String BTN_GUARDAR_RESULTADOS = "guardar_resultados";
    String BTN_CERRAR = "cerrar";

    public void ejecutar();

    public void setControlador(Controlador control);

    public void setNombrePaciente(String nombre);

    public void setObraSocial(String obraSocial);

    public String getObraSocial();

    public void detenerEdicionTabla();

    public String getMedicoSolicitante();

    void setMedicoSolicitante(String m);

    public int getCantidadFilas();

    public String getCodigo(int fila);

    public void cargarDeterminaciones(List<Determinacion> lista);

    public void mostrarSugerenciasOS(List<String> sugerencias);

    public String getNombrePrueba(int fila);

    double pedirPrecioManual();

    public String getResultado(int fila);

    void mostrarSugerenciasMedicos(List<String> sugerencias);

    public String getUnidad(int fila);

    public String getReferencia(int fila);

    public boolean getImprimir(int fila);

    public void mostrarMensaje(String mensaje);

    public double getPrecio();

}
