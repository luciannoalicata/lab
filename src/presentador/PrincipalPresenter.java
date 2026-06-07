package presentador;

import modelo.Usuario;
import presentador.router.AppRouter;
import vista.interfaces.IVistaPrincipal;

public class PrincipalPresenter {
    private final IVistaPrincipal vp;
    private final AppRouter router;
    private final Usuario usuarioLogueado;

    public PrincipalPresenter(IVistaPrincipal vp, AppRouter router, Usuario usuarioLogueado) {
        this.vp = vp;
        this.router = router;
        this.usuarioLogueado = usuarioLogueado;
    }

    public void iniciar() {
        System.out.println("DEBUG: PrincipalPresenter iniciando y conectando vista...");
        vp.setPresenter(this);
        vp.setUsuarioLogueado(usuarioLogueado.getUsername());
        
        // Configurar permisos según rol
        boolean isAdmin = usuarioLogueado.getRol().equals("ADMIN");
        boolean isBioquimico = usuarioLogueado.getRol().equals("BIOQUIMICO");
        
        vp.habilitarBotonGestionUsuarios(isAdmin);
        vp.habilitarBotonAuditoria(isAdmin);
        vp.habilitarBotonNBU(isAdmin || isBioquimico);
        
        vp.ejecutar();
        System.out.println("DEBUG: Vista principal ejecutada.");
    }

    // Métodos de navegación
    public void onPacientes() { router.irAPacientes(); }
    public void onAnalisis() { router.abrirListadoGlobalAnalisis(); }
    public void onMedicos() { router.irAMedicos(); }
    public void onObrasSociales() { router.irAObrasSociales(); }
    public void onNBU() { router.irANBU(); }
    public void onAjustes() { router.irAAjustes(); }
    public void onGestionUsuarios() { router.irAUsuarios(); }
    public void onAuditoria() { router.irAAuditoria(); }
    
    public void onCerrarSesion() {
        if(vp.confirmarAccion("¿Desea cerrar la sesión actual?", "Cerrar Sesión") == 0) {
            router.cerrarSesion();
        }
    }
}