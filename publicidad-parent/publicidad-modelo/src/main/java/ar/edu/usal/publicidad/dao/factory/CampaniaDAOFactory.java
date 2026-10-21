package ar.edu.usal.publicidad.dao.factory;

import ar.edu.usal.publicidad.dao.CampaniaDAO;
import ar.edu.usal.publicidad.dao.jdbc.CampaniaDAOJdbc;

public class CampaniaDAOFactory {

    private CampaniaDAOFactory() {
    }

    public static CampaniaDAO getCampaniaDAO() {
        return new CampaniaDAOJdbc();
    }
}