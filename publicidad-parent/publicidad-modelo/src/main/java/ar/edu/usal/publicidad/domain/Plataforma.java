package ar.edu.usal.publicidad.domain;

public class Plataforma {

    private String nombre;
    private String formatoPrincipal;
    private double alcanceMaximo;   // en millones de usuarios
    private double costoFijo;       // costo de publicacion fijo
    private double cpc;             // costo por clic estimado


    public Plataforma() {
    }

    public Plataforma(String nombre, String formatoPrincipal, double alcanceMaximo,
                      double costoFijo, double cpc) {
        this.nombre = nombre;
        this.formatoPrincipal = formatoPrincipal;
        this.alcanceMaximo = alcanceMaximo;
        this.costoFijo = costoFijo;
        this.cpc = cpc;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFormatoPrincipal() {
        return formatoPrincipal;
    }

    public void setFormatoPrincipal(String formatoPrincipal) {
        this.formatoPrincipal = formatoPrincipal;
    }

    public double getAlcanceMaximo() {
        return alcanceMaximo;
    }

    public void setAlcanceMaximo(double alcanceMaximo) {
        this.alcanceMaximo = alcanceMaximo;
    }

    public double getCostoFijo() {
        return costoFijo;
    }

    public void setCostoFijo(double costoFijo) {
        this.costoFijo = costoFijo;
    }

    public double getCpc() {
        return cpc;
    }

    public void setCpc(double cpc) {
        this.cpc = cpc;
    }
}