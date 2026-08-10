package presentador;

// @author lucianoalicata

import dao.MedicoDAO;
import modelo.Medico;
import presentador.router.AppRouter;
import vista.interfaces.IVistaMedicos;
import java.util.ArrayList;

public class MedicoPresenter {

    private final IVistaMedicos vista;
    private final AppRouter router;
    private final MedicoDAO medicoDAO;
    private Medico medicoSeleccionado;
    private boolean actualizandoVista = false; 
    
    public MedicoPresenter(IVistaMedicos vista, AppRouter router, MedicoDAO medicoDAO) {
        this.vista = vista;
        this.router = router;
        this.medicoDAO = medicoDAO;
    }

    public void iniciar() {
        vista.setPresenter(this); 
        cargarTabla();
        medicoSeleccionado = null;
    }

    private void cargarTabla() {
        actualizandoVista = true; 
        ArrayList<Medico> lista = medicoDAO.listarMedicos();
        vista.cargarMedicosEnTabla(lista);
        actualizandoVista = false; 
    }
    
    public void onGuardarMedico() {
        String nombre = vista.getNombreMedico();
        String apellido = vista.getApellidoMedico();
        String matricula = vista.getMatriculaMedico();
        String especialidad = vista.getEspecialidad();
        String observaciones = vista.getObservacionesMedico();

        if (apellido.isEmpty() || nombre.isEmpty() || matricula.isEmpty()) {
            vista.mostrarMensaje("Error: Apellido, Nombre y Matrícula son campos obligatorios.");
            return;
        }

        if (!matricula.matches("^[a-zA-Z0-9]+$")) {
            vista.mostrarMensaje("Error: La matrícula solo puede contener letras y números.");
            return;
        }

        if (!nombre.matches("^[a-zA-ZáéíóúñÑÁÉÍÓÚ\\s]+$")) {
            vista.mostrarMensaje("Error: El nombre no puede contener números.");
            return;
        }
        
        if (!apellido.matches("^[a-zA-ZáéíóúñÑÁÉÍÓÚ\\s]+$")) {
            vista.mostrarMensaje("Error: El apellido no puede contener números.");
            return;
        }

        if (medicoSeleccionado != null) {
            if (!medicoSeleccionado.getMatricula().equals(matricula)) {
                if (medicoDAO.existeMatricula(matricula)) {
                    vista.mostrarMensaje("Error: Ya existe un médico con la matrícula '" + matricula + "'.");
                    return;
                }
            }
            
            medicoSeleccionado.setNombreMedico(nombre);
            medicoSeleccionado.setApellidoMedico(apellido);
            medicoSeleccionado.setMatricula(matricula);
            medicoSeleccionado.setEspecialidad(especialidad);
            medicoSeleccionado.setObservaciones(observaciones);
            
            if (medicoDAO.actualizarMedico(medicoSeleccionado)) {
                vista.mostrarMensaje("Médico actualizado correctamente.");
                limpiarYRecargar(); 
            } else {
                vista.mostrarMensaje("Error: No se pudo actualizar el médico.");
            }
        }
        else {
            if (medicoDAO.existeMatricula(matricula)) {
                vista.mostrarMensaje("Error: Ya existe un médico con la matrícula '" + matricula + "'.");
                return;
            }
            
            Medico m = new Medico();
            m.setApellidoMedico(apellido);
            m.setNombreMedico(nombre);
            m.setMatricula(matricula);
            m.setEspecialidad(especialidad);
            m.setObservaciones(observaciones);

            if (medicoDAO.guardarMedico(m)) {
                vista.mostrarMensaje("Médico guardado correctamente.");
                limpiarYRecargar(); 
            } else {
                vista.mostrarMensaje("Error: No se pudo guardar el médico.");
            }
        }
    }
    
    private void limpiarYRecargar() {
        actualizandoVista = true;
        medicoSeleccionado = null;
        vista.limpiarCampos();
        cargarTabla();
        actualizandoVista = false;
    }

    public void onEliminarMedico() {
        Medico seleccionado = vista.getMedicoSeleccionado();

        if (seleccionado == null) {
            vista.mostrarMensaje("Por favor, seleccione un médico de la tabla.");
            return;
        }
        
        int respuesta = vista.confirmarAccion(
                "¿Está seguro que desea eliminar al Dr/a. " + seleccionado.getApellidoMedico() + " " + seleccionado.getNombreMedico() + "?", 
                "Confirmar Eliminación"
        );

        if (respuesta == 0) {
            if (medicoDAO.eliminarMedico(seleccionado.getMatricula())) {
                vista.mostrarMensaje("Médico eliminado con éxito.");
                limpiarYRecargar(); 
            } else {
                vista.mostrarMensaje("Error: No se pudo eliminar el médico seleccionado.");
            }
        }
    }
    
    public void onBuscarMedico() {
        String filtro = vista.getTextoBusqueda();
        if (filtro == null || filtro.trim().isEmpty()) {
            cargarTabla();
        } else {
            actualizandoVista = true;
            vista.cargarMedicosEnTabla(medicoDAO.buscarMedicoInteligente(filtro));
            actualizandoVista = false;
        }
    }

    public void onSeleccionarMedico() {
        if (actualizandoVista) {
            return;
        }
        
        Medico m = vista.getMedicoSeleccionado();
        if (m != null) {
            Medico completo = medicoDAO.buscarPorMatricula(m.getMatricula());
            if (completo != null) {
                medicoSeleccionado = completo;
                actualizandoVista = true;
                vista.cargarDatosMedico(completo);
                actualizandoVista = false;
            }
        } else {
            medicoSeleccionado = null;
        }
    }
    
    public void onVolver(){
        router.irAInicio();
    }
}