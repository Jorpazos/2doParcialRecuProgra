package ar.edu.usal.publicidad.util;

import ar.edu.usal.publicidad.exception.DAOException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


// Su unico trabajo es darme conexiones a la base. Los datos los saca de ConfiguracionDb.
public class ConexionSingleton {

    // La unica instancia. Empieza en null y se crea la primera vez que alguien la pide.
    private static ConexionSingleton instancia;

    private ConfiguracionDb configuracion;

    // Constructor privado : nadie desde afuera puede hacer "new ConexionSingleton()"
    private ConexionSingleton() throws DAOException {
        configuracion = new ConfiguracionDb();   // leo db.properties una sola vez
        try {
            Class.forName(configuracion.getDriver());   // cargo el driver de MySQL
        } catch (ClassNotFoundException e) {
            throw new DAOException("No encontre el driver de MySQL", e);
        }
    }

    // obtener la instancia. synchronized: Tomcat atiende cada pedido en un hilo distinto,
    // y si dos llegan a la vez la primera vez, asi solo uno crea la instancia
    public static synchronized ConexionSingleton getInstancia() throws DAOException {
        if (instancia == null) {
            instancia = new ConexionSingleton();
        }
        return instancia;
    }

    // Devuelve una conexion nueva
    public Connection getConexion() throws DAOException {
        try {
            return DriverManager.getConnection(
                    configuracion.getUrl(),
                    configuracion.getUsuario(),
                    configuracion.getPassword());
        } catch (SQLException e) {
            throw new DAOException("No pude conectarme a la base de datos", e);
        }
    }
}