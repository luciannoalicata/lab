package servicio;

// @author lucianoalicata

import dao.AnalisisDAO;
import dao.ConfiguracionDAO;
import dao.DeterminacionDAO;
import dao.MedicoDAO;
import dao.PacienteDAO;
import dao.ResultadoAnalisisDAO;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.HashMap;
import javax.swing.JDialog;
import net.sf.jasperreports.engine.JRException;

public class ReporteService {

    private final ConfiguracionDAO configDAO;
    private final AnalisisDAO analisisDAO;
    private final PacienteDAO pacienteDAO;
    private final MedicoDAO medicoDAO;
    private final ResultadoAnalisisDAO resultadoDAO;
    private final DeterminacionDAO determinacionDAO;

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

        List<java.io.InputStream> streamsAbiertos = new ArrayList<>();
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

            System.setProperty("jasper.reports.compile.temp", System.getProperty("java.io.tmpdir"));

            net.sf.jasperreports.engine.JasperReport jasperReport = net.sf.jasperreports.engine.JasperCompileManager.compileReport(reporteStream);
            java.util.Map<String, Object> params = new HashMap<>();

            String matriculaDB = analisis.getMedicoSolicitante();
            String medicoParaPDF = "";

            if (matriculaDB != null && !matriculaDB.trim().isEmpty() && !matriculaDB.equals("-")) {
                modelo.Medico medicoReal = medicoDAO.buscarPorMatricula(matriculaDB.trim());
                if (medicoReal != null) {
                    medicoParaPDF = medicoReal.getNombreMedico() + " " + medicoReal.getApellidoMedico() + " (mp. " + medicoReal.getMatricula() + ")";
                } else {
                    medicoParaPDF = matriculaDB;
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

            String valLogo = configDAO.getValor("print_logo");
            boolean incluirLogo = valLogo != null && (valLogo.trim().equalsIgnoreCase("true") || valLogo.trim().equals("1"));

            if (incluirLogo) {
                java.io.InputStream streamLogo = configDAO.getValorBinario("lab_logo");
                if (streamLogo != null) {
                    streamsAbiertos.add(streamLogo);
                    params.put("urlLogo", streamLogo);
                } else {
                    params.put("urlLogo", null);
                }
            } else {
                params.put("urlLogo", null);
            }

            java.io.InputStream streamFirma = configDAO.getValorBinario("lab_firma");
            if (streamFirma != null) {
                streamsAbiertos.add(streamFirma);
                params.put("urlFirma", streamFirma);
            } else {
                params.put("urlFirma", null);
            }

            net.sf.jasperreports.engine.data.JRBeanCollectionDataSource ds = new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource(listaConTitulos);
            net.sf.jasperreports.engine.JasperPrint jasperPrint = net.sf.jasperreports.engine.JasperFillManager.fillReport(jasperReport, params, ds);

            boolean pdfGuardadoExito = false;
            String rutaPdfGenerado = "";

            try {
                String valAuto = configDAO.getValor("print_auto");
                boolean autoPrint = valAuto != null && (valAuto.trim().equalsIgnoreCase("true") || valAuto.trim().equals("1"));

                if (autoPrint) {
                    String rutaEscritorio = javax.swing.filechooser.FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath();
                    String carpetaPdf = rutaEscritorio + java.io.File.separator + "biotec_informes";

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
                        System.err.println("No se pudo crear la carpeta en el Escritorio: " + carpetaPdf);
                    }
                }
            } catch (JRException exPdf) {
                System.err.println("Error al exportar el PDF: " + exPdf.getMessage());
                exPdf.printStackTrace();
            }

            final JDialog dialogFinal = new JDialog((java.awt.Frame) null, "Visor de Informe - BIOTEC", true);
            dialogFinal.setSize(800, 600);
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
                        // ── AQUÍ AGREGAMOS LA OPCIÓN DE EMAIL ──
                        Object[] opciones = {"Cerrar", "Enviar por WhatsApp", "Enviar por Email"};

                        int eleccion = javax.swing.JOptionPane.showOptionDialog(dialogFinal,
                                "PDF guardado exitosamente en:\n" + finalRutaPdf + "\n\n¿Desea notificar al paciente ahora?",
                                "Informe Generado - BIOTEC",
                                javax.swing.JOptionPane.DEFAULT_OPTION,
                                javax.swing.JOptionPane.INFORMATION_MESSAGE,
                                null,
                                opciones,
                                opciones[0]);

                        if (eleccion == 1) {
                            // ── LÓGICA DE WHATSAPP ──
                            try {
                                String celular = paciente.getCelular();
                                String mensaje = "Hola *" + paciente.getNombre().trim() + "*, te informamos que los resultados de tus análisis clínicos en *BIOTEC* ya están listos. Podés pasar a retirarlos en el Laboratorio (Av. Sarmiento 602, Aguilares). \n\nBIOTEC.";
                                String mensajeCodificado = java.net.URLEncoder.encode(mensaje, "UTF-8");
                                String url;

                                if (celular != null && !celular.trim().isEmpty()) {
                                    String celularLimpio = celular.replaceAll("[^0-9]", "");
                                    url = "https://api.whatsapp.com/send?phone=" + celularLimpio + "&text=" + mensajeCodificado;
                                } else {
                                    url = "https://api.whatsapp.com/send?text=" + mensajeCodificado;
                                }

                                if (java.awt.Desktop.isDesktopSupported() && java.awt.Desktop.getDesktop().isSupported(java.awt.Desktop.Action.BROWSE)) {
                                    java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
                                } else {
                                    javax.swing.JOptionPane.showMessageDialog(dialogFinal, "No se pudo abrir el navegador automáticamente en este sistema.");
                                }
                            } catch (Exception exWsp) {
                                javax.swing.JOptionPane.showMessageDialog(dialogFinal, "Error al intentar abrir WhatsApp: " + exWsp.getMessage());
                            }
                        } else if (eleccion == 2) {
                            // ── LÓGICA DE EMAIL ──
                            String emailDestino = javax.swing.JOptionPane.showInputDialog(dialogFinal,
                                    "Ingrese el correo electrónico del paciente:",
                                    "Enviar Informe por Email",
                                    javax.swing.JOptionPane.QUESTION_MESSAGE);

                            if (emailDestino != null && !emailDestino.trim().isEmpty()) {
                                enviarEmailConAdjunto(emailDestino, finalRutaPdf, paciente.getNombre());
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
            for (java.io.InputStream is : streamsAbiertos) {
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (Exception ex) {
                }
            }
        }
    }
    
    private void enviarEmailConAdjunto(String destinatario, String rutaPdf, String nombrePaciente) {
        final String remitente = "bioteclaboratoriobq@gmail.com";
        final String password = "zdzt wlcd oxsr ylsk";

        java.util.Properties props = new java.util.Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        javax.mail.Session session = javax.mail.Session.getInstance(props,
                new javax.mail.Authenticator() {
            @Override
            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                return new javax.mail.PasswordAuthentication(remitente, password);
            }
        });

        new Thread(() -> {
            try {
                javax.mail.Message mensaje = new javax.mail.internet.MimeMessage(session);
                mensaje.setFrom(new javax.mail.internet.InternetAddress(remitente, "Laboratorio BIOTEC"));
                mensaje.setRecipients(javax.mail.Message.RecipientType.TO, javax.mail.internet.InternetAddress.parse(destinatario));
                mensaje.setSubject("Resultados de Análisis Clínicos - BIOTEC");

                javax.mail.BodyPart textoParte = new javax.mail.internet.MimeBodyPart();
                textoParte.setText("Hola " + nombrePaciente.trim() + ",\n\nAdjuntamos en formato PDF los resultados de tus análisis clínicos realizados en BIOTEC.\n\nSaludos cordiales,\nLaboratorio BIOTEC.\nAv. Sarmiento 602, Aguilares.");

                javax.mail.BodyPart adjuntoParte = new javax.mail.internet.MimeBodyPart();
                javax.activation.DataSource source = new javax.activation.FileDataSource(rutaPdf);
                adjuntoParte.setDataHandler(new javax.activation.DataHandler(source));

                java.io.File archivoPdf = new java.io.File(rutaPdf);
                adjuntoParte.setFileName(archivoPdf.getName());

                javax.mail.Multipart multipart = new javax.mail.internet.MimeMultipart();
                multipart.addBodyPart(textoParte);
                multipart.addBodyPart(adjuntoParte);

                mensaje.setContent(multipart);

                javax.mail.Transport.send(mensaje);

                javax.swing.SwingUtilities.invokeLater(() -> {
                    javax.swing.JOptionPane.showMessageDialog(null, "El informe fue enviado exitosamente con su adjunto a:\n" + destinatario);
                });

            } catch (Exception e) {
                e.printStackTrace();
                javax.swing.SwingUtilities.invokeLater(() -> {
                    javax.swing.JOptionPane.showMessageDialog(null, "Error al enviar el correo:\n" + e.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }
}