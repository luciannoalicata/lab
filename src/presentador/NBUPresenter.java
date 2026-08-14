package presentador;

// @author lucianoalicata

import dao.DeterminacionDAO;
import modelo.Determinacion;
import vista.interfaces.IVistaNBU;
import presentador.router.AppRouter;
import java.util.Collections;
import java.util.List;

public class NBUPresenter {

    private final IVistaNBU vnbu;
    private final AppRouter router;
    private final DeterminacionDAO determinacionDAO;
    private String padreActualmenteEnGrilla = ""; 

    public NBUPresenter(IVistaNBU vnbu, AppRouter router, DeterminacionDAO determinacionDAO) {
        this.vnbu = vnbu;
        this.router = router;
        this.determinacionDAO = determinacionDAO;
    }

    public void iniciar() {
        vnbu.setPresenter(this);
        padreActualmenteEnGrilla = "";
        cargarDeterminaciones();
    }

    private void cargarDeterminaciones() {
        List<Determinacion> todosNBU = determinacionDAO.listarTodo();
        vnbu.cargarDeterminaciones(todosNBU);
    }

    public void onVolver() {
        vnbu.detenerEdicionTabla();
        guardarTextosHijos();
        router.irAInicio();
    }

    public void onSeleccionarPadre() {
        vnbu.detenerEdicionTabla();
        
        guardarTextosHijos();
        
        String nuevoPadre = vnbu.getCodigoPadreSeleccionado();

        if (nuevoPadre == null || nuevoPadre.trim().isEmpty()) {
            padreActualmenteEnGrilla = "";
            vnbu.cargarHijos(new java.util.ArrayList<>());
            return;
        }
        
        padreActualmenteEnGrilla = nuevoPadre;
        List<Determinacion> hijos = determinacionDAO.obtenerComponentes(nuevoPadre);
        vnbu.cargarHijos(hijos);
    }

    public void onBuscarNBU() {
        String texto = vnbu.getBusqueda();
        if (texto == null || texto.trim().isEmpty()) {
            cargarDeterminaciones();
        } else {
            List<Determinacion> resultados = determinacionDAO.buscar(texto);
            vnbu.cargarDeterminaciones(resultados);
        }
    }

    public void onAgregarHijo() {
        vnbu.detenerEdicionTabla();
        guardarTextosHijos(); 

        String padre = vnbu.getCodigoPadreSeleccionado();
        if (padre == null || padre.trim().isEmpty()) {
            vnbu.mostrarMensaje("Debe seleccionar una práctica de la tabla para vincularle un componente.");
            return;
        }

        String nombreHijo = vnbu.pedirNombreNuevoComponente();

        if (nombreHijo != null && !nombreHijo.isEmpty()) {
            int sufijo = 1;
            String nuevoCodigoHijo = padre + "." + sufijo;

            while (determinacionDAO.buscarPorCodigo(nuevoCodigoHijo) != null) {
                sufijo++;
                nuevoCodigoHijo = padre + "." + sufijo;
            }

            if (!determinacionDAO.insertarNuevaDeterminacion(nuevoCodigoHijo, nombreHijo)) {
                vnbu.mostrarMensaje("Error en la base de datos al intentar crear el componente.");
                return;
            }

            if (determinacionDAO.vincularHijo(padre, nuevoCodigoHijo)) {
                List<Determinacion> hijosActuales = determinacionDAO.obtenerComponentes(padre);
                determinacionDAO.actualizarPrioridad(nuevoCodigoHijo, hijosActuales.size());
                refrescarAmbasTablasNBU(padre);
            } else {
                vnbu.mostrarMensaje("Error al intentar vincular el componente a la práctica.");
            }
        }
    }

    public void onQuitarHijo() {
        vnbu.detenerEdicionTabla();
        guardarTextosHijos(); 

        String padre = vnbu.getCodigoPadreSeleccionado();
        String hijo = vnbu.getCodigoHijoSeleccionado();

        if (padre == null || padre.trim().isEmpty() || hijo == null || hijo.trim().isEmpty()) {
            vnbu.mostrarMensaje("Debe seleccionar un componente de la tabla de la derecha para quitarlo.");
            return;
        }

        int confirmacion = vnbu.confirmarAccion(
            "¿Está seguro de que desea desvincular el componente '" + hijo + "'?\nEsto NO borrará el código del sistema.",
            "Confirmar"
        );

        if (confirmacion == 0) {
            if (determinacionDAO.desvincularHijo(padre, hijo)) {
                refrescarAmbasTablasNBU(padre);
            } else {
                vnbu.mostrarMensaje("Hubo un error al intentar desvincular el componente.");
            }
        }
    }

