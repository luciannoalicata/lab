package modelo.dto;

public class ResumenGlobalDTO {
    private int totalAnalisis;
    private double totalFacturado;

    public ResumenGlobalDTO(int totalAnalisis, double totalFacturado) {
        this.totalAnalisis = totalAnalisis;
        this.totalFacturado = totalFacturado;
    }

    public int getTotalAnalisis() { return totalAnalisis; }
    public double getTotalFacturado() { return totalFacturado; }
}