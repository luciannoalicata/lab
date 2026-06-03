package dao;

import modelo.Paciente;
import modelo.Conexion;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.sql.ResultSet;

public class PacienteDAO {

    private Conexion con;

    public PacienteDAO(Conexion con) {
        this.con = con;
    }
    
    public boolean guardarPaciente(Paciente p) {
    String sql = """
        INSERT INTO paciente
        (dni, nombre, apellido, edad, direccion, localidad,
         nro_afiliado, obra_social, sexo, celular)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """;

    try {
        PreparedStatement ps = con.getConnection().prepareStatement(sql);

        ps.setString(1, p.getDni());
        ps.setString(2, convertirNombrePropio(p.getNombre()));
        ps.setString(3, convertirNombrePropio(p.getApellido()));
        ps.setString(4, p.getEdad());
        ps.setString(5, convertirNombrePropio(p.getDireccion()));
        ps.setString(6, convertirNombrePropio(p.getLocalidad()));
        ps.setString(7, p.getNroAfiliado().toUpperCase());
        ps.setString(8, p.getObraSocial());
        ps.setString(9, p.getSexo());
        ps.setString(10, p.getCelular());

        return ps.executeUpdate() > 0;

    } catch (java.sql.SQLIntegrityConstraintViolationException e) {
        // No imprimimos el stackTrace para evitar el mensaje en consola
        System.out.println("Intento de registro duplicado: DNI " + p.getDni());
        return false;
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

// Método adicional para validar existencia previa
public boolean existeDNI(String dni) {
    String sql = "SELECT COUNT(*) FROM paciente WHERE dni = ?";
    try {
        PreparedStatement ps = con.getConnection().prepareStatement(sql);
        ps.setString(1, dni);
        var rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return false;
}
    
public ArrayList<Paciente> listarPacientes() {
    ArrayList<Paciente> lista = new ArrayList<>();
    // Usamos COALESCE para que los pacientes sin análisis vayan al final (fecha muy vieja)
    String sql = """
        SELECT p.*, MAX(a.fecha) as ultima_fecha
        FROM paciente p
        LEFT JOIN analisis a ON p.id_paciente = a.id_paciente
        GROUP BY p.id_paciente
        ORDER BY apellido
    """;

    try {
        PreparedStatement ps = con.getConnection().prepareStatement(sql);
        ResultSet res = ps.executeQuery();

        while (res.next()) {
            Paciente p = new Paciente();
            p.setIdPaciente(res.getInt("id_paciente"));
            p.setNombre(res.getString("nombre"));
            p.setApellido(res.getString("apellido"));
            p.setDni(res.getString("dni"));
            p.setVersion(res.getInt("version"));
            
            // IMPORTANTE: Cambiamos getDate por getTimestamp para traer la hora
            java.sql.Timestamp ts = res.getTimestamp("ultima_fecha");
            if (ts != null) {
                p.setFechaUltimoAnalisis(new java.util.Date(ts.getTime()));
            }
            
            lista.add(p);
        }
    } catch (Exception ex) {
        System.out.println("Error en listarPacientes: " + ex);
    }
    return lista;
}
    
    public boolean actualizar(Paciente p) {
        // La clave es: version = version + 1  Y EL WHERE pedia la version vieja
        String sql = """
            UPDATE paciente SET 
            dni=?, nombre=?, apellido=?, edad=?, direccion=?, 
            localidad=?, nro_afiliado=?, obra_social=?, sexo=?, celular=?,
            version = version + 1
            WHERE id_paciente=? AND version=?
        """;

        try {
            PreparedStatement ps = con.getConnection().prepareStatement(sql);
            ps.setString(1, p.getDni());
            ps.setString(2, convertirNombrePropio(p.getNombre()));
            ps.setString(3, convertirNombrePropio(p.getApellido()));
            ps.setString(4, p.getEdad());
            ps.setString(5, convertirNombrePropio(p.getDireccion()));
            ps.setString(6, convertirNombrePropio(p.getLocalidad()));
            ps.setString(7, p.getNroAfiliado().toUpperCase());
            ps.setString(8, p.getObraSocial());
            ps.setString(9, p.getSexo());
            ps.setString(10, p.getCelular());
            
            ps.setInt(11, p.getIdPaciente());
            ps.setInt(12, p.getVersion()); // Enviamos la versión que el usuario leyó

            // Si executeUpdate devuelve 0, significa que el WHERE version=? falló
            // (alguien más ya subió la versión en la DB)
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    

public ArrayList<Paciente> buscarPorDniOApellidoONombre(String texto) {
    ArrayList<Paciente> lista = new ArrayList<>();
    if (texto == null || texto.trim().isEmpty()) return listarPacientes();

    String[] palabras = texto.trim().split("\\s+");
    StringBuilder sql = new StringBuilder();

    // CORRECCIÓN: Cambiamos a.fechaAnalisis por a.fecha
    sql.append("SELECT p.*, MAX(a.fecha) as ultimo_analisis ");
    sql.append("FROM paciente p ");
    sql.append("LEFT JOIN analisis a ON p.id_paciente = a.id_paciente ");
    sql.append("WHERE ");

    if (texto.matches("\\d+")) {
        sql.append("p.dni LIKE ? ");
    } else {
        sql.append("(");
        for (int i = 0; i < palabras.length; i++) {
            if (i > 0) sql.append(" AND ");
            sql.append("LOWER(CONCAT(p.apellido, ' ', p.nombre)) LIKE ?");
        }
        sql.append(") ");
    }
    
    sql.append("GROUP BY p.id_paciente ");
    sql.append("ORDER BY p.apellido ASC");

    try {
        PreparedStatement ps = con.getConnection().prepareStatement(sql.toString());

        if (texto.matches("\\d+")) {
            ps.setString(1, "%" + texto + "%");
        } else {
            for (int i = 0; i < palabras.length; i++) {
                ps.setString(i + 1, "%" + palabras[i].toLowerCase() + "%");
            }
        }

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Paciente p = new Paciente();
            p.setIdPaciente(rs.getInt("id_paciente"));
            p.setDni(rs.getString("dni"));
            p.setNombre(rs.getString("nombre"));
            p.setApellido(rs.getString("apellido"));
            
            // Aquí recuperamos la fecha del JOIN usando el alias "ultimo_analisis"
            p.setFechaUltimoAnalisis(rs.getDate("ultimo_analisis")); 

            p.setObraSocial(rs.getString("obra_social"));
            // (Asegúrate de rellenar el resto de los campos necesarios aquí)
            lista.add(p);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return lista;
}

    public Paciente buscarPorId(int id) {
        String sql = "SELECT * FROM paciente WHERE id_paciente = ?";
        try {
            PreparedStatement ps = con.getConnection().prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet res = ps.executeQuery();

            if (res.next()) {
                Paciente p = new Paciente();
                p.setIdPaciente(res.getInt("id_paciente"));
                p.setDni(res.getString("dni"));
                p.setNombre(res.getString("nombre"));
                p.setApellido(res.getString("apellido"));
                p.setEdad(res.getString("edad"));
                p.setDireccion(res.getString("direccion"));
                p.setLocalidad(res.getString("localidad"));
                p.setNroAfiliado(res.getString("nro_afiliado"));
                p.setObraSocial(res.getString("obra_social"));
                p.setSexo(res.getString("sexo"));
                p.setCelular(res.getString("celular"));
                p.setVersion(res.getInt("version")); // <--- NO OLVIDAR ESTO
                return p;
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }
    
    private String convertirNombrePropio(String texto) {
    if (texto == null || texto.trim().isEmpty()) return "";
    
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

