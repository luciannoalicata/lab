package dao;

import modelo.Conexion;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.sql.ResultSet;
import modelo.PasswordUtils;
import modelo.Usuario;

public class UsuarioDAO {

    private Conexion con;

    public UsuarioDAO(Conexion con) {
        this.con = con;
    }

    public boolean validarLogin(String user, String pass) {
        String sql = "SELECT password_hash FROM usuario WHERE username = ? AND activo = 1";
        try {
            PreparedStatement ps = con.getConnection().prepareStatement(sql);
            ps.setString(1, user);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String hashDB = rs.getString("password_hash");
                return PasswordUtils.verifyPassword(pass, hashDB);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean cambiarPassword(int idUsuario, String nuevaPass) {
        String sql = """
            UPDATE usuario
            SET password_hash = SHA2(?, 256)
            WHERE id_usuario = ?
        """;

        try {
            PreparedStatement ps = con.getConnection().prepareStatement(sql);
            ps.setString(1, nuevaPass);
            ps.setInt(2, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validarClave(String username, String claveIngresada) {
        String sql = "SELECT password_hash FROM usuario WHERE username = ? AND activo = 1";
        try {
            PreparedStatement ps = con.getConnection().prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String hashDB = rs.getString("password_hash");
                return PasswordUtils.verifyPassword(claveIngresada, hashDB);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean actualizarClave(String user, String nuevaClave) {
        String sql = "UPDATE usuario SET password_hash = ? WHERE username = ?";
        try {
            String hash = PasswordUtils.hashPassword(nuevaClave);
            PreparedStatement ps = con.getConnection().prepareStatement(sql);
            ps.setString(1, hash);
            ps.setString(2, user);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Agrega este método al UsuarioDAO
    public Usuario login(String user, String pass) {
        String sql = "SELECT * FROM usuario WHERE username = ? AND activo = 1";
        try {
            PreparedStatement ps = con.getConnection().prepareStatement(sql);
            ps.setString(1, user);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String hashDB = rs.getString("password_hash");
                if (PasswordUtils.verifyPassword(pass, hashDB)) {
                    Usuario u = new Usuario();
                    u.setIdUsuario(rs.getInt("id_usuario"));
                    u.setUsername(rs.getString("username"));
                    u.setRol(rs.getString("rol"));
                    u.setActivo(rs.getBoolean("activo"));
                    return u;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Credenciales inválidas
    }

    // ================== LISTAR TODOS LOS USUARIOS ==================
    public ArrayList<Usuario> listarTodos() {
        ArrayList<Usuario> lista = new ArrayList<>();
        String sql = "SELECT id_usuario, username, rol, activo FROM usuario ORDER BY username ASC";

        try {
            PreparedStatement ps = con.getConnection().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("id_usuario"));
                u.setUsername(rs.getString("username"));
                u.setRol(rs.getString("rol"));
                u.setActivo(rs.getBoolean("activo"));
                lista.add(u);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    // ================== GUARDAR / CREAR USUARIO ==================
    public boolean guardar(Usuario u, String passwordPlana) {
        String sql = "INSERT INTO usuario (username, password_hash, rol, activo) VALUES (?, ?, ?, ?)";
        try {
            String hash = PasswordUtils.hashPassword(passwordPlana);
            PreparedStatement ps = con.getConnection().prepareStatement(sql);
            ps.setString(1, u.getUsername());
            ps.setString(2, hash);
            ps.setString(3, u.getRol());
            ps.setBoolean(4, true); // Activo por defecto

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

// ================== ELIMINAR USUARIO ==================
    public boolean eliminar(int idUsuario) {
        // Nota: En software profesional se prefiere "desactivar" (activo = 0) 
        // pero si deseas borrarlo físicamente:
        String sql = "DELETE FROM usuario WHERE id_usuario = ?";
        try {
            PreparedStatement ps = con.getConnection().prepareStatement(sql);
            ps.setInt(1, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
