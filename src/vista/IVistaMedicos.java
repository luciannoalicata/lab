package vista;

import java.util.ArrayList;
import modelo.Medico;
/**
 *
 * @author luciano
 */
public interface IVistaMedicos {
    
    String BTN_GUARDAR_MEDICO = "guardar_medico";
    String BTN_ELIMINAR_MEDICO="eliminar_medico";
    String BTN_VOLVER = "salir_medicos";
    
    void ejecutar();
    void setControlador(java.awt.event.ActionListener presentador);    
    String getApellidoMedico();
    String getNombreMedico();
    String getMatriculaMedico();
    String getEspecialidad();
    String getObservacionesMedico();
    String getTextoBusqueda();
    void habilitarBotonGuardar(boolean b);
    void habilitarBotonEliminar(boolean b);
    void cargarMedicosEnTabla(ArrayList<Medico> pacientes);
    Medico getMedicoSeleccionado();
    void cargarDatosMedico(Medico m);
    void limpiarCampos();
    void mostrarMensaje(String mensaje); 
    int confirmarAccion(String mensaje, String titulo);
    
}
