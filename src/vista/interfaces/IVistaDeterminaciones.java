package vista.interfaces;

import java.util.List;
import modelo.Determinacion;
import presentador.DeterminacionesPresenter;

// @author lucianoalicata

public interface IVistaDeterminaciones {

    static final String BTN_AGREGAR_DETERMINACION = "agregar_determinacion";
    static final String BTN_CONTINUAR = "cargar_analisis";
    static final String BTN_ELIMINAR ="borrar_determinacion";

    void ejecutar();
    void setPresenter(DeterminacionesPresenter presenter);    
    int confirmarAccion(String mensaje, String titulo);
    void limpiarFocos();
    void cerrarPantalla();
    String getDeterminacion();
    void limpiarCampos();
    void cargarTablaConTitulos(List<Determinacion> lista);
    int[] getFilasSeleccionadas();
    void mostrarSugerencias(List<Determinacion> sugerencias);
    void mostrarMensaje(String mensaje);
}