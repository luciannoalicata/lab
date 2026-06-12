package dao;

import modelo.Analisis;
import modelo.Conexion;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AnalisisDAO {

    private Conexion con;

    public AnalisisDAO(Conexion con) {
        this.con = con;
    }

    private String convertirNombrePropio(String texto) {
        if (texto == null || texto.trim().isEmpty() || texto.trim().equals("-")) {
            return "-";
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

    // ================== CREAR ANALISIS ==================
    public int crear(Analisis a) {
        // ACTUALIZADO: matricula_medico en lugar de medico_solicitante
        String sql = """
            INSERT INTO analisis (id_paciente, codigo_os, matricula_medico, fecha, precio, observaciones, estado)
            VALUES (?, ?, ?, ?, ?, ?, 'COMPLETO')
        """;

        try (PreparedStatement ps = con.getConnection().prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, a.getIdPaciente());
            // Manejamos null/vacío para la OS (En la base es clave foránea, si no tiene, debe ser NULL)
            if (a.getObraSocial() == null || a.getObraSocial().trim().isEmpty() || a.getObraSocial().equals("-")) {
                ps.setNull(2, java.sql.Types.VARCHAR);
            } else {
                ps.setString(2, a.getObraSocial()); // Aquí debe venir el CÓDIGO de la OS, no el nombre
            }
            
            // La matrícula del médico (Opcional, si viene "-" lo mandamos como NULL para la FK)
            if (a.getMedicoSolicitante() == null || a.getMedicoSolicitante().trim().isEmpty() || a.getMedicoSolicitante().equals("-")) {
                ps.setNull(3, java.sql.Types.VARCHAR);
            } else {
                ps.setString(3, a.getMedicoSolicitante()); // Aquí debe venir la MATRÍCULA
            }

            ps.setTimestamp(4, new java.sql.Timestamp(new java.util.Date().getTime()));
            ps.setDouble(5, a.getPrecio());
            ps.setString(6, a.getObservaciones());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1); 
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    // ================== ACTUALIZAR MÉDICO SOLICITANTE ==================
    public boolean actualizarMedico(int idAnalisis, String nuevaMatricula) {
        String sql = "UPDATE analisis SET matricula_medico = ? WHERE id_analisis = ?";

        try (PreparedStatement ps = con.getConnection().prepareStatement(sql)) {
            if (nuevaMatricula == null || nuevaMatricula.trim().isEmpty() || nuevaMatricula.equals("-")) {
                ps.setNull(1, java.sql.Types.VARCHAR);
            } else {
                ps.setString(1, nuevaMatricula);
            }
            ps.setInt(2, idAnalisis);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Analisis buscarPorId(int idAnalisis) {
        String sql = "SELECT * FROM analisis WHERE id_analisis = ?";
        try (PreparedStatement ps = con.getConnection().prepareStatement(sql)) {
            ps.setInt(1, idAnalisis);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Analisis a = new Analisis();
                    a.setIdAnalisis(rs.getInt("id_analisis"));
                    a.setIdPaciente(rs.getInt("id_paciente"));
                    a.setFecha(rs.getDate("fecha"));
                    a.setPrecio(rs.getDouble("precio"));
                    a.setObservaciones(rs.getString("observaciones"));
                    a.setMedicoSolicitante(rs.getString("matricula_medico"));
                    a.setObraSocial(rs.getString("codigo_os"));
                    a.setEstado(rs.getString("estado"));
                    return a;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Analisis> listarPorPaciente(int idPaciente) {
        ArrayList<Analisis> lista = new ArrayList<>();
        // ACTUALIZADO: Hacemos JOIN para traer el nombre real del médico y la obra social
        String sql = """
            SELECT a.id_analisis, a.fecha, a.precio, a.estado, 
                   CONCAT(os.codigo, ' - ', os.nombre) AS obra_social_analisis, 
                   CONCAT(m.apellido, ' ', m.nombre) AS nombre_medico 
            FROM analisis a
            LEFT JOIN obra_social os ON a.codigo_os = os.codigo 
            LEFT JOIN medico m ON a.matricula_medico = m.matricula 
            WHERE a.id_paciente = ?
            ORDER BY a.fecha DESC
        """;
        try (PreparedStatement ps = con.getConnection().prepareStatement(sql)) {
            ps.setInt(1, idPaciente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Analisis a = new Analisis();
                    a.setIdAnalisis(rs.getInt("id_analisis"));
                    a.setFecha(rs.getTimestamp("fecha"));
                    a.setPrecio(rs.getDouble("precio"));
                    a.setEstado(rs.getString("estado"));
                    
                    // Seteamos los nuevos campos extraídos de los JOINs
                    String medicoCompleto = rs.getString("nombre_medico");
                    a.setMedicoSolicitante(medicoCompleto != null ? medicoCompleto : "-");

                    String osCompleta = rs.getString("obra_social_analisis");
                    a.setObraSocial(osCompleta != null ? osCompleta : "-");
                    
                    lista.add(a);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean cambiarEstadoGenerado(int idAnalisis) {
        String sql = "UPDATE analisis SET estado = 'GENERADO' WHERE id_analisis = ?";
        try (PreparedStatement ps = con.getConnection().prepareStatement(sql)) {
            ps.setInt(1, idAnalisis);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizarPrecio(int idAnalisis, double nuevoPrecio) {
        String sql = "UPDATE analisis SET precio = ? WHERE id_analisis = ?";
        try (PreparedStatement ps = con.getConnection().prepareStatement(sql)) {
            ps.setDouble(1, nuevoPrecio);
            ps.setInt(2, idAnalisis);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<Analisis> buscarAnalisisGlobal(String filtro) {
        ArrayList<Analisis> lista = new ArrayList<>();
        boolean tieneFiltro = (filtro != null && !filtro.trim().isEmpty());

        // ACTUALIZADO: JOIN con tabla de médicos para extraer el nombre real
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT a.*, p.nombre as pac_nombre, p.apellido as pac_apellido, p.dni as pac_dni, ");
        sql.append("CONCAT(os.codigo, ' - ', os.nombre) AS obra_social_analisis, ");
        sql.append("CONCAT(m.apellido, ' ', m.nombre) AS nombre_medico ");
        sql.append("FROM analisis a ");
        sql.append("LEFT JOIN paciente p ON a.id_paciente = p.id_paciente ");
        sql.append("LEFT JOIN obra_social os ON a.codigo_os = os.codigo ");
        sql.append("LEFT JOIN medico m ON a.matricula_medico = m.matricula ");

        if (tieneFiltro) {
            sql.append("WHERE CAST(a.id_analisis AS CHAR) LIKE ? ");
            sql.append("OR p.nombre LIKE ? OR p.apellido LIKE ? OR p.dni LIKE ? ");
        }
        sql.append("ORDER BY a.fecha DESC LIMIT 100");

        try (PreparedStatement ps = con.getConnection().prepareStatement(sql.toString())) {
            if (tieneFiltro) {
                String f = "%" + filtro.trim() + "%";
                ps.setString(1, f);
                ps.setString(2, f);
                ps.setString(3, f);
                ps.setString(4, f);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Analisis a = new Analisis();
                    a.setIdAnalisis(rs.getInt("id_analisis"));
                    a.setFecha(rs.getTimestamp("fecha"));
                    a.setPrecio(rs.getDouble("precio"));
                    a.setEstado(rs.getString("estado"));
                    
                    // Nombres extraídos por los JOINs
                    a.setPacienteNombre(rs.getString("pac_nombre"));
                    a.setPacienteApellido(rs.getString("pac_apellido"));
                    a.setPacienteDni(rs.getString("pac_dni"));
                    
                    String medicoCompleto = rs.getString("nombre_medico");
                    a.setMedicoSolicitante(medicoCompleto != null ? medicoCompleto : "-");

                    String osCompleta = rs.getString("obra_social_analisis");
                    a.setObraSocial(osCompleta != null ? osCompleta : "-");

                    lista.add(a);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean eliminar(int idAnalisis) {
        String sql = "DELETE FROM analisis WHERE id_analisis = ?";
        try (PreparedStatement ps = con.getConnection().prepareStatement(sql)) {
            ps.setInt(1, idAnalisis);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}