package modelo;
/**
 *
 * @author luciano
 */
public class Medico {
    
    private String apellidoMedico;
    private String nombreMedico;
    private String matricula;
    private String especialidad;
    private String observaciones;

    public String getApellidoMedico() {
        return apellidoMedico;
    }

    public void setApellidoMedico(String apellidoMedico) {
        this.apellidoMedico = apellidoMedico;
    }

    public String getNombreMedico() {
        return nombreMedico;
    }

    public void setNombreMedico(String nombreMedico) {
        this.nombreMedico = nombreMedico;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Medico() {
    }

    public Medico(String apellidoMedico, String nombreMedico, String matricula, String especialidad, String observaciones) {
        this.apellidoMedico = apellidoMedico;
        this.nombreMedico = nombreMedico;
        this.matricula = matricula;
        this.especialidad = especialidad;
        this.observaciones = observaciones;
    }
    
}
