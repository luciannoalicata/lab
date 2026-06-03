package presentador;

import dao.DeterminacionDAO;
import java.util.Date;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import modelo.*;
import vista.*;
import dao.*;
import java.util.List;
import javax.swing.JDialog;
import java.awt.Frame;
import javax.swing.JPanel;

public class Controlador implements ActionListener {

    private final Conexion con = new Conexion();
    private IVistaLogin vl;
    private IVistaPrincipal vp;
    private IVistaAjustes va;
    private IVistaNBU vnbu;
    private IVistaAuditoria vauditoria;
    private IVistaPaciente vpac = null;
    private IVistaAnalisis vla;
    private IVistaMedicos vm;
    private IVistaObraSocial vos;
    private IVistaDeterminaciones vd = null;
    private IVistaCargarResultados vcr = null;
    private IVistaHistorialAnalisis vha = null;
    private IVistaGestionUsuarios vgu = null;
    private IVistaVerDetalleAnalisis vvda = null;

    private final UsuarioDAO usuarioDAO;
    private final AuditoriaDAO auditoriaDAO;
    private Usuario usuarioLogueado;
    private final ConfiguracionDAO configDAO;
    private String nombreUsuarioLogueado = "";
    private final PacienteDAO pacienteDAO;
    private final MedicoDAO medicoDAO;
    private final ObraSocialDAO obraSocialDAO;
    private final DeterminacionDAO determinacionDAO;
    private final AnalisisDAO analisisDAO;
    private final ResultadoAnalisisDAO resultadoDAO;
    private Paciente pacienteActual;

    private final List<Determinacion> determinacionesSeleccionadas = new ArrayList<>();
    private List<Determinacion> listaVisualDeterminaciones = new ArrayList<>();
    private JDialog dialogoEspera;
    
   
    //refactor
    private MedicoPresenter medicoPresenter;
    private ObraSocialPresenter osPresenter;
    private AuditoriaPresenter auditoriaPresenter;
    private UsuarioPresenter usuarioPresenter;

    public Controlador() {
        con.getConnection();
        usuarioDAO = new UsuarioDAO(con);
        auditoriaDAO = new AuditoriaDAO(con);
        configDAO = new ConfiguracionDAO(con);
        pacienteDAO = new PacienteDAO(con);
        medicoDAO = new MedicoDAO(con);
        obraSocialDAO = new ObraSocialDAO(con);
        determinacionDAO = new DeterminacionDAO(con);
        analisisDAO = new AnalisisDAO(con);
        resultadoDAO = new ResultadoAnalisisDAO(con);

    }

