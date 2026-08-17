package presentador;

import dao.EstadisticaDAO;
import dao.ObraSocialDAO;
import dao.MedicoDAO;
import dao.DeterminacionDAO;
import dao.ResultadoAnalisisDAO;
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JPanel;
import modelo.dto.FilaFacturacionDTO;
import modelo.dto.MetricaDTO;
import modelo.dto.ResumenGlobalDTO;
import modelo.ResultadoAnalisis;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
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

    public EstadisticasPresenter(IVistaEstadistica vista, EstadisticaDAO estadisticaDAO, 
                                 ObraSocialDAO obraSocialDAO, MedicoDAO medicoDAO,
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
        this.vista.setPresenter(this);
    }

    public void onVolver() {
    // 1. Primero limpiar la vista completamente
    vista.limpiarFocos();
    
    // 2. Luego navegar
    router.irAInicio();
}
    /**
 * Método auxiliar para limpiar la vista desde el Router
 */
public void limpiarVista() {
    vista.limpiarFocos();
}

    public void iniciar() {
        vista.setPresenter(this);
        vista.limpiarFocos();
        vista.ejecutar();

        javax.swing.SwingUtilities.invokeLater(() -> {
            onFiltrar();
        });
    }

    public void onFiltrar() {
        Date desde = vista.getFechaDesde();
        Date hasta = vista.getFechaHasta();
        String codigoOS = vista.getObraSocialFiltro();
        String medico = vista.getMedicoFiltro();
        String practica = vista.getPracticaFiltro();

        if (desde == null || hasta == null) {
            vista.mostrarMensaje("Debe seleccionar un rango de fechas válido.");
            return;
        }
        if (desde.after(hasta)) {
            vista.mostrarMensaje("La fecha 'Desde' no puede ser mayor que 'Hasta'.");
            return;
        }

        // 1. Totales
        ResumenGlobalDTO resumen = estadisticaDAO.obtenerResumenGlobal(desde, hasta, codigoOS);
        vista.setTotalAnalisis(String.valueOf(resumen.getTotalAnalisis()));
        vista.setTotalFacturado(String.format("$ %,.2f", resumen.getTotalFacturado()));

        // 2. Gráfico Torta - Obras Sociales
        List<MetricaDTO> datosOS = estadisticaDAO.agruparPorObraSocial(desde, hasta);
        vista.mostrarGraficoObrasSociales(crearPanelGrafico(crearGraficoTorta(datosOS), 300, 200));

        // 3. Gráfico Barras - Evolución Mensual
        List<MetricaDTO> datosMes = estadisticaDAO.agruparPorMes(desde, hasta);
        vista.mostrarGraficoEvolucion(crearPanelGrafico(crearGraficoBarras(datosMes), 300, 200));

        // 4. Gráfico Prácticas - Top prácticas (SOLO PADRES)
        List<MetricaDTO> datosPracticas = estadisticaDAO.agruparPorPracticaPadre(desde, hasta, codigoOS, medico);
        vista.mostrarGraficoPracticas(crearPanelGrafico(crearGraficoBarrasPracticas(datosPracticas), 300, 200));

        // 5. Tabla
        List<FilaFacturacionDTO> detalle = estadisticaDAO.obtenerDetalleFacturacion(desde, hasta, codigoOS, medico, practica);
        llenarTabla(detalle);
    }

    // ── MÉTODO AUXILIAR PARA CONVERTIR JFreeChart A JPanel ──
    private JPanel crearPanelGrafico(JFreeChart chart, int ancho, int alto) {
        ChartPanel panel = new ChartPanel(chart);
        panel.setPreferredSize(new java.awt.Dimension(ancho, alto));
        panel.setMinimumSize(new java.awt.Dimension(ancho - 50, alto - 50));
        return panel;
    }

    // ── VER DETALLE DE PRÁCTICAS (SOLO PADRES) ──
    public void onVerDetallePracticas(int idAnalisis) {
        List<ResultadoAnalisis> resultados = resultadoDAO.listarPorAnalisis(idAnalisis);
        if (resultados.isEmpty()) {
            vista.mostrarMensaje("No se encontraron prácticas para este análisis.");
            return;
        }

        // Filtrar y agrupar por código padre
        Set<String> codigosPadre = new HashSet<>();
        List<ResultadoAnalisis> padres = new ArrayList<>();

        for (ResultadoAnalisis r : resultados) {
            String nombre = r.getNombrePrueba();
            String codigo = r.getCodigo();

            if (nombre == null || nombre.trim().isEmpty()) continue;
            if (nombre.trim().startsWith("---") && nombre.trim().endsWith("---")) continue;
            if (codigo == null || codigo.trim().isEmpty()) continue;

            String codigoPadre = codigo.contains(".")
                ? codigo.substring(0, codigo.indexOf('.'))
                : codigo;

            if (!codigosPadre.contains(codigoPadre)) {
                codigosPadre.add(codigoPadre);
                
                // Buscar el nombre del padre en el catálogo
                String nombrePadre = determinacionDAO.obtenerNombrePorCodigo(codigoPadre);
                if (nombrePadre == null || nombrePadre.trim().isEmpty()) {
                    nombrePadre = nombre;
                }
                
                ResultadoAnalisis padreVirtual = new ResultadoAnalisis();
                padreVirtual.setCodigo(codigoPadre);
                padreVirtual.setNombrePrueba(nombrePadre);
                padres.add(padreVirtual);
            }
        }

        if (padres.isEmpty()) {
            vista.mostrarMensaje("No se encontraron prácticas principales para este análisis.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("PRÁCTICAS INCLUIDAS EN EL ANÁLISIS #").append(idAnalisis).append("\n");
        sb.append("═".repeat(45)).append("\n\n");
        for (ResultadoAnalisis r : padres) {
            sb.append("  ▸  ").append(r.getNombrePrueba());
            if (r.getCodigo() != null && !r.getCodigo().isEmpty()) {
                sb.append("   [").append(r.getCodigo()).append("]");
            }
            sb.append("\n");
        }

        vista.mostrarDetallePracticas("Prácticas del Análisis #" + idAnalisis, sb.toString());
    }

    public void onExportarPlanilla() {
        vista.mostrarMensaje("Función de exportación en desarrollo. ¡Estará lista en el próximo paso!");
    }

    // ═════════════════════════════════════════════════════════════════
    //  BUSCADORES CON AUTOCOMPLETADO
    // ═════════════════════════════════════════════════════════════════
    public void onBuscarSugerenciasOS() {
        String busqueda = vista.getObraSocialFiltro();
        if (busqueda == null || busqueda.trim().isEmpty() || busqueda.equals("TODAS")) {
            vista.mostrarSugerenciasOS(new ArrayList<>());
            return;
        }
        List<modelo.ObraSocial> resultados = obraSocialDAO.buscarPorCodigoONombre(busqueda);
        List<String> sugerencias = new ArrayList<>();
        for (modelo.ObraSocial os : resultados) {
            sugerencias.add(os.getNombre() + " (" + os.getCodigo() + ")");
        }
        vista.mostrarSugerenciasOS(sugerencias);
    }

    public void onBuscarSugerenciasMedicos() {
        String busqueda = vista.getMedicoFiltro();
        if (busqueda == null || busqueda.trim().isEmpty() || busqueda.equals("TODOS")) {
            vista.mostrarSugerenciasMedicos(new ArrayList<>());
            return;
        }
        List<String> sugerencias = medicoDAO.obtenerSugerenciasMedicos(busqueda);
        vista.mostrarSugerenciasMedicos(sugerencias);
    }

    public void onBuscarSugerenciasPracticas() {
        String busqueda = vista.getPracticaFiltro();
        if (busqueda == null || busqueda.trim().isEmpty() || busqueda.equals("TODAS")) {
            vista.mostrarSugerenciasPracticas(new ArrayList<>());
            return;
        }
        List<String> sugerencias = estadisticaDAO.obtenerSugerenciasPracticasPadre(busqueda);
        vista.mostrarSugerenciasPracticas(sugerencias);
    }

    // ═════════════════════════════════════════════════════════════════
    //  GENERADORES DE JFREECHART
    // ═════════════════════════════════════════════════════════════════
    private JFreeChart crearGraficoTorta(List<MetricaDTO> datos) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        for (MetricaDTO m : datos) {
            dataset.setValue(m.getCategoria(), m.getTotal());
        }

        JFreeChart chart = ChartFactory.createPieChart("", dataset, true, true, false);
        chart.setBackgroundPaint(Color.WHITE);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        return chart;
    }

    private JFreeChart crearGraficoBarras(List<MetricaDTO> datos) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (MetricaDTO m : datos) {
            dataset.addValue(m.getTotal(), "Facturación", m.getCategoria());
        }

        JFreeChart chart = ChartFactory.createBarChart("", "", "Total Facturado ($)",
                dataset, PlotOrientation.VERTICAL, false, true, false);
        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setRangeGridlinePaint(new Color(210, 220, 232));
        return chart;
    }

    private JFreeChart crearGraficoBarrasPracticas(List<MetricaDTO> datos) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        int limit = Math.min(datos.size(), 8);
        for (int i = 0; i < limit; i++) {
            MetricaDTO m = datos.get(i);
            String label = m.getCategoria().length() > 20 ? 
                m.getCategoria().substring(0, 18) + "..." : m.getCategoria();
            dataset.addValue(m.getCantidad(), "Prácticas", label);
        }

        JFreeChart chart = ChartFactory.createBarChart("", "", "Cantidad",
                dataset, PlotOrientation.HORIZONTAL, false, true, false);
        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setRangeGridlinePaint(new Color(210, 220, 232));
        return chart;
    }

    private void llenarTabla(List<FilaFacturacionDTO> detalle) {
        Object[][] matriz = new Object[detalle.size()][6];
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        for (int i = 0; i < detalle.size(); i++) {
            FilaFacturacionDTO f = detalle.get(i);
            matriz[i][0] = f.getIdAnalisis();
            matriz[i][1] = sdf.format(f.getFecha());
            matriz[i][2] = f.getPaciente();
            matriz[i][3] = f.getObraSocial();
            matriz[i][4] = f.getMedico();
            matriz[i][5] = String.format("%,.2f", f.getPrecio());
        }
        vista.mostrarDatosTabla(matriz);
    }
}