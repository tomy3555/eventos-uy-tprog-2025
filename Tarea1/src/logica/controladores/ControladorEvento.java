package logica.controladores;

import java.util.List;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.LinkedHashSet;


import excepciones.EdicionYaExisteException;
import excepciones.EventoYaExisteException;
import excepciones.RegistroNoExiste;
import excepciones.TipoRegistroYaExisteException;
import excepciones.UsuarioNoEsAsistente;
import excepciones.PatrocinioYaExisteException;
import excepciones.ValorPatrocinioExcedidoException;
import excepciones.FechasCruzadasException;
import excepciones.CupoTipoRegistroInvalidoException;
import excepciones.CostoTipoRegistroInvalidoException;
import logica.clases.Asistente;
import logica.clases.Categoria;
import logica.clases.Ediciones;
import logica.clases.Eventos;
import logica.clases.Institucion;
import logica.clases.Patrocinio;
import logica.clases.Registro;
import logica.clases.TipoRegistro;
import logica.clases.Usuario;
import logica.datatypes.DTCategorias;
import logica.datatypes.DTEdicion;
import logica.datatypes.DTEvento;
import logica.datatypes.DTRegistro;
import logica.datatypes.DTPatrocinio;
import logica.datatypes.DTTipoRegistro;
import logica.enumerados.DTEstado;
import logica.enumerados.DTNivel;
import logica.interfaces.IControladorEvento;
import logica.manejadores.ManejadorEvento;
import logica.manejadores.ManejadorAuxiliar;
import logica.manejadores.ManejadorUsuario;

import java.lang.reflect.InvocationTargetException;




public class ControladorEvento implements IControladorEvento {
    private ManejadorEvento manejador = ManejadorEvento.getInstancia();
    private ManejadorUsuario mUsuario = ManejadorUsuario.getInstancia();

    private String edicionSeleccionadaSigla = null;

    public void altaEvento(String nombre, String desc, LocalDate fechaDeAlta, String sigla, DTCategorias categorias, String imagen) throws EventoYaExisteException {
        if (categorias == null || categorias.getCategorias() == null || categorias.getCategorias().isEmpty()) {
            throw new RuntimeException("Debe asociar al menos una categoría al evento");
        }
        if (manejador.existeEvento(nombre)) {
            throw new EventoYaExisteException(nombre);
        }
        ManejadorAuxiliar manejadorAux = ManejadorAuxiliar.getInstancia();
        Map<String, Categoria> categoriasMap = new java.util.HashMap<>();
        for (String nombreCat : categorias.getCategorias()) {
            Categoria cat = manejadorAux.obtenerCategoria(nombreCat);
            if (cat == null) {
                throw new RuntimeException("La categoría '" + nombreCat + "' no existe");
            }
            categoriasMap.put(nombreCat, cat);
        }
        Eventos nuevoEvento = new Eventos(nombre, sigla, desc, fechaDeAlta, categoriasMap, imagen);
        manejador.agregarEvento(nuevoEvento);
    }

    public void altaTipoRegistro(Ediciones edicion, String nombre, String descripcion, float costo, int cupo) throws TipoRegistroYaExisteException, CupoTipoRegistroInvalidoException, CostoTipoRegistroInvalidoException {
        if (edicion.obtenerTipoRegistro(nombre) != null) {
            throw new TipoRegistroYaExisteException(nombre);
        }
        if (cupo <= 0 || cupo > Integer.MAX_VALUE) {
            throw new CupoTipoRegistroInvalidoException(cupo);
        }
        if (costo < 0 || costo > Float.MAX_VALUE) {
            throw new CostoTipoRegistroInvalidoException(costo);
        }
        TipoRegistro tipo = new TipoRegistro(edicion, nombre, descripcion, costo, cupo);
        edicion.agregarTipoRegistro(nombre, tipo);
        ManejadorEvento manejadorEvento = ManejadorEvento.getInstancia();
        manejadorEvento.agregarTipoRegistro(tipo);
    }
    
    public void altaTipoRegistroDTO(DTEdicion dtEdicion, String nombre, String descripcion, float costo , int cupo) throws TipoRegistroYaExisteException, CupoTipoRegistroInvalidoException, CostoTipoRegistroInvalidoException {
    	
    	Ediciones edicion = obtenerEdicion(dtEdicion.getEvento().getNombre(), dtEdicion.getNombre());
		altaTipoRegistro(edicion, nombre, descripcion, costo, cupo);
	}

