package ar.edu.usal.publicidad.domain;

import java.util.ArrayList;
import java.util.List;


public class Usuario {

    private String usuario;
    private String password;
    private Perfil perfil;           // Ejecutivo de cuentas o Diseñador (viene de la base)
    private Disenador disenador;     // solo si es diseñador; si es ejecutivo queda en null
    // Las opciones del menu que le corresponden segun su perfil
    private List<OpcionMenu> opciones = new ArrayList<>();


    public Usuario() {
    }

    public Usuario(String usuario, String password, Perfil perfil) {
        this.usuario = usuario;
        this.password = password;
        this.perfil = perfil;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
    }

    public Disenador getDisenador() {
        return disenador;
    }

    public void setDisenador(Disenador disenador) {
        this.disenador = disenador;
    }

    public List<OpcionMenu> getOpciones() {
        return opciones;
    }

    public void setOpciones(List<OpcionMenu> opciones) {
        this.opciones = opciones;
    }
}