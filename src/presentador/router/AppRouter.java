package presentador.router;

import dao.DAOFactory;
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
    
    // ── ESTADO GLOBAL ──
    private Usuario usuarioLogueado;
    private ReporteService reporteService;
    
    // En la clase Router
private IVistaDeterminaciones vistaDeterminacionesActual;
private DeterminacionesPresenter determinacionesPresenterActual;
    
    // ── PRESENTADORES ──
    private PrincipalPresenter principalPresenter;
    private MedicoPresenter medicoPresenter;
    private PacientePresenter pacientePresenter;
    private ObraSocialPresenter osPresenter;
    private HistorialPresenter historialPresenter;
    private DetalleAnalisisPresenter detallePresenter;
    private AnalisisPresenter analisisPresenter; 
    private NBUPresenter nbuPresenter;
    private UsuarioPresenter usuarioPresenter;
    private AuditoriaPresenter auditoriaPresenter;
    private AjustesPresenter ajustesPresenter;

    // ── CONSTRUCTOR ──
    public AppRouter(IVistaPrincipal vp, DAOFactory daoFactory, VistaFactory vistaFactory) {
        this.vp = vp;
        this.daoFactory = daoFactory;
        this.vistaFactory = vistaFactory;
        
        // Instanciamos el servicio aquí para prestárselo a los presentadores que lo necesiten
        this.reporteService = new ReporteService(
                daoFactory.getConfigDAO(), 
                daoFactory.getAnalisisDAO(),
                daoFactory.getPacienteDAO(), 
                daoFactory.getResultadoDAO(), 
                daoFactory.getDeterminacionDAO()
        );
    }
    
