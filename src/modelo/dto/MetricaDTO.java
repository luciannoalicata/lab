package modelo.dto;

public class MetricaDTO {
    private String categoria;
    private int cantidad;  
    private double total;     

    public MetricaDTO(String categoria, int cantidad, double total) {
        this.categoria = categoria;
        this.cantidad = cantidad;
        this.total = total;
    }

    public String getCategoria() { return categoria; }
    public int getCantidad() { return cantidad; }
    public double getTotal() { return total; }
}
