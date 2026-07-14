package modelo;

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
        
        // Obtener la ruta donde se está ejecutando el programa
        String userDir = System.getProperty("user.dir");
        System.out.println("Directorio de trabajo: " + userDir);
        
        // Lista de rutas donde buscar
        String[] rutas = {
            "config.properties",                                    // Raíz del proyecto
            "src/config.properties",                                // Carpeta src
            userDir + "/config.properties.txt",                         // Directorio de trabajo
            userDir + "/src/config.properties.txt",                     // src en directorio de trabajo
            "C:/Users/Luciano Alicata/Desktop/Laboratorio/config.properties.txt", // Ruta absoluta
            "C:/Users/Luciano Alicata/Desktop/Laboratorio/src/config.properties.txt"
        };
        
        boolean cargado = false;
        
        for (String ruta : rutas) {
            try {
                File file = new File(ruta);
                if (file.exists()) {
                    try (FileInputStream input = new FileInputStream(file)) {
                        prop.load(input);
                        System.out.println("✅ config.properties cargado desde: " + ruta);
                        cargado = true;
                        break;
                    }
                }
            } catch (IOException e) {
                // Continuar con la siguiente ruta
            }
        }
        
        if (!cargado) {
            System.err.println("❌ Error: No se encontró config.properties.");
            System.err.println("Rutas buscadas:");
            for (String ruta : rutas) {
                System.err.println("   - " + ruta);
            }
            return;
        }
        
        this.host = prop.getProperty("db.host");
        this.port = prop.getProperty("db.port");
        this.dbName = prop.getProperty("db.name");
        this.usuario = prop.getProperty("db.user");
        this.clave = prop.getProperty("db.pass");
        
        if (host == null || port == null || dbName == null || usuario == null || clave == null) {
            System.err.println("❌ Error: Faltan propiedades en config.properties");
            System.err.println("   host=" + host);
            System.err.println("   port=" + port);
            System.err.println("   dbName=" + dbName);
            System.err.println("   usuario=" + usuario);
            System.err.println("   clave=" + (clave != null ? "***" : "null"));
        } else {
            System.out.println("✅ Configuración cargada correctamente.");
            System.out.println("   Host: " + host + ":" + port + "/" + dbName);
        }
    }

    public Connection conectar() {
        try {
            if (host == null || port == null || dbName == null || usuario == null || clave == null) {
                System.err.println("❌ Error: Datos de conexión incompletos.");
                return null;
            }
            
            String cadenaConexion = "jdbc:mysql://" + host + ":" + port + "/" + dbName + 
                                    "?serverTimeZone=UTC&useSSL=false&allowPublicKeyRetrieval=true";
            conexion = DriverManager.getConnection(cadenaConexion, usuario, clave);
            System.out.println("✅ Conexión establecida a la Base de Datos: " + host);
        } catch (SQLException ex) {
            System.err.println("❌ Error al conectarse a la Base de Datos: " + ex.getMessage());
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
}