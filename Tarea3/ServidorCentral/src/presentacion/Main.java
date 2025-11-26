package presentacion;

import javax.swing.JFrame;
import javax.swing.JDesktopPane;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.JInternalFrame;

import logica.CargaDatosPrueba;
import logica.fabrica;
import logica.interfaces.IControladorEvento;
import logica.interfaces.IControladorUsuario;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Dimension;
import java.awt.Point;
import java.beans.PropertyVetoException;

import publicadores.PublicadorUsuario;
import publicadores.PublicadorEstadisticas;
import publicadores.PublicadorEvento;

import java.util.List;
import java.util.function.Supplier;
import java.util.function.Consumer;
import java.util.Arrays; 

public class Main {

    private static final Color P_BG_APP   = new Color(240, 248, 255);
    private static final Color P_MENU_BG  = new Color(230, 236, 246);
    private static final Color P_MENU_FG  = new Color(25, 25, 25);

    private JFrame frame;
    private JDesktopPane desktopPane;
    private IControladorUsuario icu;
    private IControladorEvento ice;

    // ==== NUEVO: referencia a la instancia publicada ====
    private PublicadorEstadisticas estadisticasSvc;

    // Lazy: se crean on-demand
    private AltaUsuarioFrame creUsrInternalFrame;
    private ConsultaUsuario conUsrInternalFrame;
    private ConsultaEventoFrame consultaEventoFrame;
    private ConsultaEdicionEventoFrame consultaEdicionEventoFrame;
    private ConsultaTipoRegistroFrame consultaTipoRegistroFrame;
    private ConsultaRegistroFrame consultaRegistroFrame;
    private ConsultaPatrocinioFrame consultaPatrocinioFrame;
    private AltaEventoFrame altaEventoFrame;
    private AltaTipoRegistroFrame altaTipoRegistroFrame;
    private AltaPatrocinioFrame altaPatrocinioFrame;
    private AltaInstitucionFrame altaInstitucionFrame;
    private RegistroEdicionEventoFrame registroEdicionEventoFrame;
    private AltaEdicionEvento altaEdicionEventoFrame;
    private ModificarDatosUsuarioFrame modificarDatosUsuarioFrame;
    private AceptarEdicionEventoFrame aceptarEdicionEventoFrame;

    // Nuevo: frame de estadísticas Top 5
    private TopEventosFrame topEventosFrame;

    public static void main(String[] args) {
    	 System.setProperty(
    		        "jakarta.xml.bind.context.factory",
    		        "org.glassfish.jaxb.runtime.v2.ContextFactory"
    		    );
        try {
            PublicadorUsuario publicadorUsuario = new PublicadorUsuario();
            publicadorUsuario.publicar();

            PublicadorEvento publicadorEvento = new PublicadorEvento();
            publicadorEvento.publicar();

            PublicadorEstadisticas publicadorEstadisticas = new PublicadorEstadisticas();
            publicadorEstadisticas.publicar();

            try {
                boolean puesto = false;
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Metal".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        puesto = true;
                        break;
                    }
                }
                if (!puesto) {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                }
            } catch (ClassNotFoundException
                    | InstantiationException
                    | IllegalAccessException
                    | javax.swing.UnsupportedLookAndFeelException ignore) {
                // Si falla el Look&Feel, seguimos con el default
            }

