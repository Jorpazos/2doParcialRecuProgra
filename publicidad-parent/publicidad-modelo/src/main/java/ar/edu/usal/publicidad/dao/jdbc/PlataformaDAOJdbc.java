package ar.edu.usal.publicidad.dao.jdbc;

import ar.edu.usal.publicidad.dao.PlataformaDAO;
import ar.edu.usal.publicidad.domain.EstadoCampania;
import ar.edu.usal.publicidad.domain.Plataforma;
import ar.edu.usal.publicidad.exception.DAOException;
import ar.edu.usal.publicidad.util.ConexionSingleton;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

// Implementacion JDBC del DAO de plataformas.
// Cada metodo pide su propia conexion al Singleton y la cierra solo (try-with-resources).
public class PlataformaDAOJdbc implements PlataformaDAO {

    @Override
    public boolean agregar(Plataforma plataforma) throws DAOException {
        String sql = "INSERT INTO plataforma (nombre, formato_principal, alcance_maximo, costo_fijo, cpc) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = ConexionSingleton.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, plataforma.getNombre());
            ps.setString(2, plataforma.getFormatoPrincipal());
            ps.setDouble(3, plataforma.getAlcanceMaximo());
            ps.setDouble(4, plataforma.getCostoFijo());
            ps.setDouble(5, plataforma.getCpc());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new DAOException("No pude guardar la plataforma", e);
        }
    }

    @Override
    public boolean modificar(Plataforma plataforma) throws DAOException {
        // El nombre es la clave, por eso va en el WHERE y no se modifica
        String sql = "UPDATE plataforma SET formato_principal = ?, alcance_maximo = ?, "
                + "costo_fijo = ?, cpc = ? WHERE nombre = ?";
        try (Connection con = ConexionSingleton.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, plataforma.getFormatoPrincipal());
            ps.setDouble(2, plataforma.getAlcanceMaximo());
            ps.setDouble(3, plataforma.getCostoFijo());
            ps.setDouble(4, plataforma.getCpc());
            ps.setString(5, plataforma.getNombre());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new DAOException("No pude modificar la plataforma", e);
        }
    }

    @Override
    public boolean eliminar(String nombre) throws DAOException {
        // Antes de borrarla hay que sacarla de los diseñadores que la usan,
        // asi que hago los dos DELETE en una transaccion
        String sqlRelacion = "DELETE FROM disenador_plataforma WHERE plataforma = ?";
        String sqlPlataforma = "DELETE FROM plataforma WHERE nombre = ?";
        try (Connection con = ConexionSingleton.getInstancia().getConexion()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(sqlRelacion)) {
                    ps.setString(1, nombre);
                    ps.executeUpdate();
                }
                int filas;
                try (PreparedStatement ps = con.prepareStatement(sqlPlataforma)) {
                    ps.setString(1, nombre);
                    filas = ps.executeUpdate();
                }
                con.commit();
                return filas == 1;
            } catch (SQLException e) {
                con.rollback();   // por ejemplo si ya tiene campañas, no se borra nada
                throw new DAOException("No pude eliminar la plataforma (puede tener campañas)", e);
            }
        } catch (SQLException e) {
            throw new DAOException("Error de conexion al eliminar la plataforma", e);
        }
    }

    @Override
    public Plataforma buscarPorNombre(String nombre) throws DAOException {
        String sql = "SELECT nombre, formato_principal, alcance_maximo, costo_fijo, cpc "
                + "FROM plataforma WHERE nombre = ?";
        try (Connection con = ConexionSingleton.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return armarPlataforma(rs);
                }
                return null;   // no existe
            }
        } catch (SQLException e) {
            throw new DAOException("No pude buscar la plataforma", e);
        }
    }

    @Override
    public List<Plataforma> listar() throws DAOException {
        // Statement: no hay datos de afuera en este SQL
        String sql = "SELECT nombre, formato_principal, alcance_maximo, costo_fijo, cpc "
                + "FROM plataforma ORDER BY nombre";
        List<Plataforma> plataformas = new ArrayList<>();
        try (Connection con = ConexionSingleton.getInstancia().getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                plataformas.add(armarPlataforma(rs));
            }
            return plataformas;
        } catch (SQLException e) {
            throw new DAOException("No pude listar las plataformas", e);
        }
    }

    @Override
    public List<Plataforma> listarDisponiblesDeDisenador(int legajo) throws DAOException {
        // Plataformas de ese diseñador (tabla disenador_plataforma) que NO tengan una
        // campaña activa (PENDIENTE o INICIADA). La subconsulta saca las plataformas ocupadas.
        String sql = "SELECT p.nombre, p.formato_principal, p.alcance_maximo, p.costo_fijo, p.cpc "
                + "FROM plataforma p "
                + "JOIN disenador_plataforma dp ON dp.plataforma = p.nombre "
                + "WHERE dp.legajo = ? "
                + "AND p.nombre NOT IN (SELECT plataforma FROM campania WHERE estado IN (?, ?)) "
                + "ORDER BY p.nombre";
        List<Plataforma> plataformas = new ArrayList<>();
        try (Connection con = ConexionSingleton.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, legajo);
            ps.setString(2, EstadoCampania.PENDIENTE.name());
            ps.setString(3, EstadoCampania.INICIADA.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    plataformas.add(armarPlataforma(rs));
                }
            }
            return plataformas;
        } catch (SQLException e) {
            throw new DAOException("No pude listar las plataformas disponibles", e);
        }
    }

    // Convierte la fila actual del ResultSet en un objeto Plataforma
    private Plataforma armarPlataforma(ResultSet rs) throws SQLException {
        return new Plataforma(
                rs.getString("nombre"),
                rs.getString("formato_principal"),
                rs.getDouble("alcance_maximo"),
                rs.getDouble("costo_fijo"),
                rs.getDouble("cpc"));
    }
}