    // Cambia tu método ejecutar en el Controlador
    public void ejecutar() {
        if (vl == null) {
            vl = new VistaLogin();
            vl.setControlador(this); // Solo se agrega el listener la PRIMERA vez
        }
        vl.ejecutar();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        String comando = e.getActionCommand();

        switch (comando) {

            //VISTA LOGIN
            case IVistaLogin.BTN_INGRESAR:
                String user = vl.getUsuario();
                String pass = vl.getClave();

                this.usuarioLogueado = usuarioDAO.login(user, pass);

                if (this.usuarioLogueado != null) {
                    this.nombreUsuarioLogueado = this.usuarioLogueado.getUsername();
                    ((JDialog) vl).dispose();

                    if (vp == null) {
                        vp = new VistaPrincipal();
                        vp.setControlador(this);
                    }

                    // ============================================================
                    // REGISTRO DE AUDITORÍA: LOGIN EXITOSO
                    // ============================================================
                    auditoriaDAO.registrar(
                            usuarioLogueado,
                            "LOGIN",
                            "usuario",
                            usuarioLogueado.getIdUsuario(),
                            null,
                            "Sesión iniciada",
                            "El usuario " + nombreUsuarioLogueado + " ha ingresado al sistema."
                    );
                    // ============================================================

                    aplicarRestriccionesSegunRol();
                    vp.ejecutar();
                } else {
                    vl.mostrarMensaje("Usuario o Contraseña incorrectos.");
                }
                break;

            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                
            case IVistaPrincipal.BTN_PACIENTES:
                // 1. Verificamos si el panel ya existe (Caché) para ahorrar memoria
                if (this.vpac == null) {
                    this.vpac = new VistaPaciente();
                    vpac.setControlador(this);
                    vp.registrarPanel((JPanel) this.vpac, "pacientes");
                    limpiarFocoYPantalla();
                }

                // 2. Limpiamos campos y actualizamos los datos de la tabla
                vpac.limpiarCampos();
                cargarPacientesEnTabla();

                // 3. Activamos el modo inmersión (pantalla completa) y mostramos
                vp.activarModoInmersion();
                vp.mostrarSeccion("pacientes");
                break;

            case IVistaPrincipal.BTN_ANALISIS:
                // 1. Verificamos si el panel ya existe (Caché de memoria)
                if (this.vla == null) {
                    this.vla = new VistaAnalisis(); // Sin parámetros
                    vla.setControlador(this);
                    vp.registrarPanel((JPanel) this.vla, "analisis");
                    limpiarFocoYPantalla();

                }

                ArrayList<Analisis> listaGlobal = analisisDAO.buscarAnalisisGlobal("");
                System.out.println("DEBUG VistaAnalisis: registros encontrados = " + listaGlobal.size());
                if (!listaGlobal.isEmpty()) {
                    System.out.println("DEBUG primer analisis: id=" + listaGlobal.get(0).getIdAnalisis()
                            + " | nombre=" + listaGlobal.get(0).getPacienteNombreCompleto());
                }

                // 2. Cargamos los datos actualizados a la tabla
                vla.cargarAnalisisEnTabla(listaGlobal);

                // 3. Activamos el modo inmersión (pantalla completa) y mostramos
                vp.activarModoInmersion();
                vp.mostrarSeccion("analisis");
                break;

            case IVistaAnalisis.BTN_VER_DETALLES:
                Analisis selDetalle = vla.getAnalisisSeleccionado();
                if (selDetalle != null) {
                    // Llamamos al nuevo método unificado
                    this.abrirDetalleAnalisis(selDetalle.getIdAnalisis());
                }
                break;

            case IVistaAnalisis.BTN_VOLVER_VLA:
                // Salimos del modo inmersión para que reaparezcan los menús
                vp.desactivarModoInmersion();
                vp.volverInicio();
                limpiarFocoYPantalla();
                break;

            case IVistaAnalisis.BTN_IMPRIMIR_ANALISIS:
                Analisis selPrint = vla.getAnalisisSeleccionado();
                if (selPrint != null) {
                    // Cargar el análisis completo para obtener su fecha real
                    Analisis analisisCompleto = analisisDAO.buscarPorId(selPrint.getIdAnalisis());
                    if (analisisCompleto != null) {
                        // Cargar paciente para generarInforme
                        pacienteActual = pacienteDAO.buscarPorId(analisisCompleto.getIdPaciente());
                        generarInforme(analisisCompleto.getIdAnalisis(), analisisCompleto.getFecha());

                        boolean okUpdate = analisisDAO.cambiarEstadoGenerado(analisisCompleto.getIdAnalisis());
                        if (okUpdate) {
                            ArrayList<Analisis> listaActualizada
                                    = analisisDAO.buscarAnalisisGlobal(vla.getTextoBusqueda());
                            vla.cargarAnalisisEnTabla(listaActualizada);

                            auditoriaDAO.registrar(usuarioLogueado, "IMPRIMIR", "analisis",
                                    analisisCompleto.getIdAnalisis(), null, "Informe generado",
                                    "Se generó informe desde Vista Global. ID: " + analisisCompleto.getIdAnalisis());
                        }
                    }
                }
                break;

            case IVistaPrincipal.BTN_MEDICOS:
                if (this.vm == null) {
                    this.vm = new VistaMedicos();
                    
                    // ── ¡ADIÓS SWING! Pasamos la interfaz limpia ──
                    vp.registrarPanel(this.vm, "medicos"); 
                    
                    this.medicoPresenter = new MedicoPresenter(this.vm, this.vp, this.medicoDAO);
                }
                
                this.medicoPresenter.iniciar();
                break;

            case IVistaPrincipal.BTN_OBRAS_SOCIALES: 
                if (this.vos == null) {
                    this.vos = new VistaObraSocial(); 
                    
                    // ── ¡AQUÍ BORRAMOS EL CASTEO! Pasamos this.vos puro ──
                    vp.registrarPanel(this.vos, "obras_sociales");
                    
                    // CREAMOS EL PRESENTADOR AQUÍ (Una sola vez)
                    this.osPresenter = new ObraSocialPresenter(this.vos, this.vp, this.obraSocialDAO);
                }

                // INICIAMOS
                this.osPresenter.iniciar();
                break;

            case IVistaPrincipal.BTN_AJUSTES:
                if (va  == null) {
                    va  = new VistaAjustes((Frame) vp, true);
                }
                va.setControlador(this);
                va.setUsuarioActual(this.nombreUsuarioLogueado);
                va.limpiarCampos();
                cargarDatosLaboratorio();

                va.ejecutar();
                break;

            case IVistaPrincipal.BTN_CERRAR_SESION:
                if (vp != null) {
                    String rutaDestino = configDAO.getValor("ruta_backup");

                    if (rutaDestino != null && !rutaDestino.isEmpty()) {
                        mostrarAvisoBackup(true);

                        new Thread(() -> {
                            // Registramos el tiempo de inicio
                            long tiempoInicio = System.currentTimeMillis();

                            // Ejecutamos el backup real
                            boolean backupOk = BackupService.crearBackup(rutaDestino);

                            // Calculamos cuánto tiempo pasó
                            long tiempoTranscurrido = System.currentTimeMillis() - tiempoInicio;
                            long esperaMinima = 1500; // 1.5 segundos para que sea legible

                            // Si fue muy rápido, esperamos lo que falte
                            if (tiempoTranscurrido < esperaMinima) {
                                try {
                                    Thread.sleep(esperaMinima - tiempoTranscurrido);
                                } catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                }
                            }

                            java.awt.EventQueue.invokeLater(() -> {
                                mostrarAvisoBackup(false);

                                if (!backupOk) {
                                    JOptionPane.showMessageDialog(null, "Error al generar la copia de seguridad.");
                                }

                                // Continuar con el flujo de cierre
                                auditoriaDAO.registrar(usuarioLogueado, "LOGIN", "usuario",
                                        usuarioLogueado.getIdUsuario(), "Sesión activa", "Sesión cerrada",
                                        "El usuario salió. Backup finalizado.");

                                ((java.awt.Window) vp).dispose();
                                this.usuarioLogueado = null;
                                if (vl != null) {
                                    vl.limpiarCampos();
                                }
                                this.ejecutar();
                            });
                        }).start();

                    } else {
                        ((java.awt.Window) vp).dispose();
                        this.usuarioLogueado = null;
                        this.ejecutar();
                    }
                }
                break;

            case IVistaPrincipal.BTN_GESTION_USUARIOS:
                if (this.vgu == null) {
                    this.vgu = new VistaGestionUsuarios();
                    
                    // Interfaz pura, sin JPanel
                    vp.registrarPanel(this.vgu, "usuarios");
                    
                    // Instanciamos inyectando el usuarioLogueado que ya tenías en el Controlador Dios
                    this.usuarioPresenter = new UsuarioPresenter(this.vgu, this.vp, this.usuarioDAO, this.usuarioLogueado);
                }

                // Iniciamos
                this.usuarioPresenter.iniciar();
                break;

            case IVistaPrincipal.BTN_NBU:
                // 1. Verificamos si el panel ya existe (Caché de memoria)
                if (this.vnbu == null) {
                    this.vnbu = new VistaNBU(); // Instanciamos como JPanel
                    vnbu.setControlador(this);
                    vp.registrarPanel((JPanel) this.vnbu, "nbu");
                    limpiarFocoYPantalla();

                }

                // 2. Cargamos los datos frescos
                List<Determinacion> todosNBU = determinacionDAO.listarTodo();
                vnbu.cargarDeterminaciones(todosNBU);

                // 3. Activamos el modo inmersión (pantalla completa) y mostramos
                vp.activarModoInmersion();
                vp.mostrarSeccion("nbu");
                break;

            case IVistaPrincipal.BTN_AUDITORIA: 
                if (this.vauditoria == null) {
                    this.vauditoria = new VistaAuditoria(); 
                    
                    // Pasamos el objeto limpio, sin cast a JPanel
                    vp.registrarPanel(this.vauditoria, "auditoria");
                    
                    // Creamos el presentador con sus dos DAOs correspondientes
                    this.auditoriaPresenter = new AuditoriaPresenter(this.vauditoria, this.vp, this.auditoriaDAO, this.usuarioDAO);
                }

                // Iniciamos la pantalla
                this.auditoriaPresenter.iniciar();
                break;

          

            //VISTA DE AJUSTES
            case IVistaAjustes.BTN_ACTUALIZAR_CLAVE:

                String claveActual = va.getClaveActual();
                String nuevaClave = va.getNuevaClave();
                String repetirClave = va.getRepetirNuevaClave();

                if (claveActual.isEmpty() || nuevaClave.isEmpty() || repetirClave.isEmpty()) {
                    va.mostrarMensaje("Todos los campos son obligatorios");
                    return;
                }

                if (!nuevaClave.equals(repetirClave)) {
                    va.mostrarMensaje("La nueva clave no coincide");
                    return;
                }

                if (!usuarioDAO.validarClave(nombreUsuarioLogueado, claveActual)) {
                    va.mostrarMensaje("La clave actual es incorrecta");
                    return;
                }

                if (usuarioDAO.actualizarClave(nombreUsuarioLogueado, nuevaClave)) {
                    va.mostrarMensaje("Clave actualizada correctamente");
                    va.limpiarCampos();
                } else {
                    va.mostrarMensaje("Error al actualizar la clave");
                }

                break;

            case IVistaAjustes.BTN_ACTUALIZAR_DATOS:
                guardarDatosLaboratorio(); // Método nuevo
                break;

            case IVistaAjustes.BTN_GUARDAR_CONFIGURACION:
                guardarConfiguracionImpresion();
                break;

            case IVistaAjustes.BTN_GUARDAR_UB:
                if (usuarioLogueado.getRol().equals("ADMIN")) {
                    // 1. Capturamos el valor que existe actualmente en la DB antes de cambiarlo
                    String valorAnterior = configDAO.getValor("valor_ub");
                    if (valorAnterior == null) {
                        valorAnterior = "0"; // Seguridad por si es la primera vez
                    }
                    // 2. Obtenemos el nuevo valor de la vista
                    String nuevoValor = va.getValorUB();

                    // 3. Guardamos el nuevo valor en la base de datos
                    configDAO.guardar("valor_ub", nuevoValor);

                    va.mostrarMensaje("Valor de Unidad Bioquímica actualizado.");

                    // 4. Registrar en Auditoría enviando la variable 'valorAnterior'
                    auditoriaDAO.registrar(
                            usuarioLogueado,
                            "EDITAR", // <--- Ahora sí coincide con el ENUM
                            "configuracion",
                            0,
                            valorAnterior,
                            nuevoValor,
                            "Cambio de arancel global de UB: de " + valorAnterior + " a " + nuevoValor
                    );
                } else {
                    va.mostrarMensaje("Acceso denegado: Solo administradores pueden cambiar aranceles.");
                }
                break;

            case IVistaNBU.BTN_GUARDAR_CAMBIOS:
                guardarCambiosPendientesHijos(); // Llama a la cajita de arriba
                vnbu.mostrarMensaje("Cambios guardados correctamente.");
                break;

            case IVistaNBU.BTN_SALIR: // o case "BTN_SALIR": (dependiendo de cómo lo tengas)
                vnbu.detenerEdicionTabla(); // Primero cerramos cualquier celda que el bioquímico haya dejado a medio escribir
                
                // Mostramos el cartel de 3 opciones (Sí, No, Cancelar)
                int confirmacion = javax.swing.JOptionPane.showConfirmDialog(
                        null,
                        "¿Desea guardar los últimos cambios realizados antes de salir?",
                        "Confirmar salida",
                        javax.swing.JOptionPane.YES_NO_CANCEL_OPTION,
                        javax.swing.JOptionPane.QUESTION_MESSAGE
                );

                if (confirmacion == javax.swing.JOptionPane.YES_OPTION) {
                    // Dijo que SÍ: Guardamos y luego salimos
                    guardarCambiosPendientesHijos(); 
                    
                    vp.desactivarModoInmersion();
                vp.volverInicio();
                limpiarFocoYPantalla();
                    
                } else if (confirmacion == javax.swing.JOptionPane.NO_OPTION) {
                    // Dijo que NO: Salimos directamente sin guardar nada
                    
                    vp.desactivarModoInmersion();
                vp.volverInicio();
                limpiarFocoYPantalla();
                    
                }
                // Si presionó Cancelar o la "X" de la ventanita, no entra en ningún if. 
                // Se queda en la pantalla actual para seguir trabajando.
                break;

            //  PARA LA VISTA DE PACIENTES
            case IVistaPaciente.BTN_VER_HISTORIAL:
                Paciente seleccionadoEnTabla = vpac.getPacienteSeleccionado();

                if (seleccionadoEnTabla != null) {
                    // 1. Buscamos los datos completos del paciente
                    Paciente pacHistorial = pacienteDAO.buscarPorId(seleccionadoEnTabla.getIdPaciente());

                    if (pacHistorial != null) {
                        pacienteActual = pacHistorial;

                        // 2. Verificamos la caché del panel del historial
                        if (this.vha == null) {
                            this.vha = new VistaHistorialAnalisis(); // Inicializamos sin Frame ni modal
                            vha.setControlador(this);
                            vp.registrarPanel((JPanel) this.vha, "historial_analisis");
                        }

                        // Lógica de permisos de LECTOR (Se mantiene intacta)
                        if (usuarioLogueado.getRol().equals("LECTOR")) {
                            vha.habilitarBotonVerDetalles(true);
                            vha.habilitarBotonImprimir(true);
                        }

                        // 3. Configurar datos en la interfaz del historial
                        String nombreCompletoo = pacHistorial.getApellido() + " " + pacHistorial.getNombre();
                        vha.setNombrePaciente(nombreCompletoo);

                        // 4. Buscar historial en DB y cargar tabla
                        ArrayList<Analisis> historial = analisisDAO.listarPorPaciente(pacienteActual.getIdPaciente());
                        vha.cargarHistorial(historial);

                        // Precargar la fecha del análisis más reciente
                        if (historial != null && !historial.isEmpty()) {
                            vha.setFechaSeleccionada(historial.get(0).getFecha());
                        }

                        // 5. Activamos el modo inmersión y mostramos el panel
                        vp.activarModoInmersion();
                        vp.mostrarSeccion("historial_analisis");

                    } else {
                        vpac.mostrarMensaje("Error: No se pudieron recuperar los datos del paciente desde la base de datos.");
                    }
                } else {
                    vpac.mostrarMensaje("Debe seleccionar un paciente de la tabla");
                }
                break;

            case "ver_detalle_analisis":
                // Tienes que asegurarte de tener el ID del análisis que quieres abrir.
                // Por ejemplo, si lo sacas de alguna tabla:
                // int id = vista.getIdSeleccionado();

                // Y simplemente llamas al método que armamos recién, que hace toda la magia:
                // this.abrirDetalleAnalisis(id);
                break;

            case IVistaPaciente.BTN_GUARDAR_PACIENTE:
                guardarPaciente();
                break;

            case IVistaPaciente.BTN_EDITAR_PACIENTE:
                editarPaciente();
                break;

            case IVistaPaciente.BTN_BUSCAR_PACIENTE:
                buscarPacienteAutomatico();
                break;

            case IVistaPaciente.BTN_VOLVER_VPAC:
                // Salimos del modo inmersión para que reaparezcan los menús
                vp.desactivarModoInmersion();
                vp.volverInicio();
                limpiarFocoYPantalla();
                break;

            case IVistaPaciente.BTN_CARGAR_RESULTADOS: // o BTN_NUEVO_ANALISIS
                Paciente seleccionado = vpac.getPacienteSeleccionado();

                if (seleccionado == null) {
                    vpac.mostrarMensaje("Debe seleccionar un paciente");
                    return;
                }

                pacienteActual = pacienteDAO.buscarPorId(seleccionado.getIdPaciente());

                // Limpiamos determinaciones anteriores
                determinacionesSeleccionadas.clear();

                // Abrimos vista de determinaciones
                vd = new VistaDeterminaciones((Frame) vp, true);
                vd.setControlador(this);
                vd.ejecutar();
                break;

            case IVistaDeterminaciones.BTN_AGREGAR_DETERMINACION:
                String codBusqueda = vd.getDeterminacion().trim();
                if (codBusqueda.isEmpty()) {
                    vd.mostrarMensaje("Ingrese un código o nombre");
                    return;
                }

                boolean esSufijo = codBusqueda.matches("\\d{3}");
                Determinacion detExacta = esSufijo ? null : determinacionDAO.buscarPorCodigo(codBusqueda);

                if (detExacta != null) {
                    List<Determinacion> componentesHijos = determinacionDAO.obtenerComponentes(detExacta.getCodigo());

                    if (!componentesHijos.isEmpty()) {
                        int agregados = 0;
                        for (Determinacion hijo : componentesHijos) {
                            if (determinacionesSeleccionadas.stream().noneMatch(x -> x.getCodigo().equals(hijo.getCodigo()))) {
                                determinacionesSeleccionadas.add(hijo);
                                agregados++;
                            }
                        }
                        if (agregados == 0) {
                            vd.mostrarMensaje("Todos los componentes de esta práctica ya fueron agregados.");
                            vd.limpiarCampos();
                        } else {
                            refrescarTablaSeleccion(); // REFRESCAMOS AL AGREGAR
                        }
                    } else {
                        boolean yaExiste = determinacionesSeleccionadas.stream().anyMatch(x -> x.getCodigo().equals(detExacta.getCodigo()));
                        if (yaExiste) {
                            vd.mostrarMensaje("Esta determinación ya ha sido agregada.");
                        } else {
                            determinacionesSeleccionadas.add(detExacta);
                            refrescarTablaSeleccion(); // REFRESCAMOS AL AGREGAR
                        }
                    }

                } else {
                    List<Determinacion> coincidencias = determinacionDAO.buscar(codBusqueda);

                    if (coincidencias.isEmpty()) {
                        vd.mostrarMensaje("No se encontró ninguna práctica con: [" + codBusqueda + "]");
                    } else if (coincidencias.size() == 1) {
                        Determinacion det = coincidencias.get(0);
                        List<Determinacion> hijos = determinacionDAO.obtenerComponentes(det.getCodigo());

                        if (!hijos.isEmpty()) {
                            int agregados = 0;
                            for (Determinacion hijo : hijos) {
                                if (determinacionesSeleccionadas.stream().noneMatch(x -> x.getCodigo().equals(hijo.getCodigo()))) {
                                    determinacionesSeleccionadas.add(hijo);
                                    agregados++;
                                }
                            }
                            if (agregados == 0) {
                                vd.mostrarMensaje("Todos los componentes ya fueron agregados.");
                            } else {
                                refrescarTablaSeleccion(); // REFRESCAMOS AL AGREGAR
                            }
                        } else {
                            boolean yaExiste = determinacionesSeleccionadas.stream().anyMatch(x -> x.getCodigo().equals(det.getCodigo()));
                            if (yaExiste) {
                                vd.mostrarMensaje("Esta determinación ya ha sido agregada.");
                            } else {
                                determinacionesSeleccionadas.add(det);
                                refrescarTablaSeleccion(); // REFRESCAMOS AL AGREGAR
                            }
                        }
                    } else {
                        vd.mostrarSugerencias(coincidencias);
                    }
                }
                break;

            case IVistaDeterminaciones.BTN_ELIMINAR:
                int fila = vd.getFilaSeleccionada();

                // Validamos contra la lista VISUAL, que es la que ve el usuario en la tabla
                if (fila >= 0 && fila < listaVisualDeterminaciones.size()) {
                    Determinacion detAEliminar = listaVisualDeterminaciones.get(fila);
                    String nombreFila = detAEliminar.getNombre() != null ? detAEliminar.getNombre() : "";

                    if (nombreFila.startsWith("---") && nombreFila.endsWith("---")) {
                        vd.mostrarMensaje("No puede eliminar un separador visual.\nSi desea quitar la práctica, elimine sus componentes médicos.");
                        return;
                    }

                    // Eliminamos de la lista ORIGINAL buscando por su código único
                    determinacionesSeleccionadas.removeIf(d -> d.getCodigo().equals(detAEliminar.getCodigo()));
                    
                    // Reordenamos y re-dibujamos todo de inmediato
                    refrescarTablaSeleccion();
                } else {
                    vd.mostrarMensaje("Por favor, seleccione una fila válida para eliminar.");
                }
                break;

            case IVistaDeterminaciones.BTN_CONTINUAR:
                if (determinacionesSeleccionadas.isEmpty()) {
                    vd.mostrarMensaje("Debe agregar al menos una determinación.");
                    return;
                }

                ((java.awt.Window) vd).dispose(); 

                if (this.vcr == null) {
                    this.vcr = new VistaCargarResultados();
                    vcr.setControlador(this);
                    vp.registrarPanel((JPanel) this.vcr, "cargar_resultados");
                }

                vcr.setNombrePaciente(pacienteActual.getApellido() + " " + pacienteActual.getNombre());
                
                // ── MAGIA ANTI-FANTASMAS: Limpiamos las cajas de texto ──
                // Si tu objeto pacienteActual tiene guardada su obra social, puedes ponerla aquí
                // Ej: vcr.setObraSocial(pacienteActual.getObraSocial() != null ? pacienteActual.getObraSocial() : "");
                // Por ahora, simplemente las vaciamos para que no queden datos del paciente anterior:
                vcr.setObraSocial(""); 
                vcr.setMedicoSolicitante(""); 
                
                // La listaVisualDeterminaciones ya está perfectamente ordenada y con títulos listos.
                vcr.cargarDeterminaciones(this.listaVisualDeterminaciones); 

                vp.activarModoInmersion();
                vp.mostrarSeccion("cargar_resultados");
                break;

            // PARA LA VISTA DE CARGAR RESULTADOS
            case IVistaCargarResultados.BTN_GUARDAR_RESULTADOS:
                guardarResultados();
                break;

            case IVistaCargarResultados.BTN_CERRAR:
                // Retornamos a la pantalla de pacientes sin perder inmersión
                vp.mostrarSeccion("pacientes");
                break;

            // PARA LA VISTA HISTORIAL ANALISIS
            case IVistaHistorialAnalisis.BTN_GENERAR_INFORME:
                // 1. Obtenemos los datos desde la vista Historial
                int idDesdeHistorial = vha.getAnalisisSeleccionadoId();
                Date fechaDesdeHistorial = vha.getFechaSeleccionada();

                if (idDesdeHistorial != -1) {
                    // 2. Intentamos generar el informe
                    generarInforme(idDesdeHistorial, fechaDesdeHistorial);

                    // 3. Actualizamos el estado en la base de datos
                    boolean actualizado = analisisDAO.cambiarEstadoGenerado(idDesdeHistorial);

                    if (actualizado) {
                        // 4. Refrescamos la tabla del historial para que se pinte de VERDE
                        ArrayList<Analisis> listaActualizada = analisisDAO.listarPorPaciente(pacienteActual.getIdPaciente());
                        vha.cargarHistorial(listaActualizada);

                        // AUDITORÍA
                        auditoriaDAO.registrar(usuarioLogueado, "IMPRIMIR", "analisis", idDesdeHistorial,
                                "Estado: COMPLETO", "Estado: GENERADO", "Se generó informe desde Historial");
                    }
                } else {
                    vha.mostrarMensaje("Debe seleccionar un análisis de la lista.");
                }
                break;

            case IVistaHistorialAnalisis.BTN_VER_DETALLES:
                int idAnalisisDetalle = vha.getAnalisisSeleccionadoId();
                if (idAnalisisDetalle != -1) {
                    // Llamamos al mismo método unificado
                    this.abrirDetalleAnalisis(idAnalisisDetalle);
                }
                break;

            case "eliminar_fila_detalle":
                int filaSel = vvda.getGrilla().getSelectedRow();
                if (filaSel != -1) {
                    int idResEliminar = vvda.getIdResultado(filaSel);

                    // 1. CAPTURAMOS LOS DATOS ANTES DE ELIMINAR
                    modelo.ResultadoAnalisis rEliminar = resultadoDAO.buscarPorId(idResEliminar);

                    if (rEliminar == null) {
                        vvda.mostrarMensaje("Error: No se pudo encontrar la determinación.");
                        return;
                    }

                    int idAnalisisAct = rEliminar.getIdAnalisis();

                    // 2. VERIFICAMOS CUÁNTOS RESULTADOS REALES QUEDAN
                    // Usamos listarIncluidosPorAnalisis porque solo trae resultados reales (ignora los títulos virtuales)
                    java.util.List<modelo.ResultadoAnalisis> resultadosRestantes = resultadoDAO.listarIncluidosPorAnalisis(idAnalisisAct);

                    // ── CASO A: ES LA ÚLTIMA FILA ──
                    if (resultadosRestantes.size() == 1) {
                        int confirmUltimo = JOptionPane.showConfirmDialog(null,
                                "Está a punto de eliminar la última determinación de este estudio.\n"
                                + "Si continúa, EL ANÁLISIS COMPLETO SERÁ ELIMINADO de la base de datos.\n"
                                + "¿Desea continuar?",
                                "Advertencia Crítica", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                        if (confirmUltimo == JOptionPane.YES_OPTION) {
                            String infoEliminada = "Prueba: " + rEliminar.getNombrePrueba() + " | Valor: " + rEliminar.getResultado();

                            // Borramos el resultado
                            resultadoDAO.eliminarResultado(idResEliminar);

                            // Borramos el análisis padre 
                            analisisDAO.eliminar(idAnalisisAct);

                            // Auditoría
                            auditoriaDAO.registrar(usuarioLogueado, "ELIMINAR", "analisis", idAnalisisAct, "Análisis completo eliminado al vaciarse: " + infoEliminada, "REGISTRO ELIMINADO", "Eliminación en cascada");

                            // ── NAVEGACIÓN: Nos quedamos en la lista de análisis ──
                            // ── NAVEGACIÓN: Nos devolvemos a la lista de análisis ──
                            // 1. Ocultamos la vista de detalles
                            ((javax.swing.JPanel) vvda).setVisible(false); 
                            
                            // 2. Refrescamos la tabla de la lista de análisis GLOBAL (el borrado ya no estará)
                            if (vla != null) {
                                // Buscamos todos los análisis frescos de la base de datos usando tu método global
                                java.util.ArrayList<modelo.Analisis> listaFresca = analisisDAO.buscarAnalisisGlobal("");
                                
                                // 🔴 IMPORTANTE: Cambia "cargarAnalisis" por el nombre del método que uses en tu 'vla' para llenar su tabla (ej: cargarTabla, setAnalisis, etc.)
                                vla.cargarAnalisisEnTabla(listaFresca); 
                                
                                // 4. Traemos al frente la vista del historial 
                                vla.ejecutar(); 
                            }
                            
                            vvda.mostrarMensaje("Análisis eliminado por completo al quedarse sin determinaciones.");
                        }

                    // ── CASO B: QUEDAN MÁS FILAS (LÓGICA NORMAL) ──
                    } else {
                        int confirm = JOptionPane.showConfirmDialog(null,
                                "¿Está seguro de eliminar la determinación: " + rEliminar.getNombrePrueba() + "?\n"
                                + "El precio del estudio se recalculará automáticamente.",
                                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);

                        if (confirm == JOptionPane.YES_OPTION) {
                            // --- INICIO LÓGICA DE RECALCULO DE PRECIO ---
                            modelo.Determinacion detOriginal = determinacionDAO.buscarPorCodigo(rEliminar.getCodigo());
                            modelo.Analisis analisisActual = analisisDAO.buscarPorId(idAnalisisAct);

                            String valorUBConfig = configDAO.getValor("valor_ub");
                            double factorDinero = (valorUBConfig != null) ? Double.parseDouble(valorUBConfig) : 1600.0;

                            if (detOriginal != null && analisisActual != null) {
                                double montoARestar = detOriginal.getUb() * factorDinero;
                                double nuevoPrecio = analisisActual.getPrecio() - montoARestar;
                                if (nuevoPrecio < 0) {
                                    nuevoPrecio = 0;
                                }

                                analisisDAO.actualizarPrecio(idAnalisisAct, nuevoPrecio);
                            }
                            // --- FIN LÓGICA DE RECALCULO ---

                            String infoEliminada = "Prueba: " + rEliminar.getNombrePrueba() + " | Valor: " + rEliminar.getResultado();

                            if (resultadoDAO.eliminarResultado(idResEliminar)) {
                                auditoriaDAO.registrar(usuarioLogueado, "ELIMINAR", "resultado", idResEliminar, infoEliminada, "REGISTRO ELIMINADO", "Precio actualizado por eliminación");

                                // MAGIA: Refrescamos la vista intercalando los títulos virtuales
                                java.util.ArrayList<modelo.ResultadoAnalisis> listaBD = (java.util.ArrayList<modelo.ResultadoAnalisis>) resultadoDAO.listarPorAnalisis(idAnalisisAct);
                                java.util.ArrayList<modelo.ResultadoAnalisis> listaConTitulos = new java.util.ArrayList<>();
                                String ultimoPadre = "";

                                for (modelo.ResultadoAnalisis res : listaBD) {
                                    String codigoPadre = res.getCodigo().contains(".") ? res.getCodigo().substring(0, res.getCodigo().indexOf('.')) : res.getCodigo();

                                    if (!codigoPadre.equals(ultimoPadre)) {
                                        modelo.ResultadoAnalisis tituloVirtual = new modelo.ResultadoAnalisis();
                                        tituloVirtual.setIdResultado(-1);
                                        tituloVirtual.setCodigo("");

                                        modelo.Determinacion padreObjeto = determinacionDAO.buscarPorCodigo(codigoPadre);
                                        tituloVirtual.setNombrePrueba(padreObjeto != null ? padreObjeto.getNombre() : "ESTUDIO");

                                        listaConTitulos.add(tituloVirtual);
                                        ultimoPadre = codigoPadre;
                                    }
                                    listaConTitulos.add(res);
                                }

                                vvda.cargarResultadosDetalle(listaConTitulos);

                                if (vha != null) {
                                    this.listarAnalisisPaciente();
                                }
                                
                                vvda.mostrarMensaje("Fila eliminada y precio actualizado.");
                            }
                        }
                    }
                } else {
                    vvda.mostrarMensaje("Debe seleccionar una fila para eliminar.");
                }
                break;

            case IVistaVerDetalleAnalisis.BTN_EDITAR:
                vvda.detenerEdicionTabla();
                int idAnalisisActual = vvda.getIdAnalisis(); // ← antes era vha.getAnalisisSeleccionadoId()
                String nuevoMedico = vvda.getMedicoSolicitante();
                int filasDetalle = vvda.getCantidadFilas();
                boolean okRes = true;
                StringBuilder cambios = new StringBuilder();

                for (int i = 0; i < filasDetalle; i++) {
                    int idR = vvda.getIdResultado(i);
                    if (idR == -1) {
                        continue;
                    }
                    String valR = vvda.getResultadoEditado(i);

                    ResultadoAnalisis rViejo = resultadoDAO.buscarPorId(idR);
                    if (rViejo != null && !rViejo.getResultado().equals(valR)) {
                        cambios.append(rViejo.getNombrePrueba())
                                .append(": ").append(rViejo.getResultado())
                                .append(" -> ").append(valR).append("\n");
                    }
                    if (!resultadoDAO.actualizarResultado(idR, valR)) {
                        okRes = false;
                    }
                }

                boolean okMed = analisisDAO.actualizarMedico(idAnalisisActual, nuevoMedico);

                if (okRes && okMed) {
                    if (cambios.length() > 0) {
                        auditoriaDAO.registrar(usuarioLogueado, "EDITAR", "resultado", idAnalisisActual,
                                "Valores anteriores", cambios.toString(),
                                "Edición de resultados del análisis ID: " + idAnalisisActual);
                    }

                    // Refrescar vha si está abierta (flujo desde paciente)
                    if (vha != null && pacienteActual != null) {
                        vha.cargarHistorial(analisisDAO.listarPorPaciente(pacienteActual.getIdPaciente()));
                    }
                    // Refrescar vla si está abierta (flujo global)
                    if (vla != null) {
                        vla.cargarAnalisisEnTabla(analisisDAO.buscarAnalisisGlobal(vla.getTextoBusqueda()));
                    }

                    vvda.mostrarMensaje("Cambios guardados con éxito.");
                }
                break;

            case IVistaHistorialAnalisis.BTN_CERRAR:
                // El modo inmersión sigue activo, solo volvemos a la vista anterior (pacientes)
                vp.mostrarSeccion("pacientes");
                break;
            // PARA LA VISTA VER DETALLE
            case IVistaVerDetalleAnalisis.BTN_IMPRIMIR:
                int idDesdeDetalle = vvda.getIdAnalisis(); // ← desde vvda, no vha
                Date fechaDesdeDetalle = vvda.getFechaSeleccionada();

                if (idDesdeDetalle != -1) {
                    // Cargar paciente si viene desde flujo global (vha null)
                    if (pacienteActual == null) {
                        Analisis aTmp = analisisDAO.buscarPorId(idDesdeDetalle);
                        if (aTmp != null) {
                            pacienteActual = pacienteDAO.buscarPorId(aTmp.getIdPaciente());
                        }
                    }

                    generarInforme(idDesdeDetalle, fechaDesdeDetalle);

                    boolean okUpdate = analisisDAO.cambiarEstadoGenerado(idDesdeDetalle);
                    if (okUpdate) {
                        // Refrescar vha solo si está abierta (flujo desde paciente)
                        if (vha != null && pacienteActual != null) {
                            vha.cargarHistorial(analisisDAO.listarPorPaciente(pacienteActual.getIdPaciente()));
                        }
                        // Refrescar vla si está abierta (flujo global)
                        if (vla != null) {
                            vla.cargarAnalisisEnTabla(analisisDAO.buscarAnalisisGlobal(vla.getTextoBusqueda()));
                        }
                        auditoriaDAO.registrar(usuarioLogueado, "IMPRIMIR", "analisis", idDesdeDetalle,
                                null, "Informe generado",
                                "El usuario imprimió desde la vista de Detalles. ID: " + idDesdeDetalle);
                    }
                }
                break;

            case IVistaVerDetalleAnalisis.BTN_CERRAR:
                // Puedes enviarlo al historial general de analisis:
                vp.mostrarSeccion("analisis");
                // Opcional: si necesitas enviarlo al historial del paciente, 
                // usa vp.mostrarSeccion("historial_analisis");
                break;

            // VISTA DE MÉDICOS // 
           
          

            // ── NUEVOS COMANDOS DEL NBU (PANEL HIJOS) ──
            case "BTN_AGREGAR_HIJO":
                guardarCambiosPendientesHijos(); // Auto-guardado
                vnbu.detenerEdicionTabla(); // ──> ¡ESCUDO AQUÍ!
                agregarHijoNBU();
                break;

            case "BTN_QUITAR_HIJO":
                guardarCambiosPendientesHijos(); // Auto-guardado
                vnbu.detenerEdicionTabla(); // ──> ¡ESCUDO AQUÍ!
                quitarHijoNBU();
                break;

            case "BTN_SUBIR_HIJO":
                vnbu.detenerEdicionTabla(); // Escudo protector
                guardarCambiosPendientesHijos(); // Auto-guardado

                int filaActualSubir = vnbu.getIndiceHijoSeleccionado();
                // Si hay una fila seleccionada y NO es la primera de todas (porque la primera no puede subir más)
                if (filaActualSubir > 0) {
                    moverHijoNBU(-1); // Se va a la base de datos, actualiza y redibuja
                    vnbu.seleccionarHijoPorIndice(filaActualSubir - 1); // Magia UX: volvemos a marcarla instantáneamente
                }
                break;

            case "BTN_BAJAR_HIJO":
                vnbu.detenerEdicionTabla(); // Escudo protector
                guardarCambiosPendientesHijos(); // Auto-guardado

                int filaActualBajar = vnbu.getIndiceHijoSeleccionado();
                // Si hay una fila seleccionada y NO es la última
                if (filaActualBajar != -1 && filaActualBajar < vnbu.getCantidadFilas() - 1) {
                    moverHijoNBU(1); // Actualiza y redibuja
                    vnbu.seleccionarHijoPorIndice(filaActualBajar + 1); // Magia UX: volvemos a marcarla instantáneamente
                }
                break;

            case "BTN_SUBIR_PADRE":
                moverPadreNBU(-1); // -1 = Sube en la tabla
                break;

            case "BTN_BAJAR_PADRE":
                moverPadreNBU(1);  // 1 = Baja en la tabla
                break;
        }

    }

