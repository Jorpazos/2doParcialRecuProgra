package ar.edu.usal.publicidad.dao.jdbc;

import ar.edu.usal.publicidad.dao.DisenadorDAO;
import ar.edu.usal.publicidad.dao.factory.CuentaDAOFactory;
import ar.edu.usal.publicidad.domain.Cuenta;
import ar.edu.usal.publicidad.domain.Disenador;
import ar.edu.usal.publicidad.domain.Especializacion;
import ar.edu.usal.publicidad.domain.Plataforma;
import ar.edu.usal.publicidad.exception.DAOException;
import ar.edu.usal.publicidad.util.ConexionSingleton;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class DisenadorDAOJdbc implements DisenadorDAO {

    @Override
    public boolean agregar(Disenador disenador) throws DAOException {
        String sqlDisenador = "INSERT INTO disenador (legajo, nombre, apellido, dni, fecha_nacimiento, "
                + "especializacion, celular) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sqlCuenta = "INSERT INTO disenador_cuenta (legajo, cuenta) VALUES (?, ?)";
        String sqlPlataforma = "INSERT INTO disenador_plataforma (legajo, plataforma) VALUES (?, ?)";
        try (Connection con = ConexionSingleton.getInstancia().getConexion()) {
            con.setAutoCommit(false);   // empiezo la transaccion: nada se confirma hasta el commit
            try {
                // 1) El diseñador
                try (PreparedStatement ps = con.prepareStatement(sqlDisenador)) {
                    ps.setInt(1, disenador.getLegajo());
                    ps.setString(2, disenador.getNombre());
                    ps.setString(3, disenador.getApellido());
                    ps.setString(4, disenador.getDni());
                    // La fecha de Java (util.Date) la paso a la de SQL (sql.Date)
                    ps.setDate(5, new Date(disenador.getFechaNacimiento().getTime()));
                    ps.setString(6, disenador.getEspecializacion().getNombre());
                    ps.setString(7, disenador.getCelular());
                    ps.executeUpdate();
                }
                // 2) Las cuentas para las que esta autorizado
                try (PreparedStatement ps = con.prepareStatement(sqlCuenta)) {
                    for (Cuenta cuenta : disenador.getCuentas()) {
                        ps.setInt(1, disenador.getLegajo());
                        ps.setString(2, cuenta.getNombre());
                        ps.executeUpdate();
                    }
                }
                // 3) Las plataformas que puede usar
                try (PreparedStatement ps = con.prepareStatement(sqlPlataforma)) {
                    for (Plataforma plataforma : disenador.getPlataformas()) {
                        ps.setInt(1, disenador.getLegajo());
                        ps.setString(2, plataforma.getNombre());
                        ps.executeUpdate();
                    }
                }
                con.commit();    // las tres cosas salieron bien: confirmo
                return true;
            } catch (SQLException e) {
                con.rollback();  // algo fallo y se dehace todo lo anterior
                throw new DAOException("No pude guardar el diseñador, no se guardo nada", e);
            }
        } catch (SQLException e) {
            throw new DAOException("Error de conexion al guardar el diseñador", e);
        }
    }

    @Override
    public boolean modificar(Disenador disenador) throws DAOException {
        String sqlDisenador = "UPDATE disenador SET nombre = ?, apellido = ?, dni = ?, "
                + "fecha_nacimiento = ?, especializacion = ?, celular = ? WHERE legajo = ?";
        String sqlBorrarCuentas = "DELETE FROM disenador_cuenta WHERE legajo = ?";
        String sqlBorrarPlataformas = "DELETE FROM disenador_plataforma WHERE legajo = ?";
        String sqlCuenta = "INSERT INTO disenador_cuenta (legajo, cuenta) VALUES (?, ?)";
        String sqlPlataforma = "INSERT INTO disenador_plataforma (legajo, plataforma) VALUES (?, ?)";
        try (Connection con = ConexionSingleton.getInstancia().getConexion()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(sqlDisenador)) {
                    ps.setString(1, disenador.getNombre());
                    ps.setString(2, disenador.getApellido());
                    ps.setString(3, disenador.getDni());
                    ps.setDate(4, new Date(disenador.getFechaNacimiento().getTime()));
                    ps.setString(5, disenador.getEspecializacion().getNombre());
                    ps.setString(6, disenador.getCelular());
                    ps.setInt(7, disenador.getLegajo());
                    ps.executeUpdate();
                }
                // Borro las relaciones viejas y cargo las nuevas
                try (PreparedStatement ps = con.prepareStatement(sqlBorrarCuentas)) {
                    ps.setInt(1, disenador.getLegajo());
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement(sqlBorrarPlataformas)) {
                    ps.setInt(1, disenador.getLegajo());
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement(sqlCuenta)) {
                    for (Cuenta cuenta : disenador.getCuentas()) {
                        ps.setInt(1, disenador.getLegajo());
                        ps.setString(2, cuenta.getNombre());
                        ps.executeUpdate();
                    }
                }
                try (PreparedStatement ps = con.prepareStatement(sqlPlataforma)) {
                    for (Plataforma plataforma : disenador.getPlataformas()) {
                        ps.setInt(1, disenador.getLegajo());
                        ps.setString(2, plataforma.getNombre());
                        ps.executeUpdate();
                    }
                }
                con.commit();
                return true;
            } catch (SQLException e) {
                con.rollback();
                throw new DAOException("No pude modificar el diseñador, no se cambio nada", e);
            }
        } catch (SQLException e) {
            throw new DAOException("Error de conexion al modificar el diseñador", e);
        }
    }

    @Override
    public boolean eliminar(int legajo) throws DAOException {
        // Primero borro lo que depende del diseñador y al final el diseñador
        String sqlUsuario = "DELETE FROM usuario WHERE legajo = ?";
        String sqlCuentas = "DELETE FROM disenador_cuenta WHERE legajo = ?";
        String sqlPlataformas = "DELETE FROM disenador_plataforma WHERE legajo = ?";
        String sqlDisenador = "DELETE FROM disenador WHERE legajo = ?";
        try (Connection con = ConexionSingleton.getInstancia().getConexion()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(sqlUsuario)) {
                    ps.setInt(1, legajo);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement(sqlCuentas)) {
                    ps.setInt(1, legajo);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement(sqlPlataformas)) {
                    ps.setInt(1, legajo);
                    ps.executeUpdate();
                }
                int filas;
                try (PreparedStatement ps = con.prepareStatement(sqlDisenador)) {
                    ps.setInt(1, legajo);
                    filas = ps.executeUpdate();
                }
                con.commit();
                return filas == 1;
            } catch (SQLException e) {
                con.rollback();   // por ejemplo si tiene campañas, no se borra nada
                throw new DAOException("No pude eliminar el diseñador (puede tener campañas)", e);
            }
        } catch (SQLException e) {
            throw new DAOException("Error de conexion al eliminar el diseñador", e);
        }
    }

    @Override
    public Disenador buscarPorLegajo(int legajo) throws DAOException {
        String sql = "SELECT legajo, nombre, apellido, dni, fecha_nacimiento, especializacion, celular "
                + "FROM disenador WHERE legajo = ?";
        Disenador disenador = null;
        try (Connection con = ConexionSingleton.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, legajo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    disenador = armarDisenador(rs);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("No pude buscar el diseñador", e);
        }
        if (disenador != null) {
            // Le cargo sus cuentas (con el DAO de cuentas) y sus plataformas
            disenador.setCuentas(CuentaDAOFactory.getCuentaDAO().listarDeDisenador(legajo));
            disenador.setPlataformas(plataformasDe(legajo));
        }
        return disenador;
    }

    @Override
    public List<Disenador> listar() throws DAOException {

        // En el listado no cargo cuentas ni plataformas, solo los datos propios
        String sql = "SELECT legajo, nombre, apellido, dni, fecha_nacimiento, especializacion, celular "
                + "FROM disenador ORDER BY apellido, nombre";
        List<Disenador> disenadores = new ArrayList<>();
        try (Connection con = ConexionSingleton.getInstancia().getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                disenadores.add(armarDisenador(rs));
            }
            return disenadores;
        } catch (SQLException e) {
            throw new DAOException("No pude listar los diseñadores", e);
        }
    }

    // Trae las plataformas que puede usar un diseñador (abre su propia conexion)
    private List<Plataforma> plataformasDe(int legajo) throws DAOException {
        String sql = "SELECT p.nombre, p.formato_principal, p.alcance_maximo, p.costo_fijo, p.cpc "
                + "FROM plataforma p JOIN disenador_plataforma dp ON dp.plataforma = p.nombre "
                + "WHERE dp.legajo = ? ORDER BY p.nombre";
        List<Plataforma> plataformas = new ArrayList<>();
        try (Connection con = ConexionSingleton.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, legajo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    plataformas.add(new Plataforma(
                            rs.getString("nombre"),
                            rs.getString("formato_principal"),
                            rs.getDouble("alcance_maximo"),
                            rs.getDouble("costo_fijo"),
                            rs.getDouble("cpc")));
                }
            }
            return plataformas;
        } catch (SQLException e) {
            throw new DAOException("No pude traer las plataformas del diseñador", e);
        }
    }

    // Convierte la fila actual del ResultSet en un Disenador (sin sus listas)
    private Disenador armarDisenador(ResultSet rs) throws SQLException {
        return new Disenador(
                rs.getInt("legajo"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("dni"),
                rs.getDate("fecha_nacimiento"),
                new Especializacion(rs.getString("especializacion")),
                rs.getString("celular"));
    }
}