package vista.interfaces;

// @author lucianoalicata

import presentador.AjustesPresenter;

public interface IVistaAjustes {
    
    String BTN_ACTUALIZAR_CLAVE ="cambiar_clave";
    String BTN_ACTUALIZAR_DATOS="actualizar_datos";
    String BTN_GUARDAR_CONFIGURACION = "guardar_config";
    String BTN_GUARDAR_UB = "guardar_valor_ub";
    String getValorUB();
    
    void setValorUB(String valor);
    void habilitarSeccionAranceles(boolean habilitar);
    void ejecutar();
    void setPresenter(AjustesPresenter presenter);    
    int confirmarAccion(String mensaje, String titulo);
    void limpiarFocos();
    void cerrarPantalla();
    void setUsuarioActual(String usuario);
    String getMatriculaFirma();
    String getAclaracionFirma();
    void setMatriculaFirma(String m);
    void setAclaracionFirma(String a);
    void setNombreLaboratorioACtual(String nombre);
    void setDireccion(String direccion);
    void setLocalidad(String localidad);
    void setTelefono(String telefono);
    void setBioquimico(String bioquimico);
    void setMatricula(String matricula);
    void setLogo(String logo);
    void setFirma(String firma);
    void setBackup(String b);
    String getRutaPdf();
    void setRutaPdf(String ruta);
    String getClaveActual();
    String getNuevaClave();
    String getRepetirNuevaClave();
    String getNombreLaboratorio();
    String getDireccion();
    String getLocalidad();
    String getTelefono();
    String getBioquimico();
    String getMatricula();
    String getLogo();
    String getFirma();
    String getRutaBackup();
    void setRutaBackup(String ruta);
    String getTamanoHoja();
    String getOrientacion();
    boolean isIncluirLogo();
    boolean isAutoPrint();
    void setTamanoHoja(String tamano);
    void setOrientacion(String orientacion);
    void setIncluirLogo(boolean incluir);
    void setAutoPrint(boolean auto);
    void limpiarCampos();
    void mostrarMensaje(String mensaje); 
}
