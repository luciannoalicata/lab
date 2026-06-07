package principal;

import dao.DAOFactory;
import modelo.Conexion;
import presentador.SesionPresenter;
import presentador.router.AppRouter;
import vista.VistaFactory;
import vista.interfaces.IVistaLogin;
import vista.interfaces.IVistaPrincipal;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        
        SwingUtilities.invokeLater(() -> {
            
            // 1. Inicializamos la Base de Datos y las Fábricas
            Conexion con = new Conexion();
            DAOFactory daoFactory = new DAOFactory(con);
            VistaFactory vistaFactory = new VistaFactory();

            // 2. Instanciamos las dos vistas principales
            IVistaLogin vl = new vista.swing.VistaLogin();
            IVistaPrincipal vp = new vista.swing.VistaPrincipal();

            // 3. Nace el Router
            // Le pasamos el vp, daoFactory y vistaFactory
            AppRouter router = new AppRouter(vp, daoFactory, vistaFactory);

            // 4. Nace el presentador de sesión
            // Nota: Verifica que este constructor sea el que tiene tu SesionPresenter
            SesionPresenter sesion = new SesionPresenter(
                    vl, 
                    router,
                    daoFactory.getUsuarioDAO()
            );
            
            // 5. Arrancamos el sistema
            sesion.iniciar();
        });
    }
}