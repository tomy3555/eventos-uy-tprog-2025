package logica.clases;

public class TipoRegistro {
    private String nombre;
    private String descripcion;
    private float costo;
    private int cupo;
    private Ediciones edicion;

    public TipoRegistro(Ediciones edicion, String nombre, String descripcion, float costo, int cupo) {
        this.edicion = edicion;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.costo = costo;
        this.cupo = cupo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
      this.descripcion = descripcion;
    }

    public float getCosto() {
        return costo;
    }

    //public void setCosto(int costo) {
    //    this.costo = costo;
    //}

    public int getCupo() {
        return cupo;
    }

    //public void setCupo(int cupo) {
    //    this.cupo = cupo;
    //}

    public Ediciones getEdicion() {
        return edicion;
    }

    public void setEdicion(Ediciones edicion) {
        this.edicion = edicion;
    }
}