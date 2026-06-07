package presentador;

import dao.PacienteDAO;
import modelo.Paciente;
import presentador.router.AppRouter;
import vista.interfaces.IVistaPaciente;
import java.util.ArrayList;

public class PacientePresenter {

    private final IVistaPaciente vista;
    private final AppRouter router;
    private final PacienteDAO pacienteDAO;
    private final dao.AuditoriaDAO auditoriaDAO;
    private final modelo.Usuario usuarioLogueado;

    public PacientePresenter(IVistaPaciente vista, AppRouter router, PacienteDAO pacienteDAO, 
                             dao.AuditoriaDAO auditoriaDAO, modelo.Usuario usuarioLogueado) {
        this.vista = vista;
        this.router = router;
        this.pacienteDAO = pacienteDAO;
        this.auditoriaDAO = auditoriaDAO;
        this.usuarioLogueado = usuarioLogueado;
    }

    public void iniciar() {
        vista.setPresenter(this); 
        cargarTabla();
    }
    
    private void cargarTabla() {
        ArrayList<Paciente> lista = pacienteDAO.listarPacientes();
        vista.cargarPacientesEnTabla(lista);
    }

    public void onGuardarPaciente() {
        String dni = vista.getDni();
        if (dni.isEmpty() || vista.getNombre().isEmpty()) {
            vista.mostrarMensaje("DNI y Nombre son obligatorios");
            return;
        }

        if (pacienteDAO.existeDNI(dni)) {
            vista.mostrarMensaje("El paciente con DNI " + dni + " ya se encuentra registrado.");
            return;
        }

        Paciente p = new Paciente();
        p.setDni(dni);
        p.setNombre(vista.getNombre());
        p.setApellido(vista.getApellido());
        p.setEdad(vista.getEdad());
        p.setDireccion(vista.getDireccion());
        p.setLocalidad(vista.getLocalidad());
        p.setNroAfiliado(vista.getNumAfiliado());
        p.setObraSocial(vista.getObraSocial());
        p.setSexo(vista.getSexo());
        p.setCelular(vista.getCelular());

        if (pacienteDAO.guardarPaciente(p)) {
            auditoriaDAO.registrar(usuarioLogueado, "CREAR", "paciente", 0, null,
                    "DNI: " + p.getDni(), "Se registró un nuevo paciente: " + p.getApellido() + " " + p.getNombre());
            vista.mostrarMensaje("Paciente guardado correctamente");
            vista.limpiarCampos();
        } else {
            vista.mostrarMensaje("Error técnico al intentar guardar el paciente");
        }
        cargarTabla();
    }
    
    public void onEditarPaciente(){
        Paciente seleccionado = vista.getPacienteSeleccionado();
        if (seleccionado == null) {
            vista.mostrarMensaje("Debe seleccionar un paciente");
            return;
        }

        Paciente pViejo = pacienteDAO.buscarPorId(seleccionado.getIdPaciente());
        if (pViejo == null) {
            vista.mostrarMensaje("Error: El paciente ya no existe en la base de datos.");
            cargarTabla();
            return;
        }

        StringBuilder cambiosViejos = new StringBuilder();
        StringBuilder cambiosNuevos = new StringBuilder();
        
        if (!pViejo.getDni().equals(vista.getDni())) {
            cambiosViejos.append("DNI: ").append(pViejo.getDni()).append(" | ");
            cambiosNuevos.append("DNI: ").append(vista.getDni()).append(" | ");
        }
        if (!pViejo.getApellido().equalsIgnoreCase(vista.getApellido())) {
            cambiosViejos.append("Apel: ").append(pViejo.getApellido()).append(" | ");
            cambiosNuevos.append("Apel: ").append(vista.getApellido()).append(" | ");
        }

        seleccionado.setVersion(pViejo.getVersion());
        seleccionado.setDni(vista.getDni());
        seleccionado.setNombre(vista.getNombre());
        seleccionado.setApellido(vista.getApellido());
        seleccionado.setEdad(vista.getEdad());
        seleccionado.setDireccion(vista.getDireccion());
        seleccionado.setLocalidad(vista.getLocalidad());
        seleccionado.setNroAfiliado(vista.getNumAfiliado());
        seleccionado.setObraSocial(vista.getObraSocial());
        seleccionado.setSexo(vista.getSexo());
        seleccionado.setCelular(vista.getCelular());

        if (pacienteDAO.actualizar(seleccionado)) {
            if (cambiosViejos.length() > 0) {
                auditoriaDAO.registrar(usuarioLogueado, "EDITAR", "paciente",
                        seleccionado.getIdPaciente(), cambiosViejos.toString(),
                        cambiosNuevos.toString(), "Edición exitosa");
            }
            vista.mostrarMensaje("Paciente actualizado correctamente");
            vista.limpiarCampos();
        } else {
            vista.mostrarMensaje("CONCURRENCIA: Otro usuario modificó este paciente mientras usted lo editaba.\nLos datos se refrescarán. Por favor, intente de nuevo.");
        }
        cargarTabla();
    }

    public void onCargarResultados() {
        Paciente p = vista.getPacienteSeleccionado();
        if (p == null) {
            vista.mostrarMensaje("Seleccione un paciente primero.");
            return;
        }
        router.irANuevoAnalisis(p); 
    }

    public void onVerHistorial() {
        Paciente p = vista.getPacienteSeleccionado();
        if (p == null) {
            vista.mostrarMensaje("Seleccione un paciente primero.");
            return;
        }
        // Le pasamos la pelota al Router
        router.irAHistorial(p);
    }

    public void onBuscarSugerenciaOS() {
        // Asumiendo que agregas un ObraSocialDAO a este presentador, o delegas
        // Si no tienes el ObraSocialDAO aquí, se lo pasas en el constructor igual que la AuditoriaDAO.
        String texto = vista.getObraSocial(); // o el nombre de tu getter
        // logica de buscar sugerencias...
    }

    public void onVolver() {
        router.irAInicio();
    }

    public void onBuscarPaciente() {
        String texto = vista.getTextoBusqueda();
        if (texto.isEmpty()) {
            cargarTabla();
            return;
        }
        ArrayList<Paciente> lista = pacienteDAO.buscarPorDniOApellidoONombre(texto);
        vista.cargarPacientesEnTabla(lista);
    }

    public void onSeleccionarPaciente() {
        Paciente p = vista.getPacienteSeleccionado();
        if (p == null) return;
        
        Paciente completo = pacienteDAO.buscarPorId(p.getIdPaciente());
        if (completo != null) {
            vista.cargarDatosPaciente(completo);
        }
    }
}
