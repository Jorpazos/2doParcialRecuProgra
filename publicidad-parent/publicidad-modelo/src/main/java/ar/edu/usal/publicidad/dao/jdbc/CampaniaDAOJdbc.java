package ar.edu.usal.publicidad.dao.jdbc;

import ar.edu.usal.publicidad.dao.CampaniaDAO;
import ar.edu.usal.publicidad.domain.Campania;
import ar.edu.usal.publicidad.domain.Cuenta;
import ar.edu.usal.publicidad.domain.Disenador;
import ar.edu.usal.publicidad.domain.EstadoCampania;
import ar.edu.usal.publicidad.domain.Plataforma;
import ar.edu.usal.publicidad.domain.TipoContenido;
import ar.edu.usal.publicidad.exception.DAOException;
import ar.edu.usal.publicidad.util.ConexionSingleton;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class CampaniaDAOJdbc implements CampaniaDAO {

    // Este SELECT trae la campaña junto con los datos del diseñador, la plataforma y el tipo
    // de contenido, asi no hago una consulta por cada uno. Lo reuso en los tres metodos de lectura
    private static final String SELECT_CAMPANIA =
            "SELECT c.id, c.cuenta, c.costo_total, c.estado, "
                    + "d.legajo, d.nombre AS d_nombre, d.apellido AS d_apellido, "
                    + "p.nombre AS p_nombre, p.formato_principal, p.alcance_maximo, p.costo_fijo, p.cpc, "
                    + "t.nombre AS t_nombre, t.horas_produccion AS t_horas, t.factor "
                    + "FROM campania c "
                    + "JOIN disenador d ON d.legajo = c.legajo "
                    + "JOIN plataforma p ON p.nombre = c.plataforma "
                    + "JOIN tipo_contenido t ON t.nombre = c.tipo_contenido ";

    @Override
    public boolean agregar(Campania campania) throws DAOException {
        String sqlInsert = "INSERT INTO campania (legajo, cuenta, plataforma, tipo_contenido, "
                + "costo_total, estado) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionSingleton.getInstancia().getConexion()) {
            con.setAutoCommit(false);   // empiezo la transaccion
            try {
                // 1) Compruebo que la plataforma siga libre (sin campaña PENDIENTE o INICIADA).
                // CALLABLESTATEMENT: es el unico lugar donde lo uso, para cumplir esa consigna del
                // enunciado. Llama al procedure sp_contar_campanias_activas del script SQL.
                try (CallableStatement cs = con.prepareCall("{call sp_contar_campanias_activas(?, ?)}")) {
                    cs.setString(1, campania.getPlataforma().getNombre());   // parametro IN: la plataforma
                    cs.registerOutParameter(2, Types.INTEGER);               // parametro OUT: lo registro ANTES de ejecutar
                    cs.execute();
                    if (cs.getInt(2) > 0) {   // leo el OUT: cuantas campañas activas tiene
                        con.rollback();
                        return false;   // la plataforma ya esta ocupada, no se guarda nada
                    }
                }
                // 2) Inserto la campaña. RETURN_GENERATED_KEYS me devuelve el id autonumerico
                try (PreparedStatement ps = con.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, campania.getDisenador().getLegajo());
                    ps.setString(2, campania.getCuenta().getNombre());
                    ps.setString(3, campania.getPlataforma().getNombre());
                    ps.setString(4, campania.getTipoContenido().getNombre());
                    ps.setDouble(5, campania.getCostoTotal());
                    ps.setString(6, campania.getEstado().name());
                    ps.executeUpdate();
                    try (ResultSet claves = ps.getGeneratedKeys()) {
                        if (claves.next()) {
                            campania.setId(claves.getInt(1));   // le pongo a la campaña el id que le dio la base
                        }
                    }
                }
                con.commit();   // todo salio bien: confirmo
                return true;
            } catch (SQLException e) {
                con.rollback();  // algo fallo: deshago todo
                throw new DAOException("No pude guardar la campaña, no se guardo nada", e);
            }
        } catch (SQLException e) {
            throw new DAOException("Error de conexion al guardar la campaña", e);
        }
    }

    @Override
    public boolean modificar(Campania campania) throws DAOException {
        String sql = "UPDATE campania SET legajo = ?, cuenta = ?, plataforma = ?, tipo_contenido = ?, "
                + "costo_total = ?, estado = ? WHERE id = ?";
        try (Connection con = ConexionSingleton.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, campania.getDisenador().getLegajo());
            ps.setString(2, campania.getCuenta().getNombre());
            ps.setString(3, campania.getPlataforma().getNombre());
            ps.setString(4, campania.getTipoContenido().getNombre());
            ps.setDouble(5, campania.getCostoTotal());
            ps.setString(6, campania.getEstado().name());
            ps.setInt(7, campania.getId());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new DAOException("No pude modificar la campaña", e);
        }
    }

    @Override
    public boolean eliminar(int id) throws DAOException {
        String sql = "DELETE FROM campania WHERE id = ?";
        try (Connection con = ConexionSingleton.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new DAOException("No pude eliminar la campaña", e);
        }
    }

    @Override
    public boolean cambiarEstado(int id, EstadoCampania estado) throws DAOException {
        String sql = "UPDATE campania SET estado = ? WHERE id = ?";
        try (Connection con = ConexionSingleton.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, estado.name());   // guardo el nombre del enum: PENDIENTE, INICIADA o FINALIZADA
            ps.setInt(2, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new DAOException("No pude cambiar el estado de la campaña", e);
        }
    }

    @Override
    public Campania buscarPorId(int id) throws DAOException {
        String sql = SELECT_CAMPANIA + "WHERE c.id = ?";
        try (Connection con = ConexionSingleton.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return armarCampania(rs);
                }
                return null;   // no existe
            }
        } catch (SQLException e) {
            throw new DAOException("No pude buscar la campaña", e);
        }
    }

    @Override
    public List<Campania> listar() throws DAOException {
        // Statement: este SQL no tiene datos de afuera
        String sql = SELECT_CAMPANIA + "ORDER BY c.id";
        List<Campania> campanias = new ArrayList<>();
        try (Connection con = ConexionSingleton.getInstancia().getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                campanias.add(armarCampania(rs));
            }
            return campanias;
        } catch (SQLException e) {
            throw new DAOException("No pude listar las campañas", e);
        }
    }

    @Override
    public List<Campania> listarDeDisenador(int legajo) throws DAOException {
        String sql = SELECT_CAMPANIA + "WHERE c.legajo = ? ORDER BY c.id";
        List<Campania> campanias = new ArrayList<>();
        try (Connection con = ConexionSingleton.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, legajo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    campanias.add(armarCampania(rs));
                }
            }
            return campanias;
        } catch (SQLException e) {
            throw new DAOException("No pude listar las campañas del diseñador", e);
        }
    }

    // Convierte la fila actual del ResultSet (con los JOIN) en una Campania completa
    private Campania armarCampania(ResultSet rs) throws SQLException {
        // Del diseñador solo traigo legajo, nombre y apellido, que es lo que se muestra
        Disenador disenador = new Disenador();
        disenador.setLegajo(rs.getInt("legajo"));
        disenador.setNombre(rs.getString("d_nombre"));
        disenador.setApellido(rs.getString("d_apellido"));

        Plataforma plataforma = new Plataforma(
                rs.getString("p_nombre"),
                rs.getString("formato_principal"),
                rs.getDouble("alcance_maximo"),
                rs.getDouble("costo_fijo"),
                rs.getDouble("cpc"));

        TipoContenido tipo = new TipoContenido(
                rs.getString("t_nombre"),
                rs.getInt("t_horas"),
                rs.getDouble("factor"));

        Campania campania = new Campania(disenador, new Cuenta(rs.getString("cuenta")), plataforma, tipo);
        campania.setId(rs.getInt("id"));
        campania.setCostoTotal(rs.getDouble("costo_total"));
        // Las horas no se guardan en campania (estarian repetidas): salen del tipo de contenido
        campania.setHorasProduccion(tipo.getHorasProduccion());
        // El estado se guarda como texto en la base y lo convierto de nuevo a enum
        campania.setEstado(EstadoCampania.valueOf(rs.getString("estado")));
        return campania;
    }
}