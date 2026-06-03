package modelo;
import java.sql.*;
/**
 *
 * @author luciano
 */
public class Conexion {
    private Connection conexion;
    private String cadenaConexion = "jdbc:mysql://localhost:3306/laboratorio?serverTimeZone=UTC";
    private String USUARIO = "root";
    private String CLAVE = "1152";
    
    public Connection conectar(){
        try {
            conexion = DriverManager.getConnection(cadenaConexion, USUARIO, CLAVE);
            System.out.println("Conexión establecida a la Base de Datos.");
        } catch (SQLException ex){System.out.println("Error al conectarse a la Base de Datos. " + ex);}
        return conexion;
    }
    public Connection getConnection(){
        try {
            if(conexion== null || conexion.isClosed()){
                conectar();
            }
        }catch (SQLException e){System.out.println("Error de conexión: " + e);
            conectar();
        }
        return conexion;
    }
}
