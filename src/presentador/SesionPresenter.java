package presentador;

import dao.UsuarioDAO;
import modelo.Usuario;
import presentador.router.AppRouter;
import vista.interfaces.IVistaLogin;

public class SesionPresenter {

    private final IVistaLogin vista;
    private final AppRouter router;
    private final UsuarioDAO usuarioDAO;

    public SesionPresenter(IVistaLogin vista, AppRouter router, UsuarioDAO usuarioDAO) {
        this.vista = vista;
        this.router = router;
        this.usuarioDAO = usuarioDAO;
    }

    public void iniciar() {
        vista.setPresenter(this);
        vista.ejecutar();
    }

    public void onIngresar() {
        String username = vista.getUsuario();
        String password = vista.getClave();

        if (username.isEmpty() || password.isEmpty()) {
            vista.mostrarMensaje("Por favor, complete todos los campos.");
            return;
        }

        Usuario u = usuarioDAO.login(username, password); // Asegúrate que sea autenticar o login según tu DAO

        if (u != null) {
            vista.cerrarPantalla();
            // LLAMAMOS AL MÉTODO QUE ESTÁ EN APPROUTER.JAVA
            router.onLoginExitoso(u); 
        } else {
            vista.mostrarMensaje("Usuario o contraseña incorrectos.");
        }
    }
}
