package presentador.router;

// @author lucianoalicata

import dao.DAOFactory;
import dao.EstadisticaDAO;
import javax.swing.JPanel;
import modelo.Paciente;
import modelo.Usuario;
import presentador.*;
import servicio.ReporteService;
import vista.VistaFactory;
import vista.interfaces.*;

public class AppRouter {

    private final IVistaPrincipal vp;
    private final DAOFactory daoFactory;
    private final VistaFactory vistaFactory;
    private Usuario usuarioLogueado;
    private final ReporteService reporteService;
    private IVistaDeterminaciones vistaDeterminacionesActual;
    private DeterminacionesPresenter determinacionesPresenterActual;
    private PrincipalPresenter principalPresenter;
    private MedicoPresenter medicoPresenter;
    private PacientePresenter pacientePresenter;
    private ObraSocialPresenter osPresenter;
    private EstadisticasPresenter estadisticasPresenter;
    private HistorialPresenter historialPresenter;
    private DetalleAnalisisPresenter detallePresenter;
    private AnalisisPresenter analisisPresenter;
    private NBUPresenter nbuPresenter;
    private UsuarioPresenter usuarioPresenter;
    private AuditoriaPresenter auditoriaPresenter;
    private AjustesPresenter ajustesPresenter;

    public AppRouter(IVistaPrincipal vp, DAOFactory daoFactory, VistaFactory vistaFactory) {
        this.vp = vp;
        this.daoFactory = daoFactory;
        this.vistaFactory = vistaFactory;
        this.reporteService = new ReporteService(
                daoFactory.getConfigDAO(),
                daoFactory.getAnalisisDAO(),
                daoFactory.getPacienteDAO(),
                daoFactory.getResultadoDAO(),
                daoFactory.getDeterminacionDAO(),
                daoFactory.getMedicoDAO()
        );
    }

    public void irAInicio() {
        vp.desactivarModoInmersion();
        vp.volverInicio();
        vp.limpiarFocos();
    }

    public void irAPacientes() {
        if (pacientePresenter == null) {
            IVistaPaciente vista = vistaFactory.getVistaPaciente();
            vp.registrarPanel(vista, "pacientes");
            pacientePresenter = new PacientePresenter(
                    vista,
                    this,
                    daoFactory.getPacienteDAO(),
                    daoFactory.getAuditoriaDAO(),
                    daoFactory.getObraSocialDAO(),
                    usuarioLogueado
            );
        }
        pacientePresenter.iniciar();

        vp.activarModoInmersion(); 
        vp.mostrarSeccion("pacientes");
        vp.limpiarFocos();
    }

    public void irAMedicos() {
        if (medicoPresenter == null) {
            IVistaMedicos vista = vistaFactory.getVistaMedicos();
            vp.registrarPanel(vista, "medicos");
            medicoPresenter = new MedicoPresenter(vista, this, daoFactory.getMedicoDAO());
        }
        medicoPresenter.iniciar();
        vp.activarModoInmersion();
        vp.mostrarSeccion("medicos");
        vp.limpiarFocos();
    }

    public void irAObrasSociales() {
        if (this.osPresenter == null) {
            vista.interfaces.IVistaObraSocial vistaOS = vistaFactory.getVistaObraSocial();
            vp.registrarPanel(vistaOS, "obras_sociales");

            this.osPresenter = new ObraSocialPresenter(vistaOS, this, daoFactory.getObraSocialDAO());
        }

        this.osPresenter.iniciar();
        vp.activarModoInmersion();
        vp.mostrarSeccion("obras_sociales");
        vp.limpiarFocos();
    }

    public void irANBU() {
        if (nbuPresenter == null) {
            IVistaNBU vista = vistaFactory.getVistaNBU();
            vp.registrarPanel(vista, "nbu");
            nbuPresenter = new NBUPresenter(vista, this, daoFactory.getDeterminacionDAO());
        }
        nbuPresenter.iniciar(); 
        vp.activarModoInmersion();
        vp.mostrarSeccion("nbu");
        vp.limpiarFocos();
    }