    public void onSubirHijo() { moverHijoNBU(-1); }
    public void onBajarHijo() { moverHijoNBU(1); }
    public void onSubirPadre() { moverPadreNBU(-1); }
    public void onBajarPadre() { moverPadreNBU(1); }


    private void guardarTextosHijos() {
        if (padreActualmenteEnGrilla == null || padreActualmenteEnGrilla.isEmpty()) return;

        for (int i = 0; i < vnbu.getCantidadFilas(); i++) {
            String codigoHijo = vnbu.getCodigoHijoFila(i);
            if (codigoHijo == null || codigoHijo.trim().isEmpty()) continue;
            
            String nombreHijo = vnbu.getNombreHijoFila(i);
            String unidad = vnbu.getUnidad(i);
            String ref = vnbu.getReferencia(i);

            if (nombreHijo != null && !nombreHijo.trim().isEmpty()) {
                determinacionDAO.actualizarNombre(codigoHijo, nombreHijo);
            }
            determinacionDAO.actualizarUnidadReferenciaPorCodigo(codigoHijo, unidad, ref);
        }
    }

    private void moverHijoNBU(int direccion) {
        vnbu.detenerEdicionTabla();
        guardarTextosHijos();

        String padre = vnbu.getCodigoPadreSeleccionado();
        String hijoSeleccionado = vnbu.getCodigoHijoSeleccionado();
        int filaActual = vnbu.getIndiceHijoSeleccionado();

        if (padre == null || padre.trim().isEmpty() || hijoSeleccionado == null) return;
        
        if (direccion == -1 && filaActual <= 0) return;
        if (direccion == 1 && filaActual >= vnbu.getCantidadFilas() - 1) return;

        List<Determinacion> hijos = determinacionDAO.obtenerComponentes(padre);
        if (hijos.size() <= 1) return;

        int index = -1;
        for (int i = 0; i < hijos.size(); i++) {
            if (hijos.get(i).getCodigo().equals(hijoSeleccionado)) {
                index = i;
                break;
            }
        }
        if (index == -1) return;

        int nuevaPosicion = index + direccion;
        if (nuevaPosicion < 0 || nuevaPosicion >= hijos.size()) return;

        Collections.swap(hijos, index, nuevaPosicion);

        for (int i = 0; i < hijos.size(); i++) {
            determinacionDAO.actualizarPrioridad(hijos.get(i).getCodigo(), i + 1);
        }

        refrescarAmbasTablasNBU(padre);
        vnbu.seleccionarHijoPorIndice(nuevaPosicion);
    }

    private void moverPadreNBU(int direccion) {
        int filaActual = vnbu.getIndicePadreSeleccionado();
        if (filaActual < 0) return;
        
        int filaDestino = filaActual + direccion;
        if (filaDestino < 0 || filaDestino >= vnbu.getCantidadFilasPadre()) return;
        
        String codActual = vnbu.getCodigoPadreSeleccionado();
        String codDestino = vnbu.getCodigoPadreFila(filaDestino);
        
        if (codActual == null || codActual.isEmpty() || codDestino == null || codDestino.isEmpty()) {
            return;
        }

        Determinacion detActual = determinacionDAO.buscarPorCodigo(codActual);
        Determinacion detDestino = determinacionDAO.buscarPorCodigo(codDestino);
        
        if (detActual != null && detDestino != null) {
            int prioActual = detActual.getPrioridad();
            int prioDestino = detDestino.getPrioridad();
            
            determinacionDAO.actualizarPrioridad(codActual, prioDestino);
            determinacionDAO.actualizarPrioridad(codDestino, prioActual);
            
            refrescarListaPadres();
            vnbu.seleccionarPadrePorIndice(filaDestino);
        }
    }

    private void refrescarAmbasTablasNBU(String codigoPadre) {
        refrescarListaPadres();
        
        if (codigoPadre != null && !codigoPadre.trim().isEmpty()) {
            vnbu.seleccionarFilaPorCodigo(codigoPadre);
            List<Determinacion> hijos = determinacionDAO.obtenerComponentes(codigoPadre);
            vnbu.cargarHijos(hijos);
        }
    }

    private void refrescarListaPadres() {
        List<Determinacion> listaCompleta = determinacionDAO.listarTodo();
        vnbu.cargarDeterminaciones(listaCompleta);
    }
}