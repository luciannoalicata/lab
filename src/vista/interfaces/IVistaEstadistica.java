package vista.interfaces;

import java.util.Date;
import java.util.List;
import javax.swing.JPanel;
import presentador.EstadisticasPresenter;

public interface IVistaEstadistica {
    
    // ── Métodos base ──────────────────────────────────────────────────
    void ejecutar();
    void setPresenter(EstadisticasPresenter presenter);
    void limpiarFocos();
    int confirmarAccion(String mensaje, String titulo);
    void mostrarMensaje(String mensaje);
    
    // ── Fechas ──────────────────────────────────────────────────────
    Date getFechaDesde();
    Date getFechaHasta();
    
    // ── Filtros ────────────────────────────────────────────────────
    String getObraSocialFiltro();
    String getMedicoFiltro();
    String getPracticaFiltro();
    
    // ── Obras Sociales ─────────────────────────────────────────────
    void cargarComboObrasSociales(List<String> obras);
    
    // ── Totales ────────────────────────────────────────────────────
    void setTotalAnalisis(String total);
    void setTotalFacturado(String total);
    
    // ── Gráficos ────────────────────────────────────────────────────
    void mostrarGraficoObrasSociales(JPanel panelGrafico);
    void mostrarGraficoEvolucion(JPanel panelGrafico);
    void mostrarGraficoPracticas(JPanel panelGrafico);
    
    // ── Tabla ──────────────────────────────────────────────────────
    void mostrarDatosTabla(Object[][] datos);
    
    // ── Sugerencias para autocompletado ────────────────────────────
    void mostrarSugerenciasOS(List<String> sugerencias);
    void mostrarSugerenciasMedicos(List<String> sugerencias);
    void mostrarSugerenciasPracticas(List<String> sugerencias);
    
    // ── Detalle de prácticas de un análisis ────────────────────────
    void mostrarDetallePracticas(String titulo, String detalle);
}