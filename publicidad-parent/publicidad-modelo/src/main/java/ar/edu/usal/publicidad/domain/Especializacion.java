package ar.edu.usal.publicidad.domain;

public class Especializacion {


    private String nombre;

    public Especializacion() {
    }


    public Especializacion(String nombre) {
        this.nombre = nombre;   // this.nombre es el atributo, nombre es el parametro
    }


    public String getNombre() {
        return nombre;
    }


    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}