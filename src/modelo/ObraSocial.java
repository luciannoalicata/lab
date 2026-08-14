package modelo;

// @author lucianoalicata

public class ObraSocial {
    
    private String codigo;
    private String nombre;
    private double arancel;

    public ObraSocial(String codigo, String nombre, double arancel) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.arancel = arancel;
    }

    public ObraSocial() {
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getArancel() {
        return arancel;
    }

    public void setArancel(double arancel) {
        this.arancel = arancel;
    }
}