package presentador;

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
    private boolean permisoModificacion;
    private boolean permisoCargaPacientes;
    private boolean permisoCargaAnalisis;

    public PrincipalPresenter(IVistaPrincipal vp, AppRouter router, Usuario usuarioLogueado) {
        this.vp = vp;
        this.router = router;
        this.usuarioLogueado = usuarioLogueado;
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
        // 1. Mostramos el cartel de espera para que el usuario no toque nada
        vp.mostrarAvisoBackup(true);

        // 2. Definimos dónde se guardará (ej: /home/tu_usuario/Documentos/BiotecBackups)
        String rutaDirectorio = System.getProperty("user.home") + "/Documentos/BiotecBackups";
        File dir = new File(rutaDirectorio);
        if (!dir.exists()) {
            dir.mkdirs(); // Crea la carpeta si es la primera vez
        }

        // 3. Ejecutamos el backup en un Hilo separado para no congelar la pantalla visual
        new Thread(() -> {
            boolean backupExitoso = BackupService.crearBackup(rutaDirectorio);

            // 4. Volvemos al hilo visual de Swing para cerrar las cosas
            SwingUtilities.invokeLater(() -> {
                vp.mostrarAvisoBackup(false); // Ocultamos el cartel

                if (!backupExitoso) {
                    vp.mostrarMensaje("Atención: Ocurrió un error al generar la copia de seguridad automática.");
                }

                if (salirCompletamente) {
                    System.exit(0); // Apaga la JVM
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
