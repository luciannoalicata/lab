package modelo;

// @author lucianoalicata

import java.sql.Timestamp;

public class Auditoria {
    private int idAuditoria;
    private Timestamp fechaHora;
    private int idUsuario;
    private String usuarioNombre;
    private String rolUsuario;
    private String accion;
    private String tablaAfectada;
    private int idRegistroAfectado;
    private String valorAnterior;
    private String valorNuevo;
    private String detalle;

    public Auditoria() {}

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getRolUsuario() {
        return rolUsuario;
    }

    public void setRolUsuario(String rolUsuario) {
        this.rolUsuario = rolUsuario;
    }

    public int getIdRegistroAfectado() {
        return idRegistroAfectado;
    }

    public void setIdRegistroAfectado(int idRegistroAfectado) {
        this.idRegistroAfectado = idRegistroAfectado;
    }
   
    public int getIdAuditoria() { return idAuditoria; }
    public void setIdAuditoria(int idAuditoria) { this.idAuditoria = idAuditoria; }
    public Timestamp getFechaHora() { return fechaHora; }
    public void setFechaHora(Timestamp fechaHora) { this.fechaHora = fechaHora; }
    public String getUsuarioNombre() { return usuarioNombre; }
    public void setUsuarioNombre(String usuarioNombre) { this.usuarioNombre = usuarioNombre; }
    public String getAccion() { return accion; }
    public void setAccion(String accion) { this.accion = accion; }
    public String getTablaAfectada() { return tablaAfectada; }
    public void setTablaAfectada(String tablaAfectada) { this.tablaAfectada = tablaAfectada; }
    public String getValorAnterior() { return valorAnterior; }
    public void setValorAnterior(String valorAnterior) { this.valorAnterior = valorAnterior; }
    public String getValorNuevo() { return valorNuevo; }
    public void setValorNuevo(String valorNuevo) { this.valorNuevo = valorNuevo; }
    public String getDetalle() { return detalle; }
    public void setDetalle(String detalle) { this.detalle = detalle; }
}