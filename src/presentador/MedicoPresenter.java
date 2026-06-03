package presentador;

import dao.MedicoDAO;
import modelo.Medico;
import vista.IVistaMedicos;
import vista.IVistaPrincipal;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class MedicoPresenter implements ActionListener {

    private IVistaMedicos vm;
    private IVistaPrincipal vp;
    private MedicoDAO medicoDAO;

    // Inyectamos las dependencias necesarias
    public MedicoPresenter(IVistaMedicos vm, IVistaPrincipal vp, MedicoDAO medicoDAO) {
        this.vm = vm;
        this.vp = vp;
        this.medicoDAO = medicoDAO;
        
        // El presentador se "conecta" a la vista
        this.vm.setControlador(this); 
    }

    // Este método reemplaza la inicialización que tenías en el Controlador Dios
    public void iniciar() {
        vm.limpiarCampos();
        cargarMedicosEnTabla();
        vp.activarModoInmersion();
        vp.mostrarSeccion("medicos");
    }

    private void cargarMedicosEnTabla() {
        // Llama a tu método del DAO que lista los médicos
        ArrayList<Medico> lista = medicoDAO.listarMedicos(); 
        vm.cargarMedicosEnTabla(lista);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();

        switch (comando) {
            case IVistaMedicos.BTN_GUARDAR_MEDICO:
                guardarMedico();
                break;
            case IVistaMedicos.BTN_ELIMINAR_MEDICO:
                eliminarMedico();
                break;
            case IVistaMedicos.BTN_VOLVER:
                vp.desactivarModoInmersion();
                vp.volverInicio();
                // vp.limpiarFocoYPantalla(); 
                break;
                
            // ── AQUÍ ESTÁN LOS QUE FALTABAN ──
            case "BUSCAR_MEDICO":
                actualizarBusquedaMedicos(); // ¡Ahora sí se usa cada vez que tipean!
                break;
            case "SELECCIONAR_MEDICO":
                medicoSeleccionado();        // ¡Ahora sí se usa al hacer clic en la tabla!
                break;
        }
    }

    private void guardarMedico() {
        String apellido = vm.getApellidoMedico();
        String nombre = vm.getNombreMedico();
        String matricula = vm.getMatriculaMedico();
        String especialidad = vm.getEspecialidad();
        String observaciones = vm.getObservacionesMedico();

        if (apellido.isEmpty() || nombre.isEmpty() || matricula.isEmpty()) {
            vm.mostrarMensaje("Error: Apellido, Nombre y Matrícula son campos obligatorios.");
            return;
        }

        if (medicoDAO.existeMatricula(matricula)) {
            vm.mostrarMensaje("Error: Ya existe un médico con la matrícula " + matricula);
            return; 
        }

        Medico m = new Medico();
        m.setApellidoMedico(apellido);
        m.setNombreMedico(nombre);
        m.setMatricula(matricula);
        m.setEspecialidad(especialidad);
        m.setObservaciones(observaciones);

        if (medicoDAO.guardarMedico(m)) {
            vm.mostrarMensaje("Médico guardado correctamente.");
            vm.limpiarCampos();
            cargarMedicosEnTabla(); 
        } else {
            vm.mostrarMensaje("Error: No se pudo guardar el médico.");
        }
    }

    private void eliminarMedico() {
        Medico seleccionado = vm.getMedicoSeleccionado();

        if (seleccionado == null) {
            vm.mostrarMensaje("Por favor, seleccione un médico de la tabla.");
            return;
        }

        // ¡Magia! El presentador ya no sabe de JOptionPanes, solo recibe un int (0 = YES)
        int respuesta = vm.confirmarAccion(
                "¿Está seguro que desea eliminar al Dr/a. " + seleccionado.getApellidoMedico() + "?", 
                "Confirmar Eliminación"
        );

        if (respuesta == 0) { // 0 suele ser JOptionPane.YES_OPTION
            if (medicoDAO.eliminarMedico(seleccionado.getMatricula())) {
                vm.mostrarMensaje("Médico eliminado con éxito.");
                cargarMedicosEnTabla(); 
                vm.limpiarCampos();
            } else {
                vm.mostrarMensaje("Error: No se pudo eliminar el médico seleccionado.");
            }
        }
    }
    
    public void medicoSeleccionado() {
        Medico m = vm.getMedicoSeleccionado();
        if (m == null) {
            return;
        }

        // Usamos el nuevo método que devuelve UN solo médico
        Medico completo = medicoDAO.buscarPorMatricula(m.getMatricula());

        if (completo != null) {
            vm.cargarDatosMedico(completo);
            // Tip: podrías deshabilitar la edición de la matrícula aquí si no quieres que la cambien
        }
    }
    
    public void actualizarBusquedaMedicos() {
        String texto = vm.getTextoBusqueda();
        // Llamamos al DAO usando el nuevo filtro que incluye matrícula
        ArrayList<Medico> filtrados = medicoDAO.buscarMedicoInteligente(texto);
        vm.cargarMedicosEnTabla(filtrados);
    }
}