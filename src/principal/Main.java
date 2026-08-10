package principal;

// @author lucianoalicata

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
        java.util.Locale.setDefault(java.util.Locale.forLanguageTag("es-AR"));
        javax.swing.UIManager.put("OptionPane.yesButtonText", "Sí");
        javax.swing.UIManager.put("OptionPane.noButtonText", "No");
        javax.swing.UIManager.put("OptionPane.cancelButtonText", "Cancelar");
        javax.swing.UIManager.put("OptionPane.okButtonText", "Aceptar");
        SwingUtilities.invokeLater(() -> {
            
            Conexion con = new Conexion();
            DAOFactory daoFactory = new DAOFactory(con);
            VistaFactory vistaFactory = new VistaFactory();

            IVistaLogin vl = new vista.swing.VistaLogin();
            IVistaPrincipal vp = new vista.swing.VistaPrincipal();
            
            AppRouter router = new AppRouter(vp, daoFactory, vistaFactory);
            SesionPresenter sesion = new SesionPresenter(
                    vl, 
                    router,
                    daoFactory.getUsuarioDAO()
            );
            
            sesion.iniciar();
        });
    }
}