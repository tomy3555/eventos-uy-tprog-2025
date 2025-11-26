package logica;

import excepciones.CostoTipoRegistroInvalidoException;
import excepciones.CupoTipoRegistroInvalidoException;
import excepciones.EdicionYaExisteException;
import excepciones.EventoYaExisteException;
import excepciones.InstitucionYaExisteException;
import excepciones.TipoRegistroYaExisteException;
import excepciones.UsuarioYaExisteException;
import excepciones.ValorPatrocinioExcedidoException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import logica.clases.Ediciones;
import logica.clases.Eventos;
import logica.datatypes.DTCategorias;
import logica.enumerados.DTEstado;
import logica.enumerados.DTNivel;
import logica.manejadores.ManejadorEvento;

/**
 * Carga de datos hardcodeada (estilo original) usando los datos nuevos
 * e incluyendo el ESTADO en las ediciones.
 */
public class CargaDatosPrueba {

	public static void cargar() {
	    try {
	        cargarCategorias();
	        cargarInstitucionesEjemplo();
	        cargarEventosEjemplo();
	        cargarUsuariosEjemplo();
	        cargarEdicionesEjemplo();
	        cargarTipoRegistroEjemplo();
	        cargarRegistrosEjemplo();
	        cargarPatrociniosEjemplo();
	        logResumenDatos();
	    } catch (InstitucionYaExisteException
	           | excepciones.EventoYaExisteException
	           | excepciones.UsuarioYaExisteException
	           | excepciones.TipoRegistroYaExisteException
	           | excepciones.CupoTipoRegistroInvalidoException
	           | excepciones.CostoTipoRegistroInvalidoException
	           | excepciones.ValorPatrocinioExcedidoException | EdicionYaExisteException ex) {
	        throw new IllegalStateException("Error al cargar datos de prueba: " + ex.getMessage(), ex);
	    }
	}


	public static void logResumenDatos() { }
	

    

    // --- Utilidades ---
    private static LocalDate parseFecha(String fecha) {
        String[] fechaPar = fecha.split("/");
        return LocalDate.of(Integer.parseInt(fechaPar[2]), Integer.parseInt(fechaPar[1]), Integer.parseInt(fechaPar[0]));
    }

    // =========================
    // CATEGORÍAS
    // =========================
    public static void cargarCategorias() {
        var controladorEv = new logica.controladores.ControladorEvento();
        controladorEv.altaCategoria("Tecnología");
        controladorEv.altaCategoria("Innovación");
        controladorEv.altaCategoria("Literatura");
        controladorEv.altaCategoria("Cultura");
        controladorEv.altaCategoria("Música");
        controladorEv.altaCategoria("Deporte");
        controladorEv.altaCategoria("Salud");
        controladorEv.altaCategoria("Entretenimiento");
        controladorEv.altaCategoria("Agro");
        controladorEv.altaCategoria("Negocios");
        controladorEv.altaCategoria("Moda");
        controladorEv.altaCategoria("Investigación");
    }

    // =========================
    // INSTITUCIONES
    // =========================
    public static void cargarInstitucionesEjemplo() throws InstitucionYaExisteException {
        var controladorUsu = new logica.controladores.ControladorUsuario();
        controladorUsu.altaInstitucion("Facultad de Ingeniería", "Facultad de Ingeniería de la Universidad de la República", "https://www.fing.edu.uy");
        controladorUsu.altaInstitucion("ORT Uruguay", "Universidad privada enfocada en tecnología y gestión", "https://ort.edu.uy");
        controladorUsu.altaInstitucion("Universidad Católica del Uruguay", "Institución de educación superior privada", "https://ucu.edu.uy");
        controladorUsu.altaInstitucion("Antel", "Empresa estatal de telecomunicaciones", "https://antel.com.uy");
        controladorUsu.altaInstitucion("Agencia Nacional de Investigación e Innovación (ANII)", "Fomenta la investigación y la innovación en Uruguay", "https://anii.org.uy");
    }

