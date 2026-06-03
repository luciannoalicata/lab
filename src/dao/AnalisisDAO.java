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

    // ================== MÉTODO DE CONVERSIÓN (REUTILIZADO) ==================
    private String convertirNombrePropio(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return "-"; // Retorna guion si está vacío
        }
        // Si el usuario escribió un guion, lo dejamos como está
        if (texto.trim().equals("-")) {
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
        // 1. SQL actualizado incluyendo 'codigo_os'
        String sql = """
        INSERT INTO analisis (id_paciente, codigo_os, fecha, precio, observaciones, medico_solicitante)
        VALUES (?, ?, ?, ?, ?, ?)
    """;

        try {
            PreparedStatement ps = con.getConnection()
                    .prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

            ps.setInt(1, a.getIdPaciente());

            // 2. Agregamos el código de la Obra Social (vital para el desglose de facturación)
            // Si el objeto Analisis aún no tiene el campo, asegúrate de agregarlo a la clase modelo.Analisis
            ps.setString(2, a.getObraSocial());

            ps.setTimestamp(3, new java.sql.Timestamp(new java.util.Date().getTime()));
            ps.setDouble(4, a.getPrecio());
            ps.setString(5, a.getObservaciones());

            // Aplicamos conversión de nombre propio al médico
            ps.setString(6, (a.getMedicoSolicitante() == null || a.getMedicoSolicitante().isEmpty())
                    ? "-" : convertirNombrePropio(a.getMedicoSolicitante()));

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1); // Retorna el ID generado (id_analisis)
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    // ================== ACTUALIZAR MÉDICO SOLICITANTE ==================
    public boolean actualizarMedico(int idAnalisis, String nuevoMedico) {
        String sql = "UPDATE analisis SET medico_solicitante = ? WHERE id_analisis = ?";

        try {
            PreparedStatement ps = con.getConnection().prepareStatement(sql);

            // TAMBIÉN APLICAMOS LA CONVERSIÓN EN LA EDICIÓN
            ps.setString(1, convertirNombrePropio(nuevoMedico));
            ps.setInt(2, idAnalisis);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ... (listarPorPaciente y buscarPorId se mantienen igual que antes) ...
    public Analisis buscarPorId(int idAnalisis) {
        String sql = "SELECT * FROM analisis WHERE id_analisis = ?";
        try {
            PreparedStatement ps = con.getConnection().prepareStatement(sql);
            ps.setInt(1, idAnalisis);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Analisis a = new Analisis();
                a.setIdAnalisis(rs.getInt("id_analisis"));
                a.setIdPaciente(rs.getInt("id_paciente"));
                a.setFecha(rs.getDate("fecha"));
                a.setPrecio(rs.getDouble("precio"));
                a.setObservaciones(rs.getString("observaciones"));
                a.setMedicoSolicitante(rs.getString("medico_solicitante"));
                return a;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Analisis> listarPorPaciente(int idPaciente) {
        ArrayList<Analisis> lista = new ArrayList<>();
        // Incluimos la columna estado en la consulta
        String sql = """
        SELECT id_analisis, fecha, precio, estado
        FROM analisis
        WHERE id_paciente = ?
        ORDER BY fecha DESC
    """;
        try {
            PreparedStatement ps = con.getConnection().prepareStatement(sql);
            ps.setInt(1, idPaciente);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Analisis a = new Analisis();
                a.setIdAnalisis(rs.getInt("id_analisis"));
                a.setFecha(rs.getDate("fecha"));
                a.setPrecio(rs.getDouble("precio"));
                // Importante: Debes tener el atributo 'estado' en tu clase Analisis.java con su getter/setter
                a.setEstado(rs.getString("estado"));
                lista.add(a);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Método para cambiar el estado al generar el PDF
    public boolean cambiarEstadoGenerado(int idAnalisis) {
        String sql = "UPDATE analisis SET estado = 'GENERADO' WHERE id_analisis = ?";
        try {
            PreparedStatement ps = con.getConnection().prepareStatement(sql);
            ps.setInt(1, idAnalisis);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizarPrecio(int idAnalisis, double nuevoPrecio) {
        String sql = "UPDATE analisis SET precio = ? WHERE id_analisis = ?";
        try {
            PreparedStatement ps = con.getConnection().prepareStatement(sql);
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
        String sql;
        boolean tieneFiltro = (filtro != null && !filtro.trim().isEmpty());

        // ── LA MAGIA ESTÁ AQUÍ ──
        // Hacemos un JOIN con la tabla obra_social usando a.codigo_os
        // Y usamos CONCAT para que salga prolijo "1800 - SUBSIDIO DE SALUD"
        if (tieneFiltro) {
            sql = """
            SELECT a.*, p.nombre, p.apellido, p.dni, 
                   CONCAT(os.codigo, ' - ', os.nombre) AS obra_social_analisis 
            FROM analisis a 
            LEFT JOIN paciente p ON a.id_paciente = p.id_paciente 
            LEFT JOIN obra_social os ON a.codigo_os = os.codigo 
            WHERE CAST(a.id_analisis AS CHAR) LIKE ? 
               OR p.nombre LIKE ? 
               OR p.apellido LIKE ? 
               OR p.dni LIKE ?
            ORDER BY a.fecha DESC LIMIT 100
            """;
        } else {
            sql = """
            SELECT a.*, p.nombre, p.apellido, p.dni, 
                   CONCAT(os.codigo, ' - ', os.nombre) AS obra_social_analisis 
            FROM analisis a 
            LEFT JOIN paciente p ON a.id_paciente = p.id_paciente 
            LEFT JOIN obra_social os ON a.codigo_os = os.codigo 
            ORDER BY a.fecha DESC LIMIT 100
            """;
        }

        try {
            java.sql.PreparedStatement ps = con.getConnection().prepareStatement(sql);
            if (tieneFiltro) {
                String f = "%" + filtro.trim() + "%";
                ps.setString(1, f);
                ps.setString(2, f);
                ps.setString(3, f);
                ps.setString(4, f);
            }

            java.sql.ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Analisis a = new Analisis();
                a.setIdAnalisis(rs.getInt("id_analisis"));
                a.setFecha(rs.getTimestamp("fecha"));
                a.setPrecio(rs.getDouble("precio"));
                a.setMedicoSolicitante(rs.getString("medico_solicitante"));
                a.setPacienteNombre(rs.getString("nombre"));
                a.setPacienteApellido(rs.getString("apellido"));
                a.setPacienteDni(rs.getString("dni"));

                // Extraemos la obra social real cruzada con la tabla de obras sociales
                String osCompleta = rs.getString("obra_social_analisis");

                // Si por alguna razón el cruce da null (ej. OS borrada), ponemos un guión
                a.setObraSocial(osCompleta != null ? osCompleta : "-");

                lista.add(a);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }
    
    // ================== ELIMINAR ANALISIS ==================
    public boolean eliminar(int idAnalisis) {
        String sql = "DELETE FROM analisis WHERE id_analisis = ?";
        try {
            PreparedStatement ps = con.getConnection().prepareStatement(sql);
            ps.setInt(1, idAnalisis);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
