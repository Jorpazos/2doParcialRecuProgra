package ar.edu.usal.publicidad.dao;

import ar.edu.usal.publicidad.domain.Disenador;
import ar.edu.usal.publicidad.exception.DAOException;

import java.util.List;


// El "como" lo escribe DisenadorDAOJdbc usando JDBC
// Los metodos de escritura devuelven boolean (true si se pudo, false si no)
// Ninguno recibe una Connection: cada implementacion pide la suya
public interface DisenadorDAO {

    // Guarda un diseñador nuevo con sus cuentas y plataformas
    boolean agregar(Disenador disenador) throws DAOException;

    // Modifica los datos de un diseñador existente
    boolean modificar(Disenador disenador) throws DAOException;

    // Borra el diseñador que tenga ese legajo
    boolean eliminar(int legajo) throws DAOException;

    // Busca por legajo. Devuelve null si no existe
    Disenador buscarPorLegajo(int legajo) throws DAOException;

    // Devuelve todos los diseñadores
    List<Disenador> listar() throws DAOException;
}