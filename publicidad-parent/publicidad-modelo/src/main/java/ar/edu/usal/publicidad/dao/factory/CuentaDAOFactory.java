package ar.edu.usal.publicidad.dao.factory;

import ar.edu.usal.publicidad.dao.CuentaDAO;
import ar.edu.usal.publicidad.dao.jdbc.CuentaDAOJdbc;

public class CuentaDAOFactory {

    private CuentaDAOFactory() {
    }

    public static CuentaDAO getCuentaDAO() {
        return new CuentaDAOJdbc();
    }
}