package dao;

import modelo.Conexion;

public class DAOFactory {
    private final Conexion con;

    public DAOFactory(Conexion con) { this.con = con; }

    public MedicoDAO getMedicoDAO() {
        return new MedicoDAO(con);
    }
    public PacienteDAO getPacienteDAO() {
        return new PacienteDAO(con);
    }
    public AnalisisDAO getAnalisisDAO() {
        return new AnalisisDAO(con);
    }
    public ObraSocialDAO getObraSocialDAO() {
        return new ObraSocialDAO(con);
    }
    public DeterminacionDAO getDeterminacionDAO() {
        return new DeterminacionDAO(con);
    }
    public ResultadoAnalisisDAO getResultadoDAO() {
        return new ResultadoAnalisisDAO(con);
    }
    public UsuarioDAO getUsuarioDAO() {
        return new UsuarioDAO(con);
    }
    public ConfiguracionDAO getConfigDAO() {
        return new ConfiguracionDAO(con);
    }
    public AuditoriaDAO getAuditoriaDAO() {
        return new AuditoriaDAO(con);
    }
}