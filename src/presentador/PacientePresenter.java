package presentador;

import dao.PacienteDAO;
import dao.AuditoriaDAO;
import dao.ObraSocialDAO; 
import modelo.Paciente;
import modelo.ObraSocial;
import modelo.Usuario;
import presentador.router.AppRouter;
import vista.interfaces.IVistaPaciente;
import java.util.ArrayList;
import java.util.List;

public class PacientePresenter {

    private final IVistaPaciente vista;
    private final AppRouter router;
    private final PacienteDAO pacienteDAO;
    private final AuditoriaDAO auditoriaDAO;
    private final ObraSocialDAO obraSocialDAO; 
    private final Usuario usuarioLogueado;
    private Paciente pacienteSeleccionadoCompleto; 

    public PacientePresenter(IVistaPaciente vista, AppRouter router, PacienteDAO pacienteDAO, 
                             AuditoriaDAO auditoriaDAO, ObraSocialDAO obraSocialDAO, Usuario usuarioLogueado) {
        this.vista = vista;
        this.router = router;
        this.pacienteDAO = pacienteDAO;
        this.auditoriaDAO = auditoriaDAO;
        this.obraSocialDAO = obraSocialDAO; 
        this.usuarioLogueado = usuarioLogueado;
    }

    public void iniciar() {
        vista.setPresenter(this);
        vista.limpiarCampos();
        cargarTabla();
    }

    private void cargarTabla() {
        ArrayList<Paciente> lista = pacienteDAO.listarPacientes();
        vista.cargarPacientesEnTabla(lista);
    }

    public void onGuardarPaciente() {
        if (!validarCampos()) {
            return;
        }

        String dni = vista.getDni();

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

    private boolean validarCampos() {
        String apellido = vista.getApellido().trim();
        String nombre = vista.getNombre().trim();
        String dni = vista.getDni().trim();

        if (apellido.isEmpty() || nombre.isEmpty() || dni.isEmpty()) {
            vista.mostrarMensaje("Los campos Apellido, Nombre y DNI son obligatorios.");
            return false;
        }

        if (!dni.matches("\\d{7,8}")) { 
            vista.mostrarMensaje("El DNI es inválido. Debe contener entre 7 y 8 dígitos numéricos.");
            return false;
        }

        String edad = vista.getEdad().trim();
        if (!edad.isEmpty() && !edad.matches("\\d+")) {
            vista.mostrarMensaje("La edad debe ser un número válido.");
            return false;
        }

        return true;
    }

    public void onEditarPaciente() {
        modelo.Paciente seleccionado = vista.getPacienteSeleccionado();
        if (seleccionado == null) {
            vista.mostrarMensaje("Debe seleccionar un paciente");
            return;
        }

        String sexoSeleccionado = vista.getSexo();
        if (sexoSeleccionado == null || sexoSeleccionado.trim().isEmpty() || sexoSeleccionado.equals("Seleccione...")) {
            vista.mostrarMensaje("Por favor, seleccione el sexo del paciente (M, F o X).");
            return;
        }
        
        if (sexoSeleccionado.length() > 1) {
            sexoSeleccionado = sexoSeleccionado.substring(0, 1).toUpperCase();
        }

        modelo.Paciente pViejo = pacienteDAO.buscarPorId(seleccionado.getIdPaciente());
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

        pViejo.setDni(vista.getDni());
        pViejo.setNombre(vista.getNombre());
        pViejo.setApellido(vista.getApellido());
        pViejo.setEdad(vista.getEdad());
        pViejo.setDireccion(vista.getDireccion());
        pViejo.setLocalidad(vista.getLocalidad());
        pViejo.setNroAfiliado(vista.getNumAfiliado());
        pViejo.setObraSocial(vista.getObraSocial());
        pViejo.setSexo(sexoSeleccionado); 
        pViejo.setCelular(vista.getCelular());

        if (pacienteDAO.actualizar(pViejo)) {  
            if (cambiosViejos.length() > 0) {
                auditoriaDAO.registrar(usuarioLogueado, "EDITAR", "paciente",
                        pViejo.getIdPaciente(), cambiosViejos.toString(),
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
        if (pacienteSeleccionadoCompleto == null) {
            vista.mostrarMensaje("Seleccione un paciente primero.");
            return;
        }
        router.irANuevoAnalisis(pacienteSeleccionadoCompleto);
    }

    public void onVerHistorial() {
        if (pacienteSeleccionadoCompleto == null) {
            vista.mostrarMensaje("Seleccione un paciente primero.");
            return;
        }
        router.irAHistorial(pacienteSeleccionadoCompleto);
    }

    public void onBuscarSugerenciaOS() {
        String texto = vista.getObraSocial();
        
        if (texto == null || texto.trim().isEmpty()) {
            vista.mostrarSugerenciasOS(new ArrayList<>());
            return;
        }
        
        ArrayList<ObraSocial> resultados = obraSocialDAO.buscarPorCodigoONombre(texto);
        
        List<String> sugerencias = new ArrayList<>();
        for (ObraSocial os : resultados) {
            sugerencias.add(os.getNombre()); 
        }
        
        vista.mostrarSugerenciasOS(sugerencias);
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
        int id = vista.getPacienteSeleccionadoId();  
        if (id != -1) {
            pacienteSeleccionadoCompleto = pacienteDAO.buscarPorId(id);
            if (pacienteSeleccionadoCompleto != null) {
                vista.cargarDatosPaciente(pacienteSeleccionadoCompleto);
            }
        }
    }

    public Paciente getPacienteSeleccionadoCompleto() {
        return pacienteSeleccionadoCompleto;
    }
}