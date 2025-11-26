package logica.clases;

import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;

public class Asistente extends Usuario {
    private String apellido;
    private LocalDate fechaDeNacimiento;
    private Institucion institucion;
    private Map<String, Registro> registros;
    private Map<String, Registro> asistencias = new HashMap<>();
    
    public Asistente(String nickname, String nombre, String email, String contraseña, String imagen, String apellido, LocalDate fechaDeNacimiento, Institucion institucion) {
    	
    	super(nickname, nombre, email, contraseña);
    	this.apellido = apellido;
    	this.fechaDeNacimiento = fechaDeNacimiento;
    	this.institucion = institucion;
    	this.registros = new HashMap<String, Registro>();
    }
    
    public Map<String, Registro> getRegistros(){
    	return registros;
    }
    
    public void addRegistro(String ident, Registro registro) {
    	this.registros.put(ident, registro);
    }
    
    public Institucion getInstitucion() {
    	return institucion;
    }
    
    public void addInstitucion(Institucion institucion) {
    	this.institucion = institucion;
    }
    
    public void setInstitucion(Institucion institucion) {
        this.institucion = institucion;
    }
    
    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public LocalDate getFechaDeNacimiento() {
        return fechaDeNacimiento;
    }

    public void setFechaDeNacimiento(LocalDate fechaDeNacimiento) {
        this.fechaDeNacimiento = fechaDeNacimiento;
    }
    
    public Map<String, Registro> getAsistencias() {
        return asistencias;
    }

    public void addAsistencia(String ident, Registro registro) {
        this.asistencias.put(ident, registro);
    }
    // La necesitamos para que la clase usuario sea abstracta
    @Override
    public String getTipoUsuario() {
        return "Asistente";
    }
}