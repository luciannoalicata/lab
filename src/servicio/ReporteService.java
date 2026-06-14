package servicio;

import dao.AnalisisDAO;
import dao.ConfiguracionDAO;
import dao.DeterminacionDAO;
import dao.MedicoDAO;
import dao.PacienteDAO;
import dao.ResultadoAnalisisDAO;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import modelo.Analisis;
import modelo.Paciente;
import modelo.ResultadoAnalisis;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.swing.JRViewer;

public class ReporteService {

    private ConfiguracionDAO configDAO;
    private AnalisisDAO analisisDAO;
    private PacienteDAO pacienteDAO;
    private MedicoDAO medicoDAO;
    private ResultadoAnalisisDAO resultadoDAO;
    private DeterminacionDAO determinacionDAO;

    public ReporteService(ConfiguracionDAO configDAO, AnalisisDAO analisisDAO,
            PacienteDAO pacienteDAO, ResultadoAnalisisDAO resultadoDAO,
            DeterminacionDAO determinacionDAO, MedicoDAO medicoDAO) {
        
        this.configDAO = configDAO;
        this.analisisDAO = analisisDAO;
        this.pacienteDAO = pacienteDAO;
        this.resultadoDAO = resultadoDAO;
        this.determinacionDAO = determinacionDAO;
        this.medicoDAO = medicoDAO;
    }

public void generarInforme(int idAnalisis, Date fechaImpresion) {

        List<java.io.FileInputStream> streamsAbiertos = new ArrayList<>();
        JDialog dialog = null;

        try {
            if (idAnalisis == -1) {
                return;
            }

            modelo.Analisis analisis = analisisDAO.buscarPorId(idAnalisis);
            if (analisis == null) {
                return;
            }

            modelo.Paciente paciente = pacienteDAO.buscarPorId(analisis.getIdPaciente());
            if (paciente == null) {
                javax.swing.JOptionPane.showMessageDialog(null, "No se pudo cargar el paciente del análisis.");
                return;
            }

            List<modelo.ResultadoAnalisis> resultadosOriginales = resultadoDAO.listarIncluidosPorAnalisis(idAnalisis);
            if (resultadosOriginales.isEmpty()) {
                javax.swing.JOptionPane.showMessageDialog(null, "El análisis no tiene resultados incluidos");
                return;
            }

            // ── FILTRAR FILAS SIN RESULTADO ──
            List<modelo.ResultadoAnalisis> resultadosFiltrados = new ArrayList<>();
            for (modelo.ResultadoAnalisis r : resultadosOriginales) {
                String res = r.getResultado();
                String nombre = r.getNombrePrueba() != null ? r.getNombrePrueba() : "";
                boolean esSubtitulo = nombre.startsWith("---") && nombre.endsWith("---");
                if (esSubtitulo || (res != null && !res.trim().isEmpty())) {
                    resultadosFiltrados.add(r);
                }
            }

            if (resultadosFiltrados.isEmpty()) {
                javax.swing.JOptionPane.showMessageDialog(null, "No hay resultados cargados para imprimir");
                return;
            }

            // ── TÍTULOS DINÁMICOS Y FORMATEO ──
            List<modelo.ResultadoAnalisis> listaConTitulos = new ArrayList<>();
            String codigoPadreActual = "";

            for (modelo.ResultadoAnalisis r : resultadosFiltrados) {
                String codigoFila = r.getCodigo();

                if (r.getReferencia() != null && r.getReferencia().contains(";")) {
                    r.setReferencia(r.getReferencia().replace(";", "\n").trim());
                }

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

                    modelo.ResultadoAnalisis titulo = new modelo.ResultadoAnalisis();
                    titulo.setCodigo("");
                    titulo.setNombrePrueba(nombreTitulo);
                    titulo.setResultado(" ");
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

            // ── SELECCIÓN DE REPORTE SEGÚN CONFIGURACIÓN ──
            String tamanoHoja = configDAO.getValor("print_tamano");
            String orientacion = configDAO.getValor("print_orientacion");

            if (tamanoHoja == null || tamanoHoja.trim().isEmpty()) {
                tamanoHoja = "A4";
            }
            if (orientacion == null || orientacion.trim().isEmpty()) {
                orientacion = "Vertical";
            }

            String rutaReporte;
            String key = tamanoHoja + "_" + orientacion;

            switch (key) {
                case "A4_Vertical":
                    rutaReporte = "/reportes/informe_A4_vertical.jrxml";
                    break;
                case "A4_Horizontal":
                    rutaReporte = "/reportes/informe_A4_horizontal.jrxml";
                    break;
                case "A5_Vertical":
                    rutaReporte = "/reportes/informe_A5_vertical.jrxml";
                    break;
                case "A5_Horizontal":
                    rutaReporte = "/reportes/informe_A5_horizontal.jrxml";
                    break;
                default:
                    rutaReporte = "/reportes/informe_A4_vertical.jrxml";
                    break;
            }

            java.io.InputStream reporteStream = getClass().getResourceAsStream(rutaReporte);
            if (reporteStream == null) {
                javax.swing.JOptionPane.showMessageDialog(null, "Error: No se encontró el reporte: " + rutaReporte);
                return;
            }

            // ── COMPILACIÓN Y PARÁMETROS ──
            net.sf.jasperreports.engine.JasperReport jasperReport = net.sf.jasperreports.engine.JasperCompileManager.compileReport(reporteStream);
            java.util.Map<String, Object> params = new HashMap<>();

            // ── RECONSTRUCCIÓN DEL MÉDICO PARA EL PDF ──
            String matriculaDB = analisis.getMedicoSolicitante();
            String medicoParaPDF = "";
            
            if (matriculaDB != null && !matriculaDB.trim().isEmpty() && !matriculaDB.equals("-")) {
                modelo.Medico medicoReal = medicoDAO.buscarPorMatricula(matriculaDB.trim());
                if (medicoReal != null) {
                    medicoParaPDF = medicoReal.getNombreMedico() + " " + medicoReal.getApellidoMedico() + " (mp. " + medicoReal.getMatricula() + ")";
                } else {
                    medicoParaPDF = matriculaDB; // Por si el médico se borró de la base de datos
                }
            }
            params.put("medicoSolicitante", medicoParaPDF);
            
            params.put("fechaAnalisis", fechaImpresion);
            params.put("precio", analisis.getPrecio());

            params.put("labNombre", configDAO.getValor("lab_nombre") != null ? configDAO.getValor("lab_nombre") : "BIOTEC LABORATORIOS");
            params.put("labDireccion", configDAO.getValor("lab_direccion") != null ? configDAO.getValor("lab_direccion") : "");
            params.put("labLocalidad", configDAO.getValor("lab_localidad") != null ? configDAO.getValor("lab_localidad") : "");
            params.put("labTelefono", configDAO.getValor("lab_telefono") != null ? configDAO.getValor("lab_telefono") : "");
            params.put("labBioquimico", configDAO.getValor("lab_bioquimico") != null ? configDAO.getValor("lab_bioquimico") : "");
            params.put("labMatricula", configDAO.getValor("lab_matricula") != null ? configDAO.getValor("lab_matricula") : "");

            params.put("pacienteNombre", paciente.getApellido() + " " + paciente.getNombre());
            params.put("pacienteDni", paciente.getDni());

            // ── LOGO Y FIRMA ──
            String valLogo = configDAO.getValor("print_logo");
            boolean incluirLogo = valLogo != null && (valLogo.trim().equalsIgnoreCase("true") || valLogo.trim().equals("1"));
            String rutaLogo = configDAO.getValor("lab_logo");

            if (incluirLogo && rutaLogo != null && !rutaLogo.trim().isEmpty()) {
                java.io.File logoFile = new java.io.File(rutaLogo);
                if (logoFile.exists() && logoFile.isFile()) {
                    try {
                        java.io.FileInputStream fis = new java.io.FileInputStream(logoFile);
                        streamsAbiertos.add(fis);
                        params.put("urlLogo", fis);
                    } catch (Exception e) {
                        params.put("urlLogo", null);
                    }
                } else {
                    params.put("urlLogo", null);
                }
            } else {
                params.put("urlLogo", null);
            }

            String rutaFirma = configDAO.getValor("lab_firma");
            if (rutaFirma != null && !rutaFirma.trim().isEmpty()) {
                java.io.File firmaFile = new java.io.File(rutaFirma);
                if (firmaFile.exists() && firmaFile.isFile()) {
                    try {
                        java.io.FileInputStream fis = new java.io.FileInputStream(firmaFile);
                        streamsAbiertos.add(fis);
                        params.put("urlFirma", fis);
                    } catch (Exception e) {
                        params.put("urlFirma", null);
                    }
                } else {
                    params.put("urlFirma", null);
                }
            } else {
                params.put("urlFirma", null);
            }

            // ── LLENAR EL REPORTE ──
            net.sf.jasperreports.engine.data.JRBeanCollectionDataSource ds = new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource(listaConTitulos);
            net.sf.jasperreports.engine.JasperPrint jasperPrint = net.sf.jasperreports.engine.JasperFillManager.fillReport(jasperReport, params, ds);

            // ── EXPORTAR PDF AUTOMÁTICO ──
            boolean pdfGuardadoExito = false;
            String rutaPdfGenerado = "";

            try {
                String valAuto = configDAO.getValor("print_auto");
                boolean autoPrint = valAuto != null && (valAuto.trim().equalsIgnoreCase("true") || valAuto.trim().equals("1"));
                String carpetaPdf = configDAO.getValor("ruta_pdf");

                if (autoPrint && carpetaPdf != null && !carpetaPdf.trim().isEmpty()) {

                    if (carpetaPdf.startsWith("~/")) {
                        carpetaPdf = System.getProperty("user.home") + carpetaPdf.substring(1);
                    }

                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy");
                    String fechaEstudio = sdf.format(analisis.getFecha());
                    String nombreArchivo = paciente.getApellido().replace(" ", "")
                            + paciente.getNombre().replace(" ", "")
                            + "_" + fechaEstudio + ".pdf";
                    nombreArchivo = nombreArchivo.replace("ñ", "n").replace("Ñ", "N").replace("á", "a").replace("é", "e")
                            .replace("í", "i").replace("ó", "o").replace("ú", "u");

                    java.io.File folder = new java.io.File(carpetaPdf);
                    if (!folder.exists()) {
                        folder.mkdirs();
                    }

                    if (folder.exists()) {
                        java.io.File pdfFile = new java.io.File(folder, nombreArchivo);
                        net.sf.jasperreports.engine.JasperExportManager.exportReportToPdfFile(jasperPrint, pdfFile.getAbsolutePath());
                        System.out.println("PDF guardado en: " + pdfFile.getAbsolutePath());

                        pdfGuardadoExito = true;
                        rutaPdfGenerado = pdfFile.getAbsolutePath();
                    } else {
                        System.err.println("No se pudo crear la carpeta: " + carpetaPdf);
                    }
                }
            } catch (Exception exPdf) {
                System.err.println("Error al exportar el PDF: " + exPdf.getMessage());
                exPdf.printStackTrace();
            }

            // ── MOSTRAR VISOR (CON PARCHE ANTI-CRASH Y WHATSAPP) ──
            final JDialog dialogFinal = new JDialog((java.awt.Frame) null, "Visor de Informe - BIOTEC", true);
            dialogFinal.setSize(1024, 800);
            dialogFinal.setLocationRelativeTo(null);

            net.sf.jasperreports.swing.JRViewer viewer = new net.sf.jasperreports.swing.JRViewer(jasperPrint);

            try {
                javax.print.PrintService[] impresoras = java.awt.print.PrinterJob.lookupPrintServices();
                if (impresoras == null || impresoras.length == 0) {
                    for (java.awt.Component c : viewer.getComponents()) {
                        if (c.getClass().getName().contains("JRViewerToolbar") || c instanceof javax.swing.JToolBar) {
                            java.awt.Container toolbar = (java.awt.Container) c;
                            for (java.awt.Component boton : toolbar.getComponents()) {
                                if (boton instanceof javax.swing.JButton) {
                                    javax.swing.JButton btn = (javax.swing.JButton) boton;
                                    String tooltip = btn.getToolTipText();
                                    if (tooltip != null && (tooltip.toLowerCase().contains("print") || tooltip.toLowerCase().contains("imprimir"))) {
                                        btn.setEnabled(false);
                                        btn.setToolTipText("Impresión directa deshabilitada: No se detectaron impresoras instaladas en el sistema operativo.");
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception exPrintCheck) {
                System.err.println("Error al mitigar botón de impresión: " + exPrintCheck.getMessage());
            }

            dialogFinal.getContentPane().add(viewer);

            final boolean finalPdfGuardado = pdfGuardadoExito;
            final String finalRutaPdf = rutaPdfGenerado;

            dialogFinal.addWindowListener(new java.awt.event.WindowAdapter() {

                @Override
                public void windowOpened(java.awt.event.WindowEvent e) {
                    if (finalPdfGuardado) {
                        Object[] opciones = {"Cerrar", "Enviar por WhatsApp"};

                        int eleccion = javax.swing.JOptionPane.showOptionDialog(dialogFinal,
                                "✓ PDF guardado exitosamente en:\n" + finalRutaPdf + "\n\n¿Desea notificar al paciente ahora?",
                                "Informe Generado - BIOTEC",
                                javax.swing.JOptionPane.YES_NO_OPTION,
                                javax.swing.JOptionPane.INFORMATION_MESSAGE,
                                null,
                                opciones,
                                opciones[0]);

                        if (eleccion == 1) {
                            String celular = paciente.getCelular();

                            if (celular != null && !celular.trim().isEmpty()) {
                                try {
                                    String celularLimpio = celular.replaceAll("[^0-9]", "");
                                    String mensaje = "Hola *" + paciente.getNombre().trim() + "*, te informamos que los resultados de tus análisis clínicos en *BIOTEC* ya están listos. \n\nPuedes pasar a retirarlos o solicitarlos en formato PDF respondiendo a este mensaje.";
                                    String url = "https://api.whatsapp.com/send?phone=" + celularLimpio + "&text=" + java.net.URLEncoder.encode(mensaje, "UTF-8");

                                    if (java.awt.Desktop.isDesktopSupported() && java.awt.Desktop.getDesktop().isSupported(java.awt.Desktop.Action.BROWSE)) {
                                        java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
                                    } else {
                                        javax.swing.JOptionPane.showMessageDialog(dialogFinal, "No se pudo abrir el navegador automáticamente en este sistema.");
                                    }
                                } catch (Exception exWsp) {
                                    javax.swing.JOptionPane.showMessageDialog(dialogFinal, "Error al intentar abrir WhatsApp: " + exWsp.getMessage());
                                }
                            } else {
                                javax.swing.JOptionPane.showMessageDialog(dialogFinal, "El paciente no tiene un número de celular registrado en el sistema.", "Atención", javax.swing.JOptionPane.WARNING_MESSAGE);
                            }
                        }
                    }
                }

                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    dialogFinal.dispose();
                }
            });

            dialogFinal.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
            javax.swing.SwingUtilities.invokeLater(() -> {
                javax.swing.JOptionPane.showMessageDialog(null, "Error al generar informe: " + e.getMessage());
            });
        } finally {
            for (java.io.FileInputStream fis : streamsAbiertos) {
                try {
                    if (fis != null) {
                        fis.close();
                    }
                } catch (Exception ex) {
                }
            }
        }
    }

    private String getValorConfig(String clave, String defaultValue) {
        try {
            String valor = configDAO.getValor(clave);
            return (valor != null && !valor.trim().isEmpty()) ? valor : defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
