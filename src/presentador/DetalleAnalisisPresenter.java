package presentador;

import dao.AnalisisDAO;
import dao.AuditoriaDAO;
import dao.ConfiguracionDAO;
import dao.DeterminacionDAO;
import dao.MedicoDAO;
import dao.PacienteDAO;
import dao.ResultadoAnalisisDAO;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import modelo.Analisis;
import modelo.Determinacion;
import modelo.Paciente;
import modelo.ResultadoAnalisis;
import modelo.Usuario;
import servicio.ReporteService;
import presentador.router.AppRouter;
import vista.interfaces.IVistaVerDetalleAnalisis;

public class DetalleAnalisisPresenter {

    private final IVistaVerDetalleAnalisis vista;
    private final AppRouter router;
    private final AnalisisDAO analisisDAO;
    private final ResultadoAnalisisDAO resultadoDAO;
    private final PacienteDAO pacienteDAO;
    private final DeterminacionDAO determinacionDAO;
    private final AuditoriaDAO auditoriaDAO;
    private final ConfiguracionDAO configDAO;
    private final MedicoDAO medicoDAO;
    private final Usuario usuarioLogueado;
    private final ReporteService reporteService;

    private int idAnalisisActual;
    private Paciente pacienteActual;
    private String medicoOriginal;
    private Map<Integer, String> valoresOriginales;
    private boolean estaGuardando = false;
    private String origen; 
    public static final String ORIGEN_HISTORIAL = "HISTORIAL";
    public static final String ORIGEN_LISTADO = "LISTADO";

    public DetalleAnalisisPresenter(IVistaVerDetalleAnalisis vista, AppRouter router,
                                    AnalisisDAO analisisDAO, ResultadoAnalisisDAO resultadoDAO,
                                    PacienteDAO pacienteDAO, DeterminacionDAO determinacionDAO,
                                    AuditoriaDAO auditoriaDAO, ConfiguracionDAO configDAO,
                                    MedicoDAO medicoDAO, Usuario usuarioLogueado,
                                    ReporteService reporteService) {
        this.vista = vista;
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
        this.valoresOriginales = new HashMap<>();
    }

    public void iniciar(int idAnalisis, String origen) {
        this.idAnalisisActual = idAnalisis;
        this.origen = origen;
        vista.setPresenter(this);

        Analisis analisis = analisisDAO.buscarPorId(idAnalisisActual);
        if (analisis == null) return;

        this.pacienteActual = pacienteDAO.buscarPorId(analisis.getIdPaciente());
        this.medicoOriginal = analisis.getMedicoSolicitante();

        if ("LECTOR".equals(usuarioLogueado.getRol())) {
            vista.habilitarBotonGuardar(false);
            vista.habilitarBotonEliminar(false);
            vista.bloquearMedicoSolicitante();
            vista.habilitarBotonImprimir(true);
            vista.bloquearEdicionTabla();
        }

        String nombreCompleto = (pacienteActual != null) 
            ? pacienteActual.getApellido() + " " + pacienteActual.getNombre() 
            : "Desconocido";
        vista.setNombrePaciente(nombreCompleto);

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
        vista.setFechaAnalisis(sdf.format(analisis.getFecha()));
        vista.setFechaInforme(analisis.getFecha());
        vista.setIdAnalisis(idAnalisisActual);
        vista.setMedicoSolicitante(analisis.getMedicoSolicitante());

        cargarTablaConTitulos();
        router.mostrarVistaDetalleAnalisis();
    }

