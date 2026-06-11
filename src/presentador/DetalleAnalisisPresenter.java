package presentador;

import dao.AnalisisDAO;
import dao.AuditoriaDAO;
import dao.ConfiguracionDAO;
import dao.DeterminacionDAO;
import dao.MedicoDAO;
import dao.PacienteDAO;
import dao.ResultadoAnalisisDAO;
import java.util.ArrayList;
import java.util.List;
import modelo.Analisis;
import modelo.Determinacion;
import modelo.Paciente;
import modelo.ResultadoAnalisis;
import modelo.Usuario;
import servicio.ReporteService;
import presentador.router.AppRouter;
import vista.interfaces.IVistaVerDetalleAnalisis;

public class DetalleAnalisisPresenter {

    private final IVistaVerDetalleAnalisis vvda;
    private final AppRouter router; 
    private final AnalisisDAO analisisDAO;
    private final ResultadoAnalisisDAO resultadoDAO;
    private final PacienteDAO pacienteDAO;
    private final DeterminacionDAO determinacionDAO;
    private final AuditoriaDAO auditoriaDAO;
    private final ConfiguracionDAO configDAO;
    private final MedicoDAO medicoDAO;
    private final Usuario usuarioLogueado;
    
    // El servicio de impresión se inyecta por constructor
    private final ReporteService reporteService; 

    private int idAnalisisActual;
    private Paciente pacienteActual;

    public DetalleAnalisisPresenter(IVistaVerDetalleAnalisis vvda, AppRouter router, 
                                    AnalisisDAO analisisDAO, ResultadoAnalisisDAO resultadoDAO, 
                                    PacienteDAO pacienteDAO, DeterminacionDAO determinacionDAO, 
                                    AuditoriaDAO auditoriaDAO, ConfiguracionDAO configDAO, 
                                    MedicoDAO medicoDAO, Usuario usuarioLogueado,
                                    ReporteService reporteService) {
        this.vvda = vvda;
        this.router = router;
        this.analisisDAO = analisisDAO;
        this.resultadoDAO = resultadoDAO;
        this.pacienteDAO = pacienteDAO;
        this.determinacionDAO = determinacionDAO;
        this.auditoriaDAO = auditoriaDAO;
        this.configDAO = configDAO;
        this.medicoDAO = medicoDAO;
        this.usuarioLogueado = usuarioLogueado;
        this.reporteService = reporteService;
    }

    public void iniciar(int idAnalisis) {
        this.idAnalisisActual = idAnalisis;
        
        // Conectamos la vista con el presentador
        vvda.setPresenter(this); 

        Analisis analisis = analisisDAO.buscarPorId(idAnalisisActual);
        if (analisis == null) return;

        this.pacienteActual = pacienteDAO.buscarPorId(analisis.getIdPaciente());

        // Permisos LECTOR
        if (usuarioLogueado.getRol().equals("LECTOR")) {
            vvda.habilitarBotonGuardar(false);
            vvda.habilitarBotonEliminar(false);
            vvda.bloquearMedicoSolicitante();
            vvda.habilitarBotonImprimir(true);
            vvda.bloquearEdicionTabla();
        }

        // Carga de datos
        String nombreC = (pacienteActual != null) ? (pacienteActual.getApellido() + " " + pacienteActual.getNombre()) : "Desconocido";
        vvda.setNombrePaciente(nombreC);
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
        vvda.setFechaAnalisis(sdf.format(analisis.getFecha()));
        vvda.setFechaInforme(analisis.getFecha());
        vvda.setIdAnalisis(idAnalisisActual);
        vvda.setMedicoSolicitante(analisis.getMedicoSolicitante());

        cargarTablaConTitulos();
        
        // El Router se encarga de mostrar la vista
        router.mostrarVistaDetalleAnalisis();
    }

