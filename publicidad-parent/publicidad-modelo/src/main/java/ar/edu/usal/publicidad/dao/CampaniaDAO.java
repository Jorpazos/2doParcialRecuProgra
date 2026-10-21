package ar.edu.usal.publicidad.dao;

import ar.edu.usal.publicidad.domain.Campania;
import ar.edu.usal.publicidad.domain.EstadoCampania;
import ar.edu.usal.publicidad.exception.DAOException;

import java.util.List;


public interface CampaniaDAO {

    // Guarda una campaña nueva (alta). Devuelve true si se pudo.

    boolean agregar(Campania campania) throws DAOException;

    // Modifica una campaña existente
    boolean modificar(Campania campania) throws DAOException;

    // Borra la campaña con ese id
    boolean eliminar(int id) throws DAOException;

    // Cambia solo el estado de una campaña (iniciada o finalizada)
    boolean cambiarEstado(int id, EstadoCampania estado) throws DAOException;

    // Busca una campaña por id. Devuelve null si no existe
    Campania buscarPorId(int id) throws DAOException;

    // Devuelve todas las campañas (para el ejecutivo)
    List<Campania> listar() throws DAOException;

    // Devuelve solo las campañas asignadas a ese diseñador (para el diseñador)
    List<Campania> listarDeDisenador(int legajo) throws DAOException;
}