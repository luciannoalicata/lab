package presentador;

import dao.AnalisisDAO;
import java.util.ArrayList;
import modelo.Analisis;
import presentador.router.AppRouter;
import servicio.ReporteService;
import vista.interfaces.IVistaAnalisis;

// 1. Adiós al implements ActionListener
public class AnalisisPresenter {
    
    private final IVistaAnalisis vla;
    private final AppRouter router; // 2. El router maneja la navegación
    private final AnalisisDAO analisisDAO;
    private final ReporteService reporteService; // 3. El servicio para imprimir PDFs

    // Constructor actualizado
    public AnalisisPresenter(IVistaAnalisis vla, AppRouter router, AnalisisDAO analisisDAO, ReporteService reporteService) {
        this.vla = vla;
        this.router = router;
        this.analisisDAO = analisisDAO;
        this.reporteService = reporteService;
    }

    public void iniciar() {
        vla.setPresenter(this); // Conectamos la vista
        refrescarTabla("");
        // El AppRouter se encarga de mostrar la sección en la pantalla principal
    }

    public void refrescarTabla(String filtro) {
        ArrayList<Analisis> lista = analisisDAO.buscarAnalisisGlobal(filtro);
        vla.cargarAnalisisEnTabla(lista);
    }

    // ════════════════════════════════════════════════════════════════
    //  MÉTODOS EXPLÍCITOS LLAMADOS POR LA VISTA (MVP Puro)
    // ════════════════════════════════════════════════════════════════

    public void onVerDetalles() {
        Analisis sel = vla.getAnalisisSeleccionado();
        if (sel != null) {
            router.abrirDetalleAnalisis(sel.getIdAnalisis());
        } else {
            vla.mostrarMensaje("Seleccione un análisis.");
        }
    }

    public void onImprimirAnalisis() {
        Analisis sel = vla.getAnalisisSeleccionado();
        if (sel != null) {
            // El Presentador imprime usando el servicio limpio (usando la fecha actual)
            reporteService.generarInforme(sel.getIdAnalisis(), new java.util.Date());
        } else {
            vla.mostrarMensaje("Seleccione un análisis para imprimir.");
        }
    }

    public void onBuscarAnalisis() {
        refrescarTabla(vla.getTextoBusqueda());
    }

    public void onVolver() {
        router.irAInicio();
    }
}