package presentador;

// @author lucianoalicata

import dao.ObraSocialDAO;
import modelo.ObraSocial;
import vista.interfaces.IVistaObraSocial;
import presentador.router.AppRouter;
import java.util.ArrayList;

public class ObraSocialPresenter {

    private final IVistaObraSocial vos;
    private final AppRouter router; 
    private final ObraSocialDAO obraSocialDAO;
    private boolean actualizandoVista = false; 

    public ObraSocialPresenter(IVistaObraSocial vos, AppRouter router, ObraSocialDAO obraSocialDAO) {
        this.vos = vos;
        this.router = router;
        this.obraSocialDAO = obraSocialDAO;
    }

    public void iniciar() {
        vos.setPresenter(this);
        vos.limpiarCampos();
        cargarObrasSocialesEnTabla();
    }

    private void cargarObrasSocialesEnTabla() {
        actualizandoVista = true; 
        ArrayList<ObraSocial> lista = obraSocialDAO.listarObrasSociales();
        vos.cargarObrasSocialesEnTabla(lista);
        actualizandoVista = false; 
    }

    private void cargarObrasSocialesFiltradas(ArrayList<ObraSocial> lista) {
        actualizandoVista = true;
        vos.cargarObrasSocialesEnTabla(lista);
        actualizandoVista = false;
    }

    public void onAgregarOS() {
        String cod = vos.getCodigoObraSocial();
        String nom = vos.getNombreObraSocial();
        double ara = vos.getArancel();

        if (cod.isEmpty() || nom.isEmpty() || ara < 0) {
            vos.mostrarMensaje("Todos los campos son obligatorios.");
            return;
        }

        ObraSocial nueva = new ObraSocial(cod, nom, ara);
        if (obraSocialDAO.agregarObraSocial(nueva)) {
            vos.mostrarMensaje("Obra Social agregada con éxito.");
            limpiarYRecargar();
        } else {
            vos.mostrarMensaje("Error: El código ya existe o hubo un fallo al guardar.");
        }
    }

    public void onEliminarOS() {
        ObraSocial selEliminar = vos.getObraSocialSeleccionada();
        
        if (selEliminar == null) {
            vos.mostrarMensaje("Por favor, seleccione una obra social de la tabla.");
            return;
        }
        
        int confirm = vos.confirmarAccion("¿Eliminar la obra social " + selEliminar.getNombre() + "?", "Confirmar");
        if (confirm == 0) { 
            if (obraSocialDAO.eliminarObraSocial(selEliminar.getCodigo())) {
                vos.mostrarMensaje("Obra social eliminada.");
                limpiarYRecargar();
            } else {
                vos.mostrarMensaje("No se pudo eliminar la obra social.");
            }
        }
    }

    public void onCambiarArancel() {
        if (actualizandoVista) {
            return;
        }
        
        ObraSocial selArancel = vos.getObraSocialSeleccionada();
        
        if (selArancel == null) {
            vos.mostrarMensaje("Seleccione una obra social para modificar su arancel.");
            return;
        }

        String inputArancel = vos.pedirDato("Ingrese el nuevo arancel para " + selArancel.getNombre() + ":", "Actualizar Arancel");

        if (inputArancel != null && !inputArancel.trim().isEmpty()) {
            try {
                double nuevoAra = Double.parseDouble(inputArancel.replace(",", "."));
                if (obraSocialDAO.actualizarArancel(selArancel.getCodigo(), nuevoAra)) {
                    vos.mostrarMensaje("Arancel actualizado.");
                    limpiarYRecargar();
                } else {
                    vos.mostrarMensaje("Fallo al actualizar el arancel en la base de datos.");
                }
            } catch (NumberFormatException ex) {
                vos.mostrarMensaje("Error: El valor ingresado no es un número válido.");
            }
        }
    }
    
    private void limpiarYRecargar() {
        actualizandoVista = true;
        vos.limpiarCampos();
        cargarObrasSocialesEnTabla();
        actualizandoVista = false;
    }

    public void onBuscarOS() {
        String filtro = vos.getTextoBusqueda();
        ArrayList<ObraSocial> filtradas;

        if (filtro.isEmpty()) {
            filtradas = obraSocialDAO.listarObrasSociales();
        } else {
            filtradas = obraSocialDAO.buscarPorCodigoONombre(filtro);
        }
        cargarObrasSocialesFiltradas(filtradas);
    }

    public void onVolver() {
        router.irAInicio();
    }

    public void onSeleccionarOS() {
        if (actualizandoVista) {
            return;
        }
        
        ObraSocial seleccionada = vos.getObraSocialSeleccionada();
        if (seleccionada != null) {
            vos.habilitarBotonEliminar(true);
        } else {
            vos.habilitarBotonEliminar(false);
        }
    }
}