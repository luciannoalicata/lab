package dao;

import modelo.Conexion;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import modelo.PasswordUtils;
import modelo.Usuario;

public class UsuarioDAO {

    private final Conexion con;

    public UsuarioDAO(Conexion con) {
        this.con = con;
    }

    // ── LOGIN — único método de autenticación ────────────────────────
    public Usuario login(String username, String password) {
        String sql = "SELECT * FROM usuario WHERE username = ? AND activo = 1";
        try (PreparedStatement ps = con.getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hashDB = rs.getString("password_hash");
                    if (PasswordUtils.verifyPassword(password, hashDB)) {
                        Usuario u = new Usuario();
                        u.setIdUsuario(rs.getInt("id_usuario"));
                        u.setUsername(rs.getString("username"));
                        u.setRol(rs.getString("rol"));
                        u.setActivo(rs.getBoolean("activo"));
                        return u;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // ── VALIDAR CLAVE (para cambio de contraseña) ────────────────────
    public boolean validarClave(String username, String claveIngresada) {
        String sql = "SELECT password_hash FROM usuario WHERE username = ? AND activo = 1";
        try (PreparedStatement ps = con.getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return PasswordUtils.verifyPassword(claveIngresada, rs.getString("password_hash"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ── ACTUALIZAR CLAVE ─────────────────────────────────────────────
    public boolean actualizarClave(String username, String nuevaClave) {
        String sql = "UPDATE usuario SET password_hash = ? WHERE username = ?";
        try (PreparedStatement ps = con.getConnection().prepareStatement(sql)) {
            String hash = PasswordUtils.hashPassword(nuevaClave);
            ps.setString(1, hash);
            ps.setString(2, username);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ── LISTAR ───────────────────────────────────────────────────────
    public ArrayList<Usuario> listarTodos() {
        ArrayList<Usuario> lista = new ArrayList<>();
        String sql = "SELECT id_usuario, username, rol, activo FROM usuario ORDER BY username ASC";
        try (PreparedStatement ps = con.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
             
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

    // ── GUARDAR ──────────────────────────────────────────────────────
    public boolean guardar(Usuario u, String passwordPlana) {
        String sql = "INSERT INTO usuario (username, password_hash, rol, activo) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.getConnection().prepareStatement(sql)) {
            String hash = PasswordUtils.hashPassword(passwordPlana);
            ps.setString(1, u.getUsername());
            ps.setString(2, hash);
            ps.setString(3, u.getRol());
            ps.setBoolean(4, true);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ── ELIMINAR ─────────────────────────────────────────────────────
    public boolean eliminar(int idUsuario) {
        String sql = "DELETE FROM usuario WHERE id_usuario = ?";
        try (PreparedStatement ps = con.getConnection().prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}