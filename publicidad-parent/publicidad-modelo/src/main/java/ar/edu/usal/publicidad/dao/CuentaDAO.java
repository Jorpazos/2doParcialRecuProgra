package ar.edu.usal.publicidad.dao;

import ar.edu.usal.publicidad.domain.Cuenta;
import ar.edu.usal.publicidad.exception.DAOException;

import java.util.List;

public interface CuentaDAO {

    // Guarda una cuenta nueva
    boolean agregar(Cuenta cuenta) throws DAOException;

    // Busca por nombre. Devuelve null si no existe
    Cuenta buscarPorNombre(String nombre) throws DAOException;

    // Devuelve todas las cuentas
    List<Cuenta> listar() throws DAOException;

    // Devuelve las cuentas para las que ese diseñador esta autorizado
    List<Cuenta> listarDeDisenador(int legajo) throws DAOException;
}