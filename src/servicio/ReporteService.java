package servicio;

import dao.AnalisisDAO;
import dao.ConfiguracionDAO;
import dao.DeterminacionDAO;
import dao.PacienteDAO;
import dao.ResultadoAnalisisDAO;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import modelo.Analisis;
import modelo.Paciente;
import modelo.ResultadoAnalisis;

public class ReporteService {

    private ConfiguracionDAO configDAO;
    private AnalisisDAO analisisDAO;
    private PacienteDAO pacienteDAO;
    private ResultadoAnalisisDAO resultadoDAO;
    private DeterminacionDAO determinacionDAO;

    public ReporteService(ConfiguracionDAO configDAO, AnalisisDAO analisisDAO, PacienteDAO pacienteDAO, ResultadoAnalisisDAO resultadoDAO, DeterminacionDAO determinacionDAO) {
        this.configDAO = configDAO;
        this.analisisDAO = analisisDAO;
        this.pacienteDAO = pacienteDAO;
        this.resultadoDAO = resultadoDAO;
        this.determinacionDAO = determinacionDAO;
    }

    public void generarInforme(int idAnalisis, Date fechaImpresion) {
        List<java.io.InputStream> streamsAbiertos = new ArrayList<>();
        try {
            if (idAnalisis == -1) return;

            Analisis analisis = analisisDAO.buscarPorId(idAnalisis);
            if (analisis == null) return;

            Paciente paciente = pacienteDAO.buscarPorId(analisis.getIdPaciente());
            if (paciente == null) {
                JOptionPane.showMessageDialog(null, "No se pudo cargar el paciente del análisis.");
                return;
            }

            List<ResultadoAnalisis> resultadosOriginales = resultadoDAO.listarIncluidosPorAnalisis(idAnalisis);
            if (resultadosOriginales.isEmpty()) {
                JOptionPane.showMessageDialog(null, "El análisis no tiene resultados incluidos");
                return;
            }

            // ── FILTRAR FILAS SIN RESULTADO ──
            List<ResultadoAnalisis> resultadosFiltrados = new ArrayList<>();
            for (ResultadoAnalisis r : resultadosOriginales) {
                String res = r.getResultado();
                String nombre = r.getNombrePrueba() != null ? r.getNombrePrueba() : "";
                boolean esSubtitulo = nombre.startsWith("---") && nombre.endsWith("---");
                if (esSubtitulo || (res != null && !res.trim().isEmpty())) {
                    resultadosFiltrados.add(r);
                }
            }

            if (resultadosFiltrados.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No hay resultados cargados para imprimir");
                return;
            }

            // ── TÍTULOS DINÁMICOS ──
            List<ResultadoAnalisis> listaConTitulos = new ArrayList<>();
            String codigoPadreActual = "";

            for (ResultadoAnalisis r : resultadosFiltrados) {
                String codigoFila = r.getCodigo();

                if (codigoFila == null || codigoFila.trim().isEmpty()) {
                    if (r.getNombrePrueba() != null) {
                        r.setNombrePrueba(r.getNombrePrueba().replace("---", "").trim());
                    }
                    listaConTitulos.add(r);
                    continue;
                }

                String codigoPadreFila = codigoFila.contains(".") ? codigoFila.split("\\.")[0] : codigoFila;

                if (!codigoPadreFila.equals(codigoPadreActual)) {
                    modelo.Determinacion detPadre = determinacionDAO.buscarPorCodigo(codigoPadreFila);
                    String nombreTitulo = (detPadre != null) ? detPadre.getNombre() : "ESTUDIO";

                    ResultadoAnalisis titulo = new ResultadoAnalisis();
                    titulo.setCodigo(""); 
                    titulo.setNombrePrueba(nombreTitulo);
                    titulo.setResultado(" "); // TRUCO NINJA
                    titulo.setUnidad("");
                    titulo.setReferencia("");

                    listaConTitulos.add(titulo);
                    codigoPadreActual = codigoPadreFila;
                }

                String nombreFila = r.getNombrePrueba();
                if (nombreFila != null && nombreFila.startsWith("---") && nombreFila.endsWith("---")) {
                    r.setNombrePrueba(nombreFila.replace("---", "").trim());
                }

                listaConTitulos.add(r);
            }

            // ── SELECCIÓN DE REPORTE Y JASPER ──
            String formato = configDAO.getValor("imp_formato_hoja");
            if (formato == null || formato.trim().isEmpty()) formato = "a4";

            String rutaReporte;
            switch (formato.trim().toLowerCase()) {
                case "a4_horizontal": rutaReporte = "/reportes/informe_A4_horizontal.jrxml"; break;
                case "a5":            rutaReporte = "/reportes/informe_A5_vertical.jrxml"; break;
                case "a5_horizontal": rutaReporte = "/reportes/informe_A5_horizontal.jrxml"; break;
                default:              rutaReporte = "/reportes/informe_A4_vertical.jrxml"; break;
            }

            java.io.InputStream reporteStream = getClass().getResourceAsStream(rutaReporte);
            if (reporteStream == null) {
                JOptionPane.showMessageDialog(null, "Error: No se encontró " + rutaReporte);
                return;
            }

            // ── COMPILACIÓN Y PARÁMETROS ──
            net.sf.jasperreports.engine.JasperReport jasperReport = net.sf.jasperreports.engine.JasperCompileManager.compileReport(reporteStream);
            java.util.Map<String, Object> params = new java.util.HashMap<>();

            String medico = analisis.getMedicoSolicitante();
            params.put("medicoSolicitante", (medico == null || medico.trim().isEmpty() || medico.equals("-")) ? null : medico.trim());
            params.put("labNombre", configDAO.getValor("lab_nombre"));
            params.put("labDireccion", configDAO.getValor("lab_direccion"));
            params.put("labLocalidad", configDAO.getValor("lab_localidad"));
            params.put("labBioquimico", configDAO.getValor("lab_bioquimico"));
            params.put("labMatricula", configDAO.getValor("lab_matricula"));
            params.put("labTelefono", configDAO.getValor("lab_telefono"));
            params.put("fechaAnalisis", fechaImpresion);
            params.put("pacienteNombre", paciente.getApellido() + " " + paciente.getNombre());
            params.put("pacienteDni", paciente.getDni());
            params.put("precio", analisis.getPrecio());

            // Logo
            String rLogo = configDAO.getValor("lab_logo");
            if ("true".equals(configDAO.getValor("imp_incluir_logo")) && rLogo != null && !rLogo.isEmpty()) {
                java.io.File f = new java.io.File(rLogo);
                if (f.exists()) {
                    java.io.FileInputStream fis = new java.io.FileInputStream(f);
                    streamsAbiertos.add(fis);
                    params.put("urlLogo", fis);
                } else {
                    params.put("urlLogo", null);
                }
            } else {
                params.put("urlLogo", null);
            }

            // Firma
            String rFirma = configDAO.getValor("lab_firma");
            if (rFirma != null && !rFirma.isEmpty()) {
                java.io.File f = new java.io.File(rFirma);
                if (f.exists()) {
                    java.io.FileInputStream fis = new java.io.FileInputStream(f);
                    streamsAbiertos.add(fis);
                    params.put("urlFirma", fis);
                }
            }

           // ── LLENAR Y MOSTRAR ──
            net.sf.jasperreports.engine.data.JRBeanCollectionDataSource ds = new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource(listaConTitulos);
            net.sf.jasperreports.engine.JasperPrint jasperPrint = net.sf.jasperreports.engine.JasperFillManager.fillReport(jasperReport, params, ds);

            // Le pasamos null para que sea independiente
            JDialog dialog = new JDialog((java.awt.Frame) null, "Visor de Informe", true);
            dialog.setSize(1000, 800);
            dialog.setLocationRelativeTo(null); // null = se centra automáticamente en la pantalla
            dialog.getContentPane().add(new net.sf.jasperreports.swing.JRViewer(jasperPrint));
            dialog.setVisible(true);

            // ── PDF AUTOMÁTICO ──
            String carpeta = configDAO.getValor("ruta_pdf");
            if (carpeta != null && !carpeta.isEmpty()) {
                String fechaS = new java.text.SimpleDateFormat("dd-MM-yyyy").format(fechaImpresion);
                String nombre = (paciente.getApellido() + "_" + paciente.getNombre() + "_" + fechaS + ".pdf").replace(" ", "_");
                java.io.File folder = new java.io.File(carpeta);
                if (!folder.exists()) folder.mkdirs();
                net.sf.jasperreports.engine.JasperExportManager.exportReportToPdfFile(jasperPrint, new java.io.File(folder, nombre).getAbsolutePath());
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al generar informe: " + e.getMessage());
        } finally {
            for (java.io.InputStream is : streamsAbiertos) {
                try { is.close(); } catch (Exception ex) {}
            }
        }
    }
}