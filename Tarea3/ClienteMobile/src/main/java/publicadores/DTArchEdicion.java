
package publicadores;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DTArchEdicion complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="DTArchEdicion">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="nombreEvento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="siglaEdicion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="fechaInicio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="fechaFin" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="fechaArchivado" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="organizadorNick" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DTArchEdicion", propOrder = {
    "nombreEvento",
    "siglaEdicion",
    "fechaInicio",
    "fechaFin",
    "fechaArchivado",
    "organizadorNick"
})
public class DTArchEdicion {

    protected String nombreEvento;
    protected String siglaEdicion;
    protected String fechaInicio;
    protected String fechaFin;
    protected String fechaArchivado;
    protected String organizadorNick;

    /**
     * Gets the value of the nombreEvento property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNombreEvento() {
        return nombreEvento;
    }

    /**
     * Sets the value of the nombreEvento property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNombreEvento(String value) {
        this.nombreEvento = value;
    }

    /**
     * Gets the value of the siglaEdicion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSiglaEdicion() {
        return siglaEdicion;
    }

    /**
     * Sets the value of the siglaEdicion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSiglaEdicion(String value) {
        this.siglaEdicion = value;
    }

    /**
     * Gets the value of the fechaInicio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFechaInicio() {
        return fechaInicio;
    }

    /**
     * Sets the value of the fechaInicio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFechaInicio(String value) {
        this.fechaInicio = value;
    }

    /**
     * Gets the value of the fechaFin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFechaFin() {
        return fechaFin;
    }

    /**
     * Sets the value of the fechaFin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFechaFin(String value) {
        this.fechaFin = value;
    }

    /**
     * Gets the value of the fechaArchivado property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFechaArchivado() {
        return fechaArchivado;
    }

    /**
     * Sets the value of the fechaArchivado property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFechaArchivado(String value) {
        this.fechaArchivado = value;
    }

    /**
     * Gets the value of the organizadorNick property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrganizadorNick() {
        return organizadorNick;
    }

    /**
     * Sets the value of the organizadorNick property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrganizadorNick(String value) {
        this.organizadorNick = value;
    }

}
