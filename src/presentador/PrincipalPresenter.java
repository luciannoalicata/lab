package presentador;

// @author lucianoalicata

import dao.ConfiguracionDAO; 
import modelo.Usuario;
import presentador.router.AppRouter;
import vista.interfaces.IVistaPrincipal;
import javax.swing.SwingUtilities;

public class PrincipalPresenter {

    private final IVistaPrincipal vp;
    private final AppRouter router;
    private final Usuario usuarioLogueado;
    private final ConfiguracionDAO configuracionDAO; 
    private boolean permisoModificacion;
    private boolean permisoCargaPacientes;
    private boolean permisoCargaAnalisis;

    public PrincipalPresenter(IVistaPrincipal vp, AppRouter router, Usuario usuarioLogueado, ConfiguracionDAO configuracionDAO) {
        this.vp = vp;
        this.router = router;
        this.usuarioLogueado = usuarioLogueado;
        this.configuracionDAO = configuracionDAO;
    }

    public void iniciar() {
        vp.setPresenter(this);

        vp.setUsuarioLogueado(usuarioLogueado.getUsername(), usuarioLogueado.getRol());

        String rol = usuarioLogueado.getRol().toUpperCase();

        boolean isAdmin = rol.equals("ADMIN") || rol.equals("ADMINISTRADOR");
        boolean isBioquimico = rol.equals("BIOQUIMICO") || rol.equals("BIOQUÍMICO");
        boolean isTecnico = rol.equals("TECNICO") || rol.equals("TÉCNICO");
        boolean isLector = rol.equals("LECTOR");

        vp.habilitarBotonPacientes(true);
        vp.habilitarBotonAnalisis(true);
        vp.habilitarBotonMedicos(true);
        vp.habilitarBotonObrasSociales(true);

        vp.habilitarBotonNBU(isAdmin || isBioquimico);

        vp.habilitarBotonGestionUsuarios(isAdmin);
        vp.habilitarBotonAuditoria(isAdmin);
        vp.habilitarBotonAjustes(isAdmin);

    vp.ejecutar();
    }

    public void onPacientes() {
        router.irAPacientes();
    }

    public void onAnalisis() {
        router.abrirListadoGlobalAnalisis();
    }

    public void onMedicos() {
        router.irAMedicos();
    }

    public void onObrasSociales() {
        router.irAObrasSociales();
    }

    public void onNBU() {
        router.irANBU();
    }

    public void onAjustes() {
        router.irAAjustes();
    }

    public void onGestionUsuarios() {
        router.irAUsuarios();
    }

    public void onAuditoria() {
        router.irAAuditoria();
    }
    
    public void onEstadisticas() {
        router.irAEstadisticas();
    }

    public void onCerrarSesion() {
        int confirmacion = vp.confirmarAccion("¿Está seguro de que desea cerrar sesión?", "Cerrar Sesión");
        if (confirmacion == 0) {
            ejecutarBackupYSalir(false); 
        }
    }

    public void onCerrarAplicacionCompleta() {
        int confirmacion = vp.confirmarAccion("¿Está seguro de que desea salir del sistema?", "Salir del Sistema");
        if (confirmacion == 0) {
            ejecutarBackupYSalir(true); 
        }
    }

    private void ejecutarBackupYSalir(boolean salirCompletamente) {
        vp.mostrarAvisoBackup(true);

        String rutaConfigurada = configuracionDAO.getValor("ruta_backup"); 
        
        String rutaDirectorio;

        if (rutaConfigurada == null || rutaConfigurada.trim().isEmpty()) {
            String rutaMisDocumentos = javax.swing.filechooser.FileSystemView.getFileSystemView().getDefaultDirectory().getPath();
            rutaDirectorio = rutaMisDocumentos + java.io.File.separator + "BIOTEC_Backups_Default";
        } else {
            rutaDirectorio = rutaConfigurada; 
        }

        java.io.File dir = new java.io.File(rutaDirectorio);
        if (!dir.exists()) {
            dir.mkdirs(); 
        }

        new Thread(() -> {
            boolean backupExitoso = servicio.BackupService.crearBackup(rutaDirectorio);

            SwingUtilities.invokeLater(() -> {
                vp.mostrarAvisoBackup(false); 

                if (!backupExitoso) {
                    vp.mostrarMensaje("Atención: Ocurrió un error al generar la copia de seguridad automática.");
                }

                if (salirCompletamente) {
                    System.exit(0); 
                } else {
                    vp.cerrarPantalla();
                    router.cerrarSesion();
                }
            });
        }).start();
    }

    public void setPermisoCargaPacientes(boolean permiso) {
        this.permisoCargaPacientes = permiso;
    }

    public boolean tienePermisoCargaPacientes() {
        return permisoCargaPacientes;
    }

    public void setPermisoCargaAnalisis(boolean permiso) {
        this.permisoCargaAnalisis = permiso;
    }

    public boolean tienePermisoCargaAnalisis() {
        return permisoCargaAnalisis;
    }

    public void setPermisoModificacion(boolean permiso) {
        this.permisoModificacion = permiso;
    }

    public boolean tienePermisoModificacion() {
        return permisoModificacion;
    }
}