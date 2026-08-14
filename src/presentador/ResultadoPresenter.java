package presentador;

// @author lucianoalicata

import dao.AnalisisDAO;
import dao.AuditoriaDAO;
import dao.ConfiguracionDAO;
import dao.DeterminacionDAO;
import dao.MedicoDAO;
import dao.ObraSocialDAO;
import dao.ResultadoAnalisisDAO;
import java.util.List;
import modelo.Determinacion;
import modelo.ObraSocial;
import modelo.Paciente;
import modelo.Usuario;
import presentador.router.AppRouter;
import vista.interfaces.IVistaCargarResultados;

public class ResultadoPresenter {

    private final IVistaCargarResultados vcr;
    private final AppRouter router;
    private final Paciente pacienteActual;
    private final List<Determinacion> determinacionesAProcesar;
    private final Usuario usuarioLogueado;
    private final AnalisisDAO analisisDAO;
    private final ResultadoAnalisisDAO resultadoDAO;
    private final ObraSocialDAO obraSocialDAO;
    private final DeterminacionDAO determinacionDAO;
    private final ConfiguracionDAO configDAO;
    private final MedicoDAO medicoDAO;
    private final AuditoriaDAO auditoriaDAO;

    public ResultadoPresenter(IVistaCargarResultados vcr, AppRouter router, Paciente pacienteActual,
            List<Determinacion> determinaciones, Usuario usuarioLogueado,
            AnalisisDAO analisisDAO, ResultadoAnalisisDAO resultadoDAO, ObraSocialDAO obraSocialDAO,
            DeterminacionDAO determinacionDAO, ConfiguracionDAO configDAO, AuditoriaDAO auditoriaDAO, MedicoDAO medicoDAO) {
        this.vcr = vcr;
        this.router = router;
        this.pacienteActual = pacienteActual;
        this.determinacionesAProcesar = determinaciones;
        this.usuarioLogueado = usuarioLogueado;
        this.analisisDAO = analisisDAO;
        this.resultadoDAO = resultadoDAO;
        this.obraSocialDAO = obraSocialDAO;
        this.determinacionDAO = determinacionDAO;
        this.configDAO = configDAO;
        this.auditoriaDAO = auditoriaDAO;
        this.medicoDAO = medicoDAO;
    }

    public void iniciar() {
        vcr.setPresenter(this);
        vcr.setNombrePaciente(pacienteActual.getApellido() + " " + pacienteActual.getNombre());
        vcr.setObraSocial("");
        vcr.setMedicoSolicitante("");
        vcr.cargarDeterminaciones(this.determinacionesAProcesar);
    }

