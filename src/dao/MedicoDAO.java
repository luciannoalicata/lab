package dao;

import modelo.Conexion;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import modelo.Medico;

public class MedicoDAO {

    private final Conexion con;

    public MedicoDAO(Conexion con) {
        this.con = con;
    }

    public boolean guardarMedico(Medico m) {
        String sql = "INSERT INTO medico (apellido, nombre, matricula, especialidad, observaciones) VALUES (?, ?, ?, ?, ?) "
                   + "ON DUPLICATE KEY UPDATE apellido = ?, nombre = ?, especialidad = ?, observaciones = ?";

        try (PreparedStatement ps = con.getConnection().prepareStatement(sql)) {
            String ap = convertirNombrePropio(m.getApellidoMedico());
            String nom = convertirNombrePropio(m.getNombreMedico());
            String esp = m.getEspecialidad().toUpperCase();

            ps.setString(1, ap);
            ps.setString(2, nom);
            ps.setString(3, m.getMatricula());
            ps.setString(4, esp);
            ps.setString(5, m.getObservaciones());
            
            ps.setString(6, ap);
            ps.setString(7, nom);
            ps.setString(8, esp);
            ps.setString(9, m.getObservaciones());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<Medico> listarMedicos() {
        ArrayList<Medico> lista = new ArrayList<>();
        String sql = "SELECT * FROM medico ORDER BY apellido ASC";

        try (PreparedStatement ps = con.getConnection().prepareStatement(sql); 
             ResultSet res = ps.executeQuery()) {

            while (res.next()) {
                lista.add(mapearMedico(res));
            }
        } catch (SQLException ex) {
            System.out.println("Error al listar: " + ex);
        }
        return lista;
    }

    public ArrayList<Medico> buscarMedicoInteligente(String texto) {
        ArrayList<Medico> lista = new ArrayList<>();
        String filtro = "%" + texto + "%";

        String sql = "SELECT * FROM medico WHERE "
                + "CONCAT(apellido, ' ', nombre) LIKE ? OR "
                + "CONCAT(nombre, ' ', apellido) LIKE ? OR "
                + "especialidad LIKE ? OR "
                + "matricula LIKE ? "
                + "ORDER BY apellido ASC";

        try (PreparedStatement ps = con.getConnection().prepareStatement(sql)) {
            ps.setString(1, filtro);
            ps.setString(2, filtro);
            ps.setString(3, filtro);
            ps.setString(4, filtro);

            try (ResultSet res = ps.executeQuery()) {
                while (res.next()) {
                    lista.add(mapearMedico(res));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }

    public Medico buscarPorMatricula(String matricula) {
        String sql = "SELECT * FROM medico WHERE matricula = ?";
        try (PreparedStatement ps = con.getConnection().prepareStatement(sql)) {
            ps.setString(1, matricula);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearMedico(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean eliminarMedico(String matricula) {
        String sql = "DELETE FROM medico WHERE matricula = ?";
        try (PreparedStatement ps = con.getConnection().prepareStatement(sql)) {
            ps.setString(1, matricula);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean existeMatricula(String matricula) {
        String sql = "SELECT COUNT(*) FROM medico WHERE matricula = ?";
        try (PreparedStatement ps = con.getConnection().prepareStatement(sql)) {
            ps.setString(1, matricula);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<String> obtenerSugerenciasMedicos(String busqueda) {
        List<String> sugerencias = new ArrayList<>();
        String sql = "SELECT matricula, apellido, nombre FROM medico "
                   + "WHERE matricula LIKE ? OR apellido LIKE ? OR nombre LIKE ? ORDER BY apellido ASC LIMIT 8";
                   
        try (PreparedStatement ps = con.getConnection().prepareStatement(sql)) {
            String filtro = busqueda + "%"; 
            ps.setString(1, filtro);
            ps.setString(2, filtro);
            ps.setString(3, filtro);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String item = rs.getString("nombre") + " " + rs.getString("apellido") + " (mp. " + rs.getString("matricula") + ")";
                    sugerencias.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sugerencias;
    }

    public boolean actualizarMedico(Medico medico) {
        String sql = "UPDATE medico SET nombre = ?, apellido = ?, especialidad = ?, observaciones = ? WHERE matricula = ?";
        try (PreparedStatement stmt = con.getConnection().prepareStatement(sql)) {
            stmt.setString(1, convertirNombrePropio(medico.getNombreMedico()));
            stmt.setString(2, convertirNombrePropio(medico.getApellidoMedico()));
            stmt.setString(3, medico.getEspecialidad().toUpperCase());
            stmt.setString(4, medico.getObservaciones());
            stmt.setString(5, medico.getMatricula());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private Medico mapearMedico(ResultSet res) throws SQLException {
        Medico m = new Medico();
        m.setApellidoMedico(res.getString("apellido"));
        m.setNombreMedico(res.getString("nombre"));
        m.setMatricula(res.getString("matricula"));
        m.setEspecialidad(res.getString("especialidad"));
        m.setObservaciones(res.getString("observaciones"));
        return m;
    }

    private String convertirNombrePropio(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return "";
        }
        String[] palabras = texto.trim().toLowerCase().split("\\s+");
        StringBuilder resultado = new StringBuilder();
        for (String palabra : palabras) {
            if (palabra.length() > 0) {
                resultado.append(Character.toUpperCase(palabra.charAt(0)))
                         .append(palabra.substring(1))
                         .append(" ");
            }
        }
        return resultado.toString().trim();
    }
}