// ── NAVEGACIÓN PRINCIPAL ─────────────────────────────────────────
    public void irAInicio() {
        vp.desactivarModoInmersion(); // <--- Vuelve a mostrar los menús laterales
        vp.volverInicio();
        vp.limpiarFocos();
    }

    public void irAPacientes() {
        if (pacientePresenter == null) {
            IVistaPaciente vista = vistaFactory.getVistaPaciente();
            vp.registrarPanel(vista, "pacientes");
            // En tu AppRouter.java, dentro del método que abre la VistaPaciente
            pacientePresenter = new PacientePresenter(
                    vista,
                    this,
                    daoFactory.getPacienteDAO(),
                    daoFactory.getAuditoriaDAO(),
                    daoFactory.getObraSocialDAO(), // ← ¡Agrega esta línea!
                    usuarioLogueado
            );
        }
        pacientePresenter.iniciar();
        
        vp.activarModoInmersion(); // <--- ¡LA MAGIA! Oculta los menús laterales
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
    
    // Declaras la variable arriba en tu AppRouter: 
    // private presentador.ObraSocialPresenter osPresenter;

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
    
    public void irANBU(){
        if (nbuPresenter == null) {
            IVistaNBU vista = vistaFactory.getVistaNBU();
            vp.registrarPanel(vista, "nbu");
            nbuPresenter = new NBUPresenter(vista, this, daoFactory.getDeterminacionDAO());
        }
        nbuPresenter.iniciar(); // <--- ¡AQUÍ ESTABA EL ERROR! Decía medicoPresenter.iniciar()
        vp.activarModoInmersion();
        vp.mostrarSeccion("nbu");
        vp.limpiarFocos();
    }

    // ── FLUJO DE ANÁLISIS E HISTORIAL ────────────────────────────────
    public void irANuevoAnalisis(modelo.Paciente p) {
        // 1. Limpieza 100% MVP (Sin clases de Java Swing)
        if (vistaDeterminacionesActual != null) {
            vistaDeterminacionesActual.cerrarPantalla(); 
            vistaDeterminacionesActual = null;
        }
        
        if (determinacionesPresenterActual != null) {
            determinacionesPresenterActual = null;
        }

        // 2. Instanciar nueva vista y presentador
        vista.interfaces.IVistaDeterminaciones vistaDet = vistaFactory.getVistaDeterminaciones();
        vistaDeterminacionesActual = vistaDet;

        determinacionesPresenterActual = new presentador.DeterminacionesPresenter(
                vistaDet,
                this,
                daoFactory.getDeterminacionDAO(),
                p
        );

        // 3. Ejecutar (Al ser JDialog Modal, el hilo se pausa aquí hasta cerrarse)
        determinacionesPresenterActual.iniciar();
        
        vp.limpiarFocos();
    }

    public void irAHistorial(Paciente p) {
        // Siempre recreamos la vista/presentador para asegurarnos de que cargue el paciente correcto
        IVistaHistorialAnalisis vista = vistaFactory.getVistaHistorialAnalisis();
        vp.registrarPanel(vista, "historial_analisis");
        
        historialPresenter = new HistorialPresenter( 
                vista, 
                this, 
                daoFactory.getAnalisisDAO(), 
                daoFactory.getAuditoriaDAO(), 
                this.usuarioLogueado, 
                p,
                this.reporteService // <--- ¡AQUÍ ESTÁ LA PIEZA QUE FALTABA!
        );
        
        historialPresenter.iniciar();
        vp.activarModoInmersion();
        vp.mostrarSeccion("historial_analisis");
        vp.limpiarFocos();
    }

    public void abrirDetalleAnalisis(int idAnalisis) {
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

        this.detallePresenter.iniciar(idAnalisis);

        // 👇 IMPORTANTE: Mostrar la sección de detalle
        vp.mostrarSeccion("ver_detalle_analisis");
        vp.limpiarFocos();
    }

    public void abrirCargaResultados(modelo.Paciente pacienteActual, java.util.List<modelo.Determinacion> listaDeterminaciones) {
        vista.interfaces.IVistaCargarResultados vistaResultados = vistaFactory.getVistaCargarResultados();

        // 3. Registramos el panel
        vp.registrarPanel(vistaResultados, "cargar_resultados");

        // 4. Instanciamos el Presentador
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

        // 5. Iniciar y mostrar
        resultadoPresenter.iniciar();
        vp.mostrarSeccion("cargar_resultados");
        vp.limpiarFocos();
    }

    // ── RUTINAS DE COORDINACIÓN (Cerrar pantallas y refrescar) ───────
    
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
        // Coordinador: Si hay pantallas abiertas mostrando datos que acaban de cambiar, las recarga.
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
            
            // Le inyectamos el reporteService que ya vive en el Router
            this.analisisPresenter = new AnalisisPresenter(
                    vistaAnalisis, 
                    this, 
                    daoFactory.getAnalisisDAO(), 
                    this.reporteService 
            );
        }
        
        this.analisisPresenter.iniciar();
        vp.activarModoInmersion();
        vp.mostrarSeccion("analisis_global");
        vp.limpiarFocos();
    }
    // Declaras la variable arriba en tu AppRouter:
    // private UsuarioPresenter usuarioPresenter;

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
    
    // Declaras la variable arriba en tu AppRouter:
    // private AuditoriaPresenter auditoriaPresenter;

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
                    daoFactory.getConfigDAO(), // <--- Faltaba este
                    daoFactory.getUsuarioDAO(),
                    daoFactory.getAuditoriaDAO(),
                    this.usuarioLogueado              // <--- Faltaba este
            );
        }
        
        this.ajustesPresenter.iniciar();
        
        vp.limpiarFocos();
    }

    public void iniciarSesion(Usuario u) {
        this.usuarioLogueado = u;
        
        // 1. Instanciamos el presentador
        this.principalPresenter = new PrincipalPresenter(vp, this, this.usuarioLogueado);
        
        // 2. Arrancamos: esto ejecuta vp.setPresenter(this) Y vp.ejecutar()
        this.principalPresenter.iniciar(); 
        
        // 3. QUITAMOS el vp.ejecutar() que estaba dentro de onLoginExitoso
        // porque ya se ejecutó en principalPresenter.iniciar()
        // Solo dejamos la lógica de permisos:
        boolean isAdmin = u.getRol().equals("ADMIN");
        vp.habilitarBotonGestionUsuarios(isAdmin);
        vp.habilitarBotonAuditoria(isAdmin);
        
        if (u.getRol().equals("LECTOR") || u.getRol().equals("TECNICO")) {
            vp.habilitarBotonAjustes(false);
            vp.habilitarBotonNBU(false);
        }
    }
    public void onLoginExitoso(Usuario u) {
        // 1. Guardamos el usuario
        this.usuarioLogueado = u;
        
        // 2. Instanciamos el presentador principal
        this.principalPresenter = new PrincipalPresenter(vp, this, this.usuarioLogueado);
        
        // 3. Lo arrancamos (aquí es donde se hace el setPresenter de la vista principal)
        this.principalPresenter.iniciar();
        
        // 4. Configuración de permisos de botones
        boolean isAdmin = u.getRol().equals("ADMIN");
        vp.habilitarBotonGestionUsuarios(isAdmin);
        vp.habilitarBotonAuditoria(isAdmin);
        
        if (u.getRol().equals("LECTOR") || u.getRol().equals("TECNICO")) {
            vp.habilitarBotonAjustes(false);
            vp.habilitarBotonNBU(false);
        }
    }

    public void cerrarSesion() {
        // 1. Limpiamos la memoria de la sesión
        this.usuarioLogueado = null;
        
        // 2. Destruimos el presentador principal para que al volver a entrar
        // se recalculen los permisos (botones visibles) del nuevo usuario.
        this.principalPresenter = null; 
        
        // 3. Limpiamos todos los presentadores en caché por seguridad de datos
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

        // 4. Preparamos la Vista Principal de fondo ocultando datos sensibles
        vp.volverInicio(); // Vuelve a la pantalla del logo
        vp.activarModoInmersion(); // Oculta las barras laterales y el botón de cerrar sesión
        
        // 5. Lanzamos la pantalla de Login nuevamente
        vista.interfaces.IVistaLogin vistaLogin = vistaFactory.getVistaLogin();
        presentador.SesionPresenter sesion = new presentador.SesionPresenter(
                vistaLogin, 
                this, 
                daoFactory.getUsuarioDAO()
        );
        
        // Al ser un JDialog modal, esto bloqueará el sistema hasta que se logueen de nuevo
        sesion.iniciar();
    }

    public void limpiarReferenciaDeterminaciones() {
    this.vistaDeterminacionesActual = null;
    this.determinacionesPresenterActual = null;
}

}