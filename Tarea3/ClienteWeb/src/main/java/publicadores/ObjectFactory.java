
package publicadores;

import javax.xml.namespace.QName;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the publicadores package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _CostoTipoRegistroInvalidoException_QNAME = new QName("http://publicadores/", "CostoTipoRegistroInvalidoException");
    private final static QName _EdicionYaExisteException_QNAME = new QName("http://publicadores/", "EdicionYaExisteException");
    private final static QName _EventoYaExisteException_QNAME = new QName("http://publicadores/", "EventoYaExisteException");
    private final static QName _FechasCruzadasException_QNAME = new QName("http://publicadores/", "FechasCruzadasException");
    private final static QName _TipoRegistroYaExisteException_QNAME = new QName("http://publicadores/", "TipoRegistroYaExisteException");
    private final static QName _UsuarioNoExisteException_QNAME = new QName("http://publicadores/", "UsuarioNoExisteException");
    private final static QName _ValorPatrocinioExcedidoException_QNAME = new QName("http://publicadores/", "ValorPatrocinioExcedidoException");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: publicadores
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DtDatosUsuario }
     * 
     * @return
     *     the new instance of {@link DtDatosUsuario }
     */
    public DtDatosUsuario createDtDatosUsuario() {
        return new DtDatosUsuario();
    }

    /**
     * Create an instance of {@link Eventos }
     * 
     * @return
     *     the new instance of {@link Eventos }
     */
    public Eventos createEventos() {
        return new Eventos();
    }

    /**
     * Create an instance of {@link Eventos.Categorias }
     * 
     * @return
     *     the new instance of {@link Eventos.Categorias }
     */
    public Eventos.Categorias createEventosCategorias() {
        return new Eventos.Categorias();
    }

    /**
     * Create an instance of {@link DtCategorias }
     * 
     * @return
     *     the new instance of {@link DtCategorias }
     */
    public DtCategorias createDtCategorias() {
        return new DtCategorias();
    }

    /**
     * Create an instance of {@link DtEvento }
     * 
     * @return
     *     the new instance of {@link DtEvento }
     */
    public DtEvento createDtEvento() {
        return new DtEvento();
    }

    /**
     * Create an instance of {@link DtEdicion }
     * 
     * @return
     *     the new instance of {@link DtEdicion }
     */
    public DtEdicion createDtEdicion() {
        return new DtEdicion();
    }

    /**
     * Create an instance of {@link CostoTipoRegistroInvalidoException }
     * 
     * @return
     *     the new instance of {@link CostoTipoRegistroInvalidoException }
     */
    public CostoTipoRegistroInvalidoException createCostoTipoRegistroInvalidoException() {
        return new CostoTipoRegistroInvalidoException();
    }

    /**
     * Create an instance of {@link EdicionYaExisteException }
     * 
     * @return
     *     the new instance of {@link EdicionYaExisteException }
     */
    public EdicionYaExisteException createEdicionYaExisteException() {
        return new EdicionYaExisteException();
    }

    /**
     * Create an instance of {@link EventoYaExisteException }
     * 
     * @return
     *     the new instance of {@link EventoYaExisteException }
     */
    public EventoYaExisteException createEventoYaExisteException() {
        return new EventoYaExisteException();
    }

    /**
     * Create an instance of {@link FechasCruzadasException }
     * 
     * @return
     *     the new instance of {@link FechasCruzadasException }
     */
    public FechasCruzadasException createFechasCruzadasException() {
        return new FechasCruzadasException();
    }

    /**
     * Create an instance of {@link TipoRegistroYaExisteException }
     * 
     * @return
     *     the new instance of {@link TipoRegistroYaExisteException }
     */
    public TipoRegistroYaExisteException createTipoRegistroYaExisteException() {
        return new TipoRegistroYaExisteException();
    }

    /**
     * Create an instance of {@link UsuarioNoExisteException }
     * 
     * @return
     *     the new instance of {@link UsuarioNoExisteException }
     */
    public UsuarioNoExisteException createUsuarioNoExisteException() {
        return new UsuarioNoExisteException();
    }

    /**
     * Create an instance of {@link ValorPatrocinioExcedidoException }
     * 
     * @return
     *     the new instance of {@link ValorPatrocinioExcedidoException }
     */
    public ValorPatrocinioExcedidoException createValorPatrocinioExcedidoException() {
        return new ValorPatrocinioExcedidoException();
    }

    /**
     * Create an instance of {@link DtTipoRegistro }
     * 
     * @return
     *     the new instance of {@link DtTipoRegistro }
     */
    public DtTipoRegistro createDtTipoRegistro() {
        return new DtTipoRegistro();
    }

    /**
     * Create an instance of {@link DtPatrocinio }
     * 
     * @return
     *     the new instance of {@link DtPatrocinio }
     */
    public DtPatrocinio createDtPatrocinio() {
        return new DtPatrocinio();
    }

    /**
     * Create an instance of {@link DtRegistro }
     * 
     * @return
     *     the new instance of {@link DtRegistro }
     */
    public DtRegistro createDtRegistro() {
        return new DtRegistro();
    }

    /**
     * Create an instance of {@link Categoria }
     * 
     * @return
     *     the new instance of {@link Categoria }
     */
    public Categoria createCategoria() {
        return new Categoria();
    }

    /**
     * Create an instance of {@link publicadores.Ediciones }
     * 
     * @return
     *     the new instance of {@link publicadores.Ediciones }
     */
    public publicadores.Ediciones createEdiciones() {
        return new publicadores.Ediciones();
    }

    /**
     * Create an instance of {@link LocalDate }
     * 
     * @return
     *     the new instance of {@link LocalDate }
     */
    public LocalDate createLocalDate() {
        return new LocalDate();
    }

    /**
     * Create an instance of {@link TipoRegistro }
     * 
     * @return
     *     the new instance of {@link TipoRegistro }
     */
    public TipoRegistro createTipoRegistro() {
        return new TipoRegistro();
    }

    /**
     * Create an instance of {@link DTArchEdicion }
     * 
     * @return
     *     the new instance of {@link DTArchEdicion }
     */
    public DTArchEdicion createDTArchEdicion() {
        return new DTArchEdicion();
    }

    /**
     * Create an instance of {@link DtCategoriasArray }
     * 
     * @return
     *     the new instance of {@link DtCategoriasArray }
     */
    public DtCategoriasArray createDtCategoriasArray() {
        return new DtCategoriasArray();
    }

    /**
     * Create an instance of {@link DTArchEdicionArray }
     * 
     * @return
     *     the new instance of {@link DTArchEdicionArray }
     */
    public DTArchEdicionArray createDTArchEdicionArray() {
        return new DTArchEdicionArray();
    }

    /**
     * Create an instance of {@link DtEventoArray }
     * 
     * @return
     *     the new instance of {@link DtEventoArray }
     */
    public DtEventoArray createDtEventoArray() {
        return new DtEventoArray();
    }

    /**
     * Create an instance of {@link DtTipoRegistroArray }
     * 
     * @return
     *     the new instance of {@link DtTipoRegistroArray }
     */
    public DtTipoRegistroArray createDtTipoRegistroArray() {
        return new DtTipoRegistroArray();
    }

    /**
     * Create an instance of {@link StringArray }
     * 
     * @return
     *     the new instance of {@link StringArray }
     */
    public StringArray createStringArray() {
        return new StringArray();
    }

    /**
     * Create an instance of {@link DtDatosUsuario.Registros }
     * 
     * @return
     *     the new instance of {@link DtDatosUsuario.Registros }
     */
    public DtDatosUsuario.Registros createDtDatosUsuarioRegistros() {
        return new DtDatosUsuario.Registros();
    }

    /**
     * Create an instance of {@link DtDatosUsuario.Ediciones }
     * 
     * @return
     *     the new instance of {@link DtDatosUsuario.Ediciones }
     */
    public DtDatosUsuario.Ediciones createDtDatosUsuarioEdiciones() {
        return new DtDatosUsuario.Ediciones();
    }

    /**
     * Create an instance of {@link DtDatosUsuario.Seguidores }
     * 
     * @return
     *     the new instance of {@link DtDatosUsuario.Seguidores }
     */
    public DtDatosUsuario.Seguidores createDtDatosUsuarioSeguidores() {
        return new DtDatosUsuario.Seguidores();
    }

    /**
     * Create an instance of {@link DtDatosUsuario.Seguidos }
     * 
     * @return
     *     the new instance of {@link DtDatosUsuario.Seguidos }
     */
    public DtDatosUsuario.Seguidos createDtDatosUsuarioSeguidos() {
        return new DtDatosUsuario.Seguidos();
    }

    /**
     * Create an instance of {@link DtDatosUsuario.Asistencias }
     * 
     * @return
     *     the new instance of {@link DtDatosUsuario.Asistencias }
     */
    public DtDatosUsuario.Asistencias createDtDatosUsuarioAsistencias() {
        return new DtDatosUsuario.Asistencias();
    }

    /**
     * Create an instance of {@link Eventos.Categorias.Entry }
     * 
     * @return
     *     the new instance of {@link Eventos.Categorias.Entry }
     */
    public Eventos.Categorias.Entry createEventosCategoriasEntry() {
        return new Eventos.Categorias.Entry();
    }

    /**
     * Create an instance of {@link DtCategorias.Categorias }
     * 
     * @return
     *     the new instance of {@link DtCategorias.Categorias }
     */
    public DtCategorias.Categorias createDtCategoriasCategorias() {
        return new DtCategorias.Categorias();
    }

    /**
     * Create an instance of {@link DtEvento.Categorias }
     * 
     * @return
     *     the new instance of {@link DtEvento.Categorias }
     */
    public DtEvento.Categorias createDtEventoCategorias() {
        return new DtEvento.Categorias();
    }

    /**
     * Create an instance of {@link DtEvento.Ediciones }
     * 
     * @return
     *     the new instance of {@link DtEvento.Ediciones }
     */
    public DtEvento.Ediciones createDtEventoEdiciones() {
        return new DtEvento.Ediciones();
    }

    /**
     * Create an instance of {@link DtEdicion.TiposRegistro }
     * 
     * @return
     *     the new instance of {@link DtEdicion.TiposRegistro }
     */
    public DtEdicion.TiposRegistro createDtEdicionTiposRegistro() {
        return new DtEdicion.TiposRegistro();
    }

    /**
     * Create an instance of {@link DtEdicion.Patrocinios }
     * 
     * @return
     *     the new instance of {@link DtEdicion.Patrocinios }
     */
    public DtEdicion.Patrocinios createDtEdicionPatrocinios() {
        return new DtEdicion.Patrocinios();
    }

    /**
     * Create an instance of {@link DtEdicion.Registros }
     * 
     * @return
     *     the new instance of {@link DtEdicion.Registros }
     */
    public DtEdicion.Registros createDtEdicionRegistros() {
        return new DtEdicion.Registros();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CostoTipoRegistroInvalidoException }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link CostoTipoRegistroInvalidoException }{@code >}
     */
    @XmlElementDecl(namespace = "http://publicadores/", name = "CostoTipoRegistroInvalidoException")
    public JAXBElement<CostoTipoRegistroInvalidoException> createCostoTipoRegistroInvalidoException(CostoTipoRegistroInvalidoException value) {
        return new JAXBElement<>(_CostoTipoRegistroInvalidoException_QNAME, CostoTipoRegistroInvalidoException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EdicionYaExisteException }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link EdicionYaExisteException }{@code >}
     */
    @XmlElementDecl(namespace = "http://publicadores/", name = "EdicionYaExisteException")
    public JAXBElement<EdicionYaExisteException> createEdicionYaExisteException(EdicionYaExisteException value) {
        return new JAXBElement<>(_EdicionYaExisteException_QNAME, EdicionYaExisteException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EventoYaExisteException }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link EventoYaExisteException }{@code >}
     */
    @XmlElementDecl(namespace = "http://publicadores/", name = "EventoYaExisteException")
    public JAXBElement<EventoYaExisteException> createEventoYaExisteException(EventoYaExisteException value) {
        return new JAXBElement<>(_EventoYaExisteException_QNAME, EventoYaExisteException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FechasCruzadasException }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link FechasCruzadasException }{@code >}
     */
    @XmlElementDecl(namespace = "http://publicadores/", name = "FechasCruzadasException")
    public JAXBElement<FechasCruzadasException> createFechasCruzadasException(FechasCruzadasException value) {
        return new JAXBElement<>(_FechasCruzadasException_QNAME, FechasCruzadasException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TipoRegistroYaExisteException }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link TipoRegistroYaExisteException }{@code >}
     */
    @XmlElementDecl(namespace = "http://publicadores/", name = "TipoRegistroYaExisteException")
    public JAXBElement<TipoRegistroYaExisteException> createTipoRegistroYaExisteException(TipoRegistroYaExisteException value) {
        return new JAXBElement<>(_TipoRegistroYaExisteException_QNAME, TipoRegistroYaExisteException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UsuarioNoExisteException }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link UsuarioNoExisteException }{@code >}
     */
    @XmlElementDecl(namespace = "http://publicadores/", name = "UsuarioNoExisteException")
    public JAXBElement<UsuarioNoExisteException> createUsuarioNoExisteException(UsuarioNoExisteException value) {
        return new JAXBElement<>(_UsuarioNoExisteException_QNAME, UsuarioNoExisteException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ValorPatrocinioExcedidoException }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ValorPatrocinioExcedidoException }{@code >}
     */
    @XmlElementDecl(namespace = "http://publicadores/", name = "ValorPatrocinioExcedidoException")
    public JAXBElement<ValorPatrocinioExcedidoException> createValorPatrocinioExcedidoException(ValorPatrocinioExcedidoException value) {
        return new JAXBElement<>(_ValorPatrocinioExcedidoException_QNAME, ValorPatrocinioExcedidoException.class, null, value);
    }

}
