package ar.edu.usal.publicidad.domain;

public class Perfil {

    private String nombre;
    private String urlInicio;


    public Perfil() {
    }


    public Perfil(String nombre, String urlInicio) {
        this.nombre = nombre;
        this.urlInicio = urlInicio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUrlInicio() {
        return urlInicio;
    }

    public void setUrlInicio(String urlInicio) {
        this.urlInicio = urlInicio;
    }
}