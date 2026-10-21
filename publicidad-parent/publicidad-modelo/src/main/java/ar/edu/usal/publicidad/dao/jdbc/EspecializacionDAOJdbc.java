package ar.edu.usal.publicidad.dao.jdbc;

import ar.edu.usal.publicidad.dao.EspecializacionDAO;
import ar.edu.usal.publicidad.domain.Especializacion;
import ar.edu.usal.publicidad.exception.DAOException;
import ar.edu.usal.publicidad.util.ConexionSingleton;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

// Implementacion JDBC del DAO de especializaciones.
public class EspecializacionDAOJdbc implements EspecializacionDAO {

    @Override
    public List<Especializacion> listar() throws DAOException {
        // Statement: no hay datos de afuera en este SQL
        String sql = "SELECT nombre FROM especializacion ORDER BY nombre";
        List<Especializacion> especializaciones = new ArrayList<>();
        try (Connection con = ConexionSingleton.getInstancia().getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                especializaciones.add(new Especializacion(rs.getString("nombre")));
            }
            return especializaciones;
        } catch (SQLException e) {
            throw new DAOException("No pude listar las especializaciones", e);
        }
    }
}