    private List<ResultadoAnalisis> inyectarTitulos(List<ResultadoAnalisis> resultados) {
        List<ResultadoAnalisis> lista = new ArrayList<>();
        String grupoActual = "";

        for (ResultadoAnalisis r : resultados) {
            String codigo = r.getCodigo();
            if (codigo == null || codigo.trim().isEmpty()) {
                lista.add(r);
                continue;
            }

            String grupo = codigo.contains(".") ? codigo.split("\\.")[0] : codigo;

            if (!grupo.equals(grupoActual)) {
                Determinacion detPadre = determinacionDAO.buscarPorCodigo(grupo);
                String nombreGrupo = (detPadre != null) ? detPadre.getNombre() : "ESTUDIO";
                lista.add(crearFilaTitulo("--- " + nombreGrupo + " ---"));
                grupoActual = grupo;
            }
            lista.add(r);
        }
        return lista;
    }

    private ResultadoAnalisis crearFilaTitulo(String titulo) {
        ResultadoAnalisis r = new ResultadoAnalisis();
        r.setIdResultado(-1);
        r.setCodigo("");
        r.setNombrePrueba(titulo);
        r.setResultado("");
        r.setUnidad("");
        r.setReferencia("");
        return r;
    }

    // ── LÓGICA DE COMPARACIÓN BLINDADA (REEMPLAZAR TODO ESTE BLOQUE) ──
    private boolean hayCambios() {
        // 1. Verificación del médico (Comparamos solo la matrícula o el texto base)
        String medicoActual = extraerMatricula(vista.getMedicoSolicitante());
        String medicoOrig = extraerMatricula(medicoOriginal);

        if (!medicoActual.equals(medicoOrig)) {
            return true;
        }

        // 2. Verificación de los resultados en la grilla
        for (int i = 0; i < vista.getCantidadFilas(); i++) {
            int id = vista.getIdResultado(i);
            if (id == -1) continue; // Salta filas de títulos

            String nuevoValor = vista.getResultadoEditado(i);
            String valorOriginal = valoresOriginales.get(id);

            // Normalizamos nulos a cadenas vacías para evitar falsos positivos ("null" vs "")
            if (nuevoValor == null) nuevoValor = "";
            if (valorOriginal == null) valorOriginal = "";

            nuevoValor = nuevoValor.trim();
            valorOriginal = valorOriginal.trim();

            // Limpiamos los formatos numéricos (ej. "1.500,50" -> "1500,50")
            String nuevoValorLimpio = limpiarFormatoNumero(nuevoValor);
            String valorOriginalLimpio = limpiarFormatoNumero(valorOriginal);

            if (!valorOriginalLimpio.equals(nuevoValorLimpio)) {
                return true;
            }
        }
        return false;
    }

    private String limpiarFormatoNumero(String valor) {
        if (valor == null || valor.isEmpty()) {
            return "";
        }
        // Si tiene formato de miles con puntos, los sacamos para comparar el valor real
        if (valor.matches("^-?\\d{1,3}(\\.\\d{3})*(,\\d+)?$")) {
            return valor.replace(".", "");
        }
        return valor;
    }

    // Método auxiliar para pelar el nombre del médico y dejar solo la matrícula
    private String extraerMatricula(String texto) {
        if (texto == null || texto.trim().isEmpty()) return "";
        String limpio = texto.trim();
        
        // Buscamos el formato "Nombre Apellido (mp. 1234)"
        if (limpio.contains("(mp.")) {
            try {
                // Extrae todo lo que está después de "(mp. " y antes del ")"
                limpio = limpio.substring(limpio.indexOf("(mp.") + 4, limpio.indexOf(")")).trim();
            } catch (Exception e) {
                // Si el formato es raro, no hacemos nada y dejamos lo que escribió el usuario
            }
        }
        return limpio;
    }

