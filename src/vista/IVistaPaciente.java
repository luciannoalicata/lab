package vista;
/**
 *
 * @author luciano
 */
import presentador.Controlador;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import modelo.Paciente;

public interface IVistaPaciente {
  
    String BTN_GUARDAR_PACIENTE = "guardar_paciente";
    String BTN_EDITAR_PACIENTE = "editar_paciente";
    String BTN_BUSCAR_PACIENTE = "buscar_paciente";
    String BTN_CARGAR_RESULTADOS ="cargar_resultados";
    String BTN_VER_HISTORIAL ="ver_historial";
    String BTN_VOLVER_VPAC = "volver";
    
    public void ejecutar();

    public void setControlador(Controlador control);

    public String getDni();
    public String getNombre();
    public String getApellido();
    public String getSexo();
    public String getEdad();
    public String getDireccion();
    public String getLocalidad();
    public String getNumAfiliado();
    public String getObraSocial();
    public String getCelular();
    public String getTextoBusqueda();
    void habilitarBotonGuardar(boolean b);
    void habilitarBotonEditar(boolean b);
    void habilitarBotonCargarResultados(boolean b);
    public void habilitarBotonNuevoAnalisis(boolean b);
    public void mostrarSugerenciasOS(List<String> sugerencias);
    
    public void cargarPacientesEnTabla(ArrayList<Paciente> pacientes);
    public Paciente getPacienteSeleccionado();
    public void cargarDatosPaciente(Paciente p);
    public void limpiarCampos();
    public void mostrarMensaje(String mensaje); 
}
