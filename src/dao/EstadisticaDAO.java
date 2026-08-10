package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import modelo.Conexion;
import modelo.dto.FilaFacturacionDTO;
import modelo.dto.MetricaDTO;
import modelo.dto.ResumenGlobalDTO;

public class EstadisticaDAO {

    private final Conexion con;

    public EstadisticaDAO(Conexion con) {
        this.con = con;
    }

    public ResumenGlobalDTO obtenerResumenGlobal(Date desde, Date hasta, String codigoOS) {
        StringBuilder sql = new StringBuilder("""
            SELECT COUNT(a.id_analisis) AS total_analisis, 
                   COALESCE(SUM(a.precio), 0) AS total_facturado
            FROM analisis a
            WHERE a.fecha BETWEEN ? AND ?
        """);

        List<Object> parametros = new ArrayList<>();
        parametros.add(new java.sql.Timestamp(desde.getTime()));
        parametros.add(new java.sql.Timestamp(ajustarFinDeDia(hasta).getTime()));

        if (codigoOS != null && !codigoOS.trim().isEmpty() && !codigoOS.equals("TODAS")) {
            sql.append(" AND a.codigo_os = ?");
            parametros.add(codigoOS);
        }

        try (PreparedStatement ps = con.getConnection().prepareStatement(sql.toString())) {
            for (int i = 0; i < parametros.size(); i++) {
                ps.setObject(i + 1, parametros.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ResumenGlobalDTO(
                            rs.getInt("total_analisis"),
                            rs.getDouble("total_facturado")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResumenGlobalDTO(0, 0.0);
    }

    public List<MetricaDTO> agruparPorObraSocial(Date desde, Date hasta) {
        List<MetricaDTO> lista = new ArrayList<>();
        String sql = """
            SELECT COALESCE(os.nombre, 'PARTICULAR') AS categoria, 
                   COUNT(a.id_analisis) AS cantidad, 
                   SUM(a.precio) AS total
            FROM analisis a
            LEFT JOIN obra_social os ON a.codigo_os = os.codigo
            WHERE a.fecha BETWEEN ? AND ?
            GROUP BY categoria
            ORDER BY total DESC
        """;

        try (PreparedStatement ps = con.getConnection().prepareStatement(sql)) {
            ps.setTimestamp(1, new java.sql.Timestamp(desde.getTime()));
            ps.setTimestamp(2, new java.sql.Timestamp(ajustarFinDeDia(hasta).getTime()));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new MetricaDTO(
                            rs.getString("categoria"),
                            rs.getInt("cantidad"),
                            rs.getDouble("total")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<MetricaDTO> agruparPorMes(Date desde, Date hasta) {
        List<MetricaDTO> lista = new ArrayList<>();
        String sql = """
            SELECT DATE_FORMAT(a.fecha, '%m/%Y') AS categoria, 
                   COUNT(a.id_analisis) AS cantidad, 
                   SUM(a.precio) AS total
            FROM analisis a
            WHERE a.fecha BETWEEN ? AND ?
            GROUP BY DATE_FORMAT(a.fecha, '%m/%Y'), YEAR(a.fecha), MONTH(a.fecha)
            ORDER BY YEAR(a.fecha) ASC, MONTH(a.fecha) ASC
        """;

        try (PreparedStatement ps = con.getConnection().prepareStatement(sql)) {
            ps.setTimestamp(1, new java.sql.Timestamp(desde.getTime()));
            ps.setTimestamp(2, new java.sql.Timestamp(ajustarFinDeDia(hasta).getTime()));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new MetricaDTO(
                            rs.getString("categoria"),
                            rs.getInt("cantidad"),
                            rs.getDouble("total")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    // AHORA SOLO AGRUPA PRÁCTICAS PADRES (IGNORA LAS QUE TIENEN UN PUNTO EN EL CÓDIGO)
    public List<MetricaDTO> agruparPorPractica(Date desde, Date hasta, String codigoOS, String medico) {
        List<MetricaDTO> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
            SELECT d.nombre AS categoria, COUNT(*) AS cantidad, SUM(a.precio) AS total
            FROM analisis a
            INNER JOIN resultado_analisis r ON a.id_analisis = r.id_analisis
            INNER JOIN determinacion d ON r.codigo = d.codigo
            WHERE a.fecha BETWEEN ? AND ?
            AND d.codigo NOT LIKE '%.%'
        """);

        List<Object> parametros = new ArrayList<>();
        parametros.add(new java.sql.Timestamp(desde.getTime()));
        parametros.add(new java.sql.Timestamp(ajustarFinDeDia(hasta).getTime()));

        if (codigoOS != null && !codigoOS.trim().isEmpty() && !codigoOS.equals("TODAS")) {
            sql.append(" AND a.codigo_os = ?");
            parametros.add(codigoOS);
        }
        if (medico != null && !medico.trim().isEmpty() && !medico.equals("TODOS")) {
            sql.append(" AND a.matricula_medico LIKE ?");
            parametros.add("%" + medico + "%");
        }

        sql.append("""
            GROUP BY d.nombre
            ORDER BY cantidad DESC
            LIMIT 10
        """);

        try (PreparedStatement ps = con.getConnection().prepareStatement(sql.toString())) {
            for (int i = 0; i < parametros.size(); i++) {
                ps.setObject(i + 1, parametros.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new MetricaDTO(
                            rs.getString("categoria"),
                            rs.getInt("cantidad"),
                            rs.getDouble("total")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<FilaFacturacionDTO> obtenerDetalleFacturacion(Date desde, Date hasta, String codigoOS, String medico, String practica) {
        List<FilaFacturacionDTO> lista = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
            SELECT DISTINCT a.id_analisis, a.fecha, a.precio,
                   CONCAT(p.apellido, ' ', p.nombre) AS nombre_paciente,
                   COALESCE(os.nombre, a.codigo_os) AS nombre_os,
                   COALESCE(CONCAT(m.nombre, ' ', m.apellido), a.matricula_medico) AS nombre_medico
            FROM analisis a
            INNER JOIN paciente p ON a.id_paciente = p.id_paciente
            LEFT JOIN obra_social os ON a.codigo_os = os.codigo
            LEFT JOIN medico m ON a.matricula_medico = m.matricula
            WHERE a.fecha BETWEEN ? AND ?
        """);

        List<Object> parametros = new ArrayList<>();
        parametros.add(new java.sql.Timestamp(desde.getTime()));
        parametros.add(new java.sql.Timestamp(ajustarFinDeDia(hasta).getTime()));

        if (codigoOS != null && !codigoOS.trim().isEmpty() && !codigoOS.equals("TODAS")) {
            sql.append(" AND a.codigo_os = ?");
            parametros.add(codigoOS);
        }
        if (medico != null && !medico.trim().isEmpty() && !medico.equals("TODOS")) {
            sql.append(" AND (a.matricula_medico LIKE ? OR CONCAT(m.nombre, ' ', m.apellido) LIKE ?)");
            String filtroMed = "%" + medico + "%";
            parametros.add(filtroMed);
            parametros.add(filtroMed);
        }
        if (practica != null && !practica.trim().isEmpty() && !practica.equals("TODAS")) {
            sql.append("""
                AND EXISTS (
                    SELECT 1 FROM resultado_analisis r
                    INNER JOIN determinacion d ON r.codigo = d.codigo
                    WHERE r.id_analisis = a.id_analisis
                    AND (d.nombre LIKE ? OR d.codigo LIKE ?)
                )
            """);
            String filtroPrac = "%" + practica + "%";
            parametros.add(filtroPrac);
            parametros.add(filtroPrac);
        }

        sql.append(" ORDER BY a.fecha ASC");

        try (PreparedStatement ps = con.getConnection().prepareStatement(sql.toString())) {
            for (int i = 0; i < parametros.size(); i++) {
                ps.setObject(i + 1, parametros.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new FilaFacturacionDTO(
                            rs.getInt("id_analisis"),
                            rs.getTimestamp("fecha"),
                            rs.getString("nombre_paciente"),
                            rs.getString("nombre_os"),
                            rs.getString("nombre_medico"),
                            rs.getDouble("precio")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    // BUSCADOR ESPECÍFICO PARA AUTOCOMPLETADO (Solo Padres)
    public List<String> obtenerSugerenciasPracticasPadre(String busqueda) {
        List<String> sugerencias = new ArrayList<>();
        String sql = "SELECT codigo, nombre FROM determinacion "
                   + "WHERE (nombre LIKE ? OR codigo LIKE ?) AND codigo NOT LIKE '%.%' "
                   + "ORDER BY nombre ASC LIMIT 10";
        try (PreparedStatement ps = con.getConnection().prepareStatement(sql)) {
            String filtro = "%" + busqueda + "%";
            ps.setString(1, filtro);
            ps.setString(2, filtro);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    sugerencias.add(rs.getString("nombre") + " [" + rs.getString("codigo") + "]");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sugerencias;
    }

    private Date ajustarFinDeDia(Date fecha) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(fecha);
        cal.set(java.util.Calendar.HOUR_OF_DAY, 23);
        cal.set(java.util.Calendar.MINUTE, 59);
        cal.set(java.util.Calendar.SECOND, 59);
        cal.set(java.util.Calendar.MILLISECOND, 999);
        return cal.getTime();
    }
    
    // =========================================================================
//  AGRUPACIÓN POR PRÁCTICAS PADRE (SOLO LAS QUE NO TIENEN PUNTO)
// =========================================================================
public List<MetricaDTO> agruparPorPracticaPadre(Date desde, Date hasta, String codigoOS, String medico) {
    List<MetricaDTO> lista = new ArrayList<>();
    StringBuilder sql = new StringBuilder("""
        SELECT d.nombre AS categoria, COUNT(DISTINCT a.id_analisis) AS cantidad, SUM(a.precio) AS total
        FROM analisis a
        INNER JOIN resultado_analisis r ON a.id_analisis = r.id_analisis
        INNER JOIN determinacion d ON r.codigo = d.codigo
        WHERE a.fecha BETWEEN ? AND ?
          AND r.codigo NOT LIKE '%.%'
    """);

    List<Object> parametros = new ArrayList<>();
    parametros.add(new java.sql.Timestamp(desde.getTime()));
    parametros.add(new java.sql.Timestamp(ajustarFinDeDia(hasta).getTime()));

    if (codigoOS != null && !codigoOS.trim().isEmpty() && !codigoOS.equals("TODAS")) {
        sql.append(" AND a.codigo_os = ?");
        parametros.add(codigoOS);
    }
    if (medico != null && !medico.trim().isEmpty() && !medico.equals("TODOS")) {
        sql.append(" AND a.matricula_medico LIKE ?");
        parametros.add("%" + medico + "%");
    }

    sql.append("""
        GROUP BY d.nombre
        ORDER BY cantidad DESC
        LIMIT 10
    """);

    try (PreparedStatement ps = con.getConnection().prepareStatement(sql.toString())) {
        for (int i = 0; i < parametros.size(); i++) {
            ps.setObject(i + 1, parametros.get(i));
        }
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new MetricaDTO(
                    rs.getString("categoria"),
                    rs.getInt("cantidad"),
                    rs.getDouble("total")
                ));
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return lista;
}
}