    // =========================
    // EVENTOS
    // =========================
    public static void cargarEventosEjemplo() throws EventoYaExisteException {
        var controladorEve = new logica.controladores.ControladorEvento();

        List<String> catEv01 = Arrays.asList("Tecnología", "Innovación");
        List<String> catEv02 = Arrays.asList("Literatura", "Cultura");
        List<String> catEv03 = Arrays.asList("Cultura", "Música");
        List<String> catEv04 = Arrays.asList("Deporte", "Salud");
        List<String> catEv05 = Arrays.asList("Cultura", "Entretenimiento");
        List<String> catEv06 = Arrays.asList("Agro", "Negocios");
        List<String> catEv07 = Arrays.asList("Cultura", "Moda");

        // Ref 2025 — Conferencia de Tecnología
        controladorEve.altaEvento(
            "Conferencia de Tecnología",
            "Evento sobre innovación tecnológica",
            LocalDate.of(2025, 1, 10),
            "CONFTEC",
            new DTCategorias(catEv01),
            null
        );

        // Ref 2024 — Feria del Libro
        controladorEve.altaEvento(
            "Feria del Libro",
            "Encuentro anual de literatura",
            LocalDate.of(2025, 2, 1),
            "FERLIB",
            new DTCategorias(catEv02),
            "IMG-EV02.jpeg"
        );

        // Ref 2023 — Montevideo Rock
        controladorEve.altaEvento(
            "Montevideo Rock",
            "Festival de rock con artistas nacionales e internacionales",
            LocalDate.of(2023, 3, 15),
            "MONROCK",
            new DTCategorias(catEv03),
            "IMG-EV03.jpeg"
        );

        // Ref 2022 — Maratón de Montevideo
        controladorEve.altaEvento(
            "Maratón de Montevideo",
            "Competencia deportiva anual en la capital",
            LocalDate.of(2022, 1, 1),
            "MARATON",
            new DTCategorias(catEv04),
            "IMG-EV04.png"
        );

        // Ref 2024b — Montevideo Comics
        controladorEve.altaEvento(
            "Montevideo Comics",
            "Convención de historietas, cine y cultura geek",
            LocalDate.of(2024, 4, 10),
            "COMICS",
            new DTCategorias(catEv05),
            "IMG-EV05.png"
        );

        // Ref 2024c — Expointer Uruguay
        controladorEve.altaEvento(
            "Expointer Uruguay",
            "Exposición internacional agropecuaria y ganadera",
            LocalDate.of(2024, 12, 12),
            "EXPOAGRO",
            new DTCategorias(catEv06),
            "IMG-EV06.png"
        );

        // Ref 2025b — Montevideo Fashion Week
        controladorEve.altaEvento(
            "Montevideo Fashion Week",
            "Pasarela de moda uruguaya e internacional",
            LocalDate.of(2025, 7, 20),
            "MFASHION",
            new DTCategorias(catEv07),
            null
        );
    }

