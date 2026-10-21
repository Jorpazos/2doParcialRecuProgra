package ar.edu.usal.publicidad.domain;


public class TipoContenido {

    private String nombre;
    private int horasProduccion;
    private double factor;         // factor de costo del tipo de contenido

    // Constructor vacio
    public TipoContenido() {
    }


    public TipoContenido(String nombre, int horasProduccion, double factor) {
        this.nombre = nombre;
        this.horasProduccion = horasProduccion;
        this.factor = factor;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getHorasProduccion() {
        return horasProduccion;
    }

    public void setHorasProduccion(int horasProduccion) {
        this.horasProduccion = horasProduccion;
    }

    public double getFactor() {
        return factor;
    }

    public void setFactor(double factor) {
        this.factor = factor;
    }
}