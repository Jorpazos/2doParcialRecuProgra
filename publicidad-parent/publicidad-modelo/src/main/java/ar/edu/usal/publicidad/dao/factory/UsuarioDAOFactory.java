package ar.edu.usal.publicidad.dao.factory;

import ar.edu.usal.publicidad.dao.UsuarioDAO;
import ar.edu.usal.publicidad.dao.jdbc.UsuarioDAOJdbc;

public class UsuarioDAOFactory {

    private UsuarioDAOFactory() {
    }

    public static UsuarioDAO getUsuarioDAO() {
        return new UsuarioDAOJdbc();
    }
}