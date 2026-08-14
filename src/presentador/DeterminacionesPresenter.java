package presentador;

// @author lucianoalicata

import dao.DeterminacionDAO;
import java.util.ArrayList;
import java.util.List;
import modelo.Determinacion;
import modelo.Paciente;
import presentador.router.AppRouter;
import vista.interfaces.IVistaDeterminaciones;

public class DeterminacionesPresenter {

    private final IVistaDeterminaciones vd;
    private final AppRouter router;
    private final DeterminacionDAO determinacionDAO;
    private final Paciente pacienteActual;

    private final List<Determinacion> determinacionesSeleccionadas = new ArrayList<>();
    private final List<Determinacion> listaVisualDeterminaciones = new ArrayList<>();

    public DeterminacionesPresenter(IVistaDeterminaciones vd, AppRouter router, DeterminacionDAO determinacionDAO, Paciente pacienteActual) {
        this.vd = vd;
        this.router = router;
        this.determinacionDAO = determinacionDAO;
        this.pacienteActual = pacienteActual;
    }

    public void iniciar() {
        vd.setPresenter(this);
        determinacionesSeleccionadas.clear();
        listaVisualDeterminaciones.clear();
        vd.limpiarCampos();
        refrescarTablaSeleccion();

        vd.ejecutar();
    }

    public void onAgregarDeterminacion() {
        String codBusqueda = vd.getDeterminacion().trim();
        if (codBusqueda.isEmpty()) {
            vd.mostrarMensaje("Ingrese un código o nombre");
            return;
        }

        boolean esSufijo = codBusqueda.matches("\\d{3}");
        Determinacion detExacta = esSufijo ? null : determinacionDAO.buscarPorCodigo(codBusqueda);

        if (detExacta != null) {
            List<Determinacion> componentesHijos = determinacionDAO.obtenerComponentes(detExacta.getCodigo());

            if (!componentesHijos.isEmpty()) {
                int agregados = 0;
                for (Determinacion hijo : componentesHijos) {
                    if (determinacionesSeleccionadas.stream().noneMatch(x -> x.getCodigo().equals(hijo.getCodigo()))) {
                        determinacionesSeleccionadas.add(hijo);
                        agregados++;
                    }
                }
                if (agregados == 0) {
                    vd.mostrarMensaje("Todos los componentes de esta práctica ya fueron agregados.");
                    vd.limpiarCampos();
                } else {
                    refrescarTablaSeleccion();
                }
            } else {
                boolean yaExiste = determinacionesSeleccionadas.stream().anyMatch(x -> x.getCodigo().equals(detExacta.getCodigo()));
                if (yaExiste) {
                    vd.mostrarMensaje("Esta determinación ya ha sido agregada.");
                } else {
                    determinacionesSeleccionadas.add(detExacta);
                    refrescarTablaSeleccion();
                }
            }
        } else {
            List<Determinacion> coincidencias = determinacionDAO.buscar(codBusqueda);

            if (coincidencias.isEmpty()) {
                vd.mostrarMensaje("No se encontró ninguna práctica con: [" + codBusqueda + "]");
            } else if (coincidencias.size() == 1) {
                Determinacion det = coincidencias.get(0);
                List<Determinacion> hijos = determinacionDAO.obtenerComponentes(det.getCodigo());

                if (!hijos.isEmpty()) {
                    int agregados = 0;
                    for (Determinacion hijo : hijos) {
                        if (determinacionesSeleccionadas.stream().noneMatch(x -> x.getCodigo().equals(hijo.getCodigo()))) {
                            determinacionesSeleccionadas.add(hijo);
                            agregados++;
                        }
                    }
                    if (agregados == 0) {
                        vd.mostrarMensaje("Todos los componentes ya fueron agregados.");
                    } else {
                        refrescarTablaSeleccion();
                    }
                } else {
                    boolean yaExiste = determinacionesSeleccionadas.stream().anyMatch(x -> x.getCodigo().equals(det.getCodigo()));
                    if (yaExiste) {
                        vd.mostrarMensaje("Esta determinación ya ha sido agregada.");
                    } else {
                        determinacionesSeleccionadas.add(det);
                        refrescarTablaSeleccion();
                    }
                }
            } else {
                vd.mostrarSugerencias(coincidencias);
            }
        }
    }

