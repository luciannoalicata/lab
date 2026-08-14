package vista.interfaces;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import modelo.ResultadoAnalisis;
import presentador.DetalleAnalisisPresenter;

// @author lucianoalicata

public interface IVistaVerDetalleAnalisis {
    
    String BTN_IMPRIMIR = "imprimir";
    String BTN_CERRAR = "cerrar_detalle";
    String BTN_EDITAR = "editar_analisis";
    
    void ejecutar();
    void setPresenter(DetalleAnalisisPresenter presenter);    
    int confirmarAccion(String mensaje, String titulo);
    void limpiarFocos();
    void setNombrePaciente(String nombre);
    void mostrarMensaje(String mensaje); 
    void setFechaAnalisis(String fecha);
    void cargarResultadosDetalle(ArrayList<ResultadoAnalisis> lista);
    int getCantidadFilas() ;
    int getIdResultado(int fila);
    void setMedicoSolicitante(String medico);
    String getMedicoSolicitante();
    javax.swing.JTable getGrilla();
    String getResultadoEditado(int fila);
    void mostrarSugerenciasMedicos(List<String> sugerencias);
    void detenerEdicionTabla();
    void habilitarBotonGuardar(boolean b);
    void habilitarBotonEliminar(boolean b);
    void habilitarBotonImprimir(boolean b);
    void bloquearEdicionTabla();
    void bloquearMedicoSolicitante();
    void setIdAnalisis(int id);int getIdAnalisis();
    void setFechaInforme(java.util.Date fecha);
    Date getFechaSeleccionada();
}