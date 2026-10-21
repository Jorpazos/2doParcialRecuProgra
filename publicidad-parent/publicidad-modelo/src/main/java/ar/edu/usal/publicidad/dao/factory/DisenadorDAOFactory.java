package ar.edu.usal.publicidad.dao.factory;

import ar.edu.usal.publicidad.dao.DisenadorDAO;
import ar.edu.usal.publicidad.dao.jdbc.DisenadorDAOJdbc;

// FACTORY del DAO de diseñadores
// El service no sabe si el DAO usa JDBC u otra cosa: solo pide un DisenadorDAO.
public class DisenadorDAOFactory {

    private DisenadorDAOFactory() {
    }

    public static DisenadorDAO getDisenadorDAO() {
        return new DisenadorDAOJdbc();
    }
}