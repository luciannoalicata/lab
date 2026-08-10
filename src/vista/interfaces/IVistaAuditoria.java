package vista.interfaces;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import modelo.Auditoria;
import presentador.AuditoriaPresenter;

// @author lucianoalicata

public interface IVistaAuditoria {
    
    String BTN_FILTRAR_USUARIO ="filtro_por_usuario";
    String BTN_FILTRAR_FECHA="filtro_por_fecha";
    String BTN_DETALLAR_CAMBIOS="detalle_cambios";
    String BTN_SALIR ="cerrar_auditoria";
    
    void ejecutar();
    void setPresenter(AuditoriaPresenter presenter);    
    int confirmarAccion(String mensaje, String titulo);
    void limpiarFocos();
    void mostrarDetalleCambios(String titulo, String mensaje);
    void mostrarMensaje(String mensaje); 
    Date getFechaSeleccionada();
    void cargarTabla(ArrayList<Auditoria> lista);
    void cargarComboUsuarios(List<String> usuarios);
    String getUsuarioSeleccionado();
    Auditoria getAuditoriaSeleccionada();
    void habilitarBotonDetalle(boolean b);
}