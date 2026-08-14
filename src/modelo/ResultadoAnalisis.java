package modelo;

// @author lucianoalicata

public class ResultadoAnalisis {
    
    private int idResultado;
    private int idAnalisis;
    private String codigo;
    private String nombrePrueba;
    private String resultado;
    private String unidad;
    private String referencia;
    private boolean imprimir;
    private int prioridad;

    public ResultadoAnalisis() {
    }

    public ResultadoAnalisis(int idResultado, int idAnalisis, String codigo, String nombrePrueba, String resultado, String unidad, String referencia, boolean imprimir, int prioridad) {
        this.idResultado = idResultado;
        this.idAnalisis = idAnalisis;
        this.codigo = codigo;
        this.nombrePrueba = nombrePrueba;
        this.resultado = resultado;
        this.unidad = unidad;
        this.referencia = referencia;
        this.imprimir = imprimir;
        this.prioridad = prioridad;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }
    

    public int getIdResultado() {
        return idResultado;
    }

    public void setIdResultado(int idResultado) {
        this.idResultado = idResultado;
    }

    public int getIdAnalisis() {
        return idAnalisis;
    }

    public void setIdAnalisis(int idAnalisis) {
        this.idAnalisis = idAnalisis;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombrePrueba() {
        return nombrePrueba;
    }

    public void setNombrePrueba(String nombrePrueba) {
        this.nombrePrueba = nombrePrueba;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public boolean isImprimir() {
        return imprimir;
    }

    public void setImprimir(boolean imprimir) {
        this.imprimir = imprimir;
    }
}