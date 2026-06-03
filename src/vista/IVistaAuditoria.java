package vista;

import presentador.Controlador;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import modelo.Auditoria;
import modelo.Usuario;

/**
 *
 * @author luciano
 */
public interface IVistaAuditoria {
    
    
    String BTN_FILTRAR_USUARIO ="filtro_por_usuario";
    String BTN_FILTRAR_FECHA="filtro_por_fecha";
    String BTN_DETALLAR_CAMBIOS="detalle_cambios";
    String BTN_SALIR ="cerrar_auditoria";
    
    public void ejecutar();
    public void setControlador(Controlador control);
    
    public void mostrarMensaje(String mensaje); 
    public Date getFechaSeleccionada();
    public javax.swing.JTable getGrillaAuditoria();
   // public Usuario getUsuario();
    int getFilaSeleccionada();
    
    
void cargarTabla(ArrayList<Auditoria> lista);
void cargarComboUsuarios(List<String> usuarios);
String getUsuarioSeleccionado();
void habilitarBotonDetalle(boolean b);
Auditoria getAuditoriaSeleccionada();
    
    
}
