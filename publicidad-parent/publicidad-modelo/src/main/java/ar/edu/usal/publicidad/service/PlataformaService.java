package ar.edu.usal.publicidad.service;

import ar.edu.usal.publicidad.dao.PlataformaDAO;
import ar.edu.usal.publicidad.dao.factory.PlataformaDAOFactory;
import ar.edu.usal.publicidad.domain.Plataforma;
import ar.edu.usal.publicidad.exception.DAOException;
import ar.edu.usal.publicidad.exception.ValidacionException;

import java.util.List;

// Service de plataformas: validaciones y reglas del ABM de plataformas.
public class PlataformaService {

    // El DAO lo pido al factory, no hago new PlataformaDAOJdbc()
    private PlataformaDAO plataformaDAO = PlataformaDAOFactory.getPlataformaDAO();

    public boolean agregar(Plataforma plataforma) throws ValidacionException, DAOException {
        validar(plataforma);
        // El nombre es la clave: no puede haber dos plataformas con el mismo nombre
        if (plataformaDAO.buscarPorNombre(plataforma.getNombre()) != null) {
            throw new ValidacionException("Ya existe una plataforma con ese nombre");
        }
        return plataformaDAO.agregar(plataforma);
    }

    public boolean modificar(Plataforma plataforma) throws ValidacionException, DAOException {
        validar(plataforma);
        if (plataformaDAO.buscarPorNombre(plataforma.getNombre()) == null) {
            throw new ValidacionException("La plataforma que quiere modificar no existe");
        }
        return plataformaDAO.modificar(plataforma);
    }

    public boolean eliminar(String nombre) throws ValidacionException, DAOException {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ValidacionException("Falta indicar la plataforma");
        }
        if (plataformaDAO.buscarPorNombre(nombre) == null) {
            throw new ValidacionException("La plataforma que quiere eliminar no existe");
        }
        return plataformaDAO.eliminar(nombre);
    }

    public Plataforma buscar(String nombre) throws DAOException {
        return plataformaDAO.buscarPorNombre(nombre);
    }

    public List<Plataforma> listar() throws DAOException {
        return plataformaDAO.listar();
    }

    // Las plataformas que el diseñador puede usar y que estan libres (sin campaña activa)
    public List<Plataforma> listarDisponiblesDeDisenador(int legajo) throws DAOException {
        return plataformaDAO.listarDisponiblesDeDisenador(legajo);
    }

    // Revisa que los datos de la plataforma sean validos. Si algo esta mal lanza
    // ValidacionException con el mensaje que se le va a mostrar al usuario.
    private boolean validar(Plataforma p) throws ValidacionException {
        if (p == null || p.getNombre() == null || p.getNombre().trim().isEmpty()) {
            throw new ValidacionException("El nombre de la plataforma es obligatorio");
        }
        if (p.getFormatoPrincipal() == null || p.getFormatoPrincipal().trim().isEmpty()) {
            throw new ValidacionException("El formato principal es obligatorio");
        }
        if (p.getAlcanceMaximo() <= 0) {
            throw new ValidacionException("El alcance maximo debe ser mayor a 0 (en millones de usuarios)");
        }
        if (p.getCostoFijo() < 0) {
            throw new ValidacionException("El costo fijo no puede ser negativo");
        }
        if (p.getCpc() < 0) {
            throw new ValidacionException("El CPC no puede ser negativo");
        }
        p.setNombre(p.getNombre().trim());
        p.setFormatoPrincipal(p.getFormatoPrincipal().trim());
        return true;
    }
}