    // ── GUARDADO INTELIGENTE ──
    public void onEditar() {
        // ── VALIDACIÓN DE FÓRMULA LEUCOCITARIA (SUMA = 100%) ──
        int filas = vista.getCantidadFilas();
        double sumaLeucos = 0;
        boolean tieneFormula = false;

        for (int i = 0; i < filas; i++) {
            Object objNombre = vista.getGrilla().getModel().getValueAt(i, 2);
            String nombre = objNombre != null ? objNombre.toString().toLowerCase() : "";
            String res = vista.getResultadoEditado(i);

            if (nombre.contains("cayados") || nombre.contains("neutrófilos") || nombre.contains("neutrofilos")
                    || nombre.contains("eosinófilos") || nombre.contains("eosinofilos")
                    || nombre.contains("basófilos") || nombre.contains("basofilos")
                    || nombre.contains("linfocitos") || nombre.contains("monocitos")) {

                if (res != null && !res.trim().isEmpty()) {
                    tieneFormula = true;
                    try {
                        String clean = res.replace(",", "."); // Homogeneizar decimales
                        sumaLeucos += Double.parseDouble(clean.replaceAll("[^0-9.]", ""));
                    } catch (Exception e) {
                    }
                }
            }
        }

        // Margen para admitir redondeos (99.0 a 101.0)
        if (tieneFormula && (sumaLeucos < 99.0 || sumaLeucos > 101.0)) {
            vista.mostrarMensaje("ERROR EN EL HEMOGRAMA: La suma de la fórmula leucocitaria da " + sumaLeucos + "%.\nDebe ser exactamente 100%. Por favor, revise y corrija los valores.");
            return;
        }
        
        if (estaGuardando) return;
        estaGuardando = true;

        try {
            vista.detenerEdicionTabla();

            if (!hayCambios()) {
                vista.mostrarMensaje("No hay cambios para guardar.");
                return;
            }

            String inputMedico = vista.getMedicoSolicitante();
            String matriculaActual = extraerMatricula(inputMedico);
            String matriculaOrig = extraerMatricula(medicoOriginal);

            StringBuilder bitacoraCambios = new StringBuilder();
            int totalActualizados = 0;
            boolean error = false;

            // --- ACTUALIZAR RESULTADOS ---
            for (int i = 0; i < vista.getCantidadFilas(); i++) {
                int id = vista.getIdResultado(i);
                if (id == -1) continue;

                String nuevoValor = vista.getResultadoEditado(i);
                if (nuevoValor == null) nuevoValor = "";
                nuevoValor = nuevoValor.trim();

                String valorAnterior = valoresOriginales.get(id);
                if (valorAnterior == null) valorAnterior = "";
                valorAnterior = valorAnterior.trim();

                String nuevoLimpio = limpiarFormatoNumero(nuevoValor);
                String anteriorLimpio = limpiarFormatoNumero(valorAnterior);

                if (!anteriorLimpio.equals(nuevoLimpio)) {
                    ResultadoAnalisis r = resultadoDAO.buscarPorId(id);
                    if (r != null) {
                        if (resultadoDAO.actualizarResultado(id, nuevoValor)) {
                            totalActualizados++;
                            if (bitacoraCambios.length() > 0) bitacoraCambios.append("\n");
                            bitacoraCambios.append(r.getNombrePrueba())
                                .append(": ").append(valorAnterior.isEmpty() ? "[Vacío]" : valorAnterior)
                                .append(" -> ").append(nuevoValor.isEmpty() ? "[Vacío]" : nuevoValor);
                            valoresOriginales.put(id, nuevoValor);
                        } else {
                            error = true;
                        }
                    }
                }
            }

            // --- ACTUALIZAR MÉDICO ---
            boolean medicoActualizado = false;
            if (!matriculaActual.equals(matriculaOrig)) {
                if (analisisDAO.actualizarMedico(idAnalisisActual, matriculaActual)) {
                    medicoActualizado = true;
                    if (bitacoraCambios.length() > 0) bitacoraCambios.append("\n");
                    bitacoraCambios.append("Médico: ").append(matriculaOrig.isEmpty() ? "[Vacío]" : matriculaOrig)
                                   .append(" -> ").append(matriculaActual);
                    
                    // Actualizamos la referencia original a lo que dice el campo de texto exacto
                    medicoOriginal = inputMedico; 
                } else {
                    error = true;
                }
            }

            if (!error) {
                if (bitacoraCambios.length() > 0) {
                    auditoriaDAO.registrar(usuarioLogueado, "EDITAR", "resultado", 
                        idAnalisisActual, "Valores anteriores", bitacoraCambios.toString(),
                        "Edición de resultados - Análisis ID: " + idAnalisisActual);
                }

                String mensaje = "Cambios guardados exitosamente";
                if (totalActualizados > 0) mensaje += "\nSe actualizaron " + totalActualizados + " resultado(s)";
                if (medicoActualizado) mensaje += "\nSe actualizó el médico solicitante";
                
                vista.mostrarMensaje(mensaje);
                router.refrescarVistasAnalisisAbiertas();
            } else {
                vista.mostrarMensaje("✗ Error al guardar algunos cambios. Verifique e intente nuevamente.");
            }
        } finally {
            estaGuardando = false;
        }
    }
    
