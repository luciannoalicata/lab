package presentador;

import dao.UsuarioDAO;
import presentador.router.AppRouter;
import vista.interfaces.IVistaLogin;

public class SesionPresenter {

    private final IVistaLogin vista;
    private final AppRouter router;
    private final UsuarioDAO usuarioDAO;
    private boolean ingresando = false; 

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
        if (ingresando) {
            return;
        }
        ingresando = true;

        try {
            String username = vista.getUsuario();
            String password = vista.getClave();

            if (username.isEmpty() || password.isEmpty()) {
                vista.mostrarMensaje("Por favor, complete todos los campos.");
                return;
            }

            // El sistema ahora valida estrictamente consultando al DAO de la Base de Datos
            modelo.Usuario u = usuarioDAO.login(username, password);

            if (u != null) {
                vista.cerrarPantalla();
                router.onLoginExitoso(u);
            } else {
                vista.mostrarMensaje("Usuario o contraseña incorrectos.");
            }
        } finally {
            ingresando = false;
        }
    }
}