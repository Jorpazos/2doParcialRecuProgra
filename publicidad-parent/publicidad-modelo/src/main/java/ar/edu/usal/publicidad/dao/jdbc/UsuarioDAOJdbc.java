package ar.edu.usal.publicidad.dao.jdbc;

import ar.edu.usal.publicidad.dao.UsuarioDAO;
import ar.edu.usal.publicidad.dao.factory.DisenadorDAOFactory;
import ar.edu.usal.publicidad.domain.OpcionMenu;
import ar.edu.usal.publicidad.domain.Perfil;
import ar.edu.usal.publicidad.domain.Usuario;
import ar.edu.usal.publicidad.exception.DAOException;
import ar.edu.usal.publicidad.util.ConexionSingleton;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class UsuarioDAOJdbc implements UsuarioDAO {

    @Override
    public Usuario buscarPorUsuario(String nombreUsuario) throws DAOException {
        // JOIN con perfil para traer tambien la pagina de inicio del perfil
        String sql = "SELECT u.usuario, u.password, u.legajo, p.nombre AS perfil, p.url_inicio "
                + "FROM usuario u JOIN perfil p ON p.nombre = u.perfil "
                + "WHERE u.usuario = ?";
        Usuario usuario = null;
        int legajo = 0;            // 0 =  "no tiene diseñador"
        String nombrePerfil = null;
        try (Connection con = ConexionSingleton.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombreUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Perfil perfil = new Perfil(rs.getString("perfil"), rs.getString("url_inicio"));
                    usuario = new Usuario(rs.getString("usuario"), rs.getString("password"), perfil);
                    legajo = rs.getInt("legajo");   // si es NULL en la base, getInt devuelve 0
                    nombrePerfil = perfil.getNombre();
                }
            }
        } catch (SQLException e) {
            throw new DAOException("No pude buscar el usuario", e);
        }
        if (usuario == null) {
            return null;   // el usuario no existe
        }
        // Si es diseñador, le cargo sus datos (con el DAO de diseñadores)
        if (legajo != 0) {
            usuario.setDisenador(DisenadorDAOFactory.getDisenadorDAO().buscarPorLegajo(legajo));
        }
        // Le cargo las opciones de menu que le corresponden a su perfil
        usuario.setOpciones(opcionesDe(nombrePerfil));
        return usuario;
    }

    // Trae las opciones de menu de un perfil (abre su propia conexion)
    private List<OpcionMenu> opcionesDe(String perfil) throws DAOException {
        String sql = "SELECT o.url, o.texto FROM opcion_menu o "
                + "JOIN perfil_opcion po ON po.url = o.url "
                + "WHERE po.perfil = ? ORDER BY o.texto";
        List<OpcionMenu> opciones = new ArrayList<>();
        try (Connection con = ConexionSingleton.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, perfil);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    opciones.add(new OpcionMenu(rs.getString("url"), rs.getString("texto")));
                }
            }
            return opciones;
        } catch (SQLException e) {
            throw new DAOException("No pude traer el menu del usuario", e);
        }
    }
}