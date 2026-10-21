package ar.edu.usal.publicidad.dao.factory;

import ar.edu.usal.publicidad.dao.PlataformaDAO;
import ar.edu.usal.publicidad.dao.jdbc.PlataformaDAOJdbc;

public class PlataformaDAOFactory {

    private PlataformaDAOFactory() {
    }

    public static PlataformaDAO getPlataformaDAO() {
        return new PlataformaDAOJdbc();
    }
}