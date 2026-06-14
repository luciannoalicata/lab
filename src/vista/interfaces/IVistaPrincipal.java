package vista.interfaces;

import presentador.PrincipalPresenter;

/**
 * @author luciano
 */
public interface IVistaPrincipal {
    
    String BTN_PACIENTES = "seccion_pacientes";
    String BTN_ANALISIS = "seccion_analisis";
    String BTN_MEDICOS = "seccion_medicos";
    String BTN_OBRAS_SOCIALES = "seccion_obras_sociales";
    String BTN_AJUSTES = "seccion_ajustes";
    String BTN_CERRAR_SESION = "cerrar_programa";
    String BTN_NBU = "seccion_nbu";
    String BTN_AUDITORIA = "abrir_auditoria";
    String BTN_GESTION_USUARIOS = "abrir_gestion_usuarios";

    void ejecutar();
    void setPresenter(PrincipalPresenter presenter);    
    int confirmarAccion(String mensaje, String titulo);
    void limpiarFocos();
    void setUsuarioLogueado(String nombreUsuario, String rol);
    void mostrarAvisoBackup(boolean mostrar);
    void cerrarPantalla(); 
    void registrarPanel(Object vista, String nombre); 
    void mostrarSeccion(String nombre);
    void volverInicio();
    void activarModoInmersion();
    void desactivarModoInmersion();
    void habilitarBotonPacientes(boolean b);
    void habilitarBotonMedicos(boolean b);
    void habilitarBotonObrasSociales(boolean b);
    void habilitarBotonAnalisis(boolean b);
    void habilitarBotonNBU(boolean b);
    void habilitarBotonAjustes(boolean b);
    void habilitarBotonGestionUsuarios(boolean b);
    void habilitarBotonAuditoria(boolean b);
    void mostrarMensaje(String mensaje);
    void habilitarCargaPacientes(boolean b);
    void habilitarCargaAnalisis(boolean b);
    void habilitarModificacionRegistros(boolean b);
}