    private void cargarTablaConTitulos() {
    List<ResultadoAnalisis> resultados = resultadoDAO.listarPorAnalisis(idAnalisisActual);
    valoresOriginales.clear();
    
    for (ResultadoAnalisis r : resultados) {
        // Guardar valor original limpio (sin formato)
        String valorLimpio = limpiarFormatoNumero(r.getResultado());
        valoresOriginales.put(r.getIdResultado(), valorLimpio);
    }

    List<ResultadoAnalisis> conTitulos = inyectarTitulos(resultados);
    vista.cargarResultadosDetalle(new ArrayList<>(conTitulos));
}



    public void onEliminarFila() {
        if (estaGuardando) return;
        estaGuardando = true;

        try {
            int fila = vista.getGrilla().getSelectedRow();
            if (fila == -1) {
                vista.mostrarMensaje("Seleccione una fila para eliminar");
                return;
            }

            int idResultado = vista.getIdResultado(fila);
            if (idResultado == -1) {
                vista.mostrarMensaje("No se puede eliminar esta fila");
                return;
            }

            ResultadoAnalisis resultado = resultadoDAO.buscarPorId(idResultado);
            if (resultado == null) return;

            List<ResultadoAnalisis> restantes = resultadoDAO.listarIncluidosPorAnalisis(idAnalisisActual);

            if (restantes.size() == 1) {
                int confirmacion = vista.confirmarAccion(
                    "Esta es la última determinación del análisis.\n" +
                    "Si continúa, TODO EL ANÁLISIS será eliminado.\n" +
                    "¿Desea continuar?",
                    "Eliminar análisis completo"
                );

                if (confirmacion == 0) {
                    String info = "Prueba: " + resultado.getNombrePrueba() + 
                                 " | Valor: " + resultado.getResultado();
                    resultadoDAO.eliminarResultado(idResultado);
                    analisisDAO.eliminar(idAnalisisActual);
                    auditoriaDAO.registrar(usuarioLogueado, "ELIMINAR", "analisis",
                        idAnalisisActual, "Análisis eliminado: " + info, 
                        "ELIMINADO", "Eliminación en cascada");
                    
                    vista.mostrarMensaje("Análisis eliminado correctamente");
                    router.cerrarDetalleYRefrescarAnalisis();
                }
            } else {
                int confirmacion = vista.confirmarAccion(
                    "¿Eliminar " + resultado.getNombrePrueba() + "?\n" +
                    "El precio se recalculará automáticamente",
                    "Confirmar eliminación"
                );

                if (confirmacion == 0) {
                    Determinacion det = determinacionDAO.buscarPorCodigo(resultado.getCodigo());
                    Analisis analisis = analisisDAO.buscarPorId(idAnalisisActual);
                    String ubValor = configDAO.getValor("valor_ub");
                    double factor = (ubValor != null) ? Double.parseDouble(ubValor) : 1600.0;

                    if (det != null && analisis != null) {
                        double monto = det.getUb() * factor;
                        double nuevoPrecio = Math.max(0, analisis.getPrecio() - monto);
                        analisisDAO.actualizarPrecio(idAnalisisActual, nuevoPrecio);
                    }

                    String info = "Prueba: " + resultado.getNombrePrueba() + 
                                 " | Valor: " + resultado.getResultado();
                    
                    if (resultadoDAO.eliminarResultado(idResultado)) {
                        auditoriaDAO.registrar(usuarioLogueado, "ELIMINAR", "resultado",
                            idResultado, info, "ELIMINADO", 
                            "Precio actualizado por eliminación");
                        
                        valoresOriginales.remove(idResultado);
                        cargarTablaConTitulos();
                        router.refrescarVistasAnalisisAbiertas();
                        vista.mostrarMensaje("Fila eliminada y precio actualizado");
                    }
                }
            }
        } finally {
            estaGuardando = false;
        }
    }