    private void cargarTablaConTitulos() {
        List<ResultadoAnalisis> originales = resultadoDAO.listarPorAnalisis(idAnalisisActual);
        List<ResultadoAnalisis> conTitulos = inyectarTitulosEnResultados(originales);
        vvda.cargarResultadosDetalle((ArrayList<ResultadoAnalisis>) conTitulos);
    }

    // ── MÉTODOS EXPLÍCITOS LLAMADOS POR LA VISTA ──

    public void onEditar() {
        vvda.detenerEdicionTabla();
        String nuevoMedico = vvda.getMedicoSolicitante();
        boolean okRes = true;
        StringBuilder cambios = new StringBuilder();

        for (int i = 0; i < vvda.getCantidadFilas(); i++) {
            int idR = vvda.getIdResultado(i);
            if (idR == -1) continue;
            
            String valR = vvda.getResultadoEditado(i);
            ResultadoAnalisis rViejo = resultadoDAO.buscarPorId(idR);
            
            if (rViejo != null && !rViejo.getResultado().equals(valR)) {
                cambios.append(rViejo.getNombrePrueba()).append(": ").append(rViejo.getResultado()).append(" -> ").append(valR).append("\n");
            }
            if (!resultadoDAO.actualizarResultado(idR, valR)) {
                okRes = false;
            }
        }

        boolean okMed = analisisDAO.actualizarMedico(idAnalisisActual, nuevoMedico);

        if (okRes && okMed) {
            if (cambios.length() > 0) {
                auditoriaDAO.registrar(usuarioLogueado, "EDITAR", "resultado", idAnalisisActual,
                        "Valores anteriores", cambios.toString(), "Edición de resultados del análisis ID: " + idAnalisisActual);
            }
            vvda.mostrarMensaje("Cambios guardados con éxito.");
            router.refrescarVistasAnalisisAbiertas(); // Le decimos al Router que actualice el fondo
        }
    }

    public void onEliminarFila() {
        int filaSel = vvda.getGrilla().getSelectedRow();
        if (filaSel == -1) {
            vvda.mostrarMensaje("Debe seleccionar una fila para eliminar.");
            return;
        }

        int idResEliminar = vvda.getIdResultado(filaSel);
        ResultadoAnalisis rEliminar = resultadoDAO.buscarPorId(idResEliminar);
        if (rEliminar == null) return;

        List<ResultadoAnalisis> restantes = resultadoDAO.listarIncluidosPorAnalisis(idAnalisisActual);

        // CASO A: ÚLTIMA FILA
        if (restantes.size() == 1) {
            int confirm = vvda.confirmarAccion(
                    "Está a punto de eliminar la última determinación de este estudio.\nSi continúa, EL ANÁLISIS COMPLETO SERÁ ELIMINADO de la base de datos.\n¿Desea continuar?", 
                    "Advertencia Crítica");
            
            if (confirm == 0) {
                String infoEliminada = "Prueba: " + rEliminar.getNombrePrueba() + " | Valor: " + rEliminar.getResultado();
                resultadoDAO.eliminarResultado(idResEliminar);
                analisisDAO.eliminar(idAnalisisActual);
                auditoriaDAO.registrar(usuarioLogueado, "ELIMINAR", "analisis", idAnalisisActual, "Análisis completo eliminado al vaciarse: " + infoEliminada, "REGISTRO ELIMINADO", "Eliminación en cascada");
                
                vvda.mostrarMensaje("Análisis eliminado por completo al quedarse sin determinaciones.");
                router.cerrarDetalleYRefrescarAnalisis();
            }
        } 
        // CASO B: QUEDAN MÁS FILAS
        else {
            int confirm = vvda.confirmarAccion(
                    "¿Está seguro de eliminar la determinación: " + rEliminar.getNombrePrueba() + "?\nEl precio del estudio se recalculará automáticamente.", 
                    "Confirmar eliminación");

            if (confirm == 0) {
                Determinacion detOriginal = determinacionDAO.buscarPorCodigo(rEliminar.getCodigo());
                Analisis analisisActual = analisisDAO.buscarPorId(idAnalisisActual);
                String valorUBConfig = configDAO.getValor("valor_ub");
                double factorDinero = (valorUBConfig != null) ? Double.parseDouble(valorUBConfig) : 1600.0;

                if (detOriginal != null && analisisActual != null) {
                    double montoARestar = detOriginal.getUb() * factorDinero;
                    double nuevoPrecio = Math.max(0, analisisActual.getPrecio() - montoARestar);
                    analisisDAO.actualizarPrecio(idAnalisisActual, nuevoPrecio);
                }

                String infoEliminada = "Prueba: " + rEliminar.getNombrePrueba() + " | Valor: " + rEliminar.getResultado();
                if (resultadoDAO.eliminarResultado(idResEliminar)) {
                    auditoriaDAO.registrar(usuarioLogueado, "ELIMINAR", "resultado", idResEliminar, infoEliminada, "REGISTRO ELIMINADO", "Precio actualizado por eliminación");
                    cargarTablaConTitulos(); 
                    router.refrescarVistasAnalisisAbiertas(); 
                    vvda.mostrarMensaje("Fila eliminada y precio actualizado.");
                }
            }
        }
    }

