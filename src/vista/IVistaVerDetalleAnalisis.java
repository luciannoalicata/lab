package vista;

import presentador.Controlador;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import modelo.ResultadoAnalisis;

/**
 *
 * @author luciano
 */
public interface IVistaVerDetalleAnalisis {
    
    String BTN_IMPRIMIR = "imprimir";
    String BTN_CERRAR = "cerrar_detalle";
    String BTN_EDITAR = "editar_analisis";
    
    public void ejecutar();
    public void setControlador(Controlador control);
    
    public void setNombrePaciente(String nombre);
    public void mostrarMensaje(String mensaje); 
    public void setFechaAnalisis(String fecha);
    public void cargarResultadosDetalle(ArrayList<ResultadoAnalisis> lista);
    public int getCantidadFilas() ;
    public int getIdResultado(int fila);
    public void setMedicoSolicitante(String medico);
    public String getMedicoSolicitante();
    public javax.swing.JTable getGrilla();
    public String getResultadoEditado(int fila);
    public void mostrarSugerenciasMedicos(List<String> sugerencias);
    void detenerEdicionTabla();
    void habilitarBotonGuardar(boolean b);
    void habilitarBotonEliminar(boolean b);
    public void habilitarBotonImprimir(boolean b);
    public void bloquearEdicionTabla();
    void bloquearMedicoSolicitante();
    void setIdAnalisis(int id);
int getIdAnalisis();
void setFechaInforme(java.util.Date fecha);
    public Date getFechaSeleccionada();
}
