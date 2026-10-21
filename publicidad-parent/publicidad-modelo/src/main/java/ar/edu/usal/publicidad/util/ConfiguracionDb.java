package ar.edu.usal.publicidad.util;

import ar.edu.usal.publicidad.exception.DAOException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

// Esta clase solo lee el archivo db.properties y me da sus datos.
// La conexion la hace la clase ConexionSingleton, asi cada clase tiene una sola tarea.
public class ConfiguracionDb {

    private Properties propiedades = new Properties();


    public ConfiguracionDb() throws DAOException {

        try (InputStream entrada = ConfiguracionDb.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (entrada == null) {
                throw new DAOException("No encontre el archivo db.properties");
            }
            propiedades.load(entrada);   // cargo  claves=valor
        } catch (IOException e) {
            throw new DAOException("No pude leer db.properties", e);
        }
    }


    public String getDriver() {
        return propiedades.getProperty("db.driver");
    }

    public String getUrl() {
        return propiedades.getProperty("db.url");
    }

    public String getUsuario() {
        return propiedades.getProperty("db.usuario");
    }

    public String getPassword() {
        return propiedades.getProperty("db.password");
    }
}