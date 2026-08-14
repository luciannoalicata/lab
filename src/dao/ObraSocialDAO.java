package dao;

import modelo.Conexion;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.sql.ResultSet;
import modelo.ObraSocial;

public class ObraSocialDAO {

    private Conexion con;

    public ObraSocialDAO(Conexion con) {
        this.con = con;
    }
    
    public boolean agregarObraSocial(ObraSocial obs){
        String sql = "INSERT INTO obra_social (codigo, nombre, arancel) VALUES (?, ?, ?)";
        try (PreparedStatement ps = con.getConnection().prepareStatement(sql)) {
            ps.setString(1, obs.getCodigo().toUpperCase());
            ps.setString(2, obs.getNombre().toUpperCase());
            ps.setDouble(3, obs.getArancel());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public ArrayList<ObraSocial> listarObrasSociales(){
        ArrayList<ObraSocial> lista = new ArrayList<>();
        String sql = "SELECT * FROM obra_social ORDER BY nombre ASC";
        try (PreparedStatement ps = con.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
             
            while(rs.next()){
                lista.add(new ObraSocial(rs.getString("codigo"), rs.getString("nombre"), rs.getDouble("arancel")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }
    
    public ArrayList<ObraSocial> buscarPorCodigoONombre(String texto) {
        ArrayList<ObraSocial> lista = new ArrayList<>();
        String sql = "SELECT * FROM obra_social WHERE codigo LIKE ? OR nombre LIKE ? ORDER BY nombre ASC";
        try (PreparedStatement ps = con.getConnection().prepareStatement(sql)) {
            String filtro = "%" + texto + "%";
            ps.setString(1, filtro);
            ps.setString(2, filtro);
            
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()){
                    lista.add(new ObraSocial(rs.getString("codigo"), rs.getString("nombre"), rs.getDouble("arancel")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }
    
    public boolean actualizarArancel(String codigo, double nuevoArancel) {
        String sql = "UPDATE obra_social SET arancel = ? WHERE codigo = ?";
        try (PreparedStatement ps = con.getConnection().prepareStatement(sql)) {
            ps.setDouble(1, nuevoArancel);
            ps.setString(2, codigo);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean eliminarObraSocial(String codigo){
        String sql = "DELETE FROM obra_social WHERE codigo = ?";
        try (PreparedStatement ps = con.getConnection().prepareStatement(sql)) {
            ps.setString(1, codigo);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    } 
}