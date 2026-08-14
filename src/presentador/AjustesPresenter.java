package presentador;

// @author lucianoalicata

import dao.AuditoriaDAO;
import dao.ConfiguracionDAO;
import dao.UsuarioDAO;
import modelo.Usuario;
import presentador.router.AppRouter;
import vista.interfaces.IVistaAjustes;

import java.io.File;
import java.util.prefs.Preferences;

public class AjustesPresenter {

    private final IVistaAjustes va;
    private final ConfiguracionDAO configDAO;
    private final UsuarioDAO usuarioDAO;
    private final AuditoriaDAO auditoriaDAO;
    private final Usuario usuarioLogueado;
    
    private final Preferences prefsLocal;

    public AjustesPresenter(IVistaAjustes va, AppRouter router, ConfiguracionDAO configDAO, 
                            UsuarioDAO usuarioDAO, AuditoriaDAO auditoriaDAO, Usuario usuarioLogueado) {
        this.va = va;
        this.configDAO = configDAO;
        this.usuarioDAO = usuarioDAO;
        this.auditoriaDAO = auditoriaDAO;
        this.usuarioLogueado = usuarioLogueado;
        
        this.prefsLocal = Preferences.userNodeForPackage(AjustesPresenter.class);
    }

    public void iniciar() {
        va.setPresenter(this);
        va.limpiarCampos();
        va.setUsuarioActual(usuarioLogueado.getUsername());
        
        if (!usuarioLogueado.getRol().equals("ADMIN")) {
            va.habilitarSeccionAranceles(false);
        }

        cargarDatosConfiguracion();
        va.ejecutar(); 
    }

    public void onActualizarClave() {
        String claveActual = va.getClaveActual();
        String nuevaClave = va.getNuevaClave();
        String repetirClave = va.getRepetirNuevaClave();

        if (claveActual.isEmpty() || nuevaClave.isEmpty() || repetirClave.isEmpty()) {
            va.mostrarMensaje("Todos los campos son obligatorios.");
            return;
        }

        if (!nuevaClave.equals(repetirClave)) {
            va.mostrarMensaje("La nueva clave no coincide.");
            return;
        }

        if (!usuarioDAO.validarClave(usuarioLogueado.getUsername(), claveActual)) {
            va.mostrarMensaje("La clave actual es incorrecta.");
            return;
        }

        if (usuarioDAO.actualizarClave(usuarioLogueado.getUsername(), nuevaClave)) {
            va.mostrarMensaje("Clave actualizada correctamente.");
            va.limpiarCampos();
        } else {
            va.mostrarMensaje("Error al actualizar la clave.");
        }
    }

    public void onActualizarDatos() {
        configDAO.guardar("lab_nombre", va.getNombreLaboratorio());
        configDAO.guardar("lab_direccion", va.getDireccion());
        configDAO.guardar("lab_localidad", va.getLocalidad());
        configDAO.guardar("lab_telefono", va.getTelefono());
        configDAO.guardar("lab_bioquimico", va.getBioquimico());
        configDAO.guardar("lab_matricula", va.getMatricula());

        String rutaLogo = va.getLogo();
        if (rutaLogo != null && !rutaLogo.isEmpty() && !rutaLogo.equals("(Imagen guardada en Base de Datos)")) {
            File archLogo = new File(rutaLogo);
            if (archLogo.exists()) {
                configDAO.guardarBinario("lab_logo", archLogo);
            }
        }

        String rutaFirma = va.getFirma();
        if (rutaFirma != null && !rutaFirma.isEmpty() && !rutaFirma.equals("(Imagen guardada en Base de Datos)")) {
            File archFirma = new File(rutaFirma);
            if (archFirma.exists()) {
                configDAO.guardarBinario("lab_firma", archFirma);
            }
        }

        prefsLocal.put("ruta_pdf", va.getRutaPdf());
        prefsLocal.put("ruta_backup", va.getRutaBackup());
        
        va.mostrarMensaje("Datos institucionales y rutas locales actualizados correctamente.");
    }

    public void onGuardarConfiguracion() {
        configDAO.guardar("print_tamano", va.getTamanoHoja());
        configDAO.guardar("print_orientacion", va.getOrientacion());
        configDAO.guardar("print_logo", va.isIncluirLogo() ? "true" : "false");
        
        prefsLocal.putBoolean("print_auto", va.isAutoPrint());
        
        va.mostrarMensaje("Preferencias de impresión actualizadas.");
    }

    public void onGuardarUB() {
        if (!usuarioLogueado.getRol().equals("ADMIN")) {
            va.mostrarMensaje("Acceso denegado: Solo administradores pueden cambiar aranceles.");
            return;
        }

        String valorAnterior = configDAO.getValor("valor_ub");
        if (valorAnterior == null || valorAnterior.isEmpty()) valorAnterior = "0"; 

        String nuevoValor = va.getValorUB();
        configDAO.guardar("valor_ub", nuevoValor);
        va.mostrarMensaje("Valor de Unidad Bioquímica actualizado.");

        auditoriaDAO.registrar(
                usuarioLogueado,
                "EDITAR",
                "configuracion",
                0,
                valorAnterior,
                nuevoValor,
                "Cambio de arancel global de UB: de " + valorAnterior + " a " + nuevoValor
        );
    }

    public void onVolver() {
        va.cerrarPantalla();
    }

    private void cargarDatosConfiguracion() {
        va.setNombreLaboratorioACtual(configDAO.getValor("lab_nombre"));
        va.setDireccion(configDAO.getValor("lab_direccion"));
        va.setLocalidad(configDAO.getValor("lab_localidad"));
        va.setTelefono(configDAO.getValor("lab_telefono"));
        va.setBioquimico(configDAO.getValor("lab_bioquimico"));
        va.setMatricula(configDAO.getValor("lab_matricula"));
        
        va.setLogo("(Imagen guardada en Base de Datos)");
        va.setFirma("(Imagen guardada en Base de Datos)");
        
        String rutaDefaultPdf = System.getProperty("user.home") + "\\Desktop\\Informes_BIOTEC";
        String rutaDefaultBackup = System.getProperty("user.home") + "\\Desktop\\Backups_BIOTEC";
        
        va.setRutaPdf(prefsLocal.get("ruta_pdf", rutaDefaultPdf));
        va.setRutaBackup(prefsLocal.get("ruta_backup", rutaDefaultBackup));
        
        String tamano = configDAO.getValor("print_tamano");
        if (tamano != null && !tamano.isEmpty()) va.setTamanoHoja(tamano);
        
        String orient = configDAO.getValor("print_orientacion");
        if (orient != null && !orient.isEmpty()) va.setOrientacion(orient);
        
        va.setIncluirLogo("true".equals(configDAO.getValor("print_logo")));
        
        va.setAutoPrint(prefsLocal.getBoolean("print_auto", false));

        String ub = configDAO.getValor("valor_ub");
        va.setValorUB(ub != null && !ub.isEmpty() ? ub : "0.0");
    }
}