    public void onImprimir() {
        java.util.Date fechaImpresion = vvda.getFechaSeleccionada();

        // El Presentador imprime usando el servicio limpio
        reporteService.generarInforme(idAnalisisActual, fechaImpresion);

        // Actualizamos estado y auditamos
        if (analisisDAO.cambiarEstadoGenerado(idAnalisisActual)) {
            auditoriaDAO.registrar(this.usuarioLogueado, "IMPRIMIR", "analisis", idAnalisisActual, 
                    null, "Informe generado", "El usuario imprimió desde la vista de Detalles.");
            router.refrescarVistasAnalisisAbiertas();
        }
    }

    public void onVolver() {
        router.abrirListadoGlobalAnalisis();
    }

    public void onSeleccionarAnalisis() {
        // La vista ya deshabilita/habilita botones según la selección. 
        // Aquí puedes agregar lógica adicional de negocio si hace falta.
    }

    public void onBuscarSugerenciasMedicos() {
        String busqueda = vvda.getMedicoSolicitante();
        if (busqueda.length() < 1) {
            vvda.mostrarSugerenciasMedicos(new ArrayList<>());
            return;
        }
        List<String> sugerencias = medicoDAO.obtenerSugerenciasMedicos(busqueda);
        vvda.mostrarSugerenciasMedicos(sugerencias);
    }

    // ── HERRAMIENTAS VISUALES ──
    private List<ResultadoAnalisis> inyectarTitulosEnResultados(List<ResultadoAnalisis> originales) {
        List<ResultadoAnalisis> lista = new ArrayList<>();
        String codigoPadreActual = "";

        for (ResultadoAnalisis r : originales) {
            String codigoFila = r.getCodigo();
            if (codigoFila == null || codigoFila.trim().isEmpty()) {
                lista.add(r); continue;
            }

            String codigoPadreFila = codigoFila.contains(".") ? codigoFila.split("\\.")[0] : codigoFila;

            if (!codigoPadreFila.equals(codigoPadreActual)) {
                Determinacion detPadre = determinacionDAO.buscarPorCodigo(codigoPadreFila);
                String nombreTitulo = (detPadre != null) ? detPadre.getNombre() : "ESTUDIO";
                lista.add(tituloResultado("--- " + nombreTitulo + " ---"));
                codigoPadreActual = codigoPadreFila;
            }
            lista.add(r);
        }
        return lista;
    }

    private ResultadoAnalisis tituloResultado(String titulo) {
        ResultadoAnalisis r = new ResultadoAnalisis();
        r.setCodigo(""); 
        r.setNombrePrueba(titulo);
        r.setResultado(" "); 
        r.setUnidad("");
        r.setReferencia("");
        return r;
    }
}