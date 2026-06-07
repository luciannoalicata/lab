package presentador;

import dao.AuditoriaDAO;
import dao.ConfiguracionDAO;
import dao.UsuarioDAO;
import modelo.Usuario;
import presentador.router.AppRouter;
import vista.interfaces.IVistaAjustes;

public class AjustesPresenter {

    private final IVistaAjustes va;
    private final AppRouter router; // El Router maneja la navegación
    private final ConfiguracionDAO configDAO;
    private final UsuarioDAO usuarioDAO;
    private final AuditoriaDAO auditoriaDAO;
    private final Usuario usuarioLogueado;

    public AjustesPresenter(IVistaAjustes va, AppRouter router, ConfiguracionDAO configDAO, 
                            UsuarioDAO usuarioDAO, AuditoriaDAO auditoriaDAO, Usuario usuarioLogueado) {
        this.va = va;
        this.router = router;
        this.configDAO = configDAO;
        this.usuarioDAO = usuarioDAO;
        this.auditoriaDAO = auditoriaDAO;
        this.usuarioLogueado = usuarioLogueado;
    }

    public void iniciar() {
        va.setPresenter(this); // Conectamos la vista
        va.limpiarCampos();
        va.setUsuarioActual(usuarioLogueado.getUsername());
        
        if (!usuarioLogueado.getRol().equals("ADMIN")) {
            va.habilitarSeccionAranceles(false);
        }

        cargarDatosConfiguracion();
        va.ejecutar(); 
    }

    // ════════════════════════════════════════════════════════════════
    //  MÉTODOS EXPLÍCITOS LLAMADOS POR LA VISTA
    // ════════════════════════════════════════════════════════════════

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
        configDAO.guardar("lab_logo", va.getLogo());
        configDAO.guardar("lab_firma", va.getFirma());
        configDAO.guardar("ruta_pdf", va.getRutaPdf());
        configDAO.guardar("ruta_backup", va.getRutaBackup());
        
        va.mostrarMensaje("Datos institucionales guardados correctamente.");
    }

    public void onGuardarConfiguracion() {
        configDAO.guardar("print_tamano", va.getTamanoHoja());
        configDAO.guardar("print_orientacion", va.getOrientacion());
        configDAO.guardar("print_logo", va.isIncluirLogo() ? "true" : "false");
        configDAO.guardar("print_auto", va.isAutoPrint() ? "true" : "false");
        
        va.mostrarMensaje("Preferencias de impresión actualizadas.");
    }

    public void onGuardarUB() {
        if (!usuarioLogueado.getRol().equals("ADMIN")) {
            va.mostrarMensaje("Acceso denegado: Solo administradores pueden cambiar aranceles.");
            return;
        }

        String valorAnterior = configDAO.getValor("valor_ub");
        if (valorAnterior == null) valorAnterior = "0"; 

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

    // ════════════════════════════════════════════════════════════════
    //  LÓGICA PRIVADA
    // ════════════════════════════════════════════════════════════════

    private void cargarDatosConfiguracion() {
        va.setNombreLaboratorioACtual(configDAO.getValor("lab_nombre"));
        va.setDireccion(configDAO.getValor("lab_direccion"));
        va.setLocalidad(configDAO.getValor("lab_localidad"));
        va.setTelefono(configDAO.getValor("lab_telefono"));
        va.setBioquimico(configDAO.getValor("lab_bioquimico"));
        va.setMatricula(configDAO.getValor("lab_matricula"));
        va.setLogo(configDAO.getValor("lab_logo"));
        va.setFirma(configDAO.getValor("lab_firma"));
        va.setRutaPdf(configDAO.getValor("ruta_pdf"));
        va.setRutaBackup(configDAO.getValor("ruta_backup"));
        
        String tamano = configDAO.getValor("print_tamano");
        if (tamano != null) va.setTamanoHoja(tamano);
        
        String orient = configDAO.getValor("print_orientacion");
        if (orient != null) va.setOrientacion(orient);
        
        va.setIncluirLogo("true".equals(configDAO.getValor("print_logo")));
        va.setAutoPrint("true".equals(configDAO.getValor("print_auto")));

        String ub = configDAO.getValor("valor_ub");
        va.setValorUB(ub != null ? ub : "0.0");
    }
}