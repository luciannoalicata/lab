// ═══════════════════════════════════════════════════════════════════
// ARCHIVO 1: vista/interfaces/IVistaEstadistica.java
// ═══════════════════════════════════════════════════════════════════
package vista.interfaces;

import java.util.Date;
import java.util.List;
import presentador.EstadisticasPresenter;

public interface IVistaEstadistica {

    // ── Ciclo de vida ────────────────────────────────────────────────
    void ejecutar();
    void setPresenter(EstadisticasPresenter presenter);
    void limpiarFocos();
    void mostrarMensaje(String mensaje);
    int  confirmarAccion(String mensaje, String titulo);

    // ── Filtros ──────────────────────────────────────────────────────
    Date   getFechaDesde();
    Date   getFechaHasta();
    String getObraSocialFiltro();   // "" o "TODAS" = sin filtro
    String getMedicoFiltro();        // "" o "TODOS" = sin filtro
    String getDeterminacionFiltro(); // "" o "TODAS" = sin filtro

    // ── Sugerencias de autocompletado ────────────────────────────────
    void mostrarSugerenciasOS(List<String> sugerencias);
    void mostrarSugerenciasMedicos(List<String> sugerencias);
    void mostrarSugerenciasDeterminaciones(List<String> sugerencias);

    // ── Resultados ───────────────────────────────────────────────────
    void mostrarResultados(Object[][] datos);         // carga la grilla principal
    void setResumen(String totalAnalisis, String totalFacturado); // tarjetas

    // ── Gráficos ─────────────────────────────────────────────────────
    void actualizarGraficoOS(java.util.Map<String, Integer> datos);
    void actualizarGraficoPracticas(java.util.Map<String, Integer> datos);

    // ── Detalle al doble click ────────────────────────────────────────
    void mostrarDetallePracticas(String titulo, String detalle);
}