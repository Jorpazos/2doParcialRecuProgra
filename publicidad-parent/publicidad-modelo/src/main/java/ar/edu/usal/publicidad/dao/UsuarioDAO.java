package ar.edu.usal.publicidad.dao;

import ar.edu.usal.publicidad.domain.Usuario;
import ar.edu.usal.publicidad.exception.DAOException;


public interface UsuarioDAO {

    // Busca un usuario por su nombre de usuario
    // Si existe, lo devuelve ya con su perfil, su diseñador
    // y las opciones de menu que le corresponden. Si no existe, devuelve null
    Usuario buscarPorUsuario(String usuario) throws DAOException;
}