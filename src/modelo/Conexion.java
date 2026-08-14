package modelo;

// @author lucianoalicata

import java.sql.*;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;

public class Conexion {
    private Connection conexion;
    private String host, port, dbName, usuario, clave;

    public Conexion() {
        cargarConfiguracion();
    }

    private void cargarConfiguracion() {
        Properties prop = new Properties();

        String userDir = System.getProperty("user.dir");
        System.out.println("Directorio de trabajo: " + userDir);

        String[] rutas = {
            "config.properties",
            "src/config.properties",
            userDir + "/config.properties.txt",
            userDir + "/src/config.properties.txt",
            "C:/Users/Luciano Alicata/Desktop/Laboratorio/config.properties.txt",
            "C:/Users/Luciano Alicata/Desktop/Laboratorio/src/config.properties.txt"
        };

        boolean cargado = false;

        for (String ruta : rutas) {
            try {
                File file = new File(ruta);
                if (file.exists()) {
                    try (FileInputStream input = new FileInputStream(file)) {
                        prop.load(input);
                        System.out.println("config.properties cargado desde: " + ruta);
                        cargado = true;
                        break;
                    }
                }
            } catch (IOException e) {
                // Continuar con la siguiente ruta
            }
        }

        if (!cargado) {
            System.err.println("Error: No se encontró config.properties.");
            mostrarErrorGrafico(
                "Archivo de configuración no encontrado",
                "No se encontró el archivo config.properties en ninguna de las rutas conocidas.\n\n"
                + "El sistema no puede iniciar sin este archivo.\n\n"
                + "Rutas buscadas:\n"
                + String.join("\n", rutas)
                + "\n\nContacte al administrador del sistema.",
                true
            );
            return;
        }

        this.host     = prop.getProperty("db.host");
        this.port     = prop.getProperty("db.port");
        this.dbName   = prop.getProperty("db.name");
        this.usuario  = prop.getProperty("db.user");
        this.clave    = prop.getProperty("db.pass");

        if (host == null || port == null || dbName == null || usuario == null || clave == null) {
            System.err.println("Error: Faltan propiedades en config.properties");

            StringBuilder faltantes = new StringBuilder();
            if (host    == null) faltantes.append("  • db.host   → no encontrado\n");
            if (port    == null) faltantes.append("  • db.port   → no encontrado\n");
            if (dbName  == null) faltantes.append("  • db.name   → no encontrado\n");
            if (usuario == null) faltantes.append("  • db.user   → no encontrado\n");
            if (clave   == null) faltantes.append("  • db.pass   → no encontrado\n");

            mostrarErrorGrafico(
                "Configuración incompleta",
                "El archivo config.properties fue encontrado pero le faltan propiedades obligatorias:\n\n"
                + faltantes.toString()
                + "\nVerifique que el archivo contenga todas las claves necesarias "
                + "y vuelva a iniciar el sistema.\n\n"
                + "Contacte al administrador si el problema persists.",
                true
            );
        } else {
            System.out.println("Configuracion cargada correctamente.");
            System.out.println("   Host: " + host + ":" + port + "/" + dbName);
        }
    }

