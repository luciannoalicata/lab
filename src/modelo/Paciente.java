package modelo;

// @author lucianoalicata

import java.util.Date;

public class Paciente {

    private int idPaciente;
    private String dni;
    private String nombre;
    private String apellido;
    private String edad;
    private String direccion;
    private String localidad;
    private String nroAfiliado;
    private String obraSocial;
    private String sexo;
    private String celular;
    private int version;
    private Date fechaUltimoAnalisis;

    public Paciente(int idPaciente, String dni, String nombre, String apellido, String edad, String direccion, String localidad, String nroAfiliado, String obraSocial, String sexo, String celular, int version, Date fechaUltimoAnalisis) {
        this.idPaciente = idPaciente;
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
        this.direccion = direccion;
        this.localidad = localidad;
        this.nroAfiliado = nroAfiliado;
        this.obraSocial = obraSocial;
        this.sexo = sexo;
        this.celular = celular;
        this.version = version;
        this.fechaUltimoAnalisis = fechaUltimoAnalisis;
    }

    public Date getFechaUltimoAnalisis() {
        return fechaUltimoAnalisis;
    }

    public void setFechaUltimoAnalisis(Date fechaUltimoAnalisis) {
        this.fechaUltimoAnalisis = fechaUltimoAnalisis;
    }
    
    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Paciente() {
    }

    public int getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(int idPaciente) {
        this.idPaciente = idPaciente;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEdad() {
        return edad;
    }

    public void setEdad(String edad) {
        this.edad = edad;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getNroAfiliado() {
        return nroAfiliado;
    }

    public void setNroAfiliado(String nroAfiliado) {
        this.nroAfiliado = nroAfiliado;
    }

    public String getObraSocial() {
        return obraSocial;
    }

    public void setObraSocial(String obraSocial) {
        this.obraSocial = obraSocial;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }
}