    public void irANuevoAnalisis(modelo.Paciente p) {
        if (vistaDeterminacionesActual != null) {
            vistaDeterminacionesActual.cerrarPantalla();
            vistaDeterminacionesActual = null;
        }

        if (determinacionesPresenterActual != null) {
            determinacionesPresenterActual = null;
        }

        vista.interfaces.IVistaDeterminaciones vistaDet = vistaFactory.getVistaDeterminaciones();
        vistaDeterminacionesActual = vistaDet;

        determinacionesPresenterActual = new presentador.DeterminacionesPresenter(
                vistaDet,
                this,
                daoFactory.getDeterminacionDAO(),
                p
        );

        determinacionesPresenterActual.iniciar();

        vp.limpiarFocos();
    }

    public void irAHistorial(Paciente p) {
        IVistaHistorialAnalisis vista = vistaFactory.getVistaHistorialAnalisis();
        vp.registrarPanel(vista, "historial_analisis");

        historialPresenter = new HistorialPresenter(
                vista,
                this,
                daoFactory.getAnalisisDAO(),
                daoFactory.getAuditoriaDAO(),
                this.usuarioLogueado,
                p,
                this.reporteService 
        );

        historialPresenter.iniciar();
        vp.activarModoInmersion();
        vp.mostrarSeccion("historial_analisis");
        vp.limpiarFocos();
    }

    public void irAEstadisticas() {
        // Instanciar la vista y el presenter UNA SOLA VEZ.
        // Esto evita que el CardLayout apile paneles infinitamente y rompa los gráficos.
        if (this.estadisticasPresenter == null) {
            IVistaEstadistica vistaEstadistica = vistaFactory.getVistaEstadistica();
            vp.registrarPanel((JPanel) vistaEstadistica, "estadisticas");

            this.estadisticasPresenter = new EstadisticasPresenter(
                    vistaEstadistica,
                    daoFactory.getEstadisticaDAO(),
                    daoFactory.getObraSocialDAO(),
                    daoFactory.getMedicoDAO(),
                    daoFactory.getDeterminacionDAO(),
                    daoFactory.getResultadoDAO(),
                    this
            );
        }

        // Reutilizamos la instancia que ya está limpia en memoria y en el CardLayout
        this.estadisticasPresenter.iniciar();
        vp.activarModoInmersion();
        vp.mostrarSeccion("estadisticas");
        vp.limpiarFocos();
    }

    public void abrirDetalleAnalisis(int idAnalisis, String origen) {
        if (this.detallePresenter == null) {
            IVistaVerDetalleAnalisis vistaDetalle = vistaFactory.getVistaVerDetalleAnalisis();
            vp.registrarPanel(vistaDetalle, "ver_detalle_analisis");

            this.detallePresenter = new DetalleAnalisisPresenter(
                    vistaDetalle,
                    this,
                    daoFactory.getAnalisisDAO(),
                    daoFactory.getResultadoDAO(),
                    daoFactory.getPacienteDAO(),
                    daoFactory.getDeterminacionDAO(),
                    daoFactory.getAuditoriaDAO(),
                    daoFactory.getConfigDAO(),
                    daoFactory.getMedicoDAO(),
                    this.usuarioLogueado,
                    this.reporteService
            );
        }

        this.detallePresenter.iniciar(idAnalisis, origen);
        vp.mostrarSeccion("ver_detalle_analisis");
        vp.limpiarFocos();
    }

    public void volverAHistorialPaciente(Paciente paciente) {
        cerrarDetalleAnalisis();
        irAHistorial(paciente);
    }

    public void abrirCargaResultados(modelo.Paciente pacienteActual, java.util.List<modelo.Determinacion> listaDeterminaciones) {
        vista.interfaces.IVistaCargarResultados vistaResultados = vistaFactory.getVistaCargarResultados();

        vp.registrarPanel(vistaResultados, "cargar_resultados");

        presentador.ResultadoPresenter resultadoPresenter = new presentador.ResultadoPresenter(
                vistaResultados,
                this,
                pacienteActual,
                listaDeterminaciones,
                this.usuarioLogueado,
                daoFactory.getAnalisisDAO(),
                daoFactory.getResultadoDAO(),
                daoFactory.getObraSocialDAO(),
                daoFactory.getDeterminacionDAO(),
                daoFactory.getConfigDAO(),
                daoFactory.getAuditoriaDAO(),
                daoFactory.getMedicoDAO()
        );

        resultadoPresenter.iniciar();
        vp.mostrarSeccion("cargar_resultados");
        vp.limpiarFocos();
    }

