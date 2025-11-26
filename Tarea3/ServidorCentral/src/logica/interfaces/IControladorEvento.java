package logica.interfaces;

import java.time.LocalDate;
import java.util.List;

import excepciones.CostoTipoRegistroInvalidoException;
import excepciones.CupoTipoRegistroInvalidoException;
import excepciones.EdicionYaExisteException;
import excepciones.EventoYaExisteException;
import excepciones.FechasCruzadasException;
import excepciones.TipoRegistroYaExisteException;
import excepciones.ValorPatrocinioExcedidoException;
import logica.clases.Ediciones;
import logica.clases.Eventos;
import logica.clases.Institucion;
import logica.clases.TipoRegistro;
import logica.clases.Usuario;
import logica.datatypes.DTArchEdicion;
import logica.datatypes.DTCategorias;
import logica.datatypes.DTEdicion;
import logica.datatypes.DTEvento;
import logica.datatypes.DTPatrocinio;
import logica.datatypes.DTRegistro;
import logica.datatypes.DTTipoRegistro;
import logica.datatypes.DTTopEvento;
import logica.enumerados.DTNivel;


public interface IControladorEvento {
	public void altaEvento(String nombre, String desc, LocalDate fechaDeAlta, String sigla, DTCategorias categorias, String imagen) throws EventoYaExisteException;
	public void altaTipoRegistro(Ediciones edicion, String nombre, String descripcion, float costo, int cupo) throws TipoRegistroYaExisteException, CupoTipoRegistroInvalidoException, CostoTipoRegistroInvalidoException;
	public void altaPatrocinio(Ediciones edicion, Institucion institucion, DTNivel nivel, TipoRegistro tipoRegistro, int aporte, LocalDate fechaPatrocinio, int cantidadRegistros, String codigoPatrocinio) throws ValorPatrocinioExcedidoException;
	public void altaCategoria(String nombre);
    public void altaEdicionEvento(Eventos evento, Usuario organizador, String nombre, String sigla, String desc, LocalDate fechaInicio, LocalDate fechaFin, LocalDate fechaAlta, String ciudad, String pais, String imagen)throws EdicionYaExisteException, EventoYaExisteException, FechasCruzadasException;
    public DTEdicion consultaEdicionEvento(String siglaEvento, String siglaEdicion);
    public Eventos consultaEvento(String nombreEvento);
    public void altaRegistroEdicionEvento(String idRegistro, Usuario usuario, Eventos evento, Ediciones edicion, TipoRegistro tipoRegistro, LocalDate fechaRegistro, float costo, LocalDate fechaInicio);
    public List<DTEvento> listarEventos();
    public List<String> listarEdicionesEvento(String nombreEvento);
    public Ediciones obtenerEdicion(String nombreEvento, String nombreEdicion);
    public DTEdicion obtenerDtEdicion(String nombreEvento, String nombreEdicion);
    public DTEvento consultaDTEvento(String nombreEvento);

    public void seleccionarEdicion(String sigla);

    public String getEdicionSeleccionadaSigla();
    public DTEdicion obtenerEdicionSeleccionada();
    Ediciones obtenerEdicionPorSigla(String sigla);
    public String encontrarEventoPorSigla(String siglaEdicion);
    public List<String> listarEventosConEdicionesIngresadas();
    public List<String> listarEdicionesIngresadasDeEvento(String nombreEvento);
    public void aceptarRechazarEdicion(Ediciones edicion, boolean aceptar);
    public void cambiarEstadoEdicion(String evento, String edicion, boolean aceptar);
    public List<DTEvento> listarEventosPorCategoria(String nombreCategoria);
    public List<String> listarCategoriasConEventos();
    public void actualizarImagenEvento(String nombreEvento, String imagenPath) throws IllegalArgumentException;
    public void altaRegistroEdicionEvento(
            String idRegistro,
            String nickUsuario,
            String nombreEvento,
            String nombreEdicion,
            String nombreTipoRegistro,
            LocalDate fechaRegistro,
            float costo,
            LocalDate fechaInicio
        );
   public void altaTipoRegistroDTO(DTEdicion dtEdicion, String nombre, String descripcion, float costo , int cupo) throws TipoRegistroYaExisteException, CupoTipoRegistroInvalidoException, CostoTipoRegistroInvalidoException; 
    void altaEdicionEventoDTO(
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
         excepciones.FechasCruzadasException;
    public logica.datatypes.DTPatrocinio obtenerDTPatrocinio(String codigoPatrocinio);
    public DTRegistro consultaRegistro(Usuario user, String idRegistro);
    public DTTipoRegistro consultaTipoRegistro(String nombreEvento, String nombreEdicion, String nombreTipoRegistro);
    public DTEdicion obtenerEdicionPorSiglaDT(String sigla);
    public List<DTCategorias> listarDTCategorias();
    DTPatrocinio altaPatrocinioDT(
    	    String siglaEdicion,
    	    String nombreInstitucion,
    	    logica.enumerados.DTNivel nivel,
    	    String nombreTipoRegistro,
    	    int aporte,
    	    java.time.LocalDate fechaPatrocinio,
    	    int cantidadRegistros,
    	    String codigoPatrocinio
    	) throws
    	    excepciones.ValorPatrocinioExcedidoException,
    	    excepciones.PatrocinioYaExisteException,
    	    IllegalArgumentException;
    public List<DTTipoRegistro> listarTiposRegistroDeEdicion(String evento, String edicion);
    public boolean esEventoVigente(String nombreEvento);
    public void finalizarEvento(String nombreEvento);
    public List<DTEvento> listarEventosVigentes();
    public void altaRegistroEdicionEventoDT(
            String idRegistro,
            String nicknameUsuario,
            String nombreEvento,
            String nombreEdicion,
            String nombreTipoRegistro,
            LocalDate fechaRegistro,
            float costo,
            LocalDate fechaInicio
    );
    public void marcarAsistencia(String nick, String idRegistro);
    public List<String> listarEdicionesArchivables(String organizadorNick); 
    public void archivarEdicion(String nombreEdicion); 
    public void marcarAsistencia(String idRegistro);
    public List<DTArchEdicion> edicionesArchivadasOrganizadas(String nickOrg);


}