    public Connection conectar() {
        try {
            if (host == null || port == null || dbName == null || usuario == null || clave == null) {
                System.err.println("Error: Datos de conexión incompletos.");
                mostrarErrorGrafico(
                    "Datos de conexión incompletos",
                    "El sistema no puede conectarse a la base de datos porque faltan\n"
                    + "parámetros de configuración.\n\n"
                    + "Verifique el archivo config.properties y reinicie BIOTEC.\n\n"
                    + "Contacte al administrador del sistema.",
                    true // Cambiado a TRUE para cerrar si no hay datos de conexión
                );
                return null;
            }

            // Redujimos los timeouts a 5000 ms (5 segundos) para respuesta rápida ante caídas de servidor
            String cadenaConexion =
                "jdbc:mysql://" + host + ":" + port + "/" + dbName
                + "?serverTimeZone=UTC"
                + "&useSSL=false"
                + "&allowPublicKeyRetrieval=true"
                + "&connectTimeout=5000"
                + "&socketTimeout=5000";

            conexion = DriverManager.getConnection(cadenaConexion, usuario, clave);
            System.out.println("Conexion establecida a la Base de Datos: " + host);

        } catch (SQLException ex) {
            System.err.println("Error al conectarse a la Base de Datos: " + ex.getMessage());
            conexion = null;

            String titulo;
            String mensaje;

            String msgLower = ex.getMessage() != null ? ex.getMessage().toLowerCase() : "";
            int errorCode   = ex.getErrorCode();

            if (msgLower.contains("communications link failure")
                    || msgLower.contains("connection refused")
                    || msgLower.contains("connect timed out")
                    || msgLower.contains("timed out")) {
                
                titulo  = "No se pudo conectar al servidor";
                mensaje = "BIOTEC perdió o no logró establecer comunicación con el servidor de base de datos.\n\n"
                        + "Posibles causas:\n"
                        + "  • La PC servidor fue apagada o reiniciada\n"
                        + "  • El servicio MySQL se detuvo en el servidor\n"
                        + "  • Un problema de red o cable desconectado\n"
                        + "  • Un firewall está bloqueando la conexión\n\n"
                        + "Servidor: " + host + ":" + port + "\n\n"
                        + "El sistema se cerrará. Verifique que la PC servidor y MySQL estén activos e intente ingresar nuevamente.";

            } else if (errorCode == 1045 || msgLower.contains("access denied")) {
                
                titulo  = "Acceso denegado a la base de datos";
                mensaje = "El servidor MySQL rechazó las credenciales de conexión.\n\n"
                        + "Posibles causas:\n"
                        + "  • El usuario o la contraseña en config.properties son incorrectos\n"
                        + "  • El usuario no tiene permisos sobre la base de datos '" + dbName + "'\n\n"
                        + "Usuario utilizado: " + usuario + "\n\n"
                        + "Verifique las credenciales en config.properties y reinicie el sistema.";

            } else if (errorCode == 1049 || msgLower.contains("unknown database")) {
                
                titulo  = "Base de datos no encontrada";
                mensaje = "El servidor MySQL está en línea pero la base de datos '" + dbName + "' no existe.\n\n"
                        + "Verifique la configuración o ejecute el script SQL de instalación.";

            } else if (msgLower.contains("ssl") || msgLower.contains("certificate")) {
                
                titulo  = "Error de seguridad en la conexión";
                mensaje = "Ocurrió un error relacionado con SSL al conectarse a MySQL.\n\n"
                        + "Verifique las opciones de SSL en config.properties.";

            } else {
                
                titulo  = "Error de conexión a la base de datos";
                mensaje = "Ocurrió un error inesperado al conectarse a la base de datos.\n\n"
                        + "Detalle técnico:\n  " + ex.getMessage() + "\n\n"
                        + "Código de error MySQL: " + errorCode;
            }

            // Cambiado a TRUE en todos los fallos críticos para forzar la salida limpia del programa al presionar Aceptar
            mostrarErrorGrafico(titulo, mensaje, true);
        }

        return conexion;
    }

    public Connection getConnection() {
        try {
            if (conexion == null || conexion.isClosed()) {
                conectar();
            }
        } catch (SQLException e) {
            conectar();
        }
        return conexion;
    }

    private void mostrarErrorGrafico(String titulo, String mensaje, boolean fatal) {
        if (javax.swing.SwingUtilities.isEventDispatchThread()) {
            mostrarDialogo(titulo, mensaje, fatal);
        } else {
            try {
                javax.swing.SwingUtilities.invokeAndWait(
                    () -> mostrarDialogo(titulo, mensaje, fatal)
                );
            } catch (Exception e) {
                System.err.println("[" + titulo + "] " + mensaje);
                if (fatal) {
                    System.exit(1);
                }
            }
        }
    }

    private void mostrarDialogo(String titulo, String mensaje, boolean fatal) {
        javax.swing.ImageIcon icono = null;
        try {
            java.net.URL url = getClass().getResource("/reportes/img/logo_sw.png");
            if (url != null) {
                java.awt.Image img = new javax.swing.ImageIcon(url)
                    .getImage().getScaledInstance(48, 48, java.awt.Image.SCALE_SMOOTH);
                icono = new javax.swing.ImageIcon(img);
            }
        } catch (Exception ignored) {}

        javax.swing.JTextArea area = new javax.swing.JTextArea(mensaje);
        area.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        area.setEditable(false);
        area.setOpaque(false);
        area.setLineWrap(false);
        area.setFocusable(false);

        javax.swing.JOptionPane.showMessageDialog(
            null,
            area,
            (fatal ? "⛔  " : "⚠  ") + "BIOTEC — " + titulo,
            javax.swing.JOptionPane.ERROR_MESSAGE,
            icono
        );

        if (fatal) {
            System.exit(1);
        }
    }
}