    public void onEliminarDeterminacion() {
        int[] filasSeleccionadas = vd.getFilasSeleccionadas();
        if (filasSeleccionadas != null && filasSeleccionadas.length > 0) {
            List<String> codigosAEliminar = new ArrayList<>();

            for (int fila : filasSeleccionadas) {
                if (fila >= 0 && fila < listaVisualDeterminaciones.size()) {
                    Determinacion det = listaVisualDeterminaciones.get(fila);

                    if (det.getCodigo() != null && !det.getCodigo().trim().isEmpty()) {
                        codigosAEliminar.add(det.getCodigo());
                    }
                }
            }

            if (!codigosAEliminar.isEmpty()) {
                determinacionesSeleccionadas.removeIf(d -> codigosAEliminar.contains(d.getCodigo()));
                refrescarTablaSeleccion();
            }
        } else {
            vd.mostrarMensaje("Por favor, seleccione una o más filas para eliminar.");
        }
    }

    public void onBuscarSugerencias() {
        String texto = vd.getDeterminacion();
        List<Determinacion> sugerencias;

        if (texto.matches("\\d{3}")) {
            sugerencias = determinacionDAO.buscarPorSufijo(texto);
        } else {
            sugerencias = determinacionDAO.buscar(texto);
        }

        vd.mostrarSugerencias(sugerencias);
    }

    public void onContinuar() {
        if (determinacionesSeleccionadas.isEmpty()) {
            vd.mostrarMensaje("Debe agregar al menos una determinación para continuar.");
            return;
        }

        if (vd != null) {
            vd.cerrarPantalla();
        }

        router.limpiarReferenciaDeterminaciones();
        router.abrirCargaResultados(pacienteActual, listaVisualDeterminaciones);
    }

    private void refrescarTablaSeleccion() {
        determinacionesSeleccionadas.sort((d1, d2) -> {
            String codPadre1 = d1.getCodigo().contains(".") ? d1.getCodigo().split("\\.")[0] : d1.getCodigo();
            String codPadre2 = d2.getCodigo().contains(".") ? d2.getCodigo().split("\\.")[0] : d2.getCodigo();

            if (!codPadre1.equals(codPadre2)) {
                Determinacion padre1 = determinacionDAO.buscarPorCodigo(codPadre1);
                Determinacion padre2 = determinacionDAO.buscarPorCodigo(codPadre2);
                int prioGlobal1 = (padre1 != null) ? padre1.getPrioridad() : 999;
                int prioGlobal2 = (padre2 != null) ? padre2.getPrioridad() : 999;

                if (prioGlobal1 != prioGlobal2) {
                    return Integer.compare(prioGlobal1, prioGlobal2);
                }
                return codPadre1.compareTo(codPadre2);
            }
            return Integer.compare(d1.getPrioridad(), d2.getPrioridad());
        });

        this.listaVisualDeterminaciones.clear();
        String codigoPadreActual = "";

        for (Determinacion d : determinacionesSeleccionadas) {
            String codigoPadreFila = d.getCodigo().contains(".") ? d.getCodigo().split("\\.")[0] : d.getCodigo();

            if (!codigoPadreFila.equals(codigoPadreActual)) {
                Determinacion detPadre = determinacionDAO.buscarPorCodigo(codigoPadreFila);
                String nombreTitulo = (detPadre != null) ? detPadre.getNombre() : "ESTUDIO";
                this.listaVisualDeterminaciones.add(crearTituloVirtual("--- " + nombreTitulo + " ---"));
                codigoPadreActual = codigoPadreFila;
            }
            this.listaVisualDeterminaciones.add(d);
        }

        vd.cargarTablaConTitulos(this.listaVisualDeterminaciones);
    }

    private Determinacion crearTituloVirtual(String titulo) {
        Determinacion d = new Determinacion();
        d.setNombre(titulo);
        return d;
    }

    public void cerrarVista() {
        if (vd != null) {
            vd.cerrarPantalla();
        }
    }
}
