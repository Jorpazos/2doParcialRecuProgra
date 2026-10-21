package ar.edu.usal.publicidad.dao.factory;

import ar.edu.usal.publicidad.dao.TipoContenidoDAO;
import ar.edu.usal.publicidad.dao.jdbc.TipoContenidoDAOJdbc;

// FACTORY del DAO de tipos de contenido
public class TipoContenidoDAOFactory {

    private TipoContenidoDAOFactory() {
    }

    public static TipoContenidoDAO getTipoContenidoDAO() {
        return new TipoContenidoDAOJdbc();
    }
}
