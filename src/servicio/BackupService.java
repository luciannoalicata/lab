package servicio;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

public class BackupService {

   public static boolean crearBackup(String rutaCarpeta) {
    try {
        String fecha = new SimpleDateFormat("dd_MM_yyyy_HHmm").format(new Date());
        String nombreArchivo = "backup_biotec_" + fecha + ".sql";
        File archivoDestino = new File(rutaCarpeta, nombreArchivo);

        // Usamos la ruta de XAMPP que es la que tiene tus datos
        String pathMysqldump = "/opt/lampp/bin/mysqldump"; 
        
        if (!new File(pathMysqldump).exists()) {
            pathMysqldump = "mysqldump"; // Si no, intentamos con el global
        }

        List<String> comando = new ArrayList<>();
        comando.add(pathMysqldump);
        comando.add("-h");
        comando.add("127.0.0.1"); // FORZAMOS IP para evitar error de socket
        comando.add("-P");
        comando.add("3306");      // Puerto de MySQL
        comando.add("-u");
        comando.add("root");
        comando.add("-p1152");    // Clave sin espacio
        comando.add("laboratorio");
        comando.add("--result-file=" + archivoDestino.getAbsolutePath());

        ProcessBuilder pb = new ProcessBuilder(comando);
        pb.redirectErrorStream(true);
        
        Process process = pb.start();
        
        java.io.BufferedReader reader = new java.io.BufferedReader(
            new java.io.InputStreamReader(process.getInputStream())
        );
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println("MySQL Output: " + line);
        }

        int exitCode = process.waitFor();
        if (exitCode == 0) {
            System.out.println("✅ BACKUP EXITOSO: " + archivoDestino.getAbsolutePath());
            return true;
        } else {
            System.err.println("❌ ERROR: Código " + exitCode);
            return false;
        }

    } catch (Exception e) {
        System.err.println("❌ ERROR CRÍTICO: " + e.getMessage());
        return false;
    }
}
   
}