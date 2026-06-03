package vista;

import presentador.Controlador;
import java.util.List;
import modelo.Determinacion;

public interface IVistaDeterminaciones {

    public static final String BTN_AGREGAR_DETERMINACION = "agregar_determinacion";
    public static final String BTN_CONTINUAR = "cargar_analisis";
    public static final String BTN_ELIMINAR ="borrar_determinacion";

    public void ejecutar();
    public void setControlador(Controlador control);
    public String getDeterminacion();
    public void limpiarCampos();
    
    // ── NUEVO MÉTODO ──
    public void cargarTablaConTitulos(List<Determinacion> lista);
    
    public int getFilaSeleccionada();
    public void mostrarSugerencias(List<Determinacion> sugerencias);
    public void mostrarMensaje(String mensaje);
}