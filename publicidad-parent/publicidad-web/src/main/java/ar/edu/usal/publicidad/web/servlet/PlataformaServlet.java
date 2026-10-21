package ar.edu.usal.publicidad.web.servlet;

import ar.edu.usal.publicidad.domain.Plataforma;
import ar.edu.usal.publicidad.exception.DAOException;
import ar.edu.usal.publicidad.exception.ValidacionException;
import ar.edu.usal.publicidad.service.PlataformaService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// ABM de plataformas. GET muestra y POST cambia datos segun el parametro "accion"
// (crear, modificar o eliminar).
@WebServlet("/plataformas")
public class PlataformaServlet extends HttpServlet {

    private PlataformaService plataformaService = new PlataformaService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setAttribute("plataformas", plataformaService.listar());
            // Si viene ?nombre=... es para EDITAR: cargo esa plataforma en el formulario
            String nombre = req.getParameter("nombre");
            if (nombre != null) {
                req.setAttribute("plataforma", plataformaService.buscar(nombre));
            }
            req.getRequestDispatcher("/WEB-INF/jsp/plataformas.jsp").forward(req, resp);
        } catch (DAOException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain;charset=UTF-8");
        String accion = req.getParameter("accion");
        try {
            String mensaje;
            if ("crear".equals(accion)) {
                if (!plataformaService.agregar(armarPlataforma(req))) {
                    throw new ValidacionException("No se pudo guardar la plataforma");
                }
                mensaje = "Plataforma guardada correctamente";
            } else if ("modificar".equals(accion)) {
                if (!plataformaService.modificar(armarPlataforma(req))) {
                    throw new ValidacionException("No se pudo modificar la plataforma");
                }
                mensaje = "Plataforma modificada correctamente";
            } else if ("eliminar".equals(accion)) {
                if (!plataformaService.eliminar(req.getParameter("nombre"))) {
                    throw new ValidacionException("No se pudo eliminar la plataforma");
                }
                mensaje = "Plataforma eliminada correctamente";
            } else {
                throw new ValidacionException("La accion no es valida");
            }
            // La respuesta empieza con "OK:" si salio bien o con "ERROR:" si no; el JavaScript lo lee
            resp.getWriter().write("OK:" + mensaje);
        } catch (ValidacionException e) {
            resp.getWriter().write("ERROR:" + e.getMessage());
        } catch (DAOException e) {
            resp.getWriter().write("ERROR:Error en la base de datos: " + e.getMessage());
        }
    }

    // Arma una Plataforma con los datos que llegaron del formulario
    private Plataforma armarPlataforma(HttpServletRequest req) throws ValidacionException {
        Plataforma plataforma = new Plataforma();
        plataforma.setNombre(req.getParameter("nombre"));
        plataforma.setFormatoPrincipal(req.getParameter("formatoPrincipal"));
        plataforma.setAlcanceMaximo(numeroDecimal(req.getParameter("alcanceMaximo"), "El alcance maximo"));
        plataforma.setCostoFijo(numeroDecimal(req.getParameter("costoFijo"), "El costo fijo"));
        plataforma.setCpc(numeroDecimal(req.getParameter("cpc"), "El CPC"));
        return plataforma;
    }

    // Convierte un texto a numero con decimales. Si no se puede, avisa con un mensaje claro
    private double numeroDecimal(String texto, String campo) throws ValidacionException {
        try {
            return Double.parseDouble(texto.trim().replace(',', '.'));
        } catch (NumberFormatException | NullPointerException e) {
            throw new ValidacionException(campo + " debe ser un numero");
        }
    }
}
