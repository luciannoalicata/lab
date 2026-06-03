package dao;

import modelo.Conexion;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ConfiguracionDAO {
    private Conexion con;

    public ConfiguracionDAO(Conexion con) {
        this.con = con;
    }

    public String getValor(String clave) {
        String sql = "SELECT valor FROM configuracion WHERE clave = ?";
        try {
            PreparedStatement ps = con.getConnection().prepareStatement(sql);
            ps.setString(1, clave);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("valor");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

   public void guardar(String clave, String valor) {
    // Esta consulta intenta insertar, pero si la clave existe, actualiza el valor.
    String sql = "INSERT INTO configuracion (clave, valor) VALUES (?, ?) " +
                 "ON DUPLICATE KEY UPDATE valor = ?";
    try {
        PreparedStatement ps = con.getConnection().prepareStatement(sql);
        ps.setString(1, clave);
        ps.setString(2, valor);
        ps.setString(3, valor); // Para el UPDATE
        ps.executeUpdate();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
}