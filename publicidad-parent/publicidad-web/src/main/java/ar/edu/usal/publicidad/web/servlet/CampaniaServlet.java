package ar.edu.usal.publicidad.web.servlet;

import ar.edu.usal.publicidad.domain.Campania;
import ar.edu.usal.publicidad.domain.Disenador;
import ar.edu.usal.publicidad.exception.DAOException;
import ar.edu.usal.publicidad.exception.ValidacionException;
import ar.edu.usal.publicidad.service.CampaniaService;
import ar.edu.usal.publicidad.service.CuentaService;
import ar.edu.usal.publicidad.service.DisenadorService;
import ar.edu.usal.publicidad.service.PlataformaService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// Gestion de campañas (la usa el ejecutivo de cuentas).
//   GET  -> listado, o formulario de alta (?accion=nueva) o de modificacion (?accion=editar&id=..)
//   POST -> todo lo que cambia datos, segun el parametro "accion":
//           crear (asigna una campaña), modificar o eliminar
@WebServlet("/campanias")
public class CampaniaServlet extends HttpServlet {

    private CampaniaService campaniaService = new CampaniaService();
    private DisenadorService disenadorService = new DisenadorService();
    private CuentaService cuentaService = new CuentaService();
    private PlataformaService plataformaService = new PlataformaService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accion = req.getParameter("accion");
        try {
            if ("nueva".equals(accion)) {
                // Paso 1: el ejecutivo elige el diseñador por su legajo
                req.setAttribute("disenadores", disenadorService.listar());
                String legajo = req.getParameter("legajo");
                if (legajo != null && !legajo.isEmpty()) {
                    // Paso 2: ya elegido, le muestro SUS cuentas autorizadas y las plataformas
                    // que puede gestionar y que no estan en una campaña activa
                    int numero = Integer.parseInt(legajo);
                    Disenador elegido = disenadorService.buscar(numero);
                    req.setAttribute("disenadorElegido", elegido);
                    req.setAttribute("cuentas", cuentaService.listarDeDisenador(numero));
                    req.setAttribute("plataformas", plataformaService.listarDisponiblesDeDisenador(numero));
                    req.setAttribute("tipos", campaniaService.listarTipos());
                }
                req.getRequestDispatcher("/WEB-INF/jsp/campaniaNueva.jsp").forward(req, resp);
            } else if ("editar".equals(accion)) {
                Campania campania = campaniaService.buscar(Integer.parseInt(req.getParameter("id")));
                if (campania == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "La campaña no existe");
                    return;
                }
                req.setAttribute("campania", campania);
                req.setAttribute("cuentas", cuentaService.listarDeDisenador(campania.getDisenador().getLegajo()));
                req.setAttribute("tipos", campaniaService.listarTipos());
                req.getRequestDispatcher("/WEB-INF/jsp/campaniaNueva.jsp").forward(req, resp);
            } else {
                req.setAttribute("campanias", campaniaService.listar());
                req.getRequestDispatcher("/WEB-INF/jsp/campanias.jsp").forward(req, resp);
            }
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "El legajo y el numero de campaña deben ser numeros");
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
                int legajo = numeroEntero(req.getParameter("legajo"), "El legajo");
                // El service valida, calcula el costo y las horas y guarda la campaña
                Campania campania = campaniaService.asignar(legajo, req.getParameter("cuenta"),
                        req.getParameter("plataforma"), req.getParameter("tipo"));
                mensaje = "Campaña asignada. Costo total estimado: $"
                        + String.format("%.2f", campania.getCostoTotal())
                        + " - Tiempo de produccion: " + campania.getHorasProduccion() + " h";
            } else if ("modificar".equals(accion)) {
                int id = numeroEntero(req.getParameter("id"), "El numero de campaña");
                if (!campaniaService.modificar(id, req.getParameter("cuenta"), req.getParameter("tipo"))) {
                    throw new ValidacionException("No se pudo modificar la campaña");
                }
                mensaje = "Campaña modificada correctamente";
            } else if ("eliminar".equals(accion)) {
                int id = numeroEntero(req.getParameter("id"), "El numero de campaña");
                if (!campaniaService.eliminar(id)) {
                    throw new ValidacionException("No se pudo eliminar la campaña");
                }
                mensaje = "Campaña eliminada correctamente";
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

    // Convierte un texto a numero entero. Si no se puede, avisa con un mensaje claro
    private int numeroEntero(String texto, String campo) throws ValidacionException {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException | NullPointerException e) {
            throw new ValidacionException(campo + " debe ser un numero");
        }
    }
}