    public void onImprimir() {
        // 1. Bloqueamos el botón INMEDIATAMENTE para evitar la metralleta de clics
        vista.habilitarBotonImprimir(false);

        // 2. Mandamos el trabajo pesado a un hilo secundario para no congelar la pantalla
        new Thread(() -> {
            try {
                Date fecha = vista.getFechaSeleccionada();
                reporteService.generarInforme(idAnalisisActual, fecha);

                if (analisisDAO.cambiarEstadoGenerado(idAnalisisActual)) {
                    auditoriaDAO.registrar(usuarioLogueado, "IMPRIMIR", "analisis", 
                        idAnalisisActual, null, "Informe generado", 
                        "Usuario imprimió desde vista de detalles");
                }

                // 3. Volvemos al hilo principal (UI) para mostrar el mensaje y rehabilitar
                javax.swing.SwingUtilities.invokeLater(() -> {
                    vista.mostrarMensaje("✓ Informe procesado correctamente");
                    router.refrescarVistasAnalisisAbiertas();
                    vista.habilitarBotonImprimir(true);
                });
            } catch (Exception e) {
                e.printStackTrace();
                javax.swing.SwingUtilities.invokeLater(() -> {
                    vista.mostrarMensaje("❌ Error al generar el informe.");
                    vista.habilitarBotonImprimir(true);
                });
            }
        }).start();
    }

    public void onVolver() {
        vista.detenerEdicionTabla();
        
        if (hayCambios()) {
            int respuesta = vista.confirmarAccion(
                "Hay cambios sin guardar. ¿Desea guardarlos antes de salir?",
                "Cambios pendientes"
            );
            if (respuesta == 0) { // YES
                onEditar();
                volverAlOrigen();
            } else if (respuesta == 1) { // NO
                volverAlOrigen();
            }
        } else {
            volverAlOrigen();
        }
    }
    
    private void volverAlOrigen() {
        if (ORIGEN_HISTORIAL.equals(origen)) {
            router.volverAHistorialPaciente(pacienteActual);
        } else {
            router.abrirListadoGlobalAnalisis();
        }
    }

    public void onSeleccionarAnalisis() {}

    public void onBuscarSugerenciasMedicos() {
        String busqueda = vista.getMedicoSolicitante();
        if (busqueda.length() < 1) {
            vista.mostrarSugerenciasMedicos(new ArrayList<>());
            return;
        }

        List<String> sugerencias = medicoDAO.obtenerSugerenciasMedicos(busqueda);
        vista.mostrarSugerenciasMedicos(sugerencias);
    }
    

    // Función auxiliar para evitar detectar cambios cuando solo varía un decimal vacío
    private boolean esMismoNumero(String str1, String str2) {
        if (str1.equals(str2)) return true;
        try {
            double d1 = Double.parseDouble(str1.replace(",", "."));
            double d2 = Double.parseDouble(str2.replace(",", "."));
            return d1 == d2;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}