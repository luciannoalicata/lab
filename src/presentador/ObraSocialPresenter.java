package presentador;

import dao.ObraSocialDAO;
import modelo.ObraSocial;
import vista.interfaces.IVistaObraSocial;
import presentador.router.AppRouter;
import java.util.ArrayList;

public class ObraSocialPresenter {

    private final IVistaObraSocial vos;
    private final AppRouter router; 
    private final ObraSocialDAO obraSocialDAO;

    // 2. CAMBIA ESTO EN EL CONSTRUCTOR
    public ObraSocialPresenter(IVistaObraSocial vos, AppRouter router, ObraSocialDAO obraSocialDAO) {
        this.vos = vos;
        this.router = router;
        this.obraSocialDAO = obraSocialDAO;
    }

    public void iniciar() {
        vos.setPresenter(this); // Conectamos la vista
        vos.limpiarCampos();
        cargarObrasSocialesEnTabla();
    }

    private void cargarObrasSocialesEnTabla() {
        ArrayList<ObraSocial> lista = obraSocialDAO.listarObrasSociales();
        vos.cargarObrasSocialesEnTabla(lista);
    }

    // ── MÉTODOS EXPLÍCITOS LLAMADOS POR LA VISTA (Adiós al ActionPerformed) ──

    public void onAgregarOS() {
        String cod = vos.getCodigoObraSocial();
        String nom = vos.getNombreObraSocial();
        double ara = vos.getArancel();

        if (cod.isEmpty() || nom.isEmpty() || ara < 0) {
            vos.mostrarMensaje("Todos los campos son obligatorios o inválidos.");
            return;
        }

        ObraSocial nueva = new ObraSocial(cod, nom, ara);
        if (obraSocialDAO.agregarObraSocial(nueva)) {
            vos.mostrarMensaje("Obra Social agregada con éxito.");
            vos.limpiarCampos();
            cargarObrasSocialesEnTabla();
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
            if(obraSocialDAO.eliminarObraSocial(selEliminar.getCodigo())) {
                 vos.mostrarMensaje("Obra social eliminada.");
                 cargarObrasSocialesEnTabla();
            } else {
                 vos.mostrarMensaje("No se pudo eliminar la obra social.");
            }
        }
    }

    public void onCambiarArancel() {
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
                    cargarObrasSocialesEnTabla();
                } else {
                    vos.mostrarMensaje("Fallo al actualizar el arancel en la base de datos.");
                }
            } catch (NumberFormatException ex) {
                vos.mostrarMensaje("Error: El valor ingresado no es un número válido.");
            }
        }
    }

    public void onBuscarOS() {
        String filtro = vos.getTextoBusqueda();
        ArrayList<ObraSocial> filtradas;

        if (filtro.isEmpty()) {
            filtradas = obraSocialDAO.listarObrasSociales();
        } else {
            filtradas = obraSocialDAO.buscarPorCodigoONombre(filtro);
        }
        vos.cargarObrasSocialesEnTabla(filtradas);
    }

    public void onVolver() {
        // Le pasamos la pelota al taxista (Router)
        router.irAInicio();
    }

    public void onSeleccionarOS() {
        // En este módulo, la selección solo habilita botones visualmente
        // La vista (VistaObraSocial) ya maneja esto, así que aquí no hace falta lógica extra.
    }
}