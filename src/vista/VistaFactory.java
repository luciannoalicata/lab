package vista;

import vista.interfaces.*;
import vista.swing.*; 

public class VistaFactory {
    
    public IVistaPrincipal getVistaPrincipal() {
        return new VistaPrincipal(); 
    }

    public IVistaLogin getVistaLogin() {
        return new VistaLogin();
    }
    
    public IVistaMedicos getVistaMedicos() {
        return new VistaMedicos(); 
    }

    public IVistaAnalisis getVistaAnalisis() {
        return new VistaAnalisis();
    }
    
    public IVistaPaciente getVistaPaciente(){
        return new VistaPaciente();
    }
    
    public IVistaHistorialAnalisis getVistaHistorialAnalisis() {
        return new VistaHistorialAnalisis();
    }

    public IVistaVerDetalleAnalisis getVistaVerDetalleAnalisis() {
        return new VistaVerDetalleAnalisis();
    }

    public IVistaDeterminaciones getVistaDeterminaciones() {
        return new VistaDeterminaciones();
    }

    public IVistaObraSocial getVistaObraSocial() {
        return new VistaObraSocial();
    }

    public IVistaNBU getVistaNBU() {
        return new VistaNBU();
    }

    public IVistaCargarResultados getVistaCargarResultados() {
        return new VistaCargarResultados();
    }

    public IVistaGestionUsuarios getVistaGestionUsuarios() {
        return new VistaGestionUsuarios();
    }

    public IVistaAuditoria getVistaAuditoria() {
        return new VistaAuditoria();
    }

    public IVistaAjustes getVistaAjustes() {
        return new VistaAjustes(null);
    }

}