    // =========================
    // USUARIOS
    // =========================
    public static void cargarUsuariosEjemplo() throws UsuarioYaExisteException {
        var controladorUsu = new logica.controladores.ControladorUsuario();

        // Asistentes
        controladorUsu.altaUsuario("atorres", "Ana", "atorres@gmail.com", null, null, "Torres",
            LocalDate.of(1990, 5, 12), "Facultad de Ingeniería", false, "123.torres", "IMG-US01.jpg");

        controladorUsu.altaUsuario("msilva", "Martin", "martin.silva@fing.edu.uy", null, null, "Silva",
            LocalDate.of(1987, 8, 21), "Facultad de Ingeniería", false, "msilva2025", "IMG-US02.jpg");

        controladorUsu.altaUsuario("sofirod", "Sofia", "srodriguez@outlook.com", null, null, "Rodriguez",
            LocalDate.of(1995, 2, 3), "Universidad Católica del Uruguay", false, "srod.abc1", "IMG-US03.jpeg");

        controladorUsu.altaUsuario("vale23", "Valentina", "valentina.costa@mail.com", null, null, "Costa",
            LocalDate.of(1992, 12, 1), null, false, "valen11c", "IMG-US07.jpeg");

        controladorUsu.altaUsuario("luciag", "Lucía", "lucia.garcia@mail.com", null, null, "García",
            LocalDate.of(1993, 11, 9), null, false, "garcia.22l", "IMG-US08.jpeg");

        controladorUsu.altaUsuario("andrearod", "Andrea", "andrea.rod@mail.com", null, null, "Rodríguez",
            LocalDate.of(2000, 6, 10), "Agencia Nacional de Investigación e Innovación (ANII)", false, "rod77and", "IMG-US09.jpeg");

        controladorUsu.altaUsuario("AnaG", "Ana", "ana.gomez@hotmail.com", null, null, "Gómez",
            LocalDate.of(1998, 3, 15), null, false, "gomez88a", "IMG-US12.png");

        controladorUsu.altaUsuario("JaviL", "Javier", "javier.lopez@outlook.com", null, null, "López",
            LocalDate.of(1995, 7, 22), null, false, "jl99lopez", "IMG-US13.jpeg");

        controladorUsu.altaUsuario("MariR", "María", "maria.rodriguez@gmail.com", null, null, "Rodríguez",
            LocalDate.of(2000, 11, 10), null, false, "maria55r", "IMG-US14.jpeg");

        controladorUsu.altaUsuario("SofiM", "Sofía", "sofia.martinez@yahoo.com", null, null, "Martínez",
            LocalDate.of(1997, 2, 5), null, false, "smarti99z", "IMG-US15.jpeg");

        // Organizadores
        controladorUsu.altaUsuario("miseventos", "MisEventos", "contacto@miseventos.com",
            "Empresa de organización de eventos.", "https://miseventos.com", null, null, null, true, "22miseventos", "IMG-US04.jpeg");

        controladorUsu.altaUsuario("techcorp", "Corporación Tecnológica", "info@techcorp.com",
            "Empresa líder en tecnologías de la información.", null, null, null, null, true, "tech25corp", "IMG-US05.jpeg");

        controladorUsu.altaUsuario("imm", "Intendencia de Montevideo", "contacto@imm.gub.uy",
            "Gobierno departamental de Montevideo.", "https://montevideo.gub.uy", null, null, null, true, "imm2025", "IMG-US06.png");

        controladorUsu.altaUsuario("udelar", "Universidad de la República", "contacto@udelar.edu.uy",
            "Universidad pública de Uruguay.", "https://udelar.edu.uy", null, null, null, true, "25udelar", "IMG-US10.jpeg");

        controladorUsu.altaUsuario("mec", "Ministerio de Educación y Cultura", "mec@mec.gub.uy",
            "Institución pública promotora de cultura.", "https://mec.gub.uy", null, null, null, true, "mec2025ok", "IMG-US11.png");
    }

