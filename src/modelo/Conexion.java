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

            // Construir detalle de qué falta
            StringBuilder faltantes = new StringBuilder();
            if (host     == null) faltantes.append("  • db.host   → no encontrado\n");
            if (port     == null) faltantes.append("  • db.port   → no encontrado\n");
            if (dbName   == null) faltantes.append("  • db.name   → no encontrado\n");
            if (usuario  == null) faltantes.append("  • db.user   → no encontrado\n");
            if (clave    == null) faltantes.append("  • db.pass   → no encontrado\n");

            mostrarErrorGrafico(
                "Configuración incompleta",
                "El archivo config.properties fue encontrado pero le faltan propiedades obligatorias:\n\n"
                + faltantes.toString()
                + "\nVerifique que el archivo contenga todas las claves necesarias "
                + "y vuelva a iniciar el sistema.\n\n"
                + "Contacte al administrador si el problema persiste.",
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
                    false
                );
                return null;
            }

            String cadenaConexion =
                "jdbc:mysql://" + host + ":" + port + "/" + dbName
                + "?serverTimeZone=UTC"
                + "&useSSL=false"
                + "&allowPublicKeyRetrieval=true"
                + "&connectTimeout=20000"
                + "&socketTimeout=20000";

            conexion = DriverManager.getConnection(cadenaConexion, usuario, clave);
            System.out.println("Conexion establecida a la Base de Datos: " + host);

        } catch (SQLException ex) {
            System.err.println("Error al conectarse a la Base de Datos: " + ex.getMessage());
            conexion = null;

            String titulo;
            String mensaje;

            // Clasificar el error según el código SQL o el mensaje
            String msgLower = ex.getMessage() != null ? ex.getMessage().toLowerCase() : "";
            int errorCode   = ex.getErrorCode();

            if (msgLower.contains("communications link failure")
                    || msgLower.contains("connection refused")
                    || msgLower.contains("connect timed out")
                    || msgLower.contains("timed out")) {
                // Servidor caído, apagado o inaccesible
                titulo  = "No se pudo conectar al servidor";
                mensaje = "BIOTEC no logró comunicarse con el servidor de base de datos.\n\n"
                        + "Posibles causas:\n"
                        + "  • El servidor MySQL no está en ejecución\n"
                        + "  • El host o el puerto son incorrectos\n"
                        + "  • Un firewall está bloqueando la conexión\n"
                        + "  • La red no está disponible\n\n"
                        + "Servidor: " + host + ":" + port + "\n\n"
                        + "Verifique que MySQL esté iniciado y vuelva a intentarlo.\n"
                        + "Contacte al administrador si el problema persiste.";

            } else if (errorCode == 1045
                    || msgLower.contains("access denied")) {
                // Usuario o contraseña incorrectos
                titulo  = "Acceso denegado a la base de datos";
                mensaje = "El servidor MySQL rechazó las credenciales de conexión.\n\n"
                        + "Posibles causas:\n"
                        + "  • El usuario o la contraseña en config.properties son incorrectos\n"
                        + "  • El usuario no tiene permisos sobre la base de datos '"
                                + dbName + "'\n\n"
                        + "Usuario utilizado: " + usuario + "\n\n"
                        + "Verifique las credenciales en config.properties\n"
                        + "y reinicie el sistema.";

            } else if (errorCode == 1049
                    || msgLower.contains("unknown database")) {
                // Base de datos no existe
                titulo  = "Base de datos no encontrada";
                mensaje = "El servidor MySQL está en línea pero la base de datos\n"
                        + "'" + dbName + "' no existe.\n\n"
                        + "Posibles causas:\n"
                        + "  • El nombre de la base de datos en config.properties es incorrecto\n"
                        + "  • El script de creación (schema.sql) no fue ejecutado\n\n"
                        + "Ejecute el script SQL de instalación y reinicie el sistema.\n"
                        + "Contacte al administrador si el problema persiste.";

            } else if (msgLower.contains("ssl") || msgLower.contains("certificate")) {
                // Error de SSL
                titulo  = "Error de seguridad en la conexión";
                mensaje = "Ocurrió un error relacionado con SSL al conectarse a MySQL.\n\n"
                        + "Verifique las opciones de SSL en config.properties\n"
                        + "o contacte al administrador del servidor.";

            } else {
                // Error desconocido
                titulo  = "Error de conexión a la base de datos";
                mensaje = "Ocurrió un error inesperado al conectarse a la base de datos.\n\n"
                        + "Detalle técnico:\n  " + ex.getMessage() + "\n\n"
                        + "Código de error MySQL: " + errorCode + "\n\n"
                        + "Contacte al administrador del sistema con esta información.";
            }

            mostrarErrorGrafico(titulo, mensaje, false);
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

    /**
     * Muestra un JOptionPane con ícono de error y mensaje descriptivo.
     *
     * @param titulo   Título de la ventana de error
     * @param mensaje  Mensaje detallado para el usuario
     * @param fatal    Si es true, el sistema no puede continuar
     */
    private void mostrarErrorGrafico(String titulo, String mensaje, boolean fatal) {
        // Asegurarse de mostrar en el hilo de Swing
        if (javax.swing.SwingUtilities.isEventDispatchThread()) {
            mostrarDialogo(titulo, mensaje, fatal);
        } else {
            try {
                javax.swing.SwingUtilities.invokeAndWait(
                    () -> mostrarDialogo(titulo, mensaje, fatal)
                );
            } catch (Exception e) {
                // Si falla el diálogo, al menos imprimir en consola
                System.err.println("[" + titulo + "] " + mensaje);
            }
        }
    }

    private void mostrarDialogo(String titulo, String mensaje, boolean fatal) {
        // Ícono BIOTEC si está disponible
        javax.swing.ImageIcon icono = null;
        try {
            java.net.URL url = getClass().getResource("/reportes/img/logo_google.png");
            if (url != null) {
                java.awt.Image img = new javax.swing.ImageIcon(url)
                    .getImage().getScaledInstance(48, 48, java.awt.Image.SCALE_SMOOTH);
                icono = new javax.swing.ImageIcon(img);
            }
        } catch (Exception ignored) {}

        // Panel con texto monoespacio para mayor legibilidad
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