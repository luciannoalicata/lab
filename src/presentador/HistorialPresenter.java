package presentador;

import dao.AnalisisDAO;
import dao.AuditoriaDAO;
import modelo.Analisis;
import modelo.Paciente;
import modelo.Usuario;
import presentador.router.AppRouter;
import vista.interfaces.IVistaHistorialAnalisis;
import java.util.ArrayList;
import java.util.Date;
import servicio.ReporteService;

public class HistorialPresenter {

    private final IVistaHistorialAnalisis vista;
    private final AppRouter router;
    private final AnalisisDAO analisisDAO;
    private final AuditoriaDAO auditoriaDAO;
    private final Usuario usuarioLogueado;
    private ReporteService reporteService;
    private final Paciente pacienteActual; 

    public HistorialPresenter(IVistaHistorialAnalisis vista, AppRouter router, AnalisisDAO analisisDAO, 
                              AuditoriaDAO auditoriaDAO, Usuario usuarioLogueado, Paciente pacienteActual, ReporteService reporteService) {
        this.vista = vista;
        this.router = router;
        this.analisisDAO = analisisDAO;
        this.auditoriaDAO = auditoriaDAO;
        this.usuarioLogueado = usuarioLogueado;
        this.pacienteActual = pacienteActual;
        this.reporteService = reporteService;
    }

    public void iniciar() {
        vista.setPresenter(this); 
        
        // 1. Configuramos el título con el nombre del paciente
        String nombreCompleto = pacienteActual.getApellido() + " " + pacienteActual.getNombre();
        vista.setNombrePaciente(nombreCompleto);

        // 2. Permisos LECTOR
        if (usuarioLogueado != null && "LECTOR".equals(usuarioLogueado.getRol())) {
            vista.habilitarBotonVerDetalles(true);
            vista.habilitarBotonImprimir(true);
        }

        // 3. Cargamos los datos
        cargarTabla();
    }
    
    private void cargarTabla() {
        // Usamos getIdPaciente(), no el DNI
        ArrayList<Analisis> lista = analisisDAO.listarPorPaciente(pacienteActual.getIdPaciente());
        vista.cargarHistorial(lista);
        
        // Autoseleccionar la fecha más reciente si hay historial
        if (lista != null && !lista.isEmpty()) {
            vista.setFechaSeleccionada(lista.get(0).getFecha());
        }
    }

    // ── MÉTODOS DE LOS BOTONES ──
    public void onGenerarInforme() {
        int idAnalisis = vista.getAnalisisSeleccionadoId();
        Date fechaImpresion = vista.getFechaSeleccionada();

        if (idAnalisis != -1) {
            // ¡Llamada limpia, solo datos de negocio!
            reporteService.generarInforme(idAnalisis, fechaImpresion);

            if (analisisDAO.cambiarEstadoGenerado(idAnalisis)) {
                
                auditoriaDAO.registrar(this.usuarioLogueado, "IMPRIMIR", "analisis", idAnalisis, 
                        null, "Informe generado", "Impresión desde Historial");
                
                cargarTabla();
            }
        } else {
            vista.mostrarMensaje("Debe seleccionar un análisis.");
        }
    }

    public void onVerDetalles() {
        int idAnalisis = vista.getAnalisisSeleccionadoId();
        if (idAnalisis != -1) {
            router.abrirDetalleAnalisis(idAnalisis, "HISTORIAL");
        } else {
            vista.mostrarMensaje("Debe seleccionar un análisis para ver sus detalles.");
        }
    }

    public void onVolver() {
        router.volverAPacientesDesdeHistorial();
    }

    public void onSeleccionarAnalisis() {
        // Tu VistaHistorialAnalisis ya habilita/deshabilita los botones internamente 
        // cuando detecta un clic, así que aquí no hace falta agregar más código.
    }
}