    // =========================
    // EDICIONES (con ESTADO)
    // =========================
    public static void cargarEdicionesEjemplo() throws EdicionYaExisteException {
        var controladorEve = new logica.controladores.ControladorEvento();
        var manejadorEve = ManejadorEvento.getInstancia();
        var manejadorUsu = logica.manejadores.ManejadorUsuario.getInstancia();

        // Helper: crea la edición y le setea estado (si tu Controlador no recibe estado)
        java.util.function.BiConsumer<String, DTEstado> setEstadoEdicion = (sigla, estado) -> {
            Ediciones edicionIter = manejadorEve.obtenerEdicion(sigla);
            if (edicionIter != null) edicionIter.setEstado(estado);
        };

        // 1) Montevideo Rock 2025 (evento: Montevideo Rock) — Estado: Aceptada
        {
            Eventos eventoIter = manejadorEve.obtenerEvento("Montevideo Rock");
            if (eventoIter != null) {
                try {
                	controladorEve.altaEdicionEvento(
                            eventoIter,
                            manejadorUsu.getUsuarios().get("imm"),
                            "Montevideo Rock 2025",
                            "MONROCK25",
                            "",
                            parseFecha("20/11/2025"),
                            parseFecha("22/11/2025"),
                            parseFecha("12/03/2025"),
                            "Montevideo",
                            "Uruguay",
                            "IMG-EDEV01.jpeg"
                        );
                        setEstadoEdicion.accept("MONROCK25", DTEstado.Aceptada);
                }catch (excepciones.EventoYaExisteException | excepciones.FechasCruzadasException ex) {
                    System.err.println("No se pudo crear la edición 'Montevideo Rock 2025': " + ex.getMessage());
                }
            	
            }
        }

        // 2) Maratón de Montevideo (2025/2024/2022)
        {
            Eventos eventoIter = manejadorEve.obtenerEvento("Maratón de Montevideo");
            if (eventoIter != null) {
                try {
                // 2025 — Aceptada
                controladorEve.altaEdicionEvento(
                    eventoIter, manejadorUsu.getUsuarios().get("imm"),
                    "Maratón de Montevideo 2025", "MARATON25", "",
                    parseFecha("14/09/2025"), parseFecha("14/09/2025"), parseFecha("05/02/2025"),
                    "Montevideo", "Uruguay", "IMG-EDEV02.png"
                );
                setEstadoEdicion.accept("MARATON25", DTEstado.Aceptada);

                // 2024 — Rechazada
                controladorEve.altaEdicionEvento(
                    eventoIter, manejadorUsu.getUsuarios().get("imm"),
                    "Maratón de Montevideo 2024", "MARATON24", "",
                    parseFecha("14/09/2024"), parseFecha("14/09/2024"), parseFecha("21/04/2024"),
                    "Montevideo", "Uruguay", "IMG-EDEV03.jpeg"
                );
                setEstadoEdicion.accept("MARATON24", DTEstado.Aceptada);

                // 2022 — Ingresada
                controladorEve.altaEdicionEvento(
                    eventoIter, manejadorUsu.getUsuarios().get("imm"),
                    "Maratón de Montevideo 2022", "MARATON22", "",
                    parseFecha("14/09/2022"), parseFecha("14/09/2022"), parseFecha("21/05/2022"),
                    "Montevideo", "Uruguay", "IMG-EDEV04.jpeg"
                );
                setEstadoEdicion.accept("MARATON22", DTEstado.Rechazada);
                }catch (excepciones.EventoYaExisteException | excepciones.FechasCruzadasException ex) {
                    System.err.println("No se pudo crear la edición 'Montevideo Rock 2025': " + ex.getMessage());
                }
            }
        }

        // 3) Montevideo Comics (2024/2025)
        {
            Eventos eventoIter = manejadorEve.obtenerEvento("Montevideo Comics");
            if (eventoIter != null) {
                try {
                	// 2024 — Aceptada
                
                controladorEve.altaEdicionEvento(
                    eventoIter, manejadorUsu.getUsuarios().get("miseventos"),
                    "Montevideo Comics 2024", "COMICS24", "",
                    parseFecha("18/07/2024"), parseFecha("21/07/2024"), parseFecha("20/06/2024"),
                    "Montevideo", "Uruguay", "IMG-EDEV05.jpeg"
                );
                setEstadoEdicion.accept("COMICS24", DTEstado.Aceptada);

                // 2025 — Ingresada
                controladorEve.altaEdicionEvento(
                    eventoIter, manejadorUsu.getUsuarios().get("miseventos"),
                    "Montevideo Comics 2025", "COMICS25", "",
                    parseFecha("04/08/2025"), parseFecha("06/08/2025"), parseFecha("04/07/2025"),
                    "Montevideo", "Uruguay", "IMG-EDEV06.jpeg"
                );
                setEstadoEdicion.accept("COMICS25", DTEstado.Aceptada);
            }catch (excepciones.EventoYaExisteException | excepciones.FechasCruzadasException ex) {
                System.err.println("No se pudo crear la edición 'Montevideo Rock 2025': " + ex.getMessage());
            }
            }      
        }

        // 4) Expointer Uruguay 2025 — Aceptada
        {
            Eventos eventoIter = manejadorEve.obtenerEvento("Expointer Uruguay");
            if (eventoIter != null) {
                try {
                	controladorEve.altaEdicionEvento(
                            eventoIter, manejadorUsu.getUsuarios().get("miseventos"),
                            "Expointer Uruguay 2025", "EXPOAGRO25", "",
                            parseFecha("11/09/2025"), parseFecha("17/09/2025"), parseFecha("01/02/2025"),
                            "Durazno", "Uruguay", "IMG-EDEV07.jpeg"
                        );
                        setEstadoEdicion.accept("EXPOAGRO25", DTEstado.Ingresada);
                }catch (excepciones.EventoYaExisteException | excepciones.FechasCruzadasException ex) {
                    System.err.println("No se pudo crear la edición 'Montevideo Rock 2025': " + ex.getMessage());
                }

            }
        }

        // 5) Conferencia de Tecnología (ediciones futuras/otras marcas)
        {
            Eventos eventoIter = manejadorEve.obtenerEvento("Conferencia de Tecnología");
            if (eventoIter != null) {
                try {
                	// CONFTECH26 — Aceptada
                    controladorEve.altaEdicionEvento(
                        eventoIter, manejadorUsu.getUsuarios().get("udelar"),
                        "Tecnología Punta del Este 2026", "CONFTECH26", "",
                        parseFecha("06/04/2026"), parseFecha("10/04/2026"), parseFecha("01/08/2025"),
                        "Punta del Este", "Uruguay", "IMG-EDEV08.jpeg"
                    );
                    setEstadoEdicion.accept("CONFTECH26", DTEstado.Aceptada);

                    // MWC — Ingresada
                    controladorEve.altaEdicionEvento(
                        eventoIter, manejadorUsu.getUsuarios().get("techcorp"),
                        "Mobile World Congress 2025", "MWC", "",
                        parseFecha("12/12/2025"), parseFecha("15/12/2025"), parseFecha("21/08/2025"),
                        "Barcelona", "España", null
                    );
                    setEstadoEdicion.accept("MWC", DTEstado.Aceptada);

                    // WS26 — Ingresada
                    controladorEve.altaEdicionEvento(
                        eventoIter, manejadorUsu.getUsuarios().get("techcorp"),
                        "Web Summit 2026", "WS26", "",
                        parseFecha("13/01/2026"), parseFecha("01/02/2026"), parseFecha("04/06/2025"),
                        "Lisboa", "Portugal", null
                    );
                    setEstadoEdicion.accept("WS26", DTEstado.Aceptada);
                }catch (excepciones.EventoYaExisteException | excepciones.FechasCruzadasException ex) {
                    System.err.println("No se pudo crear la edición 'Montevideo Rock 2025': " + ex.getMessage());
                }

            }
        }
        
        {
            Eventos evento = manejadorEve.obtenerEvento("Montevideo Fashion Week");
            if (evento != null) {
                try {
                	controladorEve.altaEdicionEvento(
                            evento,
                            manejadorUsu.getUsuarios().get("mec"),
                            "Montevideo Fashion Week 2026",
                            "MFW26",
                            "",
                            parseFecha("16/02/2026"),
                            parseFecha("20/02/2026"),
                            parseFecha("02/10/2025"),
                            "Nueva York",
                            "Estados Unidos",
                            "IMG-EDEV11.jpeg"
                        );
                        setEstadoEdicion.accept("MFW26", DTEstado.Ingresada);
                }catch (excepciones.EventoYaExisteException | excepciones.FechasCruzadasException ex) {
                    System.err.println("No se pudo crear la edición 'Montevideo Fashion Week 2026': " + ex.getMessage());
                }
            	
            }
        }
    }

