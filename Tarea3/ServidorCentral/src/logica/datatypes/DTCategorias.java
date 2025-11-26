package logica.datatypes;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;

@XmlAccessorType(XmlAccessType.FIELD)
public class DTCategorias implements Serializable {

    @XmlElementWrapper(name = "categorias")
    @XmlElement(name = "categoria")
    private List<String> categorias;

    public DTCategorias() {
        this.categorias = new ArrayList<>();
    }

    public DTCategorias(List<String> categorias) {
        this.categorias = categorias;
    }

    public List<String> getCategorias() {
        return categorias;
    }
}