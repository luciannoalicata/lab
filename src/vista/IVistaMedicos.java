package vista;

import presentador.Controlador;
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
    
    public void ejecutar();
    public void setControlador(java.awt.event.ActionListener presentador);    
    public String getApellidoMedico();
    public String getNombreMedico();
    public String getMatriculaMedico();
    public String getEspecialidad();
    public String getObservacionesMedico();
    public String getTextoBusqueda();

    public void habilitarBotonGuardar(boolean b);
    public void habilitarBotonEliminar(boolean b);
    
    public void cargarMedicosEnTabla(ArrayList<Medico> pacientes);
    public Medico getMedicoSeleccionado();
    public void cargarDatosMedico(Medico m);
    
    public void limpiarCampos();
    public void mostrarMensaje(String mensaje); 
    public int confirmarAccion(String mensaje, String titulo);
}