    // =========================
    // TIPOS DE REGISTRO
    // =========================
    public static void cargarTipoRegistroEjemplo() throws TipoRegistroYaExisteException, CupoTipoRegistroInvalidoException, CostoTipoRegistroInvalidoException {
        var controladorEve = new logica.controladores.ControladorEvento();
        var manejadorEve = ManejadorEvento.getInstancia();

        controladorEve.altaTipoRegistro(manejadorEve.obtenerEdicion("MONROCK25"), "General", "Acceso general a Montevideo Rock (2 días)", 1500, 2000);
        controladorEve.altaTipoRegistro(manejadorEve.obtenerEdicion("MONROCK25"), "VIP", "Incluye backstage + acceso preferencial", 4000, 200);

        controladorEve.altaTipoRegistro(manejadorEve.obtenerEdicion("MARATON25"), "Corredor 42K", "Inscripción a la maratón completa", 1200, 499);
        controladorEve.altaTipoRegistro(manejadorEve.obtenerEdicion("MARATON25"), "Corredor 21K", "Inscripción a la media maratón", 800, 700);
        controladorEve.altaTipoRegistro(manejadorEve.obtenerEdicion("MARATON25"), "Corredor 10K", "Inscripción a la carrera 10K", 500, 1000);

        controladorEve.altaTipoRegistro(manejadorEve.obtenerEdicion("MARATON24"), "Corredor 42K", "Inscripción a la maratón completa", 1000, 300);
        controladorEve.altaTipoRegistro(manejadorEve.obtenerEdicion("MARATON24"), "Corredor 21K", "Inscripción a la media maratón", 500, 500);

        controladorEve.altaTipoRegistro(manejadorEve.obtenerEdicion("MARATON22"), "Corredor 42K", "Inscripción a la maratón completa", 1100, 450);
        controladorEve.altaTipoRegistro(manejadorEve.obtenerEdicion("MARATON22"), "Corredor 21K", "Inscripción a la media maratón", 900, 750);
        controladorEve.altaTipoRegistro(manejadorEve.obtenerEdicion("MARATON22"), "Corredor 10K", "Inscripción a la carrera 10K", 650, 1400);

        controladorEve.altaTipoRegistro(manejadorEve.obtenerEdicion("COMICS24"), "General", "Entrada para los 4 días de Montevideo Comics", 600, 1500);
        controladorEve.altaTipoRegistro(manejadorEve.obtenerEdicion("COMICS24"), "Cosplayer", "Entrada especial con acreditación para concurso cosplay", 300, 50);

        controladorEve.altaTipoRegistro(manejadorEve.obtenerEdicion("COMICS25"), "General", "Entrada para los 4 días de Montevideo Comics", 800, 1000);
        controladorEve.altaTipoRegistro(manejadorEve.obtenerEdicion("COMICS25"), "Cosplayer", "Entrada especial con acreditación para concurso cosplay", 500, 100);

        controladorEve.altaTipoRegistro(manejadorEve.obtenerEdicion("EXPOAGRO25"), "General", "Acceso a la exposición agropecuaria", 300, 5000);
        controladorEve.altaTipoRegistro(manejadorEve.obtenerEdicion("EXPOAGRO25"), "Empresarial", "Acceso para empresas + networking", 2000, 5);

        controladorEve.altaTipoRegistro(manejadorEve.obtenerEdicion("CONFTECH26"), "Full", "Acceso ilimitado + Cena de gala", 1800, 300);
        controladorEve.altaTipoRegistro(manejadorEve.obtenerEdicion("CONFTECH26"), "General", "Acceso general", 1500, 500);
        controladorEve.altaTipoRegistro(manejadorEve.obtenerEdicion("CONFTECH26"), "Estudiante", "Acceso para estudiantes", 1000, 50);

        controladorEve.altaTipoRegistro(manejadorEve.obtenerEdicion("MWC"), "Full", "Acceso ilimitado + Cena de gala", 750, 550);
        controladorEve.altaTipoRegistro(manejadorEve.obtenerEdicion("MWC"), "General", "Acceso general", 500, 400);
        controladorEve.altaTipoRegistro(manejadorEve.obtenerEdicion("MWC"), "Estudiante", "Acceso para estudiantes", 250, 400);

        controladorEve.altaTipoRegistro(manejadorEve.obtenerEdicion("WS26"), "Full", "Acceso ilimitado + Cena de gala", 900, 30);
        controladorEve.altaTipoRegistro(manejadorEve.obtenerEdicion("WS26"), "General", "Acceso general", 650, 5);
        controladorEve.altaTipoRegistro(manejadorEve.obtenerEdicion("WS26"), "Estudiante", "Acceso para estudiantes", 300, 1);
        controladorEve.altaTipoRegistro(
        	    manejadorEve.obtenerEdicion("MFW26"),
        	    "Full",
        	    "Acceso a todos los eventos de la semana",
        	    450,
        	    50
        	);

        	controladorEve.altaTipoRegistro(
        	    manejadorEve.obtenerEdicion("MFW26"),
        	    "Visitante",
        	    "Acceso parcial a los eventos de la semana",
        	    150,
        	    25
        	);
        
    }