    /* ================== MÉTODOS PRIVADOS ================== */
    private void guardarPaciente() {
        String dni = vpac.getDni().trim();

        if (dni.isEmpty() || vpac.getNombre().isEmpty()) {
            vpac.mostrarMensaje("DNI y Nombre son obligatorios");
            return;
        }

        // VALIDACIÓN PREVIA: Evita la excepción por consola
        if (pacienteDAO.existeDNI(dni)) {
            vpac.mostrarMensaje("El paciente con DNI " + dni + " ya se encuentra registrado.");
            return;
        }

        Paciente p = new Paciente();
        p.setDni(dni);
        p.setNombre(vpac.getNombre());
        p.setApellido(vpac.getApellido());
        p.setEdad(vpac.getEdad());
        p.setDireccion(vpac.getDireccion());
        p.setLocalidad(vpac.getLocalidad());
        p.setNroAfiliado(vpac.getNumAfiliado());
        p.setObraSocial(vpac.getObraSocial());
        p.setSexo(vpac.getSexo());
        p.setCelular(vpac.getCelular());

        boolean ok = pacienteDAO.guardarPaciente(p);
        if (ok) {
            // AUDITORÍA: Registro de nuevo paciente
            auditoriaDAO.registrar(usuarioLogueado, "CREAR", "paciente", 0, null,
                    "DNI: " + p.getDni(), "Se registró un nuevo paciente: " + p.getApellido() + " " + p.getNombre());

            vpac.mostrarMensaje("Paciente guardado correctamente");
            vpac.limpiarCampos();
        } else {
            vpac.mostrarMensaje("Error técnico al intentar guardar el paciente");
        }
        cargarPacientesEnTabla();
    }