    public void altaPatrocinio(Ediciones edicion, Institucion institucion, DTNivel nivel, TipoRegistro tipoRegistro, int aporte, LocalDate fechaPatrocinio, int cantidadRegistros, String codigoPatrocinio) throws ValorPatrocinioExcedidoException {
        ManejadorAuxiliar manejadorAux = ManejadorAuxiliar.getInstancia();
        for (Patrocinio p : manejadorAux.listarPatrocinios()) {
            if (p.getInstitucion().equals(institucion) && p.getEdicion().equals(edicion)) {
                throw new PatrocinioYaExisteException(institucion.getNombre(), edicion.getNombre());
            }
            if (p.getCodigoPatrocinio().equals(codigoPatrocinio)) {
                return;
            }
        }
        float valorRegistros = cantidadRegistros * tipoRegistro.getCosto();
        if (valorRegistros > (aporte * 0.2)) {
            throw new ValorPatrocinioExcedidoException();
        }
        Patrocinio pat = new Patrocinio(edicion, institucion, nivel, tipoRegistro, aporte, fechaPatrocinio, cantidadRegistros, codigoPatrocinio);
        manejadorAux.agregarPatrocinio(pat);
        edicion.getPatrocinios().add(pat);
    }

    public void altaCategoria(String nombre) {
        ManejadorAuxiliar manejadorAux = ManejadorAuxiliar.getInstancia();
        if (manejadorAux.existeCategoria(nombre)) {
            throw new RuntimeException("Ya existe la categoría: " + nombre);
        }
        Categoria categoria = new Categoria(nombre);
        manejadorAux.agregarCategoria(nombre, categoria);
    }

    public void altaEdicionEvento(Eventos evento, Usuario usuario, String nombre, String sigla, String desc, LocalDate fechaInicio, LocalDate fechaFin, LocalDate fechaAlta, String ciudad, String pais, String imagen) throws EdicionYaExisteException, EventoYaExisteException, FechasCruzadasException {
        ManejadorEvento manejador = ManejadorEvento.getInstancia();
        if (fechaInicio.isAfter(fechaFin)) {
            throw new FechasCruzadasException("La fecha de inicio debe ser anterior a la fecha de fin.");
        }
        if (manejador.existeEvento(evento.getNombre())) {
            if (!manejador.existeEdicion(nombre)) {
                Ediciones nuevaEdicion = new Ediciones(evento, nombre, sigla, fechaInicio, fechaFin, fechaAlta, usuario, ciudad, pais, imagen);
                evento.agregarEdicion(nuevaEdicion);
                manejador.agregarEdicion(nuevaEdicion);
                mUsuario.findOrganizador(usuario.getNickname()).agregarEdicion(nuevaEdicion);
            } else {
                throw new EdicionYaExisteException("El nombre de la edición " + nombre + " ya está en uso.");
            }
        } else {
            throw new EventoYaExisteException(evento.getNombre());
        }
    }

    public DTEdicion consultaEdicionEvento(String siglaEvento, String siglaEdicion) {
        ManejadorEvento manejador = ManejadorEvento.getInstancia();
        Eventos evento = manejador.obtenerEvento(siglaEvento);
        DTEvento dtEvento = consultaDTEvento(evento.getNombre());
        if (evento == null) return null;
        Ediciones edicion = evento.obtenerEdicion(siglaEdicion);
        if (edicion == null || edicion.getEstado() == DTEstado.Ingresada || edicion.getEstado() == DTEstado.Rechazada) return null;
        DTEdicion dto = new DTEdicion(
            edicion.getNombre(),
            edicion.getSigla(),
            edicion.getFechaInicio(),
            edicion.getFechaFin(),
            edicion.getFechaAlta(),
            edicion.getOrganizador().getNickname(),
            edicion.getCiudad(),
            edicion.getPais(),
            edicion.getImagen(),
            edicion.getEstado(),
            dtEvento
        );
        dto.setVideo(edicion.getVideo());
        return dto;
    }
    
    public List<DTEvento> listarEventosPorCategoria(String categoriaBuscada) {
        String needle = normalizar(categoriaBuscada);
        List<DTEvento> out = new ArrayList<>();

        Collection<Eventos> eventos = ManejadorEvento.getInstancia().obtenerEventos().values();

        for (Eventos ev : eventos) {
        	if (!ev.getVigente()) {
        						continue;
        	}
            List<String> cats = extraerNombresCategorias(ev.getCategorias());
            boolean match = cats.stream().anyMatch(c -> normalizar(c).equals(needle));
            if (!match) continue;

            List<String> eds = extraerNombresEdiciones(ev.getEdiciones());
            out.add(new DTEvento(
                    ev.getNombre(),
                    ev.getSigla(),
                    ev.getDescripcion(),
                    ev.getFecha(),
                    cats,
                    eds,
                    ev.getImagen()
            ));
        }
        return out;
    }

