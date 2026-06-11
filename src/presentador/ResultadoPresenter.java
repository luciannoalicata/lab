package presentador;

import dao.AnalisisDAO;
import dao.AuditoriaDAO;
import dao.ConfiguracionDAO;
import dao.DeterminacionDAO;
import dao.MedicoDAO;
import dao.ObraSocialDAO;
import dao.ResultadoAnalisisDAO;
import java.util.List;
import modelo.Analisis;
import modelo.Determinacion;
import modelo.ObraSocial;
import modelo.Paciente;
import modelo.ResultadoAnalisis;
import modelo.Usuario;
import presentador.router.AppRouter;
import vista.interfaces.IVistaCargarResultados;

// 1. Adiós al implements ActionListener
public class ResultadoPresenter {

    private final IVistaCargarResultados vcr;
    private final AppRouter router; // 2. El router toma el control
    
    // El contexto operativo
    private final Paciente pacienteActual;
    private final List<Determinacion> determinacionesAProcesar;
    private final Usuario usuarioLogueado;
    
    // El ejército de DAOs
    private final AnalisisDAO analisisDAO;
    private final ResultadoAnalisisDAO resultadoDAO;
    private final ObraSocialDAO obraSocialDAO;
    private final DeterminacionDAO determinacionDAO;
    private final ConfiguracionDAO configDAO;
    private final MedicoDAO medicoDAO;
    private final AuditoriaDAO auditoriaDAO;

    // 3. Constructor actualizado (se eliminaron redundancias)
    public ResultadoPresenter(IVistaCargarResultados vcr, AppRouter router, Paciente pacienteActual, 
                               List<Determinacion> determinaciones, Usuario usuarioLogueado,
                               AnalisisDAO analisisDAO, ResultadoAnalisisDAO resultadoDAO, ObraSocialDAO obraSocialDAO, 
                               DeterminacionDAO determinacionDAO, ConfiguracionDAO configDAO, AuditoriaDAO auditoriaDAO, MedicoDAO medicoDAO){
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
        vcr.setPresenter(this); // Conectamos la vista al presentador
        vcr.setNombrePaciente(pacienteActual.getApellido() + " " + pacienteActual.getNombre());
        vcr.setObraSocial(""); 
        vcr.setMedicoSolicitante(""); 
        vcr.cargarDeterminaciones(this.determinacionesAProcesar);
        
    }

    // ════════════════════════════════════════════════════════════════
    //  MÉTODOS EXPLÍCITOS LLAMADOS POR LA VISTA (MVP Puro)
    // ════════════════════════════════════════════════════════════════

    public void onGuardarResultados() {
        vcr.detenerEdicionTabla();

        String seleccionOS = vcr.getObraSocial().trim();
        if (seleccionOS.isEmpty()) {
            vcr.mostrarMensaje("Debe ingresar una Obra Social (o PARTICULAR).");
            return;
        }

        String medico = vcr.getMedicoSolicitante().trim();
        if (medico.isEmpty()) medico = "-";

        int filas = vcr.getCantidadFilas();
        if (filas == 0) {
            vcr.mostrarMensaje("No hay determinaciones para guardar.");
            return;
        }

        try {
             // 1. Extraer Código y Arancel de la Obra Social
            String codigoOS = seleccionOS.contains(" - ") ? seleccionOS.split(" - ")[0] : seleccionOS;
            ObraSocial osSeleccionada = obraSocialDAO.buscarPorCodigoONombre(codigoOS).stream()
                    .filter(o -> o.getCodigo().equals(codigoOS)).findFirst().orElse(null);

            if (osSeleccionada == null) {
                vcr.mostrarMensaje("La Obra Social ingresada no es válida.");
                return;
            }

             // 2. Lógica de precio
            double precioFinal;
            if (codigoOS.equals("60001")) {
                precioFinal = vcr.pedirPrecioManual();
                if (precioFinal < 0) return; // Canceló
            } else {
                double arancelOS = osSeleccionada.getArancel();
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
                    Determinacion detPadre = determinacionDAO.buscarPorCodigo(codPadre);
                    if (detPadre != null) sumaTotalUB += detPadre.getUb();
                }

                String valorConfig = configDAO.getValor("valor_ub");
                double valorUBActual = (valorConfig != null) ? Double.parseDouble(valorConfig) : 1820.0;
                precioFinal = sumaTotalUB * arancelOS * valorUBActual;
            }

             // 3. Configurar el Análisis
            Analisis a = new Analisis();
            a.setIdPaciente(pacienteActual.getIdPaciente());
            a.setObraSocial(codigoOS);
            a.setFecha(new java.util.Date());
            a.setPrecio(precioFinal);
            a.setMedicoSolicitante(medico);

            int idAnalisis = analisisDAO.crear(a);

            if (idAnalisis > 0) {
                 // 4. Guardar resultados
                for (int i = 0; i < filas; i++) {
                    String cod = vcr.getCodigo(i);
                    if (cod == null || cod.trim().isEmpty()) continue; // Salta títulos

                    ResultadoAnalisis r = new ResultadoAnalisis();
                    r.setIdAnalisis(idAnalisis);
                    r.setCodigo(cod);
                    r.setNombrePrueba(vcr.getNombrePrueba(i));
                    r.setResultado(vcr.getResultado(i));
                    r.setUnidad(vcr.getUnidad(i));
                    r.setReferencia(vcr.getReferencia(i));
                    r.setImprimir(true);

                    Determinacion det = determinacionDAO.buscarPorCodigo(cod);
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

                 // 5. Auditoría y UX
                auditoriaDAO.registrar(usuarioLogueado, "CREAR", "analisis", idAnalisis, null,
                        "Precio: $" + precioFinal + " (OS: " + codigoOS + ")",
                        "Análisis creado para: " + pacienteActual.getApellido());

                vcr.ocultarSugerenciasFlotantes();
                vcr.mostrarMensaje("Análisis guardado con éxito.\nObra Social: " + osSeleccionada.getNombre() + "\nTotal: $" + precioFinal);

                 // 6. ENRUTAMOS A LA PANTALLA FINAL (A través del AppRouter)
                router.irAPacientes(); // Como no tenías método abrirListadoGlobalAnalisis, volvemos a pacientes.
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            vcr.mostrarMensaje("Error crítico al procesar el cálculo y guardado.");
        }
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
        // Enrutamos de vuelta a la pantalla anterior
        router.irAPacientes();
    }
}