    private void cargarPacientesEnTabla() {
        if (vpac != null) {
            try {
                ArrayList<Paciente> pacientes = pacienteDAO.listarPacientes();
                vpac.cargarPacientesEnTabla(pacientes);
            } catch (Exception e) {
                System.out.println("ERROR AL LISTAR CLIENTES. " + e.getMessage());
            }
        }
    }

    private void cargarMedicosEnTabla() {
        if (vm != null) {
            try {
                ArrayList<Medico> medicos = medicoDAO.listarMedicos();
                vm.cargarMedicosEnTabla(medicos);
            } catch (Exception e) {
                System.out.println("ERROR AL LISTAR MÉDICOS. " + e.getMessage());
            }
        }
    }

    public void pacienteSeleccionado() {
        Paciente p = vpac.getPacienteSeleccionado();
        if (p == null) {
            return;
        }
        // Buscar el paciente completo en la DB
        Paciente completo = pacienteDAO.buscarPorId(p.getIdPaciente());

        if (completo != null) {
            vpac.cargarDatosPaciente(completo);
        }
    }

    private void editarPaciente() {
        Paciente seleccionado = vpac.getPacienteSeleccionado();
        if (seleccionado == null) {
            vpac.mostrarMensaje("Debe seleccionar un paciente");
            return;
        }

        // 1. Cargamos los datos actuales de la DB (incluye la versión actual)
        Paciente pViejo = pacienteDAO.buscarPorId(seleccionado.getIdPaciente());

        // Si pViejo es null, es que alguien lo borró recién
        if (pViejo == null) {
            vpac.mostrarMensaje("Error: El paciente ya no existe en la base de datos.");
            cargarPacientesEnTabla();
            return;
        }

        // 2. Comparamos cambios para la auditoría (tu lógica actual está perfecta aquí)
        StringBuilder cambiosViejos = new StringBuilder();
        StringBuilder cambiosNuevos = new StringBuilder();
        // ... (tus comparaciones de DNI, Apellido, etc.) ...

        // Función auxiliar interna para comparar (puedes ver la lógica abajo)
        // Comparamos cada campo vital:
        // DNI
        if (!pViejo.getDni().equals(vpac.getDni())) {
            cambiosViejos.append("DNI: ").append(pViejo.getDni()).append(" | ");
            cambiosNuevos.append("DNI: ").append(vpac.getDni()).append(" | ");
        }
        // APELLIDO
        if (!pViejo.getApellido().equalsIgnoreCase(vpac.getApellido())) {
            cambiosViejos.append("Apel: ").append(pViejo.getApellido()).append(" | ");
            cambiosNuevos.append("Apel: ").append(vpac.getApellido()).append(" | ");
        }
        // NOMBRE
        if (!pViejo.getNombre().equalsIgnoreCase(vpac.getNombre())) {
            cambiosViejos.append("Nom: ").append(pViejo.getNombre()).append(" | ");
            cambiosNuevos.append("Nom: ").append(vpac.getNombre()).append(" | ");
        }
        // CELULAR
        if (!pViejo.getCelular().equals(vpac.getCelular())) {
            cambiosViejos.append("Cel: ").append(pViejo.getCelular()).append(" | ");
            cambiosNuevos.append("Cel: ").append(vpac.getCelular()).append(" | ");
        }
        // OBRA SOCIAL
        if (!pViejo.getObraSocial().equals(vpac.getObraSocial())) {
            cambiosViejos.append("OS: ").append(pViejo.getObraSocial()).append(" | ");
            cambiosNuevos.append("OS: ").append(vpac.getObraSocial()).append(" | ");
        }
        // 3. Preparamos el objeto seleccionado con la versión que acabamos de leer
        seleccionado.setVersion(pViejo.getVersion()); // Aseguramos que tenga la versión fresca

        // 3. ACTUALIZAMOS EL OBJETO PARA EL DAO
        seleccionado.setDni(vpac.getDni());
        seleccionado.setNombre(vpac.getNombre());
        seleccionado.setApellido(vpac.getApellido());
        seleccionado.setEdad(vpac.getEdad());
        seleccionado.setDireccion(vpac.getDireccion());
        seleccionado.setLocalidad(vpac.getLocalidad());
        seleccionado.setNroAfiliado(vpac.getNumAfiliado());
        seleccionado.setObraSocial(vpac.getObraSocial());
        seleccionado.setSexo(vpac.getSexo());
        seleccionado.setCelular(vpac.getCelular());

        // 4. INTENTO DE GUARDADO
        boolean ok = pacienteDAO.actualizar(seleccionado);

        if (ok) {
            // ÉXITO: Nadie tocó el registro en el medio
            if (cambiosViejos.length() > 0) {
                auditoriaDAO.registrar(usuarioLogueado, "EDITAR", "paciente",
                        seleccionado.getIdPaciente(), cambiosViejos.toString(),
                        cambiosNuevos.toString(), "Edición exitosa");
            }
            vpac.mostrarMensaje("Paciente actualizado correctamente");
            vpac.limpiarCampos();
        } else {
            // ERROR DE CONCURRENCIA: La versión en DB ya es distinta a pViejo.getVersion()
            vpac.mostrarMensaje("CONCURRENCIA: Otro usuario modificó este paciente mientras usted lo editaba.\n"
                    + "Los datos se refrescarán. Por favor, intente de nuevo.");
        }

        cargarPacientesEnTabla(); // Refrescamos la tabla siempre
    }
//    private void editarPaciente() {
//        Paciente seleccionado = vpac.getPacienteSeleccionado();
//
//        if (seleccionado == null) {
//            vpac.mostrarMensaje("Debe seleccionar un paciente de la tabla");
//            return;
//        }
//
//        // 1. CAPTURAMOS EL ESTADO ANTERIOR DESDE LA DB
//        Paciente pViejo = pacienteDAO.buscarPorId(seleccionado.getIdPaciente());
//
//        // 2. PREPARAMOS LOS STRINGS PARA COMPARAR CAMPO POR CAMPO
//        StringBuilder cambiosViejos = new StringBuilder();
//        StringBuilder cambiosNuevos = new StringBuilder();
//
//        // Función auxiliar interna para comparar (puedes ver la lógica abajo)
//        // Comparamos cada campo vital:
//        // DNI
//        if (!pViejo.getDni().equals(vpac.getDni())) {
//            cambiosViejos.append("DNI: ").append(pViejo.getDni()).append(" | ");
//            cambiosNuevos.append("DNI: ").append(vpac.getDni()).append(" | ");
//        }
//        // APELLIDO
//        if (!pViejo.getApellido().equalsIgnoreCase(vpac.getApellido())) {
//            cambiosViejos.append("Apel: ").append(pViejo.getApellido()).append(" | ");
//            cambiosNuevos.append("Apel: ").append(vpac.getApellido()).append(" | ");
//        }
//        // NOMBRE
//        if (!pViejo.getNombre().equalsIgnoreCase(vpac.getNombre())) {
//            cambiosViejos.append("Nom: ").append(pViejo.getNombre()).append(" | ");
//            cambiosNuevos.append("Nom: ").append(vpac.getNombre()).append(" | ");
//        }
//        // CELULAR
//        if (!pViejo.getCelular().equals(vpac.getCelular())) {
//            cambiosViejos.append("Cel: ").append(pViejo.getCelular()).append(" | ");
//            cambiosNuevos.append("Cel: ").append(vpac.getCelular()).append(" | ");
//        }
//        // OBRA SOCIAL
//        if (!pViejo.getObraSocial().equals(vpac.getObraSocial())) {
//            cambiosViejos.append("OS: ").append(pViejo.getObraSocial()).append(" | ");
//            cambiosNuevos.append("OS: ").append(vpac.getObraSocial()).append(" | ");
//        }
//
//        // 3. ACTUALIZAMOS EL OBJETO PARA EL DAO
//        seleccionado.setDni(vpac.getDni());
//        seleccionado.setNombre(vpac.getNombre());
//        seleccionado.setApellido(vpac.getApellido());
//        seleccionado.setFechaNacimiento(vpac.getFechaNac());
//        seleccionado.setDireccion(vpac.getDireccion());
//        seleccionado.setLocalidad(vpac.getLocalidad());
//        seleccionado.setNroAfiliado(vpac.getNumAfiliado());
//        seleccionado.setObraSocial(vpac.getObraSocial());
//        seleccionado.setSexo(vpac.getSexo());
//        seleccionado.setCelular(vpac.getCelular());
//
//        // 4. GUARDAMOS EN LA DB
//        boolean ok = pacienteDAO.actualizar(seleccionado);
//
//        if (ok) {
//            // 5. REGISTRAMOS SOLO SI HUBO CAMBIOS REALES
//            if (cambiosViejos.length() > 0) {
//                auditoriaDAO.registrar(
//                        usuarioLogueado,
//                        "EDITAR",
//                        "paciente",
//                        seleccionado.getIdPaciente(),
//                        cambiosViejos.toString(),
//                        cambiosNuevos.toString(),
//                        "Se editaron campos específicos del paciente: " + seleccionado.getApellido()
//                );
//            }
//
//            vpac.mostrarMensaje("Paciente actualizado correctamente");
//            vpac.limpiarCampos();
//        } else {
//            vpac.mostrarMensaje("Error al actualizar paciente");
//        }
//
//        cargarPacientesEnTabla();
//    }

