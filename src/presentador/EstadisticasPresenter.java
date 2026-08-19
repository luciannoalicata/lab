package presentador;

import dao.*;
import java.util.*;
import javax.swing.SwingWorker;
import modelo.ResultadoAnalisis;
import presentador.router.AppRouter;
import vista.interfaces.IVistaEstadistica;

public class EstadisticasPresenter {

    private final IVistaEstadistica vista;
    private final EstadisticaDAO estadisticaDAO;
    private final ObraSocialDAO obraSocialDAO;
    private final MedicoDAO medicoDAO;
    private final DeterminacionDAO determinacionDAO;
    private final ResultadoAnalisisDAO resultadoDAO;
    private final AppRouter router;
    
    private SwingWorker<?, ?> currentWorker;

    public EstadisticasPresenter(
            IVistaEstadistica vista,
            EstadisticaDAO estadisticaDAO,
            ObraSocialDAO obraSocialDAO,
            MedicoDAO medicoDAO,
            DeterminacionDAO determinacionDAO,
            ResultadoAnalisisDAO resultadoDAO,
            AppRouter router) {

        this.vista = vista;
        this.estadisticaDAO = estadisticaDAO;
        this.obraSocialDAO = obraSocialDAO;
        this.medicoDAO = medicoDAO;
        this.determinacionDAO = determinacionDAO;
        this.resultadoDAO = resultadoDAO;
        this.router = router;
    }

    // ── Ciclo de vida ────────────────────────────────────────────────

    public void iniciar() {
        vista.setPresenter(this);  // setPresenter ya llama a limpiarFocos()
        onFiltrar();               // cargar con los filtros por defecto
    }

    public void onVolver() {
        if (currentWorker != null && !currentWorker.isDone()) currentWorker.cancel(true);
        vista.limpiarFocos();
        // ¡CORREGIDO! Vuelve al inicio limpio mostrando el logo de Biotec
        router.irAInicio(); 
    }

    // ── Acciones de la vista ─────────────────────────────────────────

    public void onFiltrar() {
        Date desde = vista.getFechaDesde();
        Date hasta = vista.getFechaHasta();

        if (desde == null || hasta == null) {
            vista.mostrarMensaje("Seleccione un período válido.");
            return;
        }
        if (desde.after(hasta)) {
            vista.mostrarMensaje("La fecha DESDE no puede ser posterior a HASTA.");
            return;
        }

        String filtroOS  = vista.getObraSocialFiltro();
        String filtroMed = vista.getMedicoFiltro();
        String filtroDet = vista.getDeterminacionFiltro();

        if (currentWorker != null && !currentWorker.isDone()) currentWorker.cancel(true);

        currentWorker = new SwingWorker<Void, Void>() {
            List<Object[]> filas;
            Map<String, Integer> datosOS;
            Map<String, Integer> datosPracticas;

            @Override
            protected Void doInBackground() throws Exception {
                // LLAMADAS AL NUEVO DAO
                filas = estadisticaDAO.buscarAnalisisFiltrado(desde, hasta, filtroOS, filtroMed, filtroDet);
                datosOS = estadisticaDAO.contarPorObraSocial(desde, hasta, filtroOS);
                datosPracticas = estadisticaDAO.contarPorPractica(desde, hasta, filtroDet);
                return null;
            }

            @Override
            protected void done() {
                if (isCancelled()) return;
                try {
                    // Preparar datos para la grilla
                    Object[][] datos = new Object[filas.size()][];
                    double facturado = 0;
                    for (int i = 0; i < filas.size(); i++) {
                        datos[i] = filas.get(i);
                        if (filas.get(i)[7] != null) {
                            facturado += Double.parseDouble(filas.get(i)[7].toString());
                        }
                    }
                    
                    vista.mostrarResultados(datos);
                    vista.setResumen(
                        String.valueOf(filas.size()),
                        String.format("$ %,.2f", facturado).replace(",", ".")
                    );
                    vista.actualizarGraficoOS(datosOS);
                    vista.actualizarGraficoPracticas(datosPracticas);
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        currentWorker.execute();
    }

    public void onExportar() {
        vista.mostrarMensaje("Función de exportación — próximamente disponible.");
    }

    // ── Detalle prácticas al doble clic ──────────────────────────────

    public void onVerDetallePracticas(int idAnalisis) {
        List<ResultadoAnalisis> resultados = resultadoDAO.listarPorAnalisis(idAnalisis);
        if (resultados.isEmpty()) {
            vista.mostrarMensaje("No se encontraron prácticas para el análisis #" + idAnalisis);
            return;
        }

        Map<String, String> padres = new LinkedHashMap<>();
        for (ResultadoAnalisis r : resultados) {
            String cod = r.getCodigo();
            if (cod == null || cod.trim().isEmpty()) continue;
            
            String nom = r.getNombrePrueba();
            if (nom != null && nom.startsWith("---") && nom.endsWith("---")) continue;

            String codigoPadre = cod.contains(".") ? cod.substring(0, cod.indexOf('.')) : cod;
            if (!padres.containsKey(codigoPadre)) {
                String nombrePadre = determinacionDAO.obtenerNombrePorCodigo(codigoPadre);
                padres.put(codigoPadre, nombrePadre != null ? nombrePadre : nom);
            }
        }

        if (padres.isEmpty()) {
            vista.mostrarMensaje("No se encontraron prácticas principales para este análisis.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("PRÁCTICAS DEL ANÁLISIS #").append(idAnalisis).append("\n");
        sb.append("-".repeat(40)).append("\n\n");
        padres.forEach((cod, nom) ->
            sb.append("  -  ").append(nom).append("   [").append(cod).append("]\n")
        );

        vista.mostrarDetallePracticas("Prácticas del Análisis #" + idAnalisis, sb.toString());
    }

    // ── Buscadores de autocompletado ─────────────────────────────────

    public void onBuscarSugerenciasOS() {
        String busqueda = vista.getObraSocialFiltro();
        if (busqueda.isEmpty()) { vista.mostrarSugerenciasOS(List.of()); return; }
        List<modelo.ObraSocial> res = obraSocialDAO.buscarPorCodigoONombre(busqueda);
        List<String> sugs = new ArrayList<>();
        for (modelo.ObraSocial os : res) sugs.add(os.getNombre() + " (" + os.getCodigo() + ")");
        vista.mostrarSugerenciasOS(sugs);
    }

    public void onBuscarSugerenciasMedicos() {
        String busqueda = vista.getMedicoFiltro();
        if (busqueda.isEmpty()) { vista.mostrarSugerenciasMedicos(List.of()); return; }
        List<String> sugs = medicoDAO.obtenerSugerenciasMedicos(busqueda);
        vista.mostrarSugerenciasMedicos(sugs);
    }

    public void onBuscarSugerenciasDeterminaciones() {
        String busqueda = vista.getDeterminacionFiltro();
        if (busqueda.isEmpty()) { vista.mostrarSugerenciasDeterminaciones(List.of()); return; }
        List<String> sugs = determinacionDAO.obtenerSugerenciasPadresPorNombreOCodigo(busqueda);
        vista.mostrarSugerenciasDeterminaciones(sugs);
    }
}