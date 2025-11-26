package logica.datatypes;

public class DTTipoRegistro {
    private String nombre;
    private String descripcion;
    private float costo;
    private int cupo;

    public DTTipoRegistro(String nombre, String descripcion, float costo, int cupo) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.costo = costo;
        this.cupo = cupo;
    }

    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public float getCosto() { return costo; }
    public int getCupo() { return cupo; }
}