    public List<String> listarCategoriasConEventos() {
        Set<String> set = new LinkedHashSet<>();
        Collection<logica.clases.Eventos> eventos =
                ManejadorEvento.getInstancia().obtenerEventos().values();

        for (Eventos eventoIter : eventos) {
            List<String> cats = extraerNombresCategorias(eventoIter.getCategorias());
            for (String nombreCatIter : cats) {
                if (nombreCatIter != null) {
                    String nombreCatIterTrim = nombreCatIter.trim();
                    if (!nombreCatIterTrim.isEmpty()) set.add(nombreCatIterTrim);
                }
            }
        }
        return new ArrayList<>(set);
    }

    private static List<String> extraerNombresCategorias(Map<?, ?> categorias) {
        List<String> res = new ArrayList<>();
        if (categorias == null || categorias.isEmpty()) return res;

        for (Object v : categorias.values()) {
            addNombre(res, v);
        }
        return res;
    }

    private static List<String> extraerNombresEdiciones(Map<?, ?> ediciones) {
        List<String> res = new ArrayList<>();
        if (ediciones == null || ediciones.isEmpty()) return res;

        for (Object v : ediciones.values()) {
            addNombre(res, v);
        }
        if (!res.isEmpty()) return res;

        for (Object k : ediciones.keySet()) {
            if (k != null) res.add(String.valueOf(k));
        }
        return res;
    }

    private static void addNombre(List<String> out, Object objAux) {
        if (objAux == null) return;

        if (objAux instanceof String textObj) {
            if (!textObj.isBlank()) out.add(textObj);
            return;
        }
        try {
            var nombreObj = objAux.getClass().getMethod("getNombre");
            Object nombre = nombreObj.invoke(objAux);
            if (nombre instanceof String nombreObjString && !nombreObjString.isBlank()) out.add(nombreObjString);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exept) {
        }

    }