    public void mostrarVistaDetalleAnalisis() {
        vp.mostrarSeccion("ver_detalle_analisis");
        vp.limpiarFocos();
    }

    public void cerrarDetalleAnalisis() {
        vp.mostrarSeccion("lista_analisis");
        vp.limpiarFocos();
    }

    public void volverAPacientesDesdeHistorial() {
        vp.mostrarSeccion("pacientes");
        vp.limpiarFocos();
    }

    public void cerrarDetalleYRefrescarAnalisis() {
        cerrarDetalleAnalisis();
        refrescarVistasAnalisisAbiertas();
    }

    public void refrescarVistasAnalisisAbiertas() {
        if (this.historialPresenter != null) {
            this.historialPresenter.iniciar();
        }
        if (this.analisisPresenter != null) {
            this.analisisPresenter.iniciar();
        }
    }

    public void abrirListadoGlobalAnalisis() {
        if (this.analisisPresenter == null) {
            IVistaAnalisis vistaAnalisis = vistaFactory.getVistaAnalisis();
            vp.registrarPanel(vistaAnalisis, "analisis_global");

            this.analisisPresenter = new AnalisisPresenter(
                    vistaAnalisis,
                    this, 
                    daoFactory.getAnalisisDAO(),
                    this.reporteService, 
                    daoFactory.getAuditoriaDAO(), 
                    this.usuarioLogueado 
            );
        }

        this.analisisPresenter.iniciar();
        vp.activarModoInmersion();
        vp.mostrarSeccion("analisis_global");
        vp.limpiarFocos();
    }

    public void irAUsuarios() {
        if (this.usuarioPresenter == null) {
            vista.interfaces.IVistaGestionUsuarios vistaUsuarios = vistaFactory.getVistaGestionUsuarios();
            vp.registrarPanel(vistaUsuarios, "gestion_usuarios");

            this.usuarioPresenter = new UsuarioPresenter(
                    vistaUsuarios,
                    this,
                    daoFactory.getUsuarioDAO(),
                    this.usuarioLogueado
            );
        }

        this.usuarioPresenter.iniciar();
        vp.activarModoInmersion();
        vp.mostrarSeccion("gestion_usuarios");
        vp.limpiarFocos();
    }

    public void irAAuditoria() {
        if (this.auditoriaPresenter == null) {
            vista.interfaces.IVistaAuditoria vistaAuditoria = vistaFactory.getVistaAuditoria();
            vp.registrarPanel(vistaAuditoria, "auditoria");

            this.auditoriaPresenter = new AuditoriaPresenter(
                    vistaAuditoria,
                    this,
                    daoFactory.getAuditoriaDAO(),
                    daoFactory.getUsuarioDAO()
            );
        }

        this.auditoriaPresenter.iniciar();
        vp.activarModoInmersion();
        vp.mostrarSeccion("auditoria");
        vp.limpiarFocos();
    }

    public void irAAjustes() {
        if (this.ajustesPresenter == null) {
            vista.interfaces.IVistaAjustes vistaAjustes = vistaFactory.getVistaAjustes();

            this.ajustesPresenter = new AjustesPresenter(
                    vistaAjustes,
                    this,
                    daoFactory.getConfigDAO(), 
                    daoFactory.getUsuarioDAO(),
                    daoFactory.getAuditoriaDAO(),
                    this.usuarioLogueado 
            );
        }

        this.ajustesPresenter.iniciar();
        vp.limpiarFocos();
    }

    public void iniciarSesion(Usuario u) {
        this.usuarioLogueado = u;

        this.principalPresenter = new PrincipalPresenter(vp, this, this.usuarioLogueado, daoFactory.getConfigDAO());

        this.principalPresenter.iniciar();

        boolean isAdmin = u.getRol().equals("ADMIN");
        vp.habilitarBotonGestionUsuarios(isAdmin);
        vp.habilitarBotonAuditoria(isAdmin);

        if (u.getRol().equals("LECTOR") || u.getRol().equals("TECNICO")) {
            vp.habilitarBotonAjustes(false);
            vp.habilitarBotonNBU(false);
        }
    }

