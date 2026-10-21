package ar.edu.usal.publicidad.dao;

import ar.edu.usal.publicidad.domain.Plataforma;
import ar.edu.usal.publicidad.exception.DAOException;

import java.util.List;


public interface PlataformaDAO {

    // Guarda una plataforma nueva
    boolean agregar(Plataforma plataforma) throws DAOException;

    // Modifica los datos de una plataforma existente
    boolean modificar(Plataforma plataforma) throws DAOException;

    // Borra la plataforma con ese nombre (el nombre es su clave)
    boolean eliminar(String nombre) throws DAOException;

    // Busca por nombre. Devuelve null si no existe
    Plataforma buscarPorNombre(String nombre) throws DAOException;

    // Devuelve todas las plataformas
    List<Plataforma> listar() throws DAOException;

    // Devuelve las plataformas que ese diseñador puede usar y que NO estan en una
    // campaña activa. Es la lista que se muestra al crear una campaña
    List<Plataforma> listarDisponiblesDeDisenador(int legajo) throws DAOException;
}