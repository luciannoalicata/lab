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

    public Determinacion buscarPorCodigo(String codigo) {
        String sql = "SELECT * FROM determinacion WHERE codigo = ? AND activo = TRUE";
        try (PreparedStatement ps = con.getConnection().prepareStatement(sql)) {
            ps.setString(1, codigo.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Determinacion> buscarPorCodigoParcial(String parcial) {
        List<Determinacion> lista = new ArrayList<>();
        String sql = """
            SELECT * FROM determinacion
            WHERE codigo LIKE ? AND activo = TRUE
            ORDER BY codigo
            LIMIT 10
        """;
        try (PreparedStatement ps = con.getConnection().prepareStatement(sql)) {
            ps.setString(1, parcial.trim() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
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

    public List<Determinacion> obtenerComponentes(String codigoPadre) {
        List<Determinacion> lista = new ArrayList<>();
        String sql = """
            SELECT d.* FROM determinacion d
            JOIN determinacion_componentes dc ON d.codigo = dc.codigo_hijo
            WHERE dc.codigo_padre = ? AND d.activo = TRUE
            ORDER BY d.prioridad ASC
        """;
        try (PreparedStatement ps = con.getConnection().prepareStatement(sql)) {
            ps.setString(1, codigoPadre.trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
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
            WHERE d.activo = TRUE 
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
        String texto = filtro.trim();
        
        boolean esNumero = texto.matches("\\d+");
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT d.*, ");
        sql.append("CASE WHEN EXISTS (SELECT 1 FROM determinacion_componentes WHERE codigo_padre = d.codigo) THEN 1 ELSE 0 END as tiene_hijos_real ");
        sql.append("FROM determinacion d ");
        sql.append("WHERE d.activo = TRUE ");
        sql.append("AND d.codigo NOT LIKE '%.%' ");
        sql.append("AND d.codigo NOT IN (SELECT codigo_hijo FROM determinacion_componentes) ");
        
        if (esNumero) {
            sql.append("AND (d.codigo LIKE ? OR d.codigo LIKE ?) ");
        } else{
            sql.append("AND d.nombre LIKE ? ");
        }
        
        sql.append("ORDER BY tiene_hijos_real DESC, ");
        sql.append("CASE WHEN EXISTS (SELECT 1 FROM determinacion_componentes WHERE codigo_padre = d.codigo) THEN d.prioridad ELSE 9999 END ASC, ");
        sql.append("TRIM(d.nombre) ASC ");
        sql.append("LIMIT 25");

        try (PreparedStatement ps = con.getConnection().prepareStatement(sql.toString())) {
            
            if (esNumero) {
                ps.setString(1, texto);            
                ps.setString(2, texto + "%");      
            } else {
                ps.setString(1, "%" + texto + "%");
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
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
        try (PreparedStatement ps = con.getConnection().prepareStatement(sql)) {
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
            WHERE activo = TRUE
            AND RIGHT(TRIM(codigo), LENGTH(?)) = ?
            ORDER BY nombre ASC
            LIMIT 15
        """;
        try (PreparedStatement ps = con.getConnection().prepareStatement(sql)) {
            ps.setString(1, sufijo.trim());
            ps.setString(2, sufijo.trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }
    
    public boolean vincularHijo(String codigoPadre, String codigoHijo) {
        String sql = "INSERT IGNORE INTO determinacion_componentes (codigo_padre, codigo_hijo) VALUES (?, ?)";
        try (PreparedStatement ps = con.getConnection().prepareStatement(sql)) {
            ps.setString(1, codigoPadre.trim());
            ps.setString(2, codigoHijo.trim());
            ps.executeUpdate();
            
            String sqlUpdate = "UPDATE determinacion SET es_compuesta = TRUE WHERE codigo = ?";
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
            ps.setString(1, nuevoNombre.trim());
            ps.setString(2, codigo.trim());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertarNuevaDeterminacion(String codigo, String nombre) {
        String sql = "INSERT INTO determinacion (codigo, nombre, ub, activo, es_compuesta, prioridad) VALUES (?, ?, 0.0, TRUE, FALSE, 999)";
        try (PreparedStatement ps = con.getConnection().prepareStatement(sql)) {
            ps.setString(1, codigo.trim());
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
            
            String sqlCheck = "SELECT COUNT(*) FROM determinacion_componentes WHERE codigo_padre = ?";
            try (PreparedStatement psCheck = con.getConnection().prepareStatement(sqlCheck)) {
                psCheck.setString(1, codigoPadre.trim());
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next() && rs.getInt(1) == 0) {
                        String sqlUpdate = "UPDATE determinacion SET es_compuesta = FALSE, prioridad = 999 WHERE codigo = ?";
                        try (PreparedStatement psUpdate = con.getConnection().prepareStatement(sqlUpdate)) {
                            psUpdate.setString(1, codigoPadre.trim());
                            psUpdate.executeUpdate();
                        }
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

    public List<String> obtenerSugerenciasPorNombreOCodigo(String busqueda) {
        List<String> sugerencias = new ArrayList<>();
        String sql = """
        SELECT codigo, nombre FROM determinacion 
        WHERE codigo LIKE ? OR nombre LIKE ? 
        ORDER BY nombre ASC LIMIT 10
    """;
        String filtro = "%" + busqueda + "%";
        try (PreparedStatement ps = con.getConnection().prepareStatement(sql)) {
            ps.setString(1, filtro);
            ps.setString(2, filtro);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    sugerencias.add(rs.getString("nombre") + " (" + rs.getString("codigo") + ")");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sugerencias;
    }
  
    // DeterminacionDAO.java
    public String obtenerNombrePorCodigo(String codigo) {
        String sql = "SELECT nombre FROM determinacion WHERE codigo = ? LIMIT 1";
        try (PreparedStatement ps = con.getConnection().prepareStatement(sql)) {
            ps.setString(1, codigo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("nombre");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // En DeterminacionDAO.java — agregar este método
    public List<String> obtenerSugerenciasPadresPorNombreOCodigo(String busqueda) {
        List<String> lista = new ArrayList<>();
        // Solo prácticas que NO tienen punto en el código = son padres
        String sql = "SELECT codigo, nombre FROM determinacion "
                + "WHERE activo = 1 AND codigo NOT LIKE '%.%' "
                + "AND (nombre LIKE ? OR codigo LIKE ?) "
                + "ORDER BY nombre ASC LIMIT 10";
        try (PreparedStatement ps = con.getConnection().prepareStatement(sql)) {
            String like = "%" + busqueda + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(rs.getString("codigo") + " — " + rs.getString("nombre"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

}
