package ar.edu.usal.publicidad.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// Representa un diseñador. La clave es el legajo
public class Disenador {

    private int legajo;
    private String nombre;
    private String apellido;
    private String dni;
    private Date fechaNacimiento;
    private Especializacion especializacion;   // objeto, no un texto suelto
    private String celular;
    // Las cuentas de clientes para las que esta autorizado
    private List<Cuenta> cuentas = new ArrayList<>();
    // Las plataformas que puede usar
    private List<Plataforma> plataformas = new ArrayList<>();


    public Disenador() {
    }

    public Disenador(int legajo, String nombre, String apellido, String dni,
                     Date fechaNacimiento, Especializacion especializacion, String celular) {
        this.legajo = legajo;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.fechaNacimiento = fechaNacimiento;
        this.especializacion = especializacion;
        this.celular = celular;
    }

    public int getLegajo() {
        return legajo;
    }

    public void setLegajo(int legajo) {
        this.legajo = legajo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public Especializacion getEspecializacion() {
        return especializacion;
    }

    public void setEspecializacion(Especializacion especializacion) {
        this.especializacion = especializacion;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public List<Cuenta> getCuentas() {
        return cuentas;
    }

    public void setCuentas(List<Cuenta> cuentas) {
        this.cuentas = cuentas;
    }

    public List<Plataforma> getPlataformas() {
        return plataformas;
    }

    public void setPlataformas(List<Plataforma> plataformas) {
        this.plataformas = plataformas;
    }
}