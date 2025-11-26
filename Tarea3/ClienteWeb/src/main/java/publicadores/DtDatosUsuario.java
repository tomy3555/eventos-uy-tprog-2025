
package publicadores;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for dtDatosUsuario complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="dtDatosUsuario">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="nickname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="nombre" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="email" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="apellido" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="fechaNac" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="nombreInstitucion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="desc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="link" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="registros" minOccurs="0">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <sequence>
 *                   <element name="registro" type="{http://publicadores/}dtRegistro" maxOccurs="unbounded" minOccurs="0"/>
 *                 </sequence>
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="ediciones" minOccurs="0">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <sequence>
 *                   <element name="edicion" type="{http://publicadores/}dtEdicion" maxOccurs="unbounded" minOccurs="0"/>
 *                 </sequence>
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="imagen" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="seguidores" minOccurs="0">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <sequence>
 *                   <element name="seguidor" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *                 </sequence>
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="seguidos" minOccurs="0">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <sequence>
 *                   <element name="seguido" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *                 </sequence>
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="asistencias" minOccurs="0">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <sequence>
 *                   <element name="asistencia" type="{http://publicadores/}dtRegistro" maxOccurs="unbounded" minOccurs="0"/>
 *                 </sequence>
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dtDatosUsuario", propOrder = {
    "nickname",
    "nombre",
    "email",
    "apellido",
    "fechaNac",
    "nombreInstitucion",
    "desc",
    "link",
    "registros",
    "ediciones",
    "imagen",
    "seguidores",
    "seguidos",
    "asistencias"
})
public class DtDatosUsuario {

    protected String nickname;
    protected String nombre;
    protected String email;
    protected String apellido;
    protected String fechaNac;
    protected String nombreInstitucion;
    protected String desc;
    protected String link;
    protected DtDatosUsuario.Registros registros;
    protected DtDatosUsuario.Ediciones ediciones;
    protected String imagen;
    protected DtDatosUsuario.Seguidores seguidores;
    protected DtDatosUsuario.Seguidos seguidos;
    protected DtDatosUsuario.Asistencias asistencias;

    /**
     * Gets the value of the nickname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Sets the value of the nickname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNickname(String value) {
        this.nickname = value;
    }

    /**
     * Gets the value of the nombre property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Sets the value of the nombre property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNombre(String value) {
        this.nombre = value;
    }

    /**
     * Gets the value of the email property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the value of the email property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmail(String value) {
        this.email = value;
    }

    /**
     * Gets the value of the apellido property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getApellido() {
        return apellido;
    }

    /**
     * Sets the value of the apellido property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setApellido(String value) {
        this.apellido = value;
    }

    /**
     * Gets the value of the fechaNac property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFechaNac() {
        return fechaNac;
    }

    /**
     * Sets the value of the fechaNac property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFechaNac(String value) {
        this.fechaNac = value;
    }

    /**
     * Gets the value of the nombreInstitucion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNombreInstitucion() {
        return nombreInstitucion;
    }

    /**
     * Sets the value of the nombreInstitucion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNombreInstitucion(String value) {
        this.nombreInstitucion = value;
    }

    /**
     * Gets the value of the desc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Sets the value of the desc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDesc(String value) {
        this.desc = value;
    }

    /**
     * Gets the value of the link property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLink() {
        return link;
    }

    /**
     * Sets the value of the link property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLink(String value) {
        this.link = value;
    }

    /**
     * Gets the value of the registros property.
     * 
     * @return
     *     possible object is
     *     {@link DtDatosUsuario.Registros }
     *     
     */
    public DtDatosUsuario.Registros getRegistros() {
        return registros;
    }

    /**
     * Sets the value of the registros property.
     * 
     * @param value
     *     allowed object is
     *     {@link DtDatosUsuario.Registros }
     *     
     */
    public void setRegistros(DtDatosUsuario.Registros value) {
        this.registros = value;
    }

    /**
     * Gets the value of the ediciones property.
     * 
     * @return
     *     possible object is
     *     {@link DtDatosUsuario.Ediciones }
     *     
     */
    public DtDatosUsuario.Ediciones getEdiciones() {
        return ediciones;
    }

    /**
     * Sets the value of the ediciones property.
     * 
     * @param value
     *     allowed object is
     *     {@link DtDatosUsuario.Ediciones }
     *     
     */
    public void setEdiciones(DtDatosUsuario.Ediciones value) {
        this.ediciones = value;
    }

    /**
     * Gets the value of the imagen property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getImagen() {
        return imagen;
    }

    /**
     * Sets the value of the imagen property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImagen(String value) {
        this.imagen = value;
    }

    /**
     * Gets the value of the seguidores property.
     * 
     * @return
     *     possible object is
     *     {@link DtDatosUsuario.Seguidores }
     *     
     */
    public DtDatosUsuario.Seguidores getSeguidores() {
        return seguidores;
    }

