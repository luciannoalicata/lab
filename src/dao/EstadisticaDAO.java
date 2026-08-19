package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import modelo.Conexion;

public class EstadisticaDAO {

    private final Conexion con;

    public EstadisticaDAO(Conexion con) {
        this.con = con;
    }

    public List<Object[]> buscarAnalisisFiltrado(Date desde, Date hasta, String filtroOS, String filtroMed, String filtroDet) {
        List<Object[]> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
            SELECT a.id_analisis, a.fecha, p.dni, 
                   CONCAT(p.apellido, ' ', p.nombre) AS paciente,
                   COALESCE(CONCAT(m.nombre, ' ', m.apellido), 'Ninguno') AS medico, -- ¡CORREGIDO AQUÍ!
                   COALESCE(os.nombre, 'PARTICULAR') AS obra_social,
                   (SELECT GROUP_CONCAT(DISTINCT d.nombre SEPARATOR ', ')
                    FROM resultado_analisis r
                    INNER JOIN determinacion d ON d.codigo = SUBSTRING_INDEX(r.codigo, '.', 1)
                    WHERE r.id_analisis = a.id_analisis
                   ) AS practicas,
                   a.precio
            FROM analisis a
            INNER JOIN paciente p ON a.id_paciente = p.id_paciente
            LEFT JOIN obra_social os ON a.codigo_os = os.codigo
            LEFT JOIN medico m ON a.matricula_medico = m.matricula
            WHERE a.fecha BETWEEN ? AND ?
        """);

        List<Object> params = new ArrayList<>();
        construirFiltros(desde, hasta, filtroOS, filtroMed, filtroDet, sql, params);
        sql.append(" ORDER BY a.fecha DESC");

        try (PreparedStatement ps = con.getConnection().prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Object[]{
                        rs.getInt("id_analisis"),
                        new java.text.SimpleDateFormat("dd/MM/yyyy").format(rs.getTimestamp("fecha")),
                        rs.getString("dni"), rs.getString("paciente"), rs.getString("medico"),
                        rs.getString("obra_social"),
                        rs.getString("practicas") != null ? rs.getString("practicas") : "Sin prácticas",
                        rs.getDouble("precio")
                    });
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return lista;
    }

    public Map<String, Integer> contarPorObraSocial(Date desde, Date hasta, String filtroOS) {
        Map<String, Integer> mapa = new LinkedHashMap<>();
        StringBuilder sql = new StringBuilder("""
            SELECT COALESCE(os.nombre, 'PARTICULAR') AS categoria, COUNT(a.id_analisis) AS cantidad
            FROM analisis a
            LEFT JOIN obra_social os ON a.codigo_os = os.codigo
            WHERE a.fecha BETWEEN ? AND ?
        """);
        
        List<Object> params = new ArrayList<>();
        construirFiltros(desde, hasta, filtroOS, null, null, sql, params);
        sql.append(" GROUP BY categoria ORDER BY cantidad DESC");

        try (PreparedStatement ps = con.getConnection().prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) mapa.put(rs.getString("categoria"), rs.getInt("cantidad"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return mapa;
    }

    public Map<String, Integer> contarPorPractica(Date desde, Date hasta, String filtroDet) {
        Map<String, Integer> mapa = new LinkedHashMap<>();
        StringBuilder sql = new StringBuilder("""
            SELECT d.nombre AS categoria, COUNT(DISTINCT a.id_analisis) AS cantidad
            FROM analisis a
            INNER JOIN resultado_analisis r ON a.id_analisis = r.id_analisis
            INNER JOIN determinacion d ON d.codigo = SUBSTRING_INDEX(r.codigo, '.', 1)
            LEFT JOIN obra_social os ON a.codigo_os = os.codigo
            LEFT JOIN medico m ON a.matricula_medico = m.matricula
            WHERE a.fecha BETWEEN ? AND ?
        """);
        
        List<Object> params = new ArrayList<>();
        construirFiltros(desde, hasta, null, null, filtroDet, sql, params);
        sql.append(" GROUP BY d.nombre ORDER BY cantidad DESC LIMIT 10");

        try (PreparedStatement ps = con.getConnection().prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) mapa.put(rs.getString("categoria"), rs.getInt("cantidad"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return mapa;
    }

    private void construirFiltros(Date desde, Date hasta, String os, String med, String det, StringBuilder sql, List<Object> params) {
        params.add(new java.sql.Timestamp(desde.getTime()));
        params.add(new java.sql.Timestamp(ajustarFinDeDia(hasta).getTime()));
        
        if (os != null && !os.isEmpty() && !os.equalsIgnoreCase("TODAS")) {
            String codigoOS = os.contains("(") ? os.substring(os.lastIndexOf('(') + 1, os.lastIndexOf(')')).trim() : os;
            sql.append(" AND a.codigo_os = ?"); params.add(codigoOS);
        }
        if (med != null && !med.isEmpty() && !med.equalsIgnoreCase("TODOS")) {
            String matMed = med.contains("(") ? med.substring(med.lastIndexOf('(') + 1, med.lastIndexOf(')')).trim() : med;
            sql.append(" AND a.matricula_medico = ?"); params.add(matMed);
        }
        if (det != null && !det.isEmpty() && !det.equalsIgnoreCase("TODAS")) {
            String codDet = det.contains("[") ? det.substring(det.lastIndexOf('[') + 1, det.lastIndexOf(']')).trim() : det;
            sql.append(" AND EXISTS (SELECT 1 FROM resultado_analisis r2 WHERE r2.id_analisis = a.id_analisis AND SUBSTRING_INDEX(r2.codigo, '.', 1) = ?)");
            params.add(codDet);
        }
    }

    private Date ajustarFinDeDia(Date fecha) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(fecha); cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59); cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }
}