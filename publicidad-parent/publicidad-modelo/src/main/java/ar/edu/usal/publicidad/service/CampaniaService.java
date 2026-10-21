package ar.edu.usal.publicidad.service;

import ar.edu.usal.publicidad.dao.CampaniaDAO;
import ar.edu.usal.publicidad.dao.CuentaDAO;
import ar.edu.usal.publicidad.dao.DisenadorDAO;
import ar.edu.usal.publicidad.dao.PlataformaDAO;
import ar.edu.usal.publicidad.dao.TipoContenidoDAO;
import ar.edu.usal.publicidad.dao.factory.CampaniaDAOFactory;
import ar.edu.usal.publicidad.dao.factory.CuentaDAOFactory;
import ar.edu.usal.publicidad.dao.factory.DisenadorDAOFactory;
import ar.edu.usal.publicidad.dao.factory.PlataformaDAOFactory;
import ar.edu.usal.publicidad.dao.factory.TipoContenidoDAOFactory;
import ar.edu.usal.publicidad.domain.Campania;
import ar.edu.usal.publicidad.domain.Cuenta;
import ar.edu.usal.publicidad.domain.Disenador;
import ar.edu.usal.publicidad.domain.EstadoCampania;
import ar.edu.usal.publicidad.domain.Plataforma;
import ar.edu.usal.publicidad.domain.TipoContenido;
import ar.edu.usal.publicidad.exception.DAOException;
import ar.edu.usal.publicidad.exception.ValidacionException;

import java.util.List;

// Service de campañas: aca estan las reglas del negocio y los calculos
// (costo total de la pauta y tiempo de produccion).
public class CampaniaService {

    // Los DAO los pido a sus factories
    private CampaniaDAO campaniaDAO = CampaniaDAOFactory.getCampaniaDAO();
    private DisenadorDAO disenadorDAO = DisenadorDAOFactory.getDisenadorDAO();
    private PlataformaDAO plataformaDAO = PlataformaDAOFactory.getPlataformaDAO();
    private CuentaDAO cuentaDAO = CuentaDAOFactory.getCuentaDAO();
    private TipoContenidoDAO tipoContenidoDAO = TipoContenidoDAOFactory.getTipoContenidoDAO();

    // ALTA: el ejecutivo elige diseñador (por legajo), cuenta, plataforma y tipo de contenido.
    // Devuelve la campaña ya guardada, con su costo y sus horas calculados.
    public Campania asignar(int legajo, String nombreCuenta, String nombrePlataforma, String nombreTipo)
            throws ValidacionException, DAOException {
        if (nombreCuenta == null || nombreCuenta.isEmpty() || nombrePlataforma == null
                || nombrePlataforma.isEmpty() || nombreTipo == null || nombreTipo.isEmpty()) {
            throw new ValidacionException("Elija cuenta, plataforma y tipo de contenido");
        }
        Disenador disenador = disenadorDAO.buscarPorLegajo(legajo);
        if (disenador == null) {
            throw new ValidacionException("No existe un diseñador con ese legajo");
        }
        // La cuenta tiene que estar entre las que el diseñador tiene autorizadas
        Cuenta cuenta = null;
        for (Cuenta c : disenador.getCuentas()) {
            if (c.getNombre().equals(nombreCuenta)) {
                cuenta = c;
            }
        }
        if (cuenta == null) {
            throw new ValidacionException("El diseñador no esta autorizado para esa cuenta");
        }
        // La plataforma tiene que estar entre las que puede gestionar y que no esten en campaña activa
        Plataforma plataforma = null;
        for (Plataforma p : plataformaDAO.listarDisponiblesDeDisenador(legajo)) {
            if (p.getNombre().equals(nombrePlataforma)) {
                plataforma = p;
            }
        }
        if (plataforma == null) {
            throw new ValidacionException("La plataforma no esta disponible para ese diseñador");
        }
        TipoContenido tipo = tipoContenidoDAO.buscarPorNombre(nombreTipo);
        if (tipo == null) {
            throw new ValidacionException("El tipo de contenido no existe");
        }

        Campania campania = new Campania(disenador, cuenta, plataforma, tipo);
        campania.setEstado(EstadoCampania.PENDIENTE);   // toda campaña nueva empieza pendiente
        campania.setCostoTotal(calcularCosto(plataforma, tipo));
        campania.setHorasProduccion(calcularHoras(tipo));

        // El DAO devuelve false si en el medio otra persona ocupo la plataforma
        if (!campaniaDAO.agregar(campania)) {
            throw new ValidacionException("La plataforma ya esta en una campaña activa");
        }
        return campania;
    }

