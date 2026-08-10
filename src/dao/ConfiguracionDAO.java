package dao;

import modelo.Conexion;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class ConfiguracionDAO {
    private Conexion con;

    public ConfiguracionDAO(Conexion con) {
        this.con = con;
    }

    public String getValor(String clave) {
        String sql = "SELECT valor FROM configuracion WHERE clave = ?";
        try (PreparedStatement ps = con.getConnection().prepareStatement(sql)) {
            ps.setString(1, clave);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("valor");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public void guardar(String clave, String valor) {
        String sql = "INSERT INTO configuracion (clave, valor) VALUES (?, ?) " +
                     "ON DUPLICATE KEY UPDATE valor = VALUES(valor)";
        try (PreparedStatement ps = con.getConnection().prepareStatement(sql)) {
            ps.setString(1, clave);
            ps.setString(2, valor);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public InputStream getValorBinario(String clave) {
        String sql = "SELECT valor_binario FROM configuracion WHERE clave = ?";
        try {
            PreparedStatement ps = con.getConnection().prepareStatement(sql);
            ps.setString(1, clave);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getBinaryStream("valor_binario");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean guardarBinario(String clave, File archivoImagen) {
        String sql = "INSERT INTO configuracion (clave, valor_binario) VALUES (?, ?) " +
                     "ON DUPLICATE KEY UPDATE valor_binario = VALUES(valor_binario)";
                     
        try (PreparedStatement ps = con.getConnection().prepareStatement(sql);
             FileInputStream fis = new FileInputStream(archivoImagen)) {
             
            ps.setString(1, clave);
            ps.setBinaryStream(2, fis, (int) archivoImagen.length());
            return ps.executeUpdate() > 0;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}