    private static String normalizar(String textNorm) {
        if (textNorm == null) return "";
        String textTrim = textNorm.trim().toLowerCase(Locale.ROOT);
        textTrim = java.text.Normalizer.normalize(textTrim, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", ""); // quita tildes
        return textTrim;
    }
    
    public static List<String> listarCategorias() {
        Set<String> set = ManejadorAuxiliar.getInstancia().listarCategorias();
        List<String> res = new ArrayList<>();
        if (set == null || set.isEmpty()) return res;

        for (String s : set) {
            if (s != null && !s.isBlank()) res.add(s);
        }
        return res;
    }


    public boolean esEventoVigente(String nombreEvento) {
		ManejadorEvento manejador = ManejadorEvento.getInstancia();
		Eventos evento = manejador.obtenerEvento(nombreEvento);
		if (evento != null) {
			return evento.getVigente();
		}
		return false;
	}
    public void finalizarEvento(String nombreEvento) {
		ManejadorEvento manejador = ManejadorEvento.getInstancia();
		Eventos evento = manejador.obtenerEvento(nombreEvento);
		if (evento != null) {
			System.out.println("Finalizando evento: " + nombreEvento);
			evento.setVigente(false);
		}
	}

    public Eventos consultaEvento(String nombreEvento) {
        ManejadorEvento manejador = ManejadorEvento.getInstancia();
        return manejador.obtenerEvento(nombreEvento);
    }

    // ARREGLADO NUEVO
    @Override
    public DTEvento consultaDTEvento(String nombreEvento) {
        ManejadorEvento manejador = ManejadorEvento.getInstancia();
        Eventos evento = manejador.obtenerEvento(nombreEvento);
        if (evento == null) return null;
        return new DTEvento(
            evento.getNombre(),
            evento.getSigla(),
            evento.getDescripcion(),
            evento.getFecha(),
            new ArrayList<>(evento.getCategorias().keySet()),
            new ArrayList<>(evento.getEdiciones().keySet()),
            evento.getImagen()
        );
    }

    public void altaRegistroEdicionEvento(String idRegistro, Usuario usuario, Eventos evento, Ediciones edicion,
            TipoRegistro tipoRegistro, LocalDate fechaRegistro, float costo, LocalDate fechaInicio) {
ManejadorEvento manejadorEvento = ManejadorEvento.getInstancia();
if (usuario.esAsistente(usuario)) {
if (edicion == null) {
throw new RuntimeException("No se encontró la edición especificada.");
}
if (tipoRegistro == null) {
throw new RuntimeException("No se encontró el tipo de registro especificado para la edición.");
}

// ===== NUEVO: bloquear si la edición ya terminó =====
LocalDate hoy = (fechaRegistro != null) ? fechaRegistro : LocalDate.now();
LocalDate fin = edicion.getFechaFin();
if (fin != null && hoy.isAfter(fin)) {
throw new RuntimeException("La edición '" + edicion.getNombre() + "' ya finalizó el " + fin + ".");
}
// =====================================================

if (tipoRegistro.getCupo() <= 0) {
throw new excepciones.CupoTipoRegistroInvalidoException(tipoRegistro.getCupo());
}
boolean yaRegistrado = false;
for (Registro reg : manejadorEvento.obtenerRegistros().values()) {
if (reg.getUsuario().equals(usuario) && reg.getEdicion().equals(edicion)) {
yaRegistrado = true;
break;
}
}
int cantidadRegistrados = 0;
for (Registro reg : manejadorEvento.obtenerRegistros().values()) {
if (reg.getTipoRegistro().equals(tipoRegistro) && reg.getEdicion().equals(edicion)) {
cantidadRegistrados++;
}
}
if (yaRegistrado) {
throw new RuntimeException("El usuario ya está registrado a esta edición.");
}
if (cantidadRegistrados >= tipoRegistro.getCupo()) {
throw new excepciones.CupoTipoRegistroInvalidoException(tipoRegistro.getCupo());
}
Registro nuevoRegistro = new Registro(idRegistro, usuario, edicion, tipoRegistro, fechaRegistro, costo, fechaInicio);
manejadorEvento.agregarRegistro(nuevoRegistro);
edicion.agregarRegistro(idRegistro, nuevoRegistro);
Asistente asist = (Asistente) usuario;
asist.addRegistro(idRegistro, nuevoRegistro);
} else {
// (sin cambios)
}
}


    public List<DTEvento> listarEventos() {
        Map<String, Eventos> eventos = manejador.obtenerEventos();
        List<DTEvento> lista = new ArrayList<>();
        for (Eventos eventIter : eventos.values()) {
            lista.add(new DTEvento(
                eventIter.getNombre(),
                eventIter.getSigla(),
                eventIter.getDescripcion(),
                eventIter.getFecha(),
                new ArrayList<>(eventIter.getCategorias().keySet()),
                new ArrayList<>(eventIter.getEdiciones().keySet())
            ));
        }
        return lista;
    }
    public List<DTEvento> listarEventosVigentes() {
        Map<String, Eventos> eventos = manejador.obtenerEventos();
        List<DTEvento> lista = new ArrayList<>();
        for (Eventos eventIter : eventos.values()) {
            if (!eventIter.getVigente()) {
				continue;
			}
        	lista.add(new DTEvento(
                eventIter.getNombre(),
                eventIter.getSigla(),
                eventIter.getDescripcion(),
                eventIter.getFecha(),
                new ArrayList<>(eventIter.getCategorias().keySet()),
                new ArrayList<>(eventIter.getEdiciones().keySet())
            ));
        }
        return lista;
    }

    public List<String> listarEdicionesEvento(String nombreEvento) {
        Eventos evento = manejador.obtenerEvento(nombreEvento);
        if (evento == null) return new ArrayList<>();
        return new ArrayList<>(evento.getEdiciones().keySet());
    }

    public Ediciones obtenerEdicion(String nombreEvento, String nombreEdicion) {
        Eventos evento = manejador.obtenerEvento(nombreEvento);
        if (evento == null) return null;
        return evento.obtenerEdicion(nombreEdicion);
    }

    public DTRegistro consultaRegistro(Usuario user, String idRegistro) {
        if (!(user instanceof Asistente)) {
            throw new UsuarioNoEsAsistente(user.getNickname());
        }
        Asistente userAsis = (Asistente) user;
        Registro registroUserAsis = userAsis.getRegistros().get(idRegistro);
        if (registroUserAsis == null) {
            throw new RegistroNoExiste(idRegistro);
        }
        return new DTRegistro(
            registroUserAsis.getId(),
            user.getNombre(),
            registroUserAsis.getEdicion().getNombre(),
            registroUserAsis.getTipoRegistro().getNombre(),
            registroUserAsis.getFechaRegistro(),
            registroUserAsis.getCosto(),
            registroUserAsis.getFechaInicio()
        );
    }

    // --- selección de edición para consultas ---
    public void seleccionarEdicion(String sigla) {
        Ediciones edicionIter = manejador.obtenerEdicion(sigla);
        if (edicionIter == null) {
            throw new RuntimeException("No existe la edición con sigla: " + sigla);
        }
        this.edicionSeleccionadaSigla = sigla;
    }

    public String getEdicionSeleccionadaSigla() {
        return edicionSeleccionadaSigla;
    }

    public DTEdicion obtenerEdicionSeleccionada() {
        if (edicionSeleccionadaSigla == null) return null;
        Ediciones edicionIter = manejador.obtenerEdicion(edicionSeleccionadaSigla);
        if (edicionIter == null) return null;
        DTEvento dtEvento = consultaDTEvento(edicionIter.getEvento().getNombre());
        DTEdicion dto = new DTEdicion(
            edicionIter.getNombre(),
            edicionIter.getSigla(),
            edicionIter.getFechaInicio(),
            edicionIter.getFechaFin(),
            edicionIter.getFechaAlta(),
            edicionIter.getOrganizador() != null ? edicionIter.getOrganizador().getNickname() : null,
            edicionIter.getCiudad(),
            edicionIter.getPais(),
            edicionIter.getImagen(),
            dtEvento
        );
        dto.setVideo(edicionIter.getVideo());
        return dto;
    }

    @Override
    public DTEdicion obtenerDtEdicion(String nombreEvento, String nombreEdicion) {
        Ediciones edicion = obtenerEdicion(nombreEvento, nombreEdicion);
        if (edicion == null) return null;
        DTEvento dtEvento = consultaDTEvento(edicion.getEvento().getNombre());
        List<DTTipoRegistro> tiposRegistroDTO = new ArrayList<>();
        for (TipoRegistro tipo : edicion.getTiposRegistro()) {
            tiposRegistroDTO.add(new DTTipoRegistro(
                tipo.getNombre(),
                tipo.getDescripcion(),
                tipo.getCosto(),
                tipo.getCupo()
            ));
        }

        List<DTPatrocinio> patrociniosDTO = new ArrayList<>();
        for (Patrocinio pat : edicion.getPatrocinios()) {
            patrociniosDTO.add(new DTPatrocinio(
                pat.getCodigoPatrocinio(),
                pat.getAporte(),
                pat.getFechaPatrocinio(),
                pat.getNivel(),
                pat.getCantidadRegistros(),
                pat.getInstitucion().getNombre(),
                pat.getEdicion().getNombre(),
                pat.getTipoRegistro().getNombre()
            ));
        }

        List<DTRegistro> registrosDTO = new ArrayList<>();
        for (Registro reg : edicion.getRegistros().values()) {
            registrosDTO.add(new DTRegistro(
                reg.getId(),
                reg.getUsuario().getNombre(),
                edicion.getNombre(),
                reg.getTipoRegistro().getNombre(),
                reg.getFechaRegistro(),
                reg.getCosto(),
                reg.getFechaInicio()
            ));
        }

        DTEdicion dto = new DTEdicion(
            edicion.getNombre(),
            edicion.getSigla(),
            edicion.getFechaInicio(),
            edicion.getFechaFin(),
            edicion.getFechaAlta(),
            edicion.getOrganizador() != null ? edicion.getOrganizador().getNickname() : null,
            edicion.getCiudad(),
            edicion.getPais(),
            edicion.getImagen(),
            edicion.getEstado(),
            tiposRegistroDTO,
            patrociniosDTO,
            registrosDTO,
            dtEvento
        );
        dto.setVideo(edicion.getVideo());
        return dto;
    }


    @Override
    public Ediciones obtenerEdicionPorSigla(String sigla) {
        return manejador.obtenerEdicion(sigla);
    }
    
    
    public DTEdicion obtenerEdicionPorSiglaDT(String sigla) {
        String nombreEvento = obtenerEdicionPorSigla(sigla).getEvento().getNombre();
        String nombreEdicion = obtenerEdicionPorSigla(sigla).getNombre();
    	return obtenerDtEdicion(nombreEvento, nombreEdicion);
        	 
    }
    
    @Override
    public String encontrarEventoPorSigla(String siglaEdicion) {
        Ediciones edicionIter = manejador.obtenerEdicion(siglaEdicion);
        if (edicionIter != null && edicionIter.getEvento() != null) {
            return edicionIter.getEvento().getNombre();
        }
        return null;
    }
    
    public List<String> listarEventosConEdicionesIngresadas() {
        List<String> resultado = new ArrayList<>();
        for (Eventos eventoIter : manejador.obtenerEventos().values()) {
            for (Ediciones edicionIter : eventoIter.getEdiciones().values()) {
                if (edicionIter.getEstado() == DTEstado.Ingresada) {
                    resultado.add(eventoIter.getNombre());
                    break;
                }
            }
        }
        return resultado;
    }

    public List<String> listarEdicionesIngresadasDeEvento(String nombreEvento) {
        Eventos evento = manejador.obtenerEvento(nombreEvento);
        List<String> resultado = new ArrayList<>();
        if (evento != null) {
            for (Ediciones edicionIter : evento.getEdiciones().values()) {
                if (edicionIter.getEstado() == DTEstado.Ingresada) {
                    resultado.add(edicionIter.getNombre());
                }
            }
        }
        return resultado;
    }

    public void aceptarRechazarEdicion(Ediciones edicion, boolean aceptar) {
        if (edicion != null) {
            edicion.setEstado(aceptar ? DTEstado.Aceptada : DTEstado.Rechazada);
        }
    }
    
    public void cambiarEstadoEdicion(String evento, String edicion, boolean aceptar) {
    	Eventos eventoIter = manejador.obtenerEvento(evento);
		if (eventoIter != null) {
			Ediciones edicionIter = eventoIter.obtenerEdicion(edicion);
			if (edicionIter != null) {
				edicionIter.setEstado(aceptar ? DTEstado.Aceptada : DTEstado.Rechazada);
			}
		}
    }
    
    public void actualizarImagenEvento(String nombreEvento, String imagenPath) throws IllegalArgumentException {
        if (nombreEvento == null || nombreEvento.isBlank()) {
            throw new IllegalArgumentException("Nombre de evento inválido");
        }
        Eventos evento = manejador.obtenerEvento(nombreEvento); // ajustá si tu manejador busca por sigla/ID
        if (evento == null) {
            throw new IllegalArgumentException("Evento no encontrado: " + nombreEvento);
        }
        evento.setImagen(imagenPath); // asegurate de haber agregado get/setImagenPath en la entidad
        // Si tu Manejador requiere persistir/cerrar transacción, hacelo aquí (p.ej., me().guardar(ev);)
    }
    
    @Override
    public void altaRegistroEdicionEvento(
        String idRegistro,
        String nickUsuario,
        String nombreEvento,
        String nombreEdicion,
        String nombreTipoRegistro,
        LocalDate fechaRegistro,
        float costo,
        LocalDate fechaInicio
    ) throws RuntimeException {
        ManejadorUsuario manejU = ManejadorUsuario.getInstancia();
        ManejadorEvento manejE = ManejadorEvento.getInstancia();

        Usuario usuario = manejU.findUsuario(nickUsuario);
        if (usuario == null || !(usuario instanceof Asistente asistente)) {
            throw new IllegalArgumentException("El usuario no es un asistente válido.");
        }

        Eventos evento = manejE.obtenerEvento(nombreEvento);
        if (evento == null) {
            throw new IllegalArgumentException("Evento no encontrado: " + nombreEvento);
        }

        Ediciones edicion = evento.getEdiciones().get(nombreEdicion);
        if (edicion == null) {
            throw new IllegalArgumentException("Edición no encontrada: " + nombreEdicion);
        }

        TipoRegistro tipo = null;
        for (TipoRegistro t : edicion.getTiposRegistro()) {
            if (t.getNombre().equalsIgnoreCase(nombreTipoRegistro)) {
                tipo = t;
                break;
            }
        }
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo de registro no encontrado: " + nombreTipoRegistro);
        }

        if (tipo.getCupo() <= 0) {
            throw new excepciones.CupoTipoRegistroInvalidoException(tipo.getCupo());
        }

        // Control de ya registrado
        for (Registro reg : manejE.obtenerRegistros().values()) {
            if (reg.getUsuario().equals(usuario) && reg.getEdicion().equals(edicion)) {
                throw new RuntimeException("El usuario ya está registrado a esta edición.");
            }
        }

        // Control de cupo ocupado (comparar por nombre, no por instancia)
        int cantidadRegistrados = 0;
        for (Registro reg : manejE.obtenerRegistros().values()) {
            if (reg.getEdicion().equals(edicion)
                && reg.getTipoRegistro() != null
                && reg.getTipoRegistro().getNombre().equalsIgnoreCase(nombreTipoRegistro)) {
                cantidadRegistrados++;
            }
        }
        if (cantidadRegistrados >= tipo.getCupo()) {
            throw new excepciones.CupoTipoRegistroInvalidoException(tipo.getCupo());
        }

        Registro nuevo = new Registro(idRegistro, usuario, edicion, tipo, fechaRegistro, costo, fechaInicio);
        manejE.agregarRegistro(nuevo);
        edicion.agregarRegistro(idRegistro, nuevo);
        asistente.addRegistro(idRegistro, nuevo);
    }
    
