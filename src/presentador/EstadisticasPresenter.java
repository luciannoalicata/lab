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
        router.irAInicio(); 
    }

    public void onFiltrar() {
        Date desde = vista.getFechaDesde();
        Date hasta = vista.getFechaHasta();

        if (desde == null || hasta == null) {
            vista.mostrarMensaje("Seleccione un período válido."); return;
        }
        if (desde.after(hasta)) {
            vista.mostrarMensaje("La fecha DESDE no puede ser posterior a HASTA."); return;
        }

        String filtroOS = extraerValor(vista.getObraSocialFiltro(), "(", ")");
        String filtroMed = extraerValor(vista.getMedicoFiltro(), "(", ")");
        String filtroDet = extraerValor(vista.getDeterminacionFiltro(), "[", "]");

        if (currentWorker != null && !currentWorker.isDone()) currentWorker.cancel(true);

        currentWorker = new SwingWorker<Void, Void>() {
            List<Object[]> filas;
            Map<String, Integer> datosOS;
            Map<String, Integer> datosPracticas;

            @Override
            protected Void doInBackground() {
                filas = estadisticaDAO.buscarAnalisisFiltrado(desde, hasta, filtroOS, filtroMed, filtroDet);
                datosOS = estadisticaDAO.contarPorObraSocial(desde, hasta, filtroOS);
                datosPracticas = estadisticaDAO.contarPorPractica(desde, hasta, filtroDet);
                return null;
            }

            @Override
            protected void done() {
                if (isCancelled()) return;
                try {
                    // Preparamos los datos para la GRILLA (7 columnas: se unen Apellido y Nombre)
                    Object[][] datosUI = new Object[filas.size()][7];
                    double facturado = 0;
                    
                    for (int i = 0; i < filas.size(); i++) {
                        Object[] f = filas.get(i);
                        // f = [0]id, [1]fecha, [2]dni, [3]apellido, [4]nombre, [5]medico, [6]os, [7]practicas, [8]precio
                        String pacienteCompleto = f[3].toString() + " " + f[4].toString();
                        
                        datosUI[i] = new Object[]{f[0], f[1], f[2], pacienteCompleto.trim(), f[5], f[6], f[7]};
                        if (f[8] != null) facturado += Double.parseDouble(f[8].toString());
                    }
                    
                    vista.mostrarResultados(datosUI);
                    vista.setResumen(String.valueOf(filas.size()), String.format("$ %,.2f", facturado).replace(",", "."));
                    vista.actualizarGraficoOS(datosOS);
                    vista.actualizarGraficoPracticas(datosPracticas);
                    
                } catch (Exception e) { e.printStackTrace(); }
            }
        };
        currentWorker.execute();
    }

    public void onExportar() {
        Date desde = vista.getFechaDesde();
        Date hasta = vista.getFechaHasta();

        String filtroOS = extraerValor(vista.getObraSocialFiltro(), "(", ")");
        String filtroMed = extraerValor(vista.getMedicoFiltro(), "(", ")");
        String filtroDet = extraerValor(vista.getDeterminacionFiltro(), "[", "]");

        List<Object[]> filas = estadisticaDAO.buscarAnalisisFiltrado(desde, hasta, filtroOS, filtroMed, filtroDet);
        if (filas.isEmpty()) {
            vista.mostrarMensaje("No hay datos para exportar en el período seleccionado.");
            return;
        }

        // Preguntar al usuario si desea incluir el chorizo de prácticas
        int resp = vista.confirmarAccion("¿Desea incluir el detalle de las Prácticas en el reporte Excel?\n(Puede hacer que las filas sean muy largas de leer)", "Opciones de Exportación");
        boolean incluirPracticas = (resp == 0); // 0 es JOptionPane.YES_OPTION

        // Armamos la tabla atómica para el Excel
        int columnasExcel = incluirPracticas ? 8 : 7;
        Object[][] datosExcel = new Object[filas.size()][columnasExcel];
        double facturado = 0;

        for (int i = 0; i < filas.size(); i++) {
            Object[] f = filas.get(i);
            // f = [0]id, [1]fecha, [2]dni, [3]apellido, [4]nombre, [5]medico, [6]os, [7]practicas, [8]precio
            if (incluirPracticas) {
                datosExcel[i] = new Object[]{f[0], f[1], f[2], f[3], f[4], f[5], f[6], f[7]};
            } else {
                datosExcel[i] = new Object[]{f[0], f[1], f[2], f[3], f[4], f[5], f[6]};
            }
            if (f[8] != null) facturado += Double.parseDouble(f[8].toString());
        }

        // Armamos los datos del encabezado
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
        String periodo = "Desde " + sdf.format(desde) + " hasta " + sdf.format(hasta);
        String totalStr = String.valueOf(filas.size());
        String facturadoStr = String.format("$ %,.2f", facturado).replace(",", ".");

        boolean exito = servicio.ExportadorExcelService.exportarTablaACSV(
                datosExcel, totalStr, facturadoStr, periodo, incluirPracticas, (java.awt.Component) vista
        );

        if (exito) {
            vista.mostrarMensaje("¡El reporte se exportó correctamente!\n Puede verlo en una hoja de cálculo. \n (EXCEL, GOOGLE HOJA DE CÁLCULO, LIBREOFFICE CALC, ETC.)");
        }
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
        sb.append("-".repeat(80)).append("\n\n");
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

    // ── MÉTODO FALTANTE ──────────────────────────────────────────────
    private String extraerValor(String texto, String delA, String delB) {
        if (texto == null || texto.trim().isEmpty() || texto.equalsIgnoreCase("TODAS") || texto.equalsIgnoreCase("TODOS")) {
            return "";
        }
        if (texto.contains(delA) && texto.contains(delB)) {
            return texto.substring(texto.lastIndexOf(delA) + 1, texto.lastIndexOf(delB)).trim();
        }
        return texto.trim();
    }
}