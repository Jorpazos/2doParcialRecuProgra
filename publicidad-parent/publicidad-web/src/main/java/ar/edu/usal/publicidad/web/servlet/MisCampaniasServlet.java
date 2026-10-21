package ar.edu.usal.publicidad.web.servlet;

import ar.edu.usal.publicidad.domain.Usuario;
import ar.edu.usal.publicidad.exception.DAOException;
import ar.edu.usal.publicidad.exception.ValidacionException;
import ar.edu.usal.publicidad.service.CampaniaService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// Pagina del diseñador: solo ve SUS campañas asignadas y las marca como iniciada y despues finalizada.
//   GET  -> listado de sus campañas
//   POST -> cambiar el estado de una de sus campañas (accion=iniciar o accion=finalizar)
@WebServlet("/mis-campanias")
public class MisCampaniasServlet extends HttpServlet {

    private CampaniaService campaniaService = new CampaniaService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // El usuario lo guarde en la Session cuando inicio sesion
        Usuario usuario = (Usuario) req.getSession().getAttribute("usuario");
        if (usuario.getDisenador() == null) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Esta pagina es solo para diseñadores");
            return;
        }
        try {
            // Filtro por SU legajo: no puede ver las campañas de otro diseñador
            req.setAttribute("campanias", campaniaService.listarDeDisenador(usuario.getDisenador().getLegajo()));
            req.getRequestDispatcher("/WEB-INF/jsp/misCampanias.jsp").forward(req, resp);
        } catch (DAOException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain;charset=UTF-8");
        Usuario usuario = (Usuario) req.getSession().getAttribute("usuario");
        try {
            if (usuario.getDisenador() == null) {
                throw new ValidacionException("Esta pagina es solo para diseñadores");
            }
            int id = Integer.parseInt(req.getParameter("id"));
            int legajo = usuario.getDisenador().getLegajo();   // el legajo sale de la Session, no del formulario
            String accion = req.getParameter("accion");
            String mensaje;
            if ("iniciar".equals(accion)) {
                if (!campaniaService.iniciar(id, legajo)) {
                    throw new ValidacionException("No se pudo iniciar la campaña");
                }
                mensaje = "Campaña iniciada";
            } else if ("finalizar".equals(accion)) {
                if (!campaniaService.finalizar(id, legajo)) {
                    throw new ValidacionException("No se pudo finalizar la campaña");
                }
                mensaje = "Campaña finalizada";
            } else {
                throw new ValidacionException("La accion no es valida");
            }
            // La respuesta empieza con "OK:" si salio bien o con "ERROR:" si no; el JavaScript lo lee
            resp.getWriter().write("OK:" + mensaje);
        } catch (NumberFormatException e) {
            resp.getWriter().write("ERROR:El numero de campaña no es valido");
        } catch (ValidacionException e) {
            resp.getWriter().write("ERROR:" + e.getMessage());
        } catch (DAOException e) {
            resp.getWriter().write("ERROR:Error en la base de datos: " + e.getMessage());
        }
    }
}