    @Override
    public void altaEdicionEventoDTO(
            logica.datatypes.DTEvento eventoDTO,
            logica.datatypes.DTDatosUsuario usuarioDTO,
            String nombre,
            String sigla,
            String desc,
            java.time.LocalDate fechaInicio,
            java.time.LocalDate fechaFin,
            java.time.LocalDate fechaAlta,
            String ciudad,
            String pais,
            String imagen,
            String video
    ) throws excepciones.EdicionYaExisteException,
             excepciones.EventoYaExisteException,
             excepciones.FechasCruzadasException {

        if (eventoDTO == null) throw new IllegalArgumentException("eventoDTO no puede ser null");
        if (usuarioDTO == null) throw new IllegalArgumentException("usuarioDTO no puede ser null");

        // Resolver entidades desde los DTOs
        logica.manejadores.ManejadorEvento manejador = logica.manejadores.ManejadorEvento.getInstancia();
        logica.clases.Eventos evento = manejador.obtenerEvento(eventoDTO.getNombre());
        if (evento == null) {
            // Mantengo tu misma excepción usada en el else original
            throw new excepciones.EventoYaExisteException(eventoDTO.getNombre());
        }

        logica.clases.Usuario usuario = mUsuario.obtenerUsuarioPorNickOEmail(usuarioDTO.getNickname());
        if (usuario == null) {
            // Si preferís otra excepción propia, cambiala; con IllegalArgumentException basta para evitar NPE
            throw new IllegalArgumentException("Organizador inexistente: " + usuarioDTO.getNickname());
        }

        // Copia 1:1 de tu lógica original (mismos checks y mismas excepciones)
        if (fechaInicio.isAfter(fechaFin)) {
            throw new excepciones.FechasCruzadasException("La fecha de inicio debe ser anterior a la fecha de fin.");
        }
        if (manejador.existeEvento(evento.getNombre())) {
            if (!manejador.existeEdicion(nombre)) {
                logica.clases.Ediciones nuevaEdicion =
                    new logica.clases.Ediciones(evento, nombre, sigla, fechaInicio, fechaFin, fechaAlta, usuario, ciudad, pais, imagen, video);

                 evento.agregarEdicion(nuevaEdicion);
                 manejador.agregarEdicion(nuevaEdicion);
                 mUsuario.findOrganizador(usuario.getNickname()).agregarEdicion(nuevaEdicion);
             } else {
                 throw new excepciones.EdicionYaExisteException("El nombre de la edición " + nombre + " ya está en uso.");
             }
         } else {
             // Mantengo tu else tal cual
             throw new excepciones.EventoYaExisteException(evento.getNombre());
         }
    }

