package presentador;

import dao.AnalisisDAO;
import dao.AuditoriaDAO;
import dao.UsuarioDAO;
import java.util.ArrayList;
import modelo.Analisis;
import modelo.Usuario;
import presentador.router.AppRouter;
import servicio.ReporteService;
import vista.interfaces.IVistaAnalisis;

// 1. Adiós al implements ActionListener
public class AnalisisPresenter {
    
    private final IVistaAnalisis vla;
    private final AppRouter router; // 2. El router maneja la navegación
    private final AnalisisDAO analisisDAO;
    private Usuario usuarioLogueado;
    private AuditoriaDAO auditoriaDAO;
    private final ReporteService reporteService; // 3. El servicio para imprimir PDFs

    // Constructor actualizado
    public AnalisisPresenter(IVistaAnalisis vla, AppRouter router, AnalisisDAO analisisDAO, ReporteService reporteService,AuditoriaDAO auditoriaDAO, Usuario usuarioLogueado) {
        this.vla = vla;
        this.router = router;
        this.analisisDAO = analisisDAO;
        this.reporteService = reporteService;
        this.auditoriaDAO = auditoriaDAO;
        this.usuarioLogueado = usuarioLogueado;
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
            router.abrirDetalleAnalisis(sel.getIdAnalisis(),"LISTADO");
        } else {
            vla.mostrarMensaje("Seleccione un análisis.");
        }
    }

    public void onImprimirAnalisis() {
        modelo.Analisis sel = vla.getAnalisisSeleccionado();
        if (sel != null) {
            // 1. Bloqueamos el botón inmediatamente para evitar clics dobles accidentales
            vla.habilitarBotonImprimir(false);

            // 2. Enviamos la carga pesada a un hilo secundario para no congelar la pantalla
            new Thread(() -> {
                try {
                    // El Presentador imprime usando el servicio limpio (usando la fecha actual)
                    reporteService.generarInforme(sel.getIdAnalisis(), new java.util.Date());
                    
                    // -- NUEVA LÓGICA: ACTUALIZAR ESTADO EN BD Y REPINTAR TABLA --
                    if (analisisDAO.cambiarEstadoGenerado(sel.getIdAnalisis())) {
                        auditoriaDAO.registrar(this.usuarioLogueado, "IMPRIMIR", "analisis", sel.getIdAnalisis(), 
                                null, "Informe generado", "Impresión desde Lista Principal");
                        
                        // Volvemos al hilo de la interfaz para pedirle a la vista que refresque la tabla
                        javax.swing.SwingUtilities.invokeLater(() -> {
                            onBuscarAnalisis(); // Recarga la tabla para que se pinte de verde
                        });
                    }
                    // ------------------------------------------------------------
                    
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    // 3. Reactivamos el botón volviendo al hilo de la interfaz visual
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