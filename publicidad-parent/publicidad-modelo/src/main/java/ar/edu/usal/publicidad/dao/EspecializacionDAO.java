package ar.edu.usal.publicidad.dao;

import ar.edu.usal.publicidad.domain.Especializacion;
import ar.edu.usal.publicidad.exception.DAOException;

import java.util.List;

// Contrato del DAO de especializaciones (Grafico, Audiovisual, Digital).
// Solo lo necesito para llenar la lista desplegable del formulario de diseñadores.
public interface EspecializacionDAO {

    // Devuelve todas las especializaciones
    List<Especializacion> listar() throws DAOException;
}