    @Override
    public DTPatrocinio obtenerDTPatrocinio(String codigoPatrocinio) {
        ManejadorAuxiliar manejadorAux = ManejadorAuxiliar.getInstancia();
        for (Patrocinio p : manejadorAux.listarPatrocinios()) {
            if (p.getCodigoPatrocinio().equals(codigoPatrocinio)) {
                return new DTPatrocinio(
                    p.getCodigoPatrocinio(),
                    p.getAporte(),
                    p.getFechaPatrocinio(),
                    p.getNivel(),
                    p.getCantidadRegistros(),
                    p.getInstitucion().getNombre(),
                    p.getEdicion().getNombre(),
                    p.getTipoRegistro().getNombre()
                );
            }
        }
        return null;
    }

    @Override
    public DTTipoRegistro consultaTipoRegistro(String nombreEvento, String nombreEdicion, String nombreTipoRegistro) {
        Eventos evento = manejador.obtenerEvento(nombreEvento);
        if (evento == null) return null;
        Ediciones edicion = evento.obtenerEdicion(nombreEdicion);
        if (edicion == null) return null;
        TipoRegistro tipo = edicion.obtenerTipoRegistro(nombreTipoRegistro);
        if (tipo == null) return null;
        return new DTTipoRegistro(
            tipo.getNombre(),
            tipo.getDescripcion(),
            tipo.getCosto(),
            tipo.getCupo()
        );
    }
    