    // =========================
    // REGISTROS
    // =========================
    public static void cargarRegistrosEjemplo() {
        var controladorEve = new logica.controladores.ControladorEvento();
        var manejadorEve = logica.manejadores.ManejadorEvento.getInstancia();
        var manejadorUsu = logica.manejadores.ManejadorUsuario.getInstancia();

        // --- Todos los registros válidos con asistentes (A) ---
        controladorEve.altaRegistroEdicionEvento("RE01",
            manejadorUsu.getUsuarios().get("sofirod"), // US03
            manejadorEve.obtenerEvento("Montevideo Rock"),
            manejadorEve.obtenerEdicion("MONROCK25"),  // EDEV01
            manejadorEve.obtenerEdicion("MONROCK25").obtenerTipoRegistro("VIP"), // TR02
            LocalDate.of(2025, 5, 14), 4000, LocalDate.of(2025, 11, 20));

        controladorEve.altaRegistroEdicionEvento("RE02",
            manejadorUsu.getUsuarios().get("sofirod"),
            manejadorEve.obtenerEvento("Maratón de Montevideo"),
            manejadorEve.obtenerEdicion("MARATON24"),
            manejadorEve.obtenerEdicion("MARATON24").obtenerTipoRegistro("Corredor 21K"),
            LocalDate.of(2024, 7, 30), 500, LocalDate.of(2024, 9, 14));

        controladorEve.altaRegistroEdicionEvento("RE03",
            manejadorUsu.getUsuarios().get("andrearod"),
            manejadorEve.obtenerEvento("Conferencia de Tecnología"),
            manejadorEve.obtenerEdicion("WS26"),
            manejadorEve.obtenerEdicion("WS26").obtenerTipoRegistro("Estudiante"),
            LocalDate.of(2025, 8, 21), 300, LocalDate.of(2026, 1, 13));

        controladorEve.altaRegistroEdicionEvento("RE04",
            manejadorUsu.getUsuarios().get("sofirod"),
            manejadorEve.obtenerEvento("Maratón de Montevideo"),
            manejadorEve.obtenerEdicion("MARATON25"),
            manejadorEve.obtenerEdicion("MARATON25").obtenerTipoRegistro("Corredor 42K"),
            LocalDate.of(2025, 3, 3), 1200, LocalDate.of(2025, 9, 14));

        controladorEve.altaRegistroEdicionEvento("RE05",
            manejadorUsu.getUsuarios().get("vale23"),
            manejadorEve.obtenerEvento("Conferencia de Tecnología"),
            manejadorEve.obtenerEdicion("MWC"),
            manejadorEve.obtenerEdicion("MWC").obtenerTipoRegistro("Full"),
            LocalDate.of(2025, 8, 22), 750, LocalDate.of(2025, 12, 12));

        controladorEve.altaRegistroEdicionEvento("RE06",
            manejadorUsu.getUsuarios().get("AnaG"),
            manejadorEve.obtenerEvento("Maratón de Montevideo"),
            manejadorEve.obtenerEdicion("MARATON25"),
            manejadorEve.obtenerEdicion("MARATON25").obtenerTipoRegistro("Corredor 10K"),
            LocalDate.of(2025, 4, 9), 500, LocalDate.of(2025, 9, 14));

        controladorEve.altaRegistroEdicionEvento("RE07",
            manejadorUsu.getUsuarios().get("JaviL"),
            manejadorEve.obtenerEvento("Maratón de Montevideo"),
            manejadorEve.obtenerEdicion("MARATON25"),
            manejadorEve.obtenerEdicion("MARATON25").obtenerTipoRegistro("Corredor 21K"),
            LocalDate.of(2025, 4, 10), 800, LocalDate.of(2025, 9, 14));

        controladorEve.altaRegistroEdicionEvento("RE08",
            manejadorUsu.getUsuarios().get("MariR"),
            manejadorEve.obtenerEvento("Montevideo Comics"),
            manejadorEve.obtenerEdicion("COMICS25"),
            manejadorEve.obtenerEdicion("COMICS25").obtenerTipoRegistro("Cosplayer"),
            LocalDate.of(2025, 8, 3), 500, LocalDate.of(2025, 8, 4));

        controladorEve.altaRegistroEdicionEvento("RE09",
            manejadorUsu.getUsuarios().get("SofiM"),
            manejadorEve.obtenerEvento("Montevideo Comics"),
            manejadorEve.obtenerEdicion("COMICS24"),
            manejadorEve.obtenerEdicion("COMICS24").obtenerTipoRegistro("General"),
            LocalDate.of(2024, 7, 16), 600, LocalDate.of(2024, 7, 18));

        controladorEve.altaRegistroEdicionEvento("RE10",
            manejadorUsu.getUsuarios().get("msilva"),
            manejadorEve.obtenerEvento("Conferencia de Tecnología"),
            manejadorEve.obtenerEdicion("CONFTECH26"),
            manejadorEve.obtenerEdicion("CONFTECH26").obtenerTipoRegistro("Full"),
            LocalDate.of(2025, 10, 1), 0, LocalDate.of(2026, 4, 6));

        controladorEve.altaRegistroEdicionEvento("RE11",
            manejadorUsu.getUsuarios().get("andrearod"),
            manejadorEve.obtenerEvento("Conferencia de Tecnología"),
            manejadorEve.obtenerEdicion("CONFTECH26"),
            manejadorEve.obtenerEdicion("CONFTECH26").obtenerTipoRegistro("General"),
            LocalDate.of(2025, 10, 6), 0, LocalDate.of(2026, 4, 6));

        controladorEve.altaRegistroEdicionEvento("RE12",
            manejadorUsu.getUsuarios().get("MariR"),
            manejadorEve.obtenerEvento("Conferencia de Tecnología"),
            manejadorEve.obtenerEdicion("CONFTECH26"),
            manejadorEve.obtenerEdicion("CONFTECH26").obtenerTipoRegistro("Estudiante"),
            LocalDate.of(2025, 10, 10), 1500, LocalDate.of(2026, 4, 6));


    }