    /**
     * Sets the value of the seguidores property.
     * 
     * @param value
     *     allowed object is
     *     {@link DtDatosUsuario.Seguidores }
     *     
     */
    public void setSeguidores(DtDatosUsuario.Seguidores value) {
        this.seguidores = value;
    }

    /**
     * Gets the value of the seguidos property.
     * 
     * @return
     *     possible object is
     *     {@link DtDatosUsuario.Seguidos }
     *     
     */
    public DtDatosUsuario.Seguidos getSeguidos() {
        return seguidos;
    }

    /**
     * Sets the value of the seguidos property.
     * 
     * @param value
     *     allowed object is
     *     {@link DtDatosUsuario.Seguidos }
     *     
     */
    public void setSeguidos(DtDatosUsuario.Seguidos value) {
        this.seguidos = value;
    }

    /**
     * Gets the value of the asistencias property.
     * 
     * @return
     *     possible object is
     *     {@link DtDatosUsuario.Asistencias }
     *     
     */
    public DtDatosUsuario.Asistencias getAsistencias() {
        return asistencias;
    }

    /**
     * Sets the value of the asistencias property.
     * 
     * @param value
     *     allowed object is
     *     {@link DtDatosUsuario.Asistencias }
     *     
     */
    public void setAsistencias(DtDatosUsuario.Asistencias value) {
        this.asistencias = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>{@code
     * <complexType>
     *   <complexContent>
     *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       <sequence>
     *         <element name="asistencia" type="{http://publicadores/}dtRegistro" maxOccurs="unbounded" minOccurs="0"/>
     *       </sequence>
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "asistencia"
    })
    public static class Asistencias {

        protected List<DtRegistro> asistencia;

        /**
         * Gets the value of the asistencia property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the Jakarta XML Binding object.
         * This is why there is not a {@code set} method for the asistencia property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getAsistencia().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link DtRegistro }
         * 
         * 
         * @return
         *     The value of the asistencia property.
         */
        public List<DtRegistro> getAsistencia() {
            if (asistencia == null) {
                asistencia = new ArrayList<>();
            }
            return this.asistencia;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>{@code
     * <complexType>
     *   <complexContent>
     *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       <sequence>
     *         <element name="edicion" type="{http://publicadores/}dtEdicion" maxOccurs="unbounded" minOccurs="0"/>
     *       </sequence>
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "edicion"
    })
    public static class Ediciones {

        protected List<DtEdicion> edicion;

        /**
         * Gets the value of the edicion property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the Jakarta XML Binding object.
         * This is why there is not a {@code set} method for the edicion property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEdicion().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link DtEdicion }
         * 
         * 
         * @return
         *     The value of the edicion property.
         */
        public List<DtEdicion> getEdicion() {
            if (edicion == null) {
                edicion = new ArrayList<>();
            }
            return this.edicion;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>{@code
     * <complexType>
     *   <complexContent>
     *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       <sequence>
     *         <element name="registro" type="{http://publicadores/}dtRegistro" maxOccurs="unbounded" minOccurs="0"/>
     *       </sequence>
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "registro"
    })
    public static class Registros {

        protected List<DtRegistro> registro;

        /**
         * Gets the value of the registro property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the Jakarta XML Binding object.
         * This is why there is not a {@code set} method for the registro property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getRegistro().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link DtRegistro }
         * 
         * 
         * @return
         *     The value of the registro property.
         */
        public List<DtRegistro> getRegistro() {
            if (registro == null) {
                registro = new ArrayList<>();
            }
            return this.registro;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>{@code
     * <complexType>
     *   <complexContent>
     *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       <sequence>
     *         <element name="seguidor" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
     *       </sequence>
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "seguidor"
    })
    public static class Seguidores {

        protected List<String> seguidor;

        /**
         * Gets the value of the seguidor property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the Jakarta XML Binding object.
         * This is why there is not a {@code set} method for the seguidor property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getSeguidor().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link String }
         * 
         * 
         * @return
         *     The value of the seguidor property.
         */
        public List<String> getSeguidor() {
            if (seguidor == null) {
                seguidor = new ArrayList<>();
            }
            return this.seguidor;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>{@code
     * <complexType>
     *   <complexContent>
     *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       <sequence>
     *         <element name="seguido" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
     *       </sequence>
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "seguido"
    })
    public static class Seguidos {

        protected List<String> seguido;

        /**
         * Gets the value of the seguido property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the Jakarta XML Binding object.
         * This is why there is not a {@code set} method for the seguido property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getSeguido().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link String }
         * 
         * 
         * @return
         *     The value of the seguido property.
         */
        public List<String> getSeguido() {
            if (seguido == null) {
                seguido = new ArrayList<>();
            }
            return this.seguido;
        }

    }

}