	@Override
    public List<DTCategorias> listarDTCategorias() {
        Set<String> nombresCategorias = ManejadorAuxiliar.getInstancia().listarCategorias();
        List<DTCategorias> lista = new ArrayList<>();
        if (nombresCategorias != null) {
            for (String nombre : nombresCategorias) {
                List<String> categorias = new ArrayList<>();
                categorias.add(nombre);
                lista.add(new DTCategorias(categorias));
            }
        }
        return lista;
    }
	
	public List<logica.datatypes.DTTipoRegistro> listarTiposRegistroDeEdicion(String evento, String edicion) {
	    if (evento == null || evento.isEmpty() || edicion == null || edicion.isEmpty()) {
	        throw new IllegalArgumentException("Evento y edición son obligatorios.");
	    }

	    logica.manejadores.ManejadorEvento me = logica.manejadores.ManejadorEvento.getInstancia();

	    logica.clases.Eventos ev = me.obtenerEvento(evento);
	    if (ev == null) {
	        throw new IllegalArgumentException("Evento inexistente: " + evento);
	    }

	    // Tu API: ev.obtenerEdicion(String nombreEdicion)
	    logica.clases.Ediciones ed = ev.obtenerEdicion(edicion);
	    if (ed == null) {
	        throw new IllegalArgumentException("Edición inexistente: " + edicion);
	    }

	    java.util.List<logica.datatypes.DTTipoRegistro> res = new java.util.ArrayList<>();
	    java.util.Collection<logica.clases.TipoRegistro> tipos = ed.getTiposRegistro(); // <- devuelve Collection
	    if (tipos != null) {
	        for (logica.clases.TipoRegistro t : tipos) {
	            if (t == null) continue;
	            res.add(new logica.datatypes.DTTipoRegistro(
	                t.getNombre(),
	                t.getDescripcion(),
	                t.getCosto(),
	                t.getCupo()
	            ));
	        }
	    }
	    return res; 
	}
	
