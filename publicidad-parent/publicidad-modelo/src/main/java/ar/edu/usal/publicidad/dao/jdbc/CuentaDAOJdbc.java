package ar.edu.usal.publicidad.dao.jdbc;

import ar.edu.usal.publicidad.dao.CuentaDAO;
import ar.edu.usal.publicidad.domain.Cuenta;
import ar.edu.usal.publicidad.exception.DAOException;
import ar.edu.usal.publicidad.util.ConexionSingleton;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class CuentaDAOJdbc implements CuentaDAO {

    @Override
    public boolean agregar(Cuenta cuenta) throws DAOException {
        // PreparedStatement: el SQL lleva ? y despues les pongo los valores
        String sql = "INSERT INTO cuenta (nombre) VALUES (?)";
        try (Connection con = ConexionSingleton.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cuenta.getNombre());
            int filas = ps.executeUpdate();   // devuelve cuantas filas se insertaron
            return filas == 1;                // true si se guardo, false si no
        } catch (SQLException e) {
            throw new DAOException("No pude guardar la cuenta", e);
        }
    }

    @Override
    public Cuenta buscarPorNombre(String nombre) throws DAOException {
        String sql = "SELECT nombre FROM cuenta WHERE nombre = ?";
        try (Connection con = ConexionSingleton.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return armarCuenta(rs);
                }
                return null;   // no existe
            }
        } catch (SQLException e) {
            throw new DAOException("No pude buscar la cuenta", e);
        }
    }

    @Override
    public List<Cuenta> listar() throws DAOException {
        // Statement lo uso aca porque el SQL no tiene ningun dato que venga de afuera
        String sql = "SELECT nombre FROM cuenta ORDER BY nombre";
        List<Cuenta> cuentas = new ArrayList<>();
        try (Connection con = ConexionSingleton.getInstancia().getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                cuentas.add(armarCuenta(rs));
            }
            return cuentas;
        } catch (SQLException e) {
            throw new DAOException("No pude listar las cuentas", e);
        }
    }

    @Override
    public List<Cuenta> listarDeDisenador(int legajo) throws DAOException {
        // JOIN con la tabla disenador_cuenta solo las cuentas autorizadas para ese legajo
        String sql = "SELECT c.nombre FROM cuenta c "
                + "JOIN disenador_cuenta dc ON dc.cuenta = c.nombre "
                + "WHERE dc.legajo = ? ORDER BY c.nombre";
        List<Cuenta> cuentas = new ArrayList<>();
        try (Connection con = ConexionSingleton.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, legajo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cuentas.add(armarCuenta(rs));
                }
            }
            return cuentas;
        } catch (SQLException e) {
            throw new DAOException("No pude listar las cuentas del diseñador", e);
        }
    }

    // Convierte la fila actual del ResultSet en un objeto Cuenta
    private Cuenta armarCuenta(ResultSet rs) throws SQLException {
        return new Cuenta(rs.getString("nombre"));
    }
}