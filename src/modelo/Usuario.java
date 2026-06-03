package modelo;

public class Usuario {
    private int idUsuario;
    private String username;
    private String rol; // ADMIN, BIOQUIMICO, TECNICO
    private boolean activo;

    // Constructores, Getters y Setters
    public Usuario() {}

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
