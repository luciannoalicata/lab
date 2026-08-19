package modelo.dto;

import java.util.Date;

public class FilaEstadisticaDTO {
    private int idAnalisis;
    private Date fecha;
    private String dni;
    private String paciente;
    private String medico;
    private String obraSocial;
    private String practicas;

    public FilaEstadisticaDTO(int idAnalisis, Date fecha, String dni, String paciente, String medico, String obraSocial, String practicas) {
        this.idAnalisis = idAnalisis;
        this.fecha = fecha;
        this.dni = dni;
        this.paciente = paciente;
        this.medico = medico;
        this.obraSocial = obraSocial;
        this.practicas = practicas;
    }

    public int getIdAnalisis() { return idAnalisis; }
    public Date getFecha() { return fecha; }
    public String getDni() { return dni; }
    public String getPaciente() { return paciente; }
    public String getMedico() { return medico; }
    public String getObraSocial() { return obraSocial; }
    public String getPracticas() { return practicas; }
}