package modelo.dto;
import java.util.Date;

public class FilaFacturacionDTO {
    private int idAnalisis;
    private Date fecha;
    private String paciente;
    private String obraSocial;
    private String medico;
    private double precio;

    public FilaFacturacionDTO(int idAnalisis, Date fecha, String paciente, String obraSocial, String medico, double precio) {
        this.idAnalisis = idAnalisis;
        this.fecha = fecha;
        this.paciente = paciente;
        this.obraSocial = obraSocial;
        this.medico = medico;
        this.precio = precio;
    }

    public int getIdAnalisis() { return idAnalisis; }
    public Date getFecha() { return fecha; }
    public String getPaciente() { return paciente; }
    public String getObraSocial() { return obraSocial; }
    public String getMedico() { return medico; }
    public double getPrecio() { return precio; }
}