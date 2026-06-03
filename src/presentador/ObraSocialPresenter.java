package presentador;
/**
 *
 * @author luciano
 */
import dao.ObraSocialDAO;
import modelo.ObraSocial;
import vista.IVistaObraSocial;
import vista.IVistaPrincipal;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ObraSocialPresenter implements ActionListener {

    private IVistaObraSocial vos;
    private IVistaPrincipal vp;
    private ObraSocialDAO obraSocialDAO;

    public ObraSocialPresenter(IVistaObraSocial vos, IVistaPrincipal vp, ObraSocialDAO obraSocialDAO) {
        this.vos = vos;
        this.vp = vp;
        this.obraSocialDAO = obraSocialDAO;
        this.vos.setControlador(this);
    }

    public void iniciar() {
        vos.limpiarCampos();
        cargarObrasSocialesEnTabla();
        vp.activarModoInmersion();
        vp.mostrarSeccion("obras_sociales");
    }

    private void cargarObrasSocialesEnTabla() {
        ArrayList<ObraSocial> lista = obraSocialDAO.listarObrasSociales();
        vos.cargarObrasSocialesEnTabla(lista);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();
        System.out.println("Boton de OS presionado, el comando es: " + comando); 

        switch (comando) {
            case IVistaObraSocial.BTN_AGREGAR_OS:
                agregarObraSocial();
                break;
            case IVistaObraSocial.BTN_ELIMINAR_OS:
                eliminarObraSocial();
                break;
            case IVistaObraSocial.BTN_MODIFICAR_ARANCEL_OS:
                modificarArancel();
                break;
            case IVistaObraSocial.BTN_VOLVER_OS:
                vp.desactivarModoInmersion();
                vp.volverInicio();
                break;
            case "BUSCAR_OS":
                actualizarBusqueda();
                break;
        }
    }

    private void agregarObraSocial() {
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

    private void eliminarObraSocial() {
        ObraSocial selEliminar = vos.getObraSocialSeleccionada();
        
        if (selEliminar == null) {
             vos.mostrarMensaje("Por favor, seleccione una obra social de la tabla.");
             return;
        }
        
        int confirm = vos.confirmarAccion("¿Eliminar la obra social " + selEliminar.getNombre() + "?", "Confirmar");
        if (confirm == 0) { // 0 = YES_OPTION
            if(obraSocialDAO.eliminarObraSocial(selEliminar.getCodigo())) {
                 vos.mostrarMensaje("Obra social eliminada.");
                 cargarObrasSocialesEnTabla();
            } else {
                 vos.mostrarMensaje("No se pudo eliminar la obra social.");
            }
        }
    }

    private void modificarArancel() {
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

    private void actualizarBusqueda() {
        String filtro = vos.getTextoBusqueda();
        ArrayList<ObraSocial> filtradas;

        if (filtro.isEmpty()) {
            filtradas = obraSocialDAO.listarObrasSociales();
        } else {
            filtradas = obraSocialDAO.buscarPorCodigoONombre(filtro);
        }
        vos.cargarObrasSocialesEnTabla(filtradas);
    }
}