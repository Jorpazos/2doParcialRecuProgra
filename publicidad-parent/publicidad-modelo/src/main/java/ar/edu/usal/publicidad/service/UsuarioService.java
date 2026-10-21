package ar.edu.usal.publicidad.service;

import ar.edu.usal.publicidad.dao.UsuarioDAO;
import ar.edu.usal.publicidad.dao.factory.UsuarioDAOFactory;
import ar.edu.usal.publicidad.domain.Usuario;
import ar.edu.usal.publicidad.exception.DAOException;
import ar.edu.usal.publicidad.exception.ValidacionException;

// Service de usuarios: se encarga del login.
public class UsuarioService {

    // El DAO lo pido al factory
    private UsuarioDAO usuarioDAO = UsuarioDAOFactory.getUsuarioDAO();

    // Comprueba usuario y contraseña. Si estan bien devuelve el Usuario completo
    // (con su perfil y su menu). Si no, lanza ValidacionException.
    public Usuario login(String nombreUsuario, String password) throws ValidacionException, DAOException {
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()
                || password == null || password.isEmpty()) {
            throw new ValidacionException("Ingrese usuario y contraseña");
        }
        Usuario usuario = usuarioDAO.buscarPorUsuario(nombreUsuario.trim());
        // Mismo mensaje si no existe o si la clave esta mal, asi no se sabe cual de los dos fallo
        if (usuario == null || !usuario.getPassword().equals(password)) {
            throw new ValidacionException("Usuario o contraseña incorrectos");
        }
        return usuario;
    }
}