    public void onLoginExitoso(Usuario u) {
        this.usuarioLogueado = u;

        this.principalPresenter = new PrincipalPresenter(vp, this, this.usuarioLogueado, daoFactory.getConfigDAO());
        vp.desactivarModoInmersion();
        this.principalPresenter.iniciar();

        String rol = u.getRol().toUpperCase();

        switch (rol) {
            case "ADMIN":
                vp.habilitarBotonPacientes(true);
                vp.habilitarBotonAnalisis(true);
                vp.habilitarBotonMedicos(true);
                vp.habilitarBotonObrasSociales(true);
                vp.habilitarBotonNBU(true);
                vp.habilitarBotonAjustes(true);
                vp.habilitarBotonGestionUsuarios(true);
                vp.habilitarBotonAuditoria(true);

                vp.habilitarCargaPacientes(true);
                vp.habilitarCargaAnalisis(true);
                vp.habilitarModificacionRegistros(true);
                break;

            case "BIOQUIMICO":
                vp.habilitarBotonPacientes(true);
                vp.habilitarBotonAnalisis(true);
                vp.habilitarBotonMedicos(true);
                vp.habilitarBotonObrasSociales(true);
                vp.habilitarBotonNBU(true);

                vp.habilitarBotonAjustes(false);
                vp.habilitarBotonGestionUsuarios(false);
                vp.habilitarBotonAuditoria(false);

                vp.habilitarCargaPacientes(true);
                vp.habilitarCargaAnalisis(true);
                vp.habilitarModificacionRegistros(true);
                break;

            case "TECNICO":
                vp.habilitarBotonPacientes(true);
                vp.habilitarBotonAnalisis(true);
                vp.habilitarBotonMedicos(true);
                vp.habilitarBotonObrasSociales(true);

                vp.habilitarBotonNBU(false);
                vp.habilitarBotonAjustes(false);
                vp.habilitarBotonGestionUsuarios(false);
                vp.habilitarBotonAuditoria(false);

                vp.habilitarCargaPacientes(true);        
                vp.habilitarCargaAnalisis(true);          
                vp.habilitarModificacionRegistros(false); 
                break;

            case "LECTOR":
                vp.habilitarBotonPacientes(true);
                vp.habilitarBotonAnalisis(true);        
                vp.habilitarBotonMedicos(true);          
                vp.habilitarBotonObrasSociales(true);  
                vp.habilitarBotonNBU(false);
                vp.habilitarBotonAjustes(false);
                vp.habilitarBotonGestionUsuarios(false);
                vp.habilitarBotonAuditoria(false);
                vp.habilitarCargaPacientes(false);   
                vp.habilitarCargaAnalisis(false);    
                vp.habilitarModificacionRegistros(false);
                break;

            default:
                vp.habilitarBotonPacientes(false);
                vp.habilitarBotonAnalisis(false);
                vp.habilitarBotonMedicos(false);
                vp.habilitarBotonObrasSociales(false);
                vp.habilitarBotonNBU(false);
                vp.habilitarBotonAjustes(false);
                vp.habilitarBotonGestionUsuarios(false);
                vp.habilitarBotonAuditoria(false);
                vp.habilitarCargaPacientes(false);
                vp.habilitarCargaAnalisis(false);
                vp.habilitarModificacionRegistros(false);
                break;
        }
    }

    public void cerrarSesion() {
        this.usuarioLogueado = null;
        this.principalPresenter = null;
        this.pacientePresenter = null;
        this.analisisPresenter = null;
        this.medicoPresenter = null;
        this.osPresenter = null;
        this.historialPresenter = null;
        this.detallePresenter = null;
        this.nbuPresenter = null;
        this.usuarioPresenter = null;
        this.auditoriaPresenter = null;
        this.ajustesPresenter = null;

        vp.volverInicio(); 
        vp.activarModoInmersion();

        vista.interfaces.IVistaLogin vistaLogin = vistaFactory.getVistaLogin();
        presentador.SesionPresenter sesion = new presentador.SesionPresenter(
                vistaLogin,
                this,
                daoFactory.getUsuarioDAO()
        );

        sesion.iniciar();
    }

    public void limpiarReferenciaDeterminaciones() {
        this.vistaDeterminacionesActual = null;
        this.determinacionesPresenterActual = null;
    }
    // En AppRouter.java — agregar este método
public void volverAPacientesDesdeEstadisticas() {
    vp.desactivarModoInmersion();
    vp.mostrarSeccion("pacientes");
    vp.limpiarFocos();
}
}