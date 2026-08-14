package vista.interfaces;

// @author lucianoalicata

import java.util.ArrayList;
import java.util.List;
import modelo.Paciente;
import presentador.PacientePresenter;

public interface IVistaPaciente {
  
    String BTN_GUARDAR_PACIENTE = "guardar_paciente";
    String BTN_EDITAR_PACIENTE = "editar_paciente";
    String BTN_BUSCAR_PACIENTE = "buscar_paciente";
    String BTN_CARGAR_RESULTADOS ="cargar_resultados";
    String BTN_VER_HISTORIAL ="ver_historial";
    String BTN_VOLVER_VPAC = "volver";
    
    void ejecutar();
    void setPresenter(PacientePresenter presenter);    
    String getDni();
    String getNombre();
    String getApellido();
    String getSexo();
    String getEdad();
    String getDireccion();
    String getLocalidad();
    String getNumAfiliado();
    String getObraSocial();
    String getCelular();
    String getTextoBusqueda();
    Paciente getPacienteSeleccionado(); 
    int getPacienteSeleccionadoId();
    void habilitarBotonGuardar(boolean b);
    void habilitarBotonEditar(boolean b);
    void habilitarBotonCargarResultados(boolean b);
    void habilitarBotonNuevoAnalisis(boolean b);
    void mostrarSugerenciasOS(List<String> sugerencias);
    void cargarPacientesEnTabla(ArrayList<Paciente> pacientes);
    void cargarDatosPaciente(Paciente p);
    void limpiarCampos();
    void mostrarMensaje(String mensaje); 
    int confirmarAccion(String mensaje, String titulo);
    void limpiarFocos();
}