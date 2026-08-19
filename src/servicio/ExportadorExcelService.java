package servicio;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ExportadorExcelService {

    public static boolean exportarTablaACSV(Object[][] datos, String totalAnalisis, String totalFacturado, 
                                            String periodo, boolean incluirPracticas, java.awt.Component padre) {
        if (datos == null || datos.length == 0) {
            return false;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Reporte Estadístico");
        fileChooser.setSelectedFile(new File("Reporte_" + new java.text.SimpleDateFormat("yyyyMMdd_HHmm").format(new java.util.Date()) + ".csv"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivo CSV (Excel) (*.csv)", "csv"));

        if (fileChooser.showSaveDialog(padre) != JFileChooser.APPROVE_OPTION) {
            return false;
        }

        File archivo = fileChooser.getSelectedFile();
        if (!archivo.getName().toLowerCase().endsWith(".csv")) {
            archivo = new File(archivo.getAbsolutePath() + ".csv");
        }

        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(archivo), StandardCharsets.UTF_8))) {
            // El BOM (Byte Order Mark) fuerza a Excel a leer los acentos y las Ñ automáticamente
            bw.write('\uFEFF');

            // Encabezados institucionales
            bw.write("REPORTE ESTADÍSTICO Y FACTURACIÓN - BIOTEC LABORATORIOS"); bw.newLine();
            bw.write("Fecha de emisión:;" + new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date())); bw.newLine();
            bw.write("Período Analizado:;" + periodo); bw.newLine();
            bw.write("Total Análisis:;" + totalAnalisis + ";Total Facturado:;" + totalFacturado); bw.newLine();
            bw.newLine();

            // Cabeceras dinámicas dependiendo si el usuario quiso o no las prácticas
            String cabeceras = incluirPracticas 
                    ? "ID;FECHA;DNI;APELLIDO;NOMBRE;MÉDICO;OBRA SOCIAL;PRÁCTICAS" 
                    : "ID;FECHA;DNI;APELLIDO;NOMBRE;MÉDICO;OBRA SOCIAL";
            bw.write(cabeceras); bw.newLine();

            // Filas
            for (Object[] fila : datos) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < fila.length; i++) {
                    String valor = (fila[i] != null) ? fila[i].toString() : "";
                    // Escapar textos que tengan punto y coma (para que no rompan las columnas)
                    if (valor.contains(";") || valor.contains("\"") || valor.contains("\n")) {
                        valor = "\"" + valor.replace("\"", "\"\"") + "\"";
                    }
                    sb.append(valor);
                    if (i < fila.length - 1) sb.append(";");
                }
                bw.write(sb.toString()); bw.newLine();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}