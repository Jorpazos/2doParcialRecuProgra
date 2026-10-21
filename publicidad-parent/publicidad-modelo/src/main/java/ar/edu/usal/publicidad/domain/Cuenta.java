package ar.edu.usal.publicidad.domain;

public class Cuenta {

    private String nombre;


    public Cuenta() {
    }


    public Cuenta(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}