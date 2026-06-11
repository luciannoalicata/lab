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

    public void onEditarPaciente() {
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

        pViejo.setDni(vista.getDni());
        pViejo.setNombre(vista.getNombre());
        pViejo.setApellido(vista.getApellido());
        pViejo.setEdad(vista.getEdad());
        pViejo.setDireccion(vista.getDireccion());
        pViejo.setLocalidad(vista.getLocalidad());
        pViejo.setNroAfiliado(vista.getNumAfiliado());
        pViejo.setObraSocial(vista.getObraSocial());
        pViejo.setSexo(vista.getSexo());
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

    // ── LÓGICA DE AUTOCOMPLETADO CORREGIDA ──
    public void onBuscarSugerenciaOS() {
        String texto = vista.getObraSocial();
        
        if (texto == null || texto.trim().isEmpty()) {
            vista.mostrarSugerenciasOS(new ArrayList<>());
            return;
        }
        
        // Usamos tu método existente que devuelve objetos ObraSocial
        ArrayList<ObraSocial> resultados = obraSocialDAO.buscarPorCodigoONombre(texto);
        
        // Convertimos esos objetos a una lista de Strings con el nombre para que la vista los muestre
        List<String> sugerencias = new ArrayList<>();
        for (ObraSocial os : resultados) {
            sugerencias.add(os.getNombre()); // Puedes usar os.getCodigo() + " - " + os.getNombre() si prefieres
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
    /**
 * Valida todos los campos del formulario antes de guardar
 * @return true si todos los campos son válidos, false en caso contrario
 */
private boolean validarCampos() {
    String dni = vista.getDni();
    String nombre = vista.getNombre();
    String apellido = vista.getApellido();
    String edad = vista.getEdad();
    String obraSocial = vista.getObraSocial();
    
    // DNI
    if (!dni.matches("\\d{7,8}")) {
        vista.mostrarMensaje("DNI inválido. Debe contener solo números y tener entre 7 y 8 dígitos.");
        return false;
    }
    
    // Nombre
    if (!nombre.matches("^[a-zA-ZáéíóúñÑÁÉÍÓÚ\\s]+$")) {
        vista.mostrarMensaje("Nombre inválido. No puede contener números.");
        return false;
    }
    
    // Apellido (opcional pero si se ingresa debe ser válido)
    if (apellido != null && !apellido.isEmpty() && !apellido.matches("^[a-zA-ZáéíóúñÑÁÉÍÓÚ\\s]+$")) {
        vista.mostrarMensaje("Apellido inválido. No puede contener números.");
        return false;
    }
    
    // Edad (opcional)
    if (edad != null && !edad.isEmpty()) {
        if (!edad.matches("\\d{1,2}")) {
            vista.mostrarMensaje("Edad inválida. Debe ser un número de hasta 2 dígitos.");
            return false;
        }
        int edadInt = Integer.parseInt(edad);
        if (edadInt <= 0 || edadInt > 100) {
            vista.mostrarMensaje("Edad inválida. Debe estar entre 0 y 99 años.");
            return false;
        }
    }
    
    // Obra Social
//    if (obraSocial != null && !obraSocial.isEmpty()) {
//        List<String> obrasValidas = obtenerListaObrasSocialesValidas();
//        if (!obrasValidas.contains(obraSocial) && !obraSocial.equals("PARTICULAR")) {
//            vista.mostrarMensaje("Obra Social inválida. Seleccione una de las opciones sugeridas.");
//            return false;
//        }
//    }
    
    return true;
}
}