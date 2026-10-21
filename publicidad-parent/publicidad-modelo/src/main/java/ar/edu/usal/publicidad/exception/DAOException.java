package ar.edu.usal.publicidad.exception;

// Este error lo lanzo desde los DAO cuando falla algo con la base de datos
// Extiende PublicidadException, asi los servlets
// pueden atraparla con el mismo catch que las demas.
public class DAOException extends PublicidadException {
    // Solo con mensaje
    public DAOException(String mensaje) {
        super(mensaje);   // se lo paso a la madre
    }

    // Con mensaje y la causa original (por ejemplo el SQLException)
    public DAOException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}