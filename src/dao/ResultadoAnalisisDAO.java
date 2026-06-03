/**
 *
 * @author luciano
 */
package dao;

import modelo.Conexion;
import modelo.ResultadoAnalisis;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ResultadoAnalisisDAO {

    private Conexion con;

    public ResultadoAnalisisDAO(Conexion con) {
        this.con = con;
    }

    // ================== GUARDAR RESULTADO ==================
    public boolean guardar(ResultadoAnalisis r) {
        String sql = """
        INSERT INTO resultado_analisis 
        (id_analisis, codigo, nombre_prueba, resultado, unidad, referencia, imprimir, prioridad) 
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
    """;
        try {
            PreparedStatement ps = con.getConnection().prepareStatement(sql);
            ps.setInt(1, r.getIdAnalisis());
            ps.setString(2, r.getCodigo());
            ps.setString(3, r.getNombrePrueba());
            ps.setString(4, r.getResultado());
            ps.setString(5, r.getUnidad());
            ps.setString(6, r.getReferencia());
            ps.setBoolean(7, r.isImprimir());
            ps.setInt(8, r.getPrioridad()); 

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ================== LISTAR RESULTADOS POR ANALISIS ==================
    public List<ResultadoAnalisis> listarPorAnalisis(int idAnalisis) {
        ArrayList<ResultadoAnalisis> lista = new ArrayList<>();
        // ── MAGIA SQL: Agrupamos por la raíz del código (ej: 660297) pero respetamos el orden en que se insertaron ──
        String sql = """
        SELECT * FROM resultado_analisis
        WHERE id_analisis = ?
        ORDER BY 
            (SELECT MIN(id_resultado) FROM resultado_analisis r2 
             WHERE r2.id_analisis = resultado_analisis.id_analisis 
             AND SUBSTRING_INDEX(r2.codigo, '.', 1) = SUBSTRING_INDEX(resultado_analisis.codigo, '.', 1)
            ) ASC,
            prioridad ASC
        """;
        try {
            PreparedStatement ps = con.getConnection().prepareStatement(sql);
            ps.setInt(1, idAnalisis);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ResultadoAnalisis r = new ResultadoAnalisis();
                r.setIdResultado(rs.getInt("id_resultado"));
                r.setIdAnalisis(rs.getInt("id_analisis"));
                r.setCodigo(rs.getString("codigo"));
                r.setNombrePrueba(rs.getString("nombre_prueba"));
                r.setResultado(rs.getString("resultado"));
                r.setUnidad(rs.getString("unidad"));
                r.setReferencia(rs.getString("referencia"));
                r.setPrioridad(rs.getInt("prioridad")); 
                lista.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    // ================== ELIMINAR RESULTADOS DE UN ANALISIS ==================
    public boolean eliminarPorAnalisis(int idAnalisis) {
        String sql = "DELETE FROM resultado_analisis WHERE id_analisis = ?";
        try {
            PreparedStatement ps = con.getConnection().prepareStatement(sql);
            ps.setInt(1, idAnalisis);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ================== LISTAR RESULTADOS INCLUIDOS ==================
    public List<ResultadoAnalisis> listarIncluidosPorAnalisis(int idAnalisis) {
        List<ResultadoAnalisis> lista = new ArrayList<>();
        String sql = """
        SELECT id_resultado, codigo, nombre_prueba, resultado, 
               unidad, referencia, imprimir, prioridad
        FROM resultado_analisis
        WHERE id_analisis = ?
        ORDER BY 
            (SELECT MIN(id_resultado) FROM resultado_analisis r2 
             WHERE r2.id_analisis = resultado_analisis.id_analisis 
             AND SUBSTRING_INDEX(r2.codigo, '.', 1) = SUBSTRING_INDEX(resultado_analisis.codigo, '.', 1)
            ) ASC,
            prioridad ASC
        """;
        try {
            PreparedStatement ps = con.getConnection().prepareStatement(sql);
            ps.setInt(1, idAnalisis);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ResultadoAnalisis r = new ResultadoAnalisis();
                r.setIdResultado(rs.getInt("id_resultado"));
                r.setCodigo(rs.getString("codigo"));
                r.setNombrePrueba(rs.getString("nombre_prueba"));
                r.setResultado(rs.getString("resultado"));
                r.setUnidad(rs.getString("unidad"));
                r.setReferencia(rs.getString("referencia"));
                r.setImprimir(rs.getBoolean("imprimir"));
                r.setPrioridad(rs.getInt("prioridad"));
                lista.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }
    
    public boolean actualizarResultado(int idResultado, String nuevoValor) {
        String sql = "UPDATE resultado_analisis SET resultado = ? WHERE id_resultado = ?";
        try {
            PreparedStatement ps = con.getConnection().prepareStatement(sql);
            ps.setString(1, nuevoValor);
            ps.setInt(2, idResultado);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // ================== ELIMINAR CON LIMPIEZA DE HUÉRFANOS ==================
    public boolean eliminarResultado(int idResultado) {
        // 1. Buscamos la información antes de borrar para saber a qué grupo de estudio pertenece
        ResultadoAnalisis r = buscarPorId(idResultado);
        if (r == null) return false;

        int idAnalisis = r.getIdAnalisis();
        // Extraemos la raíz del código (ej: de "660297.1" sacamos "660297")
        String baseCode = r.getCodigo().contains(".") ? r.getCodigo().substring(0, r.getCodigo().indexOf('.')) : r.getCodigo();

        String sql = "DELETE FROM resultado_analisis WHERE id_resultado = ?";
        try {
            PreparedStatement ps = con.getConnection().prepareStatement(sql);
            ps.setInt(1, idResultado);
            int eliminados = ps.executeUpdate();
            
            // 2. Si eliminamos la fila con éxito, pasamos la escoba para borrar los "--- TÍTULOS ---" que hayan quedado al pedo
            if (eliminados > 0) {
                limpiarSubtitulosHuerfanos(idAnalisis, baseCode);
            }
            
            return eliminados > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // ── ROBOT DE LIMPIEZA DE TÍTULOS HUÉRFANOS ──
    private void limpiarSubtitulosHuerfanos(int idAnalisis, String baseCode) {
        // Buscamos todas las filas que quedan de este mismo estudio (usando su código raíz)
        String sql = "SELECT id_resultado, nombre_prueba FROM resultado_analisis WHERE id_analisis = ? AND (codigo = ? OR codigo LIKE ?)";
        try {
            PreparedStatement ps = con.getConnection().prepareStatement(sql);
            ps.setInt(1, idAnalisis);
            ps.setString(2, baseCode);
            ps.setString(3, baseCode + ".%");
            ResultSet rs = ps.executeQuery();
            
            List<Integer> idsSubtitulos = new ArrayList<>();
            boolean tieneResultadosReales = false;
            
            while (rs.next()) {
                String nombre = rs.getString("nombre_prueba").trim();
                // Detectamos si es un subtítulo decorativo
                if (nombre.startsWith("---") && nombre.endsWith("---")) {
                    idsSubtitulos.add(rs.getInt("id_resultado"));
                } else {
                    tieneResultadosReales = true; // Encontramos un resultado de verdad
                }
            }
            
            // Si NO quedan resultados reales (solo quedaron subtítulos al pedo), los aniquilamos
            if (!tieneResultadosReales && !idsSubtitulos.isEmpty()) {
                String sqlDelete = "DELETE FROM resultado_analisis WHERE id_resultado = ?";
                PreparedStatement psDel = con.getConnection().prepareStatement(sqlDelete);
                for (int id : idsSubtitulos) {
                    psDel.setInt(1, id);
                    psDel.executeUpdate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================== BUSCAR POR ID (Para Auditoría) ==================
    public ResultadoAnalisis buscarPorId(int idResultado) {
        String sql = "SELECT * FROM resultado_analisis WHERE id_resultado = ?";
        try {
            PreparedStatement ps = con.getConnection().prepareStatement(sql);
            ps.setInt(1, idResultado);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                ResultadoAnalisis r = new ResultadoAnalisis();
                r.setIdResultado(rs.getInt("id_resultado"));
                r.setIdAnalisis(rs.getInt("id_analisis"));
                r.setCodigo(rs.getString("codigo"));
                r.setNombrePrueba(rs.getString("nombre_prueba"));
                r.setResultado(rs.getString("resultado"));
                r.setUnidad(rs.getString("unidad"));
                r.setReferencia(rs.getString("referencia"));
                return r;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}