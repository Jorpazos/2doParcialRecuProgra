package ar.edu.usal.publicidad.dao.factory;

import ar.edu.usal.publicidad.dao.EspecializacionDAO;
import ar.edu.usal.publicidad.dao.jdbc.EspecializacionDAOJdbc;

// FACTORY del DAO de especializaciones
public class EspecializacionDAOFactory {

    private EspecializacionDAOFactory() {
    }

    public static EspecializacionDAO getEspecializacionDAO() {
        return new EspecializacionDAOJdbc();
    }
}
