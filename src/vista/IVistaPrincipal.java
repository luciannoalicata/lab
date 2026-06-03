package vista;

/**
 * @author luciano
 */
public interface IVistaPrincipal {
    
    public void ejecutar();
    public void setControlador(presentador.Controlador control);
    
    public static final String BTN_PACIENTES = "seccion_pacientes";
    public static final String BTN_ANALISIS = "seccion_analisis";
    public static final String BTN_MEDICOS = "seccion_medicos";
    public static final String BTN_OBRAS_SOCIALES = "seccion_obras_sociales";
    public static final String BTN_AJUSTES = "seccion_ajustes";
    public static final String BTN_CERRAR_SESION = "cerrar_programa";
    public static final String BTN_NBU = "seccion_nbu";
    public static final String BTN_AUDITORIA = "abrir_auditoria";
    public static final String BTN_GESTION_USUARIOS = "abrir_gestion_usuarios";

    void registrarPanel(javax.swing.JPanel panel, String nombre);

    void mostrarSeccion(String nombre);

    void volverInicio();
    
    void activarModoInmersion();
    void desactivarModoInmersion();

    void habilitarBotonPacientes(boolean b);
    void habilitarBotonMedicos(boolean b);
    void habilitarBotonObrasSociales(boolean b);
public void habilitarBotonAnalisis(boolean b);
    void habilitarBotonNBU(boolean b);
    void habilitarBotonAjustes(boolean b);
    void habilitarBotonGestionUsuarios(boolean b);
    void habilitarBotonAuditoria(boolean b); // Nuevo
    public void mostrarMensaje(String mensaje);
}
