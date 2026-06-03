package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import modelo.Conexion;
import modelo.Determinacion;

public class DeterminacionDAO {

    private Conexion con;

    public DeterminacionDAO(Conexion con) {
        this.con = con;
    }

    // Buscar una determinación por código exacto
    public Determinacion buscarPorCodigo(String codigo) {
        String sql = "SELECT * FROM determinacion WHERE TRIM(codigo) = ? AND activo = TRUE";
        try (PreparedStatement ps = con.getConnection().prepareStatement(sql)) {
            ps.setString(1, codigo.trim());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapear(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Buscar sugerencias por código parcial (para futuro autocompletar)
    public List<Determinacion> buscarPorCodigoParcial(String parcial) {
        List<Determinacion> lista = new ArrayList<>();
        String sql = """
            SELECT * FROM determinacion
            WHERE codigo LIKE ? AND activo = TRUE
            ORDER BY codigo
            LIMIT 10
        """;
        try (PreparedStatement ps = con.getConnection().prepareStatement(sql)) {
            ps.setString(1, parcial + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    private Determinacion mapear(ResultSet rs) throws Exception {
        Determinacion d = new Determinacion();
        d.setId(rs.getInt("id_determinacion"));
        d.setCodigo(rs.getString("codigo"));
        d.setNombre(rs.getString("nombre"));
        d.setUnidad(rs.getString("unidad"));
        d.setReferencia(rs.getString("referencia"));
        d.setUb(rs.getDouble("ub"));
        d.setPrioridad(rs.getInt("prioridad"));
        d.setArea(rs.getString("area"));
        d.setEsCompuesta(rs.getBoolean("es_compuesta"));
        return d;
    }

    // Trae los hijos de una práctica ordenados por su orden local (1, 2, 3...)
    public List<Determinacion> obtenerComponentes(String codigoPadre) {
        List<Determinacion> lista = new ArrayList<>();
        String sql = """
            SELECT d.* FROM determinacion d
            JOIN determinacion_componentes dc ON d.codigo = dc.codigo_hijo
            WHERE dc.codigo_padre = ?
            ORDER BY d.prioridad ASC
        """;
        try (PreparedStatement ps = con.getConnection().prepareStatement(sql)) {
            ps.setString(1, codigoPadre.trim());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<Determinacion> listarTodo() {
        List<Determinacion> lista = new ArrayList<>();
        String sql = """
            SELECT d.*, 
                   CASE WHEN EXISTS (SELECT 1 FROM determinacion_componentes WHERE codigo_padre = d.codigo) THEN 1 ELSE 0 END as tiene_hijos_real
            FROM determinacion d 
            WHERE d.activo = 1 
            AND d.codigo NOT LIKE '%.%' 
            AND d.codigo NOT IN (SELECT codigo_hijo FROM determinacion_componentes)
            ORDER BY tiene_hijos_real DESC, 
                     CASE WHEN EXISTS (SELECT 1 FROM determinacion_componentes WHERE codigo_padre = d.codigo) THEN d.prioridad ELSE 9999 END ASC,
                     TRIM(d.nombre) ASC
        """; 
        try (PreparedStatement ps = con.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
             
            boolean separadorAgregado = false;
            
            while (rs.next()) {
                Determinacion padre = mapear(rs);
                boolean tieneHijosRealmente = rs.getInt("tiene_hijos_real") == 1;
                
                if (!separadorAgregado && !tieneHijosRealmente && !lista.isEmpty()) {
                    Determinacion separador = new Determinacion();
                    separador.setCodigo(""); 
                    separador.setNombre("--- PRÁCTICAS SIMPLES (SIN VINCULACIONES) ---");
                    separador.setPrioridad(999);
                    lista.add(separador);
                    separadorAgregado = true;
                }
                
                lista.add(padre);
            }
        } catch (Exception e) {
            System.err.println("Error al listar Padres NBU: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    public List<Determinacion> buscar(String filtro) {
        List<Determinacion> lista = new ArrayList<>();
        String sql = """
            SELECT d.*, 
                   CASE WHEN EXISTS (SELECT 1 FROM determinacion_componentes WHERE codigo_padre = d.codigo) THEN 1 ELSE 0 END as tiene_hijos_real
            FROM determinacion d 
            WHERE d.activo = 1 
            AND d.codigo NOT LIKE '%.%' 
            AND d.codigo NOT IN (SELECT codigo_hijo FROM determinacion_componentes) 
            AND (d.codigo LIKE ? OR d.nombre LIKE ? OR RIGHT(d.codigo, 3) = ?) 
            ORDER BY tiene_hijos_real DESC, 
                     CASE WHEN EXISTS (SELECT 1 FROM determinacion_componentes WHERE codigo_padre = d.codigo) THEN d.prioridad ELSE 9999 END ASC,
                     TRIM(d.nombre) ASC
            LIMIT 25
        """; 
        try (PreparedStatement ps = con.getConnection().prepareStatement(sql)) {
            String f = "%" + filtro + "%";
            ps.setString(1, f);
            ps.setString(2, f);
            ps.setString(3, filtro);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }
    
    public boolean actualizarUnidadReferenciaPorCodigo(String codigo, String unidad, String referencia) {
        String sql = """
            UPDATE determinacion
            SET unidad = ?, referencia = ?
            WHERE codigo = ?
        """;
        try (java.sql.PreparedStatement ps = con.getConnection().prepareStatement(sql)) {
            ps.setString(1, unidad);
            ps.setString(2, referencia);
            ps.setString(3, codigo.trim());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Determinacion> buscarPorSufijo(String sufijo) {
        List<Determinacion> lista = new ArrayList<>();
        String sql = """
            SELECT * FROM determinacion
            WHERE activo = 1
            AND RIGHT(TRIM(codigo), 3) = ?
            ORDER BY nombre ASC
            LIMIT 15
        """;
        try (PreparedStatement ps = con.getConnection().prepareStatement(sql)) {
            ps.setString(1, sufijo);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }
    
    // =========================================================================
    //  MÉTODOS PARA GESTIÓN DE COMPONENTES (PADRE - HIJO) Y ORDENAMIENTO
    // =========================================================================

    public boolean vincularHijo(String codigoPadre, String codigoHijo) {
        String sql = "INSERT IGNORE INTO determinacion_componentes (codigo_padre, codigo_hijo) VALUES (?, ?)";
        try (PreparedStatement ps = con.getConnection().prepareStatement(sql)) {
            ps.setString(1, codigoPadre.trim());
            ps.setString(2, codigoHijo.trim());
            ps.executeUpdate();
            
            String sqlUpdate = "UPDATE determinacion SET es_compuesta = 1 WHERE codigo = ?";
            try (PreparedStatement ps2 = con.getConnection().prepareStatement(sqlUpdate)) {
                ps2.setString(1, codigoPadre.trim());
                ps2.executeUpdate();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizarNombre(String codigo, String nuevoNombre) {
        String sql = "UPDATE determinacion SET nombre = ? WHERE codigo = ?";
        try (PreparedStatement ps = con.getConnection().prepareStatement(sql)) {
            // Quitamos el .toUpperCase()
            ps.setString(1, nuevoNombre.trim());
            ps.setString(2, codigo.trim());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertarNuevaDeterminacion(String codigo, String nombre) {
        String sql = "INSERT INTO determinacion (codigo, nombre, ub, activo, es_compuesta, prioridad) VALUES (?, ?, 0.0, 1, 0, 999)";
        try (PreparedStatement ps = con.getConnection().prepareStatement(sql)) {
            ps.setString(1, codigo.trim());
            // Quitamos el .toUpperCase()
            ps.setString(2, nombre.trim()); 
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error al insertar determinación: " + e.getMessage());
            return false;
        }
    }

    public boolean desvincularHijo(String codigoPadre, String codigoHijo) {
        String sql = "DELETE FROM determinacion_componentes WHERE codigo_padre = ? AND codigo_hijo = ?";
        try (PreparedStatement ps = con.getConnection().prepareStatement(sql)) {
            ps.setString(1, codigoPadre.trim());
            ps.setString(2, codigoHijo.trim());
            int filasAfectadas = ps.executeUpdate();
            
            // ── BLINDAJE: Verificamos si el padre se quedó sin hijos ──
            String sqlCheck = "SELECT COUNT(*) FROM determinacion_componentes WHERE codigo_padre = ?";
            try (PreparedStatement psCheck = con.getConnection().prepareStatement(sqlCheck)) {
                psCheck.setString(1, codigoPadre.trim());
                ResultSet rs = psCheck.executeQuery();
                if (rs.next() && rs.getInt(1) == 0) {
                    // Ya no tiene hijos, lo volvemos a la normalidad y le quitamos la prioridad
                    String sqlUpdate = "UPDATE determinacion SET es_compuesta = 0, prioridad = 999 WHERE codigo = ?";
                    try (PreparedStatement psUpdate = con.getConnection().prepareStatement(sqlUpdate)) {
                        psUpdate.setString(1, codigoPadre.trim());
                        psUpdate.executeUpdate();
                    }
                }
            }
            return filasAfectadas > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizarPrioridad(String codigo, int nuevaPrioridad) {
        String sql = "UPDATE determinacion SET prioridad = ? WHERE codigo = ?";
        try (PreparedStatement ps = con.getConnection().prepareStatement(sql)) {
            ps.setInt(1, nuevaPrioridad);
            ps.setString(2, codigo.trim());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
}