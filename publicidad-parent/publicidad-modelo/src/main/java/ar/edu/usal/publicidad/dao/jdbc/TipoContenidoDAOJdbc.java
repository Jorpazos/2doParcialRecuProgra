package ar.edu.usal.publicidad.dao.jdbc;

import ar.edu.usal.publicidad.dao.TipoContenidoDAO;
import ar.edu.usal.publicidad.domain.TipoContenido;
import ar.edu.usal.publicidad.exception.DAOException;
import ar.edu.usal.publicidad.util.ConexionSingleton;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

// Implementacion JDBC del DAO de tipos de contenido.
public class TipoContenidoDAOJdbc implements TipoContenidoDAO {

    @Override
    public TipoContenido buscarPorNombre(String nombre) throws DAOException {
        String sql = "SELECT nombre, horas_produccion, factor FROM tipo_contenido WHERE nombre = ?";
        try (Connection con = ConexionSingleton.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return armarTipo(rs);
                }
                return null;   // no existe
            }
        } catch (SQLException e) {
            throw new DAOException("No pude buscar el tipo de contenido", e);
        }
    }

    @Override
    public List<TipoContenido> listar() throws DAOException {
        // Statement: no hay datos de afuera en este SQL
        String sql = "SELECT nombre, horas_produccion, factor FROM tipo_contenido ORDER BY nombre";
        List<TipoContenido> tipos = new ArrayList<>();
        try (Connection con = ConexionSingleton.getInstancia().getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                tipos.add(armarTipo(rs));
            }
            return tipos;
        } catch (SQLException e) {
            throw new DAOException("No pude listar los tipos de contenido", e);
        }
    }

    // Convierte la fila actual del ResultSet en un TipoContenido
    private TipoContenido armarTipo(ResultSet rs) throws SQLException {
        return new TipoContenido(
                rs.getString("nombre"),
                rs.getInt("horas_produccion"),
                rs.getDouble("factor"));
    }
}
