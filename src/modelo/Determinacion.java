package modelo;

public class Determinacion {

    private int id;
    private String codigo;
    private String nombre;
    private String unidad;
    private String referencia;
    private double ub;
    private int prioridad;
    private boolean esCompuesta;
    private String area;

    public boolean isEsCompuesta() { return esCompuesta; }
    public void setEsCompuesta(boolean esCompuesta) { this.esCompuesta = esCompuesta; }
    
    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }

    // getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getUnidad() { return unidad; }
    public void setUnidad(String unidad) { this.unidad = unidad; }

    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }

    public int getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }

    public double getUb() {
        return ub;
    }

    public void setUb(double ub) {
        this.ub = ub;
    }
}