    public void buscarPacienteAutomatico() {

        String texto = vpac.getTextoBusqueda();

        if (texto.isEmpty()) {
            cargarPacientesEnTabla(); // vuelve a mostrar todos
            return;
        }

        ArrayList<Paciente> lista = pacienteDAO.buscarPorDniOApellidoONombre(texto);
        vpac.cargarPacientesEnTabla(lista);
    }

    private void guardarResultados() {

        vcr.detenerEdicionTabla();

        // 1. Validaciones iniciales de Obra Social
        String seleccionOS = vcr.getObraSocial().trim();
        if (seleccionOS.isEmpty()) {
            vcr.mostrarMensaje("Debe ingresar una Obra Social (o PARTICULAR).");
            return;
        }

        String medico = vcr.getMedicoSolicitante().trim();
        if (medico.isEmpty()) {
            medico = "-";
        }

        int filas = vcr.getCantidadFilas();
        if (filas == 0) {
            vcr.mostrarMensaje("No hay determinaciones para guardar.");
            return;
        }

        try {
            // 2. Extraer Código y Arancel de la Obra Social seleccionada
            String codigoOS = seleccionOS.contains(" - ") ? seleccionOS.split(" - ")[0] : seleccionOS;
            modelo.ObraSocial osSeleccionada = obraSocialDAO.buscarPorCodigoONombre(codigoOS).stream()
                    .filter(o -> o.getCodigo().equals(codigoOS))
                    .findFirst()
                    .orElse(null);

            if (osSeleccionada == null) {
                vcr.mostrarMensaje("La Obra Social ingresada no es válida.");
                return;
            }

            // 3. Lógica de precio según OS
            double precioFinal;

            if (codigoOS.equals("60001")) {
                // PARTICULAR: el operador ingresa el precio manualmente
                precioFinal = vcr.pedirPrecioManual();
                if (precioFinal < 0) {
                    // El usuario canceló el diálogo
                    return;
                }
            } else {
                // Resto de obras sociales: UB * Arancel
                double arancelOS = osSeleccionada.getArancel();

                // 1. Usamos un Set (conjunto) para guardar los códigos de los PADRES encontrados.
                // El Set automáticamente ignora los duplicados.
                java.util.HashSet<String> codigosPadresUnicos = new java.util.HashSet<>();

                for (int i = 0; i < filas; i++) {
                    String cod = vcr.getCodigo(i);

                    if (cod != null && !cod.trim().isEmpty()) {
                        // Si el código tiene un punto (es hijo), extraemos al padre. 
                        // Si no tiene punto (ya es padre), lo dejamos tal cual.
                        String codigoPadre = cod.contains(".") ? cod.split("\\.")[0] : cod;

                        // Añadimos el código del padre a nuestra bolsa.
                        // Si ya estaba (ej. porque cargamos 10 hijos del hemograma), el Set lo ignora.
                        codigosPadresUnicos.add(codigoPadre);
                    }
                }

                // 2. Ahora sí, sumamos la UB de cada padre único
                double sumaTotalUB = 0;
                for (String codPadre : codigosPadresUnicos) {
                    Determinacion detPadre = determinacionDAO.buscarPorCodigo(codPadre);
                    if (detPadre != null) {
                        sumaTotalUB += detPadre.getUb();
                    }
                }

                // 3. OBTENER EL VALOR GLOBAL DE LA UB DESDE CONFIGURACIÓN
                // Aquí aplicamos tu lógica de getValor("valor_ub") que mostraste arriba
                String valorConfig = configDAO.getValor("valor_ub");
                double valorUBActual = (valorConfig != null) ? Double.parseDouble(valorConfig) : 1820.0; // Puse 1820 como fallback

                // 4. El precio final es: (Suma de UB de los estudios) * (Arancel de la OS) * (Valor en pesos de 1 UB)
                // ¡OJO! Si tu Arancel de OS ya representa el precio final y no necesitas multiplicar por la UB base, 
                // entonces borra '* valorUBActual' de la línea de abajo.
                precioFinal = sumaTotalUB * arancelOS * valorUBActual;

                // DEBUG: Útil para ver qué está pasando
                System.out.println("Debug Precio: Padres Únicos Encontrados=" + codigosPadresUnicos.toString());
                System.out.println("Debug Precio: SumaUB=" + sumaTotalUB + " * ArancelOS=" + arancelOS + " * ValorUB=" + valorUBActual + " = " + precioFinal);
            }

            // 4. Configurar el objeto Analisis con el código de OS
            Analisis a = new Analisis();
            a.setIdPaciente(pacienteActual.getIdPaciente());
            a.setObraSocial(codigoOS);
            a.setFecha(new java.util.Date());
            a.setPrecio(precioFinal);
            a.setMedicoSolicitante(medico);

            int idAnalisis = analisisDAO.crear(a);

            if (idAnalisis > 0) {
                // 5. Guardar los resultados detallados — salteando filas de título
                for (int i = 0; i < filas; i++) {
                    String cod = vcr.getCodigo(i);

                    // ── SALTAR filas de título virtual (código vacío o null) ──────
                    if (cod == null || cod.trim().isEmpty()) {
                        continue;
                    }

                    modelo.ResultadoAnalisis r = new modelo.ResultadoAnalisis();
                    r.setIdAnalisis(idAnalisis);
                    r.setCodigo(cod);
                    r.setNombrePrueba(vcr.getNombrePrueba(i));
                    r.setResultado(vcr.getResultado(i));
                    r.setUnidad(vcr.getUnidad(i));
                    r.setReferencia(vcr.getReferencia(i));
                    r.setImprimir(true);

                    // Prioridad directamente de la DB
                    modelo.Determinacion det = determinacionDAO.buscarPorCodigo(cod);
                    if (det != null) {
                        r.setPrioridad(det.getPrioridad());

                        // 1. Limpiamos espacios accidentales (trim) para estar 100% seguros
                        String nombreLimpio = det.getNombre() != null ? det.getNombre().trim() : "";
                        String referenciaLimpia = det.getReferencia() != null ? det.getReferencia().trim() : "";
                        String resultadoActual = r.getResultado() != null ? r.getResultado().trim() : "";

                        // 2. Evaluamos si es un campo método y necesita autocompletarse
                        boolean esMetodo = nombreLimpio.equalsIgnoreCase("Método") || nombreLimpio.equalsIgnoreCase("Metodo");

                        if (esMetodo && resultadoActual.isEmpty() && !referenciaLimpia.isEmpty()) {
                            // ¡ÉXITO! Auto-rellenamos
                            r.setResultado(referenciaLimpia);
                            System.out.println("✅ AUTO-RELLENO APLICADO A: [" + nombreLimpio + "] -> Valor: " + referenciaLimpia);
                            
                        } else if (esMetodo) {
                            // CHIVATO: Si es un método pero NO se autocompletó, que la consola nos diga por qué falló
                            System.out.println("❌ FALLÓ AUTO-RELLENO PARA: [" + nombreLimpio + "]");
                            System.out.println("   -> ¿El campo resultado ya tenía algo escrito?: " + !resultadoActual.isEmpty());
                            System.out.println("   -> ¿El campo REFERENCIA en la base de datos está vacío?: " + referenciaLimpia.isEmpty());
                        }
                    }

                    resultadoDAO.guardar(r);
                }

                // 6. Registro de Auditoría
                auditoriaDAO.registrar(usuarioLogueado, "CREAR", "analisis", idAnalisis, null,
                        "Precio: $" + precioFinal + " (OS: " + codigoOS + ")",
                        "Análisis creado para: " + pacienteActual.getApellido());

                // 7. Notificar éxito al usuario
                vcr.mostrarMensaje("Análisis guardado con éxito.\nObra Social: " + osSeleccionada.getNombre() + "\nTotal: $" + precioFinal);

                // 8. Ocultar popups flotantes para evitar bugs de renderizado
                if (vcr != null) {
                    ((VistaCargarResultados) vcr).ocultarSugerenciasFlotantes();
                }

                // 9. NAVEGACIÓN: Llevar al usuario al Historial Global de Análisis
                if (this.vla == null) {
                    this.vla = new VistaAnalisis();
                    this.vla.setControlador(this);
                    vp.registrarPanel((javax.swing.JPanel) this.vla, "analisis");
                }

                // Recargamos los datos de la vista de análisis
                ArrayList<Analisis> listaActualizada = analisisDAO.buscarAnalisisGlobal("");
                vla.cargarAnalisisEnTabla(listaActualizada);

                // Mostramos el panel en modo inmersión y limpiamos focos
                vp.activarModoInmersion();
                vp.mostrarSeccion("analisis");
                limpiarFocoYPantalla();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            vcr.mostrarMensaje("Error crítico al procesar el cálculo y guardado.");
        }
    }

    private int analisisSeleccionadoId = -1;

    public void analisisSeleccionadoId() {
        int id = vha.getAnalisisSeleccionadoId();

        if (id == -1) {
            this.analisisSeleccionadoId = -1;
            return;
        }

        this.analisisSeleccionadoId = id;

        Analisis completo = analisisDAO.buscarPorId(id);

        if (completo != null) {
            // Ahora informamos el ID y el Precio en el log de depuración
            System.out.println("ID Análisis seleccionado: " + completo.getIdAnalisis());
            System.out.println("Precio calculado: $" + completo.getPrecio());
        }
    }

    private void generarInforme(int idAnalisis, Date fechaImpresion) {
        List<java.io.InputStream> streamsAbiertos = new ArrayList<>();
        try {
            if (idAnalisis == -1) {
                return;
            }

            Analisis analisis = analisisDAO.buscarPorId(idAnalisis);

            // ── CARGAR PACIENTE SI ES NULL (llamado desde VistaAnalisis global) ──
            if (pacienteActual == null && analisis != null) {
                pacienteActual = pacienteDAO.buscarPorId(analisis.getIdPaciente());
            }
            if (pacienteActual == null) {
                JOptionPane.showMessageDialog(null, "No se pudo cargar el paciente del análisis.");
                return;
            }

            List<ResultadoAnalisis> resultadosOriginales = resultadoDAO.listarIncluidosPorAnalisis(idAnalisis);

            if (resultadosOriginales.isEmpty()) {
                JOptionPane.showMessageDialog(null, "El análisis no tiene resultados incluidos");
                return;
            }

            // ── FILTRAR FILAS SIN RESULTADO (Protegiendo los Subtítulos) ─────────
            List<ResultadoAnalisis> resultadosFiltrados = new ArrayList<>();
            for (ResultadoAnalisis r : resultadosOriginales) {
                String res = r.getResultado();
                String nombre = r.getNombrePrueba() != null ? r.getNombrePrueba() : "";

                // Si es un subtítulo inventado en el NBU (Ej: --- FÓRMULA ---), lo dejamos pasar siempre
                boolean esSubtitulo = nombre.startsWith("---") && nombre.endsWith("---");

                if (esSubtitulo || (res != null && !res.trim().isEmpty())) {
                    resultadosFiltrados.add(r);
                }
            }

            if (resultadosFiltrados.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No hay resultados cargados para imprimir");
                return;
            }

            // ── TÍTULOS DINÁMICOS (100% Data-Driven) ─────────────────────────────
            List<ResultadoAnalisis> listaConTitulos = new ArrayList<>();
            String codigoPadreActual = "";

            for (ResultadoAnalisis r : resultadosFiltrados) {
                String codigoFila = r.getCodigo();

                if (codigoFila == null || codigoFila.trim().isEmpty()) {
                    // Por si acaso entra algún título virtual ya creado, lo limpiamos también
                    if (r.getNombrePrueba() != null) {
                        r.setNombrePrueba(r.getNombrePrueba().replace("---", "").trim());
                    }
                    listaConTitulos.add(r);
                    continue;
                }

                // Extraemos el código padre
                String codigoPadreFila = codigoFila.contains(".") ? codigoFila.split("\\.")[0] : codigoFila;

                // Si el padre cambia, inyectamos el título principal
                if (!codigoPadreFila.equals(codigoPadreActual)) {
                    modelo.Determinacion detPadre = determinacionDAO.buscarPorCodigo(codigoPadreFila);
                    String nombreTitulo = (detPadre != null) ? detPadre.getNombre() : "ESTUDIO";

                    ResultadoAnalisis titulo = new ResultadoAnalisis();
                    titulo.setCodigo(""); // Vacío para que Jasper sepa que es título
                    
                    // ── 1. TÍTULO PRINCIPAL LIMPIO (Sin guiones) ──
                    titulo.setNombrePrueba(nombreTitulo); 
                    
                    titulo.setResultado(" "); // EL TRUCO NINJA: Espacio para que Jasper no lo oculte
                    titulo.setUnidad("");
                    titulo.setReferencia("");

                    listaConTitulos.add(titulo);
                    codigoPadreActual = codigoPadreFila;
                }

                // ── 2. LIMPIEZA DE SUBTÍTULOS FALSOS ──
                // Si esta fila es un subtítulo como "--- ÍNDICES ---", le quitamos los guiones para el PDF
                String nombreFila = r.getNombrePrueba();
                if (nombreFila != null && nombreFila.startsWith("---") && nombreFila.endsWith("---")) {
                    r.setNombrePrueba(nombreFila.replace("---", "").trim());
                }

                listaConTitulos.add(r);
            }

            // ── 2. SELECCIÓN DE REPORTE Y JASPER ──────────────────────────────────
            String formato = configDAO.getValor("imp_formato_hoja");
            if (formato == null || formato.trim().isEmpty()) {
                formato = "a4";
            }

            String rutaReporte;
            switch (formato.trim().toLowerCase()) {
                case "a4_horizontal":
                    rutaReporte = "/reportes/informe_A4_horizontal.jrxml";
                    break;
                case "a5":
                    rutaReporte = "/reportes/informe_A5_vertical.jrxml";
                    break;
                case "a5_horizontal":
                    rutaReporte = "/reportes/informe_A5_horizontal.jrxml";
                    break;
                default:
                    rutaReporte = "/reportes/informe_A4_vertical.jrxml";
                    break;
            }

            java.io.InputStream reporteStream = getClass().getResourceAsStream(rutaReporte);
            if (reporteStream == null) {
                JOptionPane.showMessageDialog(null, "Error: No se encontró " + rutaReporte);
                return;
            }

            // ── 3. COMPILACIÓN Y PARÁMETROS ───────────────────────────────────────
            net.sf.jasperreports.engine.JasperReport jasperReport = net.sf.jasperreports.engine.JasperCompileManager.compileReport(reporteStream);
            java.util.Map<String, Object> params = new java.util.HashMap<>();

            String medico = analisis.getMedicoSolicitante();
            params.put("medicoSolicitante",
                    (medico == null || medico.trim().isEmpty() || medico.equals("-")) ? null : medico.trim());

            params.put("labNombre", configDAO.getValor("lab_nombre"));
            params.put("labDireccion", configDAO.getValor("lab_direccion"));
            params.put("labLocalidad", configDAO.getValor("lab_localidad"));
            params.put("labBioquimico", configDAO.getValor("lab_bioquimico"));
            params.put("labMatricula", configDAO.getValor("lab_matricula"));
            params.put("labTelefono", configDAO.getValor("lab_telefono"));
            params.put("fechaAnalisis", fechaImpresion);
            params.put("pacienteNombre", pacienteActual.getApellido() + " " + pacienteActual.getNombre());
            params.put("pacienteDni", pacienteActual.getDni());
            params.put("precio", analisis.getPrecio());

            // Logo
            String rLogo = configDAO.getValor("lab_logo");
            if ("true".equals(configDAO.getValor("imp_incluir_logo"))
                    && rLogo != null && !rLogo.isEmpty()) {
                java.io.File f = new java.io.File(rLogo);
                if (f.exists()) {
                    java.io.FileInputStream fis = new java.io.FileInputStream(f);
                    streamsAbiertos.add(fis);
                    params.put("urlLogo", fis);
                } else {
                    params.put("urlLogo", null);
                }
            } else {
                params.put("urlLogo", null);
            }

            // Firma
            String rFirma = configDAO.getValor("lab_firma");
            if (rFirma != null && !rFirma.isEmpty()) {
                java.io.File f = new java.io.File(rFirma);
                if (f.exists()) {
                    java.io.FileInputStream fis = new java.io.FileInputStream(f);
                    streamsAbiertos.add(fis);
                    params.put("urlFirma", fis);
                }
            }

            // ── 4. LLENAR Y MOSTRAR ───────────────────────────────────────────────
            net.sf.jasperreports.engine.data.JRBeanCollectionDataSource ds = new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource(listaConTitulos);
            net.sf.jasperreports.engine.JasperPrint jasperPrint = net.sf.jasperreports.engine.JasperFillManager.fillReport(jasperReport, params, ds);

            JDialog dialog = new JDialog((java.awt.Frame) vp, "Visor de Informe", true);
            dialog.setSize(1000, 800);
            dialog.setLocationRelativeTo((java.awt.Frame) vp);
            dialog.getContentPane().add(new net.sf.jasperreports.swing.JRViewer(jasperPrint));
            dialog.setVisible(true);

            // ── 5. PDF AUTOMÁTICO ─────────────────────────────────────────────────
            String carpeta = configDAO.getValor("ruta_pdf");
            if (carpeta != null && !carpeta.isEmpty()) {
                String fechaS = new java.text.SimpleDateFormat("dd-MM-yyyy").format(fechaImpresion);
                String nombre = (pacienteActual.getApellido() + "_"
                        + pacienteActual.getNombre() + "_" + fechaS + ".pdf").replace(" ", "_");
                java.io.File folder = new java.io.File(carpeta);
                if (!folder.exists()) {
                    folder.mkdirs();
                }
                net.sf.jasperreports.engine.JasperExportManager.exportReportToPdfFile(jasperPrint, new java.io.File(folder, nombre).getAbsolutePath());
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al generar informe: " + e.getMessage());
        } finally {
            for (java.io.InputStream is : streamsAbiertos) {
                try {
                    is.close();
                } catch (Exception ex) {
                }
            }
        }
    }

    private ResultadoAnalisis tituloSeccion(String nombre) {
        ResultadoAnalisis t = new ResultadoAnalisis();
        t.setNombrePrueba(nombre);
        t.setResultado("");
        t.setUnidad("");
        t.setReferencia("");
        return t;
    }

    private void cargarDatosLaboratorio() {
        // Datos de identidad
        va.setNombreLaboratorioACtual(configDAO.getValor("lab_nombre"));
        va.setDireccion(configDAO.getValor("lab_direccion"));
        va.setLocalidad(configDAO.getValor("lab_localidad"));
        va.setTelefono(configDAO.getValor("lab_telefono"));
        va.setBioquimico(configDAO.getValor("lab_bioquimico"));
        va.setMatricula(configDAO.getValor("lab_matricula"));
        va.setLogo(configDAO.getValor("lab_logo"));
        va.setFirma(configDAO.getValor("lab_firma"));
        va.setMatriculaFirma(configDAO.getValor("lab_matricula_firma"));
        va.setAclaracionFirma(configDAO.getValor("lab_aclaracion_firma"));

        // CORRECCIÓN: Nombres de clave idénticos a los de guardar
        va.setRutaBackup(configDAO.getValor("ruta_backup"));
        va.setRutaPdf(configDAO.getValor("ruta_pdf"));

        // Datos de Impresión
        va.setTamanoHoja(configDAO.getValor("imp_tamano_hoja"));
        va.setOrientacion(configDAO.getValor("imp_orientacion"));

        boolean incluirLogo = "true".equals(configDAO.getValor("imp_incluir_logo"));
        boolean autoPrint = "true".equals(configDAO.getValor("imp_auto_print"));

        va.setIncluirLogo(incluirLogo);
        va.setAutoPrint(autoPrint);

        va.setValorUB(configDAO.getValor("valor_ub"));
    }

    private void guardarDatosLaboratorio() {
        configDAO.guardar("lab_nombre", va.getNombreLaboratorio());
        configDAO.guardar("lab_direccion", va.getDireccion());
        configDAO.guardar("lab_localidad", va.getLocalidad());
        configDAO.guardar("lab_telefono", va.getTelefono());
        configDAO.guardar("lab_bioquimico", va.getBioquimico());
        configDAO.guardar("lab_matricula", va.getMatricula());
        configDAO.guardar("lab_logo", va.getLogo());
        configDAO.guardar("lab_firma", va.getFirma());
        configDAO.guardar("lab_matricula_firma", va.getMatriculaFirma());
        configDAO.guardar("lab_aclaracion_firma", va.getAclaracionFirma());

        // CORRECCIÓN: Nombres de clave idénticos a los de cargar
        configDAO.guardar("ruta_backup", va.getRutaBackup());
        configDAO.guardar("ruta_pdf", va.getRutaPdf());

        configDAO.guardar("imp_tamano_hoja", va.getTamanoHoja());
        configDAO.guardar("imp_orientacion", va.getOrientacion());
        configDAO.guardar("imp_incluir_logo", String.valueOf(va.isIncluirLogo()));
        configDAO.guardar("imp_auto_print", String.valueOf(va.isAutoPrint()));

        va.mostrarMensaje("Configuración actualizada correctamente.");
    }

    private void guardarConfiguracionImpresion() {

        String tamano = va.getTamanoHoja();        // ej: a4 o a5
        String orientacion = va.getOrientacion();  // ej: vertical u horizontal

        String incluirLogo = va.isIncluirLogo() ? "true" : "false";
        String autoPrint = va.isAutoPrint() ? "true" : "false";

        // 🔥 Construimos el formato completo que usa el switch
        String formatoFinal;

        if ("vertical".equalsIgnoreCase(orientacion)) {
            formatoFinal = tamano.toLowerCase();
        } else {
            formatoFinal = tamano.toLowerCase() + "_horizontal";
        }

        // Guardamos TODO
        configDAO.guardar("imp_tamano_hoja", tamano);
        configDAO.guardar("imp_orientacion", orientacion);
        configDAO.guardar("imp_formato_hoja", formatoFinal); // 🔥 ESTA ES LA CLAVE
        configDAO.guardar("imp_incluir_logo", incluirLogo);
        configDAO.guardar("imp_auto_print", autoPrint);

        System.out.println("FORMATO GUARDADO: " + formatoFinal);

        va.mostrarMensaje("Configuración de impresión guardada con éxito.");
    }

    public void buscarSugerenciasDeterminacion(String texto) {
        List<Determinacion> sugerencias;

        // Si son exactamente 3 dígitos, buscar SOLO por sufijo de código
        if (texto.matches("\\d{3}")) {
            sugerencias = determinacionDAO.buscarPorSufijo(texto);
        } else {
            sugerencias = determinacionDAO.buscar(texto);
        }

        vd.mostrarSugerencias(sugerencias);
    }

    public void buscarEnNBU(String filtro) {
        if (filtro == null || filtro.trim().isEmpty()) {
            // Campo vacío → mostrar lista completa con hijos
            vnbu.cargarDeterminaciones(determinacionDAO.listarTodo());
        } else {
            vnbu.cargarDeterminaciones(determinacionDAO.buscar(filtro));
        }
    }

    public Usuario getUsuarioLogueado() {
        return this.usuarioLogueado;
    }

    private void aplicarRestriccionesSegunRol() {
        String rol = usuarioLogueado.getRol();

        // Reset: Habilitamos todo por defecto
        vp.habilitarBotonPacientes(true);
        vp.habilitarBotonNBU(true);
        vp.habilitarBotonAjustes(true);

        // --- RESTRICCIONES DE SEGURIDAD ---
        // Solo el ADMIN ve Gestión de Usuarios y Auditoría
        boolean isAdmin = rol.equals("ADMIN");
        vp.habilitarBotonGestionUsuarios(isAdmin);
        vp.habilitarBotonAuditoria(isAdmin);

        if (rol.equals("LECTOR") || rol.equals("TECNICO")) {
            vp.habilitarBotonAjustes(false);
            vp.habilitarBotonNBU(false);
        }
    }

    private void mostrarAvisoBackup(boolean mostrar) {
        if (mostrar) {
            Object[] options = {}; // Array vacío para quitar botones
            JOptionPane pane = new JOptionPane("Generando copia de seguridad...\nPor favor, no cierre el programa.",
                    JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, options, null);

            // CORRECCIÓN: Casteamos vp a Component para que createDialog lo acepte
            JDialog dialog = pane.createDialog((java.awt.Component) vp, "Copia de Seguridad en curso");

            // Configuramos para que el usuario no pueda cerrarlo con la "X" manualmente
            dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

            new Thread(() -> {
                dialog.setVisible(true);
            }).start();

            this.dialogoEspera = dialog;
        } else {
            if (this.dialogoEspera != null) {
                this.dialogoEspera.dispose();
            }
        }
    }

    

    private void guardarMedico() {
        // 1. Extraer datos de la vista
        String apellido = vm.getApellidoMedico();
        String nombre = vm.getNombreMedico();
        String matricula = vm.getMatriculaMedico();
        String especialidad = vm.getEspecialidad();
        String observaciones = vm.getObservacionesMedico();

        // 2. Validación básica (Integridad de datos)
        if (apellido.isEmpty() || nombre.isEmpty() || matricula.isEmpty()) {
            vm.mostrarMensaje("Error: Apellido, Nombre y Matrícula son campos obligatorios.");
            return;
        }

        if (medicoDAO.existeMatricula(matricula)) {
            vm.mostrarMensaje("Error: Ya existe un médico registrado con la matrícula " + matricula);
            return; // Cortamos la ejecución para que no guarde
        }

        // 3. Crear el objeto modelo
        Medico m = new Medico();
        m.setApellidoMedico(apellido);
        m.setNombreMedico(nombre);
        m.setMatricula(matricula);
        m.setEspecialidad(especialidad);
        m.setObservaciones(observaciones);

        // 4. Pedir al DAO que guarde
        if (medicoDAO.guardarMedico(m)) {
            vm.mostrarMensaje("Médico guardado correctamente.");
            vm.limpiarCampos();
            cargarMedicosEnTabla(); // Refrescamos la grilla
        } else {
            vm.mostrarMensaje("Error: No se pudo guardar el médico. Verifique si la matrícula ya existe.");
        }
    }

    private void eliminarMedico() {
        // 1. Obtener el médico seleccionado de la grilla
        Medico seleccionado = vm.getMedicoSeleccionado();

        if (seleccionado == null) {
            vm.mostrarMensaje("Por favor, seleccione un médico de la tabla.");
            return;
        }

        // 2. Pedir confirmación (Utilizando la técnica de desacoplamiento)
        // Nota: Si no tienes el método confirmarAccion en la interfaz, puedes usar JOptionPane por ahora
        int respuesta = JOptionPane.showConfirmDialog(
                (java.awt.Component) vm,
                "¿Está seguro que desea eliminar al Dr/a. " + seleccionado.getApellidoMedico() + "?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (respuesta == JOptionPane.YES_OPTION) {
            // 3. Ejecutar eliminación usando la matrícula como ID único
            if (medicoDAO.eliminarMedico(seleccionado.getMatricula())) {
                vm.mostrarMensaje("Médico eliminado con éxito.");
                cargarMedicosEnTabla(); // Refrescamos la grilla
                vm.limpiarCampos();
            } else {
                vm.mostrarMensaje("Error: No se pudo eliminar el médico seleccionado.");
            }
        }
    }

    

    public void buscarSugerenciasMedicoSolicitante() {
        String busqueda = vcr.getMedicoSolicitante();

        if (busqueda.length() < 1) {
            vcr.mostrarSugerenciasMedicos(new ArrayList<>());
            return;
        }

        List<String> sugerencias = medicoDAO.obtenerSugerenciasMedicos(busqueda);
        vcr.mostrarSugerenciasMedicos(sugerencias);
    }

// para calcular precio de UB
    private double calcularPrecioAnalisis(List<Determinacion> seleccionadas) {
        // 1. Obtener el valor de la UB desde la configuración (DB)
        String valorConfig = configDAO.getValor("valor_ub");
        double valorUBActual = (valorConfig != null) ? Double.parseDouble(valorConfig) : 1600.0;

        // 2. Sumar todas las UB de las determinaciones
        double totalUB = seleccionadas.stream()
                .mapToDouble(Determinacion::getUb)
                .sum();

        // 3. Retornar el producto final
        return totalUB * valorUBActual;
    }

    private void listarAnalisisPaciente() {
        // Verificamos que tengamos un paciente seleccionado para evitar NullPointerException
        if (pacienteActual != null && vha != null) {
            // 1. Buscamos el historial actualizado en la base de datos
            ArrayList<Analisis> historial = analisisDAO.listarPorPaciente(pacienteActual.getIdPaciente());

            // 2. Cargamos la lista en la tabla de la vista del historial
            vha.cargarHistorial(historial);

            System.out.println("Historial de análisis refrescado para: " + pacienteActual.getApellido());
        }
    }

    public void actualizarBusquedaObrasSociales() {
        if (vos != null) {
            String filtro = vos.getTextoBusqueda();
            ArrayList<ObraSocial> filtradas;

            if (filtro.isEmpty()) {
                // Si el buscador está vacío, mostramos todas
                filtradas = obraSocialDAO.listarObrasSociales();
            } else {
                // Si hay texto, usamos el método de búsqueda del DAO
                filtradas = obraSocialDAO.buscarPorCodigoONombre(filtro);
            }

            vos.cargarObrasSocialesEnTabla(filtradas);
        }
    }

    private void actualizarTablaObrasSociales() {
        // 1. Verificamos que la vista no sea nula para evitar errores
        if (vos != null) {
            // 2. Obtenemos la lista actualizada desde la base de datos a través del DAO
            ArrayList<ObraSocial> listaActualizada = obraSocialDAO.listarObrasSociales();

            // 3. Le pasamos la lista a la vista para que refresque la grilla
            vos.cargarObrasSocialesEnTabla(listaActualizada);

            System.out.println("DEBUG: Tabla de Obras Sociales actualizada con éxito.");
        }
    }

    private void cargarObrasSocialesEnTabla() {
        if (vos != null) {
            try {
                ArrayList<ObraSocial> obs = obraSocialDAO.listarObrasSociales();
                vos.cargarObrasSocialesEnTabla(obs);
            } catch (Exception e) {
                System.out.println("ERROR AL LISTAR OBRAS SOCIALES. " + e.getMessage());
            }
        }
    }

    public void buscarSugerenciasOS(String texto) {
        // 1. Buscamos en el DAO
        ArrayList<ObraSocial> lista = obraSocialDAO.buscarPorCodigoONombre(texto);

        // 2. Convertimos a la lista formateada: "60001 - PARTICULAR"
        List<String> sugerencias = lista.stream()
                .map(os -> os.getCodigo() + " - " + os.getNombre())
                .toList();

        // 3. Enviamos a la vista (usando el casteo si no lo agregaste a la interfaz)
        ((VistaPaciente) vpac).mostrarSugerenciasOS(sugerencias);
    }

    public void buscarSugerenciasOSAnalisis(String texto) {
        // 1. Buscamos en el DAO
        ArrayList<ObraSocial> lista = obraSocialDAO.buscarPorCodigoONombre(texto);

        // 2. Formateamos para la lista: "CODIGO - NOMBRE"
        List<String> sugerencias = lista.stream()
                .map(os -> os.getCodigo() + " - " + os.getNombre())
                .toList();

        // 3. Enviamos a la vista de carga de resultados
        vcr.mostrarSugerenciasOS(sugerencias);
    }

    public void buscarSugerenciasMedicoSolicitanteDetalle() {
        // Usamos el mismo método del DAO que ya configuramos antes
        String busqueda = vvda.getMedicoSolicitante();
        if (busqueda.length() < 1) {
            vvda.mostrarSugerenciasMedicos(new ArrayList<>());
            return;
        }
        List<String> sugerencias = medicoDAO.obtenerSugerenciasMedicos(busqueda);
        vvda.mostrarSugerenciasMedicos(sugerencias);
    }

    public void buscarAnalisisAutomatico() {
        String filtro = vla.getTextoBusqueda();
        ArrayList<Analisis> resultados = analisisDAO.buscarAnalisisGlobal(filtro);
        vla.cargarAnalisisEnTabla(resultados);
    }

    //FACTOR COMÚN PARA ABRIR DETALLES:
    private void abrirDetalleAnalisis(int idAnalisis) {
        if (idAnalisis == -1) {
            return;
        }

        Analisis analisisDetalle = analisisDAO.buscarPorId(idAnalisis);
        if (analisisDetalle == null) {
            return;
        }

        Paciente p = pacienteDAO.buscarPorId(analisisDetalle.getIdPaciente());

        // ── 1. GESTIÓN DEL PANEL (Reemplaza al new JDialog) ──────────
        if (this.vvda == null) {
            this.vvda = new VistaVerDetalleAnalisis(); // Ahora es un JPanel sin parámetros
            this.vvda.setControlador(this);
            this.vp.registrarPanel((JPanel) this.vvda, "ver_detalle_analisis");
        }

        // ── 2. PERMISOS ──────────────────────────────────────────────
        if (usuarioLogueado.getRol().equals("LECTOR")) {
            vvda.habilitarBotonGuardar(false);
            vvda.habilitarBotonEliminar(false);
            vvda.bloquearMedicoSolicitante();
            vvda.habilitarBotonImprimir(true);
            vvda.bloquearEdicionTabla();
        }

        // ── 3. CARGA DE DATOS EN LA VISTA ────────────────────────────
        String nombreC = (p != null) ? (p.getApellido() + " " + p.getNombre()) : "Paciente Desconocido";
        vvda.setNombrePaciente(nombreC);

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
        vvda.setFechaAnalisis(sdf.format(analisisDetalle.getFecha()));
        vvda.setFechaInforme(analisisDetalle.getFecha());
        vvda.setIdAnalisis(idAnalisis);
        vvda.setMedicoSolicitante(analisisDetalle.getMedicoSolicitante());

        // ── 4. INYECTAR TÍTULOS EN LOS RESULTADOS ────────────────────
        List<ResultadoAnalisis> originales = resultadoDAO.listarPorAnalisis(idAnalisis);
        List<ResultadoAnalisis> conTitulos = inyectarTitulosEnResultados(originales);

        vvda.cargarResultadosDetalle((ArrayList<ResultadoAnalisis>) conTitulos);

        // ── 5. ACTIVAR INMERSIÓN Y MOSTRAR (Reemplaza a vvda.ejecutar()) ─
        vp.activarModoInmersion();
        vp.mostrarSeccion("ver_detalle_analisis");
    }

    /**
     * Mismo criterio de rangos que generarInforme y BTN_CONTINUAR
     */
    private List<ResultadoAnalisis> inyectarTitulosEnResultados(List<ResultadoAnalisis> originales) {
        List<ResultadoAnalisis> lista = new ArrayList<>();
        String codigoPadreActual = "";

        for (ResultadoAnalisis r : originales) {
            String codigoFila = r.getCodigo();

            // Si el código es null o vacío (por ej. si ya es un título inyectado previamente), lo pasamos directo
            if (codigoFila == null || codigoFila.trim().isEmpty()) {
                lista.add(r);
                continue;
            }

            // 1. Extraemos el código padre (Ej: de '660475.1' sacamos '660475')
            String codigoPadreFila = codigoFila.contains(".") ? codigoFila.split("\\.")[0] : codigoFila;

            // 2. Si el padre de esta fila es diferente al que veníamos agrupando, creamos un título nuevo
            if (!codigoPadreFila.equals(codigoPadreActual)) {
                // Buscamos el nombre real del Padre en la Base de Datos
                modelo.Determinacion detPadre = determinacionDAO.buscarPorCodigo(codigoPadreFila);
                String nombreTitulo = (detPadre != null) ? detPadre.getNombre() : "ESTUDIO";

                // Inyectamos el título en la lista. Le agregamos los guiones para mantener tu estética
                lista.add(tituloResultado("--- " + nombreTitulo + " ---"));

                // Actualizamos el padre actual para no repetir el título en la siguiente iteración
                codigoPadreActual = codigoPadreFila;
            }

            // 3. Agregamos la fila actual (sea un componente normal o un "Subtítulo falso" como FÓRMULA)
            lista.add(r);
        }

        return lista;
    }

    private ResultadoAnalisis tituloResultado(String titulo) {
        ResultadoAnalisis r = new ResultadoAnalisis();
        r.setCodigo(""); // Código vacío para que la vista lo reconozca como Título
        r.setNombrePrueba(titulo);
        r.setResultado(" "); // EL TRUCO NINJA: Espacio en blanco para que JasperReports NO lo oculte
        r.setUnidad("");
        r.setReferencia("");
        return r;
    }

// En crearTituloVirtual del controlador, simplificá:
    private Determinacion crearTituloVirtual(String nombre) {
        Determinacion titulo = new Determinacion();
        titulo.setCodigo("");
        titulo.setNombre(nombre); // ← texto plano, sin HTML
        titulo.setUnidad("");
        titulo.setReferencia("");
        return titulo;
    }

    private void limpiarFocoYPantalla() {
        if (vp != null) {
            // Le quita el foco a cualquier botón que haya quedado presionado
            ((javax.swing.JFrame) vp).requestFocusInWindow();
        }
    }

    // =========================================================================
    //  MÉTODOS DE GESTIÓN DEL NOMENCLADOR (NBU) - PADRES E HIJOS
    // =========================================================================
    public void seleccionarPadreNBU(String codigoPadre) {
        // ── MAGIA UX: AUTO-GUARDADO TOTAL ──
        // Guardamos todo lo que el usuario tipeó en la tabla actual ANTES de cambiarla
        guardarCambiosPendientesHijos();

        // 1. Si no hay un código válido seleccionado, enviamos una lista vacía para limpiar la tabla
        if (codigoPadre == null || codigoPadre.trim().isEmpty()) {
            vnbu.cargarHijos(new java.util.ArrayList<>()); 
            return;
        }

        // 2. Buscamos los hijos en la base de datos
        java.util.List<modelo.Determinacion> hijos = determinacionDAO.obtenerComponentes(codigoPadre);

        // 3. Le pasamos la lista de hijos a la vista para que la dibuje en la tablita de la derecha
        vnbu.cargarHijos(hijos);
    }

    // =========================================================================
    //  LÓGICA DE BOTONES: PANEL DE HIJOS (NBU)
    // =========================================================================
    private void quitarHijoNBU() {
        String padre = vnbu.getCodigoPadreSeleccionado();
        String hijo = vnbu.getCodigoHijoSeleccionado();

        if (padre == null || hijo == null) {
            vnbu.mostrarMensaje("Debe seleccionar un componente de la tabla de la derecha para quitarlo.");
            return;
        }

        int confirmacion = javax.swing.JOptionPane.showConfirmDialog(
                null,
                "¿Está seguro de que desea desvincular el componente '" + hijo + "'?\nEsto NO borrará el código del sistema, solo lo quitará de esta práctica.",
                "Confirmar",
                javax.swing.JOptionPane.YES_NO_OPTION,
                javax.swing.JOptionPane.WARNING_MESSAGE
        );

        if (confirmacion == javax.swing.JOptionPane.YES_OPTION) {
            boolean exito = determinacionDAO.desvincularHijo(padre, hijo);
            if (exito) {
                // Refrescamos la tablita derecha
                refrescarAmbasTablasNBU(padre);
            } else {
                vnbu.mostrarMensaje("Hubo un error al intentar desvincular el componente.");
            }
        }
    }

    private void moverHijoNBU(int direccion) {
        String padre = vnbu.getCodigoPadreSeleccionado();
        String hijoSeleccionado = vnbu.getCodigoHijoSeleccionado();

        if (padre == null || hijoSeleccionado == null) {
            vnbu.mostrarMensaje("Seleccione un componente de la lista para cambiar su orden.");
            return;
        }

        // 1. Traemos la lista actual de hijos
        java.util.List<modelo.Determinacion> hijos = determinacionDAO.obtenerComponentes(padre);
        if (hijos.size() <= 1) {
            return; // No hay nada que mover si hay 1 o 0 elementos
        }
        // 2. Buscamos en qué posición está el hijo que queremos mover
        int index = -1;
        for (int i = 0; i < hijos.size(); i++) {
            if (hijos.get(i).getCodigo().equals(hijoSeleccionado)) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            return;
        }

        // 3. Calculamos la nueva posición (direccion: -1 es subir, 1 es bajar)
        int nuevaPosicion = index + direccion;

        // Si se sale de los límites (ya está arriba de todo o abajo de todo), no hacemos nada
        if (nuevaPosicion < 0 || nuevaPosicion >= hijos.size()) {
            return;
        }

        // 4. Intercambiamos los elementos en la lista de Java (Swap)
        java.util.Collections.swap(hijos, index, nuevaPosicion);

        // 5. AUTO-CURACIÓN: Recorremos toda la lista y actualizamos la prioridad en la DB de 1 a N.
        // Esto soluciona el problema de que todos tengan "0" en la base de datos.
        for (int i = 0; i < hijos.size(); i++) {
            int nuevaPrioridadDb = i + 1; // 1, 2, 3...
            determinacionDAO.actualizarPrioridad(hijos.get(i).getCodigo(), nuevaPrioridadDb);
        }

        // 6. Refrescamos la tabla para que el usuario vea el cambio instantáneo
        refrescarAmbasTablasNBU(padre);

        // OPCIONAL: Esto mantiene seleccionada la fila que acabamos de mover para seguir moviéndola
        // vnbu.seleccionarFilaHijo(nuevaPosicion); (Si decides implementar este método en la vista después)
    }

    private void agregarHijoNBU() {
        String padre = vnbu.getCodigoPadreSeleccionado();
        
        // Bloqueamos si no hay nada seleccionado O si seleccionó el separador (código vacío)
        if (padre == null || padre.trim().isEmpty()) {
            vnbu.mostrarMensaje("Debe seleccionar una práctica de la tabla para vincularle un componente.");
            return;
        }

        // 1. Interfaz simplificada: Solo pedimos el nombre
        javax.swing.JTextField txtNombre = new javax.swing.JTextField();

        // ── LA MAGIA UX DEFINITIVA: Foco a prueba de balas ──
        txtNombre.addAncestorListener(new javax.swing.event.AncestorListener() {
            @Override
            public void ancestorAdded(javax.swing.event.AncestorEvent event) {
                // Buscamos la ventana flotante que contiene a este campo
                java.awt.Window window = javax.swing.SwingUtilities.getWindowAncestor(txtNombre);
                if (window != null) {
                    // Le obligamos a que, apenas la ventana gane el foco del sistema, se lo pase al campo
                    window.addWindowFocusListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowGainedFocus(java.awt.event.WindowEvent e) {
                            txtNombre.requestFocusInWindow();
                        }
                    });
                }
            }
            @Override public void ancestorRemoved(javax.swing.event.AncestorEvent event) {}
            @Override public void ancestorMoved(javax.swing.event.AncestorEvent event) {}
        });

        // Etiqueta con el tip de UX usando HTML
        javax.swing.JLabel lblTip = new javax.swing.JLabel(
                "<html><small style='color:gray;'>Tip: Para crear un título/separador visual, escríbalo entre guiones.<br>Ejemplo: <b>--- FÓRMULA ---</b></small></html>"
        );

        Object[] mensaje = {
            "Nombre del nuevo componente:", txtNombre,
            " ", // Espacio visual
            lblTip
        };

        int opcion = javax.swing.JOptionPane.showConfirmDialog(
                null,
                mensaje,
                "Agregar Nuevo Componente",
                javax.swing.JOptionPane.OK_CANCEL_OPTION,
                javax.swing.JOptionPane.PLAIN_MESSAGE
        );

        // 2. Si el usuario presiona OK
        if (opcion == javax.swing.JOptionPane.OK_OPTION) {
            String nombreHijo = txtNombre.getText().trim();

            if (nombreHijo.isEmpty()) {
                vnbu.mostrarMensaje("Error: El nombre es obligatorio.");
                return;
            }

            // 3. AUTOGENERACIÓN INTELIGENTE DEL CÓDIGO (A prueba de huérfanos)
            int sufijo = 1;
            String nuevoCodigoHijo = padre + "." + sufijo;

            // Bucle que busca el primer "agujero" o número libre en la BD
            while (determinacionDAO.buscarPorCodigo(nuevoCodigoHijo) != null) {
                sufijo++;
                nuevoCodigoHijo = padre + "." + sufijo;
            }

            // 4. Lo creamos nuevo en la base de datos con el código libre
            boolean creada = determinacionDAO.insertarNuevaDeterminacion(nuevoCodigoHijo, nombreHijo); // Sin toUpperCase
            if (!creada) {
                vnbu.mostrarMensaje("Error en la base de datos al intentar crear el componente.");
                return;
            }

            // 5. Lo vinculamos a la práctica Padre
            boolean exito = determinacionDAO.vincularHijo(padre, nuevoCodigoHijo);
            if (exito) {
                java.util.List<modelo.Determinacion> hijosActuales = determinacionDAO.obtenerComponentes(padre);
                determinacionDAO.actualizarPrioridad(nuevoCodigoHijo, hijosActuales.size());

                // Refrescamos la vista
                refrescarAmbasTablasNBU(padre);
            } else {
                vnbu.mostrarMensaje("Error al intentar vincular el componente a la práctica.");
            }
        }
    }