	public logica.datatypes.DTPatrocinio altaPatrocinioDT(
	        String siglaEdicion,
	        String nombreInstitucion,
	        logica.enumerados.DTNivel nivel,
	        String nombreTipoRegistro,
	        int aporte,
	        java.time.LocalDate fechaPatrocinio,
	        int cantidadRegistros,
	        String codigoPatrocinio
	) throws excepciones.ValorPatrocinioExcedidoException,
	         excepciones.PatrocinioYaExisteException,
	         IllegalArgumentException {

	    // Validaciones básicas
	    if (siglaEdicion == null || siglaEdicion.isEmpty())
	        throw new IllegalArgumentException("Sigla de edición obligatoria.");
	    if (nombreInstitucion == null || nombreInstitucion.isEmpty())
	        throw new IllegalArgumentException("Institución obligatoria.");
	    if (nombreTipoRegistro == null || nombreTipoRegistro.isEmpty())
	        throw new IllegalArgumentException("Tipo de registro obligatorio.");
	    if (codigoPatrocinio == null || codigoPatrocinio.isEmpty())
	        throw new IllegalArgumentException("Código de patrocinio obligatorio.");
	    if (aporte < 0 || cantidadRegistros < 0)
	        throw new IllegalArgumentException("Aporte y cantidad deben ser no negativos.");

	    // Manejadores
	    logica.manejadores.ManejadorEvento me = logica.manejadores.ManejadorEvento.getInstancia();
	    logica.manejadores.ManejadorUsuario mu = logica.manejadores.ManejadorUsuario.getInstancia();
	    logica.manejadores.ManejadorAuxiliar mx = logica.manejadores.ManejadorAuxiliar.getInstancia();

	    // Resolver edición por SIGLA
	    logica.clases.Ediciones ed = me.obtenerEdicion(siglaEdicion);
	    if (ed == null) {
	        throw new IllegalArgumentException("Edición inexistente: " + siglaEdicion);
	    }

	    // Resolver institución por nombre
	    logica.clases.Institucion inst = mu.findInstitucion(nombreInstitucion);
	    if (inst == null) {
	        throw new IllegalArgumentException("Institución inexistente: " + nombreInstitucion);
	    }

	    // Resolver tipo de registro por nombre
	    logica.clases.TipoRegistro tipo = ed.obtenerTipoRegistro(nombreTipoRegistro);
	    if (tipo == null) {
	        throw new IllegalArgumentException("Tipo de registro inexistente: " + nombreTipoRegistro);
	    }

	    // Duplicados: misma institución en la misma edición o código repetido
	    for (logica.clases.Patrocinio p : mx.listarPatrocinios()) {
	        if (p.getInstitucion().equals(inst) && p.getEdicion().equals(ed)) {
	            throw new excepciones.PatrocinioYaExisteException(inst.getNombre(), ed.getNombre());
	        }
	        if (p.getCodigoPatrocinio().equals(codigoPatrocinio)) {
	            throw new excepciones.PatrocinioYaExisteException(inst.getNombre(), ed.getNombre()); // tratamos código duplicado como conflicto
	        }
	    }

	    // Regla 20%: costo registros <= 20% del aporte
	    float valorRegistros = cantidadRegistros * tipo.getCosto();
	    if (valorRegistros > (aporte * 0.2f)) {
	        throw new excepciones.ValorPatrocinioExcedidoException();
	    }

	    // Crear y persistir
	    logica.clases.Patrocinio pat = new logica.clases.Patrocinio(
	            ed, inst, nivel, tipo, aporte, fechaPatrocinio, cantidadRegistros, codigoPatrocinio
	    );
	    mx.agregarPatrocinio(pat);
	    ed.getPatrocinios().add(pat);

	    // Retornar DTO
	    return new logica.datatypes.DTPatrocinio(
	            codigoPatrocinio,
	            aporte,
	            fechaPatrocinio,
	            nivel,
	            cantidadRegistros,
	            inst.getNombre(),
	            ed.getSigla(),
	            tipo.getNombre()
	    );
}
}