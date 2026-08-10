package presentador;

// @author lucianoalicata

import dao.AnalisisDAO;
import dao.AuditoriaDAO;
import java.util.ArrayList;
import modelo.Analisis;
import modelo.Usuario;
import presentador.router.AppRouter;
import servicio.ReporteService;
import vista.interfaces.IVistaAnalisis;

public class AnalisisPresenter {
    
    private final IVistaAnalisis vla;
    private final AppRouter router; 
    private final AnalisisDAO analisisDAO;
    private final Usuario usuarioLogueado;
    private final AuditoriaDAO auditoriaDAO;
    private final ReporteService reporteService; 

    public AnalisisPresenter(IVistaAnalisis vla, AppRouter router, AnalisisDAO analisisDAO, ReporteService reporteService,AuditoriaDAO auditoriaDAO, Usuario usuarioLogueado) {
        this.vla = vla;
        this.router = router;
        this.analisisDAO = analisisDAO;
        this.reporteService = reporteService;
        this.auditoriaDAO = auditoriaDAO;
        this.usuarioLogueado = usuarioLogueado;
    }

    public void iniciar() {
        vla.setPresenter(this);
        refrescarTabla("");
    }

    public void refrescarTabla(String filtro) {
        ArrayList<Analisis> lista = analisisDAO.buscarAnalisisGlobal(filtro);
        vla.cargarAnalisisEnTabla(lista);
    }

    public void onVerDetalles() {
        Analisis sel = vla.getAnalisisSeleccionado();
        if (sel != null) {
            router.abrirDetalleAnalisis(sel.getIdAnalisis(),"LISTADO");
        } else {
            vla.mostrarMensaje("Seleccione un análisis.");
        }
    }

    public void onImprimirAnalisis() {
        modelo.Analisis sel = vla.getAnalisisSeleccionado();
        if (sel != null) {
            vla.habilitarBotonImprimir(false);

            new Thread(() -> {
                try {
                    modelo.Analisis analisisCompleto = analisisDAO.buscarPorId(sel.getIdAnalisis());
                    java.util.Date fechaReal = analisisCompleto.getFecha();

                    reporteService.generarInforme(sel.getIdAnalisis(), fechaReal);
                    
                    if (analisisDAO.cambiarEstadoGenerado(sel.getIdAnalisis())) {
                        auditoriaDAO.registrar(this.usuarioLogueado, "IMPRIMIR", "analisis", sel.getIdAnalisis(), 
                                null, "Informe generado", "Impresión desde Lista Principal");
                        
                        javax.swing.SwingUtilities.invokeLater(() -> {
                            onBuscarAnalisis(); 
                        });
                    }
                    
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        vla.habilitarBotonImprimir(true);
                    });
                }
            }).start();
            
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