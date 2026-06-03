package vista;

import presentador.Controlador;
/**
 *
 * @author luciano
 */
public interface IVistaAjustes {
    
    String BTN_ACTUALIZAR_CLAVE ="cambiar_clave";
    String BTN_ACTUALIZAR_DATOS="actualizar_datos";
    String BTN_GUARDAR_CONFIGURACION = "guardar_config";
    String BTN_GUARDAR_UB = "guardar_valor_ub";
    String getValorUB();
    
    void setValorUB(String valor);

    void habilitarSeccionAranceles(boolean habilitar);

    public void ejecutar();
    public void setControlador(Controlador control);
    
    public void setUsuarioActual(String usuario);

    String getMatriculaFirma();
    String getAclaracionFirma();
    void setMatriculaFirma(String m);
    void setAclaracionFirma(String a);
    
    public void setNombreLaboratorioACtual(String nombre);
    public void setDireccion(String direccion);
    public void setLocalidad(String localidad);
    public void setTelefono(String telefono);
    public void setBioquimico(String bioquimico);
    public void setMatricula(String matricula);
    public void setLogo(String logo);
    public void setFirma(String firma);
    public void setBackup(String b);
    public String getRutaPdf();
    public void setRutaPdf(String ruta);
  
    public String getClaveActual();
    public String getNuevaClave();
    public String getRepetirNuevaClave();
    public String getNombreLaboratorio();
    public String getDireccion();
    public String getLocalidad();
    public String getTelefono();
    public String getBioquimico();
    public String getMatricula();
    public String getLogo();
    public String getFirma();
    
    String getRutaBackup();

    void setRutaBackup(String ruta);

    public String getTamanoHoja();

    public String getOrientacion();

    public boolean isIncluirLogo();

    public boolean isAutoPrint();

    public void setTamanoHoja(String tamano);

    public void setOrientacion(String orientacion);

    public void setIncluirLogo(boolean incluir);

    public void setAutoPrint(boolean auto);
    
    public void limpiarCampos();
    public void mostrarMensaje(String mensaje); 
}
