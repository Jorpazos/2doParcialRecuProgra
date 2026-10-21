package ar.edu.usal.publicidad.service;

import ar.edu.usal.publicidad.dao.CampaniaDAO;
import ar.edu.usal.publicidad.dao.DisenadorDAO;
import ar.edu.usal.publicidad.dao.EspecializacionDAO;
import ar.edu.usal.publicidad.dao.factory.CampaniaDAOFactory;
import ar.edu.usal.publicidad.dao.factory.DisenadorDAOFactory;
import ar.edu.usal.publicidad.dao.factory.EspecializacionDAOFactory;
import ar.edu.usal.publicidad.domain.Disenador;
import ar.edu.usal.publicidad.domain.Especializacion;
import ar.edu.usal.publicidad.exception.DAOException;
import ar.edu.usal.publicidad.exception.ValidacionException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.List;

// Service de diseñadores: validaciones y reglas del ABM de diseñadores.
public class DisenadorService {

    // Los DAO los pido a sus factories
    private DisenadorDAO disenadorDAO = DisenadorDAOFactory.getDisenadorDAO();
    private EspecializacionDAO especializacionDAO = EspecializacionDAOFactory.getEspecializacionDAO();
    private CampaniaDAO campaniaDAO = CampaniaDAOFactory.getCampaniaDAO();

    public boolean agregar(Disenador disenador) throws ValidacionException, DAOException {
        validar(disenador);
        // El legajo es la clave: no puede repetirse
        if (disenadorDAO.buscarPorLegajo(disenador.getLegajo()) != null) {
            throw new ValidacionException("Ya existe un diseñador con ese legajo");
        }
        controlarDniRepetido(disenador);
        return disenadorDAO.agregar(disenador);
    }

    public boolean modificar(Disenador disenador) throws ValidacionException, DAOException {
        validar(disenador);
        if (disenadorDAO.buscarPorLegajo(disenador.getLegajo()) == null) {
            throw new ValidacionException("El diseñador que quiere modificar no existe");
        }
        controlarDniRepetido(disenador);
        return disenadorDAO.modificar(disenador);
    }

    public boolean eliminar(int legajo) throws ValidacionException, DAOException {
        if (disenadorDAO.buscarPorLegajo(legajo) == null) {
            throw new ValidacionException("El diseñador que quiere eliminar no existe");
        }
        // Regla: no se puede borrar un diseñador que tiene campañas
        if (!campaniaDAO.listarDeDisenador(legajo).isEmpty()) {
            throw new ValidacionException("No se puede eliminar: el diseñador tiene campañas asignadas");
        }
        return disenadorDAO.eliminar(legajo);
    }

    public Disenador buscar(int legajo) throws DAOException {
        return disenadorDAO.buscarPorLegajo(legajo);
    }

    public List<Disenador> listar() throws DAOException {
        return disenadorDAO.listar();
    }

    // Las especializaciones para la lista desplegable del formulario
    public List<Especializacion> listarEspecializaciones() throws DAOException {
        return especializacionDAO.listar();
    }

    // Revisa que los datos del diseñador sean validos
    private boolean validar(Disenador d) throws ValidacionException {
        if (d == null) {
            throw new ValidacionException("Faltan los datos del diseñador");
        }
        if (d.getLegajo() <= 0) {
            throw new ValidacionException("El legajo debe ser un numero mayor a 0");
        }
        if (d.getNombre() == null || d.getNombre().trim().isEmpty()) {
            throw new ValidacionException("El nombre es obligatorio");
        }
        if (d.getApellido() == null || d.getApellido().trim().isEmpty()) {
            throw new ValidacionException("El apellido es obligatorio");
        }
        // El DNI son solo numeros, 7 u 8 digitos
        if (d.getDni() == null || !d.getDni().trim().matches("\\d{7,8}")) {
            throw new ValidacionException("El DNI debe tener 7 u 8 numeros");
        }
        if (d.getFechaNacimiento() == null) {
            throw new ValidacionException("La fecha de nacimiento es obligatoria");
        }
        LocalDate nacimiento = Instant.ofEpochMilli(d.getFechaNacimiento().getTime())
                .atZone(ZoneId.systemDefault()).toLocalDate();
        if (Period.between(nacimiento, LocalDate.now()).getYears() < 18) {
            throw new ValidacionException("El diseñador debe ser mayor de 18 años");
        }
        if (d.getEspecializacion() == null || d.getEspecializacion().getNombre() == null
                || d.getEspecializacion().getNombre().trim().isEmpty()) {
            throw new ValidacionException("La especializacion es obligatoria");
        }
        if (d.getCelular() == null || !d.getCelular().trim().matches("[0-9+\\- ]{8,15}")) {
            throw new ValidacionException("El celular debe tener entre 8 y 15 numeros");
        }
        d.setNombre(d.getNombre().trim());
        d.setApellido(d.getApellido().trim());
        d.setDni(d.getDni().trim());
        d.setCelular(d.getCelular().trim());
        return true;
    }

    // El DNI no es la clave pero tampoco puede repetirse entre dos diseñadores distintos
    private boolean controlarDniRepetido(Disenador disenador) throws ValidacionException, DAOException {
        for (Disenador otro : disenadorDAO.listar()) {
            boolean esOtraPersona = otro.getLegajo() != disenador.getLegajo();
            if (esOtraPersona && otro.getDni().equals(disenador.getDni())) {
                throw new ValidacionException("Ya existe otro diseñador con ese DNI");
            }
        }
        return true;
    }
}
