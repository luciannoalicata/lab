package presentador;

import dao.ConfiguracionDAO; // <-- Importación necesaria
import modelo.Usuario;
import presentador.router.AppRouter;
import vista.interfaces.IVistaPrincipal;
import servicio.BackupService;
import java.io.File;
import javax.swing.SwingUtilities;

public class PrincipalPresenter {

    private final IVistaPrincipal vp;
    private final AppRouter router;
    private final Usuario usuarioLogueado;
    private final ConfiguracionDAO configuracionDAO; // <-- Declaración agregada
    private boolean permisoModificacion;
    private boolean permisoCargaPacientes;
    private boolean permisoCargaAnalisis;

    // <-- Constructor actualizado para recibir el DAO
    public PrincipalPresenter(IVistaPrincipal vp, AppRouter router, Usuario usuarioLogueado, ConfiguracionDAO configuracionDAO) {
        this.vp = vp;
        this.router = router;
        this.usuarioLogueado = usuarioLogueado;
        this.configuracionDAO = configuracionDAO;
    }

    public void iniciar() {
        vp.setPresenter(this);

        vp.setUsuarioLogueado(usuarioLogueado.getUsername(), usuarioLogueado.getRol());

        // ── CONTROL DE ACCESOS SEGÚN ROL (RBAC) ──
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

    // ════════════════════════════════════════════════════════════════
    //  EVENTOS DE NAVEGACIÓN
    // ════════════════════════════════════════════════════════════════
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

    // ════════════════════════════════════════════════════════════════
    //  CIERRE Y BACKUPS AUTOMÁTICOS
    // ════════════════════════════════════════════════════════════════
    public void onCerrarSesion() {
        int confirmacion = vp.confirmarAccion("¿Está seguro de que desea cerrar sesión?", "Cerrar Sesión");
        if (confirmacion == 0) {
            ejecutarBackupYSalir(false); // false = Solo cierra sesión, vuelve al login
        }
    }

    public void onCerrarAplicacionCompleta() {
        int confirmacion = vp.confirmarAccion("¿Está seguro de que desea salir completamente del sistema?", "Salir del Sistema");
        if (confirmacion == 0) {
            ejecutarBackupYSalir(true); // true = Apaga el programa completo
        }
    }

    private void ejecutarBackupYSalir(boolean salirCompletamente) {
        vp.mostrarAvisoBackup(true);

        // 1. Obtenemos la ruta configurada dinámicamente desde la Base de Datos
        String rutaConfigurada = configuracionDAO.getValor("ruta_backup"); 
        
        String rutaDirectorio;

        // 2. Red de seguridad: Si el usuario borró la ruta en ajustes o está vacía, usamos Mis Documentos por defecto
        if (rutaConfigurada == null || rutaConfigurada.trim().isEmpty()) {
            String rutaMisDocumentos = javax.swing.filechooser.FileSystemView.getFileSystemView().getDefaultDirectory().getPath();
            rutaDirectorio = rutaMisDocumentos + java.io.File.separator + "BIOTEC_Backups_Default";
        } else {
            // Usamos EXACTAMENTE la ruta que el usuario guardó en Ajustes
            rutaDirectorio = rutaConfigurada; 
        }

        java.io.File dir = new java.io.File(rutaDirectorio);
        if (!dir.exists()) {
            dir.mkdirs(); // Si la carpeta definida en Ajustes no existe en el disco, la crea.
        }

        // 3. Ejecutamos el backup en un Hilo separado
        new Thread(() -> {
            boolean backupExitoso = servicio.BackupService.crearBackup(rutaDirectorio);

            // 4. Volvemos al hilo visual de Swing para cerrar las cosas
            SwingUtilities.invokeLater(() -> {
                vp.mostrarAvisoBackup(false); // Ocultamos el cartel

                if (!backupExitoso) {
                    vp.mostrarMensaje("Atención: Ocurrió un error al generar la copia de seguridad automática.");
                }

                if (salirCompletamente) {
                    System.exit(0); 
                } else {
                    vp.cerrarPantalla();
                    router.cerrarSesion(); // Vuelve al login
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