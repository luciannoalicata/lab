package dao;

import modelo.Conexion;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import modelo.Medico;

/**
 * @author luciano
 */
public class MedicoDAO {

    private Conexion con;

    public MedicoDAO(Conexion con) {
        this.con = con;
    }

    public boolean guardarMedico(Medico m) {
        // Usamos INSERT ... ON DUPLICATE KEY UPDATE por si decides usar matricula como PK en el futuro
        String sql = "INSERT INTO medico (apellido, nombre, matricula, especialidad, observaciones) VALUES (?, ?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE especialidad = ?, observaciones = ?";

        try {
            PreparedStatement ps = con.getConnection().prepareStatement(sql);
            String ap = convertirNombrePropio(m.getApellidoMedico());
            String nom = convertirNombrePropio(m.getNombreMedico());
            String esp = m.getEspecialidad().toUpperCase(); // Especialidad suele ir en mayúsculas

            ps.setString(1, ap);
            ps.setString(2, nom);
            ps.setString(3, m.getMatricula());
            ps.setString(4, esp);
            ps.setString(5, m.getObservaciones());
            // Para el UPDATE en caso de duplicado
            ps.setString(6, esp);
            ps.setString(7, m.getObservaciones());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<Medico> listarMedicos() {
        ArrayList<Medico> lista = new ArrayList<>();
        String sql = "SELECT * FROM medico ORDER BY apellido ASC";

        try (PreparedStatement ps = con.getConnection().prepareStatement(sql); ResultSet res = ps.executeQuery()) {

            while (res.next()) {
                Medico m = new Medico();
                // Corregido el error de dedo: "apelldio" -> "apellido"
                m.setApellidoMedico(res.getString("apellido"));
                m.setNombreMedico(res.getString("nombre"));
                m.setMatricula(res.getString("matricula"));
                m.setEspecialidad(res.getString("especialidad"));
                m.setObservaciones(res.getString("observaciones"));
                lista.add(m);
            }
        } catch (SQLException ex) {
            System.out.println("Error al listar: " + ex);
        }
        return lista;
    }

    /**
     * Búsqueda inteligente: Busca coincidencias en Apellido+Nombre,
     * Nombre+Apellido o Especialidad de forma simultánea.
     */
    public ArrayList<Medico> buscarMedicoInteligente(String texto) {
        ArrayList<Medico> lista = new ArrayList<>();
        String filtro = "%" + texto + "%";

        // SQL actualizado para incluir la matrícula en la búsqueda
        String sql = "SELECT * FROM medico WHERE "
                + "CONCAT(apellido, ' ', nombre) LIKE ? OR "
                + "CONCAT(nombre, ' ', apellido) LIKE ? OR "
                + "especialidad LIKE ? OR "
                + "matricula LIKE ? " // <--- NUEVA LÍNEA
                + "ORDER BY apellido ASC";

        try {
            PreparedStatement ps = con.getConnection().prepareStatement(sql);
            ps.setString(1, filtro);
            ps.setString(2, filtro);
            ps.setString(3, filtro);
            ps.setString(4, filtro); // <--- NUEVO PARÁMETRO

            ResultSet res = ps.executeQuery();
            while (res.next()) {
                Medico m = new Medico();
                m.setApellidoMedico(res.getString("apellido"));
                m.setNombreMedico(res.getString("nombre"));
                m.setMatricula(res.getString("matricula"));
                m.setEspecialidad(res.getString("especialidad"));
                m.setObservaciones(res.getString("observaciones"));
                lista.add(m);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }

    public Medico buscarPorMatricula(String matricula) {
        String sql = "SELECT * FROM medico WHERE matricula = ?";
        try {
            PreparedStatement ps = con.getConnection().prepareStatement(sql);
            ps.setString(1, matricula);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Medico m = new Medico();
                m.setApellidoMedico(rs.getString("apellido"));
                m.setNombreMedico(rs.getString("nombre"));
                m.setMatricula(rs.getString("matricula"));
                m.setEspecialidad(rs.getString("especialidad"));
                m.setObservaciones(rs.getString("observaciones"));
                return m;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean eliminarMedico(String matricula) {
        String sql = "DELETE FROM medico WHERE matricula = ?";
        try {
            PreparedStatement ps = con.getConnection().prepareStatement(sql);
            ps.setString(1, matricula);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
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

    public boolean existeMatricula(String matricula) {
        String sql = "SELECT COUNT(*) FROM medico WHERE matricula = ?";
        try {
            PreparedStatement ps = con.getConnection().prepareStatement(sql);
            ps.setString(1, matricula);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<String> obtenerSugerenciasMedicos(String busqueda) {
        List<String> sugerencias = new ArrayList<>();
        // Buscamos coincidencia en matricula, apellido o nombre
        String sql = "SELECT matricula, apellido, nombre FROM medico "
                + "WHERE matricula LIKE ? OR apellido LIKE ? OR nombre LIKE ? LIMIT 8";
        try {
            PreparedStatement ps = con.getConnection().prepareStatement(sql);
            String filtro = busqueda + "%"; // Empieza con...
            ps.setString(1, filtro);
            ps.setString(2, filtro);
            ps.setString(3, filtro);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                // Formateamos aquí mismo o en el controlador
                String item = rs.getString("matricula") + " - "
                        + rs.getString("apellido") + " "
                        + rs.getString("nombre");
                sugerencias.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sugerencias;
    }
}
