package ar.edu.usal.publicidad.service;

import ar.edu.usal.publicidad.dao.CuentaDAO;
import ar.edu.usal.publicidad.dao.factory.CuentaDAOFactory;
import ar.edu.usal.publicidad.domain.Cuenta;
import ar.edu.usal.publicidad.exception.DAOException;
import ar.edu.usal.publicidad.exception.ValidacionException;

import java.util.List;

// Service de cuentas: aca estan las validaciones y las reglas.
// El DAO solo habla con la base; yo decido que datos son validos antes de llamarlo.
public class CuentaService {

    // El DAO lo pido al factory, no hago new CuentaDAOJdbc()
    private CuentaDAO cuentaDAO = CuentaDAOFactory.getCuentaDAO();

    public boolean agregar(Cuenta cuenta) throws ValidacionException, DAOException {
        // Validacion: el nombre no puede estar vacio
        if (cuenta == null || cuenta.getNombre() == null || cuenta.getNombre().trim().isEmpty()) {
            throw new ValidacionException("El nombre de la cuenta es obligatorio");
        }
        // Validacion: no puede existir otra cuenta con el mismo nombre
        if (cuentaDAO.buscarPorNombre(cuenta.getNombre().trim()) != null) {
            throw new ValidacionException("Ya existe una cuenta con ese nombre");
        }
        cuenta.setNombre(cuenta.getNombre().trim());
        return cuentaDAO.agregar(cuenta);
    }

    public List<Cuenta> listar() throws DAOException {
        return cuentaDAO.listar();
    }

    // Las cuentas para las que un diseñador esta autorizado
    public List<Cuenta> listarDeDisenador(int legajo) throws DAOException {
        return cuentaDAO.listarDeDisenador(legajo);
    }
}