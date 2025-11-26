package logica.datatypes;

import java.util.List;

public class DTCategorias {
    private List<String> categorias;

    public DTCategorias(List<String> categorias) {
        this.categorias = categorias;
    }

    public List<String> getCategorias() {
        return categorias;
    }
}
