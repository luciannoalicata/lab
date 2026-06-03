/**
 *
 * @author luciano
 */
package modelo;

import java.util.Date;
import java.util.List;

public class Analisis {

    private int idAnalisis;
    private int idPaciente;
    private Date fecha;
    private double precio;
    private String observaciones;
    private String medicoSolicitante;
    private String estado;
    private String obraSocial;
    private String pacienteNombre;
    private String pacienteApellido;
    private List<ResultadoAnalisis> resultados;
    private String pacienteDni;
public String getPacienteDni() { return pacienteDni; }
public void setPacienteDni(String pacienteDni) { this.pacienteDni = pacienteDni; }

    

    public Analisis() {
    }

    public Analisis(int idAnalisis, int idPaciente, Date fecha, double precio, String observaciones, String medicoSolicitante, String estado, List<ResultadoAnalisis> resultados) {
        this.idAnalisis = idAnalisis;
        this.idPaciente = idPaciente;
        this.fecha = fecha;
        this.precio = precio;
        this.observaciones = observaciones;
        this.medicoSolicitante = medicoSolicitante;
        this.estado = estado;
        this.resultados = resultados;
    }
    
    // 🔥 HELPER ÚTIL
    public String getPacienteNombreCompleto() {
        if (pacienteApellido == null && pacienteNombre == null) return "Desconocido";
        return pacienteApellido + " " + pacienteNombre;
    }

    // 🔥 GETTERS Y SETTERS PARA LOS NUEVOS CAMPOS
    public String getPacienteNombre() { return pacienteNombre; }
    public void setPacienteNombre(String pacienteNombre) { this.pacienteNombre = pacienteNombre; }

    public String getPacienteApellido() { return pacienteApellido; }
    public void setPacienteApellido(String pacienteApellido) { this.pacienteApellido = pacienteApellido; }

    public int getIdAnalisis() {
        return idAnalisis;
    }

    public void setIdAnalisis(int idAnalisis) {
        this.idAnalisis = idAnalisis;
    }

    public int getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(int idPaciente) {
        this.idPaciente = idPaciente;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public List<ResultadoAnalisis> getResultados() {
        return resultados;
    }

    public void setResultados(List<ResultadoAnalisis> resultados) {
        this.resultados = resultados;
    }

    public String getMedicoSolicitante() {
        return medicoSolicitante;
    }

    public void setMedicoSolicitante(String medicoSolicitante) {
        this.medicoSolicitante = medicoSolicitante;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getObraSocial() {
        return obraSocial;
    }

    public void setObraSocial(String obraSocial) {
        this.obraSocial = obraSocial;
    }
    
    
}

