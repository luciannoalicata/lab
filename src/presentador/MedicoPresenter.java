package presentador;

import dao.MedicoDAO;
import modelo.Medico;
import presentador.router.AppRouter;
import vista.interfaces.IVistaMedicos;
import java.util.ArrayList;

public class MedicoPresenter {

    private final IVistaMedicos vista;
    private final AppRouter router;
    private final MedicoDAO medicoDAO;

    public MedicoPresenter(IVistaMedicos vista, AppRouter router, MedicoDAO medicoDAO) {
        this.vista = vista;
        this.router = router;
        this.medicoDAO = medicoDAO;
    }

    public void iniciar() {
        vista.setPresenter(this); 
        cargarTabla();
    }

    private void cargarTabla() {
        ArrayList<Medico> lista = medicoDAO.listarMedicos();
        vista.cargarMedicosEnTabla(lista);
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

        Medico m = new Medico();
        m.setApellidoMedico(apellido);
        m.setNombreMedico(nombre);
        m.setMatricula(matricula);
        m.setEspecialidad(especialidad);
        m.setObservaciones(observaciones);

        if (medicoDAO.guardarMedico(m)) {
            vista.mostrarMensaje("Médico guardado/actualizado correctamente.");
            vista.limpiarCampos();
            cargarTabla();
        } else {
            vista.mostrarMensaje("Error: No se pudo guardar el médico.");
        }
    }

    public void onEliminarMedico() {
        Medico seleccionado = vista.getMedicoSeleccionado();

        if (seleccionado == null) {
            vista.mostrarMensaje("Por favor, seleccione un médico de la tabla.");
            return;
        }
        
        int respuesta = vista.confirmarAccion(
                "¿Está seguro que desea eliminar al Dr/a. " + seleccionado.getApellidoMedico() + "?", 
                "Confirmar Eliminación"
        );

        if (respuesta == 0) {
            if (medicoDAO.eliminarMedico(seleccionado.getMatricula())) {
                vista.mostrarMensaje("Médico eliminado con éxito.");
                vista.limpiarCampos();
                cargarTabla(); 
            } else {
                vista.mostrarMensaje("Error: No se pudo eliminar el médico seleccionado.");
            }
        }
    }
    
    public void onBuscarMedico() {
        String filtro = vista.getTextoBusqueda();
        vista.cargarMedicosEnTabla(medicoDAO.buscarMedicoInteligente(filtro));
    }

    public void onSeleccionarMedico() {
        Medico m = vista.getMedicoSeleccionado();
        if (m != null) {
            Medico completo = medicoDAO.buscarPorMatricula(m.getMatricula());
            if (completo != null) {
                vista.cargarDatosMedico(completo);
            }
        }
    }

    public void onVolver(){
        router.irAInicio();
    }
}