            EventQueue.invokeLater(() -> {
                Main window = new Main();
                // ==== NUEVO: inyectamos la instancia publicada ====
                window.setPublicadores(publicadorUsuario, publicadorEvento, publicadorEstadisticas);
                window.frame.setVisible(true);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Main() {
        initialize();
        icu = fabrica.getInstance().getIControladorUsuario();
        ice = fabrica.getInstance().getIControladorEvento();
    }

    // ==== NUEVO: setter para guardar la instancia publicada ====
    public void setPublicadores(PublicadorUsuario pu, PublicadorEvento pe, PublicadorEstadisticas pes) {
        this.estadisticasSvc = pes;
    }

    private void initialize() {
        frame = new JFrame("Eventos.uy");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setMinimumSize(new Dimension(1000, 700));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        desktopPane = new JDesktopPane();
        desktopPane.setBackground(P_BG_APP);
        frame.add(desktopPane, BorderLayout.CENTER);

        JMenuBar menuBar = new JMenuBar();
        menuBar.setOpaque(true);
        menuBar.setBackground(P_MENU_BG);
        menuBar.setForeground(P_MENU_FG);
        frame.setJMenuBar(menuBar);

        JMenu menuSistema = new JMenu("Sistema");
        styleMenu(menuSistema);
        menuBar.add(menuSistema);

        JMenu menuUsuario = new JMenu("Usuario");
        styleMenu(menuUsuario);
        menuBar.add(menuUsuario);

        JMenu menuEvento = new JMenu("Evento");
        styleMenu(menuEvento);
        menuBar.add(menuEvento);

        // ===== Sistema
        JMenuItem itemCargaDatos = new JMenuItem("Cargar Datos Iniciales");
        styleMenuItem(itemCargaDatos);
        menuSistema.add(itemCargaDatos);
        itemCargaDatos.addActionListener(e -> {
            CargaDatosPrueba.cargar();
            JOptionPane.showMessageDialog(frame, "Datos de prueba cargados correctamente.",
                    "Carga completa", JOptionPane.INFORMATION_MESSAGE);
        });

        // ===== Usuario
        JMenuItem itemAltaUsuario = new JMenuItem("Alta de Usuario");
        styleMenuItem(itemAltaUsuario);
        menuUsuario.add(itemAltaUsuario);
        itemAltaUsuario.addActionListener(e -> {
            if (creUsrInternalFrame == null || creUsrInternalFrame.isClosed()) {
                creUsrInternalFrame = new AltaUsuarioFrame(icu, ice);
                desktopPane.add(creUsrInternalFrame);
            }
            creUsrInternalFrame.cargarInstituciones();
            showCentered(creUsrInternalFrame);
        });

        JMenuItem itemConsultaUsuario = new JMenuItem("Consulta de Usuario");
        styleMenuItem(itemConsultaUsuario);
        menuUsuario.add(itemConsultaUsuario);
        itemConsultaUsuario.addActionListener(e -> {
            if (conUsrInternalFrame == null || conUsrInternalFrame.isClosed()) {
                conUsrInternalFrame = new ConsultaUsuario(icu, ice);
                desktopPane.add(conUsrInternalFrame);
            }
            conUsrInternalFrame.cargarUsuarios();
            showCentered(conUsrInternalFrame);
        });

        JMenuItem itemAltaInstitucion = new JMenuItem("Alta de Institución");
        styleMenuItem(itemAltaInstitucion);
        menuUsuario.add(itemAltaInstitucion);
        itemAltaInstitucion.addActionListener(e -> {
            if (altaInstitucionFrame == null || altaInstitucionFrame.isClosed()) {
                altaInstitucionFrame = new AltaInstitucionFrame(icu, ice);
                desktopPane.add(altaInstitucionFrame);
            }
            showCentered(altaInstitucionFrame);
        });

        JMenuItem itemModificarUsuario = new JMenuItem("Modificar Datos de Usuario");
        styleMenuItem(itemModificarUsuario);
        menuUsuario.add(itemModificarUsuario);
        itemModificarUsuario.addActionListener(e -> {
            java.util.Map<String, logica.clases.Usuario> usuariosMap = icu.listarUsuarios();
            String[] usuarios = usuariosMap.keySet().toArray(new String[0]);
            String[][] datosUsuarios = new String[usuarios.length][7];
            for (int i = 0; i < usuarios.length; i++) {
                logica.clases.Usuario usuario = usuariosMap.get(usuarios[i]);
                datosUsuarios[i][0] = usuario.getNickname();
                datosUsuarios[i][1] = usuario.getEmail();
                datosUsuarios[i][2] = usuario.getNombre();
                if (usuario instanceof logica.clases.Asistente asistente) {
                    datosUsuarios[i][3] = asistente.getApellido() != null ? asistente.getApellido() : "";
                    datosUsuarios[i][4] = asistente.getFechaDeNacimiento() != null ? asistente.getFechaDeNacimiento().toString() : "";
                    datosUsuarios[i][5] = "";
                    datosUsuarios[i][6] = asistente.getInstitucion() != null ? asistente.getInstitucion().getNombre() : "";
                } else if (usuario instanceof logica.clases.Organizador organizador) {
                    datosUsuarios[i][3] = "";
                    datosUsuarios[i][4] = "";
                    datosUsuarios[i][5] = organizador.getDesc() != null ? organizador.getDesc() : "";
                    datosUsuarios[i][6] = organizador.getLink() != null ? organizador.getLink() : "";
                } else {
                    datosUsuarios[i][3] = "";
                    datosUsuarios[i][4] = "";
                    datosUsuarios[i][5] = "";
                    datosUsuarios[i][6] = "";
                }
            }
            if (modificarDatosUsuarioFrame == null || modificarDatosUsuarioFrame.isClosed()) {
                modificarDatosUsuarioFrame = new ModificarDatosUsuarioFrame(icu, usuarios, datosUsuarios);
                desktopPane.add(modificarDatosUsuarioFrame);
            }
            showCentered(modificarDatosUsuarioFrame);
        });

        // ===== Evento
        JMenuItem itemConsultaEvento = new JMenuItem("Consulta de Evento");
        styleMenuItem(itemConsultaEvento);
        menuEvento.add(itemConsultaEvento);
        itemConsultaEvento.addActionListener(e -> {
            if (consultaEventoFrame == null || consultaEventoFrame.isClosed()) {
                consultaEventoFrame = new ConsultaEventoFrame(icu, ice);
                desktopPane.add(consultaEventoFrame);
            }
            consultaEventoFrame.cargarEventos();
            showCentered(consultaEventoFrame);
        });

        JMenuItem itemConsultaEdicion = new JMenuItem("Consulta de Edición de Evento");
        styleMenuItem(itemConsultaEdicion);
        menuEvento.add(itemConsultaEdicion);
        itemConsultaEdicion.addActionListener(e -> {
            if (consultaEdicionEventoFrame == null || consultaEdicionEventoFrame.isClosed()) {
                consultaEdicionEventoFrame = new ConsultaEdicionEventoFrame(icu, ice);
                desktopPane.add(consultaEdicionEventoFrame);
            }
            consultaEdicionEventoFrame.cargarEventos();
            showCentered(consultaEdicionEventoFrame);
        });

        JMenuItem itemConsultaTipoRegistro = new JMenuItem("Consulta de Tipo de Registro");
        styleMenuItem(itemConsultaTipoRegistro);
        menuEvento.add(itemConsultaTipoRegistro);
        itemConsultaTipoRegistro.addActionListener(e -> {
            if (consultaTipoRegistroFrame == null || consultaTipoRegistroFrame.isClosed()) {
                consultaTipoRegistroFrame = new ConsultaTipoRegistroFrame(icu, ice);
                desktopPane.add(consultaTipoRegistroFrame);
            }
            consultaTipoRegistroFrame.cargarEventos();
            showCentered(consultaTipoRegistroFrame);
        });

        JMenuItem itemConsultaRegistro = new JMenuItem("Consulta de Registro");
        styleMenuItem(itemConsultaRegistro);
        menuUsuario.add(itemConsultaRegistro);
        itemConsultaRegistro.addActionListener(e -> {
            if (consultaRegistroFrame == null || consultaRegistroFrame.isClosed()) {
                consultaRegistroFrame = new ConsultaRegistroFrame(icu, ice);
                desktopPane.add(consultaRegistroFrame);
            }
            consultaRegistroFrame.cargarAsistentes();
            showCentered(consultaRegistroFrame);
        });

        JMenuItem itemConsultaPatrocinio = new JMenuItem("Consulta de Patrocinio");
        styleMenuItem(itemConsultaPatrocinio);
        menuEvento.add(itemConsultaPatrocinio);
        itemConsultaPatrocinio.addActionListener(e -> {
            if (consultaPatrocinioFrame == null || consultaPatrocinioFrame.isClosed()) {
                consultaPatrocinioFrame = new ConsultaPatrocinioFrame(icu, ice);
                desktopPane.add(consultaPatrocinioFrame);
            }
            consultaPatrocinioFrame.cargarDatos();
            showCentered(consultaPatrocinioFrame);
        });

        JMenuItem itemAltaEvento = new JMenuItem("Alta de Evento");
        styleMenuItem(itemAltaEvento);
        menuEvento.add(itemAltaEvento);
        itemAltaEvento.addActionListener(e -> {
            if (altaEventoFrame == null || altaEventoFrame.isClosed()) {
                altaEventoFrame = new AltaEventoFrame(icu, ice);
                desktopPane.add(altaEventoFrame);
            }
            altaEventoFrame.cargarCategorias();
            showCentered(altaEventoFrame);
        });

        JMenuItem itemAltaTipoRegistro = new JMenuItem("Alta de Tipo de Registro");
        styleMenuItem(itemAltaTipoRegistro);
        menuEvento.add(itemAltaTipoRegistro);
        itemAltaTipoRegistro.addActionListener(e -> {
            if (altaTipoRegistroFrame == null || altaTipoRegistroFrame.isClosed()) {
                altaTipoRegistroFrame = new AltaTipoRegistroFrame(icu, ice);
                desktopPane.add(altaTipoRegistroFrame);
            }
            altaTipoRegistroFrame.cargarEventos();
            showCentered(altaTipoRegistroFrame);
        });

        JMenuItem itemAltaPatrocinio = new JMenuItem("Alta de Patrocinio");
        styleMenuItem(itemAltaPatrocinio);
        menuEvento.add(itemAltaPatrocinio);
        itemAltaPatrocinio.addActionListener(e -> {
            if (altaPatrocinioFrame == null || altaPatrocinioFrame.isClosed()) {
                altaPatrocinioFrame = new AltaPatrocinioFrame(icu, ice);
                desktopPane.add(altaPatrocinioFrame);
            }
            altaPatrocinioFrame.cargarDatos();
            showCentered(altaPatrocinioFrame);
        });

        JMenuItem itemAltaEdicionEvento = new JMenuItem("Alta de Edición de Evento");
        styleMenuItem(itemAltaEdicionEvento);
        menuEvento.add(itemAltaEdicionEvento);
        itemAltaEdicionEvento.addActionListener(e -> {
            if (altaEdicionEventoFrame == null || altaEdicionEventoFrame.isClosed()) {
                altaEdicionEventoFrame = new AltaEdicionEvento(icu, ice);
                desktopPane.add(altaEdicionEventoFrame);
            }
            altaEdicionEventoFrame.cargarEventos();
            altaEdicionEventoFrame.cargarOrganizadores();
            showCentered(altaEdicionEventoFrame);
        });

        JMenuItem itemRegistroEdicionEvento = new JMenuItem("Registro/Edición de Evento");
        styleMenuItem(itemRegistroEdicionEvento);
        menuUsuario.add(itemRegistroEdicionEvento);
        itemRegistroEdicionEvento.addActionListener(e -> {
            if (registroEdicionEventoFrame == null || registroEdicionEventoFrame.isClosed()) {
                registroEdicionEventoFrame = new RegistroEdicionEventoFrame(icu, ice);
                desktopPane.add(registroEdicionEventoFrame);
            }
            registroEdicionEventoFrame.cargarDatos();
            showCentered(registroEdicionEventoFrame);
        });

        JMenuItem itemAceptarEdicion = new JMenuItem("Aceptar/Rechazar Edición de Evento");
        styleMenuItem(itemAceptarEdicion);
        menuEvento.add(itemAceptarEdicion);
        itemAceptarEdicion.addActionListener(e -> {
            if (aceptarEdicionEventoFrame == null || aceptarEdicionEventoFrame.isClosed()) {
                aceptarEdicionEventoFrame = new AceptarEdicionEventoFrame(ice);
                desktopPane.add(aceptarEdicionEventoFrame);
            }
            aceptarEdicionEventoFrame.cargarEventos();
            showCentered(aceptarEdicionEventoFrame);
        });

        // ===== Nuevo menú: Estadísticas → Top 5
        JMenu menuEstadisticas = new JMenu("Estadísticas");
        styleMenu(menuEstadisticas);
        menuBar.add(menuEstadisticas);

        JMenuItem itemTopEventos = new JMenuItem("Top 5 eventos más visitados");
        styleMenuItem(itemTopEventos);
        menuEstadisticas.add(itemTopEventos);

        itemTopEventos.addActionListener(e -> {
            // usa la MISMA instancia publicada 
            Supplier<List<logica.datatypes.DTTopEvento>> loader = () -> {
                try {
                    logica.datatypes.DTTopEvento[] arr = (estadisticasSvc == null)
                            ? null
                            : estadisticasSvc.topEventos(5);
                    return (arr == null) ? List.of() : Arrays.asList(arr);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return List.of();
                }
            };

         // Navegación: abrir Consulta de Evento para el nombre elegido
            Consumer<String> openEventoByName = (String nombreEvento) -> {
                try {
                    if (consultaEventoFrame == null || consultaEventoFrame.isClosed()) {
                        consultaEventoFrame = new ConsultaEventoFrame(icu, ice);
                        desktopPane.add(consultaEventoFrame);
                    }

                    // Aseguramos la carga y lo mostramos
                    consultaEventoFrame.cargarEventos();
                    showCentered(consultaEventoFrame);

                    // Intentar preseleccionar por nombre (reflection para no acoplar)
                    String[] candidateMethods = new String[] {
                        "preseleccionarPorNombre",
                        "seleccionarPorNombre",
                        "mostrarEventoPorNombre",
                        "preseleccionar",         // (String)
                        "seleccionarEvento",      // (String)
                        "seleccionar"             // (String)
                    };

                    boolean invoked = false;
                    for (String mName : candidateMethods) {
                        try {
                            var m = consultaEventoFrame.getClass().getMethod(mName, String.class);
                            m.setAccessible(true);
                            m.invoke(consultaEventoFrame, nombreEvento);
                            invoked = true;
                            break;
                        } catch (NoSuchMethodException ignore) {
                            // probá el siguiente nombre
                        }
                    }

                    if (!invoked) {
                        // Si tu frame no tiene aún un método de selección, avisamos
                        JOptionPane.showMessageDialog(
                            frame,
                            "Abrí la consulta de evento.\nAñadí un método en ConsultaEventoFrame para preseleccionar por nombre,\npor ejemplo: preseleccionarPorNombre(\"" + nombreEvento + "\").",
                            "Seleccionar evento",
                            JOptionPane.INFORMATION_MESSAGE
                        );
                    }

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame,
                        "No se pudo abrir la consulta del evento:\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            };

            if (topEventosFrame == null || topEventosFrame.isClosed()) {
                topEventosFrame = new TopEventosFrame(loader, openEventoByName);
                desktopPane.add(topEventosFrame);
            }
            showCentered(topEventosFrame);
        });
    }

    private void styleMenu(JMenu menu) {
        menu.setOpaque(true);
        menu.setBackground(P_MENU_BG);
        menu.setForeground(P_MENU_FG);
    }

    private void styleMenuItem(JMenuItem menuItem) {
        menuItem.setOpaque(true);
        menuItem.setBackground(P_MENU_BG);
        menuItem.setForeground(P_MENU_FG);
    }

    // ===== Helpers de posicionamiento =====
    private void showCentered(JInternalFrame framee) {
        Dimension dimension = desktopPane.getSize();
        int ancho = Math.max((int) (dimension.width * 0.8), 800);
        int altura = Math.max((int) (dimension.height * 0.8), 500);
        framee.setSize(new Dimension(ancho, altura));
        framee.setLocation(new Point((dimension.width - ancho) / 2, (dimension.height - altura) / 2));
        framee.setVisible(true);
        try {
            framee.setSelected(true);
        } catch (PropertyVetoException ignore) {
            // Si el frame veta el foco, lo dejamos visible sin foco
        }
        framee.toFront();
    }
}
