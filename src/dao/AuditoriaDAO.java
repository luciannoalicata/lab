package dao;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.util.ArrayList;
import modelo.Auditoria;
import modelo.Conexion;
import modelo.Usuario;

/**
 *
 * @author luciano
 */
public class AuditoriaDAO {
    private Conexion con;

    public AuditoriaDAO(Conexion con) { this.con = con; }

    public boolean registrar(Usuario u, String accion, String tabla, int idReg, String vAnt, String vNue, String detalle) {
        String sql = "INSERT INTO auditoria (id_usuario, usuario_nombre, rol_usuario, accion, tabla_afectada, id_registro_afectado, valor_anterior, valor_nuevo, detalle) VALUES (?,?,?,?,?,?,?,?,?)";
        try {
            PreparedStatement ps = con.getConnection().prepareStatement(sql);
            ps.setInt(1, u.getIdUsuario());
            ps.setString(2, u.getUsername());
            ps.setString(3, u.getRol());
            ps.setString(4, accion);
            ps.setString(5, tabla);
            ps.setInt(6, idReg);
            ps.setString(7, vAnt);
            ps.setString(8, vNue);
            ps.setString(9, detalle);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<Auditoria> listarConFiltros(String nombreUsuario, java.util.Date fecha) {
    ArrayList<Auditoria> lista = new ArrayList<>();
    StringBuilder sql = new StringBuilder("SELECT * FROM auditoria WHERE 1=1 ");
    
    if (nombreUsuario != null && !nombreUsuario.equals("Todos")) {
        sql.append(" AND usuario_nombre = ? ");
    }
    if (fecha != null) {
        sql.append(" AND DATE(fecha_hora) = ? ");
    }
    sql.append(" ORDER BY fecha_hora DESC LIMIT 1000");

    try {
        PreparedStatement ps = con.getConnection().prepareStatement(sql.toString());
        int i = 1;
        if (nombreUsuario != null && !nombreUsuario.equals("Todos")) {
            ps.setString(i++, nombreUsuario);
        }
        if (fecha != null) {
            ps.setDate(i++, new java.sql.Date(fecha.getTime()));
        }

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Auditoria a = new Auditoria();
            a.setIdAuditoria(rs.getInt("id_auditoria"));
            a.setFechaHora(rs.getTimestamp("fecha_hora"));
            a.setUsuarioNombre(rs.getString("usuario_nombre"));
            a.setAccion(rs.getString("accion"));
            a.setTablaAfectada(rs.getString("tabla_afectada"));
            a.setValorAnterior(rs.getString("valor_anterior"));
            a.setValorNuevo(rs.getString("valor_nuevo"));
            a.setDetalle(rs.getString("detalle"));
            lista.add(a);
        }
    } catch (Exception e) { e.printStackTrace(); }
    return lista;
}
}