    // MODIFICACION: solo se puede cambiar la cuenta y el tipo de contenido de una campaña pendiente.
    public boolean modificar(int id, String nombreCuenta, String nombreTipo)
            throws ValidacionException, DAOException {
        Campania campania = campaniaDAO.buscarPorId(id);
        if (campania == null) {
            throw new ValidacionException("La campaña no existe");
        }
        if (campania.getEstado() != EstadoCampania.PENDIENTE) {
            throw new ValidacionException("Solo se pueden modificar campañas pendientes");
        }
        Cuenta cuenta = null;
        for (Cuenta c : cuentaDAO.listarDeDisenador(campania.getDisenador().getLegajo())) {
            if (c.getNombre().equals(nombreCuenta)) {
                cuenta = c;
            }
        }
        if (cuenta == null) {
            throw new ValidacionException("El diseñador no esta autorizado para esa cuenta");
        }
        TipoContenido tipo = tipoContenidoDAO.buscarPorNombre(nombreTipo);
        if (tipo == null) {
            throw new ValidacionException("El tipo de contenido no existe");
        }
        campania.setCuenta(cuenta);
        campania.setTipoContenido(tipo);
        // Como cambio el tipo, recalculo el costo y las horas
        campania.setCostoTotal(calcularCosto(campania.getPlataforma(), tipo));
        campania.setHorasProduccion(calcularHoras(tipo));
        return campaniaDAO.modificar(campania);
    }

    // BAJA: no se puede borrar una campaña que esta en marcha
    public boolean eliminar(int id) throws ValidacionException, DAOException {
        Campania campania = campaniaDAO.buscarPorId(id);
        if (campania == null) {
            throw new ValidacionException("La campaña no existe");
        }
        if (campania.getEstado() == EstadoCampania.INICIADA) {
            throw new ValidacionException("No se puede eliminar una campaña iniciada");
        }
        return campaniaDAO.eliminar(id);
    }

    // El diseñador marca su campaña como iniciada (solo si esta pendiente)
    public boolean iniciar(int id, int legajo) throws ValidacionException, DAOException {
        Campania campania = buscarDelDisenador(id, legajo);
        if (campania.getEstado() != EstadoCampania.PENDIENTE) {
            throw new ValidacionException("Solo se puede iniciar una campaña pendiente");
        }
        return campaniaDAO.cambiarEstado(id, EstadoCampania.INICIADA);
    }

    // El diseñador marca su campaña como finalizada (solo si esta iniciada)
    public boolean finalizar(int id, int legajo) throws ValidacionException, DAOException {
        Campania campania = buscarDelDisenador(id, legajo);
        if (campania.getEstado() != EstadoCampania.INICIADA) {
            throw new ValidacionException("Solo se puede finalizar una campaña iniciada");
        }
        return campaniaDAO.cambiarEstado(id, EstadoCampania.FINALIZADA);
    }

    public Campania buscar(int id) throws DAOException {
        return campaniaDAO.buscarPorId(id);
    }

    public List<Campania> listar() throws DAOException {
        return campaniaDAO.listar();
    }

    // Un diseñador solo ve sus propias campañas
    public List<Campania> listarDeDisenador(int legajo) throws DAOException {
        return campaniaDAO.listarDeDisenador(legajo);
    }

    public List<TipoContenido> listarTipos() throws DAOException {
        return tipoContenidoDAO.listar();
    }

    // COSTO TOTAL ESTIMADO DE LA PAUTA. El enunciado no da la formula exacta, uso esta:
    // alcance (en millones) x CPC x factor del tipo de contenido + costo fijo de publicacion
    public double calcularCosto(Plataforma plataforma, TipoContenido tipo) {
        double costo = plataforma.getAlcanceMaximo() * plataforma.getCpc() * tipo.getFactor()
                + plataforma.getCostoFijo();
        return Math.round(costo * 100) / 100.0;   // lo redondeo a 2 decimales
    }

    // TIEMPO DE PRODUCCION: son las horas que dice la tabla para ese tipo de contenido
    public int calcularHoras(TipoContenido tipo) {
        return tipo.getHorasProduccion();
    }

    // Busca una campaña y comprueba que sea del diseñador que la quiere cambiar
    private Campania buscarDelDisenador(int id, int legajo) throws ValidacionException, DAOException {
        Campania campania = campaniaDAO.buscarPorId(id);
        if (campania == null || campania.getDisenador().getLegajo() != legajo) {
            throw new ValidacionException("No tiene una campaña con ese numero");
        }
        return campania;
    }
}
