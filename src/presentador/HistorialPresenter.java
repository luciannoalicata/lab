package presentador;

// @author lucianoalicata

import dao.AnalisisDAO;
import dao.AuditoriaDAO;
import modelo.Analisis;
import modelo.Paciente;
import modelo.Usuario;
import presentador.router.AppRouter;
import vista.interfaces.IVistaHistorialAnalisis;
import java.util.ArrayList;
import servicio.ReporteService;

public class HistorialPresenter {

    private final IVistaHistorialAnalisis vista;
    private final AppRouter router;
    private final AnalisisDAO analisisDAO;
    private final AuditoriaDAO auditoriaDAO;
    private final Usuario usuarioLogueado;
    private final ReporteService reporteService;
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
        
        String nombreCompleto = pacienteActual.getApellido() + " " + pacienteActual.getNombre();
        vista.setNombrePaciente(nombreCompleto);

        if (usuarioLogueado != null && "LECTOR".equals(usuarioLogueado.getRol())) {
            vista.habilitarBotonVerDetalles(true);
            vista.habilitarBotonImprimir(true);
        }
        cargarTabla();
    }
    
    private void cargarTabla() {
        ArrayList<Analisis> lista = analisisDAO.listarPorPaciente(pacienteActual.getIdPaciente());
        vista.cargarHistorial(lista);
        
        if (lista != null && !lista.isEmpty()) {
            vista.setFechaSeleccionada(lista.get(0).getFecha());
        }
    }

    public void onGenerarInforme() {
        int idAnalisis = vista.getAnalisisSeleccionadoId();
        java.util.Date fechaImpresion = vista.getFechaSeleccionada();

        if (idAnalisis != -1) {
            
            new Thread(() -> {
                try {
                    reporteService.generarInforme(idAnalisis, fechaImpresion);

                    if (analisisDAO.cambiarEstadoGenerado(idAnalisis)) {
                        auditoriaDAO.registrar(this.usuarioLogueado, "IMPRIMIR", "analisis", idAnalisis, 
                                null, "Informe generado", "Impresión desde Historial");
                        
                        javax.swing.SwingUtilities.invokeLater(() -> {
                            cargarTabla();
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

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
    }
}