    public boolean vincularHijo(String codigoPadre, String codigoHijo) {
        String sql = "INSERT IGNORE INTO determinacion_componentes (codigo_padre, codigo_hijo) VALUES (?, ?)";
        try (java.sql.PreparedStatement ps = con.getConnection().prepareStatement(sql)) {
            ps.setString(1, codigoPadre.trim());
            ps.setString(2, codigoHijo.trim());
            int filas = ps.executeUpdate();

            // ── NUEVO: Le avisamos a la DB que el padre ahora tiene hijos ──
            String sqlUpdate = "UPDATE determinacion SET es_compuesta = 1 WHERE codigo = ?";
            try (java.sql.PreparedStatement ps2 = con.getConnection().prepareStatement(sqlUpdate)) {
                ps2.setString(1, codigoPadre.trim());
                ps2.executeUpdate();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private void moverPadreNBU(int direccion) {
        int filaActual = vnbu.getIndicePadreSeleccionado();
        if (filaActual < 0) return;
        
        int filaDestino = filaActual + direccion;
        
        String codActual = vnbu.getCodigoPadreSeleccionado();
        String codDestino = vnbu.getCodigoPadreFila(filaDestino);
        
        // Bloqueo de seguridad: No permitimos mover hacia títulos o fuera de la tabla
        if (codActual == null || codActual.isEmpty() || codDestino.isEmpty()) {
            return;
        }

        // Buscamos a los dos padres involucrados en la base de datos
        modelo.Determinacion detActual = determinacionDAO.buscarPorCodigo(codActual);
        modelo.Determinacion detDestino = determinacionDAO.buscarPorCodigo(codDestino);
        
        if (detActual != null && detDestino != null) {
            // Intercambiamos sus prioridades
            int prioActual = detActual.getPrioridad();
            int prioDestino = detDestino.getPrioridad();
            
            determinacionDAO.actualizarPrioridad(codActual, prioDestino);
            determinacionDAO.actualizarPrioridad(codDestino, prioActual);
            
            // Recargamos la tabla izquierda y volvemos a seleccionar la fila que viajó
            java.util.List<modelo.Determinacion> listaPadres = determinacionDAO.listarTodo();
            vnbu.cargarDeterminaciones(listaPadres);
            vnbu.seleccionarPadrePorIndice(filaDestino);
        }
    }

    private void refrescarAmbasTablasNBU(String codigoPadre) {
        // 1. Recargar la tabla izquierda completa (Padres e Hijos)
        // Usa el método que usas normalmente para cargar, asumo que es listarTodo()
        java.util.List<modelo.Determinacion> listaCompleta = determinacionDAO.listarTodo();
        vnbu.cargarDeterminaciones(listaCompleta);

        // 2. Volver a seleccionar al Padre para no perder el foco
        vnbu.seleccionarFilaPorCodigo(codigoPadre);

        // 3. Recargar la tabla derecha (Solo los hijos)
        seleccionarPadreNBU(codigoPadre);
    }
    
    private void refrescarTablaSeleccion() {
        // 1. ORDENAMIENTO INTELIGENTE 100% BASADO EN BD
        determinacionesSeleccionadas.sort((d1, d2) -> {
            String codPadre1 = d1.getCodigo().contains(".") ? d1.getCodigo().split("\\.")[0] : d1.getCodigo();
            String codPadre2 = d2.getCodigo().contains(".") ? d2.getCodigo().split("\\.")[0] : d2.getCodigo();

            if (!codPadre1.equals(codPadre2)) {
                modelo.Determinacion padre1 = determinacionDAO.buscarPorCodigo(codPadre1);
                modelo.Determinacion padre2 = determinacionDAO.buscarPorCodigo(codPadre2);
                int prioGlobal1 = (padre1 != null) ? padre1.getPrioridad() : 999;
                int prioGlobal2 = (padre2 != null) ? padre2.getPrioridad() : 999;

                if (prioGlobal1 != prioGlobal2) return Integer.compare(prioGlobal1, prioGlobal2);
                return codPadre1.compareTo(codPadre2);
            }
            return Integer.compare(d1.getPrioridad(), d2.getPrioridad());
        });

        // 2. INYECCIÓN DINÁMICA DE TÍTULOS
        this.listaVisualDeterminaciones.clear();
        String codigoPadreActual = "";

        for (Determinacion d : determinacionesSeleccionadas) {
            String codigoPadreFila = d.getCodigo().contains(".") ? d.getCodigo().split("\\.")[0] : d.getCodigo();

            if (!codigoPadreFila.equals(codigoPadreActual)) {
                Determinacion detPadre = determinacionDAO.buscarPorCodigo(codigoPadreFila);
                String nombreTitulo = (detPadre != null) ? detPadre.getNombre() : "ESTUDIO";
                this.listaVisualDeterminaciones.add(crearTituloVirtual("--- " + nombreTitulo + " ---"));
                codigoPadreActual = codigoPadreFila;
            }
            this.listaVisualDeterminaciones.add(d);
        }

        // 3. DIBUJAR EN PANTALLA
        vd.cargarTablaConTitulos(this.listaVisualDeterminaciones);
    }
    
    private void guardarCambiosPendientesHijos() {
        vnbu.detenerEdicionTabla(); // Detiene cualquier celda a medio escribir
        
        if (vnbu.getCodigoPadreSeleccionado() == null) {
            return; // Si no hay nada seleccionado, no hace nada
        }

        // Recorre la tabla visual y guarda todo silenciosamente en la BD
        for (int i = 0; i < vnbu.getCantidadFilas(); i++) {
            String codigoHijo = vnbu.getCodigoHijoFila(i); 
            String nombreHijo = vnbu.getNombreHijoFila(i); 
            String unidad = vnbu.getUnidad(i); 
            String ref = vnbu.getReferencia(i); 

            determinacionDAO.actualizarNombre(codigoHijo, nombreHijo);
            determinacionDAO.actualizarUnidadReferenciaPorCodigo(codigoHijo, unidad, ref);
        }
    }
}