    public void onGuardarResultados() {
        vcr.detenerEdicionTabla();

        String seleccionOS = vcr.getObraSocial().trim();
        if (seleccionOS.isEmpty()) {
            vcr.mostrarMensaje("Debe ingresar una Obra Social (o PARTICULAR) para poder guardar el análisis.");
            return;
        }

        String medicoRaw = vcr.getMedicoSolicitante().trim();
        String medico = extraerMatricula(medicoRaw);
        
        if (medico.isEmpty()) {
            medico = "-";
        } else {
            modelo.Medico medicoBD = medicoDAO.buscarPorMatricula(medico);
            if (medicoBD == null) {
                vcr.mostrarMensaje("El médico ingresado no se encuentra registrado en el sistema.\nPor favor, seleccione un profesional válido de la lista sugerida o deje el campo vacío.");
                return;
            }
        }

        int filas = vcr.getCantidadFilas();
        if (filas == 0) {
            vcr.mostrarMensaje("No hay determinaciones para guardar.");
            return;
        }

        double sumaLeucos = 0;
        boolean tieneFormula = false;

        for (int i = 0; i < filas; i++) {
            String nombre = vcr.getNombrePrueba(i).toLowerCase();
            String res = vcr.getResultado(i);

            if (nombre.contains("cayados") || nombre.contains("neutrófilos") || nombre.contains("neutrofilos")
                    || nombre.contains("eosinófilos") || nombre.contains("eosinofilos")
                    || nombre.contains("basófilos") || nombre.contains("basofilos")
                    || nombre.contains("linfocitos") || nombre.contains("monocitos")) {

                if (res != null && !res.trim().isEmpty()) {
                    tieneFormula = true;
                    try {
                        String clean = res.replace(",", ".");
                        sumaLeucos += Double.parseDouble(clean.replaceAll("[^0-9.]", ""));
                    } catch (Exception e) {
                    }
                }
            }
        }
        
        if (tieneFormula && (sumaLeucos < 99.0 || sumaLeucos > 101.0)) {
            vcr.mostrarMensaje("ERROR EN EL HEMOGRAMA: La suma de la fórmula leucocitaria da " + sumaLeucos + "%.\nDebe ser exactamente 100%. Por favor, revise y corrija los valores.");
            return;
        }

        try {
            String codigoOS = seleccionOS.contains(" - ") ? seleccionOS.split(" - ")[0] : seleccionOS;
            modelo.ObraSocial osSeleccionada = obraSocialDAO.buscarPorCodigoONombre(codigoOS).stream()
                    .filter(o -> o.getCodigo().equals(codigoOS)).findFirst().orElse(null);

            if (osSeleccionada == null) {
                vcr.mostrarMensaje("""
                                   La Obra Social ingresada no es v\u00e1lida. 
                                   Puede registrarla en la secci\u00f3n de 'Obras Sociales'.""");
                return;
            }

            double precioFinal = 0.0;

            if (codigoOS.equals("60001") || osSeleccionada.getNombre().toUpperCase().contains("PARTICULAR")) {
                java.util.HashSet<String> codigosPadresUnicos = new java.util.HashSet<>();

                for (int i = 0; i < filas; i++) {
                    String cod = vcr.getCodigo(i);
                    if (cod != null && !cod.trim().isEmpty()) {
                        String codigoPadre = cod.contains(".") ? cod.split("\\.")[0] : cod;
                        codigosPadresUnicos.add(codigoPadre);
                    }
                }

                double sumaTotalUB = 0;
                for (String codPadre : codigosPadresUnicos) {
                    modelo.Determinacion detPadre = determinacionDAO.buscarPorCodigo(codPadre);
                    if (detPadre != null) {
                        sumaTotalUB += detPadre.getUb();
                    }
                }

                String valorConfig = configDAO.getValor("valor_ub");
                double valorUBActual = (valorConfig != null) ? Double.parseDouble(valorConfig) : 1820.0;
                precioFinal = sumaTotalUB * valorUBActual;
            }

            modelo.Analisis a = new modelo.Analisis();
            a.setIdPaciente(pacienteActual.getIdPaciente());
            a.setObraSocial(codigoOS);
            a.setFecha(new java.util.Date());
            a.setPrecio(precioFinal);
            a.setMedicoSolicitante(medico);

            int idAnalisis = analisisDAO.crear(a);

            if (idAnalisis > 0) {
                for (int i = 0; i < filas; i++) {
                    String cod = vcr.getCodigo(i);
                    if (cod == null || cod.trim().isEmpty()) {
                        continue; 
                    }
                    modelo.ResultadoAnalisis r = new modelo.ResultadoAnalisis();
                    r.setIdAnalisis(idAnalisis);
                    r.setCodigo(cod);
                    r.setNombrePrueba(vcr.getNombrePrueba(i));
                    r.setResultado(vcr.getResultado(i));
                    r.setUnidad(vcr.getUnidad(i));
                    r.setReferencia(vcr.getReferencia(i));
                    r.setImprimir(true);

                    modelo.Determinacion det = determinacionDAO.buscarPorCodigo(cod);
                    if (det != null) {
                        r.setPrioridad(det.getPrioridad());
                        String nombreLimpio = det.getNombre() != null ? det.getNombre().trim() : "";
                        String refLimpia = det.getReferencia() != null ? det.getReferencia().trim() : "";
                        String resActual = r.getResultado() != null ? r.getResultado().trim() : "";

                        boolean esMetodo = nombreLimpio.equalsIgnoreCase("Método") || nombreLimpio.equalsIgnoreCase("Metodo");
                        if (esMetodo && resActual.isEmpty() && !refLimpia.isEmpty()) {
                            r.setResultado(refLimpia);
                        }
                    }
                    resultadoDAO.guardar(r);
                }

                auditoriaDAO.registrar(usuarioLogueado, "CREAR", "analisis", idAnalisis, null,
                        "Precio: $" + precioFinal + " (OS: " + codigoOS + ")",
                        "Análisis creado para: " + pacienteActual.getApellido());

                vcr.ocultarSugerenciasFlotantes();

                String msgExito = "Análisis guardado con éxito.\nObra Social: " + osSeleccionada.getNombre();
                if (precioFinal > 0) {
                    msgExito += "\nTotal a cobrar: $" + precioFinal;
                }
                vcr.mostrarMensaje(msgExito);

                router.irAPacientes();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            vcr.mostrarMensaje("Error crítico al procesar el cálculo y guardado.");
        }
    }

    private String extraerMatricula(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return "";
        }
        String limpio = texto.trim();

        if (limpio.contains("(mp.")) {
            try {
                limpio = limpio.substring(limpio.indexOf("(mp.") + 4, limpio.indexOf(")")).trim();
            } catch (Exception e) {
            }
        }
        return limpio;
    }

    public void onBuscarSugerenciasOS() {
        String texto = vcr.getObraSocial();
        List<ObraSocial> lista = obraSocialDAO.buscarPorCodigoONombre(texto);
        List<String> sugerencias = lista.stream().map(os -> os.getCodigo() + " - " + os.getNombre()).toList();
        vcr.mostrarSugerenciasOS(sugerencias);
    }

    public void onBuscarSugerenciasMed() {
        String busqueda = vcr.getMedicoSolicitante();

        if (busqueda.length() < 1) {
            vcr.mostrarSugerenciasMedicos(new java.util.ArrayList<>());
            return;
        }

        java.util.List<String> sugerencias = medicoDAO.obtenerSugerenciasMedicos(busqueda);
        vcr.mostrarSugerenciasMedicos(sugerencias);
    }

    public void onVolver() {
        vcr.detenerEdicionTabla();

        boolean hayDatosCargados = false;
        for (int i = 0; i < vcr.getCantidadFilas(); i++) {
            String res = vcr.getResultado(i);
            if (res != null && !res.trim().isEmpty()) {
                hayDatosCargados = true;
                break;
            }
        }

        if (hayDatosCargados) {
            int respuesta = vcr.confirmarAccion(
                    "Hay resultados cargados sin guardar.\n\n¿Desea GUARDAR los datos antes de salir?\n(Seleccione 'No' para salir y descartar los cambios)",
                    "Confirmar Salida"
            );

            switch (respuesta) {
                case 0 -> {
                    onGuardarResultados();
                    return;
                }
                case 1 -> {
                    router.irAPacientes();
                    return;
                }
                default -> {
                    return;
                }
            }
        }
        router.irAPacientes();
    }
}