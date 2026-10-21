package ar.edu.usal.publicidad.domain;

public class Campania {

    private int id;
    private Disenador disenador;
    private Cuenta cuenta;
    private Plataforma plataforma;
    private TipoContenido tipoContenido;
    private double costoTotal;       // costo total estimado
    private int horasProduccion;     // tiempo de produccion estimado
    private EstadoCampania estado;


    public Campania() {
    }


    public Campania(Disenador disenador, Cuenta cuenta, Plataforma plataforma,
                    TipoContenido tipoContenido) {
        this.disenador = disenador;
        this.cuenta = cuenta;
        this.plataforma = plataforma;
        this.tipoContenido = tipoContenido;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Disenador getDisenador() {
        return disenador;
    }

    public void setDisenador(Disenador disenador) {
        this.disenador = disenador;
    }

    public Cuenta getCuenta() {
        return cuenta;
    }

    public void setCuenta(Cuenta cuenta) {
        this.cuenta = cuenta;
    }

    public Plataforma getPlataforma() {
        return plataforma;
    }

    public void setPlataforma(Plataforma plataforma) {
        this.plataforma = plataforma;
    }

    public TipoContenido getTipoContenido() {
        return tipoContenido;
    }

    public void setTipoContenido(TipoContenido tipoContenido) {
        this.tipoContenido = tipoContenido;
    }

    public double getCostoTotal() {
        return costoTotal;
    }

    public void setCostoTotal(double costoTotal) {
        this.costoTotal = costoTotal;
    }

    public int getHorasProduccion() {
        return horasProduccion;
    }

    public void setHorasProduccion(int horasProduccion) {
        this.horasProduccion = horasProduccion;
    }

    public EstadoCampania getEstado() {
        return estado;
    }

    public void setEstado(EstadoCampania estado) {
        this.estado = estado;
    }
}