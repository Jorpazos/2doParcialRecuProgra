package ar.edu.usal.publicidad.domain;


public class OpcionMenu {

    private String url;
    private String texto;

    // Constructor vacio
    public OpcionMenu() {
    }


    public OpcionMenu(String url, String texto) {
        this.url = url;
        this.texto = texto;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }
}