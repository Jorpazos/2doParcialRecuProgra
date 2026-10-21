package ar.edu.usal.publicidad.dao;

import ar.edu.usal.publicidad.domain.TipoContenido;
import ar.edu.usal.publicidad.exception.DAOException;

import java.util.List;

// Contrato del DAO de tipos de contenido (los cinco de la tabla del enunciado).
public interface TipoContenidoDAO {

    // Busca por nombre. Devuelve null si no existe
    TipoContenido buscarPorNombre(String nombre) throws DAOException;

    // Devuelve todos los tipos de contenido
    List<TipoContenido> listar() throws DAOException;
}