    // =========================
    // PATROCINIOS
    // =========================
    public static void cargarPatrociniosEjemplo() throws ValorPatrocinioExcedidoException {
        var controladorEve = new logica.controladores.ControladorEvento();
        var manejadorEve = ManejadorEvento.getInstancia();
        

        controladorEve.altaPatrocinio(
            manejadorEve.obtenerEdicion("CONFTECH26"),
            logica.manejadores.ManejadorUsuario.getInstancia().findInstitucion("Facultad de Ingeniería"),
            DTNivel.ORO,
            manejadorEve.obtenerEdicion("CONFTECH26").obtenerTipoRegistro("Estudiante"),
            20000,
            LocalDate.of(2025, 8, 21),
            4,
            "TECHUDELAR"
        );

        controladorEve.altaPatrocinio(
            manejadorEve.obtenerEdicion("CONFTECH26"),
            logica.manejadores.ManejadorUsuario.getInstancia().findInstitucion("Agencia Nacional de Investigación e Innovación (ANII)"),
            DTNivel.PLATA,
            manejadorEve.obtenerEdicion("CONFTECH26").obtenerTipoRegistro("General"),
            10000,
            LocalDate.of(2025, 8, 20),
            1,
            "TECHANII"
        );

        controladorEve.altaPatrocinio(
            manejadorEve.obtenerEdicion("MARATON25"),
            logica.manejadores.ManejadorUsuario.getInstancia().findInstitucion("Antel"),
            DTNivel.PLATINO,
            manejadorEve.obtenerEdicion("MARATON25").obtenerTipoRegistro("Corredor 10K"),
            25000,
            LocalDate.of(2025, 3, 4),
            10,
            "CORREANTEL"
        );

        controladorEve.altaPatrocinio(
            manejadorEve.obtenerEdicion("EXPOAGRO25"),
            logica.manejadores.ManejadorUsuario.getInstancia().findInstitucion("Universidad Católica del Uruguay"),
            DTNivel.BRONCE,
            manejadorEve.obtenerEdicion("EXPOAGRO25").obtenerTipoRegistro("General"),
            15000,
            LocalDate.of(2025, 5, 5),
            10,
            "EXPOCAT"
        );
    }
}
