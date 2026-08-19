package modelo.dto;

public class MetricaDTO {
    
    private String categoria;
    private int cantidad;
    private double total;

    public MetricaDTO() {
    }

    public MetricaDTO(String categoria, int cantidad, double total) {
        this.categoria = categoria;
        this.cantidad = cantidad;
        this.total = total;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}